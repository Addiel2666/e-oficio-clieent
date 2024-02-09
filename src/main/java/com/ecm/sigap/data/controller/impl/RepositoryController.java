/**
 * Copyright (c) 2016 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.ForbiddenException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.util.CheckinObject;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class RepositoryController extends CustomRestController {

	/** */
	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RepositoryController.class);

	/**
	 * Referencia hacia el REST controller de {@link AsuntoController}.
	 */
	@Autowired
	private AsuntoController asuntoController;

	@Autowired
	private MailController mailController;
	/**
	 * Referencia hacia el REST controller de {@link DocumentoCompartidoController}.
	 */
	@Autowired
	private DocumentoCompartidoController documentoCompartidoController;

	/**
	 * 
	 * @param checkinObject
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Versionar documento", notes = "Registra una nueva version del documento")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 201, message = "Creado"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/repository/checkin", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<CheckinObject> checkin(
			@RequestBody(required = true) CheckinObject checkinObject) throws Exception {

		File documento = null;

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			if (!esSoloLectura(userId)) {

				log.info("CHECKIN >> " + checkinObject);

				if (checkinObject.getDocumentB64() == null) {
					log.error("El contenido del getFileB64 esta vacio por lo que se rechaza la peticion");
					return new ResponseEntity<CheckinObject>(checkinObject, HttpStatus.BAD_REQUEST);
				}
				boolean isBase64 = Base64.isBase64(checkinObject.getDocumentB64());
				if (!isBase64) {
					log.error("El getFileB64 del documento no es Base64, se rechaza la peticion");
					return new ResponseEntity<CheckinObject>(checkinObject, HttpStatus.BAD_REQUEST);
				}

				String objectId = checkinObject.getObjectId();
				Version verDoc = checkinObject.getVersion();
				String versionComment = checkinObject.getComment();
				String nombreArchivo = checkinObject.getNombre();
				documento = FileUtil.createTempFile(checkinObject.getDocumentB64());

				String mimeType;

				try (InputStream theInputStream = new FileInputStream(documento);
						InputStream is = theInputStream;
						BufferedInputStream bis = new BufferedInputStream(is);) {
					AutoDetectParser parser = new AutoDetectParser();
					Detector detector = parser.getDetector();
					Metadata md = new Metadata();
					md.add(Metadata.RESOURCE_NAME_KEY, documento.getName());
					MediaType mediaType = detector.detect(bis, md);
					mimeType = mediaType.toString();
				}

				if ("application/msword".equalsIgnoreCase(mimeType) || "application/xml".equalsIgnoreCase(mimeType)) {

					List<Map<String, String>> resultsCheckin = EndpointDispatcher.getInstance().checkIn(objectId, //
							verDoc, //
							versionComment, //
							nombreArchivo, //
							documento);

					documento.delete();

					for (Map<String, String> result : resultsCheckin) {
						checkinObject.setNewObjectId(result.get("documentoId"));
						checkinObject.setNewVersion(result.get("version"));
						checkinObject.setDocumentB64(null);

						log.debug("NEW ID >> " + result.get("documentoId"));
						log.debug("NEW VERSION >> " + result.get("version"));

						return new ResponseEntity<CheckinObject>(checkinObject, HttpStatus.OK);
					}

					throw new Exception(" NO OPERATION RESULT! ");

				} else {

					return new ResponseEntity<CheckinObject>(checkinObject, HttpStatus.CONFLICT);

				}

			} else {

				return new ResponseEntity<CheckinObject>(checkinObject, HttpStatus.BAD_REQUEST);

			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());

			throw e;

		} finally {

			if (documento != null && documento.exists())
				documento.delete();
		}

	}

	/**
	 * 
	 * @param objectId
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Modificar plantilla", notes = "Permite modificar el documento del borrador")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/repository/checkout", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> checkout(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		log.info("CHECKOUT >> " + objectId);

		try {

			Boolean isCheckedOut = EndpointDispatcher.getInstance().checkOut(objectId.toLowerCase());

			Map<String, Object> result = new HashMap<String, Object>();

			result.put("isCheckedOut", isCheckedOut);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

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

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Cancelar bloqueo", notes = "Cancela la modificacion de un documento")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/repository/cancelCheckout", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> cancelCheckout(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		log.info("CANCEL CHECKOUT >> " + objectId);

		try {

			Boolean isCheckedOutCanceled = EndpointDispatcher.getInstance().cancelCheckOut(objectId.toLowerCase());

			Map<String, Object> result = new HashMap<String, Object>();

			result.put("isCheckedOutCanceled", isCheckedOutCanceled);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

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
	@RequestMapping(value = "/repository/objectProperties", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getObjectProperties(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		try {

			return new ResponseEntity<Map<String, Object>>(
					EndpointDispatcher.getInstance().getObjectProperties(objectId), HttpStatus.OK);

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
	@RequestMapping(value = "/repository/eliminar/documento", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> eliminarDocumento(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		Map<String, Object> result = new HashMap<>();

		try {

			String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

			boolean eliminated = EndpointDispatcher.getInstance(contetUser, password).eliminarDocumento(objectId);

			result.put("eliminated", eliminated);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * 
	 * @param objectId
	 * @param asuntoId
	 * @return
	 * @throws Exception
	 */
	public @ResponseBody ResponseEntity<Map<String, Object>> getDocument(
			@RequestParam(value = "objectId", required = true) String objectId, //
			@RequestParam(value = "asuntoId", required = false) Integer asuntoId //
	) throws Exception {

		return getDocument(objectId, asuntoId, "true");

	}

	/**
	 * Obtiene un documento como cadena Base64.
	 * 
	 * @param objectId Object Id del documento que se quiere obtener
	 * @param asuntoId Id del tramite al que pertenece el documento
	 * @return Documento como cadena b64
	 * @throws Exception Cualquier error al momento de obtner el documento
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Descargar documento", notes = "Obtiene un documento en base 64 y lo descarga")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@GetMapping(value = "/repository/downloadDocument")
	public @ResponseBody ResponseEntity<Map<String, Object>> getDocument(
			@RequestParam(value = "objectId", required = true) String objectId, //
			@RequestParam(value = "asuntoId", required = false) Integer asuntoId, //
			@RequestParam(value = "forceId", required = false, defaultValue = "true" //
			) String forceId) throws Exception {
		ResponseEntity<Map<String, Object>> res = null;
		try {

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			// SE ACTUALIZA EL ID POR EL ULTIMO DE LA SERIE.
			if (Boolean.parseBoolean(forceId))
				objectId = endpoint.getLastVersionSeriesId(objectId.toLowerCase());

			String user = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);
			Integer idAreaUsuario = Integer.valueOf(getHeader(HeaderValueNames.HEADER_AREA_ID));

			Map<String, Object> result = null;

			boolean flag = false;

			do {

				try {

					result = downloadDocumento(user, password, objectId);

					res = new ResponseEntity<>(result, HttpStatus.OK);
					return res;

				} catch (Exception e) {

					if (asuntoId != null && flag == false) {
						log.error("ERROR >>>> Descargando documento con objectId: " + objectId
								+ " se procede a corregir ACL ");
						tryFixAclAsunto(asuntoId, idAreaUsuario, endpoint);
						flag = true;

					} else {

						throw e;

					}

				}

			} while (flag);

			throw new Exception("fail!");

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		} finally {
			res = null;
		}

	}

	@ApiOperation(value = "Descargar documento via request attachment", notes = "Obtiene un documento y lo descarga")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "objectId", value = "Identificador del documento", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "contentUser", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "userKey", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(path = "/downloadDocument2", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(//
			@RequestParam(value = "objectId", required = true) String objectId, //
			@RequestParam(value = "contentUser", required = true) String contentUser, //
			@RequestParam(value = "userKey", required = true) String userKey //
	) throws Exception {

		String password = decryptText(userKey);
		String user = decryptText(contentUser);

		IEndpoint endpoint = EndpointDispatcher.getInstance(user, password);

		InputStream is = endpoint.getObjectContentAsInputStream(objectId);

		String nombreArchivo = endpoint.getObjectName(objectId);

		// Copia los datos del InputStream a un byte[]
		byte[] data = IOUtils.toByteArray(is);

		// Crea un InputStreamResource a partir de la copia de datos
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));
		is.close();

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"");
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");

		return ResponseEntity.ok() //
				.headers(header) //
				.contentLength(data.length).contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM) //
				.body(resource);
	}

	/**
	 * 
	 * @param asuntoId
	 * @param idAreaUsuario
	 * @param endpoint
	 * @throws Exception
	 */
	private void tryFixAclAsunto(Integer asuntoId, Integer idAreaUsuario, IEndpoint endpoint) throws Exception {

		log.debug("INFO >>>> Iniciando recuperación de ACL para idAsunto: " + asuntoId + " idAreaUsuario "
				+ idAreaUsuario);

		Asunto tramite = mngrAsunto.fetch(asuntoId);

		Integer idAreaDestinoEnElTramite = tramite.getAreaDestino().getIdArea();

		if (!TipoAsunto.ASUNTO.equals(tramite.getTipoAsunto()) // solo tramites
				&& idAreaUsuario.equals(idAreaDestinoEnElTramite))// area destinatario == area conectada.
		{

			List<String> corregidos = new ArrayList<String>();
			String corregido;

			Map<String, String> additionalData = new HashMap<>();
			String aclName = "aclNameAdjuntoTramite";

			additionalData.put("idArea", String.valueOf(idAreaDestinoEnElTramite));

			// Para el caso de los asuntos confidenciales, se le
			// asigna el ACL de Tramites confidenciales
			if (tramite.getAsuntoDetalle().getConfidencial()) {
				aclName = "aclNameAdjuntoTramiteConfidencial";
			}

			// - - - - - - -

			// DOCUMENTOS DEL TRAMITE
			List<DocumentoAsunto> documentosAsunto = asuntoController.getDocumentosAsunto(asuntoId);
			{
				log.debug("INFO >>>> Iniciando recuperación de ACL del los Documentos del tramite");

				String lastObjectId;

				for (DocumentoAsunto documentoAsunto : documentosAsunto) {

					corregido = "Se procesa el archivo " + documentoAsunto.getObjectId().toLowerCase()
							+ " con exito ? <br />";

					try {
						lastObjectId = endpoint//
								.getLastVersionSeriesId(documentoAsunto.getObjectId().toLowerCase());

						boolean ok = asuntoController.agregarPermisoDocumento(lastObjectId, //
								environment.getProperty(aclName), //
								additionalData);
						corregido += ok;

					} catch (Exception e) {
						log.error("Error >>>> corrigiendo documento del tramite." + e.getLocalizedMessage());

						corregido += false;
					}

					corregidos.add(corregido);
				}
			}

			// DOCUMENTOS COMPARTIDOS
			ResponseEntity<List<Map<String, String>>> documentosCompartidos = documentoCompartidoController
					.getDocumentosCompartidos(tramite.getIdAsunto());
			{
				String lastObjectId;
				log.debug("INFO >>>> Iniciando recuperación de ACL del los Documentos compartidos");

				for (Map<String, String> docCompartido : documentosCompartidos.getBody()) {

					corregido = "Se procesa el archivo " + docCompartido.get("r_object_id").toLowerCase()
							+ " con exito ? ";

					try {
						lastObjectId = endpoint.getLastVersionSeriesId(docCompartido.get("r_object_id").toLowerCase());

						boolean ok = asuntoController.agregarPermisoDocumento(lastObjectId, //
								environment.getProperty(aclName), //
								additionalData);
						corregido += ok;

					} catch (Exception ex) {
						log.error("Error >>>> corrigiendo documento compartido." + ex.getLocalizedMessage());

						corregido += false;
					}

					corregidos.add(corregido);
				}
			}

			String mailMesssage = "SE REPARON LOS PERMIOS DE LOS DOCUMENTOS DEL TRAMITE ID " + asuntoId
					+ " DE LOS CUALES <br />";

			for (String s : corregidos) {
				mailMesssage += "<br />" + s;
			}

			log.warn(mailMesssage);

			try {
				String mailNotificacion = environment.getProperty("mailNotificacion");

				if (StringUtils.isNotBlank(mailNotificacion)) {

					mailController.sendNotificacionEmpty(mailNotificacion, mailMesssage);

				}

			} catch (Exception e) {
				log.error("Error >>>> Enviando notificacion de documentos corregidos ");

			}

		}

	}

	/**
	 * Obtiene un documento como cadena Base64.
	 * 
	 * @param objectId Object Id del documento que se quiere obtener
	 * @return Documento como cadena b64
	 * @throws Exception Cualquier error al momento de obtner el documento
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documento", notes = "Obtiene un documento como cadena Base64")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@GetMapping(value = "/repository/downloadDocumentAsAdmin")
	public @ResponseBody ResponseEntity<Map<String, Object>> getDocumentAsAdmin(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		Map<String, Object> result = downloadDocumento(null, null, objectId);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @param objectId
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> downloadDocumento(String user, String password, String objectId)
			throws ForbiddenException, Exception {

		log.debug("Descargando el documento con ObjectId " + objectId + " con las credenciales [usuario=" + user
				+ "][password=" + password + "]");

		objectId = objectId.toLowerCase();

		IEndpoint endpoint = EndpointDispatcher.getInstance(user, password);

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> props;
		String encodeBase64String;

		File f = null;

		try {

			props = endpoint.getObjectProperties(objectId);

			if ("true".equalsIgnoreCase(props.getOrDefault("isDeleted", "false").toString()))
				encodeBase64String = Base64.encodeBase64String("El arhivo no existe".getBytes());
			else {
				encodeBase64String = endpoint.getObjectContentB64(objectId);
			}

			/*
			 * if (encodeBase64String == null) { log.
			 * error("El contenido del getFileB64 esta vacio por lo que se rechaza la peticion"
			 * ); throw new BadRequestException(); } boolean isBase64 =
			 * Base64.isBase64(encodeBase64String); if(!isBase64) {
			 * log.error("El getFileB64 del documento no es Base64, se rechaza la peticion"
			 * ); throw new BadRequestException(); }
			 */

			String fileName = ((List<?>) props.get("cmis:name")).get(0).toString();
			result.put("name", fileName);

			result.put("objectId", objectId);
			result.put("contentB64", encodeBase64String);

			f = FileUtil.createTempFile(encodeBase64String, fileName).toFile();

			String ct = new MimetypesFileTypeMap().getContentType(f);

			if (StringUtils.isEmpty(ct)) {
				log.warn("El documento \"" + fileName + "\" con id " + objectId + " no se pudo obtener su mime-type.");
				ct = APPLICATION_OCTET_STREAM;
			}

			result.put("type", ct);

			return result;

		} catch (ForbiddenException e) {

			log.error("El usuario '" + user + "' no tiene permiso para descargar el documento " + objectId);
			log.error(e.getMessage());
			throw e;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;

		} finally {

			if (f != null && f.exists())
				f.delete();

		}

	}

	/**
	 * 
	 * @param objectId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/repository/getByPath", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> getObjectByPath(
			@RequestBody(required = true) Map<String, Object> body) throws Exception {

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		String path = body.get("path").toString();

		Map<String, Object> object;

		try {

			object = endpoint.getDocumentByPath(path);

		} catch (Exception e) {

			try {

				object = endpoint.getFolderByPath(path);

			} catch (Exception e2) {
				log.error(e2.getLocalizedMessage());
				throw e2;
			}
		}

		List<Map<String, Object>> list = (List<Map<String, Object>>) object.get("Objeto");

		return new ResponseEntity<Map<String, Object>>(list.get(0), HttpStatus.OK);
	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene folder raiz", notes = "Obtiene el folder raiz de documentos de repositorio")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/repository/getRootFolder", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getRootFolder() throws Exception {

		String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
		String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

		IEndpoint endpoint = EndpointDispatcher.getInstance(contetUser, password);

		Map<String, Object> object;

		try {

			object = endpoint.getRootFolder();

		} catch (Exception e) {

			throw e;
		}

		return new ResponseEntity<Map<String, Object>>(object, HttpStatus.OK);
	}

	/**
	 * Obtiene los subfolder del folder indicado.
	 * 
	 * @param objectId
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene subfolders", notes = "Obtiene los subfolder del folder raiz de documentos de repositorio")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/repository/folder/subfolders", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Map<String, String>>> getSubFolder(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "objectName", required = false) String objectName,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws Exception {

		List<Map<String, String>> subfolders = new ArrayList<>();

		try {
			String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

			IEndpoint endpoint = EndpointDispatcher.getInstance(contetUser, password);

			List<Map<String, String>> subfolders_ = endpoint.getSubfolders(objectId, objectName, page);

			Map<String, String> folder;
			boolean isFolder;
			for (Map<String, String> map : subfolders_) {
				folder = new HashMap<>();

				folder.put("name", map.get("cmis:name"));
				folder.put("documentoId", map.get("cmis:objectId"));
				folder.put("contentType", map.get("cmis:contentStreamMimeType"));
				folder.put("objectTypeId", map.get("cmis:objectTypeId"));

				isFolder = "cmis:folder".equalsIgnoreCase(map.get("cmis:baseTypeId"))
						|| "cmis:folder".equalsIgnoreCase(map.get("cmis:objectTypeId"));

				folder.put("tipo", isFolder ? "FOLDER" : "DOCUMENTO");

				subfolders.add(folder);
			}
		} catch (Exception e) {
			log.warn(e.getCause());
		}

		return new ResponseEntity<List<Map<String, String>>>(subfolders, HttpStatus.OK);
	}

	/**
	 * 
	 * @param objectId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/repository/folder/documents", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getSubDocuments(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		// IEndpoint endpoint = EndpointDispatcher.getInstance();

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
