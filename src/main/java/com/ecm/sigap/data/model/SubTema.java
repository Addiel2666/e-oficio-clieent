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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class SubTema.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "tSubTemas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_SUBTEMA", sequenceName = "SECOBJETOS", allocationSize = 1)
@UniqueKey(columnNames = { "tema.idTema", "area.idArea", "descripcion" }, message = "{Unique.descripcion}")
public class SubTema implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4979633186057753675L;

	/** The id sub tema. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SUBTEMA")
	@Column(name = "idSubTema")
	private Integer idSubTema;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** The area. */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private Area area;

	/** The tema. */
	@OneToOne
	@JoinColumn(name = "idTema")
	@Fetch(FetchMode.SELECT)
	private Tema tema;

	/**
	 * Gets the id sub tema.
	 *
	 * @return the id sub tema
	 */
	public Integer getIdSubTema() {
		return idSubTema;
	}

	/**
	 * Sets the id sub tema.
	 *
	 * @param idSubTema the new id sub tema
	 */
	public void setIdSubTema(Integer idSubTema) {
		this.idSubTema = idSubTema;
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
	 * Gets the tema.
	 *
	 * @return the tema
	 */
	public Tema getTema() {
		return tema;
	}

	/**
	 * Sets the tema.
	 *
	 * @param tema the new tema
	 */
	public void setTema(Tema tema) {
		this.tema = tema;
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
		return "SubTema [idSubTema=" + idSubTema + ", descripcion=" + descripcion + ", activo=" + activo + ", area="
				+ area + ", tema=" + tema + "]";
	}

}
