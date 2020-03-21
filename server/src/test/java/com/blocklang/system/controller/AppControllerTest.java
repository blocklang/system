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
import com.blocklang.system.controller.data.NewAppParam;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.test.TestWithCurrentUser;

import io.restassured.http.ContentType;

@WebMvcTest(AppController.class)
public class AppControllerTest extends TestWithCurrentUser {
	
	@MockBean
	private AppService appService;

	// 匿名用户就是指请求时未携带合法的 Authorization header
	@Test
	public void newApp_anonymous_user_can_not_create() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("/apps")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void newApp_user_has_no_permission() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.empty());
		
		NewAppParam param = new NewAppParam();
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("/apps")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void newApp_name_is_blank() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		NewAppParam param = new NewAppParam();
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("apps")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入APP名称！"));
	}
	
	@Test
	public void newApp_user_id_and_name_is_duplicated() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		String name = "appName1";
		NewAppParam param = new NewAppParam();
		param.setName(name);
		
		when(appService.find(eq(user.getId()), eq(name))).thenReturn(Optional.of(new AppInfo()));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("apps")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>appName1</strong>已被占用！"));
	}
	
	@Test
	public void newApp_success() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		String name = "appName1";
		String url = "url1";
		String icon = "icon1";
		String description = "description1";
		
		NewAppParam param = new NewAppParam();
		param.setName(name);
		param.setUrl(url);
		param.setIcon(icon);
		param.setDescription(description);
		
		when(appService.find(eq(user.getId()), eq(name))).thenReturn(Optional.empty());

		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.body(param)
			.queryParam("resid", resourceId)
		.when()
			.post("/apps")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.body("id", notNullValue())
			.body("name", equalTo(name))
			.body("url", equalTo(url))
			.body("icon", equalTo(icon))
			.body("description", equalTo(description))
			.body("active", is(true))
			.body("createUserId", equalTo(user.getId()))
			.body("createTime", notNullValue())
			.body("lastUpdateUserId", nullValue())
			.body("lastUpdateTime", nullValue());
		
		verify(appService).save(any());
	}
	
	public void updateApp_anonymous_user_can_not_update() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.put("apps/{appId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void updateApp_user_has_no_permission() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.empty());
		
		NewAppParam param = new NewAppParam();
		param.setName("app1");
		
		String updateAppId = "1";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("apps/{appId}", updateAppId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void updateApp_name_is_blank() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		NewAppParam param = new NewAppParam();
		
		String updateAppId = "1";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("apps/{appId}", updateAppId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入APP名称！"));
	}
	
	@Test
	public void updateApp_user_id_and_new_name_is_duplicated() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		String name = "appName";
		NewAppParam param = new NewAppParam();
		param.setName(name);
		
		String updateAppId = "1";
		
		AppInfo existApp = new AppInfo();
		String existAppId = "2";
		existApp.setId(existAppId);
		when(appService.find(eq(user.getId()), eq(name))).thenReturn(Optional.of(existApp));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("apps/{appId}", updateAppId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>appName</strong>已被占用！"));
	}
	
	@Test
	public void updateApp_success() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		String name = "resource1";
		String url = "url1";
		String icon = "icon1";
		String description = "description1";
		
		NewAppParam param = new NewAppParam();
		param.setName(name);
		param.setUrl(url);
		param.setIcon(icon);
		param.setDescription(description);
		
		String updateAppId = "1";
		
		AppInfo existApp = new AppInfo();
		String existAppId = updateAppId;
		existApp.setId(existAppId);
		when(appService.find(eq(user.getId()), eq(name))).thenReturn(Optional.of(existApp));
		
		AppInfo updatedApp = new AppInfo();
		updatedApp.setId(updateAppId);
		updatedApp.setName("resource");
		updatedApp.setUrl("url");
		updatedApp.setIcon("icon");
		updatedApp.setDescription("description");
		updatedApp.setActive(true);
		updatedApp.setCreateUserId(user.getId());
		updatedApp.setCreateTime(LocalDateTime.now());
		
		when(appService.findById(updateAppId)).thenReturn(Optional.of(updatedApp));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("apps/{appId}", updateAppId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(updateAppId))
			.body("name", equalTo(name))
			.body("url", equalTo(url))
			.body("icon", equalTo(icon))
			.body("description", equalTo(description))
			.body("active", equalTo(true))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
		
		verify(appService).save(any());
	}
	
	@Test
	public void listApp_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("apps")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void listApp_user_has_no_permission() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("apps")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void listApp_success_no_data() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.of(true));
		
		Page<AppInfo> result = new PageImpl<AppInfo>(Collections.emptyList());
		when(appService.findAll(any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("apps")
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
	public void listApp_success_one_data() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.of(true));
		
		String appId = "appId";
		String name = "appName";
		String icon = "icon";
		String url = "url";
		String description = "description1";
		
		AppInfo actualApp = new AppInfo();
		actualApp.setId(appId);
		actualApp.setName(name);
		actualApp.setIcon(icon);
		actualApp.setUrl(url);
		actualApp.setDescription(description);
		actualApp.setActive(true);
		actualApp.setCreateUserId(user.getId());
		actualApp.setCreateTime(LocalDateTime.now());
		actualApp.setLastUpdateUserId(user.getId());
		actualApp.setLastUpdateTime(LocalDateTime.now());
		
		Page<AppInfo> result = new PageImpl<AppInfo>(Collections.singletonList(actualApp));
		when(appService.findAll(any())).thenReturn(result);
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.queryParam("appId", appId)
		.when()
			.get("apps")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("totalPages", is(1))
			.body("number", is(0))
			.body("size", is(1))
			.body("first", is(true))
			.body("last", is(true))
			.body("empty", is(false))
			.body("content.size()", is(1))
			.body("content[0].id", equalTo(appId))
			.body("content[0].name", equalTo(name))
			.body("content[0].icon", equalTo(icon))
			.body("content[0].url", equalTo(url))
			.body("content[0].description", equalTo(description))
			.body("content[0].active", is(true))
			.body("content[0].createUserId", equalTo(user.getId()))
			.body("content[0].createTime", notNullValue())
			.body("content[0].lastUpdateUserId", equalTo(user.getId()))
			.body("content[0].lastUpdateTime", notNullValue());
	}

	@Test
	public void getApp_anonymous_user_can_not_get() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("/apps/{appId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getApp_invalid_token() {
		String resourceId = "res1";
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", /*"Token " + */token)
			.queryParam("resid", resourceId)
		.when()
			.get("/apps/{appId}", "app1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getApp_user_has_no_permission() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.empty());

		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("/apps/{appId}", "app1")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void getApp_not_found() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		String queryAppId = "appId";
		when(appService.findById(eq(queryAppId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("apps/{appId}", queryAppId)
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
	
	@Test
	public void getApp_success() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		String appId = "app1";
		String name = "appName1";
		String url = "url1";
		String icon = "icon1";
		String description = "description1";
		Boolean active = true;
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName(name);
		app.setUrl(url);
		app.setIcon(icon);
		app.setDescription(description);
		app.setActive(active);
		app.setCreateUserId(user.getId());
		app.setCreateTime(LocalDateTime.now());
		app.setLastUpdateUserId(user.getId());
		app.setLastUpdateTime(LocalDateTime.now());
		when(appService.findById(eq(appId))).thenReturn(Optional.of(app));

		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("/apps/{appId}", appId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(appId))
			.body("name", equalTo(name))
			.body("url", equalTo(url))
			.body("icon", equalTo(icon))
			.body("description", equalTo(description))
			.body("active", equalTo(active))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
	}
}
