package com.blocklang.system.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.blocklang.system.constant.ResourceType;
import com.blocklang.system.constant.Tree;
import com.blocklang.system.constant.converter.ResourceTypeConverter;

@Entity
@Table(name = "sys_resource")
public class ResourceInfo extends PartialOperateFields{

	private static final long serialVersionUID = 1728461073275417714L;

	@Column(name = "app_id", nullable = false, length = 32)
	private String appId;
	
	@Column(name = "res_name", nullable = false, length = 64)
	private String name;
	
	@Column(name = "parent_id", nullable = false, length = 32)
	private String parentId = Tree.ROOT_PARENT_ID;
	
	@Column(name = "seq")
	private Integer seq = 1;
	
	@Column(name = "url", length = 255)
	private String url;
	
	@Column(name = "icon", length = 64)
	private String icon;
	
	@Convert(converter = ResourceTypeConverter.class)
	@Column(name = "resource_type", nullable = false, length = 2)
	private ResourceType resourceType;
	
	@Column(name = "description")
	private String description;

	@Column(name = "active")
	private Boolean active = true;
	
	@Column(name = "auth", length = 64)
	private String auth;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
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
