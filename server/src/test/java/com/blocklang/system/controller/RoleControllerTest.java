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

import com.blocklang.system.constant.Auth;
import com.blocklang.system.controller.data.NewRoleParam;
import com.blocklang.system.controller.data.UpdateRoleParam;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.model.RoleInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.service.RoleService;
import com.blocklang.system.test.TestWithCurrentUser;

import io.restassured.http.ContentType;

@WebMvcTest(RoleController.class)
public class RoleControllerTest extends TestWithCurrentUser{
	
	@MockBean
	private AppService appService;
	@MockBean
	private RoleService roleService;

	@Test
	public void newRole_anonymous_user_can_not_create() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void newRole_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_NEW))).thenReturn(Optional.empty());

		NewRoleParam param = new NewRoleParam();
		param.setAppId("1");
		param.setName("role1");
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void newRole_app_id_is_blank() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_NEW))).thenReturn(Optional.of(true));

		NewRoleParam param = new NewRoleParam();
		param.setName("role1");
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.appId.size()", is(1))
			.body("errors.appId", hasItem("请选择一个APP！"));
	}
	
	@Test
	public void newRole_name_is_blank() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_NEW))).thenReturn(Optional.of(true));

		NewRoleParam param = new NewRoleParam();
		param.setAppId("appId1");
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入角色名！"));
	}
	
	@Test
	public void newRole_app_id_is_not_exist() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_NEW))).thenReturn(Optional.of(true));

		NewRoleParam param = new NewRoleParam();
		String appId = "appId1";
		String roleName = "role1";
		param.setAppId(appId);
		param.setName(roleName);
		
		when(appService.findById(eq(appId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.appId.size()", is(1))
			.body("errors.appId", hasItem("<strong>appId1</strong>不存在！"));
	}
	
	// 不同的 APP 下，name 不可以重名
	@Test
	public void newRole_app_id_and_name_is_duplicated() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_NEW))).thenReturn(Optional.of(true));

		NewRoleParam param = new NewRoleParam();
		String appId = "appId1";
		String roleName = "role1";
		param.setAppId(appId);
		param.setName(roleName);
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		
		RoleInfo existRole = new RoleInfo();
		when(roleService.findByAppIdAndName(eq(appId), eq(roleName))).thenReturn(Optional.of(existRole));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>role1</strong>已被占用！"));
	}
	
	@Test
	public void newRole_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_NEW))).thenReturn(Optional.of(true));

		String appId = "appId";
		String name = "role1";
		String description = "description";
		
		NewRoleParam param = new NewRoleParam();
		param.setAppId(appId);
		param.setName(name);
		param.setDescription(description);
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		when(roleService.findByAppIdAndName(eq(appId), eq(name))).thenReturn(Optional.empty());

		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.post("roles")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.body("id", notNullValue())
			.body("appId", equalTo(appId))
			.body("name", equalTo(name))
			.body("description", equalTo(description))
			.body("seq", is(1))
			.body("active", is(true))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", nullValue())
			.body("lastUpdateTime", nullValue());
		
		verify(roleService).save(any());
	}
	
	@Test
	public void updateRole_anonymous_user_can_not_update() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.put("roles/{roleId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void updateRole_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_EDIT))).thenReturn(Optional.empty());

		UpdateRoleParam param = new UpdateRoleParam();
		param.setName("role1");
		
		String updateRoleId = "1";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("roles/{roleId}", updateRoleId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void updateRole_name_is_blank() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_EDIT))).thenReturn(Optional.of(true));

		UpdateRoleParam param = new UpdateRoleParam();
		
		String updateRoleId = "1";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("roles/{roleId}", updateRoleId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入角色名！"));
	}
	
	// 不同的 APP 下，name 不可以重名
	@Test
	public void updateRole_app_id_and_new_name_is_duplicated() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_EDIT))).thenReturn(Optional.of(true));

		UpdateRoleParam param = new UpdateRoleParam();
		String roleName = "role1";
		param.setName(roleName);
		
		String appId = "appId1";
		
		String updateRoleId = "1";
		RoleInfo updatedRole = new RoleInfo();
		updatedRole.setId(updateRoleId);
		updatedRole.setAppId(appId);
		
		when(roleService.findById(updateRoleId)).thenReturn(Optional.of(updatedRole));
		
		RoleInfo existRole = new RoleInfo();
		String existedRoleId = "2";
		existRole.setId(existedRoleId);
		when(roleService.findByAppIdAndName(eq(appId), eq(roleName))).thenReturn(Optional.of(existRole));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("roles/{roleId}", updateRoleId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>role1</strong>已被占用！"));
	}
	
	@Test
	public void updateRole_success() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_EDIT))).thenReturn(Optional.of(true));

		NewRoleParam param = new NewRoleParam();
		String appId = "appId1";
		String roleName = "role1";
		String description = "description1";
		param.setAppId(appId);
		param.setName(roleName);
		param.setDescription(description);
		
		String updateRoleId = "1";
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		
		RoleInfo existRole = new RoleInfo();
		String existedRoleId = updateRoleId; // 说明角色名未修改
		existRole.setId(existedRoleId);
		when(roleService.findByAppIdAndName(eq(appId), eq(roleName))).thenReturn(Optional.of(existRole));
		
		RoleInfo updatedRole = new RoleInfo();
		updatedRole.setId(updateRoleId);
		updatedRole.setAppId(appId);
		updatedRole.setName("role");
		updatedRole.setDescription("description");
		updatedRole.setSeq(1);
		updatedRole.setActive(true);
		updatedRole.setCreateUserId(user.getId());
		updatedRole.setCreateTime(LocalDateTime.now());
		
		when(roleService.findById(updateRoleId)).thenReturn(Optional.of(updatedRole));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
		.when()
			.put("roles/{roleId}", updateRoleId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", notNullValue())
			.body("appId", equalTo(appId))
			.body("name", equalTo(roleName))
			.body("description", equalTo(description))
			.body("seq", is(1))
			.body("active", is(true))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
		
		verify(roleService).save(any());
	}
	
	@Test
	public void listRole_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("roles")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void listRole_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_LIST))).thenReturn(Optional.empty());

		String appId = "appId";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("appId", appId)
		.when()
			.get("roles")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void listRole_success_no_data() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_LIST))).thenReturn(Optional.of(true));

		String appId = "appId";
		
		Page<RoleInfo> result = new PageImpl<RoleInfo>(Collections.emptyList());
		when(roleService.findAllByAppId(eq(appId), any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("appId", appId)
		.when()
			.get("roles")
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
	public void listRole_success_one_data() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_LIST))).thenReturn(Optional.of(true));

		String roleId = "roleId";
		String appId = "appId";
		String roleName = "role1";
		String description = "description1";
		
		RoleInfo actualRole = new RoleInfo();
		actualRole.setId(roleId);
		actualRole.setAppId(appId);
		actualRole.setName(roleName);
		actualRole.setDescription(description);
		actualRole.setSeq(1);
		actualRole.setActive(true);
		actualRole.setCreateUserId(user.getId());
		actualRole.setCreateTime(LocalDateTime.now());
		actualRole.setLastUpdateUserId(user.getId());
		actualRole.setLastUpdateTime(LocalDateTime.now());
		
		Page<RoleInfo> result = new PageImpl<RoleInfo>(Collections.singletonList(actualRole));
		when(roleService.findAllByAppId(eq(appId), any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("appId", appId)
		.when()
			.get("roles")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("totalPages", is(1))
			.body("number", is(0))
			.body("size", is(1))
			.body("first", is(true))
			.body("last", is(true))
			.body("empty", is(false))
			.body("content.size()", is(1))
			.body("content[0].id", equalTo(roleId))
			.body("content[0].appId", equalTo(appId))
			.body("content[0].name", equalTo(roleName))
			.body("content[0].description", equalTo(description))
			.body("content[0].seq", is(1))
			.body("content[0].active", is(true))
			.body("content[0].createUserId", equalTo(user.getId()))
			.body("content[0].createTime", notNullValue())
			.body("content[0].lastUpdateUserId", equalTo(user.getId()))
			.body("content[0].lastUpdateTime", notNullValue());
	}

	@Test
	public void getRole_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("roles/{roleId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getRole_user_has_no_permission() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_QUERY))).thenReturn(Optional.empty());
		
		String roleId = "roleId";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("roles/{roleId}", roleId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void getRole_not_found() {
		when(permissionService.canExecute(any(), eq(Auth.SYSTEM_ROLE_QUERY))).thenReturn(Optional.of(true));

		String roleId = "roleId";
		
		when(roleService.findById(eq(roleId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("roles/{roleId}", roleId)
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
	
	@Test
	public void getRole_success() {
		when(permissionService.canExecute(any() ,eq(Auth.SYSTEM_ROLE_QUERY))).thenReturn(Optional.of(true));

		String roleId = "roleId";
		String appId = "appId";
		String roleName = "role1";
		String description = "description1";
		
		RoleInfo actualRole = new RoleInfo();
		actualRole.setId(roleId);
		actualRole.setAppId(appId);
		actualRole.setName(roleName);
		actualRole.setDescription(description);
		actualRole.setSeq(1);
		actualRole.setActive(true);
		actualRole.setCreateUserId(user.getId());
		actualRole.setCreateTime(LocalDateTime.now());
		actualRole.setLastUpdateUserId(user.getId());
		actualRole.setLastUpdateTime(LocalDateTime.now());
		
		when(roleService.findById(eq(roleId))).thenReturn(Optional.of(actualRole));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
		.when()
			.get("roles/{roleId}", roleId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", notNullValue())
			.body("appId", equalTo(appId))
			.body("name", equalTo(roleName))
			.body("description", equalTo(description))
			.body("seq", is(1))
			.body("active", is(true))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
	}
}
