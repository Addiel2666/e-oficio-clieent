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
import com.ecm.sigap.data.model.SubFondo;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.SubFondo}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class SubFondoController extends CustomRestController //
		implements RESTController<SubFondo> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(SubFondoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/archivistica/subfondo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<SubFondo> get(@RequestParam(value = "id", required = true) Serializable id) {

		SubFondo item = null;
		try {

			item = mngrSubFondo.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<SubFondo>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/archivistica/subfondo", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) SubFondo subFondo) {

		List<?> lst = new ArrayList<SubFondo>();
		log.debug("PARAMETROS DE BUSQUEDA : " + subFondo);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (subFondo.getId() != null)
				restrictions.add(Restrictions.idEq(subFondo.getId()));

			if (subFondo.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", subFondo.getActivo()));

			if (subFondo.getCodigo() != null)
				restrictions.add(Restrictions.eq("codigo", subFondo.getCodigo()));

			if (subFondo.getCodigoDisposicion() != null)
				restrictions.add(Restrictions.eq("codigoDisposicion", subFondo.getCodigoDisposicion()));

			if (subFondo.getConcentracion() != null)
				restrictions.add(Restrictions.eq("concentracion", subFondo.getConcentracion()));

			if (subFondo.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", subFondo.getDescripcion(), MatchMode.ANYWHERE));

			if (subFondo.getDisposicion() != null)
				restrictions.add(Restrictions.eq("disposicion", subFondo.getDisposicion()));

			if (subFondo.getIdPadre() != null)
				restrictions.add(Restrictions.eq("idPadre", subFondo.getIdPadre()));

			if (subFondo.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", subFondo.getTipo()));

			if (subFondo.getTramite() != null)
				restrictions.add(Restrictions.eq("tramite", subFondo.getTramite()));

			if (subFondo.getVigencia() != null)
				restrictions.add(Restrictions.eq("vigencia", subFondo.getVigencia()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrSubFondo.search(restrictions, orders);

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
	@RequestMapping(value = "/archivistica/subfondo", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("SubFondo A ELIMINAR >> " + id);

		try {

			mngrSubFondo.delete(mngrSubFondo.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/archivistica/subfondo", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<SubFondo> save(@RequestBody(required = true) SubFondo subFondo)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("SubFondo A GUARDAR >> " + subFondo);

				if (subFondo.getId() == null) {
					mngrSubFondo.save(subFondo);
					return new ResponseEntity<SubFondo>(subFondo, HttpStatus.CREATED);
				} else {
					mngrSubFondo.update(subFondo);
					return new ResponseEntity<SubFondo>(subFondo, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<SubFondo>(subFondo, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

}
