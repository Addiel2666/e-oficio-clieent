/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.TipoRegistroWsOpe;

/**
 * Ajusta el valor del Tipo de Registro de wssincronizacompletadetalle a su
 * representacion String
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@Converter
public class TipoRegistroWsOpeToStringConverter implements AttributeConverter<TipoRegistroWsOpe, String>, Serializable {

	/** */
	private static final long serialVersionUID = -5008441544952821327L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(TipoRegistroWsOpe value) {
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
	public TipoRegistroWsOpe convertToEntityAttribute(String value) {

		return TipoRegistroWsOpe.fromTipo(value);
	}
}