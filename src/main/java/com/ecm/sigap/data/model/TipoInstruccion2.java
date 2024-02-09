/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * Clase de entidad que representa los Tipos de Instrucciones
 * 
 * @author ECM Solutions
 * @version 1.0
 *
 */
@Entity
@Table(name = "TIPOSINSTRUCCION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class TipoInstruccion2 implements Serializable {

	/** */
	private static final long serialVersionUID = -5111634691078096147L;

	/** Identificador del Tipo de Instruccion */
	@Id
	@Column(name = "IDINSTRUCCION",  insertable = false)
	private Integer idInstruccion;

	/** Identificador del Area */
	@Column(name = "IDAREA")
	private Integer idArea;

	/** Descripcion del Tipo de Instruccion */
	@Column(name = "DESCRIPCION")
	private String descripcion;

	/** Indicador si el Tipo de Instruccion requiere o no respuesta */
	@Column(name = "REQRESPSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuesta;

	/** Indicador si el Tipo de Instruccion esta activo o no */
	@Column(name = "ACTIVOSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * @return the idInstruccion
	 */
	public Integer getIdInstruccion() {
		return idInstruccion;
	}

	/**
	 * @param idInstruccion the idInstruccion to set
	 */
	public void setIdInstruccion(Integer idInstruccion) {
		this.idInstruccion = idInstruccion;
	}

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the requiereRespuesta
	 */
	public Boolean getRequiereRespuesta() {
		return requiereRespuesta;
	}

	/**
	 * @param requiereRespuesta the requiereRespuesta to set
	 */
	public void setRequiereRespuesta(Boolean requiereRespuesta) {
		this.requiereRespuesta = requiereRespuesta;
	}

	/**
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * @param activo the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoInstruccion2 [idInstruccion=" + idInstruccion + ", idArea=" + idArea + ", descripcion="
				+ descripcion + ", requiereRespuesta=" + requiereRespuesta + ", activo=" + activo + "]";
	}

}
