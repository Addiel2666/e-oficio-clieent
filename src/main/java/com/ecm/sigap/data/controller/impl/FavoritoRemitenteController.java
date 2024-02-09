package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
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
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Empresa;
import com.ecm.sigap.data.model.FavoritoRemitente;
import com.ecm.sigap.data.model.Institucion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.AreaRemitente}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class FavoritoRemitenteController extends CustomRestController implements RESTController<FavoritoRemitente> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FavoritoRemitenteController.class);

	/*
	 * Obtiene la lista de Favoritos Remitentes de una área en especifico
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta remitente favorito", notes = "Consulta remitente favoritos de una area especifica")
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
	@RequestMapping(value = "/remitenteFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) FavoritoRemitente favoritoRemitente) {

		List<?> lst = new ArrayList<FavoritoRemitente>();
		log.info("Parametros de busqueda :: " + favoritoRemitente);
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoRemitente.getFavoritoRemitenteKey() != null) {

				if (favoritoRemitente.getFavoritoRemitenteKey().getIdArea() != null) {

					// Validamos que estamos solicitando los remitentes
					// favoritas de area que esta logeado el usuario
					Integer idArea = Integer.valueOf(getHeader(HeaderValueNames.HEADER_AREA_ID));
					if (!idArea.equals(favoritoRemitente.getFavoritoRemitenteKey().getIdArea())) {
						log.error(
								"El area de la cabecera no concuerda con la solicitada por lo que se rechaza la solicitud");
						return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
					}

					restrictions.add(Restrictions.eq("favoritoRemitenteKey.idArea",
							favoritoRemitente.getFavoritoRemitenteKey().getIdArea()));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getIdRemitente())
						restrictions.add(Restrictions.eq("favoritoRemitenteKey.idRemitente",
								favoritoRemitente.getFavoritoRemitenteKey().getIdRemitente()));
				}

				// Filtramos por la informacion de la institucion
				if (favoritoRemitente.getFavoritoRemitenteKey().getPromotor() != null) {

					DetachedCriteria subquery = DetachedCriteria.forClass(Institucion.class, "institucion");

					subquery.setProjection(Projections.property("institucion.idInstitucion"));
					subquery.add(
							Restrictions.eqProperty("favoritoRemitente.favoritoRemitenteKey.promotor.idInstitucion",
									"institucion.idInstitucion"));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion())
						subquery.add(Restrictions.eq("institucion.idInstitucion",
								favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion()));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo())
						subquery.add(Restrictions.eq("institucion.tipo",
								favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo()));

					restrictions.add(Subqueries.exists(subquery));
				}
			}

			if (null != favoritoRemitente.getDescripcion() && !favoritoRemitente.getDescripcion().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", favoritoRemitente.getDescripcion(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularCargo() && !favoritoRemitente.getTitularCargo().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularCargo", favoritoRemitente.getTitularCargo(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularUsuario() && !favoritoRemitente.getTitularUsuario().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularUsuario", favoritoRemitente.getTitularUsuario(),
						MatchMode.ANYWHERE));
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));
			lst = mngrFavoritoRemitente.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Obtiene la lista de remitentes que estan en los favoritos del Area
	 * 
	 * @param favoritoRemitente Remitente favorito que se usa para excluir empresas
	 *                          y ciudadanos
	 * @return Lista de remitentes que estan los favoritos del Area
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta remitente externo favorito", notes = "Consulta remitente externos favoritos")
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
	
	@RequestMapping(value = "/remitenteExternoFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchInstitucionesExternas(
			@RequestBody(required = true) FavoritoRemitente favoritoRemitente) {

		List<?> lst = new ArrayList<FavoritoRemitente>();
		log.info("Parametros de busqueda :: " + favoritoRemitente);
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoRemitente.getFavoritoRemitenteKey() != null) {

				if (favoritoRemitente.getFavoritoRemitenteKey().getIdArea() != null) {

					// Validamos que estamos solicitando los remitentes
					// favoritas de area que esta logeado el usuario
					Integer idArea = Integer.valueOf(getHeader(HeaderValueNames.HEADER_AREA_ID));
					if (!idArea.equals(favoritoRemitente.getFavoritoRemitenteKey().getIdArea())) {
						log.error(
								"El area de la cabecera no concuerda con la solicitada por lo que se rechaza la solicitud");
						return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
					}

					restrictions.add(Restrictions.eq("favoritoRemitenteKey.idArea",
							favoritoRemitente.getFavoritoRemitenteKey().getIdArea()));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getIdRemitente())
						restrictions.add(Restrictions.eq("favoritoRemitenteKey.idRemitente",
								favoritoRemitente.getFavoritoRemitenteKey().getIdRemitente()));
				}

				// Filtramos por la informacion de la institucion
				if (favoritoRemitente.getFavoritoRemitenteKey().getPromotor() != null) {

					Integer idempresa = Integer.valueOf(getParamApp("IDEMPPROMOTOR"));
					Integer idciudadano = Integer.valueOf(getParamApp("IDCIUDPROMOTOR"));

					DetachedCriteria subquery = DetachedCriteria.forClass(Institucion.class, "institucion");

					subquery.setProjection(Projections.property("institucion.idInstitucion"));
					subquery.add(
							Restrictions.eqProperty("favoritoRemitente.favoritoRemitenteKey.promotor.idInstitucion",
									"institucion.idInstitucion"));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion()) {
						subquery.add(Restrictions.eq("institucion.idInstitucion",
								favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion()));
					} else {
						if (null != idempresa)
							subquery.add(Restrictions.ne("institucion.idInstitucion", idempresa));
						if (null != idciudadano)
							subquery.add(Restrictions.ne("institucion.idInstitucion", idciudadano));
					}

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo())
						subquery.add(Restrictions.eq("institucion.tipo",
								favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo()));

					restrictions.add(Subqueries.exists(subquery));
				}
			}

			if (null != favoritoRemitente.getDescripcion() && !favoritoRemitente.getDescripcion().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", favoritoRemitente.getDescripcion(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularCargo() && !favoritoRemitente.getTitularCargo().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularCargo", favoritoRemitente.getTitularCargo(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularUsuario() && !favoritoRemitente.getTitularUsuario().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularUsuario", favoritoRemitente.getTitularUsuario(),
						MatchMode.ANYWHERE));
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));
			lst = mngrFavoritoRemitente.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Obtiene la lista de Remitenes que no estan en los favoritos del Area
	 * 
	 * @param favoritoRemitente Remitente favorito que se usa para excluir
	 * @return Lista de Remitenes que no estan en los favoritos del Area
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta remitente externo no favorito", notes = "Consulta remitente externos no favoritos")
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

	@RequestMapping(value = "/remitenteNoFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchRemitentesNoFavoritos(
			@RequestBody(required = true) FavoritoRemitente favoritoRemitente) {

		log.info("Parametros de busqueda :: " + favoritoRemitente);

		List<?> lst = new ArrayList<Area>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<Order> orders = new ArrayList<Order>();

			restrictions.add(Restrictions.eq("activo", true));

			if (null != favoritoRemitente.getFavoritoRemitenteKey()
					&& null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor()) {

				if (favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion() != null)
					restrictions.add(Restrictions.eq("institucion.idInstitucion",
							favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion()));

				if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo())
					restrictions.add(Restrictions.eq("institucion.tipo",
							favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo()));

			}

			if (null != favoritoRemitente.getDescripcion() && !favoritoRemitente.getDescripcion().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", favoritoRemitente.getDescripcion(),
						MatchMode.ANYWHERE));
			}

			DetachedCriteria subquery = DetachedCriteria.forClass(FavoritoRemitente.class, "favoritoRemitente");

			subquery.setProjection(Projections.property("favoritoRemitenteKey.idRemitente"));
			subquery.add(Restrictions.eqProperty("favoritoRemitenteKey.idRemitente", "area.idArea"));
			subquery.add(Restrictions.eqProperty("favoritoRemitenteKey.promotor.idInstitucion",
					"institucion.idInstitucion"));
			if (null != favoritoRemitente.getTitularCargo() && !favoritoRemitente.getTitularCargo().isEmpty())
				subquery.add(EscapedLikeRestrictions.ilike("titularCargo", favoritoRemitente.getTitularCargo(),
						MatchMode.ANYWHERE));
			if (null != favoritoRemitente.getTitularUsuario() && !favoritoRemitente.getTitularUsuario().isEmpty())
				subquery.add(Restrictions.eq("titularUsuario", favoritoRemitente.getTitularUsuario()));
			subquery.add(Restrictions.eq("favoritoRemitenteKey.idArea",
					favoritoRemitente.getFavoritoRemitenteKey().getIdArea()));

			restrictions.add(Subqueries.notExists(subquery));

			if (null != favoritoRemitente.getTitularCargo() && !favoritoRemitente.getTitularCargo().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularCargo", favoritoRemitente.getTitularCargo(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularUsuario() && !favoritoRemitente.getTitularUsuario().isEmpty()) {
				restrictions.add(Restrictions.eq("titular.id", favoritoRemitente.getTitularUsuario()));
			}

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrArea.search(restrictions, orders, null, null, null);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Obtiene la lista de Empresas que estan en los favoritos del Area
	 * 
	 * @param favoritoRemitente Remitente favorito que se usa para excluir
	 * @return Lista de Empresas que estan en los favoritos del Area
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta remitente empresa favorito", notes = "Consulta remitente de empresa favorito")
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
	
	@RequestMapping(value = "/remitenteEmpresaFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchRemitentesEmpresasFavoritas(
			@RequestBody(required = true) FavoritoRemitente favoritoRemitente) {

		List<?> lst = new ArrayList<FavoritoRemitente>();
		log.info("Parametros de busqueda :: " + favoritoRemitente);
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (favoritoRemitente.getFavoritoRemitenteKey() != null) {

				if (favoritoRemitente.getFavoritoRemitenteKey().getIdArea() != null) {

					// Validamos que estamos solicitando los remitentes
					// favoritas de area que esta logeado el usuario
					Integer idArea = Integer.valueOf(getHeader(HeaderValueNames.HEADER_AREA_ID));
					if (!idArea.equals(favoritoRemitente.getFavoritoRemitenteKey().getIdArea())) {
						log.error(
								"El area de la cabecera no concuerda con la solicitada por lo que se rechaza la solicitud");
						return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
					}

					restrictions.add(Restrictions.eq("favoritoRemitenteKey.idArea",
							favoritoRemitente.getFavoritoRemitenteKey().getIdArea()));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getIdRemitente())
						restrictions.add(Restrictions.eq("favoritoRemitenteKey.idRemitente",
								favoritoRemitente.getFavoritoRemitenteKey().getIdRemitente()));
				}

				// Filtramos por la informacion de la institucion
				if (favoritoRemitente.getFavoritoRemitenteKey().getPromotor() != null) {

					Integer idempresa = Integer.valueOf(getParamApp("IDEMPPROMOTOR"));
					if (idempresa != null)
						favoritoRemitente.getFavoritoRemitenteKey().getPromotor().setIdInstitucion(idempresa);

					DetachedCriteria subquery = DetachedCriteria.forClass(Institucion.class, "institucion");

					subquery.setProjection(Projections.property("institucion.idInstitucion"));
					subquery.add(
							Restrictions.eqProperty("favoritoRemitente.favoritoRemitenteKey.promotor.idInstitucion",
									"institucion.idInstitucion"));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion())
						subquery.add(Restrictions.eq("institucion.idInstitucion",
								favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getIdInstitucion()));

					if (null != favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo())
						subquery.add(Restrictions.eq("institucion.tipo",
								favoritoRemitente.getFavoritoRemitenteKey().getPromotor().getTipo()));

					restrictions.add(Subqueries.exists(subquery));
				}
			}

			if (null != favoritoRemitente.getDescripcion() && !favoritoRemitente.getDescripcion().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", favoritoRemitente.getDescripcion(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularCargo() && !favoritoRemitente.getTitularCargo().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularCargo", favoritoRemitente.getTitularCargo(),
						MatchMode.ANYWHERE));
			}

			if (null != favoritoRemitente.getTitularUsuario() && !favoritoRemitente.getTitularUsuario().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("titularUsuario", favoritoRemitente.getTitularUsuario(),
						MatchMode.ANYWHERE));
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));
			lst = mngrFavoritoRemitente.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Obtiene la lista de Empresas que no estan en los favoritos del Area
	 * 
	 * @param favoritoRemitente Remitente favorito que se usa para excluir
	 * @return Lista de Empresas que no estan en los favoritos del Area
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta empresa no favorito", notes = "Consulta remitente de empresa no favorito")
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
	
	@RequestMapping(value = "/remitenteEmpresaNoFavorito", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchRemitentesEmpresas(
			@RequestBody(required = true) FavoritoRemitente favoritoRemitente) {

		log.info("Parametros de busqueda :: " + favoritoRemitente);

		List<?> lst = new ArrayList<Empresa>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<Order> orders = new ArrayList<Order>();

			if (null != favoritoRemitente.getDescripcion() && !favoritoRemitente.getDescripcion().isEmpty()) {
				restrictions.add(EscapedLikeRestrictions.ilike("nombre", favoritoRemitente.getDescripcion(),
						MatchMode.ANYWHERE));
			}
			if (favoritoRemitente.getActivosn() != null)
				restrictions.add(Restrictions.eq("activosn", favoritoRemitente.getActivosn()));

			Integer idEmpresa = Integer.valueOf(getParamApp("IDEMPPROMOTOR"));

			DetachedCriteria subquery = DetachedCriteria.forClass(FavoritoRemitente.class, "favoritoRemitente");

			subquery.setProjection(Projections.property("favoritoRemitenteKey.idRemitente"));
			subquery.add(Restrictions.eqProperty("favoritoRemitenteKey.idRemitente", "empresa.id"));
			subquery.add(Restrictions.eq("favoritoRemitenteKey.promotor.idInstitucion", idEmpresa));
			subquery.add(Restrictions.eq("favoritoRemitenteKey.idArea",
					favoritoRemitente.getFavoritoRemitenteKey().getIdArea()));

			restrictions.add(Subqueries.notExists(subquery));

			orders.add(Order.asc("nombre"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrEmpresa.search(restrictions, orders, null, null, null);

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
	@Override
	public @ResponseBody ResponseEntity<FavoritoRemitente> save(
			@RequestBody(required = true) FavoritoRemitente areaRemitente) throws Exception {
		throw new UnsupportedOperationException("Metodo no soportado");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<FavoritoRemitente> get(Serializable id) {
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
