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
import com.blocklang.system.controller.data.NewAppParam;
import com.blocklang.system.exception.InvalidRequestException;
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
			@Valid @RequestBody NewAppParam param, 
			BindingResult bindingResult
		) {
		permissionService.canExecute(user, Auth.SYSTEM_APP_NEW).orElseThrow(NoAuthorizationException::new);
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(appService.find(user.getId(), param.getName().trim()).isPresent()) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		AppInfo app = new AppInfo();
		app.setId(IdGenerator.uuid());
		app.setName(param.getName().trim());
		app.setUrl(param.getUrl());
		app.setIcon(param.getIcon());
		app.setDescription(param.getDescription());
		app.setCreateTime(LocalDateTime.now());
		app.setCreateUserId(user.getId());
		
		appService.save(app);
		return ResponseEntity.status(HttpStatus.CREATED).body(app);
	}
	
	@PutMapping("/apps/{appId}")
	public ResponseEntity<AppInfo> updateApp(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String appId,
			@Valid @RequestBody NewAppParam param, 
			BindingResult bindingResult) {
		permissionService.canExecute(user, Auth.SYSTEM_APP_EDIT).orElseThrow(NoAuthorizationException::new);
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		Optional<AppInfo> duplicatedApp = appService.find(user.getId(), param.getName().trim());
		if(duplicatedApp.isPresent() && !duplicatedApp.get().getId().equals(appId)) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		AppInfo existAppInfo = appService.findById(appId).orElseThrow(ResourceNotFoundException::new);
		existAppInfo.setName(param.getName().trim());
		existAppInfo.setIcon(param.getIcon().trim());
		existAppInfo.setUrl(param.getUrl().trim());
		existAppInfo.setDescription(param.getDescription());
		existAppInfo.setLastUpdateTime(LocalDateTime.now());
		existAppInfo.setLastUpdateUserId(user.getId());
		
		appService.save(existAppInfo);
		return ResponseEntity.ok(existAppInfo);
	}
	
	@GetMapping("/apps")
	public ResponseEntity<Page<AppInfo>> listApp(
			@AuthenticationPrincipal UserInfo user,
			@RequestParam(required = false, defaultValue = "0") Integer page) {
		permissionService.canExecute(user, Auth.SYSTEM_APP_LIST).orElseThrow(NoAuthorizationException::new);
		Pageable pageable = PageRequest.of(page, WebSite.PAGE_SIZE, Sort.by(Direction.DESC, "createTime"));
		Page<AppInfo> apps = appService.findAll(pageable);
		return ResponseEntity.ok(apps);
	}
	
	@GetMapping("/apps/{appId}")
	public ResponseEntity<AppInfo> getApp(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String appId) {
		permissionService.canExecute(user, Auth.SYSTEM_APP_QUERY).orElseThrow(NoAuthorizationException::new);
		AppInfo app = appService.findById(appId).orElseThrow(ResourceNotFoundException::new);
		return ResponseEntity.ok(app);
	}
}
