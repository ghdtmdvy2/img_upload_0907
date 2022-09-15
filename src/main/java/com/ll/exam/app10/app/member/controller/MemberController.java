package com.ll.exam.app10.app.member.controller;

import com.ll.exam.app10.app.member.entity.Member;
import com.ll.exam.app10.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(HttpServletRequest req, String username, String password, String email, MultipartFile profileImg) {
        Member oldMember = memberService.getMemberByUsername(username);

        String passwordClearText = password;
        password = passwordEncoder.encode(password);

        if (oldMember != null) {
            return "redirect:/?errorMsg=Already done.";
        }

        Member member = memberService.join(username, password, email, profileImg);

        try {
            req.login(username, passwordClearText);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        Member loginedMember = memberService.getMemberByUsername(principal.getName());

        model.addAttribute("loginedMember", loginedMember);

        return "member/profile";
    }

    @GetMapping("/profile/imgorigin/{id}")
    public String showProfileImgOrigin(@PathVariable Long id) {
        return "redirect:" + memberService.getMemberById(id).getProfileImgUrl();
    }

    @GetMapping("/profile/img/{id}")
    public ResponseEntity<Object> showProfileImg(@PathVariable Long id) throws URISyntaxException {
        System.out.println("안녕하세요.");

        URI redirectUri = new URI(memberService.getMemberById(id).getProfileImgUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        httpHeaders.setCacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS));
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }
}