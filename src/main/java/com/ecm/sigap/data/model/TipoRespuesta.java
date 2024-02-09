/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "tiposRespuesta2")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class TipoRespuesta implements Serializable {

	/**  */
	private static final long serialVersionUID = -2439693634208496447L;

	/** */
	@Id
	@Column(name = "idTiposRespuesta")
	private String idTipoRespuesta;

	/** */
	@Column(name = "descripcion")
	private String descripcion;

	/** */
	@Column(name = "tipoconcluidosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean tipoConcluida;

	/** */
	@Column(name = "infomex")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean infomex;

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
	 * @return the idTipoRespuesta
	 */
	public String getIdTipoRespuesta() {
		return idTipoRespuesta;
	}

	/**
	 * @param idTipoRespuesta
	 *            the idTipoRespuesta to set
	 */
	public void setIdTipoRespuesta(String idTipoRespuesta) {
		this.idTipoRespuesta = idTipoRespuesta;
	}

	/**
	 * @return the tipoConcluida
	 */
	public Boolean getTipoConcluida() {
		return tipoConcluida;
	}

	/**
	 * @param tipoConcluida
	 *            the tipoConcluida to set
	 */
	public void setTipoConcluida(Boolean tipoConcluida) {
		this.tipoConcluida = tipoConcluida;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoRespuesta [idTipoRespuesta=" + idTipoRespuesta + ", descripcion=" + descripcion + ", tipoConcluida="
				+ tipoConcluida + "]";
	}

	public Boolean getInfomex() {
		return infomex;
	}

	public void setInfomex(Boolean infomex) {
		this.infomex = infomex;
	}

}
