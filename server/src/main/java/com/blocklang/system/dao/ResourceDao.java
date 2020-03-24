package com.blocklang.system.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.model.ResourceInfo;

public interface ResourceDao extends JpaRepository<ResourceInfo, String> {

	/**
	 * 根据  auth 返回的值必然不会是多条记录。
	 * 
	 * @param auth 操作权限标识，全局唯一
	 * @return 资源信息
	 */
	Optional<ResourceInfo> findByAuth(String auth);

	List<ResourceInfo> findAllByParentIdAndResourceType(String resourceId, ResourceType resourceType);

	Optional<ResourceInfo> findByAppIdAndParentIdAndName(String appId, String parentResourceId, String resourceName);

	List<ResourceInfo> findAllByAppIdAndParentId(String appId, String resourceId, Sort sort);

	List<ResourceInfo> findAllByParentId(String resourceId);

}
