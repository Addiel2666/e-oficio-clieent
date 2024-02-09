/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "Asuntos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@Where(clause = " idTipoAsunto != 'A' ")
public class AsuntoCopiaTurnada implements Serializable {

	/** */
	private static final long serialVersionUID = -736784276725611132L;

	/** Identificador del Asunto */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** Identificador del Asunto padre */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;

	/** Tipo de Asunto {@link com.ecm.sigap.data.model.util.TipoAsunto} */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** Area Destino del Tramite */
	@OneToOne
	@JoinColumn(name = "idAreaDestino")
	@Fetch(FetchMode.SELECT)
	private Area2 areaDestino;

	/**
	 * Obtiene el Identificador del Asunto
	 * 
	 * @return Identificador del Asunto
	 */
	public Integer getIdAsunto() {

		return idAsunto;
	}

	/**
	 * Asigna el Identificador del Asunto
	 * 
	 * @param idAsunto Identificador del Asunto
	 */
	public void setIdAsunto(Integer idAsunto) {

		this.idAsunto = idAsunto;
	}

	/**
	 * Obtiene el Tipo de Asunto
	 * 
	 * @return Tipo de Asunto
	 */
	public TipoAsunto getTipoAsunto() {

		return tipoAsunto;
	}

	/**
	 * Asigna el Tipo de Asunto
	 * 
	 * @param tipoAsunto Tipo de Asunto
	 */
	public void setTipoAsunto(TipoAsunto tipoAsunto) {

		this.tipoAsunto = tipoAsunto;
	}

	/**
	 * @return the areaDestino
	 */
	public Area2 getAreaDestino() {
		return areaDestino;
	}

	/**
	 * @param areaDestino the areaDestino to set
	 */
	public void setAreaDestino(Area2 areaDestino) {
		this.areaDestino = areaDestino;
	}

	/**
	 * Obtiene el Identificador del Asunto padre
	 * 
	 * @return Identificador del Asunto padre
	 */
	public Integer getIdAsuntoPadre() {

		return idAsuntoPadre;
	}

	/**
	 * Asigna el Identificador del Asunto padre
	 * 
	 * @param idAsuntoPadre Identificador del Asunto padre
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {

		this.idAsuntoPadre = idAsuntoPadre;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AsuntoCopiaTurnada [idAsunto=" + idAsunto + ", idAsuntoPadre=" + idAsuntoPadre + ", tipoAsunto="
				+ tipoAsunto + ", areaDestino=" + areaDestino + "]";
	}

}
