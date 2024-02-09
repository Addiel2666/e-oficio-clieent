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
public enum TipoGrupoEnvio {

	/**  */
	PRIVADO("PRIVADO"), //

	PUBLICO("PUBLICO");

	/** Tipo */
	private final String value;

	/**
	 * Contructor de la clase
	 * 
	 * @param value
	 */
	TipoGrupoEnvio(String value) {
		this.value = value;
	}

	/**
	 * Retorna el Tipo de Asunto
	 * 
	 * @return
	 */
	public String getValue() {

		return value;
	}

	/**
	 * Convierte del valor a tipo Enumerado
	 * 
	 * @param t
	 *            valor del Tipo del Asunto
	 * @return Tipo Enumerado
	 */
	public static TipoGrupoEnvio fromTipo(String t) {
		if (t != null) {
			for (TipoGrupoEnvio tipo_ : TipoGrupoEnvio.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		}
		return null;
	}
}
