/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.util.StatusFirmaDocumentoConverter;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "documentosRespuestas")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class DocumentoRespuestaAux extends Documento implements Serializable {

	/**  */
	private static final long serialVersionUID = -2911075707972951273L;

	/** ID del {@link Asunto} a la que pertenece. */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** ID de la {@link Respuesta} a la que pertenece. */
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Area del dueño del documento, para aplicar ACL correspondiente. */
	@Column(name = "idArea")
	private Integer idArea;

	/** status de firma del archivo */
	@Column(name = "status")
	@Convert(converter = StatusFirmaDocumentoConverter.class)
	private StatusFirmaDocumento status;

	@Transient
	private String objectName;

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentoRespuesta [idAsunto=" + idAsunto + ", idRespuesta=" + idRespuesta + ", getObjectId()="
				+ getObjectId() + ", getFechaRegistro()=" + getFechaRegistro() + "]";
	}

	/**
	 * @return the status
	 */
	public StatusFirmaDocumento getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusFirmaDocumento status) {
		this.status = status;
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
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}
