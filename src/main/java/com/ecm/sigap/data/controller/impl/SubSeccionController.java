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
import com.ecm.sigap.data.model.SubSeccion;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.SubSeccion}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class SubSeccionController extends CustomRestController //
		implements RESTController<SubSeccion> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(SubSeccionController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/subseccion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<SubSeccion> get(@RequestParam(value = "id", required = true) Serializable id) {

		SubSeccion item = null;
		try {

			item = mngrSubSeccion.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<SubSeccion>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/subseccion", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) SubSeccion subSeccion) {

		List<?> lst = new ArrayList<SubSeccion>();
		log.debug("PARAMETROS DE BUSQUEDA : " + subSeccion);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (subSeccion.getId() != null)
				restrictions.add(Restrictions.idEq(subSeccion.getId()));

			if (subSeccion.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", subSeccion.getActivo()));

			if (subSeccion.getCodigo() != null)
				restrictions.add(Restrictions.eq("codigo", subSeccion.getCodigo()));

			if (subSeccion.getCodigoDisposicion() != null)
				restrictions.add(Restrictions.eq("codigoDisposicion", subSeccion.getCodigoDisposicion()));

			if (subSeccion.getConcentracion() != null)
				restrictions.add(Restrictions.eq("concentracion", subSeccion.getConcentracion()));

			if (subSeccion.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", subSeccion.getDescripcion(), MatchMode.ANYWHERE));

			if (subSeccion.getDisposicion() != null)
				restrictions.add(Restrictions.eq("disposicion", subSeccion.getDisposicion()));

			if (subSeccion.getIdPadre() != null)
				restrictions.add(Restrictions.eq("idPadre", subSeccion.getIdPadre()));

			if (subSeccion.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", subSeccion.getTipo()));

			if (subSeccion.getTramite() != null)
				restrictions.add(Restrictions.eq("tramite", subSeccion.getTramite()));

			if (subSeccion.getVigencia() != null)
				restrictions.add(Restrictions.eq("vigencia", subSeccion.getVigencia()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrSubSeccion.search(restrictions, orders);

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
	@RequestMapping(value = "/archivistica/subseccion", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("SubSeccion A ELIMINAR >> " + id);

		try {

			mngrSubSeccion.delete(mngrSubSeccion.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/archivistica/subseccion", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<SubSeccion> save(@RequestBody(required = true) SubSeccion subSeccion)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("SubSeccion A GUARDAR >> " + subSeccion);

				if (subSeccion.getId() == null) {
					mngrSubSeccion.save(subSeccion);
					return new ResponseEntity<SubSeccion>(subSeccion, HttpStatus.CREATED);
				} else {
					mngrSubSeccion.update(subSeccion);
					return new ResponseEntity<SubSeccion>(subSeccion, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<SubSeccion>(subSeccion, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
