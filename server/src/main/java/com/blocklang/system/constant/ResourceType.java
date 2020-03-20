package com.blocklang.system.constant;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 资源类型
 *
 */
public enum ResourceType {
	
	FUNCTION("01", "功能模块"),
	PROGRAM("02", "程序模块"),
	OPERATOR("03", "操作按钮");

	private final String key;
	private final String value;

	private ResourceType(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@JsonValue
	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public static ResourceType fromValue(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return Arrays.stream(ResourceType.values())
				.filter((each) -> value.toLowerCase().equals(each.getValue().toLowerCase()))
				.findFirst()
				.orElse(null);
	}

	public static ResourceType fromKey(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		return Arrays.stream(ResourceType.values())
				.filter((each) -> key.equals(each.getKey()))
				.findFirst()
				.orElse(null);
	}
}
