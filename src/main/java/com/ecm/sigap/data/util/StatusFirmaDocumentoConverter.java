package com.ecm.sigap.data.util;

import java.io.Serializable;

import javax.persistence.AttributeConverter;

import com.ecm.sigap.data.model.util.StatusFirmaDocumento;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class StatusFirmaDocumentoConverter implements AttributeConverter<StatusFirmaDocumento, String>, Serializable {

	/** */
	private static final long serialVersionUID = 2922913007823503163L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.
	 * Object)
	 */
	@Override
	public String convertToDatabaseColumn(StatusFirmaDocumento attribute) {
		return attribute != null ? attribute.getTipo() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.
	 * Object)
	 */
	@Override
	public StatusFirmaDocumento convertToEntityAttribute(String dbData) {
		return dbData != null ? StatusFirmaDocumento.fromString(dbData) : null;
	}

}
