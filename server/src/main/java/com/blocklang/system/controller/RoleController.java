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
import com.blocklang.system.constant.WebSite;
import com.blocklang.system.controller.param.NewRoleParam;
import com.blocklang.system.exception.InvalidRequestException;
import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.exception.ResourceNotFoundException;
import com.blocklang.system.model.RoleInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.service.RoleService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class RoleController {

	@Autowired
	private ResourcePermissionService permissionService;
	@Autowired
	private AppService appService;
	@Autowired
	private RoleService roleService;
	
	@PostMapping("/roles")
	public ResponseEntity<RoleInfo> newRole(
			@AuthenticationPrincipal UserInfo currentUser,
			@RequestParam("resid") String resourceId,
			@Valid @RequestBody NewRoleParam param,
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser, resourceId, Auth.NEW).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(appService.findById(param.getAppId()).isEmpty()) {
			bindingResult.rejectValue("appId", "NOT-EXIST", "<strong>"+param.getAppId().trim()+"</strong>不存在！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(roleService.findByAppIdAndName(param.getAppId(), param.getName().trim()).isPresent()) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		RoleInfo role = new RoleInfo();
		role.setId(IdGenerator.uuid());
		role.setAppId(param.getAppId());
		role.setName(param.getName().trim());
		role.setDescription(param.getDescription().trim());
		role.setCreateUserId(currentUser.getId());
		role.setCreateTime(LocalDateTime.now());
		
		roleService.save(role);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(role);
	}
	
	@PutMapping("/roles/{roleId}")
	public ResponseEntity<RoleInfo> updateRole(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@RequestParam("resid") String resourceId,
			@PathVariable String roleId,
			@Valid @RequestBody NewRoleParam param,
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser, resourceId, Auth.EDIT).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(appService.findById(param.getAppId()).isEmpty()) {
			bindingResult.rejectValue("appId", "NOT-EXIST", "<strong>"+param.getAppId().trim()+"</strong>不存在！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		Optional<RoleInfo> duplicatedRole = roleService.findByAppIdAndName(param.getAppId(), param.getName().trim());
		if(duplicatedRole.isPresent() && !duplicatedRole.get().getId().equals(roleId)) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		RoleInfo updatedRole = roleService.findById(roleId).orElseThrow(ResourceNotFoundException::new);
		updatedRole.setAppId(param.getAppId());
		updatedRole.setName(param.getName().trim());
		updatedRole.setDescription(param.getDescription());
		updatedRole.setLastUpdateUserId(currentUser.getId());
		updatedRole.setLastUpdateTime(LocalDateTime.now());
		
		roleService.save(updatedRole);
		return ResponseEntity.ok(updatedRole);
	}
	
	@GetMapping("/roles")
	public ResponseEntity<Page<RoleInfo>> listRole(
			@AuthenticationPrincipal UserInfo user, 
			@RequestParam("resid") String resourceId, 
			@RequestParam String appId,
			@RequestParam(required = false, defaultValue = "0") Integer page) {
		permissionService.canExecute(user, resourceId, Auth.LIST).orElseThrow(NoAuthorizationException::new);

		Sort sort = Sort.by(Direction.ASC, "seq", "name");
		Pageable pageable = PageRequest.of(page, WebSite.PAGE_SIZE, sort);
		Page<RoleInfo> roles = roleService.findAllByAppId(appId, pageable);
		return ResponseEntity.ok(roles);
	}
	
	@GetMapping("/roles/{roleId}")
	public ResponseEntity<RoleInfo> getRole(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable String roleId,
			@RequestParam("resid") String resourceId) {
		permissionService.canExecute(currentUser, resourceId, Auth.QUERY).orElseThrow(NoAuthorizationException::new);
		RoleInfo role = roleService.findById(roleId).orElseThrow(ResourceNotFoundException::new);
		return ResponseEntity.ok(role);
	}
}
