package com.blocklang.system.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.RoleInfo;

public interface RoleDao extends JpaRepository<RoleInfo, String>{

	Optional<RoleInfo> findByAppIdAndName(String appId, String roleName);

	Page<RoleInfo> findAllByAppId(String appId, Pageable pageable);

}
