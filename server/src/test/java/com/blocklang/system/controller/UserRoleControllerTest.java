package com.blocklang.system.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.Sex;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.UserRoleService;
import com.blocklang.system.test.TestWithCurrentUser;

import io.restassured.http.ContentType;

@WebMvcTest(UserRoleController.class)
public class UserRoleControllerTest extends TestWithCurrentUser{

	@MockBean
	private UserRoleService userRoleService;

	@Test
	public void listRoleUsers_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("roles/{roleId}/users", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void listRoleUsers_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_USERS))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("roles/{roleId}/users", "1")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void listRoleUsers_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_USERS))).thenReturn(Optional.of(true));

		String roleId = "1";
		
		String userId = "1";
		String username = "username";
		String nickname = "nickname";
		Sex sex = Sex.MALE;
		String phoneNumber = "phoneNumber";
		String password = "password";
		String deptId = "deptId";
		String deptName = "deptName";
		
		UserInfo actualUser = new UserInfo();
		actualUser.setId(userId);
		actualUser.setUsername(username);
		actualUser.setNickname(nickname);
		actualUser.setSex(sex);
		actualUser.setPhoneNumber(phoneNumber);
		actualUser.setPassword(password);
		actualUser.setDeptId(deptId);
		actualUser.setDeptName(deptName);
		
		when(userRoleService.findRoleUsers(eq(roleId))).thenReturn(Collections.singletonList(actualUser));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("roles/{roleId}/users", roleId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(1))
			.body("[0].id", equalTo(userId))
			.body("[0].password", nullValue())
			.body("[0].username", equalTo(username))
			.body("[0].nickname", equalTo(nickname))
			.body("[0].sex", equalTo(sex.getKey()))
			.body("[0].phoneNumber", equalTo(phoneNumber))
			.body("[0].deptId", equalTo(deptId))
			.body("[0].deptName", equalTo(deptName))
			.body("[0].admin", nullValue())
			.body("[0].lastSignInTime", nullValue())
			.body("[0].signInCount", nullValue())
			.body("[0].createTime", nullValue())
			.body("[0].createUserId", nullValue())
			.body("[0].lastUpdateTime", nullValue())
			.body("[0].lastUpdateUserId", nullValue());
	}
	
	@Test
	public void addUser_anonymous_user_can_not_add() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("/roles/{roleId}/users/{userId}/assign", "1", "2")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void addUser_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_USERS))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.post("/roles/{roleId}/users/{userId}/assign", "1", "2")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
		
	}
	
	@Test
	public void addUser_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_USERS))).thenReturn(Optional.of(true));
		
		String roleId = "1";
		String userId = "2";
		
		String username = "username";
		String nickname = "nickname";
		Sex sex = Sex.MALE;
		String phoneNumber = "phoneNumber";
		String password = "password";
		String deptId = "deptId";
		String deptName = "deptName";
		
		UserInfo actualUser = new UserInfo();
		actualUser.setId(userId);
		actualUser.setUsername(username);
		actualUser.setNickname(nickname);
		actualUser.setSex(sex);
		actualUser.setPhoneNumber(phoneNumber);
		actualUser.setPassword(password);
		actualUser.setDeptId(deptId);
		actualUser.setDeptName(deptName);
		
		when(userService.findById(eq(userId))).thenReturn(Optional.of(actualUser));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.post("/roles/{roleId}/users/{userId}/assign", roleId, userId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(userId))
			.body("password", nullValue())
			.body("username", equalTo(username))
			.body("nickname", equalTo(nickname))
			.body("sex", equalTo(sex.getKey()))
			.body("phoneNumber", equalTo(phoneNumber))
			.body("deptId", equalTo(deptId))
			.body("deptName", equalTo(deptName))
			.body("admin", nullValue())
			.body("lastSignInTime", nullValue())
			.body("signInCount", nullValue())
			.body("createTime", nullValue())
			.body("createUserId", nullValue())
			.body("lastUpdateTime", nullValue())
			.body("lastUpdateUserId", nullValue());
		
		verify(userRoleService).add(any());
	}
	
	@Test
	public void removeUser_anonymous_user_can_not_remove() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("/roles/{roleId}/users/{userId}/unassign", "1", "2")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void removeUser_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_USERS))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.post("/roles/{roleId}/users/{userId}/unassign", "1", "2")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void removeUser_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_USERS))).thenReturn(Optional.of(true));
		
		String roleId = "1";
		String userId = "2";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.post("/roles/{roleId}/users/{userId}/unassign", roleId, userId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body(equalTo("{}"));
		verify(userRoleService).remove(eq(roleId), eq(userId));
	}
}
