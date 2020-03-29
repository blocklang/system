package com.blocklang.system.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.Sex;
import com.blocklang.system.constant.WebSite;
import com.blocklang.system.controller.data.NewUserParam;
import com.blocklang.system.controller.data.UpdateUserParam;
import com.blocklang.system.exception.InvalidRequestException;
import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.exception.ResourceNotFoundException;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.EncryptService;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.service.UserService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class UserController {
	
	@Autowired
	private ResourcePermissionService permissionService;
	@Autowired
	private UserService userService;
	@Autowired
	private EncryptService encryptService;
	
	@PostMapping("/users")
	public ResponseEntity<UserInfo> newUser(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@Valid @RequestBody NewUserParam param, // 新增用户信息
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser,  Auth.SYSTEM_USER_NEW).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if (userService.findByUsername(param.getUsername().trim()).isPresent()) {
			bindingResult.rejectValue("username", "DUPLICATED", "<strong>"+param.getUsername().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		UserInfo user = new UserInfo();
		user.setId(IdGenerator.uuid());
		user.setUsername(param.getUsername().trim());
		user.setPassword(encryptService.encrypt(param.getPassword()));
		user.setNickname(param.getNickname());
		user.setSex(Sex.fromKey(param.getSex()));
		user.setPhoneNumber(param.getPhoneNumber());
		user.setDeptId(param.getDeptId());
		user.setAdmin(false);
		user.setCreateUserId(currentUser.getId());
		user.setCreateTime(LocalDateTime.now());
		
		userService.save(user);
		
		user.setPassword(null); // 不能返回用户密码
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}
	
	@PutMapping("/users/{userId}")
	public ResponseEntity<UserInfo> updateUser(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable String userId, // 要修改的用户标识
			@Valid @RequestBody UpdateUserParam param, // 修改用户信息
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_USER_EDIT).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		Optional<UserInfo> duplicatedUser = userService.findByUsername(param.getUsername());
		if(duplicatedUser.isPresent() && !duplicatedUser.get().getId().equals(userId)) {
			bindingResult.rejectValue("username", "DUPLICATED", "<strong>"+param.getUsername()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		UserInfo updatedUser = userService.findById(userId).orElseThrow(ResourceNotFoundException::new);
		updatedUser.setUsername(param.getUsername());
		updatedUser.setNickname(param.getNickname());
		updatedUser.setDeptId(param.getDeptId());
		updatedUser.setSex(Sex.fromKey(param.getSex()));
		updatedUser.setPhoneNumber(param.getPhoneNumber());
		updatedUser.setLastUpdateUserId(currentUser.getId());
		updatedUser.setLastUpdateTime(LocalDateTime.now());
		
		userService.save(updatedUser);
		
		// 这样重新调用一次，就能正确获取部门名称
		updatedUser = userService.findById(userId).orElseThrow(ResourceNotFoundException::new);
		updatedUser.setPassword(null); // 不能返回用户密码
		return ResponseEntity.ok(updatedUser);
	}

	@GetMapping("/users")
	public ResponseEntity<Page<UserInfo>> listUser(
			@AuthenticationPrincipal UserInfo user, 
			@RequestParam(required = false, defaultValue = "0") Integer page) {
		permissionService.canExecute(user, Auth.SYSTEM_USER_LIST).orElseThrow(NoAuthorizationException::new);

		Pageable pageable = PageRequest.of(page, WebSite.PAGE_SIZE, Sort.by(Direction.ASC, "username"));
		Page<UserInfo> users = userService.findAll(pageable).map(item -> {
			item.setPassword(null); // 不能返回用户密码
			return item;
		});
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<UserInfo> getUser(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable String userId // 要查询用户的标识
		) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_USER_QUERY).orElseThrow(NoAuthorizationException::new);
		UserInfo user = userService.findById(userId).orElseThrow(ResourceNotFoundException::new);
		user.setPassword(null); // 不能返回用户密码
		return ResponseEntity.ok(user);
	}
}
