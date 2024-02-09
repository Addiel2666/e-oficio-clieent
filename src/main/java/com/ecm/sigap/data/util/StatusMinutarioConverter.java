/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.StatusMinutario;

/**
 * 
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class StatusMinutarioConverter implements AttributeConverter<StatusMinutario, Integer>, Serializable {

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
	public Integer convertToDatabaseColumn(StatusMinutario value) {
		return value.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public StatusMinutario convertToEntityAttribute(Integer value) {
		return StatusMinutario.fromVal(Integer.valueOf(value));
	}
}