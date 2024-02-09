/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.config.DBVendor;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.controller.util.TreeNode;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoSeguimiento;
import com.ecm.sigap.data.model.Ciudadano;
import com.ecm.sigap.data.model.CustomAsunto;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.FolioPS;
import com.ecm.sigap.data.model.FolioPSMultiple;
import com.ecm.sigap.data.model.Remitente;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.RepresentanteLegal;
import com.ecm.sigap.data.model.RespuestaSeguimiento;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.TipoExpediente;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.TipoPrioridad;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.StatusAsunto;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoRegistro;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.data.model.util.TramiteAuxList;
import com.ecm.sigap.data.model.util.ValidaRespuestaSeguimiento;
import com.ecm.sigap.security.util.ClaveGenerator;
import com.ecm.sigap.security.util.CryptoUtil;
import com.ecm.sigap.util.SignatureUtil;
import com.ibm.icu.text.SimpleDateFormat;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Asunto}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class AsuntoController extends CustomRestController implements RESTController<Asunto> {
	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(AsuntoController.class);

	/**
	 * Referencia hacia el REST controller de {@link DocumentoAsuntoController}.
	 */
	@Autowired
	private DocumentoAsuntoController documentoAsuntoController;

	/**
	 * Referencia hacia el REST controller de {@link DocumentoCompartidoController}.
	 */
	@Autowired
	private DocumentoCompartidoController documentoCompartidoController;

	/**
	 * Referencia hacia el REST controller de {@link PermisoController}.
	 */
	@Autowired
	private PermisoController permisoController;

	/**
	 * Referencia hacia el REST controller de {@link MailController}.
	 */
	@Autowired
	private MailController mailController;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene asunto", notes = "Obtiene detalle de asunto por identificador")
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
	@RequestMapping(value = "/asunto", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> get(@RequestParam(value = "id", required = true) Serializable id) {

		Asunto item = null;

		try {
			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.idEq(Integer.valueOf((String) id)));

			List<Asunto> lst = (List<Asunto>) mngrAsunto.search(restrictions);
			if (null != lst && !lst.isEmpty()) {
				item = lst.get(0);

				if (areaId.equals(item.getArea().getIdArea())
						|| (item.getAreaDestino() != null && areaId.equals(item.getAreaDestino().getIdArea()))) {
					log.debug(" Item Out >> " + item);

					try {
						item.setAsuntoPadre(mngrAsunto.fetch(item.getIdAsuntoPadre()));
					} catch (Exception e) {

					}

					return new ResponseEntity<Asunto>(item, HttpStatus.OK);

				} else {
					return new ResponseEntity<Asunto>(new Asunto(), HttpStatus.FORBIDDEN);
				}
			} else {
				return new ResponseEntity<Asunto>(item, HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Obtiene Respuesta", notes = "Obtiene el detalle de una respuesta")
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
	@RequestMapping(value = "/asunto/respuesta", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> getRespuesta(
			@RequestParam(value = "id", required = true) Serializable id) {

		Asunto item = null;

		try {
			// Integer areaId =
			// Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.idEq(Integer.valueOf((String) id)));

			List<Asunto> lst = (List<Asunto>) mngrAsunto.search(restrictions);
			if (null != lst && !lst.isEmpty()) {
				item = lst.get(0);

				// if (areaId.equals(item.getArea().getIdArea())
				// || (item.getAreaDestino() != null &&
				// areaId.equals(item.getAreaDestino().getIdArea()))) {
				log.debug(" Item Out >> " + item);
				return new ResponseEntity<Asunto>(item, HttpStatus.OK);

				// } else {
				// return new ResponseEntity<Asunto>(new Asunto(),
				// HttpStatus.FORBIDDEN);
				// }
			} else {
				return new ResponseEntity<Asunto>(item, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the asunto detalle.
	 *
	 * @param id the id
	 * @return the asunto detalle
	 */

	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Obtiene asunto", notes = "Obtiene el detalle de un asunto")
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

	@RequestMapping(value = "/asunto/info", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> getAsuntoDetalle(
			@RequestParam(value = "id", required = true) Serializable id) {

		Asunto item = null;

		try {
			item = mngrAsunto.fetch(Integer.valueOf((String) id));

			if (item != null) {

				Asunto asuntoInfo = new Asunto();
				asuntoInfo.setAsuntoDetalle(item.getAsuntoDetalle());
				asuntoInfo.setArea(item.getArea());
				asuntoInfo.setAreaDestino(item.getAreaDestino());
				asuntoInfo.setAsignadoA(item.getAsignadoA());

				asuntoInfo.setEvento(item.getEvento());
				asuntoInfo.setExpediente(item.getExpediente());
				asuntoInfo.setFechaEvento(item.getFechaEvento());
				asuntoInfo.setSubTema(item.getSubTema());
				asuntoInfo.setTema(item.getTema());
				asuntoInfo.setTipoDocumento(item.getTipoDocumento());

				asuntoInfo.setComentarioRechazo(item.getComentarioRechazo());
				asuntoInfo.setComentario(item.getComentario());
				asuntoInfo.setDestinatario(item.getDestinatario());
				asuntoInfo.setEspecial(item.getEspecial());
				asuntoInfo.setEnTiempo(item.getEnTiempo());
				asuntoInfo.setFechaCompromiso(item.getFechaCompromiso());
				asuntoInfo.setFechaAcuse(item.getFechaAcuse());
				asuntoInfo.setFechaEnvio(item.getFechaEnvio());
				asuntoInfo.setFechaRegistro(item.getFechaRegistro());
				asuntoInfo.setFolioArea(item.getFolioArea());
				asuntoInfo.setIdAsunto(item.getIdAsunto());
				asuntoInfo.setIdAsuntoPadre(item.getIdAsuntoPadre());
				asuntoInfo.setIdAsuntoOrigen(item.getIdAsuntoOrigen());
				asuntoInfo.setInstruccion(item.getInstruccion());
				asuntoInfo.setPrioridad(item.getPrioridad());
				asuntoInfo.setStatusAsunto(item.getStatusAsunto());
				asuntoInfo.setTipoAsunto(item.getTipoAsunto());
				asuntoInfo.setTurnador(item.getTurnador());
				asuntoInfo.setTipoExpediente(item.getTipoExpediente());
				asuntoInfo.setIdTipoRegistro(item.getIdTipoRegistro());
				return new ResponseEntity<Asunto>(asuntoInfo, HttpStatus.OK);
			} else {
				return new ResponseEntity<Asunto>(new Asunto(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear asunto", notes = "Crea un nuevo asunto")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se creo de forma exitosa el asunto"),
			@ApiResponse(code = 201, message = "Creado"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 409, message = "El numero de documento ya se encuentra registrado en esta area"),
			@ApiResponse(code = 500, message = "Error del servidor") })
	@RequestMapping(value = "/enviar/tramite", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Asunto> enviaTramite(@RequestBody(required = true) Asunto asunto) throws Exception {
		log.info("Esto es tramite "+asunto);
		
		return null;
	}
	
	@ApiOperation(value = "Crear asunto", notes = "Crea un nuevo asunto")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se creo de forma exitosa el asunto"),
			@ApiResponse(code = 201, message = "Creado"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 409, message = "El numero de documento ya se encuentra registrado en esta area"),
			@ApiResponse(code = 500, message = "Error del servidor") })
	@RequestMapping(value = "/asunto", method = RequestMethod.PUT)
	@Override
	public @ResponseBody ResponseEntity<Asunto> save(@RequestBody(required = true) Asunto asunto) throws Exception {
		return save(asunto, null);
	}
	
	public @ResponseBody ResponseEntity<Asunto> save(@RequestBody(required = true) Asunto asunto, Map<String, Object> paramsTramite) throws Exception {

		String userId = (paramsTramite != null && paramsTramite.get("userId") != null) ?
						(String) paramsTramite.get("userId") : getHeader(HeaderValueNames.HEADER_USER_ID);

		Integer areaId = (paramsTramite != null && paramsTramite.get("areaId") != null) ?
						(Integer) paramsTramite.get("areaId") : Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		// String folioParaDesbloquear = null;
		String claveAutoParaDesbloquear = null;
		
		boolean nuevoAsunto = false;

		try {

			if (!esSoloLectura(userId)) {

				log.debug("ASUNTO A GUARDAR >> " + asunto);

				// Validamos los valores que se pasan por parametros
				if (null == asunto//
						|| null == asunto.getAsuntoDetalle()//
						|| null == asunto.getArea() //
						|| null == asunto.getArea().getIdArea()) {

					log.error(
							"Alguno de los datos obligatorios del Asunto no estan presentes por lo que se rechaza la solicitud");

					throw new BadRequestException();

				}

				if (asunto.getAsuntoDetalle().getFirmante() != null)
					asunto.getAsuntoDetalle().setIdFirmante(asunto.getAsuntoDetalle().getFirmante().getIdFirmante());

				if (TipoAsunto.ASUNTO == asunto.getTipoAsunto()
						&& (null == asunto.getAsuntoDetalle().getAsuntoDescripcion()
								|| asunto.getAsuntoDetalle().getAsuntoDescripcion().isEmpty()
								|| (null == asunto.getAsuntoDetalle().getNumDocto()
										&& !asunto.getAsuntoDetalle().isNumDoctoAuto())
								|| (null != asunto.getAsuntoDetalle().getNumDocto()
										&& asunto.getAsuntoDetalle().getNumDocto().isEmpty()))) {
					log.error(
							"Alguno de los datos obligatorios del Asunto no estan presentes por lo que se rechaza la solicitud");

					throw new BadRequestException();

				}

				// NO PERMITIR TRAMITE NUEVO A LA MISMA AREA.
				if (asunto.getIdAsunto() == null //
						&& null != asunto.getAreaDestino() //
						&& null != asunto.getAreaDestino().getIdArea() //
						&& areaId.equals(asunto.getAreaDestino().getIdArea())) {
					log.error("No se puede realizar un tramite para la misma area");
					throw new Exception("No se puede realizar un tramite para la misma area");
				}

				if (asunto.getAsuntoDetalle() != null //
						&& asunto.getAsuntoDetalle().getFechaElaboracion() != null
						&& asunto.getAsuntoDetalle().getFechaRecepcion() != null //
						&& asunto.getAsuntoDetalle().getFechaElaboracion()
								.after(asunto.getAsuntoDetalle().getFechaRecepcion())) {
					log.error(errorMessages.getString("errorFechaElaboracion"));
					throw new BadRequestException(errorMessages.getString("errorFechaElaboracion"));
				}

				if (asunto.getAsuntoDetalle() != null //
						&& asunto.getAsuntoDetalle().getFechaRecepcion() != null
						&& asunto.getAsuntoDetalle().getFechaRecepcion().after(new Date())) {
					log.error(errorMessages.getString("errorFechaRecepccion"));
					throw new BadRequestException(errorMessages.getString("errorFechaRecepccion"));

				}

				if (asunto.getIdAsunto() == null) {
					nuevoAsunto = true;

					if (StringUtils.isNotBlank(asunto.getAsuntoDetalle().getNumDocto())) {
						if (asunto.getTipoAsunto() != null && asunto.getTipoAsunto().equals(TipoAsunto.ASUNTO)
								&& !asunto.getAsuntoDetalle().getNumDocto().equalsIgnoreCase("S/N")) {
							if (existeNumdocto(asunto.getAsuntoDetalle().getNumDocto())) {

								log.error("El numero de documento ya existe para un asunto de tipo A en el area: "
										+ asunto.getArea().getIdArea());
								throw new ConstraintViolationException("El numero de documento ya existe", null);
							}
						}
					}

					// Registro en la tabla CUSTOMASUNTOS, aqui solo aplica para
					// Asuntos tipos ASUNTO
					CustomAsunto customAsunto = null;

					asunto.getAsuntoDetalle().setPromotor(asunto.getAsuntoDetalle().getPromotor());
					
					if(paramsTramite != null && paramsTramite.get("representante") != null)
						asunto.setTurnador((Representante)paramsTramite.get("representante"));
					else
						asunto.setTurnador(mngrRepresentante.fetch(asunto.getTurnador().getId()));
					
					if(paramsTramite != null && paramsTramite.get("area") != null)
						asunto.setArea((Area)paramsTramite.get("area"));
					else
						asunto.setArea(mngrArea.fetch(asunto.getArea().getIdArea()));

					if (asunto.getAreaDestino() != null && asunto.getAreaDestino().getIdArea() != null)
						asunto.setAreaDestino(mngrArea.fetch(asunto.getAreaDestino().getIdArea()));
					else
						asunto.setAreaDestino(null);

					// Se genera el folio Area para el caso que sea un Asunto
					// solamente
					if (TipoAsunto.ASUNTO == asunto.getTipoAsunto()) {

						String folioArea = getFolioArea(asunto.getArea().getIdArea());
						// folioParaDesbloquear = folioArea;

						log.info(" folio tomado!!! " + asunto.getArea().getIdArea() + " :: " + folioArea);
						log.info(" folio tomado!!! " + asunto.getArea().getIdArea() + " :: " + folioArea);
						log.info(" folio tomado!!! " + asunto.getArea().getIdArea() + " :: " + folioArea);

						if (folioArea == null)
							throw new Exception("NO HAY FOLIOS DISPONIBLES PARA EL AREA : "
									+ asunto.getAsuntoDetalle().getIdRemitente() + " : ");
						else
							asunto.setFolioArea(folioArea);

						// Se genera el Numero de Documento o Numero de Oficio
						if (asunto.getAsuntoDetalle().isNumDoctoAuto()
								&& StringUtils.isBlank(asunto.getAsuntoDetalle().getNumDocto())) {
							if (asunto.getAsuntoDetalle().getIdFolioMultiple() != null) {
								asunto.getAsuntoDetalle().setNumDocto(
										getNumDoctoAutomatico(true, asunto.getAsuntoDetalle().getIdRemitente(),
												asunto.getAsuntoDetalle().getIdFolioMultiple()));
							} else {
								asunto.getAsuntoDetalle().setNumDocto(
										getNumDoctoAutomatico(false, asunto.getAsuntoDetalle().getIdRemitente(), null));
							}
						}

						if (asunto.getAsuntoDetalle().isClaveAuto()
								&& StringUtils.isBlank(asunto.getAsuntoDetalle().getClave())) {

							claveAutoParaDesbloquear = getFolioClaveArea(asunto.getAsuntoDetalle().getIdRemitente());

							asunto.getAsuntoDetalle().setClave(claveAutoParaDesbloquear);
						}

						TipoExpediente tipoExpediente = null;
						tipoExpediente = getTipoExpedienteDefault(asunto.getArea().getIdArea());
						asunto.setTipoExpediente(tipoExpediente);

						/** Guardamos el Asunto Correspondencia */
						customAsunto = new CustomAsunto();
						customAsunto.setCustom0(String.valueOf(asunto.getArea().getIdArea()));
						customAsunto.setCustom1(asunto.getFolioArea());

					} else if ("TEC".contains(asunto.getTipoAsunto().getValue())
							&& !"T".equalsIgnoreCase(asunto.getIdSubTipoAsunto())) {

						Asunto asuntoPadre = null;
						if(paramsTramite != null && paramsTramite.get("asuntoPadre") != null)
							asuntoPadre = (Asunto)paramsTramite.get("asuntoPadre");
						else
							asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());

						// * * * * * * * * * * * * * * * * *

						if (Boolean.parseBoolean(environment.getProperty("permitirTurnarMismaArea", "false")))
							validarDobleTurnadoAArea(asuntoPadre.getIdAsuntoOrigen(), asunto.getAreaDestino());

						// * * * * * * * * * * * * * * * * *

						asunto.setAsuntoDetalle(asuntoPadre.getAsuntoDetalle());

						asunto.setIdAsuntoOrigen(asuntoPadre.getIdAsuntoOrigen());

						// Seteando el iDAsuntoDetalle null para que cree un
						// registro nuevo y sette idProcedencia como Interno
						// asunto.getAsuntoDetalle().setIdAsuntoDetalle(null);

						// asunto.getAsuntoDetalle().setIdProcedencia("I");

						if (null == asunto.getStatusAsunto()) {
							asunto.getStatusAsunto().setIdStatus(Status.POR_ENVIAR);
						}

						if (null == asunto.getStatusTurno()) {
							asunto.getStatusTurno().setIdStatus(Status.POR_ENVIAR);
						}

						if (asunto.getInstruccion() == null) {
							renameFolderAndUnlockFolio(asunto, EndpointDispatcher.getInstance(), areaId,
									claveAutoParaDesbloquear);
							throw new BadRequestException("El tramite debe tener instruccion.");
						}
					}

					if (asunto.getInstruccion() != null) {
						if(paramsTramite != null && paramsTramite.get("instruccion") != null)
							asunto.setInstruccion((TipoInstruccion)paramsTramite.get("instruccion"));
						else
							asunto.setInstruccion(mngrTipoInstruccion.fetch(asunto.getInstruccion().getIdInstruccion()));

						if (asunto.getPrioridad() != null && asunto.getInstruccion().getRequiereRespuesta()) {
							if(paramsTramite != null && paramsTramite.get("prioridad") != null)
								asunto.setPrioridad((TipoPrioridad)paramsTramite.get("prioridad"));
							else
								asunto.setPrioridad(mngrTipoPrioridad.fetch(asunto.getPrioridad().getIdPrioridad()));
						} else {
							asunto.setPrioridad(null);
						}
					}

					if (TipoAsunto.ASUNTO.equals(asunto.getTipoAsunto())) {
						// VALIDA QUE NO EXISTA EL FOLDER EN EL REPO Y SI EXISTE
						// LO ELIMINA
						validarFolderEnRepo(asunto.getTipoExpediente().getContentId(), asunto.getFolioArea());
					}

					log.debug("::: >>> Asunto antes de guardar" + asunto);
					mngrAsunto.save(asunto);

					log.debug(" ID ASUNTO GENERADO :: " + asunto.getIdAsunto());

					// Refresh & Complete Data Return
					Asunto asuntoTmp = mngrAsunto.fetch(asunto.getIdAsunto());

					if (asuntoTmp == null) {
						renameFolderAndUnlockFolio(asunto, EndpointDispatcher.getInstance(), areaId,
								claveAutoParaDesbloquear);
						mngrAsunto.delete(asunto);
						throw new Exception();
					} else {
						asunto = asuntoTmp;
					}

					// * * * * * * * * * * * * * * * * * * * * * * * *
					try {
						List<Timestamp> timestamps = new ArrayList<>();

						Timestamp timeStamp = new Timestamp();
						timeStamp.setTipo(TipoTimestamp.TIMESTAMP_REGISTRO);

						String stampedData = getStampedData(asunto, timeStamp.getTipo());

						Map<String, Object> time = firmaEndPoint.getTime(stampedData,
								TipoTimestamp.TIMESTAMP_REGISTRO.getTipoString());

						String timestamp = (String) time.get("Tiempo");

						timeStamp.setTimestamp(timestamp);

						timestamps.add(timeStamp);

						asunto.setTimestamps(timestamps);
					} catch (Exception e) {
						log.error("ERROR AL OBTENER EL TIMESTAMP!!!!");

						throw e;
					}
					// * * * * * * * * * * * * * * * * * * * * * * * *

					if (asunto.getTipoAsunto().getValue().equals(TipoAsunto.ASUNTO.getValue()))
						asunto.setIdAsuntoPadre(asunto.getIdAsunto());

					// Para los turnos guardamos el Area Destino
					if ("A".equals(asunto.getTipoAsunto().getValue())) {

						// Guardamos el registro en CUSTOMASUNTO
						customAsunto.setIdAsunto(asunto.getIdAsunto());
						log.debug("Guardando el registro en CUSTOMASUNTOS");
						mngrCustomAsunto.save(customAsunto);

					}

					/** Actualizamos el estatus del Asunto Padre */
					if (null != asunto.getIdAsuntoPadre() && !asunto.getIdAsunto().equals(asunto.getIdAsuntoPadre())
							&& (asunto.getTipoAsunto().getValue().equals(TipoAsunto.TURNO.getValue())
									|| asunto.getTipoAsunto().getValue().equals(TipoAsunto.ENVIO.getValue())
									|| asunto.getTipoAsunto().getValue().equals(TipoAsunto.COPIA.getValue()))) {

						Asunto asuntoPadre = null;
						if(paramsTramite != null && paramsTramite.get("asuntoPadre") != null)
							asuntoPadre = (Asunto)paramsTramite.get("asuntoPadre");
						else
							asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());

						if (!asuntoPadre.getStatusAsunto().getIdStatus().equals(Status.PROCESO)) {

							if (!asuntoPadre.getTipoAsunto().equals(TipoAsunto.ASUNTO)
									&& asunto.getInstruccion() != null
									&& asunto.getInstruccion().getRequiereRespuesta().equals(false)) {
								log.debug("No Actualiza la Copia o el Tramite, el tramite no requiere respuesta..");

							} else if (asuntoPadre.getTipoAsunto().equals(TipoAsunto.ASUNTO)) {

								log.debug("Actualizando el Asunto padre a PROCESO..");
								if(paramsTramite != null && paramsTramite.get("statusAsunto") != null)
									asuntoPadre.setStatusAsunto((Status)paramsTramite.get("statusAsunto"));
								else
									asuntoPadre.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));
								// VALIDAR
								// asuntoPadre.setStatusTurno(mngrStatus.fetch(Status.PROCESO));
								mngrAsunto.update(asuntoPadre);
							}
						}
					}

					mngrAsunto.update(asunto);

					return new ResponseEntity<Asunto>(asunto, HttpStatus.CREATED);

				} else {

					Asunto asuntoOriginal_ = mngrAsunto.fetch(asunto.getIdAsunto());

					if (StringUtils.isNotBlank(asunto.getAsuntoDetalle().getNumDocto())) {

						if (asunto.getAsuntoDetalle().getNumDocto() != null
								&& !asunto.getAsuntoDetalle().getNumDocto()
										.equalsIgnoreCase(asuntoOriginal_.getAsuntoDetalle().getNumDocto())
								&& !asunto.getAsuntoDetalle().getNumDocto().equalsIgnoreCase("S/N")
								&& asunto.getTipoAsunto() != null && asunto.getTipoAsunto().equals(TipoAsunto.ASUNTO)) {
							if (existeNumdocto(asunto.getAsuntoDetalle().getNumDocto())) {
								renameFolderAndUnlockFolio(asunto, EndpointDispatcher.getInstance(), areaId,
										claveAutoParaDesbloquear);
								throw new BadRequestException(
										"El numero de documento ya se encuentra registrado en un asunto.");
							}
						}
					}

					// no se puede cambiar el tipo expediente con un update.
					asunto.setTipoExpediente(asuntoOriginal_.getTipoExpediente());

					if (asunto.getInstruccion() != null) {
						asunto.setInstruccion(mngrTipoInstruccion.fetch(asunto.getInstruccion().getIdInstruccion()));

						if (asunto.getPrioridad() != null && asunto.getInstruccion().getRequiereRespuesta()) {
							asunto.setPrioridad(mngrTipoPrioridad.fetch(asunto.getPrioridad().getIdPrioridad()));
						} else {
							asunto.setPrioridad(null);
						}
					}

					asunto.getAsuntoDetalle().setPromotor(asunto.getAsuntoDetalle().getPromotor());
					asunto.setTurnador(mngrRepresentante.fetch(asunto.getTurnador().getId()));
					asunto.setArea(mngrArea.fetch(asunto.getArea().getIdArea()));

					if (asunto.getAreaDestino() != null && asunto.getAreaDestino().getIdArea() != null)
						asunto.setAreaDestino(mngrArea.fetch(asunto.getAreaDestino().getIdArea()));
					else
						asunto.setAreaDestino(null);

					Remitente remitente = asuntoOriginal_.getAsuntoDetalle().getRemitente();

					if (TipoRegistro.INFOMEX.getValue().equalsIgnoreCase(asunto.getIdTipoRegistro())) {

						if (remitente != null) {
							List<Criterion> fetch_ = new ArrayList<>();

							fetch_.add(Restrictions.eq("remitenteKey.idRemitente",
									remitente.getRemitenteKey().getIdRemitente()));

							fetch_.add(Restrictions.eq("remitenteKey.promotor.idInstitucion",
									remitente.getRemitenteKey().getPromotor().getIdInstitucion()));

							List<Remitente> search_ = (List<Remitente>) mngrRemitente.search(fetch_);

							asunto.getAsuntoDetalle().setRemitente(search_.isEmpty() ? null : search_.get(0));
						}
					}

					if (remitente != null) {
						if (remitente.getRemitenteKey().getIdRemitente()
								.equals(asunto.getAsuntoDetalle().getIdRemitente())) {
							asunto.getAsuntoDetalle().setIdRemitente(remitente.getRemitenteKey().getIdRemitente());
						} else if (remitente.getRemitenteKey().getIdRemitente()
								.equals(asunto.getAsuntoDetalle().getRemitente().getRemitenteKey().getIdRemitente())) {
							asunto.getAsuntoDetalle().setIdRemitente(remitente.getRemitenteKey().getIdRemitente());
						}
					} else {
						if (asunto.getAsuntoDetalle().getIdRemitente() == null) {
							asunto.getAsuntoDetalle().setIdRemitente(null);
						}
					}

					if (!asunto.getStatusAsunto().getIdStatus().equals(0)) {
						if (remitente != null) {
							asunto.getAsuntoDetalle().setIdRemitente(remitente.getRemitenteKey().getIdRemitente());
							asunto.getAsuntoDetalle().setRemitente(remitente);
						}
						asunto.getAsuntoDetalle().setPromotor(asuntoOriginal_.getAsuntoDetalle().getPromotor());
					}
					asunto.setTimestamps(asuntoOriginal_.getTimestamps());

					List<Criterion> restrictions = new ArrayList<Criterion>();
					restrictions.add(Restrictions.eq("idAsunto", asunto.getIdAsunto()));
					restrictions.add(Restrictions.eq("tipoRespuesta", mngrTipoRespuesta.fetch("C")));
					restrictions.add(Restrictions.eq("status", mngrStatus.fetch(Status.CONCLUIDO)));

					List<?> lst = mngrRespuesta.search(restrictions);

					if (lst.size() > 0) {
						asunto.setStatusAsunto(mngrStatus.fetch(Status.CONCLUIDO));
						asunto.setStatusTurno(mngrStatus.fetch(Status.CONCLUIDO));
					}

					mngrAsunto.update(asunto);

					return new ResponseEntity<Asunto>(asunto, HttpStatus.OK);
				}
			} else {

				throw new BadRequestException();

			}
		} catch (Exception e) {
			if(nuevoAsunto)
				renameFolderAndUnlockFolio(asunto, EndpointDispatcher.getInstance(), areaId, claveAutoParaDesbloquear);
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * No se puede turnar a un area mas de una vez el mismo asunto,.
	 *
	 * @param idAsuntoOrigen the id asunto origen
	 * @param areaDestino    the area destino
	 */
	private void validarDobleTurnadoAArea(Integer idAsuntoOrigen, Area areaDestino) {

		HashMap<String, Object> params = new HashMap<>();

		params.put("idAsuntoOrigen", idAsuntoOrigen);
		params.put("idArea", areaDestino.getIdArea());

		Object isTurnada = mngrAsunto.uniqueResult("isAreaAlreadyTurnada", params);

		if (Integer.valueOf(isTurnada.toString()) > 0)
			throw new BadRequestException(errorMessages.getString("errorAreaAlreadyTurnada"));

	}

	/**
	 * Gets the stamped data.
	 *
	 * @param item   the item
	 * @param tipots the tipots
	 * @return the stamped data
	 */
	private String getStampedData(Asunto item, TipoTimestamp tipots) {
		String toBeStamped = item.getIdAsunto() + "-" + item.getIdSubTipoAsunto() + "-" + tipots.getTipo();
		return toBeStamped;
	}

	/**
	 * Genera un folio para el Area correspondiente.
	 *
	 * @param idArea Identificador del Area del cual se desea generar el Folio
	 * @return Numero de Folio
	 */
	protected synchronized String getFolioArea(Integer idArea) {

		log.info(" Obteniendo folio para el area >> " + idArea);
		HashMap<String, Object> params = new HashMap<>();
		params.put("idArea", idArea);
		BigDecimal folioArea = (BigDecimal) mngrArea.uniqueResult("generaFolio", params);

		if (folioArea == null) {
			log.warn("EL AREA " + idArea + " NO TIENE FOLIOS!!!");
		} else {
			if(Boolean.parseBoolean(environment.getProperty("check.folio.area"))) {
				List<Criterion> restrictions = new ArrayList<Criterion>();
				ProjectionList projections = Projections.projectionList();
				projections.add(Projections.countDistinct("idAsunto").as("countr"));
				restrictions.add(Restrictions.eq("folioArea", folioArea.toString()));
				restrictions.add(Restrictions.or(
						Restrictions.and(Restrictions.eq("area.idArea",idArea), Restrictions.eq("tipoAsunto", TipoAsunto.ASUNTO)),
						Restrictions.and(Restrictions.eq("areaDestino.idArea",idArea), Restrictions.ne("tipoAsunto", TipoAsunto.ASUNTO))));				
				
				List<Asunto> search = (List<Asunto>) mngrAsunto.search(restrictions, null, projections, null, null);		
				final Map<String, Long> map = (Map<String, Long>) search.get(0);
				
				if(map.get("countr") > 0) {
					log.error(" El folio " + folioArea.toString() + " ya se encuentra en uso por el area " + idArea
							+ " se tomara el siguiente disponile");
					addNextFolio(idArea, folioArea.intValue());
					folioArea = (BigDecimal) mngrArea.uniqueResult("generaFolio", params);
				}				
			}
		}		
		
		log.info(" folio obtenido >> " + idArea + " >> " + folioArea.toString());
		return folioArea.toString();
	}

	/**
	 * Adds the next folio.
	 *
	 * @param idArea              the id area
	 * @param ultimoFolioAsignado the ultimo folio asignado
	 */
	protected synchronized void addNextFolio(Integer idArea, Integer ultimoFolioAsignado) {
		try {
			log.info(" Generando siguiente folio ::>> " + idArea);
			HashMap<String, Object> params = new HashMap<>();
			params.put("folio", ultimoFolioAsignado);
			params.put("idArea", idArea);
			mngrArea.uniqueResult("addNextFolio", params);
			log.debug("se agrego el siguiente folio del area :: " + ultimoFolioAsignado + "  ::  " + idArea);

		} catch (Exception e) {
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error(e.getLocalizedMessage());

		}
	}

	/**
	 * Desbloquear folio.
	 *
	 * @param idArea            the id area
	 * @param folioADesbloquear the folio A desbloquear
	 */
	protected synchronized void desbloquearFolio(Integer idArea, String folioADesbloquear) {
		try {

			HashMap<String, Object> params = new HashMap<>();
			params.put("folio", Integer.valueOf(folioADesbloquear));
			params.put("idArea", idArea);
			mngrArea.uniqueResult("desbloqueaFolio", params);
			log.debug("se regresó el folio del area :: " + folioADesbloquear + "  ::  " + idArea);

		} catch (Exception ex) {
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioADesbloquear + "  ::  " + idArea);
			log.error(ex.getLocalizedMessage());

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Serializable id) throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * Delete 2.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar tramite", notes = "Elimina el tramite de un asunto")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se elimino de forma exitosa el tramite"),
			@ApiResponse(code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/asunto", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<Status> delete2(@RequestParam(value = "id", required = true) Serializable id)
			throws Exception {

		try {

			Asunto item = mngrAsunto.fetch(Integer.valueOf((String) id));

			log.debug("Borrando el asunto " + item);

			Integer idAsuntoPadre = null;
			Status status;

			if (item != null) {

				if ((item.getTipoAsunto().getValue().equals(TipoAsunto.TURNO.getValue()))
						|| (item.getTipoAsunto().getValue().equals(TipoAsunto.ENVIO.getValue()))
						|| (item.getTipoAsunto().getValue().equals(TipoAsunto.COPIA.getValue()))) {

					if (item.getStatusTurno().getIdStatus() == Status.POR_ENVIAR
							|| item.getStatusTurno().getIdStatus() == Status.ENVIADO) {

						idAsuntoPadre = item.getIdAsuntoPadre();

						// Query Consulta ObjectIds de Documentos del Asunto
						String sqlListIdDocumentByIdAsunto = "select da.r_object_id from {SIGAP_SCHEMA}.documentosAsuntos da where da.idAsunto=:idAsunto";
						HashMap<String, Object> params = new HashMap<>();
						params.put("idAsunto", item.getIdAsunto());
						List<String> documentosId = (List<String>) mngrDocsAsunto
								.execNativeQuery(sqlListIdDocumentByIdAsunto, params);

						if (null != documentosId && documentosId.size() > 0) {

							log.debug("Se van a eliminar " + documentosId.size() + " documentos asociados al Asunto "
									+ item.getIdAsunto());
							for (String ObjectId : documentosId) {
								DocumentoAsunto documentoAsunto = new DocumentoAsunto();
								documentoAsunto.setIdAsunto(item.getIdAsunto());
								documentoAsunto.setObjectId(ObjectId);

								log.debug("Eliminando el documento ::contentId= " + documentoAsunto.getObjectId());
								mngrDocsAsunto.delete(documentoAsunto);
							}
						}

						CustomAsunto customAsunto = mngrCustomAsunto.fetch(Integer.parseInt(id.toString()));

						if (customAsunto != null)
							mngrCustomAsunto.delete(customAsunto);

						mngrAsunto.delete(item);

						// Update asunto padre
						// updateAsuntoPadreStatus(idAsuntoPadre);
						// status = mngrAsunto.fetch(idAsuntoPadre).getStatusAsunto();
						status = updateAsuntoPadre(idAsuntoPadre);

						return new ResponseEntity<Status>(status, HttpStatus.OK);

					} else {

						throw new BadRequestException("El asunto se encuentra en status "
								+ item.getStatusAsunto().getDescripcion() + ". No puede ser cancelado.");
					}

				} else {
					throw new BadRequestException(
							"EL tipo de tramite : " + item.getTipoAsunto() + " no puede ser cancelado.");
				}

			} else {
				throw new BadRequestException("No existe.");
			}

		} catch (Exception e) {
			log.error(
					"Error al momento de eliminar un asunto con la siguiente descripcion: " + e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Valida si el asunto no posee mas tramies, si es el caso lo regresa a en
	 * proceso.
	 *
	 * @param idAsuntoPadre the id asunto padre
	 * @return el status del asunto padre.
	 */
	private Status updateAsuntoPadre(Integer idAsuntoPadre) {

		Asunto asuntoPadre = mngrAsunto.fetch(idAsuntoPadre);

		if (asuntoPadre.getIdAsunto().equals(asuntoPadre.getIdAsuntoPadre())
				&& asuntoPadre.getTipoAsunto().getValue().equals(TipoAsunto.ASUNTO.getValue())) {
			// Se obtine la cantidad de tramites que tiene un asunto
			String sql = "select count(a.idAsunto) from {SIGAP_SCHEMA}.asuntos a where idAsuntoPadre=" + idAsuntoPadre
					+ " and a.idtipoAsunto in ('E','C','T')";
			List<?> asuntosHijos = (List<?>) mngrDocsAsunto.execNativeQuery(sql, null);

			// Si el Asunto no tiene Tramites porque fue eliminado el ultimo,
			// Actualiza el Status del Asunto
			Object count = asuntosHijos.get(0);

			if ((count instanceof BigDecimal && ((BigDecimal) count).intValue() > 0)
					|| (count instanceof BigInteger && ((BigInteger) count).intValue() > 0)) {
				setStatus(asuntoPadre, Status.PROCESO);
			} else {
				setStatus(asuntoPadre, Status.POR_ENVIAR);
			}

			return asuntoPadre.getStatusAsunto();
		} else {
			return asuntoPadre.getStatusAsunto();
		}

	}

	/**
	 * se le pone status al asunto indicado,
	 *
	 * @param asunto
	 */
	private void setStatus(Asunto asunto, int status) {
		asunto.setStatusAsunto(mngrStatus.fetch(status));
		mngrAsunto.update(asunto);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(Asunto object) {
		RequestWrapper<Asunto> body = new RequestWrapper<>(object);
		return search(body);
	}

	/**
	 * Obtiene la lista de Asuntos que cumplen con las condiciones especificadas.
	 *
	 * @param body the body
	 * @return Lista de Asuntos
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta asunto", notes = "Consulta la lista de asuntos")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
			@ApiResponse(code = 400, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/asunto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) RequestWrapper<Asunto> body) {

		List<?> lst = new ArrayList<Asunto>();

		Asunto asunto = body.getObject();
		Map<String, Object> params = body.getParams();
		log.debug("PARAMETROS DE BUSQUEDA :: " + body);

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));			
			
			if (asunto.getAnotacion() != null) {
				if (asunto.getAnotacion().equals("rechazo")) {
					if (asunto.getArea() != null) {
						if (asunto.getArea().getIdArea() != null && params.get("tipoAsunto") != null
								&& params.get("statusAsunto") != null) {
							Map subparams = (LinkedHashMap) params.get("tipoAsunto");
							if (subparams.get("in") != null) {
								String in = subparams.get("in").toString();
								if (in.equals("[ENVIO]")) {
									lst = mngrAsunto.execQuery("SELECT a FROM Asunto a WHERE a.area.idArea = "
											+ asunto.getArea().getIdArea()
											+ " AND a.statusAsunto = 6 AND a.tipoAsunto = 'E' ORDER BY a.fechaRegistro DESC");
									return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
								} else if (in.equals("[TURNO]")) {
									lst = mngrAsunto.execQuery("SELECT a FROM Asunto a WHERE a.area.idArea = "
											+ asunto.getArea().getIdArea()
											+ " AND a.statusAsunto = 6 AND a.tipoAsunto = 'T' ORDER BY a.fechaRegistro DESC");
									return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
								}

							}
						}
					}
				}
			}

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			// se valida si no tiene el permiso de confidencial y se agrega la
			// restriccion
			if (!permisoController.verConfidencial(userId, areaId)) {
				if (params != null && params.get("confidencial") == null) {
					restrictions.add(Restrictions.eq("asuntoDetalle.confidencial", false));
				} else if (params == null) {
					restrictions.add(Restrictions.eq("asuntoDetalle.confidencial", false));
				}
			}

			if (asunto.getIdAsunto() != null)
				restrictions.add(Restrictions.idEq(asunto.getIdAsunto()));

			if (asunto.getIdAsuntoPadre() != null)
				restrictions.add(Restrictions.eq("idAsuntoPadre", asunto.getIdAsuntoPadre()));

			if (asunto.getIdAsuntoOrigen() != null)
				restrictions.add(Restrictions.eq("idAsuntoOrigen", asunto.getIdAsuntoOrigen()));

			if (asunto.getTipoAsunto() != null)
				restrictions.add(Restrictions.eq("tipoAsunto", asunto.getTipoAsunto()));

			if (asunto.getEnTiempo() != null) {
				// Se agrega esta condicion ya que los registros que tenga
				// esta columna en null se consideran En Tiempo
				if (asunto.getEnTiempo().equals(EnTiempo.EN_TIEMPO)) {
					restrictions.add(Restrictions.or(Restrictions.eq("enTiempo", asunto.getEnTiempo()),
							Restrictions.isNull("enTiempo")));
				} else {
					restrictions.add(Restrictions.eq("enTiempo", asunto.getEnTiempo()));
				}
			}

			if (asunto.getIdSubTipoAsunto() != null) {
				restrictions.add(Restrictions.eq("idSubTipoAsunto", asunto.getIdSubTipoAsunto()));
			}

			if (null != asunto.getAsuntoDetalle() && asunto.getAsuntoDetalle().getTipoRegistro() != null) {
				restrictions.add(
						Restrictions.eq("asuntoDetalle.tipoRegistro", asunto.getAsuntoDetalle().getTipoRegistro()));
			}

			if (asunto.getDestinatario() != null)
				restrictions.add(Restrictions.eq("destinatario", asunto.getDestinatario()));

			if (asunto.getArea() != null) {

				if (asunto.getArea().getIdArea() != null)
					restrictions.add(Restrictions.eq("area.idArea", asunto.getArea().getIdArea()));
			}

			if (asunto.getAreaDestino() != null && asunto.getAreaDestino().getIdArea() != null)
				restrictions.add(Restrictions.eq("areaDestino.idArea", asunto.getAreaDestino().getIdArea()));

			if (asunto.getAreaDestino() != null && asunto.getAreaDestino().getInstitucion() != null
					&& asunto.getAreaDestino().getInstitucion().getIdInstitucion() != null)
				restrictions.add(Restrictions.eq("areaDestino.institucion.idInstitucion",
						asunto.getAreaDestino().getInstitucion().getIdInstitucion()));

			if (asunto.getAreaDestino() != null && asunto.getAreaDestino().getTitular() != null
					&& asunto.getAreaDestino().getTitular().getNombreCompleto() != null
					&& StringUtils.isNotBlank(asunto.getAreaDestino().getTitular().getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("titular.nombreCompleto",
						asunto.getAreaDestino().getTitular().getNombreCompleto(), MatchMode.ANYWHERE));

			if (asunto.getAreaDestino() != null && asunto.getAreaDestino().getTitular() != null
					&& asunto.getAreaDestino().getTitular().getId() != null)
				restrictions
						.add(Restrictions.eq("areaDestino.titular.id", asunto.getAreaDestino().getTitular().getId()));

			if (asunto.getComentario() != null)
				restrictions
						.add(EscapedLikeRestrictions.ilike("comentario", asunto.getComentario(), MatchMode.ANYWHERE));

			if (asunto.getComentarioRechazo() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("comentarioRechazo", asunto.getComentarioRechazo(),
						MatchMode.ANYWHERE));

			if (asunto.getFolioArea() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("folioArea", asunto.getFolioArea(), MatchMode.ANYWHERE));

			if (asunto.getStatusAsunto() != null && asunto.getStatusAsunto().getIdStatus() != null)
				restrictions
						.add(Restrictions.eq("statusAsunto", mngrStatus.fetch(asunto.getStatusAsunto().getIdStatus())));

			if (asunto.getEspecial() != null)
				if (asunto.getEspecial().equals(Boolean.FALSE)) {
					restrictions.add(Restrictions.or(Restrictions.eq("especial", asunto.getEspecial()),
							Restrictions.isNull("especial")));
				} else {
					restrictions.add(Restrictions.eq("especial", asunto.getEspecial()));
				}

			if (asunto.getEvento() != null)
				if (asunto.getEvento().getIdEvento() != null)
					restrictions.add(Restrictions.eq("evento.idEvento", asunto.getEvento().getIdEvento()));

			if (asunto.getFechaEvento() != null)
				restrictions.add(Restrictions.eq("fechaEvento", asunto.getFechaEvento()));

			if (asunto.getExpediente() != null)
				if (asunto.getExpediente().getIdExpediente() != null)
					restrictions
							.add(Restrictions.eq("expediente.idExpediente", asunto.getExpediente().getIdExpediente()));

			if (asunto.getTipoDocumento() != null)
				if (asunto.getTipoDocumento().getIdTipoDocumento() != null)
					restrictions.add(Restrictions.eq("tipoDocumento.idTipoDocumento",
							asunto.getTipoDocumento().getIdTipoDocumento()));

			if (asunto.getTema() != null)
				if (asunto.getTema().getIdTema() != null)
					restrictions.add(Restrictions.eq("tema.idTema", asunto.getTema().getIdTema()));

			if (asunto.getSubTema() != null)
				if (asunto.getSubTema().getIdSubTema() != null)
					restrictions.add(Restrictions.eq("subTema.idSubTema", asunto.getSubTema().getIdSubTema()));

			if (params != null && !params.isEmpty()) {

				if (params.get("idAsunto") != null) {
					Map subparams = (LinkedHashMap) params.get("idAsunto");

					if (subparams.get("in") != null) {
						List<Integer> ids = (List<Integer>) subparams.get("in");

						if (ids.size() > 0) {
							restrictions.add(Restrictions.in("idAsunto", ids));
						}
					}

					if (subparams.get("notIn") != null) {
						List<Integer> ids = (List<Integer>) subparams.get("notIn");

						if (ids.size() > 0) {
							restrictions.add(Restrictions.not(Restrictions.in("idAsunto", ids)));
						}
					}

				}

				if (params.get("procedencia") != null) {
					Map subparams = (LinkedHashMap) params.get("procedencia");

					if (subparams.get("in") != null) {
						List<String> idsProcedencia = (List<String>) subparams.get("in");

						if (idsProcedencia.size() > 0) {
							restrictions.add(Restrictions.in("asuntoDetalle.idProcedencia", idsProcedencia));
						}
					}

				}

				if (params.get("tipoAsunto") != null) {
					boolean esRespuesta = false;
					boolean esBandejaAsunto = false;
					if (params.get("esRespuesta") != null)
						esRespuesta = (boolean) params.get("esRespuesta");
					if (params.get("esBandejaAsunto") != null)
						esBandejaAsunto = (boolean) params.get("esBandejaAsunto");

					Map subparams = (LinkedHashMap) params.get("tipoAsunto");

					if (subparams.get("in") != null) {
						List<String> ids = (List<String>) subparams.get("in");
						Disjunction disjunction = Restrictions.disjunction();
						Disjunction disjunction2 = Restrictions.disjunction();

						for (String id : ids) {
							if (esRespuesta) {
								if (id.toString().equals(TipoAsunto.ASUNTO.name())
										|| id.toString().equals(TipoAsunto.ENVIO.name())
										|| id.toString().equals(TipoAsunto.COPIA.name())
										|| id.toString().equals(TipoAsunto.TURNO.name())) {

									disjunction.add(
											Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.valueOf(id))));
								}
							}
							if (esBandejaAsunto) {
								if (id.toString().equals(TipoAsunto.ASUNTO.name())
										|| id.toString().equals(TipoAsunto.ENVIO.name())
										|| id.toString().equals(TipoAsunto.COPIA.name())
										|| id.toString().equals(TipoAsunto.TURNO.name())) {

									disjunction
											.add(Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.valueOf(id)),
													Restrictions.eq("statusTurno.idStatus",
															new Integer(StatusAsunto.ENVIADO.ordinal()))));
								}
							}
							if (!esRespuesta && !esBandejaAsunto) {
								if (id.toString().equals(TipoAsunto.ASUNTO.name())) {

									disjunction
											.add(Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.valueOf(id)),
													Restrictions.eq("area.idArea", areaId)));

								} else if (id.toString().equals(TipoAsunto.ENVIO.name())
										|| id.toString().equals(TipoAsunto.COPIA.name())
										|| id.toString().equals(TipoAsunto.TURNO.name())) {

									// se quiere las creadas O las recibidas,
									// nunca
									// ambas.
									boolean wantedRecibidas = (asunto.getAreaDestino() != null
											&& asunto.getAreaDestino().getIdArea() != null) ? //
													asunto.getAreaDestino().getIdArea().toString()
															.equalsIgnoreCase(areaId.toString()) //
													: false;

									if (wantedRecibidas)

										disjunction.add(
												Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.valueOf(id)),
														Restrictions.gt("statusTurno.idStatus",
																new Integer(StatusAsunto.ENVIADO.ordinal())),
														Restrictions.eq("areaDestino.idArea", areaId)));

									else

										disjunction.add(
												Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.valueOf(id)),
														Restrictions.eq("area.idArea", areaId)));
								}
							}

						}

						if (disjunction.conditions() != null && disjunction.conditions().iterator().hasNext())
							if (disjunction2.conditions() != null && disjunction2.conditions().iterator().hasNext())
								restrictions.add(Restrictions.or(disjunction, disjunction2));
							else
								restrictions.add(Restrictions.or(disjunction));
						else if (disjunction2.conditions() != null && disjunction2.conditions().iterator().hasNext())
							restrictions.add(Restrictions.or(disjunction2));

					}

					if (subparams.get("notIn") != null) {
						List<String> ids = (List<String>) subparams.get("in");
						List<Criterion> arrayCriterion = new ArrayList<>();
						Disjunction disjunction = Restrictions.disjunction();

						for (String id : ids) {
							if (id.toString().equals(TipoAsunto.ASUNTO.name())) {
								arrayCriterion.add(Restrictions.and(Restrictions.eq("tipoAsunto", id.toString())));
							}
							if (id.toString().equals(TipoAsunto.ENVIO.name())
									|| id.toString().equals(TipoAsunto.COPIA.name())
									|| id.toString().equals(TipoAsunto.TURNO.name())) {
								arrayCriterion.add(Restrictions.and(Restrictions.eq("tipoAsunto", id.toString()),
										Restrictions.gt("statusTurno.idStatus", StatusAsunto.ENVIADO.ordinal())));
								disjunction.add(Restrictions.and(Restrictions.eq("tipoAsunto", TipoAsunto.valueOf(id)),
										Restrictions.gt("statusTurno.idStatus",
												new Integer(StatusAsunto.ENVIADO.ordinal()))));
							}
						}
						if (disjunction.conditions() != null && disjunction.conditions().iterator().hasNext())
							restrictions.add(Restrictions.not(Restrictions.or(disjunction)));
					}
				}

				if (params.get("idSubTipoAsunto") != null) {
					Map subparams = (LinkedHashMap) params.get("idSubTipoAsunto");

					if (subparams.get("in") != null) {
						List<String> ids = (List<String>) subparams.get("in");

						if (ids.size() > 0) {
							restrictions.add(Restrictions.in("idSubTipoAsunto", ids));
						}
					}

					if (subparams.get("notIn") != null) {
						List<String> ids = (List<String>) subparams.get("notIn");

						if (ids.size() > 0) {
							restrictions.add(Restrictions.not(Restrictions.in("idSubTipoAsunto", ids)));
						}
					}
				}

				if (params.get("statusAsunto") != null) {
					Map subparams = (LinkedHashMap) params.get("statusAsunto");

					if (subparams.get("in") != null) {
						List<String> ids = (List<String>) subparams.get("in");
						List<Integer> listStatusAsunto = new ArrayList<>();

						for (String id : ids) {
							listStatusAsunto.add(StatusAsunto.valueOf(id).ordinal());
						}

						if (listStatusAsunto.size() > 0) {
							restrictions.add(Restrictions.in("statusAsunto.idStatus", listStatusAsunto));
						}
					}

					if (subparams.get("notIn") != null) {
						List<String> ids = (List<String>) subparams.get("notIn");
						List<Integer> listStatusAsunto = new ArrayList<>();

						for (String id : ids) {
							listStatusAsunto.add(StatusAsunto.valueOf(id).ordinal());
						}

						if (listStatusAsunto.size() > 0) {
							restrictions
									.add(Restrictions.not(Restrictions.in("statusAsunto.idStatus", listStatusAsunto)));
						}
					}
				}

				if (params.get("statusTurno") != null) {
					Map subparams = (LinkedHashMap) params.get("statusTurno");

					if (subparams.get("in") != null) {
						List<String> ids = (List<String>) subparams.get("in");
						List<Integer> listStatusAsunto = new ArrayList<>();

						for (String id : ids) {
							listStatusAsunto.add(StatusAsunto.valueOf(id).ordinal());
						}

						if (listStatusAsunto.size() > 0) {
							restrictions.add(Restrictions.in("statusTurno.idStatus", listStatusAsunto));
						}
					}

					if (subparams.get("notIn") != null) {
						List<String> ids = (List<String>) subparams.get("notIn");
						List<Integer> listStatusAsunto = new ArrayList<>();

						for (String id : ids) {
							listStatusAsunto.add(StatusAsunto.valueOf(id).ordinal());
						}

						if (listStatusAsunto.size() > 0) {
							restrictions
									.add(Restrictions.not(Restrictions.in("statusTurno.idStatus", listStatusAsunto)));
						}
					}
				}

				if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null)
					restrictions.add(Restrictions.between("fechaRegistro", //
							new Date((Long) params.get("fechaRegistroInicial")),
							new Date((Long) params.get("fechaRegistroFinal"))));

				if (params.get("fechaCompromisoInicial") != null && params.get("fechaCompromisoFinal") != null)
					restrictions.add(Restrictions.between("fechaCompromiso", //
							new Date((Long) params.get("fechaCompromisoInicial")),
							new Date((Long) params.get("fechaCompromisoFinal"))));

				if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") != null)
					restrictions.add(Restrictions.between("fechaEnvio", //
							new Date((Long) params.get("fechaEnvioInicial")),
							new Date((Long) params.get("fechaEnvioFinal"))));

				if (params.get("fechaAcuseInicial") != null && params.get("fechaAcuseFinal") != null)
					restrictions.add(Restrictions.between("fechaAcuse", //
							new Date((Long) params.get("fechaAcuseInicial")),
							new Date((Long) params.get("fechaAcuseFinal"))));
				// Parametros de En Tiempo, Fuera de Tiempo, Por vencer
				if (params.get("etfts") != null) {

					JSONObject etfts = new JSONObject("{" + params.get("etfts") + "}");
					JSONArray etftArray = etfts.getJSONArray("etft");
					Set<EnTiempo> tipos = new HashSet<>();
					for (int i = 0; i < etftArray.length(); ++i) {
						JSONObject etft = etftArray.getJSONObject(i);
						tipos.add(EnTiempo.valueOf(etft.getString("tipo")));
					}

					// Se agrega esta condicion ya que los registros que tenga
					// esta columna en null se consideran En Tiempo
					if (tipos.contains(EnTiempo.EN_TIEMPO)) {
						restrictions.add(
								Restrictions.or(Restrictions.in("enTiempo", tipos), Restrictions.isNull("enTiempo")));
					} else {
						restrictions.add(Restrictions.in("enTiempo", tipos));
					}
				}
			}

			// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +

			if (asunto.getAsuntoDetalle() != null) {

				if (asunto.getAsuntoDetalle().getNumDocto() != null)
					restrictions.add(EscapedLikeRestrictions.ilike("asuntoDetalle.numDocto",
							asunto.getAsuntoDetalle().getNumDocto(), MatchMode.ANYWHERE));

				if (asunto.getAsuntoDetalle().getIdProcedencia() != null)
					restrictions.add(Restrictions.eq("asuntoDetalle.idProcedencia",
							asunto.getAsuntoDetalle().getIdProcedencia()));

				if (asunto.getAsuntoDetalle().getAsuntoDescripcion() != null
						&& StringUtils.isNotBlank(asunto.getAsuntoDetalle().getAsuntoDescripcion()))
					restrictions.add(EscapedLikeRestrictions.ilike("asuntoDetalle.asuntoDescripcion",
							asunto.getAsuntoDetalle().getAsuntoDescripcion(), MatchMode.ANYWHERE));

				if (asunto.getAsuntoDetalle().getRemitente() != null)
					if (asunto.getAsuntoDetalle().getRemitente().getRemitenteKey() != null) {

						if (asunto.getAsuntoDetalle().getRemitente().getRemitenteKey().getIdRemitente() != null)
							restrictions.add(Restrictions.eq("asuntoDetalle.remitente.remitenteKey.idRemitente",
									asunto.getAsuntoDetalle().getRemitente().getRemitenteKey().getIdRemitente()));

						if (asunto.getAsuntoDetalle().getRemitente().getRemitenteKey().getPromotor() != null)
							if (asunto.getAsuntoDetalle().getRemitente().getRemitenteKey().getPromotor()
									.getIdInstitucion() != null)
								restrictions.add(
										Restrictions.eq("asuntoDetalle.remitente.remitenteKey.promotor.idInstitucion",
												asunto.getAsuntoDetalle().getRemitente().getRemitenteKey().getPromotor()
														.getIdInstitucion()));
					}

				if (asunto.getAsuntoDetalle().getDirigidoA() != null) {

					if (asunto.getAsuntoDetalle().getDirigidoA() != null) {
						if (asunto.getAsuntoDetalle().getDirigidoA().getIdUsuario() != null)
							Restrictions.eq("idUsuario", asunto.getAsuntoDetalle().getDirigidoA().getIdUsuario());
					}
					if (asunto.getAsuntoDetalle().getDirigidoA().getAreaAux() != null) {

						if (asunto.getAsuntoDetalle().getDirigidoA().getAreaAux().getInstitucion() != null)
							if (asunto.getAsuntoDetalle().getDirigidoA().getAreaAux().getInstitucion()
									.getIdInstitucion() != null)
								restrictions.add(
										Restrictions.eq("areaAux.institucion.idInstitucion", asunto.getAsuntoDetalle()
												.getDirigidoA().getAreaAux().getInstitucion().getIdInstitucion()));

						if (asunto.getAsuntoDetalle().getDirigidoA().getAreaAux().getIdArea() != null)
							restrictions.add(Restrictions.eq("areaAux.idArea",
									asunto.getAsuntoDetalle().getDirigidoA().getAreaAux().getIdArea()));
					}
				}

				if (asunto.getAsuntoDetalle().getPromotor() != null)
					if (asunto.getAsuntoDetalle().getPromotor().getIdInstitucion() != null)
						restrictions.add(Restrictions.eq("asuntoDetalle.promotor.idInstitucion",
								asunto.getAsuntoDetalle().getPromotor().getIdInstitucion()));

				if (asunto.getAsuntoDetalle().getFolioIntermedio() != null)
					restrictions.add(Restrictions.eq("asuntoDetalle.folioIntermedio",
							asunto.getAsuntoDetalle().getFolioIntermedio()));

				if (asunto.getAsuntoDetalle().getPalabraClave() != null
						&& StringUtils.isNotBlank(asunto.getAsuntoDetalle().getPalabraClave()))
					restrictions.add(EscapedLikeRestrictions.ilike("asuntoDetalle.palabraClave",
							asunto.getAsuntoDetalle().getPalabraClave(), MatchMode.ANYWHERE));
			}

			if (asunto.getPrioridad() != null && asunto.getPrioridad().getIdPrioridad() != null)
				restrictions.add(Restrictions.eq("prioridad.idPrioridad", asunto.getPrioridad().getIdPrioridad()));

			// * * * * * * * * * * * * * * * * * * * * * *

			List<Order> orders = new ArrayList<Order>();
			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			}

			// * * * * * * * * * * * * * * * * * * * * * *
			// lst = mngrAsunto.search(restrictions, orders, null,
			// body.getSize(), body.getBeginAt());

			if((restrictions.size() == 1 && restrictions.get(0).toString().contains("asuntoDetalle.confidencial")) || restrictions.size() == 0) {
				log.error(" :: QUUERY CON CONFIDENCIAL O SIN FILTROS, VALIDAR IDASUNTOPADRE Y IDORIGEN EN LA BASE:: ");
				throw new IllegalArgumentException("No se recibieron filtros para la consulta");
			} else {
				lst = mngrAsunto.search(restrictions, orders);
			}

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Buscar un asunto.
	 *
	 * @param body the body
	 * @return the response entity
	 */
	@RequestMapping(value = "/asunto/status", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> searchAsuntoStatus(
			@RequestBody(required = true) RequestWrapper<Asunto> body) {

		List<?> lst = new ArrayList<Asunto>();

		// Asunto asunto = body.getObject();
		// Map<String, Object> params = body.getParams();

		log.debug("PARAMETROS DE BUSQUEDA :: " + body);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			List<TipoAsunto> tipoAsunto = new ArrayList<>();
			tipoAsunto.add(TipoAsunto.COPIA);
			tipoAsunto.add(TipoAsunto.ENVIO);
			tipoAsunto.add(TipoAsunto.TURNO);

			restrictions.add(Restrictions.in("tipoAsunto", tipoAsunto));
			restrictions.add(Restrictions.eq("asuntoPadre.statusAsunto.idStatus", Status.PROCESO));

			// * * * * * * * * * * * * * * * * * * * * * *

			List<Order> orders = new ArrayList<Order>();
			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			}

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrAsunto.search(restrictions, orders, null, body.getSize(), body.getBeginAt());
			log.debug("Size found >> " + lst.size());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

	}

	/**
	 * Estrctura para generar el numero documento automatico.
	 */
	@Value("${estructuraNumDocto}")
	private String estructuraNumDocto;

	/**
	 * Calcula el Numero de Documento o Numero de Oficio.
	 *
	 * @param idRemitente the id remitente
	 * @return Numero de Documento o Numero de Oficio
	 */
	protected String getNumDoctoAutomatico(boolean isFolioMultiple, Integer idRemitente, Integer idFolioMultiple) {

		Integer idArea = idRemitente;
		List<Criterion> restrictions = new ArrayList<Criterion>();

		HashMap<String, Object> params = new HashMap<>();
		params.put("idTipo", 1);

		String numDocto = null;
		String prefijo = "";
		String sufijo = "";
		SimpleDateFormat sdf;

		if (isFolioMultiple) {

			params.put("idFoliopsMultiple", idFolioMultiple);

			FolioPSMultiple folio = mngrFoliopsmultiple.fetch(new FolioPSMultiple(idFolioMultiple, idArea));

			if (folio != null && "H".equals(folio.getTipo()))
				folio = mngrFoliopsmultiple.fetch(
						new FolioPSMultiple(Integer.valueOf(folio.getIdFolioHeredado()), folio.getIdAreaHereda()));

			folio = folio == null ? new FolioPSMultiple() : folio;

			prefijo = folio.getPrefijoFolio();
			sufijo = folio.getSufijoFolio();

		} else {

			params.put("idArea", idArea);

			FolioPS folio = mngrFoliops.fetch(idArea);
			folio = folio == null ? new FolioPS() : folio;

			prefijo = folio.getPrefijoFolio();
			sufijo = folio.getSufijoFolio();

		}

		do {

			Date now = new Date();

			numDocto = estructuraNumDocto.replace("{prefijo}", //
					StringUtils.isBlank(prefijo) ? "" : prefijo);

			String numDoctoAuto = isFolioMultiple
					? mngrFolioAreaMultiple.uniqueResult("generaNumDoctoMultiple", params).toString()
					: mngrArea.uniqueResult("generaNumDoctoAuto", params).toString();

			numDocto = numDocto.replace("{consecutivo}", //
					numDoctoAuto);

			numDocto = numDocto.replace("{consecutivo_trimed}", //
					StringUtils.stripStart(numDoctoAuto, "0"));

			// - - - - - - -

			sdf = new SimpleDateFormat("yyyy");
			numDocto = numDocto.replace("{year}", //
					sdf.format(now));

			sdf = new SimpleDateFormat("MM");
			numDocto = numDocto.replace("{month}", //
					sdf.format(now));

			sdf = new SimpleDateFormat("HH");
			numDocto = numDocto.replace("{day}", //
					sdf.format(now));

			// - - - - - - -

			numDocto = numDocto.replace("{sufijo}", StringUtils.isBlank(sufijo) ? "" : sufijo);

			// Validamos que el numero de documento ya no este creado
			restrictions.clear();
			restrictions.add(Restrictions.eq("area.idArea", idArea));
			restrictions.add(Restrictions.eq("asuntoDetalle.numDocto", numDocto));

		} while (!mngrAsunto.search(restrictions).isEmpty());

		return numDocto;
	}

	/**
	 * Calcula la Clave del Asunto.
	 *
	 * @param idArea
	 * @return
	 */
	private synchronized String getFolioClaveArea(Integer idArea) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("idArea", idArea);
		String folioClave = mngrArea.uniqueResult("generaFolioClave", params).toString();
		return folioClave;
	}

	/**
	 * Obtiene el tipo de expediente por defecto para el area del
	 * asunto/turno/envio.
	 *
	 * @param idArea Identificaro del area del que se va a obtener el Tipo de
	 *               Expediente
	 * @return Tipo de expediente por defecto para el area
	 */
	private TipoExpediente getTipoExpedienteDefault(Integer idArea) {
		List<Criterion> restrictions = new ArrayList<Criterion>();

		log.debug("Obteniendo el Tipo de Expediente por defecto del Area " + idArea);
		restrictions.add(
				Restrictions.eq("descripcion", environment.getProperty("folderNameExpedienteDefault")).ignoreCase());
		restrictions.add(Restrictions.eq("area.idArea", idArea));

		@SuppressWarnings("unchecked")
		List<TipoExpediente> tiposExpediente = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);

		if (tiposExpediente.isEmpty())
			log.warn("EL AREA ID " + idArea + " NO TIENE EXPEDIENTES!!!");

		TipoExpediente tipoExpediente = tiposExpediente.get(0);

		log.debug("El tipo de expediente por defecto es " + tipoExpediente);

		return tipoExpediente;
	}

	/**
	 * Gets the timestamps.
	 *
	 * @param id the id
	 * @return the timestamps
	 */
	@RequestMapping(value = "/asunto/timestamps", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Timestamp>> getTimestamps(
			@RequestParam(value = "id", required = true) Serializable id) {

		try {

			Asunto item = mngrAsunto.fetch(Integer.valueOf((String) id));

			return new ResponseEntity<List<Timestamp>>(item.getTimestamps(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Marcar un tramite como cancelado.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Cancelar tramite", notes = "Marca un tramite como cancelado")
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

	@RequestMapping(value = "/asunto/cancelar", method = RequestMethod.DELETE)
	public ResponseEntity<Asunto> cancelar(@RequestParam(value = "id", required = true) Serializable id)
			throws Exception {

		try {

			log.info("CANCELANDO EL TRAMITE ID :: " + id);

			Asunto item = mngrAsunto.fetch(Integer.valueOf((String) id));

			Status statusAsunto = item.getStatusAsunto();

			if (statusAsunto.getIdStatus() == Status.POR_ENVIAR || statusAsunto.getIdStatus() == Status.ENVIADO) {

				item.setStatusAsunto(mngrStatus.fetch(Status.CANCELADO));

				mngrAsunto.update(item);

				return new ResponseEntity<Asunto>(item, HttpStatus.OK);

			} else {

				throw new Exception(
						"El tramite se encuentra en status " + item.getStatusAsunto() + ". No puede ser cancelado.");

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Concluir asunto.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Concluir asunto", notes = "Concluye un asunto")
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

	@RequestMapping(value = "/asunto/concluir", method = RequestMethod.GET)
	public ResponseEntity<Asunto> concluirAsunto(@RequestParam(value = "id", required = true) String id)
			throws Exception {

		try {
			log.info("CONCLUYENDO EL ASUNTO ID :: " + id);

			Asunto item = mngrAsunto.fetch(Integer.valueOf(id));
			if (item != null) {
				// se marca como concluido
				item.setStatusAsunto(mngrStatus.fetch(Status.CONCLUIDO));
				mngrAsunto.update(item);

				return new ResponseEntity<Asunto>(item, HttpStatus.OK);
			}

			throw new BadRequestException();

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Concluir tramite.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Concluir tramite", notes = "Concluye un tramite")
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

	@RequestMapping(value = "/tramite/concluir", method = RequestMethod.GET)
	public ResponseEntity<Asunto> concluirTramite(@RequestParam(value = "id", required = true) String id)
			throws Exception {

		try {

			log.info("CONCLUYENDO EL TRAMITE ID :: " + id);

			Asunto item = mngrAsunto.fetch(Integer.valueOf(id));

			if (item != null) {

				if (item.getTipoAsunto() == TipoAsunto.TURNO //
						|| item.getTipoAsunto() == TipoAsunto.ENVIO//
						|| item.getTipoAsunto() == TipoAsunto.COPIA) {

					if (item.getStatusTurno().getIdStatus() == Status.POR_ENVIAR) {

						// se marca como concluido
						Status concludedStatus = mngrStatus.fetch(Status.CONCLUIDO);
						   item.setStatusTurno(concludedStatus);
						   item.setStatusAsunto(concludedStatus);

						mngrAsunto.update(item);

						return new ResponseEntity<Asunto>(item, HttpStatus.OK);

					}

				}

			}

			throw new BadRequestException();

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Envia un tramite a su area destino.
	 *
	 * @param id Identificador del Tramite a enviar
	 * @return Informacion completa del Tramite enviado
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Envia tramite area", notes = "Envia un tramite a su area destino")
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

	@RequestMapping(value = "/asunto/enviar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> enviar(@RequestParam(value = "id", required = true) Serializable id, Asunto asuntoPadre, Status status)
			throws Exception {

		try {

			log.info("ENVIANDO EL TRAMITE ID :: " + id);

			Integer idAsunto = Integer.valueOf((String) id);

			Asunto item = mngrAsunto.fetch(idAsunto);

			boolean agregarPermisoDocto = false;

			if ((item.getTipoAsunto().getValue().equals(TipoAsunto.TURNO.getValue())
					|| item.getTipoAsunto().getValue().equals(TipoAsunto.ENVIO.getValue())
					|| item.getTipoAsunto().getValue().equals(TipoAsunto.COPIA.getValue()))) {

				if (item.getStatusTurno().getIdStatus() == Status.POR_ENVIAR
						|| item.getStatusTurno().getIdStatus() == Status.RECHAZADO) {

					SubTipoAsunto subTipoAsunto = SubTipoAsunto.fromTipo(item.getIdSubTipoAsunto());
					String nombreDestinatario = "";
					switch (subTipoAsunto) {

					case C: /** Funcionarios Internos */

						// + + + + + + + + + + + + + + + + + + + + + + + + + + +
						// + + + + + + + + +

						log.debug("Enviando el asunto a un Funcionario Interno");

						List<Criterion> restrictions = new ArrayList<>();

						restrictions.add(Restrictions.ilike("idUsuario", item.getDestinatario()));

						List<?> lst = mngrUsuario.search(restrictions);

						if (lst.isEmpty())
							throw new Exception("Usuario destinatario no encontrado. :: " + item.getDestinatario());

						Usuario user = (Usuario) lst.get(0);

						Area areaDestino = item.getAreaDestino();

						if ((user != null && (!user.getActivo() || !user.getCapacitado()))) {

							throw new Exception(errorMessages.getString("errorNoEnvioElectronico") + user.getNombres()
									+ " " + user.getApellidoPaterno() + " " + user.getMaterno() + " "
									+ errorMessages.getString("errorNoEnvioElectronico2"));

						} else if (areaDestino != null && (!areaDestino.getActivo())) {

							throw new Exception(errorMessages.getString("errorNoEnvioElectronico") + user.getNombres()
									+ " " + user.getApellidoPaterno() + " " + user.getMaterno() + " "
									+ errorMessages.getString("errorNoEnvioElectronico3"));

						}

						agregarPermisoDocto = true;

						break;

					// + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
					// + + + + + + +

					case D: /** Ciudadano */

						// + + + + + + + + + + + + + + + + + + + + + + + + + + +
						// + + + + + + + + +

						Ciudadano ciudadano = mngrCiudadano.fetch(Integer.valueOf(item.getDestinatario()));

						nombreDestinatario = ciudadano.getNombreCompleto();

						if (StringUtils.isNotBlank(ciudadano.getEmail()) && isValidEmailAddress(ciudadano.getEmail())) {

							boolean sent = sendAsuntoViewerMail(item.getIdAsunto(), ciudadano.getNombreCompleto(),
									ciudadano.getEmail(), ciudadano.getRfc());
							if (sent)
								break;
						}

						throw new Exception(errorMessages.getString("errorNoEnvioElectronico") + nombreDestinatario);

					// + + + + + + + + + + + + + + + + + + + + + + + + + + +
					// + + + + + + + + +

					case R: /** Representante Legal */

						// + + + + + + + + + + + + + + + + + + + + + + + + + + +
						// + + + + + + + + +

						RepresentanteLegal repLegal = mngrRepresentanteLegal
								.fetch(Integer.valueOf(item.getDestinatario()));

						nombreDestinatario = repLegal.getNombreCompleto();

						if (StringUtils.isNotBlank(repLegal.getEmail()) && isValidEmailAddress(repLegal.getEmail())) {

							boolean sent = sendAsuntoViewerMail(item.getIdAsunto(), repLegal.getNombreCompleto(),
									repLegal.getEmail(), repLegal.getRfc());
							if (sent)
								break;
						}

						throw new Exception(errorMessages.getString("errorNoEnvioElectronico") + nombreDestinatario);

					// + + + + + + + + + + + + + + + + + + + + + + + + + + +
					// + + + + + + + + +

					case F: /** Funcionarios Externos */

						// + + + + + + + + + + + + + + + + + + + + + + + + + + +
						// + + + + + + + + +

						nombreDestinatario = mngrRepresentante.fetch(item.getDestinatario()).getNombreCompleto();

						throw new BadRequestException(
								errorMessages.getString("errorNoEnvioElectronico") + nombreDestinatario);

					// + + + + + + + + + + + + + + + + + + + + + + + + + + +
					// + + + + + + + + +

					default:

						log.warn("TIPO DESTINATARIO NO ENCONTRADO!!!");

						break;

					}

					// Setea el nuevo estatus y el timeStamp para el tramite
					item = setEstatusSetTimeStampTramite(item);

					final List<DocumentoAsunto> documentosAsunto = agregarPermisoDocto ? 
																	getDocumentosAsunto(item.getIdAsunto()) : null;
										
					Map<String, String> additionalData = new HashMap<>();
					// Para el caso de los asuntos confidenciales, se le
					// asigna el ACL de Tramites confidenciales
					final String aclName = item.getAsuntoDetalle().getConfidencial() && agregarPermisoDocto ? 
											"aclNameAdjuntoTramiteConfidencial" : "aclNameAdjuntoTramite";
					ResponseEntity<List<Map<String, String>>> documentosCompartidos = agregarPermisoDocto ?
																	documentoCompartidoController.getDocumentosCompartidos(idAsunto) : null;
					if (agregarPermisoDocto) {
						// Le asignamos el permiso de lectura al area a la que
						// se
						// esta enviando el Tramite por cada documento que tenga
						// el
						// Asunto Padre
						additionalData.put("idArea", String.valueOf(item.getAreaDestino().getIdArea()));
						
						if(documentosAsunto != null)
							documentosAsunto.parallelStream().forEach(documentoAsunto -> {
								try {
									for (int i = 0; i <= 3; i++) {
										boolean resultAcl = agregarPermisoDocumento(documentoAsunto.getObjectId(),
												environment.getProperty(aclName), additionalData);
										if (resultAcl) {
											break;
										} else if (resultAcl == Boolean.FALSE && i == 3) {
											throw new Exception();
										}
									}
								} catch (Exception e) {

									log.error("error agregando permisos en documentos del asunto");
									revocarPermisosDocumentos(documentosAsunto, null, aclName, additionalData);
									throw new RuntimeException("Error agregando permisos a los documentos del asunto.");
								}
							});

						documentosCompartidos.getBody().parallelStream().forEach(docCompartido -> {
							try {
								for (int i = 0; i <= 3; i++) {
									boolean resultAcl = agregarPermisoDocumento(docCompartido.get("r_object_id"),
											environment.getProperty(aclName), additionalData);
									if (resultAcl) {
										break;
									} else if (resultAcl == Boolean.FALSE && i == 3) {
										throw new Exception();
									}
								}
							} catch (Exception e) {

								log.error("error agregando permisos en documentos compartidos del asunto");
								revocarPermisosDocumentos(documentosAsunto, documentosCompartidos.getBody(), aclName,
										additionalData);
								throw new RuntimeException(
										"Error agregando permisos a los documentos compartidos del asunto.");
							}
						});
					}
					try {
						// Actualiza el tramite
						mngrAsunto.update(item);
					} catch (Exception e) {
						revocarPermisosDocumentos(documentosAsunto, documentosCompartidos.getBody(), aclName,
								additionalData);
					}
					/** Guardamos el registros en la tabla CUSTOMASUNTO */
					log.debug("Creando el registro en CUSTOMASUNTO para el tramite " + item.getIdAsunto());
					CustomAsunto customAsunto = new CustomAsunto();
					customAsunto.setIdAsunto(item.getIdAsunto());
					customAsunto.setCustom0(String.valueOf(item.getAreaDestino().getIdArea()));
					mngrCustomAsunto.save(customAsunto);

					/** Actualizamos el estatus del Asunto Padre */
					actualizaAsuntoPadreDelTramite(item, asuntoPadre, status);

					/** * * * * * * * * * */

					return new ResponseEntity<Asunto>(item, HttpStatus.OK);

				} else {

					throw new Exception(
							"El tramite se encuentra en un estado diferente a registrado. No puede ser enviado.");
				}
			} else {

				throw new Exception("El tipo de tramite : " + item.getTipoAsunto() + " no puede ser enviado.");
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Sets the estatus set time stamp tramite.
	 *
	 * @param item the item
	 * @return the asunto
	 * @throws Exception the exception
	 */
	protected Asunto setEstatusSetTimeStampTramite(Asunto item) throws Exception {
		item.setStatusTurno(mngrStatus.fetch(Status.ENVIADO));
		item.setStatusAsunto(mngrStatus.fetch(Status.ENVIADO));

		item.setComentarioRechazo("");

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

		return item;
	}

	/**
	 * Actualiza asunto padre del tramite.
	 *
	 * @param item the item
	 */
	protected void actualizaAsuntoPadreDelTramite(Asunto item, Asunto itemPadre, Status status) {		
		Asunto asuntoPadre = null;
		if(itemPadre != null)
			asuntoPadre = itemPadre;
		else
			asuntoPadre = mngrAsunto.fetch(item.getIdAsuntoPadre());

		if (!asuntoPadre.getStatusAsunto().getIdStatus().equals(Status.PROCESO)) {

			if (!asuntoPadre.getTipoAsunto().equals(TipoAsunto.ASUNTO) //
					&& item.getInstruccion() != null //
					&& item.getInstruccion().getRequiereRespuesta().equals(false)) {

				log.warn("No Actualiza la Copia o el Tramite, el tramite no requiere respuesta..");

			} else if (asuntoPadre.getTipoAsunto().equals(TipoAsunto.ASUNTO)) {

				log.debug("Actualizando el Asunto padre a PROCESO..");
				if(status != null)
					asuntoPadre.setStatusAsunto(status);
				else
					asuntoPadre.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));
				// VALIDAR
				// asuntoPadre.setStatusTurno(mngrStatus.fetch(Status.PROCESO));

				mngrAsunto.update(asuntoPadre);

			}
		}
	}

	/**
	 * Save tramites list.
	 *
	 * @param tramites the tramites
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Enviar tramite", notes = "Envia un tramite o varios tramites")
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

	@RequestMapping(value = "/asunto/enviar/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> saveDocumentList(
			@RequestBody(required = true) List<Asunto> tramites) throws Exception {

		try {
			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();

			if (!tramites.isEmpty()) {
				Asunto asuntoPadre = mngrAsunto.fetch(tramites.get(0).getIdAsuntoPadre());
				Status status = mngrStatus.fetch(Status.PROCESO);							

				tramites.parallelStream().forEach(tramite -> {
					try {
						
						ResponseEntity<Asunto> rr = enviar(tramite.getIdAsunto().toString(), asuntoPadre, status);
						asuntoPadre.setStatusAsunto(status);
						success.add(rr.getBody());

					} catch (BadRequestException e) {
						listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()), HttpStatus.BAD_REQUEST);
					} catch (Exception e) {
						if (e.getMessage().contains(errorMessages.getString("errorNoEnvioElectronico2")))
							listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()),
									errorMessages.getString("errorNoEnvioElectronico2"));
						else
							listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()),
									HttpStatus.INTERNAL_SERVER_ERROR);
					}

				});

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
	 * Obtiene un arbol con todos los hijos de los nodos de un tramite desde su
	 * asunto origen.
	 *
	 * @param id the id
	 * @return the seguimiento
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene seguimiento", notes = "Obtiene un arbol con todos los hijos de los nodos de un tramite desde su asunto origen")
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

	@RequestMapping(value = "/asunto/seguimiento", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TreeNode<AsuntoSeguimiento>> getSeguimiento(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		Integer idAsunto = Integer.valueOf((String) id);

		TreeNode<AsuntoSeguimiento> top;

		if (idAsunto != null) {

			top = new TreeNode<AsuntoSeguimiento>(mngrAsuntoSeguimiento.fetch(idAsunto));
			getChild(top, true, null);

		} else {
			top = new TreeNode<AsuntoSeguimiento>(null);
		}

		return new ResponseEntity<TreeNode<AsuntoSeguimiento>>(top, HttpStatus.OK);

	}
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene seguimiento", notes = "Obtiene todos los hijos de un asunto desde el asunto origen")
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

	@RequestMapping(value = "/asunto/seguimiento2", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<AsuntoSeguimiento>> getSeguimiento2(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		int level = 0;
		List<AsuntoSeguimiento> descendientes = new ArrayList<>();
		Integer idAsunto = Integer.valueOf((String) id);
		
		
		// ASUNTO PADRE //
		AsuntoSeguimiento asuntoPadre = mngrAsuntoSeguimiento.fetch(idAsunto);
		
		if(asuntoPadre != null) {
			
			// SET ASUNTO PADRE
			asuntoPadre.setLevel(String.valueOf(level));
			descendientes.add(asuntoPadre);
			
			// GET HIJOS //
			getHijos(asuntoPadre, descendientes, level);
		}

		return new ResponseEntity<List<AsuntoSeguimiento>>(descendientes, HttpStatus.OK);

	}

	/**
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Descargar seguimiento", notes = "Descarga el seguimiento de un tramite en Base 64")
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

	@RequestMapping(value = "/asunto/descargar/seguimiento", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, String>> getDescargarSeguimiento(
			@RequestBody(required = true) Map<String, Object> body) throws Exception {

		Integer idAsunto = null;
		ValidaRespuestaSeguimiento conRespuesta = new ValidaRespuestaSeguimiento();
		conRespuesta.setValidaRFE(false);
		conRespuesta.setValidaRFA(false);

		if (body.get("idAsunto") != null) {
			idAsunto = Integer.valueOf(body.get("idAsunto").toString());
		}

		if (idAsunto != null) {
			String idAsuntoOrigen = "";
			String imagen = null;
			String imagen64 = null;
			byte imagenLogo[] = null;

			Asunto asunto = mngrAsunto.fetch(idAsunto);

			if (body.get("idAsuntoOrigen") != null) {
				idAsuntoOrigen = body.get("idAsuntoOrigen").toString();
			} else {
				try {
					idAsuntoOrigen = asunto.getIdAsuntoOrigen().toString();
				} catch (Exception e) {
					idAsuntoOrigen = "";
				}
			}

			if (body.get("imagenLogo") != null) {
				imagen = body.get("imagenLogo").toString();
				imagen64 = imagen.substring(imagen.indexOf(",") + 1);
				imagenLogo = Base64.decodeBase64(imagen64);
			}

			Date fechaRegistro = asunto.getFechaRegistro();
			Map<String, String> item = new HashMap<>();

			TreeNode<AsuntoSeguimiento> top;
			top = new TreeNode<AsuntoSeguimiento>(mngrAsuntoSeguimiento.fetch(idAsunto));
			getChild(top, true, null);

			Document document = new Document(PageSize.LETTER, 40, 40, 40, 60);
			PdfWriter writer = null;
			try(ByteArrayOutputStream stream = new ByteArrayOutputStream()){
				writer = PdfWriter.getInstance(document, stream);

				document.open();

				Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
				Font fontBold = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
				Font fontNormalPie = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
				Font fontBoldPie = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
				Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
				Font fontSeguimiento = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

				AsuntoSeguimiento asuntoSeguimiento = top.getObject();

				try {
					Image image = Image.getInstance(imagenLogo);
					image.scaleAbsolute(150f, 50f);
					document.add(image);
				} catch (Exception e) {
					log.info("No se logró generar la imagen para el PDF");
				}

				String pattern = "dd/MM/yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

				Paragraph fechaImpresion = new Paragraph(new Chunk("Fecha de elaboración: ", fontBold));
				fechaImpresion.add(new Chunk(simpleDateFormat.format(new Date()), fontNormal));
				fechaImpresion.setAlignment(Element.ALIGN_RIGHT);
				document.add(fechaImpresion);

				document.add(Chunk.NEWLINE);

				Paragraph titulo = new Paragraph("Seguimiento de Asunto", fontTitulo);
				titulo.setAlignment(Element.ALIGN_CENTER);
				document.add(titulo);

				document.add(Chunk.NEWLINE);

				Paragraph area = new Paragraph(new Chunk("Área: ", fontBold));
				area.add(new Chunk(asuntoSeguimiento.getArea() != null ? asuntoSeguimiento.getArea() : "", fontNormal));
				area.setAlignment(Element.ALIGN_LEFT);
				document.add(area);

				Paragraph pFechaRegistro = new Paragraph(new Chunk("Fecha de registro: ", fontBold));
				if (fechaRegistro != null)
					pFechaRegistro.add(new Chunk(simpleDateFormat.format(fechaRegistro), fontNormal));
				pFechaRegistro.setAlignment(Element.ALIGN_LEFT);
				document.add(pFechaRegistro);

				Paragraph idOrigen = new Paragraph(new Chunk("ID. origen: ", fontBold));
				idOrigen.add(new Chunk(idAsuntoOrigen, fontNormal));
				idOrigen.setAlignment(Element.ALIGN_LEFT);
				document.add(idOrigen);

				Paragraph descripcion = new Paragraph(new Chunk("Descripción: ", fontBold));
				descripcion.add(new Chunk(
						asuntoSeguimiento.getAsuntoDescripcion() != null ? asuntoSeguimiento.getAsuntoDescripcion() : "",
						fontNormal));
				descripcion.setAlignment(Element.ALIGN_JUSTIFIED);
				descripcion.setIndentationLeft(77);
				descripcion.setFirstLineIndent(-77);
				document.add(descripcion);

				document.add(Chunk.NEWLINE);

				Paragraph parrafo = getParrafo(asuntoSeguimiento, fontSeguimiento, conRespuesta);
				document.add(parrafo);

				List<TreeNode<AsuntoSeguimiento>> listaNodos = top.getChildren();

				if (listaNodos != null)
					addParrafosNodos(listaNodos, document, fontSeguimiento, 16, conRespuesta);

				Phrase pline = new Phrase(new Chunk(
						"_______________________________________________________________________________________________",
						fontNormalPie));
				Phrase pfe = new Phrase(new Chunk("FE: ", fontBoldPie));
				pfe.add(new Chunk("Fecha de envío.", fontNormalPie));
				Phrase pfa = new Phrase(new Chunk("FA: ", fontBoldPie));
				pfa.add(new Chunk("Fecha de acuse.", fontNormalPie));
				Phrase prfe = new Phrase(new Chunk("RFE: ", fontBoldPie));
				prfe.add(new Chunk("Respuesta fecha de envío.", fontNormalPie));
				Phrase prfa = new Phrase(new Chunk("RFA: ", fontBoldPie));
				prfa.add(new Chunk("Respuesta fecha de acuse.", fontNormalPie));

				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pline, 40, 55, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pfe, 50, 40, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pfa, 50, 30, 0);
				if (conRespuesta.getValidaRFE().equals(true)) {
					ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, prfe, 50, 20, 0);
				}
				if (conRespuesta.getValidaRFA().equals(true)) {
					ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, prfa, 50, 10, 0);
				}
				//writer.close();
				document.close();

				item.put("type", "application/pdf");
				item.put("name", "Seguimiento_Asunto_" + idAsunto.toString() + ".pdf");
				item.put("contentB64", Base64.encodeBase64String(stream.toByteArray()));
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			} finally {
				if(null != writer && !writer.isCloseStream())writer.close();
				if(document.isOpen()) document.close();
			}
			
			return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);
			
		} else {
			throw new BadRequestException("El id del asunto no se encuentra registrado");
		}
	}
	
	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Descargar seguimiento", notes = "Descarga el seguimiento de un tramite en Base 64")
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

	@RequestMapping(value = "/asunto/descargar/seguimiento2", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, String>> getDescargarSeguimiento2(
			@RequestBody(required = true) Map<String, Object> body) throws Exception {

		Integer idAsunto = null;
		ValidaRespuestaSeguimiento conRespuesta = new ValidaRespuestaSeguimiento();
		conRespuesta.setValidaRFE(false);
		conRespuesta.setValidaRFA(false);

		URL rET = getClass().getClassLoader().getResource("/images/ET.png");
		Image imgET = Image.getInstance(Objects.requireNonNull(rET));
		imgET.scaleAbsolute(8f, 8f);

		URL rFT = getClass().getClassLoader().getResource("/images/FT.png");
		Image imgFT = Image.getInstance(Objects.requireNonNull(rFT));
		imgFT.scaleAbsolute(8f, 8f);

		if (body.get("idAsunto") != null) {
			idAsunto = Integer.valueOf(body.get("idAsunto").toString());
		}

		if (idAsunto != null) {
			String idAsuntoOrigen = "";
			String imagen = null;
			String imagen64 = null;
			byte imagenLogo[] = null;

			Asunto asunto = mngrAsunto.fetch(idAsunto);

			if (body.get("idAsuntoOrigen") != null) {
				idAsuntoOrigen = body.get("idAsuntoOrigen").toString();
			} else {
				try {
					idAsuntoOrigen = asunto.getIdAsuntoOrigen().toString();
				} catch (Exception e) {
					idAsuntoOrigen = "";
				}
			}

			if (body.get("imagenLogo") != null) {
				imagen = body.get("imagenLogo").toString();
				imagen64 = imagen.substring(imagen.indexOf(",") + 1);
				imagenLogo = Base64.decodeBase64(imagen64);
			}

			Date fechaRegistro = asunto.getFechaRegistro();
			Map<String, String> item = new HashMap<>();

			Document document = new Document(PageSize.LETTER.rotate(), 30, 30, 30, 50);
			PdfWriter writer = null;
			try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
				writer = PdfWriter.getInstance(document, stream);

				document.open();

				Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
				Font fontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
				Font fontNormalPie = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK);
				Font fontBoldPie = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
				Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLD, BaseColor.BLACK);
				Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
				Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);

				// ASUNTO PADRE //
				AsuntoSeguimiento asuntoPadre = mngrAsuntoSeguimiento.fetch(idAsunto);

				try {
					Image image = Image.getInstance(imagenLogo);
					image.scaleAbsolute(150f, 50f);
					document.add(image);
				} catch (Exception e) {
					log.info("No se logró generar la imagen para el PDF");
				}

				String pattern = "dd/MM/yy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

				Paragraph fechaImpresion = new Paragraph(new Chunk("Fecha de elaboración: ", fontBold));
				fechaImpresion.add(new Chunk(simpleDateFormat.format(new Date()), fontNormal));
				fechaImpresion.setAlignment(Element.ALIGN_RIGHT);
				document.add(fechaImpresion);

				document.add(Chunk.NEWLINE);

				Paragraph titulo = new Paragraph("Seguimiento de Asunto", fontTitulo);
				titulo.setAlignment(Element.ALIGN_CENTER);
				document.add(titulo);

				document.add(Chunk.NEWLINE);

				Paragraph area = new Paragraph(new Chunk("Área: ", fontBold));
				area.add(new Chunk(asuntoPadre.getArea() != null ? asuntoPadre.getArea() : "", fontNormal));
				area.setAlignment(Element.ALIGN_LEFT);
				document.add(area);

				Paragraph pFechaRegistro = new Paragraph(new Chunk("Fecha de registro: ", fontBold));
				if (fechaRegistro != null)
					pFechaRegistro.add(new Chunk(simpleDateFormat.format(fechaRegistro), fontNormal));
				pFechaRegistro.setAlignment(Element.ALIGN_LEFT);
				document.add(pFechaRegistro);

				Paragraph idOrigen = new Paragraph(new Chunk("ID. origen: ", fontBold));
				idOrigen.add(new Chunk(idAsuntoOrigen, fontNormal));
				idOrigen.setAlignment(Element.ALIGN_LEFT);
				document.add(idOrigen);

				Paragraph descripcion = new Paragraph(new Chunk("Descripción: ", fontBold));
				descripcion.add(
						new Chunk(asuntoPadre.getAsuntoDescripcion() != null ? asuntoPadre.getAsuntoDescripcion() : "",
								fontNormal));
				descripcion.setAlignment(Element.ALIGN_JUSTIFIED);
				descripcion.setIndentationLeft(77);
				descripcion.setFirstLineIndent(-77);
				document.add(descripcion);

				document.add(Chunk.NEWLINE);

				int level = 0;
				List<AsuntoSeguimiento> descendientes = new ArrayList<>();
				if (asuntoPadre != null) {
					// SET ASUNTO PADRE
					asuntoPadre.setLevel(String.valueOf(level));
					descendientes.add(asuntoPadre);
					// GET HIJOS //
					getHijos(asuntoPadre, descendientes, level);
				}

				PdfPTable pdfPTable = new PdfPTable(24);
				pdfPTable.setWidthPercentage(100);

				// Add cells to table
				pdfPTable.addCell(new PdfPCell(new Paragraph("Folio", fontHeader))).setColspan(2);
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell(new Paragraph("ETFT", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Institución", fontHeader))).setColspan(2);
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell(new Paragraph("Área", fontHeader))).setColspan(4);
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell(new Paragraph("Titular", fontHeader))).setColspan(2);
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell(new Paragraph("FE", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("FC", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Tipo asunto", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Estado asunto/tramite", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("FA", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Instrucción", fontHeader))).setColspan(2);
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell(new Paragraph("RFE", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Resp.", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Tipo resp.", fontHeader))).setColspan(1);
				pdfPTable.addCell(new PdfPCell(new Paragraph("Comentario de rechazo", fontHeader))).setColspan(2);
				pdfPTable.addCell(new PdfPCell());
				pdfPTable.addCell(new PdfPCell(new Paragraph("RR", fontHeader))).setColspan(1);

				for (AsuntoSeguimiento asuntoSeguimiento : descendientes) {
					String tab = "";
					for (int j = 0; j < Integer.valueOf(asuntoSeguimiento.getLevel()); j++) {
						tab = tab + "   ";
					}
					// Folio
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getFolioArea(), fontBody)))
							.setColspan(2);
					pdfPTable.addCell(new PdfPCell());
					// ETFT
					if (TipoAsunto.ASUNTO.getValue().equals(asuntoSeguimiento.getTipoAsunto().getValue())) {
						pdfPTable.addCell(new PdfPCell(new Phrase(""))).setColspan(1);
					} else {
						if (asuntoSeguimiento.getEnTiempo() != null) {
							pdfPTable.addCell(new PdfPCell(new Phrase(new Chunk(
									asuntoSeguimiento.getEnTiempo() == EnTiempo.FUERA_DE_TIEMPO ? imgFT : imgET, 4, 8,
									true)))).setColspan(1);
						} else
							pdfPTable.addCell(new PdfPCell(new Phrase(""))).setColspan(1);

					}
					// Institucion
					pdfPTable
							.addCell(new PdfPCell(new Phrase(
									asuntoSeguimiento.getTipoAsunto().getValue() == TipoAsunto.ASUNTO.getValue()
											? asuntoSeguimiento.getPromotor()
											: asuntoSeguimiento.getAreaDestino().getInstitucion().getDescripcion(),
									fontBody)))
							.setColspan(2);
					pdfPTable.addCell(new PdfPCell());
					// Area
					pdfPTable
							.addCell(
									new PdfPCell(new PdfPCell(new Phrase(
											asuntoSeguimiento.getTipoAsunto().getValue() == TipoAsunto.ASUNTO.getValue()
													? "»" + asuntoSeguimiento.getArea()
													: tab + "»" + asuntoSeguimiento.getAreaDestino().getDescripcion(),
											fontBody))))
							.setColspan(4);
					pdfPTable.addCell(new PdfPCell());
					pdfPTable.addCell(new PdfPCell());
					pdfPTable.addCell(new PdfPCell());
					// Titular
					pdfPTable.addCell(new PdfPCell(new PdfPCell(new Phrase(
							asuntoSeguimiento.getTipoAsunto().getValue() == TipoAsunto.ASUNTO.getValue()
									? asuntoSeguimiento.getTitularArea()
									: asuntoSeguimiento.getAreaDestino().getTitular().getNombreCompleto(),
							fontBody)))).setColspan(2);
					pdfPTable.addCell(new PdfPCell());
					// Fecha Envio
					pdfPTable
							.addCell(
									new PdfPCell(
											new Phrase(
													asuntoSeguimiento.getFechaEnvio() != null ? simpleDateFormat
															.format(asuntoSeguimiento.getFechaEnvio()).toString() : "",
													fontBody)))
							.setColspan(1);
					// Fecha Compromiso
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getFechaCompromiso() != null
							? simpleDateFormat.format(asuntoSeguimiento.getFechaCompromiso()).toString()
							: "", fontBody))).setColspan(1);
					// Tipo Asunto
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getTipoAsunto().name(), fontBody)))
							.setColspan(1);
					// Estatus
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getStatusTurno().toString(), fontBody)))
							.setColspan(1);
					// Fecha Acuse
					pdfPTable
							.addCell(
									new PdfPCell(
											new Phrase(
													asuntoSeguimiento.getFechaAcuse() != null ? simpleDateFormat
															.format(asuntoSeguimiento.getFechaAcuse()).toString() : "",
													fontBody)))
							.setColspan(1);
					// Instruccion
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getInstruccionDescripcion(), fontBody)))
							.setColspan(2);
					pdfPTable.addCell(new PdfPCell());
					// Respuesta Fecha Envio
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getRespuesta() != null
							&& asuntoSeguimiento.getRespuesta().getFechaEnvio() != null
									? simpleDateFormat.format(asuntoSeguimiento.getRespuesta().getFechaEnvio())
											.toString()
									: "",
							fontBody))).setColspan(1);
					// Respuesta Estatus
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getRespuesta() != null
							&& asuntoSeguimiento.getRespuesta().getStatus() != null
									? asuntoSeguimiento.getRespuesta().getStatus().getDescripcion()
									: "",
							fontBody))).setColspan(1);
					// Respuesta Tipo Respuesta
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getRespuesta() != null
							&& asuntoSeguimiento.getRespuesta().getTipoRespuestaDescripcion() != null
									? asuntoSeguimiento.getRespuesta().getTipoRespuestaDescripcion()
									: "",
							fontBody))).setColspan(1);
					// Comentario Rechazo Respuesta
					pdfPTable.addCell(new PdfPCell(new Phrase(asuntoSeguimiento.getComentarioRechazo(), fontBody)))
							.setColspan(2);
					pdfPTable.addCell(new PdfPCell());
					// Requiere Respuesta
					pdfPTable
							.addCell(new PdfPCell(
									new Phrase(asuntoSeguimiento.getRequiereRespuesta() ? "SI" : "NO", fontBody)))
							.setColspan(1);
				}

				document.add(pdfPTable);

				Phrase pline = new Phrase(new Chunk(
						"_______________________________________________________________________________________________",
						fontNormalPie));
				Phrase pfe = new Phrase(new Chunk("FE: ", fontBoldPie));
				pfe.add(new Chunk("Fecha de envío.", fontNormalPie));
				Phrase pfc = new Phrase(new Chunk("FC: ", fontBoldPie));
				pfc.add(new Chunk("Fecha de compromiso.", fontNormalPie));
				Phrase pfa = new Phrase(new Chunk("FA: ", fontBoldPie));
				pfa.add(new Chunk("Fecha de acuse.", fontNormalPie));
				Phrase prfe = new Phrase(new Chunk("RFE: ", fontBoldPie));
				prfe.add(new Chunk("Respuesta fecha de envío.", fontNormalPie));
				Phrase rr = new Phrase(new Chunk("RR: ", fontBoldPie));
				rr.add(new Chunk("Requiere respuesta.", fontNormalPie));

				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pline, 40, 60, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pfe, 50, 50, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pfc, 50, 40, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, pfa, 50, 30, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, prfe, 50, 20, 0);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, rr, 50, 10, 0);
				// writer.close();
				document.close();

				item.put("type", "application/pdf");
				item.put("name", "Seguimiento_Asunto_" + idAsunto.toString() + ".pdf");
				item.put("contentB64", Base64.encodeBase64String(stream.toByteArray()));
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			} finally {
				if (null != writer && !writer.isCloseStream())
					writer.close();
				if (document.isOpen())
					document.close();
			}

			return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);

		} else {
			throw new BadRequestException("El id del asunto no se encuentra registrado");
		}
	}

	/**
	 * 
	 * @param asuntoSeguimiento
	 * @param fontSeguimiento
	 * 
	 */
	private Paragraph getParrafo(AsuntoSeguimiento asuntoSeguimiento, Font fontSeguimiento,
			ValidaRespuestaSeguimiento conRespuesta) {
		TipoAsunto tipoAsunto = asuntoSeguimiento.getTipoAsunto();
		RespuestaSeguimiento respuesta = asuntoSeguimiento.getRespuesta();
		Boolean requiereRespuesta = asuntoSeguimiento.getRequiereRespuesta();
		String folioArea = asuntoSeguimiento.getFolioArea();
		EnTiempo enTiempo = asuntoSeguimiento.getEnTiempo();
		String area = asuntoSeguimiento.getArea() != null ? asuntoSeguimiento.getArea() : "";
		String areaDestino = asuntoSeguimiento.getAreaDestino() != null ? asuntoSeguimiento.getAreaDestino().getDescripcion() : "";
		String statusturno = asuntoSeguimiento.getStatusTurno().toString();
		String comentarioRechazo = asuntoSeguimiento.getComentarioRechazo();
		Date fechaEnvio = asuntoSeguimiento.getFechaEnvio();
		Date fechaAcuse = asuntoSeguimiento.getFechaAcuse();
		String tipoRespuestaDescripcion = null;
		String statusDescripcion = null;

		String pattern = "dd/MM/yyyy hh:mm aaa";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		if (respuesta != null) {
			tipoRespuestaDescripcion = respuesta.getTipoRespuestaDescripcion() != null
					? respuesta.getTipoRespuestaDescripcion().toLowerCase()
					: "";
			statusDescripcion = respuesta.getStatus().getDescripcion() != null
					? upperCaseFirst(respuesta.getStatus().getDescripcion())
					: "";
		}

		Paragraph parrafo = new Paragraph();

		Font fontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
		Font fontEnTiempo = FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(141, 198, 63));
		Font fontFueraTiempo = FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(229, 36, 32));
		Font fontPorVencer = FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(247, 203, 29));

		parrafo.add(new Chunk(tipoAsunto.name().toString(), fontBold));
		parrafo.add(new Chunk(": ", fontBold));

		parrafo.add(new Chunk(folioArea != null ? folioArea : "Sin Folio", fontSeguimiento));
		parrafo.add(new Chunk(" - ", fontSeguimiento));

		switch (enTiempo) {
		case EN_TIEMPO:
			parrafo.add(new Chunk("En tiempo", fontEnTiempo));
			break;

		case FUERA_DE_TIEMPO:
			parrafo.add(new Chunk("Fuera de tiempo", fontFueraTiempo));
			break;

		case POR_VENCER:
			parrafo.add(new Chunk("Por vencer", fontPorVencer));
			break;

		default:
			break;
		}

		parrafo.add(new Chunk(" - ", fontSeguimiento));

		parrafo.add(new Chunk(tipoAsunto == TipoAsunto.ASUNTO ? area : areaDestino, fontSeguimiento));

		if (!requiereRespuesta || tipoAsunto == TipoAsunto.COPIA)
			parrafo.add(new Chunk(" - No requiere respuesta", fontSeguimiento));

		if (respuesta != null) {
			parrafo.add(new Chunk(" - Respuesta de " + tipoRespuestaDescripcion, fontSeguimiento));
			parrafo.add(new Chunk(" - " + statusDescripcion, fontSeguimiento));
		}

		if (statusturno == null || (respuesta == null && requiereRespuesta && !statusturno.equals("RECHAZADO")
				&& !statusturno.equals("ATENDIDO") && tipoAsunto != TipoAsunto.COPIA))
			parrafo.add(new Chunk(" - Sin respuesta", fontSeguimiento));

		if (statusturno != null && statusturno.equals("RECHAZADO")
				&& (tipoAsunto == TipoAsunto.TURNO || tipoAsunto == TipoAsunto.ENVIO))
			parrafo.add(new Chunk(" - Rechazado Enviado", fontSeguimiento));

		if (comentarioRechazo != null && statusturno != null && statusturno.equals("ATENDIDO")
				&& (tipoAsunto == TipoAsunto.TURNO || tipoAsunto == TipoAsunto.ENVIO))
			parrafo.add(new Chunk(" - Rechazado Atendido", fontSeguimiento));

		if (fechaEnvio != null) {
			parrafo.add(new Chunk(" FE: ", fontBold));
			parrafo.add(new Chunk(simpleDateFormat.format(fechaEnvio).toString(), fontSeguimiento));
		}

		if (fechaAcuse != null) {
			parrafo.add(new Chunk(" FA: ", fontBold));
			parrafo.add(new Chunk(simpleDateFormat.format(fechaAcuse).toString(), fontSeguimiento));
		}

		if (respuesta != null && tipoRespuestaDescripcion != null && tipoRespuestaDescripcion.equals("concluido")) {
			Date fechaEnvioR = respuesta.getFechaEnvio();
			Date fechaAcuseR = respuesta.getFechaAcuse();

			if (fechaEnvioR != null) {
				conRespuesta.setValidaRFE(true);
				parrafo.add(new Chunk(" RFE: ", fontBold));
				parrafo.add(new Chunk(simpleDateFormat.format(respuesta.getFechaEnvio()).toString(), fontSeguimiento));
			}
			if (fechaAcuseR != null) {
				conRespuesta.setValidaRFA(true);
				parrafo.add(new Chunk(" RFA: ", fontBold));
				parrafo.add(new Chunk(simpleDateFormat.format(respuesta.getFechaAcuse()).toString(), fontSeguimiento));
			}
		}

		return parrafo;
	}

	/**
	 * 
	 * @param val
	 * 
	 */
	private String upperCaseFirst(String val) {
		val = val.toLowerCase();
		char[] arr = val.toCharArray();
		arr[0] = Character.toUpperCase(arr[0]);
		return new String(arr);
	}

	/**
	 * 
	 * @param listaNodos
	 * @param document
	 * @param fontSeguimiento
	 * @param identacion
	 */
	private void addParrafosNodos(List<TreeNode<AsuntoSeguimiento>> listaNodos, Document document, Font fontSeguimiento,
			float identacion, ValidaRespuestaSeguimiento conRespuesta) throws DocumentException {
		List<TipoAsunto> order = Arrays.asList(TipoAsunto.ENVIO, TipoAsunto.TURNO, TipoAsunto.COPIA);
		Collections.sort(listaNodos, (nodo1, nodo2) -> order.indexOf(nodo1.getObject().getTipoAsunto())
				- order.indexOf(nodo2.getObject().getTipoAsunto()));
		for (int i = 0; i < listaNodos.size(); i++) {
			TreeNode<AsuntoSeguimiento> nodo = listaNodos.get(i);
			AsuntoSeguimiento asuntoSeguimiento = nodo.getObject();
			Paragraph parrafo = getParrafo(asuntoSeguimiento, fontSeguimiento, conRespuesta);
			parrafo.setIndentationLeft(identacion);
			document.add(parrafo);
			if (nodo.getChildren() != null)
				addParrafosNodos(nodo.getChildren(), document, fontSeguimiento, identacion + 16, conRespuesta);
		}
	}

	/**
	 * 
	 * @param nodeTop
	 * @param origen
	 * @param rest
	 */
	@SuppressWarnings("unchecked")
	private void getChild(TreeNode<AsuntoSeguimiento> nodeTop, boolean origen, List<AsuntoSeguimiento> rest) {

		List<AsuntoSeguimiento> desendientes = null;
		List<AsuntoSeguimiento> hijos = new ArrayList<>();

		Integer nodeTop_idAsunto = nodeTop.getObject().getIdAsunto();

		if (origen) {
			// Buscamos todos los desendientes del asunto origen
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("idAsuntoOrigen", nodeTop_idAsunto));
			restrictions.add(Restrictions.in("tipoAsunto", //
					new Object[] { TipoAsunto.COPIA, //
							TipoAsunto.ENVIO, //
							TipoAsunto.TURNO }));

			if (!"TRUE".equalsIgnoreCase(environment.getProperty("seguimientoMostrarConfidenciales")))
				restrictions.add(Restrictions.eqOrIsNull("confidencial", false));

			desendientes = (List<AsuntoSeguimiento>) mngrAsuntoSeguimiento.search(restrictions);

		} else {
			// Asignamos los desendientes que faltan por asignar
			desendientes = (List<AsuntoSeguimiento>) rest;
		}

		// Creamos la lista de hijos del asunto actual segun los desendientes que quedan
		for (int i = 0; i < desendientes.size(); i++) {
			if (nodeTop_idAsunto.equals(desendientes.get(i).getIdAsuntoPadre())) {
				hijos.add(desendientes.get(i));
				desendientes.remove(i);
				i--;
			}
		}

		// Agregamos los hijos
		for (int i = 0; i < hijos.size(); i++) {
			TreeNode<AsuntoSeguimiento> node = new TreeNode<AsuntoSeguimiento>(hijos.get(i));
			nodeTop.add(node);
			getChild(node, false, desendientes);
		}
	}
	
	/**
	 * Metodo que obtiene los hijos de un asunto.
	 * 
	 * @param asuntoPadre
	 * @param descendientes
	 * @param level
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<AsuntoSeguimiento> getHijos(AsuntoSeguimiento asuntoPadre, List<AsuntoSeguimiento> descendientes, int level) {

		// Buscamos todos los descendientes hijos
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("idAsuntoPadre", asuntoPadre.getIdAsunto()));
		restrictions.add(
				Restrictions.in("tipoAsunto", new Object[] { TipoAsunto.COPIA, TipoAsunto.ENVIO, TipoAsunto.TURNO }));
		if (!"TRUE".equalsIgnoreCase(environment.getProperty("seguimientoMostrarConfidenciales")))
			restrictions.add(Restrictions.eqOrIsNull("confidencial", false));

		List<AsuntoSeguimiento> lst = (List<AsuntoSeguimiento>) mngrAsuntoSeguimiento.search(restrictions);
		level++;
		
		for (AsuntoSeguimiento asunto : lst) {
			asunto.setLevel(String.valueOf(level));
			descendientes.add(asunto);
			getNietos(asunto, descendientes, level);
		}
		
		return descendientes;
	}
	
	/**
	 * Metodo que obtiene los hijos de los hijos de un asunto.
	 * 
	 * @param asuntoPadre
	 * @param descendientes
	 * @param level
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<AsuntoSeguimiento> getNietos(AsuntoSeguimiento asuntoPadre, List<AsuntoSeguimiento> descendientes, int level) {

		// Buscamos todos los descendientes nietos
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("idAsuntoPadre", asuntoPadre.getIdAsunto()));
		restrictions.add(
				Restrictions.in("tipoAsunto", new Object[] { TipoAsunto.COPIA, TipoAsunto.ENVIO, TipoAsunto.TURNO }));
		if (!"TRUE".equalsIgnoreCase(environment.getProperty("seguimientoMostrarConfidenciales")))
			restrictions.add(Restrictions.eqOrIsNull("confidencial", false));

		List<AsuntoSeguimiento> lst = (List<AsuntoSeguimiento>) mngrAsuntoSeguimiento.search(restrictions);
		level++;
		
		for (AsuntoSeguimiento asunto : lst) {
			asunto.setLevel(String.valueOf(level));
			descendientes.add(asunto);
			getNietos(asunto, descendientes, level);
		}
		return descendientes;
		
	}
	

	/**
	 * Sets the expediente.
	 *
	 * @param idAsunto     the id asunto
	 * @param idExpediente the id expediente
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene expediente ", notes = "Obtiene detalle de un asunto guardado")
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

	@RequestMapping(value = "/asunto/expediente", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> setExpediente(
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto,
			@RequestParam(value = "idExpediente", required = true) Serializable idExpediente) throws Exception {

		try {
			IEndpoint endpoint = EndpointDispatcher.getInstance();

			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			Asunto asunto = mngrAsunto.fetch(Integer.valueOf(idAsunto.toString()));

			TipoExpediente expediente = mngrTipoExpediente.fetch(idExpediente.toString());

			if (expediente == null || expediente.getContentId() == null || expediente.getContentId().trim().equals(""))
				throw new BadRequestException("Expediente incompleto o incorrecto.");

			log.info("Moviendo el asunto :: " + asunto.getIdAsunto() + " al expediente " + expediente.getDescripcion()
					+ " en el folder :: " + expediente.getContentId());

			// SI ES ASUNTO DE TIPO COPIA SOLO GUARDA EN BD EL EXPEDIENTE PORQUE
			// AL
			// REGISTRAR LAS COPIAS NO SE LE GENERA FOLDER (HAY QUE REPLICARLO
			// DE V4)
			if (TipoAsunto.COPIA.equals(asunto.getTipoAsunto())) {

				if (null == asunto.getTipoExpediente() || !expediente.getIdExpediente()
						.equalsIgnoreCase(asunto.getTipoExpediente().getIdExpediente())) {
					asunto.setTipoExpediente(expediente);
					mngrAsunto.update(asunto);
				}

			} else if (!expediente.getIdExpediente().equalsIgnoreCase(asunto.getTipoExpediente().getIdExpediente())) {

				boolean result = false;
				result = endpoint.moveObject(asunto.getContentId(), expediente.getContentId());

				if (result) {
					asunto.setTipoExpediente(expediente);
					mngrAsunto.update(asunto);

				} else {
					log.error("Error al mover el asunto :: " + asunto.getIdAsunto() + " al expediente "
							+ expediente.getDescripcion() + " en el folder :: " + expediente.getContentId());

					throw new Exception("Error al intentar cambiar el expediente");
				}

			} else {

				log.warn("El asunto " + asunto.getIdAsunto() + " ya esta bajo el expediente "
						+ expediente.getIdExpediente());

			}

			if (areaId.equals(asunto.getArea().getIdArea())
					|| (asunto.getAreaDestino() != null && areaId.equals(asunto.getAreaDestino().getIdArea()))) {
				log.debug(" Item Out >> " + asunto);
				asunto.setAsuntoPadre(mngrAsunto.fetch(asunto.getIdAsuntoPadre()));
				return new ResponseEntity<Asunto>(asunto, HttpStatus.OK);
			}

			return new ResponseEntity<Asunto>(asunto, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Registra un Asunto recibido como aceptado por el area.
	 *
	 * @param id Identificador del Asunto que se esta aceptando
	 * @return Asunto Asunto Aceptado
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Aceptar Asunto", notes = "Registra un asunto recibido como aceptado por el area")
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

	@RequestMapping(value = "/asunto/registrar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> registrar(@RequestParam(value = "id", required = true) Serializable id)
			throws Exception {
		Asunto item = null;
		Integer idAreaUsuario = null;
		IEndpoint endpoint = EndpointDispatcher.getInstance();
		try {

			log.info("Aceptando el Asunto con id " + id);

			item = mngrAsunto.fetch(Integer.valueOf((String) id));
			idAreaUsuario = Integer.valueOf(getHeader(HeaderValueNames.HEADER_AREA_ID));

			if (item != null) {

				TipoAsunto tipoAsunto = item.getTipoAsunto();

				log.debug("tipoAsunto :: " + tipoAsunto);

				if (tipoAsunto == null) {
					throw new BadRequestException("TipoAsunto is null.");
				}

				// Validamos que el area donde esta logeado el usuario
				if (item.getAreaDestino() != null && item.getAreaDestino().getIdArea().equals(idAreaUsuario)) {

					String tipoAsuntoValue = tipoAsunto.getValue();

					boolean isCopia = tipoAsuntoValue.equals(TipoAsunto.COPIA.getValue());
					boolean isTurno = tipoAsuntoValue.equals(TipoAsunto.TURNO.getValue());
					boolean isEnvio = tipoAsuntoValue.equals(TipoAsunto.ENVIO.getValue());

					log.debug("isCopia :: " + isCopia);
					log.debug("isTurno :: " + isTurno);
					log.debug("isEnvio :: " + isEnvio);

					if ((isTurno || isCopia || isEnvio)) {

						log.info(" item.getStatusTurno() ::: " + item.getStatusTurno());

						if (item.getStatusTurno() == null || item.getStatusTurno().getIdStatus() == Status.ENVIADO) {

							boolean noRequiereRespuesta = false;

							{
								List<Timestamp> timestamps = item.getTimestamps();

								if (timestamps == null)
									timestamps = new ArrayList<>();

								timestamps.removeIf(
										t -> t.getTipo().equals(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO)
								// ||
								// t.getTipo().equals(TipoTimestamp.TIMESTAMP_ENVIO)
								);

								Timestamp timeStamp = new Timestamp();
								timeStamp.setTipo(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO);

								String stampedData = getStampedData(item, timeStamp.getTipo());

								Map<String, Object> time = firmaEndPoint.getTime(stampedData,
										TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO.getTipoString());

								String timestamp = (String) time.get("Tiempo");

								timeStamp.setTimestamp(timestamp);
								timestamps.add(timeStamp);

								item.setTimestamps(timestamps);
								item.setFechaAcuse(SignatureUtil.timestampToDate(timestamp));
							}

							// Actualizar el Asunto en el Repositorio
							item = actualizaAsuntoEnRepo(item);

							if (item.getInstruccion() != null && !item.getInstruccion().getRequiereRespuesta())
								noRequiereRespuesta = true;

							// Actualizamos los valores de la tabla
							// CustomAsuntos
							CustomAsunto customAsunto = mngrCustomAsunto.fetch(item.getIdAsunto());

							if (null != customAsunto) {
								customAsunto.setCustom2(getHeader(HeaderValueNames.HEADER_USER_ID));
								mngrCustomAsunto.update(customAsunto);
							}

							Status estatusProceso = mngrStatus
									.fetch(isCopia || noRequiereRespuesta ? Status.CONCLUIDO : Status.PROCESO);
							item.setStatusTurno(estatusProceso);
							item.setStatusAsunto(estatusProceso);

							mngrAsunto.update(item);

							if (StringUtils.isNotBlank(item.getFolioArea()) && !TipoAsunto.COPIA.equals(tipoAsunto)) {
								addNextFolio(idAreaUsuario, Integer.parseInt(item.getFolioArea()));
							}

							return new ResponseEntity<Asunto>(item, HttpStatus.OK);

						} else {

							throw new BadRequestException(
									"El tramite se encuentra en un estado diferente a registrado. No puede ser aceptado por el area");

						}
					}
				}

				log.error("El tipo de tramite : " + tipoAsunto
						+ " no puede ser aceptado ya que no es un tramite o no pertenece al area " + idAreaUsuario
						+ " en la que esta logeado el usuario");

				throw new BadRequestException("Error al obtener el area destino.");

			} else {

				throw new BadRequestException("No existe el item a aceptar.");

			}
		} catch (Exception e) {
			renameFolderAndUnlockFolio(item, endpoint, idAreaUsuario, null);
			log.error(e.getLocalizedMessage());

			throw e;

		}
	}

	/**
	 * Rechaza un Asunto que se envio al area.
	 *
	 * @param id         Identificador del Asunto que se esta rechazando
	 * @param comentario Comentario del rechazo
	 * @return Asunto luego de la modificacion
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Rechazar asunto", notes = "Rechaza un asunto que se envio al area")
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

	@RequestMapping(value = "/asunto/rechazar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> rechazar(@RequestParam(value = "id", required = true) Serializable id,
			@RequestParam(value = "comentario", required = true) Serializable comentario) throws Exception {

		try {

			log.info("Rechazando el Asunto con id " + id);

			Asunto item = mngrAsunto.fetch(Integer.valueOf((String) id));
			String idAreaUsuario = getHeader(HeaderValueNames.HEADER_AREA_ID);

			if (item.getAreaDestino().getIdArea().equals(Integer.valueOf(idAreaUsuario))) {
				if (item.getTipoAsunto().getValue().equals(TipoAsunto.TURNO.getValue())
						|| item.getTipoAsunto().getValue().equals(TipoAsunto.ENVIO.getValue())
						|| item.getTipoAsunto().getValue().equals(TipoAsunto.COPIA.getValue())) {

					if (item.getStatusAsunto().getIdStatus() == Status.ENVIADO
							&& item.getStatusTurno().getIdStatus() == Status.ENVIADO) {

						List<Timestamp> timestamps = item.getTimestamps();

						if (timestamps == null)
							timestamps = new ArrayList<>();

						Timestamp timeStamp = new Timestamp();

						timeStamp.setTipo(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO);

						String stampedData = getStampedData(item, timeStamp.getTipo());

						Map<String, Object> time = firmaEndPoint.getTime(stampedData,
								TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO.getTipoString());

						String timestamp = (String) time.get("Tiempo");

						timeStamp.setTimestamp(timestamp);

						if (timestamps.contains(timeStamp)) {
							for (Timestamp t : timestamps) {
								if (t.getTipo() == (timeStamp.getTipo())) {
									t.setTimestamp(timestamp);
								}
							}
						} else {
							timestamps.add(timeStamp);
						}
						// Actualizamos los atributos del Tramite a rechazar
						Status estatusRechazado = mngrStatus.fetch(Status.RECHAZADO);
						item.setStatusAsunto(estatusRechazado);
						item.setStatusTurno(estatusRechazado);
						item.setComentarioRechazo(String.valueOf(comentario));
						item.setFechaAcuse(SignatureUtil.timestampToDate(timestamp));
						item.setTimestamps(timestamps);

						mngrAsunto.update(item);

						return new ResponseEntity<Asunto>(item, HttpStatus.OK);

					} else {
						log.error(
								"El tramite se encuentra en un estado diferente a registrado. No puede ser rechazado.");
						return new ResponseEntity<Asunto>(item, HttpStatus.BAD_REQUEST);
					}
				}
			}

			log.error("El tipo de tramite : " + item.getTipoAsunto()
					+ " no puede ser rechazado ya que no es un tramite o no pertenece al area " + idAreaUsuario
					+ " en la que esta logeado el usuario");
			return new ResponseEntity<Asunto>(item, HttpStatus.BAD_REQUEST);

		} catch (

		Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtiene customASunto.
	 *
	 * @param idAsunto Identificador del Asunto
	 * @return Registro de customAsunto asociado
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene usuario", notes = "Obtiene al usuario que acepta el tramite")
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

	@RequestMapping(value = "/asunto/custom", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<CustomAsunto> getCustom(
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto) {

		try {

			CustomAsunto custom = mngrCustomAsunto.fetch(Integer.valueOf((String) idAsunto));
			log.debug(" Item Out >> " + custom);
			return new ResponseEntity<CustomAsunto>(custom, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtiene la lista de documentos asociados al Asunto.
	 *
	 * @param idAsunto Identificador del Asunto
	 * @return Lista de documentos asociados al Asunto
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	@SuppressWarnings("unchecked")
	protected List<DocumentoAsunto> getDocumentosAsunto(Integer idAsunto) throws Exception {

		// Creamos el objeto para hacer la consulta
		DocumentoAsunto documentoAsunto = new DocumentoAsunto();
		documentoAsunto.setIdAsunto(idAsunto);

		ResponseEntity<List<?>> response = documentoAsuntoController.search(documentoAsunto);

		if (response.getStatusCode() == HttpStatus.OK) {

			// Lista de documentos del Asunto
			return (List<DocumentoAsunto>) response.getBody();
		}

		throw new Exception("No se pudo obtener la informacion de los documentos anexos");
	}

	/**
	 * Existe numdocto.
	 *
	 * @param numdocto the numdocto
	 * @return true, if successful
	 */
	private boolean existeNumdocto(String numdocto) {

		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		List<?> asuntoExistente = new ArrayList<Asunto>();
		List<Criterion> restrictions = new ArrayList<Criterion>();

		restrictions.add(Restrictions.eq("area.idArea", areaId));
		restrictions.add(Restrictions.eq("tipoAsunto", TipoAsunto.ASUNTO));
		restrictions.add(Restrictions.eq("asuntoDetalle.numDocto", numdocto));
		asuntoExistente = mngrAsunto.search(restrictions);

		return (!asuntoExistente.isEmpty() && asuntoExistente.size() > 0) ? true : false;
	}

	/**
	 * Genera los parametros para link a e-ciudadano.
	 *
	 * @param idAsunto    the id asunto
	 * @param claveAcceso the clave acceso
	 * @param email       the email
	 * @return the link params asunto viewer
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private String getLinkParamsAsuntoViewer(Integer idAsunto, String claveAcceso, String email, String rfc)
			throws NoSuchAlgorithmException {

		String param2Value = "idAsunto=" + idAsunto + ";codigo=" + claveAcceso + ";mail=" + email + ";rfc=" + rfc;

		KeyGenerator keygen = KeyGenerator.getInstance("AES");

		keygen.init(128);

		byte[] key = keygen.generateKey().getEncoded();

		String keyStr = Hex.encodeHexString(key);

		String messageEncrypted = CryptoUtil.encryptText(param2Value, key);

		return keyStr + "/" + messageEncrypted;

	}

	/**
	 * Se manda al ciudadno la notificacion/accesso para asuntoViewer.
	 *
	 * @param idTramite      the id tramite
	 * @param nombreCompleto the nombre completo
	 * @param email          the email
	 * @return true, if successful
	 */
	private boolean sendAsuntoViewerMail(Integer idTramite, String nombreCompleto, String email, String rfc) {

		try {

			String claveAcceso = ClaveGenerator.generateClave();

			log.debug("KEY GENERATED FOR " + idTramite + " :: " + claveAcceso);

			String linkParams = getLinkParamsAsuntoViewer(idTramite, claveAcceso, email.trim(),
					StringUtils.isBlank(rfc) ? "" : rfc.trim());

			String link = environment.getProperty("e-ciudadano.url") + linkParams;

			Usuario destinatario = new Usuario();
			destinatario.setIdUsuario("IdPiloto");
			destinatario.setNombreCompleto(nombreCompleto);
			destinatario.setEmail(email.trim());

			// log.info("LINK GENERADO " + link);

			// send mail.
			mailController.sendNotificacionCorreoExterno(destinatario, claveAcceso, link);

			return true;

		} catch (Exception e) {
			log.error(e.getMessage());

			return false;
		}
	}

	/**
	 * Recuperar Trmite.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Recuperar tramite", notes = "Recupera un tramite enviado")
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

	@RequestMapping(value = "/tramite/recuperar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> recuperarTramite(
			@RequestParam(value = "id", required = true) Integer id) throws Exception {

		try {

			Asunto tramite = mngrAsunto.fetch(id);

			if (tramite != null) {

				Integer idAsuntoPadre = tramite.getIdAsuntoPadre();

				if (tramite.getStatusAsunto().getIdStatus().equals(Status.ENVIADO)
						&& tramite.getStatusTurno().getIdStatus().equals(Status.ENVIADO)) {

					tramite.setStatusTurno(mngrStatus.fetch(Status.POR_ENVIAR));
					tramite.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));
					tramite.getTimestamps()
							.removeIf(t -> TipoTimestamp.TIMESTAMP_ENVIO.getTipo() == t.getTipo().getTipo());

					// Le quitamos el permiso de lectura al area a la que se
					// se le envió el Tramite por cada documento que tenga el
					// Asunto Padre

					List<DocumentoAsunto> documentosAsunto = getDocumentosAsunto(idAsuntoPadre);
					Map<String, String> additionalData = new HashMap<>();
					additionalData.put("idArea", String.valueOf(tramite.getAreaDestino().getIdArea()));

					String aclName = "aclNameAdjuntoTramite";

					// Para el caso de los asuntos confidenciales, se le
					// asigna el ACL de Tramites confidenciales
					if (tramite.getAsuntoDetalle().getConfidencial()) {
						aclName = "aclNameAdjuntoTramiteConfidencial";
					}

					ResponseEntity<List<Map<String, String>>> documentosCompartidos = documentoCompartidoController
							.getDocumentosCompartidos(tramite.getIdAsunto());

					revocarPermisosDocumentos(documentosAsunto, documentosCompartidos.getBody(), aclName,
							additionalData);

				} else {

					throw new ConstraintViolationException(errorMessages.getString("asuntoYaAceptado"), null);
					// return new ResponseEntity<Asunto>(new Asunto(), HttpStatus.BAD_REQUEST);

				}

				mngrCustomAsunto.delete(mngrCustomAsunto.fetch(tramite.getIdAsunto()));
				mngrAsunto.update(tramite);

				updateAsuntoPadre(idAsuntoPadre);

				return new ResponseEntity<Asunto>(tramite, HttpStatus.OK);

			} else {

				return new ResponseEntity<Asunto>(new Asunto(), HttpStatus.BAD_REQUEST);

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * 
	 * @param idAsuntoPadre
	 */
	private void updateAsuntoPadreStatus_(Integer idAsuntoPadre) {

		Status statusNew = mngrStatus.fetch(Status.POR_ENVIAR);

		List<Criterion> rest = new ArrayList<Criterion>();

		rest.add(Restrictions.eq("idAsuntoPadre", idAsuntoPadre));
		rest.add(Restrictions.not(Restrictions.eq("idAsunto", idAsuntoPadre)));

		List<Integer> status = new ArrayList<>();
		status.add(Status.ENVIADO);
		status.add(Status.PROCESO);

		rest.add(Restrictions.in("statusAsunto.idStatus", status));

		List<?> lst = mngrAsunto.search(rest);

		if (lst.size() == 0) {

			Asunto asuntoPadre = mngrAsunto.fetch(idAsuntoPadre);

			Status statusOld = asuntoPadre.getStatusAsunto();

			if (statusOld.getIdStatus() != statusNew.getIdStatus()) {
				asuntoPadre.setStatusAsunto(statusNew);
				mngrAsunto.update(asuntoPadre);
			}

		}

	}

	/**
	 * Agregar permiso documento.
	 *
	 * @param objectId       the object id
	 * @param aclName        the acl name
	 * @param additionalData the additional data
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	protected synchronized boolean agregarPermisoDocumento(String objectId, String aclName,
			Map<String, String> additionalData) throws Exception {
		IEndpoint endpoint = EndpointDispatcher.getInstance();
		boolean result = false;
		try {

			if (endpoint.addPermisos(objectId, aclName, additionalData)) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			return false;
		}
		return result;
	}

	/**
	 * Revocar permisos documentos.
	 *
	 * @param documentosAsunto      the documentos asunto
	 * @param documentosCompartidos the documentos compartidos
	 * @param aclName               the acl name
	 * @param additionalData        the additional data
	 */
	private void revocarPermisosDocumentos(List<DocumentoAsunto> documentosAsunto,
			List<Map<String, String>> documentosCompartidos, String aclName, Map<String, String> additionalData) {
		IEndpoint endpoint = EndpointDispatcher.getInstance();

		log.debug(">>>::: iniciando revocar documentos");
		if (documentosAsunto != null) {
			for (DocumentoAsunto documentoAsunto : documentosAsunto) {

				try {
					endpoint.revocarAces(documentoAsunto.getObjectId(), environment.getProperty(aclName),
							additionalData);
				} catch (Exception e) {

				}
			}
		}

		if (documentosCompartidos != null) {
			for (Map<String, String> docCompartido : documentosCompartidos) {
				try {
					endpoint.revocarAces(docCompartido.get("r_object_id"), environment.getProperty(aclName),
							additionalData);
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * Valida si el nodocto dado en para un asunto ya fue usado en una respuesta.
	 *
	 * @param id the id
	 * @return the response entity
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene numero documento", notes = "Valida si el numero del documento dado para un asunto ya fue usado en una respuesta")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/asunto/noDoctoExiste", method = RequestMethod.GET)
	public ResponseEntity<Boolean> validarNoDoctoExiste(@RequestParam(value = "noDocto", required = true) String id) {

		HashMap<String, Object> params = new HashMap<>();

		params.put("folioRespuesta", id);

		Integer hasRefrences = Integer.valueOf(mngrAsunto.uniqueResult("noDoctoExisteRespuestas", params).toString());

		return new ResponseEntity<Boolean>(hasRefrences > 0, HttpStatus.OK);

	}

	/**
	 * Validar folder en repo.
	 *
	 * @param contenIdExpdiente the conten id expdiente
	 * @param folioArea         the folio area
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	private void validarFolderEnRepo(String contenIdExpdiente, String folioArea) throws Exception {
		IEndpoint endpoint = EndpointDispatcher.getInstance();

		String rutaExpediente = endpoint.getObjectPath(contenIdExpdiente);
		// valida que exista el folder en el repo
		boolean existeFolderAsunto = endpoint.existeCarpetaDctm(rutaExpediente + "/" + folioArea);
		if (existeFolderAsunto) {
			// Obtiene el objectId del folder
			String objectIdFolderAsunto = endpoint.getFolderIdByPath(rutaExpediente + "/" + folioArea);

			// Valida que el folder no este asociado a un asunto
			String sqlListContentAsunto = "select a.idAsunto from asuntos a where a.contentId=:contentId";
			HashMap<String, Object> params = new HashMap<>();
			params.put("contentId", objectIdFolderAsunto);
			List<String> asuntosId = (List<String>) mngrAsunto.execNativeQuery(sqlListContentAsunto, params);

			// Si el folder no esta asociado a un asunto procede a eliminarlo
			if (null == asuntosId || asuntosId.isEmpty()) {

				// Valida que el folder este vacio para eliminarlo
				List<Map<String, Object>> subdocumentos = endpoint.getSubDocumentos(objectIdFolderAsunto);

				if (null == subdocumentos || subdocumentos.isEmpty()) {
					// Eliminar El folder
					boolean addPermisos = endpoint.addPermisos(objectIdFolderAsunto,
							environment.getProperty("aclRollbackFolderAsunto"), null);
					if (addPermisos) {

						// Se elimina para ser resplazado por renombrarFolder
						// boolean resultEliminarFolder =
						// endpoint.eliminarFolder(objectIdFolderAsunto);

						StringBuilder nuevoNombreFolder = new StringBuilder();
						nuevoNombreFolder.append(folioArea).append("_BACKUP_ERROR_")
								.append(dateFormat.format(new Date()));

						endpoint.renameFolder(objectIdFolderAsunto, nuevoNombreFolder.toString());
						boolean resultEliminarFolder = true;

						if (resultEliminarFolder) {
							log.error(
									"INFO>>>SE ELIMINO UN FOLDER HUERFANO PARA  VOLVERLO A CREAR Y USARLO EN UN ASUNTO");
						} else {
							log.error("INFO>>> NO SE PUDO ELIMINAR FOLDER HUERFANO PARA EL FOLIO ACTUAL");
						}
					}
				}
			}
			log.error("INFO>>> Un folder Huerfano no se puede eliminar porque está asociado a un asunto");
		}
	}

	/**
	 * Actualiza asunto en repo.
	 *
	 * @param item the item
	 * @return the asunto
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	protected Asunto actualizaAsuntoEnRepo(Asunto item) throws Exception {

		Integer idAreaUsuario = Integer.valueOf(getHeader(HeaderValueNames.HEADER_AREA_ID));
		IEndpoint endpoint = EndpointDispatcher.getInstance();
		String folioArea = null;
		if (!TipoAsunto.COPIA.equals(item.getTipoAsunto())) {
			// Generamos el Folio del area que esta
			// aceptando el Asunto recibido
			folioArea = getFolioArea(idAreaUsuario);
			item.setFolioArea(folioArea);
		} else {
			// Se genera el Folio con el prefijo 'C' mas el idAsunto para las
			// copias
			folioArea = TipoAsunto.COPIA.getValue() + item.getIdAsunto().toString();
			item.setFolioArea(folioArea);
		}

		// Asignamos el Tipo de Expediente por defecto
		// del area
		item.setTipoExpediente(getTipoExpedienteDefault(item.getAreaDestino().getIdArea()));

		// VALIDA QUE NO EXISTA EL FOLDER EN EL REPO Y
		// SI EXISTE LO ELIMINA
		validarFolderEnRepo(item.getTipoExpediente().getContentId(), folioArea);

		// Generamos la carpeta del Asunto en el
		// repositorio en el Area
		String folderIdAsunto = "";
		try {
			folderIdAsunto = endpoint.createFolderIntoId(//
					item.getTipoExpediente().getContentId(), //
					environment.getProperty("folderTypeAsunto"), //
					folioArea);
		} catch (Exception e) {

		    try {
		        boolean addPermisos = endpoint.addPermisos(folderIdAsunto,
	                    environment.getProperty("aclRollbackFolderAsunto"), null);

	            if (addPermisos) {

	                // Se elimina para sustituir por renombrar el folder y asi no
	                // elimine loa
	                // documentos origen linkeados
	                // boolean eliminarFolder =
	                // endpoint.eliminarFolder(folderIdAsunto);

	                StringBuilder nuevoNombreFolder = new StringBuilder();
	                nuevoNombreFolder.append(item.getFolioArea()).append("_BACKUP_ERROR_")
	                        .append(dateFormat.format(new Date()));

	                endpoint.renameFolder(folderIdAsunto, nuevoNombreFolder.toString());
	                boolean eliminarFolder = true;

	                if (eliminarFolder) {
	                    log.error("NO SE PUDO ELIMINAR EL FOLDER DEL ASUNTO EN EL ROLLBACK DE REGISTRAR TRAMITE:: "
	                            + folderIdAsunto);
	                } else {
	                    log.error("INFO>> SE ELIMINÓ EL FOLDER DEL ASUNTO EN EL ROLLBACK DE REGISTRAR TRAMITE:: "
	                            + folderIdAsunto);
	                }
	            } else {
	                log.error("NO SE PUDO AGREGAR PERMISOS AL FOLDER PARA PODERLO ELIMINAR...");
	            }
		    } catch (Exception ex) {
		        log.error("ERROR:: NO SE PUDO AGREGAR PERMISOS AL FOLDER PARA PODERLO ELIMINAR...");
            }
		}
		
		if (folderIdAsunto.equals("")) {
		    try {
		        folderIdAsunto = endpoint.createFolderIntoId(//
	                    item.getTipoExpediente().getContentId(), //
	                    environment.getProperty("folderTypeAsunto"), //
	                    folioArea);
            } catch (Exception e) {
                if(TipoAsunto.COPIA.equals(item.getTipoAsunto())){
                    /**
                     * Si vuelve a fallar es porque ya existe el folder pero hubo una interferencia al momento de aceptar.
                     * Entonces se consulta el folderIdAsunto */
                    String rutaExpediente = endpoint.getObjectPath(item.getTipoExpediente().getContentId());
                    folderIdAsunto = endpoint.getFolderIdByPath(rutaExpediente + "/" + folioArea);
                } else {
                    throw new BadRequestException(
                            "No se puede obtener el folderIdAsunto, buscar el valor solo aplica para las copias.");
                }
            }
		}

		item.setContentId(folderIdAsunto);

		log.debug("Copiando los documentos del asunto padre al tramite");
		// List<DocumentoAsunto> documentos =
		// getDocumentosAsunto(item.getIdAsunto());

		// Query Consulta ObjectIds de Documentos del
		// Asunto
		String sqlListIdDocumentByIdAsunto = "select da.r_object_id from {SIGAP_SCHEMA}.documentosAsuntos da where da.idAsunto=:idAsunto";
		HashMap<String, Object> params = new HashMap<>();
		params.put("idAsunto", item.getIdAsunto());
		List<String> documentos = (List<String>) mngrDocsAsunto.execNativeQuery(sqlListIdDocumentByIdAsunto, params);
		for (String documento : documentos) {
			log.debug("Haciendo el link del documento " + documento);
			try {

				for (int i = 0; i <= 3; i++) {
					boolean resultSetLink = endpoint.link(documento, item.getContentId());
					if (resultSetLink) {
						break;
					} else if (resultSetLink == Boolean.FALSE && i == 3) {
						throw new Exception();
					}
				}

			} catch (Exception e) {
				log.error("Error al momento de hacer el link del documento " + documento + " al folder del asunto "
						+ item.getIdAsunto());

			}
		}

		log.debug("Fin del proceso de actualizacion del asunto en el repositorio");
		return item;
	}

	/**
	 * Save tramites Interoperabilidad list.
	 *
	 * @param tramites the tramites
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Guardar tramite interoperabilidad", notes = "Guarda los tramites de interoperabilidad")
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

	@RequestMapping(value = "/asunto/enviarInteroperar/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> interoperarList(
			@RequestBody(required = true) List<Asunto> tramites) throws Exception {

		try {
			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();

			if (!tramites.isEmpty()) {

				for (Asunto tramite : tramites) {
					try {

						ResponseEntity<Asunto> rr = enviarInreporar(tramite.getIdAsunto().toString());

						success.add(rr.getBody());

					} catch (BadRequestException e) {
						listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()), e.getMessage());
					} catch (Exception e) {
						listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()),
								HttpStatus.INTERNAL_SERVER_ERROR);
					}

				}

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
	@RequestMapping(value = "/asunto/enviarInteroperar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Asunto> enviarInreporar(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		try {

			log.info("ENVIANDO EL TRAMITE ID :: " + id);

			Integer idAsunto = Integer.valueOf((String) id);

			Asunto item = mngrAsunto.fetch(idAsunto);

			if ((null != item && item.getTipoAsunto().getValue().equals(TipoAsunto.TURNO.getValue())
					|| item.getTipoAsunto().getValue().equals(TipoAsunto.ENVIO.getValue())
					|| item.getTipoAsunto().getValue().equals(TipoAsunto.COPIA.getValue()))) {

				if (item.getStatusTurno().getIdStatus() == Status.POR_ENVIAR
						|| item.getStatusTurno().getIdStatus() == Status.RECHAZADO) {

					if (SubTipoAsunto.F.getValue().equals(item.getIdSubTipoAsunto())) {

						// Valida que se pueda interoperar
						if (Boolean.TRUE.equals(item.getArea().getInteropera())
								&& Boolean.TRUE.equals(item.getAreaDestino().getInteropera())
								&& StringUtils.isNotBlank(item.getAreaDestino().getIdExterno())) {

							// Si se cumplen las condiciones para interoperar se
							// solicitan los
							// certificado del usuario
							return new ResponseEntity<Asunto>(item, HttpStatus.OK);

						} else {
							String nombreDestinatario = mngrRepresentante.fetch(item.getDestinatario())
									.getNombreCompleto();
							throw new BadRequestException(
									"No se puede enviar el trámite de manera electrónica para el destinatario "
											+ nombreDestinatario);
						}
					}

					return new ResponseEntity<Asunto>(item, HttpStatus.OK);

				} else {

					throw new Exception(
							"El tramite se encuentra en un estado diferente a registrado. No puede ser enviado.");
				}
			} else {

				throw new Exception("El tipo de tramite : " + item.getTipoAsunto() + " no puede ser enviado.");
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Checks if is valid email address.
	 *
	 * @param email the email
	 * @return true, if is valid email address
	 */
	public static boolean isValidEmailAddress(String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	/**
	 * Save tramites list.
	 *
	 * @param tramites the tramites
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear Tramite", notes = "Crea un tramite y lo guarda")
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

	@RequestMapping(value = "/tramite/list", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> tramiteList(
			@RequestBody(required = true) TramiteAuxList tramiteAndDocList) throws Exception {

		try {

			Map<String, Object> listResult = new HashMap<>();
			Map<String, Object> listResultFail = new HashMap<>();

			List<Object> success = new ArrayList<>();
			IEndpoint endpoint = EndpointDispatcher.getInstance();

			if (tramiteAndDocList.getListTramite() != null && !tramiteAndDocList.getListTramite().isEmpty()) {

				boolean addDocCopias = tramiteAndDocList.getAddDocTramitesCopias();
				// addDoc;
				
				Map<String, String> lstDocs = new HashMap<>();
				
				// Valida los documentos para el tramite.
				if (tramiteAndDocList.getListDocTramite() != null
						&& !tramiteAndDocList.getListDocTramite().isEmpty()) {
					tramiteAndDocList.getListDocTramite().parallelStream().forEach(doc -> {
						if (doc.getObjectId() != null) {
							try {
								lstDocs.put(doc.getObjectId().toLowerCase(),
										endpoint.getLastVersionSeriesId(
												doc.getObjectId().toLowerCase()).toLowerCase());
							} catch (Exception e) {
								log.error("Error al validar el documento con id:: " + doc.getObjectId().toLowerCase());
							}
						}
					});
				}

				Map<String, Object> paramsTramite = new HashMap<>();
				Status status = mngrStatus.fetch(Status.PROCESO);
				
				if(paramsTramite.isEmpty()) {
					paramsTramite.put("representante", mngrRepresentante.fetch(tramiteAndDocList.getListTramite().get(0).getTurnador().getId()));
					paramsTramite.put("area", mngrArea.fetch(tramiteAndDocList.getListTramite().get(0).getArea().getIdArea()));
					
					if(tramiteAndDocList.getListTramite().get(0).getIdAsuntoPadre() != null)
						paramsTramite.put("asuntoPadre", mngrAsunto.fetch(tramiteAndDocList.getListTramite().get(0).getIdAsuntoPadre()));
					
					if(tramiteAndDocList.getListTramite().get(0).getInstruccion() != null 
							&& tramiteAndDocList.getListTramite().get(0).getInstruccion().getIdInstruccion() != null)
						paramsTramite.put("instruccion", mngrTipoInstruccion.fetch(tramiteAndDocList.getListTramite().get(0).getInstruccion().getIdInstruccion()));

					if(tramiteAndDocList.getListTramite().get(0).getPrioridad() != null 
							&& tramiteAndDocList.getListTramite().get(0).getPrioridad().getIdPrioridad() != null)
						paramsTramite.put("prioridad", mngrTipoPrioridad.fetch(tramiteAndDocList.getListTramite().get(0).getPrioridad().getIdPrioridad()));

					paramsTramite.put("statusAsunto", status);
					paramsTramite.put("userId", getHeader(HeaderValueNames.HEADER_USER_ID));
					paramsTramite.put("areaId", Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID)));
				}
				
				tramiteAndDocList.getListTramite().forEach(tramite -> {
					try {
						boolean addDoc = true;

						// Guarda el tramite
						ResponseEntity<Asunto> rr = save(tramite, paramsTramite);
						((Asunto)paramsTramite.get("asuntoPadre")).setStatusAsunto(status);
						success.add(rr.getBody());

						// Valida que se le debe asignar o no documentos a las
						// copias
						if (tramite.getTipoAsunto().equals(TipoAsunto.COPIA) && !addDocCopias) {
							addDoc = false;
						}

						// Guarda los documentos para el tramite.
						if (addDoc) {
							try {
								tramiteAndDocList.getListDocTramite().parallelStream().forEach(doc -> {
									if (doc.getObjectId() != null) {
										try {
											if(!(doc.getObjectId().toLowerCase())
													.equals(lstDocs.get(doc.getObjectId().toLowerCase()))) {
												doc.setObjectId(lstDocs.get(doc.getObjectId().toLowerCase()));
												doc.setStatus(null);
											}
											doc.setIdAsunto(rr.getBody().getIdAsunto());
											mngrDocsAsunto.save(doc);
										} catch (Exception e) {
											try {
												delete2(tramite.getIdAsunto().toString());
												success.remove(rr.getBody());
												listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()),
													HttpStatus.BAD_REQUEST);
												throw new RuntimeException(String.format("Error al guardar el documento %s en el tramite %d",
																			doc.getObjectId(), tramite.getIdAsunto()));
											} catch (Exception e1) {
												throw new RuntimeException(e1.getMessage());
											}
										}
									}
								});
							} catch (Exception e1) {
								log.error(e1.getMessage());
							}
						}
					} catch (BadRequestException e) {
						listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()), HttpStatus.BAD_REQUEST);
					} catch (Exception e) {
						listResultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()),
								HttpStatus.INTERNAL_SERVER_ERROR);
					}
				});

				if (tramiteAndDocList.getListTramite() != null && !tramiteAndDocList.getListTramite().isEmpty()) {
					success.add(mngrAsunto.fetch(tramiteAndDocList.getListTramite().get(0).getIdAsuntoPadre()));
				}

				listResult.put("success", success);
				listResult.put("error", listResultFail);

				return new ResponseEntity<Map<String, Object>>(listResult, HttpStatus.OK);

			} else {
				throw new BadRequestException();
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @param asunto
	 * @param endpoint
	 * @param idAreaUsuario
	 * @param claveAutoParaDesbloquear
	 * @throws Exception
	 */
	protected synchronized void renameFolderAndUnlockFolio(Asunto asunto, IEndpoint endpoint, Integer idAreaUsuario,
			String claveAutoParaDesbloquear) throws Exception {
		if (StringUtils.isNotBlank(asunto.getContentId())) {
			String folderIdAsunto = asunto.getContentId();
			boolean addPermisos = endpoint.addPermisos(folderIdAsunto,
					environment.getProperty("aclRollbackFolderAsunto"), null);
			if (addPermisos) {
				StringBuilder nuevoNombreFolder = new StringBuilder();
				nuevoNombreFolder.append(asunto.getFolioArea()).append("_BACKUP_ERROR_")
						.append(dateFormat.format(new Date()));

				endpoint.renameFolder(folderIdAsunto, nuevoNombreFolder.toString());
			} else {
				log.error("NO SE PUDO AGREGAR PERMISOS AL FOLDER PARA PODERLO ELIMINAR...");
			}
		}
		if (null != asunto && !asunto.getTipoAsunto().equals(TipoAsunto.COPIA) && StringUtils.isNotBlank(asunto.getFolioArea())) {
			unlockFolio(idAreaUsuario, Long.valueOf(asunto.getFolioArea()));
		}
		if (StringUtils.isNotBlank(claveAutoParaDesbloquear)) {
			unlockFolio(idAreaUsuario, Long.valueOf(claveAutoParaDesbloquear));
		}

		if (asunto != null && asunto.getIdAsunto() != null &&
		        (null != asunto.getStatusAsunto() && asunto.getStatusAsunto().getIdStatus().equals(Status.POR_ENVIAR)))
			mngrAsunto.delete(asunto);

	}

	/**
	 * 
	 * @param idArea
	 * @param folio
	 */
	public void unlockFolio(Integer idArea, Long folio) {
		try {

			String queryName;
			if (DBVendor.POSTGRESQL == dbVendor)
				queryName = String.format(environment.getProperty("recuperarFolio_PG"));
			else
				queryName = String.format(environment.getProperty("recuperarFolio"));

			HashMap<String, Object> params = new HashMap<>();
			params.put("idArea", idArea);
			params.put("folio", folio);
			mngrArea.execNativeQuery(queryName, params);
			log.debug("se regresó el folio del area :: " + folio + "  ::  " + idArea);
		} catch (Exception ex) {
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folio + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folio + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folio + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folio + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folio + "  ::  " + idArea);
			log.error(ex.getLocalizedMessage());

		}
	}

	/*
	 * Obtiene lista de áreas remitentes
	 *
	 * @param asunto
	 * 
	 * @return the response entity
	 * 
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta bandeja entrada", notes = "Consulta la lista de asuntos de la bandeja de entrada")
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
	@RequestMapping(value = "/asunto/areaRemitente", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> areaRemitenteList(@RequestBody(required = true) Asunto asunto) {

		List<Asunto> lst = new ArrayList<Asunto>();
		log.debug("PARAMETROS DE BUSQUEDA :: " + asunto);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			ProjectionList projections = Projections.projectionList();
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (asunto.getAreaDestino().getIdArea() != null && asunto.getStatusAsunto().getIdStatus() != null)
				projections.add(Projections
						.distinct(Projections.projectionList().add(Projections.property("area.idArea"), "idArea")
								.add(Projections.property("area.descripcion"), "descripcion")));

			if (asunto.getAreaDestino().getIdArea() != null)
				restrictions.add(Restrictions.eq("areaDestino.idArea", asunto.getAreaDestino().getIdArea()));

			if (asunto.getStatusAsunto().getIdStatus() != null)
				restrictions.add(Restrictions.eq("statusAsunto.idStatus", asunto.getStatusAsunto().getIdStatus()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			lst = (List<Asunto>) mngrAsunto.search(restrictions, orders, projections, null, null);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta asunto origen ", notes = "Consulta y obtiene el identificador del asunto origen")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 206, message = "La petición servirá parcialmente el contenido solicitado"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/asunto/asuntoOrigen", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Asunto>> getAsuntoByAsuntoOrigen(
			@RequestBody(required = true) Asunto asunto) {

		try {
			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("idAsuntoOrigen", asunto.getIdAsuntoOrigen()));
			restrictions.add(Restrictions.isNotNull("folioArea"));
			restrictions.add(Restrictions.or(Restrictions.eq("areaDestino.idArea", areaId),
					Restrictions.eq("area.idArea", areaId)));

			// se valida si no tiene el permiso de confidencial y se agrega la
			// restriccion
			if (!permisoController.verConfidencial(userId, areaId)) {
				restrictions.add(Restrictions.eq("asuntoDetalle.confidencial", false));
			}
			
			List<Asunto> lst = (List<Asunto>) mngrAsunto.search(restrictions);
			if (null != lst && !lst.isEmpty()) {
				List<Asunto> asunto_a = new ArrayList<Asunto>();
				List<Asunto> asunto_t = new ArrayList<Asunto>();

				lst.stream().filter(a -> a.getTipoAsunto().equals(TipoAsunto.ASUNTO)).forEach(ra -> {
					if (ra.getIdAsuntoPadre() != null)
						ra.setAsuntoPadre(mngrAsunto.fetch(ra.getIdAsuntoPadre()));
					asunto_a.add(ra);
				});

				if (asunto_a.size() > 0) {
					return new ResponseEntity<List<Asunto>>(asunto_a, HttpStatus.OK);
				} else {
					lst.stream().filter(a -> !a.getTipoAsunto().equals(TipoAsunto.ASUNTO)).forEach(ra -> {
						ra.setAsuntoPadre(mngrAsunto.fetch(ra.getIdAsuntoPadre()));
						asunto_t.add(ra);
					});

					if (asunto_t.size() > 0) {
						return new ResponseEntity<List<Asunto>>(asunto_t, HttpStatus.OK);
					} else {
						return new ResponseEntity<List<Asunto>>(new ArrayList<Asunto>(), HttpStatus.NOT_ACCEPTABLE);
					}
				}

			} else {
				return new ResponseEntity<List<Asunto>>(new ArrayList<Asunto>(), HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}
}
