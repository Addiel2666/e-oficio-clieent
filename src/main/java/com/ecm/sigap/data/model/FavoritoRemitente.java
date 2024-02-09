/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * Clase de entidad que representa la vista FAVAREASREMITENTES
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "favareasremitentes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class FavoritoRemitente implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 203088857204248716L;

	/** */
	@Column(name = "desremitente")
	private String descripcion;

	/** */
	@Column(name = "titularcargo")
	private String titularCargo;

	/** */
	@Column(name = "titularusuario")
	private String titularUsuario;

	/** Clave compuesta de la entidad */
	@EmbeddedId
	private FavoritoRemitenteKey favoritoRemitenteKey;
	
	/** The activo. */
	@Transient
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activosn;


	public Boolean getActivosn() {
		return activosn;
	}

	public void setActivosn(Boolean activosn) {
		this.activosn = activosn;
	}

	/**
	 * Obtiene la Clave compuesta de la entidad
	 *
	 * @return Clave compuesta de la entidad
	 */
	public FavoritoRemitenteKey getFavoritoRemitenteKey() {

		return favoritoRemitenteKey;
	}

	/**
	 * Asigna la Clave compuesta de la entidad
	 *
	 * @param favoritoRemitenteKey Clave compuesta de la entidad
	 */
	public void setFavoritoRemitenteKey(FavoritoRemitenteKey favoritoRemitenteKey) {

		this.favoritoRemitenteKey = favoritoRemitenteKey;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the titularcargo
	 */
	public String getTitularCargo() {
		return titularCargo;
	}

	/**
	 * @param descripcion the titular cargo to set
	 */
	public void setTitularCargo(String titularCargo) {
		this.titularCargo = titularCargo;
	}

	/**
	 * @return the titularusuario
	 */
	public String getTitularUsuario() {
		return titularUsuario;
	}

	/**
	 * @param descripcion the titularusuario to set
	 */
	public void setTitularUsuario(String titularUsuario) {
		this.titularUsuario = titularUsuario;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FavoritoRemitente [descripcion=" + descripcion + ", titularCargo=" + titularCargo + ", titularUsuario="
				+ titularUsuario + ", favoritoRemitenteKey=" + favoritoRemitenteKey + "]";
	}

}
