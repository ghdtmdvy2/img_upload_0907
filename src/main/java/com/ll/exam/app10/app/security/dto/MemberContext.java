package com.ll.exam.app10.app.security.dto;

import com.ll.exam.app10.app.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberContext extends User {
    private final Long id;
    private final String profileImgUrl;

    public MemberContext(Member member, List<GrantedAuthority> authorities) {
        // 상속을 받았기 때문에 이부분은 무조건 해줘야해서 기본적으로 어떤 User인지 저장해준다.
        // 그냥 간단하게 생각하면 principal에서의 기본적인 정보를 제공하는 곳이라고 생각하면 된다.
        super(member.getUsername(), member.getPassword(), authorities);
        this.id = member.getId();
        this.profileImgUrl = member.getProfileImgUrl();
    }
}