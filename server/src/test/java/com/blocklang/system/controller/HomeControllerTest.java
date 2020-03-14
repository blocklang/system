package com.blocklang.system.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.blocklang.system.service.JwtService;
import com.blocklang.system.service.UserService;
import com.blocklang.system.test.AbstractControllerTest;

import io.restassured.http.ContentType;

@WebMvcTest(HomeController.class)
public class HomeControllerTest extends AbstractControllerTest{

	@MockBean
	private UserService userService;
	@MockBean
	private JwtService jwtService;
	
	@Test
	public void toHome_success() {
		given()
			.contentType(ContentType.HTML)
		.when()
			.get("/login") // 能匿名访问该地址
		.then()
			.statusCode(HttpStatus.SC_OK)
			.content(equalTo(""));
	}
	
}
