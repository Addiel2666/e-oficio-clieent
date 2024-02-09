/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "folioArchivistica")
public final class FolioArchivistica implements Serializable {

	/** */
	private static final long serialVersionUID = -1749037698650029203L;
	/** */
	@Id
	@Column(name = "idArea", nullable=false)
	private Integer idArea;
	/** */
	@Column(name = "prefijo")
	private String prefijo;
	/** */
	@Column(name = "sufijo")
	private String sufijo;
	/** */
	@Column(name = "folio", nullable = false)
	private Integer folio;

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea
	 *            the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the prefijo
	 */
	public String getPrefijo() {
		return prefijo;
	}

	/**
	 * @param prefijo
	 *            the prefijo to set
	 */
	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	/**
	 * @return the sufijo
	 */
	public String getSufijo() {
		return sufijo;
	}

	/**
	 * @param sufijo
	 *            the sufijo to set
	 */
	public void setSufijo(String sufijo) {
		this.sufijo = sufijo;
	}

	/**
	 * @return the folio
	 */
	public Integer getFolio() {
		return folio;
	}

	/**
	 * @param folio
	 *            the folio to set
	 */
	public void setFolio(Integer folio) {
		this.folio = folio;
	}

}
