package com.blocklang.system.controller;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public class CheckUsernameParam {
	@NotBlank(message = "请输入用户名！")
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
