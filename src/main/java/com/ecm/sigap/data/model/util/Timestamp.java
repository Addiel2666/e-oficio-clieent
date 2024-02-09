/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Embeddable
public class Timestamp {

	/** */
	@Lob
	@Column(name = "timestamp_b64")
	private String timestamp;
	/** */
	@Column(name = "tipo")
	@Enumerated(EnumType.ORDINAL)
	private TipoTimestamp tipo;

	/** */
	public Timestamp() {
		super();
	}

	/**
	 * 
	 * @param timestamp
	 * @param tipo
	 */
	public Timestamp(String timestamp, TipoTimestamp tipo) {
		super();
		this.timestamp = timestamp;
		this.tipo = tipo;
	}

	/**
	 * @return the tipo
	 */
	public TipoTimestamp getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(TipoTimestamp tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Timestamp [" + (timestamp != null ? "timestamp=" + timestamp + ", " : "")
				+ (tipo != null ? "tipo=" + tipo : "") + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timestamp other = (Timestamp) obj;
		if (tipo != other.tipo)
			return false;
		return true;
	}
	
	
	

}
