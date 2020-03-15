package com.blocklang.system.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.blocklang.system.service.ResourcePermissionService;

@Service
public class ResourcePermissionServiceImpl implements ResourcePermissionService {

	@Override
	public Optional<Boolean> canAccess(String userId, String resourceId, String auth) {
		// TODO Auto-generated method stub
		return Optional.of(true);
	}
	
}
