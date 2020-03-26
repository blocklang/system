package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;



public class NewAppParam {

	@NotBlank(message = "请输入APP名称！")
	private String name;
	
	private String icon;
	
	private String url;
	
	private String description; // 不需要 trim()

	public String getName() {
		return name.trim();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return StringUtils.trimToNull(icon);
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return StringUtils.trimToNull(url);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
