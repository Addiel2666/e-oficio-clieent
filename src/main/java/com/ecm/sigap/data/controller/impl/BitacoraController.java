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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
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
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Bitacora;
import com.ecm.sigap.data.model.util.BitacoraTipoIdentificador;
import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.TipoAuditoria;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;


/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Bitacora}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class BitacoraController extends CustomRestController implements RESTController<Bitacora> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(BitacoraController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta bitacora", notes = "Consulta los movimientos de asuntos, respuestas y tramites del sistema")
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
	@RequestMapping(value = "/bitacora", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestWrapper<List<Bitacora>>> search(
			@RequestBody(required = true) RequestWrapper<Bitacora> body) throws Exception {

		try {
			Bitacora item = body.getObject();
			Map<String, Object> params = body.getParams();
			Long completeCount = 0L;

			Integer cantidadRegistros = null;

			if (null != body.getSize())
				cantidadRegistros = body.getSize();

			Integer empezarEn = null;

			if (null != body.getBeginAt())
				empezarEn = body.getBeginAt();
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (item.getGrupo() != null)
				restrictions.add(Restrictions.eq("grupo", item.getGrupo()));

			if (item.getIdentificador() != null)
				restrictions.add(Restrictions.eq("identificador", item.getIdentificador()));

			if (item.getTipoIdentificador() != null) {
				if (item.getTipoIdentificador().equals(BitacoraTipoIdentificador.T)) {
					restrictions.add(Restrictions.or(Restrictions.eq("tipoIdentificador", BitacoraTipoIdentificador.T),
							Restrictions.eq("tipoIdentificador", BitacoraTipoIdentificador.E),
							Restrictions.eq("tipoIdentificador", BitacoraTipoIdentificador.C)));
				} else {
					restrictions.add(Restrictions.eq("tipoIdentificador", item.getTipoIdentificador()));
				}
			}

			if (item.getUsuario() != null && (item.getUsuario().getIdUsuario() != null))
				restrictions.add(Restrictions.eq("usuario.idUsuario", item.getUsuario().getIdUsuario()));

			if (item.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", item.getIdArea()));
			
			if (item.getInstitucionId() != null)
            	restrictions.add(Restrictions.eq("institucionId", item.getInstitucionId()));
			
			if (item.getOrigenId() != null)
            	restrictions.add(Restrictions.eq("origenId", item.getOrigenId()));

			if (params != null) {

				if("R".equals(item.getTipoIdentificador().toString())) {
					try {
						if(params.get("origen") != null) {
							
							if (params.get("accion") != null) {
								
								switch (params.get("origen").toString()) {
								case "GENERATED":
									if(
											params.get("accion").equals(TipoAuditoria.SAVE.getValue()) || 
											params.get("accion").equals(TipoAuditoria.SEND.getValue()) ||
											params.get("accion").equals(TipoAuditoria.DELETE.getValue()) ||
											params.get("accion").equals(TipoAuditoria.UPDATE.getValue())
										) {
										restrictions.add(Restrictions.eq("accion", params.get("accion")));
									} else {
										restrictions.add(Restrictions.eq("accion", "NOGENERATED"));
									}
									
									break;
									
								case "RECIVIED":
									if(
											params.get("accion").equals(TipoAuditoria.ACCEPT.getValue()) || 
											params.get("accion").equals(TipoAuditoria.REJECT.getValue())
										) {
										restrictions.add(Restrictions.eq("accion", params.get("accion")));
									} else {
										restrictions.add(Restrictions.eq("accion", "NORECIVIED"));
									}
									break;
								}
								
							} else {
								if(params.get("origen").equals("GENERATED")) {
									List<String> acciones = new ArrayList<>();
									acciones.add(TipoAuditoria.SAVE.getValue());
									acciones.add(TipoAuditoria.SEND.getValue());
									restrictions.add(Restrictions.in("accion", acciones));							
								} else {
									if(params.get("origen").equals("RECIVIED")) {
										List<String> acciones = new ArrayList<>();
										acciones.add(TipoAuditoria.REJECT.getValue());
										acciones.add(TipoAuditoria.ACCEPT.getValue());
										restrictions.add(Restrictions.in("accion", acciones));
									}
								}
							}
							
						} else {
							if (params.get("accion") != null)
								restrictions.add(Restrictions.eq("accion", params.get("accion")));
						}
					} catch (Exception e) {
						if (params.get("accion") != null)
							restrictions.add(Restrictions.eq("accion", params.get("accion")));
					}
				} else {
					if (params.get("accion") != null)
						restrictions.add(Restrictions.eq("accion", params.get("accion")));
				}

				// FILSTROS PARA FECHAS
				if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
					restrictions.add(Restrictions.between("fechaRegistro", //
							new Date((Long) params.get("fechaRegistroInicial")),
							new Date((Long) params.get("fechaRegistroFinal"))));
				} else if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") == null) {
					restrictions
							.add(Restrictions.ge("fechaRegistro", new Date((Long) params.get("fechaRegistroInicial"))));
				} else if (params.get("fechaRegistroInicial") == null && params.get("fechaRegistroFinal") != null) {
					restrictions
							.add(Restrictions.le("fechaRegistro", new Date((Long) params.get("fechaRegistroFinal"))));
				}
			}

			ProjectionList projections = Projections.projectionList();

			projections.add(Projections.countDistinct("id").as("countr"));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("fechaRegistro"));

			// * * * * * * * * * * * * * * * * * * * * * *

			List<Bitacora> lst = mngrBitacora.search(restrictions, orders, null, cantidadRegistros, empezarEn);
			List<?> totalRegistros = mngrBitacora.search(restrictions, null, projections, null, null);

			Map<String, Long> map = (Map<String, Long>) totalRegistros.get(0);

			completeCount = map.get("countr");

			Map<String, Object> paramResult = new HashMap<>();
			paramResult.put("total", completeCount);

			RequestWrapper<List<Bitacora>> bitacora = new RequestWrapper<List<Bitacora>>();
			bitacora.setObject(lst);
			bitacora.setParams(paramResult);

			log.debug("Size found >> " + lst.size());
			log.debug("Size found >> " + completeCount);

			return new ResponseEntity<RequestWrapper<List<Bitacora>>>(bitacora, HttpStatus.OK);

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
	
	@ApiOperation(value = "Obtiene bitacora creador", notes = "Obtiene al usuario que creo el asunto")
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
	
	@RequestMapping(value = "/bitacora/created", method = RequestMethod.GET)
	public ResponseEntity<Bitacora> created(//
			@RequestParam(value = "id", required = true) Integer id, //
			@RequestParam(value = "tipo", required = true) String tipo) throws Exception {
		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("grupo", 1));

			restrictions.add(Restrictions.eq("identificador", id));

			restrictions.add(Restrictions.eq("tipoIdentificador", BitacoraTipoIdentificador.fromString(tipo)));

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.desc("fechaRegistro"));

			@SuppressWarnings("unchecked")
			List<Bitacora> bitacoras_ = (List<Bitacora>) mngrBitacora.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (DESC) * * * * * *
			Collections.sort(bitacoras_, new Comparator<Bitacora>() {
				@Override
				public int compare(Bitacora b1, Bitacora b2) {
					return b2.getFechaRegistro().compareTo(b1.getFechaRegistro());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			Bitacora bitacora_ = bitacoras_.isEmpty() ? new Bitacora() : (Bitacora) bitacoras_.get(0);

			return new ResponseEntity<Bitacora>(bitacora_, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * 
	 * @param id
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene bitacora modificado", notes = "Obtiene al usuario que lo puede modificar")
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
	
	@RequestMapping(value = "/bitacora/lastUpdate", method = RequestMethod.GET)
	public ResponseEntity<Bitacora> lastUpdate(//
			@RequestParam(value = "id", required = true) Integer id, //
			@RequestParam(value = "tipo", required = true) String tipo) throws Exception {
		try {

			BitacoraTipoIdentificador tipoIdentificador = BitacoraTipoIdentificador.fromString(tipo);

			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tipoIdentificador == BitacoraTipoIdentificador.A)
				restrictions.add(Restrictions.eq("grupo", 5));
			else
				restrictions.add(Restrictions.eq("grupo", 3));

			restrictions.add(Restrictions.eq("identificador", id));

			restrictions.add(Restrictions.eq("tipoIdentificador", tipoIdentificador));

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.desc("fechaRegistro"));

			@SuppressWarnings("unchecked")
			List<Bitacora> bitacoras_ = (List<Bitacora>) mngrBitacora.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (DESC) * * * * * *
			Collections.sort(bitacoras_, new Comparator<Bitacora>() {
				@Override
				public int compare(Bitacora b1, Bitacora b2) {
					return b2.getFechaRegistro().compareTo(b1.getFechaRegistro());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			Bitacora bitacora_ = bitacoras_.isEmpty() ? new Bitacora() : (Bitacora) bitacoras_.get(0);

			return new ResponseEntity<Bitacora>(bitacora_, HttpStatus.OK);

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
	public ResponseEntity<Bitacora> get(Serializable id) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	public ResponseEntity<Bitacora> save(Bitacora object) throws Exception {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(Bitacora object) throws Exception {
		throw new UnsupportedOperationException();
	}

}
