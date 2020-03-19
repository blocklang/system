package com.blocklang.system.controller.data;

import java.util.HashSet;
import java.util.Set;

/**
 * 资源权限
 * 
 * @author Zhengwei Jin
 *
 */
public class ResourcePermissionData {
	
	private String id; // 资源标识
	
	private Boolean canAccess = false; // 是否有权访问程序模块
	
	private Set<String> permissions = new HashSet<String>(); // 对某个资源有权执行的操作列表

	public ResourcePermissionData(String resourceId) {
		this.id = resourceId;
	}
	
	public String getId() {
		return id;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public Boolean getCanAccess() {
		return canAccess;
	}

	public void setCanAccess(Boolean canAccess) {
		this.canAccess = canAccess;
	}


	public void addPermission(String auth) {
		permissions.add(auth);
	}
	
}
