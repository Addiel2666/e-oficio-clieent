/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class Area.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "areas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "descripcion", "claveDepartamental",
		"institucion.idInstitucion" }, message = "{Unique.descripcion}")
public final class Area2 implements Serializable {

	/** */
	private static final long serialVersionUID = -8355331643806172526L;

	/** The id area. */
	@Id
	@Column(name = "idArea", insertable = false)
	private Integer idArea;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The institucion. */
	@OneToOne
	@JoinColumn(name = "idInstitucion")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Institucion institucion;

	/** The id area padre. */
	@Column(name = "idAreaPadre")
	private Integer idAreaPadre;

	/** The titular. */
	@OneToOne
	@JoinColumn(name = "titularUsuario")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Representante titular;

	/** The titular cargo. */
	@Column(name = "titularCargo")
	private String titularCargo;

	/** area padre */
	@OneToOne(targetEntity = AreaPadre.class)
	@JoinTable(name = "AreasPadresOnly", //
			joinColumns = { @JoinColumn(name = "idArea") }, //
			inverseJoinColumns = //
			{ @JoinColumn(name = "idAreaPadre", insertable = false, updatable = false) })
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private AreaPadre areaPadre;

	/**
	 * Gets the id area.
	 *
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
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
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * Gets the id area padre.
	 *
	 * @return the id area padre
	 */
	public Integer getIdAreaPadre() {
		return idAreaPadre;
	}

	/**
	 * Gets the titular.
	 *
	 * @return the titular
	 */
	public Representante getTitular() {
		return titular;
	}

	/**
	 * Gets the titular cargo.
	 *
	 * @return the titularCargo
	 */
	public String getTitularCargo() {
		return titularCargo;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the areaPadre.
	 *
	 * @return the areaPadre
	 */
	public AreaPadre getAreaPadre() {
		return areaPadre;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idArea == null) ? 0 : idArea.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Area2 other = (Area2) obj;
		if (idArea == null) {
			if (other.idArea != null)
				return false;
		} else if (!idArea.equals(other.idArea))
			return false;
		return true;
	}

}
