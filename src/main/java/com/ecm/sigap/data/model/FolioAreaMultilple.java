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
 * @author ECM Solutions
 * @version 1.0
 */
@Entity
@Table(name = "foliosAreaMultiple")
@NamedNativeQueries(value = {
		// NUMERO DE DOCUMENTO FOLIADORA MULTIPLE
		@NamedNativeQuery(name = "generaNumDoctoMultiple", //
				query = " select {SIGAP_SCHEMA}.FOLIOSMULTIPLES_FSEL(:idFoliopsMultiple, :idTipo) from dual ")
})
public class FolioAreaMultilple implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5726793148799176806L;

	/** The folio key. */
	@EmbeddedId
	private FolioAreaKeyMultiple folioAreaKeyMul;

	/** The folio. */
	@Column(name = "folio", nullable = false, precision = 38, scale = 0)
	private Integer folio;

	/** The vlock. */
	@Column(name = "vlock", length = 15)
	private String vlock;

	/**
	 * Instantiates a new folio area.
	 */
	public FolioAreaMultilple() {
		super();
	}

	/**
	 * 
	 * @param folioAreaKeyMul
	 * @param folio
	 * @param vlock
	 */
	public FolioAreaMultilple(FolioAreaKeyMultiple folioAreaKeyMul, Integer folio, String vlock) {
		super();
		this.folioAreaKeyMul = folioAreaKeyMul;
		this.folio = folio;
		this.vlock = vlock;
	}

	/**
	 * @return the folioAreaKeyMul
	 */
	public FolioAreaKeyMultiple getFolioAreaKeyMul() {
		return folioAreaKeyMul;
	}

	/**
	 * @param folioAreaKeyMul
	 *            the folioAreaKeyMul to set
	 */
	public void setFolioAreaKeyMul(FolioAreaKeyMultiple folioAreaKeyMul) {
		this.folioAreaKeyMul = folioAreaKeyMul;
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
	 * @param folio
	 *            the new folio
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
	 * @param vlock
	 *            the new vlock
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
		return "FolioArea [folioAreaKeyMul=" + folioAreaKeyMul + ", folio=" + folio + ", vlock=" + vlock + "]";
	}

}
