package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.dao.ResourceDao;
import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.service.ResourceService;
import com.blocklang.system.test.AbstractServiceTest;

public class ResourceServiceImplTest extends AbstractServiceTest{

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ResourceDao resourceDao;
	
	@Test
	public void save_app_id_parent_id_name_duplicated() {
		String appId = "1";
		String parentId = "2";
		String name = "3";
		
		ResourceInfo resource1 = new ResourceInfo();
		resource1.setId("1");
		resource1.setAppId(appId);
		resource1.setParentId(parentId);
		resource1.setName(name);
		resource1.setResourceType(ResourceType.PROGRAM);
		resource1.setCreateTime(LocalDateTime.now());
		resource1.setCreateUserId("1");
		resourceService.save(resource1);
		
		ResourceInfo resource2 = new ResourceInfo();
		resource2.setId("2");
		resource2.setAppId(appId);
		resource2.setParentId(parentId);
		resource2.setName(name);
		resource2.setResourceType(ResourceType.PROGRAM);
		resource2.setCreateTime(LocalDateTime.now());
		resource2.setCreateUserId("1");
		resourceService.save(resource2);
		
		assertThrows(DataIntegrityViolationException.class, ()-> {
			resourceDao.flush();
		});
	}
	
	@Test
	public void find_success() {
		String appId = "1";
		String parentId = "2";
		String name = "3";
		
		ResourceInfo resource1 = new ResourceInfo();
		resource1.setId("1");
		resource1.setAppId(appId);
		resource1.setParentId(parentId);
		resource1.setName(name);
		resource1.setResourceType(ResourceType.PROGRAM);
		resource1.setCreateTime(LocalDateTime.now());
		resource1.setCreateUserId("1");
		resourceDao.save(resource1);
		
		assertThat(resourceService.find(appId, parentId, name)).isPresent();
		assertThat(resourceService.find(appId, parentId, name + "1")).isEmpty();
	}
	
	@Test
	public void findById_success() {
		String appId = "1";
		String parentId = "2";
		String name = "3";
		
		ResourceInfo resource1 = new ResourceInfo();
		resource1.setId("1");
		resource1.setAppId(appId);
		resource1.setParentId(parentId);
		resource1.setName(name);
		resource1.setResourceType(ResourceType.PROGRAM);
		resource1.setCreateTime(LocalDateTime.now());
		resource1.setCreateUserId("1");
		resourceDao.save(resource1);
		
		assertThat(resourceService.findById("1")).isPresent();
		assertThat(resourceService.findById("2")).isEmpty();
	}
	
	@Test
	public void findChildren_success() {
		String appId = "1";
		
		ResourceInfo resource1 = new ResourceInfo();
		resource1.setId("1");
		resource1.setAppId(appId);
		resource1.setParentId("parentId1");
		resource1.setName("name1");
		resource1.setResourceType(ResourceType.PROGRAM);
		resource1.setCreateTime(LocalDateTime.now());
		resource1.setCreateUserId("1");
		resourceDao.save(resource1);
		
		ResourceInfo resource2 = new ResourceInfo();
		resource2.setId("2");
		resource2.setAppId(appId);
		resource2.setParentId("1");
		resource2.setName("name2");
		resource2.setResourceType(ResourceType.OPERATOR);
		resource2.setCreateTime(LocalDateTime.now());
		resource2.setCreateUserId("1");
		resourceDao.save(resource2);
		
		ResourceInfo resource3 = new ResourceInfo();
		resource3.setId("3");
		resource3.setAppId(appId);
		resource3.setParentId("1");
		resource3.setName("name3");
		resource3.setResourceType(ResourceType.OPERATOR);
		resource3.setCreateTime(LocalDateTime.now());
		resource3.setCreateUserId("1");
		resourceDao.save(resource3);
		
		assertThat(resourceService.findChildren(appId, "1", Sort.by("name").descending()))
			.hasSize(2)
			.first()
			.isEqualToComparingOnlyGivenFields(resource3, "name");
	}
	
}
