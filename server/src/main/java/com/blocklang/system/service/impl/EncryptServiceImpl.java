package com.blocklang.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.blocklang.system.service.EncryptService;

@Service
public class EncryptServiceImpl implements EncryptService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public String encrypt(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public boolean check(String checkPassword, String realPassword) {
		return passwordEncoder.matches(checkPassword, realPassword);
	}

}
