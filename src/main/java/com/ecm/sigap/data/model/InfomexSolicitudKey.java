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
public class InfomexSolicitudKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 313562430475448201L;

	/** The foliosisi. */
	@Column(name = "folioSisi")
	private String folioSisi;

	/** The id institucion. */
	@Column(name = "idInstitucion")
	private Integer idInstitucion;

	/**
	 * Gets the folio sisi.
	 *
	 * @return the folio sisi
	 */
	public String getFolioSisi() {
		return folioSisi;
	}

	/**
	 * Sets the folio sisi.
	 *
	 * @param folioSisi
	 *            the new folio sisi
	 */
	public void setFolioSisi(String folioSisi) {
		this.folioSisi = folioSisi;
	}

	/**
	 * Gets the id institucion.
	 *
	 * @return the id institucion
	 */
	public Integer getIdInstitucion() {
		return idInstitucion;
	}

	/**
	 * Sets the id institucion.
	 *
	 * @param idInstitucion
	 *            the new id institucion
	 */
	public void setIdInstitucion(Integer idInstitucion) {
		this.idInstitucion = idInstitucion;
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
		return "InfomexSolicitudKey [folioSisi=" + folioSisi + ", idInstitucion=" + idInstitucion + "]";
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

		if (!(obj instanceof InfomexSolicitudKey)) {
			return false;
		}

		if (!InfomexSolicitudKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		InfomexSolicitudKey tmp = (InfomexSolicitudKey) obj;

		try {

			if (tmp.folioSisi.equals(this.folioSisi)//
					&& tmp.idInstitucion == this.idInstitucion//
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
		return Objects.hash(this.folioSisi, this.idInstitucion);
	}

}
