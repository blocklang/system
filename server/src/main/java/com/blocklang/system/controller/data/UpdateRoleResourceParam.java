package com.blocklang.system.controller.data;

import java.util.List;

public class UpdateRoleResourceParam {

	private String appId;
	private List<String> resources;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

}
