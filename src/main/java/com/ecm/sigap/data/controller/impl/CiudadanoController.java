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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import com.ecm.sigap.data.model.Ciudadano;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Ciudadano}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class CiudadanoController extends CustomRestController implements RESTController<Ciudadano> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(CiudadanoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/* 
	 * Documentacion con swagger
	*/

	@ApiOperation(value = "Obtiene destinatario ciudadano", notes = "Obtiene el detalle del destinatario ciudadano")
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
	
	@Override
	@RequestMapping(value = "/ciudadano", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Ciudadano> get(@RequestParam(value = "id", required = true) Serializable id) {

		Ciudadano item = null;
		try {

			item = mngrCiudadano.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Ciudadano>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario ciudadano", notes = "Consulta a los destinatarios de ciudadanos")
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
	
	@Override
	@RequestMapping(value = "/ciudadano", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Ciudadano ciudadano) {

		List<?> lst = new ArrayList<Ciudadano>();
		log.debug("PARAMETROS DE BUSQUEDA : " + ciudadano);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (ciudadano.getId() != null)
				restrictions.add(Restrictions.idEq(ciudadano.getId()));

			if (ciudadano.getActivosn() != null)
				restrictions.add(Restrictions.eq("activosn", ciudadano.getActivosn()));

			if ((ciudadano.getCurp() != null) && (!ciudadano.getCurp().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("curp", ciudadano.getCurp(), MatchMode.ANYWHERE));

			if ((ciudadano.getEmail() != null) && (!ciudadano.getEmail().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("email", ciudadano.getEmail(), MatchMode.ANYWHERE));

			if ((ciudadano.getHomonimo() != null) && (!ciudadano.getHomonimo().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("homonimo", ciudadano.getHomonimo(), MatchMode.ANYWHERE));

			if ((ciudadano.getIdTipo() != null) && (!ciudadano.getIdTipo().isEmpty()))
				restrictions.add(Restrictions.eq("idTipo", ciudadano.getIdTipo()));

			if ((ciudadano.getMaterno() != null) && (!ciudadano.getMaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("materno", ciudadano.getMaterno(), MatchMode.ANYWHERE));

			if ((ciudadano.getPaterno() != null) && (!ciudadano.getPaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", ciudadano.getPaterno(), MatchMode.ANYWHERE));

			if ((ciudadano.getNombres() != null) && (!ciudadano.getNombres().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombres", ciudadano.getNombres(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(ciudadano.getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCompleto", ciudadano.getNombreCompleto(),
						MatchMode.ANYWHERE));

			if ((ciudadano.getRfc() != null) && (!ciudadano.getRfc().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("rfc", ciudadano.getRfc(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrCiudadano.search(restrictions, orders);

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
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	

	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Eliminar ciudadano", notes = "Elimina a un ciudadano")
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
	
	@Override
	@RequestMapping(value = "/ciudadano", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("CIUDADANO A ELIMINAR >> " + id);

		try {

			mngrCiudadano.delete(mngrCiudadano.fetch(Integer.valueOf((String) id)));

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
	
	@ApiOperation(value = "Agregar ciudadano", notes = "Agrega a un nuevo ciudadano")
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
	@RequestMapping(value = "/ciudadano", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Ciudadano> save(@RequestBody(required = true) Ciudadano ciudadano)
			throws Exception {
		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {
				log.debug("CIUDADANO A GUARDAR >> " + ciudadano);

				// Valida curp/rfc
				if (validarRFCyCURP(ciudadano)) {

					if (ciudadano.getId() == null) {
						mngrCiudadano.save(ciudadano);
						return new ResponseEntity<Ciudadano>(ciudadano, HttpStatus.CREATED);

					} else {
						mngrCiudadano.update(ciudadano);
						return new ResponseEntity<Ciudadano>(ciudadano, HttpStatus.OK);
					}

				} else {
					return new ResponseEntity<Ciudadano>(ciudadano, HttpStatus.CONFLICT);
				}

			} else {
				return new ResponseEntity<Ciudadano>(ciudadano, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Valida que el RFC y/o CURP del ciudadano no exista en los registros.
	 * 
	 * @param Ciudadano
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean validarRFCyCURP(Ciudadano c) {
		boolean isValid = true;
		List<Ciudadano> lst = new ArrayList<Ciudadano>();
		List<Criterion> restrictions = new ArrayList<Criterion>();

		if (c.getId() != null)
			restrictions.add(Restrictions.not(Restrictions.eq("id", c.getId())));

		if ((c.getCurp() != null) && (!c.getCurp().isEmpty()) && (c.getRfc() != null) && (!c.getRfc().isEmpty())) {
			restrictions.add(Restrictions.or(Restrictions.eq("curp", c.getCurp()).ignoreCase(),
					Restrictions.eq("rfc", c.getRfc()).ignoreCase()));
		} else if ((c.getCurp() != null) && (!c.getCurp().isEmpty())) {
			restrictions.add(Restrictions.eq("curp", c.getCurp()).ignoreCase());
		} else if ((c.getRfc() != null) && (!c.getRfc().isEmpty())) {
			restrictions.add(Restrictions.eq("rfc", c.getRfc()).ignoreCase());
		} else {
			return isValid;
		}

		lst = (List<Ciudadano>) mngrCiudadano.search(restrictions);
		if (lst != null && lst.size() > 0) {
			isValid = false;
		}
		return isValid;
	}

}
