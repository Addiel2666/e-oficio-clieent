/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The Class Area Remitente.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "areasremitentes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AreaRemitente implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 203088857204248716L;

	/** Clave compuesta de la entidad */
	@EmbeddedId
	private AreaRemitenteKey areaRemitenteKey;

	/**
	 * Obtiene la Clave compuesta de la entidad
	 *
	 * @return Clave compuesta de la entidad
	 */
	public AreaRemitenteKey getAreaRemitenteKey() {
		return areaRemitenteKey;
	}

	/**
	 * Asigna la Clave compuesta de la entidad
	 *
	 * @param areaRemitenteKey
	 *            Clave compuesta de la entidad
	 */
	public void setAreaRemitenteKey(AreaRemitenteKey areaRemitenteKey) {

		this.areaRemitenteKey = areaRemitenteKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AreaRemitente [areaRemitenteKey=" + areaRemitenteKey + "]";
	}

}