package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

public class NewAppParam {

	@NotBlank(message = "请输入APP名称！")
	private String name;
	private String icon;
	private String url;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
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
