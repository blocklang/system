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
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.controller.data.NewDeptParam;
import com.blocklang.system.exception.InvalidRequestException;
import com.blocklang.system.exception.NoAuthorizationException;
import com.blocklang.system.exception.ResourceNotFoundException;
import com.blocklang.system.model.DeptInfo;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.DeptService;
import com.blocklang.system.service.ResourcePermissionService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class DeptController {
	
	@Autowired
	private ResourcePermissionService permissionService;
	@Autowired
	private DeptService deptService;

	@PostMapping("/depts")
	public ResponseEntity<DeptInfo> newDept(
			@AuthenticationPrincipal UserInfo currentUser,
			@Valid @RequestBody NewDeptParam param,
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser,  Auth.SYSTEM_DEPT_NEW).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		// 校验父部门是否存在
		if(deptService.findById(param.getParentId().trim()).isEmpty()) {
			bindingResult.rejectValue("parentId", "NOT-EXIST", "<strong>"+param.getParentId().trim()+"</strong>不存在！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if(deptService.find(param.getParentId(), param.getName().trim()).isPresent()) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		
		DeptInfo dept = new DeptInfo();
		dept.setId(IdGenerator.uuid());
		dept.setParentId(param.getParentId().trim());
		dept.setName(param.getName().trim());
		dept.setCreateUserId(currentUser.getId());
		dept.setCreateTime(LocalDateTime.now());
		
		deptService.save(dept);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(dept);
	}
	
	@PutMapping("/depts/{deptId}")
	public ResponseEntity<DeptInfo> updateDept(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable String deptId,
			@Valid @RequestBody NewDeptParam param,
			BindingResult bindingResult) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_DEPT_EDIT).orElseThrow(NoAuthorizationException::new);
		
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		// 校验父部门是否存在
		if(deptService.findById(param.getParentId().trim()).isEmpty()) {
			bindingResult.rejectValue("parentId", "NOT-EXIST", "<strong>"+param.getParentId().trim()+"</strong>不存在！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		Optional<DeptInfo> duplicatedDept = deptService.find(param.getParentId(), param.getName().trim());
		if(duplicatedDept.isPresent() && !duplicatedDept.get().getId().equals(deptId)) {
			bindingResult.rejectValue("name", "DUPLICATED", "<strong>"+param.getName().trim()+"</strong>已被占用！");
		}
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}

		DeptInfo updatedDept = deptService.findById(deptId).orElseThrow(ResourceNotFoundException::new);
		// 在此处不需要设置 parentId，因为约定这个值不能改变
		updatedDept.setName(param.getName().trim());
		updatedDept.setLastUpdateUserId(currentUser.getId());
		updatedDept.setLastUpdateTime(LocalDateTime.now());

		deptService.save(updatedDept);
		return ResponseEntity.ok(updatedDept);
	}
	
	@GetMapping("/depts/{deptId}/children")
	public ResponseEntity<List<DeptInfo>> listDept(
			@AuthenticationPrincipal UserInfo user, 
			@PathVariable String deptId // 要获取此部门下的所有直属部门
		) {
		permissionService.canExecute(user, Auth.SYSTEM_DEPT_LIST).orElseThrow(NoAuthorizationException::new);
		
		Sort sort = Sort.by(Direction.ASC, "seq", "name");
		List<DeptInfo> depts = deptService.findChildren(deptId, sort);
		return ResponseEntity.ok(depts);
	}
	
	@GetMapping("/depts/{deptId}")
	public ResponseEntity<DeptInfo> getDept(
			@AuthenticationPrincipal UserInfo currentUser, // 登录用户信息
			@PathVariable String deptId
		) {
		permissionService.canExecute(currentUser, Auth.SYSTEM_DEPT_QUERY).orElseThrow(NoAuthorizationException::new);
		DeptInfo dept = deptService.findById(deptId).orElseThrow(ResourceNotFoundException::new);
		return ResponseEntity.ok(dept);
	}
	
}
