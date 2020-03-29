package com.blocklang.system.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.controller.data.NewResourceParam;
import com.blocklang.system.exception.InvalidRequestException;
import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.exception.ResourceNotFoundException;
import com.blocklang.system.model.ResourceInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.AppService;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.service.ResourceService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class ResourceController {
	
	@Autowired
	private ResourcePermissionService permissionService;
	@Autowired
	private AppService appService;
	@Autowired
	private ResourceService resourceService;
	
	@PostMapping("/resources")
	public ResponseEntity<ResourceInfo> newResource(
			@AuthenticationPrincipal UserInfo currentUser,
			@Valid @RequestBody NewResourceParam param,
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_RES_NEW).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(appService.findById(param.getAppId()).isEmpty()) {
			bindingResult.rejectValue("appId", "NOT-EXIST", "<strong>"+param.getAppId()+"</strong>不存在！");
		}
		if(resourceService.find(param.getAppId(), param.getParentId(), param.getName()).isPresent()) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName()+"</strong>已被占用！");
		}
		if(resourceService.findByAuth(param.getAuth()).isPresent()) {
			bindingResult.rejectValue("auth", "DUPLICATED", "<strong>"+param.getAuth()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		ResourceInfo resource = new ResourceInfo();
		resource.setId(IdGenerator.uuid());
		resource.setAppId(param.getAppId());
		resource.setParentId(param.getParentId());
		resource.setName(param.getName());
		resource.setUrl(param.getUrl());
		resource.setIcon(param.getIcon());
		resource.setResourceType(ResourceType.fromKey(param.getResourceType()));
		resource.setDescription(param.getDescription());
		resource.setAuth(param.getAuth());
		resource.setActive(true);
		resource.setCreateUserId(currentUser.getId());
		resource.setCreateTime(LocalDateTime.now());
		
		resourceService.save(resource);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(resource);
	}
	
	@PutMapping("/resources/{resourceId}")
	public ResponseEntity<ResourceInfo> updateResource(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable("resourceId") String updatedResourceId, // 当前正在修改的标识
			@Valid @RequestBody NewResourceParam param,
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_RES_EDIT).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(appService.findById(param.getAppId()).isEmpty()) {
			bindingResult.rejectValue("appId", "NOT-EXIST", "<strong>"+param.getAppId().trim()+"</strong>不存在！");
		}
		Optional<ResourceInfo> duplicatedResource = resourceService.find(param.getAppId(), param.getParentId(), param.getName().trim());
		if(duplicatedResource.isPresent() && !duplicatedResource.get().getId().equals(updatedResourceId)) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		duplicatedResource = resourceService.findByAuth(param.getAuth());
		if(duplicatedResource.isPresent() && !duplicatedResource.get().getId().equals(updatedResourceId)) {
			bindingResult.rejectValue("auth", "DUPLICATED", "<strong>"+param.getAuth()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}

		ResourceInfo updatedResource = resourceService.findById(updatedResourceId).orElseThrow(ResourceNotFoundException::new);
		// 在此处不需要设置 appId 和 parentId，因为约定这两个值不能改变
		updatedResource.setName(param.getName().trim());
		updatedResource.setUrl(param.getUrl().trim());
		updatedResource.setIcon(param.getIcon().trim());
		updatedResource.setResourceType(ResourceType.fromKey(param.getResourceType()));
		updatedResource.setDescription(param.getDescription());
		updatedResource.setAuth(param.getAuth().trim());
		updatedResource.setLastUpdateUserId(currentUser.getId());
		updatedResource.setLastUpdateTime(LocalDateTime.now());

		resourceService.save(updatedResource);
		return ResponseEntity.ok(updatedResource);
	}
	
	@GetMapping("/resources/{resourceId}/children")
	public ResponseEntity<List<ResourceInfo>> listResource(
			@AuthenticationPrincipal UserInfo user, 
			@RequestParam("appid") String appId,
			@PathVariable("resourceId") String parentResourceId // 要获取此资源下的所有直属资源
		) {
		permissionService.canExecute(user, Auth.SYSTEM_RES_LIST).orElseThrow(NoAuthorizationException::new);
		
		Sort sort = Sort.by(Direction.ASC, "seq", "name");
		List<ResourceInfo> resources = resourceService.findChildren(appId, parentResourceId, sort);
		
		return ResponseEntity.ok(resources);
	}
	
	@GetMapping("/resources/{resourceId}")
	public ResponseEntity<ResourceInfo> getResource(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable("resourceId") String queryResourceId // 要查询的资源模块标识
		) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_RES_QUERY).orElseThrow(NoAuthorizationException::new);
		ResourceInfo resource = resourceService.findById(queryResourceId).orElseThrow(ResourceNotFoundException::new);
		return ResponseEntity.ok(resource);
	}
	
}
