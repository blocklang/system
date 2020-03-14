package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.model.AppInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.test.AbstractServiceTest;

public class AppServiceImplTest extends AbstractServiceTest{

	@Autowired
	private AppService appService;
	
	@Test
	public void findById_no_data() {
		assertThat(appService.findById("1")).isEmpty();
	}
	
	@Test
	public void findAll_no_data() {
		assertThat(appService.findAll()).isEmpty();
	}
	
	@Test
	public void save_success() {
		AppInfo app = new AppInfo();
		app.setId("1");
		app.setName("app1");
		app.setCreateUserId("user1");
		app.setCreateTime(LocalDateTime.now());
		
		assertThat(appService.save(app)).isNotNull();
		
		assertThat(appService.findById("1")).isPresent();
		assertThat(appService.findAll()).hasSize(1);
	}
	
	@Test
	public void update_success() {
		AppInfo app = new AppInfo();
		app.setId("1");
		app.setName("app1");
		app.setCreateUserId("user1");
		app.setCreateTime(LocalDateTime.now());
		
		assertThat(appService.save(app)).isNotNull();
		
		app = appService.findById("1").get();
		app.setName("app2");
		appService.update(app);
		
		assertThat(appService.findById("1").get().getName()).isEqualTo("app2");
	}
}
