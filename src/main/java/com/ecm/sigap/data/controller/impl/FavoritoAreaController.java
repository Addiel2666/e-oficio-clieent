/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.*;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link FavoritoArea}
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@RestController
public class FavoritoAreaController extends CustomRestController implements RESTController<FavoritoArea> {

    /**
     * Log de suscesos.
     */
    private static final Logger log = LogManager.getLogger(FavoritoAreaController.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
     */
    
    /*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta areas favoritas", notes = "Consulta la lista de areas favoritas")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
    
    @Override
    @RequestMapping(value = "/favoritoArea", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<List<?>> search(
            @RequestBody(required = true) FavoritoArea favoritoArea) {
        List<?> lst = new ArrayList<FavoritoArea>();
        List<?> lstareaobj = new ArrayList<>();

        log.info("Parametros de busqueda :: " + favoritoArea);

        try {

            // * * * * * * * * * * * * * * * * * * * * * *
            List<Criterion> restrictions = new ArrayList<Criterion>();

            if (favoritoArea.getFavoritoAreaKey() != null) {

                if (favoritoArea.getFavoritoAreaKey().getIdArea() != null)
                    restrictions.add(Restrictions.eq("favoritoAreaKey.idArea",
                            favoritoArea.getFavoritoAreaKey().getIdArea()));

                if (favoritoArea.getFavoritoAreaKey().getIdAreaFavorita() != null)
                    restrictions.add(Restrictions.eq("favoritoAreaKey.idAreaFavorita",
                            favoritoArea.getFavoritoAreaKey().getIdAreaFavorita()));

            }

            List<Order> orders = new ArrayList<Order>();

            // orders.add(Order.asc("favoritoAreaKey.idArea"));

            // * * * * * * * * * * * * * * * * * * * * * *
            lst = mngrFavoritoArea.search(restrictions, orders);
            List<Integer> listareas = new ArrayList<>();
            for (Object favarea : lst) {
                if (favarea instanceof FavoritoArea) {
                    listareas.add(((FavoritoArea) favarea).getFavoritoAreaKey().getIdAreaFavorita());
                }
            }
            if (!listareas.isEmpty()) {
                List<Criterion> restrictionsarea = new ArrayList<Criterion>();

                restrictionsarea.add(Restrictions.eq("activo", true));
                restrictionsarea.add(Restrictions.in("idArea", listareas));

                lstareaobj = mngrArea.search(restrictionsarea);
            }
            log.debug("Size found >> " + lstareaobj.size());

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            
            throw e;
        }


        return new ResponseEntity<List<?>>(lstareaobj, HttpStatus.OK);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
     */
	
	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Agregar area favoritos", notes = "Agrega un area a la lista de favoritos")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 201, message = "Creado"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
    @Override
    @RequestMapping(value = "/favoritoArea", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<FavoritoArea> save(
            @RequestBody(required = true) FavoritoArea favoritoArea) throws Exception {
        try {

            String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
            if (!esSoloLectura(userId)) {

                log.debug("FAVORITO A GUARDAR >> " + favoritoArea);

                if (favoritoArea.getFavoritoAreaKey() != null //
                        && (favoritoArea.getFavoritoAreaKey().getIdArea() != null)
                        && (favoritoArea.getFavoritoAreaKey().getIdAreaFavorita() != null)) {

                    // Validamos que las reglas de validacion de la entidad Tipo
                    // FavoritoArea no se esten violando con este nuevo
                    // registro
                    validateEntity(mngrFavoritoArea, favoritoArea);

                    // Guardamos la informacion
                    mngrFavoritoArea.save(favoritoArea);

                    return new ResponseEntity<FavoritoArea>(favoritoArea, HttpStatus.OK);

                } else {
                    return new ResponseEntity<FavoritoArea>(favoritoArea, HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<FavoritoArea>(favoritoArea, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            
            throw e;
        }

    }

    /**
     * Delete.
     *
     * @param favoritoArea the favorito Area
     */
	
	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Eliminar area favoritos", notes = "Elimina una area de la lista de favoritos")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
    @RequestMapping(value = "/favoritoArea", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@RequestParam(value = "idArea", required = true) Serializable idArea,
                       @RequestParam(value = "idAreaFavorita", required = true) Serializable idAreaFavorita) {
        try {
            FavoritoAreaKey favoritoAreaKey = new FavoritoAreaKey();
            favoritoAreaKey.setIdArea(Integer.valueOf((String) idArea));
            favoritoAreaKey.setIdAreaFavorita(Integer.valueOf((String) idAreaFavorita));

            log.debug("FAVORITO A ELIMINAR >> " + favoritoAreaKey);

            if ((favoritoAreaKey.getIdArea() != null) && (favoritoAreaKey.getIdAreaFavorita() != null)) {

                mngrFavoritoArea.delete(mngrFavoritoArea.fetch(favoritoAreaKey));
            }
            log.debug("DELETE! ");

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
    public ResponseEntity<FavoritoArea> get(Serializable id) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
     */
    @Override
    public void delete(Serializable id) {
        throw new UnsupportedOperationException();

    }

}
