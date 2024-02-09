/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.firma.model;

/**
 * @author hugo hernandez
 * @version 1.0
 *
 */
public class FirmaImpresaTemplate {

	/** */
	private String label;
	/** */
	private String value;
	/** */
	private boolean isPDF;

	/**
	 * 
	 * @param label
	 * @param value
	 * @param isPDF
	 */
	public FirmaImpresaTemplate(String label, String value, boolean isPDF) {
		this.label = label;
		this.value = value;
		this.isPDF = isPDF;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPDF() {
		return isPDF;
	}

	/**
	 * 
	 * @param isPDF
	 */
	public void setPDF(boolean isPDF) {
		this.isPDF = isPDF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FirmaImpresaTemplate [label=" + label + ", value=" + value + ", isPDF=" + isPDF + "]";
	}

}
