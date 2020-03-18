package com.blocklang.system.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blocklang.system.model.RoleInfo;

public interface RoleService {

	Optional<RoleInfo> findByAppIdAndName(String appId, String roleName);

	RoleInfo save(RoleInfo role);

	Optional<RoleInfo> findById(String roleId);

	Page<RoleInfo> findAllByAppId(String appId, Pageable pageable);

}
