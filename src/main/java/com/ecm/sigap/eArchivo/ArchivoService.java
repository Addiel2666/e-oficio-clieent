/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo;

import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.eArchivo.model.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servicios de e-archivo.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
public interface ArchivoService {

	/**
	 * @return
	 * @throws Exception
	 */
	public JSONObject getExpedientes(EArchivoExpediente expediente, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws Exception;

	/**
	 * @param unidad
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUnidad(EArchivoUnidad unidad, String userId) throws Exception;

	/**
	 * @param tipocatalogo
	 * @return
	 * @throws Exception
	 */
	public JSONObject getTipoCatalogo(EArchivoTipoDocCatalogo tipocatalogo, String userId) throws Exception;

	/**
	 * @throws Exception
	 */
	public void aplicarExpediente(String objectId, String expedienteId) throws Exception;

	/**
	 * @param documento
	 * @return
	 * @throws Exception
	 */
	public JSONArray glosarDocumento(EArchivoDocumento documento, DocumentoAsunto documentoAsunto, String userId,
			String claveDepartamental, String user_key, String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param area
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject findArea(EArchivoArea area, String userId) throws Exception;

	/**
	 * 
	 * @param exp
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject findProceso(EArchivoExpediente exp, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param serie
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSerie(EArchivoSerieSubserie serie, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param serie
	 * @param userId
	 * @param claveDepartamental
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSerieAcceso(EArchivoSerieSubserie serie, String userId, String claveDepartamental,
			String user_key, String contentUser, String authToken) throws Exception;
	
	/**
	 * 
	 * @param serie
	 * @param userId
	 * @param claveDepartamental
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSerieAccesoFolio(EArchivoSerieSubserie serie, String userId, String claveDepartamental,
			String user_key, String contentUser, String authToken);

	/**
	 * 
	 * @param subserie
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSubserie(EArchivoSerieSubserie subserie, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param subserie
	 * @param userId
	 * @param claveDepartamental
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSubserieAcceso(EArchivoSerieSubserie subserie, String userId, String claveDepartamental,
			String user_key, String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param exp
	 * @param
	 * @return
	 * @throws Exception
	 */
	public JSONObject findSerieSubserie(Object exp, Map<String, String> headers) throws Exception;

	/**
	 * 
	 * @param legajo
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONArray findLegajo(EArchivoLegajo legajo, String claveDepartamental, String userId, String user_key,
			String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param objectId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getStatusExpediente(String objectId, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception;

	/**
	 * 
	 * @param documento
	 * @param oficioDoc
	 * @param userId
	 * @param claveDepartamental
	 * @param user_key
	 * @param contentUser
	 * @param authToken
	 * @return
	 * @throws Exception
	 */
	public JSONArray glosarDocumentoMultipart(EArchivoDocumento documento, DocumentoAsunto oficioDoc, String userId,
			String claveDepartamental, String user_key, String contentUser, String authToken) throws Exception;

	/**
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUsuario(String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken, String idUsuario) throws Exception;
	
	/**
	 * @param fondo
	 * @return
	 * @throws Exception
	 */
	public JSONObject getFondo(String userId, String claveDepartamental, String user_key, String contentUser,
			String authToken, EArchivoFondoCuadro fondo);

	/**
	 * 
	 * @param serie subserie
	 * @param userId
	 * @param claveDepartamental
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSeriesSubseries(EArchivoSerieSubserie seriesubserie, String userId, String clave, String user_key,
			String contentUser, String authToken) throws Exception;
	
	/**
	 * 
	 * @param userId
	 * @param claveDepartamental
	 * @return JSONObject
	 */
	public JSONObject getCurrentAcceso(String userId, String claveDepartamental);
	
	/**
	 * 
	 * @param expediente
	 * @param claveDepartamental
	 * @param userId
	 * @param user_key
	 * @param contentUser
	 * @param authToken
	 * @return JSONObject
	 * @throws Exception
	 */
	public JSONObject saveExpediente(HashMap<String, Object> expediente, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws Exception;
}
