/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.interoperabilidad;

import java.net.MalformedURLException;
import java.util.List;

import mx.com.ecmsolutions.sigap.interoperabilidad.MensajeNoEnviado_Exception;

/**
 * Sevicio de interoperabilidad.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public interface InteroperabilidadService {

	/**
	 * @throws MensajeNoEnviado_Exception 
	 * @throws MalformedURLException 
	 * 
	 */
	public String generarDocumentoElectronico(String asunto, String tipoSolicitud, List<String> areasDestino,
			List<String> areasCopia) throws MalformedURLException, MensajeNoEnviado_Exception;

	/**
	 * @throws MensajeNoEnviado_Exception
	 * @throws MalformedURLException
	 * 
	 */
	public void registrarInstancia() throws MalformedURLException, MensajeNoEnviado_Exception;

	/**
	 * 
	 * @param asunto
	 * @param oficioElectronico
	 * @param tipoSolicitud
	 * @param areasDestino
	 * @param areasCopia
	 * @throws MalformedURLException
	 * @throws MensajeNoEnviado_Exception
	 */
	public void registrarOficioElectronico(String asunto, String oficioElectronico, String tipoSolicitud,
			List<String> areasDestino, List<String> areasCopia)
			throws MalformedURLException, MensajeNoEnviado_Exception;

	/**
	 * 
	 * @param institucionesDestino
	 * @param respuesta
	 * @throws MalformedURLException
	 * @throws MensajeNoEnviado_Exception
	 */
	public void respuestaSuscripcionInstancias(List<String> institucionesDestino, boolean respuesta)
			throws MalformedURLException, MensajeNoEnviado_Exception;

	/**
	 * 
	 * @throws MalformedURLException
	 * @throws MensajeNoEnviado_Exception
	 */
	public void sincronizarDirectorioCompleto() throws MalformedURLException, MensajeNoEnviado_Exception;

	/**
	 * @throws MensajeNoEnviado_Exception
	 * @throws MalformedURLException
	 * 
	 */
	public void sincronizarDirectorioParcial() throws MalformedURLException, MensajeNoEnviado_Exception;

	/**
	 * 
	 * @param list
	 * @throws MalformedURLException
	 * @throws MensajeNoEnviado_Exception
	 */
	public void SolicitarSuscripcionInstancias(List<String> list)
			throws MalformedURLException, MensajeNoEnviado_Exception;

}
