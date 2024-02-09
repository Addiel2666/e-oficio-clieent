package com.ecm.sigap.ope.dao.model;

import java.util.List;

import javax.persistence.AttributeConverter;

public class JsonArrayConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		// TODO Auto-generated method stub
		return null;
	}

}
