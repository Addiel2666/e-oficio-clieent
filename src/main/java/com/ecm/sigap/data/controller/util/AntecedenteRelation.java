/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.util;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class AntecedenteRelation {

	/** */
	private String folioArea;
	/** */
	private Integer idAsunto;

	/**
	 * @return the folioArea
	 */
	public String getFolioArea() {
		return folioArea;
	}

	/**
	 * @param folioArea
	 *            the folioArea to set
	 */
	public void setFolioArea(String folioArea) {
		this.folioArea = folioArea;
	}

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto
	 *            the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Antecedente [folioArea=" + folioArea + ", idAsunto=" + idAsunto + "]";
	}

}
