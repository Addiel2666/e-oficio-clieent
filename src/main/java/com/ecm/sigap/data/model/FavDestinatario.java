/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "favDestinatarios")
public class FavDestinatario implements java.io.Serializable {

	/** */
	private static final long serialVersionUID = -6621833936948198924L;

	/** Llave primaria de la clase */
	@EmbeddedId
	private FavDestinatarioKey destinatarioKey;

	/** Apellido materno del Destinatario */
	@Column(name = "materno")
	private String materno;

	/** Nombre del Destinatario */
	@Column(name = "nombre")
	private String nombre;

	/** Apellido paterno del Destinatario */
	@Column(name = "paterno")
	private String paterno;

	/**
	 * Obtiene la Llave primaria de la clase
	 * 
	 * @return Llave primaria de la clase
	 */
	public FavDestinatarioKey getDestinatarioKey() {

		return destinatarioKey;
	}

	/**
	 * Asigna la Llave primaria de la clase
	 * 
	 * @param destinatarioKey Llave primaria de la clase
	 */
	public void setDestinatarioKey(FavDestinatarioKey destinatarioKey) {

		this.destinatarioKey = destinatarioKey;
	}

	/**
	 * Obtiene el Apellido materno del Destinatario
	 * 
	 * @return Apellido materno del Destinatario
	 */
	public String getMaterno() {

		return null != materno ? materno : "";
	}

	/**
	 * Asigna el Apellido materno del Destinatario
	 * 
	 * @param materno Apellido materno del Destinatario
	 */
	public void setMaterno(String materno) {

		this.materno = materno;
	}

	/**
	 * Obtiene el Nombre del Destinatario
	 * 
	 * @return Nombre del Destinatario
	 */
	public String getNombre() {

		return nombre;
	}

	/**
	 * Asigna el Nombre del Destinatario
	 * 
	 * @param nombre Nombre del Destinatario
	 */
	public void setNombre(String nombre) {

		this.nombre = nombre;
	}

	/**
	 * Obtiene el Apellido paterno del Destinatario
	 * 
	 * @return Apellido paterno del Destinatario
	 */
	public String getPaterno() {

		return paterno;
	}

	/**
	 * Asigna el Apellido paterno del Destinatario
	 * 
	 * @param paterno Apellido paterno del Destinatario
	 */
	public void setPaterno(String paterno) {

		this.paterno = paterno;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FavDestinatario [destinatarioKey=" + destinatarioKey + ", materno=" + materno + ", nombre=" + nombre
				+ ", paterno=" + paterno + "]";
	}
}
