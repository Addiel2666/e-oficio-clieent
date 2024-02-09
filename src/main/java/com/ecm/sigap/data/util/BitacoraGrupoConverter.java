/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ecm.sigap.data.model.util.BitacoraGrupo;

/**
 * 
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Converter
public class BitacoraGrupoConverter implements AttributeConverter<BitacoraGrupo, Integer>, Serializable {

	/**  */
	private static final long serialVersionUID = 2040175290037908128L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang
	 * .Object)
	 */
	@Override
	public Integer convertToDatabaseColumn(BitacoraGrupo value) {
		return value.getGrupo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang
	 * .Object)
	 */
	@Override
	public BitacoraGrupo convertToEntityAttribute(Integer value) {
		return BitacoraGrupo.fromString(value);
	}
}