/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.StatusInstitucionOpe;

/**
 * Ajusta el valor del Status de InstitucionOpe a su representacion String
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@Converter
public class StatusInstitucionOpeToStringConverter implements AttributeConverter<StatusInstitucionOpe, String>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8434291412460695614L;

	/** */

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(StatusInstitucionOpe value) {
		if (null != value) {

			return value.getValue();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public StatusInstitucionOpe convertToEntityAttribute(String value) {

		return StatusInstitucionOpe.fromTipo(value);
	}
}