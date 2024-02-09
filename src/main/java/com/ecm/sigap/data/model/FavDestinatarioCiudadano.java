/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;

/**
 * The Class Area.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "favDestinatariosCiudadanos")
public class FavDestinatarioCiudadano implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 966505245514991500L;

	/** The id area. */
	@Column(name = "idArea", insertable = false, updatable = false, nullable = true)
	private Integer idArea;

	/** The id tipo destinatario. */
	@Column(name = "idTipoDestinatario", insertable = false, updatable = false, nullable = true)
	private Integer idTipoDestinatario;

	/** The id. */
	@Id
	@Column(name = "id", insertable = false, updatable = false, nullable = true)
	private Integer idCiudadano;

	/** The paterno. */
	@Column(name = "paterno", insertable = false, updatable = false, nullable = true)
	private String paterno;

	/** The materno. */
	@Column(name = "materno", insertable = false, updatable = false, nullable = true)
	private String materno;

	/** The nombre. */
	@Column(name = "nombres", insertable = false, updatable = false, nullable = true)
	private String nombre;

	/** The homonimo. */
	@Column(name = "homonimo", insertable = false, updatable = false, nullable = true)
	private String homonimo;

	/** The rfc. */
	@Column(name = "rfc", insertable = false, updatable = false, nullable = true)
	private String rfc;

	/** The curp. */
	@Column(name = "curp", insertable = false, updatable = false, nullable = true)
	private String curp;

	/** The email. */
	@Column(name = "email", insertable = false, updatable = false, nullable = true)
	private String email;

	/** The id tipo. */
	@Column(name = "idTipo", insertable = false, updatable = false, nullable = true)
	private String idTipo;

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
	 * Gets the id ciudadano.
	 *
	 * @return the id ciudadano
	 */
	public Integer getIdCiudadano() {
		return idCiudadano;
	}

	/**
	 * Sets the id ciudadano.
	 *
	 * @param idCiudadano
	 *            the new id ciudadano
	 */
	public void setIdCiudadano(Integer idCiudadano) {
		this.idCiudadano = idCiudadano;
	}

	/**
	 * Gets the paterno.
	 *
	 * @return the paterno
	 */
	public String getPaterno() {
		return paterno;
	}

	/**
	 * Sets the paterno.
	 *
	 * @param paterno
	 *            the new paterno
	 */
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	/**
	 * Gets the materno.
	 *
	 * @return the materno
	 */
	public String getMaterno() {
		return null != materno ? materno : "";
	}

	/**
	 * Sets the materno.
	 *
	 * @param materno
	 *            the new materno
	 */
	public void setMaterno(String materno) {

		this.materno = materno;
	}

	/**
	 * Gets the nombre.
	 *
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Sets the nombre.
	 *
	 * @param nombre
	 *            the new nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Gets the homonimo.
	 *
	 * @return the homonimo
	 */
	public String getHomonimo() {
		return homonimo;
	}

	/**
	 * Sets the homonimo.
	 *
	 * @param homonimo
	 *            the new homonimo
	 */
	public void setHomonimo(String homonimo) {
		this.homonimo = homonimo;
	}

	/**
	 * Gets the rfc.
	 *
	 * @return the rfc
	 */
	public String getRfc() {
		return rfc;
	}

	/**
	 * Sets the rfc.
	 *
	 * @param rfc
	 *            the new rfc
	 */
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	/**
	 * Gets the curp.
	 *
	 * @return the curp
	 */
	public String getCurp() {
		return curp;
	}

	/**
	 * Sets the curp.
	 *
	 * @param curp
	 *            the new curp
	 */
	public void setCurp(String curp) {
		this.curp = curp;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email
	 *            the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the id tipo.
	 *
	 * @return the id tipo
	 */
	public String getIdTipo() {
		return idTipo;
	}

	/**
	 * Sets the id tipo.
	 *
	 * @param idTipo
	 *            the new id tipo
	 */
	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
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
		return "FavDestinatarioCiudadano [idArea=" + idArea + ", idTipoDestinatario=" + idTipoDestinatario
				+ ", idCiudadano=" + idCiudadano + ", paterno=" + paterno + ", materno=" + materno + ", nombre="
				+ nombre + ", homonimo=" + homonimo + ", rfc=" + rfc + ", curp=" + curp + ", email=" + email
				+ ", idTipo=" + idTipo + "]";
	}

}
