package com.ecm.sigap.data.model.util;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum SignContentType {

	PDF("application/pdf/signed"), //
	OFICIO("text/xml/oficio");

	/** */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	SignContentType(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

}
