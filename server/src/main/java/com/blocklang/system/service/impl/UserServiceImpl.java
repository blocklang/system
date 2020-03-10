package com.blocklang.system.service.impl;

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
	public Optional<UserInfo> findById(Integer userId) {
		return userDao.findById(userId);
	}

	@Override
	public Optional<UserInfo> findByUsername(String username) {
		return userDao.findByUsername(username);
	}

}
