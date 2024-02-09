/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.TipoDocumento;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.TipoDocumento}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class TipoDocumentoController extends CustomRestController implements RESTController<TipoDocumento> {
	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TipoDocumentoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene tipo documento", notes = "Obtiene el detalle de tipo de documento de la seccion documentos")
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
	@RequestMapping(value = "/tipoDocumento", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TipoDocumento> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		TipoDocumento item = null;
		try {

			item = mngrTipoDocumento.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<TipoDocumento>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	
	 /*
	 * Documentacion con swagger
	 */
		
	@ApiOperation(value = "Consulta tipo documento", notes = "Consulta la lista de tipo de documento de la seccion documentos")
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
	@RequestMapping(value = "/tipoDocumento", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) TipoDocumento tipoDocumento) {

		List<?> lst = new ArrayList<TipoDocumento>();
		log.info("Parametros de busqueda :: " + tipoDocumento);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tipoDocumento.getIdTipoDocumento() != null)
				restrictions.add(Restrictions.idEq(tipoDocumento.getIdTipoDocumento()));

			if (tipoDocumento.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", tipoDocumento.getDescripcion(),
						MatchMode.ANYWHERE));

			if (tipoDocumento.getArea() != null) {
				if (tipoDocumento.getArea().getIdArea() != null)
					restrictions.add(Restrictions.eq("area.idArea", tipoDocumento.getArea().getIdArea()));
			}
			if (tipoDocumento.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", tipoDocumento.getActivo()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrTipoDocumento.search(restrictions, orders);

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
	
	@ApiOperation(value = "Agregar tipo documento", notes = "Agrega o edita un tipo de documento en la seccion de documentos")
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
	@RequestMapping(value = "/tipoDocumento", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<TipoDocumento> save(@RequestBody(required = true) TipoDocumento tipoDocumento)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> TIPO DE DOCUMENTO A GUARDAR O ACTUALIZAR >> " + tipoDocumento);

				if (tipoDocumento.getIdTipoDocumento() == null) {
					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoDocumento no se esten violando con este nuevo
					// registro
					// validateEntity(mngrTipoDocumento, tipoDocumento);

					List<Criterion> restrictions = new ArrayList<Criterion>();

					String descripcionTipo = Normalizer
							.normalize(tipoDocumento.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
							.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipo, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoDocumento.getArea().getIdArea()));

					List<TipoDocumento> tipos = (List<TipoDocumento>) mngrTipoDocumento.search(restrictions);

					if (tipos != null && tipos.size() > 0) {
						tipos.forEach(tm -> {
							String tipoTmp = Normalizer
									.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
									.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if (descripcionTipo.equals(tipoTmp)) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre",
										null);
							}
						});
					}

					// Guardamos la informacion
					mngrTipoDocumento.save(tipoDocumento);

					return new ResponseEntity<TipoDocumento>(tipoDocumento, HttpStatus.CREATED);
				} else {
					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoDocumento no se esten violando con este nuevo
					// registro
					// validateEntity(mngrTipoDocumento, tipoDocumento);

					List<Criterion> restrictions = new ArrayList<Criterion>();

					String descripcionTipo = Normalizer
							.normalize(tipoDocumento.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
							.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipo, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoDocumento.getArea().getIdArea()));

					List<TipoDocumento> tipos = (List<TipoDocumento>) mngrTipoDocumento.search(restrictions);

					if (tipos != null && tipos.size() > 0) {
						tipos.forEach(tm -> {
							String tipoTmp = Normalizer
									.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
									.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if (descripcionTipo.equals(tipoTmp)
									&& !tipoDocumento.getIdTipoDocumento().equals(tm.getIdTipoDocumento())) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre",
										null);
							}
						});
					}

					// Actualizamos la informacion
					mngrTipoDocumento.update(tipoDocumento);

					log.debug("::>> Registro Actualizado");
					return new ResponseEntity<TipoDocumento>(tipoDocumento, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<TipoDocumento>(tipoDocumento, HttpStatus.BAD_REQUEST);
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
		// TODO Auto-generated method stub

	}

}
