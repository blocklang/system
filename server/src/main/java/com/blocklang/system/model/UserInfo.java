package com.blocklang.system.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.blocklang.system.constant.Sex;
import com.blocklang.system.constant.converter.SexConverter;

@Entity
@Table(name = "sys_user")
public class UserInfo extends PartialOperateFields {

	private static final long serialVersionUID = -5077066164347230146L;

	@Column(name = "username", nullable = false, length = 64, unique = true)
	private String username;
	
	@Column(name = "password", nullable = false, length = 64)
	private String password;
	
	@Column(name = "nickname", nullable = true, length = 128)
	private String nickname;
	
	@Convert(converter = SexConverter.class)
	@Column(name = "sex", length = 1)
	private Sex sex;

	@Column(name = "phone_number", length = 11)
	private String phoneNumber;
	
	@Column(name = "admin", nullable = false)
	private Boolean admin = false;
	
	@Column(name = "dept_id", length = 3211)
	private String deptId;

	@Column(name = "last_sign_in_time", insertable = true, updatable = true, nullable = false)
	private LocalDateTime lastSignInTime;
	
	@Column(name="sign_in_count", nullable = false)
	private Integer signInCount = 0;
	
	@Transient
	private String deptName;
	
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

	public Boolean isAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Integer getSignInCount() {
		return signInCount;
	}

	public void setSignInCount(Integer signInCount) {
		this.signInCount = signInCount;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

}
