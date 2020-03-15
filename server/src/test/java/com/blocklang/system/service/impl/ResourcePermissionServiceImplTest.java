package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.dao.AuthDao;
import com.blocklang.system.dao.ResourceDao;
import com.blocklang.system.dao.UserRoleDao;
import com.blocklang.system.model.AuthInfo;
import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.model.UserRoleInfo;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.test.AbstractServiceTest;

public class ResourcePermissionServiceImplTest extends AbstractServiceTest{

	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private AuthDao authDao;
	
	@Autowired
	private ResourcePermissionService permissionService;
	
	@Test
	public void canAccess_user_id_is_null_or_blank() {
		assertThat(permissionService.canExecute(null, null, null)).isEmpty();
		assertThat(permissionService.canExecute("", null, null)).isEmpty();
	}
	
	@Test
	public void canAccess_resource_id_is_null_or_blank() {
		assertThat(permissionService.canExecute("1", null, null)).isEmpty();
		assertThat(permissionService.canExecute("1", "", null)).isEmpty();
	}
	
	@Test
	public void canAccess_auth_tag_is_null_or_blank() {
		assertThat(permissionService.canExecute("1", "2", null)).isEmpty();
		assertThat(permissionService.canExecute("1", "2", "")).isEmpty();
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
	public void canAccess_success() {
		String userId = "userId1";
		String resourceId1 = "resourceId1";
		String resourceId2 = "resourceId2";
		String appId = "appId1";
		String roleId = "roleId1";
		String userRoleId = "userRoleId1";
		
		// 程序模块
		ResourceInfo resource = new ResourceInfo();
		resource.setId(resourceId1);
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
		assertThat(permissionService.canExecute(userId, resourceId1, Auth.INDEX)).isEmpty();
		
		// resourceId1 + Auth.QUERY 的组合 = resourceId2
		assertThat(permissionService.canExecute(userId, resourceId1, Auth.QUERY)).isPresent();
	}
}
