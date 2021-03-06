package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

public class NewRoleParam {

	@NotBlank(message = "请输入角色名！")
	private String name;
	
	@NotBlank(message = "请选择一个APP！")
	private String appId;
	
	private String description;

	public String getName() {
		return StringUtils.trimToNull(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppId() {
		return StringUtils.trimToNull(appId);
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
