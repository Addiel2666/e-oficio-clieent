/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.controller.util.TreeNode;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.CustomAsunto;
import com.ecm.sigap.data.model.Institucion;
import com.ecm.sigap.data.model.ParametroApp;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.interop.InstitucionOpe;
import com.ecm.sigap.data.model.interop.Registro;
import com.ecm.sigap.data.model.interop.WsSincronizaCompletaDetalle;
import com.ecm.sigap.data.model.util.CopiaRespuesta;
import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.StatusInstitucionOpe;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoFirma;
import com.ecm.sigap.data.model.util.TipoRegistroWsOpe;
import com.ecm.sigap.interoperabilidad.impl.InteroperabilidadServiceImpl;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controlador REST para la seccion interoperabilidad.
 * 
 * @author Gustavo Vielma
 * @version 2.0
 *
 */
@RestController
public class InteroperabilidadController extends CustomRestController {

	/** Manejador para el tipo {@link ParametroApp }. */
	@Autowired
	private ParametroAppController parametroAppController;

	/** Manejador para el tipo {@link Institucion}. */
	@Autowired
	private InstitucionController institucionController;

	/** Manejador para el tipo {@link Asunto}. */
	@Autowired
	private AsuntoController asuntoController;

	/** Manejador para el tipo {@link Respuesta}. */
	@Autowired
	private RespuestaController respuestaController;

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(InteroperabilidadController.class);

	/**
	 * Registro.
	 *
	 * @param registro the registro
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Registrar institucion", notes = "Registra una institucion para interoperabilidad")
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
	
	@RequestMapping(value = "/interop/registro", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Registro> registro(@RequestBody(required = true) Registro registro)
			throws Exception {
		try {

			// Codigo para almacenar información
			if (null != registro) {
				log.debug("REGISTRO A GUARDAR >> " + registro);

				Integer idInstitucion = registro.getInstitucion().getIdInstitucion();
				String idUsuario = registro.getUsuario().getIdUsuario();
				String endpoint = registro.getEndpoint();
				String uri = registro.getUri();
				String urlWebServiceSigap = registro.getUrlWebServiceSigap();
				String urlWebServiceOPE = registro.getUrlWebServiceOpe();

				if (null != idInstitucion && StringUtils.isNotBlank(idUsuario) && StringUtils.isNotBlank(endpoint)
						&& StringUtils.isNotBlank(uri) && StringUtils.isNotBlank(urlWebServiceSigap)
						&& StringUtils.isNotBlank(urlWebServiceOPE)) {

					Institucion institucion = mngrInstitucion.fetch(idInstitucion);
					Usuario usuario = mngrUsuario.fetch(idUsuario);

					if (null != institucion && null != usuario) {
						institucion.setEndpoint(endpoint);
						institucion.setUri(uri);
						mngrInstitucion.update(institucion);

					} else {
						return new ResponseEntity<Registro>(registro, HttpStatus.BAD_REQUEST);
					}

					ParametroApp paramApp = new ParametroApp();
					paramApp.setIdSeccion("SIGAP");
					paramApp.setIdClave("INST_ID_INTER");
					paramApp.setValor(idInstitucion.toString());
					parametroAppController.save(paramApp);

					paramApp = new ParametroApp();
					paramApp.setIdSeccion("SIGAP");
					paramApp.setIdClave("USER_ID_INTER");
					paramApp.setValor(idUsuario);
					parametroAppController.save(paramApp);

					paramApp = new ParametroApp();
					paramApp.setIdSeccion("SIGAP");
					paramApp.setIdClave("WEBSERVICESITE");
					paramApp.setValor(urlWebServiceSigap);
					parametroAppController.save(paramApp);

					paramApp = new ParametroApp();
					paramApp.setIdSeccion("SIGAP");
					paramApp.setIdClave("ENDPOINTOPE");
					paramApp.setValor(urlWebServiceOPE);
					parametroAppController.save(paramApp);

				} else {
					return new ResponseEntity<Registro>(registro, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Registro>(registro, HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<Registro>(registro, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the datos inst registrada.
	 *
	 * @param registro the registro
	 * @return the datos inst registrada
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta institucion registrada", notes = "Consulta la lista de instituciones registradas para interoperabilidad")
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
	
	@RequestMapping(value = "/interop/registro", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Registro> getDatosInstRegistrada(
			@RequestBody(required = true) Registro registro) {
		try {

			Integer idInstRegistrada = Integer.parseInt(getParamApp("SIGAP", "INST_ID_INTER"));
			String idUsuarioInstRegistrada = getParamApp("SIGAP", "USER_ID_INTER");

			Registro datosInstRegistrada = new Registro();

			Institucion instRegistrada = null;
			Usuario usuarioInstRegistrada = null;

			if (null != idInstRegistrada && null != idUsuarioInstRegistrada) {
				instRegistrada = mngrInstitucion.fetch(idInstRegistrada);
				usuarioInstRegistrada = mngrUsuario.fetch(idUsuarioInstRegistrada);
			}

			datosInstRegistrada.setInstitucion(instRegistrada);
			datosInstRegistrada.setUsuario(usuarioInstRegistrada);
			datosInstRegistrada.setEndpoint(instRegistrada.getEndpoint());
			datosInstRegistrada.setUri(null != instRegistrada ? instRegistrada.getUri() : null);
			datosInstRegistrada.setUrlWebServiceOpe(getParamApp("SIGAP", "ENDPOINTOPE"));
			datosInstRegistrada.setUrlWebServiceSigap(getParamApp("SIGAP", "WEBSERVICESITE"));
			datosInstRegistrada.setRegistradoOpe(getRegistradoOPE());

			return new ResponseEntity<Registro>(datosInstRegistrada, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Registrar instancia.
	 *
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Registrar instancia", notes = "Obtiene detalle de una instancia y la registra")
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
	
	@RequestMapping(value = "/interop/registrarInstancia", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> registrarInstancia() throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();

		try {
			// validarRegistrodeinstancia()

			if (!getRegistradoOPE()) {

				interoperabilidadEndPoint.registrarInstancia();

				// Actualizar parametros app
				ParametroApp paramApp = new ParametroApp();
				paramApp.setIdSeccion("SIGAP");
				paramApp.setIdClave("REGOPE");
				paramApp.setValor("Y");
				parametroAppController.save(paramApp);

			} else {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());

			throw e;
		}

	}

	/**
	 * Sincronizacion parcial.
	 *
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Sincronizacion parcial areas", notes = "Sincroniza las areas seleccionadas para interoperar")
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
	
	@RequestMapping(value = "/interop/sincronizacion/parcial", method = RequestMethod.GET)
	public void sincronizacionParcial() throws Exception {
		try {
			if (existChanges()) {
				interoperabilidadEndPoint.sincronizarDirectorioParcial();
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Sincronizacion completa.
	 *
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Sincronizacion completa areas", notes = "Sincroniza todas las areas para interoperar")
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
	
	@RequestMapping(value = "/interop/sincronizacion/completa", method = RequestMethod.GET)
	public void sincronizacionCompleta() throws Exception {
		try {
			interoperabilidadEndPoint.sincronizarDirectorioCompleto();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Exist changes.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean existChanges() throws Exception {
		List<Criterion> rests = new ArrayList<Criterion>();
		List<?> lst = mngrModificacion.search(rests);
		return !lst.isEmpty() ? true : false;
	}

	/**
	 * Gets the institucion interopera.
	 *
	 * @return the institucion interopera
	 */
	public Institucion getInstitucionInteropera() {
		String param = getParamApp("INST_ID_INTER");
		if (param != null) {
			int idInstitucion = Integer.parseInt(param);
			return mngrInstitucion.fetch(idInstitucion);
		}
		return null;
	}

