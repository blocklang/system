package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.dao.UserRoleDao;
import com.blocklang.system.model.UserRoleInfo;
import com.blocklang.system.service.UserRoleService;
import com.blocklang.system.test.AbstractServiceTest;

public class UserRoleServiceImplTest extends AbstractServiceTest{

	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Test
	public void findRoleUsers_success() {
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId("1");
		userRole.setRoleId("roleId");
		userRole.setUserId("userId");
		userRole.setCreateUserId("userId");
		userRole.setCreateTime(LocalDateTime.now());
		userRoleService.add(userRole);
		
		assertThat(userRoleService.findRoleUsers("roleId")).hasSize(1);
	}
	
	@Test
	public void remove_success() {
		String roleId = "roleId";
		String userId = "userId";
		
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId("1");
		userRole.setRoleId(roleId);
		userRole.setUserId(userId);
		userRole.setCreateUserId("userId");
		userRole.setCreateTime(LocalDateTime.now());
		userRoleService.add(userRole);
		
		assertThat(userRoleDao.findById("1")).isPresent();
		userRoleService.remove(roleId, userId);
		assertThat(userRoleDao.findById("1")).isEmpty();
	}
	
}
