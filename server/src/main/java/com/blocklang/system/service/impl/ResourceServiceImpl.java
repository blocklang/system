package com.blocklang.system.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

	@Override
	public Optional<ResourceInfo> findBy(String appId, String parentResourceId, String resourceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceInfo save(ResourceInfo resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ResourceInfo> findById(String resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourceInfo> findChildren(String appId, String resourceId, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

}
