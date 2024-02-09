/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import com.ecm.sigap.data.model.AsuntoConsulta;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Carlos Sotolongo
 * @version 1.0
 *
 */
public class PlantillaExcel implements Serializable {

	/** */
	private static final long serialVersionUID = 1390569367735007301L;
	/** */
	private String id;
	/** */
	private String nombre;
	/** */
	private String file;
	/** */
	private String tipo;
	/** */
	private List<AsuntoConsulta> asuntos;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the asuntos
	 */
	public List<AsuntoConsulta> getAsuntos() {
		return asuntos;
	}

	/**
	 * @param asuntos
	 *            the asuntos to set
	 */
	public void setAsuntos(List<AsuntoConsulta> asuntos) {
		this.asuntos = asuntos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PlantillaExcel [id=" + id + ", nombre=" + nombre + ", file=" + file + ", tipo=" + tipo + ", asuntos=["
				+ (asuntos != null ? asuntos.size() : "empty") + "] ]";
	}

}
