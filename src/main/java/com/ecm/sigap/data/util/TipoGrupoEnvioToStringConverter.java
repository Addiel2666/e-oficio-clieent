/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.TipoGrupoEnvio;

/**
 * Ajusta el valor del Tipo de Asunto a su representacion String
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class TipoGrupoEnvioToStringConverter implements AttributeConverter<TipoGrupoEnvio, String>, Serializable {

	/** */
	private static final long serialVersionUID = -2955623410201769336L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(TipoGrupoEnvio value) {
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
	public TipoGrupoEnvio convertToEntityAttribute(String value) {
		return TipoGrupoEnvio.fromTipo(value);
	}
}