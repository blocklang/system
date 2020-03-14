package com.blocklang.system.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.blocklang.system.test.AbstractControllerTest;

import io.restassured.http.ContentType;

@WebMvcTest(HomeController.class)
public class HomeControllerTest extends AbstractControllerTest{

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
