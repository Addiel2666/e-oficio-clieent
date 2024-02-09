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
import com.ecm.sigap.data.model.Fondo;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Fondo}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class FondoController extends CustomRestController //
		implements RESTController<Fondo> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FondoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/fondo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Fondo> get(@RequestParam(value = "id", required = true) Serializable id) {

		Fondo item = null;
		try {

			item = mngrFondo.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Fondo>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/fondo", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Fondo fondo) {

		List<?> lst = new ArrayList<Fondo>();
		log.debug("PARAMETROS DE BUSQUEDA : " + fondo);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (fondo.getId() != null)
				restrictions.add(Restrictions.idEq(fondo.getId()));

			if (fondo.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", fondo.getActivo()));

			if (fondo.getCodigo() != null)
				restrictions.add(Restrictions.eq("codigo", fondo.getCodigo()));

			if (fondo.getCodigoDisposicion() != null)
				restrictions.add(Restrictions.eq("codigoDisposicion", fondo.getCodigoDisposicion()));

			if (fondo.getConcentracion() != null)
				restrictions.add(Restrictions.eq("concentracion", fondo.getConcentracion()));

			if (fondo.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", fondo.getDescripcion(), MatchMode.ANYWHERE));

			if (fondo.getDisposicion() != null)
				restrictions.add(Restrictions.eq("disposicion", fondo.getDisposicion()));

			if (fondo.getIdPadre() != null)
				restrictions.add(Restrictions.eq("idPadre", fondo.getIdPadre()));

			if (fondo.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", fondo.getTipo()));

			if (fondo.getTramite() != null)
				restrictions.add(Restrictions.eq("tramite", fondo.getTramite()));

			if (fondo.getVigencia() != null)
				restrictions.add(Restrictions.eq("vigencia", fondo.getVigencia()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFondo.search(restrictions, orders);

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
	@RequestMapping(value = "/archivistica/fondo", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("Fondo A ELIMINAR >> " + id);

		try {

			mngrFondo.delete(mngrFondo.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/archivistica/fondo", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Fondo> save(@RequestBody(required = true) Fondo fondo) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("Fondo A GUARDAR >> " + fondo);

				if (fondo.getId() == null) {
					mngrFondo.save(fondo);
					return new ResponseEntity<Fondo>(fondo, HttpStatus.CREATED);
				} else {
					mngrFondo.update(fondo);
					return new ResponseEntity<Fondo>(fondo, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<Fondo>(fondo, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
