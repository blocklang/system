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
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.test.AbstractServiceTest;

public class AppServiceImplTest extends AbstractServiceTest{

	@Autowired
	private AppService appService;
	@Autowired
	private AppDao appDao;
	
	@Test
	public void findById_no_data() {
		assertThat(appService.findById("1")).isEmpty();
	}
	
	@Test
	public void findAll_no_data() {
		Pageable pageable = PageRequest.of(1, 1);
		assertThat(appService.findAll(pageable)).isEmpty();
	}
	
	@Test
	public void save_success_user_id_and_name_duplicated() {
		String userId = "user1";
		String name = "app1";
		
		AppInfo app1 = new AppInfo();
		app1.setId("1");
		app1.setName(name);
		app1.setCreateUserId(userId);
		app1.setCreateTime(LocalDateTime.now());
		appService.save(app1);
		
		AppInfo app2 = new AppInfo();
		app2.setId("2");
		app2.setName(name);
		app2.setCreateUserId(userId);
		app2.setCreateTime(LocalDateTime.now());
		appService.save(app2);
		
		assertThrows(DataIntegrityViolationException.class, () -> {
			appDao.flush();
		});
	}
	
	@Test
	public void find_success() {
		String userId = "user1";
		String name = "app1";
		
		AppInfo app1 = new AppInfo();
		app1.setId("1");
		app1.setName(name);
		app1.setCreateUserId(userId);
		app1.setCreateTime(LocalDateTime.now());
		appService.save(app1);
		
		assertThat(appService.find(userId, name)).isPresent();
		assertThat(appService.find(userId, "not-exist")).isEmpty();
	}
}
