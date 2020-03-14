package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.JwtService;

public class JwtServiceImplTest {

	private JwtService jwtService;

	@BeforeEach
	public void setUp() throws Exception {
		jwtService = new JwtServiceImpl("123123", 3600);
	}

	@Test
	public void should_generate_and_parse_token() throws Exception {
		UserInfo user = new UserInfo();
		user.setId("a");
		String token = jwtService.toToken(user);
		assertThat(token).isNotBlank();
		Optional<String> optional = jwtService.getSubFromToken(token);
		
		assertThat(optional).isPresent().get().isEqualTo("a");
	}

	@Test
	public void should_get_null_with_wrong_jwt() throws Exception {
		Optional<String> optional = jwtService.getSubFromToken("123");
		assertThat(optional).isEmpty();
	}

	@Test
	public void should_get_null_with_expired_jwt() throws Exception {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhaXNlbnNpeSIsImV4cCI6MTUwMjE2MTIwNH0.SJB-U60WzxLYNomqLo4G3v3LzFxJKuVrIud8D8Lz3-mgpo9pN1i7C8ikU_jQPJGm8HsC1CquGMI-rSuM7j6LDA";
		assertThat(jwtService.getSubFromToken(token)).isEmpty();
	}
}
