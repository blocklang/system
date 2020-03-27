package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

// 因为不会修改数据的 APPId,所以不需要传入
public class UpdateRoleParam {

	@NotBlank(message = "请输入角色名！")
	private String name;
	
	private String description;

	public String getName() {
		return StringUtils.trimToNull(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
