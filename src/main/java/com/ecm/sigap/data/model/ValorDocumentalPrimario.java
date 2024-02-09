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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "valorDocPrimario")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_VALORDOCPRIMARIO", sequenceName = "VALORDOCPRIMARIO_SEQ", allocationSize = 1)
public class ValorDocumentalPrimario implements Serializable {

	/** */
	private static final long serialVersionUID = -4673924285258965321L;
	/** */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VALORDOCPRIMARIO")
	@Column(name = "identificador")
	private Integer id;
	/** */
	@Column(name = "descripcion")
	private String descripcion;
	/** */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValorDocumentalPrimario [id=" + id + ", descripcion=" + descripcion + ", activo=" + activo + "]";
	}

}
