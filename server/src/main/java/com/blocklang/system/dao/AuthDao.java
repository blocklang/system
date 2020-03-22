package com.blocklang.system.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.AuthInfo;

public interface AuthDao extends JpaRepository<AuthInfo, String>{

	List<AuthInfo> findAllByResourceId(String resourceId);

	List<AuthInfo> findAllByRoleId(String roleId);

}
