/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.util.SubTipoAsuntoToStringConverter;

/**
 * Clase de Identidad que representa la vista DESTINATARIOS
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@IdClass(DestinatarioKey.class)
@Entity
@Immutable
@Table(name = "destinatarios")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public final class Destinatario implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 382313373084871627L;

	/** Identificador */
	@Id
	@Column(name = "identificador")
	private String identificador;

	/** Identificador del area */
	@Id
	@Column(name = "idarea")
	private Integer idArea;

	/** Descripcion de la institucion */
	@Column(name = "institucion")
	private String institucion;

	/** Descripcion del area */
	@Column(name = "area")
	private String area;

	/** Nombres */
	@Column(name = "nombre")
	private String nombres;

	/** Apellido paterno */
	@Column(name = "paterno")
	private String paterno;

	/** Apellido materno */
	@Column(name = "materno")
	private String materno;

	/** Identificador del Sub Tipo Asunto */
	@Column(name = "idSubTipoAsunto")
	@Convert(converter = SubTipoAsuntoToStringConverter.class)
	@Enumerated(EnumType.STRING)
	private SubTipoAsunto idSubTipoAsunto;
	
	/** The nombre completo. */
	@Formula(" concat( NOMBRE , concat( ' ' , concat(PATERNO , concat(' ' ,  MATERNO)))) ")
	private String nombreCompleto;

	/**
	 * Constructor por defecto de la clase
	 */
	public Destinatario() {
	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param identificador
	 *            Identificador
	 * @param idArea
	 *            Identificador del area
	 */
	public Destinatario(String identificador, Integer idArea) {
		this.identificador = identificador;
		this.idArea = idArea;
	}

	/**
	 * Obtiene el Identificador
	 * 
	 * @return Identificador
	 */
	public final String getIdentificador() {

		return identificador;
	}

	/**
	 * Asigna el Identificador
	 * 
	 * @param identificador
	 *            Identificador
	 */
	public void setIdentificador(String identificador) {

		this.identificador = identificador;
	}

	/**
	 * Obtiene el Identificador del area
	 * 
	 * @return Identificador del area
	 */
	public final Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del area
	 * 
	 * @param idArea
	 *            Identificador del area
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene la Descripcion de la institucion
	 * 
	 * @return Descripcion de la institucion
	 */
	public final String getInstitucion() {

		return institucion;
	}

	/**
	 * Asigna la Descripcion de la institucion
	 * 
	 * @param institucion
	 *            Descripcion de la institucion
	 */
	public void setInstitucion(String institucion) {

		this.institucion = institucion;
	}

	/**
	 * Obtiene la Descripcion del area
	 * 
	 * @return Descripcion del area
	 */
	public final String getArea() {

		return area;
	}

	/**
	 * Asigna la Descripcion del area
	 * 
	 * @param area
	 *            Descripcion del area
	 */
	public void setArea(String area) {

		this.area = area;
	}

	/**
	 * Obtiene los Nombres
	 * 
	 * @return Nombres
	 */
	public final String getNombres() {

		return nombres;
	}

	/**
	 * Asigna los Nombres
	 * 
	 * @param nombres
	 *            Nombres
	 */
	public void setNombres(String nombres) {

		this.nombres = nombres;
	}

	/**
	 * Obtiene el apellido paterno
	 * 
	 * @return Apellido paterno
	 */
	public final String getPaterno() {

		return paterno;
	}

	/**
	 * Asigna el apellido paterno
	 * 
	 * @param paterno
	 *            Apellido paterno
	 * 
	 */
	public void setPaterno(String paterno) {

		this.paterno = paterno;
	}

	/**
	 * Obtiene el Apellido materno
	 * 
	 * @return Apellido materno
	 */
	public final String getMaterno() {

		return null != materno ? materno : "";
	}

	/**
	 * Asigna el Apellido materno
	 * 
	 * @param materno
	 *            Apellido materno
	 */
	public void setMaterno(String materno) {

		this.materno = materno;
	}

	/**
	 * Obtiene el Identificador del Sub Tipo Asunto
	 * 
	 * @return Identificador del Sub Tipo Asunto
	 */
	public final SubTipoAsunto getIdSubTipoAsunto() {

		return idSubTipoAsunto;
	}

	/**
	 * Asigna el Identificador del Sub Tipo Asunto
	 * 
	 * @param idSubTipoAsunto
	 *            Identificador del Sub Tipo Asunto
	 */
	public void setIdSubTipoAsunto(SubTipoAsunto idSubTipoAsunto) {

		this.idSubTipoAsunto = idSubTipoAsunto;
	}
	

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Destinatario [identificador=" + identificador + ", idArea=" + idArea + ", institucion=" + institucion
				+ ", area=" + area + ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno
				+ ", idSubTipoAsunto=" + idSubTipoAsunto + ", nombreCompleto=" + nombreCompleto + "]";
	}
	

}
