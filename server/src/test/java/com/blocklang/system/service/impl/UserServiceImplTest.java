package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.blocklang.system.dao.DeptDao;
import com.blocklang.system.dao.UserDao;
import com.blocklang.system.model.DeptInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.UserService;
import com.blocklang.system.test.AbstractServiceTest;

public class UserServiceImplTest extends AbstractServiceTest{
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private DeptDao deptDao;
	
	@Test
	public void findById_no_data() {
		assertThat(userService.findById("1")).isEmpty();
	}
	
	@Test
	public void findById_success() {
		String deptId = "dept1";
		String deptName = "deptName";
		DeptInfo dept1 = new DeptInfo();
		dept1.setId(deptId);
		dept1.setName(deptName);
		dept1.setCreateTime(LocalDateTime.now());
		dept1.setCreateUserId("1");
		deptDao.save(dept1);
		
		UserInfo user1 = new UserInfo();
		user1.setId("1");
		user1.setUsername("jack");
		user1.setPassword("123");
		user1.setDeptId(deptId);
		user1.setLastSignInTime(LocalDateTime.now());
		user1.setCreateTime(LocalDateTime.now());
		user1.setCreateUserId("1");
		userService.save(user1);
		
		Optional<UserInfo> userOption = userService.findById("1");
		assertThat(userOption).isPresent().get().extracting("deptName").isEqualTo(deptName);
	}
	
	@Test
	public void findByUsername_no_data() {
		assertThat(userService.findByUsername("jack")).isEmpty();
	}
	
	@Test
	public void save_first_user_is_admin() {
		UserInfo user1 = new UserInfo();
		user1.setId("1");
		user1.setUsername("jack");
		user1.setPassword("123");
		user1.setLastSignInTime(LocalDateTime.now());
		user1.setCreateTime(LocalDateTime.now());
		user1.setCreateUserId("1");
		userService.save(user1);
		
		UserInfo user2 = new UserInfo();
		user2.setId("2");
		user2.setUsername("jack1");
		user2.setPassword("123");
		user2.setLastSignInTime(LocalDateTime.now());
		user2.setCreateTime(LocalDateTime.now());
		user2.setCreateUserId("1");
		userService.save(user2);
		
		assertThat(userService.findById("1").get().isAdmin()).isTrue();
		assertThat(userService.findById("2").get().isAdmin()).isFalse();
	}
	
	@Test
	public void save_username_is_duplicated() {
		String username = "jack";
		
		UserInfo user1 = new UserInfo();
		user1.setId("1");
		user1.setUsername(username);
		user1.setPassword("123");
		user1.setLastSignInTime(LocalDateTime.now());
		user1.setCreateTime(LocalDateTime.now());
		user1.setCreateUserId("1");
		userService.save(user1);
		
		UserInfo user2 = new UserInfo();
		user2.setId("2");
		user2.setUsername(username);
		user2.setPassword("123");
		user2.setLastSignInTime(LocalDateTime.now());
		user2.setCreateTime(LocalDateTime.now());
		user2.setCreateUserId("1");
		userService.save(user2);
		
		assertThrows(DataIntegrityViolationException.class, ()-> {
			userDao.flush();
		});
	}
	
	@Test
	public void save_phone_number_is_duplicated() {
		String phoneNumber = "111";
		
		UserInfo user1 = new UserInfo();
		user1.setId("1");
		user1.setUsername("jack");
		user1.setPassword("123");
		user1.setPhoneNumber(phoneNumber);
		user1.setLastSignInTime(LocalDateTime.now());
		user1.setCreateTime(LocalDateTime.now());
		user1.setCreateUserId("1");
		userService.save(user1);
		
		UserInfo user2 = new UserInfo();
		user2.setId("2");
		user2.setUsername("jack1");
		user2.setPassword("123");
		user2.setPhoneNumber(phoneNumber);
		user2.setLastSignInTime(LocalDateTime.now());
		user2.setCreateTime(LocalDateTime.now());
		user2.setCreateUserId("1");
		userService.save(user2);
		
		assertThrows(DataIntegrityViolationException.class, ()-> {
			userDao.flush();
		});
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

	@Test
	public void login_success() {
		LocalDateTime lastSignInTime = LocalDateTime.now().minusDays(1);
		UserInfo user = new UserInfo();
		user.setId("1");
		user.setUsername("jack");
		user.setPassword("123");
		user.setLastSignInTime(lastSignInTime);
		user.setSignInCount(1);
		user.setCreateTime(LocalDateTime.now());
		user.setCreateUserId("1");
		UserInfo savedUser = userDao.save(user);
		
		userService.login(savedUser);
		
		savedUser = userService.findById(user.getId()).get();
		assertThat(savedUser.getSignInCount()).isEqualTo(2);
		assertThat(savedUser.getLastSignInTime()).isAfter(lastSignInTime);
	}
	
	@Test
	public void findAll_no_data() {
		Pageable pageable = PageRequest.of(0, 1);
		assertThat(userService.findAll(true, pageable)).isEmpty();
		assertThat(userService.findAll(false, pageable)).isEmpty();
	}
	
	@Test
	public void findAll_success() {
		String deptId = "dept1";
		String deptName = "deptName";
		DeptInfo dept1 = new DeptInfo();
		dept1.setId(deptId);
		dept1.setName(deptName);
		dept1.setCreateTime(LocalDateTime.now());
		dept1.setCreateUserId("1");
		deptDao.save(dept1);
		
		UserInfo user1 = new UserInfo();
		user1.setId("1");
		user1.setUsername("jack");
		user1.setPassword("123");
		user1.setAdmin(true);
		user1.setDeptId(deptId);
		user1.setLastSignInTime(LocalDateTime.now());
		user1.setCreateTime(LocalDateTime.now());
		user1.setCreateUserId("1");
		userService.save(user1);
		
		Pageable pageable = PageRequest.of(0, 1);
		Page<UserInfo> users = userService.findAll(true, pageable);
		assertThat(users).hasSize(0);
		users = userService.findAll(false, pageable);
		assertThat(users).hasSize(1).first().extracting("deptName").isEqualTo(deptName);
	}
}
