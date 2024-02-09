/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clase de entidad que se usa como clave primaria de la vista RespuestaCopia.
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */
@Embeddable
public final class RespuestaCopiaKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8216913183094768065L;

	/** Identificador del Asunto. */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Identificador del area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** Identificador del SubTipoAsunto. */
	@Column(name = "idSubTipoAsunto")
	private String idSubTipoAsunto;

	/** Identificador del Destinatario. */
	@Column(name = "idDestinatario")
	private String idDestinatario;

	/**
	 * Constructor por defecto de la clase.
	 */
	public RespuestaCopiaKey() {
		super();
	}

	/**
	 * 
	 * @param idAsunto
	 * @param respuesta
	 * @param idArea
	 * @param idSubTipoAsunto
	 * @param idDestinatario
	 */
	public RespuestaCopiaKey(Integer idAsunto, Integer idRespuesta, Integer idArea, String idSubTipoAsunto,
			String idDestinatario) {
		super();
		this.idAsunto = idAsunto;
		this.idRespuesta = idRespuesta;
		this.idArea = idArea;
		this.idSubTipoAsunto = idSubTipoAsunto;
		this.idDestinatario = idDestinatario;
	}

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the idSubTipoAsunto
	 */
	public String getIdSubTipoAsunto() {
		return idSubTipoAsunto;
	}

	/**
	 * @param idSubTipoAsunto the idSubTipoAsunto to set
	 */
	public void setIdSubTipoAsunto(String idSubTipoAsunto) {
		this.idSubTipoAsunto = idSubTipoAsunto;
	}

	/**
	 * @return the idDestinatario
	 */
	public String getIdDestinatario() {
		return idDestinatario;
	}

	/**
	 * @param idDestinatario the idDestinatario to set
	 */
	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * @return the idRespuesta
	 */
	public Integer getIdRespuesta() {
		return idRespuesta;
	}

	/**
	 * @param idRespuesta the idRespuesta to set
	 */
	public void setIdRespuesta(Integer idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RespuestaCopiaKey [idAsunto=" + idAsunto + ", idRespuesta=" + idRespuesta + ", idArea=" + idArea
				+ ", idSubTipoAsunto=" + idSubTipoAsunto + ", idDestinatario=" + idDestinatario + "]";
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

		if (!(obj instanceof RespuestaCopiaKey)) {
			return false;
		}

		if (!RespuestaCopiaKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		RespuestaCopiaKey tmp = (RespuestaCopiaKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idAsunto == this.idAsunto//
					&& tmp.idDestinatario.equals(this.idDestinatario) //
					&& tmp.idSubTipoAsunto.equals(this.idSubTipoAsunto) //
					&& tmp.idRespuesta.equals(this.idRespuesta))
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
		return Objects.hash(this.idArea, this.idAsunto, this.idDestinatario, this.idSubTipoAsunto, this.idRespuesta);
	}
}
