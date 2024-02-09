/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The Class Domicilio.
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */

@Embeddable
public class Domicilio implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5200886583631596963L;

	/** The idTipoOrigen. */
	@Column(name = "idTipoOrigen")
	private String idTipoOrigen;

	/** The calleNumero. */
	@Column(name = "calleNumero")
	private String calleNumero;

	/** The colonia. */
	@Column(name = "colonia")
	private String colonia;

	/** The delegacion. */
	@Column(name = "delegacion")
	private String delegacion;

	/** The cp. */
	@Column(name = "cp")
	private String cp;

	/** The telefono. */
	@Column(name = "telefono")
	private String telefono;

	/** Entidades */
	@OneToOne
	@JoinColumn(name = "identidad")
	@Fetch(FetchMode.SELECT)
	private Entidad identidad;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */

	/**
	 * @return the idTipoOrigen
	 */
	public String getIdTipoOrigen() {
		return idTipoOrigen;
	}

	/**
	 * Sets the id idTipoOrigen.
	 *
	 * @param idTipoOrigen the idTipoOrigen to set
	 */
	public void setIdTipoOrigen(String idTipoOrigen) {
		this.idTipoOrigen = idTipoOrigen;
	}

	/**
	 * @return the calleNumero
	 */
	public String getCalleNumero() {
		return calleNumero;
	}

	/**
	 * Sets the id calleNumero.
	 *
	 * @param calleNumero the calleNumero to set
	 */
	public void setCalleNumero(String calleNumero) {
		this.calleNumero = calleNumero;
	}

	/**
	 * @return the colonia
	 */
	public String getColonia() {
		return colonia;
	}

	/**
	 * Sets the id calleNumero.
	 *
	 * @param calleNumero the calleNumero to set
	 */
	public void setColonia(String colonia) {
		this.colonia = colonia;
	}

	/**
	 * @return the delegacion
	 */
	public String getDelegacion() {
		return delegacion;
	}

	/**
	 * Sets the delegacion.
	 *
	 * @param delegacion the delegacion to set
	 */
	public void setDelegacion(String delegacion) {
		this.delegacion = delegacion;
	}

	/**
	 * @return the cp
	 */
	public String getCp() {
		return cp;
	}

	/**
	 * Sets the cp.
	 *
	 * @param cp the cp to set
	 */
	public void setCp(String cp) {
		this.cp = cp;
	}

	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Sets the telefono.
	 *
	 * @param telefono the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Entidad getIdentidad() {
		return identidad;
	}

	public void setIdentidad(Entidad identidad) {
		this.identidad = identidad;
	}

	@Override
	public String toString() {
		return "Domicilio [idTipoOrigen=" + idTipoOrigen + ", calleNumero=" + calleNumero + ", colonia=" + colonia
				+ ", delegacion=" + delegacion + ", cp=" + cp + ", telefono=" + telefono + ", identidad=" + identidad
				+ "]";
	}
}
