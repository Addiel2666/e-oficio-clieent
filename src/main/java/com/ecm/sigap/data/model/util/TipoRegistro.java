/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * Clase Enumerado que representa los Tipos de registros que tiene el sistema
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoRegistro {

	/** Asunto / Turnos de Control de Gestion */
	CONTROL_GESTION("C"),

	/** Asuntos Infomex */
	INFOMEX("T");

	/** Tipo de Asunto */
	private final String value;

	/**
	 * Contructor de la clase
	 * 
	 * @param value
	 */
	TipoRegistro(String value) {

		this.value = value;
	}

	/**
	 * Retorna el Tipo de registro
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
	 *            valor del Tipo de Registro
	 * @return Tipo Enumerado
	 */
	public static TipoRegistro fromTipo(String t) {
		if (t != null) {
			for (TipoRegistro tipo_ : TipoRegistro.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		}
		throw null;
	}
}
