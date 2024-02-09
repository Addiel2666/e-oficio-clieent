/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.impl.async.FirmarAsyncProcess;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.model.util.TipoFirma;
import com.ecm.sigap.firma.FirmaCore;
import com.ecm.sigap.firma.model.FirmaImpresaTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de Firma Digital.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class FirmaController extends FirmaCore {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(FirmaController.class);

	/**
	 * Referencia hacia el REST controller {@link RepositoryController}.
	 */
	@Autowired
	private RepositoryController repositorioController;

	/** Referencia hacia el controller {@link FirmarAsyncProcess}. */
	@Autowired
	private FirmarAsyncProcess firmarAsyncProcess;

	/**
	 * Obtiene los tipos de firma aplicables a un documento en base a las firmas ya
	 * aplicadas a este.
	 *
	 * @param objectId            the object id
	 * @param isAntefirmaMejorada the is antefirma mejorada
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene tipo firmas aplicables", notes = "Obtiene los tipos de firmas aplicables a un documento")
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

	@RequestMapping(value = "/firmasAplicables", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<TipoFirma>> firmasAplicables(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "isAntefirmaMejorada", required = false, defaultValue = "false") String isAntefirmaMejorada)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			Usuario usuario = mngrUsuario.fetch(userId);

			log.debug(" GET :: /firmasAplicables " + objectId);

			List<TipoFirma> result = new ArrayList<TipoFirma>();

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			String fileName = endpoint.getObjectName(objectId);

			log.debug(" >> " + objectId + " nombre >> " + fileName);

			Boolean isVersionable = isAnexoVersionable(fileName);

			log.debug(" >> " + objectId + " isVersionable >> " + isVersionable);

			List<TipoFirma> firmasOmitir = new ArrayList<>();

			if (Boolean.parseBoolean(isAntefirmaMejorada)) {

				// calcular tipos de firma q se vana a exlcuir en antefirma,
				String tiposOmitir = environment.getProperty("antefirma.tiposOmitir");
				if (StringUtils.isNotBlank(tiposOmitir)) {
					for (String tipo_ : tiposOmitir.split(",")) {
						firmasOmitir.add(TipoFirma.fromString(tipo_));
					}
				}

			}

			if (isVersionable) {

				Boolean is_signed = isFirmado(objectId);

				boolean has_PDF_CLASIFICACION = false;
				boolean has_PDF_DESCLASIFICACION = false;
				boolean has_PDF_FIRMA = false;
				boolean has_PDF_PDF_ANTEFIRMA = false;

				if (is_signed) {

					List<?> sad_tipo_firma = (List<?>) endpoint.getObjectProperty(objectId,
							environment.getProperty("fieldTipoFirma"));

					List<?> sad_firmante = (List<?>) endpoint.getObjectProperty(objectId,
							environment.getProperty("fieldFirmante"));

					TipoFirma tipo_;
					String firmante_;

					for (int index = 0; index < sad_tipo_firma.size(); index++) {

						firmante_ = (String) sad_firmante.get(index);
						tipo_ = TipoFirma.fromString((String) sad_tipo_firma.get(index));

						if (!firmante_.equalsIgnoreCase(usuario.getEmail())) {
							continue;
						}

						switch (tipo_) {
						case PDF_CLASIFICACION:
							has_PDF_CLASIFICACION = true;
							break;
						case PDF_DESCLASIFICACION:
							has_PDF_DESCLASIFICACION = true;
							break;
						case PDF_FIRMA:
							has_PDF_FIRMA = true;
							break;
						case PDF_ANTEFIRMA:
							has_PDF_PDF_ANTEFIRMA = true;
							break;
						default:
							break;
						}

						log.debug(sad_tipo_firma);
					}

				}

				for (TipoFirma t : TipoFirma.values()) {

					if (t == TipoFirma.PDF_MULTISIGN)

						result.add(t);

					else if (((t == TipoFirma.PDF_ANTEFIRMA) && has_PDF_PDF_ANTEFIRMA) //
							|| ((t == TipoFirma.PDF_ANTEFIRMA) && has_PDF_FIRMA) //
							|| ((t == TipoFirma.PDF_CLASIFICACION) && has_PDF_CLASIFICACION) //
							|| ((t == TipoFirma.PDF_DESCLASIFICACION) && has_PDF_DESCLASIFICACION))

						log.debug(" Tipo de firma :: " + t.getTipo() + " ya ha sido aplicada. ");

					else if (t == TipoFirma.CMS)

						log.debug(" Tipo de firma CMS no se puede aplicar. ");

					else if (t == TipoFirma.PDF_DESCLASIFICACION)

						if (has_PDF_CLASIFICACION)
							result.add(t);
						else
							log.debug(" Un tipo de firma :: " + t.getTipo() + " similar ya ha sido aplicada. ");

					else if (((t == TipoFirma.PDF_FIRMA) && has_PDF_PDF_ANTEFIRMA)
							|| ((t == TipoFirma.PDF_ANTEFIRMA) && has_PDF_FIRMA))

						log.debug(" Un tipo de firma :: " + t.getTipo() + " similar ya ha sido aplicada. ");

					else
						result.add(t);

				}

				if (!firmasOmitir.isEmpty()) {

					for (TipoFirma tipoFirma : firmasOmitir) {
						if (result.contains(tipoFirma))
							result.remove(tipoFirma);
					}

				}

			} else {

				result.add(TipoFirma.CMS);

			}

			log.debug(" >> " + objectId + " ALL FIRMAS APLICABLES >> " + result.toString());

			return new ResponseEntity<List<TipoFirma>>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtiene la lista de firmas ya aplicadas a un documento.
	 *
	 * @param objectId the object id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta firmas aplicadas", notes = "Obtiene la lista de firmas ya aplicadas a un documento")
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

	@RequestMapping(value = "/firmasAplicadas", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Map<String, Object>>> firmasAplicadas(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {
		try {
			log.debug(" GET :: /firmasAplicadas " + objectId);

			List<Map<String, Object>> firmas = new ArrayList<Map<String, Object>>();

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			Boolean is_signed = isFirmado(objectId);

			if (is_signed) {

				List<?> sad_id_firma = (List<?>) endpoint.getObjectProperty(objectId,
						environment.getProperty("fieldIdentificador"));

				log.debug(" >> " + objectId + " sad_id_firma ?? " + is_signed);

				List<?> sad_firma = (List<?>) endpoint.getObjectProperty(objectId,
						environment.getProperty("fieldFirma"));

				log.debug(" >> " + objectId + " sad_firma ?? " + sad_firma);

				List<?> sad_fecha_firma = (List<?>) endpoint.getObjectProperty(objectId,
						environment.getProperty("fieldFechaFirma"));

				log.debug(" >> " + objectId + " sad_fecha_firma ?? " + sad_fecha_firma);

				List<?> sad_nombre_firma = (List<?>) endpoint.getObjectProperty(objectId,
						environment.getProperty("fieldFirmante"));

				log.debug(" >> " + objectId + " sad_nombre_firma ?? " + sad_nombre_firma);

				List<?> sad_hash_archivo = (List<?>) endpoint.getObjectProperty(objectId,
						environment.getProperty("fieldHashArchivo"));

				log.debug(" >> " + objectId + " sad_hash_archivo ?? " + sad_hash_archivo);

				List<?> sad_tipo_firma = (List<?>) endpoint.getObjectProperty(objectId,
						environment.getProperty("fieldTipoFirma"));

				log.debug(" >> " + objectId + " sad_tipo_firma ?? " + sad_tipo_firma);

				Map<String, Object> firma;

				for (int i = 0; i <= sad_id_firma.size(); i++) {
					firma = new HashMap<String, Object>();
					try {
						firma.put("sad_id_firma", sad_id_firma.get(i));
						firma.put("sad_firma", sad_firma.get(i));
						firma.put("sad_fecha_firma", sad_fecha_firma.get(i));
						firma.put("sad_nombre_firma", sad_nombre_firma.get(i));
						firma.put("sad_hash_archivo", sad_hash_archivo.get(i));
						firma.put("sad_tipo_firma", sad_tipo_firma.get(i));
					} catch (Exception e) {
					}
					firmas.add(firma);
				}

				log.debug(" >> " + objectId + " FIRMAS APLICADAS >>  " + firmas);

			} else {

				log.warn(" >> " + objectId + " DOCUMENTO NO FIRMADO !!! ");

			}

			return new ResponseEntity<List<Map<String, Object>>>(firmas, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Asigna un status tipo {@link TipoFirma} a un documento de un asunto.
	 *
	 * @param objectId Object ID del documento a marcar
	 * @param status   Estatus a marcar {@link StatusFirmaDocumento}
	 * @param idAsunto Identificador del Asunto asociado al documento
	 * @return Documento marcado
	 * @throws Exception Cualquier error al momento de ejecuta el metodo
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Asignar estado documento asunto", notes = "Asigna un estado a un documento de un asunto")
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

	@RequestMapping(value = "/statusDocumentoAsunto", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DocumentoAsunto> marcarDocumentoAsunto(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "status", required = true) String status,
			@RequestParam(value = "idAsunto", required = true) Integer idAsunto) throws Exception {

		log.debug("Marcando el documento con objectId " + objectId + " del asunto " + idAsunto);

		DocumentoAsunto documento = marcarDocumentoAsuntoProcess(objectId, status, idAsunto);

		if (StringUtils.isBlank(status)) {
			documento.setFechaMarca(null);
			mngrDocsAsunto.update(documento);
		} else if (StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status)
				|| StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
			documento.setFechaMarca(new Date());
			mngrDocsAsunto.update(documento);
		} else {
			try {
				mngrDocsAsunto.updateBitacora(documento);
			}catch (Exception e) {
				// TODO: handle exception
				log.error("Error el auxiliar");
			}
		}

		return new ResponseEntity<DocumentoAsunto>(documento, HttpStatus.OK);
	}

	/**
	 * Asigna un status tipo {@link TipoFirma} a un documento de una respuesta.
	 *
	 * @param objectId the object id
	 * @param status   the status
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Asignar estado documento respuesta", notes = "Asigna un estado a un documento de una respuesta")
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

	@RequestMapping(value = "/statusDocumentoRespuesta", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DocumentoRespuesta> marcarDocumentoRespuesta(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "status", required = false) String status) throws Exception {

		DocumentoRespuesta documento = marcarDocumentoRespuestaProcess(objectId, status);

		if (StringUtils.isBlank(status)) {
			documento.setFechaMarca(null);
			mngrDocsRespuesta.update(documento);
		} else if (StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status)
				|| StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
			documento.setFechaMarca(new Date());
			mngrDocsRespuesta.update(documento);
		} else {
			try {
				mngrDocsRespuesta.updateBitacora(documento);
			}catch (Exception e) {
				// TODO: handle exception
				log.error("Error función de actualizar bitacora");
			}
		}

		return new ResponseEntity<>(documento, HttpStatus.OK);

	}

	/**
	 * Core process de cargarParaFirma.
	 *
	 * @param objectId  the object id
	 * @param tipoFirma the tipo firma
	 * @return the map
	 * @throws Exception               the exception
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException             Signals that an I/O exception has occurred.
	 */
	private Map<String, Object> cargarParaFirmaProcess(String objectId, String tipoFirma)
			throws Exception, ClientProtocolException, IOException {

		ResponseEntity<Map<String, Object>> response = repositorioController.getDocument(objectId, null);

		Map<String, Object> body = response.getBody();

		String fileB64 = body.get("contentB64").toString();
		String fileName = body.get("name").toString();

		TipoFirma tipoFirma_ = TipoFirma.fromString(tipoFirma);

		SignContentType signContentType = isAnexoVersionable(fileName) ? SignContentType.PDF : SignContentType.OFICIO;

		Map<String, Object> uploadId = firmaEndPoint.uploadFile(fileB64, fileName, tipoFirma_, signContentType,
				objectId);

		log.debug("IdDocumento >> " + uploadId.get("IdDocumento"));
		return uploadId;
	}

	/**
	 * Core process de iniciarFirma.
	 *
	 * @param body_ the body
	 * @param user  the user
	 * @return the map
	 * @throws Exception the exception
	 */
	private Map<String, Object> iniciarFirmaProcess(JSONObject body_, Usuario user, String algoritmoFirma)
			throws Exception {
		try {

			String fileName = body_.getString("objectName");
			int coordenadax = 0;
			int coordenaday = 0;
			int onpage = 0;
			String imageB64 = null;
			String nivelUI = null;
			String cargo = body_.isNull("cargo") ? null : body_.getString("cargo");

			TipoFirma tipoFirma_ = TipoFirma.fromString(body_.getString("tipoFirma"));

			SignContentType signContentType = isAnexoVersionable(fileName) ? SignContentType.PDF
					: SignContentType.OFICIO;

			String certificadoB64 = body_.getString("certificadoB64");
			String valtipoFirma = tipoFirma_.getTipo();
			boolean isFirmaAvanzada = valtipoFirma.equalsIgnoreCase("PDF_FIRMA_IMG")
					|| valtipoFirma.equalsIgnoreCase("PDF_ANTEFIRMA") && !body_.isNull("isFirmaAvanzada");

			if (isFirmaAvanzada) {
				imageB64 = body_.getString("firmaImgB64");
				nivelUI = body_.getString("nivelUI");
				String pivotx = body_.getString("x");
				String pivoty = body_.getString("y");
				String pivotpage = body_.getString("onPage");
				float coordenadaxfloat = Float.parseFloat(pivotx);
				float coordenadayfloat = Float.parseFloat(pivoty);
				float onpagefloat = Float.parseFloat(pivotpage);

				coordenadax = (int) coordenadaxfloat;
				coordenaday = (int) coordenadayfloat;
				onpage = (int) onpagefloat;
			}

			// para certificados pem
			certificadoB64 = certificadoB64.replace("-----BEGIN CERTIFICATE-----", "")
					.replace("-----END CERTIFICATE-----", "").trim();

			Map<String, Object> hashFile = null;
			if (!isFirmaAvanzada) {
				hashFile = firmaEndPoint.startSign(//
						body_.getInt("id"), //
						user.getEmail(), //
						certificadoB64, //
						tipoFirma_, //
						signContentType, //
						algoritmoFirma, //
						null, //
						null, //
						null, //
						imageB64, //
						nivelUI, //
						cargo);
			}
			if (isFirmaAvanzada) {
				hashFile = firmaEndPoint.startSign(//
						body_.getInt("id"), //
						user.getEmail(), //
						certificadoB64, //
						tipoFirma_, //
						signContentType, //
						algoritmoFirma, //
						coordenadax, //
						coordenaday, //
						onpage, //
						imageB64, //
						nivelUI, //
						cargo);
			}

			log.debug("HashArchivo >> " + hashFile.get("HashArchivo"));

			byte[] encodedHexB64 = Base64.decodeBase64(hashFile.get("HashArchivo").toString());
			String uuid_hex = Hex.encodeHexString(encodedHexB64);

			hashFile.put("HashArchivoHex", uuid_hex);

			return hashFile;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Servicio de firma optimizado. Step 1/2.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Firma documento paso 1", notes = "El servicio firma el documento")
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
	@RequestMapping(value = "/firmar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> firmar(@RequestBody Map<String, Object> body)
			throws Exception {

		try {

			List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

			String crestificadoB64 = body.get("certificadoB64").toString();
			String algoritmoFirma = body.get("algoritmoFirma").toString();

			Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));
			Map<String, List<?>> resultado = new HashMap<String, List<?>>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();
			List<Map<String, String>> paraFirmar_;

			for (Map<String, String> map : paraFirmar) {

				paraFirmar_ = new ArrayList<Map<String, String>>();
				paraFirmar_.add(map);

				JSONObject documento = new JSONObject(map);
				// conocer los datos con los que cuenta documento

				try {

					if (validarUsuarioFirmaExistente(user, paraFirmar_)) {
						throw new Exception(errorMessages.getString("usuarioYaFirmo"));
					}

					Map<String, Object> cargado = cargarParaFirmaProcess(documento.getString("objectId"),
							documento.getString("tipoFirma"));

					documento.put("IdDocumento", cargado.get("IdDocumento"));
					documento.put("id", cargado.get("IdDocumento"));
					documento.put("certificadoB64", crestificadoB64);

					Map<String, Object> hashed = iniciarFirmaProcess(documento, user, algoritmoFirma);

					documento.put("HashArchivo", hashed.get("HashArchivo"));
					documento.put("HashArchivoHex", hashed.get("HashArchivoHex"));

					Map<String, Object> result = new ObjectMapper().readValue(documento.toString(), HashMap.class);

					listExito.add(result);

				} catch (Exception e) {

					documento.put("failCause", e.getMessage());

					Map<String, Object> result = new ObjectMapper().readValue(documento.toString(), HashMap.class);

					listFail.add(result);

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
	 * Servicio de firma optimizado. Step 2/2.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Firmar documento paso 2", notes = "El servicio firma el documento")
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
	@RequestMapping(value = "/endFirmar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> endFirmar(@RequestBody Map<String, Object> body)
			throws Exception {

		try {

			List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

			String algoritmoFirma = (String) body.get("algoritmoFirma");

			Map<String, List<?>> resultado = new HashMap<String, List<?>>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();

			if (paraFirmar != null && !paraFirmar.isEmpty()) {

				Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));

				ExecutorService taskExecutor = Executors.newFixedThreadPool(20);

				for (Map<String, String> map : paraFirmar) {

					taskExecutor.execute(new EndFirmarLoop(map, user, algoritmoFirma, listExito, listFail));

				}

				taskExecutor.shutdown();

				try {

					taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

				} catch (InterruptedException e) {

				}

			}

			// Actualizamos bitacora
			try {
				listExito.stream().forEach(
					item -> {
						if(null != item.get("newObjectId")) {
							DocumentoAsunto da = new DocumentoAsunto();
							da.setIdAsunto( Integer.valueOf( item.get("idAsunto").toString()) );
							da.setObjectId( item.get("newObjectId").toString() );
							DocumentoAsunto documento = mngrDocsAsunto.fetch( da );
							
							if(null != documento)
								mngrDocsAsunto.updateBitacora(documento);
						} else {
//							DocumentoRespuesta da = new DocumentoRespuesta();
//							da.setIdAsunto( Integer.valueOf( item.get("idAsunto").toString()) );
//							da.setObjectId( item.get("objectId").toString() );
							DocumentoRespuesta documento = mngrDocsRespuesta.fetch( item.get("newObjectIdR").toString()  );
							
							if(null != documento)
								mngrDocsRespuesta.updateBitacora(documento);
						}
						
					}
				);
			} catch (Exception e) {
				log.error("Error:: No se pudo registrar en bitacora la firma del documento.");
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
	 * 
	 * @author jmorales
	 *
	 */
	public class EndFirmarLoop extends Thread {

		/** */
		private Map<String, String> map;
		/** */
		private Usuario user;
		/** */
		private String algoritmoFirma;
		/** */
		private List<Map<String, Object>> listExito;
		/** */
		private List<Map<String, Object>> listFail;

		/**
		 * 
		 * @param map
		 * @param user
		 * @param algoritmoFirma
		 * @param listExito
		 * @param listFail
		 */
		public EndFirmarLoop(Map<String, String> map, Usuario user, String algoritmoFirma,
				List<Map<String, Object>> listExito, List<Map<String, Object>> listFail) {
			this.map = map;
			this.user = user;
			this.algoritmoFirma = algoritmoFirma;
			this.listExito = listExito;
			this.listFail = listFail;
		}

		@SuppressWarnings("unchecked")
		public void run() {

			JSONObject documento = new JSONObject(map);

			try {

				documento.put("firmaHex", documento.getString("firma"));

				Map<String, Object> validado = validarFirmaProcess(documento, user, algoritmoFirma);

				documento.put("isValid", validado.get("isValid"));
				documento.put("HashArchivo", documento.get("HashArchivoHex"));

				Map<String, Object> firmado = aplicarFirmaProcess(documento, user);

				documento.put("result", firmado.get("result"));

				Map<String, Object> result = new ObjectMapper().readValue(documento.toString(), HashMap.class);

				listExito.add(result);

			} catch (Exception e) {

				documento.put("failCause", errorMessages.getString("firmaErrorRetry"));

				Map<String, Object> result = new HashMap<String, Object>();
				try {
					result = new ObjectMapper().readValue(documento.toString(), HashMap.class);
				} catch (JsonParseException e1) {
					log.error(e1.getLocalizedMessage());
				} catch (JsonMappingException e1) {
					log.error(e1.getLocalizedMessage());
				} catch (IOException e1) {
					log.error(e1.getLocalizedMessage());
				}

				listFail.add(result);

			}

		}

	}

	/**
	 * Servicio de firma optimizado. Asyncrono. Step 2/2.
	 * 
	 * @param body
	 * @return
	 * @throws InterruptedException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/endFirmarAsync", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> endFirmarAsync(@RequestBody Map<String, Object> body)
			throws InterruptedException {

		List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

		String algoritmoFirma = (String) body.get("algoritmoFirma");

		Usuario user = mngrUsuario.fetch(getHeader(HeaderValueNames.HEADER_USER_ID));

		for (Map<String, String> map : paraFirmar) {

			JSONObject documento = new JSONObject(map);

			try {

				String objectId = documento.getString("objectId");

				Integer idAsunto = documento.getInt("idAsunto");

				String tipoOperacion = documento.getString("tipo");

				if ("R".equalsIgnoreCase(tipoOperacion))
					marcarDocumentoRespuesta(objectId, //
							StatusFirmaDocumento.FIRMANDO_ARCHIVO.toString());

				else if ("A".equalsIgnoreCase(tipoOperacion))
					marcarDocumentoAsunto(objectId, //
							StatusFirmaDocumento.FIRMANDO_ARCHIVO.toString(), idAsunto);

				firmarAsyncProcess.process(body, user, algoritmoFirma);

			} catch (Exception e) {

			}

		}

		return new ResponseEntity<Map<String, List<?>>>(new HashMap<>(), HttpStatus.OK);

	}

	/**
	 * Firma templates.
	 *
	 * @param objectId the object id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documento", notes = "Obtiene el documento para la descarga de representacion impresa")
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

	@RequestMapping(value = "/firma/templates", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<FirmaImpresaTemplate>> firmaTemplates(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		try {
			List<FirmaImpresaTemplate> lst = new ArrayList<>();
			IEndpoint endpoint = EndpointDispatcher.getInstance();

			Boolean isFirmado = isFirmado(objectId);

			if (!isFirmado) {
				throw new Exception("Documento no firmado: " + objectId);
			}

			List<?> tipoFirma = (List<?>) endpoint.getObjectProperty(objectId,
					environment.getProperty("fieldTipoFirma"));

			if (tipoFirma != null && tipoFirma.size() == 0) {
				throw new Exception("Tipo de firma no encontrado: " + objectId);
			}

			TipoFirma tipo;
			boolean isPDF = false;
			for (Object tipo_firma : tipoFirma) {

				tipo = TipoFirma.fromString((String) tipo_firma);

				switch (tipo) {
				case PDF_FIRMA:
				case PDF_ANTEFIRMA:
				case PDF_CLASIFICACION:
				case PDF_DESCLASIFICACION:
				case PDF_MULTISIGN:
					isPDF = true;
					break;
				case CMS:
					isPDF = false;
					break;
				default:
					throw new Exception("Tipo firma no soportado en firma impresa: " + tipo);
				}
			}

			String[] defecto = environment.getProperty((isPDF ? "pdfDefaultTemplate" : "cmsDefaultTemplate"))
					.split(":");
			if (defecto != null && defecto.length != 2) {
				throw new Exception("La plantilla por defecto esta mal configurada");
			}

			FirmaImpresaTemplate def = new FirmaImpresaTemplate(defecto[0], defecto[1], isPDF);
			lst.add(def);

			String templates_ = environment.getProperty((isPDF ? "pdfTemplates" : "cmsTemplates"));

			if (templates_ != null && !"".equalsIgnoreCase(templates_.trim())) {
				String templates[] = templates_.split(",");
				if (templates.length > 0) {
					for (int i = 0; i < templates.length; i++) {
						String temp[] = templates[i].split(":");
						if (temp != null && temp.length != 2) {
							throw new Exception(
									"La plantillas" + (isPDF ? " PDF " : " CMS ") + "estan mal configuradas");
						}

						lst.add(new FirmaImpresaTemplate(temp[0], temp[1], isPDF));
					}
				}
			}

			return new ResponseEntity<List<FirmaImpresaTemplate>>(lst, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtiene el archivo pkcs7 de un archivo firmado.
	 *
	 * @param objectId the object id
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Descargar evidencia firma", notes = "Descarga la evidencia de un documento firmado")
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

	@RequestMapping(value = "/firma/evidencia", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> evidencia(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		try {

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			Map<String, Object> props = endpoint.getObjectProperties(objectId);

			String fileName = ((List<?>) props.get("cmis:name")).get(0).toString();

			File rendicionfile_;

			Boolean isAnexoVersionable = isAnexoVersionable(fileName);

			if (isAnexoVersionable) {

				File pdfFirmado = endpoint.getFile(objectId);

				AcroFields acroFields = new PdfReader(pdfFirmado.getAbsolutePath()).getAcroFields();

				List<String> names_ = acroFields.getSignatureNames();

				List<File> pkcs7Files = new ArrayList<File>();

				File fileFirma;
				PdfDictionary dict;
				PdfString contents;

				for (String name : names_) {

					dict = acroFields.getSignatureDictionary(name);

					contents = (PdfString) PdfReader.getPdfObject(dict.get(PdfName.CONTENTS));

					fileFirma = File.createTempFile(name, "_pkcs7");

					fileFirma.deleteOnExit();

					FileUtils.writeByteArrayToFile(fileFirma, contents.getOriginalBytes());

					pkcs7Files.add(fileFirma);

				}

				// last one
				rendicionfile_ = pkcs7Files.get(pkcs7Files.size() - 1);

				pdfFirmado.delete();

			} else {

				rendicionfile_ = endpoint.getLastRendition(objectId);

			}

			Map<String, Object> result = new HashMap<>();

			result.put("contentB64", FileUtil.fileToStringB64(rendicionfile_));
			result.put("objectId", objectId);
			result.put("type", ((List<?>) props.get("cmis:contentStreamMimeType")).get(0));
			result.put("name", fileName + ".p7m");

			rendicionfile_.delete();

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Firma impresa.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Descarga representacion impresa", notes = "Descarga la representacion impresa de la firma digital")
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
	@RequestMapping(value = "/firma/impresa", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> firmaImpresa(@RequestBody(required = true) String body)
			throws Exception {

		try {
			JSONObject json = new JSONObject(body);

			String objectId = json.getString("objectId");
			String evidence = json.getString("evidence");
			String tipoConf = json.getString("tipoConf");

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			Boolean isFirmado = isFirmado(objectId);

			if (!isFirmado) {
				throw new BadRequestException("Documento no firmado.");
			}

			List<?> tipoFirma = (List<?>) endpoint.getObjectProperty(objectId,
					environment.getProperty("fieldTipoFirma"));

			if (tipoFirma != null && tipoFirma.size() == 0) {
				throw new Exception("Tipo de firma no encontrado: " + objectId);
			}

			TipoFirma tipo;
			boolean isPDF = false;
			for (Object tipo_firma : tipoFirma) {

				tipo = TipoFirma.fromString((String) tipo_firma);

				switch (tipo) {
				case PDF_FIRMA:
				case PDF_ANTEFIRMA:
				case PDF_CLASIFICACION:
				case PDF_DESCLASIFICACION:
				case PDF_MULTISIGN:
					isPDF = true;
					break;
				case CMS:
					isPDF = false;
					break;
				default:
					throw new Exception("Tipo firma no soportado en firma impresa: " + tipo);
				}
			}

			List<String> listFirmas = (List<String>) endpoint.getObjectProperty(objectId,
					environment.getProperty("fieldIdentificador"));

			String idDocumento = listFirmas.get(listFirmas.size() - 1);

			Map<String, Object> repImpresa = firmaEndPoint.getRepresentacionImpresaPlantilla(Long.valueOf(idDocumento),
					(isPDF ? TipoFirma.PDF_FIRMA : TipoFirma.CMS), tipoConf, evidence, "");

			return new ResponseEntity<Map<String, Object>>(repImpresa, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Comprueba si ya un usuario ha firmado un documento.
	 * 
	 * @param user
	 * @param paraFirmar
	 * @return
	 * @throws Exception
	 */
	private boolean validarUsuarioFirmaExistente(Usuario user, List<Map<String, String>> paraFirmar) throws Exception {
		IEndpoint endpoint = EndpointDispatcher.getInstance();

		List<Map<String, String>> documentosFirmados = endpoint.obtenerDocumentosFirmados(
				environment.getProperty("docTypeAdjuntoAsunto"), //
				environment.getProperty("fieldFirmante"), //
				user.getEmail());

		if (documentosFirmados.isEmpty()) {

			return false;

		} else {

			List<String> pf_ = paraFirmar.stream().map(pf -> pf.get("objectId").toLowerCase()).distinct()
					.collect(Collectors.toCollection(ArrayList::new));
			List<String> df_ = documentosFirmados.stream().map(pf -> pf.get("r_object_id").toLowerCase()).distinct()
					.collect(Collectors.toCollection(ArrayList::new));

			boolean found_ = CollectionUtils.containsAny(df_, pf_);

			return found_;

		}

	}
}
