package com.blocklang.system.service;

import java.util.List;

import com.blocklang.system.model.AuthInfo;

public interface RoleResourceService {

	List<AuthInfo> findRoleResources(String roleId);

	void batchUpdate(String roleId, String appId, List<String> resources, String createUserId);

}
