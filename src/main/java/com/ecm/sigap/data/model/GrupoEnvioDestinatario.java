/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ecm.sigap.data.model.util.TipoDestinatario;

/**
 * 
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public final class GrupoEnvioDestinatario implements Serializable {

	/** */
	private static final long serialVersionUID = -4757378087303500904L;

	/** */
	@Column(name = "idDestinatario")
	private String idDestinatario;

	/** */
	@Column(name = "idTipoDestinatario")
	@Enumerated(EnumType.ORDINAL)
	private TipoDestinatario tipoDestinatario;

	/** */
	@Column(name = "idArea")
	private Integer idArea;

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
	 * @return the tipoDestinatario
	 */
	public TipoDestinatario getTipoDestinatario() {
		return tipoDestinatario;
	}

	/**
	 * @param tipoDestinatario the tipoDestinatario to set
	 */
	public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
		this.tipoDestinatario = tipoDestinatario;
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

	@Override
	public String toString() {
		return "GrupoEnvioDestinatario [idDestinatario=" + idDestinatario + ", tipoDestinatario=" + tipoDestinatario
				+ ", idArea=" + idArea + "]";
	}

}
