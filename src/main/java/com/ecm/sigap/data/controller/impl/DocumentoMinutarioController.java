/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.BadRequestException;

import org.apache.commons.codec.binary.Base64;
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

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoMinutario;
import com.ecm.sigap.data.model.Minutario;
import com.ecm.sigap.data.model.util.StatusMinutario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.DocumentoMinutario}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class DocumentoMinutarioController extends CustomRestController implements RESTController<DocumentoMinutario> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(DocumentoMinutarioController.class);

	/**
	 * Referencia hacia el REST controller de {@link RepositoryController}.
	 */
	@Autowired
	private RepositoryController repositoryController;

	/**
	 * Documentos pertenecientes a un {@link Asunto}
	 *
	 * @param id Id del Asunto.
	 * @return Lista de documentos.
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documentos borrador", notes = "Obtiene los documentos de un borrador")
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

	@RequestMapping(value = "/documentos/minutario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> get(@RequestParam(value = "id", required = true) Integer id) {

		List<?> items = new ArrayList<DocumentoMinutario>();
		try {
			// Consultamos la lista de Documentos Anexos que tiene el Minutario
			items = searchDocumentos(id);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

		}

		return new ResponseEntity<List<?>>(items, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/documentos/minutario", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoMinutario> save(
			@RequestBody(required = true) DocumentoMinutario documento) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {
				log.debug("DOCUMENTO_MINUTARIO A GUARDAR >> " + documento);

				if (documento.getObjectId() != null) {

					mngrDocsMinutario.update(documento);

				} else {

					Minutario minutario = mngrMinutario.fetch(documento.getIdMinutario());

					log.debug(minutario);

					documento.setParentContentId(minutario.getContentId());

					{// UPLOAD TO THE REPOSITORY

						log.debug("SAVING NEW DOCUMENTO MINUTARIO :: " + documento.toString());

						if (documento.getFileB64() == null) {
							log.error("El contenido del getFileB64 esta vacio por lo que se rechaza la peticion");
							return new ResponseEntity<DocumentoMinutario>(documento, HttpStatus.BAD_REQUEST);
						}
						boolean isBase64 = Base64.isBase64(documento.getFileB64());
						if (!isBase64) {
							log.error("El getFileB64 del documento no es Base64, se rechaza la peticion");
							return new ResponseEntity<DocumentoMinutario>(documento, HttpStatus.BAD_REQUEST);
						}

						File documento_ = FileUtil.createTempFile(documento.getFileB64());
						String parentFolderId = documento.getParentContentId();
						String nombreArchivo = documento.getObjectName();
						String tipoDoc = environment.getProperty("docTypeAdjuntoAnexoMinutario");
						Version verDoc = Version.MAYOR;
						String descDoc = documento.getObjectName();

						Map<String, String> additionalData = new HashMap<String, String>();

						// subir archivo al repositorio
						String newID = EndpointDispatcher.getInstance().saveDocumentoIntoId(parentFolderId, //
								nombreArchivo, tipoDoc, verDoc, descDoc, documento_);

						documento_.delete();

						Map<String, Object> properties = new HashMap<>();

						// Obtenemos el User Name para asignarlo como el Owner
						// del
						// documento

						String userName = EndpointDispatcher.getInstance()
								.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

						if (StringUtils.isNotBlank(documento.getOwnerName())) {
							userName = documento.getOwnerName();
						}

						properties.put("owner_name", userName);

						EndpointDispatcher.getInstance().setProperties(newID, properties);

						if (minutario.getRemitente().getIdArea().equals(Integer.valueOf(documento.getIdArea()))
								&& !minutario.getStatus().equals(StatusMinutario.PARA_REVISION)) {
							log.debug("Se esta subiendo un documento de la misma area del usuario "
									+ "remitente por lo que se aplica el ACL 'aclNameAdjuntoAnexoMinutario'");

							additionalData.put("idOwnerDoc", userName);

							// AGREGAR ACL
							EndpointDispatcher.getInstance().setACL(newID,
									environment.getProperty("aclNameAdjuntoAnexoMinutario"), additionalData);
						} else {

							log.debug("Se esta subiendo un documento de otra area del usuario "
									+ "remitente por lo que se aplica el ACL 'aclNameAdjuntoAnexoMinutarioRevisor'");
							String ownerUserName = EndpointDispatcher.getInstance()
									.getUserName(minutario.getUsuario().getUserKey());

							String firmanteUserName = EndpointDispatcher.getInstance()
									.getUserName(minutario.getFirmante().getUserKey());
							additionalData.put("idOwnerDoc", ownerUserName);
							additionalData.put("idFirmanteDoc", firmanteUserName);
							additionalData.put("idRevisor", userName);

							// AGREGAR ACL
							EndpointDispatcher.getInstance().setACL(newID,
									environment.getProperty("aclNameAdjuntoAnexoMinutarioRevisor"), additionalData);
						}

						documento.setOwnerName(userName);
						documento.setObjectId(newID);
					}

					mngrDocsMinutario.save(documento);

					// se elimina el archivo eb base64 para aligerar el JSON
					documento.setFileB64(null);
				}

				return new ResponseEntity<DocumentoMinutario>(documento, HttpStatus.OK);
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
	 * @param documentos
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar documento borrador", notes = "Agrega un documento a un borrador")
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

	@RequestMapping(value = "/documentos/minutario/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveAll(
			@RequestBody(required = true) List<DocumentoMinutario> documentos) throws Exception {
		try {
			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();
			List<DocumentoMinutario> successList = new ArrayList<DocumentoMinutario>();

			if (!documentos.isEmpty()) {

				for (DocumentoMinutario documentoAsunto : documentos) {

					try {

						ResponseEntity<DocumentoMinutario> response = save(documentoAsunto);
						successList.add(response.getBody());

					} catch (BadRequestException e) {
						listResultFail.put(documentoAsunto.getObjectName(), HttpStatus.BAD_REQUEST);
					} catch (Exception e) {
						if (e instanceof UnsupportedOperationException) {
							listResultFail.put(documentoAsunto.getObjectName(), "El documento debe ser mayor a 0KB");
						} else {
							listResultFail.put(documentoAsunto.getObjectName(), HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
				}

			} else {
				throw new BadRequestException();
			}

			listResult.put("success", successList);
			listResult.put("error", listResultFail);

			return new ResponseEntity<>(listResult, HttpStatus.OK);

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
	public ResponseEntity<DocumentoMinutario> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Serializable id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Permite eliminar un documento de la lista de Documentos Anexos del minutario
	 *
	 * @param idMinutario Identificador del Minutario
	 * @param objectId    Identificador del Documento que se quiere eliminar
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar documento borrador", notes = "Elimina un documento del borrador")
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

	@RequestMapping(value = "/documentos/minutario", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<List<?>> delete(
			@RequestParam(value = "idMinutario", required = true) Integer idMinutario,
			@RequestParam(value = "objectId", required = true) String objectId) {

		List<?> items = new ArrayList<DocumentoMinutario>();
		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("idMinutario", idMinutario));
			restrictions.add(Restrictions.eq("objectId", objectId));

			// Se elimina el documento
			items = mngrDocsMinutario.search(restrictions, null);

			if (!items.isEmpty()) {
				DocumentoMinutario documento = (DocumentoMinutario) items.get(0);
				// String userName = EndpointDispatcher.getInstance()
				// .getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
				// if(userName.equals(documento.getOwnerName()))
				mngrDocsMinutario.delete(documento);
				// else
				// return new ResponseEntity<List<?>>(items, HttpStatus.NOT_ACCEPTABLE);
			}

			// Se devuelve la lista de documento restantes en el minutario
			items = searchDocumentos(idMinutario);

			return new ResponseEntity<List<?>>(items, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			return new ResponseEntity<List<?>>(items, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(DocumentoMinutario object) {

		List<?> items = new ArrayList<DocumentoMinutario>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			List<Order> orders = new ArrayList<Order>();

			// * * * * * * * * * * * * * * * * * * * * * *

			items = mngrDocsAsunto.search(restrictions, orders);

			log.debug(items);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Size Out >> " + items.size());

		return new ResponseEntity<List<?>>(items, HttpStatus.OK);

	}

	private List<?> searchDocumentos(Integer idMinutario) throws Exception {

		List<?> items = new ArrayList<DocumentoMinutario>();
		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("idMinutario", idMinutario));

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("fechaRegistro"));

			items = mngrDocsMinutario.search(restrictions, orders);

			log.debug(" Cantidad de documentos anexos obtenidos >> " + items.size());

			return items;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	// Metodo para eliminar anexos de minutario masivamente
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/eliminarDocumentoMasivo/minutario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> deleteDocs(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {

		Integer idMinutario = (Integer) params.get("idMinutario");
		List<String> idsDocs = (List<String>) params.get("anexos");

		String userName = EndpointDispatcher.getInstance().getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

		// Convertir/Castear de List<Map<Object, Object>> a <DocumentoMinutario>
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<DocumentoMinutario>> typeReference = new TypeReference<List<DocumentoMinutario>>() {
		};
		List<DocumentoMinutario> Docs = mapper.convertValue(idsDocs, typeReference);

		try {
			Map<String, Object> result = new HashMap<String, Object>();
			Minutario minutario = mngrMinutario.fetch(idMinutario);
			if (null == minutario) {
				log.error(":: La respuesta no existe");
				throw new IllegalArgumentException(":: El borrador no existe");
			}

			Set<String> anexosExitosos = new HashSet<>();
			Set<String> anexosFallidos = new HashSet<>();

			Docs.parallelStream().forEach(anexo -> {
				try {
					anexo.setIdMinutario(idMinutario);
					// DocumentoMinutario item = mngrDocsMinutario.fetch(anexo.getObjectId());
					// if(userName.equals(item.getOwnerName())) {
					mngrDocsMinutario.delete(anexo);
					anexosExitosos.add(anexo.getObjectName());
					// } else
					// anexosFallidos.add(anexo.getObjectName() + " - No es el propietario del
					// documento");

				} catch (Exception e) {
					log.error(":: Error al intentar eliminar el anexo de minutario");
					anexosFallidos.add(anexo.getObjectName() + e.getLocalizedMessage());
				}
			});

			result.put("success", anexosExitosos);
			result.put("fail", anexosFallidos);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	// Metodo para descargar documentos de respuesta masivamente
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/generarArchivoZip/minutario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> generateZip(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {
		Instant star = Instant.now();

		Integer idMinutario = (Integer) params.get("idMinutario");
		List<String> idsDocs = (List<String>) params.get("anexos");

		// Convertir/Castear de List<Map<Object, Object>> a <DocumentoMinutario>
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<DocumentoMinutario>> typeReference = new TypeReference<List<DocumentoMinutario>>() {
		};
		List<DocumentoMinutario> Docs = mapper.convertValue(idsDocs, typeReference);

		Map<String, Object> result = new HashMap<String, Object>();

		Set<String> documentosAdjuntos = new HashSet<>();
		
		File zipFile = null;
		try {
			for (DocumentoMinutario documento : Docs) {
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
			log.error(":: Total de Documentos del minutario" + documentosAdjuntos.size());

			try {
				// crear el archivo zip con los documentos generados
				log.debug(":: Iniciando la creacion del zip");
				String zipFileName = "anexosBorrador_" + idMinutario + ".zip";
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
