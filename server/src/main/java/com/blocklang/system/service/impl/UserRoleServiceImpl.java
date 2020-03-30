package com.blocklang.system.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.UserDao;
import com.blocklang.system.dao.UserRoleDao;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.model.UserRoleInfo;
import com.blocklang.system.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private UserDao userDao;
	
	@Override
	public List<UserInfo> findRoleUsers(String roleId) {
		return userRoleDao.findAllByRoleId(roleId).stream().map(userRole -> {
			// 找不到用户信息的情况，通常不会出现，因为不允许删除用户
			// 但是如果使用 flatMap，就会排除掉空用户，所有这里依然使用 map
			return userDao.findById(userRole.getUserId()).orElse(new UserInfo());
		}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void remove(String roleId, String userId) {
		userRoleDao.deleteByRoleIdAndUserId(roleId, userId);
	}

	@Override
	public UserRoleInfo add(UserRoleInfo userRole) {
		return userRoleDao.save(userRole);
	}

}
