/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
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
import com.ecm.sigap.data.model.DestinatarioGrupoEnvio;
import com.ecm.sigap.data.model.DestinatarioGrupoEnvioKey;
import com.ecm.sigap.data.model.GrupoEnvio;
import com.ecm.sigap.data.model.GrupoEnvioDestinatario;
import com.ecm.sigap.data.model.util.TipoDestinatario;
import com.ecm.sigap.data.model.util.TipoGrupoEnvio;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author acolina
 * @version 1.0
 *
 */
@RestController
public class GrupoEnvioController extends CustomRestController implements RESTController<GrupoEnvio> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(GrupoEnvioController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/grupoEnvio", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<GrupoEnvio> get(@RequestParam(value = "id", required = true) Serializable id) {

		GrupoEnvio item = null;
		try {

			item = mngrGrupoEnvio.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<GrupoEnvio>(item, HttpStatus.OK);
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

	@ApiOperation(value = "Elimar grupo envio", notes = "Elimina de la lista a un grupo de envio")
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
	@RequestMapping(value = "/grupoEnvio", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("GRUPOENVIO A ELIMINAR >> " + id);

		try {

			mngrGrupoEnvio.delete(mngrGrupoEnvio.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta grupo envios", notes = "Consulta la lista de grupo de envios")
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
	@RequestMapping(value = "/grupoEnvio", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) GrupoEnvio grupoEnvio) {
		List<?> lst = new ArrayList<GrupoEnvio>();
		log.debug("PARAMETROS DE BUSQUEDA : " + grupoEnvio);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (grupoEnvio.getId() != null)
				restrictions.add(Restrictions.idEq(grupoEnvio.getId()));

			if ((grupoEnvio.getDescripcion() != null) && (!grupoEnvio.getDescripcion().isEmpty()))
				restrictions.add(
						EscapedLikeRestrictions.ilike("descripcion", grupoEnvio.getDescripcion(), MatchMode.ANYWHERE));

			if (grupoEnvio.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", grupoEnvio.getIdArea()));
			
			// Se agrega para que muestre los gpos privados unicamente al usuario que los creo, 
			// gpos publicos se muestran a todos los del usuarios del area.
			restrictions.add(Restrictions.or(Restrictions.eq("tipo", TipoGrupoEnvio.PUBLICO),
					Restrictions.and(Restrictions.eq("tipo", TipoGrupoEnvio.PRIVADO),
							Restrictions.eq("idUsuario", grupoEnvio.getIdUsuario()))));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrGrupoEnvio.search(restrictions);

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

	@ApiOperation(value = "Crear grupo envio", notes = "Crea un nuevo grupo de envio")
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
	@RequestMapping(value = "/grupoEnvio", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<GrupoEnvio> save(@RequestBody(required = true) GrupoEnvio grupoEnvio)
			throws Exception {
		try {

			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID).toString());
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			if (!esSoloLectura(userId)) {
				log.debug("GRUPOENVIO A GUARDAR >> " + grupoEnvio);
				
				grupoEnvio.setIdUsuario(TipoGrupoEnvio.PRIVADO.getValue().equals(grupoEnvio.getTipo().getValue()) ? userId : null);
				
				if (grupoEnvio.getId() == null) {

					grupoEnvio.setIdArea(areaId);
					
					if(validarDescripcionGpo(grupoEnvio))
						return new ResponseEntity<GrupoEnvio>(grupoEnvio, HttpStatus.CONFLICT);
					
					mngrGrupoEnvio.save(grupoEnvio);
					return new ResponseEntity<GrupoEnvio>(grupoEnvio, HttpStatus.CREATED);
				} else {
					
					if(validarDescripcionGpo(grupoEnvio))
						return new ResponseEntity<GrupoEnvio>(grupoEnvio, HttpStatus.CONFLICT);
					
					mngrGrupoEnvio.update(grupoEnvio);
					return new ResponseEntity<GrupoEnvio>(grupoEnvio, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<GrupoEnvio>(grupoEnvio, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}


	@SuppressWarnings("unchecked")
	private boolean validarDescripcionGpo(GrupoEnvio grupoEnvio){
		List<Criterion> restrictions = new ArrayList<Criterion>();
		if (grupoEnvio.getId() != null)
			restrictions.add(Restrictions.ne("id", grupoEnvio.getId()));
		restrictions.add(EscapedLikeRestrictions.ilike("descripcion", grupoEnvio.getDescripcion(), MatchMode.EXACT));
		restrictions.add(Restrictions.eq("idArea", grupoEnvio.getIdArea()));
		List<GrupoEnvio> lst = (List<GrupoEnvio>) mngrGrupoEnvio.search(restrictions);
		if (!lst.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Agregar un destinatario al grupo.
	 * 
	 * @param idGrupo
	 * @param idDestinatario
	 * @param idTipoDestinatario
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Agregar destinatario grupo", notes = "Agrega un destinatario a un grupo de envio")
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
	
	@RequestMapping(value = "/grupoEnvio/addDestinatario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<GrupoEnvioDestinatario>> addDestinatario(
			@RequestParam(required = true, value = "idGrupo") Integer idGrupo,
			@RequestParam(required = true, value = "idDestinatario") String idDestinatario,
			@RequestParam(required = true, value = "idAreaDestinatario") Integer idAreaDestinatario,
			@RequestParam(required = true, value = "idTipoDestinatario") Integer tipoDestinatario) throws Exception {
		try {

			GrupoEnvio grupo = mngrGrupoEnvio.fetch(idGrupo);

			if (grupo == null)
				return new ResponseEntity<List<GrupoEnvioDestinatario>>(new ArrayList<GrupoEnvioDestinatario>(),
						HttpStatus.BAD_REQUEST);

			DestinatarioGrupoEnvio e = new DestinatarioGrupoEnvio();
			e.setDestinatarioGrupoEnvioKey(new DestinatarioGrupoEnvioKey());
			e.getDestinatarioGrupoEnvioKey().setIdGrupo(idGrupo);
			e.getDestinatarioGrupoEnvioKey().setIdDestinatario(idDestinatario);
			e.getDestinatarioGrupoEnvioKey().setIdArea(idAreaDestinatario);
			e.getDestinatarioGrupoEnvioKey().setTipoDestinatario(TipoDestinatario.fromVal(tipoDestinatario));

			for (GrupoEnvioDestinatario dest : grupo.getDestinatarios()) {

				if (e.getDestinatarioGrupoEnvioKey().getTipoDestinatario().equals(dest.getTipoDestinatario()) //
						&& e.getDestinatarioGrupoEnvioKey().getIdArea().equals(dest.getIdArea())
						&& e.getDestinatarioGrupoEnvioKey().getIdDestinatario().equals(dest.getIdDestinatario())) {

					throw new ConstraintViolationException(errorMessages.getString("destinatarioYaAgregado"),
							new HashSet<ConstraintViolation<Serializable>>());

				}

			}

			mngrDestinatarioGrupoEnvio.save(e);

			grupo = mngrGrupoEnvio.fetch(idGrupo);

			return new ResponseEntity<List<GrupoEnvioDestinatario>>(grupo.getDestinatarios(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
