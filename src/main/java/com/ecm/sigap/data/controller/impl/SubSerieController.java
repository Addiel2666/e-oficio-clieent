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
import com.ecm.sigap.data.model.SubSerie;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.SubSerie}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class SubSerieController extends CustomRestController //
		implements RESTController<SubSerie> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(SubSerieController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/subserie", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<SubSerie> get(@RequestParam(value = "id", required = true) Serializable id) {

		SubSerie item = null;
		try {

			item = mngrSubSerie.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<SubSerie>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/subserie", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) SubSerie subSerie) {

		List<?> lst = new ArrayList<SubSerie>();
		log.debug("PARAMETROS DE BUSQUEDA : " + subSerie);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (subSerie.getId() != null)
				restrictions.add(Restrictions.idEq(subSerie.getId()));

			if (subSerie.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", subSerie.getActivo()));

			if (subSerie.getCodigo() != null)
				restrictions.add(Restrictions.eq("codigo", subSerie.getCodigo()));

			if (subSerie.getCodigoDisposicion() != null)
				restrictions.add(Restrictions.eq("codigoDisposicion", subSerie.getCodigoDisposicion()));

			if (subSerie.getConcentracion() != null)
				restrictions.add(Restrictions.eq("concentracion", subSerie.getConcentracion()));

			if (subSerie.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", subSerie.getDescripcion(), MatchMode.ANYWHERE));

			if (subSerie.getDisposicion() != null)
				restrictions.add(Restrictions.eq("disposicion", subSerie.getDisposicion()));

			if (subSerie.getIdPadre() != null)
				restrictions.add(Restrictions.eq("idPadre", subSerie.getIdPadre()));

			if (subSerie.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", subSerie.getTipo()));

			if (subSerie.getTramite() != null)
				restrictions.add(Restrictions.eq("tramite", subSerie.getTramite()));

			if (subSerie.getVigencia() != null)
				restrictions.add(Restrictions.eq("vigencia", subSerie.getVigencia()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrSubSerie.search(restrictions, orders);

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
	@RequestMapping(value = "/archivistica/subserie", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("SubSerie A ELIMINAR >> " + id);

		try {

			mngrSubSerie.delete(mngrSubSerie.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/archivistica/subserie", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<SubSerie> save(@RequestBody(required = true) SubSerie SubSerie)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("SubSerie A GUARDAR >> " + SubSerie);

				if (SubSerie.getId() == null) {
					mngrSubSerie.save(SubSerie);
					return new ResponseEntity<SubSerie>(SubSerie, HttpStatus.CREATED);
				} else {
					mngrSubSerie.update(SubSerie);
					return new ResponseEntity<SubSerie>(SubSerie, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<SubSerie>(SubSerie, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
