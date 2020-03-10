package com.blocklang.system.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sys_user")
public class UserInfo extends PartialIdField {

	private static final long serialVersionUID = -5077066164347230146L;

	@Column(name = "username", nullable = false, length = 32, unique = true)
	private String username;
	@Column(name = "password", nullable = false, length = 64, unique = true)
	private String password;
	@Column(name = "nickname", nullable = true, length = 128)
	private String nickname;
	@Column(name = "last_sign_in_time", insertable = true, updatable = true, nullable = false)
	private LocalDateTime lastSignInTime;
	@Column(name = "admin", nullable = false)
	private Boolean admin = false;
	@Column(name = "create_user_id", insertable = true, updatable = false)
	private Integer createUserId;
	@Column(name = "create_time", insertable = true, updatable = false, nullable = false)
	private LocalDateTime createTime;
	@Column(name = "last_update_user_id", insertable = false)
	private Integer lastUpdateUserId;
	@Column(name = "last_update_time", insertable = false)
	private LocalDateTime lastUpdateTime;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public LocalDateTime getLastSignInTime() {
		return lastSignInTime;
	}

	public void setLastSignInTime(LocalDateTime lastSignInTime) {
		this.lastSignInTime = lastSignInTime;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public Integer getLastUpdateUserId() {
		return lastUpdateUserId;
	}

	public void setLastUpdateUserId(Integer lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Boolean isAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

}
