package com.ll.exam.app10;

import com.ll.exam.app10.app.home.controller.HomeController;
import com.ll.exam.app10.app.member.controller.MemberController;
import com.ll.exam.app10.app.member.entity.Member;
import com.ll.exam.app10.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
// application.yml이 기본적으로 세팅이 되지만 테스트할 때 이런 식으로 쓰면 application-test.yml 파일을 적용하겠다(덮어씌우겠다).
// 또한 application-base-addi.yml 파일을 적용하겠다(덮어씌우겠다).
@ActiveProfiles({"base-addi", "test"})
class AppTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private MemberService memberService;

	@Test
	@Transactional
	@DisplayName("메인화면에서는 안녕이 나와야 한다.")
	void t1() throws Exception {
		// when
		ResultActions resultActions = mvc
				.perform(get("/")) // 유저가 get 루트 페이지로 이동해라.
				.andDo(print()); // print는 console에 출력을 해줘라.

		// then
		resultActions
				.andExpect(status().is2xxSuccessful()) // 200 : 성공, 300 : redirection, 400 : 클라 잘못, 500 : 서버 잘못 <- 지금은 성공
				.andExpect(handler().handlerType(HomeController.class)) // HomeController로 갈거라고
				.andExpect(handler().methodName("main")) // main 메서드로 갈거라고
				.andExpect(content().string(containsString("안녕"))); // 페이지에 접속을 했을 때 "안녕"이 포함 되어있다.
	}

	@Test
	@DisplayName("회원의 수")
	@Rollback(false)
	void t2() throws Exception {
		long count = memberService.count();
		assertThat(count).isGreaterThan(0);
	}

	@Test
	@DisplayName("user1로 로그인 후 프로필페이지에 접속하면 user1의 이메일이 보여야 한다.")
	@Rollback(false)
	void t3() throws Exception {
		// WHEN
		// GET /
		ResultActions resultActions = mvc
				.perform(
						get("/member/profile")
								.with(user("user1").password("1234").roles("user"))
				)
				.andDo(print());

		// THEN
		// 안녕
		resultActions
				.andExpect(status().is2xxSuccessful())
				.andExpect(handler().handlerType(MemberController.class))
				.andExpect(handler().methodName("showProfile"))
				.andExpect(content().string(containsString("user1@test.com")));
	}

	@Test
	@DisplayName("user4로 로그인 후 프로필페이지에 접속하면 user4의 이메일이 보여야 한다.")
	@Rollback(false)
	void t4() throws Exception {
		// WHEN
		// GET /
		ResultActions resultActions = mvc
				.perform(
						get("/member/profile")
								.with(user("user4").password("1234").roles("user"))
				)
				.andDo(print());

		// THEN
		// 안녕
		resultActions
				.andExpect(status().is2xxSuccessful())
				.andExpect(handler().handlerType(MemberController.class))
				.andExpect(handler().methodName("showProfile"))
				.andExpect(content().string(containsString("user4@test.com")));
	}

	@Test
	@DisplayName("회원가입")
	void t5() throws Exception {
		String testUploadFileUrl = "https://picsum.photos/200/300";
		String originalFileName = "test.png";

		// wget 와 같은 코드이다. 그런 이미지를 다운 받아온다는 것이다.
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Resource> response = restTemplate.getForEntity(testUploadFileUrl, Resource.class);
		InputStream inputStream = response.getBody().getInputStream();

		MockMultipartFile profileImg = new MockMultipartFile(
				"profileImg", // 파일 key 이름
				originalFileName, // 업로드 될 당시 파일 이름 ( 말 그대로 originalFileName )
				"image/png", // 저장될 이미지 타입
				inputStream // 업로드 될 이미지.
		);

		// 회원가입(MVC MOCK)
		// when
		ResultActions resultActions = mvc.perform(
						multipart("/member/join") // 파일 업로드를 위해서는 multipart라고 써줘야한다.
								.file(profileImg) // profileImg는 당시 업로드 된 파일 이미지의 객체를 가져와서 파라미터를 이용.
								.param("username", "user5")
								.param("password", "1234")
								.param("email", "user5@test.com")
								.characterEncoding("UTF-8"))
				.andDo(print());

		// 5번회원이 생성되어야 함, 테스트
		// 여기 마저 구현
		resultActions
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/member/profile"))
				.andExpect(handler().handlerType(MemberController.class))
				.andExpect(handler().methodName("join"));

		Member member = memberService.getMemberById(5L);

		assertThat(member).isNotNull();
		// 5번회원의 프로필 이미지 제거
		memberService.removeProfileImg(member);

	}
}