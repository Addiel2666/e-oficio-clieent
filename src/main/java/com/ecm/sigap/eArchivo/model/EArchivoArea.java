/*
 * Copyright (C) 2017 Pivotal Software, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ecm.sigap.eArchivo.model;

import java.io.Serializable;

/**
 * 
 * @author
 * @version 1.0
 *
 */
public class EArchivoArea implements Serializable {

	/**  */
	private static final long serialVersionUID = -2647751339093650386L;
	/** */
	private Integer idArea;
	/** */
	private String descripcion;
	/** */
	private String clavePresupuestal;
	/** */
	private String siglas;

	/**
	 * 
	 * @return
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * 
	 * @param idArea
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
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

	/**
	 * 
	 * @return
	 */
	public String getClavePresupuestal() {
		return clavePresupuestal;
	}

	/**
	 * 
	 * @param clavePresupuestal
	 */
	public void setClavePresupuestal(String clavePresupuestal) {
		this.clavePresupuestal = clavePresupuestal;
	}

	/**
	 * 
	 * @return
	 */
	public String getSiglas() {
		return siglas;
	}

	/**
	 * 
	 * @param siglas
	 */
	public void setSiglas(String siglas) {
		this.siglas = siglas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EArchivoArea [idArea=" + idArea + ", descripcion=" + descripcion + ", clavePresupuestal="
				+ clavePresupuestal + ", siglas=" + siglas + "]";
	}

}
