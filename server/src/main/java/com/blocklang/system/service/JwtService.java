package com.blocklang.system.service;

import java.util.Optional;

import com.blocklang.system.model.UserInfo;

public interface JwtService {
	String toToken(UserInfo user);

	Optional<String> getSubFromToken(String token);
}
