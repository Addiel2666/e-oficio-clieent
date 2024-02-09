/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.DocumentoRespuestaFirmado;
import com.ecm.sigap.data.model.Usuario;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para busqueda de
 * {@link com.ecm.sigap.data.model.DocumentoRespuesta} firmados,
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class DocumentoFirmadoRespuestaController extends CustomRestController {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(DocumentoFirmadoRespuestaController.class);

	/**
	 * 
	 * @param documento
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta documentos firmados respuesta", notes = "Consulta los documentos firmados en una respuesta")
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/documentosFirmados/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<DocumentoRespuestaFirmado>> search(
			@RequestBody(required = true) RequestWrapper<DocumentoRespuesta> body) throws Exception {

		List<DocumentoRespuestaFirmado> lst = new ArrayList<DocumentoRespuestaFirmado>();

		List<Map<String, String>> documentosFirmados = null;

		try {
			Map<String, Object> params = body.getParams();

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (params != null) {

				// String areaId = getHeader(HeaderValueNames.HEADER_AREA_ID);
				// restrictions.add(Restrictions.eq("idArea", Integer.parseInt(areaId)));

				Object userId = params.get("userId");

				if (userId == null)
					return new ResponseEntity<List<DocumentoRespuestaFirmado>>(lst, HttpStatus.BAD_REQUEST);

				Usuario usuario_ = mngrUsuario.fetch(userId.toString());

				if (usuario_ == null)
					return new ResponseEntity<List<DocumentoRespuestaFirmado>>(lst, HttpStatus.OK);

				documentosFirmados = endpoint.obtenerDocumentosFirmadosRespuestas(
						environment.getProperty("docTypeAdjuntoAsunto"), //
						environment.getProperty("fieldFirmante"), //
						usuario_.getEmail());

				if (documentosFirmados.isEmpty())
					return new ResponseEntity<List<DocumentoRespuestaFirmado>>(lst, HttpStatus.OK);

				List<String> lst2 = new ArrayList<String>();

				for (Map<String, String> map : documentosFirmados) {
					lst2.add(map.get("r_object_id"));
				}

				restrictions.add(Restrictions.in("objectId", lst2));

				// FILSTROS PARA FECHAS
				if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
					restrictions.add(Restrictions.between("fechaRegistro", //
							new Date((Long) params.get("fechaRegistroInicial")),
							new Date((Long) params.get("fechaRegistroFinal"))));
				} else if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") == null) {
					restrictions
							.add(Restrictions.ge("fechaRegistro", new Date((Long) params.get("fechaRegistroInicial"))));
				} else if (params.get("fechaRegistroInicial") == null && params.get("fechaRegistroFinal") != null) {
					restrictions
							.add(Restrictions.le("fechaRegistro", new Date((Long) params.get("fechaRegistroFinal"))));
				}

			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("fechaRegistro"));

			lst = (List<DocumentoRespuestaFirmado>) mngrDocsRespuestaFirmados.search(restrictions, orders);

			if (documentosFirmados != null)
				for (DocumentoRespuestaFirmado drf : lst)
					for (Map<String, String> map : documentosFirmados) {
						if (map.get("r_object_id").equalsIgnoreCase(drf.getObjectId())) {
							drf.setObjectName(map.get("object_name").toString());
							drf.setOwnerName(map.get("owner_name").toString());
						}
					}

		} catch (Exception e) {
			
			log.error(e.getLocalizedMessage());
			throw e;
		}

		return new ResponseEntity<List<DocumentoRespuestaFirmado>>(lst, HttpStatus.OK);
	}

}
