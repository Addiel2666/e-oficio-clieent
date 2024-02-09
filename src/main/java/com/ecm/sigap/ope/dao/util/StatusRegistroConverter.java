/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.dao.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.ope.model.StatusRegistro;

/**
 * 
 * @author Alfredo Morales
 *
 */
@Converter
public class StatusRegistroConverter implements AttributeConverter<StatusRegistro, String>, Serializable {

	/**  */
	private static final long serialVersionUID = -8339777053166386228L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(StatusRegistro value) {
		return value != null ? value.getValue() : StatusRegistro.REGISTRADO.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public StatusRegistro convertToEntityAttribute(String value) {
		return StatusRegistro.fromTipo(value);
	}
}