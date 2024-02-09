/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Column;

/**
 * The Class Area.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "favDestinatariosRepLegal")
public class FavDestinatarioRepLegal implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7667792346540439934L;

	/** The id rep legal. */
	@Id
	@Column(name = "idRepLegal", insertable = false, updatable = false)
	private Integer idRepLegal;

	/** The id tipo destinatario. */
	@Column(name = "idTipoDestinatario", insertable = false, updatable = false)
	private Integer idTipoDestinatario;

	/** The id area. */
	@Column(name = "idArea", insertable = false, updatable = false)
	@Formula("COALESCE(idArea, 0)")
	private Integer idArea;

	/** The id empresa. */
	@Column(name = "idEmpresa", insertable = false, updatable = false)
	private Integer idEmpresa;

	/** The empresa. */
	@Column(name = "empresa", insertable = false, updatable = false)
	private String empresa;

	/** The paterno. */
	@Column(name = "paterno", insertable = false, updatable = false)
	private String paterno;

	/** The materno. */
	@Column(name = "materno", insertable = false, updatable = false)
	private String materno;

	/** The nombre. */
	@Column(name = "nombres", insertable = false, updatable = false)
	private String nombre;

	/**
	 * Gets the id rep legal.
	 *
	 * @return the id rep legal
	 */
	public Integer getIdRepLegal() {
		return idRepLegal;
	}

	/**
	 * Sets the id rep legal.
	 *
	 * @param idRepLegal
	 *            the new id rep legal
	 */
	public void setIdRepLegal(Integer idRepLegal) {
		this.idRepLegal = idRepLegal;
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
	 * Gets the id empresa.
	 *
	 * @return the id empresa
	 */
	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	/**
	 * Sets the id empresa.
	 *
	 * @param idEmpresa
	 *            the new id empresa
	 */
	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	/**
	 * Gets the empresa.
	 *
	 * @return the empresa
	 */
	public String getEmpresa() {
		return empresa;
	}

	/**
	 * Sets the empresa.
	 *
	 * @param empresa
	 *            the new empresa
	 */
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
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
		return "FavDestinatarioRepLegal [idRepLegal=" + idRepLegal + ", idTipoDestinatario=" + idTipoDestinatario
				+ ", idArea=" + idArea + ", idEmpresa=" + idEmpresa + ", empresa=" + empresa + ", paterno=" + paterno
				+ ", materno=" + materno + ", nombre=" + nombre + "]";
	}

}
