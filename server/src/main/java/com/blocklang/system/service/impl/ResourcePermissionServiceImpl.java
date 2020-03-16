package com.blocklang.system.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class ResourcePermissionServiceImpl implements ResourcePermissionService {

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
	
	@Override
	public Optional<Boolean> canExecute(UserInfo user, String resourceId, String auth) {
		if(user == null) {
			return Optional.empty();
		}
		String userId = user.getId();
		if(userId == null || userId.isBlank()) {
			return Optional.empty();
		}
		if(resourceId == null || resourceId.isBlank()) {
			return Optional.empty();
		}
		// 本方法不适用于页面，而 Auth.INDEX 专用于页面
		if(auth == null || auth.isBlank() || auth == Auth.INDEX) {
			return Optional.empty();
		}
		
		// 确保当前资源存在，且没有失效
		Optional<ResourceInfo> resourceOption = resourceDao.findByParentIdAndAuth(resourceId, auth);
		if(resourceOption.isEmpty()) {
			return Optional.empty();
		}
		// 管理员可以访问所有存在的资源，包括失效的资源
		if(user.isAdmin()) {
			return Optional.of(true);
		}

		ResourceInfo resource = resourceOption.get();
		// 本方法只用于校验按钮的操作权限
		if(resource.getResourceType() != ResourceType.OPERATOR) {
			return Optional.empty();
		}
		
		// 确保资源所归属的 APP 存在且没有失效
		Optional<AppInfo> appOption = appDao.findById(resource.getAppId());
		if(appOption.isEmpty() || !appOption.get().getActive()) {
			return Optional.empty();
		}
		
		// 确保所有父资源都没有失效，如果有一个失效则无权访问该资源
		String parentResourceId = resource.getParentId();
		while(parentResourceId != Tree.ROOT_PARENT_ID) {
			Optional<ResourceInfo> parentResourceOption = resourceDao.findById(parentResourceId);
			// 无需判断 parentResourceOption 是否为 empty，能执行到此处必然有值
			ResourceInfo parentResource = parentResourceOption.get();
			if(!parentResource.getActive()) {
				return Optional.empty();
			}
			parentResourceId = parentResource.getParentId();
		}
		
		// 获取为用户配置的角色，过滤掉失效的角色。
		// 此处过滤掉失效的角色后，就无需过滤授权信息中的失效角色。
		List<UserRoleInfo> userRoles = userRoleDao.findAllByUserId(userId).stream().filter(authInfo -> {
			Optional<RoleInfo> roleOption = roleDao.findById(authInfo.getRoleId());
			if(roleOption.isEmpty()) {
				return false;
			}
			if(!roleOption.get().getActive()) {
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		if(userRoles.isEmpty()) {
			return Optional.empty();
		}
		
		// 获取为操作按钮关联的角色信息
		List<AuthInfo> authes = authDao.findAllByResourceId(resource.getId());
		
		// 匹配 userRoles 和 authes 中的角色列表，有一个能匹配上就称为有权限
		boolean matched = false;
		for(UserRoleInfo userRole : userRoles) {
			for(AuthInfo authInfo: authes) {
				if(userRole.getRoleId().equals(authInfo.getRoleId())) {
					matched = true;
					break;
				}
			}
		}
		
		if(!matched) {
			return Optional.empty();
		}
		return Optional.of(true);
	}
	
}
