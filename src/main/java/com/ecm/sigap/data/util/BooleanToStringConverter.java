/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Ajusta el valos "S" o "Y" a un valor booleano.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String>, Serializable {

	/** */
	private static final long serialVersionUID = -875299822552388332L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(Boolean value) {
		return (value != null && value) ? "S" : "N";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public Boolean convertToEntityAttribute(String value) {
		return "Y".equalsIgnoreCase(value) || "S".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value)
				|| "SI".equalsIgnoreCase(value);
	}
}