	/**
	 * Gets the usuario interopera.
	 *
	 * @return the usuario interopera
	 */
	private Usuario getUsuarioInteropera() {
		String param = getParamApp("USER_ID_INTER");
		if (param != null) {
			return mngrUsuario.fetch(param);
		}
		return null;
	}

	/**
	 * Gets the interopera.
	 *
	 * @return the interopera
	 */
	private Boolean getInteropera() {
		String param = getParamApp("REGINTEROP");
		return "S".equalsIgnoreCase(param);
	}

	/**
	 * Gets the modificacion.
	 *
	 * @return the modificacion
	 */
	private String getModificacion() {
		return getParamApp("INTEROP", "IO_Mod");
	}

	/**
	 * Gets the baja.
	 *
	 * @return the baja
	 */
	private String getBaja() {
		return getParamApp("INTEROP", "IO_Baj");
	}

	/**
	 * Gets the alta.
	 *
	 * @return the alta
	 */
	private String getAlta() {
		return getParamApp("INTEROP", "IO_Alt");
	}

	/**
	 * Gets the registrado OPE.
	 *
	 * @return the registrado OPE
	 */
	private Boolean getRegistradoOPE() {
		String param = getParamApp("REGOPE");
		return "Y".equalsIgnoreCase(param) || "S".equalsIgnoreCase(param) ? true : false;
	}

	/**
	 * Gets the WS file XML.
	 *
	 * @param asunto         the asunto
	 * @param tipoSolicitud  the tipo solicitud
	 * @param certificadoB64 the certificado B 64
	 * @param areasDestino   the areas destino
	 * @param areasCopia     the areas copia
	 * @return the WS file XML
	 * @throws Exception the exception
	 */
	private byte[] getWSFileXML(String asunto, String tipoSolicitud, String certificadoB64, List<String> areasDestino,
			List<String> areasCopia) throws Exception {

		InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();

		String mensajeOficio = service.generarDocumentoElectronico(asunto, tipoSolicitud.toUpperCase(), areasDestino,
				areasCopia);

		// convertir cadena a document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document oficio = builder.parse(new InputSource(new StringReader(mensajeOficio)));

		// reemplazar elemento certificado.
		NodeList nodes = oficio.getElementsByTagName("Certificado");
		Node oficioElectronico = nodes.item(0);

		Element certificado = oficio.createElement("Certificado");

		certificado.setTextContent(certificadoB64);

		oficioElectronico.getParentNode()//
				.replaceChild(oficio.importNode(certificado, true), oficioElectronico);

		// convert Documento to File
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){

			Source xmlSource = new DOMSource(oficio);

			Result outputTarget = new StreamResult(outputStream);

			transformer.transform(xmlSource, outputTarget);

			return outputStream.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Inicia registro oficio electronico tramite.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/interop/iniciar/registroOficioElectronicoTramite", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> iniciaRegistroOficioElectronicoTramite(
			@RequestBody(required = true) RequestWrapper<List<Asunto>> body) throws Exception {

		try {

			List<Asunto> tramites_ = body.getObject();
			Map<String, Object> params = body.getParams();

			String certificadoB64 = params.get("certificadoB64").toString();

			String algoritmoFirma = params.get("algoritmoFirma").toString();

			String tipoSolicitud = "Solicitud"; // Solicitud o Respuesta

			Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));

			Map<String, Object> resultado = new HashMap<String, Object>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();

			List<String> areasDestino;
			List<String> areasCopias;

			for (Asunto tramite : tramites_) {
				areasDestino = new ArrayList<>();
				areasCopias = new ArrayList<>();

				if (TipoAsunto.ENVIO.equals(tramite.getTipoAsunto())
						|| TipoAsunto.TURNO.equals(tramite.getTipoAsunto())) {

					areasDestino.add(tramite.getAreaDestino().getIdArea().toString());

				} else if (TipoAsunto.COPIA.equals(tramite.getTipoAsunto())) {

					areasCopias.add(tramite.getAreaDestino().getIdArea().toString());

				}
				try {
					// se crea el XML con el tramite.
					byte[] xmlMessage = getWSFileXML(tramite.getIdAsunto().toString(), tipoSolicitud, certificadoB64,
							areasDestino, areasCopias);

					String contentB64 = Base64.encodeBase64String(xmlMessage);

					// Iniciar Proceso de firma.
					Map<String, Object> resultHash = beginFirmaMensaje(tramite.getIdAsunto(), contentB64,
							certificadoB64, user.getEmail(), algoritmoFirma);

					resultHash.put("idTramite", tramite.getIdAsunto());

					listExito.add(resultHash);

				} catch (Exception e) {

					Map<String, Object> resultFail = new HashMap<>();
					resultFail.put(String.format("Tramite_%d", tramite.getIdAsunto()), e.getLocalizedMessage());
					listFail.add(resultFail);

				}
			}

			resultado.put("succes", listExito);
			resultado.put("error", listFail);

			return new ResponseEntity<Map<String, Object>>(resultado, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Finalizar registro oficio electronico tramite.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interop/finalizar/registroOficioElectronicoTramite", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> finalizarRegistroOficioElectronicoTramite(
			@RequestBody Map<String, Object> body) throws Exception {

		try {

			List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

			String algoritmoFirma = (String) body.get("algoritmoFirma");

			String certB64 = body.get("certificado").toString();
			// para certificados pem
			certB64 = certB64.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "")
					.trim();

			String tipoSolicitud = "Solicitud";// body.get("tipoSolicitud").toString(); // Solicitud o Respuesta

			Map<String, List<?>> resultado = new HashMap<String, List<?>>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();

			if (paraFirmar != null && !paraFirmar.isEmpty()) {

				InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();

				Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));

