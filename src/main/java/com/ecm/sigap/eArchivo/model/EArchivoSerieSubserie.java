/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo.model;

/**
 * 
 * @author
 * @version 1.0
 *
 */
public class EArchivoSerieSubserie {

	/** */
	private String serieSubserie;

	/**
	 * 
	 * @return
	 */
	public String getSerieSubserie() {
		return serieSubserie;
	}

	/**
	 * 
	 * @param serieSubserie
	 */
	public void setSerieSubserie(String serieSubserie) {
		this.serieSubserie = serieSubserie;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EArchivoSerieSubserie{" + "serieSubserie='" + serieSubserie + '\'' + '}';
	}
}
