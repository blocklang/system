package com.blocklang.system.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.service.AppService;

@WebMvcTest(AppController.class)
public class AppControllerTest extends TestWithCurrentUser {
	
	@MockBean
	private AppService appService;

	// 匿名用户就是指请求时未携带合法的 Authorization header
	@Test
	public void newApp_anonymous_user_can_not_create() {
		AppInfo app = new AppInfo();
		app.setName("app1");

		given()
			.contentType("application/json")
			.body(app)
		.when()
			.post("/apps")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void newApp_user_has_no_permission() {
		AppInfo app = new AppInfo();
		app.setName("app1");
		String resourceId = "res1";

		when(permissionService.canExecute(any(), eq(resourceId), anyString())).thenReturn(Optional.empty());
		
		given()
			.contentType("application/json")
			.header("Authorization", "Token " + token)
			.body(app)
			.queryParam("resid", resourceId)
		.when()
			.post("/apps")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void newApp_success() {
		String name = "app1";
		String url = "url1";
		String icon = "icon1";
		String description = "description1";
		String resourceId = "res1";
		
		AppInfo app = new AppInfo();
		app.setName(name);
		app.setUrl(url);
		app.setIcon(icon);
		app.setDescription(description);

		when(permissionService.canExecute(any(), eq(resourceId), anyString())).thenReturn(Optional.of(true));
		
		given()
			.contentType("application/json")
			.header("Authorization", "Token " + token)
			.body(app)
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

	@Test
	public void getApp_anonymous_user_can_not_get() {
		AppInfo app = new AppInfo();
		app.setName("app1");

		given()
			.contentType("application/json")
		.when()
			.get("/users", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getApp_invalid_token() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.empty());

		given()
			.contentType("application/json")
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
			.contentType("application/json")
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("/apps/{appId}", "app1")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void getApp_success() {
		String resourceId = "res1";
		String appId = "app1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		when(appService.findById(eq(appId))).thenReturn(Optional.of(app));

		given()
			.contentType("application/json")
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("/apps/{appId}", appId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(appId))
			.body("active", is(true));
	}
}
