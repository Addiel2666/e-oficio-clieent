/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.util;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
public final class Order {

	/** */
	private String field;
	/** */
	private boolean desc;

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the desc
	 */
	public boolean isDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(boolean desc) {
		this.desc = desc;
	}

}
