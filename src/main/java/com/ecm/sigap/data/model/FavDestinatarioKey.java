/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Embeddable
public class FavDestinatarioKey implements java.io.Serializable {

	/** */
	private static final long serialVersionUID = 4126331167312839522L;

	/** Identificador del Area favorito */
	@Column(name = "idArea")
	private Integer idArea;

	/** Area a la que pertenece el destinatario */
	@OneToOne
	@JoinColumn(name = "areaDestinatario")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Area areaDestinatario;

	/** Identificador del Destinatario */
	@Column(name = "idDestinatario", insertable = false, updatable = false)
	private String idDestinatario;

	/** Identificador del Tipo de Destinatario */
	@Column(name = "idTipoDestinatario")
	private Integer idTipoDestinatario;

	/** Identificador de Area a la que pertenece el destinatario */
	@Column(name = "idAreaDestinatario")
	private Integer idAreaDestinatario;

	/** Nombre del area o empresa al que pertenece el destinatario */
	@Column(name = "descAreaDestinatario")
	private String descAreaDestinatario;

	/**
	 * Obtiene el Identificador del Area favorito
	 * 
	 * @return Identificador del Area favorito
	 */
	public final Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del Area favorito
	 * 
	 * @param idArea Identificador del Area favorito
	 */
	public final void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene el Area a la que pertenece el destinatario
	 * 
	 * @return Area a la que pertenece el destinatario
	 */
	public final Area getAreaDestinatario() {

		return areaDestinatario;
	}

	/**
	 * Asigna el Area a la que pertenece el destinatario
	 * 
	 * @param areaDestinatario Area a la que pertenece el destinatario
	 */
	public final void setAreaDestinatario(Area areaDestinatario) {

		this.areaDestinatario = areaDestinatario;
	}

	/**
	 * Obtiene el Identificador del Destinatario
	 * 
	 * @return Identificador del Destinatario
	 */
	public final String getIdDestinatario() {

		return idDestinatario;
	}

	/**
	 * Asigna el Identificador del Destinatario
	 * 
	 * @param idDestinatario Identificador del Destinatario
	 */
	public final void setIdDestinatario(String idDestinatario) {

		this.idDestinatario = idDestinatario;
	}

	/**
	 * Obtiene el Identificador del Tipo de Destinatario
	 * 
	 * @return Identificador del Tipo de Destinatario
	 */
	public final Integer getIdTipoDestinatario() {

		return idTipoDestinatario;
	}

	/**
	 * Asigna el Identificador del Tipo de Destinatario
	 * 
	 * @param idTipoDestinatario Identificador del Tipo de Destinatario
	 */
	public final void setIdTipoDestinatario(Integer idTipoDestinatario) {

		this.idTipoDestinatario = idTipoDestinatario;
	}

	/**
	 * Obtiene el Identificador de Area a la que pertenece el destinatario
	 * 
	 * @return Identificador de Area a la que pertenece el destinatario
	 */
	public Integer getIdAreaDestinatario() {

		return idAreaDestinatario;
	}

	/**
	 * Asigna el Identificador de Area a la que pertenece el destinatario
	 * 
	 * @param idAreaDestinatario Identificador de Area a la que pertenece el
	 *                           destinatario
	 */
	public void setIdAreaDestinatario(Integer idAreaDestinatario) {

		this.idAreaDestinatario = idAreaDestinatario;
	}

	/**
	 * Obtiene el Nombre del area o empresa al que pertenece el destinatario
	 * 
	 * @return Nombre del area o empresa al que pertenece el destinatario
	 */
	public String getDescAreaDestinatario() {

		return descAreaDestinatario;
	}

	/**
	 * Asigna el Nombre del area o empresa al que pertenece el destinatario
	 * 
	 * @param descAreaDestinatario Nombre del area o empresa al que pertenece el
	 *                             destinatario
	 */
	public void setDescAreaDestinatario(String descAreaDestinatario) {

		this.descAreaDestinatario = descAreaDestinatario;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FavDestinatarioKey [idArea=" + idArea + ", areaDestinatario=" + areaDestinatario + ", idDestinatario="
				+ idDestinatario + ", idTipoDestinatario=" + idTipoDestinatario + ", idAreaDestinatario="
				+ idAreaDestinatario + ", descAreaDestinatario=" + descAreaDestinatario + "]";
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

		if (!(obj instanceof FavDestinatarioKey)) {
			return false;
		}

		if (!FavDestinatarioKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavDestinatarioKey tmp = (FavDestinatarioKey) obj;

		try {

			if (tmp.areaDestinatario == this.areaDestinatario//
					&& tmp.descAreaDestinatario.equals(this.descAreaDestinatario)//
					&& tmp.idArea == this.idArea //
					&& tmp.idTipoDestinatario == this.idTipoDestinatario//
					&& tmp.descAreaDestinatario.equals(this.descAreaDestinatario)//
					&& tmp.idDestinatario.equals(this.idDestinatario)//
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
		return Objects.hash(this.idArea, this.idAreaDestinatario, this.idTipoDestinatario, this.areaDestinatario,
				this.descAreaDestinatario, this.idDestinatario);
	}

}
