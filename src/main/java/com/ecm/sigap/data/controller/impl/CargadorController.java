/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Cargador via archivo de excel.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class CargadorController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(CargadorController.class);

	/**
	 * 
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Cargador archivos", notes = "Carga los archivos")
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cargador", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> save(
			@RequestBody(required = true) Map<String, Object> payload) throws Exception {
		try {

			List<Map<String, String>> files = (List<Map<String, String>>) payload.get("files");

			Path file_;
			String absolutePath;

			for (Map<String, String> map : files) {

				file_ = FileUtil.createTempFile(map.get("fileB64").toString(), map.get("objectName").toString());

				absolutePath = file_.toAbsolutePath().toString();

				log.info(" File :: " + absolutePath);

				if (absolutePath.toLowerCase().endsWith(".xls") || absolutePath.toLowerCase().endsWith(".xlsx")) {

					// TODO

				} else {

					log.warn("Formato no soportado.");
				}

			}

			return null;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

}
