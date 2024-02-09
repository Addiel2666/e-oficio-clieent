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
public enum StatusFirmaDocumento {

	/** Documento Pendiente de Firma. */
	PARA_FIRMA("P"),

	/** Documento bloqueado, no puede ser firmado o marcado para firma. */
	BLOQUEADO("B"),

	/** al documento se le ha aplicado una firma. */
	FIRMADO("F"),

	/**
	 * al documento se le ha aplicado una firma pero fue marcado para firma de
	 * nuevo.
	 */
	PARA_FIRMA_Y_FIRMADO("G"),

	/** el archivo esta siendo firmado */
	FIRMANDO_ARCHIVO("X"),

	/** el archivo esta siendo antefirmado */
	ENVIO_ANTEFIRMA("E"),

	/** el archivo esta siendo antefirmado y fue marcado para firma */
	ENVIO_ANTEFIRMA_Y_PARA_FIRMA("H"),

	/** auxiliar para indicar que se elimino firmante de antefirma */
	AUX_QUITA_ENVIO_ANTEFIRMA("O"),

	/** el archivo esta siendo antefirmado y fue firmado */
	ENVIO_ANTEFIRMA_Y_FIRMADO("K"),

	/** el archivo fue firmado, esta siendo antefirmado y fue marcado para firma */
	ENVIO_ANTEFIRMA_PARA_FIRMA_Y_FIRMADO("J"),

	/** auxiliar para indicar que se desbloquea el doc */
	AUX_QUITA_BLOQUEO("W"),

	/** el archivo fue firmado y se bloqueo */
	FIRMADO_Y_BLOQUEADO("L");

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	StatusFirmaDocumento(String t) {
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
	public static StatusFirmaDocumento fromString(String t) {
		if (t != null)
			for (StatusFirmaDocumento tipo_ : StatusFirmaDocumento.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
