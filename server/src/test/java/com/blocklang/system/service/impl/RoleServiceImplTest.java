package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.blocklang.system.dao.AppDao;
import com.blocklang.system.dao.RoleDao;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.model.RoleInfo;
import com.blocklang.system.service.RoleService;
import com.blocklang.system.test.AbstractServiceTest;

public class RoleServiceImplTest extends AbstractServiceTest{

	@Autowired
	private RoleService roleService;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private AppDao appDao;
	
	@Test
	public void findByAppIdAndName_no_data() {
		assertThat(roleService.findByAppIdAndName("1", "role")).isEmpty();
	}
	
	@Test
	public void findByAppIdAndName_success() {
		String appId = "1";
		String roleName = "role";
		
		RoleInfo role1 = new RoleInfo();
		role1.setId("1");
		role1.setName(roleName);
		role1.setAppId(appId);
		role1.setCreateTime(LocalDateTime.now());
		role1.setCreateUserId("1");
		roleDao.save(role1);
		
		RoleInfo role2 = new RoleInfo();
		role2.setId("2");
		role2.setName("role");
		role2.setAppId("2");
		role2.setCreateTime(LocalDateTime.now());
		role2.setCreateUserId("1");
		roleDao.save(role2);
		
		assertThat(roleService.findByAppIdAndName(appId, roleName)).isPresent();
	}
	
	@Test
	public void save_app_id_and_name_is_duplicated() {
		String appId = "1";
		String roleName = "role";
		
		RoleInfo role1 = new RoleInfo();
		role1.setId("1");
		role1.setName(roleName);
		role1.setAppId(appId);
		role1.setCreateTime(LocalDateTime.now());
		role1.setCreateUserId("1");
		roleService.save(role1);
		
		RoleInfo role2 = new RoleInfo();
		role2.setId("2");
		role2.setName(roleName);
		role2.setAppId(appId);
		role2.setCreateTime(LocalDateTime.now());
		role2.setCreateUserId("1");
		roleService.save(role2);
		// 如果使用 save 则不会做真正的保存，所以不会抛异常
		// 这里需要立即生效，所以加上 flush 操作
		assertThrows(DataIntegrityViolationException.class, ()-> {
			roleDao.flush();
		});
	}
	
	@Test
	public void findById_no_data() {
		assertThat(roleService.findById("1")).isEmpty();
	}
	
	@Test
	public void findById_success() {
		String appId = "1";
		String appName = "appName";
		String roleName = "role";

		AppInfo app1 = new AppInfo();
		app1.setId("1");
		app1.setName(appName);
		app1.setCreateUserId("userId");
		app1.setCreateTime(LocalDateTime.now());
		appDao.save(app1);
		
		RoleInfo role1 = new RoleInfo();
		role1.setId("1");
		role1.setName(roleName);
		role1.setAppId(appId);
		role1.setCreateTime(LocalDateTime.now());
		role1.setCreateUserId("1");
		roleDao.save(role1);
		
		assertThat(roleService.findById(appId)).isPresent().get().extracting("appName").isEqualTo(appName);
	}
	
	@Test
	public void findAllByAppId_no_data() {
		Pageable pageable = PageRequest.of(0, 1);
		assertThat(roleService.findAllByAppId("1", pageable)).isEmpty();
	}
	
	@Test
	public void findAllByAppId_success() {
		String appId = "1";
		
		RoleInfo role1 = new RoleInfo();
		role1.setId("1");
		role1.setName("role1");
		role1.setAppId(appId);
		role1.setCreateTime(LocalDateTime.now());
		role1.setCreateUserId("1");
		roleDao.save(role1);
		
		RoleInfo role2 = new RoleInfo();
		role2.setId("2");
		role2.setName("role2");
		role2.setAppId(appId);
		role2.setCreateTime(LocalDateTime.now());
		role2.setCreateUserId("1");
		roleDao.save(role2);
		
		Pageable pageable = PageRequest.of(0, 2);
		assertThat(roleService.findAllByAppId(appId, pageable)).hasSize(2);
	}
}
