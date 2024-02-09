/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoDetalle;
import com.ecm.sigap.data.model.Destinatario;
import com.ecm.sigap.data.model.DiaFestivo;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoMinutario;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Firmante;
import com.ecm.sigap.data.model.Minutario;
import com.ecm.sigap.data.model.Remitente;
import com.ecm.sigap.data.model.RemitenteKey;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.TipoPrioridad;
import com.ecm.sigap.data.model.TipoRespuesta;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.CopiaRespuesta;
import com.ecm.sigap.data.model.util.DestinatariosMinutario;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.model.util.RevisorMinutario;
import com.ecm.sigap.data.model.util.StatusMinutario;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoDestinatario;
import com.ecm.sigap.data.model.util.TipoRegistro;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Minutario}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class MinutarioController extends CustomRestController implements RESTController<Minutario> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(MinutarioController.class);

	/**
	 * Referencia hacia el REST controller de {@link Asunto}.
	 */
	@Autowired
	private AsuntoController asuntoController;

	/**
	 * Referencia hacia el REST controller de {@link Respuesta}.
	 */
	@Autowired
	private RespuestaController respuestaController;

	/**
	 * Referencia hacia el REST controller de {@link PlantillaController}.
	 */
	@Autowired
	private PlantillaController plantillasController;

	/**
	 * Referencia hacia el REST controller {@link RepositoryController}.
	 */
	@Autowired
	private RepositoryController repositorioController;

	/**
	 * Referencia hacia el REST controller de {@link TipoPrioridad}.
	 */
	@Autowired
	private TipoPrioridadController prioridadController;

	/**
	 * Referencia hacia el REST controller de {@link TipoInstruccion}.
	 */
	@Autowired
	private TipoInstruccionController instruccionController;

	/**
	 * Referencia hacia el REST controller de {@link FirmanteController}.
	 */
	@Autowired
	private FirmanteController firmanteController;

	/**
	 * Referencia hacia el REST controller de {@link DocumentoAsuntoController}.
	 */
	@Autowired
	private DocumentoAsuntoController documentoAsuntoController;

	/**
	 * Referencia hacia el REST controller de {@link DocumentoRespuestaController}.
	 */
	@Autowired
	private DocumentoRespuestaController documentoRespuestaController;

	/**
	 * Referencia hacia el REST controller de {@link DocumentoMinutarioController}.
	 */
	@Autowired
	private DocumentoMinutarioController documentoMinutarioController;

	/**
	 * Obtiene el {@link Area} a la que pertenece el destinatario del minutario
	 * <p>
	 * Este metodo se creo ya que se estaba usando el area 0 como el area de
	 * Ciudadanos y Empresas
	 *
	 * @param destinatario {@link DestinatariosMinutario} del minutario
	 * @return Area a la que pertenece el destinatario
	 * @throws IllegalArgumentException El tipo de destinatario que se indicado no
	 *                                  es uno valido dentro del sistema
	 */
	private Area getAreaDestinatario(DestinatariosMinutario destinatario) throws IllegalArgumentException {

		switch (destinatario.getIdTipoDestinatario()) {
		case FUNCIONARIO_INTERNO:
		case FUNCIONARIO_INTERNO_CCP:
		case FUNCIONARIO_INTERNO_TURNO:
		case FUNCIONARIO_EXTERNO:
		case FUNCIONARIO_EXTERNO_CCP:
		case FUNCIONARIO_EXTERNO_TURNO:
			return destinatario.getIdAreaDestinatario();
		case CIUDADANO:
		case CIUDADANO_CCP:
		case CIUDADANO_TURNO:
			return mngrArea.fetch(Integer.valueOf(getParamApp("IDCIUDPROMOTOR")));
		case REPRESENTANTE_LEGAL:
		case REPRESENTANTE_LEGAL_CCP:
		case REPRESENTANTE_LEGAL_TURNO:
			return mngrArea.fetch(Integer.valueOf(getParamApp("IDEMPPROMOTOR")));
		}

		throw new IllegalArgumentException("El tipo de destinatario indicado no esta permitido dentro del sistema");
	}

	/**
	 * Asigna los permisos al Documento del Minutario
	 *
	 * @param revisores      Lista de revisores o versiones del Documento
	 * @param additionalData Informacion sobre los permisos que se van a colocar
	 * @param aclName        Nombre del ACL que se va a aplicar
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-key", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", required = true, dataType = "string", paramType = "header") })
	private void setAclsDocumento(List<RevisorMinutario> revisores, Map<String, String> additionalData, String aclName)
			throws Exception {

		log.debug(">>> INICIANDO SET ACL A DOCUMENTO ");

		if (null != revisores && !revisores.isEmpty()) {

			RevisorMinutario lastRevisor = revisores.get(revisores.size() - 1);

			String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
			String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

			IEndpoint endpoint = EndpointDispatcher.getInstance(contetUser, password);

			// Para no replicar el mismo codigo en varias parte, se coloca esta
			// validadcion. Como minimo siempre se le asigna el "owner" o due#o
			// del minutario el permiso
			if (null == additionalData) {
				// Owner del minutario
				additionalData = new HashMap<>();
				String userName = EndpointDispatcher.getInstance()
						.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
				additionalData.put("idOwnerDoc", userName);
			}

			log.debug(">>> APLICANDO ACL AL }DOCUMENTO");
			try {
				endpoint.setACL(lastRevisor.getObjectId(), environment.getProperty(aclName), additionalData);
			} catch (Exception e) {
				log.error(">>> ERROR APLICANDO ACL AL DOCUMENTO");
				throw e;
			}
		}
	}

	/**
	 * Asigna un ACL especifico a los documentos anexos del Minutario
	 *
	 * @param idMinutario    Identificador del Minutario
	 * @param additionalData Informacion sobre los permisos que se van a colocar
	 * @param aclName        Nombre del ACL que se va a aplicar
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	@SuppressWarnings("unchecked")
	private void setAclsDocAnexos(Integer idMinutario, Map<String, String> additionalData, String aclName)
			throws Exception {

		String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
		String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

		IEndpoint endpoint = EndpointDispatcher.getInstance(contetUser, password);

		// Para no replicar el mismo codigo en varias parte, se coloca esta
		// validadcion. Como minimo siempre se le asigna el "owner" o due#o del
		// minutario el permiso
		if (null == additionalData) {
			// Owner del minutario
			additionalData = new HashMap<>();
			String userName = EndpointDispatcher.getInstance()
					.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
			additionalData.put("idOwnerDoc", userName);
		}

		// aplicar ACL a los documentos adjuntos
		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("idMinutario", idMinutario));

		List<DocumentoMinutario> docMinutario = (List<DocumentoMinutario>) mngrDocsMinutario.search(restrictions);

		for (DocumentoMinutario doc : docMinutario)
			endpoint.setACL(doc.getObjectId(), //
					environment.getProperty(aclName), //
					additionalData);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene detalle borrador", notes = "Obtiene detalle de un borrador")
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
	@RequestMapping(value = "/minutario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> get(@RequestParam(value = "id", required = true) Serializable id) {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			Minutario item = mngrMinutario.fetch(id);

			if (item != null) {
				// Se realiza el ordenamiento en reversa de los revisores para
				// que la posicion 0 sea el ultimo revisor
				Collections.sort(item.getRevisores(),
						Comparator.comparing(RevisorMinutario::getFechaRegistro).reversed());

				// Se valida si el usuario de sesion es el creador del minutario
				// o si es el ultimo revisor
				if ((userId.equalsIgnoreCase(item.getUsuario().getIdUsuario())) || (!item.getRevisores().isEmpty()
						&& userId.equalsIgnoreCase(item.getRevisores().get(0).getId()))) {

					// Obtenemos el nombre completo del Revisor del minutario,
					// Se obtiene para la tabla de Documentos Revisores

					for (RevisorMinutario revisor : item.getRevisores()) {

						if (null != revisor.getUsuario()) {
							revisor.setNombreUsuario(
									mngrRepresentante.fetch(revisor.getUsuario().getIdUsuario()).getNombreCompleto());
						}
					}

					log.debug(" Data Out >> " + item);
					return new ResponseEntity<Minutario>(item, HttpStatus.OK);
				} else {
					return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.FORBIDDEN);

				}
			}
			log.debug(" Data Out >> " + item);
			return new ResponseEntity<Minutario>(item, HttpStatus.OK);
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

	@ApiOperation(value = "Crear borrador", notes = "Crear un nuevo borrador en el sistema")
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
	@Override
	@RequestMapping(value = "/minutario", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Minutario> save(@RequestBody Minutario minutario) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("MINUTARIO A GUARDAR >> " + minutario);

				boolean has = hasDestinatariosMinutario(minutario.getDestinatarios());

				if (!has) {

					if (minutario.getIdMinutario() == null) {
						return new ResponseEntity<Minutario>(minutario, HttpStatus.BAD_REQUEST);
					} else {
						Minutario minutario_ = mngrMinutario.fetch(minutario.getIdMinutario());
						return new ResponseEntity<Minutario>(minutario_, HttpStatus.BAD_REQUEST);
					}
				}

				if (minutario.getDestinatarios() != null) {
					int orden = 1;

					for (DestinatariosMinutario destinatario : minutario.getDestinatarios()) {

						destinatario.setIdAreaDestinatario(getAreaDestinatario(destinatario));
						destinatario.setOrden(orden);
						orden++;
					}
				}

				if (minutario.getIdMinutario() == null) {

					// Se asigna el estatus del minutario a guardar
					minutario.setStatus(StatusMinutario.REGISTRADO);

					minutario.setRemitente(mngrArea.fetch(minutario.getRemitente().getIdArea()));

					mngrMinutario.save(minutario);

					return new ResponseEntity<Minutario>(minutario, HttpStatus.CREATED);

				} else {

					List<DestinatariosMinutario> destinatarios = minutario.getDestinatarios();

					List<DestinatariosMinutario> listWithoutDuplicates = destinatarios.stream().distinct()
							.collect(Collectors.toList());

					minutario.setDestinatarios(listWithoutDuplicates);

					mngrMinutario.update(minutario);

					if (StatusMinutario.AUTORIZADO == minutario.getStatus()
							|| StatusMinutario.CONCLUIDO == minutario.getStatus()
							|| StatusMinutario.CANCELADO == minutario.getStatus()) {

						// Se asignan los pemisos al documento del Minutario
						setAclsDocumento(minutario.getRevisores(), null, "aclNameAdjuntoMinutarioReadOnly");

						// Se asignan los pemisos de los documentos anexos
						setAclsDocAnexos(minutario.getIdMinutario(), null, "aclNameAdjuntoMinutarioReadOnly");
					}

					return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<Minutario>(minutario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Valida que se deba tener almenos un destinatario en el minutario.
	 *
	 * @param destinatarios
	 * @throws Exception
	 */
	private boolean hasDestinatariosMinutario(List<DestinatariosMinutario> destinatarios) throws Exception {

		for (DestinatariosMinutario destinatario : destinatarios) {

			if (destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_TURNO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_CCP
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_TURNO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_CCP
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_TURNO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_CCP
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_TURNO
					|| destinatario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_CCP)

				return true;
		}

		return false;
	}

	/**
	 * Hacela busqueda de un Minutario a partir de los datos enviados
	 *
	 * @param body Parametros de la busqueda y del Objeto a buscar
	 * @return Lista de Minutarios
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta borrador", notes = "Consulta la lista de borradores")
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
	
	@RequestMapping(value = "/minutario", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) RequestWrapper<Minutario> body) {

		List<?> lst = new ArrayList<Minutario>();
		Minutario minutario = body.getObject();
		Map<String, Object> params = body.getParams();

		log.debug("PARAMETROS DE BUSQUEDA :: " + body);

		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (minutario.getIdMinutario() != null)
				restrictions.add(Restrictions.idEq(minutario.getIdMinutario()));

			if (minutario.getAsunto() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("asunto", minutario.getAsunto(), MatchMode.ANYWHERE));

			if (minutario.getTituloDocumento() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("tituloDocumento", minutario.getTituloDocumento(),
						MatchMode.ANYWHERE));

			if (minutario.getFechaRegistro() != null)
				restrictions.add(Restrictions.eq("fechaRegistro", minutario.getFechaRegistro()));

			if (minutario.getInstitucion() != null && minutario.getInstitucion().getIdInstitucion() != null)
				restrictions.add(
						Restrictions.eq("institucion.idInstitucion", minutario.getInstitucion().getIdInstitucion()));

			if (minutario.getStatus() != null)
				restrictions.add(Restrictions.eq("status", minutario.getStatus()));

			if (minutario.getRemitente() != null && minutario.getRemitente().getIdArea() != null)
				restrictions.add(Restrictions.eq("remitente.idArea", minutario.getRemitente().getIdArea()));

			if (minutario.getFirmante() != null && minutario.getFirmante().getIdUsuario() != null)
				restrictions.add(Restrictions.eq("firmante.idUsuario", minutario.getFirmante().getIdUsuario()));

			if (minutario.getUsuario() != null && minutario.getUsuario() != null)
				restrictions.add(Restrictions.eq("usuario.idUsuario", minutario.getUsuario().getIdUsuario()));

			if (params != null) {
				if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
					restrictions.add(Restrictions.between("fechaRegistro", //
							new Date((Long) params.get("fechaRegistroInicial")),
							new Date((Long) params.get("fechaRegistroFinal"))));
				}
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("fechaRegistro"));

			lst = mngrMinutario.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/*
	 * Elimina el minutario
	 *
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar borrador", notes = "Elimina un borrador de la lista")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})

	@Override
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/minutario", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("MINUTARIO A ELIMINAR >> " + id);

		try {

			List<DocumentoMinutario> items = new ArrayList<DocumentoMinutario>();
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("idMinutario", Integer.valueOf((String) id)));

			items = (List<DocumentoMinutario>) mngrDocsMinutario.search(restrictions, null);
			for (DocumentoMinutario documento : items) {

				mngrDocsMinutario.delete(documento);
			}

			mngrMinutario.delete(mngrMinutario.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * @param idMinutario
	 * @param objectId
	 * @throws Exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar plantilla", notes = "Elimina una plantilla exportada")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/revisor/minutario", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<Minutario> deleteRevisor(
			@RequestParam(value = "idMinutario", required = true) Integer idMinutario,
			@RequestParam(value = "objectId", required = true) String objectId)
			throws JsonParseException, JsonMappingException, IOException, Exception {
		Minutario minutario = mngrMinutario.fetch(idMinutario);

		List<RevisorMinutario> newListRevisores = new ArrayList<RevisorMinutario>();
		List<RevisorMinutario> toDelete = new ArrayList<RevisorMinutario>();

		String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
		String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

		IEndpoint endpoint = EndpointDispatcher.getInstance(contetUser, password);

		for (RevisorMinutario revisor : minutario.getRevisores()) {

			if (revisor.getObjectId().equalsIgnoreCase(objectId)) {
				toDelete.add(revisor);
			} else {
				newListRevisores.add(revisor);
			}

		}

		if (!toDelete.isEmpty()) {

			if (toDelete.size() == 1) {
				try {
					// si es el unico con ese objectId
					// se intenta elimnar del repo.
					endpoint.eliminarDocumento(objectId);
				} catch (Exception e) {
					
				}
			} else {

				// ordena por version desc
				Collections.sort(toDelete);

				// agrega todos menos el ultimo
				for (int i = 0; i < toDelete.size() - 1; i++)
					newListRevisores.add(toDelete.get(i));

			}

			minutario.setRevisores(newListRevisores);

			mngrMinutario.update(minutario);
		}

		return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);

	}

	/**
	 * Search minutario revisor. Obtiene los minutarios en estatus PARA_REVISON
	 * asignados a un usuario revisor
	 *
	 * @param idUsuario the id usuario
	 * @return the response entity
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene status borrador", notes = "Obtiene los borradores en estado para revision")
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
	@RequestMapping(value = "/minutario/revisor", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> searchMinutarioRevisor(
			@RequestParam(value = "idUsuario", required = true) String idUsuario) {

		List<Minutario> lst = new ArrayList<Minutario>();
		List<Minutario> lstResult = new ArrayList<Minutario>();
		log.debug("PARAMETROS DE BUSQUEDA :: " + idUsuario);

		try {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("status", StatusMinutario.PARA_REVISION));
			restrictions.add(Restrictions.eq("revisor.id", idUsuario));
			// restrictions.add(Restrictions.ne("revisor.usuario.idUsuario",
			// idUsuario));
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("idMinutario"));
			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<Minutario>) mngrMinutario.search(restrictions, orders);
			// Se ordenan los revisores de cada minutario, el reversed coloca el
			// ultimo en la posicion 0

			String idRevisor;
			Minutario minutarioTmp;
			List<RevisorMinutario> listRevisores;

			for (Minutario minutario : lst) {
				// Se realiza un fetch porque el search no devuelve el objeto
				// con la lista de todos los revisores
				minutarioTmp = mngrMinutario.fetch(minutario.getIdMinutario());
				listRevisores = minutarioTmp.getRevisores();
				Collections.sort(listRevisores, Comparator.comparing(RevisorMinutario::getFechaRegistro).reversed());
				// Se agrega a la lista los minutario q su ultimo revisor
				// el Usuario enviado por parametro.
				idRevisor = listRevisores.get(0).getId();

				if (idRevisor.equalsIgnoreCase(idUsuario)) {
					lstResult.add(minutario);
				}
			}

			log.debug("Size found >> " + lstResult.size());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lstResult, HttpStatus.OK);
	}

	/**
	 * Revisar oficio.
	 *
	 * @param minutario the minutario
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Enviar a revision", notes = "Envia un borrador a revision")
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
	
	@RequestMapping(value = "/revisarOficio", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Minutario> revisarOficio(@RequestBody Minutario minutario) throws Exception {

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		log.debug("MINUTARIO A REVISAR >> " + minutario);
		try {

			if (minutario.getIdMinutario() != null) {

				Minutario minutarioTemp = mngrMinutario.fetch(minutario.getIdMinutario());
				if (!minutarioTemp.getStatus().equals(StatusMinutario.REVISADO)
						&& !minutarioTemp.getStatus().equals(StatusMinutario.REGISTRADO)) {

					throw new BadRequestException(
							"El minutario se encuentra en un status distinto a En elaboracion o Revisado.");
				}

				log.debug(">>> ACTUALIZANDO MINUTARIO");

				// Se cambio la forma de obtener la fecha de registro - ticket
				// 1432
				minutario.getRevisores().get(minutario.getRevisores().size() - 1).setFechaRegistro(new Date());
				// minutario.getRevisores().get(minutario.getRevisores().size()
				// - 1)
				// .setFechaRegistro(getCurrentTime(getStampedData(minutario,
				// TipoTimestamp.TIMESTAMP_REGISTRO)));

				// Guardamos el minutario con los cambios que tenga
				mngrMinutario.update(minutario);

				List<RevisorMinutario> revisiones = minutario.getRevisores();

				RevisorMinutario lastRevisor = revisiones.get(revisiones.size() - 1);

				List<Map<String, Object>> permisos = null;
				try {
					permisos = endpoint.getObjectAces(lastRevisor.getObjectId());
				} catch (Exception e) {
					log.error(">>> ERROR OBTNIENDO PERMISOS DE: " + lastRevisor.getObjectId() + " " + e.getMessage());
					throw e;
				}
				boolean actualiza = true;
				for (Map<String, Object> permiso : permisos) {

					if (permiso.get("principalId").equals(lastRevisor.getId())) {
						actualiza = false;
						break;
					}
				}

				if (actualiza) {

					log.debug(">>> AGREGANDO ACL A DOCUMENTOS Y ANEXOS");

					// AGREGAR ACL
					// Owner del minutario
					Map<String, String> additionalData = new HashMap<>();

					try {
						String userName = endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
						additionalData.put("idOwnerDoc", userName);
					} catch (Exception e) {
						log.error(">>> ERROR OBTENIENDO EL USERNAME DEL OWNER DEL MINUTARIO " + e.getMessage());
						throw e;
					}
					try {
						// Revisor del Minutario
						Usuario usuario = mngrUsuario.fetch(lastRevisor.getId());
						String tmp = endpoint.getUserName(usuario.getUserKey());
						additionalData.put("idRevisor", String.valueOf(tmp));
					} catch (Exception e) {
						log.error(">>> ERROR OBTENIENDO EL USERNAME DEL OWNER DEL ULTIMO REVISOR " + e.getMessage());
						throw e;
					}

					// Se asignan los pemisos al documento del Minutario
					setAclsDocumento(revisiones, additionalData, "aclNameAdjuntoMinutarioRevision");

					// Se asignan los pemisos de los documentos anexos
					setAclsDocAnexos(minutario.getIdMinutario(), additionalData, "aclNameAdjuntoMinutarioRevision");

				}

				log.debug(">>> AGREGANDO EL REVISOR");
				// Colocando el nombre al revisor recien agredado
				lastRevisor = revisiones.get(revisiones.size() - 1);
				if (null != lastRevisor.getUsuario()) {
					Usuario usuario = mngrUsuario.fetch(lastRevisor.getUsuario().getIdUsuario());
					lastRevisor.setNombreUsuario(endpoint.getUserName(usuario.getUserKey()));

					// llenando el objeto Usuario del ultimo revisor ya q se
					// pasa solo el id, y este se toma para piantar el AUTOR en
					// la tabla.
					lastRevisor.setUsuario(usuario);
				}

				return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);
			} else {

				return new ResponseEntity<Minutario>(minutario, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * @param idMinutario
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Recuperar borrador", notes = "Recupera un borrador cancelado")
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
	@RequestMapping(value = "/recuperarOficio", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> recuperarOficio(
			@RequestParam(required = true, name = "idMinutario") Integer idMinutario) throws Exception {

		try {

			Minutario oldMinutario = mngrMinutario.fetch(idMinutario);

			if (oldMinutario != null) {

				if (oldMinutario.getStatus() == StatusMinutario.CANCELADO) {

					oldMinutario.setStatus(StatusMinutario.REGISTRADO);

					oldMinutario.setIdMinutario(null);

					ResponseEntity<Minutario> response = save(oldMinutario);

					Minutario minutarioNuevo = response.getBody();

					Integer nuevoMinutarioId = minutarioNuevo.getIdMinutario();

					log.info(" El minutario " + idMinutario + " recuperado en >> " + nuevoMinutarioId);

					// obtener documentos del minutario original
					ResponseEntity<List<?>> oldDocumentosMinutariosResponse = documentoMinutarioController
							.get(idMinutario);

					List<DocumentoMinutario> oldDocumentosMinutarios = (List<DocumentoMinutario>) oldDocumentosMinutariosResponse
							.getBody();

					ResponseEntity<Map<String, Object>> responseDocumentoAdjunto;
					String contentB64;

					for (DocumentoMinutario oldDoc : oldDocumentosMinutarios) {

						try {

							// obtener el contenido ooriginal
							responseDocumentoAdjunto = repositorioController.getDocument(oldDoc.getObjectId(), null);
							contentB64 = (String) responseDocumentoAdjunto.getBody().get("contentB64");

							oldDoc.setObjectId(null);
							oldDoc.setFileB64(contentB64);
							oldDoc.setParentContentId(null);
							oldDoc.setIdMinutario(nuevoMinutarioId);
							oldDoc.setIdArea(minutarioNuevo.getRemitente().getIdArea().toString());

							// agregarlo al nuevo minutario
							documentoMinutarioController.save(oldDoc);

						} catch (Exception e) {
							log.error(e.getMessage());
							
						}
					}

					return response;

				} else {

					throw new BadRequestException("El minutario se encuentra en un status distinto a cancelado.");

				}

			} else {

				throw new BadRequestException("No existe el minutario indicado para recuperar.");

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Marcar un minutario como revisado por el usuario al que se le envio.
	 *
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Marcar borrador revisado", notes = "Marca un borrador como revisado por el usuario al que se le envio")
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
	
	@RequestMapping(value = "/minutarioMarcarRevisado", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Minutario> minutarioMarcarRevisado(@RequestBody Map<String, Object> body)
			throws Exception {

		Integer idMinutario = Integer.valueOf(body.get("idMinutario").toString());
		String comentario = body.get("comentario").toString();

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			Usuario usuario = mngrUsuario.fetch(userId);

			Minutario minutario = mngrMinutario.fetch(idMinutario);

			RevisorMinutario last_ = getLastRevision(idMinutario);

			if (minutario != null //
					&& usuario != null //
					&& last_ != null //
					&& minutario.getStatus() == StatusMinutario.PARA_REVISION //
			) {

				RevisorMinutario new_ = new RevisorMinutario();

				new_.setComentario(comentario);
				new_.setDocumentName(last_.getDocumentName());
				// new_.setDocumentProperties(documentProperties);
				new_.setFechaRegistro(getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
				new_.setId(userId);
				// new_.setNombreUsuario(nombreUsuario);
				new_.setObjectId(last_.getObjectId());
				new_.setUsuario(usuario);
				new_.setVersion(last_.getVersion());

				minutario.getRevisores().add(new_);

				minutario.setStatus(StatusMinutario.REVISADO);

				mngrMinutario.update(minutario);

				return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);

			} else {

				return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Regresa un Minutario revisado.
	 *
	 * @param minutario Minutario Revisado
	 * @return the response entity
	 * @throws Exception Error al momento de ejecutar el metodo
	 */
	@RequestMapping(value = "/minutario/revisado", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Minutario> minutarioRevisado(@RequestBody Minutario minutario)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("MINUTARIO REVISADO >> " + minutario);

				// IEndpoint endpoint = EndpointDispatcher.getInstance();

				if (minutario.getIdMinutario() != null && null != minutario.getUsuario()) {
					// Guardamos el minutario con los cambios que tenga
					mngrMinutario.update(minutario);

					// RevisorMinutario lastRevisor =
					// minutario.getRevisores().get(minutario.getRevisores().size()
					// - 1);
					//
					// String objectId = lastRevisor.getObjectId();

					// AGREGAR ACL
					// Owner del minutario
					Map<String, String> additionalData = new HashMap<>();
					String userName = EndpointDispatcher.getInstance().getUserName(minutario.getUsuario().getUserKey());
					additionalData.put("idOwnerDoc", userName);

					// Se asignan los pemisos al documento del Minutario
					setAclsDocumento(minutario.getRevisores(), additionalData, "aclNameAdjuntoAnexoMinutario");

					// Se asignan los pemisos de los documentos anexos
					setAclsDocAnexos(minutario.getIdMinutario(), additionalData, "aclNameAdjuntoAnexoMinutario");

					return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);
				} else {

					return new ResponseEntity<Minutario>(minutario, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Minutario>(minutario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Autorizar oficio.
	 *
	 * @param minutario the minutario
	 * @param request   the request
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Autorizar borrador", notes = "Autoriza un borrador")
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

	@RequestMapping(value = "/autorizarOficio", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> autorizarOficio(
			@RequestParam(value = "id", required = true) Integer idMinutario) throws Exception {

		log.debug("MINUTARIO A AUTORIZAR >> " + idMinutario);

		Minutario minutario = mngrMinutario.fetch(idMinutario);

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			StatusMinutario statusOriginal = minutario.getStatus();

			if ((minutario != null)
					&& (statusOriginal == StatusMinutario.REGISTRADO || statusOriginal == StatusMinutario.REVISADO)

					&& (userId.equalsIgnoreCase(minutario.getUsuario().getIdUsuario()))) {

				if (minutario.getRevisores().isEmpty()) {
					throw new Exception("Este borrador no tiene un documento asociado");
				}

				minutario.setStatus(StatusMinutario.AUTORIZADO);

				mngrMinutario.update(minutario);

				try {
					// Se asignan los pemisos al documento del Minutario
					setAclsDocumento(minutario.getRevisores(), null, "aclNameAdjuntoMinutarioReadOnly");

					// Se asignan los pemisos de los documentos anexos
					setAclsDocAnexos(minutario.getIdMinutario(), null, "aclNameAdjuntoMinutarioReadOnly");

					return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);

				} catch (Exception e) {
					minutario.setStatus(statusOriginal);
					mngrMinutario.update(minutario);
					throw e;
				}

			} else {
				return new ResponseEntity<Minutario>(minutario, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Generar un asunto en base al minutario indicado.
	 *
	 * @param idMinutario
	 * @param numdoctoAuto
	 * @param numdocto
	 * @param generarAlFirmante
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Generar asunto", notes = "Genera un asunto en base al borrador indicado")
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/generarAsunto", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> generarAsunto(
			@RequestParam(value = "id", required = true) Integer idMinutario,
			@RequestParam(value = "numdoctoAuto", required = true) String numdoctoAuto,
			@RequestParam(value = "idFolioMultiple", required = false) Integer idFolioMultiple,
			@RequestParam(value = "numdocto", required = false) String numdocto,
			@RequestParam(value = "firmante", required = false) String generarAlFirmante,
			@RequestParam(value = "idAreaFirmante", required = false) Integer idAreaFirmante) throws Exception {

		log.debug("MINUTARIO >> " + idMinutario + " >> PARA CONVERTIR A ASUNTO >> ");

		boolean numdoctoAutomatico = Boolean.parseBoolean(numdoctoAuto);

		if (!numdoctoAutomatico && (numdocto == null || "".equals(numdocto.trim()))) {
			log.error("::: El numero de documento no se indico o si se debe de generar automatico");
			return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);
		}

		/**************************
		 * validar Usuario Logueado con sistema de Seguridad
		 ***************************/
		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		Minutario minutario = new Minutario();
		Area area = new Area();
		Firmante firmante = new Firmante();

		// Map<String, Object> items = new HashMap<String, Object>();

		ExecutorService taskExecutorPool = Executors.newFixedThreadPool(2);

		FutureTask taskminutario = new FutureTask(new ThreadGenerarAsuntoConsultaMinutario(minutario, idMinutario));
		FutureTask taskarea = new FutureTask(new ThreadGenerarAsuntoConsultaArea(area, areaId, idAreaFirmante));

		taskExecutorPool.submit(taskminutario);
		taskExecutorPool.submit(taskarea);

		try {

			minutario = (Minutario) taskminutario.get();
			area = (Area) taskarea.get();

		} catch (Exception e) {

			System.err.println(e);

		}
		taskExecutorPool.shutdown();

		ExecutorService taskExecutor = Executors.newSingleThreadExecutor();
		FutureTask taskfirmante = new FutureTask(
				new ThreadGenerarAsuntoObtenerFirmante(firmante, minutario, generarAlFirmante));
		taskExecutor.submit(taskfirmante);
		try {

			firmante = (Firmante) taskfirmante.get();

		} catch (Exception e) {

			System.err.println(e);

		}

		taskExecutor.shutdown();

		try {

			taskExecutorPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		} catch (InterruptedException e) {
			
		}

		log.debug("MINUTARIO >> " + idMinutario + " >> PARA CONVERTIR A ASUNTO >> ");

		try {
			// validando parametro idMinutario
			if (minutario != null) {

				// validadno el status actual del minutario y el usuario creador
				if ((minutario.getStatus() == StatusMinutario.AUTORIZADO)
						&& (userId.equalsIgnoreCase(minutario.getUsuario().getIdUsuario()))) {

					// Consultando la lista de los anexos del minutario
					List<Criterion> restrictions = new ArrayList<>();
					restrictions.add(Restrictions.eq("idMinutario", minutario.getIdMinutario()));
					List<DocumentoMinutario> documentosMinutario = (List<DocumentoMinutario>) mngrDocsMinutario
							.search(restrictions);

					// Obteniendo y ordenando la lista de revisosres del
					// minutario
					List<RevisorMinutario> lisRevisores = new ArrayList<RevisorMinutario>();
					lisRevisores = minutario.getRevisores();

					Collections.sort(lisRevisores);

					if (lisRevisores.isEmpty()) {
						throw new Exception("Este borrador no tiene un documento asociado");
					}

					// Construyendo el asuntoDetalle
					AsuntoDetalle asuntoDetalle = new AsuntoDetalle();

					asuntoDetalle.setIdProcedencia("S");

					if (generarAlFirmante == null || generarAlFirmante.trim().equals("")) {

						Usuario usuarioFirmante = minutario.getFirmante();

						firmante.setMaterno(usuarioFirmante.getMaterno());
						firmante.setNombres(usuarioFirmante.getNombres());
						firmante.setPaterno(usuarioFirmante.getApellidoPaterno());
						firmante.setCargo(usuarioFirmante.getCargo());
						firmante.setIdFirmante(usuarioFirmante.getIdUsuario());
						firmante.setIdRemitente(usuarioFirmante.getIdArea());
						firmante.setIdPromotor(minutario.getInstitucion().getIdInstitucion());

					} else {

						Firmante f = new Firmante();
						f.setIdFirmante(generarAlFirmante);

						firmante = (Firmante) firmanteController.search(f).getBody().get(0);
					}
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
					cal1.set(Calendar.MINUTE, cal1.get(Calendar.MINUTE) - 10);
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO)));
					cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 5);
					
					asuntoDetalle.setFechaElaboracion(cal1.getTime());
					asuntoDetalle.setFechaRecepcion(cal.getTime());
					asuntoDetalle.setFirmante(firmante);
					asuntoDetalle.setIdFirmante(firmante.getIdFirmante());
					asuntoDetalle.setFirmanteCargo(firmante.getCargo());
					asuntoDetalle.setTipoRegistro(TipoRegistro.CONTROL_GESTION);
					asuntoDetalle.setAsuntoDescripcion(minutario.getAsunto());
					asuntoDetalle.setConfidencial(minutario.getConfidencial());
					asuntoDetalle.setPromotor(minutario.getInstitucion());

					/** Creamos el objeto Remitente */
					Remitente remitente = new Remitente();
					remitente.setRemitenteKey(new RemitenteKey());

					remitente.setDescripcion(minutario.getRemitente().getDescripcion());

					remitente.getRemitenteKey().setPromotor(
							mngrInstitucion.fetch(minutario.getRemitente().getInstitucion().getIdInstitucion()));

					remitente.getRemitenteKey().setIdRemitente(minutario.getRemitente().getIdArea());

					asuntoDetalle.setRemitente(remitente);

					// ??
					asuntoDetalle.setIdRemitente(remitente.getRemitenteKey().getIdRemitente());

					// Numdocto
					asuntoDetalle.setNumDoctoAuto(numdoctoAutomatico);
					asuntoDetalle.setIdFolioMultiple(idFolioMultiple);

					if (!numdoctoAutomatico) {
						asuntoDetalle.setNumDocto(numdocto);
					}

					// Construyendo el asunto
					ResponseEntity<Asunto> resultAsunto;
					Asunto asunto = new Asunto();
					asunto.setTipoAsunto(TipoAsunto.ASUNTO);
					asunto.setIdSubTipoAsunto(SubTipoAsunto.C.getValue());
					asunto.setIdTipoRegistro(TipoRegistro.CONTROL_GESTION.getValue());
					asunto.setArea(area);
					asunto.setFechaRegistro(
							getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
					asunto.setTurnador(mngrRepresentante.fetch(userId));
					asunto.setDestinatario(userId);
					asunto.setAsuntoDetalle(asuntoDetalle);
					// asunto.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));
					asunto.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));

					if (minutario.getConfidencial() != null && minutario.getConfidencial())
						asunto.setAtributo("SNNNNNNNNN");

					// Guardando el nuevo asunto
					resultAsunto = asuntoController.save(asunto);

					Integer idAsunto = resultAsunto.getBody().getIdAsunto();
					log.info(" NUEVO ASUNTO >> " + idAsunto.toString());

					// Asignamos el Identificador del Asunto padre, que para el
					// caso del Asunto es el mismo
					asunto.setIdAsuntoPadre(idAsunto);
					asuntoController.save(asunto);

					// Agregando el nuevo status para el minutario
					minutario.setIdAsunto(idAsunto);
					minutario.setStatus(StatusMinutario.CONCLUIDO);

					// Actualizando el minutario a su nuevo status
					mngrMinutario.update(minutario);

					// Agregando los documentos anexos al nuevo asunto
					log.debug("AGREGANDO LOSDOCUMENTOS ANEXOS AL ASUNTO NUEVO");
					{
						DocumentoAsunto documentoAsunto = null;
						String contentB64;
						ResponseEntity<Map<String, Object>> response;

						for (DocumentoMinutario documentoMinutario : documentosMinutario) {
							documentoAsunto = new DocumentoAsunto();

							response = repositorioController.getDocument(documentoMinutario.getObjectId(), null);
							contentB64 = (String) response.getBody().get("contentB64");

							documentoAsunto.setFechaRegistro(getCurrentTime(
									getDocStampedData(documentoMinutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
							documentoAsunto.setFileB64(contentB64);
							documentoAsunto.setGubernamental(false);
							documentoAsunto.setIdArea(minutario.getRemitente().getIdArea());
							documentoAsunto.setIdAsunto(idAsunto);
							// documentoAsunto.setObjectId(documentoMinutario.getObjectId());
							documentoAsunto.setObjectName(documentoMinutario.getObjectName());
							documentoAsunto.setOwnerName(documentoMinutario.getOwnerName());
							documentoAsunto.setParentContentId(asunto.getContentId());
							documentoAsunto.setStatus(null);
							documentoAsunto.setEnabledToSend(true);

							documentoAsuntoController.save(documentoAsunto);

						}
					}

					DocumentoAsunto documentoAsunto = new DocumentoAsunto();

					// Agregando la plantilla al nuevo asunto
					{
						// obtenermos el ultimo registro de revisores
						int lastRevIndex = lisRevisores.size() - 1;

						RevisorMinutario lastRevisor = lisRevisores.get(lastRevIndex);

						documentoAsunto.setFechaRegistro(lastRevisor.getFechaRegistro());

						ResponseEntity<Map<String, Object>> response = repositorioController
								.getDocument(lastRevisor.getObjectId(), null);

						String contentB64Old = (String) response.getBody().get("contentB64");

						String contentB64;

						{// se completan los datos faltantes en la plantilla.

							byte[] plantillaFileString = Base64.getDecoder().decode(contentB64Old);

							File file = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX,
									lastRevisor.getDocumentName());

							file.deleteOnExit();

							FileUtils.writeByteArrayToFile(file, plantillaFileString);

							String mimeType;
							// Files.probeContentType(file.toPath());
							
							try (InputStream theInputStream = new FileInputStream(file);
								InputStream is = theInputStream;
								BufferedInputStream bis = new BufferedInputStream(is);) {
								AutoDetectParser parser = new AutoDetectParser();
								Detector detector = parser.getDetector();
								Metadata md = new Metadata();
								md.add(Metadata.RESOURCE_NAME_KEY, file.getName());
								MediaType mediaType = detector.detect(bis, md);
								mimeType = mediaType.toString();
							} catch (Exception e) {
								log.error(e.getMessage());
								throw e;
							}

							log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>> MIMETYPE >>>>>> " + mimeType);

							if ("application/msword".equalsIgnoreCase(mimeType)
									|| "application/xml".equalsIgnoreCase(mimeType)) {

								String newPlantilla = FileUtils.readFileToString(file, "UTF-8");

								newPlantilla = plantillasController.reemplazaKeysMinutario(minutario, newPlantilla,
										null);

								contentB64 = Base64.getEncoder().encodeToString(newPlantilla.getBytes("UTF-8"));

							} else {

								log.warn("El archivo " + file.getAbsolutePath()
										+ " contiene un mime/type diferente a un xml de una plantilla no se puede reemplazar los tags.");
								// el archivo es diferente a una plantilla(xml),
								// no modificar.
								contentB64 = contentB64Old;

							}

							file.delete();
						}

						documentoAsunto.setFileB64(contentB64);
						documentoAsunto.setGubernamental(true);
						documentoAsunto.setIdArea(areaId);
						documentoAsunto.setIdAsunto(idAsunto);
						// documentoAsunto.setObjectId(lisRevisores.get(lastRevIndex).getObjectId());
						documentoAsunto.setObjectName(lastRevisor.getDocumentName());

						// documentoAsunto.setOwnerName(lastRevisor.getUsuario().getIdUsuario());
						documentoAsunto.setOwnerName(userId);

						documentoAsunto.setParentContentId(asunto.getContentId());
						documentoAsunto.setStatus(null);
						documentoAsunto.setEnabledToSend(true);

						documentoAsuntoController.save(documentoAsunto);
					}

					// Turnar el asunto
					List<DestinatariosMinutario> destinatarios = minutario.getDestinatarios();

					// Consultando la lista de los Documentos del Asunto
					// generado
					List<Criterion> restrictionsDoctoAsunto = new ArrayList<>();
					restrictionsDoctoAsunto.add(Restrictions.eq("idAsunto", idAsunto));
					List<DocumentoAsunto> listDocumentoAsuntoNew = (List<DocumentoAsunto>) mngrDocsAsunto
							.search(restrictionsDoctoAsunto);

					Asunto asuntoTurno;
					TipoPrioridad tipoPrioridad;
					ResponseEntity<DiaFestivo> fechaCompromiso;
					for (DestinatariosMinutario destinatarioMinutario : destinatarios) {

						asuntoTurno = new Asunto();
						asuntoTurno.setAntecedentes(null);
						asuntoTurno.setArea(asunto.getArea());
						asuntoTurno.setAreaDestino(destinatarioMinutario.getIdAreaDestinatario());
						asuntoTurno.setAsignadoA(null);
						asuntoTurno.setAsuntoDetalle(asunto.getAsuntoDetalle());
						asuntoTurno.setComentario(asunto.getComentario());
						asuntoTurno.setComentarioRechazo(null);
						asuntoTurno.setContentId(asunto.getContentId());

						asuntoTurno.setDestinatario(destinatarioMinutario.getIdDestinatario());

						asuntoTurno.setEnTiempo(null);
						asuntoTurno.setFechaAcuse(asunto.getFechaAcuse());
						asuntoTurno.setFechaCompromiso(null);
						asuntoTurno.setFechaEnvio(null);
						asuntoTurno.setFechaRegistro(asunto.getFechaRegistro());
						asuntoTurno.setFolioArea(null);
						asuntoTurno.setIdAsunto(null);
						asuntoTurno.setIdTipoRegistro(TipoRegistro.CONTROL_GESTION.getValue());
						asuntoTurno.setIdAsuntoPadre(asunto.getIdAsunto());

						// +++

						// asuntoTurno.setIdSubTipoAsunto(asunto.getIdSubTipoAsunto());

						if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO
								|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_CCP
								|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_TURNO)
							asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.D.getValue());

						else if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO
								|| destinatarioMinutario
										.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_CCP
								|| destinatarioMinutario
										.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_TURNO)
							asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.F.getValue());

						else if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO
								|| destinatarioMinutario
										.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_CCP
								|| destinatarioMinutario
										.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_TURNO)
							asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.C.getValue());

						else if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL
								|| destinatarioMinutario
										.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_CCP
								|| destinatarioMinutario
										.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_TURNO)
							asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.R.getValue());

						// +++

						// ++
						if ((destinatarioMinutario.getIdTipoDestinatario().equals(TipoDestinatario.FUNCIONARIO_INTERNO))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.FUNCIONARIO_EXTERNO))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.REPRESENTANTE_LEGAL))
								|| (destinatarioMinutario.getIdTipoDestinatario().equals(TipoDestinatario.CIUDADANO))) {

							asuntoTurno.setTipoAsunto(TipoAsunto.ENVIO);

						} else if ((destinatarioMinutario.getIdTipoDestinatario()
								.equals(TipoDestinatario.FUNCIONARIO_INTERNO_CCP))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.FUNCIONARIO_EXTERNO_CCP))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.REPRESENTANTE_LEGAL_CCP))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.CIUDADANO_CCP))) {

							asuntoTurno.setTipoAsunto(TipoAsunto.COPIA);

						} else if ((destinatarioMinutario.getIdTipoDestinatario()
								.equals(TipoDestinatario.FUNCIONARIO_INTERNO_TURNO))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.FUNCIONARIO_EXTERNO_TURNO))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.REPRESENTANTE_LEGAL_TURNO))
								|| (destinatarioMinutario.getIdTipoDestinatario()
										.equals(TipoDestinatario.CIUDADANO_TURNO))) {

							asuntoTurno.setTipoAsunto(TipoAsunto.TURNO);

						}

						// Obtenemos la Instruccion por defecto
						asuntoTurno.setInstruccion(instruccionController.getDefaultValue(asunto.getArea().getIdArea()));
						// Obtenemos la prioridad por defecto y su fecha de
						// compromiso
						tipoPrioridad = prioridadController.getDefaultValue(asunto.getArea().getIdArea());
						fechaCompromiso = prioridadController.getFechaCompromiso(tipoPrioridad.getIdPrioridad());

						asuntoTurno.setPrioridad(tipoPrioridad);
						asuntoTurno.setFechaCompromiso(fechaCompromiso.getBody().getKey().getDia());

						asuntoTurno.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));
						asuntoTurno.setStatusTurno(mngrStatus.fetch(Status.POR_ENVIAR));
						asuntoTurno.setTimestamps(null);
						asuntoTurno.setTurnador(asunto.getTurnador());
						asuntoTurno.setIdAsuntoPadre(idAsunto);

						asuntoController.save(asuntoTurno);

						{
							DocumentoAsunto documentoTramite = null;
							String contentB64;
							ResponseEntity<Map<String, Object>> response;

							// Se agregan los documentos de Asunto generadoa a
							// los tramites
							for (DocumentoAsunto documentoAsuntoNew : listDocumentoAsuntoNew) {
								documentoTramite = new DocumentoAsunto();

								response = repositorioController.getDocument(documentoAsuntoNew.getObjectId(), null);

								contentB64 = (String) response.getBody().get("contentB64");

								documentoTramite.setFechaRegistro(getCurrentTime(
										getDocAsuntoStampedData(documentoAsuntoNew, TipoTimestamp.TIMESTAMP_REGISTRO)));
								documentoTramite.setFileB64(contentB64);
								documentoTramite.setGubernamental(documentoAsuntoNew.getGubernamental());
								documentoTramite.setIdArea(minutario.getRemitente().getIdArea());
								documentoTramite.setIdAsunto(asuntoTurno.getIdAsunto());
								documentoTramite.setObjectId(documentoAsuntoNew.getObjectId());
								documentoTramite.setObjectName(documentoAsuntoNew.getObjectName());
								documentoTramite.setOwnerName(documentoAsuntoNew.getOwnerName());
								documentoTramite.setParentContentId(asuntoTurno.getContentId());
								documentoTramite.setStatus(documentoAsuntoNew.getStatus());
								documentoTramite.setEnabledToSend(documentoAsuntoNew.getEnabledToSend());

								mngrDocsAsunto.save(documentoTramite);

							}
						}

						// Para dejar los asuntos por Minutarios estado
						// POR_ENVIAR
						if (destinatarios.isEmpty()) {
							asunto.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));
						}
						mngrAsunto.update(asunto);

					}

					return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);

				} else {
					return new ResponseEntity<Minutario>(minutario, HttpStatus.CONFLICT);

				}

			} else {
				return new ResponseEntity<Minutario>(minutario, HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * @param minutario
	 * @param tipots
	 * @return
	 */
	private String getStampedData(Minutario minutario, TipoTimestamp tipots) {
		String toBeStamped = minutario.getIdMinutario() + "-" + minutario.getIdDocumento() + "-" + tipots.getTipo();
		return toBeStamped;
	}

	/**
	 * @param docMin
	 * @param tipots
	 * @return
	 */
	private String getDocStampedData(DocumentoMinutario docMin, TipoTimestamp tipots) {
		String toBeStamped = docMin.getIdMinutario() + "-" + docMin.getObjectId() + "-" + tipots.getTipo();
		return toBeStamped;
	}

	/**
	 * @param docMin
	 * @param tipots
	 * @return
	 */
	private String getDocAsuntoStampedData(DocumentoAsunto docAs, TipoTimestamp tipots) {
		String toBeStamped = docAs.getIdAsunto() + "-" + docAs.getObjectId() + "-" + tipots.getTipo();
		return toBeStamped;
	}
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Asignar firmante", notes = "Obtiene el detalle de un firmante y lo asigna al borrador")
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

	@RequestMapping(value = "/generarAlFirmante", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> generarAlFirmante(
			@RequestParam(value = "idMinutario", required = true) Integer idMinutario,
			@RequestParam(value = "idFirmante", required = true) String firmante) throws Exception {

		try {
			// Consultando el minutario
			Minutario origen = mngrMinutario.fetch(idMinutario);

			/**************************
			 * validar Usuario Logueado con sistema de Seguridad
			 ***************************/
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			// Integer areaId =
			// Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			// validando parametro idMinutario
			if (origen != null) {

				// validadno el status actual del minutario y el usuario creador
				if ((origen.getStatus() == StatusMinutario.AUTORIZADO)
						&& (userId.equalsIgnoreCase(origen.getUsuario().getIdUsuario()))) {

					// clonar minutario y destinatarios
					Minutario destino = copiaMinutario(origen, firmante);

					if (destino == null) {
						// si no se pudo crear el nuevo minutario
						throw new InternalServerErrorException("Error al guardar el nuevo minutario");
					}

					// copiar el nuevo oficio con su historial de revisiones
					copiaOficio(origen, destino);

					// copiar los anexos al nuevo minutario
					copiaAnexos(idMinutario, destino);

					destino.setStatus(StatusMinutario.REGISTRADO);
					save(destino);

					origen.setStatus(StatusMinutario.CONCLUIDO);
					save(origen);

					return new ResponseEntity<Minutario>(origen, HttpStatus.OK);

				} else {
					// si el minutario origen no esta autorizado o no viene del
					// usuario creador
					return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);

				}

			} else {
				// el minutario origen no existe
				return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * @param origen
	 * @param destino
	 * @throws Exception
	 */
	private void copiaOficio(Minutario origen, Minutario destino) throws Exception {
		// clonar historial de revisiones
		IEndpoint repo = EndpointDispatcher.getInstance();

		// obtener la ultima version del oficio original
		RevisorMinutario lastRevision = getLastRevision(origen.getIdMinutario());
		List<Documento> versiones = getDocVersion(lastRevision.getObjectId());

		// aqui se guarda el objectId de la ultima version guardada
		String lastObjectId = null;

		/*
		 * las versiones ya vienen ordenadas de la menor a la mayor por eso solo se
		 * versionan secuencialmente como las regresa el metodo
		 */
		for (Documento version : versiones) {

			// obtener el contenido del docto
			File contenido = repo.getFile(version.getObjectId());

			// si es la primera version entonces crear documento
			if ("1.0".equals(StringUtils.left(version.getVersion(), 3))) {

				version.setFileB64(FileUtil.fileToStringB64(contenido));

				lastObjectId = generarOficio(contenido, version.getObjectName(), destino);

			} else {
				// si no es la primera entonces versionar
				Boolean isCheckedOut = repo.checkOut(lastObjectId);

				if (isCheckedOut) {
					List<Map<String, String>> newObjectId = repo.checkIn(lastObjectId, Version.MAYOR,
							"Version replicada", version.getObjectName(), contenido);
					lastObjectId = newObjectId.get(0).get("documentoId");

					// setDocumentOwner(lastObjectId,
					// destino.getFirmante().getUserKey());

				}
			}

			// actualiza los registros de revision nuevos con el contentid de la
			// version generada
			for (RevisorMinutario revDestino : destino.getRevisores()) {
				if (lastObjectId != null && revDestino.getObjectId().equalsIgnoreCase(version.getObjectId())) {
					revDestino.setObjectId(lastObjectId);
				}
			}

		}
	}

	private List<Documento> getDocVersion(String objectId) throws Exception {
		IEndpoint repo = EndpointDispatcher.getInstance();

		List<Documento> versiones_ = new ArrayList<Documento>();

		List<Map<String, Object>> versiones = repo.getDocumentVersions(objectId);

		Documento doc;
		for (Map<String, Object> version : versiones) {
			doc = new Documento() {
			};
			doc.setObjectId(version.get("r_object_id").toString());
			doc.setObjectName(version.get("object_name").toString());
			doc.setVersion(version.get("r_version_label").toString());

			versiones_.add(doc);
		}

		return versiones_;
	}

	/**
	 * @param oficio
	 * @param objectName
	 * @param destino
	 * @return
	 * @throws Exception
	 */
	private String generarOficio(File oficio, String objectName, Minutario destino) throws Exception {
		IEndpoint repo = EndpointDispatcher.getInstance();
		String newObjectId = repo.saveDocumentoIntoId( //
				destino.getIdDocumento(), //
				objectName, //
				environment.getProperty("docTypeAdjuntoMinutario"), //
				Version.NONE, //
				"Generado al firmante", //
				oficio);

		setDocumentOwner(newObjectId, destino.getUsuario().getUserKey());

		oficio.delete();

		// Map<String, Object> properties = new HashMap<>();
		// // Obtenemos el User Name del firmante
		// String userName =
		// repo.getUserName(destino.getFirmante().getUserKey());
		// properties.put("owner_name", userName);
		//
		// repo.setProperties(newObjectId, properties);
		//
		// // AGREGAR ACL
		// Map<String, String> additionalData = new HashMap<>();
		// additionalData.put("idOwnerDoc", userName);
		//
		// repo.setACL(newObjectId,
		// environment.getProperty("aclNameAdjuntoMinutario"),
		// additionalData);

		return newObjectId;
	}

	/**
	 * @param objectId
	 * @param userKey
	 * @throws Exception
	 */
	private void setDocumentOwner(String objectId, String userKey) throws Exception {
		IEndpoint repo = EndpointDispatcher.getInstance();
		Map<String, Object> properties = new HashMap<>();
		// Obtenemos el User Name del firmante
		String userName = repo.getUserName(userKey);
		properties.put("owner_name", userName);

		repo.setProperties(objectId, properties);

		// AGREGAR ACL
		Map<String, String> additionalData = new HashMap<>();
		additionalData.put("idOwnerDoc", userName);

		repo.setACL(objectId, environment.getProperty("aclNameAdjuntoMinutario"), additionalData);
	}

	/**
	 * @param idMinutarioOrigen
	 * @param destino
	 * @return
	 * @throws Exception
	 */
	private List<DocumentoMinutario> copiaAnexos(Integer idMinutarioOrigen, Minutario destino) throws Exception {
		// conectarse al repo con los datos del usuario en sesion
		IEndpoint repo = EndpointDispatcher.getInstance();

		List<DocumentoMinutario> anexos = new ArrayList<>();
		ResponseEntity<List<?>> respDocs = documentoMinutarioController.get(idMinutarioOrigen);
		if (respDocs.getStatusCode() == HttpStatus.OK) {
			List<?> anexosOrigen = respDocs.getBody();
			String folderDestinoId = destino.getIdDocumento();

			for (Object obj : anexosOrigen) {
				DocumentoMinutario anexo = (DocumentoMinutario) obj;
				// le seteamos el folder padre
				anexo.setParentContentId(folderDestinoId);

				// traer el content y setearlo en el anexo
				File fileOrigen = repo.getFile(anexo.getObjectId());
				anexo.setFileB64(FileUtil.fileToStringB64(fileOrigen));

				// seteamos el objectId original a null para que el metodo save
				// guarde uno nuevo
				anexo.setObjectId(null);
				// id area del firmante para q aplique el acl correcto
				anexo.setIdArea(String.valueOf(destino.getRemitente().getIdArea()));
				anexo.setIdMinutario(destino.getIdMinutario());
				anexo.setOwnerName(repo.getUserName(destino.getUsuario().getIdUsuario()));

				// guardamos la nueva copia
				documentoMinutarioController.save(anexo);

				anexos.add(anexo);
			}

		}

		return anexos;
	}

	/**
	 * @param idMinutario
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public RevisorMinutario getLastRevision(Integer idMinutario) throws Exception {
		RevisorMinutario ultimo = new RevisorMinutario();
		List<?> lst = new ArrayList<Minutario>();
		log.debug("PARAMETROS DE BUSQUEDA :: " + idMinutario);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			ProjectionList projections = Projections.projectionList();
			List<Criterion> restrictions = new ArrayList<Criterion>();

			projections.add(Projections.max("revisor.version").as("version"));
			projections.add(Projections.max("revisor.fechaRegistro").as("fechaRegistro"));
			projections.add(Projections.max("idMinutario").as("idMinutario"));
			projections.add(Projections.max("revisor.documentName").as("documentName"));
			projections.add(Projections.max("revisor.objectId").as("objectId"));
			projections.add(Projections.max("revisor.comentario").as("comentario"));
			projections.add(Projections.max("revisor.id").as("idRevisor"));
			projections.add(Projections.max("revisor.usuario").as("usuario"));

			restrictions.add(Restrictions.eq("idMinutario", idMinutario));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("idMinutario"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrMinutario.search(restrictions, orders, projections, null, null);

			if (lst != null && lst.size() != 1) {
				throw new Exception("Error al obtener la ultima revision");
			}

			HashMap last = (HashMap) lst.get(0);

			ultimo.setObjectId((String) last.get("objectId"));
			ultimo.setId((String) last.get("idRevisor"));
			ultimo.setVersion((String) last.get("version"));
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(((Timestamp) last.get("fechaRegistro")).getTime());
			ultimo.setFechaRegistro(calendar.getTime());
			ultimo.setComentario((String) last.get("comentario"));
			ultimo.setDocumentName((String) last.get("documentName"));

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return ultimo;

	}

	/**
	 * @param origen
	 * @return
	 * @throws Exception
	 */
	private Minutario copiaMinutario(Minutario origen, String firmante) throws Exception {
		/*
		 * clonar registro minutario clonar registroS de destinatarios clonar registros
		 * de revisores
		 */
		Minutario nuevo = new Minutario();
		BeanUtils.copyProperties(origen, nuevo);
		nuevo.setIdMinutario(null);
		Usuario usuarioFirmante = mngrUsuario.fetch(firmante);
		nuevo.setUsuario(usuarioFirmante);
		nuevo.setRemitente(mngrArea.fetch(origen.getFirmante().getIdArea()));
		nuevo.setDestinatarios(null);
		nuevo.setRevisores(null);

		// copiar destinatarios
		List<DestinatariosMinutario> copiaDest = new ArrayList<>();
		for (int i = 0; i < origen.getDestinatarios().size(); i++) {
			// crear el espacio para la nuevo copia del bean de lista
			copiaDest.add(new DestinatariosMinutario());
			BeanUtils.copyProperties(origen.getDestinatarios().get(i), copiaDest.get(i));
		}
		nuevo.setDestinatarios(copiaDest);

		// copiar revisores
		List<RevisorMinutario> copiaRevs = new ArrayList<>();
		for (int i = 0; i < origen.getRevisores().size(); i++) {
			// crear el espacio para la nuevo copia del bean de lista
			copiaRevs.add(new RevisorMinutario());
			BeanUtils.copyProperties(origen.getRevisores().get(i), copiaRevs.get(i));
		}
		nuevo.setRevisores(copiaRevs);

		ResponseEntity<Minutario> respMin = save(nuevo);
		if (respMin.getStatusCode() == HttpStatus.CREATED) {
			return nuevo;
		}

		return null;
	}

	/**
	 * Generar respuesta.
	 *
	 * @param idMinutario the id minutario
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Generar respuesta", notes = "Genera una respuesta a un asunto existente")
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
	@RequestMapping(value = "/generarRespuesta", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> generarRespuesta(
			@RequestParam(value = "idMinutario", required = true) Integer idMinutario,
			@RequestParam(value = "idAsunto", required = true) Integer idAsunto,
			@RequestParam(value = "folioAutomatico", required = false) String folioAutomatico,
			@RequestParam(value = "idFolioMultiple", required = false) Integer idFolioMultiple,
			@RequestParam(value = "folio", required = true) String folio) throws Exception {

		log.debug("MINUTARIO >> " + idMinutario + " >> PARA CONVERTIR A RESPUESTA DEL ASUNTO  >> " + idAsunto);

		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		Area area = mngrArea.fetch(areaId);

		// Consultando el minutario
		Minutario minutario = mngrMinutario.fetch(idMinutario);
		// Consultando el asunto
		Asunto asunto = mngrAsunto.fetch(idAsunto);
		// la nueva respuesta q se genera
		Respuesta nuevaRespuesta = null;
		try {
			/**************************
			 * validar Usuario Logueado con sistema de Seguridad
			 ***************************/

			// validando parametro idMinutario
			if ((minutario != null) && (asunto != null)) {

				// validadno el status actual del minutario y el usuario creador
				if ((minutario.getStatus() == StatusMinutario.AUTORIZADO)
						&& (userId.equalsIgnoreCase(minutario.getUsuario().getIdUsuario()))) {

					// Construyendo la respuesta
					ResponseEntity<Respuesta> resultRespuesta;
					Respuesta respuesta = new Respuesta();
					TipoRespuesta tipoRespuesta = mngrTipoRespuesta.fetch("A");

					respuesta.setArea(area);
					respuesta.setAreaDestino(asunto.getArea());
					respuesta.setComentario(minutario.getAsunto());
					respuesta.setComentarioRechazo(null);
					respuesta.setFechaAcuse(null);
					// respuesta.setFechaEnvio(getCurrentTime(getStampedData(minutario,
					// TipoTimestamp.TIMESTAMP_ENVIO)));
					respuesta.setFechaRegistro(
							getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
					respuesta.setIdAsunto(asunto.getIdAsunto());
					respuesta.setPorcentaje(getMaxProcentaje(asunto.getIdAsunto()) + 1);
					respuesta.setStatus(mngrStatus.fetch(Status.POR_ENVIAR));
					respuesta.setTipoRespuesta(tipoRespuesta);
					respuesta.setAtributos(null);
					respuesta.setFolioRespuesta(folio);
					// Numdocto
					respuesta.setIdFolioMultiple(idFolioMultiple);

					// Consultando la lista de los anexos del minutario
					List<Criterion> restrictions = new ArrayList<>();
					restrictions.add(Restrictions.eq("idMinutario", minutario.getIdMinutario()));
					List<DocumentoMinutario> documentosMinutario = (List<DocumentoMinutario>) mngrDocsMinutario
							.search(restrictions);

					// Obteniendo y ordenando la lista de revisosres del
					// minutario
					List<RevisorMinutario> lisRevisores = new ArrayList<RevisorMinutario>();
					lisRevisores = minutario.getRevisores();

					if (lisRevisores.isEmpty()) {
						throw new Exception("Este borrador no tiene un documento asociado");
					}

					// agregar copias de la respuesta.
					List<CopiaRespuesta> copias = new ArrayList<>();

					CopiaRespuesta copia;
					for (DestinatariosMinutario destinatario : minutario.getDestinatarios()) {

						if (!destinatario.getIdAreaDestinatario().getIdArea().equals(asunto.getArea().getIdArea())) {
							copia = new CopiaRespuesta();

							// TODO comentado intencionalmente mientras se
							// prueba el nuevo mapping
							// copia.setArea(destinatario.getIdAreaDestinatario());
							Destinatario destinatarioCopia = new Destinatario(destinatario.getIdDestinatario(),
									destinatario.getIdAreaDestinatario().getIdArea());
							copia.setArea(destinatarioCopia);
							copia.setIdAsunto(asunto.getIdAsunto());
							copia.setStatus(mngrStatus.fetch(Status.POR_ENVIAR));

							switch (destinatario.getIdTipoDestinatario()) {
							case FUNCIONARIO_INTERNO:
							case FUNCIONARIO_INTERNO_CCP:
							case FUNCIONARIO_INTERNO_TURNO:
								copia.setIdSubTipoAsunto(SubTipoAsunto.C);
								// copia.setDestinatario(destinatario.getIdDestinatario());
								break;
							case FUNCIONARIO_EXTERNO:
							case FUNCIONARIO_EXTERNO_CCP:
							case FUNCIONARIO_EXTERNO_TURNO:
								// copia.setDestinatario(destinatario.getIdDestinatario());
								copia.setIdSubTipoAsunto(SubTipoAsunto.F);
								break;
							case CIUDADANO:
							case CIUDADANO_CCP:
							case CIUDADANO_TURNO:
								// copia.setDestinatario(destinatario.getIdDestinatario());
								copia.setIdSubTipoAsunto(SubTipoAsunto.D);
								break;
							case REPRESENTANTE_LEGAL:
							case REPRESENTANTE_LEGAL_CCP:
							case REPRESENTANTE_LEGAL_TURNO:
								// copia.setDestinatario(destinatario.getIdDestinatario());
								copia.setIdSubTipoAsunto(SubTipoAsunto.R);
								break;
							default:
								continue;
							}
							copias.add(copia);
						}
					}

					respuesta.setCopias(copias);

					// * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
					// * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

					// Guardando la nueva respuesta
					log.debug(">> ENVIANDO A GUARDAR LA RESPUESTA.");
					resultRespuesta = respuestaController.save(respuesta, folioAutomatico);

					nuevaRespuesta = resultRespuesta.getBody();
					
					if(nuevaRespuesta.getIdRespuesta() == null) {
						//throw new Exception("No se pudo generar la respuesta");
						return new ResponseEntity<Minutario>(minutario, HttpStatus.CONFLICT);
					}

					log.info(" NUEVA RESPUESTA >> " + nuevaRespuesta.getIdRespuesta().toString());

					// * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
					// * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

					{ // Agregando los documentos anexos a la nueva respuesta
						DocumentoRespuesta documentoRespuesta = null;
						String contentB64;
						ResponseEntity<Map<String, Object>> response;

						for (DocumentoMinutario documentoMinutario : documentosMinutario) {
							documentoRespuesta = new DocumentoRespuesta();

							documentoRespuesta.setFechaRegistro(documentoMinutario.getFechaRegistro());

							response = repositorioController.getDocument(documentoMinutario.getObjectId(), null);
							contentB64 = (String) response.getBody().get("contentB64");

							documentoRespuesta.setFileB64(contentB64);
							documentoRespuesta.setIdArea(minutario.getRemitente().getIdArea());
							documentoRespuesta.setIdAsunto(idAsunto);
							documentoRespuesta.setIdRespuesta(nuevaRespuesta.getIdRespuesta());
							// documentoRespuesta.setObjectId(documentoMinutario.getObjectId());
							documentoRespuesta.setObjectName(documentoMinutario.getObjectName());
							documentoRespuesta.setOwnerName(documentoMinutario.getOwnerName());
							documentoRespuesta.setParentContentId(asunto.getContentId());
							documentoRespuesta.setStatus(null);

							documentoRespuestaController.save(documentoRespuesta);

						}
					}

					minutario.setIdAsunto(nuevaRespuesta.getIdAsunto());
					minutario.setAsunto(nuevaRespuesta.getIdAsunto().toString());

					{ // Agregando la plantilla a la nueva respuesta

						log.debug(">> AGRGANDO LA PLANTILLA A LA NUEVA RESPUESTA");
						DocumentoRespuesta documentoRespuesta = new DocumentoRespuesta();

						Collections.sort(lisRevisores,
								Comparator.comparing(RevisorMinutario::getFechaRegistro).reversed());

						RevisorMinutario revisorMinutario = new RevisorMinutario();
						revisorMinutario = lisRevisores.get(0);

						documentoRespuesta.setFechaRegistro(revisorMinutario.getFechaRegistro());

						File plantilla = null;

						{
							ResponseEntity<Map<String, Object>> response = repositorioController
									.getDocument(revisorMinutario.getObjectId(), null);

							String contentB64 = (String) response.getBody().get("contentB64");

							// se completan los datos faltantes en la plantilla.

							byte[] plantillaFileString = Base64.getDecoder().decode(contentB64);

							File file = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX, ".doc");

							file.deleteOnExit();

							FileUtils.writeByteArrayToFile(file, plantillaFileString);

							String newPlantilla = FileUtils.readFileToString(file, "UTF-8");

							newPlantilla = plantillasController.reemplazaKeysMinutario(minutario, newPlantilla,
									nuevaRespuesta.getFolioRespuesta());

							contentB64 = Base64.getEncoder().encodeToString(newPlantilla.getBytes("UTF-8"));

							Path newPlantillaFile = FileUtil.createTempFile(contentB64, "plantilla_completa.doc");

							plantilla = newPlantillaFile.toFile();

							log.debug(newPlantillaFile.toFile().getAbsolutePath());

							file.delete();
						}

						documentoRespuesta.setFileB64(
								Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(plantilla)));
						documentoRespuesta.setIdArea(areaId);
						documentoRespuesta.setIdAsunto(idAsunto);
						documentoRespuesta.setIdRespuesta(nuevaRespuesta.getIdRespuesta());
						// documentoRespuesta.setObjectId(revisorMinutario.getObjectId());
						documentoRespuesta.setObjectName(revisorMinutario.getDocumentName());
						documentoRespuesta.setOwnerName(revisorMinutario.getUsuario().getIdUsuario());
						documentoRespuesta.setParentContentId(asunto.getContentId());
						documentoRespuesta.setStatus(null);

						documentoRespuestaController.save(documentoRespuesta);

						log.debug("Documento adjunto a la respuesta ::" + documentoRespuesta.toString());

						plantilla.delete();

					}

					// Agregando el nuevo status para el minutario
					minutario.setStatus(StatusMinutario.CONCLUIDO);

					// Actualizando el minutario a su nuevo status
					mngrMinutario.update(minutario);
					return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);

				} else {
					return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);

				}

			} else {

				return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {

			if (nuevaRespuesta != null)
				respuestaController.delete(nuevaRespuesta.getIdRespuesta());

			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * @param idAsunto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int getMaxProcentaje(Integer idAsunto) {

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("idAsunto", idAsunto));

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.max("porcentaje").as("maxProcentaje"));

		List<HashMap<String, String>> result = mngrRespuesta.search(restrictions, null, projections, null, null);
		if (result == null) {
			return 9;
		} else {
			Object o = result.get(0).get("maxProcentaje");

			if (o != null) {
				return Integer.parseInt(o.toString());
			} else {
				return 9; // el minimo por q no hay uno anterior.
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(Minutario object) throws Exception {
		// Se reemplaza por search(RequestWrapper<Minutario> body)
		throw new UnsupportedOperationException();
	}

	/**
	 * @param representante
	 * @return
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta firmante", notes = "Consulta la lista de firmantes para asignar a un borrador")
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
	
	@RequestMapping(value = "/searchFirmante", method = RequestMethod.POST)
	public ResponseEntity<List<?>> searchFirmante(@RequestBody(required = true) Representante representante) {

		List<?> lst = new ArrayList<Representante>();
		log.debug("PARAMETROS DE BUSQUEDA : " + representante);

		try {

			String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
			int idAreaUsuario = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
			Area area = mngrArea.fetch(idAreaUsuario);

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if ((representante.getArea() != null) && (representante.getArea().getIdArea()) != null)
				restrictions.add(Restrictions.eq("area.idArea", representante.getArea().getIdArea()));

			restrictions.add(Restrictions.not(Restrictions.eq("id", idUsuario)));
			restrictions.add(Restrictions.not(Restrictions.eq("id", area.getTitular().getId())));

			if ((representante.getMaterno() != null) && (!representante.getMaterno().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("materno", representante.getMaterno(), MatchMode.ANYWHERE));

			if ((representante.getPaterno() != null) && (!representante.getPaterno().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("paterno", representante.getPaterno(), MatchMode.ANYWHERE));

			if ((representante.getNombres() != null) && (!representante.getNombres().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("nombres", representante.getNombres(), MatchMode.ANYWHERE));

			if (representante.getArea() != null) {
				if (representante.getArea().getActivo() != null)
					restrictions.add(Restrictions.eq("area.activo", representante.getArea().getActivo()));

				if (representante.getArea().getInstitucion() != null) {
					if (StringUtils.isNotBlank(representante.getArea().getInstitucion().getTipo()))
						restrictions.add(Restrictions.eq("institucion.tipo",
								representante.getArea().getInstitucion().getTipo()));

					if (representante.getArea().getInstitucion().getActivo() != null)
						restrictions.add(Restrictions.eq("institucion.activo",
								representante.getArea().getInstitucion().getActivo()));
				}
			}

			if ((representante.getActivosn() != null))
				restrictions.add(Restrictions.or(Restrictions.eq("activosn", representante.getActivosn()),
						Restrictions.isNull("activosn")));
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("paterno"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrRepresentante.search(restrictions, orders, null, null, null);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/generarAsuntoOLD", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Minutario> generarAsuntoThread(
			@RequestParam(value = "id", required = true) Integer idMinutario,
			@RequestParam(value = "numdoctoAuto", required = true) String numdoctoAuto,
			@RequestParam(value = "numdocto", required = false) String numdocto,
			@RequestParam(value = "firmante", required = false) String generarAlFirmante,
			@RequestParam(value = "idAreaFirmante", required = false) Integer idAreaFirmante) throws Exception {

		log.debug("MINUTARIO >> " + idMinutario + " >> PARA CONVERTIR A ASUNTO >> ");

		boolean numdoctoAutomatico = Boolean.parseBoolean(numdoctoAuto);

		if (!numdoctoAutomatico && (numdocto == null || "".equals(numdocto.trim()))) {
			log.error("::: El numero de documento no se indico o si se debe de generar automatico");
			return new ResponseEntity<Minutario>(new Minutario(), HttpStatus.BAD_REQUEST);
		}

		/**************************
		 * validar Usuario Logueado con sistema de Seguridad
		 ***************************/
		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		Minutario minutario = new Minutario();
		Area area = new Area();
		Firmante firmante = new Firmante();

		// Map<String, Object> items = new HashMap<String, Object>();

		ExecutorService taskExecutorPool = Executors.newFixedThreadPool(2);

		FutureTask taskminutario = new FutureTask(new ThreadGenerarAsuntoConsultaMinutario(minutario, idMinutario));
		FutureTask taskarea = new FutureTask(new ThreadGenerarAsuntoConsultaArea(area, areaId, idAreaFirmante));

		taskExecutorPool.submit(taskminutario);
		taskExecutorPool.submit(taskarea);

		try {

			minutario = (Minutario) taskminutario.get();
			area = (Area) taskarea.get();

		} catch (Exception e) {

			System.err.println(e);

		}
		taskExecutorPool.shutdown();

		ExecutorService taskExecutor = Executors.newSingleThreadExecutor();
		FutureTask taskfirmante = new FutureTask(
				new ThreadGenerarAsuntoObtenerFirmante(firmante, minutario, generarAlFirmante));
		taskExecutor.submit(taskfirmante);
		try {

			firmante = (Firmante) taskfirmante.get();

		} catch (Exception e) {

			System.err.println(e);

		}

		taskExecutor.shutdown();

		try {

			taskExecutorPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		} catch (InterruptedException e) {
			
		}
		if (minutario != null) {

			// validadno el status actual del minutario y el usuario creador
			if ((minutario.getStatus() == StatusMinutario.AUTORIZADO)
					&& (userId.equalsIgnoreCase(minutario.getUsuario().getIdUsuario()))) {

				// Obteniendo y ordenando la lista de revisosres del
				// minutario
				List<RevisorMinutario> lisRevisores = new ArrayList<RevisorMinutario>();
				lisRevisores = minutario.getRevisores();

				Collections.sort(lisRevisores);

				if (lisRevisores.isEmpty()) {
					throw new Exception("Este borrador no tiene un documento asociado");
				}

				// Construyendo el asuntoDetalle
				AsuntoDetalle asuntoDetalle = new AsuntoDetalle();

				asuntoDetalle.setIdProcedencia("S");
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
				cal1.set(Calendar.MINUTE, cal1.get(Calendar.MINUTE) - 10);

				Calendar cal = Calendar.getInstance();
				cal.setTime(getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO)));
				cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 5);

				asuntoDetalle.setFirmante(firmante);
				asuntoDetalle.setIdFirmante(firmante.getIdFirmante());
				asuntoDetalle.setFirmanteCargo(firmante.getCargo());
				asuntoDetalle.setTipoRegistro(TipoRegistro.CONTROL_GESTION);
				asuntoDetalle.setAsuntoDescripcion(minutario.getAsunto());

				asuntoDetalle.setFechaElaboracion(cal1.getTime());

				asuntoDetalle.setFechaRecepcion(cal.getTime());
				asuntoDetalle.setConfidencial(minutario.getConfidencial());
				asuntoDetalle.setPromotor(minutario.getInstitucion());

				/** Creamos el objeto Remitente */
				Remitente remitente = new Remitente();
				remitente.setRemitenteKey(new RemitenteKey());

				remitente.setDescripcion(minutario.getRemitente().getDescripcion());

				remitente.getRemitenteKey().setPromotor(
						mngrInstitucion.fetch(minutario.getRemitente().getInstitucion().getIdInstitucion()));

				remitente.getRemitenteKey().setIdRemitente(minutario.getRemitente().getIdArea());

				asuntoDetalle.setRemitente(remitente);

				// ??
				asuntoDetalle.setIdRemitente(remitente.getRemitenteKey().getIdRemitente());

				asuntoDetalle.setNumDoctoAuto(numdoctoAutomatico);

				if (!numdoctoAutomatico) {
					asuntoDetalle.setNumDocto(numdocto);
				}

				// Construyendo el asunto
				ResponseEntity<Asunto> resultAsunto;
				Asunto asunto = new Asunto();
				asunto.setTipoAsunto(TipoAsunto.ASUNTO);
				asunto.setIdSubTipoAsunto(SubTipoAsunto.C.getValue());
				asunto.setIdTipoRegistro(TipoRegistro.CONTROL_GESTION.getValue());
				asunto.setArea(area);
				asunto.setFechaRegistro(getCurrentTime(getStampedData(minutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
				asunto.setTurnador(mngrRepresentante.fetch(userId));
				asunto.setDestinatario(userId);
				asunto.setAsuntoDetalle(asuntoDetalle);
				// asunto.setStatusAsunto(mngrStatus.fetch(Status.PROCESO));
				asunto.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));

				if (minutario.getConfidencial() != null && minutario.getConfidencial())
					asunto.setAtributo("SNNNNNNNNN");

				// Guardando el nuevo asunto
				resultAsunto = asuntoController.save(asunto);

				Integer idAsunto = resultAsunto.getBody().getIdAsunto();
				log.info(" NUEVO ASUNTO >> " + idAsunto.toString());

				// Asignamos el Identificador del Asunto padre, que para el
				// caso del Asunto es el mismo
				asunto.setIdAsuntoPadre(idAsunto);
				asuntoController.save(asunto);

				// Agregando el nuevo status para el minutario
				minutario.setIdAsunto(idAsunto);
				minutario.setStatus(StatusMinutario.CONCLUIDO);

				// Actualizando el minutario a su nuevo status
				mngrMinutario.update(minutario);

				// Agregando los documentos anexos al nuevo asunto
				log.debug("AGREGANDO LOSDOCUMENTOS ANEXOS AL ASUNTO NUEVO");

				DocumentoAsunto documentoAsunto = new DocumentoAsunto();

				String contentB64;
				ResponseEntity<Map<String, Object>> response;
				// Consultando la lista de los anexos del minutario
				List<Criterion> restrictions = new ArrayList<>();
				restrictions.add(Restrictions.eq("idMinutario", minutario.getIdMinutario()));
				List<DocumentoMinutario> documentosMinutario = (List<DocumentoMinutario>) mngrDocsMinutario
						.search(restrictions);

				for (DocumentoMinutario documentoMinutario : documentosMinutario) {
					documentoAsunto = new DocumentoAsunto();

					response = repositorioController.getDocument(documentoMinutario.getObjectId(), null);
					contentB64 = (String) response.getBody().get("contentB64");

					documentoAsunto.setFechaRegistro(
							getCurrentTime(getDocStampedData(documentoMinutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
					documentoAsunto.setFileB64(contentB64);
					documentoAsunto.setGubernamental(false);
					documentoAsunto.setIdArea(minutario.getRemitente().getIdArea());
					documentoAsunto.setIdAsunto(idAsunto);
					// documentoAsunto.setObjectId(documentoMinutario.getObjectId());
					documentoAsunto.setObjectName(documentoMinutario.getObjectName());
					documentoAsunto.setOwnerName(documentoMinutario.getOwnerName());
					documentoAsunto.setParentContentId(asunto.getContentId());
					documentoAsunto.setStatus(null);

					documentoAsuntoController.save(documentoAsunto);

				}
				ExecutorService taskExecutorPool2 = Executors.newFixedThreadPool(2);

				ThreadGenerarAsuntoAgregarPlantillaAsunto taskAgregarPlantilla = new ThreadGenerarAsuntoAgregarPlantillaAsunto(
						documentoAsunto, lisRevisores, idAsunto, minutario, userId, areaId, asunto);
				taskAgregarPlantilla.setHEADER_CONTENT_USER(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
				taskAgregarPlantilla.setHEADER_USER_ID(getHeader(HeaderValueNames.HEADER_USER_ID));
				taskAgregarPlantilla.setHEADER_USER_KEY(getHeader(HeaderValueNames.HEADER_USER_KEY));
				taskAgregarPlantilla.setHEADER_AREA_ID(getHeader(HeaderValueNames.HEADER_AREA_ID));

				ThreadGenerarAsuntoTurnarAsunto taskTurnarAsunto = new ThreadGenerarAsuntoTurnarAsunto(minutario,
						idAsunto, asunto);
				taskTurnarAsunto.setHEADER_AREA_ID(getHeader(HeaderValueNames.HEADER_AREA_ID));
				taskTurnarAsunto.setHEADER_USER_ID(getHeader(HeaderValueNames.HEADER_USER_ID));
				taskTurnarAsunto.setHEADER_USER_KEY(getHeader(HeaderValueNames.HEADER_USER_KEY));
				taskTurnarAsunto.setHEADER_CONTENT_USER(getHeader(HeaderValueNames.HEADER_CONTENT_USER));

				taskExecutorPool2.submit(taskAgregarPlantilla);
				taskExecutorPool2.submit(taskTurnarAsunto);
				taskExecutorPool2.shutdown();
				try {

					// taskAgregarPlantilla.get();
					// taskarea.get();
					// taskAgregarDocumento.get();
					// espera 120 sec y termina la ejecucion de todos los
					// threads
					taskExecutorPool2.awaitTermination(120, TimeUnit.SECONDS);
				} catch (Exception e) {

					System.err.println(e);

				}

				return new ResponseEntity<Minutario>(minutario, HttpStatus.OK);

			} else {
				return new ResponseEntity<Minutario>(minutario, HttpStatus.CONFLICT);

			}

		} else {
			return new ResponseEntity<Minutario>(minutario, HttpStatus.CONFLICT);
		}

	}

	public class ThreadGenerarAsuntoConsultaMinutario implements Callable<Minutario> {

		private Minutario minutario;
		private int idMinutario;

		public ThreadGenerarAsuntoConsultaMinutario(Minutario minutario, int idMinutario) {
			this.minutario = minutario;
			this.idMinutario = idMinutario;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Minutario call() {

			Minutario resultminutario = new Minutario();

			try {
				// * * * * * * * * * * * * * * * * * * * * * *

				// Consultando el minutario
				resultminutario = mngrMinutario.fetch(idMinutario);

				// * * * * * * * * * * * * * * * * * * * * * *

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
			}

			minutario = resultminutario;
			return minutario;

		}
	}

	public class ThreadGenerarAsuntoConsultaArea implements Callable<Area> {

		private Area area;
		private int areaId;
		private Integer idAreaFirmante;

		public ThreadGenerarAsuntoConsultaArea(Area area, Integer areaId, Integer idAreaFirmante) {
			this.area = area;
			this.areaId = areaId;
			this.idAreaFirmante = idAreaFirmante;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Area call() {

			Area resultArea = new Area();

			try {
				// * * * * * * * * * * * * * * * * * * * * * *

				// Consultando el minutario
				resultArea = mngrArea.fetch((idAreaFirmante != null ? idAreaFirmante : areaId));

				// * * * * * * * * * * * * * * * * * * * * * *

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
			}

			area = resultArea;
			return area;
		}
	}

	public class ThreadGenerarAsuntoObtenerFirmante implements Callable<Firmante> {

		private Minutario minutario;
		private String generarAlFirmante;
		private Firmante firmante;

		public ThreadGenerarAsuntoObtenerFirmante(Firmante firmante, Minutario minutario, String generarAlFirmante) {
			this.minutario = minutario;
			this.generarAlFirmante = generarAlFirmante;
			this.firmante = firmante;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Firmante call() {

			Firmante firmanteResult = new Firmante();

			try {
				// * * * * * * * * * * * * * * * * * * * * * *
				// Creamos el objeto Firmante

				if (generarAlFirmante == null || generarAlFirmante.trim().equals("")) {

					Usuario usuarioFirmante = minutario.getFirmante();

					firmanteResult.setMaterno(usuarioFirmante.getMaterno());
					firmanteResult.setNombres(usuarioFirmante.getNombres());
					firmanteResult.setPaterno(usuarioFirmante.getApellidoPaterno());
					firmanteResult.setCargo(usuarioFirmante.getCargo());
					firmanteResult.setIdFirmante(usuarioFirmante.getIdUsuario());
					firmanteResult.setIdRemitente(usuarioFirmante.getIdArea());
					firmanteResult.setIdPromotor(minutario.getInstitucion().getIdInstitucion());

				} else {

					Firmante f = new Firmante();
					f.setIdFirmante(generarAlFirmante);

					firmanteResult = (Firmante) firmanteController.search(f).getBody().get(0);
				}

				// * * * * * * * * * * * * * * * * * * * * * *

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
			}

			firmante = firmanteResult;
			return firmante;

		}
	}

	public class ThreadGenerarAsuntoAgregarDocumentoAsunto implements Runnable {

		private Minutario minutario;
		private Integer idAsunto;
		private Asunto asunto;

		private String HEADER_USER_ID;
		private String HEADER_CONTENT_USER;
		private String HEADER_USER_KEY;
		private String HEADER_AREA_ID;

		public ThreadGenerarAsuntoAgregarDocumentoAsunto(Minutario minutario, Integer idAsunto, Asunto asunto) {
			this.minutario = minutario;
			this.idAsunto = idAsunto;
			this.asunto = asunto;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void run() {

			try {

				DocumentoAsunto documentoAsunto = null;
				String contentB64;
				ResponseEntity<Map<String, Object>> response;
				// Consultando la lista de los anexos del minutario
				List<Criterion> restrictions = new ArrayList<>();
				restrictions.add(Restrictions.eq("idMinutario", minutario.getIdMinutario()));
				List<DocumentoMinutario> documentosMinutario = (List<DocumentoMinutario>) mngrDocsMinutario
						.search(restrictions);

				for (DocumentoMinutario documentoMinutario : documentosMinutario) {
					documentoAsunto = new DocumentoAsunto();
					repositorioController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
					repositorioController.addTempHeader(HeaderValueNames.HEADER_CONTENT_USER, HEADER_CONTENT_USER);
					repositorioController.addTempHeader(HeaderValueNames.HEADER_USER_KEY, HEADER_USER_KEY);
					repositorioController.addTempHeader(HeaderValueNames.HEADER_AREA_ID, HEADER_AREA_ID);

					response = repositorioController.getDocument(documentoMinutario.getObjectId(), null);
					contentB64 = (String) response.getBody().get("contentB64");

					documentoAsunto.setFechaRegistro(
							getCurrentTime(getDocStampedData(documentoMinutario, TipoTimestamp.TIMESTAMP_REGISTRO)));
					documentoAsunto.setFileB64(contentB64);
					documentoAsunto.setGubernamental(false);
					documentoAsunto.setIdArea(minutario.getRemitente().getIdArea());
					documentoAsunto.setIdAsunto(idAsunto);
					// documentoAsunto.setObjectId(documentoMinutario.getObjectId());
					documentoAsunto.setObjectName(documentoMinutario.getObjectName());
					documentoAsunto.setOwnerName(documentoMinutario.getOwnerName());
					documentoAsunto.setParentContentId(asunto.getContentId());
					documentoAsunto.setStatus(null);
					documentoAsuntoController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
					documentoAsuntoController.addTempHeader(HeaderValueNames.HEADER_CONTENT_USER, HEADER_CONTENT_USER);

					documentoAsuntoController.save(documentoAsunto);

				}

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
			}
			// return minutario;
		}

		public void setHEADER_AREA_ID(String HEADER_AREA_ID) {
			this.HEADER_AREA_ID = HEADER_AREA_ID;
		}

		public void setHEADER_USER_KEY(String HEADER_USER_KEY) {
			this.HEADER_USER_KEY = HEADER_USER_KEY;
		}

		public void setHEADER_USER_ID(String HEADER_USER_ID) {
			this.HEADER_USER_ID = HEADER_USER_ID;
		}

		public void setHEADER_CONTENT_USER(String HEADER_CONTENT_USER) {
			this.HEADER_CONTENT_USER = HEADER_CONTENT_USER;
		}
	}

	public class ThreadGenerarAsuntoTurnarAsunto implements Runnable {

		private Minutario minutario;
		private Integer idAsunto;
		private Asunto asunto;

		private String HEADER_USER_ID;
		private String HEADER_CONTENT_USER;
		private String HEADER_USER_KEY;
		private String HEADER_AREA_ID;

		public ThreadGenerarAsuntoTurnarAsunto(Minutario minutario, Integer idAsunto, Asunto asunto) {
			this.minutario = minutario;
			this.idAsunto = idAsunto;
			this.asunto = asunto;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void run() {

			try {
				// Turnar el asunto
				List<DestinatariosMinutario> destinatarios = minutario.getDestinatarios();
				List<DocumentoAsunto> listDocumentoAsuntoNew = new ArrayList<>();

				Asunto asuntoTurno;
				TipoPrioridad tipoPrioridad;
				ResponseEntity<DiaFestivo> fechaCompromiso;
				for (DestinatariosMinutario destinatarioMinutario : destinatarios) {

					asuntoTurno = new Asunto();
					asuntoTurno.setAntecedentes(null);
					asuntoTurno.setArea(asunto.getArea());
					asuntoTurno.setAreaDestino(destinatarioMinutario.getIdAreaDestinatario());
					asuntoTurno.setAsignadoA(null);
					asuntoTurno.setAsuntoDetalle(asunto.getAsuntoDetalle());
					asuntoTurno.setComentario(asunto.getComentario());
					asuntoTurno.setComentarioRechazo(null);
					asuntoTurno.setContentId(asunto.getContentId());

					asuntoTurno.setDestinatario(destinatarioMinutario.getIdDestinatario());

					asuntoTurno.setEnTiempo(null);
					asuntoTurno.setFechaAcuse(asunto.getFechaAcuse());
					asuntoTurno.setFechaCompromiso(null);
					asuntoTurno.setFechaEnvio(null);
					asuntoTurno.setFechaRegistro(asunto.getFechaRegistro());
					asuntoTurno.setFolioArea(null);
					asuntoTurno.setIdAsunto(null);
					asuntoTurno.setIdTipoRegistro(TipoRegistro.CONTROL_GESTION.getValue());
					asuntoTurno.setIdAsuntoPadre(asunto.getIdAsunto());

					// +++

					// asuntoTurno.setIdSubTipoAsunto(asunto.getIdSubTipoAsunto());

					if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO
							|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_CCP
							|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_TURNO)
						asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.D.getValue());

					else if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO
							|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_CCP
							|| destinatarioMinutario
									.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_TURNO)
						asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.F.getValue());

					else if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO
							|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_CCP
							|| destinatarioMinutario
									.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_TURNO)
						asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.C.getValue());

					else if (destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL
							|| destinatarioMinutario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_CCP
							|| destinatarioMinutario
									.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_TURNO)
						asuntoTurno.setIdSubTipoAsunto(SubTipoAsunto.R.getValue());

					// +++

					// ++
					if ((destinatarioMinutario.getIdTipoDestinatario().equals(TipoDestinatario.FUNCIONARIO_INTERNO))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.FUNCIONARIO_EXTERNO))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.REPRESENTANTE_LEGAL))
							|| (destinatarioMinutario.getIdTipoDestinatario().equals(TipoDestinatario.CIUDADANO))) {

						asuntoTurno.setTipoAsunto(TipoAsunto.ENVIO);

					} else if ((destinatarioMinutario.getIdTipoDestinatario()
							.equals(TipoDestinatario.FUNCIONARIO_INTERNO_CCP))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.FUNCIONARIO_EXTERNO_CCP))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.REPRESENTANTE_LEGAL_CCP))
							|| (destinatarioMinutario.getIdTipoDestinatario().equals(TipoDestinatario.CIUDADANO_CCP))) {

						asuntoTurno.setTipoAsunto(TipoAsunto.COPIA);

					} else if ((destinatarioMinutario.getIdTipoDestinatario()
							.equals(TipoDestinatario.FUNCIONARIO_INTERNO_TURNO))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.FUNCIONARIO_EXTERNO_TURNO))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.REPRESENTANTE_LEGAL_TURNO))
							|| (destinatarioMinutario.getIdTipoDestinatario()
									.equals(TipoDestinatario.CIUDADANO_TURNO))) {

						asuntoTurno.setTipoAsunto(TipoAsunto.TURNO);

					}

					// Obtenemos la Instruccion por defecto
					asuntoTurno.setInstruccion(instruccionController.getDefaultValue(asunto.getArea().getIdArea()));
					// Obtenemos la prioridad por defecto y su fecha de
					// compromiso
					tipoPrioridad = prioridadController.getDefaultValue(asunto.getArea().getIdArea());
					fechaCompromiso = prioridadController.getFechaCompromiso(tipoPrioridad.getIdPrioridad());

					asuntoTurno.setPrioridad(tipoPrioridad);
					asuntoTurno.setFechaCompromiso(fechaCompromiso.getBody().getKey().getDia());

					asuntoTurno.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));
					asuntoTurno.setStatusTurno(mngrStatus.fetch(Status.POR_ENVIAR));
					asuntoTurno.setTimestamps(null);
					asuntoTurno.setTurnador(asunto.getTurnador());
					asuntoTurno.setIdAsuntoPadre(idAsunto);

					asuntoController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
					asuntoController.addTempHeader(HeaderValueNames.HEADER_AREA_ID, HEADER_AREA_ID);
					asuntoController.save(asuntoTurno);

					{
						DocumentoAsunto documentoTramite = null;
						String contentB64;
						ResponseEntity<Map<String, Object>> response;

						if (listDocumentoAsuntoNew.size() == 0) {

							// Consultando la lista de los Documentos del Asunto
							// generado

							List<Criterion> restrictionsDoctoAsunto = new ArrayList<>();
							restrictionsDoctoAsunto.add(Restrictions.eq("idAsunto", idAsunto));
							listDocumentoAsuntoNew = (List<DocumentoAsunto>) mngrDocsAsunto
									.search(restrictionsDoctoAsunto);
						}

						// Se agregan los documentos de Asunto generadoa a los
						// tramites

						for (DocumentoAsunto documentoAsuntoNew : listDocumentoAsuntoNew) {
							documentoTramite = new DocumentoAsunto();

							repositorioController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
							repositorioController.addTempHeader(HeaderValueNames.HEADER_CONTENT_USER,
									HEADER_CONTENT_USER);
							repositorioController.addTempHeader(HeaderValueNames.HEADER_USER_KEY, HEADER_USER_KEY);
							repositorioController.addTempHeader(HeaderValueNames.HEADER_AREA_ID, HEADER_AREA_ID);
							response = repositorioController.getDocument(documentoAsuntoNew.getObjectId(), null);

							contentB64 = (String) response.getBody().get("contentB64");

							documentoTramite.setFechaRegistro(getCurrentTime(
									getDocAsuntoStampedData(documentoAsuntoNew, TipoTimestamp.TIMESTAMP_REGISTRO)));
							documentoTramite.setFileB64(contentB64);
							documentoTramite.setGubernamental(documentoAsuntoNew.getGubernamental());
							documentoTramite.setIdArea(minutario.getRemitente().getIdArea());
							documentoTramite.setIdAsunto(asuntoTurno.getIdAsunto());
							documentoTramite.setObjectId(documentoAsuntoNew.getObjectId());
							documentoTramite.setObjectName(documentoAsuntoNew.getObjectName());
							documentoTramite.setOwnerName(documentoAsuntoNew.getOwnerName());
							documentoTramite.setParentContentId(asuntoTurno.getContentId());
							documentoTramite.setStatus(documentoAsuntoNew.getStatus());
							mngrDocsAsunto.save(documentoTramite);

						}
					}

					// Para dejar los asuntos por Minutarios estado
					// POR_ENVIAR
					asunto.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));
					mngrAsunto.update(asunto);
				}

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
			}

			// return minutario;

		}

		public void setHEADER_USER_KEY(String HEADER_USER_KEY) {
			this.HEADER_USER_KEY = HEADER_USER_KEY;
		}

		public void setHEADER_CONTENT_USER(String HEADER_CONTENT_USER) {
			this.HEADER_CONTENT_USER = HEADER_CONTENT_USER;
		}

		public void setHEADER_USER_ID(String HEADER_USER_ID) {
			this.HEADER_USER_ID = HEADER_USER_ID;
		}

		public void setHEADER_AREA_ID(String HEADER_AREA_ID) {
			this.HEADER_AREA_ID = HEADER_AREA_ID;
		}
	}

	public class ThreadGenerarAsuntoAgregarPlantillaAsunto implements Runnable {

		private DocumentoAsunto documentoAsunto;
		private Asunto asunto;
		private Integer idAsunto;
		private String userId;
		private Integer areaId;
		private Minutario minutario;
		private List<RevisorMinutario> lisRevisores;

		private String HEADER_USER_ID;
		private String HEADER_CONTENT_USER;
		private String HEADER_USER_KEY;
		private String HEADER_AREA_ID;

		public ThreadGenerarAsuntoAgregarPlantillaAsunto(DocumentoAsunto documentoAsunto,
				List<RevisorMinutario> lisRevisores, Integer idAsunto, Minutario minutario, String userId,
				Integer areaId, Asunto asunto) {
			this.documentoAsunto = documentoAsunto;
			this.lisRevisores = lisRevisores;
			this.asunto = asunto;
			this.idAsunto = idAsunto;
			this.userId = userId;
			this.areaId = areaId;
			this.minutario = minutario;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {

			try {

				int lastRevIndex = lisRevisores.size() - 1;

				RevisorMinutario lastRevisor = lisRevisores.get(lastRevIndex);

				documentoAsunto.setFechaRegistro(lastRevisor.getFechaRegistro());

				repositorioController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
				repositorioController.addTempHeader(HeaderValueNames.HEADER_CONTENT_USER, HEADER_CONTENT_USER);
				repositorioController.addTempHeader(HeaderValueNames.HEADER_USER_KEY, HEADER_USER_KEY);
				repositorioController.addTempHeader(HeaderValueNames.HEADER_AREA_ID, HEADER_AREA_ID);
				ResponseEntity<Map<String, Object>> response = repositorioController
						.getDocument(lastRevisor.getObjectId(), null);

				String contentB64Old = (String) response.getBody().get("contentB64");

				String contentB64;

				{// se completan los datos faltantes en la plantilla.

					byte[] plantillaFileString = Base64.getDecoder().decode(contentB64Old);

					File file = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX,
							lastRevisor.getDocumentName());

					file.deleteOnExit();

					FileUtils.writeByteArrayToFile(file, plantillaFileString);

					String mimeType;
					// Files.probeContentType(file.toPath());

					try (InputStream theInputStream = new FileInputStream(file);
						InputStream is = theInputStream; 
						BufferedInputStream bis = new BufferedInputStream(is);) {
						AutoDetectParser parser = new AutoDetectParser();
						Detector detector = parser.getDetector();
						Metadata md = new Metadata();
						md.add(Metadata.RESOURCE_NAME_KEY, file.getName());
						MediaType mediaType = detector.detect(bis, md);
						mimeType = mediaType.toString();
					} catch (Exception e) {
						log.error(e.getMessage());
						throw e;
					}

					log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>> MIMETYPE >>>>>> " + mimeType);

					if ("application/msword".equalsIgnoreCase(mimeType)
							|| "application/xml".equalsIgnoreCase(mimeType)) {

						String newPlantilla = FileUtils.readFileToString(file, "UTF-8");
						plantillasController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
						plantillasController.addTempHeader(HeaderValueNames.HEADER_CONTENT_USER, HEADER_CONTENT_USER);
						plantillasController.addTempHeader(HeaderValueNames.HEADER_USER_KEY, HEADER_USER_KEY);
						plantillasController.addTempHeader(HeaderValueNames.HEADER_AREA_ID, HEADER_AREA_ID);
						newPlantilla = plantillasController.reemplazaKeysMinutario(minutario, newPlantilla, null);

						contentB64 = Base64.getEncoder().encodeToString(newPlantilla.getBytes("UTF-8"));

					} else {

						log.warn("El archivo " + file.getAbsolutePath()
								+ " contiene un mime/type diferente a un xml de una plantilla no se puede reemplazar los tags.");
						// el archivo es diferente a una plantilla(xml),
						// no modificar.
						contentB64 = contentB64Old;

					}
				}

				documentoAsunto.setFileB64(contentB64);
				documentoAsunto.setGubernamental(true);
				documentoAsunto.setIdArea(areaId);
				documentoAsunto.setIdAsunto(idAsunto);
				// documentoAsunto.setObjectId(lisRevisores.get(lastRevIndex).getObjectId());
				documentoAsunto.setObjectName(lastRevisor.getDocumentName());

				// documentoAsunto.setOwnerName(lastRevisor.getUsuario().getIdUsuario());
				documentoAsunto.setOwnerName(userId);

				documentoAsunto.setParentContentId(asunto.getContentId());
				documentoAsunto.setStatus(null);
				documentoAsuntoController.addTempHeader(HeaderValueNames.HEADER_USER_ID, HEADER_USER_ID);
				documentoAsuntoController.addTempHeader(HeaderValueNames.HEADER_CONTENT_USER, HEADER_CONTENT_USER);
				documentoAsuntoController.addTempHeader(HeaderValueNames.HEADER_USER_KEY, HEADER_USER_KEY);
				documentoAsuntoController.addTempHeader(HeaderValueNames.HEADER_AREA_ID, HEADER_AREA_ID);

				documentoAsuntoController.save(documentoAsunto);

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				
			}

			//
		}

		public void setHEADER_AREA_ID(String HEADER_AREA_ID) {
			this.HEADER_AREA_ID = HEADER_AREA_ID;
		}

		public void setHEADER_USER_KEY(String HEADER_USER_KEY) {
			this.HEADER_USER_KEY = HEADER_USER_KEY;
		}

		public void setHEADER_CONTENT_USER(String HEADER_CONTENT_USER) {
			this.HEADER_CONTENT_USER = HEADER_CONTENT_USER;
		}

		public void setHEADER_USER_ID(String HEADER_USER_ID) {
			this.HEADER_USER_ID = HEADER_USER_ID;
		}
	}

}
