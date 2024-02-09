/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * Clase de entidad que se usa como clave primaria de la vista DESTINATARIOS
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@Embeddable
public final class DestinatarioKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4208122104742951379L;

	/** Identificador */
	private String identificador;

	/** Identificador del area */
	private Integer idArea;

	/**
	 * Constructor por defecto de la clase
	 */
	public DestinatarioKey() {
	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param identificador
	 *            Identificador
	 * @param idArea
	 *            Identificador del area
	 */
	public DestinatarioKey(String identificador, Integer idArea) {
		this.identificador = identificador;
		this.idArea = idArea;
	}

	/**
	 * Obtiene el Identificador
	 * 
	 * @return Identificador
	 */
	public final String getIdentificador() {

		return identificador;
	}

	/**
	 * Asigna el Identificador
	 * 
	 * @param identificador
	 *            Identificador
	 */
	public final void setIdentificador(String identificador) {

		this.identificador = identificador;
	}

	/**
	 * Obtiene el Identificador del area
	 * 
	 * @return Identificador del area
	 */
	public final Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del area
	 * 
	 * @param idArea
	 *            Identificador del area
	 */
	public final void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DestinatarioKey [identificador=" + identificador + ", idArea=" + idArea + "]";
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
		if (!DestinatarioKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		DestinatarioKey tmp = (DestinatarioKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.identificador.equals(this.identificador)//
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
		return Objects.hash(this.idArea, this.identificador);
	}

}
