/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoUsuario {

	I("Interno"), //
	E("Externo");

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	TipoUsuario(String t) {
		this.t = t;
	}

	/**
	 * 
	 * @return
	 */
	public String getTipo() {
		return this.t;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static TipoUsuario fromString(String t) {
		if (t != null)
			for (TipoUsuario tipo_ : TipoUsuario.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
