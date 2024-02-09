/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.AreaRevisor;
import com.ecm.sigap.data.model.AreaRevisorKey;
import com.ecm.sigap.data.model.Representante;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;


/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.AreaRevisor}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class AreaRevisorController extends CustomRestController implements RESTController<AreaRevisor> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AreaRevisorController.class);

	/*
	 * Obtiene la informacion de los Revisores que estan como favoritos del
	 * Usuario
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta remitente revision", notes = "Consulta a todos los remitentes")
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
	@RequestMapping(value = "/areaRevisor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) AreaRevisor areaRevisor) {

		List<?> lst = new ArrayList<AreaRevisor>();
		log.info("Parametros de busqueda :: " + areaRevisor);

		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (areaRevisor.getAreaRevisorKey().getIdArea() != null)
				restrictions.add(Restrictions.eq("areaRevisorKey.idArea", areaRevisor.getAreaRevisorKey().getIdArea()));

			if (StringUtils.isNotBlank(areaRevisor.getAreaRevisorKey().getIdUsuario()))
				restrictions.add(
						Restrictions.eq("areaRevisorKey.idUsuario", areaRevisor.getAreaRevisorKey().getIdUsuario()));

			// Filtro por Nombre y Apellidos del Revisor
			if (null != areaRevisor.getAreaRevisorKey().getRevisor()) {

				DetachedCriteria subquery = DetachedCriteria.forClass(Representante.class, "revisor");
				subquery.setProjection(Projections.property("revisor.id"));
				subquery.add(Restrictions.eqProperty("areaRevisor.areaRevisorKey.revisor.id", "revisor.id"));

				if (StringUtils.isNotBlank(areaRevisor.getAreaRevisorKey().getRevisor().getNombres())) {
					subquery.add(EscapedLikeRestrictions.ilike("revisor.nombres",
							areaRevisor.getAreaRevisorKey().getRevisor().getNombres(), MatchMode.ANYWHERE));
				}

				if (StringUtils.isNotBlank(areaRevisor.getAreaRevisorKey().getRevisor().getPaterno())) {
					subquery.add(EscapedLikeRestrictions.ilike("revisor.paterno",
							areaRevisor.getAreaRevisorKey().getRevisor().getPaterno(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotBlank(areaRevisor.getAreaRevisorKey().getRevisor().getMaterno())) {
					subquery.add(EscapedLikeRestrictions.ilike("revisor.materno",
							areaRevisor.getAreaRevisorKey().getRevisor().getMaterno(), MatchMode.ANYWHERE));
				}
				restrictions.add(Subqueries.exists(subquery));
			}

			List<Order> orders = new ArrayList<Order>();
			// orders.add(Order.asc("areaRevisor.areaRevisorKey.revisor.paterno"));
			lst = mngrAreaRevisor.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Obtiene la lista de Remitentes que no estan incluidos en los favoritos de
	 * Remitentes
	 * 
	 * @param areaRevisor
	 *            Objeto del tipos {@link com.ecm.sigap.data.model.AreaRevisor}
	 * @return Lista de usuarios internos no incluidos en los favoritos
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta remitente no favorito", notes = "Consulta a los remitentes que no estan incluidos en favoritos")
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
	
	@RequestMapping(value = "/areaRevisor/noFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchNoFavoritos(
			@RequestBody(required = true) AreaRevisor areaRevisor) {

		List<?> lst = new ArrayList<AreaRevisor>();
		log.info("Parametros de busqueda :: " + areaRevisor);

		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("institucion.tipo", "I"));

			restrictions.add(Restrictions.eq("representante.activosn", Boolean.TRUE));

			// Se saca de la lista de revisores al usuario que esta haciendo la
			// consulta
			String usuarioRequest = getHeader(HeaderValueNames.HEADER_USER_ID);
			restrictions.add(Restrictions.ne("representante.id", usuarioRequest));

			// Filtro por Nombre y Apellidos del Revisor
			if (null != areaRevisor.getAreaRevisorKey().getRevisor()) {

				if (  StringUtils.isNotBlank(areaRevisor.getAreaRevisorKey().getRevisor().getNombres())) {
					restrictions.add(EscapedLikeRestrictions.ilike("representante.nombres",
							areaRevisor.getAreaRevisorKey().getRevisor().getNombres(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotBlank(areaRevisor.getAreaRevisorKey().getRevisor().getPaterno())) {
					restrictions.add(EscapedLikeRestrictions.ilike("representante.paterno",
							areaRevisor.getAreaRevisorKey().getRevisor().getPaterno(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotBlank( areaRevisor.getAreaRevisorKey().getRevisor().getMaterno())) {
					restrictions.add(EscapedLikeRestrictions.ilike("representante.materno",
							areaRevisor.getAreaRevisorKey().getRevisor().getMaterno(), MatchMode.ANYWHERE));
				}
			}

			DetachedCriteria subquery = DetachedCriteria.forClass(AreaRevisor.class,"AreaRevisor");
			subquery.setProjection(Projections.property("areaRevisorKey.revisor.id"));
			subquery.add(Restrictions.eqProperty("areaRevisorKey.revisor.id", "representante.id"));
			subquery.add(Restrictions.eq("areaRevisorKey.idArea", areaRevisor.getAreaRevisorKey().getIdArea()));
			subquery.add(Restrictions.eq("areaRevisorKey.idUsuario", areaRevisor.getAreaRevisorKey().getIdUsuario()));

			restrictions.add(Subqueries.notExists(subquery));

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("representante.paterno"));

			lst = mngrRepresentante.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Agregar favoritos", notes = "Agrega un remitente a favoritos")
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
	@RequestMapping(value = "/areaRevisor", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<AreaRevisor> save(@RequestBody(required = true) AreaRevisor areaRevisor)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("AREAREVISOR A GUARDAR >> " + areaRevisor);

				if ((areaRevisor.getAreaRevisorKey().getIdArea() != null)
						&& (areaRevisor.getAreaRevisorKey().getRevisor().getId() != null)
						&& (areaRevisor.getAreaRevisorKey().getIdUsuario() != null)) {

					// Validamos que las reglas de validacion de la entidad Tipo
					// AreaRevisor no se esten violando con este nuevo registro
					validateEntity(mngrAreaRevisor, areaRevisor);

					// Guardamos la informacion
					mngrAreaRevisor.save(areaRevisor);
					return new ResponseEntity<AreaRevisor>(areaRevisor, HttpStatus.CREATED);

				} else {
					return new ResponseEntity<AreaRevisor>(areaRevisor, HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<AreaRevisor>(areaRevisor, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Delete.
	 *
	 * @param areaRevisor
	 *            the area revisor
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Eliminar favoritos", notes = "Elimina a un remitente de favoritos")
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
	
	@RequestMapping(value = "/areaRevisor", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idRevisor", required = true) Serializable idRevisor,
			@RequestParam(value = "idUsuario", required = true) Serializable idUsuario) {

		try {

			Representante areaRevisor = new Representante();
			areaRevisor.setId(String.valueOf((String) idRevisor));

			AreaRevisorKey areaRevisorKey = new AreaRevisorKey();
			areaRevisorKey.setIdArea(Integer.valueOf((String) idArea));
			areaRevisorKey.setRevisor(areaRevisor);
			areaRevisorKey.setIdUsuario(String.valueOf((String) idUsuario));

			log.debug("PROMOTOR A ELIMINAR >> " + areaRevisor);

			if ((areaRevisorKey.getIdArea() != null) && (areaRevisorKey.getRevisor().getId() != null)
					&& (areaRevisorKey.getIdUsuario() != null)) {

				mngrAreaRevisor.delete(mngrAreaRevisor.fetch(areaRevisorKey));
			}
			log.debug("DELETE! ");

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<AreaRevisor> get(Serializable id) {
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

}
