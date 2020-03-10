package com.blocklang.system.service;

import java.util.Optional;

import com.blocklang.system.model.UserInfo;

public interface UserService {

	Optional<UserInfo> findById(Integer userId);

	Optional<UserInfo> findByUsername(String username);
}
