package com.blocklang.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "sys_role")
public class RoleInfo extends PartialOperateFields{

	private static final long serialVersionUID = 7640344248629124400L;

	@Column(name = "app_id", nullable = false, length = 32)
	private String appId;
	
	@Column(name = "role_name", nullable = false, length = 64)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "seq")
	private Integer seq = 1;

	@Column(name = "active")
	private Boolean active = true;
	
	@Transient
	private String appName;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
}
