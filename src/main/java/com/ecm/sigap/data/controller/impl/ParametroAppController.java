/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
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
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroApp;
import com.ecm.sigap.data.model.ParametroAppPK;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.ParametroApp}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class ParametroAppController extends CustomRestController implements RESTController<ParametroApp> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ParametroAppController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<ParametroApp> get(Serializable id) {
		throw new NotImplementedException();
	}

	/**
	 * 
	 * @param idClave
	 * @param idSeccion
	 * @return
	 */
	@RequestMapping(value = "/parametroApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ParametroApp> get(
			@RequestParam(value = "idClave", required = true) String idClave,
			@RequestParam(value = "idSeccion", required = true) String idSeccion) {

		try {
			ParametroAppPK pk = new ParametroAppPK();

			pk.setIdClave(idClave);
			pk.setIdSeccion(idSeccion);

			ParametroApp item = mngrParamApp.fetch(pk);

			log.debug(" Item Out >> " + item);

			return new ResponseEntity<ParametroApp>(item, HttpStatus.OK);

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
	public void delete(Serializable id) throws Exception {
		throw new NotImplementedException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/parametroApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) ParametroApp parametroApp)
			throws Exception {

		List<?> lst = new ArrayList<Parametro>();
		log.info("Parametros de busqueda :: " + parametroApp);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (!StringUtils.isBlank(parametroApp.getIdClave()))
				restrictions.add(Restrictions.eq("idClave", parametroApp.getIdClave()));

			if (!StringUtils.isBlank(parametroApp.getIdSeccion()))
				restrictions.add(Restrictions.eq("idSeccion", parametroApp.getIdClave()));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrParametro.search(restrictions, null);

			log.debug("Size found >> " + lst.size());

			return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/parametroApp", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<ParametroApp> save(@RequestBody(required = true) ParametroApp parametroApp)
			throws Exception {

		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

		ResponseEntity<ParametroApp> response = get(parametroApp.getIdClave(), parametroApp.getIdSeccion());

		ParametroApp exist = response.getBody();

		if (!esSoloLectura(userId)) {

			if (exist != null) {

				mngrParamApp.update(parametroApp);

			} else {

				mngrParamApp.save(parametroApp);

			}

			return new ResponseEntity<ParametroApp>(parametroApp, HttpStatus.OK);

		} else {

			return new ResponseEntity<ParametroApp>(parametroApp, HttpStatus.UNAUTHORIZED);

		}

	}

}