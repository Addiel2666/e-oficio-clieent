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
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.StatusExpediente;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.StatusExpediente}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class StatusExpedienteController extends CustomRestController implements RESTController<StatusExpediente> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(StatusExpedienteController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/statusExpediente", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<StatusExpediente> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		StatusExpediente item = null;
		try {

			item = mngrStatusExpediente.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);
		return new ResponseEntity<StatusExpediente>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/statusExpediente", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) StatusExpediente statusExpediente) {

		List<?> lst = new ArrayList<StatusExpediente>();
		log.info("Parametros de busqueda :: " + statusExpediente);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (statusExpediente.getId() != null)
				restrictions.add(Restrictions.idEq(statusExpediente.getId()));

			if (statusExpediente.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", statusExpediente.getActivo()));

			if (statusExpediente.getDescripcion() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("descripcion", statusExpediente.getDescripcion(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrStatusExpediente.search(restrictions, orders);

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
	@RequestMapping(value = "/statusExpediente", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<StatusExpediente> save(
			@RequestBody(required = true) StatusExpediente StatusExpediente) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("STATUS EXPEDIENTE A GUARDAR O ACTUALIZAR >> " + StatusExpediente);

				if (StatusExpediente.getId() == null) {

					// Guardamos la informacion
					mngrStatusExpediente.save(StatusExpediente);

					return new ResponseEntity<StatusExpediente>(StatusExpediente, HttpStatus.CREATED);
				} else {
					// Actualizamos la informacion
					mngrStatusExpediente.update(StatusExpediente);

					return new ResponseEntity<StatusExpediente>(StatusExpediente, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<StatusExpediente>(StatusExpediente, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Serializable id) {
		// TODO Auto-generated method stub

	}

}
