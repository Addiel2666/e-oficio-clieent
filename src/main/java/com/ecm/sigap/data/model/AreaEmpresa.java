/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * Clase de entidad que representa la tabla AREASEMPRESAS
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 */
@IdClass(AreaEmpresaKey.class)
@Entity
@UniqueKey(columnNames = { "idArea", "idEmpresa", "idInstitucion" }, message = "${Unique.descripcion}")
@Table(name = "areasempresas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AreaEmpresa implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2166711770629342119L;

	@Id
	@Column(name = "IDAREA")
	/** Identificador del Area al que pertenece el favorito */
	private Integer idArea;

	@Id
	@Column(name = "IDEMPRESA")
	/** Identificador de la Empresa */
	private Integer idEmpresa;

	@Id
	@Column(name = "IDINSTITUCION")
	/**
	 * Identificador de la Institucion, en este caso el Identificador de Empresa
	 * en la tabla INSTITUCIONES
	 */
	private Integer idInstitucion;

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
}
