package com.blocklang.system.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.system.controller.param.CheckUsernameParam;
import com.blocklang.system.controller.param.LoginParam;
import com.blocklang.system.controller.param.UserWithToken;
import com.blocklang.system.exception.InvalidRequestException;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.EncryptService;
import com.blocklang.system.service.JwtService;
import com.blocklang.system.service.UserService;
import com.blocklang.system.utils.IdGenerator;

@RestController
public class LoginController {

	@Autowired
	private EncryptService encryptService;
	@Autowired
	private UserService userService;
	@Autowired
	private JwtService jwtService;
	
	/**
	 * 注册用户
	 */
	@PostMapping("/user/register")
	public ResponseEntity<Map<String, UserWithToken>> createUser(@Valid @RequestBody LoginParam registerParam, BindingResult bindingResult) {
		checkInput(registerParam, bindingResult);

		UserInfo user = new UserInfo();
		String id = IdGenerator.uuid();
		user.setId(id);
		user.setUsername(registerParam.getUsername().trim());
		user.setPassword(encryptService.encrypt(registerParam.getPassword()));
		user.setCreateUserId(id);
		user.setCreateTime(LocalDateTime.now());
		user.setLastSignInTime(LocalDateTime.now());
		
		userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(userResponse(user));
	}
	
	private void checkInput(LoginParam registerParam, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		if (userService.findByUsername(registerParam.getUsername().trim()).isPresent()) {
			bindingResult.rejectValue("username", "DUPLICATED", "<strong>"+registerParam.getUsername().trim()+"</strong>已被占用！");
		}

		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
	}
	
	@PostMapping("/user/check-username")
	public ResponseEntity<Map<String, Object>> checkUsername(
			@Valid @RequestBody CheckUsernameParam param, 
			BindingResult bindingResult) {
		
		if(userService.findByUsername(param.getUsername()).isPresent()) {
			bindingResult.rejectValue("username", "DUPLICATED", "<strong>"+param.getUsername().trim()+"</strong>已被占用！");
		}
		
		if(bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult);
		}
		return ResponseEntity.ok(new HashMap<String,Object>());
	}
	
	/**
	 * 用户登录
	 */
	@PostMapping("/user/login")
	public ResponseEntity<Map<String,UserWithToken>> userLogin(@Valid @RequestBody LoginParam loginParam, BindingResult bindingResult) {
		Optional<UserInfo> userOption = userService.findByUsername(loginParam.getUsername());
		if(userOption.isEmpty() || !encryptService.check(loginParam.getPassword(), userOption.get().getPassword())) {
			bindingResult.reject("INVALID", "用户名或密码无效！");
			throw new InvalidRequestException(bindingResult);
		}
		UserInfo user = userOption.get();
		userService.login(user);
		return ResponseEntity.ok(userResponse(user));
	}
	
	private Map<String, UserWithToken> userResponse(UserInfo user) {
		UserWithToken userWithToken = new UserWithToken(user, jwtService.toToken(user));
		return Collections.singletonMap("user", userWithToken);
	}

	@GetMapping("/user/resources/{resourceId}/permissions")
	public ResponseEntity<>
}
