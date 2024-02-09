/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

// TODO: Auto-generated Javadoc
/**
 * The Class InfomexSolicitudKey.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Embeddable
public class FolioAreaKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6733490833380206997L;

	/** The id area. */
	@Column(name = "idArea", nullable = false, precision = 38, scale = 0)
	private Integer idArea;

	/** The id tipo folio. */
	@Column(name = "idTipoFolio", nullable = false, precision = 38, scale = 0)
	private Integer idTipoFolio;

	/**
	 * Instantiates a new folio area key.
	 */
	public FolioAreaKey() {
		super();
	}

	/**
	 * Instantiates a new folio area key.
	 *
	 * @param idArea
	 *            the id area
	 * @param idTipoFolio
	 *            the id tipo folio
	 */
	public FolioAreaKey(Integer idArea, Integer idTipoFolio) {
		super();
		this.idArea = idArea;
		this.idTipoFolio = idTipoFolio;
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
	 * Gets the id tipo folio.
	 *
	 * @return the id tipo folio
	 */
	public Integer getIdTipoFolio() {
		return idTipoFolio;
	}

	/**
	 * Sets the id tipo folio.
	 *
	 * @param idTipoFolio
	 *            the new id tipo folio
	 */
	public void setIdTipoFolio(Integer idTipoFolio) {
		this.idTipoFolio = idTipoFolio;
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
		return "FolioAreaKey [idArea=" + idArea + ", idTipoFolio=" + idTipoFolio + "]";
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

		if (!(obj instanceof FolioAreaKey)) {
			return false;
		}

		if (!FolioAreaKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FolioAreaKey tmp = (FolioAreaKey) obj;

		try {

			if (tmp.idTipoFolio == this.idTipoFolio//
					&& tmp.idArea == this.idArea //
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
		return Objects.hash(this.idArea, this.idTipoFolio);
	}

}
