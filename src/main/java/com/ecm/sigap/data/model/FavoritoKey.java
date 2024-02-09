/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Class FavoritoKey.
 */
@Embeddable
public class FavoritoKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4569921769294537150L;

	/** The id area. */
	@Column(name = "idArea", insertable = false, updatable = false)
	private Integer idArea;

	/** The id firm area. */
	@Column(name = "idFirmArea", insertable = false, updatable = false)
	private Integer idFirmArea;

	/** The id institucion. */
	@Column(name = "idInstitucion", insertable = false, updatable = false)
	private Integer idInstitucion;

	/** The id firmante. */
	@Column(name = "idFirmante", insertable = false, updatable = false)
	private String idFirmante;

	/** The id area representante. */
	@Column(name = "idAreaRepresentante", insertable = false, updatable = false)
	private Integer idAreaRepresentante;

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
	 * Gets the id firm area.
	 *
	 * @return the id firm area
	 */
	public Integer getIdFirmArea() {
		return idFirmArea;
	}

	/**
	 * Sets the id firm area.
	 *
	 * @param idFirmArea
	 *            the new id firm area
	 */
	public void setIdFirmArea(Integer idFirmArea) {
		this.idFirmArea = idFirmArea;
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
	 * Gets the id firmante.
	 *
	 * @return the id firmante
	 */
	public String getIdFirmante() {
		return idFirmante;
	}

	/**
	 * Sets the id firmante.
	 *
	 * @param idFirmante
	 *            the new id firmante
	 */
	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	/**
	 * Gets the id area representante.
	 *
	 * @return the id area representante
	 */
	public Integer getIdAreaRepresentante() {
		return idAreaRepresentante;
	}

	/**
	 * Sets the id area representante.
	 *
	 * @param idAreaRepresentante
	 *            the new id area representante
	 */
	public void setIdAreaRepresentante(Integer idAreaRepresentante) {
		this.idAreaRepresentante = idAreaRepresentante;
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
		return "FavoritoKey [idArea=" + idArea + ", idFirmArea=" + idFirmArea + ", idInstitucion=" + idInstitucion
				+ ", idFirmante=" + idFirmante + ", idAreaRepresentante=" + idAreaRepresentante + "]";
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

		if (!(obj instanceof FavoritoKey)) {
			return false;
		}

		if (!FavoritoKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavoritoKey tmp = (FavoritoKey) obj;

		try {

			if (tmp.idAreaRepresentante == this.idAreaRepresentante//
					&& tmp.idArea == this.idArea //
					&& tmp.idFirmArea == this.idFirmArea//
					&& tmp.idInstitucion == this.idInstitucion//
					&& tmp.idFirmante.equals(this.idFirmante)//
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
		return Objects.hash(this.idArea, this.idAreaRepresentante, this.idFirmante, this.idFirmante, this.idFirmArea,
				this.idInstitucion);
	}

}
