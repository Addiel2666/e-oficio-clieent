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

// TODO: Auto-generated Javadoc
/**
 * The Class FolioArea.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "foliosArea")
@NamedNativeQueries(value = {

		// ACTUALIZA FOLIO
		@NamedNativeQuery(name = "actualizaFolio", //
				query = " call FOLIOS_UPD (:idArea, :folio)"),

		@NamedNativeQuery(name = "actualizaFolio2", //
				query = "select {SIGAP_SCHEMA}.folios_upd(:idArea, :folio) ")
		
})
public class FolioArea implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5726793148799176806L;

	/** The folio key. */
	@EmbeddedId
	private FolioAreaKey folioAreaKey;

	/** The folio. */
	@Column(name = "folio", nullable = false, precision = 38, scale = 0)
	private Integer folio;

	/** The vlock. */
	@Column(name = "vlock", length = 15)
	private String vlock;

	/**
	 * Instantiates a new folio area.
	 */
	public FolioArea() {
		super();
	}

	/**
	 * Instantiates a new folio area.
	 *
	 * @param folioAreaKey the folio area key
	 * @param folio        the folio
	 * @param vlock        the vlock
	 */
	public FolioArea(FolioAreaKey folioAreaKey, Integer folio, String vlock) {
		super();
		this.folioAreaKey = folioAreaKey;
		this.folio = folio;
		this.vlock = vlock;
	}

	/**
	 * Gets the folio area key.
	 *
	 * @return the folio area key
	 */
	public FolioAreaKey getFolioAreaKey() {
		return folioAreaKey;
	}

	/**
	 * Sets the folio area key.
	 *
	 * @param folioAreaKey the new folio area key
	 */
	public void setFolioAreaKey(FolioAreaKey folioAreaKey) {
		this.folioAreaKey = folioAreaKey;
	}

	/**
	 * Gets the folio.
	 *
	 * @return the folio
	 */
	public Integer getFolio() {
		return folio;
	}

	/**
	 * Sets the folio.
	 *
	 * @param folio the new folio
	 */
	public void setFolio(Integer folio) {
		this.folio = folio;
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
		return "FolioArea [folioAreaKey=" + folioAreaKey + ", folio=" + folio + ", vlock=" + vlock + "]";
	}

}
