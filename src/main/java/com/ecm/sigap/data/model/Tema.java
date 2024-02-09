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
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "temas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_OBJETOS", sequenceName = "SECOBJETOS", allocationSize = 1)
@UniqueKey(columnNames = { "descripcion", "area.idArea" }, message = "{Unique.descripcion}")
public class Tema implements Serializable {

	/**  */
	private static final long serialVersionUID = -6152434586190787093L;
	/** */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OBJETOS")
	@Column(name = "idTema")
	private Integer idTema;
	/** */
	@Column(name = "descripcion")
	private String descripcion;
	/** */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** The id area. */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private Area area;

	/**
	 * @return the idTema
	 */
	public Integer getIdTema() {
		return idTema;
	}

	/**
	 * @param idTema
	 *            the idTema to set
	 */
	public void setIdTema(Integer idTema) {
		this.idTema = idTema;
	}

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
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * @param activo
	 *            the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * @return the idArea
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * @param idArea
	 *            the idArea to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tema [idTema=" + idTema + ", descripcion=" + descripcion + ", activo=" + activo + ", Area=" + area
				+ "]";
	}

}
