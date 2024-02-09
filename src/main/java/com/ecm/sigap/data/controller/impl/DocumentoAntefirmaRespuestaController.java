/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
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
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.DocumentoAntefirmaKey;
import com.ecm.sigap.data.model.DocumentoAntefirmaRespuesta;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.TipoNotificacion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.DocumentoAntefirma}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class DocumentoAntefirmaRespuestaController extends CustomRestController
		implements RESTController<DocumentoAntefirmaRespuesta> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(DocumentoAntefirmaRespuestaController.class);

	/**
	 * Referencia hacia el REST controller de {@link MailController}.
	 */
	@Autowired
	private MailController mailController;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public @ResponseBody ResponseEntity<DocumentoAntefirmaRespuesta> get(Serializable id) {
		throw new UnsupportedOperationException();

	}

	/**
	 * 
	 * @param objectId
	 * @param id
	 * @param tipo
	 * @param idFirmante
	 * @param tipoFirmate
	 * @return
	 */
	@RequestMapping(value = "/documentos/respuesta/antefirma", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DocumentoAntefirmaRespuesta> get(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "tipo", required = true) String tipo,
			@RequestParam(value = "idFirmante", required = true) String idFirmante,
			@RequestParam(value = "tipoFirmate", required = true) Integer tipoFirmate) {

		DocumentoAntefirmaRespuesta item = null;

		try {

			DocumentoAntefirmaKey dak = new DocumentoAntefirmaKey();

			dak.setFirmante(idFirmante);
			dak.setId(id);
			dak.setObjectId(objectId);
			dak.setTipo(tipo);
			dak.setTipoFirmate(tipoFirmate);

			item = mngrDocumentoAntefirmaRespuesta.fetch(dak);

			log.debug(" Item Out >> " + item);

			return new ResponseEntity<DocumentoAntefirmaRespuesta>(item, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Serializable id) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param objectId
	 * @param id
	 * @param tipo
	 * @param idFirmante
	 * @param tipoFirmate
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Eliminar firmante antefirma", notes = "Elimina un firmante del documento de respuesta")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/documentos/respuesta/antefirma", method = RequestMethod.DELETE)
	public void deleteDocumentoAntefirma(//
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "tipo", required = true) String tipo,
			@RequestParam(value = "idFirmante", required = true) String idFirmante,
			@RequestParam(value = "tipoFirmate", required = true) Integer tipoFirmate,
			@RequestParam(value = "comentarioRechazo", required = false) String comentarioRechazo)
			throws Exception {

		DocumentoAntefirmaKey dak = new DocumentoAntefirmaKey();

		dak.setFirmante(idFirmante);
		dak.setId(id);
		dak.setObjectId(objectId);
		dak.setTipo(tipo);
		dak.setTipoFirmate(tipoFirmate);

		DocumentoAntefirmaRespuesta documentoAntefirmaSrch = new DocumentoAntefirmaRespuesta();

		documentoAntefirmaSrch.setDocumentoAntefirmaKey(dak);

		@SuppressWarnings("unchecked")
		List<DocumentoAntefirmaRespuesta> list = (List<DocumentoAntefirmaRespuesta>) search(documentoAntefirmaSrch)
				.getBody();

		if (list.isEmpty()) {

			throw new BadRequestException();

		} else {

			for (DocumentoAntefirmaRespuesta documentoAntefirma : list)
				if (documentoAntefirma.getFirmado()) {

					log.warn(documentoAntefirma + "Firma ya aplicada.");

				} else {

					log.warn("ELIMINANDO EL OBJETO >> " + documentoAntefirma);
					mngrDocumentoAntefirmaRespuesta.delete(documentoAntefirma);

					// Enviar NOTIFICACION
					if (!StringUtils.isBlank(comentarioRechazo)) {
						try {
							log.debug(":: INICIA EL PROCESO DE NOTIFICACIÓN AL REMITENTE ::");
							if (mailController.enviarNotificacionSigap(
									documentoAntefirma,
									TipoNotificacion.RECANTEFIRMAREC,
									comentarioRechazo)) {
								log.debug(" :: SE HA ENVIADO LA NOTIFICACION DE TIPO "
										+ TipoNotificacion.RECANTEFIRMAREC
										+ " SATISFACTORIAMENTE PARA EL DOCUMENTO RECHAZADO "
										+ id + " :: ");
							} else {
								throw new Exception(
										" :: NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
												+ TipoNotificacion.RECANTEFIRMAREC
												+ " PARA EL DOCUMENTO RECHAZADO "
												+ id + " :: ");
							}

						} catch (Exception e) {
							log.debug(" :: NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
									+ TipoNotificacion.RECANTEFIRMAREC
									+ " PARA EL DOCUMENTO RECHAZADO "
									+ id
									+ " :: ");
						} // FIN ENVIAR NOTIFICACION
					}
				}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta documento antefirma", notes = "Consulta el detalle del documento de una respuesta")
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
	
	@SuppressWarnings("unchecked")
	@Override
	@RequestMapping(value = "/documentos/respuesta/antefirma", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) DocumentoAntefirmaRespuesta documentoAntefirma) throws Exception {

		List<DocumentoAntefirmaRespuesta> lst = new ArrayList<DocumentoAntefirmaRespuesta>();

		log.info("Parametros de busqueda :: " + documentoAntefirma);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (documentoAntefirma.getDocumentoAntefirmaKey() != null) {

				if (StringUtils.isNotBlank(documentoAntefirma.getDocumentoAntefirmaKey().getFirmante())) {
					restrictions.add(Restrictions.eq("documentoAntefirmaKey.firmante",
							documentoAntefirma.getDocumentoAntefirmaKey().getFirmante()));
				}

				if (StringUtils.isNotBlank(documentoAntefirma.getDocumentoAntefirmaKey().getObjectId())) {
					restrictions.add(Restrictions.eq("documentoAntefirmaKey.objectId",
							documentoAntefirma.getDocumentoAntefirmaKey().getObjectId()));
				}

				if (StringUtils.isNotBlank(documentoAntefirma.getDocumentoAntefirmaKey().getTipo())) {
					restrictions.add(Restrictions.eq("documentoAntefirmaKey.tipo",
							documentoAntefirma.getDocumentoAntefirmaKey().getTipo()));
				}

				if (documentoAntefirma.getDocumentoAntefirmaKey().getTipoFirmante() != null) {
					restrictions.add(Restrictions.eq("documentoAntefirmaKey.tipoFirmante",
							documentoAntefirma.getDocumentoAntefirmaKey().getTipoFirmante()));
				}

				if (documentoAntefirma.getDocumentoAntefirmaKey().getId() != null) {
					restrictions.add(Restrictions.eq("documentoAntefirmaKey.id",
							documentoAntefirma.getDocumentoAntefirmaKey().getId()));
				}

			}

			if (documentoAntefirma.getFirmado() != null) {
				if (documentoAntefirma.getFirmado())
					restrictions.add(Restrictions.eq("firmado", documentoAntefirma.getFirmado()));
				else {
					LogicalExpression or_ = Restrictions.or(// false or null
							Restrictions.isNull("firmado"),
							Restrictions.eq("firmado", documentoAntefirma.getFirmado()));
					restrictions.add(or_);
				}
			}

			if (documentoAntefirma.getFechaFirma() != null) {
				restrictions.add(Restrictions.eq("fechaFirma", documentoAntefirma.getFechaFirma()));
			}

			if (documentoAntefirma.getTipoFirma() != null) {
				restrictions.add(Restrictions.eq("tipoFirma", documentoAntefirma.getTipoFirma()));
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("fechaFirma"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<DocumentoAntefirmaRespuesta>) mngrDocumentoAntefirmaRespuesta.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

			return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Agregar firmante antefirma", notes = "Agrega un firmante al documento de respuesta")
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
	
	@Override
	@RequestMapping(value = "/documentos/respuesta/antefirma", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoAntefirmaRespuesta> save(@RequestBody DocumentoAntefirmaRespuesta item)
			throws Exception {

		try {

			DocumentoAntefirmaRespuesta exist = get(//
					item.getDocumentoAntefirmaKey().getObjectId(), //
					item.getDocumentoAntefirmaKey().getId(), //
					item.getDocumentoAntefirmaKey().getTipo(), //
					item.getDocumentoAntefirmaKey().getFirmante(), //
					item.getDocumentoAntefirmaKey().getTipoFirmante()//
			).getBody();

			if (exist != null) {
				if (exist.getFirmado()) {
					// El usuario indicado ya ha firmado el documento.
					return new ResponseEntity<DocumentoAntefirmaRespuesta>(item, HttpStatus.CONFLICT);
				}

				mngrDocumentoAntefirmaRespuesta.update(item);

			} else {

				mngrDocumentoAntefirmaRespuesta.save(item);

				try {
					Map<String, String> additionalData = new HashMap<>();

					Usuario antefirmante = mngrUsuario.fetch(item.getDocumentoAntefirmaKey().getFirmante());

					additionalData.put("idAntefirmante",
							EndpointDispatcher.getInstance().getUserName(antefirmante.getUserKey()));

					String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
					String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

					EndpointDispatcher.getInstance(contetUser, password).addPermisos(
							item.getDocumentoAntefirmaKey().getObjectId(), "acl_send_antedirma", additionalData);

				} catch (Exception e) {
					
					mngrDocumentoAntefirmaRespuesta.delete(item);
					throw e;
				}

			}

			log.debug(" Item Out >> " + item);

			return new ResponseEntity<DocumentoAntefirmaRespuesta>(item, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
