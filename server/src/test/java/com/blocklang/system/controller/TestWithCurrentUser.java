package com.blocklang.system.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import com.blocklang.system.model.UserInfo;
import com.blocklang.system.test.AbstractControllerTest;

abstract class TestWithCurrentUser extends AbstractControllerTest {

	protected UserInfo user;
	protected String token;
	protected String username;

	protected void userFixture() {
		username = "jack";
		user = new UserInfo();
		user.setId("1");

		when(userService.findById(eq(user.getId()))).thenReturn(Optional.of(user));

		token = "token";
		when(jwtService.getSubFromToken(eq(token))).thenReturn(Optional.of(user.getId()));
	}

	@BeforeEach
	public void setUpUser() {
		userFixture();
	}
}
