package com.blocklang.system.constant.converter;

import javax.persistence.AttributeConverter;

import com.blocklang.system.constant.Sex;

public class SexConverter implements AttributeConverter<Sex, String> {

	@Override
	public String convertToDatabaseColumn(Sex attribute) {
		return attribute == null ? null : attribute.getKey();
	}

	@Override
	public Sex convertToEntityAttribute(String dbData) {
		return Sex.fromKey(dbData);
	}

}