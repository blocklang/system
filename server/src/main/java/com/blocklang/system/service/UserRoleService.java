package com.blocklang.system.service;

import java.util.List;

import com.blocklang.system.model.UserInfo;
import com.blocklang.system.model.UserRoleInfo;

public interface UserRoleService {

	List<UserInfo> findRoleUsers(String roleId);

	void remove(String roleId, String userId);

	UserRoleInfo add(UserRoleInfo userRole);

}
