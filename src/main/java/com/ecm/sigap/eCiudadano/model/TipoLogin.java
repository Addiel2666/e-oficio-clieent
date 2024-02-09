/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eCiudadano.model;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoLogin {

	/** */
	Ciudadano("C"),
	/** */
	Empresa("E"),
	/** */
	RepresentanteLegal("R");

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	TipoLogin(String t) {
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
	public static TipoLogin fromString(String t) {
		if (t != null)
			for (TipoLogin tipo_ : TipoLogin.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
