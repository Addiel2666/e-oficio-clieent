/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.client.exception;

/**
 * Excepcion en caso de que el hash del mensaje firmado no coincida con el hash
 * del body del request,
 * 
 * @author Alfredo Morales
 *
 */
public class HashInSignatureNoMatchException extends Exception {

	/** */
	private static final long serialVersionUID = 3219466429522340631L;

}
