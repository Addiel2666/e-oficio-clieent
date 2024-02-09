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
public class FolioKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -131729238163233690L;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The folio. */
	@Column(name = "folio", nullable = false, precision = 38, scale = 0)
	private Integer folio;

	/**
	 * 
	 */
	public FolioKey() {
		super();
	}

	/**
	 * 
	 * @param idArea
	 * @param folio
	 */
	public FolioKey(Integer idArea, Integer folio) {
		super();
		this.idArea = idArea;
		this.folio = folio;
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
	 * @param idArea the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the folio.
	 *
	 * @return the folio
	 */
	public Integer getFolio() {
		return folio;
	}

	/**
	 * Sets the folio.
	 *
	 * @param folio the new folio
	 */
	public void setFolio(Integer folio) {
		this.folio = folio;
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
		return "FolioKey [idArea=" + idArea + ", folio=" + folio + "]";
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

		if (!(obj instanceof FolioKey)) {
			return false;
		}

		if (!FolioKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FolioKey tmp = (FolioKey) obj;

		try {

			if (tmp.folio == this.folio//
					&& tmp.idArea == this.idArea//
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
		return Objects.hash(this.folio, this.idArea);
	}

}
