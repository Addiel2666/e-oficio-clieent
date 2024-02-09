package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class Entidad.
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */
@Entity
@Table(name = "Entidades")
public class Entidad implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 782594882914568269L;

	/** The id. */
	@Id
	@Column(name = "identidad")
	private String id;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The abreviatura. */
	@Column(name = "abreviatura")
	private String abreviatura;



	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
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
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the abreviatura.
	 *
	 * @return the abreviatura
	 */
	public String getAbreviatura() {
		return abreviatura;
	}

	/**
	 * Sets the abreviatura.
	 *
	 * @param abreviatura
	 *            the abreviatura to set
	 */
	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	@Override
	public String toString() {
		return "Entidad [id=" + id + ", descripcion=" + descripcion + ", abreviatura=" + abreviatura + "]";
	}

}
