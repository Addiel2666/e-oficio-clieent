/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo.model;

/**
 * 
 * @author
 * @version 1.0
 *
 */
public class EArchivoLegajo {

	/** */
	private Integer idLegajo;
	/** */
	private Integer idExpediente;
	/** */
	private String descripcion;
	private Integer numero;

	public EArchivoLegajo() {
	}

	public EArchivoLegajo(Integer idExpediente, Integer numero) {
		this.idExpediente = idExpediente;
		this.numero = numero;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getIdLegajo() {
		return idLegajo;
	}

	/**
	 * 
	 * @param idLegajo
	 */
	public void setIdLegajo(Integer idLegajo) {
		this.idLegajo = idLegajo;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getIdExpediente() {
		return idExpediente;
	}

	/**
	 * 
	 * @param idExpediente
	 */
	public void setIdExpediente(Integer idExpediente) {
		this.idExpediente = idExpediente;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * 
	 * @param descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EArchivoLegajo{" + "idLegajo=" + idLegajo + ", idExpediente='" + idExpediente + '\'' + ", descripcion='"
				+ descripcion + '\'' + '}';
	}
}
