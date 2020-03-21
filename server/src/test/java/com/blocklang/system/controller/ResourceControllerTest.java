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

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.constant.Tree;
import com.blocklang.system.controller.data.NewResourceParam;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.service.ResourceService;
import com.blocklang.system.test.TestWithCurrentUser;

import io.restassured.http.ContentType;

@WebMvcTest(ResourceController.class)
public class ResourceControllerTest extends TestWithCurrentUser{

	@MockBean
	private AppService appService;
	@MockBean
	private ResourceService resourceService;
	
	@Test
	public void newResource_anonymous_user_can_not_create() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void newResource_user_has_no_permission() {
		NewResourceParam param = new NewResourceParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		param.setAppId("1");
		param.setName("resource1");
		
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void newResource_app_id_is_blank() {
		NewResourceParam param = new NewResourceParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		param.setName("resource1");
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.appId.size()", is(1))
			.body("errors.appId", hasItem("请选择一个APP！"));
	}
	
	@Test
	public void newResource_parent_id_is_blank() {
		NewResourceParam param = new NewResourceParam();
		param.setAppId("appId");
		param.setName("resource1");
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.parentId.size()", is(1))
			.body("errors.parentId", hasItem("请选择一个父资源！"));
	}
	
	@Test
	public void newResource_name_is_blank() {
		NewResourceParam param = new NewResourceParam();
		param.setAppId("appId");
		param.setParentId(Tree.ROOT_PARENT_ID);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入资源名！"));
	}
	
	@Test
	public void newResource_app_id_is_not_exist() {
		NewResourceParam param = new NewResourceParam();
		String appId = "appId1";
		String parentId = "parentId1";
		String name = "resource1";
		param.setParentId(parentId);
		param.setAppId(appId);
		param.setName(name);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		when(appService.findById(eq(appId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.appId.size()", is(1))
			.body("errors.appId", hasItem("<strong>appId1</strong>不存在！"));
	}
	
	// 不同的 APP 下，同一级下 name 不可以重名
	@Test
	public void newResource_app_id_and_parent_id_and_name_is_duplicated() {
		NewResourceParam param = new NewResourceParam();
		String appId = "appId1";
		String parentId = "parentId1";
		String name = "resource1";
		param.setParentId(parentId);
		param.setAppId(appId);
		param.setName(name);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		
		when(resourceService.find(eq(appId), eq(parentId), eq(name))).thenReturn(Optional.of(new ResourceInfo()));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>resource1</strong>已被占用！"));
	}
	
	@Test
	public void newResource_success() {
		NewResourceParam param = new NewResourceParam();
		String appId = "appId1";
		String parentId = "parentId1";
		String name = "resource1";
		String url = "url";
		String icon = "icon";
		String resourceType = ResourceType.PROGRAM.getKey();
		String description = "description";
		String auth = Auth.INDEX;
		
		param.setParentId(parentId);
		param.setAppId(appId);
		param.setName(name);
		param.setUrl(url);
		param.setIcon(icon);
		param.setResourceType(resourceType);
		param.setDescription(description);
		param.setAuth(auth);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		
		when(resourceService.find(eq(appId), eq(parentId), eq(name))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("resources")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.body("id", notNullValue())
			.body("parentId", equalTo(parentId))
			.body("appId", equalTo(appId))
			.body("name", equalTo(name))
			.body("seq", is(1))
			.body("url", equalTo(url))
			.body("icon", equalTo(icon))
			.body("resourceType", equalTo(resourceType))
			.body("description", equalTo(description))
			.body("active", is(true))
			.body("auth", equalTo(auth))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", nullValue())
			.body("lastUpdateTime", nullValue());
		
		verify(resourceService).save(any());
	}
	
	@Test
	public void updateResource_anonymous_user_can_not_update() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.put("resources/{resourceId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void updateResource_user_has_no_permission() {
		NewResourceParam param = new NewResourceParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		param.setAppId("1");
		param.setName("resource1");
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void updateResource_app_id_is_blank() {
		NewResourceParam param = new NewResourceParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		param.setName("resource1");
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.appId.size()", is(1))
			.body("errors.appId", hasItem("请选择一个APP！"));
	}
	
	@Test
	public void updateResource_parent_id_is_blank() {
		NewResourceParam param = new NewResourceParam();
		param.setAppId("appId");
		param.setName("resource1");
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.parentId.size()", is(1))
			.body("errors.parentId", hasItem("请选择一个父资源！"));
	}
	
	@Test
	public void updateResource_app_id_is_not_exist() {
		String appId = "appId1";
		String parentId = "parentId1";
		String name = "resource1";
		
		NewResourceParam param = new NewResourceParam();
		param.setAppId(appId);
		param.setParentId(parentId);
		param.setName(name);
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		when(appService.findById(eq(appId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.appId.size()", is(1))
			.body("errors.appId", hasItem("<strong>appId1</strong>不存在！"));
	}
	
	@Test
	public void updateResource_name_is_blank() {
		NewResourceParam param = new NewResourceParam();
		param.setAppId("appId");
		param.setParentId("parentId1");
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入资源名！"));
	}
	
	// 不同的 APP 下，同一级下 name 不可以重名
	@Test
	public void updateResource_app_id_parent_id_and_new_name_is_duplicated() {
		NewResourceParam param = new NewResourceParam();
		String appId = "appId1";
		String parentId = "parentId1";
		String name = "resource1";
		param.setParentId(parentId);
		param.setAppId(appId);
		param.setName(name);
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		
		ResourceInfo existResource = new ResourceInfo();
		String existResourceId = "2";
		existResource.setId(existResourceId);
		when(resourceService.find(eq(appId), eq(parentId), eq(name))).thenReturn(Optional.of(existResource));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>resource1</strong>已被占用！"));
	}
	
	@Test
	public void updateResource_success() {
		NewResourceParam param = new NewResourceParam();
		String appId = "appId1";
		String parentId = "parentId1";
		String name = "resource1";
		String url = "url1";
		String icon = "icon1";
		String resourceType = ResourceType.PROGRAM.getKey();
		String description = "description1";
		String auth = Auth.INDEX;
		
		param.setParentId(parentId);
		param.setAppId(appId);
		param.setName(name);
		param.setUrl(url);
		param.setIcon(icon);
		param.setResourceType(resourceType);
		param.setDescription(description);
		param.setAuth(auth);
		
		String updateResourceId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		when(appService.findById(eq(appId))).thenReturn(Optional.of(new AppInfo()));
		
		ResourceInfo existResource = new ResourceInfo();
		String existResourceId = updateResourceId;
		existResource.setId(existResourceId);
		when(resourceService.find(eq(appId), eq(parentId), eq(name))).thenReturn(Optional.of(existResource));
		
		ResourceInfo updatedResource = new ResourceInfo();
		updatedResource.setId(updateResourceId);
		updatedResource.setAppId(appId);
		updatedResource.setParentId(parentId);
		updatedResource.setName("resource");
		updatedResource.setUrl("url");
		updatedResource.setIcon("icon");
		updatedResource.setResourceType(ResourceType.OPERATOR);
		updatedResource.setDescription("description");
		updatedResource.setActive(true);
		updatedResource.setAuth(Auth.NEW);
		updatedResource.setSeq(1);
		updatedResource.setCreateUserId(user.getId());
		updatedResource.setCreateTime(LocalDateTime.now());
		
		when(resourceService.findById(updateResourceId)).thenReturn(Optional.of(updatedResource));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("resources/{resourceId}", updateResourceId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", notNullValue())
			.body("parentId", equalTo(parentId))
			.body("appId", equalTo(appId))
			.body("name", equalTo(name))
			.body("seq", equalTo(1))
			.body("url", equalTo(url))
			.body("icon", equalTo(icon))
			.body("resourceType", equalTo(resourceType))
			.body("description", equalTo(description))
			.body("active", equalTo(true))
			.body("auth", equalTo(auth))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
		
		verify(resourceService).save(any());
	}
	
	@Test
	public void listResource_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("resources/{resourceId}/children", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void listResource_user_has_no_permission() {
		String resourceId = "res1"; // 资源管理模块的标识
		String appId = "appId";
		String parentResourceId = "1"; // 在资源管理模块中管理的资源标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.queryParam("appId", appId)
		.when()
			.get("resources/{resourceId}/children", parentResourceId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void listResource_success_no_data() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.of(true));
		
		String appId = "appId";
		String parentResourceId = "1"; // 在资源管理模块中管理的资源标识
		when(resourceService.findChildren(eq(appId), eq(parentResourceId), any())).thenReturn(Collections.emptyList());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.queryParam("appId", appId)
		.when()
			.get("resources/{resourceId}/children", parentResourceId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(0));
	}
	
	@Test
	public void listResource_success_one_data() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.of(true));
		
		String existedResourceId = "resourceId1";
		String appId = "appId";
		String parentResourceId = "1"; // 在资源管理模块中管理的资源标识
		String name = "resource";
		String url = "url";
		String icon = "icon";
		ResourceType resourceType = ResourceType.OPERATOR;
		String description = "description";
		Boolean active = true;
		String auth = Auth.NEW;
		Integer seq = 1;
		
		ResourceInfo actualResource = new ResourceInfo();
		actualResource.setId(existedResourceId);
		actualResource.setAppId(appId);
		actualResource.setParentId(parentResourceId);
		actualResource.setName(name);
		actualResource.setUrl(url);
		actualResource.setIcon(icon);
		actualResource.setResourceType(resourceType);
		actualResource.setDescription(description);
		actualResource.setActive(active);
		actualResource.setAuth(auth);
		actualResource.setSeq(seq);
		actualResource.setCreateUserId(user.getId());
		actualResource.setCreateTime(LocalDateTime.now());
		actualResource.setLastUpdateUserId(user.getId());
		actualResource.setLastUpdateTime(LocalDateTime.now());
		when(resourceService.findChildren(eq(appId), eq(parentResourceId), any())).thenReturn(Collections.singletonList(actualResource));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.queryParam("appId", appId)
		.when()
			.get("resources/{resourceId}/children", parentResourceId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("size()", equalTo(1))
			.body("[0].id", equalTo(existedResourceId))
			.body("[0].parentId", equalTo(parentResourceId))
			.body("[0].appId", equalTo(appId))
			.body("[0].name", equalTo(name))
			.body("[0].seq", equalTo(seq))
			.body("[0].url", equalTo(url))
			.body("[0].icon", equalTo(icon))
			.body("[0].resourceType", equalTo(resourceType.getKey()))
			.body("[0].description", equalTo(description))
			.body("[0].active", equalTo(active))
			.body("[0].auth", equalTo(auth))
			.body("[0].createTime", notNullValue())
			.body("[0].createUserId", equalTo(user.getId()))
			.body("[0].lastUpdateUserId", equalTo(user.getId()))
			.body("[0].lastUpdateTime", notNullValue());
	}

	@Test
	public void getResource_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("resources/{resourceId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getResource_user_has_no_permission() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.empty());
		
		String queryResourceId = "resourceId";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("resources/{resourceId}", queryResourceId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void getResource_not_found() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		String queryResourceId = "resourceId";
		when(resourceService.findById(eq(queryResourceId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("resources/{resourceId}", queryResourceId)
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
	
	@Test
	public void getResource_success() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		String queryResourceId = "resourceId1";
		String appId = "appId";
		String parentId = "1"; // 在资源管理模块中管理的资源标识
		String name = "resource";
		String url = "url";
		String icon = "icon";
		ResourceType resourceType = ResourceType.OPERATOR;
		String description = "description";
		Boolean active = true;
		String auth = Auth.NEW;
		Integer seq = 1;
		
		ResourceInfo actualResource = new ResourceInfo();
		actualResource.setId(queryResourceId);
		actualResource.setAppId(appId);
		actualResource.setParentId(parentId);
		actualResource.setName(name);
		actualResource.setUrl(url);
		actualResource.setIcon(icon);
		actualResource.setResourceType(resourceType);
		actualResource.setDescription(description);
		actualResource.setActive(active);
		actualResource.setAuth(auth);
		actualResource.setSeq(seq);
		actualResource.setCreateUserId(user.getId());
		actualResource.setCreateTime(LocalDateTime.now());
		actualResource.setLastUpdateUserId(user.getId());
		actualResource.setLastUpdateTime(LocalDateTime.now());
		when(resourceService.findById(eq(queryResourceId))).thenReturn(Optional.of(actualResource));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("resources/{resourceId}", queryResourceId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(queryResourceId))
			.body("parentId", equalTo(parentId))
			.body("appId", equalTo(appId))
			.body("name", equalTo(name))
			.body("seq", equalTo(seq))
			.body("url", equalTo(url))
			.body("icon", equalTo(icon))
			.body("resourceType", equalTo(resourceType.getKey()))
			.body("description", equalTo(description))
			.body("active", equalTo(active))
			.body("auth", equalTo(auth))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
	}
	
}
