package com.blocklang.system.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.ResourceDao;
import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

	@Autowired
	private ResourceDao resourceDao;
	
	@Override
	public Optional<ResourceInfo> find(String appId, String parentResourceId, String resourceName) {
		// 因为此时所有 APP 顶层资源的父标识都是 -1，需要结合 appId，查询出顶层资源
		// 而如果父标识不是 -1，因为 parentResourceId 本身就能确保唯一，所以可不使用 appId 这个条件
		// 但本实现中使用了三个条件。
		
		return resourceDao.findByAppIdAndParentIdAndName(appId, parentResourceId, resourceName);
	}

	@Override
	public ResourceInfo save(ResourceInfo resource) {
		return resourceDao.save(resource);
	}

	@Override
	public Optional<ResourceInfo> findById(String resourceId) {
		return resourceDao.findById(resourceId);
	}

	@Override
	public List<ResourceInfo> findChildren(String appId, String resourceId, Sort sort) {
		return resourceDao.findAllByAppIdAndParentId(appId, resourceId, sort);
	}

}
