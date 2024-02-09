/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum SubTipoAsunto {

	/** Representante Legal */
	R("R"),

	/** Ciudadano */
	D("D"),

	/** Funcionarios Internos */
	C("C"),

	/** Infomex */
	T("T"),

	/** Funcionarios Externos */
	F("F");

	/** */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	SubTipoAsunto(String value) {
		this.value = value;
	}

	/**
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
	 *            valor del SubTipo del Asunto
	 * @return Tipo Enumerado
	 */
	public static SubTipoAsunto fromTipo(String t) {
		if (t != null) {
			for (SubTipoAsunto tipo_ : SubTipoAsunto.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		} else {
			return null;
		}
		throw new IllegalArgumentException("No existe un Subtipo de asunto " + t);
	}
}
