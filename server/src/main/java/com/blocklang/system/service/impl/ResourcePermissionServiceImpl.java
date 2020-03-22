package com.blocklang.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blocklang.system.constant.Auth;
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
		// FIXME: 待确认逻辑，是否实现了程序模块未失效的判断
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
		
		if(!matchRole(userRoles, authes)) {
			return Optional.empty();
		}
		return Optional.of(true);
	}
	
	@Override
	public ResourcePermissionData getPermission(UserInfo user, String resourceId) {
		ResourcePermissionData result = new ResourcePermissionData(resourceId);
		
		if(user == null) {
			return result;
		}
		if(resourceId == null || resourceId.isBlank()) {
			return result;
		}
		String userId = user.getId();
		if(userId == null || userId.isBlank()) {
			return result;
		}
		
		// 确保当前资源存在，且没有失效
		Optional<ResourceInfo> resourceOption = resourceDao.findById(resourceId);
		if(resourceOption.isEmpty()) {
			return result;
		}
		
		// 如果是管理员，则只要模块存在(不论 APP、功能模块或程序模块是否启用)，就有访问权限
		if(user.isAdmin()) {
			result.setCanAccess(true);
			resourceDao.findAllByParentIdAndResourceType(resourceId, ResourceType.OPERATOR).forEach(item -> {
				// 即使子资源失效，系统管理员也能访问
				result.addPermission(item.getAuth());
			});
			return result;
		}
		
		ResourceInfo resource = resourceOption.get();
		
		if(!resource.getActive()) {
			return result;
		}
		
		// 确保资源所归属的 APP 存在且没有失效
		Optional<AppInfo> appOption = appDao.findById(resource.getAppId());
		if(appOption.isEmpty() || !appOption.get().getActive()) {
			return result;
		}
				
		// 确保所有父资源都没有失效，如果有一个失效则无权访问该资源
		String parentResourceId = resource.getParentId();
		while(parentResourceId != Tree.ROOT_PARENT_ID) {
			Optional<ResourceInfo> parentResourceOption = resourceDao.findById(parentResourceId);
			// 无需判断 parentResourceOption 是否为 empty，能执行到此处必然有值
			ResourceInfo parentResource = parentResourceOption.get();
			if(!parentResource.getActive()) {
				return result;
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
			return result;
		}
		
		// 如果有权访问程序模块，则将 access 设置为 true
		List<AuthInfo> authes = authDao.findAllByResourceId(resourceId);

		if(matchRole(userRoles, authes)) {
			result.setCanAccess(true);
		}
		// 如果对程序模块本身都没有访问权限，则对模块中的操作也不应该有权限。
		if(!result.getCanAccess()) {
			return result;
		}
		
		// 获取为资源和所有子资源配置的角色
		// 用户的角色与资源授予的角色进行匹配
		
		// 获取资源的所有为操作类型的子资源
		resourceDao.findAllByParentIdAndResourceType(resourceId, ResourceType.OPERATOR).forEach(item -> {
			// 也要确保操作资源未失效
			if(item.getActive()) {
				List<AuthInfo> childAuthes = authDao.findAllByResourceId(item.getId());
				if(matchRole(userRoles, childAuthes)) {
					result.addPermission(item.getAuth());
				}
			}
		});
		
		return result;
	}
	

	/**
	 * 匹配 userRoles 和 authes 中的角色列表，有一个角色能匹配上就称为有权限
	 * 
	 * @param userRoles 用户拥有的角色列表
	 * @param authes 资源拥有的角色列表
	 * @return 如果有权限则返回 <code>true</code>；否则返回 <code>false</code>
	 */
	private boolean matchRole(List<UserRoleInfo> userRoles, List<AuthInfo> authes) {
		for(UserRoleInfo userRole : userRoles) {
			for(AuthInfo authInfo: authes) {
				if(userRole.getRoleId().equals(authInfo.getRoleId())) {
					return true;
				}
			}
		}
		return false;
	}

	// 这里假定传入的 resourceId 必然存在且启用的，则父资源也必然是有效且启用的，所以不在此方法中校验父资源
	@Override
	public List<ResourceInfo> getUserChildResources(UserInfo user, String resourceId) {
		List<ResourceInfo> result = new ArrayList<ResourceInfo>();
		
		if(user == null) {
			return result;
		}
		if(resourceId == null || resourceId.isBlank()) {
			return result;
		}
		String userId = user.getId();
		if(userId == null || userId.isBlank()) {
			return result;
		}
		
		if(user.isAdmin()) {
			return resourceDao.findAllByParentId(resourceId).stream().filter(item -> item.getActive()).collect(Collectors.toList());
		}
		// TODO: 判断用户是否禁用，如果禁用直接返回
		// 1. 获取用户的所有角色，过滤掉失效的角色
		// 2. 获取每个角色能访问的所有程序模块，过滤掉失效的程序模块
		// 3. 过滤掉重复的程序模块
		
		result = userRoleDao.findAllByUserId(userId)
			.stream()
			.filter(userRole -> {
				Optional<RoleInfo> roleOption = roleDao.findById(userRole.getRoleId());
				if(roleOption.isEmpty()) {
					return false;
				}
				if(!roleOption.get().getActive()) {
					return false;
				}
				return true;
			})
			.flatMap(userRole -> authDao.findAllByRoleId(userRole.getRoleId()).stream().map(authInfo -> authInfo.getResourceId()))
			.distinct()
			.map(resId -> {
				// FIXME:最好只查出直属子模块
				Optional<ResourceInfo> resourceOption = resourceDao.findById(resId);
				return resourceOption;
			})
			.filter(resourceOption -> !resourceOption.isEmpty() && resourceOption.get().getActive() && resourceOption.get().getParentId().equals(resourceId))
			.flatMap(resourceOption -> resourceOption.stream())
			.collect(Collectors.toList());
		
		return result;
	}
}
