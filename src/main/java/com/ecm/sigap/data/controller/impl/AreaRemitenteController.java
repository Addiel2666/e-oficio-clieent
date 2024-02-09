package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.AreaRemitente;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.AreaRemitente}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class AreaRemitenteController extends CustomRestController implements RESTController<AreaRemitente> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AreaRemitenteController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) AreaRemitente favoritoRemitente) {
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
	
	@ApiOperation(value = "Agregar area favoritos", notes = "Agrega un area remitente a favoritos")
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
	@RequestMapping(value = "/areaRemitente", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<AreaRemitente> save(@RequestBody(required = true) AreaRemitente areaRemitente)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("AREA FAVORITA >> " + areaRemitente);

				if ((areaRemitente.getAreaRemitenteKey().getIdArea() != null)
						&& (areaRemitente.getAreaRemitenteKey().getIdInstitucion() != null)
						&& (areaRemitente.getAreaRemitenteKey().getAreaRemitente() != null)
						&& areaRemitente.getAreaRemitenteKey().getAreaRemitente().getIdArea() != null) {

					countMaxFavoritos(areaRemitente.getAreaRemitenteKey().getIdArea());

					areaRemitente.getAreaRemitenteKey().setAreaRemitente(
							mngrArea.fetch(areaRemitente.getAreaRemitenteKey().getAreaRemitente().getIdArea()));

					// Validamos que las reglas de validacion de la entidad Tipo
					// AreaRemitente no se esten violando con este nuevo
					// registro
					validateEntity(mngrAreaRemitente, areaRemitente);

					mngrAreaRemitente.save(areaRemitente);

					return new ResponseEntity<AreaRemitente>(areaRemitente, HttpStatus.OK);

				} else {
					return new ResponseEntity<AreaRemitente>(areaRemitente, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<AreaRemitente>(areaRemitente, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * 
	 * @param areaRemitente
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Remover area favoritos", notes = "Remueve un area remitente de favoritos")
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
	
	@RequestMapping(value = "/removerAreaRemitente", method = RequestMethod.POST)
	public void delete(@RequestBody(required = true) AreaRemitente areaRemitente) {

		log.debug("QUITAR AREA FAVORITA >> " + areaRemitente);

		try {

			if (areaRemitente.getAreaRemitenteKey() != null //
					&& (areaRemitente.getAreaRemitenteKey().getIdArea() != null)
					&& (areaRemitente.getAreaRemitenteKey().getIdInstitucion() != null)
					&& (areaRemitente.getAreaRemitenteKey().getAreaRemitente() != null)
					&& (areaRemitente.getAreaRemitenteKey().getAreaRemitente().getIdArea() != null)) {

				areaRemitente.getAreaRemitenteKey().setAreaRemitente(
						mngrArea.fetch(areaRemitente.getAreaRemitenteKey().getAreaRemitente().getIdArea()));

				mngrAreaRemitente.delete(areaRemitente);
				return;
			}
			throw new BadRequestException();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<AreaRemitente> get(Serializable id) {
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

	/** */
	@Value("${maxFavoritosAreaRemitente}")
	private Integer maxFavoritosAreaRemitente;

	/**
	 * Limitar la cantidad de favoritos q se pueden guardar por area.
	 * @param idArea
	 */
	private void countMaxFavoritos(Integer idArea) throws BadRequestException {

		String sqlquery = "select count(*) as countr from AreaRemitente where areaRemitenteKey.idArea = " + idArea;

		List<?> search = mngrAreaRemitente.execQuery(sqlquery);

		Long contr = (Long) search.get(0);

		if (contr > maxFavoritosAreaRemitente) {

			throw new BadRequestException(
					"Excedio el maximo permitido de favoritos, elimine alguno antes de agregar uno nuevo.");
		}

	}

}
