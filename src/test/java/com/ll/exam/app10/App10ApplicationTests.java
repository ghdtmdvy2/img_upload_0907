package com.ll.exam.app10;

import com.ll.exam.app10.app.home.controller.HomeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppTests {

	@Autowired
	private MockMvc mvc; // 호출기, 브라우저이다. 특정 url 요청을 보낼 수 있다.

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
}
