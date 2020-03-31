package com.blocklang.system.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.RoleResourceDao;
import com.blocklang.system.model.AuthInfo;
import com.blocklang.system.service.RoleResourceService;
import com.blocklang.system.utils.IdGenerator;

@Service
public class RoleResourceServiceImpl implements RoleResourceService {

	@Autowired
	private RoleResourceDao roleResourceDao;
	
	@Override
	public List<AuthInfo> findRoleResources(String roleId) {
		return roleResourceDao.findAllByRoleId(roleId);
	}

	@Transactional
	@Override
	public void batchUpdate(String roleId, String appId, List<String> resources, String createUserId) {
		// 删除一个角色下的所有资源
		roleResourceDao.deleteByRoleId(roleId);
		List<AuthInfo> auths = resources.stream().map(resourceId -> {
			AuthInfo authInfo = new AuthInfo();
			authInfo.setId(IdGenerator.uuid());
			authInfo.setAppId(appId);
			authInfo.setRoleId(roleId);
			authInfo.setResourceId(resourceId);
			authInfo.setCreateUserId(createUserId);
			authInfo.setCreateTime(LocalDateTime.now());
			return authInfo;
		}).collect(Collectors.toList());
		roleResourceDao.saveAll(auths);
	}

}
