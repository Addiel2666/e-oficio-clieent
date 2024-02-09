/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.util.TipoAsunto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controlador REST para la seccion de estadisticas de la aplicacion.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class EstadisticasController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(EstadisticasController.class);

	/**
	 * Obtiene valores estadiscticos sobre Asunto.
	 * 
	 * @param type
	 * @param inbound
	 * @param idInstitucion
	 * @param idArea
	 * @param dateInicial
	 * @param dateFinal
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene estadisticas", notes = "Obtiene valores estadisticos sobre un Asunto")
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
	
	@RequestMapping(value = "/estadisticas", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> get(//
			// query solicitado
			@RequestParam(value = "type", required = true) Integer type,
			// entrante o saliente
			@RequestParam(value = "inbound", required = true) Integer inbound,
			// parametros de busqueda
			@RequestParam(value = "idInstitucion", required = true) Integer idInstitucion,
			@RequestParam(value = "idArea", required = true) Integer idArea,
			@RequestParam(value = "dateInicial", required = false) Long dateInicial,
			@RequestParam(value = "dateFinal", required = false) Long dateFinal) throws Exception {

		List<?> items = null;
		try {

			ProjectionList projections = Projections.projectionList();
			List<Order> orders = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<>();
			switch (type) {
			case 0:
				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				// POR ESTADO
				projections.add(Projections.count("idAsunto").as("countr"));

				projections.add(Projections.groupProperty("areaDestino.idArea").as("idArea"));
				projections.add(Projections.groupProperty("areaDestino.descripcion").as("nameArea"));

				projections.add(Projections.groupProperty("statusAsunto.idStatus").as("status"));
				projections.add(Projections.groupProperty("statusAsunto.descripcion").as("statusName"));

				orders.add(Order.asc("areaDestino.descripcion"));
				orders.add(Order.asc("statusAsunto.idStatus"));

				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				break;
			case 1:
				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				// POR TIPO EXPEDIENTE
				projections.add(Projections.count("idAsunto").as("countr"));

				projections.add(Projections.groupProperty("area.idArea").as("idArea"));
				projections.add(Projections.groupProperty("area.descripcion").as("nameArea"));

				projections.add(Projections.groupProperty("tipoExpediente.idExpediente").as("status"));
				projections.add(Projections.groupProperty("tipoExpediente.descripcion").as("statusName"));

				orders.add(Order.asc("area.descripcion"));
				orders.add(Order.asc("tipoExpediente.descripcion"));

				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				break;
			case 2:
				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				// POR INSTITUCION
				projections.add(Projections.count("idAsunto").as("countr"));

				projections.add(Projections.groupProperty("area.idArea").as("idArea"));
				projections.add(Projections.groupProperty("area.descripcion").as("nameArea"));

				projections.add(Projections.groupProperty("institucionArea.idInstitucion").as("status"));
				projections.add(Projections.groupProperty("institucionArea.descripcion").as("statusName"));

				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				break;
			case 3:
				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				// POR REMITENTE
				projections.add(Projections.count("idAsunto").as("countr"));

				projections.add(Projections.groupProperty("area.idArea").as("idArea"));
				projections.add(Projections.groupProperty("area.descripcion").as("nameArea"));

				projections.add(Projections.groupProperty("remitenteAsunto.idArea").as("status"));
				projections.add(Projections.groupProperty("remitenteAsunto.descripcion").as("statusName"));

				orders.add(Order.asc("area.descripcion"));
				orders.add(Order.asc("remitenteAsunto.descripcion"));

				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				break;
			case 4:
				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				// POR TEMA
				projections.add(Projections.count("idAsunto").as("countr"));

				projections.add(Projections.groupProperty("temaArea.idTema").as("idArea"));
				projections.add(Projections.groupProperty("temaArea.descripcion").as("nameArea"));

				projections.add(Projections.groupProperty("asuntoTema.idTema").as("status"));
				projections.add(Projections.groupProperty("asuntoTema.descripcion").as("statusName"));

				orders.add(Order.asc("temaArea.descripcion"));
				orders.add(Order.asc("asuntoTema.descripcion"));

				// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				break;
			default:
				throw new Exception("Uninplemented Method.");
			}

			switch (inbound) {
			case 1:
				// recibidos/generados

				Conjunction lhs = Restrictions.and( //
						Restrictions.eq("areaDestino.idArea", idArea), //
						Restrictions.in("tipoAsunto", new Object[] { TipoAsunto.ENVIO, TipoAsunto.TURNO }), //
						Restrictions.not(Restrictions.in("statusAsunto.idStatus",
								new Object[] { Status.POR_ENVIAR, Status.ENVIADO })));

				LogicalExpression rhs = Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.ASUNTO),
						Restrictions.eq("area.idArea", idArea));

				LogicalExpression rs = Restrictions.or(lhs, rhs);

				restrictions.add(rs);

				break;
			case 2:
				// enviados

				restrictions.add(Restrictions.in("tipoAsunto", new Object[] { TipoAsunto.ENVIO, TipoAsunto.TURNO }));

				restrictions.add(Restrictions.eq("area.idArea", idArea));

				break;
			default:
				throw new Exception("Uninplemented Method.");
			}

			if (dateInicial != null && dateFinal != null)
				restrictions.add(Restrictions.between("fechaRegistro", new Date(dateInicial), new Date(dateFinal)));

			items = mngrAsunto.search(restrictions, orders, projections, null, null);

			log.debug(" Item Out >> " + items);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(items, HttpStatus.OK);
	}

}
