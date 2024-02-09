package com.ecm.sigap.data.model.util;

import java.util.ResourceBundle;

/**
 * Clase enumerada que representa los tipos de Acuses
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoTimestamp {

	/** Registro */
	TIMESTAMP_REGISTRO(0),

	/** Acuse de Recepcion o Rechazo */
	TIMESTAMP_ACUSE_RECEPCION_RECHAZO(1),

	/** Envio */
	TIMESTAMP_ENVIO(2),

	/** Atencion */
	TIMESTAMP_ATENCION(2),

	/** Conclusion */
	TIMESTAMP_CONCLUCION(3);

	/** Identificador del tipo de Asunto */
	private final int i;

	/**
	 * Full constructor de la clase
	 * 
	 * @param i Identificador del tipo de Acuse
	 */
	private TipoTimestamp(int i) {
		this.i = i;
	}

	/**
	 * Obtiene el Identificador del tipo de Acuse
	 * 
	 * @return Identificador del tipo de Acuse
	 */
	public int getTipo() {
		return i;
	}

	/**
	 * Obtiene el Tipo de Acuse
	 * 
	 * @param t Identificador del tipo de Acuse
	 * @return Tipo de Acus
	 */
	public static TipoTimestamp fromString(int t) {
		for (TipoTimestamp tipo_ : TipoTimestamp.values())
			if (t == tipo_.i)
				return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

	/** */
	private static final ResourceBundle firmaConfig = ResourceBundle.getBundle("firmaDigital");

	/**
	 * Dependiendo del tipo de timestamp devuelve el parametro de configuracion
	 * correspondiente,
	 * 
	 * @return
	 */
	public String getTipoString() {

		if (i == TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO.i)
			return firmaConfig.getString("timestamp.acuse_recepcion_rechazo");

		else if (i == TipoTimestamp.TIMESTAMP_ATENCION.i)
			return firmaConfig.getString("timestamp.atencion");

		else if (i == TipoTimestamp.TIMESTAMP_CONCLUCION.i)
			return firmaConfig.getString("timestamp.conclucion");

		else if (i == TipoTimestamp.TIMESTAMP_ENVIO.i)
			return firmaConfig.getString("timestamp.envio");

		else if (i == TipoTimestamp.TIMESTAMP_REGISTRO.i)
			return firmaConfig.getString("timestamp.registro");

		return "";
	}

}
