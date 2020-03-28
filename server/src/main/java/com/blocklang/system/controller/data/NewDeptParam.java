package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

public class NewDeptParam {

	@NotBlank(message = "请选择一个父部门！")
	private String parentId;
	
	@NotBlank(message = "请输入部门名称！")
	private String name;

	public String getParentId() {
		return StringUtils.trimToNull(parentId);
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return StringUtils.trimToNull(name);
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
