/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.firma.Firma6Service;
import com.ecm.sigap.firma.FirmaCore;

/**
 * Controladores REST para manejo de Firma Digital.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class Firma6Controller extends FirmaCore {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(Firma6Controller.class);

	/**
	 * Servicio de llamadas REST al WS de Firma Digital
	 */
	@Autowired
	@Qualifier("firma6Service")
	protected Firma6Service firma6EndPoint;

	/**
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/firma6/iniciarFirma", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> iniciarFirma(//
			@RequestBody Map<String, Object> body) throws Exception {

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> documentos = (List<Map<String, Object>>) body.get("documentos");

		Map<String, List<?>> response = firma6EndPoint.iniciarFirma(documentos);

		return new ResponseEntity<Map<String, List<?>>>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/firma6/concluirFirma", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> concluirFirma(@RequestBody Map<String, Object> body)
			throws Exception {
		Map<String, List<?>> response = new HashMap<String, List<?>>();

		Map<String, Object> body_;
		for (String key_ : body.keySet()) {
			body_ = new HashMap<String, Object>();

			// firmaController.endFirmar(body_);
		}

		return new ResponseEntity<Map<String, List<?>>>(response, HttpStatus.OK);
	}

}
