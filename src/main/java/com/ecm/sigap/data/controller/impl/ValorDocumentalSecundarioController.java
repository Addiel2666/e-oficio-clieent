/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
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
import com.ecm.sigap.data.model.ValorDocumentalSecundario;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.ValorDocumentalSecundario}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class ValorDocumentalSecundarioController extends CustomRestController
		implements RESTController<ValorDocumentalSecundario> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ValorDocumentalSecundarioController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/valorDocumentalSecundario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ValorDocumentalSecundario> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		ValorDocumentalSecundario item = null;
		try {

			item = mngrValorDocumentalSecundario.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<ValorDocumentalSecundario>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/valorDocumentalSecundario", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(
			@RequestBody(required = true) ValorDocumentalSecundario valorDocumentalSecundario) {

		List<?> lst = new ArrayList<ValorDocumentalSecundario>();
		log.debug("PARAMETROS DE BUSQUEDA : " + valorDocumentalSecundario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			List<Order> orders = new ArrayList<Order>();

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrValorDocumentalSecundario.search(restrictions, orders);

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
	@RequestMapping(value = "/valorDocumentalSecundario", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("ValorDocumentalSecundario A ELIMINAR >> " + id);

		try {

			mngrValorDocumentalSecundario.delete(mngrValorDocumentalSecundario.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/valorDocumentalSecundario", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<ValorDocumentalSecundario> save(
			@RequestBody(required = true) ValorDocumentalSecundario valorDocumentalSecundario) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("ValorDocumentalSecundario A GUARDAR >> " + valorDocumentalSecundario);

				if (valorDocumentalSecundario.getId() == null) {
					mngrValorDocumentalSecundario.save(valorDocumentalSecundario);
					return new ResponseEntity<ValorDocumentalSecundario>(valorDocumentalSecundario, HttpStatus.CREATED);
				} else {
					mngrValorDocumentalSecundario.update(valorDocumentalSecundario);
					return new ResponseEntity<ValorDocumentalSecundario>(valorDocumentalSecundario, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<ValorDocumentalSecundario>(valorDocumentalSecundario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
}
