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
 * The Class TipoPrioridad.
 *
 * @author ECM Solutions
 * @version 1.0
 */
@Entity
@Table(name = "tiposPrioridad")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class TipoPrioridad2 implements Serializable {

	/** */
	private static final long serialVersionUID = 3986991092743529818L;

	/** The id prioridad. */
	@Id
	@Column(name = "idPrioridad", insertable = false)
	private Integer idPrioridad;

	/** Identificador del Area */
	@Column(name = "idArea")
	private Integer idArea;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The dias. */
	@Column(name = "dias")
	private Integer dias;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * @return the idPrioridad
	 */
	public Integer getIdPrioridad() {
		return idPrioridad;
	}

	/**
	 * @param idPrioridad the idPrioridad to set
	 */
	public void setIdPrioridad(Integer idPrioridad) {
		this.idPrioridad = idPrioridad;
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
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the dias
	 */
	public Integer getDias() {
		return dias;
	}

	/**
	 * @param dias the dias to set
	 */
	public void setDias(Integer dias) {
		this.dias = dias;
	}

	/**
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * @param activo the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoPrioridad2 [idPrioridad=" + idPrioridad + ", idArea=" + idArea + ", descripcion=" + descripcion
				+ ", dias=" + dias + ", activo=" + activo + "]";
	}
}
