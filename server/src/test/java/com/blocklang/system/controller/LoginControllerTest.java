package com.blocklang.system.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.EncryptService;
import com.blocklang.system.service.JwtService;
import com.blocklang.system.service.UserService;
import com.blocklang.system.test.AbstractControllerTest;

import io.restassured.http.ContentType;

@WebMvcTest(LoginController.class)
public class LoginControllerTest extends AbstractControllerTest{
	
	@MockBean
	private UserService userService;
	@MockBean
	private JwtService jwtService;
	@MockBean
	private EncryptService encryptService;
	
	@Test
	public void createUser_username_is_blank() {
		String username = "";
		String password = "aaabbbccc111";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("请输入用户名！"));
	}
	
	@Test
	public void createUser_username_is_duplicated() {
		String username = "jack";
		String password = "aaabbbccc111";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);
		
		UserInfo user = new UserInfo();
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(user));
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("<strong>jack</strong>已被占用！"));
	}
	
	@Test
	public void createUser_password_is_blank() {
		String username = "jack";
		String password = "";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.password.size()", is(1))
			.body("errors.password", hasItem("请输入密码！"));
	}
	
	@Test
	public void createUser_success() {
		String username = "jack";
		String password = "aaabbbccc111";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);
		
		when(userService.findByUsername(eq(username))).thenReturn(Optional.empty());
		when(jwtService.toToken(any())).thenReturn("token123");
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.body("user.username", equalTo("jack"))
			.body("user.token", equalTo("token123"))
			.body("user.password", nullValue());
		
		verify(userService).save(any());
	}

	@Test
	public void userLogin_with_wrong_username() {
		String username = "jack1";
		String password = "aaabbbccc111";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);

		when(userService.findByUsername(eq(username))).thenReturn(Optional.empty());

		given()
			.contentType("application/json")
			.body(param)
		.when()
			.post("/users/login")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.globalErrors", hasItem("用户名或密码无效！"))
			.body("errors.globalErrors.size()", is(1));
	}
	
	@Test
	public void userLogin_with_wrong_password() {
		String username = "jack1";
		String password = "aaabbb111";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);

		UserInfo user = new UserInfo();
		user.setPassword("a");
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(user));
		when(encryptService.check(eq(password), eq(user.getPassword()))).thenReturn(false);

		given()
			.contentType("application/json")
			.body(param)
		.when()
			.post("/users/login")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.globalErrors", hasItem("用户名或密码无效！"))
			.body("errors.globalErrors.size()", is(1));
	}
	
	@Test
	public void userLogin_success() {
		String username = "jack";
		String password = "aaabbb111";
		
		LoginParam param = new LoginParam();
		param.setUsername(username);
		param.setPassword(password);

		UserInfo user = new UserInfo();
		user.setUsername("jack");
		user.setPassword("aaabbb111");
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(user));
		when(encryptService.check(eq(password), eq(user.getPassword()))).thenReturn(true);
		when(jwtService.toToken(any())).thenReturn("token123");
		
		given()
			.contentType("application/json")
			.body(param)
		.when()
			.post("/users/login")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("user.username", equalTo(username))
			.body("user.token", equalTo("token123"));
	}

	@Test
	public void checkUsername_username_is_blank() {
		String username = "";
		
		CheckUsernameParam param = new CheckUsernameParam();
		param.setUsername(username);
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("/users/check-username")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("请输入用户名！"));
	}
	
	@Test
	public void checkUsername_username_is_duplicated() {
		String username = "jack";
		
		CheckUsernameParam param = new CheckUsernameParam();
		param.setUsername(username);
		
		UserInfo user = new UserInfo();
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(user));
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("/users/check-username")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("<strong>jack</strong>已被占用！"));
	}
	
	@Test
	public void checkUsername_username_success() {
		String username = "jack";
		
		CheckUsernameParam param = new CheckUsernameParam();
		param.setUsername(username);
		
		when(userService.findByUsername(eq(username))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("/users/check-username")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body(equalTo("{}"));
	}
}