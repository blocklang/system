package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

public class NewResourceParam {
	
	@NotBlank(message = "请选择一个APP！")
	private String appId;
	
	@NotBlank(message = "请选择一个父资源！")
	private String parentId;
	
	@NotBlank(message = "请输入资源名！")
	private String name;
	
	private String url;
	
	private String icon;
	
	private String resourceType;
	
	private String description;
	
	private Boolean active;
	
	private String auth;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}
	
}
