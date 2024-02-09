/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.exception;

/**
 * Esta clase es una sub-clase de las exepciones que maneja el sistema y se esta
 * relacionada a la Firma del Documento/ Mensaje de Interoperabilidad.
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha 11-Dic-2012
 * @see com.ecm.sigap.interoperabilidad.exception.ApplicationException
 */
public class CertificateUtilityException extends WsSecurityException {

	private static final long serialVersionUID = -3785011661639455284L;

	/**
	 * Contruye una nueva exepcion con el detalle en <t>null</t>
	 */
	public CertificateUtilityException() {

		super();
	}

	/**
	 * Construye una nueva exepcion con el detalle del error
	 * 
	 * @param message
	 *            Detalle del Error
	 */
	public CertificateUtilityException(String message) {

		super(message);
	}

	/**
	 * Contruye una nueva exepcion con la causa de la misma
	 * 
	 * @param cause
	 *            Causa de la exepcion
	 */
	public CertificateUtilityException(Throwable cause) {

		super(cause);
	}

	/**
	 * Contruye una nueva exepcion con el detalle y la causa de la misma
	 * 
	 * @param message
	 *            Detalle del Error
	 * @param cause
	 *            Causa de la exepcion
	 */
	public CertificateUtilityException(String message, Throwable cause) {

		super(message, cause);
	}
}
