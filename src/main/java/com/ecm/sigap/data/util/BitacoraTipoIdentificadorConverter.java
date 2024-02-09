/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.BitacoraTipoIdentificador;

/**
 * 
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class BitacoraTipoIdentificadorConverter
		implements AttributeConverter<BitacoraTipoIdentificador, String>, Serializable {

	/** */
	private static final long serialVersionUID = 5690343286805081299L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(BitacoraTipoIdentificador value) {
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
	public BitacoraTipoIdentificador convertToEntityAttribute(String value) {
		return BitacoraTipoIdentificador.fromString(value);
	}
}