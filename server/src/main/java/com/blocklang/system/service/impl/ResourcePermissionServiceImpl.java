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
	public Optional<Boolean> canExecute(String userId, String resourceId, String auth) {
		if(userId == null || userId.isBlank()) {
			return Optional.empty();
		}
		if(resourceId == null || resourceId.isBlank()) {
			return Optional.empty();
		}
		if(auth == null || auth.isBlank()) {
			return Optional.empty();
		}
		if(auth == Auth.INDEX) { // 本方法不适用于页面，而 Auth.INDEX 专用于页面
			return Optional.empty();
		}
		
		// 获取为用户配置的角色
		// TODO: 过滤掉失效的角色，如果角色已失效，则直接删除为角色配置的模块信息？
		// 那么当角色激活之后，又要重新配置？
		List<UserRoleInfo> userRoles = userRoleDao.findAllByUserId(userId);
		if(userRoles.isEmpty()) {
			return Optional.empty();
		}
		
		// 获取程序模块标识，如果 auth 的值为 index，则直接 resourceId
		Optional<ResourceInfo> resourceOption = resourceDao.findByParentIdAndAuth(resourceId, auth);
		if(resourceOption.isEmpty()) {
			return Optional.empty();
		}
		ResourceInfo resource = resourceOption.get();
		if(resource.getResourceType() != ResourceType.OPERATOR) {
			return Optional.empty();
		}
		
		Optional<AppInfo> appOption = appDao.findById(resource.getAppId());
		if(appOption.isEmpty() || !appOption.get().getActive()) {
			return Optional.empty();
		}
		
		String parentResourceId = resource.getParentId();
		while(parentResourceId != Tree.ROOT_PARENT_ID) {
			Optional<ResourceInfo> parentResourceOption = resourceDao.findById(parentResourceId);
			if(parentResourceOption.isEmpty()) {
				return Optional.empty();
			}
			ResourceInfo parentResource = parentResourceOption.get();
			if(!parentResource.getActive()) {
				return Optional.empty();
			}
			parentResourceId = parentResource.getParentId();
		}
		
		// 获取为操作按钮关联的角色信息
		List<AuthInfo> authes = authDao.findAllByResourceId(resource.getId()).stream().filter(authInfo -> {
			Optional<RoleInfo> roleOption = roleDao.findById(authInfo.getRoleId());
			if(roleOption.isEmpty()) {
				return false;
			}
			if(!roleOption.get().getActive()) {
				return false;
			}
			
			return true;
		}).collect(Collectors.toList());
		
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
