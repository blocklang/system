package com.blocklang.system.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.UserRoleInfo;

public interface UserRoleDao extends JpaRepository<UserRoleInfo, String>{

	List<UserRoleInfo> findAllByUserId(String userId);

}
