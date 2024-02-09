/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.Destinatario;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.RespuestaConsulta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.CopiaRespuesta;
import com.ecm.sigap.data.model.util.StatusAsunto;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.util.SignatureUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Respuesta}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class RespuestaConsultaController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RespuestaConsultaController.class);

	/**
	 * Referencia hacia el REST controller de {@link PermisoController}.
	 */
	@Autowired
	private PermisoController permisoController;

	/**
	 * Consultar.
	 *
	 * @param body the body
	 * @return the response entity
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consultar respuesta", notes = "Consulta una respuesta de la lista")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/respuesta/consultar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestWrapper<List<RespuestaConsulta>>> consultar(
			@RequestBody(required = true) RequestWrapper<RespuestaConsulta> body) {

		List<RespuestaConsulta> lst = new ArrayList<RespuestaConsulta>();

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		RespuestaConsulta respuestaConsulta = body.getObject();

		Map<String, Object> params = body.getParams();

		String tipoConsulta = "";

		Integer cantidadRegistros = null;

		if (null != body.getSize())
			cantidadRegistros = body.getSize();

		Integer empezarEn = null;

		if (null != body.getBeginAt())
			empezarEn = body.getBeginAt();

		log.info("Parametros de busqueda :: " + respuestaConsulta);
		Long completeCount = 0L;

		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<Criterion> restrictionsCount = new ArrayList<Criterion>();
			restrictions = createCriterionConsultar(idArea, respuestaConsulta, params, false);
			restrictionsCount = createCriterionConsultar(idArea, respuestaConsulta, params, true);

			List<Order> orders = new ArrayList<Order>();
			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			} else {
				if (params.get("tipoConsulta") != null)
					tipoConsulta = params.get("tipoConsulta").toString();

				switch (tipoConsulta) {
				case "R":
					orders.add(Order.desc("fechaAcuse"));
					break;
				// TODO: Ordenamiendo por fechaAcuse para recibidas, Saber qué ordenamiento
				// tendrán la general.
				default:
					orders.add(Order.desc("fechaRegistro"));
					break;
				}

			}

			// * * * * * * * * * * * * * * * * * * * * * *
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.countDistinct("idRespuesta").as("countr"));

			final List<?> search = mngrRespuestaCount.search(restrictionsCount, null, projections, null, null);
			final Map<String, Long> map = (Map<String, Long>) search.get(0);
			completeCount = map.get("countr");

			if (cantidadRegistros == null || empezarEn == null) {
				List<RespuestaConsulta> lstR = new ArrayList<>();
				int firtsResult = 0;
				int fetchSize = 10;
				do {
					lstR = (List<RespuestaConsulta>) mngrRespuestaConsulta.search(restrictions, orders, null, fetchSize,
							firtsResult);
					lst.addAll(lstR);
					firtsResult += fetchSize;
				} while (lstR.size() == 10);
			} else
				lst = mngrRespuestaConsulta.search(restrictions, orders, null, cantidadRegistros, empezarEn);

			Map<String, Object> paramResult = new HashMap<>();
			paramResult.put("total", completeCount);
			// paramResult.put("valor_busqueda_estatus", listStatusAsunto);
			RequestWrapper<List<RespuestaConsulta>> respuesta = new RequestWrapper<List<RespuestaConsulta>>();
			respuesta.setObject(lst);
			respuesta.setParams(paramResult);

			return new ResponseEntity<RequestWrapper<List<RespuestaConsulta>>>(respuesta, HttpStatus.OK);
			// return new ResponseEntity<List<RespuestaConsulta>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Crea un CSV.
	 *
	 * @param body the body
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Descargar csv respuesta", notes = "Descarga un archivo csv con la lista de todas las respuestas")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/respuesta/csv", method = RequestMethod.POST)
	public void toDownloadCSV(@RequestBody(required = true) RequestWrapper<RespuestaConsulta> body,
			HttpServletResponse response, HttpServletRequest request) throws Exception {

		List<RespuestaConsulta> lst = new ArrayList<RespuestaConsulta>();

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		RespuestaConsulta respuestaConsulta = body.getObject();

		Map<String, Object> params = body.getParams();

		log.info("Parametros de busqueda :: " + respuestaConsulta);

		try {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions = createCriterionConsultar(idArea, respuestaConsulta, params, false);
			List<Order> orders = new ArrayList<Order>();
			switch (params.get("tipoConsulta").toString()) {
			case "R":
				orders.add(Order.desc("fechaAcuse"));
				break;
			// TODO: Ordenamiendo por fechaAcuse para recibidas, Saber qué ordenamiento
			// tendrán la general.
			default:
				orders.add(Order.desc("fechaRegistro"));
				break;
			}

			List<RespuestaConsulta> lstR = new ArrayList<>();
			int firtsResult = 0;
			final int fetchSize = Integer.parseInt(environment.getProperty("csv.resp.max.size.data", "100"));
			do {
				lstR = (List<RespuestaConsulta>) mngrRespuestaConsulta.search(restrictions, orders, null, fetchSize,
						firtsResult);
				lst.addAll(lstR);
				firtsResult += fetchSize;
			} while (lstR.size() == fetchSize);

			// * * * * * * * * * * * * * * * * * * * * * *
			response.setContentType("text/csv;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"ConsultaRespuestas.csv\"");
			final String columnNamesList = "Enviada por - Respuesta," + "Fecha de envío - Respuesta,"
					+ "Fecha acuse - Respuesta," + "Comentario - Respuesta," + "Tipo - Respuesta,"
					+ "Porcentaje Avance - Respuesta," + "Num. Docto - Respuesta," + "Respuesta de - Respuesta,"
					+ "Área - Respuesta," + "Institución - Respuesta," + "Intrucción - Trámite,"
					+ "Intrucción adicional - Trámite," + "Fecha envío - Trámite," + "Fecha acuse - Trámite,"
					+ "Fecha compromiso - Trámite," + "ET.FT - Trámite," + "Prioridad," + "Seguimiento," + "Categoría,"
					+ "ID Origen," + "Folio - Asunto," + "No de documento - Asunto," + "Fecha de elaboración,"
					+ "Fecha de registro," + "Asunto - Asunto," + "Firmante - Asunto," + "Cargo - Asunto,"
					+ "Área - Asunto," + "Institución - Asunto," + "Tipo - Asunto," + "Aceptó/Rechazó," + "Estado";

			try (OutputStream outputStream = response.getOutputStream()) {
				// SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

				StringBuilder builder = new StringBuilder();

				builder.append(columnNamesList + "\n");
				String str = "";

				for (RespuestaConsulta ob : lst) {
					builder.append(ob.getFechaEnvio() != null ? ob.getNombreFirmante() : "");
					builder.append(',');
					builder.append(ob.getFechaEnvio() != null ? fmt.format(ob.getFechaEnvio()) : "");
					builder.append(',');
					builder.append(ob.getFechaAcuse() != null ? fmt.format(ob.getFechaAcuse()) : "");
					builder.append(',');
					builder.append(ob.getComentario() != null ? escapeText(ob.getComentario()) : "");
					builder.append(',');
					builder.append('"' + ob.getTipoRespuestaDescripcion() + '"');
					builder.append(',');
					str = "" + ob.getPorcentaje();
					builder.append('"' + str + '"');
					builder.append(',');
					builder.append(StringUtils.isNotEmpty(ob.getFolioRespuesta())
							? StringEscapeUtils.escapeCsv(ob.getFolioRespuesta())
							: "");
					builder.append(',');
					builder.append('"' + ob.getAreaTitularNombrecompleto() + '"');
					builder.append(',');
					builder.append('"' + ob.getAreaDescripcion() + '"');
					builder.append(',');
					builder.append('"' + ob.getAreaInstitucionDescripcion() + '"');
					builder.append(',');

					builder.append(ob.getInstruccionDescripcionAsunto() != null
							? StringEscapeUtils.escapeCsv(ob.getInstruccionDescripcionAsunto())
							: "");

					builder.append(',');
					builder.append(StringUtils.isNotEmpty(ob.getComentarioAsunto())
							? StringEscapeUtils.escapeCsv(ob.getComentarioAsunto())
							: "");
					builder.append(',');
					builder.append(ob.getFechaEnvioAsunto() != null ? fmt.format(ob.getFechaEnvioAsunto()) : "");
					builder.append(',');
					builder.append(ob.getFechaAcuseAsunto() != null ? fmt.format(ob.getFechaAcuseAsunto()) : "");
					builder.append(',');
					builder.append(
							ob.getFechaCompromisoAsunto() != null ? fmt.format(ob.getFechaCompromisoAsunto()) : "");
					builder.append(',');
					builder.append(ob.getEnTiempo());
					builder.append(',');

					builder.append(StringUtils.isEmpty(ob.getPrioridadDescripcionAsunto()) ? " "
							: StringEscapeUtils.escapeCsv(ob.getPrioridadDescripcionAsunto()));

					builder.append(',');
					builder.append(ob.getEspecialsnAsunto().equals("S") ? "Sí" : "No");
					builder.append(',');
					builder.append(ob.getTipoAsunto());
					builder.append(',');

					str = "" + ob.getIdAsuntoOrigen();
					builder.append('"' + str + '"');

					builder.append(',');
					String folioAreaAsuntoPadre = ob.getFolioAreaAsuntoPadreAsunto() != null
							? ob.getFolioAreaAsuntoPadreAsunto()
							: "";
					builder.append('"' + folioAreaAsuntoPadre + '"');
					builder.append(',');
					builder.append(StringEscapeUtils.escapeCsv(ob.getNumDoctoPadreAsunto()));
					builder.append(',');
					builder.append(ob.getFechaElaboracionPadreAsunto() != null
							? fmt.format(ob.getFechaElaboracionPadreAsunto())
							: "");
					builder.append(',');
					builder.append(
							ob.getFechaRegistroPadreAsunto() != null ? fmt.format(ob.getFechaRegistroPadreAsunto())
									: "");
					builder.append(',');
					builder.append(escapeText(ob.getAsuntoDescripcionPadreAsunto()));
					builder.append(',');

					builder.append('"');
					builder.append(StringUtils.isNotEmpty(ob.getFirmanteAsuntoPadreAsunto())
							? ob.getFirmanteAsuntoPadreAsunto()
							: " ");
					builder.append('"');
					builder.append(',');
					builder.append(
							ob.getFirmanteCargoPadreAsunto() != null ? '"' + ob.getFirmanteCargoPadreAsunto() + '"'
									: "");
					builder.append(',');
					builder.append(
							ob.getAreaDestinoDescripcion() != null ? escapeText(ob.getAreaDestinoDescripcion()) : "");
					builder.append(',');
					builder.append(ob.getPromotorPadreAsunto() != null ? '"' + ob.getPromotorPadreAsunto() + '"' : "");
					builder.append(',');
					builder.append(ob.getTipoAsuntoPadreAsunto());
					builder.append(',');
					builder.append(ob.getAcepto_rechazo_nombrecompleto() != null
							? '"' + ob.getAcepto_rechazo_nombrecompleto() + '"'
							: "");
					builder.append(',');
					builder.append('"' + ob.getStatusAsuntoPadreAsunto() + '"');
					builder.append('\n');
				}
				String outputResult = builder.toString();

				outputStream.write(outputResult.getBytes("UTF-8"));
				outputStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getLocalizedMessage());
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}

	}

	/**
	 * Consultar respuesta por recibir.
	 *
	 * @return the response entity
	 */

	/*
	 * Documentacion con swagger
	 */

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Obtiene respuestas recibidas", notes = "Obtiene respuestas recibidas en una area")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/respuesta/porRecibir", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<RequestWrapper<List<RespuestaConsulta>>> consultarRespuestaPorRecibir(
			@RequestParam(value = "idRemitente", required = false) Serializable idRemitente,
			@RequestParam(value = "avance", required = false) Serializable avance,
			@RequestParam(value = "concluido", required = false) Serializable concluido,
			@RequestParam(value = "prorroga", required = false) Serializable prorroga,
			@RequestParam(value = "ria", required = false) Serializable ria,
			@RequestParam(value = "beginAt", required = false) Integer empiezaEn,
			@RequestParam(value = "size", required = false) Integer cantidadRegistros) {

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);

		List<RespuestaConsulta> lst = new ArrayList<RespuestaConsulta>();
		RequestWrapper<List<RespuestaConsulta>> respuesta = new RequestWrapper<List<RespuestaConsulta>>();

		try {
			boolean verConfidencial = permisoController.verConfidencial(idUsuario, idArea);
			List<TipoAsunto> listTipoAsunto = Arrays.asList(TipoAsunto.ENVIO, TipoAsunto.TURNO, TipoAsunto.COPIA);

			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("areaDestinoId", idArea));
			restrictions.add(Restrictions.eq("status.idStatus", 1));
			restrictions.add(Restrictions.in("tipoAsunto", listTipoAsunto));

			if (null != idRemitente) {
				int idAreaRemitente = Integer.valueOf((String) idRemitente);
				restrictions.add(Restrictions.eq("areaId", idAreaRemitente));
			}

			if (null != avance && null != concluido && null != prorroga && null != ria) {
				List<String> tipoRespuesta = new ArrayList<>();

				if (Boolean.TRUE.toString().equals(String.valueOf((String) avance)))
					tipoRespuesta.add("A");

				if (Boolean.TRUE.toString().equals(String.valueOf((String) concluido))) {
					tipoRespuesta.add("C");
				}

				if (Boolean.TRUE.toString().equals(String.valueOf((String) prorroga))) {
					tipoRespuesta.add("P");
				}

				if (Boolean.TRUE.toString().equals(String.valueOf((String) ria))) {
					tipoRespuesta.add("R");
				}

				if (!tipoRespuesta.isEmpty())
					restrictions.add(Restrictions.in("tipoRespuestaId", tipoRespuesta));
			}

			if (!verConfidencial) {
				restrictions.add(Restrictions.eq("confidencial", false));
			}

			// * * * * * * * * * * * * * * * * * * * * * *
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.countDistinct("idRespuesta").as("countr"));

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("fechaEnvio"));

			Long completeCount = 0L;

			if (empiezaEn == null || cantidadRegistros == null) {
				List<RespuestaConsulta> lstR = new ArrayList<>();
				int firtsResult = 0;
				final int fetchSize = Integer.parseInt(environment.getProperty("csv.resp.max.size.data", "100"));
				do {
					lstR = (List<RespuestaConsulta>) mngrRespuestaConsulta.search(restrictions, orders, null, fetchSize,
							firtsResult);
					lst.addAll(lstR);
					firtsResult += fetchSize;
				} while (lstR.size() == fetchSize);
				completeCount = (long) lst.size();
			} else {
				final List<?> search = (List<RespuestaConsulta>) mngrRespuestaConsulta.search(restrictions, null,
						projections, null, null);
				final Map<String, Long> map = (Map<String, Long>) search.get(0);
				completeCount = map.get("countr");
				lst = (List<RespuestaConsulta>) mngrRespuestaConsulta.search(restrictions, orders, null,
						cantidadRegistros, empiezaEn);
			}

			Map<String, Object> paramResult = new HashMap<>();
			paramResult.put("total", completeCount);
			respuesta.setObject(lst);
			respuesta.setParams(paramResult);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<RequestWrapper<List<RespuestaConsulta>>>(respuesta, HttpStatus.OK);
	}

	@ApiOperation(value = "Obtiene todas las areas remitentes de las respuestas recibidas")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/respuesta/porRecibir/areasRemitentes", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Area>> consultarRespuestaPorRecibirAreasRemitentes(
			@RequestParam(value = "idRemitente", required = false) Serializable idRemitente,
			@RequestParam(value = "avance", required = false) Serializable avance,
			@RequestParam(value = "concluido", required = false) Serializable concluido,
			@RequestParam(value = "prorroga", required = false) Serializable prorroga,
			@RequestParam(value = "ria", required = false) Serializable ria) {

		ResponseEntity<RequestWrapper<List<RespuestaConsulta>>> response_ = //
				consultarRespuestaPorRecibir(idRemitente, avance, concluido, prorroga, ria, null, null);

		List<Area> response = response_.getBody().getObject().parallelStream()//
				.map(RespuestaConsulta::getAreaId)//
				.distinct() //
				.map((Integer i) -> mngrArea.fetch(i)) //
				.collect(Collectors.toList());

		return new ResponseEntity<List<Area>>(response, HttpStatus.OK);

	}

	@ApiOperation(value = "Obtiene todas las areas remitentes de las respuestas recibidas")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/respuesta/porRecibir/csv", method = RequestMethod.GET)
	public void consultarRespuestaPorRecibirCSV(
			@RequestParam(value = "idRemitente", required = false) Serializable idRemitente,
			@RequestParam(value = "avance", required = false) Serializable avance,
			@RequestParam(value = "concluido", required = false) Serializable concluido,
			@RequestParam(value = "prorroga", required = false) Serializable prorroga,
			@RequestParam(value = "ria", required = false) Serializable ria, //
			HttpServletResponse response, HttpServletRequest request) {

		ResponseEntity<RequestWrapper<List<RespuestaConsulta>>> response_ = //
				consultarRespuestaPorRecibir(idRemitente, avance, concluido, prorroga, ria, null, null);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			response.setContentType("text/csv;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"BandejaRespuestas.csv\"");
			final String columnNamesList = "Fecha de respuesta,Comentario,Tipo,Porcentaje,Titular,Área Titular,Institución,"
					+ "Num Docto,Instrucción - Trámite,Comentario - Trámite,Fecha envío - Trámite,Fecha acuse - Trámite,"
					+ "Fecha compromiso - Trámite,ET.FT - Trámite,Prioridad,Seguimiento especial - Trámite,Categoria - Trámite,"
					+ "ID Origen,Folio - Asunto,No de documento - Asunto,Fecha elaboración - Asunto,Fecha registro - Asunto,"
					+ "Asunto - Asunto,Firmante - Asunto,Firmante cargo - Asunto,Área - Asunto,Institución - Asunto,Tipo - Asunto,"
					+ "Remitido por - Asunto,Estado - Asunto";

			try (OutputStream outputStream = response.getOutputStream()) {
				// SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

				StringBuilder builder = new StringBuilder();

				builder.append(columnNamesList + "\n");
				String str = "";

				for (RespuestaConsulta ob : response_.getBody().getObject()) {

					builder.append(ob.getFechaEnvio() != null ? fmt.format(ob.getFechaEnvio()) : "");

					builder.append(',');
					builder.append(ob.getComentario() != null ? escapeText(ob.getComentario()) : "");

					builder.append(',');
					builder.append('"' + ob.getTipoRespuestaDescripcion() + '"');

					builder.append(',');
					str = "" + ob.getPorcentaje();
					builder.append('"' + str + '"');

					builder.append(',');
					builder.append('"' + ob.getAreaTitularNombrecompleto() + '"');

					builder.append(',');
					builder.append('"' + ob.getAreaDescripcion() + '"');

					builder.append(',');
					builder.append('"' + ob.getAreaInstitucionDescripcion() + '"');

					builder.append(',');
					builder.append(StringUtils.isNotEmpty(ob.getFolioRespuesta())
							? StringEscapeUtils.escapeCsv(ob.getFolioRespuesta())
							: "");

					builder.append(',');
					builder.append(ob.getInstruccionDescripcionAsunto() != null
							? StringEscapeUtils.escapeCsv(ob.getInstruccionDescripcionAsunto())
							: "");

					builder.append(',');
					builder.append(StringUtils.isNotEmpty(ob.getComentarioAsunto())
							? StringEscapeUtils.escapeCsv(ob.getComentarioAsunto())
							: "");

					builder.append(',');
					builder.append(ob.getFechaEnvioAsunto() != null ? fmt.format(ob.getFechaEnvioAsunto()) : "");

					builder.append(',');
					builder.append(ob.getFechaAcuseAsunto() != null ? fmt.format(ob.getFechaAcuseAsunto()) : "");

					builder.append(',');
					builder.append(
							ob.getFechaCompromisoAsunto() != null ? fmt.format(ob.getFechaCompromisoAsunto()) : "");

					builder.append(',');
					builder.append(ob.getEnTiempo());

					builder.append(',');
					builder.append(StringUtils.isEmpty(ob.getPrioridadDescripcionAsunto()) ? " "
							: StringEscapeUtils.escapeCsv(ob.getPrioridadDescripcionAsunto()));

					builder.append(',');
					builder.append(ob.getEspecialsnAsunto().equals("S") ? "Sí" : "No");

					builder.append(',');
					builder.append(ob.getTipoAsunto());

					builder.append(',');
					str = "" + ob.getIdAsuntoOrigen();
					builder.append('"' + str + '"');

					// - - -

					builder.append(',');
					String folioAreaAsuntoPadre = ob.getFolioAreaAsuntoPadreAsunto() != null
							? ob.getFolioAreaAsuntoPadreAsunto()
							: "";
					builder.append('"' + folioAreaAsuntoPadre + '"');

					builder.append(',');
					builder.append(StringEscapeUtils.escapeCsv(ob.getNumDoctoPadreAsunto()));

					builder.append(',');
					builder.append(ob.getFechaElaboracionPadreAsunto() != null
							? fmt.format(ob.getFechaElaboracionPadreAsunto())
							: "");

					builder.append(',');
					builder.append(
							ob.getFechaRegistroPadreAsunto() != null ? fmt.format(ob.getFechaRegistroPadreAsunto())
									: "");

					builder.append(',');
					builder.append(escapeText(ob.getAsuntoDescripcionPadreAsunto()));

					builder.append(',');
					builder.append('"');
					builder.append(StringUtils.isNotEmpty(ob.getFirmanteAsuntoPadreAsunto())
							? ob.getFirmanteAsuntoPadreAsunto()
							: " ");
					builder.append('"');

					builder.append(',');
					builder.append(
							ob.getFirmanteCargoPadreAsunto() != null ? '"' + ob.getFirmanteCargoPadreAsunto() + '"'
									: "");

					// - - -

					builder.append(',');
					builder.append(ob.getAreaPadreAsunto() != null ? escapeText(ob.getAreaPadreAsunto()) : "");

					builder.append(',');
					builder.append(ob.getPromotorPadreAsunto() != null ? '"' + ob.getPromotorPadreAsunto() + '"' : "");

					builder.append(',');
					builder.append(ob.getTipoAsuntoPadreAsunto());

					// - - -

					builder.append(',');
					builder.append(
							ob.getRemitentePadreAsunto() != null ? escapeText(ob.getRemitentePadreAsunto()) : "");

					// - - -
					builder.append(',');
					builder.append('"' + ob.getStatusAsuntoPadreAsunto() + '"');

					builder.append('\n');
				}
				String outputResult = builder.toString();

				outputStream.write(outputResult.getBytes("UTF-8"));
				outputStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getLocalizedMessage());
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}

	}

	@ApiOperation(value = "Obtiene todas las areas destino de las respuestas rechazadas")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })
	@RequestMapping(value = "/respuesta/rechazadas/areasDestino", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Area>> respuestasRechazadasAreasRemitentes(
			@RequestBody(required = true) RequestWrapper<RespuestaConsulta> body) {

		body.setBeginAt(null);
		body.setSize(null);

		ResponseEntity<RequestWrapper<List<RespuestaConsulta>>> response_ = //
				consultar(body);

		List<Area> response = response_.getBody().getObject().parallelStream()//
				.map(RespuestaConsulta::getAreaDestinoId) //
				.distinct() //
				.map(i -> mngrArea.fetch(i)) //
				.collect(Collectors.toList());

		return new ResponseEntity<List<Area>>(response, HttpStatus.OK);

	}

	/**
	 * 
	 * @author Alfredo Morales
	 * @version 1.0
	 *
	 */
	class RespuestaConsultaComparator implements Comparator<RespuestaConsulta> {
		@Override
		public int compare(RespuestaConsulta a1, RespuestaConsulta a2) {

			Date a2_fechaRegistro = a2.getFechaRegistro();
			Date a1_fechaRegistro = a1.getFechaRegistro();

			if (a1_fechaRegistro == null || a2_fechaRegistro == null)
				return 0;

			return a2_fechaRegistro.compareTo(a1_fechaRegistro);
		}

	}

	/**
	 * Registrar.
	 *
	 * @param resp the resp
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Registrar respuesta recibida", notes = "Registra una respuesta en el area indicada")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/respuesta/registrar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RespuestaConsulta> registrar(//
			@RequestBody(required = true) Respuesta resp) throws Exception {

		try {

			log.debug("Registrando la respuesta :: " + resp);

			// variable apra verificar si pudo ser actualizada
			Respuesta respAceptada = null;

			// variable para estatus aceptada o rechazada en cadena
			String option = "";

			// verificar como identificar una copia respuesta
			boolean isCopiaRespuesta = false;

			if (isCopiaRespuesta) {
				// tabla COPIASRESPUESTAS
				option = "C";

			} else {
				// verificar funcionalidad archivistica
				boolean isArchivistica = false;

				boolean aceptada = resp.getStatus().getIdStatus() != Status.RECHAZADO;

				option = (aceptada ? "A" : (isCopiaRespuesta ? "C" : "R"));

				if ("Y".equals(getParamApp("WSRECORDM")) && isArchivistica && aceptada) {

					// Validar que todos los documentos del asunto estan
					// clasificados archivisticamente
					boolean documentosClasificados = false;

					if (documentosClasificados && aceptada) {
						respAceptada = aceptarRespuesta(resp, option);
					} else {
						log.error("Los documentos del expediente no estan clasificados");
					}

				} else {
					respAceptada = aceptarRespuesta(resp, option);
				}

			}

			if (respAceptada != null) {

				RespuestaConsulta rc = mngrRespuestaConsulta.fetch(respAceptada.getIdRespuesta());

				return new ResponseEntity<RespuestaConsulta>(rc, HttpStatus.OK);

			} else

				return new ResponseEntity<RespuestaConsulta>(new RespuestaConsulta(), HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Aceptar rechazar respuesta.
	 *
	 * @param resp   the resp
	 * @param option the option
	 * @return the respuesta
	 * @throws Exception the exception
	 */
	/*
	 * @SuppressWarnings("unused") private Respuesta aceptarRespuesta2(Respuesta
	 * resp, String option) throws Exception {
	 * 
	 * Respuesta respuesta = null;
	 * 
	 * // validar si es rechazado que tenga comentario de rechazo if
	 * (option.equals("R") && StringUtils.isBlank(resp.getComentarioRechazo())) {
	 * 
	 * throw new
	 * Exception("Debe de capturar el comentario cuando rechaza una respuesta");
	 * 
	 * } else {
	 * 
	 * respuesta = mngrRespuesta.fetch(resp.getIdRespuesta());
	 * 
	 * // validar que la respuesta se encuentre en el estatus correcto if
	 * ("A".equals(option) || "R".equals(option)) {
	 * 
	 * if (respuesta.getStatus().getIdStatus() == Status.PROCESO ||
	 * respuesta.getStatus().getIdStatus() == Status.CONCLUIDO ||
	 * respuesta.getStatus().getIdStatus() == Status.RECHAZADO) {
	 * 
	 * throw new Exception(
	 * "La Respuesta no pudo ser actualizada, verifique que no se haya aceptado/rechazado previamente"
	 * ); }
	 * 
	 * }
	 * 
	 * Asunto asunto = mngrAsunto.fetch(resp.getIdAsunto());
	 * 
	 * // se substituye el procedure WSASUNTOACTESTATUS con el siguiente // bloque
	 * boolean isExterno = (asunto.getAreaDestino() != null &&
	 * "E".equals(asunto.getAreaDestino().getInstitucion().getTipo()) ? true :
	 * false);
	 * 
	 * // actualizar idEstatusRespuesta if (isExterno) {
	 * 
	 * if ("A".equals(option)) {
	 * 
	 * respuesta.setStatus(mngrStatus.fetch(Status.PROCESO)); if
	 * (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty()) for
	 * (CopiaRespuesta copia : respuesta.getCopias()) {
	 * copia.setStatus(mngrStatus.fetch(Status.PROCESO)); }
	 * 
	 * } else {
	 * 
	 * respuesta.setStatus(mngrStatus.fetch(Status.RECHAZADO)); if
	 * (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty()) for
	 * (CopiaRespuesta copia : respuesta.getCopias()) {
	 * copia.setStatus(mngrStatus.fetch(Status.RECHAZADO)); }
	 * 
	 * } }
	 * 
	 * // obtener estampa desde firma Timestamp timeStamp = new Timestamp();
	 * timeStamp.setTipo(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO);
	 * 
	 * String stampedData = getStampedData(respuesta, timeStamp.getTipo());
	 * 
	 * Map<String, Object> time = firmaEndPoint.getTime(stampedData,
	 * TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO.getTipoString());
	 * 
	 * String timestamp = (String) time.get("Tiempo");
	 * 
	 * timeStamp.setTimestamp(timestamp);
	 * 
	 * // obtener la lista de estampas de la respuesta
	 * respuesta.getTimestamps().add(timeStamp);
	 * respuesta.setFechaAcuse(SignatureUtil.timestampToDate(timestamp));
	 * 
	 * // se sustituye el procedure RESPUESTA_AR con el siguiente bloque //
	 * SINCRONIZACION ESTATUSTURNO <-> ESTATUSASUNTO // if ("A".equals(option)) {
	 * 
	 * // status = 3 ACEPTADO
	 * respuesta.setStatus(mngrStatus.fetch(Status.CONCLUIDO)); if
	 * (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty()) for
	 * (CopiaRespuesta copia : respuesta.getCopias()) {
	 * copia.setStatus(mngrStatus.fetch(Status.CONCLUIDO)); }
	 * 
	 * // si es respuesta concluida entonces le cambia el estatus al // asunto a
	 * conlcuido if ("C".equals(respuesta.getTipoRespuesta().getIdTipoRespuesta()))
	 * { asunto.setStatusAsunto(mngrStatus.fetch(Status.CONCLUIDO));
	 * asunto.setStatusTurno(mngrStatus.fetch(Status.CONCLUIDO)); if
	 * (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty()) for
	 * (CopiaRespuesta copia : respuesta.getCopias()) {
	 * copia.setStatus(mngrStatus.fetch(Status.CONCLUIDO)); }
	 * 
	 * } else { // si el turno no esta concluido, actualiza el estatus del // turno
	 * = al estatus del asunto if (asunto.getStatusTurno() != null &&
	 * asunto.getStatusTurno().getIdStatus() != Status.CONCLUIDO) {
	 * asunto.setStatusTurno(asunto.getStatusAsunto());
	 * 
	 * } }
	 * 
	 * } else {
	 * 
	 * respuesta.setStatus(mngrStatus.fetch(Status.RECHAZADO));
	 * respuesta.setComentarioRechazo(resp.getComentarioRechazo()); if
	 * (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty()) for
	 * (CopiaRespuesta copia : respuesta.getCopias()) {
	 * copia.setStatus(mngrStatus.fetch(Status.RECHAZADO)); }
	 * 
	 * }
	 * 
	 * // Validar que las copias no tengan area null para los tipo Internos (Cuando
	 * le // cambian el titular al area no coninside el titular guardado en la copia
	 * con // el titular que trae la vista) int contId = 0; for (CopiaRespuesta
	 * copia : respuesta.getCopias()) { // Validar si la copia trae el area null y
	 * si es de tipo interno if (null == copia.getArea() &&
	 * copia.getIdSubTipoAsunto().equals(SubTipoAsunto.C)) { // Consulta los idAreas
	 * de las copias que no coinsida el titular con el titular // del area String
	 * sqlListIdArea =
	 * "select cr.IDAREA from COPIASRESPUESTA cr where cr.IDRESPUESTA=" +
	 * respuesta.getIdRespuesta() +
	 * " and cr.IDDESTINATARIO NOT IN(Select a.TITULARUSUARIO FROM Areas a where cr.IDAREA=a.IDAREA)"
	 * ;
	 * 
	 * @SuppressWarnings("unchecked") List<BigDecimal> areaIds = (List<BigDecimal>)
	 * mngrRespuesta.execNativeQuery(sqlListIdArea, null); if (!areaIds.isEmpty()) {
	 * // Consulta el destinatario para setearlo en la copia
	 * copia.setArea(getDestinatarioInterno(areaIds.get(contId).intValue()));
	 * contId++; } } } // actualizar registros mngrAsunto.update(asunto);
	 * mngrRespuesta.update(respuesta);
	 * 
	 * IEndpoint endpointDispatcher = EndpointDispatcher.getInstance();
	 * 
	 * if (!isExterno) {
	 * 
	 * log.debug("Haciendo el link de los documentos de la respuesta");
	 * List<DocumentoRespuesta> documentos =
	 * getDocumentosRespuesta(respuesta.getIdRespuesta());
	 * 
	 * Asunto asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());
	 * 
	 * for (DocumentoRespuesta documento : documentos) {
	 * log.debug("Haciendo el link del documento " + documento.getObjectName());
	 * 
	 * if (!endpointDispatcher.link(documento.getObjectId(),
	 * asuntoPadre.getContentId())) {
	 * log.error("Error al momento de hacer el link del documento " +
	 * documento.getObjectId() + " al folder del asunto " +
	 * asuntoPadre.getIdAsunto()); } }
	 * 
	 * } else {
	 * 
	 * // Para mover los documentos de las respuestas Asunto asuntoPadre =
	 * mngrAsunto.fetch(asunto.getIdAsuntoPadre()); String pathFolio =
	 * endpointDispatcher.getObjectPath(asuntoPadre.getContentId()); String
	 * pathExterno = getParamApp("CABINETEXTERNO") + "/" + asunto.getIdAsunto() +
	 * "_" + respuesta.getIdRespuesta();
	 * 
	 * endpointDispatcher.moverDocumentos(pathExterno, pathFolio); }
	 * 
	 * }
	 * 
	 * return respuesta; }
	 */

	/**
	 * Aceptar rechazar respuesta. Async.
	 * 
	 * @param resp
	 * @param option
	 * @return
	 * @throws Exception
	 */
	private Respuesta aceptarRespuesta(Respuesta resp, String option) throws Exception {

		Respuesta respuesta = null;

		// validar si es rechazado que tenga comentario de rechazo
		if (option.equals("R") && StringUtils.isBlank(resp.getComentarioRechazo())) {

			throw new Exception("Debe de capturar el comentario cuando rechaza una respuesta");

		} else {

			respuesta = mngrRespuesta.fetch(resp.getIdRespuesta());

			// validar que la respuesta se encuentre en el estatus correcto
			if ("A".equals(option) || "R".equals(option)) {

				if (respuesta.getStatus().getIdStatus() == Status.PROCESO
						|| respuesta.getStatus().getIdStatus() == Status.CONCLUIDO
						|| respuesta.getStatus().getIdStatus() == Status.RECHAZADO) {

					throw new Exception(
							"La Respuesta no pudo ser actualizada, verifique que no se haya aceptado/rechazado previamente");
				}

			}

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			Usuario usuarioSesion = mngrUsuario.fetch(userId);
			respuesta.setAceptoRespuesta(usuarioSesion);

			process(respuesta, option, resp.getComentarioRechazo());

		}

		return respuesta;
	}

	/**
	 * Gets the destinatario interno.
	 *
	 * @param idArea the id area
	 * @return the destinatario interno
	 */
	public Destinatario getDestinatarioInterno(Integer idArea, String identificador) {

		String query = "SELECT d from Destinatario d WHERE d.idArea = " + idArea + " AND d.identificador = '"
				+ identificador + "' AND d.idSubTipoAsunto='" + SubTipoAsunto.C + "'";

		List<Destinatario> destinatario = (List<Destinatario>) mngrDestinatario.execQuery(query);

		if (null == destinatario || destinatario.isEmpty() || destinatario.size() > 1) {
			return null;
		}

		return destinatario.get(0);
	}

	/**
	 * Gets the stamped data.
	 *
	 * @param resp   the resp
	 * @param tipots the tipots
	 * @return the stamped data
	 */
	public String getStampedData(Respuesta resp, TipoTimestamp tipots) {
		String toBeStamped = resp.getIdRespuesta() + "-" + tipots.getTipo();

		return toBeStamped;
	}

	/**
	 * Obtiene la lista de documentos asociados a la Respuesta.
	 *
	 * @param idRespuesta Identificador de la Respuesta
	 * @return Lista de documentos asociados a la Respuesta
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentoRespuesta> getDocumentosRespuesta(Integer idRespuesta) throws Exception {

		List<Criterion> restrictions = new ArrayList<Criterion>();

		restrictions.add(Restrictions.eq("idRespuesta", idRespuesta));

		return (List<DocumentoRespuesta>) mngrDocsRespuesta.search(restrictions);
	}

	/**
	 * 
	 * @param idArea
	 * @param respuestaConsulta
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Criterion> createCriterionConsultar(Integer idArea, RespuestaConsulta respuestaConsulta,
			Map<String, Object> params, boolean forCount) {
		List<Criterion> restrictions = new ArrayList<Criterion>();
		String tipoConsulta = (String) params.get("tipoConsulta");

		// TODO VALIDAR Q VENGA EL ID DONDE DEBE
		if (respuestaConsulta.getIdAsunto() != null)
			restrictions.add(Restrictions.eq("idAsunto", respuestaConsulta.getIdAsunto()));

		// status INICIO
		List<Integer> listStatusAsunto = new ArrayList<>();
		if (params.get("status") != null) {
			Map<?, ?> subparams = (LinkedHashMap<?, ?>) params.get("status");
			if (subparams.get("in") != null) {
				List<String> ids = (List<String>) subparams.get("in");
				for (String id : ids) {
					listStatusAsunto.add(StatusAsunto.valueOf(id).ordinal());
				}
			}
		}
		// status FIN

		// Condición agregada para buscar las respuestas en general
		if (tipoConsulta != null && tipoConsulta.equals("A")) {
			if (null != respuestaConsulta.getArea() && null != respuestaConsulta.getArea().getIdArea()
					&& null != respuestaConsulta.getAreaDestinoId()) {

				if (!idArea.equals(respuestaConsulta.getAreaDestinoId())
						&& !respuestaConsulta.getArea().getIdArea().equals(idArea)) {
					throw new BadRequestException();
				}

				SimpleExpression eq_ = Restrictions.eq("areaId", respuestaConsulta.getArea().getIdArea());
				SimpleExpression eq2_ = Restrictions.eq("areaDestinoId", respuestaConsulta.getAreaDestinoId());
				SimpleExpression eq3_ = Restrictions.eq("areaDestinoId", respuestaConsulta.getAreaDestinoId());

				if (respuestaConsulta.isBandRespRech())
					restrictions.add(Restrictions.and(eq_, forCount ? eq3_ : eq2_));

				Disjunction disjunction = Restrictions.disjunction();
				Disjunction disjunction2 = Restrictions.disjunction();

				Conjunction todo1 = Restrictions.and(eq_);
				Conjunction todo2 = Restrictions.and(Restrictions.and(forCount ? eq3_ : eq2_));

				if (respuestaConsulta.getFolioAreaAsunto() != null
						&& respuestaConsulta.getFolioAreaAsuntoPadreAsunto() != null) {

					todo1.add(Restrictions.and(EscapedLikeRestrictions.ilike("folioAreaAsunto",
							respuestaConsulta.getFolioAreaAsunto(), MatchMode.ANYWHERE)));

					todo2.add(Restrictions.and(EscapedLikeRestrictions.ilike("folioAreaAsuntoPadreAsunto",
							respuestaConsulta.getFolioAreaAsuntoPadreAsunto(), MatchMode.ANYWHERE)));
				}

				if (listStatusAsunto.size() > 0) {
					boolean hasConcluido = listStatusAsunto.indexOf(StatusAsunto.CONCLUIDO.ordinal()) != -1;
					todo1.add(Restrictions.in("status.idStatus", listStatusAsunto));

					if (hasConcluido)
						todo2.add(Restrictions.in("status.idStatus", StatusAsunto.CONCLUIDO.ordinal()));
					else
						todo2.add(Restrictions.in("status.idStatus", StatusAsunto.NINGUNO.ordinal())); // para que no
																										// traiga
																										// resultados
				} else
					todo2.add(Restrictions.in("status.idStatus", StatusAsunto.NINGUNO.ordinal())); // para que no traiga
																									// resultados

				disjunction.add(todo1);
				disjunction2.add(todo2);

				restrictions.add(Restrictions.or(disjunction, disjunction2));
			}
//            else {
//                restrictions.add(Restrictions.or(eq_, forCount ? eq3_ : eq2_));
//            }
		} else {

			// se elimina para poder consultar respuestas recibidas de otras areas
			if (null != respuestaConsulta.getArea()) {
				if (null != respuestaConsulta.getArea().getIdArea()) {
					restrictions.add(Restrictions.eq("areaId", respuestaConsulta.getArea().getIdArea()));
				}

				if (null != respuestaConsulta.getArea().getInstitucion()
						&& null != respuestaConsulta.getArea().getInstitucion().getIdInstitucion()) {
					restrictions.add(Restrictions.eq("areaInstitucionId",
							respuestaConsulta.getArea().getInstitucion().getIdInstitucion()));
				}

				if (null != respuestaConsulta.getArea().getTitular()) {
					if (null != respuestaConsulta.getArea().getTitular().getId())
						restrictions.add(
								Restrictions.eq("areaTitularId", respuestaConsulta.getArea().getTitular().getId()));
					if (null != respuestaConsulta.getArea().getTitular().getArea()) {
						if (null != respuestaConsulta.getArea().getTitular().getArea().getIdArea())
							restrictions.add(Restrictions.eq("areaTitularAreaId",
									respuestaConsulta.getArea().getTitular().getArea().getIdArea()));
						if (null != respuestaConsulta.getArea().getTitular().getArea().getInstitucion())
							if (null != respuestaConsulta.getArea().getTitular().getArea().getInstitucion()
									.getIdInstitucion())
								restrictions.add(Restrictions.eq("areaTitularAreaInstitucionId", respuestaConsulta
										.getArea().getTitular().getArea().getInstitucion().getIdInstitucion()));
					}
				}
			}

			if (null != respuestaConsulta.getAreaDestinoId()) {
				if (!idArea.equals(respuestaConsulta.getAreaDestinoId())
						&& !respuestaConsulta.getArea().getIdArea().equals(idArea)) {
					throw new BadRequestException();
				}

				restrictions.add(Restrictions.eq("areaDestinoId", respuestaConsulta.getAreaDestinoId()));
			}

			if (null != respuestaConsulta.getAreaDestinoInstitucionId()) {
				restrictions.add(
						Restrictions.eq("areaDestinoInstitucionId", respuestaConsulta.getAreaDestinoInstitucionId()));

			}

			if (null != respuestaConsulta.getAreaDestinoTitularId()) {
				restrictions.add(Restrictions.eq("areaDestinoTitularId", respuestaConsulta.getAreaDestinoTitularId()));
			}

			if (null != respuestaConsulta.getAreaDestinoTitularAreaId())
				restrictions.add(
						Restrictions.eq("areaDestinoTitularAreaId", respuestaConsulta.getAreaDestinoTitularAreaId()));

			if (null != respuestaConsulta.getAreaDestinoTitularAreaInstitucionId())
				restrictions.add(Restrictions.eq("areaDestinoTitularAreaInstitucionId",
						respuestaConsulta.getAreaDestinoTitularAreaInstitucionId()));

			if (null != respuestaConsulta.getAreaTitularId()) {
				restrictions.add(Restrictions.eq("areaTitularId", respuestaConsulta.getAreaTitularId()));
			}

			if (null != respuestaConsulta.getAreaTitularAreaId())
				restrictions.add(Restrictions.eq("areaTitularAreaId", respuestaConsulta.getAreaTitularAreaId()));

			if (null != respuestaConsulta.getAreaTitularAreaInstitucionId())
				restrictions.add(Restrictions.eq("areaTitularAreaInstitucionId",
						respuestaConsulta.getAreaTitularAreaInstitucionId()));

		}

		if (respuestaConsulta.getStatus() != null) {
			restrictions.add(Restrictions.eq("status", mngrStatus.fetch(respuestaConsulta.getStatus().getIdStatus())));
		}

		if (respuestaConsulta.getIdRespuesta() != null)
			restrictions.add(Restrictions.idEq(respuestaConsulta.getIdRespuesta()));

		if (StringUtils.isNotBlank(respuestaConsulta.getComentario())
				|| StringUtils.isNotBlank(respuestaConsulta.getComentarioRechazo())) {

			if (StringUtils.isNotBlank(respuestaConsulta.getComentario())
					&& StringUtils.isNotBlank(respuestaConsulta.getComentarioRechazo())) {
				if (respuestaConsulta.getComentario().equals(respuestaConsulta.getComentarioRechazo())) {
					restrictions.add(Restrictions.or(
							EscapedLikeRestrictions.ilike("comentarioRechazo", respuestaConsulta.getComentarioRechazo(),
									MatchMode.ANYWHERE),
							EscapedLikeRestrictions.ilike("comentario", respuestaConsulta.getComentario(),
									MatchMode.ANYWHERE)));
				} else {
					if (StringUtils.isNotBlank(respuestaConsulta.getComentario()))
						restrictions.add(EscapedLikeRestrictions.ilike("comentario", respuestaConsulta.getComentario(),
								MatchMode.ANYWHERE));

					if (StringUtils.isNotBlank(respuestaConsulta.getComentarioRechazo()))
						restrictions.add(EscapedLikeRestrictions.ilike("comentarioRechazo",
								respuestaConsulta.getComentarioRechazo(), MatchMode.ANYWHERE));
				}
			} else {

				if (StringUtils.isNotBlank(respuestaConsulta.getComentario()))
					restrictions.add(EscapedLikeRestrictions.ilike("comentario", respuestaConsulta.getComentario(),
							MatchMode.ANYWHERE));

				if (StringUtils.isNotBlank(respuestaConsulta.getComentarioRechazo()))
					restrictions.add(EscapedLikeRestrictions.ilike("comentarioRechazo",
							respuestaConsulta.getComentarioRechazo(), MatchMode.ANYWHERE));
			}
		}

		if ((null != respuestaConsulta.getTipoRespuestaId()))
			restrictions.add(Restrictions.eq("tipoRespuestaId", respuestaConsulta.getTipoRespuestaId()));

		if (null != respuestaConsulta.getInfomexZip()) {
			restrictions.add(Restrictions.eq("infomexZip", respuestaConsulta.getInfomexZip()));
		}
		if (respuestaConsulta.getFolioRespuesta() != null
				&& StringUtils.isNotBlank(respuestaConsulta.getFolioRespuesta())) {
			restrictions.add(Restrictions.eq("folioRespuesta", respuestaConsulta.getFolioRespuesta()));
		}

		if (respuestaConsulta.getIdAsunto() != null) {
			restrictions.add(Restrictions.eq("idAsunto", respuestaConsulta.getIdAsunto()));
		}

		if (respuestaConsulta.getIdAsuntoOrigen() != null) {
			restrictions.add(Restrictions.eq("idAsuntoOrigen", respuestaConsulta.getIdAsuntoOrigen()));
		}

		if (tipoConsulta == null || tipoConsulta.equals("G") || tipoConsulta.equals("R")) {
			if (respuestaConsulta.getFolioAreaAsunto() != null) {
				restrictions.add(EscapedLikeRestrictions.ilike("folioAreaAsunto",
						respuestaConsulta.getFolioAreaAsunto(), MatchMode.ANYWHERE));
			}

			if (respuestaConsulta.getFolioAreaAsuntoPadreAsunto() != null) {
				restrictions.add(EscapedLikeRestrictions.ilike("folioAreaAsuntoPadreAsunto",
						respuestaConsulta.getFolioAreaAsuntoPadreAsunto(), MatchMode.ANYWHERE));
			}

			if (params.get("status") != null) {
				if (listStatusAsunto.size() > 0) {
					restrictions.add(Restrictions.in("status.idStatus", listStatusAsunto));
				}
			}
		}

		// if (params != null && !params.isEmpty())
		{
			if (params.get("tipoRespuesta") != null) {
				Map<?, ?> subparams = (LinkedHashMap<?, ?>) params.get("tipoRespuesta");

				if (subparams.get("in") != null) {
					List<String> ids = (List<String>) subparams.get("in");
					List<String> listTipoRespuesta = new ArrayList<>();

					for (String id : ids) {
						listTipoRespuesta.add(id);
					}

					if (listTipoRespuesta.size() > 0) {
						restrictions.add(Restrictions.in("tipoRespuestaId", listTipoRespuesta));
					}
				}
			}

			if (params.get("tipoAsunto") != null) {
				Map<?, ?> subparams = (LinkedHashMap<?, ?>) params.get("tipoAsunto");

				if (subparams.get("in") != null) {
					List<String> ids = (List<String>) subparams.get("in");
					List<TipoAsunto> listTipoAsunto = new ArrayList<TipoAsunto>();

					for (String id : ids) {
						listTipoAsunto.add(TipoAsunto.fromTipo(id));
					}

					if (listTipoAsunto.size() > 0) {
						restrictions.add(Restrictions.in("tipoAsunto", listTipoAsunto));
					}
				}
			}
		}

		restrictions.add(Restrictions.not(Restrictions.eq("tipoAsunto", TipoAsunto.ASUNTO)));

		return restrictions;
	}

	/**
	 * 
	 * @param respuesta
	 * @param option
	 * @param comentarioRechazo
	 * @throws Exception
	 */
	@Async
	public synchronized void process(Respuesta respuesta, String option, String comentarioRechazo) //
			throws Exception {

		Asunto asunto = mngrAsunto.fetch(respuesta.getIdAsunto());

		// se substituye el procedure WSASUNTOACTESTATUS con el siguiente
		// bloque
		boolean isExterno = (asunto.getAreaDestino() != null
				&& "E".equals(asunto.getAreaDestino().getInstitucion().getTipo()) ? true : false);

		// actualizar idEstatusRespuesta
		if (isExterno) {

			if ("A".equals(option)) {

				respuesta.setStatus(mngrStatus.fetch(Status.PROCESO));
				if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty())
					for (CopiaRespuesta copia : respuesta.getCopias()) {
						copia.setStatus(mngrStatus.fetch(Status.PROCESO));
					}

			} else {

				respuesta.setStatus(mngrStatus.fetch(Status.RECHAZADO));
				if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty())
					for (CopiaRespuesta copia : respuesta.getCopias()) {
						copia.setStatus(mngrStatus.fetch(Status.RECHAZADO));
					}

			}
		}

		// obtener estampa desde firma
		Timestamp timeStamp = new Timestamp();
		timeStamp.setTipo(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO);

		String stampedData = getStampedData(respuesta, timeStamp.getTipo());

		Map<String, Object> time = firmaEndPoint.getTime(stampedData,
				TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO.getTipoString());

		String timestamp = (String) time.get("Tiempo");

		timeStamp.setTimestamp(timestamp);

		// obtener la lista de estampas de la respuesta
		respuesta.getTimestamps().add(timeStamp);
		respuesta.setFechaAcuse(SignatureUtil.timestampToDate(timestamp));

		// se sustituye el procedure RESPUESTA_AR con el siguiente bloque
		/* SINCRONIZACION ESTATUSTURNO <-> ESTATUSASUNTO */
		if ("A".equals(option)) {

			// status = 3 ACEPTADO
			respuesta.setStatus(mngrStatus.fetch(Status.CONCLUIDO));
			if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty())
				for (CopiaRespuesta copia : respuesta.getCopias()) {
					if (!copia.getStatus().getIdStatus().equals(Status.ATENDIDO))
						copia.setStatus(mngrStatus.fetch(Status.CONCLUIDO));
				}

			// si es respuesta concluida entonces le cambia el estatus al
			// asunto a conlcuido
			if ("C".equals(respuesta.getTipoRespuesta().getIdTipoRespuesta())) {
				asunto.setStatusAsunto(mngrStatus.fetch(Status.CONCLUIDO));
				asunto.setStatusTurno(mngrStatus.fetch(Status.CONCLUIDO));
				if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty())
					for (CopiaRespuesta copia : respuesta.getCopias()) {
						if (!copia.getStatus().getIdStatus().equals(Status.ATENDIDO))
							copia.setStatus(mngrStatus.fetch(Status.CONCLUIDO));
					}

			} else {
				// si el turno no esta concluido, actualiza el estatus del
				// turno = al estatus del asunto
				if (asunto.getStatusTurno() != null && asunto.getStatusTurno().getIdStatus() != Status.CONCLUIDO) {
					asunto.setStatusTurno(asunto.getStatusAsunto());

				}
			}

		} else {

			respuesta.setStatus(mngrStatus.fetch(Status.RECHAZADO));
			respuesta.setComentarioRechazo(comentarioRechazo);
			if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty())
				for (CopiaRespuesta copia : respuesta.getCopias()) {
					copia.setStatus(mngrStatus.fetch(Status.RECHAZADO));
				}

		}

		// Validar que las copias no tengan area null para los tipo Internos (Cuando le
		// cambian el titular al area no coninside el titular guardado en la copia con
		// el titular que trae la vista)
		int contId = 0;
		for (CopiaRespuesta copia : respuesta.getCopias()) {
			// Validar si la copia trae el area null y si es de tipo interno
			if (null == copia.getArea() && copia.getIdSubTipoAsunto().equals(SubTipoAsunto.C)) {
				// Consulta los idAreas de las copias que no coinsida el titular con el titular
				// del area
				String sqlListIdArea = "select cr.IDAREA from COPIASRESPUESTA cr where cr.IDRESPUESTA="
						+ respuesta.getIdRespuesta()
						+ " and cr.IDDESTINATARIO NOT IN(Select a.TITULARUSUARIO FROM Areas a where cr.IDAREA=a.IDAREA)";
				@SuppressWarnings("unchecked")
				List<BigDecimal> areaIds = (List<BigDecimal>) mngrRespuesta.execNativeQuery(sqlListIdArea, null);
				if (!areaIds.isEmpty()) {
					// Busca el titular del area ya que el iddestinatario no coincide
					Area area = mngrArea.fetch(areaIds.get(contId).intValue());
					// Consulta los datos del destinatario para setearlo en la copia
					copia.setArea(getDestinatarioInterno(area.getIdArea(), area.getTitular().getId()));
					contId++;
				}
			} else if (copia.getArea() != null && copia.getIdSubTipoAsunto().equals(SubTipoAsunto.C)) {
				// Valida que el titular sea el mismo que el destinatario
				// (Cuando cambian el titular al area no coninside el titular guardado en la
				// copia con el titular que trae la vista)
				Area area = mngrArea.fetch(copia.getArea().getIdArea());

				// si area.getTitular() == null no es un area normal, entonces dejamos el area
				// como esta.
				if (area != null && area.getTitular() != null
						&& !(copia.getArea().getIdentificador()).equals(area.getTitular().getId())) {
					// Consulta los datos del destinatario para setearlo en la copia
					copia.setArea(getDestinatarioInterno(area.getIdArea(), area.getTitular().getId()));
					contId++;
				}
			}
		}
		// actualizar registros
		mngrAsunto.update(asunto);
		mngrRespuesta.update(respuesta);

		IEndpoint endpointDispatcher = EndpointDispatcher.getInstance();
		try {
			if (!isExterno) {

				log.debug("Haciendo el link de los documentos de la respuesta");
				List<DocumentoRespuesta> documentos = getDocumentosRespuesta(respuesta.getIdRespuesta());

				Asunto asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());

				for (DocumentoRespuesta documento : documentos) {
					log.debug("Haciendo el link del documento " + documento.getObjectName());

					if (!endpointDispatcher.link(documento.getObjectId(), asuntoPadre.getContentId())) {
						log.error("Error al momento de hacer el link del documento " + documento.getObjectId()
								+ " al folder del asunto " + asuntoPadre.getIdAsunto());
					}
				}

			} else {

				// Para mover los documentos de las respuestas
				Asunto asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());
				String pathFolio = endpointDispatcher.getObjectPath(asuntoPadre.getContentId());
				String pathExterno = getParamApp("CABINETEXTERNO") + "/" + asunto.getIdAsunto() + "_"
						+ respuesta.getIdRespuesta();

				endpointDispatcher.moverDocumentos(pathExterno, pathFolio);
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
		}

	}

	/**
	 * @param text
	 * @return
	 */
	private String escapeText(String text) {
		return '"' + text.replace("\"", "\"\"") + '"';
	}

}
