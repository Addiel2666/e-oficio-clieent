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

/**
 * Clase de entidad que representa la clave compuesta de la vista
 * FAVAREASREMITENTES
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 */
@Embeddable
public class FavoritoRemitenteKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3053203706769116073L;

	/** Identificador del Area a la que pertenece el Remitente */
	@Column(name = "idArea")
	private Integer idArea;

	/** Institucion a la que pertence el Remitente */
	@OneToOne
	@JoinColumn(name = "idPromotor")
	@Fetch(FetchMode.SELECT)
	private Institucion promotor;

	@Column(name = "idRemitente")
	private Integer idRemitente;

	/**
	 * Obtiene el Identificador del Area a la que pertenece el Remitente
	 *
	 * @return Identificador del Area a la que pertenece el Remitente
	 */
	public Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del Area a la que pertenece el Remitente
	 *
	 * @param idArea Identificador del Area a la que pertenece el Remitente
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * @return the idPromotor
	 */
	public final Institucion getPromotor() {
		return promotor;
	}

	/**
	 * @param idPromotor the idPromotor to set
	 */
	public final void setPromotor(Institucion promotor) {
		this.promotor = promotor;
	}

	/**
	 * @return the idRemitente
	 */
	public final Integer getIdRemitente() {
		return idRemitente;
	}

	/**
	 * @param idRemitente the idRemitente to set
	 */
	public final void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FavoritoRemitenteKey [idArea=" + idArea + ", idPromotor=" + promotor + ", idRemitente=" + idRemitente
				+ "]";
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

		if (!(obj instanceof FavoritoRemitenteKey)) {
			return false;
		}

		if (!FavoritoRemitenteKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavoritoRemitenteKey tmp = (FavoritoRemitenteKey) obj;

		try {

			if (tmp.idArea == this.idArea //
					&& tmp.idRemitente == this.idRemitente//
					&& tmp.promotor == this.promotor//
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
		return Objects.hash(this.idArea, this.idRemitente, this.promotor);
	}

}
