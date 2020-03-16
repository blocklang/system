package com.blocklang.system.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.UserDao;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	@Override
	public Optional<UserInfo> findById(String userId) {
		return userDao.findById(userId);
	}

	@Override
	public Optional<UserInfo> findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public UserInfo save(UserInfo user) {
		// 如果没有管理员，则第一个注册的用户就是管理员，这个逻辑不严谨，比如误删了管理员，则普通用户就会成为管理员。
		// 所以这里判断用户表中没有记录时，才将此用户设置为管理员
		if(userDao.count() == 0) {
			user.setAdmin(true);
		}
		return userDao.save(user);
	}

	@Override
	public UserInfo login(UserInfo user) {
		user.setLastSignInTime(LocalDateTime.now());
		user.setSignInCount(user.getSignInCount() + 1);
		userDao.save(user);
		return user;
	}

}
