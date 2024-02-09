/**
 * Copyright (c) 2023 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.StatusAsunto;

/**
 * 
 * 
 * @author ECM Solutions
 * @version 1.0
 *
 */
@Converter
public class StatusAsuntoConverter implements AttributeConverter<StatusAsunto, Integer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8208776979411392181L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public Integer convertToDatabaseColumn(StatusAsunto value) {
		return value.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public StatusAsunto convertToEntityAttribute(Integer value) {
		return StatusAsunto.fromVal(Integer.valueOf(value));
	}
}