				for (Map<String, String> map : paraFirmar) {

					try {

						JSONObject documento = new JSONObject(map);

						Integer idAsunto = Integer.parseInt(documento.get("idTramite").toString());
						Integer uploadId = Integer.parseInt(documento.get("uploadId").toString());
						String firmaHex = documento.get("HashArchivo").toString();

						List<String> areasDestino = new ArrayList<>();
						List<String> areasCopia = new ArrayList<>();

						byte[] decodedHex = Hex.decodeHex(firmaHex.toCharArray());

						String OficioElectronicoFirmado = Base64.encodeBase64String(decodedHex);

						if (validarFirmaMensajeProcess(uploadId, user.getEmail(), certB64, OficioElectronicoFirmado,
								algoritmoFirma)) {

							try {
								Asunto tramite = mngrAsunto.fetch(idAsunto);

								if (TipoAsunto.ENVIO.equals(tramite.getTipoAsunto())
										|| TipoAsunto.TURNO.equals(tramite.getTipoAsunto())) {

									areasDestino.add(tramite.getAreaDestino().getIdArea().toString());

								} else if (TipoAsunto.COPIA.equals(tramite.getTipoAsunto())) {
									areasCopia.add(tramite.getAreaDestino().getIdArea().toString());
								}

								try {

									service.registrarOficioElectronico(tramite.getIdAsunto().toString(),
											uploadId.toString(), tipoSolicitud.toUpperCase(), areasDestino, areasCopia);

								} catch (Exception e) {
									log.error(errorMessages.getString("errorInteropRegistrarOficioElectronico") + " "
											+ e.getLocalizedMessage());

									Map<String, Object> result = new HashMap<>();
									result.put("failCause",
											errorMessages.getString("errorInteropRegistrarOficioElectronico"));
									listFail.add(result);

									// Si falla el Registro del oficio electronico salta al siguiente.
									continue;
								}

								// Actualizar el tramite

								// Setea nuevo estatus y timeStamp
								tramite = asuntoController.setEstatusSetTimeStampTramite(tramite);

								// Actualiza el Tramite
								mngrAsunto.update(tramite);

								/** Guardamos el registros en la tabla CUSTOMASUNTO */
								log.debug(
										"Creando el registro en CUSTOMASUNTO para el tramite " + tramite.getIdAsunto());
								CustomAsunto customAsunto = new CustomAsunto();
								customAsunto.setIdAsunto(tramite.getIdAsunto());
								customAsunto.setCustom0(String.valueOf(tramite.getAreaDestino().getIdArea()));
								mngrCustomAsunto.save(customAsunto);

								// Actualiza el estatus del Asunto padre del tramite
								asuntoController.actualizaAsuntoPadreDelTramite(tramite, null, null);

								Map<String, Object> result = new HashMap<>();
								result.put("idTramite", tramite.getIdAsunto());

								listExito.add(result);

							} catch (Exception e) {

								throw e;
							}

						}

					} catch (Exception e) {
						log.error(errorMessages.getString("firmaErrorRetry") + " " + e.getLocalizedMessage());

						Map<String, Object> result = new HashMap<>();
						result.put("failCause", errorMessages.getString("firmaErrorRetry"));
						listFail.add(result);
					}

				}
			}

			resultado.put("exito", listExito);
			resultado.put("fail", listFail);

			return new ResponseEntity<Map<String, List<?>>>(resultado, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Inicia registro oficio electronico respuesta.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/interop/iniciar/registroOficioElectronicoRespuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> iniciaRegistroOficioElectronicoRespuesta(
			@RequestBody(required = true) RequestWrapper<List<Respuesta>> body) throws Exception {

		try {

			List<Respuesta> lstRespuesta_ = body.getObject();
			Map<String, Object> params = body.getParams();

			String certificadoB64 = params.get("certificadoB64").toString();

			String algoritmoFirma = params.get("algoritmoFirma").toString();

			String tipoSolicitud = "Respuesta"; // Solicitud o Respuesta

			Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));

			Map<String, Object> resultado = new HashMap<String, Object>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();

			List<String> areasDestino;
			List<String> areasCopias;

			for (Respuesta respuesta : lstRespuesta_) {

				respuesta = mngrRespuesta.fetch(respuesta.getIdRespuesta());

				areasDestino = new ArrayList<>();
				areasCopias = new ArrayList<>();

				areasDestino.add(respuesta.getAreaDestino().getIdArea().toString());

				// Obtener los idAreas Copias de la respuesta.
				for (CopiaRespuesta copiaRespuesta : respuesta.getCopias()) {
					areasCopias.add(copiaRespuesta.getArea().getIdArea().toString());
				}

				try {
					// se crea el XML con el tramite.
					byte[] xmlMessage = getWSFileXML(respuesta.getIdRespuesta().toString(), tipoSolicitud,
							certificadoB64, areasDestino, areasCopias);

					String contentB64 = Base64.encodeBase64String(xmlMessage);

					// Iniciar Proceso de firma.
					Map<String, Object> resultHash = beginFirmaMensaje(respuesta.getIdRespuesta(), contentB64,
							certificadoB64, user.getEmail(), algoritmoFirma);

					resultHash.put("idRespuesta", respuesta.getIdRespuesta());

					listExito.add(resultHash);

				} catch (Exception e) {

					Map<String, Object> resultFail = new HashMap<>();
					resultFail.put(String.format("Respuesta_%d", respuesta.getIdRespuesta()), e.getLocalizedMessage());
					listFail.add(resultFail);

				}
			}

			resultado.put("succes", listExito);
			resultado.put("error", listFail);

			return new ResponseEntity<Map<String, Object>>(resultado, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Finalizar registro oficio electronico respuesta.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interop/finalizar/registroOficioElectronicoRespuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> finalizarRegistroOficioElectronicoRespuesta(
			@RequestBody Map<String, Object> body) throws Exception {

		try {

			List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

			String algoritmoFirma = (String) body.get("algoritmoFirma");

			String certB64 = body.get("certificado").toString();
			// para certificados pem
			certB64 = certB64.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "")
					.trim();

			String tipoSolicitud = "Respuesta";// body.get("tipoSolicitud").toString(); // Solicitud o Respuesta

			Map<String, List<?>> resultado = new HashMap<String, List<?>>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();

			if (paraFirmar != null && !paraFirmar.isEmpty()) {

				InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();

				Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));

				for (Map<String, String> map : paraFirmar) {

					try {

						JSONObject documento = new JSONObject(map);

						Integer idRespuesta = Integer.parseInt(documento.get("idRespuesta").toString());
						Integer uploadId = Integer.parseInt(documento.get("uploadId").toString());
						String firmaHex = documento.get("HashArchivo").toString();

						List<String> areasDestino = new ArrayList<>();
						List<String> areasCopia = new ArrayList<>();

						byte[] decodedHex = Hex.decodeHex(firmaHex.toCharArray());

						String OficioElectronicoFirmado = Base64.encodeBase64String(decodedHex);

						if (validarFirmaMensajeProcess(uploadId, user.getEmail(), certB64, OficioElectronicoFirmado,
								algoritmoFirma)) {

							try {
								Respuesta respuesta = mngrRespuesta.fetch(idRespuesta);

								areasDestino.add(respuesta.getAreaDestino().getIdArea().toString());

								// Obtener los idAreas Copias de la respuesta.
								for (CopiaRespuesta copiaRespuesta : respuesta.getCopias()) {
									areasCopia.add(copiaRespuesta.getArea().getIdArea().toString());
								}

								try {

									service.registrarOficioElectronico(respuesta.getIdRespuesta().toString(),
											OficioElectronicoFirmado, tipoSolicitud.toUpperCase(), areasDestino,
											areasCopia);

								} catch (Exception e) {
									log.error(errorMessages.getString("errorInteropRegistrarOficioElectronico") + " "
											+ e.getLocalizedMessage());

									Map<String, Object> result = new HashMap<>();
									result.put("failCause",
											errorMessages.getString("errorInteropRegistrarOficioElectronico"));
									listFail.add(result);

								}

								// Modificar Respuesta enviada.
								// Setea nuevo estatus y timeStamp
								respuesta = respuestaController.setEstatusSetTimeStampRespuesta(respuesta);
								mngrRespuesta.update(respuesta);

								Asunto asunto = mngrAsunto.fetch(respuesta.getIdAsunto());
								// Actualizamos el estatus del Asunto
								respuestaController.actualizarAsuntoPadreRespuesta(asunto.getIdAsuntoPadre());

								Map<String, Object> result = new HashMap<>();
								result.put("idRespuesta", respuesta.getIdRespuesta());

								listExito.add(result);

							} catch (Exception e) {

								throw e;
							}

						}

					} catch (Exception e) {
						log.error(errorMessages.getString("firmaErrorRetry") + " " + e.getLocalizedMessage());

						Map<String, Object> result = new HashMap<>();
						result.put("failCause", errorMessages.getString("firmaErrorRetry"));
						listFail.add(result);
					}

				}
			}

			resultado.put("exito", listExito);
			resultado.put("fail", listFail);

			return new ResponseEntity<Map<String, List<?>>>(resultado, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Obtener instituciones ope por status.
	 *
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene instituciones por status", notes = "Obtiene instituciones registradas para interoperar por status")
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
	
	@RequestMapping(value = "/interop/obtenerInstitucioniesOpePorStatus", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> obtenerInstitucionesOpePorStatus() throws Exception {

		try {

			Map<String, List<?>> lstInstOpe = new HashMap<String, List<?>>();

			// Obtines Lista de Instituciones Registradas para Inteorperar
			List<Institucion> lstInstRegistradas = getInstitucionesOpeRegistradas();

			// Lllenado de parametros a enviar al metodo getInstitucionesOpeByStatus
			List<StatusInstitucionOpe> lstStatusInstOpe = new ArrayList<>();
			lstStatusInstOpe.add(StatusInstitucionOpe.SOLICITUD_ENVIADA);
			lstStatusInstOpe.add(StatusInstitucionOpe.DIRECTORIO);
			lstStatusInstOpe.add(StatusInstitucionOpe.POR_ACEPTAR);
			lstStatusInstOpe.add(StatusInstitucionOpe.VINCULADA);
			lstStatusInstOpe.add(StatusInstitucionOpe.ACEPTADA);

			// Consulta todas las InstitucionesOpe por los estatus enviados por parametro
			List<InstitucionOpe> lstInstOpePorStatus = getInstitucionesOpeByStatus(lstStatusInstOpe);

			// filtra y obtiene solo institucionesOpe en status solicitud enviada
			List<InstitucionOpe> lstInstOpeSolEnviada = lstInstOpePorStatus.stream()
					.filter(x -> StatusInstitucionOpe.SOLICITUD_ENVIADA.equals(x.getEstatus()))
					.collect(Collectors.toList());

			// filtra y obtiene solo institucionesOpe en status directorio
			List<InstitucionOpe> lstInstOpeDirectorio = lstInstOpePorStatus.stream()
					.filter(x -> StatusInstitucionOpe.DIRECTORIO.equals(x.getEstatus())).collect(Collectors.toList());

			// filtra y obtiene solo institucionesOpe en status por aceptar
			List<InstitucionOpe> lstInstOpePorAceptar = lstInstOpePorStatus.stream()
					.filter(x -> StatusInstitucionOpe.POR_ACEPTAR.equals(x.getEstatus())).collect(Collectors.toList());

			// filtra y obtiene solo institucionesOpe en status por Vinculada
			List<InstitucionOpe> lstInstOpeVinculada = lstInstOpePorStatus.stream()
					.filter(x -> StatusInstitucionOpe.VINCULADA.equals(x.getEstatus())
							|| StatusInstitucionOpe.ACEPTADA.equals(x.getEstatus()))
					.collect(Collectors.toList());

			// Inserta todas las listas obtenidas para devolver en el request
			lstInstOpe.put("instOpeDirectorio", lstInstOpeDirectorio);
			lstInstOpe.put("instOpeSolicitudEnviada", lstInstOpeSolEnviada);
			lstInstOpe.put("instOpeVinculadas", lstInstOpeVinculada);
			lstInstOpe.put("instOpeRegistradas", lstInstRegistradas);
			lstInstOpe.put("instOpePoAceptar", lstInstOpePorAceptar);

			return new ResponseEntity<Map<String, List<?>>>(lstInstOpe, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Instituciones suscripcion instancia.
	 *
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene institucion suscripcion instancia", notes = "Obtiene el detalle de las instituciones registradas")
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
	@RequestMapping(value = "/interop/institucionesSuscripcionInstancia", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Institucion>> institucionesSuscripcionInstancia() throws Exception {

		try {

			List<Institucion> lstInstitucion = new ArrayList<>();

			// Se debe refactorizar e implementar de forma nativa en hibernate
			String query = "select I.IDINSTITUCION from INSTITUCIONES I "
					+ " where activosn = 'S' AND interoperasn = 'N' "
					+ " and i.idinstitucion not in (select nvl(valor, '') from parametrosapp "
					// INTITUCIONES EXCLUIDAS
					+ " where idclave in ('INST_ID_INTER', 'IDCIUDPROMOTOR', 'IDEMPPROMOTOR' ) ) "
					// INSTITUCIONES OPE EN ESTADO DE PENDIENTE EXCLUIDAS
					+ " and i.idinstitucion not in (select i2.idinstitucion from instituciones i2, instituciones_ope io2 where io2.uri = i2.uri and io2.endpoint = i2.endpoint and io2.estatus  = 'P' ) "
					+ " and I.IDTIPOINSTITUCION='E' " + " order by DESCRIPCION ";

			List<BigDecimal> items = (List<BigDecimal>) mngrInstitucion.execNativeQuery(query, null);

			// Obtener lista de idInstitucion en Integer
			List<Integer> lstIds = items.stream().map(i -> i.intValue()).collect(Collectors.toList());

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.in("idInstitucion", lstIds));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lstInstitucion = (List<Institucion>) mngrInstitucion.search(restrictions, orders);

			return new ResponseEntity<List<Institucion>>(lstInstitucion, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Procesar match.
	 *
	 * @param idInstitucion    the id institucion
	 * @param idInstitucionOpe the id institucion ope
	 * @param statusInstOpe    the status inst ope
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Procesar match", notes = "Valida si la institucion realiza el match con la institucion a interoperar")
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
	@RequestMapping(value = "/interop/procesarMatch", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Institucion> procesarMatch(
			@RequestParam(value = "idInstitucion", required = true) Serializable idInstitucion,
			@RequestParam(value = "idInstitucionOpe", required = true) Serializable idInstitucionOpe,
			@RequestParam(value = "statusInstOpe", required = true) Serializable statusInstOpe) throws Exception {

		Institucion institucion = new Institucion();
		try {

			StatusInstitucionOpe statusOpe = StatusInstitucionOpe.valueOf((String) statusInstOpe);

			institucion = mngrInstitucion.fetch(Integer.valueOf((String) idInstitucion));
			InstitucionOpe institucionOpe = mngrInstitucionOpe.fetch(Integer.valueOf((String) idInstitucionOpe));

			if (null != institucion && null != institucionOpe) {

				HashMap<String, Object> params = new HashMap<>();
				params.put("idInstitucionOPE", institucionOpe.getIdInstitucionOpe());
				params.put("status", statusOpe.getValue());
				params.put("idInstitucion", institucion.getIdInstitucion());

				if (StatusInstitucionOpe.POR_ACEPTAR.equals(statusOpe)) {
					// Valida secondVal

					List<InstitucionOpe> lstInstOpe = null;
					if (StringUtils.isNotBlank(institucion.getEndpoint())
							&& StringUtils.isNotBlank(institucion.getUri())) {
						// * * * * * * * * * * * * * * * * * * * * * *
						List<Criterion> restrictions = new ArrayList<Criterion>();
						restrictions.add(
								EscapedLikeRestrictions.ilike("endpoint", institucion.getEndpoint(), MatchMode.EXACT));
						restrictions.add(EscapedLikeRestrictions.ilike("uri", institucion.getUri(), MatchMode.EXACT));

						// * * * * * * * * * * * * * * * * * * * * * *
						lstInstOpe = (List<InstitucionOpe>) mngrInstitucionOpe.search(restrictions, null);
					}

					if (null != lstInstOpe && !lstInstOpe.isEmpty()
							&& !lstInstOpe.get(0).getIdInstitucionOpe().equals(institucionOpe.getIdInstitucionOpe())) {
						throw new BadRequestException("Ya existe un elemento que coincide en Uri/Endopoint.");
					} else {

						// Se ejecuta el Match. Sustitulle llamda al procedure INSTITUCIONMATCHINSTOPE
						institucion = matchInstitucion(institucion, institucionOpe);
						institucion = institucionController.save(institucion).getBody();

						// Se comenta porque se sutituye por hacer la actualizacion directa en el objeto
						// institucion
						// mngrInstitucionOpe.execUpdateQuery("procesarMatchInstOpe", params);
						//
						// String query = String.format(
						// " select i.idinstitucion from instituciones i, instituciones_ope io where
						// io.endpoint = i.endpoint and io.uri = i.uri and io.id = '%s' ",
						// institucionOpe.getIdInstitucionOpe());
						//
						// // Se optiene el id de la institución a la que se le aplicó el match
						// List<BigDecimal> items_ = (List<BigDecimal>)
						// mngrInstitucion.execNativeQuery(query, null);
						//
						// // Obtener lista de idInstitucion en String
						// List<String> lstIdsInstAceptadas = items_.stream().map(i -> i.toString())
						// .collect(Collectors.toList());

						// Se optiene una lista del id de la institución a la que se le aplicó el match
						List<String> lstIdsInstAceptadas = new ArrayList<>();
						lstIdsInstAceptadas.add(institucion.getIdInstitucion().toString());

						// Se llama al webServices ws2
						InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();
						service.respuestaSuscripcionInstancias(lstIdsInstAceptadas, true);

						// Se actualiza el estatus, equivalene a llamar al procedure institucionOpe
						// institucionope_del
						institucionOpe.setEstatus(StatusInstitucionOpe.ACEPTADA);
						mngrInstitucionOpe.update(institucionOpe);

						// se actualiza la intitucion Interopera SI.
						institucion.setInteropera(true);
						mngrInstitucion.update(institucion);

					}

				} else if (StatusInstitucionOpe.DIRECTORIO.equals(statusOpe)) {
					if (StringUtils.isBlank(institucion.getUri()) && StringUtils.isBlank(institucion.getEndpoint())) {

						// Se ejecuta el Match. Sustitulle llamda al procedure INSTITUCIONMATCHINSTOPE
						institucion = matchInstitucion(institucion, institucionOpe);
						institucion = institucionController.save(institucion).getBody();

						// Se actualiza el estatus de la institucion ope vinculada
						institucionOpe.setEstatus(StatusInstitucionOpe.VINCULADA);
						mngrInstitucionOpe.update(institucionOpe);
						// mngrInstitucionOpe.execUpdateQuery("procesarMatchInstOpe", params);

					} else {

						throw new BadRequestException("La institución ya ha realizado match con otra institucion ope.");
					}
				} else {
					return new ResponseEntity<Institucion>(new Institucion(), HttpStatus.BAD_REQUEST);
				}
				// Vuelve a consultar porque el procedure actualiza la institucion
				institucion = mngrInstitucion.fetch(Integer.valueOf((String) idInstitucion));
				return new ResponseEntity<Institucion>(institucion, HttpStatus.OK);
			} else {
				return new ResponseEntity<Institucion>(new Institucion(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Match institucion.
	 *
	 * @param institucionLocal the institucion local
	 * @param institucionOpe   the institucion ope
	 * @return the institucion
	 */
	private Institucion matchInstitucion(Institucion institucionLocal, InstitucionOpe institucionOpe) {

		institucionLocal.setDescripcion(institucionOpe.getNombre());
		institucionLocal.setUri(institucionOpe.getUri());
		institucionLocal.setEndpoint(institucionOpe.getEndpoint());
		institucionLocal.setClave(institucionOpe.getNombreCorto());
		institucionLocal.setAbreviatura(institucionOpe.getNombreCorto());

		return institucionLocal;
	}

	/**
	 * Rechazar solicitud ope.
	 *
	 * @param idInstitucionOpe the id institucion ope
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Rechazar solicitud Ope", notes = "Rechaza una solicitud de la ope")
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
	
	@RequestMapping(value = "/interop/rechazarSolicitudOpe", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InstitucionOpe> rechazarSolicitudOpe(
			@RequestParam(value = "idInstitucionOpe", required = true) Serializable idInstitucionOpe) throws Exception {

		try {

			InstitucionOpe institucionOpe = mngrInstitucionOpe.fetch(Integer.valueOf((String) idInstitucionOpe));

			if (null != institucionOpe) {

				List<String> lstIdsInstRechazadas = new ArrayList<String>();
				lstIdsInstRechazadas.add(institucionOpe.getIdInstitucionOpe().toString());

				// Se llama al webServices ws2

				// Se comentan las 2 lineas siguientes para efectos de pruebas
				InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();
				service.respuestaSuscripcionInstancias(lstIdsInstRechazadas, false);

				// Se actualiza la institucionOpe es el equivalente a llamr al procedure
				// INSTITUCIONOPE_RECHAZAR
				institucionOpe.setEstatus(StatusInstitucionOpe.DIRECTORIO);
				mngrInstitucionOpe.update(institucionOpe);

			} else {
				return new ResponseEntity<InstitucionOpe>(institucionOpe, HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<InstitucionOpe>(institucionOpe, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Solicitud interoperar inst ope.
	 *
	 * @param idInstitucion the id institucion
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interop/solicitudInteroperarInstOpe", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> solicitudInteroperarInstOpe(
			@RequestParam(value = "idInstitucion", required = true) Serializable idInstitucion) throws Exception {

		try {

			Institucion institucion = mngrInstitucion.fetch(Integer.valueOf((String) idInstitucion));

			if (null != institucion && StringUtils.isNotBlank(institucion.getUri())
					&& StringUtils.isNotBlank(institucion.getEndpoint())) {

				// Se eleimina porque se implementa la forma nativa hibernate
//				String query = String.format(
//						" select id from instituciones_ope, (select uri, endpoint from instituciones where idinstitucion = %s ) "
//								+ " inst where instituciones_ope.uri = inst.uri and instituciones_ope.endpoint = inst.endpoint "
//								+ " and instituciones_ope.estatus in ( 'M', 'A' )",
//						institucion.getIdInstitucion());
//
//				// Se ejecuta el query para validar si la institucion se hizo match
//				List<BigDecimal> validarInstitucionMatched = (List<BigDecimal>) mngrInstitucionOpe
//						.execNativeQuery(query, null);

				// Se filtra para saber si existe una institucion ope con la que se haya
				// vinculado
				// * * * * * * * * * * * * * * * * * * * * * *
				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(EscapedLikeRestrictions.ilike("endpoint", institucion.getEndpoint(), MatchMode.EXACT));
				restrictions.add(EscapedLikeRestrictions.ilike("uri", institucion.getUri(), MatchMode.EXACT));
				// * * * * * * * * * * * * * * * * * * * * * *
				List<InstitucionOpe> resultInstOpeVinculada = (List<InstitucionOpe>) mngrInstitucionOpe
						.search(restrictions);

				// Se valida que exita una institucionOpe con la que se haya vinculado
				if (null != resultInstOpeVinculada && !resultInstOpeVinculada.isEmpty()
						&& resultInstOpeVinculada.size() == 1) {

					// Se optiene la institucionOpe con la que se viculo
					InstitucionOpe institucionOpeVinculada = resultInstOpeVinculada.get(0);

					// Se llena el parametro para el ws2
					List<String> institucionesDestino = new ArrayList<String>();
					institucionesDestino.add(institucion.getIdInstitucion().toString());

					// Se llama al webServices ws2 para enviar mensaje de Solicitud de
					// interoperabilidad
					InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();
					service.SolicitarSuscripcionInstancias(institucionesDestino);

					// Se actualiza institucionOpe, es el equivalente a ejecutar el procedure
					// INSTITUCIONOPE_PENDIENTE
					institucionOpeVinculada.setEstatus(StatusInstitucionOpe.SOLICITUD_ENVIADA);
					mngrInstitucionOpe.update(institucionOpeVinculada);

				} else {
					throw new BadRequestException(
							"La institución seleccionada no ha sido sincronizada con ninguna institución del Directorio.");
				}

			} else {
				return new ResponseEntity<String>("fail", HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<String>("Ok", HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Begin firma mensaje.
	 *
	 * @param idAsunto       the id asunto
	 * @param contentB64     the content B 64
	 * @param certificadoB64 the certificado B 64
	 * @param email          the email
	 * @return the map
	 * @throws Exception the exception
	 */
	private Map<String, Object> beginFirmaMensaje(Integer idAsunto, String contentB64, String certificadoB64,
			String email, String algoritmoFirma) throws Exception {

		try {

			Map<String, Object> result = new HashMap<String, Object>();
			// ---- UPLOAD
			Map<String, Object> uploadIdResponse = firmaEndPoint.uploadFile(//
					contentB64, //
					"mensaje.xml", //
					TipoFirma.OFICIO_AUTOR, //
					SignContentType.OFICIO, null);

			log.debug("IdDocumento >> " + uploadIdResponse.get("IdDocumento"));
			Integer uploadId = Integer.parseInt(uploadIdResponse.get("IdDocumento").toString());

			// ---- INICIAR FIRMA
			// para certificados pem
			certificadoB64 = certificadoB64.replace("-----BEGIN CERTIFICATE-----", "")
					.replace("-----END CERTIFICATE-----", "").trim();

			Map<String, Object> responseStartSign = firmaEndPoint.startSign(uploadId, email, certificadoB64,
					TipoFirma.OFICIO_AUTOR, SignContentType.OFICIO, algoritmoFirma, null, null, null, null, null, null);

			byte[] encodedHexB64 = Base64.decodeBase64(responseStartSign.get("HashArchivo").toString());
			String hashFileHex = Hex.encodeHexString(encodedHexB64);

			result.put("uploadId", uploadId);
			result.put("HashArchivo", hashFileHex);

			return result;

		} catch (Exception e) {

			log.error(e.getMessage());

			throw e;
		}

	}

	/**
	 * Validar firma mensaje process.
	 *
	 * @param uploadId the upload id
	 * @param email    the email
	 * @param certB64  the cert B 64
	 * @param firmaB64 the firma B 64
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean validarFirmaMensajeProcess(Integer uploadId, String email, String certB64, String firmaB64,
			String algoritmoFirma) throws Exception {

		try {

			Map<String, Object> firmaValida = firmaEndPoint.validateSign(uploadId, email, certB64, firmaB64,
					TipoFirma.OFICIO_AUTOR, SignContentType.OFICIO, algoritmoFirma);

			log.debug("firmaValida >> " + firmaValida.get("isValid"));

			return firmaValida.get("isValid").equals(true) ? true : false;

		} catch (Exception e) {

			log.error(e.getMessage());

			throw e;
		}

	}

	/**
	 * Gets the instituciones ope registradas.
	 *
	 * @return the instituciones ope registradas
	 */
	@SuppressWarnings("unchecked")
	private List<Institucion> getInstitucionesOpeRegistradas() {
		List<Institucion> lstInstitucionOpeRegistrada = new ArrayList<Institucion>();

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("activo", Boolean.TRUE));
			restrictions.add(Restrictions.eq("interopera", Boolean.TRUE));
			restrictions.add(Restrictions.eq("tipo", "E"));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lstInstitucionOpeRegistrada = (List<Institucion>) mngrInstitucion.search(restrictions, orders);

			log.debug("Size found >> " + lstInstitucionOpeRegistrada.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return lstInstitucionOpeRegistrada;
	}

	/**
	 * Gets the instituciones ope by status.
	 *
	 * @param lstStatusInstOpe the lst status inst ope
	 * @return the instituciones ope by status
	 */
	@SuppressWarnings("unchecked")
	private List<InstitucionOpe> getInstitucionesOpeByStatus(List<StatusInstitucionOpe> lstStatusInstOpe) {
		List<InstitucionOpe> lstInstitucionOpe = new ArrayList<InstitucionOpe>();

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (null != lstStatusInstOpe && !lstStatusInstOpe.isEmpty()) {
				restrictions.add(Restrictions.in("estatus", lstStatusInstOpe));
			}

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("nombre"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lstInstitucionOpe = (List<InstitucionOpe>) mngrInstitucionOpe.search(restrictions, orders);

			log.debug("Size found >> " + lstInstitucionOpe.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return lstInstitucionOpe;
	}

	/**
	 * Gets the tree institucion local.
	 *
	 * @param idInstitucionOpe the id institucion ope
	 * @return the tree institucion local
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene institucion local", notes = "Obtiene el arbol de la institucion local")
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
	@RequestMapping(value = "/interop/treeInstitucionLocal", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TreeNode<Object>> getTreeInstitucionLocal(
			@RequestParam(value = "idInstitucionOpe", required = true) Serializable idInstitucionOpe) throws Exception {
		try {

			Integer idInstitucionLocal = null;

			{// Obtener institucion local ya sincronizada por idInstitucionOpe

				WsSincronizaCompletaDetalle institucionOpe = mngrWsSincronizaCompletaDetalle
						.fetch(Integer.valueOf((String) idInstitucionOpe));

				if (null == institucionOpe) {
					throw new BadRequestException("No Existe la institucion Ope");
				}

				// * * * * * * * * * * * * * * * * * * * * * *
				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("idExterno", institucionOpe.getNoDistinguido()));

				// * * * * * * * * * * * * * * * * * * * * * *
				List<Institucion> lstInstitucionLocal = (List<Institucion>) mngrInstitucion.search(restrictions);

				if (null != lstInstitucionLocal && !lstInstitucionLocal.isEmpty() && lstInstitucionLocal.size() == 1) {
					idInstitucionLocal = lstInstitucionLocal.get(0).getIdInstitucion();
				} else {
					restrictions = new ArrayList<Criterion>();
					restrictions.add(Restrictions.eq("descripcion", institucionOpe.getNombre()));
					restrictions.add(Restrictions.eq("uri", institucionOpe.getCustom5()));
					lstInstitucionLocal = (List<Institucion>) mngrInstitucion.search(restrictions);

					if (null != lstInstitucionLocal && !lstInstitucionLocal.isEmpty()
							&& lstInstitucionLocal.size() == 1) {
						idInstitucionLocal = lstInstitucionLocal.get(0).getIdInstitucion();
					}

				}
			}

			TreeNode<Object> top;
			if (null != idInstitucionLocal) {

				Institucion institucion = mngrInstitucion.fetch(idInstitucionLocal);

				if (null != institucion && Boolean.TRUE.equals(institucion.getActivo())
						&& "E".equals(institucion.getTipo())) {
					top = new TreeNode<Object>(institucion);
					// * * * * * * * * * * * *
					getChildAreasLocal(top, institucion.getIdInstitucion(), null);

					// * * * * * * * * * * * *
				} else {
					return new ResponseEntity<TreeNode<Object>>(new TreeNode<Object>(null), HttpStatus.OK);

				}
			} else {
				throw new NotAcceptableException(
						"La institucion ope seleccionada no ha sincronizada con alguna institucion local previamente");
			}

			return new ResponseEntity<TreeNode<Object>>(top, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the tree institucion ope.
	 *
	 * @param id the id
	 * @return the tree institucion ope
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene institucion ope", notes = "Obtiene el arbol de la institucion ope")
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
	
	@RequestMapping(value = "/interop/treeInstitucionOpe", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TreeNode<Object>> getTreeInstitucionOpe(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {
		try {

			WsSincronizaCompletaDetalle institucionOpe = mngrWsSincronizaCompletaDetalle
					.fetch(Integer.valueOf((String) id));
			TreeNode<Object> top;

			if (null != institucionOpe
					&& TipoRegistroWsOpe.REGISTRO_INSTITUCION.equals(institucionOpe.getTipoRegistro())) {
				top = new TreeNode<Object>(institucionOpe);

				String uriInstitucion = institucionOpe.getCustom5();
				getChildAreasOpe(top, institucionOpe.getIdRegistro(), uriInstitucion);

			} else {
				return new ResponseEntity<TreeNode<Object>>(new TreeNode<Object>(null), HttpStatus.OK);

			}

			return new ResponseEntity<TreeNode<Object>>(top, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the child areas local.
	 *
	 * @param nodeTop       the node top
	 * @param idInstitucion the id institucion
	 * @param idAreaPadre   the id area padre
	 * @return the child areas local
	 */
	@SuppressWarnings("unchecked")
	private void getChildAreasLocal(TreeNode<Object> nodeTop, Integer idInstitucion, Integer idAreaPadre) {

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<>();

			if (null != idInstitucion) {
				restrictions.add(Restrictions.eq("institucion.idInstitucion", idInstitucion));
			}
			if (null == idAreaPadre) {
				restrictions.add(Restrictions.isNull("idAreaPadre"));
			} else {
				restrictions.add(Restrictions.eq("idAreaPadre", idAreaPadre));
			}
			restrictions.add(Restrictions.eq("activo", Boolean.TRUE));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Area> lstAreas = (List<Area>) mngrArea.search(restrictions);

			for (Area area : lstAreas) {
				TreeNode<Object> nodeArea = new TreeNode<Object>(area);

				getChildUsuariosLocal(nodeArea, area.getIdArea());

				if (hashSubAreasLocal(area.getIdArea())) {
					// getChildSubAreasLocal(nodeArea, area.getIdArea());
					getChildAreasLocal(nodeArea, null, area.getIdArea());
				}

				nodeTop.add(nodeArea);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Gets the child areas ope.
	 *
	 * @param nodeTop        the node top
	 * @param idInstitucion  the id institucion
	 * @param uriInstitucion the uri institucion
	 * @return the child areas ope
	 */
	@SuppressWarnings("unchecked")
	private void getChildAreasOpe(TreeNode<Object> nodeTop, Integer idInstitucion, String uriInstitucion) {

		try {

			String query = String.format(
					" select wcd.IDREGISTRO from wssincronizacompletadetalle wcd join wssincronizacompleta wc "
							+ " on wc.idarea = wcd.identificador where wcd.tiregistro = 'A'"
							+ " and wcd.idpadre in ( select identificador from wssincronizacompletadetalle "
							+ " where idregistro = %s) and upper(wc.uri) = upper('%s') ",
					idInstitucion, uriInstitucion);

			List<BigDecimal> items = (List<BigDecimal>) mngrWsSincronizaCompletaDetalle.execNativeQuery(query, null);
			List<Integer> idsRegistro = items.stream().map(i -> i.intValue()).collect(Collectors.toList());

			if (null != idsRegistro && !idsRegistro.isEmpty()) {
				// * * * * * * * * * * * * * * * * * * * * * *
				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions.in("idRegistro", idsRegistro));

				// * * * * * * * * * * * * * * * * * * * * * *
				List<WsSincronizaCompletaDetalle> areasOpeHijas = (List<WsSincronizaCompletaDetalle>) mngrWsSincronizaCompletaDetalle
						.search(restrictions);

				for (WsSincronizaCompletaDetalle areaOpe : areasOpeHijas) {
					TreeNode<Object> nodeArea = new TreeNode<Object>(areaOpe);

					getChildUsuariosOpe(nodeArea, areaOpe.getIdRegistro(), uriInstitucion);

					if (hashSubAreasOpe(areaOpe.getIdRegistro())) {
						getChildAreasOpe(nodeArea, areaOpe.getIdRegistro(), uriInstitucion);
					}
					nodeTop.add(nodeArea);
				}
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Gets the child usuarios local.
	 *
	 * @param nodeChildArea the node child area
	 * @param idArea        the id area
	 * @return the child usuarios local
	 */
	@SuppressWarnings("unchecked")
	private void getChildUsuariosLocal(TreeNode<Object> nodeChildArea, Integer idArea) {

		try {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<>();

			restrictions.add(Restrictions.eq("area.idArea", idArea));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Representante> lstUsers = (List<Representante>) mngrRepresentante.search(restrictions);

			for (Representante usuario : lstUsers) {
				TreeNode<Object> nodeUsuario = new TreeNode<Object>(usuario);
				nodeChildArea.add(nodeUsuario);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the child usuarios ope.
	 *
	 * @param nodeChildArea  the node child area
	 * @param idArea         the id area
	 * @param uriInstitucion the uri institucion
	 * @return the child usuarios ope
	 */
	@SuppressWarnings("unchecked")
	private void getChildUsuariosOpe(TreeNode<Object> nodeChildArea, Integer idArea, String uriInstitucion) {

		try {
			String query = String.format(" select IDREGISTRO from wssincronizacompletadetalle where tiregistro = 'U' "
					+ " and idpadre in ( select identificador from wssincronizacompletadetalle "
					+ " where idregistro = %s ) and upper(custom5) = upper('%s') ", idArea, uriInstitucion);

			List<BigDecimal> items = (List<BigDecimal>) mngrWsSincronizaCompletaDetalle.execNativeQuery(query, null);
			List<Integer> idsRegistro = items.stream().map(i -> i.intValue()).collect(Collectors.toList());

			if (null != idsRegistro && !idsRegistro.isEmpty()) {

				// * * * * * * * * * * * * * * * * * * * * * *
				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions.in("idRegistro", idsRegistro));

				// * * * * * * * * * * * * * * * * * * * * * *
				List<WsSincronizaCompletaDetalle> lstUsers = (List<WsSincronizaCompletaDetalle>) mngrWsSincronizaCompletaDetalle
						.search(restrictions);

				for (WsSincronizaCompletaDetalle usuario : lstUsers) {
					TreeNode<Object> nodeUsuario = new TreeNode<Object>(usuario);
					nodeChildArea.add(nodeUsuario);
				}
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Hash sub areas local.
	 *
	 * @param idArea the id area
	 * @return the boolean
	 */
	private Boolean hashSubAreasLocal(Integer idArea) {
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<>();

			restrictions.add(Restrictions.eq("idAreaPadre", idArea));
			restrictions.add(Restrictions.eq("activo", Boolean.TRUE));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<?> lstSubAreas = mngrArea.search(restrictions);

			return null != lstSubAreas && !lstSubAreas.isEmpty() ? true : false;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Hash sub areas ope.
	 *
	 * @param idArea the id area
	 * @return the boolean
	 */
	@SuppressWarnings("unchecked")
	private Boolean hashSubAreasOpe(Integer idArea) {
		try {

			Boolean result = false;
			String query = String.format("select count(identificador) from wssincronizacompletadetalle "
					+ " where tiregistro = 'A' and idpadre in ( select identificador from wssincronizacompletadetalle where idregistro = %s )",
					idArea);

			List<BigDecimal> subAreas = (List<BigDecimal>) mngrWsSincronizaCompletaDetalle.execNativeQuery(query, null);

			if (null != subAreas && !subAreas.isEmpty() && subAreas.get(0).intValue() > 0) {
				result = true;
			}
			return result;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the instituciones ws sincroniza.
	 *
	 * @return the instituciones ws sincroniza
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtener instituciones", notes = "Obtiene el arbol de instituciones de la ope")
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
	
	@RequestMapping(value = "/interop/ObtenerInstitucionesArbolOpe", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> getInstitucionesWsSincroniza() throws Exception {

		try {

			List<?> lstInstWsSincroniza = new ArrayList<WsSincronizaCompletaDetalle>();

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.isNull("idPadre"));
			restrictions.add(Restrictions.eq("tipoRegistro", TipoRegistroWsOpe.REGISTRO_INSTITUCION));
			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("nombre"));
			// * * * * * * * * * * * * * * * * * * * * * *
			lstInstWsSincroniza = mngrWsSincronizaCompletaDetalle.search(restrictions, orders);
			log.debug("Size found >> " + lstInstWsSincroniza.size());

			return new ResponseEntity<List<?>>(lstInstWsSincroniza, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

}