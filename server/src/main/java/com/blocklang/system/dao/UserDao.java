package com.blocklang.system.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.UserInfo;

public interface UserDao extends JpaRepository<UserInfo, Integer>{
	
	Optional<UserInfo> findById(Integer userId);

	Optional<UserInfo> findByUsername(String username);

}
