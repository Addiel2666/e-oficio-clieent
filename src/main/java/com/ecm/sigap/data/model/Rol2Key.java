/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class Rol2Key implements Serializable {

	/** */
	private static final long serialVersionUID = 1216865869414065014L;

	/** Identificador del Rol */
	@Column(name = "idRol")
	private Integer idRol;

	/** Identificador del Area al que pertenece el Rol */
	@Column(name = "idArea")
	private Integer idArea;

	/**
	 * @return the idRol
	 */
	public Integer getIdRol() {
		return idRol;
	}

	/**
	 * @param idRol the idRol to set
	 */
	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rol2Key [idRol=" + idRol + ", idArea=" + idArea + "]";
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

		if (!(obj instanceof Rol2Key)) {
			return false;
		}

		if (!Rol2Key.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		Rol2Key tmp = (Rol2Key) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idRol == this.idRol//
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
		return Objects.hash(this.idArea, this.idRol);
	}

}
