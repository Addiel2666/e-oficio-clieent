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
public enum TipoAuditoria {

	DELETE("Delete"), //
	UPDATE("Update"), //
	SAVE("Save"), //
	//////////////////////////////////
	REJECT("Reject"), // Rechazado
	SEND("Send"), // Enviado
	CONCLUDE("Conclude"), // Concluido
	CANCEL("Cancel"), // Cancelado
	ACCEPT("Accept"), // Aceptado
	INACTIVE("Inactive"), // Inactivo
	
	// Inicio Bitacora
	ACTIVE("Active"), // Activo
	ACCESSA("AccessA"), // Acceso usuario alta
	ACCESSD("AccessD"), // Acceso usuario delete
	// ---
	UPDATEBITACORA("UpdateBitacora"), // Alternativa registro de bitacora
	DOCLOCK("DocLock"),	// Documento bloqueado
	DOCUUNLOCK("DocUnlock"), // Documento desbloqueado
	DOCSIGNED("DocSigned"), // Documento firmado
	DOCMARKEDF("DocMarkedF"), // Documento marcado para firma
	DOCMARKEDAF("DocMarkedAF"), // Documento marcado para antefirma
	DOCSENDF("DocSendAF"), // Documento enviado para antefirma
	DELETEVERSION("DeleteVersion"), // Documento eliminado por versionamiento
	VERSION("Version"); // Documento versionado
	// Fin Bitacora

	/** */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	TipoAuditoria(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

}
