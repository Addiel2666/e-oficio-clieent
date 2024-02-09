/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
public enum TipoNotificacion {

	RECRESPUESTA("RESPUESTA"), // Envia, Rechazo

	RECTURNO("TRAMITE"), // Enviado,

	RECTURNORECH("TRAMITE_RECHAZADO"), // Tramite Rechazado

	RECETFTP("ASUNTOS_POR_VENCER"), // JOB

	RECETFTF("ASUNTOS_VENCIDOS"), // JOB

	REOFICIO("REVISION_OFICIO"), // para resisio y vuando, revisado, genara
									// borrador en otro firmante

	RECDOCFIR("DOCUMENTO_FIRMA"), // le llega a todos los del Area menos al
									// Titular

	RECDOCANTEFIR("DOCUMENTO_ANTEFIRMA"), // le llega a titular y
											// administradores del area.

	RECANTEFIRMAREC("DOCUMENTO_ANTEFIRMA_RECHAZADO"), // le llega al remitente

	PAGINAINICIAL("PAGINAINICIAL"), // pagina inicial en sigap 4

	RECDOCPFIR("DOCUMENTO_PARA_FIRMA"), // Le llega solo al titular

	FIRMA_FALLIDA("FIRMA_FALLIDA"), // sucedio un error la firmar un documento en
									// segundo plano.
	DOC_PARA_ANTEFIRMA("DOC_PARA_ANTEFIRMA"), // Le llega al usuario que se selecciono para antefirmar.

	DOC_CANCELADO("DOCUMENTO_CANCELADO") // Le llega a los usuarios titulares de las areas.

	;

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	TipoNotificacion(String t) {
		this.t = t;
	}

	/**
	 * 
	 * @return
	 */
	public String getTipo() {
		return this.t;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static TipoNotificacion fromString(String t) {
		if (t != null)
			for (TipoNotificacion tipo_ : TipoNotificacion.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
