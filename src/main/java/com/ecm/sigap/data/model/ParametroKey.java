/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Class InfomexSolicitudKey.
 */
@Embeddable
public class ParametroKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -131729238163233690L;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The id seccion. */
	@Column(name = "idSeccion")
	private String idSeccion;

	/** The id clave. */
	@Column(name = "idClave")
	private String idClave;

	/**
	 * Instantiates a new parametro key.
	 */
	public ParametroKey() {
		super();
	}

	/**
	 * Instantiates a new parametro key.
	 *
	 * @param idArea
	 *            the id area
	 * @param idSeccion
	 *            the id seccion
	 * @param idClave
	 *            the id clave
	 */
	public ParametroKey(Integer idArea, String idSeccion, String idClave) {
		super();
		this.idArea = idArea;
		this.idSeccion = idSeccion;
		this.idClave = idClave;
	}

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea
	 *            the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the id seccion.
	 *
	 * @return the id seccion
	 */
	public String getIdSeccion() {
		return idSeccion;
	}

	/**
	 * Sets the id seccion.
	 *
	 * @param idSeccion
	 *            the new id seccion
	 */
	public void setIdSeccion(String idSeccion) {
		this.idSeccion = idSeccion;
	}

	/**
	 * Gets the id clave.
	 *
	 * @return the id clave
	 */
	public String getIdClave() {
		return idClave;
	}

	/**
	 * Sets the id clave.
	 *
	 * @param idClave
	 *            the new id clave
	 */
	public void setIdClave(String idClave) {
		this.idClave = idClave;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ParametroKey [idArea=" + idArea + ", idSeccion=" + idSeccion + ", idClave=" + idClave + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this)
			return true;

		if (!(obj instanceof ParametroKey)) {
			return false;
		}

		if (!ParametroKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		ParametroKey tmp = (ParametroKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idClave.equals(this.idClave)//
					&& tmp.idSeccion.equals(this.idSeccion)//
			)
				return true;

		} catch (NullPointerException e) {
			return false;
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.idArea, this.idClave, this.idSeccion);
	}

}
