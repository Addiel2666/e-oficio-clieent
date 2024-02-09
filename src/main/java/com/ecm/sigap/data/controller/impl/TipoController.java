/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Tipo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Tipo}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class TipoController extends CustomRestController implements RESTController<Tipo> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TipoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<Tipo> get(Serializable id) {

		Tipo item = null;
		try {

			item = mngrTipo.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Tipo>(item, HttpStatus.OK);

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
	
	@ApiOperation(value = "Consulta tipo", notes = "Consulta la lista de tipo de un asunto")
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
	@RequestMapping(value = "/tipo", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Tipo Tipo) {

		List<Tipo> lst = new ArrayList<Tipo>();
		log.info("Parametros de busqueda :: " + Tipo);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (Tipo.getIdTipo() != null)
				restrictions.add(Restrictions.idEq(Tipo.getIdTipo()));

			if (Tipo.getDescripcion() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("descripcion", Tipo.getDescripcion(), MatchMode.ANYWHERE));

			if (Tipo.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", Tipo.getActivo()));

			//List<Order> orders = new ArrayList<Order>();
			//orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<Tipo>) mngrTipo.search(restrictions, null);
			
			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * * 
			Collections.sort(lst, new Comparator<Tipo>() {
				@Override
				public int compare(Tipo t1, Tipo t2) {
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

	@ApiOperation(value = "Agregar tipo", notes = "Agrega un tipo de asunto a la lista")
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
	@RequestMapping(value = "/tipo", method = RequestMethod.PUT, produces = { "application/json;charset=utf-8",
			"text/plain;charset=utf-8" })
	public @ResponseBody ResponseEntity<Tipo> save(@RequestBody(required = true) Tipo tipo) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			boolean isInsert = (tipo.getIdTipo() == null);
			if (!esSoloLectura(userId)) {

				log.debug("::>> TIPO A GUARDAR O ACTUALIZAR >> " + tipo);

				// Se agrego para validar si descripción ya existe
				if (validarDuplicados(tipo, isInsert)) {
					if (isInsert) {

						mngrTipo.save(tipo);

						return new ResponseEntity<Tipo>(tipo, HttpStatus.CREATED);

					} else {

						mngrTipo.update(tipo);

						return new ResponseEntity<Tipo>(tipo, HttpStatus.OK);

					}
				} else {
					return new ResponseEntity<Tipo>(tipo, HttpStatus.CONFLICT);
				}

			} else {

				return new ResponseEntity<Tipo>(tipo, HttpStatus.BAD_REQUEST);

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

	}

	/**
	 * Valida si la descripción ya existe
	 */
	@SuppressWarnings("unchecked")
	private boolean validarDuplicados(Tipo Tipo, boolean isInsert) {
		boolean isValid = true;
		boolean b = true;
		if(!isInsert){
			Tipo tipoTmp = mngrTipo.fetch(Tipo.getIdTipo());
			b = (tipoTmp.getActivo() == Tipo.getActivo());
		}
		List<Criterion> restrictions = new ArrayList<Criterion>();
		if (Tipo.getDescripcion() != null)
			restrictions.add(EscapedLikeRestrictions.ilike("descripcion", Tipo.getDescripcion(), MatchMode.ANYWHERE));
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("descripcion"));
		List<Tipo> lst = new ArrayList<Tipo>();
		lst = (List<Tipo>) mngrTipo.search(restrictions, orders);
		if (lst != null && lst.size() > 0 && b) {
			isValid = false;
		}
		return isValid;
	}

}