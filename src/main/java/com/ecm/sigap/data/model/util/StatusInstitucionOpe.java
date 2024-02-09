/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * Clase Enumerado que representa los estatus que tienen una institución de
 * interoperabilidad
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
public enum StatusInstitucionOpe {

	/** Directorio */
	DIRECTORIO("D"),

	/** Por aceptar */
	POR_ACEPTAR("S"),

	/** Solicitud enviada */
	SOLICITUD_ENVIADA("P"),

	/** Vinculada */
	VINCULADA("M"),

	/** Rechazada */
	RECHAZADA("R"),

	/** Aceptada */
	ACEPTADA("A");

	/** Status Intitucion_OPE */
	private final String value;

	/**
	 * Contructor de la clase
	 * 
	 * @param value
	 */
	StatusInstitucionOpe(String value) {

		this.value = value;
	}

	/**
	 * Retorna el Status de la Intitucion_OPE
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
	 *            valor del Stauts Intitucion_OPE
	 * @return Tipo Enumerado
	 */
	public static StatusInstitucionOpe fromTipo(String t) {
		if (t != null) {
			for (StatusInstitucionOpe tipo_ : StatusInstitucionOpe.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		}
		return null;
	}
}
