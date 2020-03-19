package com.blocklang.system.controller.data;

import com.blocklang.system.model.UserInfo;

public class UserWithToken {
	private String username;
	private String token;

	public UserWithToken(UserInfo userInfo, String token) {
		this.username = userInfo.getUsername();
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}
}
