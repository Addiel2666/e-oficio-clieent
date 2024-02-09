/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controlador REST para la seccion de contadores del home.
 * 
 * @author Alfredo Morales
 * @version 2.0
 *
 */
@RestController
public class ContadoresController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ContadoresController.class);


	/**
	 * Devuelve los datos de la pagina principal y el conteo de asuntos turnos
	 * borradores y respuestas del usuario/area conectada.
	 * 
	 * @param idArea
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene contadores inicio", notes = "Obtiene todos los contadores que se muestran en la pantalla principal")
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
	
	@GetMapping(value = "/contadores")
	public @ResponseBody ResponseEntity<String> get() throws Exception {
		log.info(":::Iniciando contadores oficio:::");
		
		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		
		String funcion = "getContadores";
		
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("USUARIO", idUsuario);
		params.put("AREA", idArea);		
		
		String respuesta = mngrAsunto.callFunction(funcion, params).toString();

		return new ResponseEntity<String>(respuesta,HttpStatus.OK);
	}
}