package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public class CheckUsernameParam {
	@NotBlank(message = "请输入用户名！")
	private String username;

	public String getUsername() {
		// 因为是必填项，所以不做 null 判断
		return username.trim();
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
