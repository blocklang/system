package com.blocklang.system.constant.converter;

import javax.persistence.AttributeConverter;

import com.blocklang.system.constant.ResourceType;

public class ResourceTypeConverter implements AttributeConverter<ResourceType, String> {

	@Override
	public String convertToDatabaseColumn(ResourceType attribute) {
		return attribute == null ? null : attribute.getKey();
	}

	@Override
	public ResourceType convertToEntityAttribute(String dbData) {
		return ResourceType.fromKey(dbData);
	}

}
