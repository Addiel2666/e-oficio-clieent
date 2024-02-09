/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * The Class FileBase64.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
public class FileBase64 {

	/** The string base64. */
	private String stringBase64;

	/** The file name. */
	private String fileName;

	/**
	 * Gets the string base64.
	 *
	 * @return the string base64
	 */
	public String getStringBase64() {
		return stringBase64;
	}

	/**
	 * Sets the string base64.
	 *
	 * @param stringBase64
	 *            the new string base64
	 */
	public void setStringBase64(String stringBase64) {
		this.stringBase64 = stringBase64;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName
	 *            the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileBase64 [stringBase64=" + stringBase64 + ", fileName=" + fileName + "]";
	}

}
