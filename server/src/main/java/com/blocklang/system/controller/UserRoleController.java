package com.blocklang.system.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.controller.data.UserData;
import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.exception.ResourceNotFoundException;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.model.UserRoleInfo;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.service.UserRoleService;
import com.blocklang.system.service.UserService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class UserRoleController {
	
	@Autowired
	private ResourcePermissionService permissionService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private UserService userService;
	
	@GetMapping("/roles/{roleId}/users")
	public ResponseEntity<List<UserData>> listRoleUsers(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String roleId
			) {
		permissionService.canExecute(user, Auth.SYSTEM_ROLE_USERS).orElseThrow(NoAuthorizationException::new);
		List<UserData> result = userRoleService.findRoleUsers(roleId).stream().map(item -> {
			UserData data = new UserData();
			data.setId(item.getId());
			data.setUsername(item.getUsername());
			data.setNickname(item.getNickname());
			data.setSex(item.getSex().getKey());
			data.setPhoneNumber(item.getPhoneNumber());
			data.setDeptId(item.getDeptId());
			data.setDeptName(item.getDeptName());
			return data;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/roles/{roleId}/users/{userId}/assign")
	public ResponseEntity<UserData> addUser(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String roleId,
			@PathVariable String userId
			) {
		permissionService.canExecute(user, Auth.SYSTEM_ROLE_USERS).orElseThrow(NoAuthorizationException::new);
		
		UserRoleInfo userRole = new UserRoleInfo();
		userRole.setId(IdGenerator.uuid());
		userRole.setRoleId(roleId);
		userRole.setUserId(userId);
		userRole.setCreateTime(LocalDateTime.now());
		userRole.setCreateUserId(user.getId());
		
		userRoleService.add(userRole);
		
		UserData result = userService.findById(userId).map(item -> {
			UserData data = new UserData();
			data.setId(item.getId());
			data.setUsername(item.getUsername());
			data.setNickname(item.getNickname());
			data.setSex(item.getSex().getKey());
			data.setPhoneNumber(item.getPhoneNumber());
			data.setDeptId(item.getDeptId());
			data.setDeptName(item.getDeptName());
			return data;
		}).orElseThrow(ResourceNotFoundException::new);
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/roles/{roleId}/users/{userId}/unassign")
	public ResponseEntity<Map<String, Object>> removeUser(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String roleId,
			@PathVariable String userId
			) {
		permissionService.canExecute(user, Auth.SYSTEM_ROLE_USERS).orElseThrow(NoAuthorizationException::new);
		userRoleService.remove(roleId, userId);
		return ResponseEntity.ok(Collections.emptyMap());
	}
}
