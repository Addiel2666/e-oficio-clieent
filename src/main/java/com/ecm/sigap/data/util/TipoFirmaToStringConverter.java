/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.TipoFirma;

/**
 * Ajusta el valor del Tipo de Registro {@link TipoFirma} a su representacion
 * String
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class TipoFirmaToStringConverter implements AttributeConverter<TipoFirma, String>, Serializable {

	/** */
	private static final long serialVersionUID = -6939979571789019890L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public String convertToDatabaseColumn(TipoFirma value) {
		if (null != value) {
			return value.getTipo();
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
	public TipoFirma convertToEntityAttribute(String value) {
		if (value == null)
			return null;
		else
			return TipoFirma.fromString(value);
	}
}