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
import com.ecm.sigap.data.model.ExpedienteInfo;
import com.ecm.sigap.data.model.FolioArchivistica;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.ExpedienteInfo}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class ExpedienteController extends CustomRestController implements RESTController<ExpedienteInfo> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ExpedienteController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/expediente", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ExpedienteInfo> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		ExpedienteInfo item = null;
		try {

			item = mngrExpediente.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);
		return new ResponseEntity<ExpedienteInfo>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/expediente", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) ExpedienteInfo expediente) {

		List<?> lst = new ArrayList<ExpedienteInfo>();
		log.debug("PARAMETROS DE BUSQUEDA : " + expediente);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (expediente.getId() != null)
				restrictions.add(Restrictions.idEq(expediente.getId()));

			if (expediente.getOficial() != null)
				restrictions.add(Restrictions.eq("oficial", expediente.getOficial()));

			if (expediente.getArea() != null)
				restrictions.add(Restrictions.eq("area", expediente.getArea()));

			if (expediente.getAsunto() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("asunto", expediente.getAsunto(), MatchMode.ANYWHERE));

			if (expediente.getClasificacionDocumental() != null)
				restrictions.add(Restrictions.eq("clasificacionDocumental", expediente.getClasificacionDocumental()));

			if (expediente.getConsecutivo() != null)
				restrictions.add(Restrictions.eq("consecutivo", expediente.getConsecutivo()));

			if (expediente.getExpediente() != null)
				restrictions.add(Restrictions.eq("expediente", expediente.getExpediente()));

			if (expediente.getFechaApertura() != null)
				restrictions.add(Restrictions.eq("fechaApertura", expediente.getFechaApertura()));

			if (expediente.getStatus() != null)
				restrictions.add(Restrictions.eq("status", expediente.getStatus()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("asunto"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrExpediente.search(restrictions, orders);

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
	@RequestMapping(value = "/expediente", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("Expediente A ELIMINAR >> " + id);

		try {

			mngrExpediente.delete(mngrExpediente.fetch(Integer.valueOf((String) id)));

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
	@RequestMapping(value = "/expediente", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<ExpedienteInfo> save(@RequestBody(required = true) ExpedienteInfo expediente)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {
				
				log.debug("Expediente A GUARDAR >> " + expediente);
				if (expediente.getId() == null) {

					FolioArchivistica folio = mngrFolioArchivistica.fetch(expediente.getArea().getIdArea());

					expediente.setConsecutivo(folio.getPrefijo() + folio.getFolio() + folio.getSufijo());

					mngrExpediente.save(expediente);

					folio.setFolio(folio.getFolio() + 1);

					mngrFolioArchivistica.update(folio);

					return new ResponseEntity<ExpedienteInfo>(expediente, HttpStatus.CREATED);
				} else {
					mngrExpediente.update(expediente);
					return new ResponseEntity<ExpedienteInfo>(expediente, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<ExpedienteInfo>(expediente, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
}
