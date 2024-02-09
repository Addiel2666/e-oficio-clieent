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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "documentosRespuestas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@Where(clause = "status = 'F'")
public final class DocumentoRespuestaFirmado implements Serializable {

	/**  */
	private static final long serialVersionUID = -4071861155974121340L;

	/** Id en el repositorio del documento. */
	@Id
	@Column(name = "r_object_id")
	private String objectId;

	/** ID del {@link Asunto} a la que pertenece. */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** ID de la {@link Respuesta} a la que pertenece. */
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Area del dueño del documento, para aplicar ACL correspondiente. */
	@Column(name = "idArea")
	private Integer idArea;

	/** Fecha de carga del documento. */
	@Column(name = "fechaRegistro")
	private Date fechaRegistro;

	/** Nombre del archivo */
	@Transient
	private String objectName;

	/** Dueño del documento en el repositorio. */
	@Transient
	private String ownerName;

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

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
	 * @return the fechaRegistro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * @param fechaRegistro the fechaRegistro to set
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
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

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Override
	public String toString() {
		return "DocumentoRespuestaFirmado [objectId=" + objectId + ", idAsunto=" + idAsunto + ", idRespuesta="
				+ idRespuesta + ", idArea=" + idArea + ", fechaRegistro=" + fechaRegistro + ", objectName=" + objectName
				+ ", ownerName=" + ownerName + "]";
	}

}
