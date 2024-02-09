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
import com.ecm.sigap.data.model.interop.InstitucionOpe;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.InstitucionOpe}
 *
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class InstitucionOpeController extends CustomRestController implements RESTController<InstitucionOpe> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(InstitucionOpeController.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/institucionOpe", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InstitucionOpe> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		InstitucionOpe item = null;
		try {

			item = mngrInstitucionOpe.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
		}

		log.debug(" Data Out >> " + item);

		return new ResponseEntity<InstitucionOpe>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/institucionOpe", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public @ResponseBody void delete(@RequestParam(value = "id", required = true) Serializable id) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/institucionOpe", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<InstitucionOpe> save(@RequestBody InstitucionOpe institucionOpe)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("INSTITUCIONOPE A GUARDAR >> " + institucionOpe);

				if (institucionOpe.getIdInstitucionOpe() == null) {

					// Validamos que las reglas de validacion de la entidad
					// Tipo InstitucionOpe no se esten violando con este nuevo
					// registro
					validateEntity(mngrInstitucionOpe, institucionOpe);
					mngrInstitucionOpe.save(institucionOpe);
					return new ResponseEntity<InstitucionOpe>(institucionOpe, HttpStatus.CREATED);
				} else {

					InstitucionOpe oldInst = mngrInstitucionOpe.fetch(institucionOpe.getIdInstitucionOpe());

					if (!institucionOpe.getNombre().equals(oldInst.getNombre())) {

						List<Criterion> restrictions = new ArrayList<Criterion>();

						if (institucionOpe.getNombre() != null)
							restrictions.add(Restrictions.eq("nombre", institucionOpe.getNombre()));

						List<Order> orders = new ArrayList<Order>();
						orders.add(Order.asc("nombre"));

						List<?> lst = new ArrayList<InstitucionOpe>();
						lst = mngrInstitucionOpe.search(restrictions, orders);

						if (!lst.isEmpty()) {
							throw new Exception("Esta institucionOpe ya existe");
						}
					}

					mngrInstitucionOpe.update(institucionOpe);
					return new ResponseEntity<InstitucionOpe>(institucionOpe, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<InstitucionOpe>(institucionOpe, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/institucionOpe", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) InstitucionOpe institucionOpe) {

		List<?> lst = new ArrayList<InstitucionOpe>();
		log.debug("PARAMETROS DE BUSQUEDA :: " + institucionOpe);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (institucionOpe.getIdInstitucionOpe() != null)
				restrictions.add(Restrictions.idEq(institucionOpe.getIdInstitucionOpe()));

			if (institucionOpe.getNombre() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("nombre", institucionOpe.getNombre(), MatchMode.ANYWHERE));

			if (institucionOpe.getNombreCorto() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCorto", institucionOpe.getNombreCorto(),
						MatchMode.ANYWHERE));

			if (institucionOpe.getEndpoint() != null)
				restrictions.add(
						EscapedLikeRestrictions.ilike("endpoint", institucionOpe.getEndpoint(), MatchMode.ANYWHERE));

			if (institucionOpe.getUri() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("uri", institucionOpe.getUri(), MatchMode.ANYWHERE));

			if (institucionOpe.getEstatus() != null)
				restrictions.add(Restrictions.eq("estatus", institucionOpe.getEstatus()));

			if (institucionOpe.getIdMensajeSuscripcion() != null)
				restrictions.add(Restrictions.eq("idMensajeSuscripcion", institucionOpe.getIdMensajeSuscripcion()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("nombre"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrInstitucionOpe.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

}
