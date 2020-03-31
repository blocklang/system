package com.blocklang.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.controller.data.UpdateRoleResourceParam;
import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.model.AuthInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.service.RoleResourceService;

@RestController
public class UserResourceController {

	@Autowired
	private ResourcePermissionService permissionService;
	@Autowired
	private RoleResourceService roleResourceService;
	
	@GetMapping("/roles/{roleId}/resources")
	public ResponseEntity<List<String>> listRoleResources(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String roleId
			) {
		permissionService.canExecute(user, Auth.SYSTEM_ROLE_RESOURCES).orElseThrow(NoAuthorizationException::new);
		List<String> result = roleResourceService.findRoleResources(roleId)
				.stream()
				.map(AuthInfo::getResourceId)
				.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/roles/{roleId}/resources")
	public ResponseEntity<Map<String, Object>> updateRoleResources(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String roleId,
			@RequestBody UpdateRoleResourceParam param) {
		permissionService.canExecute(user, Auth.SYSTEM_ROLE_RESOURCES).orElseThrow(NoAuthorizationException::new);
		roleResourceService.batchUpdate(roleId, param.getAppId(), param.getResources(), user.getId());
		return ResponseEntity.ok(Collections.emptyMap());
	}
	
}
