/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * The Class AreaRevisorKey.
 */
@Embeddable
public class AreaRevisorKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -29032249518261578L;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The revisor. */
	@OneToOne(fetch = FetchType.EAGER, targetEntity = Representante.class)
	@JoinColumn(name = "idRevisor")
	//@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Representante revisor;

	/** The id usuario. */
	@Column(name = "idUsuario")
	private String idUsuario;

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the revisor.
	 *
	 * @return the revisor
	 */
	public Representante getRevisor() {
		return revisor;
	}

	/**
	 * Sets the revisor.
	 *
	 * @param revisor the new revisor
	 */
	public void setRevisor(Representante revisor) {
		this.revisor = revisor;
	}

	/**
	 * Gets the id usuario.
	 *
	 * @return the id usuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * Sets the id usuario.
	 *
	 * @param idUsuario the new id usuario
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "AreaRevisorKey [idArea=" + idArea + ", revisor=" + revisor + ", idUsuario=" + idUsuario + "]";
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
		if (!AreaRemitenteKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		AreaRevisorKey tmp = (AreaRevisorKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idUsuario.equals(this.idUsuario)//
					&& tmp.revisor == this.revisor//
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
		return Objects.hash(this.idArea, this.idUsuario, this.revisor);
	}

}
