/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.dao.model;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.EnTiempo;

/**
 * Ajusta el valor "En Tiempo" o "Fuera de Tiempo" a un valor booleano.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class ETFTToStringConverter implements AttributeConverter<EnTiempo, String>, Serializable {

	/** */
	private static final long serialVersionUID = 2025063468634855458L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(EnTiempo value) {
		return value != null ? value.getTipo() : EnTiempo.EN_TIEMPO.getTipo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public EnTiempo convertToEntityAttribute(String value) {
		return EnTiempo.fromTipo(value);
	}
}