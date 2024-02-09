/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The Class AreaPromotorKey.
 */
@Embeddable
public final class AreaPromotorKey implements Serializable {

	/**  */
	private static final long serialVersionUID = -1460902893416349284L;

	/** The institucion. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idInstitucion")
	@Fetch(value = FetchMode.SELECT)
	private Institucion institucion;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

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
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion the new institucion
	 */
	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AreaPromotorKey [idArea=" + idArea + ", institucion=" + institucion + "]";
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
		if (!AreaPromotorKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		AreaPromotorKey tmp = (AreaPromotorKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.institucion == this.institucion)
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
		return Objects.hash(this.idArea, this.institucion);
	}

}
