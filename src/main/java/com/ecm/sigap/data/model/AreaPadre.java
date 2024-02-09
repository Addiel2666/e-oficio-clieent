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
 * The Class AreaPadre
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */
@Entity
@Table(name = "areasPadre")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public final class AreaPadre implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9148234048624086294L;

	/** The id area. */
	@Id
	@Column(name = "idArea", insertable = false)
	private Integer idArea;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The id area padre. */
	@Column(name = "idAreaPadre")
	private Integer idAreaPadre;

	/** The interopera. */
	@Column(name = "interoperasn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean interopera;

	/**
	 * Gets the id area.
	 *
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea
	 *            the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the id area padre.
	 *
	 * @return the id area padre
	 */
	public Integer getIdAreaPadre() {
		return idAreaPadre;
	}

	/**
	 * Sets the id area padre.
	 *
	 * @param idAreaPadre
	 *            the new id area padre
	 */
	public void setIdAreaPadre(Integer idAreaPadre) {
		this.idAreaPadre = idAreaPadre;
	}

	@Override
	public String toString() {
		return "AreaPadre [idArea=" + idArea + ", descripcion=" + descripcion + ", idAreaPadre=" + idAreaPadre + "]";
	}

	public Boolean getInteropera() {
		return interopera;
	}

	public void setInteropera(Boolean interopera) {
		this.interopera = interopera;
	}

}
