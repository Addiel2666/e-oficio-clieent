/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

// TODO: Auto-generated Javadoc
/**
 * The Class FavDestinatarioFuncionarioKey.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class FavNoDestinatarioFuncionarioKey implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -269484932383870976L;

	/** The id representante. */
	@Column(name = "idRepresentante", insertable = false, updatable = false)
	private String idRepresentante;

	/** The id area destinatario. */
	@Column(name = "idAreaDestinatario", insertable = false, updatable = false)
	private Integer idAreaDestinatario;

	/**
	 * Gets the id representante.
	 *
	 * @return the id representante
	 */
	public String getIdRepresentante() {
		return idRepresentante;
	}

	/**
	 * Sets the id representante.
	 *
	 * @param idRepresentante
	 *            the new id representante
	 */
	public void setIdRepresentante(String idRepresentante) {
		this.idRepresentante = idRepresentante;
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
		return "FavNoDestinatarioFuncionarioKey [idRepresentante=" + idRepresentante + ", idAreaDestinatario="
				+ idAreaDestinatario + "]";
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

		if (!(obj instanceof FavNoDestinatarioFuncionarioKey)) {
			return false;
		}

		if (!FavNoDestinatarioFuncionarioKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavNoDestinatarioFuncionarioKey tmp = (FavNoDestinatarioFuncionarioKey) obj;

		try {

			if (tmp.idAreaDestinatario == this.idAreaDestinatario//
					&& tmp.idRepresentante.equals(this.idRepresentante)//
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
		return Objects.hash(this.idAreaDestinatario, this.idRepresentante);
	}

}
