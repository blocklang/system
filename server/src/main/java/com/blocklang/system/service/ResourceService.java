package com.blocklang.system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.blocklang.system.model.ResourceInfo;

public interface ResourceService {

	Optional<ResourceInfo> findBy(String appId, String parentResourceId, String resourceName);

	ResourceInfo save(ResourceInfo resource);

	Optional<ResourceInfo> findById(String resourceId);

	/**
	 * 获取选中资源的直属子资源
	 * 
	 * @param appId       APP 标识
	 * @param resourceId  选中资源的标识
	 * @param sort        排序规则
	 * 
	 * @return 直属子资源列表
	 */
	List<ResourceInfo> findChildren(String appId, String resourceId, Sort sort);

}
