package com.blocklang.system.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.UserInfo;

public interface UserDao extends JpaRepository<UserInfo, String>{
	
	Optional<UserInfo> findById(String userId);

	Optional<UserInfo> findByUsername(String username);

}
