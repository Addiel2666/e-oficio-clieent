/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eCiudadano.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 * 
 *
 */
@Entity
@Table(name = "ACUSEFIRMADO_EPORTAL")
public class AcuseFirmado implements Serializable {

	/** */
	private static final long serialVersionUID = -2078221865279406807L;

	/** Identificador del Asunto */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** Id del objeto en el repositorio */
	@Column(name = "objectId")
	private String objectId;

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto
	 *            the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AcuseFirmado [idAsunto=" + idAsunto + ", objectId=" + objectId + "]";
	}

}
