/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Criterion;
import org.springframework.http.HttpStatus;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroKey;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Parametro}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class ParametroController extends CustomRestController implements RESTController<Parametro> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ParametroController.class);

	/**
	 * Gets the.
	 *
	 * @param idArea
	 *            the id area
	 * @param idSeccion
	 *            the id seccion
	 * @param idClave
	 *            the id clave
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene parametro", notes = "Obtiene detalle del parametro de modalidad de entrega")
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
	
	@RequestMapping(value = "/parametro", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Parametro> get(
			@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idSeccion", required = true) Serializable idSeccion,
			@RequestParam(value = "idClave", required = true) Serializable idClave) {

		Parametro item = null;
		ParametroKey parametroKey = new ParametroKey();
		try {

			parametroKey.setIdArea(Integer.valueOf((String) idArea));
			parametroKey.setIdSeccion(String.valueOf((String) idSeccion));
			parametroKey.setIdClave(String.valueOf((String) idClave));

			item = mngrParametro.fetch(parametroKey);

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Parametro>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/parametro", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Parametro parametro) {

		List<?> lst = new ArrayList<Parametro>();
		log.info("Parametros de busqueda :: " + parametro);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (parametro.getParametroKey() != null) {

				if (parametro.getParametroKey().getIdArea() != null)
					restrictions.add(Restrictions.eq("parametroKey.idArea", parametro.getParametroKey().getIdArea()));

				if (parametro.getParametroKey().getIdSeccion() != null
						&& !parametro.getParametroKey().getIdSeccion().isEmpty())
					restrictions
							.add(Restrictions.eq("parametroKey.idSeccion", parametro.getParametroKey().getIdSeccion()));

				if (parametro.getParametroKey().getIdClave() != null
						&& !parametro.getParametroKey().getIdClave().isEmpty())
					restrictions.add(Restrictions.eq("parametroKey.idClave", parametro.getParametroKey().getIdClave()));

			}

			if (parametro.getValor() != null && !parametro.getValor().isEmpty())
				restrictions.add(Restrictions.eq("valor", parametro.getValor()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("parametroKey.idArea"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrParametro.search(restrictions, orders);

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

	@ApiOperation(value = "Guardar parametro", notes = "Guarda un nuevo parametro en la lista")
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
	@RequestMapping(value = "/parametro", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Parametro> save(@RequestBody(required = true) Parametro parametro)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("PARAMETRO A GUARDAR >> " + parametro);

				if ((parametro.getParametroKey() != null && parametro.getParametroKey().getIdArea() != null)
						&& (parametro.getParametroKey().getIdSeccion() != null
								&& !parametro.getParametroKey().getIdSeccion().isEmpty())
						&& (parametro.getParametroKey().getIdClave() != null
								&& !parametro.getParametroKey().getIdClave().isEmpty())) {

					Parametro parametroTemp = mngrParametro.fetch(parametro.getParametroKey());

					if (parametroTemp == null) {

						mngrParametro.save(parametro);
						return new ResponseEntity<Parametro>(parametro, HttpStatus.CREATED);
					} else {
						mngrParametro.update(parametro);
						return new ResponseEntity<Parametro>(parametro, HttpStatus.OK);
					}

				} else {

					return new ResponseEntity<Parametro>(parametro, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Parametro>(parametro, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Delete.
	 *
	 * @param idArea
	 *            the id area
	 * @param idSeccion
	 *            the id seccion
	 * @param idClave
	 *            the id clave
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar parametro", notes = "Elimina un parametro de la lista")
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
	
	@RequestMapping(value = "/parametro", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idSeccion", required = true) Serializable idSeccion,
			@RequestParam(value = "idClave", required = true) Serializable idClave) {

		ParametroKey parametroKey = new ParametroKey();

		try {
			parametroKey.setIdArea(Integer.valueOf((String) idArea));
			parametroKey.setIdSeccion(String.valueOf((String) idSeccion));
			parametroKey.setIdClave(String.valueOf((String) idClave));

			log.debug("PARAMETRO A ELIMINAR >> " + parametroKey);

			mngrParametro.delete(mngrParametro.fetch(parametroKey));

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
	public ResponseEntity<Parametro> get(Serializable id) {
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