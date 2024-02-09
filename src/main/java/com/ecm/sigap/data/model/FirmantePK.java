/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */

package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * The Class ParametroAppPK.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
public class FirmantePK implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5123546223886145874L;

	/** The id firmante. */
	protected String idFirmante;

	/** The id promotor. */
	protected Integer idPromotor;
	
	/** The id remitente */
	protected Integer idRemitente;
	
	/** Tipo firmante */
	protected String tipoFirmante;

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
	 * Gets the id promotor.
	 *
	 * @return the id promotor
	 */
	public Integer getIdPromotor() {
		return idPromotor;
	}

	/**
	 * Sets the id promotor.
	 *
	 * @param idPromotor
	 *            the new id promotor
	 */
	public void setIdPromotor(Integer idPromotor) {
		this.idPromotor = idPromotor;
	}

	public Integer getIdRemitente() {
		return idRemitente;
	}

	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	/**
	 * @return the tipoFirmante
	 */
	public String getTipoFirmante() {
		return tipoFirmante;
	}

	/**
	 * @param tipoFirmante the tipoFirmante to set
	 */
	public void setTipoFirmante(String tipoFirmante) {
		this.tipoFirmante = tipoFirmante;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Instantiates a new firmante PK.
	 *
	 * @param idFirmante
	 *            the id firmante
	 * @param idPromotor
	 *            the id promotor
	 */
	public FirmantePK(String idFirmante, Integer idPromotor, Integer idRemitente) {
		super();
		this.idFirmante = idFirmante;
		this.idPromotor = idPromotor;
		this.idRemitente = idRemitente;
	}

	/**
	 * Instantiates a new firmante PK.
	 */
	public FirmantePK() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FirmantePK [idFirmante=" + idFirmante + ", idPromotor=" + idPromotor + ", idRemitente=" + idRemitente
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

		if (!(obj instanceof FirmantePK)) {
			return false;
		}

		if (!FirmantePK.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FirmantePK tmp = (FirmantePK) obj;

		try {

			if (tmp.idFirmante.equals(this.idFirmante)//
					&& tmp.idPromotor == this.idPromotor//
					&& tmp.idRemitente == this.idRemitente
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
		return Objects.hash(this.idFirmante, this.idPromotor, this.idRemitente);
	}

}
