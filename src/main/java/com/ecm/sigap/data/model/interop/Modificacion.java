/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.interop;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "modificaciones_interop")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Modificacion {

	/** The parametro key. */
	@EmbeddedId
	private ModificacionKey modificacionKey;

	/**
	 * @return the modificacionKey
	 */
	public ModificacionKey getModificacionKey() {
		return modificacionKey;
	}

	/**
	 * @param modificacionKey
	 *            the modificacionKey to set
	 */
	public void setModificacionKey(ModificacionKey modificacionKey) {
		this.modificacionKey = modificacionKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Modificacion [modificacionKey=" + modificacionKey + "]";
	}

}
