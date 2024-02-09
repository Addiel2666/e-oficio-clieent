/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

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
 * The Class Tipo.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "tipos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_TIPO", sequenceName = "SECOBJETOS", allocationSize = 1)
public class Tipo implements Serializable {

	/** */
	private static final long serialVersionUID = 3880266225174971385L;

	/** */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TIPO")
	@Column(name = "idTipo")
	private Integer idTipo;

	/** */
	@Column(name = "descripcion")
	private String descripcion;

	/** */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * @return the idTipo
	 */
	public Integer getIdTipo() {
		return idTipo;
	}

	/**
	 * @param idTipo the idTipo to set
	 */
	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tipo [idTipo=" + idTipo + ", descripcion=" + descripcion + ", activo=" + activo + "]";
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

		if (!(obj instanceof Tipo)) {
			return false;
		}

		if (!Tipo.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		Tipo tmp = (Tipo) obj;

		try {

			if (tmp.idTipo == this.idTipo//
					&& tmp.descripcion.equals(this.descripcion) //
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
		return Objects.hash(this.idTipo, this.descripcion);
	}
}
