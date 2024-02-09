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
 * Clase de entidad que representa la clave compuesta de la tabla AREASREMITENTE
 * 
 * @author Alejandro Guzman
 * @version 1.0
 */
@Embeddable
public class AreaRemitenteKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3053203706769116073L;

	/** Identificador del Area a la que pertenece el Remitente */
	@Column(name = "idArea")
	private Integer idArea;

	/** Identificador de la Institucion a la que pertence el Remitente */
	@Column(name = "idInstitucion")
	private Integer idInstitucion;

	/** Identificador del Area Remitente */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idRemitente")
	@Fetch(FetchMode.SELECT)
	private Area remitente;

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
	 * Obtiene el Identificador de la Institucion a la que pertence el Remitente.
	 *
	 * @return Identificador de la Institucion a la que pertence el Remitente
	 */
	public Integer getIdInstitucion() {

		return idInstitucion;
	}

	/**
	 * Asigna el Identificador de la Institucion a la que pertence el Remitente
	 *
	 * @param idInstitucion Identificador de la Institucion a la que pertence el
	 *                      Remitente
	 */
	public void setIdInstitucion(Integer idInstitucion) {

		this.idInstitucion = idInstitucion;
	}

	/**
	 * Obtiene el Identificador del Area Remitente
	 * 
	 * @return Identificador del Area Remitente
	 */
	public Area getAreaRemitente() {

		return remitente;
	}

	/**
	 * Asigna el Identificador del Area Remitente
	 * 
	 * @param remitente Identificador del Area Remitente
	 */
	public void setAreaRemitente(Area remitente) {

		this.remitente = remitente;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AreaRemitenteKey [idArea=" + idArea + ", idInstitucion=" + idInstitucion + ", remitente=" + remitente
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
		if (!AreaRemitenteKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		AreaRemitenteKey tmp = (AreaRemitenteKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idInstitucion == this.idInstitucion//
					&& tmp.remitente == this.remitente)
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
		return Objects.hash(this.idArea, this.idInstitucion, this.remitente);
	}

}
