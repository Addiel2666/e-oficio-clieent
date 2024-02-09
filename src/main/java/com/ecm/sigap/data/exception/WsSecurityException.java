/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.exception;

/**
 * Esta clase es la Super Clase de todas las excepciones no relacionadas a los
 * Mensajes de Interoperabilidad, es decir, a las excepciones de la aplicacion
 * durante su ejecucion.
 * <p>
 * Esta relacionado con los errores que ocurren en el servidor al procesar un
 * Mensaje, que no es atribuible a un problema tipificado con otro c�digo de
 * error (Codigo de error <t>ErrorInterno<t>).
 * 
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha 09-Nov-2012
 * @see java.lang.Exception
 */
public class WsSecurityException extends Exception {

	private static final long serialVersionUID = 1874291937288354166L;

	/** Detalle del Error */
	private String message;

	/**
	 * Contruye una nueva exepcion con el detalle en <t>null</t>
	 */
	public WsSecurityException() {

		super();
	}

	/**
	 * Construye una nueva exepcion con el detalle del error
	 * 
	 * @param message
	 *            Detalle del Error
	 */
	public WsSecurityException(String message) {

		super(message);
		this.message = message;
	}

	/**
	 * Contruye una nueva exepcion con la causa de la misma
	 * 
	 * @param cause
	 *            Causa de la exepcion
	 */
	public WsSecurityException(Throwable cause) {

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
	public WsSecurityException(String message, Throwable cause) {

		super(message, cause);
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {

		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {

		return message;
	}
}
