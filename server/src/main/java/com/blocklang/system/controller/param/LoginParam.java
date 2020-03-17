package com.blocklang.system.controller.param;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public class LoginParam {
	@NotBlank(message = "请输入用户名！")
	private String username;
	@NotBlank(message = "请输入密码！")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
