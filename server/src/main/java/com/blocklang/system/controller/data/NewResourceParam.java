package com.blocklang.system.controller.data;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

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
	
	@NotBlank(message = "请输入资源标识！")
	private String auth;

	public String getAppId() {
		return StringUtils.trimToNull(appId);
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

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

	public String getUrl() {
		return StringUtils.trimToNull(url);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return StringUtils.trimToNull(icon);
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
		return StringUtils.trimToNull(auth);
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}
	
}
