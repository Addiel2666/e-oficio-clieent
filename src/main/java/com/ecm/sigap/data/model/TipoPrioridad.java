/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class TipoPrioridad.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "tiposPrioridad")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_TIPOPRIORIDAD", sequenceName = "SECOBJETOS", allocationSize = 1)
@UniqueKey(columnNames = { "descripcion", "area.idArea" }, message = "{Unique.descripcion}")
public class TipoPrioridad implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5666453394629331695L;

	/** The id prioridad. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TIPOPRIORIDAD")
	@Column(name = "idPrioridad")
	private Integer idPrioridad;

	/** The id area. */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private AreaAuxiliar area;

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
	 * Gets the id prioridad.
	 *
	 * @return the id prioridad
	 */
	public Integer getIdPrioridad() {
		return idPrioridad;
	}

	/**
	 * Sets the id prioridad.
	 *
	 * @param idPrioridad the new id prioridad
	 */
	public void setIdPrioridad(Integer idPrioridad) {
		this.idPrioridad = idPrioridad;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public AreaAuxiliar getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area the new area
	 */
	public void setArea(AreaAuxiliar area) {
		this.area = area;
	}

	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion the new descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the dias.
	 *
	 * @return the dias
	 */
	public Integer getDias() {
		return dias;
	}

	/**
	 * Sets the dias.
	 *
	 * @param dias the new dias
	 */
	public void setDias(Integer dias) {
		this.dias = dias;
	}

	/**
	 * Gets the activo.
	 *
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Sets the activo.
	 *
	 * @param activo the new activo
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoPrioridad [idPrioridad=" + idPrioridad + ", area=" + area + ", descripcion=" + descripcion
				+ ", dias=" + dias + ", activo=" + activo + "]";
	}
}
