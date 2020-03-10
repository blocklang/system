package com.blocklang.system.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.system.exception.InvalidRequestException;
import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.EncryptService;
import com.blocklang.system.service.JwtService;
import com.blocklang.system.service.UserService;

@RestController
public class LoginController {

	@Autowired
	private EncryptService encryptService;
	@Autowired
	private UserService userService;
	@Autowired
	private JwtService jwtService;
	
	@PostMapping("/users/login")
	public ResponseEntity<UserWithToken> userLogin(@Valid @RequestBody LoginParam loginParam, BindingResult bindingResult) {
		Optional<UserInfo> userOption = userService.findByUsername(loginParam.getUsername());
		if(userOption.isEmpty() || !encryptService.check(loginParam.getPassword(), userOption.get().getPassword())) {
			bindingResult.rejectValue("password", "INVALID", "用户名或密码无效");
			throw new InvalidRequestException(bindingResult);
		}
		UserInfo user = userOption.get();
		return ResponseEntity.ok(new UserWithToken(user, jwtService.toToken(user)));
	}

}
