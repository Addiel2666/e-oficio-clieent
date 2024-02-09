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
public class EArchivoTipoDocCatalogo {

	/** */
	private String descripcion;
	/** */
	private Integer idTipoDocCatalogo;

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
	 * @return the idTipoDocCatalogo
	 */
	public Integer getIdTipoDocCatalogo() {
		return idTipoDocCatalogo;
	}

	/**
	 * @param idTipoDocCatalogo
	 *            the idTipoDocCatalogo to set
	 */
	public void setIdTipoDocCatalogo(Integer idTipoDocCatalogo) {
		this.idTipoDocCatalogo = idTipoDocCatalogo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EArchivoTipoDocCatalogo [descripcion=" + descripcion + ", idTipoDocCatalogo=" + idTipoDocCatalogo + "]";
	}

}
