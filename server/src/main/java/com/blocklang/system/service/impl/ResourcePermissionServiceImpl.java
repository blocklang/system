package com.blocklang.system.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.blocklang.system.service.ResourcePermissionService;

@Service
public class ResourcePermissionServiceImpl implements ResourcePermissionService {

	@Override
	public Optional<Boolean> canAccess(String userId, String resourceId, String auth) {
		if(userId == null || userId.isBlank()) {
			return Optional.empty();
		}
		if(resourceId == null || resourceId.isBlank()) {
			return Optional.empty();
		}
		if(auth == null || auth.isBlank()) {
			return Optional.empty();
		}
		
		return Optional.of(true);
	}
	
}
