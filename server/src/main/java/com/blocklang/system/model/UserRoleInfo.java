package com.blocklang.system.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sys_user_role")
public class UserRoleInfo extends PartialIdField{

	private static final long serialVersionUID = -7341709893692700550L;

	@Column(name = "role_id", nullable = false, length = 32)
	private String roleId;
	
	@Column(name = "user_id", nullable = false, length = 32)
	private String userId;

	@Column(name = "create_user_id", insertable = true, updatable = false, nullable = false)
	private String createUserId;
	
	@Column(name = "create_time", insertable = true, updatable = false, nullable = false)
	private LocalDateTime createTime;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
}