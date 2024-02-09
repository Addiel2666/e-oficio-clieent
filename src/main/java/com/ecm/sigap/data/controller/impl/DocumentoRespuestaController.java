/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;

import org.apache.chemistry.opencmis.commons.impl.jaxb.EnumPropertiesBase;
import org.apache.chemistry.opencmis.commons.impl.jaxb.EnumPropertiesDocument;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoConsultaEspecial;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.DocumentoRespuestaAux;
import com.ecm.sigap.data.model.Respuesta;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.DocumentoRespuesta}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class DocumentoRespuestaController extends CustomRestController implements RESTController<DocumentoRespuesta> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(DocumentoRespuestaController.class);

	/**
	 * Referencia hacia el REST controller de {@link AsuntoController}.
	 */
	@Autowired
	private AsuntoController asuntoController;
	/**
	 * Referencia hacia el REST controller de {@link RespuestaController}.
	 */
	@Autowired
	private RespuestaController respuestaController;

	@Autowired
	private PermisoController permisoController;

	/**
	 * Referencia hacia el REST controller de {@link RepositoryController}.
	 */
	@Autowired
	private RepositoryController repositoryController;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@RequestMapping(value = "/documentos/respuesta", method = RequestMethod.PUT)
	public synchronized @ResponseBody ResponseEntity<DocumentoRespuesta> save(
			@RequestBody(required = true) DocumentoRespuesta documento) throws Exception {

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("Datos del documento respuesta a guardar " + documento);
				if (documento.getObjectId() != null)
					mngrDocsRespuesta.update(documento);
				else {

					Respuesta respuesta = mngrRespuesta.fetch(documento.getIdRespuesta());

					Asunto asunto = null;
					AsuntoConsultaEspecial ac = new AsuntoConsultaEspecial();

					if (respuesta != null)
						asunto = mngrAsunto.fetch(respuesta.getIdAsunto());
					
					try {
						ac.setIdAsuntoOrigen(asunto.getIdAsuntoOrigen());
						ac.setIdPromotor(asunto.getAsuntoDetalle().getPromotor().getIdInstitucion());
					} catch (Exception e) {
						log.error("Fallo al setear datos de asunto al documento");
					}
					

					if (respuesta == null || asunto == null) {
						throw new BadRequestException("El asunto o la respuesta no existen.");
					}

					if (StringUtils.isBlank(asunto.getContentId())) {
						asunto = asuntoController.actualizaAsuntoEnRepo(asunto);
						mngrAsunto.update(asunto);
					}

					documento.setIdAsunto(respuesta.getIdAsunto());
					documento.setParentContentId(asunto.getContentId());

					{
						log.debug("SAVING NEW DOCUMENTO RESPUESTA :: " + documento.toString());

						if (documento.getFileB64() == null) {
							log.error("El contenido del getFileB64 esta vacio por lo que se rechaza la peticion");
							return new ResponseEntity<DocumentoRespuesta>(documento, HttpStatus.BAD_REQUEST);
						}
						boolean isBase64 = Base64.isBase64(documento.getFileB64());
						if (!isBase64) {
							log.error("El getFileB64 del documento no es Base64, se rechaza la peticion");
							return new ResponseEntity<DocumentoRespuesta>(documento, HttpStatus.BAD_REQUEST);
						}

						File documento_ = FileUtil.createTempFile(documento.getFileB64());
						String parentFolderId = documento.getParentContentId();
						String nombreArchivo = documento.getObjectName();
						String tipoDoc = environment.getProperty("docTypeAdjuntoRespuesta");
						Version verDoc = Version.MAYOR;
						String descDoc = documento.getObjectName();

						// subir archivo al repositorio
						String newID = endpoint.saveDocumentoIntoId(parentFolderId, nombreArchivo, tipoDoc, verDoc,
								descDoc, documento_);

						documento_.delete();

						Map<String, Object> properties = new HashMap<>();
						// Obtenemos el User Name para asignarlo como el Owner
						// del documento
						String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
						properties.put("owner_name", userName);

						// Asignamos las propiedades del Asunto
						properties.put("idasunto", String.valueOf(asunto.getIdAsunto()));
						properties.put("idrespuesta", String.valueOf(respuesta.getIdRespuesta()));

						endpoint.setProperties(newID, properties);

						// AGREGAR ACL
						Map<String, String> additionalData = new HashMap<>();

						additionalData.put("idArea", documento.getIdArea().toString());

						String aclName = "aclNameAdjuntoRespuesta";
						// Para el caso de los asuntos confidenciales, se le
						// asigna el ACL de Asuntos confidenciales
						if (asunto.getAsuntoDetalle().getConfidencial()) {
							aclName = "aclNameAdjuntoRespuestaConfidencial";
						}

						log.debug("Aplicando el ACL " + aclName + " documento ");
						endpoint.setACL(newID, environment.getProperty(aclName), additionalData);

						documento.setObjectId(newID);
						documento.setAsuntoConsulta(ac);
					}

					mngrDocsRespuesta.save(documento);

					documento.setFileB64(null);
				}

			} else {

				return new ResponseEntity<DocumentoRespuesta>(documento, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}

		return new ResponseEntity<DocumentoRespuesta>(documento, HttpStatus.OK);
	}

	/**
	 * Save all.
	 *
	 * @param documentos the documentos
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/documentos/respuesta/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveAll(
			@RequestBody(required = true) List<DocumentoRespuesta> documentos) throws Exception {
		try {
			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();
			List<DocumentoRespuesta> listSuccess = new ArrayList<>();

			if (!documentos.isEmpty()) {

				for (DocumentoRespuesta documentoRespuesta : documentos) {
					try {
						ResponseEntity<DocumentoRespuesta> response = save(documentoRespuesta);
						listSuccess.add(response.getBody());
					} catch (BadRequestException e) {
						listResultFail.put(documentoRespuesta.getObjectName(), HttpStatus.BAD_REQUEST);
					} catch (Exception e) {
						listResultFail.put(documentoRespuesta.getObjectName(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}

				listResult.put("success", listSuccess);
				listResult.put("error", listResultFail);
				return new ResponseEntity<>(listResult, HttpStatus.OK);

			} else {

				throw new BadRequestException();

			}
		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @param jsonDocs
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private List<DocumentoRespuesta> parseJsonDocsJ(String jsonDocs)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonDocsFinal = "[" + jsonDocs + "]";
		return mapper.readValue(jsonDocsFinal, new TypeReference<List<DocumentoRespuesta>>() {
		});
	}

	/**
	 * 
	 * @param request
	 * @param documentos
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar documento", notes = "Agrega un documento a una respuesta")
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

	@RequestMapping(value = "/documentos/respuesta/list/multipart", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveDocumentListMultipart(
			MultipartHttpServletRequest request, @RequestParam("documentos") String documentos) throws Exception {
		try {

			List<MultipartFile> files = null;
			List<DocumentoRespuesta> docs = null;

			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();

			if (!documentos.isEmpty()) {

				files = request.getFiles("files");
				docs = parseJsonDocsJ(documentos);
				for (DocumentoRespuesta document : docs) {
					for (MultipartFile fileunico : files) {
						if (fileunico.getOriginalFilename().equals(document.getObjectName())) {
							try(InputStream inputStream = fileunico.getInputStream()) {
								byte[] bytes = IOUtils.toByteArray(inputStream);
								String encoded = java.util.Base64.getEncoder().encodeToString(bytes);
								document.setFileB64(encoded);
								document.setFechaRegistro(new Date());
								ResponseEntity<DocumentoRespuesta> rr = save(document);
								success.add(rr.getBody());
							} catch (BadRequestException e) {
								listResultFail.put(document.getObjectName(), HttpStatus.BAD_REQUEST);
							} catch (Exception e) {
								listResultFail.put(document.getObjectName(), HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
					}
				}

				listResult.put("success", success);
				listResult.put("error", listResultFail);

				return new ResponseEntity<Map<String, Object>>(listResult, HttpStatus.OK);

			} else {
				throw new BadRequestException();
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<DocumentoRespuesta> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar documento", notes = "Elimina un documento de una respuesta")
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

	@Override
	@RequestMapping(value = "/documentos/respuesta", method = RequestMethod.DELETE)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		log.debug("Documento a borrar :: [id=" + id + "]");

		try {

			DocumentoRespuesta item = mngrDocsRespuesta.fetch(id);

			{

				HashMap<String, Object> params = new HashMap<>();

				params.put("objectId", id.toString());

				Integer hasRefrences = Integer
						.valueOf(mngrDocsAsunto.uniqueResult("delDocRespHasRefs", params).toString());

				if (hasRefrences > 0) {
					throw new BadRequestException(errorMessages.getString("documentoAsociadoAAntefirma"));
				}

			}

			if (item != null)
				mngrDocsRespuesta.delete(item);
			else
				throw new BadRequestException("Documento no encontrado.");

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta documento", notes = "Consulta todos los documentos de una respuesta")
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

	@Override
	@RequestMapping(value = "/documentos/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) DocumentoRespuesta docRespuesta) {

		List<?> items = new ArrayList<DocumentoAsunto>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (docRespuesta.getObjectId() != null)
				restrictions.add(Restrictions.idEq(docRespuesta.getObjectId()));

			if (docRespuesta.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", docRespuesta.getIdArea()));

			if (docRespuesta.getIdAsunto() != null)
				restrictions.add(Restrictions.eq("idAsunto", docRespuesta.getIdAsunto()));

			if (docRespuesta.getIdRespuesta() != null)
				restrictions.add(Restrictions.eq("idRespuesta", docRespuesta.getIdRespuesta()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("fechaRegistro"));

			// * * * * * * * * * * * * * * * * * * * * * *

			items = mngrDocsRespuesta.search(restrictions, orders);

			log.debug(items);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Size Out >> " + items.size());

		return new ResponseEntity<List<?>>(items, HttpStatus.OK);

	}

	/**
	 * Obtiene la lista de Documentos de las Respuestas marcados para Firma.
	 *
	 * @param documentoRespuesta Identificador del Area del cual se van a obtener
	 *                           los Documentos
	 * @return lista de Documentos de las Respuestas marcados para Firma
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta documentos paraFirma", notes = "Consulta la lista de documentos marcados para firma")
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

	@RequestMapping(value = "/documentos/respuesta/para_firma", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<DocumentoRespuesta>> getDocumentosRespuestaParaFirma() throws Exception {

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);

		List<DocumentoRespuesta> items = getDocsRespuestaParaFirmaProcess(idUsuario, idArea);

		return new ResponseEntity<List<DocumentoRespuesta>>(items, HttpStatus.OK);

	}

	/**
	 * @param idArea
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected List<DocumentoRespuesta> getDocsRespuestaParaFirmaProcess(String idUsuario, Integer idArea)
			throws Exception {

		List<DocumentoRespuesta> items = new ArrayList<DocumentoRespuesta>();

		try {

			boolean verConfidencial = permisoController.verConfidencial(idUsuario, idArea);

//			String sql = "select da from DocumentoRespuesta da " //
//					+ " where (da.respuestaConsulta.asuntoConsulta.idArea = " + idArea //
//					+ " or da.respuestaConsulta.asuntoConsulta.idAreaDestino = " + idArea + ")" //
//					+ " and da.respuestaConsulta.asuntoConsulta.folioArea is not null "//
//					+ " and da.status= 'P' ";

			String sql = "select da from DocumentoRespuesta da "
					+ " join AsuntoConsulta as_ on da.idAsunto = as_.idAsunto where (as_.idArea = " + idArea
					+ " or as_.idAreaDestino = " + idArea + ") and as_.folioArea is not null "
					+ (!verConfidencial ? " and as_.confidencial = " + Boolean.FALSE : "")
					+ " and da.status in ('P', 'G') ";

			items = (List<DocumentoRespuesta>) mngrDocsRespuesta.execQuery(sql);

			for (DocumentoRespuesta doc : items) {

				try {
					// OBETENER INFO DEL ARCHIVO DEL REPO
					Map<String, Object> docProperties = EndpointDispatcher.getInstance()
							.getObjectProperties(doc.getObjectId());
					try {
						doc.setObjectName(
								((List<String>) docProperties.get(EnumPropertiesBase.CMIS_NAME.value())).get(0));
					} catch (Exception e) {

					}
					try {
						doc.setOwnerName(((List<String>) docProperties.get("owner_name")).get(0));
					} catch (Exception e) {

					}

					try {
						doc.setCheckout(((List<Boolean>) docProperties
								.get(EnumPropertiesDocument.CMIS_IS_VERSION_SERIES_CHECKED_OUT.value())).get(0));
					} catch (Exception e) {

					}
				} catch (Exception e) {

				}

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Size Out >> " + items.size());
		return items;
	}

	/**
	 * Obtener documentos respuesta.
	 *
	 * @param docRespuestaAux the doc respuesta
	 * @return the response entity
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documento respuesta", notes = "Obtiene la lista de documentos de una respuesta recibida")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/documentos/respuesta/obtener", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<DocumentoRespuestaAux>> obtenerDocumentosRespuesta(
			@RequestBody(required = true) DocumentoRespuestaAux docRespuestaAux) {

		List<DocumentoRespuestaAux> items = new ArrayList<DocumentoRespuestaAux>();

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (docRespuestaAux.getObjectId() != null)
				restrictions.add(Restrictions.idEq(docRespuestaAux.getObjectId()));

			if (docRespuestaAux.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", docRespuestaAux.getIdArea()));

			if (docRespuestaAux.getIdAsunto() != null)
				restrictions.add(Restrictions.eq("idAsunto", docRespuestaAux.getIdAsunto()));

			if (docRespuestaAux.getIdRespuesta() != null)
				restrictions.add(Restrictions.eq("idRespuesta", docRespuestaAux.getIdRespuesta()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("fechaRegistro"));

			// * * * * * * * * * * * * * * * * * * * * * *

			items = (List<DocumentoRespuestaAux>) mngrDocsRespuestaAux.search(restrictions, orders);

			log.debug(items);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Size Out >> " + items.size());

		return new ResponseEntity<List<DocumentoRespuestaAux>>(items, HttpStatus.OK);

	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Generar respuesta documentos adjuntos", notes = "Obtiene los documentos de una respuesta y genera una nueva respuesta")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/respuesta/recibida/allDocuments", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<DocumentoRespuestaAux>> getRespuestasRecibidasAllDocs(
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto) throws Exception {

		List<DocumentoRespuestaAux> items = new ArrayList<DocumentoRespuestaAux>();

		try {

			// * * * * * * * * * * * * * * * * * * * * * *

			List<Respuesta> respuestasRecibidas = respuestaController.getRespuestasRecibidas(idAsunto).getBody();

			List<Integer> idRespuestas = respuestasRecibidas.stream().map(Respuesta::getIdRespuesta)
					.collect(Collectors.toList());

			if (idRespuestas != null && !idRespuestas.isEmpty()) {

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.in("idRespuesta", idRespuestas));

				items = (List<DocumentoRespuestaAux>) mngrDocsRespuestaAux.search(restrictions);

				for (DocumentoRespuestaAux i : items) {

					i.setObjectName(EndpointDispatcher.getInstance().getObjectName(i.getObjectId()));

				}

				log.debug(items);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Size Out >> " + items.size());

		return new ResponseEntity<List<DocumentoRespuestaAux>>(items, HttpStatus.OK);

	}

	@RequestMapping(value = "/documentos/respuesta/addDocuments", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> addDocsRespuesta(
			@RequestBody(required = true) BodyAddDocuments body) throws Exception {

		try {

			Map<String, Object> resp = new HashMap<String, Object>();

			Respuesta respuesta = mngrRespuesta.fetch(body.getIdRespuesta());

			DocumentoRespuesta documento;
			for (DocumentoRespuestaAux drx : body.getDocs()) {
				documento = new DocumentoRespuesta();
				documento.setIdRespuesta(respuesta.getIdRespuesta());
				documento.setIdAsunto(respuesta.getIdAsunto());
				documento.setFileB64(EndpointDispatcher.getInstance().getObjectContentB64(drx.getObjectId()));
				documento.setObjectName(drx.getObjectName());
				documento.setIdArea(Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));

				save(documento);

			}

			return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	// Metodo para eliminar documentos de respuesta masivamente
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/eliminarDocumentoMasivo/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> deleteDocs(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {

		Serializable idRespuesta = (Serializable) params.get("idRespuesta");
		List<String> idsDocs = (List<String>) params.get("documentos");

		// Convertir/Castear de List<Map<Object, Object>> a <DocumentoRespuesta>
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<DocumentoRespuesta>> typeReference = new TypeReference<List<DocumentoRespuesta>>() {
		};
		List<DocumentoRespuesta> Docs = mapper.convertValue(idsDocs, typeReference);

		try {
			Map<String, Object> result = new HashMap<String, Object>();
			Respuesta respuesta = mngrRespuesta.fetch(idRespuesta);
			if (null == respuesta) {
				log.error(":: La respuesta no existe");
				throw new IllegalArgumentException(":: El respuesta no existe");
			}

			Set<String> documentosExitosos = new HashSet<>();
			Set<String> documentosFallidos = new HashSet<>();

			Docs.parallelStream().forEach(documento -> {
				try {
					delete(documento.getObjectId());
					documentosExitosos.add(documento.getObjectName());
				} catch (Exception e) {
					log.error(":: Error al intentar eliminar el documento de respuesta");
					documentosFallidos.add(documento.getObjectName() + e.getLocalizedMessage());
				}
			});

			result.put("success", documentosExitosos);
			result.put("fail", documentosFallidos);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	// Metodo para descargar documentos de respuesta masivamente
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/generarArchivoZip/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> generateZip(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {
		Instant star = Instant.now();

		Integer idRespuesta = (Integer) params.get("idRespuesta");
		List<Map<Object, Object>> Docs = (List<Map<Object, Object>>) params.get("documentos");

		// Convertir/Castear de List<Map<Object, Object>> a <DocumentoRespuesta>
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<DocumentoRespuesta>> typeReference = new TypeReference<List<DocumentoRespuesta>>() {
			/* */};
		List<DocumentoRespuesta> documentos = mapper.convertValue(Docs, typeReference);

		Map<String, Object> result = new HashMap<String, Object>();

		Set<String> documentosAdjuntos = new HashSet<>();
		
		File zipFile = null;
		try {
			for (DocumentoRespuesta documento : documentos) {
				try {
					// Obtenemos el contenido del documento
					ResponseEntity<Map<String, Object>> resp = repositoryController
							.getDocumentAsAdmin(documento.getObjectId());

					// si retorna información, se agrega.
					if (resp != null) {
						documentosAdjuntos.add(FileUtil
								.createTempFiles(resp.getBody().get("contentB64").toString(), documento.getObjectName())
								.toString());
					}

				} catch (Exception e) {
					log.error(":: Error, no se obtener el documento del repositorio");
				}
			}
			log.error(":: Total de documentos de la respuesta" + documentosAdjuntos.size());			
			try {
				// crear el archivo zip con los documentos generados
				log.debug(":: Iniciando la creacion del zip");
				String zipFileName = "documentosRespuesta_" + idRespuesta + ".zip";
				zipFile = FileUtil.zipFiles(zipFileName, documentosAdjuntos);

				result.put("contentB64", FileUtil.fileToStringB64(zipFile));
				result.put("type", "application/zip");
				result.put("name", zipFileName);

				// Eliminamos los documentos generados
				documentosAdjuntos.add(zipFile.getPath());
				
				Instant end = Instant.now();
				Duration duration = Duration.between(star, end);
				log.error("Duracion de generacion de zip:: " + duration);
				log.error("Archivo Zip creado exitosamente ::: Tamanio total: " + zipFile.length() + " bytes");
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				throw e;
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		} finally {
			FileUtil.deleteFiles(documentosAdjuntos);
			// Eliminamos los documentos generados
			if(zipFile != null)
				zipFile.delete();
		}

	}

}
