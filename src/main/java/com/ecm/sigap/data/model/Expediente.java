/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "expedientesRegistrables")
public class Expediente implements Serializable {

	/** */
	private static final long serialVersionUID = 7118527368282205294L;
	/** */
	@Id
	@Column(name = "identificador")
	private Integer id;
	/** */
	@OneToOne
	@JoinColumn(name = "idFondo")
	@JsonIgnoreProperties(value = { "idPadre", "codigo", "tramite", "concentracion", "disposicion", "vigencia",
			"codigoDisposicion" })
	@Fetch(value = FetchMode.SELECT)
	private Fondo fondo;
	/** */
	@OneToOne
	@JoinColumn(name = "idSubfondo")
	@JsonIgnoreProperties(value = { "tramite", "codigo", "concentracion", "disposicion", "vigencia",
			"codigoDisposicion" })
	@Fetch(value = FetchMode.SELECT)
	private SubFondo subfondo;
	/** */
	@OneToOne
	@JoinColumn(name = "idSeccion")
	@JsonIgnoreProperties(value = { "tramite", "concentracion", "disposicion", "vigencia", "codigoDisposicion" })
	@Fetch(value = FetchMode.SELECT)
	private Seccion seccion;

	/** */
	@OneToOne
	@JoinColumn(name = "idSubseccion")
	@JsonIgnoreProperties(value = { "tramite", "concentracion", "disposicion", "vigencia", "codigoDisposicion" })
	@Fetch(value = FetchMode.SELECT)
	private SubSeccion subseccion;

	/** */
	@OneToOne
	@JoinColumn(name = "idSerie")
	@Fetch(value = FetchMode.SELECT)
	private Serie serie;

	/** */
	@OneToOne
	@JoinColumn(name = "idSubserie")
	@Fetch(value = FetchMode.SELECT)
	private SubSerie subserie;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the fondo
	 */
	public Fondo getFondo() {
		return fondo;
	}

	/**
	 * @param fondo the fondo to set
	 */
	public void setFondo(Fondo fondo) {
		this.fondo = fondo;
	}

	/**
	 * @return the subfondo
	 */
	public SubFondo getSubfondo() {
		return subfondo;
	}

	/**
	 * @param subfondo the subfondo to set
	 */
	public void setSubfondo(SubFondo subfondo) {
		this.subfondo = subfondo;
	}

	/**
	 * @return the seccion
	 */
	public Seccion getSeccion() {
		return seccion;
	}

	/**
	 * @param seccion the seccion to set
	 */
	public void setSeccion(Seccion seccion) {
		this.seccion = seccion;
	}

	/**
	 * @return the subseccion
	 */
	public SubSeccion getSubseccion() {
		return subseccion;
	}

	/**
	 * @param subseccion the subseccion to set
	 */
	public void setSubseccion(SubSeccion subseccion) {
		this.subseccion = subseccion;
	}

	/**
	 * @return the serie
	 */
	public Serie getSerie() {
		return serie;
	}

	/**
	 * @param serie the serie to set
	 */
	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	/**
	 * @return the subserie
	 */
	public SubSerie getSubserie() {
		return subserie;
	}

	/**
	 * @param subserie the subserie to set
	 */
	public void setSubserie(SubSerie subserie) {
		this.subserie = subserie;
	}

	/**
	 * 
	 * @return
	 */
	public String getBreadcrumb() {

		StringBuilder sb = new StringBuilder();

		sb.append("/").append(fondo.getDescripcion());

		if (subfondo != null)
			sb.append("/").append(subfondo.getDescripcion());

		sb.append("/").append(seccion.getDescripcion());

		if (subseccion != null)
			sb.append("/").append(subseccion.getDescripcion());

		sb.append("/").append(serie.getDescripcion());

		if (subserie != null)
			sb.append("/").append(subserie.getDescripcion());

		return sb.toString();
	}
}
