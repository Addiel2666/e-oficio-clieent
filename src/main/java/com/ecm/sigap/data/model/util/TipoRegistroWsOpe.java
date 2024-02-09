/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * Clase Enumerado que representa los tipo de registros que tienen
 * wssincronizacompletadetalle de interoperabilidad
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
public enum TipoRegistroWsOpe {

	/** Institucion */
	REGISTRO_INSTITUCION("I"),

	/** Area */
	REGISTRO_AREA("A"),

	/** Usuario */
	REGISTRO_USUARIO("U");

	/** Tipo Registro */
	private final String value;

	/**
	 * Contructor de la clase
	 * 
	 * @param value
	 */
	TipoRegistroWsOpe(String value) {

		this.value = value;
	}

	/**
	 * Retorna el tipo de registro
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
	 *            valor del tipo de registro
	 * @return Tipo Enumerado
	 */
	public static TipoRegistroWsOpe fromTipo(String t) {
		if (t != null) {
			for (TipoRegistroWsOpe tipo_ : TipoRegistroWsOpe.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		}
		return null;
	}
}
