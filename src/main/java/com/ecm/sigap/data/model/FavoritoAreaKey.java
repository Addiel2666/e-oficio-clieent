/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The Class FavoritoAreaKey.
 */
@Embeddable
public class FavoritoAreaKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3053203706769116073L;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The id area favorita. */
	@Column(name = "idAreaFavorita")
	private Integer idAreaFavorita;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getIdArea() {
		return idArea;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public Integer getIdAreaFavorita() {
		return idAreaFavorita;
	}

	public void setIdAreaFavorita(Integer idAreaFavorita) {
		this.idAreaFavorita = idAreaFavorita;
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

		if (!FavoritoAreaKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		FavoritoAreaKey tmp = (FavoritoAreaKey) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idAreaFavorita == this.idAreaFavorita//
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
		return Objects.hash(this.idArea, this.idAreaFavorita);
	}

	@Override
	public String toString() {
		return "FavoritoAreaKey{" + "idArea=" + idArea + ", idAreaFavorita=" + idAreaFavorita + '}';
	}
}
