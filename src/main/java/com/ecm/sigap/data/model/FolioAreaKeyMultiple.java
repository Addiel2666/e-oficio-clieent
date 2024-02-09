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
 * @author ECM Solutions
 * @version 1.0
 */
@Embeddable
public class FolioAreaKeyMultiple implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6733490833380206997L;

	/** The id FoliopsMultiple. */
	@Column(name = "idFoliopsMultiple", nullable = false, precision = 38, scale = 0)
	private Integer idFoliopsMultiple;

	/** The id tipo folio. */
	@Column(name = "idTipoFolio", nullable = false, precision = 38, scale = 0)
	private Integer idTipoFolio;

	/**
	 * Instantiates a new folio area key.
	 */
	public FolioAreaKeyMultiple() {
		super();
	}

	/**
	 * Instantiates a new folio area key.
	 *
	 * @param idFoliopsMultiple
	 *            the id FoliopsMultiple
	 * @param idTipoFolio
	 *            the id tipo folio
	 */
	public FolioAreaKeyMultiple(Integer idFoliopsMultiple, Integer idTipoFolio) {
		super();
		this.idFoliopsMultiple = idFoliopsMultiple;
		this.idTipoFolio = idTipoFolio;
	}

	/**
	 * @return the idFoliopsMultiple
	 */
	public Integer getIdFoliopsMultiple() {
		return idFoliopsMultiple;
	}

	/**
	 * @param idFoliopsMultiple
	 *            the idFoliopsMultiple to set
	 */
	public void setIdFoliopsMultiple(Integer idFoliopsMultiple) {
		this.idFoliopsMultiple = idFoliopsMultiple;
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
		return "FolioAreaKey [idFoliopsMultiple=" + idFoliopsMultiple + ", idTipoFolio=" + idTipoFolio + "]";
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

		if (!(obj instanceof FolioAreaKeyMultiple)) {
			return false;
		}

		if (!FolioAreaKeyMultiple.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FolioAreaKeyMultiple tmp = (FolioAreaKeyMultiple) obj;

		try {

			if (tmp.idTipoFolio == this.idTipoFolio//
					&& tmp.idFoliopsMultiple == this.idFoliopsMultiple //
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
		return Objects.hash(this.idFoliopsMultiple, this.idTipoFolio);
	}

}
