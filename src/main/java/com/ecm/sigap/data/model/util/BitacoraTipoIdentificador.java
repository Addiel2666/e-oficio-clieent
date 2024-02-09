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
public enum BitacoraTipoIdentificador {

	U("U"), // USUARIO
	R("R"), // RESPUESTA
	P("P"), //
	A("A"), // ASUNTO TIPO_ASUNTO
	T("T"), // ASUNTO TIPO_TURNO
	C("C"), // ASUNTO TIPO_COPIA
	E("E"), // ASUNTO TIPO_ENVIO
	O("O"), // OTROS
	Z("Z"), // DOCUMENTO ASUNTO DEPURADO
	X("X") // DOCUMENTO RESPUESTA DEPURADO

	;

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	BitacoraTipoIdentificador(String t) {
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
	public static BitacoraTipoIdentificador fromString(String t) {
		if (t != null) {

			if ("TURNO".equalsIgnoreCase(t.trim()))
				return BitacoraTipoIdentificador.T;

			else if ("ASUNTO".equalsIgnoreCase(t.trim()))
				return BitacoraTipoIdentificador.A;

			else if ("ENVIO".equalsIgnoreCase(t.trim()))
				return BitacoraTipoIdentificador.E;

			else if ("COPIA".equalsIgnoreCase(t.trim()))
				return BitacoraTipoIdentificador.C;

			for (BitacoraTipoIdentificador tipo_ : BitacoraTipoIdentificador.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;

		}
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
