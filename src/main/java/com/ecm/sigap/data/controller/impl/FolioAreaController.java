/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
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
import com.ecm.sigap.data.model.FolioArea;
import com.ecm.sigap.data.model.FolioAreaKey;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FolioArea}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class FolioAreaController extends CustomRestController implements RESTController<FolioArea> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FolioAreaController.class);

	/**
	 * Gets the.
	 *
	 * @param idArea
	 *            the id area
	 * @param idTipoFolio
	 *            the id tipo folio
	 * @return the response entity
	 */
	@RequestMapping(value = "/folioArea", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<FolioArea> get(
			@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idTipoFolio", required = true) Serializable idTipoFolio) {

		FolioAreaKey folioAreaKey = new FolioAreaKey();
		folioAreaKey.setIdArea(Integer.valueOf((String) idArea));
		folioAreaKey.setIdTipoFolio(Integer.valueOf((String) idTipoFolio));

		FolioArea item = null;

		try {

			item = mngrFolioArea.fetch(folioAreaKey);

			log.debug("::: Informacion del Folio de retorno " + item);

			return new ResponseEntity<FolioArea>(item, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());
			return new ResponseEntity<FolioArea>(item, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/folioArea", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) FolioArea folioArea) throws Exception {

		List<?> lst = new ArrayList<FolioArea>();
		log.info("Parametros de busqueda :: " + folioArea);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (folioArea.getFolioAreaKey() != null) {
				if (folioArea.getFolioAreaKey().getIdArea() != null)
					restrictions.add(Restrictions.eq("folioAreaKey.idArea", folioArea.getFolioAreaKey().getIdArea()));
				if (folioArea.getFolioAreaKey().getIdTipoFolio() != null)
					restrictions.add(
							Restrictions.eq("folioAreaKey.idTipoFolio", folioArea.getFolioAreaKey().getIdTipoFolio()));
			}

			if (folioArea.getVlock() != null && !folioArea.getVlock().isEmpty())
				restrictions.add(Restrictions.eq("vlock", folioArea.getVlock()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("idArea"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFolioArea.search(restrictions, orders);

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
	@RequestMapping(value = "/folioArea", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<FolioArea> save(@RequestBody(required = true) FolioArea folioArea)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> FOLIOAREA A GUARDAR O ACTUALIZAR >> " + folioArea);

				if (folioArea.getFolioAreaKey() != null //
						&& folioArea.getFolioAreaKey().getIdArea() != null //
						&& folioArea.getFolioAreaKey().getIdTipoFolio() != null) {

					FolioArea folioAreaTemp = mngrFolioArea.fetch(folioArea.getFolioAreaKey());

					if (folioAreaTemp == null) {

						// Guardamos la informacion
						mngrFolioArea.save(folioArea);
						log.debug("::>> Registro folioArea Guardado");
						return new ResponseEntity<FolioArea>(folioArea, HttpStatus.CREATED);

					} else {

						if (folioAreaTemp.getFolio() > folioArea.getFolio())
							throw new BadRequestException(errorMessages.getString("errorRetrazarFolio"));

						// Actualizamos la informacion
						mngrFolioArea.update(folioArea);
						log.debug("::>> Registro folioArea Actualizado");
						return new ResponseEntity<FolioArea>(folioArea, HttpStatus.OK);

					}

				} else {

					throw new BadRequestException();

				}

			} else {

				throw new BadRequestException();

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
	@RequestMapping(value = "/folioArea", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(Serializable id) {

		log.debug("FOLIO A ELIMINAR >> " + id);

		try {

			mngrFolioArea.delete(mngrFolioArea.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<FolioArea> get(Serializable id) {
		throw new UnsupportedOperationException();
	}
}
