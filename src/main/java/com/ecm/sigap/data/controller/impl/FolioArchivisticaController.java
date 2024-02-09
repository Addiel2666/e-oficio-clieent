/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.springframework.http.HttpStatus;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.model.FolioArchivistica;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FolioArchivistica}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class FolioArchivisticaController extends CustomRestController implements RESTController<FolioArchivistica> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FolioArchivisticaController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/folioArchivistica", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<FolioArchivistica> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		FolioArchivistica item = null;
		try {

			item = mngrFolioArchivistica.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<FolioArchivistica>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/folioArchivistica", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(
			@RequestBody(required = true) FolioArchivistica folioArchivistica) {

		List<?> lst = new ArrayList<FolioArchivistica>();
		log.info("Parametros de busqueda :: " + folioArchivistica);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (folioArchivistica.getFolio() != null)
				restrictions.add(Restrictions.eq("folio", folioArchivistica.getFolio()));

			if (folioArchivistica.getPrefijo() != null && !folioArchivistica.getPrefijo().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("prefijo", folioArchivistica.getPrefijo(), MatchMode.ANYWHERE));

			if (folioArchivistica.getSufijo() != null && !folioArchivistica.getSufijo().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("sufijo", folioArchivistica.getSufijo(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("idArea"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFolioArchivistica.search(restrictions, orders);

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
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/folioArchivistica", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("FOLIOARCHIVISTICA A ELIMINAR >> " + id);

		try {
			mngrFolioArchivistica.delete(mngrFolioArchivistica.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/folioArchivistica", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<FolioArchivistica> save(
			@RequestBody(required = true) FolioArchivistica folioArchivistica) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("FOLIOARCHIVISTICA A GUARDAR >> " + folioArchivistica);
				
				if (folioArchivistica.getIdArea() != null && folioArchivistica.getFolio() != null) {
					FolioArchivistica folioArchivisticaTemp = mngrFolioArchivistica
							.fetch(folioArchivistica.getIdArea());

					if (folioArchivisticaTemp == null) {
						mngrFolioArchivistica.save(folioArchivistica);
						return new ResponseEntity<FolioArchivistica>(folioArchivistica, HttpStatus.CREATED);
					} else {
						mngrFolioArchivistica.update(folioArchivistica);
						return new ResponseEntity<FolioArchivistica>(folioArchivistica, HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<FolioArchivistica>(folioArchivistica, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<FolioArchivistica>(folioArchivistica, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

}