/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ecm.sigap.data.controller.HeaderValueNames;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.FolioClave;
import com.ecm.sigap.data.model.FolioKey;

import javax.ws.rs.BadRequestException;

/**
 * Controladores REST para manejo de elementos tipo {@link FolioClave}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class FolioClaveController extends CustomRestController implements RESTController<FolioClave> {

    /**
     * Log de suscesos.
     */
    private static final Logger log = LogManager.getLogger(FolioClaveController.class);

    /**
     * @param idArea
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/folio/clave/disponible", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<FolioClave> getFolioDisponible(
            @RequestParam(value = "idArea", required = true) Serializable idArea) {

        try {

            FolioClave folio = new FolioClave();
            FolioKey folioKey = new FolioKey();

            ProjectionList projections = Projections.projectionList();
            List<Criterion> restrictions = new ArrayList<>();


            projections.add(Projections.min("folioKey.folio").as("folio"));

            restrictions.add(Restrictions.eq("folioKey.idArea", Integer.valueOf((String) idArea)));
            restrictions.add(Restrictions.eq("vlock", "D"));


            Map<String, Integer> map = (Map<String, Integer>) mngrFolioClave
                    .search(restrictions, null, projections, null, null).get(0);

            if (map != null && !map.isEmpty()) {
                folioKey.setFolio(map.get("folio"));
                folioKey.setIdArea(Integer.valueOf((String) idArea));
                folio.setFolioKey(folioKey);
                folio.setVlock("D");

            }

            return new ResponseEntity<FolioClave>(folio, HttpStatus.OK);

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
    public ResponseEntity<FolioClave> get(Serializable id) {
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
    public ResponseEntity<List<?>> search(FolioClave object) throws Exception {
        throw new NotImplementedException();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
     */
    @Override
    public @ResponseBody
    ResponseEntity<FolioClave> save(@RequestBody(required = true) FolioClave folioClave)
            throws Exception {

        try {
            String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
            if (!esSoloLectura(userId)) {

                log.debug("::>> FOLIOAREA A GUARDAR O ACTUALIZAR >> " + folioClave);

                if (folioClave.getFolioKey() != null //
                        && folioClave.getFolioKey().getIdArea() != null //
                        && folioClave.getFolioKey().getFolio() != null) {

                    FolioClave folioClaveTemp = mngrFolioClave.fetch(folioClave.getFolioKey());


                    if (folioClaveTemp == null) {
                        folioClave.setVlock("D");
                        // Guardamos la informacion
                        mngrFolioClave.save(folioClave);
                        log.debug("::>> Registro folioArea Guardado");
                        return new ResponseEntity<FolioClave>(folioClave, HttpStatus.CREATED);

                    } else {

                        if (folioClaveTemp.getFolioKey().getFolio() > folioClave.getFolioKey().getFolio())
                            throw new BadRequestException(errorMessages.getString("errorRetrazarFolio"));

                        // Actualizamos la informacion
                        mngrFolioClave.update(folioClave);
                        log.debug("::>> Registro folioArea Actualizado");
                        return new ResponseEntity<FolioClave>(folioClave, HttpStatus.OK);

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
