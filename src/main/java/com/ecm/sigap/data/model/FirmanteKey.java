/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de la entidad Firmante
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@Embeddable
public class FirmanteKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8928863946794127799L;

	/** Identificador de la Institucion / Empresa a la que pertence el firmante */
	@Column(name = "idPromotor")
	private Integer idPromotor;
	
	/** Identificador del Area o Empresa a la que pertenece el Firmante */
	@Column(name = "idRemitente")
	private Integer idRemitente;
	
	/** Identificador del Firmante */
	@Column(name = "idfirmante")
	private String idFirmante;

	/**
	 * Otiene el Identificador del Firmante
	 * 
	 * @return Identificador del Firmante
	 */
	public final String getIdFirmante() {

		return idFirmante;
	}

	/**
	 * Asigna el Identificador del Firmante
	 * 
	 * @param idFirmante
	 *            Identificador del Firmante
	 */
	public final void setIdFirmante(String idFirmante) {

		this.idFirmante = idFirmante;
	}

	/**
	 * Obtiene el Tipo de Firmante
	 * 
	 * @return Tipo de Firmante
	 */
	public final Integer getIdPromotor() {

		return idPromotor;
	}

	/**
	 * Asigna el Tipo de Firmante
	 * 
	 * @param tipoFirmante
	 *            Tipo de Firmante
	 */
	public final void setIdPromotor(Integer idPromotor) {

		this.idPromotor = idPromotor;
	}

	/**
	 * Obtiene el Identificador del Area o Empresa a la que pertenece el
	 * Firmante
	 * 
	 * @return Identificador del Area o Empresa a la que pertenece el Firmante
	 */
	public final Integer getIdRemitente() {

		return idRemitente;
	}

	/**
	 * Asigna el Identificador del Area o Empresa a la que pertenece el Firmante
	 * 
	 * @param remitente
	 *            Identificador del Area o Empresa a la que pertenece el
	 *            Firmante
	 */
	public final void setIdRemitente(Integer remitente) {

		this.idRemitente = remitente;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FirmanteKey [idFirmante=" + idFirmante + ", idPromotor=" + idPromotor + ", idRemitente="
				+ idRemitente + "]";
	}

}
