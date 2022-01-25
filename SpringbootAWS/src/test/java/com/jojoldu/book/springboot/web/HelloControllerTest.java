package com.jojoldu.book.springboot.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class) // SpringRunner 스프링 실행자 사용 (스프링 부트 테스트와 JUnit 사이 연결자)
@WebMvcTest(controllers = HelloController.class) // 스프링 테스트 어노테이션 중 Web(Spring MVC)에 집중할 수 있는 어노테이션
public class HelloControllerTest {

    @Autowired
    /**
     * 웹API 테스트에 사용
     * 스프링 MVC 테스트의 시작점
     */
    private MockMvc mvc;

    // HTTP VIEW Test
    @Test
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello")) // MockMvc를 통해 /hello 주소로 HTTP GET 요청
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }

    // HTTP API Test
    @Test
    public void helloDto가_리턴된다() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(
                get("/hello/dto")
                        .param("name", name)
                        .param("amount", String.valueOf(amount))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name))) //json 응답값을 필드별로 검증할 수 있는 메소드
                .andExpect(jsonPath("$.amount", is(amount)));
    }

}
