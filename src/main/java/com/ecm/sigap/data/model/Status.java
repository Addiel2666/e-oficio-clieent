/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "estados")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Status implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3727025193698253627L;
	/** */
	@Id
	@GeneratedValue
	@Column(name = "idEstado")
	private Integer idStatus;
	/** */
	@Column(name = "descripcion")
	private String descripcion;

	/** */
	public static final int POR_ENVIAR = 0;
	/** */
	public static final int ENVIADO = 1;
	/** */
	public static final int PROCESO = 2;
	/** */
	public static final int CONCLUIDO = 3;
	/** */
	public static final int VENCIDO = 4;
	/** */
	public static final int RESUELTO = 5;
	/** */
	public static final int RECHAZADO = 6;
	/** */
	public static final int CANCELADO = 7;
	/** */
	public static final int ATENDIDO = 8;

	/**
	 * 
	 */
	public Status() {
		super();
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the idStatus
	 */
	public Integer getIdStatus() {
		return idStatus;
	}

	/**
	 * @param idStatus
	 *            the idStatus to set
	 */
	public void setIdStatus(Integer idStatus) {
		this.idStatus = idStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Status [idStatus=" + idStatus + ", descripcion=" + descripcion + "]";
	}

}
