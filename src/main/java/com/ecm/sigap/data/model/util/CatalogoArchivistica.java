/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@MappedSuperclass
public abstract class CatalogoArchivistica {

	/** */
	@Id
	@SequenceGenerator(name = "SEQ_CATALOGOSARCHIVISTICA", sequenceName = "CATALOGOSARCHIVISTICA_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CATALOGOSARCHIVISTICA")
	@Column(name = "identificador")
	private Integer id;
	/** */
	@Column(name = "descripcion")
	private String descripcion;
	/** */
	@Column(name = "activoSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;
	/** */
	@Column(name = "idPadre")
	private Integer idPadre;
	/** */
	@Column(name = "tipoCatalogo")
	private String tipo;
	/** */
	@Column(name = "codigo")
	private String codigo;
	/** */
	@Column(name = "atramite")
	private Integer tramite;
	/** */
	@Column(name = "aconcentracion")
	private Integer concentracion;
	/** */
	@Column(name = "disposicion")
	private String disposicion;
	/** */
	@Column(name = "vigencia")
	private Integer vigencia;
	/** */
	@Column(name = "codigoDisposicion")
	private String codigoDisposicion;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * @param activo
	 *            the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * @return the idPadre
	 */
	public Integer getIdPadre() {
		return idPadre;
	}

	/**
	 * @param idPadre
	 *            the idPadre to set
	 */
	public void setIdPadre(Integer idPadre) {
		this.idPadre = idPadre;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo
	 *            the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return the tramite
	 */
	public Integer getTramite() {
		return tramite;
	}

	/**
	 * @param tramite
	 *            the tramite to set
	 */
	public void setTramite(Integer tramite) {
		this.tramite = tramite;
	}

	/**
	 * @return the concentracion
	 */
	public Integer getConcentracion() {
		return concentracion;
	}

	/**
	 * @param concentracion
	 *            the concentracion to set
	 */
	public void setConcentracion(Integer concentracion) {
		this.concentracion = concentracion;
	}

	/**
	 * @return the disposicion
	 */
	public String getDisposicion() {
		return disposicion;
	}

	/**
	 * @param disposicion
	 *            the disposicion to set
	 */
	public void setDisposicion(String disposicion) {
		this.disposicion = disposicion;
	}

	/**
	 * @return the vigencia
	 */
	public Integer getVigencia() {
		return vigencia;
	}

	/**
	 * @param vigencia
	 *            the vigencia to set
	 */
	public void setVigencia(Integer vigencia) {
		this.vigencia = vigencia;
	}

	/**
	 * @return the codigoDisposicion
	 */
	public String getCodigoDisposicion() {
		return codigoDisposicion;
	}

	/**
	 * @param codigoDisposicion
	 *            the codigoDisposicion to set
	 */
	public void setCodigoDisposicion(String codigoDisposicion) {
		this.codigoDisposicion = codigoDisposicion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[id=" + id + ", descripcion=" + descripcion + ", activo=" + activo + ", idPadre=" + idPadre + ", tipo="
				+ tipo + ", codigo=" + codigo + ", tramite=" + tramite + ", concentracion=" + concentracion
				+ ", disposicion=" + disposicion + ", vigencia=" + vigencia + ", codigoDisposicion=" + codigoDisposicion
				+ "]";
	}

}
