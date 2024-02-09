/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clase de identidad que representa la clave primaria de la entidad Permisos
 * {@link com.ecm.sigap.data.model.Permiso }
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 23-Mar-2016
 * 
 *          Creacion de la clase
 *
 */
@Embeddable
public class PermisoKey implements java.io.Serializable {

	/** */
	private static final long serialVersionUID = 5293414453201172587L;

	/** Tipo de Permiso */
	@Column(name = "idTipoPermiso", nullable = false, length = 1)
	private String idTipoPermiso;

	/** Identificador del Permiso */
	@Column(name = "idObjeto", nullable = false, length = 10)
	private String idObjeto;

	/** Identificador del Area al que pertene el Rol */
	@Column(name = "idArea")
	private Integer idArea;

	/** Identificador del Rol */
	@Column(name = "idRol", nullable = false, precision = 38, scale = 0)
	private Integer idRol;

	/**
	 * Constructor por defecto de la clase
	 */
	public PermisoKey() {
	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param idTipoPermiso
	 *            Tipo de Permiso
	 * @param idObjeto
	 * @param idArea
	 * @param idRol
	 */
	public PermisoKey(String idTipoPermiso, String idObjeto, Integer idArea, Integer idRol) {
		this.idTipoPermiso = idTipoPermiso;
		this.idObjeto = idObjeto;
		this.idArea = idArea;
		this.idRol = idRol;
	}

	/**
	 * Obtiene el tipo de Permiso
	 * 
	 * @return Tipo de Permiso
	 */
	public String getIdTipoPermiso() {

		return this.idTipoPermiso;
	}

	/**
	 * Asigna el tipo de Permiso
	 * 
	 * @param idTipoPermiso
	 *            Tipo de Permiso
	 */
	public void setIdTipoPermiso(String idTipoPermiso) {

		this.idTipoPermiso = idTipoPermiso;
	}

	/**
	 * Obtiene el identificador del Permiso
	 * 
	 * @return Identificador del Permiso
	 */
	public String getIdObjeto() {

		return this.idObjeto;
	}

	/**
	 * Asigna el identificador del Permiso
	 * 
	 * @param idObjeto
	 *            Identificador del Permiso
	 */
	public void setIdObjeto(String idObjeto) {

		this.idObjeto = idObjeto;
	}

	/**
	 * Obtiene el identificador del Area al que pertene el Rol
	 * 
	 * @return Identificador del Area al que pertene el Rol
	 */
	public Integer getIdArea() {

		return this.idArea;
	}

	/**
	 * Asigna el identificador del Area al que pertene el Rol
	 * 
	 * @param idArea
	 *            Identificador del Area al que pertene el Rol
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene el identificador del Rol
	 * 
	 * @return Identificador del Rol
	 */
	public Integer getIdRol() {

		return this.idRol;
	}

	/**
	 * Asigna el identificador del Rol
	 * 
	 * @param idRol
	 *            Identificador del Rol
	 */
	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PermisoKey [idTipoPermiso=" + idTipoPermiso + ", idObjeto=" + idObjeto + ", idArea=" + idArea
				+ ", idRol=" + idRol + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this)
			return true;

		if (!(obj instanceof PermisoKey)) {
			return false;
		}

		if (!PermisoKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		PermisoKey tmp = (PermisoKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idRol == this.idRol//
					&& tmp.idObjeto.equals(this.idObjeto)//
					&& tmp.idTipoPermiso.equals(this.idTipoPermiso)//
			)
				return true;

		} catch (NullPointerException e) {
			return false;
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.idArea, this.idObjeto, this.idRol, this.idTipoPermiso);
	}

}
