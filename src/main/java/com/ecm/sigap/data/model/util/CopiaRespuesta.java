/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.model.Destinatario;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.util.SubTipoAsuntoToStringConverter;

/**
 * Clase de entidad que representa la tabla COPIASRESPUESTA.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class CopiaRespuesta {

	/** Identificador del Asunto */
	@Column(name = "idAsunto")
	@NotNull
	private Integer idAsunto;

	/** Area a la que se esta enviando la copia */
	@OneToOne
	@NotNull
	@JoinColumns({@JoinColumn(name = "idArea", referencedColumnName = "idArea") , 
		@JoinColumn(name = "idDestinatario", referencedColumnName = "identificador")})
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Destinatario area;

	/** Identificador del Sub Tipo Asunto */
	@Column(name = "idSubTipoAsunto")
	@Convert(converter = SubTipoAsuntoToStringConverter.class)
	@Enumerated(EnumType.STRING)
	private SubTipoAsunto idSubTipoAsunto;

	/** {@link Status}} de la Copia */
	@OneToOne
	@JoinColumn(name = "idEstatus")
	@Fetch(FetchMode.SELECT)
	private Status status;

	/**
	 * Obtiene el Identificador del Asunto
	 * 
	 * @return Identificador del Asunto
	 */
	public Integer getIdAsunto() {

		return idAsunto;
	}

	/**
	 * Asigna el Identificador del Asunto
	 * 
	 * @param idAsunto Identificador del Asunto
	 */
	public void setIdAsunto(Integer idAsunto) {

		this.idAsunto = idAsunto;
	}

	// /**
	// * Obtiene el Identificador del Area
	// *
	// * @return Identificador del Area
	// */
	// public final Integer getIdArea() {
	//
	// return idArea;
	// }
	//
	// /**
	// * Asigna el Identificador del Area
	// *
	// * @param idArea
	// * Identificador del Area
	// */
	// public final void setIdArea(Integer idArea) {
	//
	// this.idArea = idArea;
	// }

	/**
	 * Obtiene el Area a la que se esta enviando la copia
	 * 
	 * @return Area a la que se esta enviando la copia
	 */
	public Destinatario getArea() {

		return area;
	}

	/**
	 * Asigna el Area a la que se esta enviando la copia
	 * 
	 * @param area Area a la que se esta enviando la copia
	 */
	public void setArea(Destinatario area) {

		this.area = area;
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
	 * @param idSubTipoAsunto Identificador del Sub Tipo Asunto
	 */
	public final void setIdSubTipoAsunto(SubTipoAsunto idSubTipoAsunto) {

		this.idSubTipoAsunto = idSubTipoAsunto;
	}

	// /**
	// * Obtiene el Identificador del Usuario Destinatario de la Copia
	// *
	// * @return Identificador del Usuario Destinatario de la Copia
	// */
	// public final String getDestinatario() {
	//
	// return destinatario;
	// }
	//
	// /**
	// * Asigna el Identificador del Usuario Destinatario de la Copia
	// *
	// * @param destinatario
	// * Identificador del Usuario Destinatario de la Copia
	// */
	// public final void setDestinatario(String destinatario) {
	// this.destinatario = destinatario;
	// }

	/**
	 * Obtiene el Identificador del estatus de la copia
	 * 
	 * @return Identificador del estatus de la copia
	 */
	public Status getStatus() {

		return status;
	}

	/**
	 * Asigna el Identificador del estatus de la copia
	 * 
	 * @param status Identificador del estatus de la copia
	 */
	public void setStatus(Status status) {

		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CopiaRespuesta [idAsunto=" + idAsunto + ", area=" + area + ", idSubTipoAsunto=" + idSubTipoAsunto
		// + ", destinatario=" + destinatario
				+ ", status=" + status + "]";
	}
}