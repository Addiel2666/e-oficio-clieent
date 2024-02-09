/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ecm.sigap.data.controller.util.AntecedenteRelation;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoAntecedente;
import com.ecm.sigap.data.model.util.Antecedente;
import com.ecm.sigap.data.model.util.TipoAsunto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link AntecedenteController}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class AntecedenteController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AntecedenteController.class);

	/**
	 * Referencia hacia el REST controller de {@link PermisoController}.
	 */
	@Autowired
	private PermisoController permisoController;

	/**
	 * Agrega antecedentes al asunto indicado.
	 * 
	 * @param antecedentes
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar antecedente", notes = "Agrega un antecedente a un asunto")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 201, message = "Creado"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/asunto/antecedente", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<List<Antecedente>> addAntecedente(
			@RequestBody(required = true) List<AntecedenteRelation> antecedentes) throws Exception {

		List<Antecedente> antecedentes_ = new ArrayList<Antecedente>();
		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		if (!esSoloLectura(userId)) {

			LinkedHashMap<String, Object> params;

			Integer idAsunto;
			String folioArea;
			Antecedente ant;
			// List<Criterion> restrictions;
			// List<Asunto> lst;
			// Integer idAsuntoAgregar;
			Asunto asuntoDestino;

			for (AntecedenteRelation antecedente : antecedentes) {

				idAsunto = antecedente.getIdAsunto();
				folioArea = antecedente.getFolioArea();

				if (StringUtils.isBlank(folioArea) || idAsunto == null) {
					throw new ConstraintViolationException(errorMessages.getString("tramiteIndicadoAgregarNoFolio"),
							null);
				}

				asuntoDestino = mngrAsunto.fetch(idAsunto);

				params = new LinkedHashMap<String, Object>();
				params.put("vidasunto", idAsunto);
				params.put("vantecedente", folioArea);

				if (asuntoDestino.getFolioArea().equalsIgnoreCase(folioArea))
					throw new ConstraintViolationException(errorMessages.getString("tramiteIndicadoNoAgregar"), null);
				// se coloca para validar que el idAsunto no tiene el mismo folio del area del
				// asunto a relacionar
				if (conteoIdasuntosEnFolioArea(idAsunto, folioArea)) {
					throw new ConstraintViolationException(errorMessages.getString("tramiteIndicadoNoAgregar"), null);
				}
				/*
				 * se comenta para realizar la validacion con un proceso mas rapido
				 * ArrayList<Criterion> restrictions = new ArrayList<Criterion>();
				 * restrictions.add(EscapedLikeRestrictions.ilike("folioArea", folioArea,
				 * MatchMode.EXACT)); lst = (List<Asunto>) mngrAsunto.search(restrictions);
				 * 
				 * if (!lst.isEmpty()) { for (Asunto asunto : lst) { idAsuntoAgregar =
				 * asunto.getIdAsunto();
				 * 
				 * if (idAsuntoAgregar.equals(idAsunto)) throw new
				 * ConstraintViolationException(errorMessages.getString(
				 * "tramiteIndicadoNoAgregar"), null); } }
				 */

				mngrAsunto.createStoredProcedureCall("ANTECEDENTEMULTIPLE_INS", params);

				ant = new Antecedente();
				ant.setIdAntecedentes(folioArea);
				antecedentes_.add(ant);

			}

			return new ResponseEntity<List<Antecedente>>(antecedentes_, HttpStatus.OK);

		} else {
			return new ResponseEntity<List<Antecedente>>(antecedentes_, HttpStatus.BAD_REQUEST);
		}
	}

	// se crea la funcion para validar que el idAsunto no tiene el mismo folio del
	// area del asunto a relacionar
	@SuppressWarnings("unchecked")
	private boolean conteoIdasuntosEnFolioArea(Integer idAsunto, String folioArea) {
		boolean encontrado = false;
		ArrayList<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.and(Restrictions.eq("idAsunto", idAsunto),
				EscapedLikeRestrictions.ilike("folioArea", folioArea, MatchMode.EXACT)));
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.countDistinct("idAsunto").as("countr"));
		final Map<String, Long> map = (Map<String, Long>) mngrAsunto.search(restrictions, null, projections, null, null)
				.get(0);
		final Long conteo = map.get("countr");
		if (conteo > 0) {
			encontrado = true;
		}
		return encontrado;
	}

	/**
	 * Remover un "antecedente" a un asunto.
	 * 
	 * @param idAsunto
	 * @param folio
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar antecedente", notes = "Elimina un antecedente a un asunto")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/asunto/antecedente", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<List<Antecedente>> deleteAntecedente(
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto,
			@RequestParam(value = "folio", required = true) String folio) {

		Asunto asunto = mngrAsunto.fetch(Integer.valueOf((String) idAsunto));
		log.debug("size initial :: " + asunto.getAntecedentes().size());

		List<Antecedente> antecedentesFiltrados = new ArrayList<Antecedente>();

		for (Antecedente antecedente : asunto.getAntecedentes())
			if (!antecedente.getIdAntecedentes().equalsIgnoreCase(folio))
				antecedentesFiltrados.add(antecedente);

		if (asunto.getAntecedentes().size() > antecedentesFiltrados.size()) {

			asunto.setAntecedentes(antecedentesFiltrados);

			log.debug("size after remove :: " + asunto.getAntecedentes().size());

			mngrAsunto.update(asunto);

			asunto = mngrAsunto.fetch(Integer.valueOf((String) idAsunto));

			log.debug("size after :: " + asunto.getAntecedentes().size());
		}

		return new ResponseEntity<List<Antecedente>>(asunto.getAntecedentes(), HttpStatus.OK);

	}

	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Consulta asuntos a relacionar y relacionados", notes = "Consulta asuntos a relacionar y relacionados")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/asunto/antecedente", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestWrapper<List<AsuntoAntecedente>>> getAsuntosAntecedentes(
			@RequestBody(required = true) RequestWrapper<AsuntoAntecedente> body) {

		log.debug(" PARAMETROS DE BUSQUEDA PARA ANTECEDENTES :: " + body);

		AsuntoAntecedente asuntoRelacionar = body.getObject();
		Map<String, Object> params = body.getParams();
		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		List<Order> orders = new ArrayList<Order>();
		Map<String, Object> paramResult = new HashMap<>();
		RequestWrapper<List<AsuntoAntecedente>> respuesta = new RequestWrapper<List<AsuntoAntecedente>>();

		try {

			boolean verConfidencial = permisoController.verConfidencial(idUsuario, idArea);

			List<Criterion> restrictions = createCriterionAntecedentes(idArea, asuntoRelacionar, params,
					verConfidencial);

			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			}

			List<AsuntoAntecedente> list = (List<AsuntoAntecedente>) mngrAsuntoRelacionado.search(restrictions, orders);
			paramResult.put("total", list.size());
			respuesta.setObject(list);
			respuesta.setParams(paramResult);

			return new ResponseEntity<RequestWrapper<List<AsuntoAntecedente>>>(respuesta, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Criterion> createCriterionAntecedentes(Integer idArea, AsuntoAntecedente asuntoAntecedente,
			Map<String, Object> params, boolean verConfidencial) {
		List<Criterion> restrictions = new ArrayList<Criterion>();

		if (asuntoAntecedente.getIdAsunto() != null)
			restrictions.add(Restrictions.eq("idAsunto", asuntoAntecedente.getIdAsunto()));

		if (StringUtils.isNotBlank(asuntoAntecedente.getFolioArea()))
			restrictions.add(
					EscapedLikeRestrictions.ilike("folioArea", asuntoAntecedente.getFolioArea(), MatchMode.ANYWHERE));

		if (asuntoAntecedente.getNumDocto() != null)
			restrictions.add(
					EscapedLikeRestrictions.ilike("numDocto", asuntoAntecedente.getNumDocto(), MatchMode.ANYWHERE));

		if (StringUtils.isNotBlank(asuntoAntecedente.getAsuntoDescripcion()))
			restrictions.add(EscapedLikeRestrictions.ilike("asuntoDescripcion",
					asuntoAntecedente.getAsuntoDescripcion(), MatchMode.ANYWHERE));

		if (asuntoAntecedente.getIdTipoRegistro() != null)
			restrictions.add(Restrictions.eq("idTipoRegistro", asuntoAntecedente.getIdTipoRegistro()));

		if (asuntoAntecedente.getTipoAsunto() != null)
			restrictions.add(Restrictions.eq("tipoAsunto", asuntoAntecedente.getTipoAsunto()));

		if (asuntoAntecedente.getFolioIntermedio() != null)
			restrictions.add(EscapedLikeRestrictions.ilike("folioIntermedio", asuntoAntecedente.getFolioIntermedio(),
					MatchMode.ANYWHERE));

		if (StringUtils.isNotBlank(asuntoAntecedente.getRemitente()))
			restrictions.add(Restrictions.eq("remitente", asuntoAntecedente.getRemitente()));

		if (asuntoAntecedente.getIdAreaDestino() != null)
			restrictions.add(Restrictions.eq("idAreaDestino", asuntoAntecedente.getIdAreaDestino()));

		if (asuntoAntecedente.getIdArea() != null)
			restrictions.add(Restrictions.eq("idArea", asuntoAntecedente.getIdArea()));

		if (StringUtils.isNotBlank(asuntoAntecedente.getArea()))
			restrictions.add(Restrictions.eq("area", asuntoAntecedente.getArea()));

		if (StringUtils.isNotBlank(asuntoAntecedente.getIdFirmante()))
			restrictions.add(Restrictions.eq("idFirmante", asuntoAntecedente.getIdFirmante()));

		if (StringUtils.isNotBlank(asuntoAntecedente.getFirmanteAsunto()))
			restrictions.add(Restrictions.eq("firmanteAsunto", asuntoAntecedente.getFirmanteAsunto()));

		if (StringUtils.isNotBlank(asuntoAntecedente.getFirmanteCargo()))
			restrictions.add(Restrictions.eq("firmanteCargo", asuntoAntecedente.getFirmanteCargo()));

		if (asuntoAntecedente.getIdAsuntoOrigen() != null)
			restrictions.add(Restrictions.eq("idAsuntoOrigen", asuntoAntecedente.getIdAsuntoOrigen()));

		if (asuntoAntecedente.getIdTipo() != null)
			restrictions.add(Restrictions.eq("idTipo", asuntoAntecedente.getIdTipo()));

		if (StringUtils.isNotBlank(asuntoAntecedente.getTipo()))
			restrictions.add(EscapedLikeRestrictions.ilike("tipo", asuntoAntecedente.getTipo(), MatchMode.ANYWHERE));

		if (!verConfidencial)
			restrictions.add(Restrictions.eq("confidencial", Boolean.FALSE));

		// ------------ PARAMS ------------

		if (params != null) {
			if (params.containsKey("idStatusAsuntoNotIn")) {
				List<Integer> val = (List<Integer>) params.get("idStatusAsuntoNotIn");
				if (!val.isEmpty())
					restrictions.add(Restrictions.not(Restrictions.in("idStatusAsunto", val)));
			}

			if (params.containsKey("tipoAsuntoIn")) {
				List<String> val_ = (List<String>) params.get("tipoAsuntoIn");
				List<TipoAsunto> val = new ArrayList<>();
				for (String tipoAsunto : val_) {
					val.add(TipoAsunto.valueOf(tipoAsunto));
				}
				if (!val.isEmpty())
					restrictions.add(Restrictions.in("tipoAsunto", val));
			}
			
			if (params.containsKey("idAreaORIdAreaDestino")) {
                Integer val = Integer.parseInt(params.get("idAreaORIdAreaDestino").toString());

                restrictions.add(Restrictions.or(//
                        Restrictions.eq("idAreaDestino", val), //
                        Restrictions.and(Restrictions.eq("idArea", val), Restrictions.isNull("idAreaDestino")))//
                );

            }
			
			if (params.containsKey("inFolioArea")) {
                List<String> val = (ArrayList<String>) params.get("inFolioArea");
                restrictions.add(Restrictions.in("folioArea", val));

            }
		}

		return restrictions;
	}

}
