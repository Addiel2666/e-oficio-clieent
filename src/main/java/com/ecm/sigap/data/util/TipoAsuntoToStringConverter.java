/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.TipoAsunto;

/**
 * Ajusta el valor del Tipo de Asunto a su representacion String
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class TipoAsuntoToStringConverter implements AttributeConverter<TipoAsunto, String>, Serializable {

	/** */
	private static final long serialVersionUID = -2955623410201769336L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(TipoAsunto value) {
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
	public TipoAsunto convertToEntityAttribute(String value) {

		return TipoAsunto.fromTipo(value);
	}
}