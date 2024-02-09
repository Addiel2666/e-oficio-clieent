/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

/**
 * Clase de entidad que representa la vista de REMITENTES
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@Entity
@Table(name = "REMITENTES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@Immutable
public class Remitente implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2665392952367012292L;

	/** Clave primaria compuesta de la Entidad */
	@EmbeddedId
	private RemitenteKey remitenteKey;

	/** Descripcion del Area / Empresa */
	@Column(name = "desRemitente")
	private String descripcion;

	/**
	 * Obtiene la Clave primaria compuesta de la Entidad
	 * 
	 * @return Clave primaria compuesta de la Entidad
	 */
	public RemitenteKey getRemitenteKey() {
		return remitenteKey;
	}

	/**
	 * Asigna la Clave primaria compuesta de la Entidad
	 * 
	 * @param remitenteKey
	 *            Clave primaria compuesta de la Entidad
	 */
	public void setRemitenteKey(RemitenteKey remitenteKey) {
		this.remitenteKey = remitenteKey;
	}

	/**
	 * Obtiene la Descripcion del Area / Empresa
	 * 
	 * @return Descripcion del Area / Empresa
	 */
	public String getDescripcion() {

		return descripcion;
	}

	/**
	 * Asigna la Descripcion del Area / Empresa
	 * 
	 * @param descripcion
	 *            Descripcion del Area / Empresa
	 */
	public void setDescripcion(String descripcion) {

		this.descripcion = descripcion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "Remitente [remitenteKey=" + remitenteKey + ", descripcion=" + descripcion + "]";
	}

}
