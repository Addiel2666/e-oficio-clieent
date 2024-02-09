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
public final class DestinatarioGrupoEnvioKey implements Serializable {

	/** */
	private static final long serialVersionUID = -8458884507467260367L;

	/** */
	@Column(name = "idGrupo")
	private Integer idGrupo;

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
	 * @return the idGrupo
	 */
	public Integer getIdGrupo() {
		return idGrupo;
	}

	/**
	 * @param idGrupo the idGrupo to set
	 */
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idArea == null) ? 0 : idArea.hashCode());
		result = prime * result + ((idDestinatario == null) ? 0 : idDestinatario.hashCode());
		result = prime * result + ((idGrupo == null) ? 0 : idGrupo.hashCode());
		result = prime * result + ((tipoDestinatario == null) ? 0 : tipoDestinatario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DestinatarioGrupoEnvioKey other = (DestinatarioGrupoEnvioKey) obj;
		if (idArea == null) {
			if (other.idArea != null)
				return false;
		} else if (!idArea.equals(other.idArea))
			return false;
		if (idDestinatario == null) {
			if (other.idDestinatario != null)
				return false;
		} else if (!idDestinatario.equals(other.idDestinatario))
			return false;
		if (idGrupo == null) {
			if (other.idGrupo != null)
				return false;
		} else if (!idGrupo.equals(other.idGrupo))
			return false;
		if (tipoDestinatario != other.tipoDestinatario)
			return false;
		return true;
	}

}
