/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Clase de identidad de la tabla CopiasRespuesta.
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */
@Entity
@Table(name = "copiasRespuesta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RespuestaCopia implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -855845592612525161L;

	/** The acceso key. */
	@EmbeddedId
	private RespuestaCopiaKey respuestaCopiaKey;

	/** Estatus de la Respuesta. */
	@OneToOne(fetch = FetchType.EAGER, optional = true, targetEntity = Status.class)
	@JoinColumn(name = "idEstatus")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Status status;

	/* Area a la que se esta enviando la copia */
	@OneToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Destinatario.class)
	@NotNull
	@JoinColumns({ @JoinColumn(name = "idArea", referencedColumnName = "idArea", insertable = false, updatable = false),
			@JoinColumn(name = "idDestinatario", referencedColumnName = "identificador", insertable = false, updatable = false) })
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Destinatario area;

	@ManyToOne
	@JoinColumn(name = "idRespuesta", insertable = false, updatable = false)
	private RespuestaCopiaConsulta respuesta;

	/**
	 * Gets the respuesta copia key.
	 *
	 * @return the respuesta copia key
	 */
	public RespuestaCopiaKey getRespuestaCopiaKey() {
		return respuestaCopiaKey;
	}

	/**
	 * Sets the respuesta copia key.
	 *
	 * @param respuestaCopiaKey the new respuesta copia key
	 */
	public void setRespuestaCopiaKey(RespuestaCopiaKey respuestaCopiaKey) {
		this.respuestaCopiaKey = respuestaCopiaKey;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public Destinatario getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area the new area
	 */
	public void setArea(Destinatario area) {
		this.area = area;
	}

	/**
	 * @return the respuesta
	 */
	public RespuestaCopiaConsulta getRespuesta() {
		return respuesta;
	}

	/**
	 * @param respuesta the respuesta to set
	 */
	public void setRespuesta(RespuestaCopiaConsulta respuesta) {
		this.respuesta = respuesta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RespuestaCopia [respuestaCopiaKey=" + respuestaCopiaKey + ", status=" + status + ", area=" + area + "]";
	}

}
