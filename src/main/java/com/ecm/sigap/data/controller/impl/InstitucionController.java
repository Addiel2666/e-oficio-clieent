/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Institucion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Institucion}
 *
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class InstitucionController extends CustomRestController implements RESTController<Institucion> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(InstitucionController.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/institucion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Institucion> get(@RequestParam(value = "id", required = true) Serializable id) {

		Institucion item = null;
		try {

			item = mngrInstitucion.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
		}

		log.debug(" Data Out >> " + item);

		return new ResponseEntity<Institucion>(item, HttpStatus.OK);
	}

	/**
	 * Obtener inst param app.
	 *
	 * @param tipo the tipo
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene parametros instituciones", notes = "Obtiene parametros de instituciones")
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
	
	@RequestMapping(value = "/obtenerInstParamApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Institucion> obtenerInstParamApp(
			@RequestParam(value = "tipo", required = true) Serializable tipo) {

		Institucion item = null;
		Integer idInstitucionParamApp = null;

		if ("empresa".equals(tipo.toString())) {

			idInstitucionParamApp = Integer.parseInt(getParamApp("SIGAP", "IDEMPPROMOTOR"));

		} else if ("ciudadano".equals(tipo.toString()))
			idInstitucionParamApp = Integer.parseInt(getParamApp("SIGAP", "IDCIUDPROMOTOR"));
		try {

			item = mngrInstitucion.fetch(idInstitucionParamApp);

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
		}

		log.debug(" Data Out >> " + item);

		return new ResponseEntity<Institucion>(item, HttpStatus.OK);
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

	@ApiOperation(value = "Eliminar institucion", notes = "Se elimina una institucion de la lista")
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
	@RequestMapping(value = "/institucion", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public @ResponseBody void delete(@RequestParam(value = "id", required = true) Serializable id) {

		Institucion item = null;
		try {

			item = mngrInstitucion.fetch(id);

			if (item != null)
				mngrInstitucion.delete(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/institucion", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Institucion> save(@RequestBody Institucion institucion) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("INSTITUCION A GUARDAR >> " + institucion);

				if (institucion.getIdInstitucion() == null) {

					// Validamos que las reglas de validacion de la entidad
					// Tipo Institucion no se esten violando con este nuevo
					// registro
					validateEntity(mngrInstitucion, institucion);
					if (!validarNombre(institucion)) {
						throw new ConstraintViolationException("{Unique.descripcion}", null);
					}
					if (!validarNomAbreCla(institucion)) {
						throw new ConstraintViolationException("Abreviatura o clave, ya existe", null);
					}
					mngrInstitucion.save(institucion);

					return new ResponseEntity<Institucion>(institucion, HttpStatus.CREATED);
				} else {
					// validar que no modifiquen el tipo de institucion para evitar errores de areas
					// en el repo
					Institucion oldInst = mngrInstitucion.fetch(institucion.getIdInstitucion());
					if (!institucion.getTipo().equals(oldInst.getTipo())) {
						throw new BadRequestException("No se permite cambiar el tipo de institucion");
					}
					
					if (!validarNombre(institucion)) {
						throw new ConstraintViolationException("{Unique.descripcion}", null);
					}

					if (!validarNomAbreCla(institucion)) {
						throw new ConstraintViolationException("Ya existe un registro con el mismo nombre, abreviatura o clave.", null);
					}
					
					if(institucion.getActivo() != oldInst.getActivo())
						institucion.setActiveInactive(institucion.getActivo().toString());
					else
						institucion.setActiveInactive(null);

					mngrInstitucion.update(institucion);

					return new ResponseEntity<Institucion>(institucion, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<Institucion>(institucion, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	@Autowired
	private AreaController areaController;
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear institucion", notes = "Crea una nueva institucion")
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

	@RequestMapping(value = "/institucion/crear", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Institucion> crearInstitucion(@RequestBody BodyCrearInstitucion body)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				Area areaNueva = body.getArea();
				Institucion institucionNueva = body.getInstitucion();

				ResponseEntity<Institucion> responseCrearInstitucion = save(institucionNueva);

				if (responseCrearInstitucion.getStatusCode() != HttpStatus.CREATED)
					return responseCrearInstitucion;

				try {

					areaNueva.setInstitucion(institucionNueva);

					ResponseEntity<Area> responseCrearArea = areaController.save(areaNueva);

					if (responseCrearArea.getStatusCode() != HttpStatus.OK
							&& responseCrearArea.getStatusCode() != HttpStatus.CREATED)
						throw new InternalServerErrorException();

				} catch (Exception e) {
					delete(institucionNueva.getIdInstitucion());

					Set<ConstraintViolation<Serializable>> violations = new HashSet<ConstraintViolation<Serializable>>();

					throw new ConstraintViolationException(e.getMessage(), violations);

				}

				return new ResponseEntity<Institucion>(institucionNueva, HttpStatus.OK);

			} else {
				return new ResponseEntity<Institucion>(new Institucion(), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Se crea un sequence para el folio institucional,
	 * 
	 * @param idInstitucion
	 */
	private void crearSequenceIntitucion(Integer idInstitucion) {

		String sql = "CREATE SEQUENCE SEQFOLIOINSTITUCIONAL_" + idInstitucion
				+ " START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE";

		// TODO ver como ejecutar este query... porq asi da error...
		mngrArea.execUpdateQuery(sql, null);

	}

	public boolean validarNomAbreCla(Institucion institucion) {
		boolean isValid = true;

		List<Criterion> restrictions = new ArrayList<Criterion>();
		if (null != institucion.getIdInstitucion())
			restrictions.add(Restrictions.ne("idInstitucion", institucion.getIdInstitucion()));//throw new ConstraintViolationException("Abreviatura o clave, ya existe", null);

		restrictions.add(Restrictions.or(EscapedLikeRestrictions.ilike("descripcion", institucion.getDescripcion().toLowerCase(), MatchMode.EXACT),
										EscapedLikeRestrictions.ilike("abreviatura", institucion.getAbreviatura().toLowerCase(), MatchMode.EXACT),
										EscapedLikeRestrictions.ilike("clave", institucion.getClave().toLowerCase(), MatchMode.EXACT)));

		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("descripcion"));

		List<?> lst = new ArrayList<Institucion>();
		lst = mngrInstitucion.search(restrictions, orders);

		if (!lst.isEmpty()) {
			isValid = false;
		}

		return isValid;
	}

	public boolean validarNombre(Institucion institucion) {
		boolean isValid = true;

		List<Criterion> restrictions = new ArrayList<Criterion>();
		if (null != institucion.getIdInstitucion())
			restrictions.add(Restrictions.ne("idInstitucion", institucion.getIdInstitucion()));

		restrictions.add(EscapedLikeRestrictions.ilike("descripcion", institucion.getDescripcion().toLowerCase(), MatchMode.EXACT));

		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("descripcion"));

		List<?> lst = new ArrayList<Institucion>();
		lst = mngrInstitucion.search(restrictions, orders);

		if (!lst.isEmpty()) {
			isValid = false;
		}

		return isValid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta institucion", notes = "Consulta la lista de instituciones")
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
	@RequestMapping(value = "/institucion", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Institucion institucion) {

		List<Institucion> lst = new ArrayList<Institucion>();
		log.debug("PARAMETROS DE BUSQUEDA :: " + institucion);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			
			//Se coloca para realizar consulta general asignando el id
			restrictions.add(Restrictions.isNotNull("idInstitucion"));
			
			if (institucion.getIdInstitucion() != null)
				restrictions.add(Restrictions.idEq(institucion.getIdInstitucion()));

			if (institucion.getDescripcion() != null)
				restrictions.add(
						EscapedLikeRestrictions.ilike("descripcion", institucion.getDescripcion(), MatchMode.ANYWHERE));

			if (institucion.getClave() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("clave", institucion.getClave(), MatchMode.ANYWHERE));

			if (institucion.getAbreviatura() != null)
				restrictions.add(
						EscapedLikeRestrictions.ilike("abreviatura", institucion.getAbreviatura(), MatchMode.ANYWHERE));

			if (institucion.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", institucion.getActivo()));

			if (institucion.getEndpoint() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("endpoint", institucion.getEndpoint(), MatchMode.ANYWHERE));

			if (institucion.getUri() != null)
				restrictions.add(Restrictions.ilike("uri", institucion.getUri()));

			if (institucion.getInteropera() != null)
				restrictions.add(Restrictions.eq("interopera", institucion.getInteropera()));

			if (institucion.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", institucion.getTipo()));
			
			// Para no incluir a EMPRESA
			if (institucion.isNotCiudadano())
				restrictions.add(Restrictions.not(Restrictions.idEq(Integer.parseInt(getParamApp("SIGAP", "IDCIUDPROMOTOR")))));

			// Para no incluir a CIUDADANO
			if(institucion.isNotEmpresa())
				restrictions.add(Restrictions.not(Restrictions.idEq(Integer.parseInt(getParamApp("SIGAP", "IDEMPPROMOTOR")))));

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<Institucion>) mngrInstitucion.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<Institucion>() {
				@Override
				public int compare(Institucion i1, Institucion i2) {
					return i1.getDescripcion().compareTo(i2.getDescripcion());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * *

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 *
	 * Obtiene la direccion general de una institucion indicada por id.
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/institucion/areaPrincipal", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Area> getAreaPrincipal(
			@RequestParam(value = "id", required = true) Serializable id) {

		Area item = null;
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			Institucion inst = mngrInstitucion.fetch(Integer.valueOf((String) id));

			restrictions.add(Restrictions.eq("institucion", inst));
			restrictions.add(Restrictions.isNull("idAreaPadre"));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<?> items = mngrArea.search(restrictions);

			if (!items.isEmpty())
				item = (Area) items.get(0);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Area>(item, HttpStatus.OK);
	}

}
