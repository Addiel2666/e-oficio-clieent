/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.client;

import com.ecm.sigap.ope.model.ResponseAsuntoTramite;
import com.ecm.sigap.ope.model.ResponseConfirmarSubscripcion;
import com.ecm.sigap.ope.model.ResponseRecibirSubscripcion;
import com.ecm.sigap.ope.model.ResponseSincronizacionCompleta;
import com.ecm.sigap.ope.model.ResponseVersionCatalogo;

/**
 * Endpoints de la Oficina Postal Electronica,
 * 
 * @author Alfredo Morales
 *
 */
public interface OpeClient {

	/**
	 * Solicitar registro de instancia para interoperar,
	 * 
	 * @param nombre      Descripcion o nombre con el que se mostrara el registro,
	 * @param nombreCorte Nombre corto o Identificador para sucesivas operaciones,
	 *                    sin espacios o caracteres especiales,
	 * @param url         Direccion URL donde se encuentran el solicitante,
	 * @throws Exception
	 */
	public ResponseRecibirSubscripcion solicitarSubscripcion(String nombre, String nombreCorte, String url)
			throws Exception;

	/**
	 * 
	 * @param id
	 * @param nombreCorte
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ResponseSincronizacionCompleta solicitarSincronizacionCompleta(String nombre, String nombreCorte, String url)
			throws Exception;

	/**
	 * 
	 * @param id
	 * @param nombreCorte
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ResponseConfirmarSubscripcion confirmarSubscripcion(String id, String nombreCorte, String url)
			throws Exception;

	/**
	 * Obtener el numero de version del catalogo actual del cliente indicado,
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ResponseVersionCatalogo obtenerVersionCatalogo(String url) throws Exception;
	
	public ResponseAsuntoTramite envioTramite(String url) throws Exception;

}
