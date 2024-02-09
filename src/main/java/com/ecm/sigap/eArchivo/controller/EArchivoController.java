/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.eArchivo.model.EArchivoDocumento;
import com.ecm.sigap.eArchivo.model.EArchivoExpediente;
import com.ecm.sigap.eArchivo.model.EArchivoFondoCuadro;
import com.ecm.sigap.eArchivo.model.EArchivoLegajo;
import com.ecm.sigap.eArchivo.model.EArchivoSerieSubserie;
import com.ecm.sigap.eArchivo.model.EArchivoTipoDocCatalogo;
import com.ecm.sigap.eArchivo.model.EArchivoUnidad;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;



/**
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public final class EArchivoController extends CustomRestController {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(EArchivoController.class);

	/**
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta expedientes", notes = "Consulta la lista de expedientes")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/expedientes", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getExpedentes(@RequestBody EArchivoExpediente expediente)
			throws Exception {

		try {
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);

			items = archivoService.getExpedientes(expediente, area.getClaveDepartamental(), userId, user_key,
					contentUser, authToken);
			String r = items.get("entity").toString();

			return new ResponseEntity<String>(r, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/expediente", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> aplicarExpedentes(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "expedienteId", required = true) String expedienteId) throws Exception {
		try {

			archivoService.aplicarExpediente(objectId, expedienteId);
			return new ResponseEntity<>(null, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param documento
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Glosar expediente", notes = "Glosa el documento al expediente")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 201, message = "Creado"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/documento/{idAsunto}", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> glosarDocumento(//
			@PathVariable("idAsunto") Integer idAsunto, //
			@RequestBody EArchivoDocumento documento) throws Exception {
		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			
			DocumentoAsunto doc = new DocumentoAsunto();
			String[] objId = documento.getObjectId().split(",");
			for (String oId : objId) {
				if(StringUtils.isNotBlank(oId)){
					doc.setIdAsunto(idAsunto);
					doc.setObjectId(oId);
					documento.setObjectId(oId);
	
					doc = mngrDocsAsunto.fetch(doc);
					JSONArray items = archivoService.glosarDocumentoMultipart(documento, doc, userId,
							area.getClaveDepartamental(), user_key, contentUser, authToken);
	
					String first = "";
	
					for (int i = 0; i < items.length(); i++) {
						first = items.getJSONObject(i).optString("docRepoId");
						if (first != null && !first.isEmpty()) {
							break;
						}
					}
	
					// doc.setArchivoId(first);
					// mngrDocsAsunto.update(doc);
	
					LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
					params.put("v_idArea", doc.getIdArea());
					params.put("v_idDocumento", oId);
					params.put("v_idGlosado", first);
	
					mngrDocsAsunto.createStoredProcedureCall("update_doc_glosado", params);
				}
			}

			return new ResponseEntity<>(doc, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param unidad
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/unidad", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getUnidades(@RequestBody EArchivoUnidad unidad) throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			items = archivoService.getUnidad(unidad, userId);

			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param tipoCatalogo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/tipoDocCatalogo", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> gettipoDocCatalogos(@RequestBody EArchivoTipoDocCatalogo tipoCatalogo)
			throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			items = archivoService.getTipoCatalogo(tipoCatalogo, userId);

			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param serie
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/serie", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getSerie(@RequestBody EArchivoSerieSubserie serie) throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.getSerie(serie, userId, area.getClaveDepartamental(), user_key, contentUser,
					authToken);
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param serie
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta serie", notes = "Consulta la lista de subseries")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/serie/acceso", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getSerieAcceso(@RequestBody EArchivoSerieSubserie serie)
			throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.getSerieAcceso(serie, userId, area.getClaveDepartamental(), user_key, contentUser,
					authToken);
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/**
	 *
	 * @param serie
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta series", notes = "Consulta la lista de series")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/serie/acceso/folio", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getSerieAccesoFolio(@RequestBody EArchivoSerieSubserie serie)
			throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.getSerieAccesoFolio(serie, userId, area.getClaveDepartamental(), user_key, contentUser,
					authToken);
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param subserie
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/subserie", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getSubserie(@RequestBody EArchivoSerieSubserie subserie)
			throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);

			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.getSubserie(subserie, area.getClaveDepartamental(), userId, user_key, contentUser,
					authToken);
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param subserie
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta subseries", notes = "Consulta la lista de subseries")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/subserie/acceso", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getSubserieAcceso(@RequestBody EArchivoSerieSubserie subserie)
			throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);

			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.getSubserieAcceso(subserie, userId, area.getClaveDepartamental(), user_key,
					contentUser, authToken);
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/**
	 *
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta series o subseries", notes = "Consulta la lista de series o subseries")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/serie-subserie", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getSeriesSubseries(@RequestBody EArchivoSerieSubserie obj) throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));		
			
			items = archivoService
					.getSeriesSubseries(
						obj,
						userId,
						area.getClaveDepartamental(),
						user_key,
						contentUser,
						authToken);
			
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param legajo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/legajo", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getLegajos(@RequestBody EArchivoLegajo legajo) throws Exception {
		try {
			JSONArray items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);

			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.findLegajo(legajo, area.getClaveDepartamental(), userId, user_key, contentUser,
					authToken);
			return new ResponseEntity<>(items.toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param objectId
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping(value = "/e-archivo/expediente/status/{objectId}", method = RequestMethod.GET)
//	public @ResponseBody ResponseEntity<String> statusExpedienteDocumento(
//			@PathVariable(value = "objectId") String objectId) throws Exception {
//		try {
//			JSONObject items;
//			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
//			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
//			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
//			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
//
//			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
//			items = archivoService.getStatusExpediente(objectId, userId, area.getClaveDepartamental(), user_key,
//					contentUser, authToken);
//			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);
//
//		} catch (Exception e) {
//			log.error(e.getLocalizedMessage());
//			
//			throw e;
//		}
//
//	}

	/**
	 *
	 * @param objectId
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene estado expediente", notes = "Obtiene el estado del expediente")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/expediente/status", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> statusExpedienteDocumento(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "ownerName", required = true) String ownerName) throws Exception {

		// IEndpoint endpoint = EndpointDispatcher.getInstance();
		// String userName =
		// endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

		// if (ownerName.equals(userName)) {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);

			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.getStatusExpediente(objectId, userId, area.getClaveDepartamental(), user_key,
					contentUser, authToken);
			if (items.length() == 0)
				return new ResponseEntity<>("{}", HttpStatus.OK);
			else
				return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		// } else {
		// return new ResponseEntity<>("{}", HttpStatus.OK);
		// }

	}
	
	/**
	 * Metodo para borrar relación de X documento archivo-eoficio.
	 * @param documento
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar relacion documento", notes = "Elimina la relacion de un documento a un expediente")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 201, message = "Creado"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/desglosarDocumento", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> desglosarDocumento(//
			@RequestBody DocumentoAsunto documento) throws Exception {
		try {
			LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("v_idArea", documento.getIdArea());
			params.put("v_idDocumento", documento.getObjectId());
			// Se manda vacio para resetear el R_OBJECT_ID_ARCHIVO
			params.put("v_idGlosado", "");
			mngrDocsAsunto.createStoredProcedureCall("update_doc_glosado", params);

			return new ResponseEntity<>(documento, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta proceso", notes = "Consulta la lista de proceso")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})

	@RequestMapping(value = "/e-archivo/proceso", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getProcesos(@RequestBody EArchivoExpediente exp) throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);

			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			items = archivoService.findProceso(exp, userId, area.getClaveDepartamental(), user_key, contentUser,
					authToken);
			return new ResponseEntity<>(items.get("entity").toString(), HttpStatus.OK);

		} catch (Exception e) {
			if(e.getLocalizedMessage().toLowerCase().matches(".*no cuenta con las condiciones necesarias.*")) {
				Map<String, Object> response = new HashMap<>();
				response.put("message", e.getLocalizedMessage());
				ObjectMapper mapper = new ObjectMapper();
				String jsonResponse = mapper.writeValueAsString(response);
				return new ResponseEntity<>(jsonResponse, HttpStatus.CONFLICT);
			}
			else {
				log.error(e.getLocalizedMessage());
				
				throw e;
			}
		}

	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/e-archivo/usuario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getUsuario(
			@RequestParam(value = "idUsuario", required = true) String idUsuario) throws Exception {
		try {
			
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			
			JSONObject usuario;
			usuario = archivoService.getUsuario(userId, area.getClaveDepartamental(), user_key, contentUser, authToken, idUsuario);
			String response = usuario.get("entity").toString();
			return new ResponseEntity<String>(response, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta fondos", notes = "Consulta los fondos del catalaogo de disposicio documental")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "e-archivo/cuadroArchivistica/fondo", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> search(
			@RequestBody(required = true) EArchivoFondoCuadro Fondo)  throws Exception {
		try {
			
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			
			JSONObject usuario;
			usuario = archivoService.getFondo(userId, area.getClaveDepartamental(), user_key, contentUser, authToken, Fondo);
			String response = usuario.get("entity").toString();
			return new ResponseEntity<String>(response, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/**
	 *
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene acceso area", notes = "Obtiene el acceso del area")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/area/acceso", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getArea() throws Exception {
		try {
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			
			items = archivoService.getCurrentAcceso(userId, area.getClaveDepartamental());
			
			return new ResponseEntity<>(items.toString(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear expediente", notes = "Crea un nuevo expediente")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 201, message = "Creado"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/e-archivo/expediente", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> saveExpediente(@RequestBody(required = true) HashMap<String, Object> expediente)
			throws Exception {
		try {
			Area area = mngrArea.fetch(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
			JSONObject items;
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID, false);
			String user_key = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			String contentUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String authToken = getHeader(HeaderValueNames.HEADER_AUTH_TOKEN, false);
			String r = "";
			HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			try {
				items = archivoService.saveExpediente(
					expediente,
					area.getClaveDepartamental(),
					userId, user_key,
					contentUser,
					authToken);
				
				r = items.get("entity").toString();
				httpStatus = HttpStatus.OK;
			} catch (Exception e) {
				if(e.getLocalizedMessage().contains("No puede crearse un nuevo expediente porque ya existe otro con el mismo titulo en esta área.")) {
					httpStatus = HttpStatus.OK;
					r = "{\"evaluaDuplicado\":true, \"mensajeDuplicado\":\"Titulo expediente ya registrado\"}";
				}	
				else if(e.getLocalizedMessage().contains("No puede crearse un nuevo expediente porque ya existe otro con la misma descripción (asunto) en esta área.")) {
					httpStatus =  HttpStatus.OK;
					r = "{\"evaluaDuplicado\":true, \"mensajeDuplicado\":\"Descripcion expediente ya registrado\"}";
				}
				else {
					log.error(e.getLocalizedMessage());
					
					throw e;
				}
			}
			return new ResponseEntity<String>(r, httpStatus);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
}
