package com.blocklang.system.constant;

import java.util.Arrays;

/**
 * 性别
 *
 */
public enum Sex {

	MALE("1", "男"),
	FEMALE("2", "女");

	private final String key;
	private final String value;

	private Sex(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public static Sex fromValue(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return Arrays.stream(Sex.values())
				.filter((each) -> value.toLowerCase().equals(each.getValue().toLowerCase()))
				.findFirst()
				.orElse(null);
	}

	public static Sex fromKey(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		return Arrays.stream(Sex.values())
				.filter((each) -> key.equals(each.getKey()))
				.findFirst()
				.orElse(null);
	}
}
