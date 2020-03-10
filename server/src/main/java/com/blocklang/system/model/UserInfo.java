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
	@Column(name = "nickname", nullable = true, length = 64)
	private String nickname;
	@Column(name = "last_sign_in_time")
	private LocalDateTime lastSignInTime;
	@Column(name = "create_user_id", insertable = true, updatable = false, nullable = false)
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

}
