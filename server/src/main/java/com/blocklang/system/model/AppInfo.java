package com.blocklang.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sys_app")
public class AppInfo extends PartialOperateFields {

	private static final long serialVersionUID = -5582839331913827453L;

	@Column(name = "app_name", nullable = false, length = 64)
	private String name;
	
	@Column(name = "url", length = 64)
	private String url;
	
	@Column(name = "icon", length = 64)
	private String icon;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "seq", nullable = false)
	private Integer seq;
	
	@Column(name = "active", nullable = false)
	private Boolean active = true;

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
	
}
