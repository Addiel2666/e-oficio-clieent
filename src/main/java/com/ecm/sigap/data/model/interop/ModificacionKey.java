/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.interop;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import com.ecm.sigap.data.util.TipoModificacionToStringConverter;

/**
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Embeddable
public class ModificacionKey implements Serializable {

	/** */
	private static final long serialVersionUID = 1398505551360570831L;

	/** */
	@Column(name = "idUsuario")
	private String idUsuario;

	/** */
	@Column(name = "idArea")
	private String idArea;

	/** */
	@Column(name = "keyUsuario")
	private String keyUsario;

	/** */
	@Column(name = "tipoModificacion")
	@Convert(converter = TipoModificacionToStringConverter.class)
	private TipoModificacion tipo;

	/**
	 * @return the idUsuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @param idUsuario
	 *            the idUsuario to set
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * @return the idArea
	 */
	public String getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea
	 *            the idArea to set
	 */
	public void setIdArea(String idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the keyUsario
	 */
	public String getKeyUsario() {
		return keyUsario;
	}

	/**
	 * @param keyUsario
	 *            the keyUsario to set
	 */
	public void setKeyUsario(String keyUsario) {
		this.keyUsario = keyUsario;
	}

	/**
	 * @return the tipo
	 */
	public TipoModificacion getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(TipoModificacion tipo) {
		this.tipo = tipo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModificacionKey [idUsuario=" + idUsuario + ", idArea=" + idArea + ", keyUsario=" + keyUsario + ", tipo="
				+ tipo + "]";
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

		if (!(obj instanceof ModificacionKey)) {
			return false;
		}

		if (!ModificacionKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		ModificacionKey tmp = (ModificacionKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idUsuario.equals(this.idUsuario) //
					&& tmp.keyUsario.equals(this.keyUsario) //
					&& tmp.tipo == this.tipo//
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
		return Objects.hash(this.idArea, this.idUsuario, this.keyUsario, this.tipo);
	}

}
