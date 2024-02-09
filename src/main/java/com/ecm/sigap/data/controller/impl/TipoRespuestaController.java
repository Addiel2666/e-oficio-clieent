/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.TipoRespuesta;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.TipoRespuesta}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class TipoRespuestaController extends CustomRestController implements RESTController<TipoRespuesta> {
	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TipoRespuestaController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene tipo respuesta", notes = "Obtiene el detalle de tipo respuesta de la seccion respuesta")
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
	@RequestMapping(value = "/tipoRespuesta", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TipoRespuesta> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		TipoRespuesta item = null;
		try {

			item = mngrTipoRespuesta.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<TipoRespuesta>(item, HttpStatus.OK);
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
	
	@ApiOperation(value = "Consulta tipo respuesta", notes = "Consulta la lista de tipo respuesta de la seccion respuesta")
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
	@RequestMapping(value = "/tipoRespuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) TipoRespuesta tipoRespuesta) {

		List<TipoRespuesta> lst = new ArrayList<TipoRespuesta>();
		log.info("Parametros de busqueda :: " + tipoRespuesta);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tipoRespuesta.getIdTipoRespuesta() != null)
				restrictions.add(Restrictions.idEq(tipoRespuesta.getIdTipoRespuesta()));

			if (tipoRespuesta.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", tipoRespuesta.getDescripcion(), MatchMode.ANYWHERE));
			else
				restrictions.add(Restrictions.isNotNull("descripcion"));
				
			if (tipoRespuesta.getTipoConcluida() != null)
				restrictions.add(Restrictions.eq("tipoConcluida", tipoRespuesta.getTipoConcluida()));

			if (tipoRespuesta.getInfomex() != null)
				restrictions.add(Restrictions.eq("infomex", tipoRespuesta.getInfomex()));

			//List<Order> orders = new ArrayList<Order>();

			//orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<TipoRespuesta>) mngrTipoRespuesta.search(restrictions, null);
			
			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * * 
			Collections.sort(lst, new Comparator<TipoRespuesta>() {
				@Override
				public int compare(TipoRespuesta t1, TipoRespuesta t2) {
					return t1.getDescripcion().compareTo(t2.getDescripcion());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

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

	@ApiOperation(value = "Agregar tipo respuesta", notes = "Agrega o edita un tipo respuesta de la seccion respuesta")
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
	@RequestMapping(value = "/tipoRespuesta", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<TipoRespuesta> save(@RequestBody(required = true) TipoRespuesta tipoRespuesta)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> TIPO RESPUESTA A GUARDAR O ACTUALIZAR >> " + tipoRespuesta);

				if (tipoRespuesta.getIdTipoRespuesta() == null) {
					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoRespuesta no se esten violando con este nuevo
					// registro
					validateEntity(mngrTipoRespuesta, tipoRespuesta);

					// Guardamos la informacion
					mngrTipoRespuesta.save(tipoRespuesta);

					return new ResponseEntity<TipoRespuesta>(tipoRespuesta, HttpStatus.CREATED);
				} else {
					// Actualizamos la informacion
					mngrTipoRespuesta.update(tipoRespuesta);

					log.debug("::>> Registro Actualizado");
					return new ResponseEntity<TipoRespuesta>(tipoRespuesta, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<TipoRespuesta>(tipoRespuesta, HttpStatus.BAD_REQUEST);
			}
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
	public void delete(Serializable id) {
		throw new UnsupportedOperationException();
	}

}
