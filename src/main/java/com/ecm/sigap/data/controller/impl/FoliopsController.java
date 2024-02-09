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
import com.ecm.sigap.data.model.FolioPS;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FolioPS}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class FoliopsController extends CustomRestController implements RESTController<FolioPS> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FoliopsController.class);

	@Override
	@RequestMapping(value = "/foliops", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<FolioPS> get(@RequestParam(value = "id", required = true) Serializable id) {

		FolioPS item = null;

		try {

			item = mngrFoliops.fetch(Integer.valueOf((String) id));

			log.debug("::: Informacion del Folio de retorno " + item);

			return new ResponseEntity<FolioPS>(item, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());
			return new ResponseEntity<FolioPS>(item, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/foliops", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(Serializable id) {

		log.debug("FOLIO A ELIMINAR >> " + id);

		try {

			mngrFoliops.delete(mngrFoliops.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/foliops", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) FolioPS folioPS) throws Exception {

		List<?> lst = new ArrayList<FolioPS>();
		log.info("Parametros de busqueda :: " + folioPS);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (folioPS.getIdArea() != null)
				restrictions.add(Restrictions.idEq(folioPS.getIdArea()));

			if ((folioPS.getPrefijoFolio() != null) && (!folioPS.getPrefijoFolio().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("prefijoFolio", folioPS.getPrefijoFolio(), MatchMode.ANYWHERE));

			if ((folioPS.getSufijoFolio() != null) && (!folioPS.getSufijoFolio().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("sufijoFolio", folioPS.getSufijoFolio(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("idArea"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFoliops.search(restrictions, orders);

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
	@RequestMapping(value = "/foliops", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<FolioPS> save(@RequestBody(required = true) FolioPS folioPS) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> FOLIOPS A GUARDAR O ACTUALIZAR >> " + folioPS);

				if (folioPS.getIdArea() != null) {

					FolioPS folioTemp = mngrFoliops.fetch(folioPS.getIdArea());

					if (folioTemp == null) {
						// Guardamos la informacion
						mngrFoliops.save(folioPS);
						log.debug("::>> Registro Guardado");
						return new ResponseEntity<FolioPS>(folioPS, HttpStatus.CREATED);
					} else {
						// Actualizamos la informacion
						mngrFoliops.update(folioPS);
						log.debug("::>> Registro Actualizado");
						return new ResponseEntity<FolioPS>(folioPS, HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<FolioPS>(folioPS, HttpStatus.BAD_REQUEST);
				}

			} else {
				return new ResponseEntity<FolioPS>(folioPS, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}
}
