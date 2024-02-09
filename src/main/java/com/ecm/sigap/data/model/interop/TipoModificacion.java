/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.interop;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoModificacion {

	/** */
	TIPO_CAMBIO_MODIFICACION("1"),
	/** */
	TIPO_CAMBIO_BAJA("2"),
	/** */
	TIPO_CAMBIO_ALTA("3");

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	TipoModificacion(String t) {
		this.t = t;
	}

	/**
	 * 
	 * @return
	 */
	public String getTipo() {
		return this.t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.t;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static TipoModificacion fromString(String t) {
		if (t != null)
			for (TipoModificacion tipo_ : TipoModificacion.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
