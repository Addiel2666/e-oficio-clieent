/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Destinatario;
import com.ecm.sigap.data.model.RespuestaCopia;
import com.ecm.sigap.data.model.RespuestaCopiaKey;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.util.StatusAsunto;
import com.ecm.sigap.data.model.util.SubTipoAsunto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

// TODO: Auto-generated Javadoc

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.RespuestaCopia}
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */
@RestController
public class RespuestaCopiaController extends CustomRestController implements RESTController<RespuestaCopia> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(RespuestaCopiaController.class);

	/**
	 * Search.
	 *
	 * @param body the body
	 * @return the response entity
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consultar copias respuestas", notes = "Consulta la lista de copias respuestas")
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
	@RequestMapping(value = "/respuestaCopia", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) RequestWrapper<RespuestaCopia> body) {

		List<RespuestaCopia> lst = new ArrayList<RespuestaCopia>();

		RespuestaCopia respuestaCopia = body.getObject();
		Map<String, Object> params = body.getParams();

		log.info("Parametros de busqueda :: " + body);

		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (null != respuestaCopia.getRespuestaCopiaKey()) {

				if (null != respuestaCopia.getRespuestaCopiaKey().getIdArea())
					restrictions.add(Restrictions.eq("respuestaCopiaKey.idArea",
							respuestaCopia.getRespuestaCopiaKey().getIdArea()));

				if (null != respuestaCopia.getRespuestaCopiaKey().getIdAsunto())
					restrictions.add(Restrictions.eq("respuestaCopiaKey.idAsunto",
							respuestaCopia.getRespuestaCopiaKey().getIdAsunto()));

				if (null != respuestaCopia.getRespuestaCopiaKey().getIdDestinatario())
					restrictions.add(Restrictions.eq("respuestaCopiaKey.idDestinatario",
							respuestaCopia.getRespuestaCopiaKey().getIdDestinatario()));

				if (null != respuestaCopia.getRespuestaCopiaKey().getIdSubTipoAsunto())
					restrictions.add(Restrictions.eq("respuestaCopiaKey.idSubTipoAsunto",
							respuestaCopia.getRespuestaCopiaKey().getIdSubTipoAsunto()));

				if (respuestaCopia.getRespuesta() != null) {
					if (respuestaCopia.getRespuesta().getIdRespuesta() != null)
						restrictions.add(Restrictions.eq("respuestaCopiaKey.idRespuesta",
								respuestaCopia.getRespuesta().getIdRespuesta()));
				}

			}
			if (params != null && !params.isEmpty()) {

				if (params.get("statusList") != null) {
					List<Integer> listStatus = new ArrayList<>();
					List<String> ids = (List<String>) params.get("statusList");
					if (!ids.isEmpty()) {
						for (String id : ids) {
							listStatus.add(StatusAsunto.valueOf(id).ordinal());
						}
						restrictions.add(Restrictions.in("status.idStatus", listStatus));
					}
				}

				if (params.get("asuntoPadreId") != null) {
					restrictions.add(Restrictions.eq("respuesta.idAsuntoPadre",
							Integer.parseInt(params.get("asuntoPadreId").toString())));
				}

				// FILTROS PARA FECHAS
				if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") != null) {

					restrictions.add(Restrictions.between("la_respuesta.fechaEnvio", //
							new Date((Long) params.get("fechaEnvioInicial")),
							new Date((Long) params.get("fechaEnvioFinal"))));

				} else if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") == null) {

					restrictions.add(Restrictions.ge("la_respuesta.fechaEnvio",
							new Date((Long) params.get("fechaEnvioInicial"))));

				} else if (params.get("fechaEnvioInicial") == null && params.get("fechaEnvioFinal") != null) {

					restrictions.add(
							Restrictions.le("la_respuesta.fechaEnvio", new Date((Long) params.get("fechaEnvioFinal"))));

				}

			}

			if (respuestaCopia.getRespuesta() != null) {
				if (respuestaCopia.getRespuesta().getAreaDestinoId() != null)
					restrictions.add(Restrictions.eq("la_respuesta.areaDestinoId",
							respuestaCopia.getRespuesta().getAreaDestinoId()));
				
				if (respuestaCopia.getRespuesta().getArea() != null)
					if (respuestaCopia.getRespuesta().getArea().getIdArea() != null)
						restrictions.add(Restrictions.eq("la_respuesta.area.idArea",
								respuestaCopia.getRespuesta().getArea().getIdArea()));
			}
				
			
			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<RespuestaCopia>) mngrRespuestaCopia.search(restrictions, null);
			

			// * * * * * ORDENA EL LIST POR RESPUESTA_FECHAREGISTRO (DESC) * * * * * *
			Collections.sort(lst, new Comparator<RespuestaCopia>() {
				@Override
				public int compare(RespuestaCopia rc1, RespuestaCopia rc2) {
					return (rc2.getRespuesta().getFechaRegistro()).compareTo(rc1.getRespuesta().getFechaRegistro());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * 
	 * @param idRespuesta
	 * @return
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene copias respuestas", notes = "Obtiene detalle de copias respuestas")
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
	@RequestMapping(value = "/respuestaCopia", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> getCopias(
			@RequestParam(value = "idRespuesta", required = true) Serializable idRespuesta) {

		Integer id = Integer.valueOf((String) idRespuesta);
		List<RespuestaCopia> lst = new ArrayList<RespuestaCopia>();
		List<Criterion> restrictions = new ArrayList<Criterion>();

		restrictions.add(Restrictions.eq("respuestaCopiaKey.idRespuesta", id));

		lst = (List<RespuestaCopia>) mngrRespuestaCopia.search(restrictions);
		
		// (Cuando le cambian el titular al area no conincide el titular
		// guardado en la copia con el titular que trae la vista)
		// Valida este caso en las copias para los tipo Internos
		for (RespuestaCopia copia : lst) {
			if (SubTipoAsunto.C.getValue().equals(copia.getRespuestaCopiaKey().getIdSubTipoAsunto())
					&& copia.getRespuestaCopiaKey().getIdArea() != null) {
				// Busca el titular del area para compararlo con el iddestinatario
				Area area = mngrArea.fetch(copia.getRespuestaCopiaKey().getIdArea());
				if (area != null) {
					// Consulta los datos del destinatario para setearlo en la copia
					copia.setArea(
						getDestinatarioInterno(
							copia.getRespuestaCopiaKey().getIdArea(),
							copia.getRespuestaCopiaKey().getIdDestinatario()
						)
					);
				}
			}
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	public ResponseEntity<RespuestaCopia> save(RespuestaCopia respuestaCopia) throws Exception {
		{
			try {
				String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
				if (!esSoloLectura(userId)) {

					log.debug("RespuestaCopia A GUARDAR >> " + respuestaCopia);
					if (respuestaCopia.getRespuesta().getIdRespuesta() != null) {
						mngrRespuestaCopia.update(respuestaCopia);
						return new ResponseEntity<RespuestaCopia>(respuestaCopia, HttpStatus.OK);
					}

				}
				return new ResponseEntity<RespuestaCopia>(respuestaCopia, HttpStatus.BAD_REQUEST);

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
				throw e;
			}
		}
	}

	/**
	 * Consultar respuesta por recibir.
	 *
	 * @return the response entity
	 */
	// Parametros para ser usados por Swagger
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", required = true, dataType = "int", paramType = "header") })
	@RequestMapping(value = "/respuestaCopia/porRecibir", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> consultarRespuestaPorRecibir() {

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		List<?> lst = new ArrayList<RespuestaCopia>();
		List<Criterion> restrictions = new ArrayList<Criterion>();

		try {

			restrictions.add(Restrictions.eq("respuestaCopiaKey.idArea", idArea));

			restrictions.add(Restrictions.eq("status.idStatus", Status.ENVIADO));

			lst = mngrRespuestaCopia.search(restrictions, null);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(RespuestaCopia object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseEntity<RespuestaCopia> get(Serializable id) {

		return null;
	}

	/**
	 * ConcluirCopia.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Marcar atendido copia respuesta", notes = "Marca como atendido y concluye una copia respuesta")
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
	
	@RequestMapping(value = "/respuestaCopia/concluir", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RespuestaCopia> concluir(
			@RequestBody(required = true) RespuestaCopia respuestaCopia) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("RespuestaCopia A GUARDAR >> " + respuestaCopia);
				if (respuestaCopia.getRespuesta().getIdRespuesta() != null
						&& !respuestaCopia.getStatus().getIdStatus().equals(Status.ATENDIDO)) {
					respuestaCopia.setStatus(mngrStatus.fetch(Status.ATENDIDO));
					mngrRespuestaCopia.update(respuestaCopia);
					return new ResponseEntity<RespuestaCopia>(respuestaCopia, HttpStatus.OK);
				}

			}
			return new ResponseEntity<RespuestaCopia>(respuestaCopia, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene respuesta copia", notes = "Obtiene el detalle de una respuesta copia")
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
	@RequestMapping(value = "/respuestaCopia/isRespuestaCopia", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> isRespuestaCopia(//
			@RequestParam(value = "asuntoPadreId", required = true) Integer idAsunto, //
			@RequestParam(value = "idArea", required = true) Integer idArea) {

		Map<String, Object> respuestaCopia = new HashMap<String, Object>();

		try {

			RequestWrapper<RespuestaCopia> body_ = new RequestWrapper<RespuestaCopia>();
			body_.setObject(new RespuestaCopia());
			body_.getObject().setRespuestaCopiaKey(new RespuestaCopiaKey());

			body_.getObject().getRespuestaCopiaKey().setIdArea(idArea);

			ResponseEntity<List<?>> resp_ = search(body_);

			List<RespuestaCopia> copias_ = (List<RespuestaCopia>) resp_.getBody();

			Integer idAsuntoPadreTmp;

			for (RespuestaCopia respuestaCopia_ : copias_) {
				idAsuntoPadreTmp = respuestaCopia_.getRespuesta().getIdAsuntoPadre();
				if (idAsuntoPadreTmp.equals(idAsunto)) {
					respuestaCopia.put("isRespuestaCopia", true);
					return new ResponseEntity<Map<String, Object>>(respuestaCopia, HttpStatus.OK);
				}
			}

			respuestaCopia.put("isRespuestaCopia", false);
			return new ResponseEntity<Map<String, Object>>(respuestaCopia, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	/**
	 * Gets the destinatario interno.
	 *
	 * @param idArea the id area
	 * @return the destinatario interno
	 */
	public Destinatario getDestinatarioInterno(Integer idArea, String identificador) {

		String query = "SELECT d from Destinatario d WHERE d.idArea = " + idArea + " AND d.identificador = '" + identificador + "'";
				//+ "' AND d.idSubTipoAsunto='" + SubTipoAsunto.C + "'";

		List<Destinatario> destinatario = (List<Destinatario>) mngrDestinatario.execQuery(query);

		if (null == destinatario || destinatario.isEmpty() || destinatario.size() > 1) {
			return null;
		}

		return destinatario.get(0);
	}

}
