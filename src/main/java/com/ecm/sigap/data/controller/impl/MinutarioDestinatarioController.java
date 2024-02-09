/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Value;
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
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.MinutarioDestinatario;
import com.ecm.sigap.data.model.MinutarioDestinatarioKey;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.MinutarioDestinatario}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class MinutarioDestinatarioController extends CustomRestController
		implements RESTController<MinutarioDestinatario> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(MinutarioDestinatarioController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/minutarioDestinatario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) MinutarioDestinatario minutarioDestinatario) {

		List<?> lst = new ArrayList<>();

		log.info("Parametros de busqueda :: " + minutarioDestinatario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (minutarioDestinatario.getMinutarioDestinatarioKey().getIdArea() != null)
				restrictions.add(Restrictions.eq("minutarioDestinatarioKey.idArea",
						minutarioDestinatario.getMinutarioDestinatarioKey().getIdArea()));

			if ((minutarioDestinatario.getMinutarioDestinatarioKey().getIdDestinatario() != null)
					&& (!minutarioDestinatario.getMinutarioDestinatarioKey().getIdDestinatario().isEmpty()))
				restrictions.add(Restrictions.eq("minutarioDestinatarioKey.idDestinatario",
						minutarioDestinatario.getMinutarioDestinatarioKey().getIdDestinatario()));

			if (minutarioDestinatario.getMinutarioDestinatarioKey().getIdTipoDestinatario() != null)
				restrictions.add(Restrictions.eq("minutarioDestinatarioKey.idTipoDestinatario",
						minutarioDestinatario.getMinutarioDestinatarioKey().getIdTipoDestinatario()));

			if (minutarioDestinatario.getMinutarioDestinatarioKey().getAreaDestinatario() != null) {

				if (minutarioDestinatario.getMinutarioDestinatarioKey().getAreaDestinatario().getIdArea() != null) {
					restrictions.add(Restrictions.eq("minutarioDestinatarioKey.areaDestinatario.idArea",
							minutarioDestinatario.getMinutarioDestinatarioKey().getAreaDestinatario().getIdArea()));
				}
			}

			List<Order> orders = new ArrayList<Order>();
			// orders.add(Order.asc("minutarioDestinatarioKey.areaDestinatario.titular.paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrMinutarioDestinatario.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

			return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

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

	@ApiOperation(value = "Agregar destinatario", notes = "Agrega un destinatario a favoritos")
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
	@RequestMapping(value = "/minutarioDestinatario", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<MinutarioDestinatario> save(
			@RequestBody(required = true) MinutarioDestinatario minutarioDestinatario) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("minutarioDestinatario A GUARDAR >> " + minutarioDestinatario);

				if ((minutarioDestinatario.getMinutarioDestinatarioKey().getIdArea() != null)
						&& (minutarioDestinatario.getMinutarioDestinatarioKey().getIdDestinatario() != null)
						&& (minutarioDestinatario.getMinutarioDestinatarioKey().getIdTipoDestinatario() != null)
						&& (minutarioDestinatario.getMinutarioDestinatarioKey().getAreaDestinatario()
								.getIdArea() != null)) {
					
					Long contr = countMaxFavoritos(minutarioDestinatario.getMinutarioDestinatarioKey().getIdArea());

					if (contr > maxFavoritoMinutarioDestinatario) {
						return new ResponseEntity<MinutarioDestinatario>(minutarioDestinatario, HttpStatus.NOT_ACCEPTABLE);
//						throw new BadRequestException(
//								"Excedio el maximo permitido de favoritos, elimine alguno antes de agregar uno nuevo.");
					}				

					// Validamos que las reglas de validacion de la entidad Tipo
					// AreaPromotor no se esten violando con este nuevo registro
					validateEntity(mngrMinutarioDestinatario, minutarioDestinatario);

					// Guardamos la informacion
					mngrMinutarioDestinatario.save(minutarioDestinatario);
					return new ResponseEntity<MinutarioDestinatario>(minutarioDestinatario, HttpStatus.CREATED);

				} else {
					return new ResponseEntity<MinutarioDestinatario>(minutarioDestinatario, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<MinutarioDestinatario>(minutarioDestinatario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/** */
	@Value("${maxFavoritoMinutarioDestinatario}")
	private Integer maxFavoritoMinutarioDestinatario;

	/**
	 * Limitar la cantidad de favoritos q se pueden guardar por area.
	 * @param idArea
	 */
	private Long countMaxFavoritos(Integer idArea) {

		String sqlquery = "select count(*) as countr from MinutarioDestinatario where minutarioDestinatarioKey.idArea = "
				+ idArea;

		List<?> search = mngrMinutarioDestinatario.execQuery(sqlquery);

		Long contr = (Long) search.get(0);
		
		return contr;
	}

	/**
	 * 
	 * @param minutarioDestinatario
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar destinatario", notes = "Elimina un destinatario de favoritos")
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
	
	@RequestMapping(value = "/minutarioDestinatario", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idDestinatario", required = true) Serializable idDestinatario,
			@RequestParam(value = "idTipoDestinatario", required = true) Serializable idTipoDestinatario,
			@RequestParam(value = "idAreaDestinatario", required = true) Serializable idAreaDestinatario) {

		try {
			Area areaDestinatario = new Area();
			areaDestinatario.setIdArea(Integer.valueOf((String) idAreaDestinatario));

			MinutarioDestinatarioKey minutarioDestinatarioKey = new MinutarioDestinatarioKey();
			minutarioDestinatarioKey.setIdArea(Integer.valueOf((String) idArea));
			minutarioDestinatarioKey.setIdDestinatario(String.valueOf((String) idDestinatario));
			minutarioDestinatarioKey.setIdTipoDestinatario(Integer.valueOf((String) idTipoDestinatario));
			minutarioDestinatarioKey.setAreaDestinatario(areaDestinatario);

			if ((minutarioDestinatarioKey.getIdArea() != null)
					&& (minutarioDestinatarioKey.getIdDestinatario() != null
							&& !minutarioDestinatarioKey.getIdDestinatario().isEmpty())
					&& (minutarioDestinatarioKey.getIdTipoDestinatario() != null)
					&& (minutarioDestinatarioKey.getAreaDestinatario().getIdArea() != null)) {
				
				mngrMinutarioDestinatario.delete(mngrMinutarioDestinatario.fetch(minutarioDestinatarioKey));
			}

			log.debug("DELETE! ");

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@Override
	public ResponseEntity<MinutarioDestinatario> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Serializable id) {
		throw new UnsupportedOperationException();

	}

}
