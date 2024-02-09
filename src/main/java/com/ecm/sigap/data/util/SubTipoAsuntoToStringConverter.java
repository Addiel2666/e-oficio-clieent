/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.SubTipoAsunto;

/**
 * Ajusta el valor del SubTipo de Asunto a su representacion String
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class SubTipoAsuntoToStringConverter implements AttributeConverter<SubTipoAsunto, String>, Serializable {

	/** */
	private static final long serialVersionUID = 3023793622582525398L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(SubTipoAsunto value) {
		if (null != value) {

			return value.getValue();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public SubTipoAsunto convertToEntityAttribute(String value) {
		
		return SubTipoAsunto.fromTipo(value);
	}
}