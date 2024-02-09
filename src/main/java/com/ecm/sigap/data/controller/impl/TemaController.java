/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.List;

import javax.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.io.Serializable;
import java.text.Normalizer;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
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

import com.ecm.sigap.data.model.Tema;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Tema}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class TemaController extends CustomRestController implements RESTController<Tema> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TemaController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene tema", notes = "Obtiene el detalle de un tema de la seccion asunto")
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
	@RequestMapping(value = "/tema", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Tema> get(@RequestParam(value = "id", required = true) Serializable id) {

		Tema item = null;
		try {

			item = mngrTema.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Tema>(item, HttpStatus.OK);

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
	
	@ApiOperation(value = "Consulta tema", notes = "Consulta la lista de temas de la seccion asunto")
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
	@RequestMapping(value = "/tema", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Tema tema) {

		List<?> lst = new ArrayList<Tema>();
		log.info("Parametros de busqueda :: " + tema);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tema.getIdTema() != null)
				restrictions.add(Restrictions.idEq(tema.getIdTema()));

			if (tema.getDescripcion() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("descripcion", tema.getDescripcion(), MatchMode.ANYWHERE));

			if (tema.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", tema.getActivo()));

			if (tema.getArea() != null && tema.getArea().getIdArea() != null)
				restrictions.add(Restrictions.eq("area.idArea", tema.getArea().getIdArea()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrTema.search(restrictions, orders);

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

	@ApiOperation(value = "Eliminar tema", notes = "Elimina un tema de la lista de la seccion asunto")
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
	@RequestMapping(value = "/tema", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("TEMA A ELIMINAR >> " + id);

		try {
			mngrTema.delete(mngrTema.fetch(Integer.valueOf((String) id)));

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

	@ApiOperation(value = "Agregar tema", notes = "Agrega o edita un tema de la seccion asunto")
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
	@RequestMapping(value = "/tema", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Tema> save(@RequestBody(required = true) Tema tema) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("TEMA A GUARDAR >> " + tema);

				if (tema.getDescripcion() != null //
						&& !tema.getDescripcion().isEmpty() //
						&& tema.getArea() != null //
						&& tema.getArea().getIdArea() != null) {

					if (tema.getIdTema() == null) {

						tema.setArea(mngrArea.fetch(tema.getArea().getIdArea()));
						
						// Validamos que las reglas de validacion de la entidad
						// Tipo Tema no se esten violando con este nuevo
						// registro
						//validateEntity(mngrTema, tema);
						
						List<Criterion> restrictions = new ArrayList<Criterion>();
						
						String descripcionTema = Normalizer.normalize(tema.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
						
						restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTema, MatchMode.ANYWHERE));
						restrictions.add(Restrictions.eq("area.idArea", tema.getArea().getIdArea()));
							
						List<Tema> temas = (List<Tema>) mngrTema.search(restrictions);
							
						if(temas!=null && temas.size()>0) {
							temas.forEach(tm -> {
								String temaTemp = Normalizer.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
								if(descripcionTema.equals(temaTemp)) {
									throw new ConstraintViolationException("Ya existe un registro con el mismo nombre", null);
								}
							});
						}

						mngrTema.save(tema);
						return new ResponseEntity<Tema>(tema, HttpStatus.CREATED);

					} else {
						
						// Validamos que las reglas de validacion de la entidad
						// Tipo Tema no se esten violando con este nuevo
						// registro						
						List<Criterion> restrictions = new ArrayList<Criterion>();
						
						String descripcionTema = Normalizer.normalize(tema.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
						
						restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTema, MatchMode.ANYWHERE));
						restrictions.add(Restrictions.eq("area.idArea", tema.getArea().getIdArea()));
							
						List<Tema> temas = (List<Tema>) mngrTema.search(restrictions);
							
						if(temas!=null && temas.size()>0) {
							temas.forEach(tm -> {
								String temaTmp = Normalizer.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
								if(descripcionTema.equals(temaTmp) && !tema.getIdTema().equals(tm.getIdTema())) {
									throw new ConstraintViolationException("Ya existe un registro con el mismo nombre", null);
								}
							});
						}
						
						// Actualizamos la informacion
						mngrTema.update(tema);
						return new ResponseEntity<Tema>(tema, HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<Tema>(tema, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Tema>(tema, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

}