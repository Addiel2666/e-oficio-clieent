/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class Area.
 *
 * @author
 * @version 1.0
 */
@Entity
@Table(name = "areasAuxiliar")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public final class AreaAuxiliar implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9148234048624086294L;

	/** The id area. */
	@Id
	@Column(name = "idArea")
	private Integer idArea;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The institucion. */
	@OneToOne
	@JoinColumn(name = "idInstitucion")
	@Fetch(FetchMode.SELECT)
	private Institucion institucion;

	/** The id area padre. */
	@Column(name = "idAreaPadre")
	private Integer idAreaPadre;

	/** The titular. */
	@Column(name = "titularUsuario")
	private String titular;

	/** The titular cargo. */
	@Column(name = "titularCargo")
	private String titularCargo;

	/** The clave. */
	@Column(name = "claveCDD")
	private String clave;

	/** The siglas. */
	@Column(name = "siglas")
	private String siglas;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** The interopera. */
	@Column(name = "interoperasn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean interopera;

	/** The ruta. */
	@Formula("{SIGAP_SCHEMA}.obtiene_path_area_desc_v3(idArea)")
	private String ruta;

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
	 * @param idArea the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion the new descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion the new institucion
	 */
	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	/**
	 * Gets the id area padre.
	 *
	 * @return the id area padre
	 */
	public Integer getIdAreaPadre() {
		return idAreaPadre;
	}

	/**
	 * Sets the id area padre.
	 *
	 * @param idAreaPadre the new id area padre
	 */
	public void setIdAreaPadre(Integer idAreaPadre) {
		this.idAreaPadre = idAreaPadre;
	}

	/**
	 * Gets the titular.
	 *
	 * @return the titular
	 */
	public String getTitular() {
		return titular;
	}

	/**
	 * Sets the titular.
	 *
	 * @param titular the new titular
	 */
	public void setTitular(String titular) {
		this.titular = titular;
	}

	/**
	 * Gets the titular cargo.
	 *
	 * @return the titular cargo
	 */
	public String getTitularCargo() {
		return titularCargo;
	}

	/**
	 * Sets the titular cargo.
	 *
	 * @param titularCargo the new titular cargo
	 */
	public void setTitularCargo(String titularCargo) {
		this.titularCargo = titularCargo;
	}

	/**
	 * Gets the clave.
	 *
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * Sets the clave.
	 *
	 * @param clave the new clave
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}

	/**
	 * Gets the siglas.
	 *
	 * @return the siglas
	 */
	public String getSiglas() {
		return siglas;
	}

	/**
	 * Sets the siglas.
	 *
	 * @param siglas the new siglas
	 */
	public void setSiglas(String siglas) {
		this.siglas = siglas;
	}

	/**
	 * Gets the activo.
	 *
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Sets the activo.
	 *
	 * @param activo the new activo
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Gets the interopera.
	 *
	 * @return the interopera
	 */
	public Boolean getInteropera() {
		return interopera;
	}

	/**
	 * Sets the interopera.
	 *
	 * @param interopera the new interopera
	 */
	public void setInteropera(Boolean interopera) {
		this.interopera = interopera;
	}

	/**
	 * Gets the ruta.
	 *
	 * @return the ruta
	 */
	public String getRuta() {
		return ruta;
	}

	/**
	 * Sets the ruta.
	 *
	 * @param ruta the new ruta
	 */
	public void setRuta(String ruta) {
		this.ruta = ruta;
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
		return "AreaAuxiliar [idArea=" + idArea + ", descripcion=" + descripcion + ", institucion=" + institucion
				+ ", idAreaPadre=" + idAreaPadre + ", titular=" + titular + ", titularCargo=" + titularCargo
				+ ", clave=" + clave + ", siglas=" + siglas + ", activo=" + activo + ", interopera=" + interopera
				+ ", ruta=" + ruta + "]";
	}

}
