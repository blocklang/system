package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.constant.Tree;
import com.blocklang.system.controller.data.ResourcePermissionData;
import com.blocklang.system.dao.AppDao;
import com.blocklang.system.dao.AuthDao;
import com.blocklang.system.dao.ResourceDao;
import com.blocklang.system.dao.RoleDao;
import com.blocklang.system.dao.UserRoleDao;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.model.AuthInfo;
import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.model.RoleInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.model.UserRoleInfo;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.test.AbstractServiceTest;

public class ResourcePermissionServiceImplTest extends AbstractServiceTest{

	@Autowired
	private AppDao appDao;
	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private AuthDao authDao;
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private ResourcePermissionService permissionService;
	
	@Test
	public void canExecute_user_is_null_or_blank() {
		assertThat(permissionService.canExecute(null, null)).isEmpty();
		assertThat(permissionService.canExecute(new UserInfo(), null)).isEmpty();
		
		UserInfo user = new UserInfo();
		user.setId(" ");
		assertThat(permissionService.canExecute(user, null)).isEmpty();
	}
	
	@Test
	public void canExecute_auth_tag_is_null_or_blank() {
		UserInfo user = new UserInfo();
		user.setId("userId");
		
		assertThat(permissionService.canExecute(user,  null)).isEmpty();
		assertThat(permissionService.canExecute(user, "")).isEmpty();
	}
	
