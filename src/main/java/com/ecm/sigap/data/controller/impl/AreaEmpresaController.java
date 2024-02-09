package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
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
import com.ecm.sigap.data.model.AreaEmpresa;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.AreaEmpresa}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class AreaEmpresaController extends CustomRestController implements RESTController<AreaEmpresa> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AreaEmpresaController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) AreaEmpresa favoritoRemitente) {
		throw new UnsupportedOperationException("Metodo no soportado");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar empresa favoritos", notes = "Agrega un area empresa a favoritos")
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
	@RequestMapping(value = "/areaEmpresa", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<AreaEmpresa> save(@RequestBody(required = true) AreaEmpresa areaEmpresa)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("AREA EMPRESA FAVORITA >> " + areaEmpresa);

				if ((areaEmpresa.getIdArea() != null) && (areaEmpresa.getIdInstitucion() != null)
						&& (areaEmpresa.getIdEmpresa() != null)) {

					// Validamos que las reglas de validacion de la entidad Tipo
					// AreaEmpresa no se esten violando con este nuevo registro
					validateEntity(mngrAreaEmpresa, areaEmpresa);

					mngrAreaEmpresa.save(areaEmpresa);

					return new ResponseEntity<AreaEmpresa>(areaEmpresa, HttpStatus.OK);

				} else {
					return new ResponseEntity<AreaEmpresa>(areaEmpresa, HttpStatus.BAD_REQUEST);
				}

			} else {
				return new ResponseEntity<AreaEmpresa>(areaEmpresa, HttpStatus.BAD_REQUEST);
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
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<AreaEmpresa> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Elimina un registro de la tabla
	 * 
	 * @param idArea
	 *            Identificador del Area
	 * @param idEmpresa
	 *            Identificador de la Empresa
	 * @param idInstitucion
	 *            Identificador Institucion
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar empresa favoritos", notes = "Elimina un area empresa de favoritos")
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
	
	@RequestMapping(value = "/areaEmpresa", method = RequestMethod.DELETE)
	public void delete(@RequestParam(value = "idArea", required = true) Integer idArea,
			@RequestParam(value = "idEmpresa", required = true) Integer idEmpresa,
			@RequestParam(value = "idInstitucion", required = true) Integer idInstitucion) {

		AreaEmpresa asuntoEmpresa = new AreaEmpresa();

		asuntoEmpresa.setIdArea(idArea);
		asuntoEmpresa.setIdEmpresa(idEmpresa);
		asuntoEmpresa.setIdInstitucion(idInstitucion);

		log.debug("ELIMINA AREA EMPRESA FAVORITA >> " + asuntoEmpresa);
		try {

			mngrAreaEmpresa.delete(asuntoEmpresa);

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
	public void delete(Serializable idArea) {
		throw new UnsupportedOperationException();
	}
}
