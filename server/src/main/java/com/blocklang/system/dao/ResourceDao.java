package com.blocklang.system.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.model.ResourceInfo;

public interface ResourceDao extends JpaRepository<ResourceInfo, String> {

	/**
	 * 根据 resourceId 和 auth 返回的值必然不会是多条记录，因为当资源为程序模块时，此约束唯一。
	 * 
	 * @param resourceId 资源标识，必须为程序模块
	 * @param auth 操作权限标识
	 * @return 资源信息
	 */
	Optional<ResourceInfo> findByParentIdAndAuth(String resourceId, String auth);

	List<ResourceInfo> findAllByParentIdAndResourceType(String resourceId, ResourceType resourceType);

	Optional<ResourceInfo> findByAppIdAndParentIdAndName(String appId, String parentResourceId, String resourceName);

	List<ResourceInfo> findAllByAppIdAndParentId(String appId, String resourceId, Sort sort);

}
