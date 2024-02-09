/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * Clase de entidad que representa la tabla AREASEMPRESAS
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 */
@Embeddable
public class AreaEmpresaKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8147155050566103913L;

	/** Identificador del Area al que pertenece el favorito */
	private Integer idArea;

	/** Identificador de la Empresa */
	private Integer idEmpresa;

	/**
	 * Identificador de la Institucion, en este caso el Identificador de Empresa
	 * en la tabla INSTITUCIONES
	 */
	private Integer idInstitucion;

	/**
	 * Constructor por defecto de la clase
	 */
	public AreaEmpresaKey() {

	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param idArea
	 *            Identificador del Area al que pertenece el favorito
	 * @param idEmpresa
	 *            Identificador de la Empresa
	 * @param idInstitucion
	 *            Identificador de la Institucion
	 */
	public AreaEmpresaKey(Integer idArea, Integer idEmpresa, Integer idInstitucion) {
		this.idArea = idArea;
		this.idEmpresa = idEmpresa;
		this.idInstitucion = idInstitucion;
	}

	/**
	 * Obtiene el Identificador del Area al que pertenece el favorito
	 * 
	 * @return Identificador del Area al que pertenece el favorito
	 */
	public final Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del Area al que pertenece el favorito
	 * 
	 * @param idArea
	 *            Identificador del Area al que pertenece el favorito
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene el Identificador de la Empresa
	 * 
	 * @return Identificador de la Empresa
	 */
	public final Integer getIdEmpresa() {

		return idEmpresa;
	}

	/**
	 * Asigna el Identificador de la Empresa
	 * 
	 * @param idEmpresa
	 *            Identificador de la Empresa
	 */
	public final void setIdEmpresa(Integer idEmpresa) {

		this.idEmpresa = idEmpresa;
	}

	/**
	 * Obtiene el Identificador de la Institucion
	 * 
	 * @return Identificador de la Institucion
	 */
	public final Integer getIdInstitucion() {

		return idInstitucion;
	}

	/**
	 * Asigna el Identificador de la Institucion, en este caso el Identificador
	 * de Empresa en la tabla INSTITUCIONES
	 * 
	 * @param idInstitucion
	 *            Identificador de la Institucion
	 */
	public final void setIdInstitucion(Integer idInstitucion) {

		this.idInstitucion = idInstitucion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AreaEmpresa [idArea=" + idArea + ", idEmpresa=" + idEmpresa + ", idInstitucion=" + idInstitucion + "]";
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
		if (!AreaEmpresaKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		AreaEmpresaKey tmp = (AreaEmpresaKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idEmpresa == this.idEmpresa //
					&& tmp.idInstitucion == this.idInstitucion)
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
		return Objects.hash(this.idArea, this.idEmpresa, this.idInstitucion);
	}

}