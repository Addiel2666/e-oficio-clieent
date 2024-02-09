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
 * Clave primaria de la entidad Remitente
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@Embeddable
public class RemitenteKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4164542872541487384L;

	/** Institucion a la que pertence el Remitente */
	@OneToOne
	@JoinColumn(name = "idPromotor")
	@Fetch(FetchMode.SELECT)
	private Institucion promotor;

	/** Identificador del Area / Empresa */
	@Column(name = "idRemitente")
	private Integer idRemitente;

	/**
	 * Obtiene el Identificador del Area / Empresa
	 * 
	 * @return Identificador del Area / Empresa
	 */
	public Integer getIdRemitente() {

		return idRemitente;
	}

	/**
	 * Asigna el Identificador del Area / Empresa
	 * 
	 * @param idRemitente
	 *            Identificador del Area / Empresa
	 */
	public void setIdRemitente(Integer idRemitente) {

		this.idRemitente = idRemitente;
	}

	/**
	 * Obtiene la Institucion a la que pertence el Remitente
	 * 
	 * @return Institucion a la que pertence el Remitente
	 */
	public final Institucion getPromotor() {

		return promotor;
	}

	/**
	 * Asigna la Institucion a la que pertence el Remitente
	 * 
	 * @param promotor
	 *            Institucion a la que pertence el Remitente
	 */
	public final void setPromotor(Institucion promotor) {

		this.promotor = promotor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "RemitenteKey [promotor=" + promotor + ", idRemitente=" + idRemitente + "]";
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

		if (!(obj instanceof RemitenteKey)) {
			return false;
		}

		if (!RemitenteKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		RemitenteKey tmp = (RemitenteKey) obj;

		try {

			if (tmp.idRemitente == this.idRemitente//
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
		return Objects.hash(this.idRemitente, this.promotor);
	}

}
