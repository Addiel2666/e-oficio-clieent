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
import com.ecm.sigap.data.model.Seccion;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Seccion}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class SeccionController extends CustomRestController //
		implements RESTController<Seccion> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(SeccionController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/seccion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Seccion> get(@RequestParam(value = "id", required = true) Serializable id) {

		Seccion item = null;
		try {

			item = mngrSeccion.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Seccion>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/seccion", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Seccion seccion) {

		List<?> lst = new ArrayList<Seccion>();
		log.debug("PARAMETROS DE BUSQUEDA : " + seccion);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (seccion.getId() != null)
				restrictions.add(Restrictions.idEq(seccion.getId()));

			if (seccion.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", seccion.getActivo()));

			if (seccion.getCodigo() != null)
				restrictions.add(Restrictions.eq("codigo", seccion.getCodigo()));

			if (seccion.getCodigoDisposicion() != null)
				restrictions.add(Restrictions.eq("codigoDisposicion", seccion.getCodigoDisposicion()));

			if (seccion.getConcentracion() != null)
				restrictions.add(Restrictions.eq("concentracion", seccion.getConcentracion()));

			if (seccion.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", seccion.getDescripcion(), MatchMode.ANYWHERE));

			if (seccion.getDisposicion() != null)
				restrictions.add(Restrictions.eq("disposicion", seccion.getDisposicion()));

			if (seccion.getIdPadre() != null)
				restrictions.add(Restrictions.eq("idPadre", seccion.getIdPadre()));

			if (seccion.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", seccion.getTipo()));

			if (seccion.getTramite() != null)
				restrictions.add(Restrictions.eq("tramite", seccion.getTramite()));

			if (seccion.getVigencia() != null)
				restrictions.add(Restrictions.eq("vigencia", seccion.getVigencia()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrSeccion.search(restrictions, orders);

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
	@RequestMapping(value = "/archivistica/seccion", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("Seccion A ELIMINAR >> " + id);

		try {

			mngrSeccion.delete(mngrSeccion.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/archivistica/seccion", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Seccion> save(@RequestBody(required = true) Seccion seccion) throws Exception {


		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {		
			
			log.debug("Seccion A GUARDAR >> " + seccion);

			if (seccion.getId() == null) {
				mngrSeccion.save(seccion);
				return new ResponseEntity<Seccion>(seccion, HttpStatus.CREATED);
			} else {
				mngrSeccion.update(seccion);
				return new ResponseEntity<Seccion>(seccion, HttpStatus.OK);
			}
			} else {
				return new ResponseEntity<Seccion>(seccion, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
