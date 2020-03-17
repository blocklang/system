package com.blocklang.system.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blocklang.system.model.UserInfo;

public interface UserService {

	Optional<UserInfo> findById(String userId);

	Optional<UserInfo> findByUsername(String username);

	UserInfo save(UserInfo user);

	/**
	 * 用户登录时更新最近登录时间和登录次数等信息
	 * 
	 * @param user 用户信息
	 * @return 更新之后的用户信息
	 */
	UserInfo login(UserInfo user);

	Page<UserInfo> findAll(Pageable pageable);
}
