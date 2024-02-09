/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * The Class DocumentoCompartidoController.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class DocumentoCompartidoController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(DocumentoCompartidoController.class);

	/**
	 * Save documentos compartidos.
	 *
	 * @param item the item
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/documentos/compartidos", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveDocumentosCompartidos(
			@RequestBody(required = true) HashMap<String, Object> item) throws Exception {

		try {
			log.debug(">>> Documento compartido a guardar " + item.get("nombreArchivo").toString());
			Integer idAsunto = Integer.parseInt(item.get("idAsunto").toString());

			String ruta = getFolderAsuntoColaborativo(idAsunto);

			if (StringUtils.isBlank(ruta))
				throw new BadRequestException(
						" El folder colaborativo no esta seteado :: idSeccion = SIGAP idClave = COLABORATIVO");

			// * * * * * * * * * * * * * * * * * * *

			Asunto asunto = mngrAsunto.fetch(idAsunto);

			Integer idAsuntoOrigen = asunto.getIdAsuntoOrigen();

			Asunto asuntoOrigen = mngrAsunto.fetch(idAsuntoOrigen);

			// * * * * * * * * * * * * * * * * * * *

			String subFolderName = ruta.substring(ruta.lastIndexOf("/") + 1);

			String folderName = ruta.substring(0, ruta.lastIndexOf("/"));

			folderName = folderName.substring(folderName.lastIndexOf("/") + 1);

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			String tipoFolder = getTipoFolderColaborativo();

			tipoFolder = tipoFolder.toLowerCase();

			String parentId;

			try {

				parentId = endpoint.getFolderIdByPath(ruta);

			} catch (Exception e) {

				if (e.getMessage() != null && e.getMessage().contains("Object Not Found!")) {

					// No existe el folder colaborativo del asunto. crearlo.

					String subRuta = ruta.substring(0, ruta.lastIndexOf("/"));

					String subParentId;

					try {

						subParentId = endpoint.getFolderIdByPath(subRuta);

					} catch (Exception e1) {

						if (e.getMessage() != null && e.getMessage().contains("Object Not Found!")) {

							// subruta no existe. crear ...
							String colaboratiboFolderId = endpoint.getFolderIdByPath(getColaborativoPath());

							subParentId = endpoint.createFolderIntoId(colaboratiboFolderId, tipoFolder, folderName);

						} else {
							log.error(e1.getMessage());
							throw e1;
						}
					}
					log.debug(">>> Se va amandar a crear el folder");
					parentId = endpoint.createFolderIntoId(subParentId, tipoFolder, subFolderName);

				} else {

					log.error(e.getMessage());
					throw e;
				}
			}

			String folderId = parentId;
			String nombreArchivo = item.get("nombreArchivo").toString();
			String tipoDoc = "";
			Version verDoc = Version.NONE;
			String descDoc = "Documento compartido cargado via SIGAP V";

			if (item.get("fileB64").toString() == null) {
				log.error("El contenido del item.get(fileB64).toString() esta vacio por lo que se rechaza la peticion");
				return new ResponseEntity<Map<String, Object>>(item, HttpStatus.BAD_REQUEST);
			}
			boolean isBase64 = Base64.isBase64(item.get("fileB64").toString());
			if (!isBase64) {
				log.error("El item.get(fileB64).toString() del item no es Base64, se rechaza la peticion");
				return new ResponseEntity<Map<String, Object>>(item, HttpStatus.BAD_REQUEST);
			}

			File documento = FileUtil.B64StringToFile(item.get("fileB64").toString(), nombreArchivo);

			log.debug(">>> Agregando documento al folder");
			// agregar documento al folder.
			String newDocId = endpoint.saveDocumentoIntoId(folderId, nombreArchivo, tipoDoc, verDoc, descDoc,
					documento);

			documento.delete();

			Map<String, Object> properties = new HashMap<>();
			// Obtenemos el User Name para asignarlo como el Owner
			// del documento
			String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
			properties.put("owner_name", userName);

			endpoint.setProperties(newDocId, properties);

			item.put("objectId", newDocId);
			item.remove("fileB64");

			// AGREGAR ACL
			Map<String, String> additionalData = new HashMap<>();

			additionalData.put("idArea", asuntoOrigen.getArea().getId());

			String aclName = "aclNameDocumentoCompartidoAsunto";

			// Aplicando ACL en 3 intentos.
			log.debug("Aplicando el ACL" + aclName + " a documento compartido ");

			try {
				for (int i = 0; i <= 3; i++) {
					boolean resultSetAcl = endpoint.setACL(newDocId, environment.getProperty(aclName), additionalData);
					if (resultSetAcl) {
						break;
					} else if (resultSetAcl == Boolean.FALSE && i == 3) {
						log.debug(">>> Se intentó Aplicar el ACL " + aclName + " a documento adjunto " + i + " veces");
						throw new Exception();
					}
				}
			} catch (Exception e) {

				endpoint.eliminarDocumento(newDocId);
				throw new Exception("Error agregando permisos al documento compartido.");
			}

			return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Save document list.
	 *
	 * @param documentos the documentos
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar documento compartido", notes = "Agrega un documento compartido a un asunto")
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

	@RequestMapping(value = "/documentos/compartidosList", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<Object, Object>> saveDocumentList(
			@RequestBody(required = true) List<HashMap<String, Object>> documentos) throws Exception {

		try {
			Map<Object, Object> listResult = new HashMap<Object, Object>();
			if (!documentos.isEmpty()) {

				for (HashMap<String, Object> item : documentos) {
					try {
						ResponseEntity<Map<String, Object>> response = saveDocumentosCompartidos(item);
						listResult.put(response.getBody(), response.getStatusCode());

					} catch (BadRequestException e) {
						listResult.put(item, HttpStatus.BAD_REQUEST);
					} catch (Exception e) {
						listResult.put(item, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}

				return new ResponseEntity<Map<Object, Object>>(listResult, HttpStatus.OK);
			} else {
				return new ResponseEntity<Map<Object, Object>>(listResult, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * Se agrega un documento compartido al asunto origen.
	 *
	 * @param idAsunto the id asunto
	 * @return the documentos compartidos
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene  documentos asunto", notes = "Obtiene los documentos de un asunto")
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

	@RequestMapping(value = "/documentos/compartidos", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Map<String, String>>> getDocumentosCompartidos(
			@RequestParam(value = "idAsunto", required = true) Integer idAsunto) throws Exception {

		List<Map<String, String>> result = new ArrayList<>();

		try {

			String ruta = getFolderAsuntoColaborativo(idAsunto);

			if (ruta != null) {
				IEndpoint endpoint = EndpointDispatcher.getInstance();

				String parentId = endpoint.getFolderIdByPath(ruta);

				result = endpoint.obtenerDocumentosCompartidos(parentId);

				// via path como deberia de ser
				// String parentId = endpoint.getFolderIdByPath(ruta);
				// List<Map<String, String>> children =
				// endpoint.getSubfolders(parentId, null, null);

			}

		} catch (Exception e) {

			if (e.getMessage() != null && e.getMessage().contains("Object Not Found!")) {

				return new ResponseEntity<List<Map<String, String>>>(new ArrayList<Map<String, String>>(),
						HttpStatus.OK);
			}

			log.error(e.getMessage());
			// throw e;
		}

		return new ResponseEntity<List<Map<String, String>>>(result, HttpStatus.OK);

	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar documento compartido", notes = "Elimina un documento compartido de un asunto")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/documentos/compartidos", method = RequestMethod.DELETE)
	public void delete(@RequestParam(value = "objectId", required = true) Serializable objectId) throws Exception {
		log.debug("Documento a borrar :: [contentId=" + objectId + "]");
		boolean isDelete = false;
		IEndpoint endpoint = EndpointDispatcher.getInstance();
		String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
		List<String> ownerName = (List<String>) endpoint.getObjectProperty(objectId.toString(), "owner_name");
		String owner_name = (ownerName == null || ownerName.isEmpty()) ? null : ownerName.get(0);
		if (userName.equals(String.valueOf(owner_name)))
			isDelete = endpoint.eliminarDocumento(objectId.toString());
		if (!isDelete)
			throw new Exception("No tienes permisos para realizar esta accion.");
	}

	/** */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");

	/**
	 * Obtiene el folder del asunto raiz donde se almacenan los documentos
	 * colaborativos.
	 *
	 * @param idAsunto the id asunto
	 * @return the folder asunto colaborativo
	 */
	private String getFolderAsuntoColaborativo(Integer idAsunto) {

		Asunto asunto = mngrAsunto.fetch(idAsunto);

		Integer idAsuntoOrigen = asunto.getIdAsuntoOrigen();

		String ruta = null;

		if (idAsuntoOrigen != null) {
			asunto = mngrAsunto.fetch(idAsuntoOrigen);

			Integer idAsuntoDetalle = asunto.getAsuntoDetalle().getIdAsuntoDetalle();

			String dateRegistro = "";

			if (null != asunto.getFechaRegistro()) {
				dateRegistro = sdf.format(asunto.getFechaRegistro());
			} else {
				dateRegistro = sdf.format(asunto.getAsuntoDetalle().getFechaElaboracion());
			}

			String colaborativoPath = getColaborativoPath();

			if (StringUtils.isBlank(colaborativoPath)) {
				log.error(" El folder colaborativo no esta seteado :: idSeccion = SIGAP idClave = COLABORATIVO");
				return null;
			}

			ruta = colaborativoPath + "/" + dateRegistro + "/" + idAsuntoDetalle;

		} else {

			log.error("RUTA DOC COLABORATIVO :: " + idAsunto + " NO TIENE ASUNTORIGEN");
		}

		log.debug("RUTA DOC COLABORATIVO :: " + ruta);

		return ruta;

	}

	/**
	 * Obtiene el tipo documental del folder colaborativo.
	 *
	 * @return the tipo folder colaborativo
	 */
	private String getTipoFolderColaborativo() {
		String idSeccion = "SIGAP";
		String idClave = "FOLDERTYPEFOLIO";
		return getParamApp(idSeccion, idClave);
	}

	/**
	 * Obtiene el folder donde se crean los folders colavorativos por asunto.
	 *
	 * @return the colaborativo path
	 */
	private String getColaborativoPath() {
		String idSeccion = "SIGAP";
		String idClave = "COLABORATIVO";
		return getParamApp(idSeccion, idClave);
	}

}
