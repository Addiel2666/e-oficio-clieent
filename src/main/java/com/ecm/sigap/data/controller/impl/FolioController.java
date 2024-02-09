/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.springframework.http.HttpStatus;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.ProjectionList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.model.Folio;
import com.ecm.sigap.data.model.FolioKey;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.CustomRestController;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Folio}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class FolioController extends CustomRestController implements RESTController<Folio> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FolioController.class);

	/**
	 * 
	 * @param idArea
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/folio/disponible", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Folio> getFolioDisponible(
			@RequestParam(value = "idArea", required = true) Serializable idArea) {

		try {

			Folio folio = new Folio();
			FolioKey folioKey = new FolioKey();

			ProjectionList projections = Projections.projectionList();
			List<Criterion> restrictions = new ArrayList<>();

			projections.add(Projections.min("folioKey.folio").as("folio"));

			restrictions.add(Restrictions.eq("folioKey.idArea", Integer.valueOf((String) idArea)));
			restrictions.add(Restrictions.eq("vlock", "D"));

			Map<String, Integer> map = (Map<String, Integer>) mngrFolio
					.search(restrictions, null, projections, null, null).get(0);

			if (map != null && !map.isEmpty()) {

				folioKey.setFolio(map.get("folio"));
				folioKey.setIdArea(Integer.valueOf((String) idArea));
				folio.setFolioKey(folioKey);
				folio.setVlock("D");

			}

			return new ResponseEntity<Folio>(folio, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<Folio> get(Serializable id) {
		throw new NotImplementedException();
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
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(Folio object) throws Exception {
		throw new NotImplementedException();
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	public ResponseEntity<Folio> save(Folio object) throws Exception {
		throw new NotImplementedException();
	}

}
