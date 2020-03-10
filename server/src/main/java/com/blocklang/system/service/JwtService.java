package com.blocklang.system.service;

import java.util.Optional;

import com.blocklang.system.model.UserInfo;

public interface JwtService {
	String toToken(UserInfo user);

	Optional<Integer> getSubFromToken(String token);
}
