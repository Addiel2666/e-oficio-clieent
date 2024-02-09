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
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Representante;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

// TODO: Auto-generated Javadoc
/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Representante}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class RepresentanteController extends CustomRestController implements RESTController<Representante> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RepresentanteController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene representante", notes = "Obtiene el representante de tramite, respuesta")
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
	@RequestMapping(value = "/representante", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Representante> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		Representante item = null;
		try {

			item = mngrRepresentante.fetch((String) id);

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Data Out >> " + item);
		return new ResponseEntity<Representante>(item, HttpStatus.OK);
	}

	/**
	 * Search.
	 *
	 * @param representante
	 *            the representante
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta representante", notes = "Consulta el detalle de un representante")
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
	@RequestMapping(value = "/representante", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Representante representante) {

		List<?> lst = new ArrayList<Representante>();
		log.debug("PARAMETROS DE BUSQUEDA : " + representante);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			// ProjectionList projections = Projections.projectionList();

			if ((representante.getArea() != null) && (representante.getArea().getIdArea()) != null)
				restrictions.add(Restrictions.eq("area.idArea", representante.getArea().getIdArea()));

			if (representante.getId() != null)
				restrictions.add(Restrictions.idEq(representante.getId()));

			if ((representante.getMaterno() != null) && (!representante.getMaterno().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("materno", representante.getMaterno(), MatchMode.ANYWHERE));

			if ((representante.getPaterno() != null) && (!representante.getPaterno().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("paterno", representante.getPaterno(), MatchMode.ANYWHERE));

			if ((representante.getNombres() != null) && (!representante.getNombres().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("nombres", representante.getNombres(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(representante.getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCompleto", representante.getNombreCompleto(),
						MatchMode.ANYWHERE));
			
			if(representante.getUsuario() != null && StringUtils.isNotBlank(representante.getUsuario().getIdUsuario()))
				restrictions.add(EscapedLikeRestrictions.ilike("usuario.idUsuario", representante.getUsuario().getIdUsuario(), MatchMode.ANYWHERE));

			if (representante.getArea() != null) {
				if (representante.getArea().getActivo() != null)
					restrictions.add(Restrictions.eq("area.activo", representante.getArea().getActivo()));

				if (representante.getArea().getInstitucion() != null) {
					// projections.add(Projections.groupProperty("area.institucion").as("institucion"));
					if (StringUtils.isNotBlank(representante.getArea().getInstitucion().getTipo()))
						restrictions.add(Restrictions.ilike("institucion.tipo",
								representante.getArea().getInstitucion().getTipo()));

					if (representante.getArea().getInstitucion().getIdInstitucion() != null)
						restrictions.add(Restrictions.eq("institucion.idInstitucion",
								representante.getArea().getInstitucion().getIdInstitucion()));

					if (representante.getArea().getInstitucion().getActivo() != null)
						restrictions.add(Restrictions.eq("institucion.activo",
								representante.getArea().getInstitucion().getActivo()));
				}
			}

			if ((representante.getIdTipo() != null) && (!representante.getIdTipo().isEmpty()))
				restrictions.add(Restrictions.eq("idTipo", representante.getIdTipo()));

			if ((representante.getActivosn() != null))
				restrictions.add(Restrictions.or(Restrictions.eq("activosn", representante.getActivosn()),
						Restrictions.isNull("activosn")));
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrRepresentante.search(restrictions, orders, null, null, null);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Representante titular.
	 *
	 * @param representante
	 *            the representante
	 * @return the response entity
	 */
	@RequestMapping(value = "/titular", method = RequestMethod.POST)
	public ResponseEntity<List<?>> representanteTitular(@RequestBody(required = true) Representante representante) {

		List<?> lst = new ArrayList<Representante>();
		log.debug("PARAMETROS DE BUSQUEDA : " + representante);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			// ProjectionList projections = Projections.projectionList();

			if ((representante.getArea() != null) && (representante.getArea().getIdArea()) != null)
				restrictions.add(Restrictions.eq("area.idArea", representante.getArea().getIdArea()));

			if (representante.getId() != null)
				restrictions.add(Restrictions.idEq(representante.getId()));

			if ((representante.getMaterno() != null) && (!representante.getMaterno().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("materno", representante.getMaterno(), MatchMode.ANYWHERE));

			if ((representante.getPaterno() != null) && (!representante.getPaterno().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("paterno", representante.getPaterno(), MatchMode.ANYWHERE));

			if ((representante.getNombres() != null) && (!representante.getNombres().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("nombres", representante.getNombres(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(representante.getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCompleto", representante.getNombreCompleto(),
						MatchMode.ANYWHERE));

			if (representante.getArea() != null) {
				if (representante.getArea().getActivo() != null)
					restrictions.add(Restrictions.eq("area.activo", representante.getArea().getActivo()));

				if (representante.getArea().getInstitucion() != null) {
					// projections.add(Projections.groupProperty("area.institucion").as("institucion"));
					if (StringUtils.isNotBlank(representante.getArea().getInstitucion().getTipo()))
						restrictions.add(Restrictions.ilike("institucion.tipo",
								representante.getArea().getInstitucion().getTipo()));

					if (representante.getArea().getInstitucion().getIdInstitucion() != null)
						restrictions.add(Restrictions.eq("institucion.idInstitucion",
								representante.getArea().getInstitucion().getIdInstitucion()));

					if (representante.getArea().getInstitucion().getActivo() != null)
						restrictions.add(Restrictions.eq("institucion.activo",
								representante.getArea().getInstitucion().getActivo()));
				}
			}

			if ((representante.getIdTipo() != null) && (!representante.getIdTipo().isEmpty()))
				restrictions.add(Restrictions.eq("idTipo", representante.getIdTipo()));

			if ((representante.getActivosn() != null))
				restrictions.add(Restrictions.or(Restrictions.eq("activosn", representante.getActivosn()),
						Restrictions.isNull("activosn")));

			{
				DetachedCriteria cr = DetachedCriteria.forClass(Area.class, "a");

				cr.setProjection(Projections.property("idArea"));

				cr.add(Restrictions.eqProperty("a.titular.id", "representante.id"));

				restrictions.add(Subqueries.exists(cr));
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrRepresentante.search(restrictions, orders, null, null, null);

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

	@ApiOperation(value = "Eliminar representante", notes = "Elimina un representante de la lista")
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
	@RequestMapping(value = "/representante", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(Serializable id) {

		log.debug("REPRESENTANTE A ELIMINAR >> " + id);

		try {

			mngrRepresentante.delete(mngrRepresentante.fetch((String) id));

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

	@ApiOperation(value = "Agregar representante", notes = "Agrega un nuevo representante a la lista")
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
	@RequestMapping(value = "/representante", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Representante> save(@RequestBody(required = true) Representante repre)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("REPRESENTANTE A GUARDAR >> " + repre);

				if (repre.getId() == null) {
					mngrRepresentante.save(repre);
					return new ResponseEntity<Representante>(repre, HttpStatus.CREATED);
				} else {
					mngrRepresentante.update(repre);
					return new ResponseEntity<Representante>(repre, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<Representante>(repre, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

}
