/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Clase de entidad que representa la tabla ASUNTOSCORRESPONDENCIA
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 *          Creacion de la clase
 *
 */
public class AsuntoCorrespondencia implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2461559722219985594L;

	/** Identificador del Asunto */
	private Integer idAsunto;

	/** Tipo de Documento {@link TipoDocumento} */
	private TipoDocumento tipoDocumento;

	/** Identificador del Area */
	private Integer idArea;

	/** Identificador del Expediente */
	private TipoExpediente expediente;

	/** */
	private Tema tema;

	/** Identificador del Evento */
	private TipoEvento evento;

	/** */
	private Date fechaEvento;

	/** */
	private SubTema subTema;

	/**
	 * Constructor por defecto
	 */
	public AsuntoCorrespondencia() {
		super();
	}

	/**
	 * Full Constructor de la clase
	 * 
	 * @param idAsunto
	 *            Identificador del Asunto
	 * @param tipoDocumento
	 *            Tipo de Documento {@link TipoDocumento}
	 * @param idArea
	 *            Identificador del Area
	 * @param expediente
	 *            Identificador del Expediente {@link TipoExpediente}
	 * @param tema
	 * @param evento
	 * @param fechaEvento
	 * @param subTema
	 */
	public AsuntoCorrespondencia(Integer idAsunto, TipoDocumento tipoDocumento, Integer idArea,
			TipoExpediente expediente, Tema tema, TipoEvento evento, Date fechaEvento, SubTema subTema) {
		super();
		this.idAsunto = idAsunto;
		this.tipoDocumento = tipoDocumento;
		this.idArea = idArea;
		this.expediente = expediente;
		this.tema = tema;
		this.evento = evento;
		this.fechaEvento = fechaEvento;
		this.subTema = subTema;
	}

	/**
	 * Constructor minimo de la clase
	 * 
	 * @param idAsunto
	 *            Identificador del Asunto
	 * @param idArea
	 *            Identificador del Area
	 * @param expediente
	 *            Identificador del Expediente {@link TipoExpediente}
	 */
	public AsuntoCorrespondencia(Integer idAsunto, Integer idArea, TipoExpediente expediente) {
		super();
		this.idAsunto = idAsunto;
		this.idArea = idArea;
		this.expediente = expediente;
	}

	/**
	 * Obtiene el Identificador del Asunto
	 * 
	 * @return Identificador del Asunto
	 */
	public Integer getIdAsunto() {

		return this.idAsunto;
	}

	/**
	 * Asigna el Identificador del Asunto
	 * 
	 * @param idAsunto
	 *            Identificador del Asunto
	 */
	public void setIdAsunto(Integer idAsunto) {

		this.idAsunto = idAsunto;
	}

	/**
	 * Obtiene el Tipo de Documento {@link TipoDocumento}
	 * 
	 * @return Tipo de Documento {@link TipoDocumento}
	 */
	public TipoDocumento getTipoDocumento() {

		return this.tipoDocumento;
	}

	/**
	 * Asigna el Tipo de Documento {@link TipoDocumento}
	 * 
	 * @param tipoDocumento
	 *            Tipo de Documento {@link TipoDocumento}
	 */
	public void setTipoDocumento(TipoDocumento tipoDocumento) {

		this.tipoDocumento = tipoDocumento;
	}

	/**
	 * Obtiene el Identificador del Area
	 * 
	 * @return Identificador del Area
	 */
	public Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del Area
	 * 
	 * @param idArea
	 *            Identificador del Area
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene el Identificador del Expediente {@link TipoExpediente}
	 * 
	 * @return Identificador del Expediente {@link TipoExpediente}
	 */
	public TipoExpediente getExpediente() {

		return this.expediente;
	}

	/**
	 * Asigna el Identificador del Expediente {@link TipoExpediente}
	 * 
	 * @param expediente
	 *            Identificador del Expediente {@link TipoExpediente}
	 */
	public void setExpediente(TipoExpediente expediente) {

		this.expediente = expediente;
	}

	public Tema getTema() {
		return this.tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public TipoEvento getEvento() {
		return this.evento;
	}

	public void setEvento(TipoEvento evento) {
		this.evento = evento;
	}

	public Date getFechaEvento() {
		return this.fechaEvento;
	}

	public void setFechaEvento(Date fechaEvento) {
		this.fechaEvento = fechaEvento;
	}

	/**
	 * @return the subTema
	 */
	public SubTema getSubtema() {
		return subTema;
	}

	/**
	 * @param subTema
	 *            the subTema to set
	 */
	public void setSubtema(SubTema subTema) {
		this.subTema = subTema;
	}

	@Override
	public String toString() {
		return "AsuntoCorrespondencia [idAsunto=" + idAsunto + ", tipoDocumento=" + tipoDocumento + ", idArea=" + idArea
				+ ", expediente=" + expediente + ", tema=" + tema + ", evento=" + evento + ", fechaEvento="
				+ fechaEvento + ", subTema=" + subTema + "]";
	}

}
