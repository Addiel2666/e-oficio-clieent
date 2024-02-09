/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.ecm.sigap.data.model.AreaAuxiliar;
import com.ecm.sigap.data.model.FavoritoFirmante;
import com.ecm.sigap.data.model.FavoritoFirmanteKey;
import com.ecm.sigap.data.model.Representante;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FavoritoFirmante}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class FavoritoFirmanteController extends CustomRestController implements RESTController<FavoritoFirmante> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FavoritoFirmanteController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/favoritoFirmante", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) FavoritoFirmante favoritoFirmante) {

		List<?> lst = new ArrayList<FavoritoFirmante>();

		log.info("Parametros de busqueda :: " + favoritoFirmante);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoFirmante.getFavoritoFirmanteKey() != null) {

				if (favoritoFirmante.getFavoritoFirmanteKey().getIdArea() != null)
					restrictions.add(Restrictions.eq("favoritoFirmanteKey.idArea",
							favoritoFirmante.getFavoritoFirmanteKey().getIdArea()));

				if (favoritoFirmante.getFavoritoFirmanteKey().getFirmante() != null) {
					if (favoritoFirmante.getFavoritoFirmanteKey().getFirmante().getId() != null)
						restrictions.add(Restrictions.eq("favoritoFirmanteKey.firmante.id",
								favoritoFirmante.getFavoritoFirmanteKey().getFirmante().getId()));
				}
				if (favoritoFirmante.getFavoritoFirmanteKey().getFirmArea() != null) {
					if (favoritoFirmante.getFavoritoFirmanteKey().getFirmArea().getIdArea() != null)
						restrictions.add(Restrictions.eq("favoritoFirmanteKey.firmArea.idArea",
								favoritoFirmante.getFavoritoFirmanteKey().getFirmArea().getIdArea()));
				}
			}

			List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.asc("favoritoFirmanteKey.idArea"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFavoritoFirmante.search(restrictions, orders);

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

	@ApiOperation(value = "Agregar firmante favorito", notes = "Agregar a un firmante a la lista favoritos de borradores")
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
	@RequestMapping(value = "/favoritoFirmante", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<FavoritoFirmante> save(
			@RequestBody(required = true) FavoritoFirmante favoritoFirmante) throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			if (!esSoloLectura(userId)) {

				log.debug("FAVORITO A GUARDAR >> " + favoritoFirmante);

				if (favoritoFirmante.getFavoritoFirmanteKey() != null //
						&& (favoritoFirmante.getFavoritoFirmanteKey().getIdArea() != null)
						&& (favoritoFirmante.getFavoritoFirmanteKey().getFirmante().getId() != null)
						&& (favoritoFirmante.getFavoritoFirmanteKey().getFirmArea().getIdArea() != null)) {

					countMaxFavoritos(favoritoFirmante.getFavoritoFirmanteKey().getIdArea());

					// Validamos que las reglas de validacion de la entidad Tipo
					// FavoritoFirmante no se esten violando con este nuevo
					// registro
					validateEntity(mngrFavoritoFirmante, favoritoFirmante);

					// Guardamos la informacion
					mngrFavoritoFirmante.save(favoritoFirmante);

					return new ResponseEntity<FavoritoFirmante>(favoritoFirmante, HttpStatus.OK);

				} else {
					throw new BadRequestException();
				}
			} else {
				throw new BadRequestException();
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/** */
	@Value("${maxFavoritoFirmante}")
	private Integer maxFavoritoFirmante;

	/**
	 * Limitar la cantidad de favoritos q se pueden guardar por area.
	 * @param idArea
	 */
	private void countMaxFavoritos(Integer idArea) throws BadRequestException {

		String sqlquery = "select count(*) as countr from FavoritoFirmante where favoritoFirmanteKey.idArea = "
				+ idArea;

		List<?> search = mngrAreaRemitente.execQuery(sqlquery);

		Long contr = (Long) search.get(0);

		if (contr > maxFavoritoFirmante) {

			throw new BadRequestException(
					"Excedio el maximo permitido de favoritos, elimine alguno antes de agregar uno nuevo.");
		}

	}

	/**
	 * Delete.
	 *
	 * @param favoritoFirmante the favorito firmante
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar firmante favorito", notes = "Elimina de la lista a un firmante favorito de borradores")
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
	
	@RequestMapping(value = "/favoritoFirmante", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idFirmante", required = true) Serializable idFirmante,
			@RequestParam(value = "idAreaFirmante", required = true) Serializable idAreaFirmante) {

		try {
			Representante firmante = new Representante();
			firmante.setId(String.valueOf((String) idFirmante));
			AreaAuxiliar areaFirmante = new AreaAuxiliar();
			areaFirmante.setIdArea(Integer.valueOf((String) idAreaFirmante));
			FavoritoFirmanteKey favoritoFirmanteKey = new FavoritoFirmanteKey();
			favoritoFirmanteKey.setIdArea(Integer.valueOf((String) idArea));
			favoritoFirmanteKey.setFirmante(firmante);
			favoritoFirmanteKey.setFirmArea(areaFirmante);

			log.debug("FAVORITO A ELIMINAR >> " + favoritoFirmanteKey);

			if ((favoritoFirmanteKey.getIdArea() != null) && (favoritoFirmanteKey.getFirmante().getId() != null)
					&& (favoritoFirmanteKey.getFirmArea().getIdArea() != null)) {

				mngrFavoritoFirmante.delete(mngrFavoritoFirmante.fetch(favoritoFirmanteKey));
			}
			log.debug("DELETE! ");

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Gets the list firmante.
	 *
	 * @param representante the representante
	 * @return the list firmante
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta firmantes", notes = "Consulta la lista de firmantes de borrador")
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
	@RequestMapping(value = "/getLisfirmantes", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<FavoritoFirmante>> getListFirmante(
			@RequestBody(required = true) Representante representante) {

		List<Representante> lstRepresentante = new ArrayList<Representante>();
		List<FavoritoFirmante> lstFavoritoFirmante = new ArrayList<FavoritoFirmante>();
		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

		log.debug("PARAMETROS DE BUSQUEDA : " + representante);

		try {

			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			// Lista de Firmantes por Area
			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<Criterion> restrictionsFavorito = new ArrayList<Criterion>();

			if (representante.getArea() != null) {
				if (representante.getArea().getIdArea() != null) {

					// representante.setArea(mngrArea.fetch(representante.getArea().getIdArea()));

					restrictions.add(Restrictions.eq("area.idArea", representante.getArea().getIdArea()));

					restrictionsFavorito
							.add(Restrictions.eq("favoritoFirmanteKey.idArea", representante.getArea().getIdArea()));

				}
			}

			if ((representante.getIdTipo() != null) && (!representante.getIdTipo().isEmpty()))
				restrictions.add(Restrictions.eq("idTipo", representante.getIdTipo()));

			if ((representante.getActivosn() != null))
				restrictions.add(Restrictions.eq("activosn", representante.getActivosn()));

			if ((representante.getId() != null))
				restrictions.add(Restrictions.idEq(representante.getId()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			lstRepresentante = (List<Representante>) mngrRepresentante.search(restrictions, orders);
			boolean existUser = false;
			for (Representante rep : lstRepresentante) {
				if (rep.getId().equalsIgnoreCase(userId)) {
					existUser = true;
					break;
				}
			}

			if (!existUser) {
				Representante representanteTemp = mngrRepresentante.fetch(userId);
				lstRepresentante.add(0, representanteTemp);
			}

			// * * * * * * * * * * * * * * * * * * * * * *
			// Lista de Firmantes Favoritos por Area

			List<Order> ordersFavorito = new ArrayList<Order>();

			lstFavoritoFirmante = (List<FavoritoFirmante>) mngrFavoritoFirmante.search(restrictionsFavorito,
					ordersFavorito);

			if (!lstRepresentante.isEmpty()) {

				for (Representante rep : lstRepresentante) {

					FavoritoFirmante favTemp = new FavoritoFirmante();
					FavoritoFirmanteKey favTempKey = new FavoritoFirmanteKey();
					favTempKey.setFirmante(rep);
					favTempKey.setFirmArea(rep.getArea());
					favTempKey.setIdArea(rep.getArea().getIdArea());
					favTemp.setFavoritoFirmanteKey(favTempKey);

					lstFavoritoFirmante.add(0, favTemp);

				}
			}
			List<FavoritoFirmante> newLstFavoritoFirmante = new ArrayList<FavoritoFirmante>();

			for (FavoritoFirmante favoritoFirmante : lstFavoritoFirmante) {
				Optional<FavoritoFirmante> optFav = newLstFavoritoFirmante.stream()
						.filter(p -> (p.getFavoritoFirmanteKey().getFirmante().getId()
								.equalsIgnoreCase(favoritoFirmante.getFavoritoFirmanteKey().getFirmante().getId())
								&& p.getFavoritoFirmanteKey().getFirmArea().getIdArea()
										.equals(favoritoFirmante.getFavoritoFirmanteKey().getFirmArea().getIdArea())))
						.findFirst();

				if (!optFav.isPresent()) {
					newLstFavoritoFirmante.add(favoritoFirmante);

				}
			}

			log.debug("Size found >> " + newLstFavoritoFirmante.size());

			return new ResponseEntity<List<FavoritoFirmante>>(newLstFavoritoFirmante, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<FavoritoFirmante> get(Serializable id) {
		throw new UnsupportedOperationException();
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
