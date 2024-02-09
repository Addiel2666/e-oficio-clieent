/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

/**
 * The Class RespuestaConsulta.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "RESPUESTASEGUIMIENTO_LITE")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RespuestaSeguimiento implements Serializable {

	/** */
	private static final long serialVersionUID = 2910910711423802593L;

	/** Identificador de la Respuesta. */
	@Id
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Fecha de envio de la Respuesta. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** */
	@Column(name = "tipoRespuestaDescripcion")
	private String tipoRespuestaDescripcion;

	/** Estatus de la Respuesta. */
	@OneToOne
	@JoinColumn(name = "idEstatusRespuesta", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Status status;	

	/** Fecha de acuse de la Respuesta. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/**
	 * @return the idRespuesta
	 */
	public Integer getIdRespuesta() {
		return idRespuesta;
	}

	/**
	 * @param idRespuesta the idRespuesta to set
	 */
	public void setIdRespuesta(Integer idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	/**
	 * @return the fechaEnvio
	 */
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * @param fechaEnvio the fechaEnvio to set
	 */
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * @return the tipoRespuestaDescripcion
	 */
	public String getTipoRespuestaDescripcion() {
		return tipoRespuestaDescripcion;
	}

	/**
	 * @param tipoRespuestaDescripcion the tipoRespuestaDescripcion to set
	 */
	public void setTipoRespuestaDescripcion(String tipoRespuestaDescripcion) {
		this.tipoRespuestaDescripcion = tipoRespuestaDescripcion;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the fechaAcuse
	 */
	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	/**
	 * @param fechaEnvio the fechaAcuse to set
	 */
	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	@Override
	public String toString() {
		return "RespuestaSeguimiento [idRespuesta=" + idRespuesta + ", fechaEnvio=" + fechaEnvio
				+ ", fechaAcuse=" + fechaAcuse + ", tipoRespuestaDescripcion=" + tipoRespuestaDescripcion 
				+ ", status=" + status + "]";
	}

}
