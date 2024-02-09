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
public class EArchivoUnidad {

	/** */
	private Integer idUnidad;
	/** */
	private String descripcion;
	/** */
	private String titutlar;

	/**
	 * @return the idUnidad
	 */
	public Integer getIdUnidad() {
		return idUnidad;
	}

	/**
	 * @param idUnidad
	 *            the idUnidad to set
	 */
	public void setIdUnidad(Integer idUnidad) {
		this.idUnidad = idUnidad;
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
	 * @return the titutlar
	 */
	public String getTitutlar() {
		return titutlar;
	}

	/**
	 * @param titutlar
	 *            the titutlar to set
	 */
	public void setTitutlar(String titutlar) {
		this.titutlar = titutlar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EArchivoUnidad [idUnidad=" + idUnidad + ", descripcion=" + descripcion + ", titutlar=" + titutlar + "]";
	}

}
