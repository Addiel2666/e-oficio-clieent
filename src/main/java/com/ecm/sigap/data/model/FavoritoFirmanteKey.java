/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * The Class FavoritoFirmanteKey.
 */
@Embeddable
public class FavoritoFirmanteKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3053203706769116073L;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The firmante. */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idFirmante")
	@Fetch(FetchMode.SELECT)
	private Representante firmante;

	/** The id firma area. */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idFirmArea")
	@Fetch(FetchMode.SELECT)
	private AreaAuxiliar firmArea;

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
	 * Gets the firmante.
	 *
	 * @return the firmante
	 */
	public Representante getFirmante() {
		return firmante;
	}

	/**
	 * Sets the firmante.
	 *
	 * @param firmante the new firmante
	 */
	public void setFirmante(Representante firmante) {
		this.firmante = firmante;
	}

	/**
	 * Gets the firma area.
	 *
	 * @return the firma area
	 */
	public AreaAuxiliar getFirmArea() {
		return firmArea;
	}

	/**
	 * Sets the firma area.
	 *
	 * @param firmaArea the new firma area
	 */
	public void setFirmArea(AreaAuxiliar firmaArea) {
		this.firmArea = firmaArea;
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
		return "FavoritoFirmanteKey [idArea=" + idArea + ", firmante=" + firmante + ", firmArea=" + firmArea + "]";
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

		if (!(obj instanceof FavoritoFirmanteKey)) {
			return false;
		}

		if (!FavoritoFirmanteKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavoritoFirmanteKey tmp = (FavoritoFirmanteKey) obj;

		try {

			if (tmp.firmante == this.firmante//
					&& tmp.firmArea == this.firmArea //
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
		return Objects.hash(this.idArea, this.firmante, this.firmArea);
	}

}
