/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * Clase Enumerado que representa los Tipos de Asuntos que tiene el sistema
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoAsunto {

	/** Asunto */
	ASUNTO("A"),

	/** Turno */
	TURNO("T"),

	/** Envio */
	ENVIO("E"),

	/** Copia */
	COPIA("C");

	/** Tipo de Asunto */
	private final String value;

	/**
	 * Contructor de la clase
	 * 
	 * @param value
	 */
	TipoAsunto(String value) {

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
	public static TipoAsunto fromTipo(String t) {
		if (t != null) {
			for (TipoAsunto tipo_ : TipoAsunto.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		}
		return null;
	}
}
