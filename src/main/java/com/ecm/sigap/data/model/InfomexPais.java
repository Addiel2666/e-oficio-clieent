/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "SISI_PAISES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class InfomexPais implements Serializable {

	/**  */
	private static final long serialVersionUID = -5346610493645676972L;

	/**  */
	@Id
	@Column(name = "id")
	private Integer id;

	/** */
	@Column(name = "idClave")
	private String idClave;

	/** */
	@Column(name = "descripcion")
	private String descripcion;

	/** */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the idClave
	 */
	public String getIdClave() {
		return idClave;
	}

	/**
	 * @param idClave
	 *            the idClave to set
	 */
	public void setIdClave(String idClave) {
		this.idClave = idClave;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * @param activo
	 *            the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfomexPais [id=" + id + ", idClave=" + idClave + ", descripcion=" + descripcion + ", activo=" + activo
				+ "]";
	}

}