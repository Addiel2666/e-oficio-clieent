/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.EmbeddedId;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class FavoritoFirmante.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "favFirmantes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
//@UniqueKey(columnNames = { "favoritoFirmanteKey.idArea", "favoritoFirmanteKey.firmante.id",
//		"favoritoFirmanteKey.firmArea.idArea" }, message = "{Unique.descripcion}")
public class FavoritoFirmante implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3863699503473951947L;

	/** The favorito firmante key. */
	@EmbeddedId
	private FavoritoFirmanteKey favoritoFirmanteKey;

	/**
	 * Gets the favorito firmante key.
	 *
	 * @return the favorito firmante key
	 */
	public FavoritoFirmanteKey getFavoritoFirmanteKey() {
		return favoritoFirmanteKey;
	}

	/**
	 * Sets the favorito firmante key.
	 *
	 * @param favoritoFirmanteKey
	 *            the new favorito firmante key
	 */
	public void setFavoritoFirmanteKey(FavoritoFirmanteKey favoritoFirmanteKey) {
		this.favoritoFirmanteKey = favoritoFirmanteKey;
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
		return "FavoritoFirmante [favoritoFirmanteKey=" + favoritoFirmanteKey + "]";
	}
}
