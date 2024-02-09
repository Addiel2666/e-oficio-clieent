/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import com.ecm.sigap.data.model.validator.UniqueKey;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * The Class FavoritoArea.
 *
 * @author Adan Quintero
 * @version 1.0
 *
 */
@Entity
@Table(name = "favAreas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "favoritoAreaKey.idArea",
		"favoritoAreaKey.idAreaFavorita" }, message = "{Unique.descripcion}")
public class FavoritoArea implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3863699503473951947L;

	/** The favorito area key. */
	@EmbeddedId
	private FavoritoAreaKey favoritoAreaKey;

	/**
	 * Gets the favorito area key.
	 *
	 * @return the favorito firmante key
	 */
	public FavoritoAreaKey getFavoritoAreaKey() {
		return favoritoAreaKey;
	}

	/**
	 * Sets the favorito area key.
	 *
	 * @param favoritoAreaKey the new favorito area key
	 */
	public void setFavoritoAreaKey(FavoritoAreaKey favoritoAreaKey) {
		this.favoritoAreaKey = favoritoAreaKey;
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

		FavoritoArea tmp = (FavoritoArea) obj;

		try {

			if (tmp.favoritoAreaKey.getIdArea() == this.favoritoAreaKey.getIdArea()//
					&& tmp.favoritoAreaKey.getIdAreaFavorita() == this.favoritoAreaKey.getIdAreaFavorita()//
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
		return Objects.hash(this.favoritoAreaKey.getIdArea(), this.favoritoAreaKey.getIdAreaFavorita());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FavoritoArea{" + "favoritoAreaKey=" + favoritoAreaKey + '}';
	}
}
