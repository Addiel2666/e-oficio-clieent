/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Firmante;
import com.ecm.sigap.data.model.FirmanteActivoSN;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Firmante}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class FirmanteController extends CustomRestController implements RESTController<Firmante> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FirmanteController.class);

	

	/**
	 * 
	 * @param representante
	 * @return
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta usuario conectado", notes = "Consulta al usrio conectado, para utilizarlo como firmante")
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
	@RequestMapping(value = "/firmante", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Firmante firmante) {

		List<?> lst = new ArrayList<Firmante>();
		log.debug("PARAMETROS DE BUSQUEDA : " + firmante);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (firmante.getNombres() != null && !firmante.getNombres().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("nombres", firmante.getNombres(), MatchMode.ANYWHERE));

			if (firmante.getPaterno() != null && !firmante.getPaterno().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", firmante.getPaterno(), MatchMode.ANYWHERE));

			if (firmante.getPaterno() != null && !firmante.getPaterno().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("materno", firmante.getMaterno(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(firmante.getNombreCompleto()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("nombreCompleto", firmante.getNombreCompleto(), MatchMode.ANYWHERE));

			
				if (StringUtils.isNotBlank(firmante.getIdFirmante()))
					restrictions.add(Restrictions.eq("idFirmante", firmante.getIdFirmante()));

				if (firmante.getIdPromotor() != null)
					restrictions.add(Restrictions.eq("idPromotor", firmante.getIdPromotor()));

				if (firmante.getIdRemitente() != null)
					restrictions.add(Restrictions.eq("idRemitente", firmante.getIdRemitente()));
			

			if (firmante.getCargo() != null && !firmante.getCargo().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("cargo", firmante.getCargo(), MatchMode.ANYWHERE));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFirmante.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/firmantesn", method = RequestMethod.POST)
	public ResponseEntity<List<?>> searchTitulares(@RequestBody(required = true) FirmanteActivoSN firmante) {

		List<?> lst = new ArrayList<FirmanteActivoSN>();
		log.debug("PARAMETROS DE BUSQUEDA : " + firmante);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (firmante.getNombres() != null && !firmante.getNombres().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("nombres", firmante.getNombres(), MatchMode.ANYWHERE));

			if (firmante.getPaterno() != null && !firmante.getPaterno().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("paterno", firmante.getPaterno(), MatchMode.ANYWHERE));

			if (firmante.getPaterno() != null && !firmante.getPaterno().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("materno", firmante.getMaterno(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(firmante.getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCompleto", firmante.getNombreCompleto(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(firmante.getIdFirmante()))
				restrictions.add(Restrictions.eq("idFirmante", firmante.getIdFirmante()));

			if (firmante.getIdPromotor() != null)
				restrictions.add(Restrictions.eq("idPromotor", firmante.getIdPromotor()));

			if (firmante.getIdRemitente() != null)
				restrictions.add(Restrictions.eq("idRemitente", firmante.getIdRemitente()));
			
			if (firmante.getCargo() != null && !firmante.getCargo().isEmpty())
				restrictions.add(EscapedLikeRestrictions.ilike("cargo", firmante.getCargo(), MatchMode.ANYWHERE));
			
			if (firmante.isSoloTitularesArea())
				restrictions.add(Restrictions.not(Restrictions.eq("numAreasComoTitular", 0)));
			
			restrictions.add(Restrictions.eq("activosn", "S"));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrFirmanteAvtivoSN.search(restrictions, orders);
			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	@Override
	public void delete(Serializable id) throws Exception {
		throw new UnsupportedOperationException();

	}

	@Override
	public ResponseEntity<Firmante> save(Firmante object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseEntity<Firmante> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

}
