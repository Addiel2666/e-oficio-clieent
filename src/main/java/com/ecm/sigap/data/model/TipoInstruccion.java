/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * Clase de entidad que representa los Tipos de Instrucciones
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 15-Oct-2015
 * 
 *          Creacion de la clase
 *
 */
@Entity
@Table(name = "TIPOSINSTRUCCION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_OBJETOS", sequenceName = "SECOBJETOS", allocationSize = 1)
@UniqueKey(columnNames = { "area.idArea", "descripcion" }, message = "{Unique.descripcion}")
public class TipoInstruccion implements Serializable {

	private static final long serialVersionUID = 6723033412150821973L;

	/** Identificador del Tipo de Instruccion */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OBJETOS")
	@Column(name = "IDINSTRUCCION", nullable = false, precision = 38, scale = 0)
	private Integer idInstruccion;

	/** Area con la cual tiene relacion el Tipo de Instruccion {@link Area} */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private AreaAuxiliar area;

	/** Descripcion del Tipo de Instruccion */
	@Column(name = "DESCRIPCION", length = 150)
	private String descripcion;

	/** Indicador si el Tipo de Instruccion requiere o no respuesta */
	@Column(name = "REQRESPSN", length = 1)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuesta;

	/** Indicador si el Tipo de Instruccion esta activo o no */
	@Column(name = "ACTIVOSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * Constructor por defecto de la clase
	 */
	public TipoInstruccion() {

	}

	/**
	 * Constructor minimo de la clase
	 * 
	 * @param idInstruccion Identificador del Tipo de Instruccion
	 * @param area          Area a la que pertenece el Tipo de Instruccion
	 *                      {@link Area}
	 */
	public TipoInstruccion(Integer idInstruccion, AreaAuxiliar area) {
		this.idInstruccion = idInstruccion;
		this.area = area;
	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param idInstruccion     Identificador del Tipo de Instruccion
	 * @param area              Area a la que pertenece el Tipo de Instruccion
	 *                          {@link Area}
	 * @param descripcion       Descripcion del Tipo de Instruccion
	 * @param requiereRespuesta Indicador si el Tipo de Instruccion requiere o no
	 *                          respuesta
	 * @param activo
	 */
	public TipoInstruccion(Integer idInstruccion, AreaAuxiliar area, String descripcion, Boolean requiereRespuesta,
			Boolean activo) {
		this.idInstruccion = idInstruccion;
		this.area = area;
		this.descripcion = descripcion;
		this.requiereRespuesta = requiereRespuesta;
		this.activo = activo;
	}

	/**
	 * Obtiene el Identificador de la Instruccion
	 * 
	 * @return Identificador de la Instruccion
	 */
	public Integer getIdInstruccion() {

		return this.idInstruccion;
	}

	/**
	 * Asigna el Identificador de la Instruccion
	 * 
	 * @param idInstruccion Identificador de la Instruccion
	 */
	public void setIdInstruccion(Integer idInstruccion) {

		this.idInstruccion = idInstruccion;
	}

	/**
	 * Obtiene el Area asociada a el Tipo de Instruccion
	 * 
	 * @return Area asociada a el Tipo de Instruccion
	 */
	public AreaAuxiliar getArea() {

		return this.area;
	}

	/**
	 * Asigna el Area asociada a el Tipo de Instruccion
	 * 
	 * @param area Area asociada a el Tipo de Instruccion
	 */
	public void setArea(AreaAuxiliar area) {

		this.area = area;
	}

	/**
	 * Obtiene la Descripcion del Tipo de Instruccion
	 * 
	 * @return Descripcion del Tipo de Instruccion
	 */
	public String getDescripcion() {

		return this.descripcion;
	}

	/**
	 * Asigna la Descripcion del Tipo de Instruccion
	 * 
	 * @param descripcion Descripcion del Tipo de Instruccion
	 */
	public void setDescripcion(String descripcion) {

		this.descripcion = descripcion;
	}

	/**
	 * Obtiene el Indicador si el Tipo de Instruccion requiere o no respuesta
	 * 
	 * @return Indicador si el Tipo de Instruccion requiere o no respuesta
	 */
	public Boolean getRequiereRespuesta() {

		return this.requiereRespuesta;
	}

	/**
	 * Asigna el Indicador si el Tipo de Instruccion requiere o no respuesta
	 * 
	 * @param requiereRespuesta Indicador si el Tipo de Instruccion requiere o no
	 *                          respuesta
	 */
	public void setRequiereRespuesta(Boolean requiereRespuesta) {

		this.requiereRespuesta = requiereRespuesta;
	}

	/**
	 * Obtiene el Indicador si el Tipo de Instruccion esta activo o no
	 * 
	 * @return Indicador si el Tipo de Instruccion esta activo o no
	 */
	public Boolean getActivo() {

		return this.activo;
	}

	/**
	 * Asigna el Indicador si el Tipo de Instruccion esta activo o no
	 * 
	 * @param activo Indicador si el Tipo de Instruccion esta activo o no
	 */
	public void setActivo(Boolean activo) {

		this.activo = activo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoInstruccion [idInstruccion=" + idInstruccion + ", area=" + area + ", descripcion=" + descripcion
				+ ", requiereRespuesta=" + requiereRespuesta + ", activo=" + activo + "]";
	}

}
