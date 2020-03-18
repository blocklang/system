package com.blocklang.system.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.RoleDao;
import com.blocklang.system.model.RoleInfo;
import com.blocklang.system.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;
	
	@Override
	public Optional<RoleInfo> findByAppIdAndName(String appId, String roleName) {
		return roleDao.findByAppIdAndName(appId, roleName);
	}

	@Override
	public RoleInfo save(RoleInfo role) {
		return roleDao.save(role);
	}

	@Override
	public Optional<RoleInfo> findById(String roleId) {
		return roleDao.findById(roleId);
	}

	@Override
	public Page<RoleInfo> findAllByAppId(String appId, Pageable pageable) {
		return roleDao.findAllByAppId(appId, pageable);
	}

}
