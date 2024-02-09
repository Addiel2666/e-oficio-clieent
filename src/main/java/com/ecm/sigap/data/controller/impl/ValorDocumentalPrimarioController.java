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
import com.ecm.sigap.data.model.ValorDocumentalPrimario;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.ValorDocumentalPrimario}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class ValorDocumentalPrimarioController extends CustomRestController
		implements RESTController<ValorDocumentalPrimario> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ValorDocumentalPrimarioController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/valorDocumentalPrimario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ValorDocumentalPrimario> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		ValorDocumentalPrimario item = null;
		try {

			item = mngrValorDocumentalPrimario.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<ValorDocumentalPrimario>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/valorDocumentalPrimario", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(
			@RequestBody(required = true) ValorDocumentalPrimario valorDocumentalPrimario) {

		List<?> lst = new ArrayList<ValorDocumentalPrimario>();
		log.debug("PARAMETROS DE BUSQUEDA : " + valorDocumentalPrimario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			List<Order> orders = new ArrayList<Order>();

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrValorDocumentalPrimario.search(restrictions, orders);

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
	@RequestMapping(value = "/valorDocumentalPrimario", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("ValorDocumentalPrimario A ELIMINAR >> " + id);

		try {

			mngrValorDocumentalPrimario.delete(mngrValorDocumentalPrimario.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/valorDocumentalPrimario", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<ValorDocumentalPrimario> save(
			@RequestBody(required = true) ValorDocumentalPrimario valorDocumentalPrimario) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("ValorDocumentalPrimario A GUARDAR >> " + valorDocumentalPrimario);

				if (valorDocumentalPrimario.getId() == null) {
					mngrValorDocumentalPrimario.save(valorDocumentalPrimario);
					return new ResponseEntity<ValorDocumentalPrimario>(valorDocumentalPrimario, HttpStatus.CREATED);
				} else {
					mngrValorDocumentalPrimario.update(valorDocumentalPrimario);
					return new ResponseEntity<ValorDocumentalPrimario>(valorDocumentalPrimario, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<ValorDocumentalPrimario>(valorDocumentalPrimario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
}
