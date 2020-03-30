package com.blocklang.system.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.Assert;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.Sex;
import com.blocklang.system.controller.data.NewUserParam;
import com.blocklang.system.controller.data.UpdateUserParam;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.EncryptService;
import com.blocklang.system.test.TestWithCurrentUser;

import io.restassured.http.ContentType;

@WebMvcTest(UserController.class)
public class UserControllerTest extends TestWithCurrentUser{

	@MockBean
	private EncryptService encryptService;
	
	@Test
	public void listUser_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("users")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void listUser_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_LIST))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("users")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void listUser_success_no_data() {
		// 注意：在做权限校验时，此处不能传 eq(user)，否则不能精准匹配
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_LIST))).thenReturn(Optional.of(true));
		
		Page<UserInfo> result = new PageImpl<UserInfo>(Collections.emptyList());
		when(userService.findAll(eq(true), any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("users")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("totalPages", is(1))
			.body("number", is(0))
			.body("size", is(0))
			.body("first", is(true))
			.body("last", is(true))
			.body("empty", is(true))
			.body("content.size()", is(0));
	}
	
	@Test
	public void listUser_success_one_data() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_LIST))).thenReturn(Optional.of(true));
		
		String userId = "1";
		String username = "username";
		String nickname = "nickname";
		Sex sex = Sex.MALE;
		String phoneNumber = "phoneNumber";
		String password = "password";
		Boolean admin = true;
		LocalDateTime lastSignInTime = LocalDateTime.now();
		int signInCount = 1;
		LocalDateTime createTime = LocalDateTime.now();
		String createUserId = "1";
		
		UserInfo actualUser = new UserInfo();
		actualUser.setId(userId);
		actualUser.setUsername(username);
		actualUser.setNickname(nickname);
		actualUser.setSex(sex);
		actualUser.setPhoneNumber(phoneNumber);
		actualUser.setPassword(password);
		actualUser.setAdmin(admin);
		actualUser.setLastSignInTime(lastSignInTime);
		actualUser.setSignInCount(signInCount);
		actualUser.setCreateTime(createTime);
		actualUser.setCreateUserId(createUserId);
		Page<UserInfo> result = new PageImpl<UserInfo>(Collections.singletonList(actualUser));

		when(userService.findAll(eq(true), any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("users")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("totalPages", is(1))
			.body("number", is(0))
			.body("size", is(1))
			.body("first", is(true))
			.body("last", is(true))
			.body("empty", is(false))
			.body("content.size()", is(1))
			.body("content[0].id", equalTo(userId))
			.body("content[0].password", nullValue())
			.body("content[0].username", equalTo(username))
			.body("content[0].nickname", equalTo(nickname))
			.body("content[0].sex", equalTo(sex.getKey()))
			.body("content[0].phoneNumber", equalTo(phoneNumber))
			.body("content[0].admin", is(admin))
			.body("content[0].lastSignInTime", notNullValue())
			.body("content[0].signInCount", is(signInCount))
			.body("content[0].createTime", notNullValue())
			.body("content[0].createUserId", equalTo(createUserId));
	}
	
	@Test
	public void listUser_success_page() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_LIST))).thenReturn(Optional.of(true));
		
		Page<UserInfo> result = new PageImpl<UserInfo>(Collections.emptyList());
		when(userService.findAll(eq(true), any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("page", "1")
		.when()
			.get("users")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("totalPages", is(1))
			.body("number", is(0))
			.body("size", is(0))
			.body("first", is(true))
			.body("last", is(true))
			.body("empty", is(true))
			.body("content.size()", is(0));
	}

	@Test
	public void getUser_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("users/{userId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void getUser_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_QUERY))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("users/{userId}", user.getId())
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void getUser_not_found() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_QUERY))).thenReturn(Optional.of(true));
		// 注意在 TestWithCurrentUser 中已 mock 一个登录用户
		// 此处要模拟查不到用户的情况，就要避开此用户
		
		String userId = "2";
		Assert.isTrue(!userId.equals(user.getId()), "");
		
		when(userService.findById(eq(userId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("users/{userId}", userId)
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
	
	@Test
	public void getUser_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_QUERY))).thenReturn(Optional.of(true));
		
		String userId = "2";
		Assert.isTrue(!userId.equals(user.getId()), "");
		
		String username = "username";
		String nickname = "nickname";
		Sex sex = Sex.MALE;
		String phoneNumber = "phoneNumber";
		String password = "password";
		Boolean admin = true;
		LocalDateTime lastSignInTime = LocalDateTime.now();
		int signInCount = 1;
		LocalDateTime createTime = LocalDateTime.now();
		String createUserId = "1";
		
		UserInfo actualUser = new UserInfo();
		actualUser.setId(userId);
		actualUser.setUsername(username);
		actualUser.setNickname(nickname);
		actualUser.setSex(sex);
		actualUser.setPhoneNumber(phoneNumber);
		actualUser.setPassword(password);
		actualUser.setAdmin(admin);
		actualUser.setLastSignInTime(lastSignInTime);
		actualUser.setSignInCount(signInCount);
		actualUser.setCreateTime(createTime);
		actualUser.setCreateUserId(createUserId);
		
		when(userService.findById(eq(userId))).thenReturn(Optional.of(actualUser));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("users/{userId}", userId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(userId))
			.body("password", nullValue())
			.body("username", equalTo(username))
			.body("nickname", equalTo(nickname))
			.body("sex", equalTo(sex.getKey()))
			.body("phoneNumber", equalTo(phoneNumber))
			.body("admin", is(admin))
			.body("lastSignInTime", notNullValue())
			.body("signInCount", is(signInCount))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(createUserId));
	}

	@Test
	public void newUser_anonymous_user_can_not_create() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void newUser_user_has_no_permission() {
		NewUserParam param = new NewUserParam();
		param.setUsername("username");
		param.setPassword("password");
		
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_NEW))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void newUser_username_is_blank() {
		when(permissionService.canExecute(any(),  eq(Auth.SYSTEM_USER_NEW))).thenReturn(Optional.of(true));
		NewUserParam param = new NewUserParam();
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("请输入用户名！"));
	}
	
	@Test
	public void newUser_password_is_blank() {
		when(permissionService.canExecute(any(),  eq(Auth.SYSTEM_USER_NEW))).thenReturn(Optional.of(true));

		NewUserParam param = new NewUserParam();
		param.setUsername("username");
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.password.size()", is(1))
			.body("errors.password", hasItem("请输入密码！"));
	}

	@Test
	public void newUser_username_is_duplicated() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_NEW))).thenReturn(Optional.of(true));

		NewUserParam param = new NewUserParam();
		String username = "jack";
		param.setUsername(username);
		param.setPassword("password");
		
		UserInfo existUser = new UserInfo();
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(existUser));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("<strong>jack</strong>已被占用！"));
	}
	
	@Test
	public void newUser_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_NEW))).thenReturn(Optional.of(true));

		String username = "username";
		String nickname = "nickname";
		String sex = Sex.MALE.getKey();
		String phoneNumber = "phoneNumber";
		String password = "password";
		
		NewUserParam param = new NewUserParam();
		param.setUsername(username);
		param.setPassword(password);
		param.setNickname(nickname);
		param.setSex(sex);
		param.setPhoneNumber(phoneNumber);
		
		
		when(userService.findByUsername(eq(username))).thenReturn(Optional.empty());
		when(encryptService.encrypt(eq(password))).thenReturn(password);
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("users")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.body("id", notNullValue())
			.body("password", nullValue())
			.body("username", equalTo(username))
			.body("nickname", equalTo(nickname))
			.body("sex", equalTo(sex))
			.body("phoneNumber", equalTo(phoneNumber))
			.body("admin", is(false))
			.body("lastSignInTime", nullValue())
			.body("signInCount", is(0))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", nullValue())
			.body("lastUpdateTime", nullValue());
		
		verify(userService).save(any());
	}
	
	@Test
	public void updateUser_anonymous_user_can_not_update() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.put("users/{userId}", "2")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void updateUser_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_EDIT))).thenReturn(Optional.empty());

		UpdateUserParam param = new UpdateUserParam();
		param.setUsername("username");
		
		String updateUserId = "2";
		Assert.isTrue(!user.getId().equals(updateUserId), "");
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("users/{userId}", updateUserId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void updateUser_username_is_blank() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_EDIT))).thenReturn(Optional.of(true));

		UpdateUserParam param = new UpdateUserParam();
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("users/{userId}", "2")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("请输入用户名！"));
	}
	
	@Test
	public void updateUser_new_username_is_duplicated() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_EDIT))).thenReturn(Optional.of(true));

		String updateUserId = "2";
		
		UpdateUserParam param = new UpdateUserParam();
		String username = "new-jack"; // 这是修改后的用户名，但这个用户名在数据库中已存在
		param.setUsername(username);
		
		UserInfo existUser = new UserInfo();
		String existUserId = "3";
		existUser.setId(existUserId);
		Assert.isTrue(!existUserId.equals(updateUserId), "");
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(existUser));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("users/{userId}", updateUserId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.username.size()", is(1))
			.body("errors.username", hasItem("<strong>new-jack</strong>已被占用！"));
	}
	
	@Test
	public void updateUser_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_USER_EDIT))).thenReturn(Optional.of(true));

		String updateUserId = "2";
		String username = "username1";
		String nickname = "nickname1";
		String sex = Sex.MALE.getKey();
		String phoneNumber = "phoneNumber1";
		
		UpdateUserParam param = new UpdateUserParam();
		param.setUsername(username);
		param.setNickname(nickname);
		param.setSex(sex);
		param.setPhoneNumber(phoneNumber);
		
		
		// 用于校验用户名是否被占用
		// 这是没有修改用户名的情况
		UserInfo existUser = new UserInfo();
		existUser.setId(updateUserId);
		when(userService.findByUsername(eq(username))).thenReturn(Optional.of(existUser));
		
		UserInfo updatedUser = new UserInfo();
		updatedUser.setId(updateUserId);
		updatedUser.setUsername("username");
		updatedUser.setNickname("nickname");
		updatedUser.setSex(Sex.FEMALE);
		updatedUser.setPhoneNumber("phoneNumber");
		updatedUser.setAdmin(false);
		updatedUser.setCreateUserId(user.getId());
		updatedUser.setCreateTime(LocalDateTime.now());
		when(userService.findById(updateUserId)).thenReturn(Optional.of(updatedUser));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("users/{userId}", updateUserId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(updateUserId))
			.body("password", nullValue())
			.body("username", equalTo(username))
			.body("nickname", equalTo(nickname))
			.body("sex", equalTo(sex))
			.body("phoneNumber", equalTo(phoneNumber))
			.body("admin", is(false))
			.body("lastSignInTime", nullValue())
			.body("signInCount", is(0))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
		
		verify(userService).save(any());
	}
}
