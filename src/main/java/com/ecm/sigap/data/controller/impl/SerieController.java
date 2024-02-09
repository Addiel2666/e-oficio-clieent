/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import com.ecm.sigap.data.model.Serie;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Serie}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class SerieController extends CustomRestController //
		implements RESTController<Serie> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(SerieController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/serie", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Serie> get(@RequestParam(value = "id", required = true) Serializable id) {

		Serie item = null;
		try {

			item = mngrSerie.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Serie>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/serie", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Serie serie) {

		List<?> lst = new ArrayList<Serie>();
		log.debug("PARAMETROS DE BUSQUEDA : " + serie);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (serie.getId() != null)
				restrictions.add(Restrictions.idEq(serie.getId()));

			if (serie.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", serie.getActivo()));

			if (serie.getCodigo() != null)
				restrictions.add(Restrictions.eq("codigo", serie.getCodigo()));

			if (serie.getCodigoDisposicion() != null)
				restrictions.add(Restrictions.eq("codigoDisposicion", serie.getCodigoDisposicion()));

			if (serie.getConcentracion() != null)
				restrictions.add(Restrictions.eq("concentracion", serie.getConcentracion()));

			if (serie.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", serie.getDescripcion(), MatchMode.ANYWHERE));

			if (serie.getDisposicion() != null)
				restrictions.add(Restrictions.eq("disposicion", serie.getDisposicion()));

			if (serie.getIdPadre() != null)
				restrictions.add(Restrictions.eq("idPadre", serie.getIdPadre()));

			if (serie.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", serie.getTipo()));

			if (serie.getTramite() != null)
				restrictions.add(Restrictions.eq("tramite", serie.getTramite()));

			if (serie.getVigencia() != null)
				restrictions.add(Restrictions.eq("vigencia", serie.getVigencia()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrSerie.search(restrictions, orders);

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
	 * com.ecm.sigap.data.controller.RestController#delete(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/serie", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("Serie A ELIMINAR >> " + id);

		try {

			mngrSerie.delete(mngrSerie.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RestController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/serie", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Serie> save(@RequestBody(required = true) Serie serie) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("Serie A GUARDAR >> " + serie);

				if (serie.getId() == null) {
					mngrSerie.save(serie);
					return new ResponseEntity<Serie>(serie, HttpStatus.CREATED);
				} else {
					mngrSerie.update(serie);
					return new ResponseEntity<Serie>(serie, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<Serie>(serie, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
