/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
public enum BitacoraGrupo {

	ASUNTO_CREACION(1), //

	ASUNTO_ULTIMA_MODIFICACION(5), //

	TRAMITE_ULTIMA_MODIFICACION(3), //

	DOCUMENTO_ASUNTO_DEPURADO(24), //

	DOCUMENTO_RESPUESTA_DEPURADO(25)//

	;

	/** */
	private final int t;

	/**
	 * 
	 * @param t
	 */
	BitacoraGrupo(int t) {
		this.t = t;
	}

	/**
	 * 
	 * @return
	 */
	public int getGrupo() {
		return this.t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return Integer.toString(t);
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static BitacoraGrupo fromString(int t) {

		if (t == 1)
			return BitacoraGrupo.ASUNTO_CREACION;

		else if (t == 5)
			return BitacoraGrupo.ASUNTO_ULTIMA_MODIFICACION;

		else if (t == 3)
			return BitacoraGrupo.TRAMITE_ULTIMA_MODIFICACION;

		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
