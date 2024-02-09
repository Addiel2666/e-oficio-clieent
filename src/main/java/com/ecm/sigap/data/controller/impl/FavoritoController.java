/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.AreaRevisor;
import com.ecm.sigap.data.model.FavDestinatarioCiudadano;
import com.ecm.sigap.data.model.FavDestinatarioFuncionario;
import com.ecm.sigap.data.model.FavDestinatarioRepLegal;
import com.ecm.sigap.data.model.FavNoDestinatarioFuncionario;
import com.ecm.sigap.data.model.Favorito;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.TitularNoFavorito;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * The Class FavoritoController.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@RestController
public class FavoritoController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FavoritoController.class);

	/**
	 * Search favorito firmante. Obtiene los favoritos firmantes agregados en
	 * favFirmantes
	 *
	 * @param favorito
	 *            the favorito
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta firmante favorito", notes = "Consulta la lista de firmantes favoritos de borradores")
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
	
	@RequestMapping(value = "/favorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchFavoritoFirmante(
			@RequestBody(required = true) Favorito favorito) {

		List<?> lst = new ArrayList<Favorito>();

		log.info("Parametros de busqueda :: " + favorito);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favorito.getFavoritoKey().getIdArea() != null)
				restrictions.add(Restrictions.eq("favoritoKey.idArea", favorito.getFavoritoKey().getIdArea()));

			if (favorito.getFavoritoKey().getIdFirmArea() != null)
				restrictions.add(Restrictions.eq("favoritoKey.idFirmArea", favorito.getFavoritoKey().getIdFirmArea()));

			if (favorito.getAreactivosn() != null)
				restrictions.add(Restrictions.eq("areactivosn", favorito.getAreactivosn()));

			if (favorito.getFavoritoKey().getIdInstitucion() != null)
				restrictions.add(
						Restrictions.eq("favoritoKey.idInstitucion", favorito.getFavoritoKey().getIdInstitucion()));

			if ((favorito.getIdTipoInstitucion() != null) && (!favorito.getIdTipoInstitucion().isEmpty()))
				restrictions.add(Restrictions.eq("idTipoInstitucion", favorito.getIdTipoInstitucion()));

			if (favorito.getInstitucionActivosn() != null)
				restrictions.add(Restrictions.eq("institucionActivosn", favorito.getInstitucionActivosn()));

			if ((favorito.getFavoritoKey().getIdFirmante() != null)
					&& (!favorito.getFavoritoKey().getIdFirmante().isEmpty()))
				restrictions.add(Restrictions.eq("favoritoKey.idFirmante", favorito.getFavoritoKey().getIdFirmante()));

			if ((favorito.getIdTipoFirmante() != null) && (!favorito.getIdTipoFirmante().isEmpty()))
				restrictions.add(Restrictions.eq("idTipoFirmante", favorito.getIdTipoFirmante()));

			if ((favorito.getPaterno() != null) && (!favorito.getPaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", favorito.getPaterno(), MatchMode.ANYWHERE));

			if ((favorito.getMaterno() != null) && (!favorito.getMaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("materno", favorito.getMaterno(), MatchMode.ANYWHERE));

			if ((favorito.getNombre() != null) && (!favorito.getNombre().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombre", favorito.getNombre(), MatchMode.ANYWHERE));

			if (favorito.getUsuarioActivosn() != null)
				restrictions.add(Restrictions.eq("usuarioActivosn", favorito.getUsuarioActivosn()));
			
			if ((favorito.getCargo() != null) && (!favorito.getCargo().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("cargo", favorito.getCargo(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavorito.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search titular no favorito. Obtiene los titulares de Areas no asignados
	 * como favoritos en favFirmante
	 *
	 * @param titularNoFavorito
	 *            the titular no favorito
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta titulares no favoritos", notes = "Consulta la lista de titulares no favoritos en borradores")
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
	
	@RequestMapping(value = "/titularNoFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchTitularNoFavorito(
			@RequestBody(required = true) TitularNoFavorito titularNoFavorito) {

		List<?> lst = new ArrayList<Favorito>();

		log.info("Parametros de busqueda :: " + titularNoFavorito);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (titularNoFavorito.getTitularNoFavoritoKey().getIdArea() != null)
				restrictions.add(Restrictions.eq("titularNoFavoritoKey.idArea",
						titularNoFavorito.getTitularNoFavoritoKey().getIdArea()));

			if (titularNoFavorito.getTitularNoFavoritoKey().getIdFirmArea() != null)
				restrictions.add(Restrictions.eq("titularNoFavoritoKey.idFirmArea",
						titularNoFavorito.getTitularNoFavoritoKey().getIdFirmArea()));

			if (titularNoFavorito.getAreactivosn() != null)
				restrictions.add(Restrictions.eq("areactivosn", titularNoFavorito.getAreactivosn()));

			if (titularNoFavorito.getTitularNoFavoritoKey().getIdInstitucion() != null)
				restrictions.add(Restrictions.eq("titularNoFavoritoKey.idInstitucion",
						titularNoFavorito.getTitularNoFavoritoKey().getIdInstitucion()));

			if ((titularNoFavorito.getIdTipoInstitucion() != null)
					&& (!titularNoFavorito.getIdTipoInstitucion().isEmpty()))
				restrictions.add(Restrictions.eq("idTipoInstitucion", titularNoFavorito.getIdTipoInstitucion()));

			if (titularNoFavorito.getInstitucionActivosn() != null)
				restrictions.add(Restrictions.eq("institucionActivosn", titularNoFavorito.getInstitucionActivosn()));

			if ((titularNoFavorito.getTitularNoFavoritoKey().getIdFirmante() != null)
					&& (!titularNoFavorito.getTitularNoFavoritoKey().getIdFirmante().isEmpty()))
				restrictions.add(Restrictions.eq("titularNoFavoritoKey.idFirmante",
						titularNoFavorito.getTitularNoFavoritoKey().getIdFirmante()));

			if ((titularNoFavorito.getIdTipoFirmante() != null) && (!titularNoFavorito.getIdTipoFirmante().isEmpty()))
				restrictions.add(Restrictions.eq("idTipoFirmante", titularNoFavorito.getIdTipoFirmante()));

			if ((titularNoFavorito.getPaterno() != null) && (!titularNoFavorito.getPaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", titularNoFavorito.getPaterno(), MatchMode.ANYWHERE));

			if ((titularNoFavorito.getMaterno() != null) && (!titularNoFavorito.getMaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("materno", titularNoFavorito.getMaterno(), MatchMode.ANYWHERE));

			if ((titularNoFavorito.getNombre() != null) && (!titularNoFavorito.getNombre().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombre", titularNoFavorito.getNombre(), MatchMode.ANYWHERE));

			if (titularNoFavorito.getUsuarioActivosn() != null)
				restrictions.add(Restrictions.eq("usuarioActivosn", titularNoFavorito.getUsuarioActivosn()));
			
			if ((titularNoFavorito.getCargo() != null) && (!titularNoFavorito.getCargo().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("cargo", titularNoFavorito.getCargo(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrTitularNoFavorito.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search favorito destinatario ciudadano. Obtiene los Destinatarios
	 * Ciudadanos agregados como favoritos en minutariosDestinatarios
	 * dependiendo si son turnos o copia
	 *
	 * @param favoritoCiudadano
	 *            the favorito ciudadano
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario ciudadano", notes = "Consulta la lista de destinatarios ciudadanos agregados a favoritos")
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
	
	@RequestMapping(value = "/favDestinatarioCiudadano", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchFavoritoDestinatarioCiudadano(
			@RequestBody(required = true) FavDestinatarioCiudadano favoritoCiudadano) {

		List<?> lst = new ArrayList<FavDestinatarioCiudadano>();

		log.info("Parametros de busqueda :: " + favoritoCiudadano);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if ((favoritoCiudadano.getIdArea() != null) && (favoritoCiudadano.getIdTipoDestinatario() != null)) {
				restrictions.add(Restrictions.eq("idArea", favoritoCiudadano.getIdArea()));
				restrictions.add(Restrictions.eq("idTipoDestinatario", favoritoCiudadano.getIdTipoDestinatario()));

				if ((favoritoCiudadano.getPaterno() != null) && (!favoritoCiudadano.getPaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("paterno", favoritoCiudadano.getPaterno(), MatchMode.ANYWHERE));

				if ((favoritoCiudadano.getMaterno() != null) && (!favoritoCiudadano.getMaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("materno", favoritoCiudadano.getMaterno(), MatchMode.ANYWHERE));

				if ((favoritoCiudadano.getNombre() != null) && (!favoritoCiudadano.getNombre().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("nombre", favoritoCiudadano.getNombre(), MatchMode.ANYWHERE));
			} else {

				return new ResponseEntity<List<?>>(lst, HttpStatus.CONFLICT);
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavDestinatarioCiudadano.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search no favorito destinatario ciudadano. Obtiene los Ciudadanos no
	 * agregados como favoritos en minutariosdestinatarios dependiendo si son
	 * turnos o copia
	 * 
	 * @param favoritoCiudadano
	 *            the favorito ciudadano
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario ciudadano", notes = "Consulta la lista de destinatarios ciudadanos no agregados a favoritos")
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
	
	@RequestMapping(value = "/noFavDestinatarioCiudadano", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchNoFavoritoDestinatarioCiudadano(
			@RequestBody(required = true) FavDestinatarioCiudadano favoritoCiudadano) {

		List<?> lst = new ArrayList<FavDestinatarioCiudadano>();

		log.info("Parametros de busqueda :: " + favoritoCiudadano);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoCiudadano.getIdTipoDestinatario() != null) {

				restrictions.add(Restrictions.or(
						Restrictions.ne("idTipoDestinatario", favoritoCiudadano.getIdTipoDestinatario()),
						Restrictions.isNull("idTipoDestinatario")));

				if ((favoritoCiudadano.getPaterno() != null) && (!favoritoCiudadano.getPaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("paterno", favoritoCiudadano.getPaterno(), MatchMode.ANYWHERE));

				if ((favoritoCiudadano.getMaterno() != null) && (!favoritoCiudadano.getMaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("materno", favoritoCiudadano.getMaterno(), MatchMode.ANYWHERE));

				if ((favoritoCiudadano.getNombre() != null) && (!favoritoCiudadano.getNombre().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("nombre", favoritoCiudadano.getNombre(), MatchMode.ANYWHERE));

			} else {
				return new ResponseEntity<List<?>>(lst, HttpStatus.CONFLICT);
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavDestinatarioCiudadano.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search favorito destinatario rep legal. Obtiene los Destinatarios
	 * Representante Legal agregados como favoritos en minutariosDestinatarios
	 * dependiendo si son turnos o copia
	 *
	 * @param favoritoRepLegal
	 *            the favorito rep legal
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario representante legal", notes = "Consulta la lista de destinatarios representante legal agregados a favoritos")
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
	
	@RequestMapping(value = "/favDestinatarioRepLegal", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchFavoritoDestinatarioRepLegal(
			@RequestBody(required = true) FavDestinatarioRepLegal favoritoRepLegal) {

		List<?> lst = new ArrayList<FavDestinatarioRepLegal>();

		log.info("Parametros de busqueda :: " + favoritoRepLegal);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if ((favoritoRepLegal.getIdArea() != null) && (favoritoRepLegal.getIdTipoDestinatario() != null)) {
				restrictions.add(Restrictions.eq("idArea", favoritoRepLegal.getIdArea()));
				restrictions.add(Restrictions.eq("idTipoDestinatario", favoritoRepLegal.getIdTipoDestinatario()));

				if (favoritoRepLegal.getIdEmpresa() != null)
					restrictions.add(Restrictions.eq("idEmpresa", favoritoRepLegal.getIdEmpresa()));

				if ((favoritoRepLegal.getPaterno() != null) && (!favoritoRepLegal.getPaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("paterno", favoritoRepLegal.getPaterno(), MatchMode.ANYWHERE));

				if ((favoritoRepLegal.getMaterno() != null) && (!favoritoRepLegal.getMaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("materno", favoritoRepLegal.getMaterno(), MatchMode.ANYWHERE));

				if ((favoritoRepLegal.getNombre() != null) && (!favoritoRepLegal.getNombre().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("nombre", favoritoRepLegal.getNombre(), MatchMode.ANYWHERE));

			} else {
				return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
			}
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavDestinatarioRepLegal.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search no favorito destinatario rep legal. Obtiene los Representantes
	 * Legal no agregados como favoritos en minutariosdestinatarios dependiendo
	 * si son turnos o copia
	 *
	 * @param favoritoRepLegal
	 *            the favorito rep legal
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario representante legal", notes = "Consulta la lista de destinatarios representante legal no agregados a favoritos")
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
	
	@RequestMapping(value = "/noFavDestinatarioRepLegal", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchNoFavoritoDestinatarioRepLegal(
			@RequestBody(required = true) FavDestinatarioRepLegal favoritoRepLegal) {

		List<?> lst = new ArrayList<FavDestinatarioRepLegal>();

		log.info("Parametros de busqueda :: " + favoritoRepLegal);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoRepLegal.getIdTipoDestinatario() != null) {
				restrictions.add(
						Restrictions.or(Restrictions.ne("idTipoDestinatario", favoritoRepLegal.getIdTipoDestinatario()),
								Restrictions.isNull("idTipoDestinatario")));

				if (favoritoRepLegal.getIdEmpresa() != null)
					restrictions.add(Restrictions.eq("idEmpresa", favoritoRepLegal.getIdEmpresa()));

				if ((favoritoRepLegal.getPaterno() != null) && (!favoritoRepLegal.getPaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("paterno", favoritoRepLegal.getPaterno(), MatchMode.ANYWHERE));

				if ((favoritoRepLegal.getMaterno() != null) && (!favoritoRepLegal.getMaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("materno", favoritoRepLegal.getMaterno(), MatchMode.ANYWHERE));

				if ((favoritoRepLegal.getNombre() != null) && (!favoritoRepLegal.getNombre().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("nombre", favoritoRepLegal.getNombre(), MatchMode.ANYWHERE));

			} else {
				return new ResponseEntity<List<?>>(lst, HttpStatus.CONFLICT);
			}
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavDestinatarioRepLegal.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search favorito destinatario funcionario.
	 *
	 * @param favoritoFuncionario
	 *            the favorito funcionario
	 * @return the response entity
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario funcionario", notes = "Consulta la lista de destinatarios funcionarios agregados a favoritos")
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
	
	@RequestMapping(value = "/favDestinatarioFuncionario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchFavoritoDestinatarioFuncionario(
			@RequestBody(required = true) FavDestinatarioFuncionario favoritoFuncionario) throws Exception {

		List<?> lst = new ArrayList<FavDestinatarioFuncionario>();

		log.info("Parametros de busqueda :: " + favoritoFuncionario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoFuncionario.getFuncionarioKey() != null
					&& (favoritoFuncionario.getFuncionarioKey().getIdArea() != null)
					&& (favoritoFuncionario.getFuncionarioKey().getIdTipoDestinatario() != null)) {

				restrictions.add(Restrictions.or(Restrictions.eq("usuarioActivoSN", "S"),
						Restrictions.isNull("usuarioActivoSN")));

				restrictions.add(
						Restrictions.eq("funcionarioKey.idArea", favoritoFuncionario.getFuncionarioKey().getIdArea()));
				restrictions.add(Restrictions.eq("funcionarioKey.idTipoDestinatario",
						favoritoFuncionario.getFuncionarioKey().getIdTipoDestinatario()));

				if (favoritoFuncionario.getIdInstitucion() != null)
					restrictions.add(Restrictions.eq("idInstitucion", favoritoFuncionario.getIdInstitucion()));
				
				if (favoritoFuncionario.getFuncionarioKey().getIdAreaDestinatario() != null)
					restrictions.add(Restrictions.eq("funcionarioKey.idAreaDestinatario",
							favoritoFuncionario.getFuncionarioKey().getIdAreaDestinatario()));
				

				if ((favoritoFuncionario.getPaterno() != null) && (!favoritoFuncionario.getPaterno().isEmpty()))
					restrictions
							.add(EscapedLikeRestrictions.ilike("paterno", favoritoFuncionario.getPaterno(), MatchMode.ANYWHERE));

				if ((favoritoFuncionario.getMaterno() != null) && (!favoritoFuncionario.getMaterno().isEmpty()))
					restrictions
							.add(EscapedLikeRestrictions.ilike("materno", favoritoFuncionario.getMaterno(), MatchMode.ANYWHERE));

				if ((favoritoFuncionario.getNombre() != null) && (!favoritoFuncionario.getNombre().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("nombre", favoritoFuncionario.getNombre(), MatchMode.ANYWHERE));

				if ((favoritoFuncionario.getCargo() != null) && (!favoritoFuncionario.getCargo().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("cargo", favoritoFuncionario.getCargo(), MatchMode.ANYWHERE));

			} else {

				return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
				// throw new Exception("No hay parametros de busqueda.");

			}
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavDestinatarioFuncionario.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

			return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Search no favorito destinatario funcionario.
	 *
	 * @param favoritoFuncionario
	 *            the favorito funcionario
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta destinatario funcionario", notes = "Consulta la lista de destinatarios funcionarios no agregados a favoritos")
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
	@RequestMapping(value = "/noFavDestinatarioFuncionario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<FavDestinatarioFuncionario>> searchNoFavoritoDestinatarioFuncionario(
			@RequestBody(required = true) FavNoDestinatarioFuncionario nofavoritoFuncionario) {

		List<FavDestinatarioFuncionario> lst = new ArrayList<FavDestinatarioFuncionario>();

		log.info("Parametros de busqueda :: " + nofavoritoFuncionario);

		try {

			if (nofavoritoFuncionario.getIdInstitucion() != null) {
				// * * * * * * * * * * * * * * * * * * * * * *
				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.or(Restrictions.eq("usuarioActivoSN", "S"),
						Restrictions.isNull("usuarioActivoSN")));

				restrictions.add(Restrictions.eq("idInstitucion", nofavoritoFuncionario.getIdInstitucion()));

				if (nofavoritoFuncionario.getNoFavfuncionarioKey().getIdAreaDestinatario() != null)
					restrictions.add(Restrictions.eq("noFavfuncionarioKey.idAreaDestinatario",
							nofavoritoFuncionario.getNoFavfuncionarioKey().getIdAreaDestinatario()));

				if ((nofavoritoFuncionario.getNoFavfuncionarioKey() != null)
						&& (!nofavoritoFuncionario.getNoFavfuncionarioKey().getIdRepresentante().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("noFavfuncionarioKey.idRepresentante",
							nofavoritoFuncionario.getNoFavfuncionarioKey().getIdRepresentante(), MatchMode.ANYWHERE));

				if ((nofavoritoFuncionario.getPaterno() != null) && (!nofavoritoFuncionario.getPaterno().isEmpty()))
					restrictions
							.add(EscapedLikeRestrictions.ilike("paterno", nofavoritoFuncionario.getPaterno(), MatchMode.ANYWHERE));

				if ((nofavoritoFuncionario.getMaterno() != null) && (!nofavoritoFuncionario.getMaterno().isEmpty()))
					restrictions
							.add(EscapedLikeRestrictions.ilike("materno", nofavoritoFuncionario.getMaterno(), MatchMode.ANYWHERE));

				if ((nofavoritoFuncionario.getNombre() != null) && (!nofavoritoFuncionario.getNombre().isEmpty()))
					restrictions
							.add(EscapedLikeRestrictions.ilike("nombre", nofavoritoFuncionario.getNombre(), MatchMode.ANYWHERE));

				if (nofavoritoFuncionario.getIdTipoRepresentante() != null)
					restrictions.add(
							Restrictions.eq("idTipoRepresentante", nofavoritoFuncionario.getIdTipoRepresentante()));

				if ((nofavoritoFuncionario.getCargo() != null) && (!nofavoritoFuncionario.getCargo().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("cargo", nofavoritoFuncionario.getCargo(), MatchMode.ANYWHERE));

				List<Order> orders = new ArrayList<Order>();

				orders.add(Order.asc("paterno"));

				// * * * * * * * * * * * * * * * * * * * * * *
				lst = (List<FavDestinatarioFuncionario>) mngrfavNoDestinatarioFuncionario.search(restrictions, orders);

				log.debug("Size found >> " + lst.size());
			} else {
				return new ResponseEntity<List<FavDestinatarioFuncionario>>(lst, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<FavDestinatarioFuncionario>>(lst, HttpStatus.OK);
	}

	/**
	 * Busqueda de Area no Asignada Como Remitentes.
	 *
	 * @param areaRemitente
	 *            the area remitente
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta area no asignada", notes = "Consulta la lista de areas no asignadas como remitentes")
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
	
	@RequestMapping(value = "/noFavRevisor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchRemitentes(
			@RequestBody(required = true) Representante representante) {

		List<?> lst = new ArrayList<Representante>();
		log.debug("PARAMETROS DE BUSQUEDA : " + representante);
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<Order> orders = new ArrayList<Order>();

			if ((representante.getArea() != null) && (representante.getArea().getIdArea()) != null)
				restrictions.add(Restrictions.eq("area.idArea", representante.getArea().getIdArea()));

			if (representante.getId() != null)
				restrictions.add(Restrictions.idEq(representante.getId()));

			if ((representante.getMaterno() != null) && (!representante.getMaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("materno", representante.getMaterno(), MatchMode.ANYWHERE));

			if ((representante.getPaterno() != null) && (!representante.getPaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", representante.getPaterno(), MatchMode.ANYWHERE));

			if ((representante.getNombres() != null) && (!representante.getNombres().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombres", representante.getNombres(), MatchMode.ANYWHERE));

			restrictions.add(Restrictions.eq("idTipo", "U"));

			restrictions.add(Restrictions.eq("activosn", "S"));

			DetachedCriteria subquery = DetachedCriteria.forClass(AreaRevisor.class, "AreaRevisor");

			subquery.setProjection(Projections.property("areaRevisorKey.revisor.id"));
			subquery.add(Restrictions.eqProperty("areaRevisorKey.revisor.id", "representante.id"));

			restrictions.add(Subqueries.notExists(subquery));

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

}
