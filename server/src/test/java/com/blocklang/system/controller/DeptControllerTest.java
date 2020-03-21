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
import com.blocklang.system.constant.Tree;
import com.blocklang.system.controller.data.NewDeptParam;
import com.blocklang.system.model.DeptInfo;
import com.blocklang.system.service.DeptService;
import com.blocklang.system.test.TestWithCurrentUser;

import io.restassured.http.ContentType;

@WebMvcTest(DeptController.class)
public class DeptControllerTest extends TestWithCurrentUser{

	@MockBean
	private DeptService deptService;
	
	@Test
	public void newDept_anonymous_user_can_not_create() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void newDept_user_has_no_permission() {
		NewDeptParam param = new NewDeptParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		param.setName("dept1");
		
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void newDept_parent_id_is_blank() {
		NewDeptParam param = new NewDeptParam();
		param.setName("dept1");
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.parentId.size()", is(1))
			.body("errors.parentId", hasItem("请选择一个父部门！"));
	}
	
	@Test
	public void newDept_name_is_blank() {
		NewDeptParam param = new NewDeptParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入部门名称！"));
	}
	
	@Test
	public void newDept_parent_id_is_not_exist() {
		String parentId = "parentId1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		param.setName("name");
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		when(deptService.findById(eq(parentId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.parentId.size()", is(1))
			.body("errors.parentId", hasItem("<strong>parentId1</strong>不存在！"));
	}
	
	// 同一级部门下 name 不可以重名
	@Test
	public void newDept_parent_id_and_name_is_duplicated() {
		String parentId = "parentId1";
		String name = "name1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		param.setName(name);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		DeptInfo parentDept = new DeptInfo();
		when(deptService.findById(eq(parentId))).thenReturn(Optional.of(parentDept));
		
		DeptInfo duplicatedDept = new DeptInfo();
		when(deptService.find(eq(parentId), eq(name))).thenReturn(Optional.of(duplicatedDept));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>name1</strong>已被占用！"));
	}
	
	@Test
	public void newDept_success() {
		String parentId = "parentId1";
		String name = "name1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		param.setName(name);
		
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.NEW))).thenReturn(Optional.of(true));
		
		DeptInfo parentDept = new DeptInfo();
		when(deptService.findById(eq(parentId))).thenReturn(Optional.of(parentDept));
		
		when(deptService.find(eq(parentId), eq(name))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.post("depts")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.body("id", notNullValue())
			.body("parentId", equalTo(parentId))
			.body("name", equalTo(name))
			.body("seq", is(1))
			.body("active", is(true))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", nullValue())
			.body("lastUpdateTime", nullValue());
		
		verify(deptService).save(any());
	}
	
	@Test
	public void updateDept_anonymous_user_can_not_update() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.put("depts/{deptId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void updateDept_user_has_no_permission() {
		NewDeptParam param = new NewDeptParam();
		param.setParentId(Tree.ROOT_PARENT_ID);
		param.setName("name1");
		
		String updateDeptId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("depts/{deptId}", updateDeptId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void updateDept_parent_id_is_blank() {
		NewDeptParam param = new NewDeptParam();
		param.setName("name1");
		
		String updateDeptId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("depts/{deptId}", updateDeptId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.parentId.size()", is(1))
			.body("errors.parentId", hasItem("请选择一个父部门！"));
	}
	
	@Test
	public void updateDept_parent_id_is_not_exist() {
		String parentId = "parentId1";
		String name = "resource1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		param.setName(name);
		
		String updateDeptId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		when(deptService.findById(eq(parentId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("depts/{detpId}", updateDeptId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.parentId.size()", is(1))
			.body("errors.parentId", hasItem("<strong>parentId1</strong>不存在！"));
	}
	
	@Test
	public void updateDept_name_is_blank() {
		String parentId = "parentId1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		
		String updateDeptId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("depts/{deptId}", updateDeptId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("请输入部门名称！"));
	}
	
	// 一个部门下不能有同级的同名子部门
	@Test
	public void updateDept_parent_id_and_new_name_is_duplicated() {
		String parentId = "parentId1";
		String name = "name1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		param.setName(name);
		
		String updateDeptId = "1";
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		DeptInfo parentDept = new DeptInfo();
		when(deptService.findById(eq(parentId))).thenReturn(Optional.of(parentDept));
		
		DeptInfo existDept = new DeptInfo();
		String existDeptId = "2";
		existDept.setId(existDeptId);
		when(deptService.find(eq(parentId), eq(name))).thenReturn(Optional.of(existDept));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("depts/{deptId}", updateDeptId)
		.then()
			.statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
			.body("errors.name.size()", is(1))
			.body("errors.name", hasItem("<strong>name1</strong>已被占用！"));
	}
	
	@Test
	public void updateDept_success() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.EDIT))).thenReturn(Optional.of(true));
		
		String parentId = "parentId1";
		String name = "name1";
		
		NewDeptParam param = new NewDeptParam();
		param.setParentId(parentId);
		param.setName(name);
		
		String updateDeptId = "1";
		DeptInfo parentDept = new DeptInfo();
		when(deptService.findById(eq(parentId))).thenReturn(Optional.of(parentDept));
		
		DeptInfo existDept = new DeptInfo();
		String existDeptId = updateDeptId;
		existDept.setId(existDeptId);
		when(deptService.find(eq(parentId), eq(name))).thenReturn(Optional.of(existDept));
		
		DeptInfo updatedDept = new DeptInfo();
		updatedDept.setId(updateDeptId);
		updatedDept.setParentId(parentId);
		updatedDept.setName("resource");
		updatedDept.setActive(true);
		updatedDept.setSeq(1);
		updatedDept.setCreateUserId(user.getId());
		updatedDept.setCreateTime(LocalDateTime.now());
		when(deptService.findById(updateDeptId)).thenReturn(Optional.of(updatedDept));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
			.body(param)
		.when()
			.put("depts/{dept}", updateDeptId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", notNullValue())
			.body("parentId", equalTo(parentId))
			.body("name", equalTo(name))
			.body("seq", equalTo(1))
			.body("active", equalTo(true))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
		
		verify(deptService).save(any());
	}
	
	@Test
	public void listDept_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("depts/{deptId}/children", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void listDept_user_has_no_permission() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.empty());
		
		String parentDeptId = "1";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("depts/{deptId}/children", parentDeptId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void listDept_success_no_data() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.of(true));
		
		String parentDeptId = "1";
		when(deptService.findChildren(eq(parentDeptId), any())).thenReturn(Collections.emptyList());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("depts/{deptId}/children", parentDeptId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(0));
	}
	
	@Test
	public void listDept_success_one_data() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.LIST))).thenReturn(Optional.of(true));
		
		String deptId = "deptId1";
		String parentDeptId = "1";
		String name = "deptName1";
		Boolean active = true;
		Integer seq = 1;
		
		DeptInfo actualDept = new DeptInfo();
		actualDept.setId(deptId);
		actualDept.setParentId(parentDeptId);
		actualDept.setName(name);
		actualDept.setActive(active);
		actualDept.setSeq(seq);
		actualDept.setCreateUserId(user.getId());
		actualDept.setCreateTime(LocalDateTime.now());
		actualDept.setLastUpdateUserId(user.getId());
		actualDept.setLastUpdateTime(LocalDateTime.now());
		when(deptService.findChildren(eq(parentDeptId), any())).thenReturn(Collections.singletonList(actualDept));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("depts/{deptId}/children", parentDeptId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("size()", equalTo(1))
			.body("[0].id", equalTo(deptId))
			.body("[0].parentId", equalTo(parentDeptId))
			.body("[0].name", equalTo(name))
			.body("[0].seq", equalTo(seq))
			.body("[0].active", equalTo(active))
			.body("[0].createTime", notNullValue())
			.body("[0].createUserId", equalTo(user.getId()))
			.body("[0].lastUpdateUserId", equalTo(user.getId()))
			.body("[0].lastUpdateTime", notNullValue());
	}

	@Test
	public void getDept_anonymous_user_can_not_list() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("depts/{deptId}", "1")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getDept_user_has_no_permission() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.empty());
		
		String queryDeptId = "deptId";
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("depts/{deptId}", queryDeptId)
		.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
	
	@Test
	public void getDept_not_found() {
		String resourceId = "res1";
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		String queryDeptId = "deptId";
		when(deptService.findById(eq(queryDeptId))).thenReturn(Optional.empty());
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("depts/{deptId}", queryDeptId)
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
	
	@Test
	public void getDept_success() {
		String resourceId = "res1"; // 资源管理模块的标识
		when(permissionService.canExecute(any(), eq(resourceId), eq(Auth.QUERY))).thenReturn(Optional.of(true));
		
		String queryDeptId = "deptId1";
		String parentId = "1"; // 在资源管理模块中管理的资源标识
		String name = "resource";
		Boolean active = true;
		Integer seq = 1;
		
		DeptInfo actualDept = new DeptInfo();
		actualDept.setId(queryDeptId);
		actualDept.setParentId(parentId);
		actualDept.setName(name);
		actualDept.setActive(active);
		actualDept.setSeq(seq);
		actualDept.setCreateUserId(user.getId());
		actualDept.setCreateTime(LocalDateTime.now());
		actualDept.setLastUpdateUserId(user.getId());
		actualDept.setLastUpdateTime(LocalDateTime.now());
		when(deptService.findById(eq(queryDeptId))).thenReturn(Optional.of(actualDept));
		
		given()
			.contentType(ContentType.JSON)
			.header("Authorization", "Token " + token)
			.queryParam("resid", resourceId)
		.when()
			.get("depts/{deptId}", queryDeptId)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("id", equalTo(queryDeptId))
			.body("parentId", equalTo(parentId))
			.body("name", equalTo(name))
			.body("seq", equalTo(seq))
			.body("active", equalTo(active))
			.body("createTime", notNullValue())
			.body("createUserId", equalTo(user.getId()))
			.body("lastUpdateUserId", equalTo(user.getId()))
			.body("lastUpdateTime", notNullValue());
	}
	
}
