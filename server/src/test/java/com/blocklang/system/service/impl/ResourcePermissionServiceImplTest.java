package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.constant.Tree;
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
		assertThat(permissionService.canExecute(null, null, null)).isEmpty();
		assertThat(permissionService.canExecute(new UserInfo(), null, null)).isEmpty();
		
		UserInfo user = new UserInfo();
		user.setId(" ");
		assertThat(permissionService.canExecute(user, null, null)).isEmpty();
	}
	
	@Test
	public void canExecute_resource_id_is_null_or_blank() {
		UserInfo user = new UserInfo();
		user.setId("userId");
		
		assertThat(permissionService.canExecute(user, null, null)).isEmpty();
		assertThat(permissionService.canExecute(user, "", null)).isEmpty();
	}
	
	@Test
	public void canExecute_auth_tag_is_null_or_blank() {
		UserInfo user = new UserInfo();
		user.setId("userId");
		
		assertThat(permissionService.canExecute(user, "2", null)).isEmpty();
		assertThat(permissionService.canExecute(user, "2", "")).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
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
		auth.setResourceId(resourceId2);
		auth.setAppId(appId);
		auth.setCreateTime(LocalDateTime.now());
		auth.setCreateUserId(userId);
		authDao.save(auth);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isPresent();
	}
	
	/**
	 * 程序模块不存在
	 */
	@Test
	public void canExecute_operator_resource_is_not_exists() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		
		UserInfo user = new UserInfo();
		user.setId(userId);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
	}
	
	// 注意，准备数据时若出现此类数据，则就视为错误数据
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth("INVALID_AUTH");
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
		
		assertThat(permissionService.canExecute(user, resourceId0, Auth.INDEX)).isEmpty();
		assertThat(permissionService.canExecute(user, resourceId0, "INVALID_AUTH")).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
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
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
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
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
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
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isEmpty();
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
		resource.setAuth(Auth.INDEX);
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
		resource.setAuth(Auth.QUERY);
		resource.setCreateTime(LocalDateTime.now());
		resource.setCreateUserId(userId);
		resourceDao.save(resource);
		
		// resourceId1 + Auth.INDEX 的组合 = resourceId1
		assertThat(permissionService.canExecute(user, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(user, resourceId1, Auth.QUERY)).isPresent();
	}
}
