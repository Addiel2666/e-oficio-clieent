/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.ecm.sigap.data.model.RepresentanteLegal;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.RepresentanteLegal}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class RepresentanteLegalController extends CustomRestController implements RESTController<RepresentanteLegal> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RepresentanteLegalController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene representante legal", notes = "Obtiene el detalle representante legal")
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
	@RequestMapping(value = "/representanteLegal", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<RepresentanteLegal> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		RepresentanteLegal item = null;
		try {

			item = mngrRepresentanteLegal.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Data Out >> " + item);

		return new ResponseEntity<RepresentanteLegal>(item, HttpStatus.OK);

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
	
	@ApiOperation(value = "Consulta representante legal", notes = "Consulta un representante legal")
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
	@RequestMapping(value = "/representanteLegal", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) RepresentanteLegal representanteLegal) {

		List<?> lst = new ArrayList<RepresentanteLegal>();
		log.debug("PARAMETROS DE BUSQUEDA : " + representanteLegal);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			//Se coloca para realizar consulta general asignando el id
			restrictions.add(Restrictions.isNotNull("id"));

			if (representanteLegal.getId() != null)
				restrictions.add(Restrictions.idEq(representanteLegal.getId()));

			if ((representanteLegal.getCurp() != null) && (!representanteLegal.getCurp().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("curp", representanteLegal.getCurp(), MatchMode.ANYWHERE));

			if ((representanteLegal.getEmail() != null) && (!representanteLegal.getEmail().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("email", representanteLegal.getEmail(), MatchMode.ANYWHERE));

			if ((representanteLegal.getHomonimo() != null) && (!representanteLegal.getHomonimo().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("homonimo", representanteLegal.getHomonimo(), MatchMode.ANYWHERE));

			if (representanteLegal.getEmpresa() != null && representanteLegal.getEmpresa().getId() != null)
				restrictions
						.add(Restrictions.eq("empresa", mngrEmpresa.fetch(representanteLegal.getEmpresa().getId())));

			if ((representanteLegal.getMaterno() != null) && (!representanteLegal.getMaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("materno", representanteLegal.getMaterno(), MatchMode.ANYWHERE));

			if ((representanteLegal.getPaterno() != null) && (!representanteLegal.getPaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", representanteLegal.getPaterno(), MatchMode.ANYWHERE));

			if ((representanteLegal.getNombres() != null) && (!representanteLegal.getNombres().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombres", representanteLegal.getNombres(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(representanteLegal.getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCompleto", representanteLegal.getNombreCompleto(),
						MatchMode.ANYWHERE));

			if ((representanteLegal.getRfc() != null) && (!representanteLegal.getRfc().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("rfc", representanteLegal.getRfc(), MatchMode.ANYWHERE));
			
			if (representanteLegal.getActivosn() != null)
				restrictions.add(Restrictions.eq("activosn", representanteLegal.getActivosn()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("empresa"));
			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrRepresentanteLegal.search(restrictions, orders);

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

	@ApiOperation(value = "Eliminar representante legal", notes = "Elimina de la lista a un representante legal")
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
	@RequestMapping(value = "/representanteLegal", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(Serializable id) {

		log.debug("REPRESENTANTE LEGAL A ELIMINAR >> " + id);

		try {

			mngrRepresentanteLegal.delete(mngrRepresentanteLegal.fetch(Integer.valueOf((String) id)));

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

	@ApiOperation(value = "Agregar representante legal", notes = "Agrega a la lista un nuevo representante legal")
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
	@RequestMapping(value = "/representanteLegal", method = RequestMethod.PUT)
	public ResponseEntity<RepresentanteLegal> save(@RequestBody(required = true) RepresentanteLegal repLegal) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("REPRESENTANTE LEGAL A GUARDAR >> " + repLegal);
				
				// Se comprueba si existe un repLegal con el mismo rfc y/o curp en una misma empresa
//				if(!validarRFCCURP(repLegal)) {
//					//throw new Exception("Ya existe un representante legar con el mismo RFC o CURP");
//					return new ResponseEntity<RepresentanteLegal>(repLegal, HttpStatus.CONFLICT);
//				}
				
				if (repLegal.getId() == null) {
					mngrRepresentanteLegal.save(repLegal);
					return new ResponseEntity<RepresentanteLegal>(repLegal, HttpStatus.CREATED);
				} else {
					mngrRepresentanteLegal.update(repLegal);
					return new ResponseEntity<RepresentanteLegal>(repLegal, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<RepresentanteLegal>(repLegal, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean validarRFCCURP(RepresentanteLegal representante) {
		boolean isValid = true;
		
		// Para comprobar si se intenta registrar con los campos rfc y/o curp con datos
		if(null!=representante.getRfc() || null!=representante.getCurp()) {
			
			List<RepresentanteLegal> lst = new ArrayList<RepresentanteLegal>();
			List<Criterion> restrictions = new ArrayList<Criterion>();
			
			if(null!=representante.getId()) {
				restrictions.add(Restrictions.ne("id", representante.getId()));
			}
			
			if(null!=representante.getRfc() && null!=representante.getCurp()) {
				restrictions.add(Restrictions.eq("empresa.id", representante.getEmpresa().getId()));
				restrictions.add(Restrictions.or(
						Restrictions.eq("rfc", representante.getRfc()).ignoreCase(),
						Restrictions.eq("curp", representante.getCurp()).ignoreCase()
						));
				
			} else if (null!=representante.getRfc() && null==representante.getCurp()) {
				restrictions.add(Restrictions.eq("empresa.id", representante.getEmpresa().getId()));
				restrictions.add(Restrictions.eq("rfc", representante.getRfc()).ignoreCase());
				
			} else if (null==representante.getRfc() && null!=representante.getCurp()){
				restrictions.add(Restrictions.eq("empresa.id", representante.getEmpresa().getId()));
				restrictions.add(Restrictions.eq("curp", representante.getCurp()).ignoreCase());
			}
			
			lst = (List<RepresentanteLegal>) mngrRepresentanteLegal.search(restrictions);
			if(lst!=null && lst.size() > 0) {
				isValid = false;
			}
		}
		
		return isValid;
	}

}
