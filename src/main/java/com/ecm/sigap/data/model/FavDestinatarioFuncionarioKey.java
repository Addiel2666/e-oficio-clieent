/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Class FavDestinatarioFuncionarioKey.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class FavDestinatarioFuncionarioKey implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4126331167312839522L;

	/** The id area. */
	@Column(name = "idArea", nullable = true, insertable = false, updatable = false)
	private Integer idArea;

	/** The id destinatario. */
	@Column(name = "idDestinatario", insertable = false, updatable = false)
	private String idDestinatario;

	/** The id tipo destinatario. */
	@Column(name = "idTipoDestinatario", nullable = true, insertable = false, updatable = false)
	private Integer idTipoDestinatario;

	/** The id area destinatario. */

	@Column(name = "idAreaDestinatario", insertable = false, updatable = false)
	private Integer idAreaDestinatario;

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
	 * Gets the id destinatario.
	 *
	 * @return the id destinatario
	 */
	public String getIdDestinatario() {
		return idDestinatario;
	}

	/**
	 * Sets the id destinatario.
	 *
	 * @param idDestinatario
	 *            the new id destinatario
	 */
	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * Gets the id tipo destinatario.
	 *
	 * @return the id tipo destinatario
	 */
	public Integer getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	/**
	 * Sets the id tipo destinatario.
	 *
	 * @param idTipoDestinatario
	 *            the new id tipo destinatario
	 */
	public void setIdTipoDestinatario(Integer idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	/**
	 * Gets the id area destinatario.
	 *
	 * @return the id area destinatario
	 */
	public Integer getIdAreaDestinatario() {
		return idAreaDestinatario;
	}

	/**
	 * Sets the id area destinatario.
	 *
	 * @param idAreaDestinatario
	 *            the new id area destinatario
	 */
	public void setIdAreaDestinatario(Integer idAreaDestinatario) {
		this.idAreaDestinatario = idAreaDestinatario;
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
		return "FavDestinatarioFuncionarioKey [idArea=" + idArea + ", idDestinatario=" + idDestinatario
				+ ", idTipoDestinatario=" + idTipoDestinatario + ", idAreaDestinatario=" + idAreaDestinatario + "]";
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

		if (!(obj instanceof FavDestinatarioFuncionarioKey)) {
			return false;
		}

		if (!FavDestinatarioFuncionarioKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavDestinatarioFuncionarioKey tmp = (FavDestinatarioFuncionarioKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idDestinatario.equals(this.idDestinatario) //
					&& tmp.idAreaDestinatario == this.idAreaDestinatario//
					&& tmp.idTipoDestinatario == this.idTipoDestinatario//
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
		return Objects.hash(this.idArea, this.idAreaDestinatario, this.idDestinatario, this.idTipoDestinatario);
	}
}
