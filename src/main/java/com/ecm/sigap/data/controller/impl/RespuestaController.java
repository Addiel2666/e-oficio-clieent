
/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.NotImplementedException;
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

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.FolioPS;
import com.ecm.sigap.data.model.FolioPSMultiple;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.RespuestaConsulta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.TipoRespuesta;
import com.ecm.sigap.data.model.util.ConfiguracionArea;
import com.ecm.sigap.data.model.util.CopiaRespuesta;
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
public class RespuestaController extends CustomRestController implements RESTController<Respuesta> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RespuestaController.class);

	/**
	 * Referencia hacia el REST controller de {@link DocumentoRespuestaController}.
	 */
	@Autowired
	private DocumentoRespuestaController documentoRespuestaController;

	/** Referencia hacia el REST controller {@link RepositoryController}. */
	@Autowired
	private RepositoryController repositorioController;

	/** Referencia hacia el REST controller {@link RepositoryController}. */
	@Autowired
	private ConfiguracionController configuracionController;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta respuestas", notes = "Consulta respuestas de la lista")
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
	@Override
	@RequestMapping(value = "/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Respuesta respuesta) {

		List<RespuestaConsulta> lst = new ArrayList<RespuestaConsulta>();
		log.info("Parametros de busqueda :: " + respuesta);

		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			// se elimina para poder consultar respuestas recibidas de
			// otras areas
			if ((null != respuesta.getArea()) && (null != respuesta.getArea().getIdArea())) {
				restrictions.add(Restrictions.eq("area.idArea", respuesta.getArea().getIdArea()));
			}

			if ((null != respuesta.getAreaDestino()) //
					&& (null != respuesta.getAreaDestino().getIdArea())) {

				if (!idArea.equals(respuesta.getAreaDestino().getIdArea()) //
						&& !respuesta.getArea().getIdArea().equals(idArea)) {
					return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
				}
				restrictions.add(Restrictions.eq("areaDestinoId", //
						respuesta.getAreaDestino().getIdArea()));
			}

			if (respuesta.getStatus() != null) {
				restrictions.add(Restrictions.eq("status", //
						mngrStatus.fetch(respuesta.getStatus().getIdStatus())));
			}

			if (respuesta.getIdRespuesta() != null)
				restrictions.add(Restrictions.idEq(respuesta.getIdRespuesta()));

			if (respuesta.getIdAsunto() != null)
				restrictions.add(Restrictions.eq("idAsunto", respuesta.getIdAsunto()));

			if (respuesta.getComentario() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("comentario", //
						respuesta.getComentario(), MatchMode.ANYWHERE));

			if (respuesta.getComentarioRechazo() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("comentarioRechazo", //
						respuesta.getComentarioRechazo(), MatchMode.ANYWHERE));

			if ((null != respuesta.getTipoRespuesta()) //
					&& (null != respuesta.getTipoRespuesta().getIdTipoRespuesta()))
				restrictions.add(Restrictions.eq("tipoRespuesta.idTipoRespuesta",
						respuesta.getTipoRespuesta().getIdTipoRespuesta()));

			if (null != respuesta.getInfomexZip()) {
				restrictions.add(Restrictions.eq("infomexZip", respuesta.getInfomexZip()));
			}

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("fechaRegistro"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<RespuestaConsulta>) mngrRespuestaConsulta.search(restrictions, null);

			Collections.sort(lst, new Comparator<RespuestaConsulta>() {
				@Override
				public int compare(RespuestaConsulta a1, RespuestaConsulta a2) {
					return a1.getFechaRegistro().compareTo(a2.getFechaRegistro());
				}
			});

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			return new ResponseEntity<List<?>>(lst, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Gets the respuestas recibidas.
	 *
	 * @param idAsunto Identificador del Asunto a consultar
	 * @return Lista de respuestas recibidas por el asunto
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene respuestas recibidas", notes = "Obtiene la lista de respuestas recibidas por el asunto")
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
	@RequestMapping(value = "/respuesta/recibida", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Respuesta>> getRespuestasRecibidas(
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto) {

		List<Respuesta> lst = new ArrayList<Respuesta>();

		log.info("Parametro de busqueda id asunto ::: " + idAsunto);

		try {
			List<Map<String, Integer>> lstAsunto;
			{
				List<Criterion> restrictionsAsunto = new ArrayList<Criterion>();
				ProjectionList projections = Projections.projectionList();

				projections.add(Projections.property("idAsunto").as("idAsunto"));
				restrictionsAsunto.add(Restrictions.eq("idAsuntoPadre", Integer.valueOf((String) idAsunto)));
				
				if(!Boolean.valueOf(environment.getProperty("enviar.respuesta.concluida"))) {
					restrictionsAsunto.add(Restrictions.in("tipoAsunto", new Object[] { TipoAsunto.TURNO, TipoAsunto.ENVIO }));
				} else {
					restrictionsAsunto.add(Restrictions.in("tipoAsunto", new Object[] { TipoAsunto.TURNO, TipoAsunto.ENVIO,
							TipoAsunto.COPIA }));
				}
				

				lstAsunto = mngrAsunto.search(restrictionsAsunto, null, projections, null, null);
			}

			// El Asunto que se envio no contiene ninguna respuesta recibida
			if (!lstAsunto.isEmpty()) {

				Object[] arreglo = new Object[lstAsunto.size()];
				int i = 0;
				for (Map<String, Integer> ids : lstAsunto) {
					arreglo[i++] = ids.get("idAsunto");
				}

				List<Criterion> restrictions = new ArrayList<Criterion>();
				restrictions.add(Restrictions.in("idAsunto", arreglo));
				restrictions.add(Restrictions.in("status.idStatus",
						new Object[] { Status.CONCLUIDO, Status.VENCIDO, Status.RESUELTO }));
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc("fechaAcuse"));

				// * * * * * * * * * * * * * * * * * * * * * *
				lst = (List<Respuesta>) mngrRespuesta.search(restrictions, orders);

				Collections.sort(lst, new Comparator<Respuesta>() {
					@Override
					public int compare(Respuesta a, Respuesta b) {
						return b.getFechaAcuse().compareTo(a.getFechaAcuse());
					}
				});

			}

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			return new ResponseEntity<List<Respuesta>>(lst, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<List<Respuesta>>(lst, HttpStatus.OK);
	}

	/**
	 * Guardar nueva respuestas.
	 *
	 * @param nuevaRespuesta the nueva respuesta
	 * @param generarFolio   the generar folio
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Guardar respuesta", notes = "Guarda una nueva respuesta")
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
	@RequestMapping(value = "/respuesta", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Respuesta> save( //
			@RequestBody(required = true) Respuesta nuevaRespuesta, //
			@RequestParam(value = "generarFolio", required = false) String generarFolio //
	) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			if (!esSoloLectura(userId)) {

				log.info("RESPUESTA A GUARDAR :: " + nuevaRespuesta);

				Integer idAsunto = nuevaRespuesta.getIdAsunto();

				if (nuevaRespuesta.getIdRespuesta() == null) {

					// * * * * * * * * * * * * * * * * * * * * * * * *

					nuevaRespuesta.setTipoRespuesta(
							mngrTipoRespuesta.fetch(nuevaRespuesta.getTipoRespuesta().getIdTipoRespuesta()));

					log.debug("CONSULTANDO EL ASUNTO " + idAsunto);
					Asunto asunto_ = mngrAsunto.fetch(idAsunto);

					if (asunto_ == null) {
						throw new BadRequestException();
					}

					// * * * * * * * * * * * * * * * * * * * * * * * *
					log.debug("VALIDANDO PORCENTAJE");
					int error = validaPorcentaje(nuevaRespuesta);

					if (error > 0) {
						if (error == 1)
							return new ResponseEntity<Respuesta>(nuevaRespuesta, HttpStatus.CONFLICT); // yaExisteRespuestaConcluida
						else
							return new ResponseEntity<Respuesta>(nuevaRespuesta,
									HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE); // porcentajeInvalido
					}

					// * * * * * * * * * * * * * * * * * * * * * * * *

					if (generarFolio != null && Boolean.TRUE.toString().equalsIgnoreCase(generarFolio)) {
						log.debug("GENERANDO FOLIORESPUESTA");
						// params.put("idArea",
						// nuevaRespuesta.getAreaDestino().getIdArea());
						// BigDecimal folioRespuesta = (BigDecimal)
						// mngrArea.uniqueResult("generaFolio", params);
						if (nuevaRespuesta.getIdFolioMultiple() != null) {
							nuevaRespuesta.setFolioRespuesta(
									generarFolioRespuesta(true, nuevaRespuesta.getIdFolioMultiple()));
						} else {
							nuevaRespuesta.setFolioRespuesta(generarFolioRespuesta(false, null));
						}

					} else {

						if (!"S/N".equalsIgnoreCase(nuevaRespuesta.getFolioRespuesta())) {

							HashMap<String, Object> params = new HashMap<>();

							params.put("folioRespuesta", nuevaRespuesta.getFolioRespuesta());

							Integer hasRefrences = Integer
									.valueOf(mngrDocsAsunto.uniqueResult("folioRespuestaExiste", params).toString());

							if (hasRefrences > 0) {
								log.error("El número de documento de la respuesta ya existe");
								throw new ConstraintViolationException("El numero de documento ya existe", null); // errorFolioRespuestaExiste
							}

						}

					}

					// Se filtran las copias para que no permita enviar duplicadas.
					if (nuevaRespuesta.getCopias() != null && !nuevaRespuesta.getCopias().isEmpty()) {

						List<CopiaRespuesta> copiasNew = new ArrayList<>();
						for (CopiaRespuesta copia : nuevaRespuesta.getCopias()) {

							Optional<CopiaRespuesta> optCopia = copiasNew.stream()
									.filter(ct -> ct.getIdAsunto().equals(copia.getIdAsunto())
											&& ct.getArea().getIdArea().equals(copia.getArea().getIdArea())
											&& ct.getArea().getIdentificador()
													.equals(copia.getArea().getIdentificador())
											&& ct.getIdSubTipoAsunto().equals(copia.getIdSubTipoAsunto()))
									.findFirst();
							if (!optCopia.isPresent()) {
								copia.setStatus(mngrStatus.fetch(copia.getStatus().getIdStatus()));
								copiasNew.add(copia);
							}
						}

						nuevaRespuesta.setCopias(null);
						nuevaRespuesta.setCopias(copiasNew);
					}
					// * * * * * * * * * * * * * * * * * * * * * * * *

					nuevaRespuesta.setStatus(mngrStatus.fetch(nuevaRespuesta.getStatus().getIdStatus()));

					nuevaRespuesta.setAreaDestino(mngrArea.fetch(nuevaRespuesta.getAreaDestino().getIdArea()));

					nuevaRespuesta.setArea(mngrArea.fetch(nuevaRespuesta.getArea().getIdArea()));

					nuevaRespuesta.setStatus(mngrStatus.fetch(nuevaRespuesta.getStatus().getIdStatus()));

					// * * * * * * * * * * * * * * * * * * * * * * * *

					List<Timestamp> timestamps = new ArrayList<>();

					Timestamp timeStamp = new Timestamp();
					timeStamp.setTipo(TipoTimestamp.TIMESTAMP_REGISTRO);

					String stampedData = getStampedData(nuevaRespuesta, timeStamp.getTipo());

					Map<String, Object> time = firmaEndPoint.getTime(stampedData,
							TipoTimestamp.TIMESTAMP_REGISTRO.getTipoString());

					String timestamp = time.get("Tiempo").toString();

					timeStamp.setTimestamp(timestamp);

					timestamps.add(timeStamp);

					nuevaRespuesta.setTimestamps(timestamps);

					// * * * * * * * * * * * * * * * * * * * * * * * *

					Date fechaEnvio = SignatureUtil.timestampToDate(timestamp);

					nuevaRespuesta.setFechaRegistro(fechaEnvio);

					mngrRespuesta.save(nuevaRespuesta);

				} else {

					Respuesta oldRespuesta = mngrRespuesta.fetch(nuevaRespuesta.getIdRespuesta());

					{

						TipoRespuesta tr = mngrTipoRespuesta
								.fetch(nuevaRespuesta.getTipoRespuesta().getIdTipoRespuesta());
						nuevaRespuesta.setTipoRespuesta(tr);

						if (!oldRespuesta.getTipoRespuesta().getTipoConcluida()
								&& nuevaRespuesta.getTipoRespuesta().getTipoConcluida()) {
							List<Criterion> restrictions = new ArrayList<>();

							restrictions.add(Restrictions.eq("idAsunto", nuevaRespuesta.getIdAsunto()));
							restrictions.add(
									Restrictions.not(Restrictions.eq("idRespuesta", nuevaRespuesta.getIdRespuesta())));
							restrictions.add(Restrictions.eq("tipoRespuesta.tipoConcluida", true));

							List<Respuesta> respuestasConcluidas = (List<Respuesta>) mngrRespuesta.search(restrictions);

							if (!respuestasConcluidas.isEmpty())
								return new ResponseEntity<Respuesta>(nuevaRespuesta, HttpStatus.CONFLICT); // yaExisteRespuestaConcluida
						}
					}

					if (oldRespuesta.getStatus().getIdStatus() != Status.POR_ENVIAR) {
						throw new BadRequestException();
					}

					if (!oldRespuesta.getTipoRespuesta().getTipoConcluida()
							&& oldRespuesta.getPorcentaje() != nuevaRespuesta.getPorcentaje()
							&& nuevaRespuesta.getPorcentaje() < getMaxPorcentaje(idAsunto,
									nuevaRespuesta.getIdRespuesta(), false))
						return new ResponseEntity<Respuesta>(nuevaRespuesta,
								HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE); // porcentajeInvalido

					if (nuevaRespuesta.getTipoRespuesta().getIdTipoRespuesta().equals("A")
							&& nuevaRespuesta.getPorcentaje().equals(100))
						return new ResponseEntity<Respuesta>(nuevaRespuesta,
								HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE); // porcentajeInvalido

					// throw new Exception("Porcentaje invalido.");

					if (nuevaRespuesta.getCopias() != null && !nuevaRespuesta.getCopias().isEmpty()) {

						for (CopiaRespuesta copiaRespuesta : nuevaRespuesta.getCopias()) {
							copiaRespuesta.setIdAsunto(idAsunto);
						}

					}

					if (nuevaRespuesta.getAreaDestino() == null)
						nuevaRespuesta.setAreaDestino(oldRespuesta.getAreaDestino());

					mngrRespuesta.update(nuevaRespuesta);

				}

			} else {
				return new ResponseEntity<Respuesta>(nuevaRespuesta, HttpStatus.BAD_REQUEST);
			}

		} catch (

		Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<Respuesta>(nuevaRespuesta, HttpStatus.OK);
	}

	/**
	 * Obtiene el porcentaje maximo de las respuestas del asunto.
	 *
	 * @param idAsunto the id asunto
	 * @return the max porcentaje
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public int getMaxPorcentaje(Integer idAsunto, Integer idRespuesta, boolean enviado) throws Exception {

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("idAsunto", idAsunto));
		restrictions.add(Restrictions.ne("idRespuesta", idRespuesta));

		if (enviado) {
			List<Criterion> restrictionsStatus = new ArrayList<>();
			restrictionsStatus.add(Restrictions.in("idStatus", 1, 3));

			restrictions.add(Restrictions.in("status", mngrStatus.search(restrictionsStatus)));
		}

		List<Respuesta> respuestasConcluidas = (List<Respuesta>) mngrRespuesta.search(restrictions);

		Iterator<Respuesta> it = respuestasConcluidas.iterator();

		int maxPorcentaje = 0;

		while (it.hasNext()) {
			Respuesta respuestaYaGuardadaAlAsunto = it.next();
			if (respuestaYaGuardadaAlAsunto.getPorcentaje() > maxPorcentaje)
				maxPorcentaje = respuestaYaGuardadaAlAsunto.getPorcentaje();
		}

		return maxPorcentaje;
	}

	/**
	 * Valida si el porcentaje de la respuesta es valido,.
	 *
	 * @param nuevaRespuesta the nueva respuesta
	 * @return int typeError
	 */
	@SuppressWarnings("unchecked")
	public int validaPorcentaje(Respuesta nuevaRespuesta) {

		int typeError = 0; // todoOk

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("idAsunto", nuevaRespuesta.getIdAsunto()));

		List<Respuesta> respuestasConcluidas = (List<Respuesta>) mngrRespuesta.search(restrictions);

		Iterator<Respuesta> it = respuestasConcluidas.iterator();

		int maxPorcentaje = 0;

		Respuesta respuestaYaGuardadaAlAsunto;

		while (it.hasNext()) {

			respuestaYaGuardadaAlAsunto = it.next();

			if (respuestaYaGuardadaAlAsunto.getTipoRespuesta().getTipoConcluida()) {

				if (respuestaYaGuardadaAlAsunto.getStatus().getIdStatus() == Status.RECHAZADO
						|| respuestaYaGuardadaAlAsunto.getStatus().getIdStatus() == Status.ATENDIDO) {
					// OK
				} else {
					typeError = 1; // yaExisteRespuestaConcluida
				}

			} else {

				if (respuestaYaGuardadaAlAsunto.getStatus().getIdStatus() != Status.RECHAZADO
						&& respuestaYaGuardadaAlAsunto.getStatus().getIdStatus() != Status.ATENDIDO)

					if (maxPorcentaje < respuestaYaGuardadaAlAsunto.getPorcentaje()) {
						maxPorcentaje = respuestaYaGuardadaAlAsunto.getPorcentaje();
					}
			}

		}

		if (nuevaRespuesta.getPorcentaje() == null //
				|| maxPorcentaje >= nuevaRespuesta.getPorcentaje() //
				|| (!nuevaRespuesta.getTipoRespuesta().getTipoConcluida() && nuevaRespuesta.getPorcentaje() >= 100)
				|| (nuevaRespuesta.getTipoRespuesta().getTipoConcluida() && nuevaRespuesta.getPorcentaje() != 100)) {

			typeError = 2; // porcentajeInvalido
		}

		return typeError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@RequestMapping(value = "/respuesta", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> get(@RequestParam(value = "id", required = true) Serializable id) {

		Respuesta item = null;
		try {

			item = mngrRespuesta.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Data Out >> " + item);

		return new ResponseEntity<Respuesta>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar respuesta", notes = "Elimina una respuesta de la lista")
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

	@SuppressWarnings("unchecked")
	@Override
	@RequestMapping(value = "/respuesta", method = RequestMethod.DELETE)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("RESPUESTA A ELIMINAR >> " + id);

		try {

			Integer id_ = Integer.valueOf((String) id.toString());

			Respuesta respuesta = mngrRespuesta.fetch(id_);

			if (respuesta != null) {

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("idRespuesta", id_));

				List<DocumentoRespuesta> docsRespuesta = (List<DocumentoRespuesta>) mngrDocsRespuesta
						.search(restrictions);

				for (DocumentoRespuesta doc : docsRespuesta) {
					try {
						mngrDocsRespuesta.delete(doc);
					} catch (Exception e) {

					}
				}

				mngrRespuesta.delete(respuesta);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Envia una Respuesta al area que genero el Asunto.
	 * 
	 * @param id Identificador de la Respuesta a enviar
	 * @return Respuesta enviada
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Enviar respuesta", notes = "Envia una respuesta al area que genero el asunto")
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

	@RequestMapping(value = "/respuesta/enviar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> enviar(@RequestParam(value = "id", required = true) Serializable id)
			throws Exception {

		try {

			log.info("ENVIANDO LA RESPUESTA ID :: " + id);

			Respuesta item = mngrRespuesta.fetch(Integer.valueOf((String) id));
			if (item != null) {
				if (item.getStatus().getIdStatus() == Status.POR_ENVIAR) {

					Asunto asunto = mngrAsunto.fetch(item.getIdAsunto());

					if (TipoAsunto.ASUNTO.equals(asunto.getTipoAsunto())) {
						log.debug("NO Se puede enviar Respuesta así mismo, el Asunto es de tipo Asunto");
						return new ResponseEntity<Respuesta>(item, HttpStatus.CONFLICT);
					}

					if (item.getPorcentaje() < getMaxPorcentaje(asunto.getIdAsunto(), item.getIdRespuesta(), true))
						return new ResponseEntity<Respuesta>(item, HttpStatus.NOT_ACCEPTABLE);
					// Setea nuevo estatus y timeStamp
					item = setEstatusSetTimeStampRespuesta(item);

					mngrRespuesta.update(item);
					mngrRespuesta.flush();

					// Refrescamos a entidad
					item = mngrRespuesta.fetch(item.getIdRespuesta());

					// Le asignamos el permiso de lectura al area a la que se
					// esta enviando con la Respuesta
					log.debug("Asignando el ACL a los documentos asociados a la respuesta");

					List<DocumentoRespuesta> documentosRespuesta = getDocumentosRespuesta(item.getIdRespuesta());
					Map<String, String> additionalData = new HashMap<>();
					additionalData.put("idArea", String.valueOf(asunto.getArea().getIdArea()));

					String aclName = "aclNameAdjuntoRespuestaEnviada";

					// Para el caso de los asuntos confidenciales, se le
					// asigna el ACL de Respuestas confidenciales
					if (asunto.getAsuntoDetalle().getConfidencial()) {
						aclName = "aclNameAdjuntoRespuestaEnviadaConfidencial";
					}

					for (DocumentoRespuesta documentoRespuesta : documentosRespuesta) {

						EndpointDispatcher.getInstance().addPermisos(documentoRespuesta.getObjectId(),
								environment.getProperty(aclName), additionalData);
					}

					// Actualizamos el estatus del Asunto
					actualizarAsuntoPadreRespuesta(asunto.getIdAsuntoPadre());

					/** * * * * * * * * * */

					return new ResponseEntity<Respuesta>(item, HttpStatus.OK);

				} else {

					throw new Exception(
							"La Respuesta se encuantra en un estado diferente a registrado. No puede ser enviado.");
				}
			} else {
				return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}
	// nuevo servicio para realizar envio de respuestas multiple

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Enviar respuestas", notes = "Envia todas las respuestas generadas")
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

	@RequestMapping(value = "/respuesta/enviar2", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<String>> enviar2(@RequestBody(required = true) List<Integer> id)
			throws Exception {

		try {
			// para guardar el id y estatus en cada iteracion
			List<Integer> enviomul = new ArrayList<>();
			List<String> statusenviomul = new ArrayList<>();
			if (!id.isEmpty()) {
				for (Integer iterar : id) {
					// banderas para no dejar pasar la respuesta si tiene alguno de los siguientes
					// estatus
					int flag409 = 0;
					int flag406 = 0;

					Respuesta item = mngrRespuesta.fetch(iterar);
					if (item != null) {
						if (item.getStatus().getIdStatus() == Status.POR_ENVIAR) {

							Asunto asunto = mngrAsunto.fetch(item.getIdAsunto());

							if (TipoAsunto.ASUNTO.equals(asunto.getTipoAsunto())) {
								log.debug("NO Se puede enviar Respuesta así mismo, el Asunto es de tipo Asunto");
								enviomul.add(iterar);
								statusenviomul.add("409");
								flag409 = 1;
							}

							if (item.getPorcentaje() < getMaxPorcentaje(asunto.getIdAsunto(), item.getIdRespuesta(),
									true)) {
								enviomul.add(iterar);
								statusenviomul.add("406");
								flag406 = 1;
							}
							if (flag409 == 0 && flag406 == 0) {
								// Setea nuevo estatus y timeStamp
								item = setEstatusSetTimeStampRespuesta(item);

								mngrRespuesta.update(item);
								mngrRespuesta.flush();

								// Refrescamos a entidad
								item = mngrRespuesta.fetch(item.getIdRespuesta());

								// Le asignamos el permiso de lectura al area a la que se
								// esta enviando con la Respuesta
								log.debug("Asignando el ACL a los documentos asociados a la respuesta");

								List<DocumentoRespuesta> documentosRespuesta = getDocumentosRespuesta(
										item.getIdRespuesta());
								Map<String, String> additionalData = new HashMap<>();
								additionalData.put("idArea", String.valueOf(asunto.getArea().getIdArea()));

								String aclName = "aclNameAdjuntoRespuestaEnviada";

								// Para el caso de los asuntos confidenciales, se le
								// asigna el ACL de Respuestas confidenciales
								if (asunto.getAsuntoDetalle().getConfidencial()) {
									aclName = "aclNameAdjuntoRespuestaEnviadaConfidencial";
								}

								for (DocumentoRespuesta documentoRespuesta : documentosRespuesta) {

									EndpointDispatcher.getInstance().addPermisos(documentoRespuesta.getObjectId(),
											environment.getProperty(aclName), additionalData);
								}

								// Actualizamos el estatus del Asunto
								actualizarAsuntoPadreRespuesta(asunto.getIdAsuntoPadre());

								/** * * * * * * * * * */
								enviomul.add(iterar);
								statusenviomul.add("200");
							}

						} else {
							throw new Exception(
									"La Respuesta se encuentra en un estado diferente a registrado. No puede ser enviado.");
						}

					} else {
						enviomul.add(iterar);
						statusenviomul.add("500");
						return new ResponseEntity<List<String>>(statusenviomul, HttpStatus.BAD_REQUEST);
					}
				}

				return new ResponseEntity<List<String>>(statusenviomul, HttpStatus.OK);

			} else {
				enviomul.add(0);
				statusenviomul.add("500");
				return new ResponseEntity<List<String>>(statusenviomul, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Guardar respuesta interoperabilidad", notes = "Guarda un respuesta de interoperabilidad")
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

	@RequestMapping(value = "/respuesta/enviarInteroperar/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> interoperarList(
			@RequestBody(required = true) List<Respuesta> lstRespuesta) throws Exception {

		try {
			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();

			if (!lstRespuesta.isEmpty()) {

				for (Respuesta respuesta : lstRespuesta) {
					try {

						// Valida que la respuesta pueda enviarse via interoperabilidad
						ResponseEntity<Respuesta> rr = validarRespuestaInteropera(
								respuesta.getIdRespuesta().toString());

						success.add(rr.getBody());

					} catch (BadRequestException e) {
						listResultFail.put(String.format("Tramite_%d", respuesta.getIdRespuesta()), e.getMessage());
					} catch (Exception e) {
						listResultFail.put(String.format("Tramite_%d", respuesta.getIdRespuesta()),
								HttpStatus.INTERNAL_SERVER_ERROR);
					}

				}

				// Lista success y error si existe algun succes el front solicita certificados
				listResult.put("success", success);
				listResult.put("error", listResultFail);

				return new ResponseEntity<>(listResult, HttpStatus.OK);

			} else {
				throw new BadRequestException();
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * Envia un tramite via interoperabilidad a su area destino.
	 * 
	 * @param id Identificador del Tramite a enviar
	 * @return Informacion completa del Tramite enviado
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Enviar respuesta interoperabilidad", notes = "Envia una respuesta via interoperabilidad a su area destino")
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

	@RequestMapping(value = "/respuesta/enviarInteroperar", method = RequestMethod.GET)
	public synchronized @ResponseBody ResponseEntity<Respuesta> validarRespuestaInteropera(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		try {

			log.info("ENVIANDO LA RESPUESTA ID :: " + id);

			Integer idRespuesta = Integer.valueOf((String) id);

			Respuesta item = mngrRespuesta.fetch(idRespuesta);

			if (null != item && item.getStatus().getIdStatus() == Status.POR_ENVIAR) {

				Asunto asuntoRespuesta = mngrAsunto.fetch(item.getIdAsunto());

				if (TipoAsunto.ASUNTO.equals(asuntoRespuesta.getTipoAsunto())) {
					log.debug("NO Se puede enviar Respuesta así mismo, el Asunto es de tipo Asunto");
					throw new BadRequestException("No se puede enviar la respuesta, No pertenece a un tramite.");
				}

				if ("EXTERNO".equalsIgnoreCase(asuntoRespuesta.getAtributo().trim().toString())) {

					// Valida que se pueda interoperar

					if (Boolean.TRUE.equals(item.getArea().getInteropera())
							&& Boolean.TRUE.equals(item.getAreaDestino().getInteropera())

					// && StringUtils.isNotBlank(item.getAreaDestino().getIdExterno())
					) {

						// Si se cumplen las condiciones para interoperar se solicitan los
						// certificado del usuario
						return new ResponseEntity<Respuesta>(item, HttpStatus.OK);

					} else {

						throw new BadRequestException(
								"No se puede enviar la respuesta de manera electrónica via interoperabilidad");
					}
				}

				return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);

			} else {

				throw new Exception(
						"La respuesta no puede ser enviada, se encuentra en estatus diferente a por enviar.");
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Sets the estatus set time stamp respuesta.
	 *
	 * @param item the item
	 * @return the respuesta
	 * @throws Exception the exception
	 */
	protected Respuesta setEstatusSetTimeStampRespuesta(Respuesta item) throws Exception {
		item.setStatus(mngrStatus.fetch(Status.ENVIADO));

		List<Timestamp> timestamps = item.getTimestamps();

		if (timestamps == null)
			timestamps = new ArrayList<>();

		Timestamp timeStamp = new Timestamp();

		timeStamp.setTipo(TipoTimestamp.TIMESTAMP_ENVIO);

		String stampedData = getStampedData(item, timeStamp.getTipo());

		Map<String, Object> time = firmaEndPoint.getTime(stampedData, TipoTimestamp.TIMESTAMP_ENVIO.getTipoString());

		String timestamp = (String) time.get("Tiempo");

		timeStamp.setTimestamp(timestamp);

		timestamps.add(timeStamp);

		item.setTimestamps(timestamps);

		item.setFechaEnvio(SignatureUtil.timestampToDate(timestamp));

		if (item.getCopias() != null && !item.getCopias().isEmpty())
			for (CopiaRespuesta copia : item.getCopias()) {
				copia.setStatus(mngrStatus.fetch(Status.ENVIADO));
			}
		return item;
	}

	/**
	 * Actualizar asunto padre respuesta.
	 *
	 * @param idAsuntoPadre the id asunto padre
	 */
	protected void actualizarAsuntoPadreRespuesta(Integer idAsuntoPadre) {

		// Actualizamos el estatus del Asunto Padre y se valida dependendo el tipo
		// Asunto

		Asunto asuntoPadre = mngrAsunto.fetch(idAsuntoPadre);

		if (asuntoPadre.getTipoAsunto().equals(TipoAsunto.ASUNTO)) {

			if (!asuntoPadre.getStatusAsunto().getIdStatus().equals(Status.PROCESO)
					&& !asuntoPadre.getStatusAsunto().getIdStatus().equals(Status.CONCLUIDO)) {
				log.debug("Actualizando el Asunto padre a PROCESO..");
				asuntoPadre.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));
				asuntoPadre.setStatusTurno(mngrStatus.fetch(Status.PROCESO));

				mngrAsunto.update(asuntoPadre);
			}
		} else {
			if (!asuntoPadre.getStatusTurno().getIdStatus().equals(Status.PROCESO)
					&& !asuntoPadre.getStatusTurno().getIdStatus().equals(Status.CONCLUIDO)) {
				log.debug("Actualizando el Asunto padre a PROCESO..");
				asuntoPadre.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));
				asuntoPadre.setStatusTurno(mngrStatus.fetch(Status.PROCESO));

				mngrAsunto.update(asuntoPadre);
			}
		}
	}

	/**
	 * Gets the timestamps.
	 *
	 * @param id the id
	 * @return the timestamps
	 */
	@RequestMapping(value = "/respuesta/timestamps", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Timestamp>> getTimestamps(
			@RequestParam(value = "id", required = true) Serializable id) {

		try {

			Respuesta item = mngrRespuesta.fetch(Integer.valueOf((String) id));

			return new ResponseEntity<List<Timestamp>>(item.getTimestamps(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Concluir.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Concluir respuesta", notes = "Concluye una respuesta")
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

	@RequestMapping(value = "/respuesta/concluir", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> concluir(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		try {
			Respuesta item = mngrRespuesta.fetch(Integer.valueOf((String) id));
			if (item != null) {

				if (item.getStatus().getIdStatus() == Status.RECHAZADO) {
					item.setStatus(mngrStatus.fetch(Status.ATENDIDO));
					if (item.getCopias() != null && !item.getCopias().isEmpty())
						for (CopiaRespuesta copia : item.getCopias()) {
							copia.setStatus(mngrStatus.fetch(Status.ATENDIDO));
						}
				} else {
					Asunto asuntoTemp = mngrAsunto.fetch(item.getIdAsunto());
					if (TipoAsunto.ASUNTO.equals(asuntoTemp.getTipoAsunto())
							&& item.getStatus().getIdStatus() == Status.POR_ENVIAR) {
						item.setStatus(mngrStatus.fetch(Status.CONCLUIDO));
						if (item.getCopias() != null && !item.getCopias().isEmpty())
							for (CopiaRespuesta copia : item.getCopias()) {
								copia.setStatus(mngrStatus.fetch(Status.CONCLUIDO));
							}

					} else {
						return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);
					}
				}
				/**
				 * se deberia guardar un timestamp cuando se concluye una respuesta rechazada.
				 */
				// String timestamp =
				// firmaEndPoint.getTime().get("Tiempo").toString();
				// item.getTimestamps().add(new Timestamp(timestamp,
				// TipoTimestamp.TIMESTAMP_CONCLUCION));
				/** */

				mngrRespuesta.update(item);

				return new ResponseEntity<Respuesta>(item, HttpStatus.OK);

			} else {
				return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Recuperar.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Recuperar respuesta", notes = "Recupera una respuesta enviada al area que genero el asunto")
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

	@RequestMapping(value = "/respuesta/recuperar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> recuperar(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		try {
			Respuesta item = mngrRespuesta.fetch(Integer.valueOf((String) id));

			if (item != null) {

				if (item.getStatus().getIdStatus() == Status.ENVIADO
						&& "I".equalsIgnoreCase(item.getAreaDestino().getInstitucion().getTipo())) {

					item.setStatus(mngrStatus.fetch(Status.POR_ENVIAR));
					item.setFechaEnvio(null); // Se coloca null porque regresa a estatus por enviar
					item.getTimestamps()
							.removeIf(t -> TipoTimestamp.TIMESTAMP_ENVIO.getTipo() == t.getTipo().getTipo());

				} else {

					if (item.getStatus().getIdStatus() == Status.CONCLUIDO
							&& "I".equalsIgnoreCase(item.getAreaDestino().getInstitucion().getTipo())) {
						return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.NOT_ACCEPTABLE);
					}

					return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);

				}

				if (item.getCopias() != null && !item.getCopias().isEmpty())

					for (CopiaRespuesta copia : item.getCopias()) {
						copia.setStatus(mngrStatus.fetch(Status.POR_ENVIAR));
					}

				mngrRespuesta.update(item);

				return new ResponseEntity<Respuesta>(item, HttpStatus.OK);

			} else {

				return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the stamped data.
	 *
	 * @param resp   the resp
	 * @param tipots the tipots
	 * @return the stamped data
	 */
	public String getStampedData(Respuesta resp, TipoTimestamp tipots) {
		String toBeStamped = (resp.getIdRespuesta() == null ? resp.getIdAsunto() + "" + resp.getPorcentaje()
				: resp.getIdRespuesta()) + "-" + tipots.getTipo();

		return toBeStamped;
	}

	/**
	 * Guarda una Respuesta Recibida como una respuesta del Asunto, es decir, como
	 * una Respuesta Generada.
	 *
	 * @param idRespuesta Identificador de la Respuesta Recibida
	 * @param idAsunto    Identificador del Asunto donde se va a
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Copiar respuesta", notes = "Guarda una respuesta recibida como una respuesta generada del asunto")
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
	@RequestMapping(value = "/respuesta/recibida/guardar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> saveRespuestaRecibida(
			@RequestParam(value = "idRespuesta", required = true) Serializable idRespuesta,
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto) throws Exception {

		log.debug("Guardando la respuesta " + idRespuesta + " como una respuesta generada del asunto " + idAsunto);

		try {

			Respuesta respuestaGenerada = mngrRespuesta.fetch(Integer.valueOf((String) idRespuesta));
			Asunto asunto = mngrAsunto.fetch(Integer.valueOf((String) idAsunto));
			Respuesta respuestaNew = new Respuesta();

			// Creamos la respuesta que vamos a usar
			respuestaNew.setIdAsunto(asunto.getIdAsunto());
			// respuestaNew.setIdAsuntoOrigen(asunto.getIdAsuntoOrigen());
			respuestaNew.setArea(respuestaGenerada.getAreaDestino());

			respuestaNew.setAreaDestino(asunto.getArea());
			respuestaNew.setStatus(mngrStatus.fetch(Status.POR_ENVIAR));
			respuestaNew.setTipoRespuesta(respuestaGenerada.getTipoRespuesta());
			respuestaNew.setPorcentaje(respuestaGenerada.getPorcentaje());
			respuestaNew.setComentario(respuestaGenerada.getComentario());
			respuestaNew.setFolioRespuesta(respuestaGenerada.getFolioRespuesta());
			respuestaNew.setFechaRegistro(new Date());

			mngrRespuesta.save(respuestaNew);

			respuestaNew
					.setFechaRegistro(getCurrentTime(getStampedData(respuestaNew, TipoTimestamp.TIMESTAMP_REGISTRO)));
			mngrRespuesta.update(respuestaNew);

			log.debug("Respuesta guardada con exito " + respuestaNew);

			// Obtenemos la lista de documentos de la respuesta para copiarlos a
			// la nueva respuesta
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("idRespuesta", Integer.valueOf((String) idRespuesta)));
			List<DocumentoRespuesta> documentos = (List<DocumentoRespuesta>) mngrDocsRespuesta.search(restrictions);

			for (DocumentoRespuesta documento : documentos) {
				DocumentoRespuesta docRespuestaNew = new DocumentoRespuesta();
				docRespuestaNew.setIdRespuesta(respuestaNew.getIdRespuesta());
				docRespuestaNew.setIdAsunto(respuestaNew.getIdAsunto());

				// Obtenemos el contenido del documento
				// Usamos el metodo 'downloadDocumento' sin usuario y password
				// para descargarlo como superusuario
				Map<String, Object> response = repositorioController.downloadDocumento(null, null,
						documento.getObjectId());
				String contentB64 = (String) response.get("contentB64");

				docRespuestaNew.setFileB64(contentB64);
				docRespuestaNew.setObjectName(documento.getObjectName());
				docRespuestaNew.setIdArea(asunto.getArea().getIdArea());
				docRespuestaNew.setFechaRegistro(
						getCurrentTime(getDocStampedData(docRespuestaNew, TipoTimestamp.TIMESTAMP_REGISTRO)));
				documentoRespuestaController.save(docRespuestaNew);

			}
			return new ResponseEntity<Respuesta>(respuestaNew, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the doc stamped data.
	 *
	 * @param docRespuestaNew the doc respuesta new
	 * @param tipots          the tipots
	 * @return the doc stamped data
	 */
	private String getDocStampedData(DocumentoRespuesta docRespuestaNew, TipoTimestamp tipots) {
		String toBeStamped = docRespuestaNew.getIdRespuesta() + "-" + docRespuestaNew.getObjectName() + "-"
				+ tipots.getTipo();
		return toBeStamped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	public ResponseEntity<Respuesta> save(Respuesta object) throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * Obtiene la lista de documentos asociados a la Respuesta.
	 *
	 * @param idRespuesta Identificador de la Respuesta
	 * @return Lista de documentos asociados a la Respuesta
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	@SuppressWarnings("unchecked")
	private List<DocumentoRespuesta> getDocumentosRespuesta(Integer idRespuesta) throws Exception {

		List<Criterion> restrictions = new ArrayList<Criterion>();

		restrictions.add(Restrictions.eq("idRespuesta", idRespuesta));

		return (List<DocumentoRespuesta>) mngrDocsRespuesta.search(restrictions);
	}

	/**
	 * Generar folio respuesta.
	 *
	 * @return the string
	 * @throws Exception the exception
	 */
	private String generarFolioRespuesta(boolean isFolioMultiple, Integer idFolioMultiple) throws Exception {

		String folioRespuestaCompuesto = "";

		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		Area area = mngrArea.fetch(idArea);
		HashMap<String, Object> params = new HashMap<>();

		if (isFolioMultiple) {

			log.debug("CONSULTANTO FOLIOMULTIPLE PARA RESPUESTA");
			params.put("idFoliopsMultiple", idFolioMultiple);
			params.put("idTipo", 0);

			BigDecimal folioRespuesta = (BigDecimal) mngrFolioAreaMultiple.uniqueResult("generaNumDoctoMultiple",
					params);

			log.debug("CONSULTANTO FOLIOPS");
			FolioPSMultiple folioPS = mngrFoliopsmultiple.fetch(new FolioPSMultiple(idFolioMultiple, idArea));
			if (folioPS != null && "H".equals(folioPS.getTipo()))
				folioPS = mngrFoliopsmultiple.fetch(
						new FolioPSMultiple(Integer.valueOf(folioPS.getIdFolioHeredado()), folioPS.getIdAreaHereda()));

			log.debug(folioPS);

			if (null != folioPS && StringUtils.isNotBlank(folioPS.getPrefijoFolio())
					&& StringUtils.isNotBlank(folioPS.getPrefijoFolio())) {
				folioRespuestaCompuesto = folioPS.getPrefijoFolio() + folioRespuesta.toString()
						+ folioPS.getSufijoFolio();
			} else {

				folioRespuestaCompuesto = area.getClave() + folioRespuesta.toString();
			}

		} else {

			log.debug("CONSULTANTO PARAMETROS APP");

			// asi estaba antes.
			// String folioConsolidaOld = getParamApp("SIGAP", "FOLIOCONSOLIDA");

			ConfiguracionArea config = configuracionController.configuracioArea(idArea.toString()).getBody();

			String folioConsolida = config.getFoliadorUnicoSN();

			if (StringUtils.isNotBlank(folioConsolida)) {

				Integer idTipo;

				if ("S".equals(folioConsolida)) {
					idTipo = 1;
				} else {
					idTipo = 0;
				}

				log.debug("CONSULTANTO FOLIORESPUESTA");
				params.put("idArea", idArea);
				params.put("idTipo", idTipo);
				BigDecimal folioRespuesta = (BigDecimal) mngrArea.uniqueResult("generaNumDoctoAuto", params);

				log.debug("CONSULTANTO FOLIOPS");
				FolioPS folioPS = mngrFoliops.fetch(idArea);

				log.debug(folioPS);

				if (null != folioPS && StringUtils.isNotBlank(folioPS.getPrefijoFolio())
						&& StringUtils.isNotBlank(folioPS.getPrefijoFolio())) {

					folioRespuestaCompuesto = folioPS.getPrefijoFolio() + folioRespuesta.toString()
							+ folioPS.getSufijoFolio();
				} else {

					folioRespuestaCompuesto = area.getClave() + folioRespuesta.toString();
				}

			} else {
				throw new Exception(
						"Los parametros idClave=FOLIOCONSOLIDA y idSección=SIGAP no se encuentran configurados.");
			}
		}

		return folioRespuestaCompuesto;
	}

}
