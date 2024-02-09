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
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Destinatario;
import com.ecm.sigap.data.model.FavDestinatario;
import com.ecm.sigap.data.model.FavDestinatarioFuncionario;
import com.ecm.sigap.data.model.FavDestinatarioKey;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;



/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FavDestinatario}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class DestinatariosController extends CustomRestController implements RESTController<FavDestinatario> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(DestinatariosController.class);

	/**
	 * Referencia hacia el REST controller de
	 * {@link MinutarioDestinatarioController}.
	 */
	@Autowired
	private MinutarioDestinatarioController minutarioDestinatarioController;

	/**
	 * Gets the.
	 *
	 * @param id
	 *            the id
	 * @param idTipoDestinatario
	 *            the id tipo destinatario
	 * @param idArea
	 *            the id area
	 * @param idAreaDest
	 *            the id area dest
	 * @return the response entity
	 */
	@RequestMapping(value = "/destinatario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<FavDestinatario> get(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "idTipo", required = true) Integer idTipoDestinatario,
			@RequestParam(value = "idArea", required = true) Integer idArea,
			@RequestParam(value = "idAreaDest", required = true) Integer idAreaDest) {

		FavDestinatario item = null;
		FavDestinatarioKey fav = new FavDestinatarioKey();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *

			if ((id != null && !id.isEmpty()) && (idTipoDestinatario != null) && (idArea != null)
					&& (idAreaDest != null)) {

				Area areaDestinatario = mngrArea.fetch(idAreaDest);

				fav.setIdDestinatario(id);
				fav.setIdTipoDestinatario(idTipoDestinatario);
				fav.setIdArea(idArea);
				fav.setAreaDestinatario(areaDestinatario);

			} else {
				return new ResponseEntity<FavDestinatario>(item, HttpStatus.BAD_REQUEST);
			}

			item = mngrfavDestinatario.fetch(fav);

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);
		return new ResponseEntity<FavDestinatario>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/destinatario", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<FavDestinatario> save(
			@RequestBody(required = true) FavDestinatario destinatario) throws Exception {
		throw new Exception("Metodo no implementado");

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
	
	@ApiOperation(value = "Consulta destinarios", notes = "Consulta a los destinatarios del area")
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
	@RequestMapping(value = "/destinatario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) FavDestinatario destinatario) {

		List<?> lst = new ArrayList<>();

		log.info("Parametros de busqueda :: " + destinatario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (destinatario.getDestinatarioKey() != null) {
				Integer idDestinatario = destinatario.getDestinatarioKey().getIdTipoDestinatario();
				if (idDestinatario.equals(0) || idDestinatario.equals(1) || idDestinatario.equals(2)
						|| idDestinatario.equals(3) || idDestinatario.equals(8) || idDestinatario.equals(9)) {
					deleteFavFuncionariosHuerfanos();
				}

				// Filtro por el id del destinatario
				if ((destinatario.getDestinatarioKey().getIdDestinatario() != null)
						&& (!StringUtils.isBlank(destinatario.getDestinatarioKey().getIdDestinatario())))
					restrictions.add(Restrictions.eq("destinatarioKey.idDestinatario",
							destinatario.getDestinatarioKey().getIdDestinatario()));

				// Filtro por el id del Area
				if (destinatario.getDestinatarioKey().getIdArea() != null)
					restrictions.add(
							Restrictions.eq("destinatarioKey.idArea", destinatario.getDestinatarioKey().getIdArea()));

				// Filtro por el tipo de destinatario
				if (destinatario.getDestinatarioKey().getIdTipoDestinatario() != null)
					restrictions.add(Restrictions.eq("destinatarioKey.idTipoDestinatario",
							destinatario.getDestinatarioKey().getIdTipoDestinatario()));

				// Filtro por el Identificador del Area del Destinatario
				if ((destinatario.getDestinatarioKey().getAreaDestinatario() != null)
						&& (destinatario.getDestinatarioKey().getAreaDestinatario().getIdArea() != null))

					restrictions.add(Restrictions.eq("destinatarioKey.idAreaDestinatario",
							destinatario.getDestinatarioKey().getAreaDestinatario().getIdArea()));
			}

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrfavDestinatario.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

			return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/destinatarios/fav", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<FavDestinatario>> 
		searchFav(@RequestBody(required = true) List<Integer> tipoDestinatarios) {

		log.info("Parametros de busqueda :: " + tipoDestinatarios);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<FavDestinatario> lst = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<Criterion>();
			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
			
			restrictions.add(Restrictions.eq("destinatarioKey.idArea", areaId ));
			
			restrictions.add(Restrictions.in("destinatarioKey.idTipoDestinatario", tipoDestinatarios));
			

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<FavDestinatario>) mngrfavDestinatario.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

			return new ResponseEntity<List<FavDestinatario>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}
	
	@RequestMapping(value = "/destinatarios", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchDes(@RequestBody(required = true) Destinatario destinatario) {

		List<?> lst = new ArrayList<>();

		log.info("Parametros de busqueda :: " + destinatario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			if (destinatario.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", destinatario.getIdArea()));

			if (StringUtils.isNotBlank(destinatario.getNombreCompleto()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("nombreCompleto", destinatario.getNombreCompleto(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrDestinatario.search(restrictions, orders);

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
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<FavDestinatario> get(Serializable id) {
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

	/**
	 * Delete fav funcionarios huerfanos.
	 */
	@SuppressWarnings("unchecked")
	private void deleteFavFuncionariosHuerfanos() {

		List<FavDestinatario> lst = new ArrayList<>();
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("destinatarioKey.idArea", areaId));

			restrictions.add(Restrictions.in("destinatarioKey.idTipoDestinatario", new Object[] { 0, 1, 2, 3, 8, 9 }));

			DetachedCriteria subquery = DetachedCriteria.forClass(FavDestinatarioFuncionario.class, "favFunc");

			subquery.setProjection(Projections.property("funcionarioKey.idDestinatario"));

			subquery.add(Restrictions.eqProperty("funcionarioKey.idDestinatario",
					"favoritoDestinatario.destinatarioKey.idDestinatario"));
			subquery.add(Restrictions.eqProperty("funcionarioKey.idAreaDestinatario",
					"favoritoDestinatario.destinatarioKey.idAreaDestinatario"));

			subquery.add(Restrictions.eq("funcionarioKey.idArea", areaId));

			restrictions.add(Subqueries.notExists(subquery));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<FavDestinatario>) mngrfavDestinatario.search(restrictions, null);
			log.debug("Lista de Favoritos Funcionario Huerfanos a eliminar >> " + lst.size());
			for (FavDestinatario favDestinatario : lst) {

				minutarioDestinatarioController.delete(favDestinatario.getDestinatarioKey().getIdArea().toString(),
						favDestinatario.getDestinatarioKey().getIdDestinatario().toString(),
						favDestinatario.getDestinatarioKey().getIdTipoDestinatario().toString(),
						favDestinatario.getDestinatarioKey().getIdAreaDestinatario().toString());
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

}
