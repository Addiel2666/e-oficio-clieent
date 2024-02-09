/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

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
import com.ecm.sigap.data.model.TipoInstruccion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.TipoInstruccion}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class TipoInstruccionController extends CustomRestController implements RESTController<TipoInstruccion> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TipoInstruccionController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene instruccion", notes = "Obtiene el detalle de una instruccion de la seccion nuevo tramite")
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
	@RequestMapping(value = "/tipoInstruccion", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody ResponseEntity<TipoInstruccion> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("::>> Obteniendo el Tipo de Instruccion con identificador " + id);

		TipoInstruccion item = null;

		item = mngrTipoInstruccion.fetch(Integer.parseInt(id.toString()));

		if (item != null) {
			log.debug("::>> Se van a retornar el Tipo de Instruccion: " + item);
			return new ResponseEntity<TipoInstruccion>(item, HttpStatus.OK);
		} else {
			log.debug("::>> No existe el registro !!");
			return new ResponseEntity<TipoInstruccion>(item, HttpStatus.NO_CONTENT);
		}
	}

	/**
	 *
	 * Obtiene el listado de Instrucciones que concuerde con los parametros
	 *
	 * @param id
	 *            Identificador del Area del cual se quieren obtener los Tipos
	 *            de Instruccion
	 * @return Lista de los Tipos de Instruccion que estan asociados al
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta instruccion", notes = "Consulta la lista de instrucciones de la seccion nuevo tramite")
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
	
	@RequestMapping(value = "/tipoInstruccion", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) TipoInstruccion tipoInstruccion) {
		try {
			log.debug("::>> Obteniendo todos los Tipo de Instruccion con los siguientes criterios " + tipoInstruccion);

			List<?> items = null;

			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("area.idArea", tipoInstruccion.getArea().getIdArea()));

			if (tipoInstruccion.getDescripcion() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("descripcion", tipoInstruccion.getDescripcion(), MatchMode.ANYWHERE));

			if (tipoInstruccion.getRequiereRespuesta() != null)
				restrictions.add(Restrictions.eq("requiereRespuesta", tipoInstruccion.getRequiereRespuesta()));

			if (tipoInstruccion.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", tipoInstruccion.getActivo()));

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("descripcion"));

			items = mngrTipoInstruccion.search(restrictions, orders);

			log.debug("::>> Se van a retornar " + items.size() + " tipos de instruccion para los parametros "
					+ tipoInstruccion);

			return new ResponseEntity<List<?>>(items, HttpStatus.OK);

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

	@ApiOperation(value = "Guardar instruccion", notes = "Guarda o edita una instruccion de la seccion nuevo tramite")
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
	
	@RequestMapping(value = "/tipoInstruccion", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody ResponseEntity<TipoInstruccion> save(
			@RequestBody(required = true) TipoInstruccion tipoInstruccion) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> Guardando los valores del Tipo de Instruccion: " + tipoInstruccion);

				if (tipoInstruccion.getIdInstruccion() == null) {

					// Validamos que las reglas de validacion de la entidad Tipo
					// Institucion no se esten violando con este nuevo registro
					//validateEntity(mngrTipoInstruccion, tipoInstruccion);
					
					List<Criterion> restrictions = new ArrayList<Criterion>();
					
					String descripcionTipo = Normalizer.normalize(tipoInstruccion.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					
					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipo, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoInstruccion.getArea().getIdArea()));
						
					List<TipoInstruccion> tipos = (List<TipoInstruccion>) mngrTipoInstruccion.search(restrictions);
						
					if(tipos!=null && tipos.size()>0) {
						tipos.forEach(tm -> {
							String tipoTmp = Normalizer.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if(descripcionTipo.equals(tipoTmp)) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre", null);
							}
						});
					}

					log.debug("::>> Se va a guardar la informacion");
					// Guardamos la informacion
					mngrTipoInstruccion.save(tipoInstruccion);

					return new ResponseEntity<TipoInstruccion>(tipoInstruccion, HttpStatus.CREATED);

				} else {
					// Validamos que las reglas de validacion de la entidad Tipo
					// Institucion no se esten violando con este nuevo registro
					
					List<Criterion> restrictions = new ArrayList<Criterion>();
					
					String descripcionTipo = Normalizer.normalize(tipoInstruccion.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					
					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipo, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoInstruccion.getArea().getIdArea()));
						
					List<TipoInstruccion> tipos = (List<TipoInstruccion>) mngrTipoInstruccion.search(restrictions);
						
					if(tipos!=null && tipos.size()>0) {
						tipos.forEach(tm -> {
							String tipoTmp = Normalizer.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if(descripcionTipo.equals(tipoTmp) && !tipoInstruccion.getIdInstruccion().equals(tm.getIdInstruccion())) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre", null);
							}
						});
					}
					// Actualizamos la informacion
					mngrTipoInstruccion.update(tipoInstruccion);

					log.debug("::>> Registro Actualizado");

					return new ResponseEntity<TipoInstruccion>(tipoInstruccion, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<TipoInstruccion>(tipoInstruccion, HttpStatus.BAD_REQUEST);
			}
		} catch (ConstraintViolationException e) {
			// Se lanza la excepcion para que sea capturada por el
			// GlobalExceptionController
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

	/**
	 * Obtiene el primer valor de la tabla
	 * 
	 * @param idArea
	 *            Identificador del Area
	 * @return Tipo de Instruccion del Area
	 * @throws Exception
	 */
	public TipoInstruccion getDefaultValue(Integer idArea) throws Exception {

		List<Criterion> restrictions = new ArrayList<Criterion>();
		try {

			restrictions.add(Restrictions.eq("area.idArea", idArea));
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("idInstruccion"));

			@SuppressWarnings("unchecked")
			List<TipoInstruccion> values = mngrTipoInstruccion.search(restrictions, orders, null, 1, 0);

			if (!values.isEmpty()) {
				return values.get(0);
			}

			log.error("No existe un Tipo de prioridad para el Area con identificador " + idArea);
			throw new Exception("No existe un Tipo de prioridad para el Area con identificador " + idArea);

		} catch (Exception e) {

			log.error("Error al momento de obtener el Tipo de Prioridad");
			throw e;
		}

	}

}