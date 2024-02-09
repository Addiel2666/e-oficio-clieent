/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "RespuestaConsultaAux")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RespuestaConsultaAux implements Serializable {

	/** */
	private static final long serialVersionUID = 3150926949790861934L;

	/** Identificador de la Respuesta. */
	@Id
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Identificador del Asunto Padre. */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;

	/** Fecha de envio de la Respuesta. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

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
	 * @return the idAsuntoPadre
	 */
	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	/**
	 * @param idAsuntoPadre the idAsuntoPadre to set
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	@Override
	public String toString() {
		return "RespuestaConsultaAux [idRespuesta=" + idRespuesta + ", idAsuntoPadre=" + idAsuntoPadre + ", fechaEnvio="
				+ fechaEnvio + "]";
	}

}