	/**
	 * 要测试的话，需要在以下表中添加数据：
	 * 
	 * 1. App 信息
	 * 2. 用户信息
	 * 3. 程序模块
	 * 4. 角色信息
	 * 5. 用户与角色关系
	 * 6. 角色与资源关系
	 * 
	 * 但在准备测试数据时，不需在以下表中插入数据，因为 App 信息等表不需要真正保存数据，只需要提供一个 appId 即可：
	 * 
	 * 1. App 信息
	 * 2. 用户信息
	 * 3. 角色信息
	 */
	@Test
	//@Timeout(value = 20, unit = TimeUnit.MILLISECONDS)
	public void canExecute_success() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		role.setActive(true);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		auth = new AuthInfo();
		auth.setId("authId2");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		// 这个是程序模块，不能用 execute 判断
		assertThat(permissionService.canExecute(user, "auth1")).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isPresent();
	}
	
	/**
	 * 程序模块不存在
	 */
	@Test
	public void canExecute_operator_resource_is_not_exists() {
		String userId = "userId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		assertThat(permissionService.canExecute(user, "not-exist-auth")).isEmpty();
	}
	
	@Test
	public void canExecute_operator_resource_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setActive(false);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// canExecute 不能用于程序模块，专用于操作按钮权限
		assertThat(permissionService.canExecute(user, "auth1")).isEmpty();
		
		// 过滤掉失效的操作权限
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	/**
	 * 程序模块未启用
	 */
	@Test
	public void canExecute_program_resource_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(false);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块已失效，所以用户无权访问操作按钮
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	/**
	 * 功能模块未启用
	 */
	@Test
	public void canExecute_function_resource_is_not_active() {
		String userId = "userId1";
		String resourceId0 = "resourceId0";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
				
		// 功能模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId0);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setActive(false);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		// 程序模块
		resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(resourceId0);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth3");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);

		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth3")).isEmpty();
	}

	/**
	 * APP 不存在
	 */
	@Test
	public void canExecute_app_is_not_exist() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);

		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	/**
	 * APP未启用
	 */
	@Test
	public void canExecute_app_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(false);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	@Test
	public void canExecute_parent_resource_is_not_exist() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId("not-exist-parent-id");
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);

		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	// 注意，准备数据时若出现此测试用例中的数据，则就视为错误数据
	@Test
	public void canExecute_can_not_apply_to_program_resource() {
		String userId = "userId1";
		String resourceId0 = "resourceId0";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId0);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		// 子程序模块1
		resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(resourceId0);
		resource.setAppId(appId);
		resource.setName("resourceName1");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		// 子程序模块2
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId0);
		resource.setAppId(appId);
		resource.setName("resourceName2");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth3");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		auth = new AuthInfo();
		auth.setId("authId21");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
		assertThat(permissionService.canExecute(user, "auth3")).isEmpty();
	}
	
	@Test
	public void canExecute_role_is_not_exists() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);

		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	@Test
	public void canExecute_role_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		role.setActive(false);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);

		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	@Test
	public void canExecute_user_role_and_resource_role_is_not_matched() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId1 = "roleId1";
		String roleId2 = "roleId2";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId1);
		role.setName("roleName1");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		role = new RoleInfo();
		role.setId(roleId2);
		role.setName("roleName2");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId1);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId2);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);

		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isEmpty();
	}
	
	// 管理员也有权访问未启用的资源
	// 但如果资源不存在，则也无权访问
	@Test
	public void canExecute_admin_can_execute_all_resources() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		user.setAdmin(true);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, "auth1")).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, "auth2")).isPresent();
	}

	@Test
	public void getPermission_user_id_or_resource_id_is_blank() {
		assertThat(permissionService.getPermission(null, null)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(null));
		assertThat(permissionService.getPermission(new UserInfo(), null)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(null));
		assertThat(permissionService.getPermission(null, " ")).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(" "));
		assertThat(permissionService.getPermission(new UserInfo(), " ")).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(" "));
		assertThat(permissionService.getPermission(new UserInfo(), "1")).usingRecursiveComparison().isEqualTo(new ResourcePermissionData("1"));
		
		UserInfo user = new UserInfo();
		user.setId(" ");
		assertThat(permissionService.getPermission(user, "1")).usingRecursiveComparison().isEqualTo(new ResourcePermissionData("1"));
	}
	
	@Test
	@Timeout(value = 50, unit = TimeUnit.MILLISECONDS)
	public void getPermission_success() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String resourceId3 = "resourceId3";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的新增操作（没有为用户配置此权限）
		resource = new ResourceInfo();
		resource.setId(resourceId3);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op2");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth3");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		role.setActive(true);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		auth = new AuthInfo();
		auth.setId("authId2");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		ResourcePermissionData expected = new ResourcePermissionData(resourceId1);
		expected.setCanAccess(true);
		expected.setPermissions(new HashSet<String>(Arrays.asList("auth2")));
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(expected);
	}
	
	/**
	 * 程序模块不存在
	 */
	@Test
	public void getPermission_program_resource_is_not_exists() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	/**
	 * 程序模块未启用
	 */
	@Test
	public void getPermission_program_resource_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(false);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	/**
	 * APP 不存在
	 */
	@Test
	public void getPermission_app_is_not_exist() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	/**
	 * APP未启用
	 */
	@Test
	public void getPermission_app_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(false);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	/**
	 * 功能模块未启用
	 */
	@Test
	public void getPermission_function_resource_is_not_active() {
		String userId = "userId1";
		String resourceId0 = "resourceId0";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
				
		// 功能模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId0);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setActive(false);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		// 程序模块
		resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(resourceId0);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	// 功能模块启用，但没有为程序模块授权
	@Test
	public void getPermission_function_resource_is_active() {
		String userId = "userId1";
		String resourceId0 = "resourceId0";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// App
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		app.setActive(true);
		appDao.save(app);
				
		// 功能模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId0);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setActive(true);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		// 程序模块
		resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(resourceId0);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(true);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	@Test
	public void getPermission_role_is_not_exists() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	@Test
	public void getPermission_role_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		role.setActive(false);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	@Test
	public void getPermission_user_role_and_program_resource_role_is_not_matched() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		String roleId1 = "roleId1";
		String roleId2 = "roleId2";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId1);
		role.setName("roleName1");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		role = new RoleInfo();
		role.setId(roleId2);
		role.setName("roleName2");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId1);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId2);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(new ResourcePermissionData(resourceId1));
	}
	
	@Test
	public void getPermission_operator_resource_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId1 = "roleId1";
		String roleId2 = "roleId2";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setActive(false);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId1);
		role.setName("roleName1");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		role = new RoleInfo();
		role.setId(roleId2);
		role.setName("roleName2");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId1);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId1);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		// 但是操作的权限分给了 role2
		auth = new AuthInfo();
		auth.setId("authId2");
		auth.setRoleId(roleId2);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		ResourcePermissionData expected = new ResourcePermissionData(resourceId1);
		expected.setCanAccess(true);
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	public void getPermission_user_role_and_operator_resource_role_is_not_matched() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId1 = "roleId1";
		String roleId2 = "roleId2";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId1);
		role.setName("roleName1");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		role = new RoleInfo();
		role.setId(roleId2);
		role.setName("roleName2");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId1);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId1);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		// 但是操作的权限分给了 role2
		auth = new AuthInfo();
		auth.setId("authId2");
		auth.setRoleId(roleId2);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		ResourcePermissionData expected = new ResourcePermissionData(resourceId1);
		expected.setCanAccess(true);
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(expected);
	}
	
	// 如果资源不存在，则管理员也无访问权限，
	// 对于模块权限，要具体罗列出程序模块的操作，并让管理员具有所有操作权限
	@Test
	public void getPermission_admin_can_execute_all_operators() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		user.setAdmin(true);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块中的查询操作
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-op");
		resource.setResourceType(ResourceType.OPERATOR);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 虽然此处没有配置权限，但管理员依然具有权限
		
		ResourcePermissionData expected = new ResourcePermissionData(resourceId1);
		expected.setCanAccess(true);
		expected.setPermissions(new HashSet<String>(Arrays.asList("auth2")));
		assertThat(permissionService.getPermission(user, resourceId1)).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	public void getUserChildResources_user_id_or_resource_id_is_blank() {
		assertThat(permissionService.getUserChildResources(null, null)).isEmpty();
		assertThat(permissionService.getUserChildResources(new UserInfo(), null)).isEmpty();
		assertThat(permissionService.getUserChildResources(null, " ")).isEmpty();
		assertThat(permissionService.getUserChildResources(new UserInfo(), " ")).isEmpty();
		assertThat(permissionService.getUserChildResources(new UserInfo(), "1")).isEmpty();
		
		UserInfo user = new UserInfo();
		user.setId(" ");
		assertThat(permissionService.getUserChildResources(user, "1")).isEmpty();
	}
	
	@Test
	//@Timeout(value = 20, unit = TimeUnit.MILLISECONDS)
	public void getUserChildResources_success() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String resourceId3 = "resourceId3";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 功能模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块1
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-program1");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块2
		resource = new ResourceInfo();
		resource.setId(resourceId3);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-program2");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth3");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		role.setActive(true);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		auth = new AuthInfo();
		auth.setId("authId2");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		auth = new AuthInfo();
		auth.setId("authId3");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId3);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		// 根节点下有一个功能模块
		assertThat(permissionService.getUserChildResources(user, Tree.ROOT_PARENT_ID)).hasSize(1);
		// 此功能模块下有两个程序模块
		assertThat(permissionService.getUserChildResources(user, resourceId1)).hasSize(2);
	}
	
	@Test
	public void getUserChildResources_role_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 功能模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		role.setActive(false);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		userRole = new UserRoleInfo();
		userRole.setId("userRoleId2");
		userRole.setUserId(userId);
		userRole.setRoleId("not-exist");// 关联的角色不存在
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId); // 关联的角色失效
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);

		assertThat(permissionService.getUserChildResources(user, Tree.ROOT_PARENT_ID)).hasSize(0);
	}
	
	@Test
	public void getUserChildResources_resource_is_not_active() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 功能模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setActive(false);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 角色
		RoleInfo role = new RoleInfo();
		role.setId(roleId);
		role.setName("roleName");
		role.setAppId(appId);
		role.setCreateTime(LocalDateTime.now());
		role.setCreateUserId(userId);
		roleDao.save(role);
		
		// 用户与角色关联
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(userRoleId);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(userId);
		userRoleDao.save(userRole);
		
		// 角色与资源关联
		AuthInfo auth = new AuthInfo();
		auth.setId("authId1");
		auth.setRoleId(roleId);
		auth.setResourceId(resourceId1);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		auth = new AuthInfo();
		auth.setId("authId2");
		auth.setRoleId(roleId);
		auth.setResourceId("not-exist"); // 该资源在 sys_resource 表中未定义
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);

		assertThat(permissionService.getUserChildResources(user, Tree.ROOT_PARENT_ID)).hasSize(0);
	}
	
	// 不用配置权限，管理员也能访问所有启用的模块
	@Test
	public void getUserChildResources_admin_can_access_active_resources() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String resourceId3 = "resourceId3";
		String appId = "appId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		user.setAdmin(true);
		
		AppInfo app = new AppInfo();
		app.setId(appId);
		app.setName("appName");
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(userId);
		appDao.save(app);
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
		resource.setParentId(Tree.ROOT_PARENT_ID);
		resource.setAppId(appId);
		resource.setName("resourceName-func1");
		resource.setResourceType(ResourceType.FUNCTION);
		resource.setAuth("auth1");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 程序模块1
		resource = new ResourceInfo();
		resource.setId(resourceId2);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-program1");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setAuth("auth2");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		// 程序模块2
		resource = new ResourceInfo();
		resource.setId(resourceId3);
		resource.setParentId(resourceId1);
		resource.setAppId(appId);
		resource.setName("resourceName-program2");
		resource.setResourceType(ResourceType.PROGRAM);
		resource.setActive(false);
		resource.setAuth("auth3");
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// 虽然此处没有配置权限，但管理员依然具有权限
		
		assertThat(permissionService.getUserChildResources(user, Tree.ROOT_PARENT_ID)).hasSize(1);
		// 此功能模块下有两个程序模块，一个启用，一个未启用
		assertThat(permissionService.getUserChildResources(user, resourceId1)).hasSize(1);
	}
	
	
	// TODO: 管理员能访问所有操作
	// 如果是管理员，则返回 *，还是查出所有操作？
	// 因为不需要为管理员配置任何权限。还是查出为资源配置的所有权限？
	// 系统管理员能访问失效的程序模块及其所有操作
	
	// 程序模块不存在
	// 程序模块未启用
	// 功能模块未启用
	// APP 不存在
	// APP 未启用
	
}
