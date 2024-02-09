/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.interop.TipoModificacion;

/**
 * Conversor de datos a {@link TipoModificacion}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class TipoModificacionToStringConverter implements AttributeConverter<TipoModificacion, String>, Serializable {

	/** */
	private static final long serialVersionUID = -8244393599319101354L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(TipoModificacion value) {
		return value.getTipo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public TipoModificacion convertToEntityAttribute(String value) {
		return TipoModificacion.fromString(value);
	}
}