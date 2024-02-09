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
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.FolioAreaKeyMultiple;
import com.ecm.sigap.data.model.FolioAreaMultilple;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FolioAreaMultilple}
 * 
 * @author ECM Solutions
 * @version 1.0
 *
 */
@RestController
public class FolioAreaMultilpleController extends CustomRestController implements RESTController<FolioAreaMultilple> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FolioAreaMultilpleController.class);

	@Override
	@RequestMapping(value = "/folioAreaMultiple", method = RequestMethod.GET)
	public ResponseEntity<FolioAreaMultilple> get(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@RequestMapping(value = "/folioAreaMultiple", method = RequestMethod.DELETE)
	public void delete(Serializable id) throws Exception {

		log.debug("FOLIOAREA A ELIMINAR >> " + id);

		try {

			List<FolioAreaMultilple> lst = new ArrayList<FolioAreaMultilple>();
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("folioAreaKeyMul.idFoliopsMultiple", Integer.valueOf((String) id)));
			restrictions.add(Restrictions.in("folioAreaKeyMul.idTipoFolio", new Object[] { 0, 1, 2 }));

			lst = (List<FolioAreaMultilple>) mngrFolioAreaMultiple.search(restrictions);

			for (FolioAreaMultilple fma : lst) {
				mngrFolioAreaMultiple.delete(
						new FolioAreaMultilple(new FolioAreaKeyMultiple(fma.getFolioAreaKeyMul().getIdFoliopsMultiple(),
								fma.getFolioAreaKeyMul().getIdTipoFolio()), fma.getFolio(), fma.getVlock()));
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	@Override
	@RequestMapping(value = "/folioAreaMultiple", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(FolioAreaMultilple folioAreaMult) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@RequestMapping(value = "/folioAreaMultiple", method = RequestMethod.PUT)
	public ResponseEntity<FolioAreaMultilple> save(FolioAreaMultilple folioAreaMult) throws Exception {
		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> FOLIO MULTIPLE X AREA A GUARDAR O ACTUALIZAR >> " + folioAreaMult);

				if (folioAreaMult.getFolioAreaKeyMul() != null //
						&& folioAreaMult.getFolioAreaKeyMul().getIdFoliopsMultiple() != null //
						&& folioAreaMult.getFolioAreaKeyMul().getIdTipoFolio() != null) {

					FolioAreaMultilple folioMulAreaTemp = mngrFolioAreaMultiple
							.fetch(folioAreaMult.getFolioAreaKeyMul());

					if (folioMulAreaTemp == null) {

						// Guardar la informacion
						mngrFolioAreaMultiple.save(folioAreaMult);
						log.debug("::>> Registro Folio Multiple x Area Guardado");
						return new ResponseEntity<FolioAreaMultilple>(folioAreaMult, HttpStatus.CREATED);

					} else {

						if (folioMulAreaTemp.getFolio() > folioAreaMult.getFolio())
							throw new BadRequestException(errorMessages.getString("errorRetrazarFolio"));

						// Actualizar la informacion
						mngrFolioAreaMultiple.update(folioAreaMult);
						log.debug("::>> Registro Folio Multiple x Area Actualizado");
						return new ResponseEntity<FolioAreaMultilple>(folioAreaMult, HttpStatus.OK);

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

}
