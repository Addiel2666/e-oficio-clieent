/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * 
 * @author Adaulfo Herrera
 * @version 1.0
 *
 */
public enum EnTiempo {

	EN_TIEMPO("E"),

	FUERA_DE_TIEMPO("F"),

	POR_VENCER("P");

	/** Valor de la Base de Datos */
	private final String t;

	/**
	 * Asigna el valor del estado del Asunto
	 * 
	 * @param t valor del estado del Asunto
	 */
	EnTiempo(String t) {

		this.t = t;
	}

	/**
	 * Devuelve la descripcion del estado del Asunto
	 * 
	 * @return Descripcion del Estado
	 */
	public String getTipo() {

		return this.t;
	}

	/**
	 * Convierte del valor a tipo Enumerado
	 * 
	 * @param t valor del estado del Asunto
	 * @return Tipo Enumerado
	 */
	public static EnTiempo fromTipo(String t) {
		if (t != null) {
			for (EnTiempo tipo_ : EnTiempo.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		} else {
			return EN_TIEMPO;
		}
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
