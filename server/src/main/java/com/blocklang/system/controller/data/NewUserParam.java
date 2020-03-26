package com.blocklang.system.controller.data;

import org.apache.commons.lang3.StringUtils;

public class NewUserParam extends LoginParam {

	private String nickname;
	private String sex;
	private String phoneNumber;

	public String getNickname() {
		// 如果值为空，则确保插入数据库的值为 null，不能是空字符串
		return StringUtils.trimToNull(nickname);
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhoneNumber() {
		return StringUtils.trimToNull(phoneNumber);
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
