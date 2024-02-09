/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

/**
 * The Class Folio.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
/**
 * The Class FolioArea.
 */
@Entity
@Table(name = "folios")
@NamedNativeQueries(value = {

		// ACTUALIZA FOLIO
		@NamedNativeQuery(name = "foliosPorArea", //
				query = " call {SIGAP_SCHEMA}.FOLIOSXAREA (:idAreaDestino, :idAreaOrigen)"),

		@NamedNativeQuery(name = "foliosPorArea2", //
				query = "select {SIGAP_SCHEMA}.FOLIOSXAREA(:idAreaDestino, :idAreaOrigen) ")

})
public class Folio implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4617970488586872310L;

	/** The folio key. */
	@EmbeddedId
	private FolioKey folioKey;

	/** The vlock. */
	@Column(name = "vlock", length = 15)
	private String vlock;

	/**
	 * Gets the folio key.
	 *
	 * @return the folio key
	 */
	public FolioKey getFolioKey() {
		return folioKey;
	}

	/**
	 * Sets the folio key.
	 *
	 * @param folioKey the new folio key
	 */
	public void setFolioKey(FolioKey folioKey) {
		this.folioKey = folioKey;
	}

	/**
	 * Gets the vlock.
	 *
	 * @return the vlock
	 */
	public String getVlock() {
		return vlock;
	}

	/**
	 * Sets the vlock.
	 *
	 * @param vlock the new vlock
	 */
	public void setVlock(String vlock) {
		this.vlock = vlock;
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
		return "Folio [folioKey=" + folioKey + ", vlock=" + vlock + "]";
	}

}
