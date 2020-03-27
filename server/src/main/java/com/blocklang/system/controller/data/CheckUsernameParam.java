package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public class CheckUsernameParam {
	@NotBlank(message = "请输入用户名！")
	private String username;

	public String getUsername() {
		return StringUtils.trimToNull(username);
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
