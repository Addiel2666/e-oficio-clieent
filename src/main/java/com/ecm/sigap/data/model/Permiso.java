/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Clase de identidad que representa los permisos del usuario
 * {@link com.ecm.sigap.data.model.Permiso }
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 23-Mar-2016
 * 
 *          Creacion de la clase
 *
 */
@Entity
@Table(name = "PERMISOS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Permiso implements Serializable {

	/**  */
	private static final long serialVersionUID = 7300669725019299938L;

	/** Clave primaria de la entidad */
	private PermisoKey permisoKey;

	/**
	 * Descripcion del permiso. Para el caso de la nueva version de la
	 * aplicacion es el identificador de Permiso
	 */
	@Column(name = "descripcion", length = 15)
	private String descripcion;

	/**
	 * Constructor por defecto de la clase
	 */
	public Permiso() {
	}

	/**
	 * Constructor de la clase
	 * 
	 * @param permisoKey
	 *            Clave primaria de la entidad
	 */
	public Permiso(PermisoKey permisoKey) {
		this.permisoKey = permisoKey;
	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param id
	 *            Clave primaria de la entidad
	 * @param descripcion
	 *            Descripcion del permiso
	 */
	public Permiso(PermisoKey id, String descripcion) {
		this.permisoKey = id;
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene la clave primaria de la entidad
	 * 
	 * @return Clave primaria de la entidad
	 */
	@EmbeddedId
	public PermisoKey getPermisoKey() {
		return this.permisoKey;
	}

	/**
	 * Asigna la clave primaria de la entidad
	 * 
	 * @param permisoKey
	 *            Clave primaria de la entidad
	 */
	public void setPermisoKey(PermisoKey permisoKey) {
		this.permisoKey = permisoKey;
	}

	/**
	 * Obtiene la descripcion del permiso
	 * 
	 * @return Descripcion del permiso
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	/**
	 * Asigna la descripcion del permiso
	 * 
	 * @param descripcion
	 *            Descripcion del permiso
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Permiso [id=" + permisoKey + ", descripcion=" + descripcion + "]";
	}

}
