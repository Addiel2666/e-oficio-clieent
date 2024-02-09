/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The Class AreaRevisorKey.
 */
@Embeddable
public class AccesoKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -29032244418261578L;

	/** The area. */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private Area area;

	/** The rol. */
	@OneToOne
	@JoinColumn(name = "idRol")
	@Fetch(FetchMode.SELECT)
	private Rol rol;

	/** The id usuario. */
	@Column(name = "idUsuario")
	private String idUsuario;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccesoKey [area=" + area + ", idRol=" + rol + ", idUsuario=" + idUsuario + "]";
	}

	/**
	 * @return the area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * @return the rol
	 */
	public Rol getRol() {
		return rol;
	}

	/**
	 * @param rol the rol to set
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}

	/**
	 * @return the idUsuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @param idUsuario the idUsuario to set
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
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

		if (!(obj instanceof AccesoKey)) {
			return false;
		}

		if (!AccesoKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		AccesoKey tmp = (AccesoKey) obj;

		try {

			if (tmp.hashCode() == this.hashCode())
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
		return Objects.hash(this.area, this.idUsuario, this.rol);
	}

}
