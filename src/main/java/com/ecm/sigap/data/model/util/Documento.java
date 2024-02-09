/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Documento adjunto a la applicacion.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Access(AccessType.FIELD)
@MappedSuperclass
public abstract class Documento {

	/** Id en el repositorio del documento. */
	@Id
	@Column(name = "r_object_id")
	private String objectId;

	/** Fecha de carga del documento. */
	@Column(name = "fechaRegistro")
	private Date fechaRegistro;

	/** Nombre del archivo */
	@Transient
	private String objectName;

	/** Cadena en base64 con el contenido del archivo */
	@Transient
	private String fileB64;

	/** ID del folder padre. */
	@JsonIgnore
	@Transient
	private String parentContentId;

	/** Dueño del documento en el repositorio. */
	@Transient
	private String ownerName;

	/** Documento Bloqueado. */
	@Transient
	private Boolean checkout;
	
	/** Version */
	@Transient
	private String version;

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the fechaRegistro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * @param fechaRegistro
	 *            the fechaRegistro to set
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * @return the fileB64
	 */
	public String getFileB64() {
		return fileB64;
	}

	/**
	 * @param fileB64
	 *            the fileB64 to set
	 */
	public void setFileB64(String fileB64) {
		this.fileB64 = fileB64;
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return the parentContentId
	 */
	public String getParentContentId() {
		return parentContentId;
	}

	/**
	 * @param parentContentId
	 *            the parentContentId to set
	 */
	public void setParentContentId(String parentContentId) {
		this.parentContentId = parentContentId;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName
	 *            the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @return the checkout
	 */
	public Boolean isCheckout() {
		return checkout;
	}

	/**
	 * @param checkout
	 *            the checkout to set
	 */
	public void setCheckout(Boolean checkout) {
		this.checkout = checkout;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
