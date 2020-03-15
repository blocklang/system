package com.blocklang.system.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.exception.ResourceNotFoundException;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class AppController {

	@Autowired
	private ResourcePermissionService permissionService;
	
	@Autowired
	private AppService appService;
	
	@PostMapping("/apps")
	public ResponseEntity<AppInfo> newApp(
			@AuthenticationPrincipal UserInfo user, 
			@RequestParam("resid") String resourceId,
			@RequestBody AppInfo appInfo, BindingResult bindingResult
		) {
		permissionService.canExecute(user.getId(), resourceId, "new").orElseThrow(NoAuthorizationException::new);
		
		appInfo.setId(IdGenerator.uuid());
		appInfo.setCreateTime(LocalDateTime.now());
		appInfo.setCreateUserId(user.getId());
		
		appService.save(appInfo);
		return ResponseEntity.status(HttpStatus.CREATED).body(appInfo);
	}
	
	@GetMapping("/apps")
	public ResponseEntity<List<AppInfo>> listApp(@AuthenticationPrincipal UserInfo user) {
		if(user == null) {
			throw new NoAuthorizationException();
		}
		List<AppInfo> apps = appService.findAll();
		return ResponseEntity.ok(apps);
	}
	
	@GetMapping("/apps/{appId}")
	public ResponseEntity<AppInfo> getApp(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String appId,
			@RequestParam("resid") String resourceId) {
		permissionService.canExecute(user.getId(), resourceId, "query").orElseThrow(NoAuthorizationException::new);
		AppInfo app = appService.findById(appId).orElseThrow(ResourceNotFoundException::new);
		return ResponseEntity.ok(app);
	}
	
	@PutMapping("/apps")
	public ResponseEntity<AppInfo> updateApp(@AuthenticationPrincipal UserInfo user, @RequestBody AppInfo appInfo, BindingResult bindingResult) {
		if(user == null) {
			throw new NoAuthorizationException();
		}
		// TODO: 完善校验逻辑
		
		AppInfo existAppInfo = appService.findById(appInfo.getId()).orElseThrow(ResourceNotFoundException::new);
		existAppInfo.setName(appInfo.getName());
		existAppInfo.setIcon(appInfo.getIcon());
		existAppInfo.setUrl(appInfo.getUrl());
		existAppInfo.setDescription(appInfo.getDescription());
		existAppInfo.setLastUpdateTime(LocalDateTime.now());
		existAppInfo.setLastUpdateUserId(user.getId());
		
		appService.update(existAppInfo);
		return ResponseEntity.ok(appInfo);
	}
}
