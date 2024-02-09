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
 * The Class TipoEvento.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "tiposEvento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_TIPOEVENTO", sequenceName = "SECOBJETOS", allocationSize = 1)
@UniqueKey(columnNames = { "area.idArea", "descripcion" }, message = "{Unique.descripcion}")
public class TipoEvento implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2295173749796666893L;

	/** The id evento. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TIPOEVENTO")
	@Column(name = "idEvento")
	private Integer idEvento;

	/** The id area. */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private Area area;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/**
	 * Gets the id evento.
	 *
	 * @return the id evento
	 */
	public Integer getIdEvento() {
		return idEvento;
	}

	/**
	 * Sets the id evento.
	 *
	 * @param idEvento the new id evento
	 */
	public void setIdEvento(Integer idEvento) {
		this.idEvento = idEvento;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area the new area
	 */
	public void setArea(Area area) {
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
		return "TipoEvento [idEvento=" + idEvento + ", area=" + area + ", descripcion=" + descripcion + ", activo="
				+ activo + "]";
	}

}
