/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.model.util.Documento;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "MINUTARIODOCUMENTOS")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class DocumentoMinutario extends Documento implements Serializable {

	/**  */
	private static final long serialVersionUID = 5771513696558570051L;

	/** ID del {@link Minutario} al que pertenece. */
	@Column(name = "idMinutario")
	private Integer idMinutario;

	/** Area del dueño del documento, para aplicar ACL correspondiente. */
	@Transient
	private String idArea;

	/**
	 * @return the idMinutario
	 */
	public Integer getIdMinutario() {
		return idMinutario;
	}

	/**
	 * @param idMinutario
	 *            the idMinutario to set
	 */
	public void setIdMinutario(Integer idMinutario) {
		this.idMinutario = idMinutario;
	}

	/**
	 * @return the idArea
	 */
	public String getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea
	 *            the idArea to set
	 */
	public void setIdArea(String idArea) {
		this.idArea = idArea;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentoMinutario [idMinutario=" + idMinutario + ", idArea=" + idArea + ", getObjectId()="
				+ getObjectId() + ", getFechaRegistro()=" + getFechaRegistro() + ", getObjectName()=" + getObjectName()
				+ ", getParentContentId()=" + getParentContentId() + ", getOwnerName()=" + getOwnerName()
				+ ", isCheckout()=" + isCheckout() + ", getVersion()=" + getVersion() + "]";
	}

}
