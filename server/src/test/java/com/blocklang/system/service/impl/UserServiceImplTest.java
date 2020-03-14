package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.UserService;
import com.blocklang.system.test.AbstractServiceTest;

public class UserServiceImplTest extends AbstractServiceTest{
	
	@Autowired
	private UserService userService;
	
	@Test
	public void findById_no_data() {
		assertThat(userService.findById("1")).isEmpty();
	}
	
	@Test
	public void findByUsername_no_data() {
		assertThat(userService.findByUsername("jack")).isEmpty();
	}
	
	@Test
	public void save_success() {
		UserInfo user = new UserInfo();
		user.setId("1");
		user.setUsername("jack");
		user.setPassword("123");
		user.setLastSignInTime(LocalDateTime.now());
		user.setCreateTime(LocalDateTime.now());
		user.setCreateUserId("1");
		
		assertThat(userService.save(user)).isNotNull();
		
		assertThat(userService.findById("1")).isPresent();
		assertThat(userService.findByUsername("jack")).isPresent();
	}
}
