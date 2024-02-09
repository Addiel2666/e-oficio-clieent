/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;

import org.apache.chemistry.opencmis.commons.impl.jaxb.EnumPropertiesBase;
import org.apache.chemistry.opencmis.commons.impl.jaxb.EnumPropertiesDocument;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
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
import com.ecm.sigap.data.controller.util.CheckinObject;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoNotificacion;
import com.ecm.sigap.data.model.util.TipoPlantilla;
import com.ecm.sigap.util.CertificateUtility;
import com.ecm.sigap.util.CollectionUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.DocumentoAsunto}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class DocumentoAsuntoController extends CustomRestController implements RESTController<DocumentoAsunto> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(DocumentoAsuntoController.class);

	/**
	 * Referencia hacia el REST controller de {@link MailController}.
	 */
	@Autowired
	private MailController mailController;

	@Autowired
	private RepositoryController repositoryController;

	@Autowired
	private PermisoController permisoController;

	/**
	 * Referencia hacia el REST controller de {@link DocumentoCompartidoController}.
	 */
	@Autowired
	private DocumentoCompartidoController documentoCompartidoController;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/documentos/asunto", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> save(@RequestBody(required = true) DocumentoAsunto documento)
			throws Exception {

		try {
			IEndpoint endpoint = EndpointDispatcher.getInstance();

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

			if (!esSoloLectura(userId)) {
				log.debug("DOCUMENTO_ASUNTO A GUARDAR >> " + documento);

				Asunto asunto = mngrAsunto.fetch(documento.getIdAsunto());

				if (documento.getObjectId() == null) {

					// Validamos que se envio la informacion del documento a
					// guardar
					if (documento.getFileB64() == null) {
						log.error("El contenido del getFileB64 esta vacio por lo que se rechaza la peticion");
						return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
					}

					boolean isBase64 = Base64.isBase64(documento.getFileB64());
					if (!isBase64) {
						log.error("El getFileB64 del documento no es Base64, se rechaza la peticion");
						return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
					}

					// Suponemos que al no tener Content Id, es un Tramite que
					// aun no tiene folder en el repositorio por lo que se
					// guarda con el mismo Conten Id de su Asunto Padre
					//
					// Se replica este comportamiento de SIGAP 4

					if (null != asunto.getContentId()) {
						log.debug(
								"Se va a guardar el documento del asunto en el repositorio :: " + documento.toString());

						documento.setParentContentId(asunto.getContentId());

						File documento_ = FileUtil.createTempFile(documento.getFileB64());
						String parentFolderId = documento.getParentContentId();
						String nombreArchivo = documento.getObjectName();
						String tipoDoc = environment.getProperty("docTypeAdjuntoAsunto");
						Version verDoc = Version.MAYOR;
						String descDoc = documento.getObjectName();

						String newID = endpoint.saveDocumentoIntoId(parentFolderId, nombreArchivo, tipoDoc, verDoc,
								descDoc, documento_);

						documento_.delete();

						Map<String, Object> properties = new HashMap<>();
						// Obtenemos el User Name para asignarlo como el Owner
						// del documento
						properties.put("owner_name", userName);

						endpoint.setProperties(newID, properties);

						// AGREGAR ACL
						Map<String, String> additionalData = new HashMap<>();

						additionalData.put("idArea", documento.getIdArea().toString());

						// String aclName = "aclNameAdjuntoAsunto";
						String aclName = "aclNameAdjuntoAsuntoPublico";
						// Para el caso de los asuntos confidenciales, se le
						// asigna el ACL de Asuntos confidenciales
						if (asunto.getAsuntoDetalle().getConfidencial()) {
							aclName = "aclNameAdjuntoAsuntoConfidencial";
						}
						// Aplicando ACL en 3 intentos.
						log.debug("Aplicando el ACL " + aclName + " a documento adjunto ");
						try {
							for (int i = 0; i <= 3; i++) {
								boolean resultSetAcl = endpoint.setACL(newID, environment.getProperty(aclName),
										additionalData);
								if (resultSetAcl) {
									break;
								} else if (resultSetAcl == Boolean.FALSE && i == 3) {
									log.debug(">>> Se intento Aplicar el ACL " + aclName + " a documento adjunto " + i
											+ " veces");
									throw new Exception();
								}
							}
						} catch (Exception e) {
							throw new Exception("Error agregando permisos al documento adjunto.");
						}

						documento.setObjectId(newID);

					} else {
						throw new BadRequestException(errorMessages.getString("asuntoSinFolder"));
					}

					// se fuerza a tener los valores por default para version publica
					if (documento.getEnabledToSend() == null)
						documento.setEnabledToSend(true);
					if (documento.getPublicVersion() == null)
						documento.setPublicVersion(false);

					mngrDocsAsunto.save(documento);

					// se elimina el archivo en base64 para aligerar el JSON
					documento.setFileB64(null);
					documento.setOwnerName(userName);

				} else {

					if (null == documento.getIdAsunto()) {
						throw new BadRequestException(errorMessages.getString("noIdAsunto"));
					}

					DocumentoAsunto doc = new DocumentoAsunto();
					doc.setObjectId(documento.getObjectId());
					doc.setIdAsunto(documento.getIdAsunto());

					DocumentoAsunto documentoGuardado = mngrDocsAsunto.fetch(doc);

					if (StringUtils.isNotBlank(documento.getPathPublished())) {
						documento.setPathPublished(documento.getPathPublished());
					}

					if (null != documentoGuardado && documentoGuardado.getIdArea().equals(documento.getIdArea())) {
						log.debug("Actualizando la informacion del documento " + documento);
						mngrDocsAsunto.update(documento);
					} else {
						log.debug("Guardando la informacion del nuevo documento " + documento);
						// se fuerza a tener los valores por default para version publica
						if (documento.getEnabledToSend() == null)
							documento.setEnabledToSend(true);
						if (documento.getPublicVersion() == null)
							documento.setPublicVersion(false);
						mngrDocsAsunto.save(documento);
					}

					// se elimina el archivo en base64 para aligerar el JSON
					documento.setFileB64(null);
					documento.setOwnerName(userName);
				}

				return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);
			} else {
				return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	private List<DocumentoAsunto> parseJsonDocs(String jsonDocs)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper.readValue(jsonDocs, new TypeReference<List<DocumentoAsunto>>() {
		});
	}

	private List<DocumentoAsunto> parseJsonDocsJ(String jsonDocs)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonDocsFinal = "[" + jsonDocs + "]";

		return mapper.readValue(jsonDocsFinal, new TypeReference<List<DocumentoAsunto>>() {
		});
	}

	@RequestMapping(value = "/documentos/asunto/mult", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<DocumentoAsunto> saveMultipart(MultipartHttpServletRequest request,
			@RequestParam("documentos") String documentos) throws Exception {
		List<MultipartFile> files = null;
		List<DocumentoAsunto> docs = null;
		files = request.getFiles("files");
		docs = parseJsonDocs(documentos);
		DocumentoAsunto documento = docs.get(0);
		try {
			IEndpoint endpoint = EndpointDispatcher.getInstance();

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

			if (!esSoloLectura(userId)) {
				log.debug("DOCUMENTO_ASUNTO A GUARDAR >> " + documento);

				Asunto asunto = mngrAsunto.fetch(documento.getIdAsunto());

				if (documento.getObjectId() == null) {

					// Validamos que se envio la informacion del documento a
					// guardar
					if (files.isEmpty()) {
						log.error("El contenido del documento esta vacio por lo que se rechaza la peticion");
						return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
					}

					// Suponemos que al no tener Content Id, es un Tramite que
					// aun no tiene folder en el repositorio por lo que se
					// guarda con el mismo Conten Id de su Asunto Padre
					//
					// Se replica este comportamiento de SIGAP 4

					if (null != asunto.getContentId()) {
						log.debug(
								"Se va a guardar el documento del asunto en el repositorio :: " + documento.toString());

						documento.setParentContentId(asunto.getContentId());
						// donde se obtiene el archivo
						File documento_ = FileUtil.createTempFile(files.get(0));
						String parentFolderId = documento.getParentContentId();
						String nombreArchivo = documento.getObjectName();
						String tipoDoc = environment.getProperty("docTypeAdjuntoAsunto");
						Version verDoc = Version.MAYOR;
						String descDoc = documento.getObjectName();

						String newID = endpoint.saveDocumentoIntoId(parentFolderId, nombreArchivo, tipoDoc, verDoc,
								descDoc, documento_);

						documento_.delete();

						Map<String, Object> properties = new HashMap<>();
						// Obtenemos el User Name para asignarlo como el Owner
						// del documento
						properties.put("owner_name", userName);

						endpoint.setProperties(newID, properties);

						// AGREGAR ACL
						Map<String, String> additionalData = new HashMap<>();

						additionalData.put("idArea", documento.getIdArea().toString());

						String aclName = "aclNameAdjuntoAsunto";
						// Para el caso de los asuntos confidenciales, se le
						// asigna el ACL de Asuntos confidenciales
						if (asunto.getAsuntoDetalle().getConfidencial()) {
							aclName = "aclNameAdjuntoAsuntoConfidencial";
						}
						// Aplicando ACL en 3 intentos.
						log.debug("Aplicando el ACL " + aclName + " a documento adjunto ");
						try {
							for (int i = 0; i <= 3; i++) {
								boolean resultSetAcl = endpoint.setACL(newID, environment.getProperty(aclName),
										additionalData);
								if (resultSetAcl) {
									break;
								} else if (resultSetAcl == Boolean.FALSE && i == 3) {
									log.debug(">>> Se intento Aplicar el ACL " + aclName + " a documento adjunto " + i
											+ " veces");
									throw new Exception();
								}
							}
						} catch (Exception e) {
							throw new Exception("Error agregando permisos al documento adjunto.");
						}

						documento.setObjectId(newID);

					} else {
						throw new BadRequestException(errorMessages.getString("asuntoSinFolder"));
					}

					mngrDocsAsunto.save(documento);

					// se elimina el archivo en base64 para aligerar el JSON
					documento.setFileB64(null);
					documento.setOwnerName(userName);

				} else {

					if (null == documento.getIdAsunto()) {
						throw new BadRequestException(errorMessages.getString("noIdAsunto"));
					}

					DocumentoAsunto doc = new DocumentoAsunto();
					doc.setObjectId(documento.getObjectId());
					doc.setIdAsunto(documento.getIdAsunto());

					DocumentoAsunto documentoGuardado = mngrDocsAsunto.fetch(doc);

					if (StringUtils.isNotBlank(documento.getPathPublished())) {
						documento.setPathPublished(documento.getPathPublished());
					}

					if (null != documentoGuardado && documentoGuardado.getIdArea().equals(documento.getIdArea())) {
						log.debug("Actualizando la informacion del documento " + documento);
						mngrDocsAsunto.update(documento);
					} else {
						log.debug("Guardando la informacion del nuevo documento " + documento);
						mngrDocsAsunto.save(documento);
					}

					// se elimina el archivo en base64 para aligerar el JSON
					documento.setFileB64(null);
					documento.setOwnerName(userName);
				}

				return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);
			} else {
				return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@Deprecated
	private ResponseEntity<DocumentoAsunto> saveFast(MultipartFile request, DocumentoAsunto documento)
			throws Exception {

		try {
			IEndpoint endpoint = EndpointDispatcher.getInstance();

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

			if (!esSoloLectura(userId)) {
				log.debug("DOCUMENTO_ASUNTO A GUARDAR >> " + documento);

				Asunto asunto = mngrAsunto.fetch(documento.getIdAsunto());

				if (documento.getObjectId() == null) {

					// Validamos que se envio la informacion del documento a
					// guardar
					if (request.isEmpty()) {
						log.error("El contenido del documento esta vacio por lo que se rechaza la peticion");
						return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
					}

					// Suponemos que al no tener Content Id, es un Tramite que
					// aun no tiene folder en el repositorio por lo que se
					// guarda con el mismo Conten Id de su Asunto Padre
					//
					// Se replica este comportamiento de SIGAP 4

					if (null != asunto.getContentId()) {
						log.debug(
								"Se va a guardar el documento del asunto en el repositorio :: " + documento.toString());

						documento.setParentContentId(asunto.getContentId());
						// donde se obtiene el archivo
						File documento_ = FileUtil.createTempFile(request);
						// File documento_ = FileUtil.createTempFile(documento.getFileB64());
						String parentFolderId = documento.getParentContentId();
						String nombreArchivo = documento.getObjectName();
						String tipoDoc = environment.getProperty("docTypeAdjuntoAsunto");
						Version verDoc = Version.MAYOR;
						String descDoc = documento.getObjectName();

						String newID = endpoint.saveDocumentoIntoId(parentFolderId, nombreArchivo, tipoDoc, verDoc,
								descDoc, documento_);

						documento_.delete();

						Map<String, Object> properties = new HashMap<>();
						// Obtenemos el User Name para asignarlo como el Owner
						// del documento
						properties.put("owner_name", userName);

						endpoint.setProperties(newID, properties);

						// AGREGAR ACL
						Map<String, String> additionalData = new HashMap<>();

						additionalData.put("idArea", documento.getIdArea().toString());

						String aclName = "aclNameAdjuntoAsunto";
						// Para el caso de los asuntos confidenciales, se le
						// asigna el ACL de Asuntos confidenciales
						if (asunto.getAsuntoDetalle().getConfidencial()) {
							aclName = "aclNameAdjuntoAsuntoConfidencial";
						}
						// Aplicando ACL en 3 intentos.
						log.debug("Aplicando el ACL " + aclName + " a documento adjunto ");
						try {
							for (int i = 0; i <= 3; i++) {
								boolean resultSetAcl = endpoint.setACL(newID, environment.getProperty(aclName),
										additionalData);
								if (resultSetAcl) {
									break;
								} else if (resultSetAcl == Boolean.FALSE && i == 3) {
									log.debug(">>> Se intento Aplicar el ACL " + aclName + " a documento adjunto " + i
											+ " veces");
									throw new Exception();
								}
							}
						} catch (Exception e) {
							throw new Exception("Error agregando permisos al documento adjunto.");
						}

						documento.setObjectId(newID);

					} else {
						throw new BadRequestException(errorMessages.getString("asuntoSinFolder"));
					}

					mngrDocsAsunto.save(documento);

					// se elimina el archivo en base64 para aligerar el JSON
					documento.setFileB64(null);
					documento.setOwnerName(userName);

				} else {

					if (null == documento.getIdAsunto()) {
						throw new BadRequestException(errorMessages.getString("noIdAsunto"));
					}

					DocumentoAsunto doc = new DocumentoAsunto();
					doc.setObjectId(documento.getObjectId());
					doc.setIdAsunto(documento.getIdAsunto());

					DocumentoAsunto documentoGuardado = mngrDocsAsunto.fetch(doc);

					if (StringUtils.isNotBlank(documento.getPathPublished())) {
						documento.setPathPublished(documento.getPathPublished());
					}

					if (null != documentoGuardado && documentoGuardado.getIdArea().equals(documento.getIdArea())) {
						log.debug("Actualizando la informacion del documento " + documento);
						mngrDocsAsunto.update(documento);
					} else {
						log.debug("Guardando la informacion del nuevo documento " + documento);
						mngrDocsAsunto.save(documento);
					}

					// se elimina el archivo en base64 para aligerar el JSON
					documento.setFileB64(null);
					documento.setOwnerName(userName);
				}

				return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);
			} else {
				return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
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

	@ApiOperation(value = "Guardar documento", notes = "Guarda un documento al asunto")
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

	@RequestMapping(value = "/documentos/asunto/list/multipart", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveDocumentListMultipart(
			MultipartHttpServletRequest request, //
			@RequestParam("documentos") String documentos) throws Exception {

		Instant start = Instant.now();

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		boolean esSoloLectura = !esSoloLectura(userId);
		String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

		Asunto asunto = null;

		File documento_;
		String parentFolderId;
		String nombreArchivo;
		String tipoDoc;
		Version verDoc = Version.MAYOR;
		String descDoc;
		String newID;
		Map<String, Object> properties;
		Map<String, String> additionalData;
		String aclName;

		DocumentoAsunto doc;
		DocumentoAsunto documentoGuardado;

		try {

			List<MultipartFile> files = null;
			List<DocumentoAsunto> docs = null;

			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();

			if (!documentos.isEmpty()) {

				files = request.getFiles("files");
				docs = parseJsonDocsJ(documentos);

				for (DocumentoAsunto documento : docs) {

					for (MultipartFile fileunico : files) {

						if (fileunico.getOriginalFilename().equals(documento.getObjectName())) {
							try {

								if (esSoloLectura) {
									log.debug("DOCUMENTO_ASUNTO A GUARDAR >> " + documento);

									// se calcula en el primer loop, despues solo se reusa
									if (asunto == null)
										asunto = mngrAsunto.fetch(documento.getIdAsunto());
																		
									documento.setIdOrigen(asunto.getIdAsuntoOrigen());

									if (documento.getObjectId() == null) {

										// Validamos que se envio la informacion del documento a
										// guardar
										if (fileunico.isEmpty()) {
											log.error(
													"El contenido del documento esta vacio por lo que se rechaza la peticion");

											listResultFail.put(documento.getObjectName(), HttpStatus.BAD_REQUEST);
											continue;
										}

										// Suponemos que al no tener Content Id, es un Tramite que
										// aun no tiene folder en el repositorio por lo que se
										// guarda con el mismo Conten Id de su Asunto Padre
										//
										// Se replica este comportamiento de SIGAP 4

										if (null != asunto.getContentId()) {
											log.debug("Se va a guardar el documento del asunto en el repositorio :: "
													+ documento.toString());

											documento.setParentContentId(asunto.getContentId());
											// donde se obtiene el archivo
											documento_ = FileUtil.createTempFile(fileunico);
											parentFolderId = documento.getParentContentId();
											nombreArchivo = documento.getObjectName();
											tipoDoc = environment.getProperty("docTypeAdjuntoAsunto");
											descDoc = documento.getObjectName();

											newID = endpoint.saveDocumentoIntoId(parentFolderId, nombreArchivo, tipoDoc,
													verDoc, descDoc, documento_);

											documento_.delete();

											properties = new HashMap<>();
											// Obtenemos el User Name para asignarlo como el Owner
											// del documento
											properties.put("owner_name", userName);

											endpoint.setProperties(newID, properties);

											// AGREGAR ACL
											additionalData = new HashMap<>();
											additionalData.put("idArea", documento.getIdArea().toString());

											// aclName = "aclNameAdjuntoAsunto";
											aclName = "aclNameAdjuntoAsuntoPublico";
											// Para el caso de los asuntos confidenciales, se le
											// asigna el ACL de Asuntos confidenciales
											if (asunto.getAsuntoDetalle().getConfidencial()) {
												aclName = "aclNameAdjuntoAsuntoConfidencial";
											}

											// Aplicando ACL en 3 intentos.
											log.debug("Aplicando el ACL " + aclName + " a documento adjunto ");
											try {
												for (int i = 0; i <= 3; i++) {
													boolean resultSetAcl = endpoint.setACL(newID,
															environment.getProperty(aclName), additionalData);
													if (resultSetAcl) {
														break;
													} else if (resultSetAcl == Boolean.FALSE && i == 3) {
														log.debug(">>> Se intento Aplicar el ACL " + aclName
																+ " a documento adjunto " + i + " veces");
														throw new Exception();
													}
												}
											} catch (Exception e) {
												throw new Exception("Error agregando permisos al documento adjunto.");
											}

											documento.setObjectId(newID);

										} else {
											throw new BadRequestException(errorMessages.getString("asuntoSinFolder"));
										}

										// se fuerza a tener los valores por default para version publica
										documento.setEnabledToSend(true);
										documento.setPublicVersion(false);

										mngrDocsAsunto.save(documento);

										// se elimina el archivo en base64 para aligerar el JSON
										documento.setFileB64(null);
										documento.setOwnerName(userName);

									} else {

										if (null == documento.getIdAsunto()) {
											throw new BadRequestException(errorMessages.getString("noIdAsunto"));
										}

										doc = new DocumentoAsunto();
										doc.setObjectId(documento.getObjectId());
										doc.setIdAsunto(documento.getIdAsunto());

										documentoGuardado = mngrDocsAsunto.fetch(doc);

										if (StringUtils.isNotBlank(documento.getPathPublished())) {
											documento.setPathPublished(documento.getPathPublished());
										}

										if (null != documentoGuardado
												&& documentoGuardado.getIdArea().equals(documento.getIdArea())) {
											log.debug("Actualizando la informacion del documento " + documento);
											mngrDocsAsunto.update(documento);
										} else {
											log.debug("Guardando la informacion del nuevo documento " + documento);
											mngrDocsAsunto.save(documento);
										}

										// se elimina el archivo en base64 para aligerar el JSON
										documento.setFileB64(null);
										documento.setOwnerName(userName);
									}

									success.add(documento);

								} else {

									listResultFail.put(documento.getObjectName(), HttpStatus.BAD_REQUEST);

								}

							} catch (BadRequestException e) {

								listResultFail.put(documento.getObjectName(), HttpStatus.BAD_REQUEST);

							} catch (Exception e) {

								listResultFail.put(documento.getObjectName(), HttpStatus.INTERNAL_SERVER_ERROR);

							}
						}
					}

				}

				listResult.put("success", success);
				listResult.put("error", listResultFail);

				log.debug("Duracion del proceso de carga de documentos :: cantidad de archivos " + docs.size()
						+ " :: tiempo " + Duration.between(start, Instant.now()));

				return new ResponseEntity<Map<String, Object>>(listResult, HttpStatus.OK);

			} else {
				throw new BadRequestException();
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
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

	@ApiOperation(value = "Guarda documento repositorio", notes = "Guarda un documento que se tiene en el repositorio")
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

	@RequestMapping(value = "/documentos/asunto/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveDocumentList(
			@RequestBody(required = true) List<DocumentoAsunto> documentos) throws Exception {

		try {
			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();

			if (!documentos.isEmpty()) {

				for (DocumentoAsunto documentoAsunto : documentos) {
					try {
						// se fuerza a tener los valores por default para version publica
						documentoAsunto.setEnabledToSend(true);
						documentoAsunto.setPublicVersion(false);
						ResponseEntity<DocumentoAsunto> rr = save(documentoAsunto);
						success.add(rr.getBody());

					} catch (BadRequestException e) {

						listResultFail.put(documentoAsunto.getObjectName(), HttpStatus.BAD_REQUEST);

					} catch (Exception e) {

						listResultFail.put(documentoAsunto.getObjectName(), HttpStatus.INTERNAL_SERVER_ERROR);

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

	/**
	 * Guardamos los documentos de un tramite, para este caso solo se guarda la
	 * informacion en la BD y cuando se acepta el Tramite se guarda la info en el
	 * repo
	 *
	 * @param documento Objeto del tipo {@link DocumentoAsunto}
	 * @return Documento guardado
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	@RequestMapping(value = "/documentos/tramite", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> saveTramite(
			@RequestBody(required = true) DocumentoAsunto documento) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("DOCUMENTO TRAMITE A GUARDAR >> " + documento);

				if (documento.getObjectId() != null) {

					mngrDocsAsunto.save(documento);
					return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);

				} else {

					throw new BadRequestException();

				}

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
	@RequestMapping(value = "/documentos/asunto", method = RequestMethod.GET)
	public ResponseEntity<DocumentoAsunto> get(@RequestParam(value = "idAsunto", required = true) Serializable idAsunto,
			@RequestParam(value = "contentId", required = true) Serializable contentId) {

		DocumentoAsunto documento = new DocumentoAsunto();
		try {

			log.debug("Documento a consultar :: [idAsunto=" + idAsunto + "],[contentId=" + contentId + "]");

			documento.setIdAsunto(Integer.valueOf((String) idAsunto));
			documento.setObjectId((String) contentId);

			documento = mngrDocsAsunto.fetch(documento);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + documento);

		return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);
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

	@ApiOperation(value = "Eliminar documento", notes = "Elimina un documento del asunto")
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

	@RequestMapping(value = "/documentos/asunto", method = RequestMethod.DELETE)
	public void delete(@RequestParam(value = "idAsunto", required = true) Serializable idAsunto,
			@RequestParam(value = "contentId", required = true) Serializable contentId) throws Exception {

		log.debug("Documento a borrar :: [idAsunto=" + idAsunto + "],[contentId=" + contentId + "]");

		try {

			HashMap<String, Object> params = new HashMap<>();

			params.put("objectId", contentId.toString());
			params.put("idAsunto", Integer.valueOf(idAsunto.toString()));

			Asunto asunto = mngrAsunto.fetch(Integer.valueOf(idAsunto.toString()));

			Integer hasRefrencesTramite = Integer
					.valueOf(mngrDocsAsunto.uniqueResult("delDocAsuHasRefsTramite", params).toString());

			if (hasRefrencesTramite > 0) {
				if (asunto.getTipoAsunto().equals(TipoAsunto.ASUNTO))
					throw new ConstraintViolationException(errorMessages.getString("documentoAsociadoATramite"),
							new HashSet<ConstraintViolation<Serializable>>());
				else
					throw new ConstraintViolationException(errorMessages.getString("documentoAsociadoAAsuntoNoPropio"),
							new HashSet<ConstraintViolation<Serializable>>());
			}

			Integer hasRefrencesFirma = Integer
					.valueOf(mngrDocsAsunto.uniqueResult("delDocAsuHasRefsFirma", params).toString());

			if (hasRefrencesFirma > 0) {

				throw new ConstraintViolationException(errorMessages.getString("documentoAsociadoAFirma"),
						new HashSet<ConstraintViolation<Serializable>>());

			}

			params.remove("idAsunto");

			Integer hasRefrencesAntefirma = Integer
					.valueOf(mngrDocsAsunto.uniqueResult("delDocAsuHasRefsAntefirma", params).toString());

			if (hasRefrencesAntefirma > 0) {

				throw new ConstraintViolationException(errorMessages.getString("documentoAsociadoAAntefirma"),
						new HashSet<ConstraintViolation<Serializable>>());

			}

			DocumentoAsunto documento = new DocumentoAsunto();

			documento.setObjectId(String.valueOf((String) contentId));
			documento.setIdAsunto(Integer.valueOf(idAsunto.toString()));

			// Buscamos el documento a Eliminar
			documento = mngrDocsAsunto.fetch(documento);

			try {
				// si es una versión publica unica de un documento original,
				// se marca como enviable el original
				if (documento.getObjectIdOrigen() != null) {
					if (isUnique(documento))
						markEnableDoc(documento);
				}
			} catch (Exception e) {
				log.error("Error :: No se pudo consultar si existen mas documentos con el mismo objectIdOrigen");
			}

			if (documento != null) {
				mngrDocsAsunto.delete(documento);
			} else {

				throw new ConstraintViolationException(errorMessages.getString("docAsuntoNotFound"),
						new HashSet<ConstraintViolation<Serializable>>());

			}

			log.debug("Documento eliminado exitosamente !!");

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta documentos firma", notes = "Consulta los documentos para firma")
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

	@RequestMapping(value = "/documentos/asunto/para_firma", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> getDocumentosAsuntoParaFirma() throws Exception {

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);

		List<DocumentoAsunto> itemsResult = getDocAsuntoParaFirmaProcess(idUsuario, idArea);

		return new ResponseEntity<List<?>>(itemsResult, HttpStatus.OK);

	}

	/**
	 * @param idArea
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected List<DocumentoAsunto> getDocAsuntoParaFirmaProcess(String idUsuario, Integer idArea) throws Exception {
		List<DocumentoAsunto> items = new ArrayList<DocumentoAsunto>();
		List<DocumentoAsunto> itemsResult = new ArrayList<DocumentoAsunto>();

		// log.info("Parametros de busqueda :: " + documentoAsunto);

		try {

			boolean verConfidencial = permisoController.verConfidencial(idUsuario, idArea);
			// * * * * * * * * * * * * * * * * * * * * * *

			String sql = "select da from DocumentoAsunto da " //
					+ " where (da.asuntoConsulta.idArea = " + idArea //
					+ " or da.asuntoConsulta.idAreaDestino = " + idArea + ")" //
					+ " and da.asuntoConsulta.folioArea is not null "//
					+ (!verConfidencial ? " and da.asuntoConsulta.confidencial = " + Boolean.FALSE : "")
					+ " and da.status in ('P', 'G','H','J') ";

			items = (List<DocumentoAsunto>) mngrDocsAsunto.execQuery(sql);

			// FILTRA EL RESULTADO DE LA CONSULTA
			for (DocumentoAsunto doctoAsunto : items) {

				// SI El ASUNTO DEL DOUMENTO ES TIPO ASUNTO COMPARA EL IDAREA
				// CON EL IDAREA DEL ASUNTO
				if (doctoAsunto.getAsuntoConsulta().getTipoAsunto().equals(TipoAsunto.ASUNTO.getValue())
						&& doctoAsunto.getAsuntoConsulta().getIdArea().equals(idArea)) {

					itemsResult.add(doctoAsunto);

					// SI EL ASUNTO ES DE TIPO TRAMITE COMPARA EL IDAREA CON EL
					// IDAREA DESTINO DEL ASUNTO

				} else if (!doctoAsunto.getAsuntoConsulta().getTipoAsunto().equals(TipoAsunto.ASUNTO.getValue())
						&& doctoAsunto.getAsuntoConsulta().getIdAreaDestino().equals(idArea)) {

					itemsResult.add(doctoAsunto);

				}

			}
			// COMPLETA INFORMACION DEL OJETO DocumentoAsunto CONSULTANDO EL
			// REPO
			for (DocumentoAsunto documentoAsunto : itemsResult) {
				try {
					// OBETENER INFO DEL ARCHIVO DEL REPO
					Map<String, Object> docProperties = EndpointDispatcher.getInstance()
							.getObjectProperties(documentoAsunto.getObjectId());
					try {
						documentoAsunto.setObjectName(
								((List<String>) docProperties.get(EnumPropertiesBase.CMIS_NAME.value())).get(0));
					} catch (Exception e) {

					}
					try {
						documentoAsunto.setOwnerName(((List<String>) docProperties.get("owner_name")).get(0));
					} catch (Exception e) {

					}

					try {
						documentoAsunto.setCheckout(((List<Boolean>) docProperties
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

		log.debug(" Size Out >> " + itemsResult.size());
		return itemsResult;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta documentos", notes = "Consulta los documentos de un asunto")
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
	@Override
	@RequestMapping(value = "/documentos/asunto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) DocumentoAsunto item) {

		List<DocumentoAsunto> items = new ArrayList<DocumentoAsunto>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (item.getGubernamental() != null)
				restrictions.add(Restrictions.eq("gubernamental", item.getGubernamental()));

			if (null != item.getObjectId()) {
				restrictions.add(Restrictions.eq("objectId", item.getObjectId()));
			}

			if (item.getFechaRegistro() != null)
				restrictions.add(Restrictions.eq("fechaRegistro", item.getFechaRegistro()));

			if (item.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", item.getIdArea()));

			if (item.getIdAsunto() != null)
				restrictions.add(Restrictions.eq("idAsunto", item.getIdAsunto()));

			if (item.getObjectName() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("objectName", item.getObjectName(), MatchMode.ANYWHERE));

			if (item.getOwnerName() != null)
				restrictions.add(Restrictions.eq("ownerName", item.getOwnerName()));

			if (item.getStatus() != null)
				restrictions.add(Restrictions.eq("status", item.getStatus()));

			if (StringUtils.isNotBlank(item.getPathPublished()))
				restrictions.add(Restrictions
						.and(EscapedLikeRestrictions.ilike("pathPublished", item.getPathPublished(), MatchMode.START)));

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.desc("fechaRegistro"));

			// * * * * * * * * * * * * * * * * * * * * * *

			items = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (DESC) * * * * * *
			Collections.sort(items, new Comparator<DocumentoAsunto>() {
				@Override
				public int compare(DocumentoAsunto d1, DocumentoAsunto d2) {
					return d2.getFechaRegistro().compareTo(d1.getFechaRegistro());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Size Out >> " + items.size());

		return new ResponseEntity<List<?>>(items, HttpStatus.OK);

	}

	/**
	 * @param documentoAsunto
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Marcar Principal", notes = "Marca un documento de un asunto como principal")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/documento/asunto/marcarPrincipal", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> marcarPrincipal(
			@RequestBody(required = true) DocumentoAsunto documentoAsunto) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> DocumentoAsunto a marcar como principal >> " + documentoAsunto);
				List<DocumentoAsunto> docAsuntoResult = new ArrayList<>();

				if (documentoAsunto.getIdAsunto() != null && documentoAsunto.getObjectId() != null) {

					List<Criterion> restrictions = new ArrayList<Criterion>();
					restrictions.add(Restrictions.eq("idAsunto", documentoAsunto.getIdAsunto()));
					List<DocumentoAsunto> items = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

					items.forEach(item -> {
						if (documentoAsunto.getIdAsunto().equals(item.getIdAsunto())
								&& documentoAsunto.getObjectId().equalsIgnoreCase(item.getObjectId())) {
							item.setGubernamental(true);
							docAsuntoResult.add(item);
						} else {
							item.setGubernamental(false);
						}
						mngrDocsAsunto.update(item);
					});
					return new ResponseEntity<DocumentoAsunto>(docAsuntoResult.get(0), HttpStatus.OK);
				} else {
					return new ResponseEntity<DocumentoAsunto>(documentoAsunto, HttpStatus.BAD_REQUEST);
				}

			} else {
				return new ResponseEntity<DocumentoAsunto>(documentoAsunto, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Desmarcar principal", notes = "Desmarca un documento de un asunto como principal")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/documento/asunto/removerPrincipal", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> removerPrincipal(
			@RequestBody(required = true) DocumentoAsunto documentoAsunto) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> DocumentoAsunto a remover como principal >> " + documentoAsunto);
				List<DocumentoAsunto> docAsuntoResult = new ArrayList<>();

				if (documentoAsunto.getIdAsunto() != null && documentoAsunto.getObjectId() != null) {

					List<Criterion> restrictions = new ArrayList<Criterion>();
					restrictions.add(Restrictions.eq("idAsunto", documentoAsunto.getIdAsunto()));
					List<DocumentoAsunto> items = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

					items.forEach(item -> {
						if (documentoAsunto.getIdAsunto().equals(item.getIdAsunto())
								&& documentoAsunto.getObjectId().equalsIgnoreCase(item.getObjectId())) {
							item.setGubernamental(false);
							docAsuntoResult.add(item);
						} else {
							item.setGubernamental(false);
						}
						mngrDocsAsunto.update(item);
					});
					return new ResponseEntity<DocumentoAsunto>(docAsuntoResult.get(0), HttpStatus.OK);
				} else {
					return new ResponseEntity<DocumentoAsunto>(documentoAsunto, HttpStatus.BAD_REQUEST);
				}

			} else {
				return new ResponseEntity<DocumentoAsunto>(documentoAsunto, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Fue reemplazado por el metodo get(serializable, serializable)
	 */
	@Override
	public ResponseEntity<DocumentoAsunto> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Fue reemplazado por el metodo delete(Iteger, Serializable)
	 */
	@Override
	public void delete(Serializable id) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param body
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Seleccion documento tramite", notes = "Selecciona un documento de un tramite que este cancelado y no es de tipo acta")
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
	@RequestMapping(value = "/documentos/tramite", method = RequestMethod.POST)
	public void updateDocsTramite(@RequestBody HashMap<String, Object> body) throws Exception {

		try {
			Integer idTramite = (Integer) body.get("idAsunto");

			List<String> documentos = (List<String>) body.get("docsTramite");

			Asunto asunto = mngrAsunto.fetch(idTramite);

			if (asunto == null)
				new BadRequestException();

			List<String> documentosAsuntoExistentes;
			List<DocumentoAsunto> list;

			{
				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions.eq("idAsunto", idTramite));

				list = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

				documentosAsuntoExistentes = new ArrayList<>();

				for (DocumentoAsunto d : list) {
					documentosAsuntoExistentes.add(d.getObjectId());
				}

			}

			List<String> quitar = (List<String>) CollectionUtil.substract(documentosAsuntoExistentes, documentos);

			List<String> agregar = (List<String>) CollectionUtil.substract(documentos, documentosAsuntoExistentes);

			if (quitar.isEmpty() && agregar.isEmpty())
				return;

			for (String id : quitar) {
				for (DocumentoAsunto d : list) {
					if (id.equalsIgnoreCase(d.getObjectId())) {

						mngrDocsAsunto.delete(d);
						continue;

					}
				}
			}

			for (String id : agregar) {

				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions.eq("idAsunto", asunto.getIdAsuntoPadre()));
				restrictions.add(Restrictions.eq("objectId", id));

				List<DocumentoAsunto> toAdd = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

				for (DocumentoAsunto d : toAdd) {

					d.setIdAsunto(idTramite);

					mngrDocsAsunto.save(d);
				}

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * @param documento
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Version publica", notes = "Guarda la version publica de un documento")
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

	@RequestMapping(value = "/documento/asunto/public/version", method = RequestMethod.PUT)
	public ResponseEntity<DocumentoAsunto> guardarVersionPublica(@RequestBody DocumentoAsunto documento)
			throws Exception {
		try {
			if (documento.isVersionable()) {
				// Entra aqui cuando SI se selecciona el check de versionar en la version
				// publica entonces SI SE PROCEDE A VERSIONAR EL DOC.
				Boolean isCheckedOut = EndpointDispatcher.getInstance().checkOut(documento.getObjectId());
				File documento_ = FileUtil.createTempFile(documento.getFileB64());
				if (isCheckedOut) {
					mngrDocsAsunto.delete(documento);
					List<Map<String, String>> newObjectId = EndpointDispatcher.getInstance().checkIn(
							documento.getObjectId(), Version.MAYOR, "Version replicada", documento.getObjectName(),
							documento_);
					documento_.delete();
					String id = newObjectId.get(0).get("documentoId");
					documento.setObjectId(id);
					documento.setPublicVersion(true);
					mngrDocsAsunto.save(documento);
					return ResponseEntity.ok(documento);
				} else {
					if (null != documento_ && documento_.exists())
						documento_.delete();
					return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.CONFLICT);
				}

			} else {
				// Entra aqui cuando NO se selecciona el check de versionar en la version
				// publica entonces NO SE PROCEDE A VERSIONAR EL DOC, SOLO A ACTUALIZARLO.
				Boolean isCheckedOut = EndpointDispatcher.getInstance().checkOut(documento.getObjectId());
				File documento_ = FileUtil.createTempFile(documento.getFileB64());
				if (isCheckedOut) {
					List<Map<String, String>> objectId = EndpointDispatcher.getInstance().checkIn(
							documento.getObjectId(), Version.NONE, "Version replicada", documento.getObjectName(),
							documento_);
					String id = objectId.get(0).get("documentoId");
					documento.setObjectId(id);
					documento.setPublicVersion(true);
					documento.setEnabledToSend(false);
					mngrDocsAsunto.update(documento);
					// BORRAR TMP
					documento_.delete();

					return ResponseEntity.ok(documento);
				} else {
					if (null != documento_ && documento_.exists())
						documento_.delete();
					return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.CONFLICT);
				}

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * @param idAsunto
	 * @param contentId
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Despublicar documento", notes = "Elimina la publicacion de un documento")
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

	@RequestMapping(value = "/documento/asunto/publish", method = RequestMethod.DELETE)
	public ResponseEntity<DocumentoAsunto> removerDocumentoPublicado(
			@RequestParam(value = "idAsunto", required = true) Integer idAsunto,
			@RequestParam(value = "objectId", required = true) String contentId) throws Exception {

		try {

			// String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			Asunto asunto = mngrAsunto.fetch(idAsunto);

			DocumentoAsunto documento = new DocumentoAsunto();

			documento.setIdAsunto(idAsunto);
			documento.setObjectId(contentId);

			documento = mngrDocsAsunto.fetch(documento);

			if (asunto == null || documento == null) {

				throw new BadRequestException("El documento " + contentId + " no pertenece al asunto " + idAsunto);

			} else {

				if (asunto.getTipoAsunto() == TipoAsunto.ASUNTO
						&& asunto.getStatusAsunto().getIdStatus() == Status.CONCLUIDO
				// && userId.equalsIgnoreCase(asunto.getAsuntoDetalle().getIdFirmante())
				) {

					String tipo = environment.getRequiredProperty("sipot.tipo");

					if ("APACHE".equalsIgnoreCase(tipo))
						removerDocumentoPublicadoApache(contentId, asunto, documento);
					else if ("DCTM_PLUGIN".equalsIgnoreCase(tipo))
						removerDocumentoPublicadoDctm(contentId, asunto, documento);
					else
						throw new BadRequestException();

					documento.setPathPublished(null);
					mngrDocsAsunto.update(documento);
					return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);

				} else {

					throw new BadRequestException(
							"El asunto " + idAsunto + " no es de tipo A o no esta en estado concluido.");

				}

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * @param contentId
	 * @param asunto
	 * @param documento
	 */
	private void removerDocumentoPublicadoDctm(String contentId, Asunto asunto, DocumentoAsunto documento) {

	}

	/**
	 * @param contentId
	 * @param asunto
	 * @param documento
	 * @throws Exception
	 */
	private void removerDocumentoPublicadoApache(String contentId, Asunto asunto, DocumentoAsunto documento)
			throws Exception {

		String hotFolderSipot = environment.getRequiredProperty("sipot.ruta_publicacion");

		File filePublicado = new File(hotFolderSipot + documento.getPathPublished() + documento.getObjectName());

		if (filePublicado.exists()) {

			filePublicado.delete();

		} else {

			throw new Exception("El archivo NO existe.");

		}

	}

	/**
	 * Publicar documento de manera publica, requerimeinto sipot.
	 *
	 * @param idAsunto
	 * @param contentId
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Publicar documento", notes = "Publica un documento de manera publica")
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

	@RequestMapping(value = "/documento/asunto/publish", method = RequestMethod.GET)
	public ResponseEntity<DocumentoAsunto> publicarDocumento(
			@RequestParam(value = "idAsunto", required = true) Integer idAsunto,
			@RequestParam(value = "objectId", required = true) String contentId) throws Exception {

		try {

			// String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			Asunto asunto = mngrAsunto.fetch(idAsunto);

			DocumentoAsunto documento = new DocumentoAsunto();

			documento.setIdAsunto(idAsunto);
			documento.setObjectId(contentId);

			documento = mngrDocsAsunto.fetch(documento);

			if (asunto == null || documento == null) {

				throw new BadRequestException();

			} else {

				if (asunto.getTipoAsunto() == TipoAsunto.ASUNTO
						&& asunto.getStatusAsunto().getIdStatus() == Status.CONCLUIDO) {

					String tipo = environment.getRequiredProperty("sipot.tipo");

					if ("APACHE".equalsIgnoreCase(tipo))
						publicarDocApache(contentId, asunto, documento);

					else if ("DCTM_PLUGIN".equalsIgnoreCase(tipo))
						publicarDctm(contentId, asunto, documento);

					else
						throw new BadRequestException();

					return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);

				} else {

					throw new BadRequestException();

				}

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Obtener Documentos publicados sipot.
	 *
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documentos publicados", notes = "Obtiene todos los documentos publicados")
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
	@RequestMapping(value = "/documento/asunto/published", method = RequestMethod.GET)
	public ResponseEntity<List<DocumentoAsunto>> buscarDocumentosPublicados() {

		try {

			Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			List<Criterion> restrictions = new ArrayList<>();

			restrictions.add(Restrictions.isNotNull("pathPublished"));

			restrictions.add(Restrictions.eq("idArea", idArea));

			List<DocumentoAsunto> documentos = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

			return new ResponseEntity<List<DocumentoAsunto>>(documentos, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/*
	 * Se utiliza para obtener los documentos que pueden ser cancelados
	 * 
	 * @param idAsunto
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documentos cancelar", notes = "Obtiene los documentos que pueden ser cancelados")
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "documentos/asunto/cancelar", method = RequestMethod.GET)
	public ResponseEntity<List> getCancelar(@RequestParam(value = "idAsunto", required = true) Serializable idAsunto)
			throws Exception {

		List lst = new ArrayList<>();
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("idAsunto", Integer.valueOf((String) idAsunto)));
			List<DocumentoAsunto> documentos = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

			documentos.forEach(documento -> {
				// validamos que el documento sea del area
				if (documento.getIdArea().equals(areaId)) {
					List<Criterion> rest = new ArrayList<Criterion>();
					rest.add(Restrictions.eq("objectId", documento.getObjectId()));
					rest.add(Restrictions.ne("asuntoConsulta.tipoAsunto", TipoAsunto.ASUNTO));
					List<DocumentoAsunto> doc = (List<DocumentoAsunto>) mngrDocsAsunto.search(rest);

					doc.forEach(d -> {
						if (d.getAsuntoConsulta().getTipoAsunto().equals(TipoAsunto.COPIA)
								|| d.getAsuntoConsulta().getTipoAsunto().equals(TipoAsunto.ENVIO)
								|| d.getAsuntoConsulta().getTipoAsunto().equals(TipoAsunto.TURNO)) {
							if (d.getAsuntoConsulta().getIdStatusAsunto().equals(Status.PROCESO)
									|| d.getAsuntoConsulta().getIdStatusAsunto().equals(Status.CONCLUIDO)) {
								if (d.getStatusCancelacion() == null) {
									lst.add(documento.getObjectId());
									return;
								}
							}

						}
					});
				}
			});
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List>(lst, HttpStatus.OK);
	}

	/**
	 * Se utiliza para cancelar los documentos.
	 *
	 * @param body
	 * @return DocumentoAsunto
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Cancelar documento", notes = "Cancela un documento")
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

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "documentos/asunto/cancelar", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAsunto> cancelar(@RequestBody HashMap<String, Object> body)
			throws Exception {

		String accionActa = (String) body.get("accionActa");
		Integer idAsunto = (Integer) body.get("idAsunto");
		String objectId = (String) body.get("objectId");
		String motivoCancelacion = (String) body.get("motivoCancelacion");

		IEndpoint endpoint = EndpointDispatcher.getInstance();
		String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		try {
			DocumentoAsunto current = new DocumentoAsunto();
			current.setObjectId(objectId);
			current.setIdAsunto(idAsunto);
			DocumentoAsunto docCurrent = mngrDocsAsunto.fetch(current);

			// verifico si el usuario logueado pertenece al area del documento
			if (docCurrent.getIdArea().equals(areaId)) {

				// se comprueba si existe el documento en algun tramite aceptado
				List<Criterion> rest = new ArrayList<Criterion>();
				rest.add(Restrictions.eq("objectId", objectId));
				rest.add(Restrictions.ne("asuntoConsulta.tipoAsunto", TipoAsunto.ASUNTO));
				rest.add(Restrictions.isNull("statusCancelacion"));
				rest.add(Restrictions.or(Restrictions.eq("asuntoConsulta.idStatusAsunto", Status.PROCESO),
						Restrictions.eq("asuntoConsulta.idStatusAsunto", Status.CONCLUIDO)));
				List<DocumentoAsunto> docs = (List<DocumentoAsunto>) mngrDocsAsunto.search(rest);

				// se procede a cancelar el documento si existe en un tramite o está firmado
				if ((docs != null && !docs.isEmpty()) || (docCurrent.getStatus() != null
						&& docCurrent.getStatus().equals(StatusFirmaDocumento.FIRMADO))) {

					TipoNotificacion tipoNotificacion = TipoNotificacion.DOC_CANCELADO;
					List<Integer> idAreasNotificar = new ArrayList<>();
					List<Criterion> restri = new ArrayList<Criterion>();
					restri.add(Restrictions.eq("objectId", docCurrent.getObjectId()));
					List<DocumentoAsunto> dcs = (List<DocumentoAsunto>) mngrDocsAsunto.search(restri);

					// se cancelan los documentos con el mismo objectId y se capturan las areas a
					// ser notificadas
					dcs.forEach(doc -> {
						doc.setMotivoCancelacion(motivoCancelacion);
						doc.setFechaCancelacion(new Date());
						doc.setStatusCancelacion("C");
						mngrDocsAsunto.update(doc);
						if (!doc.getAsuntoConsulta().getTipoAsunto().equals(TipoAsunto.ASUNTO)) {
							idAreasNotificar.add(doc.getAsuntoConsulta().getIdAreaDestino());
						}
					});

					docCurrent.setOwnerName(userName);
					docCurrent.setStatusCancelacion("C");
					docCurrent.setFechaCancelacion(new Date());
					docCurrent.setMotivoCancelacion(motivoCancelacion);

					// Inicio acta de cancelacion automatico //
					if (accionActa.equals("crearActa")) {
						try {
							String area_id = getHeader(HeaderValueNames.HEADER_AREA_ID);
							Area userArea = mngrArea.fetch(Integer.valueOf(area_id));

							String plantillasIntitucionalesFolder = getParamApp("SIGAP", "PLANTILLASINSTITUCIONALES");
							String plantillasAreaFolder = StringUtils.isBlank(userArea.getContentId()) ? "-1"
									: userArea.getContentId();

							IEndpoint instance = EndpointDispatcher.getInstance();
							List<Map<String, String>> object = //
									instance.obtenerPlantillas(TipoPlantilla.INSTITUCIONAL.toString(),
											plantillasIntitucionalesFolder, plantillasAreaFolder);

							for (Map<String, String> entry : object) {
								if (entry.get("object_name").equals("acta_cancelacion.xml")) {

									// Obtengo la plantilla del acta_cancelacion
									String plantillaActaB64 = EndpointDispatcher.getInstance()
											.getObjectContentB64(entry.get("r_object_id"));

									// creo el nombre del acta
									String nameDoc = docCurrent.getObjectName().replaceFirst("[.][^.]+$", "");
									Date now = new Date();
									String objectNameActa = "ActaCancelacion-" + nameDoc + "-"
											+ new SimpleDateFormat("yyyy").format(now)
											+ new SimpleDateFormat("MM").format(now)
											+ new SimpleDateFormat("dd").format(now);
									String sha256ContectDoc = "";

									// obtengo el sha256 del documento cancelado, si es que esta firmado
									if (docCurrent.getStatus() != null) {
										if (docCurrent.getStatus().getTipo().equals("F")) {
											String contentDocCurrent = EndpointDispatcher.getInstance()
													.getObjectContentB64(docCurrent.getObjectId());
											sha256ContectDoc = CertificateUtility
													.getSha256Hex(Base64.decodeBase64(contentDocCurrent));
											sha256ContectDoc = sha256ContectDoc.toUpperCase();
										}
									}

									// prepado la fecha de hoy en una variable
									Locale esLocale = new Locale("es", "ES");
									SimpleDateFormat dmy = new SimpleDateFormat("dd MMMMM yyyy", esLocale);

									// capturo en bytes la plantilla del acta
									byte[] base64decodedBytes = Base64.decodeBase64(plantillaActaB64.getBytes());

									// inicio un archivo
									File file = File.createTempFile(objectNameActa, ".doc");
									file.deleteOnExit();
									FileUtils.writeByteArrayToFile(file, base64decodedBytes);

									// convierto a String la plantilla acta
									String contenidoFile = FileUtils.readFileToString(file, "UTF-8");

									// reemplazo las etiquetas de la plantilla acta
									contenidoFile = contenidoFile.replace(
											plantillasKeys.getString("documento.asunto.descripcion"),
											docCurrent.getAsuntoConsulta().getAsuntoDescripcion().toString());
									contenidoFile = contenidoFile.replace(
											plantillasKeys.getString("documento.objectName"),
											docCurrent.getObjectName().toString());
									contenidoFile = contenidoFile.replace(
											plantillasKeys.getString("documento.firma.sha256"), sha256ContectDoc);
									contenidoFile = contenidoFile.replace(
											plantillasKeys.getString("documento.fechahoy"), dmy.format(new Date()));
									contenidoFile = contenidoFile.replace(plantillasKeys.getString("documento.motivo"),
											docCurrent.getMotivoCancelacion().toString());

									// capturo en bytes la plantilla reemplazada
									byte[] bytesEncoded = Base64.encodeBase64(contenidoFile.getBytes("UTF-8"));
									// paso a string la plantilla reemplazada
									String contentDocActaB64 = new String(bytesEncoded);

									// inicio un nuevo archivo
									Path newPlantillaFile = FileUtil.createTempFile(contentDocActaB64,
											objectNameActa + ".doc");

									// capturo el archivo listo
									File plantilla = newPlantillaFile.toFile();
									// newPlantillaFile.toFile().getAbsolutePath();

									// convierto a Base64 archivo creado en base a la plantilla del acta
									String plantillaB64 = Base64
											.encodeBase64String(FileUtils.readFileToByteArray(plantilla));

									// inicio y seteo el acta a subir
									DocumentoAsunto docActa = new DocumentoAsunto();
									docActa.setFechaCancelacion(docCurrent.getFechaCancelacion());
									docActa.setMotivoCancelacion(motivoCancelacion);
									docActa.setIdArea(docCurrent.getIdArea());
									docActa.setFileB64(plantillaB64);
									docActa.setObjectName(objectNameActa + ".doc");
									docActa.setStatusCancelacion("P");
									docActa.setGubernamental(false);
									docActa.setIdAsunto(idAsunto);

									// guardado el acta
									DocumentoAsunto actaGuardada = save(docActa).getBody();

									// actualizar idActa_cancelacion en el documento cancelado
									docCurrent.setIdActaCancelacion(actaGuardada.getObjectId());
									mngrDocsAsunto.update(docCurrent);
									break;
								}
							}
						} catch (Exception e) {
							log.error(e.getLocalizedMessage());

							throw e;
						}
					}
					// Fin acta de cancelacion automatico //

					// Enviar notificacion a las areas
					if (idAreasNotificar.size() > 0) {

						try {
							if (mailController.sendNotificacionDocto(docCurrent, idAreasNotificar, tipoNotificacion)) {
								log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + tipoNotificacion
										+ " SATISFACTORIAMENTE PARA EL DOCUMENTO CANCELADO "
										+ docCurrent.getObjectId());
							} else {
								log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO " + tipoNotificacion
										+ " PARA EL DOCUMENTO CANCELADO " + docCurrent.getObjectId());
							}
						} catch (Exception e) {
							log.error(e.getLocalizedMessage());

						}
					}

				} else {
					// no se realiza la cancelacion porque no esta firmado ni es parte de un tramite
					return new ResponseEntity<DocumentoAsunto>(docCurrent, HttpStatus.NOT_ACCEPTABLE);
				}

				// responde exitoso si se hace todo el proceso sin fallas
				return new ResponseEntity<DocumentoAsunto>(docCurrent, HttpStatus.OK);
			} else {
				throw new Exception("El archivo no pertenece al área.");
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Editar acta cancelacion", notes = "Descarga el acta de cancelacion")
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

	@RequestMapping(value = "/documento/asunto/checkout", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> documentoCheckout(
			@RequestParam(value = "objectIdActa", required = true) String objectIdActa,
			@RequestParam(value = "objectIdDocC", required = true) String objectIdDocC,
			@RequestParam(value = "asuntoId", required = true) String asuntoId) throws Exception {

		try {
			IEndpoint endpoint = EndpointDispatcher.getInstance();
			String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

			// creo una variable
			Map<String, Object> result = new HashMap<String, Object>();

			// obtengo datos del acta
			DocumentoAsunto acta = new DocumentoAsunto();
			acta.setObjectId(objectIdActa);
			acta.setIdAsunto(Integer.valueOf((String) asuntoId));
			acta = mngrDocsAsunto.fetch(acta);

			// se valida que sea el usuario que creo el acta
			if (acta.getOwnerName().equals(userName)) {
				// se cambian los ACLs para poder versionar
				Map<String, String> additionalData = new HashMap<>();
				additionalData.put("idArea", acta.getIdArea().toString());
				// Acl acl = endpoint.getAcl(environment.getProperty("aclNameFolderAsunto"),
				// additionalData);

				endpoint = EndpointDispatcher.getInstance();
				endpoint.setACL(objectIdActa, environment.getProperty("aclNameActaEdition"), additionalData);

				// se bloquea el acta
				boolean isCheckedOut = endpoint.checkOut(objectIdActa.toLowerCase());

				if (isCheckedOut) {
					// se cambia el status_cancelacion del acta a "E" (En Edicion)
					acta.setStatusCancelacion("E");
					mngrDocsAsunto.update(acta);

					// se manda a descargar el documento
					result = repositoryController.getDocument(objectIdActa, Integer.valueOf((String) asuntoId))
							.getBody();
				} else {
					result.put("isCheckedOut", isCheckedOut);
				}
			} else {
				result.put("notUser", "Solo el usuario " + acta.getOwnerName() + " puede editar el acta");
				return new ResponseEntity<>(result, HttpStatus.OK);
			}

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/*
	 * Se usa cuando se van a registrar los cambios del acta
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Registro acta cancelacion", notes = "Se registra el documento con los cambios de la acta")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/documento/asunto/checkin", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<CheckinObject> documentoCheckin(
			@RequestBody(required = true) CheckinObject checkinObject) throws Exception {

		IEndpoint endpoint = EndpointDispatcher.getInstance();
		String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

		int asuntoId = Integer.valueOf((String) checkinObject.getComment());
		String objectIdActa = checkinObject.getObjectId();
		Version verionActa = checkinObject.getVersion();
		String Comentario = "";
		String nombreActa = checkinObject.getNombre();

		// obtengo datos del acta
		DocumentoAsunto old_acta = new DocumentoAsunto();
		old_acta.setObjectId(objectIdActa);
		old_acta.setIdAsunto(asuntoId);
		old_acta = mngrDocsAsunto.fetch(old_acta);

		// valido si es el dueño del acta
		if (old_acta.getOwnerName().equals(userName)) {

			File documento = null;

			try {
				String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

				if (!esSoloLectura(userId)) {

					documento = FileUtil.createTempFile(checkinObject.getDocumentB64());

					String formatDoc = nombreActa.split("\\.")[nombreActa.split("\\.").length - 1];

					if ("doc".equalsIgnoreCase(formatDoc) || "docx".equalsIgnoreCase(formatDoc)) {
						endpoint = EndpointDispatcher.getInstance();
						List<Map<String, String>> resultsCheckin = endpoint.checkIn(objectIdActa, //
								verionActa, //
								Comentario, //
								nombreActa, //
								documento);

						documento.delete();

						for (Map<String, String> result : resultsCheckin) {

							DocumentoAsunto new_acta = new DocumentoAsunto();
							new_acta.setMotivoCancelacion(old_acta.getMotivoCancelacion());
							new_acta.setFechaCancelacion(old_acta.getFechaCancelacion());
							new_acta.setFechaRegistro(old_acta.getFechaRegistro());
							new_acta.setGubernamental(old_acta.getGubernamental());
							new_acta.setObjectId(result.get("documentoId"));
							new_acta.setIdAsunto(old_acta.getIdAsunto());
							new_acta.setIdArea(old_acta.getIdArea());
							new_acta.setStatusCancelacion("P");

							mngrDocsAsunto.save(new_acta);
							mngrDocsAsunto.delete(old_acta);

							// * * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * *
							// busca el documento cancelado al que le pertenece el acta y se le asigna la
							// nueva version del acta
							List<Criterion> restrictions = new ArrayList<Criterion>();
							restrictions.add(Restrictions.eq("idActaCancelacion", objectIdActa));
							List<DocumentoAsunto> list = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);
							list.forEach(doc -> {
								DocumentoAsunto docCancelado = new DocumentoAsunto();
								docCancelado.setObjectId(doc.getObjectId());
								docCancelado.setIdAsunto(asuntoId);
								docCancelado = mngrDocsAsunto.fetch(docCancelado);
								docCancelado.setIdActaCancelacion(result.get("documentoId"));
								mngrDocsAsunto.update(docCancelado);
							});
							// * * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * *

							checkinObject.setNewObjectId(result.get("documentoId"));
							checkinObject.setNewVersion(result.get("version"));
							checkinObject.setDocumentB64(null);

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
		} else {
			checkinObject.setComment("notUser");
			return new ResponseEntity<CheckinObject>(checkinObject, HttpStatus.OK);
		}
	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Confirmar acta", notes = "Se confirma el acta")
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

	@RequestMapping(value = "/documento/asunto/confirmar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DocumentoAsunto> documentoConfirmar(
			@RequestParam(value = "objectIdActa", required = true) String objectIdActa,
			@RequestParam(value = "asuntoId", required = true) String asuntoId) throws Exception {

		try {

			IEndpoint endpoint = EndpointDispatcher.getInstance();
			String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

			DocumentoAsunto acta = new DocumentoAsunto();
			acta.setObjectId(objectIdActa);
			acta.setIdAsunto(Integer.valueOf((String) asuntoId));
			acta = mngrDocsAsunto.fetch(acta);

			// valido si es el dueño del acta
			if (acta.getOwnerName().equals(userName)) {
				// se cambia el status_cancelacion del acta a "F" (Finalizado)
				acta.setStatusCancelacion("F");
				mngrDocsAsunto.update(acta);

				// se cambian el acl para que pueda interactuar con otros usuarios del area
				Map<String, String> additionalData = new HashMap<>();
				additionalData.put("idArea", acta.getIdArea().toString());
				endpoint.setACL(objectIdActa, environment.getProperty("aclNameActaConfirmed"), additionalData);
			} else {
				return new ResponseEntity<DocumentoAsunto>(acta, HttpStatus.CONFLICT);
			}

			return new ResponseEntity<DocumentoAsunto>(acta, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Se utiliza el plugin de documentum para publicar los documentos.
	 *
	 * @param contentId
	 * @param asunto
	 * @param documento
	 */
	private void publicarDctm(String contentId, Asunto asunto, DocumentoAsunto documento) {
		// TODO Auto-generated method stub

	}

	/**
	 * Se utiliza un apache httpd y el folder para publicar los documentos.
	 *
	 * @param contentId
	 * @param asunto
	 * @param documento
	 * @throws Exception
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private void publicarDocApache(String contentId, Asunto asunto, DocumentoAsunto documento)
			throws Exception, JsonParseException, JsonMappingException, IOException {

		String hotFolderSipot = environment.getRequiredProperty("sipot.ruta_publicacion");

		StringBuilder newPath = getPublishPath(asunto);

		documento.setPathPublished(newPath.toString());

		File pathOfPublication = new File(hotFolderSipot + newPath.toString());

		boolean pathsCreated = pathOfPublication.mkdirs();

		log.debug("PATH " + pathOfPublication.getCanonicalPath() + " CREATED :: " + pathsCreated);

		if (!pathOfPublication.exists()) {
			throw new Exception("Ruta destino no existe. Error al crearla.");
		}

		File filePublicado = new File(hotFolderSipot + newPath.toString() + documento.getObjectName());

		if (filePublicado.exists()) {
			throw new Exception("El archivo ya existe en el destino.");
		}

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		String fileContentB64 = endpoint.getObjectContentB64(contentId);

		FileUtil.createFile(fileContentB64, filePublicado);

		if (filePublicado.exists()) {

			mngrDocsAsunto.update(documento);

		} else {

			throw new Exception("El archivo no fue creado.");

		}

	}

	/**
	 * Formato para año.
	 */
	private static final SimpleDateFormat sdf_year = new SimpleDateFormat("YYYY");

	/**
	 * genera la ruta de publicacion de sipot.
	 *
	 * @param asunto
	 * @return
	 */
	private StringBuilder getPublishPath(Asunto asunto) {

		Area area = asunto.getArea();

		Date d = new Date();

		StringBuilder newPath = new StringBuilder();

		newPath.append(area.getRuta().replace('/', File.separatorChar));

		newPath.append(File.separatorChar);

		newPath.append(area.getClave());

		newPath.append(File.separatorChar);

		newPath.append(sdf_year.format(d));

		newPath.append(File.separatorChar);

		newPath.append(asunto.getFolioArea());

		newPath.append(File.separatorChar);

		return newPath;

	}

	@SuppressWarnings("unchecked")
	private boolean isUnique(DocumentoAsunto documento) {
		List<Criterion> restrictions = new ArrayList<Criterion>();
		List<DocumentoAsunto> items = new ArrayList<DocumentoAsunto>();

		try {

			if (null != documento.getObjectId())
				restrictions.add(Restrictions.ne("objectId", documento.getObjectId()));

			if (null != documento.getIdAsunto())
				restrictions.add(Restrictions.eq("idAsunto", documento.getIdAsunto()));

			if (null != documento.getObjectIdOrigen())
				restrictions.add(Restrictions.eq("objectIdOrigen", documento.getObjectIdOrigen()));

			items = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions, null);

			if (items != null) {
				if (items.size() > 0)
					return false;
				else
					return true;
			}

		} catch (Exception e) {
			log.error("Error:: No se pudo consultar si existen mas documentos con el mismo objectIdOrigen");
		}
		return false;
	}

	private void markEnableDoc(DocumentoAsunto documento) {
		try {
			// se busca un documento que tenga ese ObjectId para marcarlo como sí enviable
			DocumentoAsunto originalDoc = new DocumentoAsunto();
			originalDoc.setObjectId(documento.getObjectIdOrigen());
			originalDoc.setIdAsunto(documento.getIdAsunto());
			originalDoc = mngrDocsAsunto.fetch(originalDoc);
			if (originalDoc != null) {
				originalDoc.setEnabledToSend(true);
				mngrDocsAsunto.update(originalDoc);
			}
		} catch (Exception e) {
			log.error(":: Error al intentar marcar el documento original como enviable para tramites");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/generarArchivoZip/asunto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> generateZip(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {
		Instant star = Instant.now();
		Integer idAsunto = (Integer) params.get("idAsunto");
		List<Map<Object, Object>> Docs = (List<Map<Object, Object>>) params.get("documentos");

		// Convertir/Castear de List<Map<Object, Object>> a <DocumentoAsunto>
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<DocumentoAsunto>> typeReference = new TypeReference<List<DocumentoAsunto>>() {
			/* */};
		List<DocumentoAsunto> documentos = mapper.convertValue(Docs, typeReference);

		Map<String, Object> result = new HashMap<String, Object>();

		Set<String> documentosAdjuntos = new HashSet<>();
		
		File zipFile = null;
		try {
			for (DocumentoAsunto documento : documentos){
				try {
					// Obtenemos el contenido del documento
					ResponseEntity<Map<String, Object>> resp = repositoryController.getDocument(documento.getObjectId(),
							null);

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
			
			log.error(":: Total de Documentos del asunto" + documentosAdjuntos.size());
			try {
				// crear el archivo zip con los documentos generados
				log.debug(":: Iniciando la creacion del zip");
				String zipFileName = "documentosAsunto_" + idAsunto + ".zip";
				zipFile = FileUtil.zipFiles(zipFileName, documentosAdjuntos);
				zipFile.deleteOnExit();

				result.put("contentB64", FileUtil.fileToStringB64(zipFile));
				result.put("type", "application/zip");
				result.put("name", zipFileName);

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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/eliminarDocumentoMasivo/asunto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> deleteDocs(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {

		Serializable idAsunto = (Serializable) params.get("idAsunto");
		List<String> idsDocs = (List<String>) params.get("documentos");
		List<Map<Object, Object>> idsDocsComp = (List<Map<Object, Object>>) params.get("documentosCompartidos");
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		// Convertir/Castear de List<Map<Object, Object>> a <DocumentoAsunto>
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<DocumentoAsunto>> typeReference = new TypeReference<List<DocumentoAsunto>>() {
		};
		List<DocumentoAsunto> documentosCompartidos = mapper.convertValue(idsDocsComp, typeReference);

		try {
			Map<String, Object> result = new HashMap<String, Object>();
			Asunto asunto = mngrAsunto.fetch(idAsunto);
			if (null == asunto) {
				log.error(":: El Asunto no existe");
				throw new IllegalArgumentException(":: El Asunto no existe");
			}

			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<DocumentoAsunto> docs = new ArrayList<DocumentoAsunto>();
			DocumentoAsunto item = new DocumentoAsunto();
			item.setIdAsunto((Integer) idAsunto);
			restrictions.add(Restrictions.eq("idAsunto", item.getIdAsunto()));

			// importante porque un usuario no puede eliminar un documento que no le
			// pertenezca
			docs = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions, null);
			
			docs = docs.stream().filter(d -> idsDocs.contains(d.getObjectId())).collect(Collectors.toList());

			Set<String> documentosExitosos = new HashSet<>();
			Set<String> documentosFallidos = new HashSet<>();

			docs.parallelStream().forEach(documento -> {
				// Obtenemos el contenido del documento
				if (idsDocs.contains(documento.getObjectId())) {

					if (documento.getIdArea().equals(areaId)) {
						try {
							delete(idAsunto, documento.getObjectId());
							documentosExitosos.add(documento.getObjectName());
						} catch (ConstraintViolationException ce) {
							log.error(":: ConstraintViolationException " + ce.getLocalizedMessage());
							documentosFallidos.add(documento.getObjectName() + " - " + ce.getLocalizedMessage());
						} catch (Exception e) {
							log.error(":: Error al intentar eliminar el documento");
							documentosFallidos
									.add(documento.getObjectName() + " - Error al intentar eliminar el documento.");
						}

					} else {
						documentosFallidos.add(documento.getObjectName() + " - No es dueño del documento");
					}
				}
			});

			documentosCompartidos.parallelStream().forEach(documento -> {
				try {
					documentoCompartidoController.delete(documento.getObjectId());
					documentosExitosos.add(documento.getObjectName());
				} catch (Exception e) {
					log.error(":: Error al intentar eliminar el documento");
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

}
