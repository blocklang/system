package com.blocklang.system.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.blocklang.system.model.AuthInfo;

public interface RoleResourceDao extends JpaRepository<AuthInfo, String>{

	List<AuthInfo> findAllByRoleId(String roleId);

	@Modifying
	@Query("delete from AuthInfo a where a.roleId = ?1")
	void deleteByRoleId(String roleId);

}
