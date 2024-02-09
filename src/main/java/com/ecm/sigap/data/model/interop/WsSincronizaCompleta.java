/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.interop;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The Class WsSincronizaCompleta.
 *
 * @author Gustavo Vielmas
 * @version 1.0
 */
@Entity
@Table(name = "wsSincronizaCompleta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class WsSincronizaCompleta implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6186873914068011320L;

	/** The id registro. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRegistro")
	private Integer idRegistro;

	/** The id institucion. */
	@Column(name = "idInstitucion")
	private String idInstitucion;

	/** The no institucion. */
	@Column(name = "noInstitucion")
	private String noInstitucion;

	/** The id area. */
	@Column(name = "idArea")
	private String idArea;

	/** The no area. */
	@Column(name = "noArea")
	private String noArea;

	/** The id usuario. */
	@Column(name = "idUsuario")
	private String idUsuario;

	/** The no usuario. */
	@Column(name = "noUsuario")
	private String noUsuario;

	/** The uri. */
	@Column(name = "uri")
	private String uri;

	/**
	 * Gets the id registro.
	 *
	 * @return the id registro
	 */
	public Integer getIdRegistro() {
		return idRegistro;
	}

	/**
	 * Sets the id registro.
	 *
	 * @param idRegistro
	 *            the new id registro
	 */
	public void setIdRegistro(Integer idRegistro) {
		this.idRegistro = idRegistro;
	}

	/**
	 * Gets the id institucion.
	 *
	 * @return the id institucion
	 */
	public String getIdInstitucion() {
		return idInstitucion;
	}

	/**
	 * Sets the id institucion.
	 *
	 * @param idInstitucion
	 *            the new id institucion
	 */
	public void setIdInstitucion(String idInstitucion) {
		this.idInstitucion = idInstitucion;
	}

	/**
	 * Gets the no institucion.
	 *
	 * @return the no institucion
	 */
	public String getNoInstitucion() {
		return noInstitucion;
	}

	/**
	 * Sets the no institucion.
	 *
	 * @param noInstitucion
	 *            the new no institucion
	 */
	public void setNoInstitucion(String noInstitucion) {
		this.noInstitucion = noInstitucion;
	}

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public String getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea
	 *            the new id area
	 */
	public void setIdArea(String idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the no area.
	 *
	 * @return the no area
	 */
	public String getNoArea() {
		return noArea;
	}

	/**
	 * Sets the no area.
	 *
	 * @param noArea
	 *            the new no area
	 */
	public void setNoArea(String noArea) {
		this.noArea = noArea;
	}

	/**
	 * Gets the id usuario.
	 *
	 * @return the id usuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * Sets the id usuario.
	 *
	 * @param idUsuario
	 *            the new id usuario
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * Gets the no usuario.
	 *
	 * @return the no usuario
	 */
	public String getNoUsuario() {
		return noUsuario;
	}

	/**
	 * Sets the no usuario.
	 *
	 * @param noUsuario
	 *            the new no usuario
	 */
	public void setNoUsuario(String noUsuario) {
		this.noUsuario = noUsuario;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri
	 *            the new uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
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
		return "WsSincronizaCompleta [idRegistro=" + idRegistro + ", idInstitucion=" + idInstitucion
				+ ", noInstitucion=" + noInstitucion + ", idArea=" + idArea + ", noArea=" + noArea + ", idUsuario="
				+ idUsuario + ", noUsuario=" + noUsuario + ", uri=" + uri + "]";
	}

}
