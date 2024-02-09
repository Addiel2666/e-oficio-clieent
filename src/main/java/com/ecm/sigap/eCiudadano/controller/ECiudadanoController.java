/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eCiudadano.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.x500.X500Principal;
import javax.ws.rs.BadRequestException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
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
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.impl.AcuseController;
import com.ecm.sigap.data.controller.impl.DocumentoAsuntoController;
import com.ecm.sigap.data.controller.impl.DocumentoRespuestaController;
import com.ecm.sigap.data.controller.impl.EntidadController;
import com.ecm.sigap.data.controller.impl.FirmaController;
import com.ecm.sigap.data.controller.impl.RepositoryController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoConsulta;
import com.ecm.sigap.data.model.Ciudadano;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Empresa;
import com.ecm.sigap.data.model.Entidad;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.RepresentanteLegal;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.TipoRespuesta;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoFirma;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.eCiudadano.model.AcuseFirmado;
import com.ecm.sigap.eCiudadano.model.TipoLogin;
import com.ecm.sigap.firma.model.FirmaImpresaTemplate;
import com.ecm.sigap.security.util.CryptoUtil;
import com.ecm.sigap.util.SignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ECiudadanoController.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public final class ECiudadanoController extends CustomRestController {

	/** The acuse controller. */
	@Autowired
	private AcuseController acuseController;

	/**
	 * Referencia hacia el REST controller {@link DocumentoAsuntoController}.
	 */
	@Autowired
	private DocumentoAsuntoController documentoAsuntoController;

	/** The repository controller. */
	@Autowired
	private RepositoryController repositoryController;

	/** Referencia hacia el REST controller {@link DocumentoRespuestaController}. */
	@Autowired
	private DocumentoRespuestaController documentorespuestaController;

	/** Referencia hacia el REST controller {@link FirmaController}. */
	@Autowired
	private FirmaController firmaController;

	/** Referencia hacia el REST controller {@link EntidadController}. */
	@Autowired
	private EntidadController entidadController;

	/**
	 * Referencia hacia el REST controller {@link RepositoryController}.
	 */
	@Autowired
	private RepositoryController repositorioController;

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ECiudadanoController.class);

	/**
	 * <f:param name="param1" value="#{login.key}" />
	 * <f:param name="param2" value="#{login.asuntoB64}" />
	 *
	 * @param key       the key
	 * @param asuntoB64 the asunto B 64
	 * @param certB64   the cert B 64
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/login", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> inicarLogin(//
			@RequestParam(value = "param1", required = true) String key, //
			@RequestParam(value = "param2", required = true) String asuntoB64, //
			@RequestParam(value = "param3", required = true) String certB64, //
			@RequestParam(value = "param4", required = true) String algoritmoFirma) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();

		try {

			String validarEmail = environment.getProperty("eportal.loginValidacionEmail");
			String validarRFC = environment.getProperty("eportal.loginValidacionRFC");
			String asuntoString = CryptoUtil.decryptText(asuntoB64, CryptoUtil.fromHex(key));

			log.debug("asunto decoded :: " + asuntoString);

			StringTokenizer args = new StringTokenizer(asuntoString, ";");

			String nextToken = args.nextToken();
			String replace = nextToken.replace("idAsunto=", "");

			Integer idAsunto = Integer.valueOf(replace);

			// usado en asuntoViewer 4
			String password = args.nextToken();
			password.replace("codigo=", "");

			String mail = args.nextToken();
			String mailInRequest = mail.replace("mail=", "");
			// Valida que email del certificado sea el mismo que el de registro del
			// ciudadano.
			if (Boolean.parseBoolean(validarEmail)) {
				String certificateMail = getCertificateMail(certB64);
				if (!certificateMail.toLowerCase().contains(mailInRequest.toLowerCase())) {
					throw new BadRequestException("El E-mail del certificado no coincide con el email del ciudadano.");
				}
			}

			String rfc = args.hasMoreTokens() ? args.nextToken() : "";
			String rfcInRequest = rfc.replace("rfc=", "");
			// Valida que RFC del certificado sea el mismo que el de registro del ciudadano.
			if (Boolean.parseBoolean(validarRFC)) {
				String certificateRFC = getRfcSignerCertificate(certB64);
				if (!(certificateRFC.toLowerCase().trim()).contains(rfcInRequest.toLowerCase())) {
					throw new BadRequestException("El RFC del certificado no coincide con el RFC del ciudadano.");
				}
			}

			Asunto item = mngrAsunto.fetch(idAsunto);

			if (item != null) {

				if (item.getStatusTurno().getIdStatus().equals(Status.POR_ENVIAR)) {
					throw new BadRequestException(errorMessages.getString("errorLoginTramiteNoEnviado"));
				}

				AcuseFirmado acuseFirmado = mngrAcuseFirmado.fetch(idAsunto);

				if (acuseFirmado != null && StringUtils.isNotBlank(acuseFirmado.getObjectId())) {

					response.put("asunto", item);

					List<?> docs = getDocumentosAsunto(idAsunto);
					response.put("documentosAsunto", docs);

					List<?> resps = getRespuestasAsunto(idAsunto);
					response.put("respuestasAsunto", resps);

					String destinatario = getNombreDestinatario(item);
					response.put("nombreDestinatario", destinatario);

					Area area = getAreaDestinatario(item);
					response.put("areaDestinatario", area);

					// si ya existe el acuse firmado solo hay q devolver el
					// asunto.
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

				} else {

					// Actualizar asunto
					actualizarAsunto(item);

					// no se ha firmado el acuse,
					ResponseEntity<Map<String, String>> acuseResponse = acuseController
							.getAcuseAsunto(idAsunto.toString());

					Map<String, String> acuse = acuseResponse.getBody();

					String contentB64 = acuse.get("contentB64");
					String name = acuse.get("name");

					Map<String, Object> upload = beginFirmaLogin(idAsunto, name, contentB64, certB64, mailInRequest,
							algoritmoFirma);

					// se envia un status especifico para indicarle al front q tiene q firmar el
					// documento del acuse.
					response.put("contentB64", contentB64);
					response.put("name", name);
					response.put("uploadId", upload.get("uploadId"));
					response.put("HashArchivo", upload.get("HashArchivo"));

					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.PRECONDITION_REQUIRED);

				}

			} else {
				throw new BadRequestException();
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Complete firma.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/login/firmar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> completeFirma(//
			@RequestBody Map<String, Object> body//
	) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();

		String algoritmoFirma = (String) body.get("algoritmoFirma");
		if (StringUtils.isBlank(algoritmoFirma)) {
			algoritmoFirma = "SHA1withRSA";
		}

		try {

			Integer idAsunto;
			String nameFile = body.get("name").toString();
			String certB64 = body.get("param3").toString();
			// para certificados pem
			certB64 = certB64.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "")
					.trim();

			Integer uploadId = (Integer) body.get("uploadId");
			String firmaHex = body.get("firmaB64").toString();

			byte[] decodedHex = Hex.decodeHex(firmaHex.toCharArray());

			String firmaB64 = Base64.encodeBase64String(decodedHex);

			String asuntoB64 = body.get("param2").toString();
			String key = body.get("param1").toString();

			String asuntoString = CryptoUtil.decryptText(asuntoB64, CryptoUtil.fromHex(key));

			log.debug("asunto decoded :: " + asuntoString);

			StringTokenizer args = new StringTokenizer(asuntoString, ";");

			String nextToken = args.nextToken();
			String replace = nextToken.replace("idAsunto=", "");

			idAsunto = Integer.valueOf(replace);

			String password = args.nextToken();
			password.replace("codigo=", "");
			String mail = args.nextToken();
			String mailInRequest = mail.replace("mail=", "");

			// Validar Firma
			if (!validarFirmaProcess(uploadId, mailInRequest, certB64, firmaB64, TipoFirma.PDF_FIRMA,
					SignContentType.PDF, algoritmoFirma)) {
				return new ResponseEntity<Map<String, Object>>(new HashMap<String, Object>(), HttpStatus.CONFLICT);
			}

			// obtener documento firmado en formato file
			File evidencia = getEvidencia(uploadId, TipoFirma.PDF_FIRMA, nameFile, false);

			Asunto asunto = mngrAsunto.fetch(idAsunto);
			Asunto asuntoOrigen = mngrAsunto.fetch(asunto.getIdAsuntoOrigen());
			String tipoDoc = environment.getProperty("docTypeAdjuntoAsunto");
			Version verDoc = Version.MAYOR;

			IEndpoint endpoint = EndpointDispatcher.getInstance();
			String newID = endpoint.saveDocumentoIntoId(asuntoOrigen.getContentId(), nameFile, tipoDoc, verDoc,
					nameFile, evidencia);

			// AGREGAR ACL
			Map<String, String> additionalData = new HashMap<>();

			additionalData.put("idArea", asunto.getArea().getIdArea().toString());

			String aclName = "aclNameAcuseTramite";
			log.debug("Aplicando el ACL " + aclName + " documento ");
			endpoint.setACL(newID, environment.getProperty(aclName), additionalData);

			AcuseFirmado item = new AcuseFirmado();
			item.setIdAsunto(idAsunto);
			item.setObjectId(newID);

			mngrAcuseFirmado.save(item);

			Asunto asuntoResult = mngrAsunto.fetch(item.getIdAsunto());
			response.put("asunto", asuntoResult);

			List<?> docs = getDocumentosAsunto(idAsunto);
			response.put("documentosAsunto", docs);

			List<?> resps = getRespuestasAsunto(idAsunto);
			response.put("respuestasAsunto", resps);

			String destinatario = getNombreDestinatario(asuntoResult);
			response.put("nombreDestinatario", destinatario);

			Area area = getAreaDestinatario(asuntoResult);
			response.put("areaDestinatario", area);

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());

			throw e;
		}

	}

	/**
	 * Gets the nombre destinatario.
	 *
	 * @param item the item
	 * @return the nombre destinatario
	 */
	private String getNombreDestinatario(Asunto item) {

		SubTipoAsunto subTipoAsunto = SubTipoAsunto.fromTipo(item.getIdSubTipoAsunto());

		String nombreDestinatario = "";

		switch (subTipoAsunto) {
		case C:
			Usuario user = mngrUsuario.fetch(item.getDestinatario());
			nombreDestinatario = user == null ? "" : user.getNombreCompleto();
			break;
		case D:
			Ciudadano ciudadano = mngrCiudadano.fetch(Integer.valueOf(item.getDestinatario()));
			nombreDestinatario = ciudadano == null ? "" : ciudadano.getNombreCompleto();
			break;
		case R:
			RepresentanteLegal representanteLegal = mngrRepresentanteLegal
					.fetch(Integer.valueOf(item.getDestinatario()));
			nombreDestinatario = representanteLegal == null ? "" : representanteLegal.getNombreCompleto();
			break;
		case F:
			Representante representante = mngrRepresentante.fetch(item.getDestinatario());
			nombreDestinatario = representante == null ? "" : representante.getNombreCompleto();
			break;
		default:
			break;
		}

		return nombreDestinatario;
	}

	/**
	 * Gets the area destinatario.
	 *
	 * @param item the item
	 * @return the area destinatario
	 */
	private Area getAreaDestinatario(Asunto item) {

		SubTipoAsunto subTipoAsunto = SubTipoAsunto.fromTipo(item.getIdSubTipoAsunto());

		Area area = null;

		switch (subTipoAsunto) {
		case C:
			Usuario user = mngrUsuario.fetch(item.getDestinatario());
			area = mngrArea.fetch(Integer.valueOf(user.getIdArea()));
			break;
		case D:
			// Ciudadano ciudadano
			area = mngrArea.fetch(Integer.valueOf(getParamApp("IDCIUDPROMOTOR")));
			break;
		case R:
			// RepresentanteLegal representanteLegal
			area = mngrArea.fetch(Integer.valueOf(getParamApp("IDEMPPROMOTOR")));
			break;
		case F:
			Representante representante = mngrRepresentante.fetch(item.getDestinatario());
			area = mngrArea.fetch(Integer.valueOf(representante.getArea().getIdArea()));
			break;
		default:
			break;
		}

		return area;
	}

	/**
	 * Save documento respuesta.
	 *
	 * @param documento the documento
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/documentos/respuesta", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DocumentoRespuesta> saveDocumentoRespuesta(
			@RequestBody(required = true) DocumentoRespuesta documento) throws Exception {

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		try {

			log.debug("Datos del documento respuesta a guardar " + documento);
			if (documento.getObjectId() != null)
				mngrDocsRespuesta.update(documento);
			else {

				Respuesta respuesta = mngrRespuesta.fetch(documento.getIdRespuesta());

				Asunto asunto = null;

				if (respuesta != null)
					asunto = mngrAsunto.fetch(respuesta.getIdAsunto());

				if (respuesta == null || asunto == null) {
					throw new BadRequestException("El asunto o la respuesta no existen.");
				}

				documento.setIdAsunto(respuesta.getIdAsunto());
				documento.setParentContentId(asunto.getContentId());

				{
					log.debug("SAVING NEW DOCUMENTO RESPUESTA :: " + documento.toString());

					File documento_ = FileUtil.createTempFile(documento.getFileB64());
					String parentFolderId = documento.getParentContentId();
					String nombreArchivo = documento.getObjectName();
					String tipoDoc = environment.getProperty("docTypeAdjuntoRespuesta");
					Version verDoc = Version.MAYOR;
					String descDoc = documento.getObjectName();

					// System.out.println(parentFolderId);
					// System.out.println(nombreArchivo);
					// System.out.println(tipoDoc);
					// System.out.println(verDoc);
					// System.out.println(descDoc);
					// System.out.println(documento_);
					// subir archivo al repositorio
					String newID = endpoint.saveDocumentoIntoId(parentFolderId, nombreArchivo, tipoDoc, verDoc, descDoc,
							documento_);

					documento_.delete();

					Map<String, Object> properties = new HashMap<>();
					// Obtenemos el User Name para asignarlo como el Owner
					// del documento
					// String userName =
					// endpoint.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
					// properties.put("owner_name", "test");

					// Asignamos las propiedades del Asunto
					properties.put("idasunto", String.valueOf(asunto.getIdAsunto()));
					properties.put("idrespuesta", String.valueOf(respuesta.getIdRespuesta()));

					endpoint.setProperties(newID, properties);

					// AGREGAR ACL
					Map<String, String> additionalData = new HashMap<>();

					String idArea = getParamApp("IDCIUDPROMOTOR");
					additionalData.put("idArea", idArea);

					String aclName = "aclNameAdjuntoRespuesta";
					// Para el caso de los asuntos confidenciales, se le
					// asigna el ACL de Asuntos confidenciales
					if (asunto.getAsuntoDetalle().getConfidencial()) {
						aclName = "aclNameAdjuntoRespuestaConfidencial";
					}

					log.debug("Aplicando el ACL " + aclName + " documento ");
					endpoint.setACL(newID, environment.getProperty(aclName), additionalData);

					documento.setObjectId(newID);
				}

				mngrDocsRespuesta.save(documento);

				documento.setFileB64(null);
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			throw e;
		}

		return new ResponseEntity<DocumentoRespuesta>(documento, HttpStatus.OK);
	}

	/**
	 * Gets the documento respuesta.
	 *
	 * @param docRespuesta the doc respuesta
	 * @return the documento respuesta
	 */
	@RequestMapping(value = "/e-ciudadano/documentos/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> getDocumentoRespuesta(
			@RequestBody(required = true) DocumentoRespuesta docRespuesta) {

		return documentorespuestaController.search(docRespuesta);
	}

	/**
	 * Delete documento respuesta.
	 *
	 * @param id the id
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/documentos/respuesta", method = RequestMethod.DELETE)
	public void deleteDocumentoRespuesta(@RequestParam(value = "id", required = true) Serializable id)
			throws Exception {

		documentorespuestaController.delete(id);
	}

	/**
	 * Gets the documentos.
	 *
	 * @param idAsunto the id asunto
	 * @return the documentos
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/e-ciudadano/documentos/asunto", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<DocumentoAsunto>> getDocumentos(
			@RequestParam(value = "idAsunto", required = true) Serializable idAsunto//
	) throws Exception {

		DocumentoAsunto documentoAsunto = new DocumentoAsunto();

		documentoAsunto.setIdAsunto(Integer.valueOf((String) idAsunto));

		ResponseEntity<List<?>> searchResponse = documentoAsuntoController.search(documentoAsunto);

		List<DocumentoAsunto> listDocuemntoAsunto = (List<DocumentoAsunto>) searchResponse.getBody();

		return new ResponseEntity<List<DocumentoAsunto>>(listDocuemntoAsunto, HttpStatus.OK);

	}

	/**
	 * Search.
	 *
	 * @param tipoRespuesta the tipo respuesta
	 * @return the response entity
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/e-ciudadano/tipoRespuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<TipoRespuesta>> search(
			@RequestBody(required = true) TipoRespuesta tipoRespuesta) {

		List<TipoRespuesta> lst = new ArrayList<TipoRespuesta>();
		log.info("Parametros de busqueda :: " + tipoRespuesta);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tipoRespuesta.getIdTipoRespuesta() != null)
				restrictions.add(Restrictions.idEq(tipoRespuesta.getIdTipoRespuesta()));

			if (tipoRespuesta.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", tipoRespuesta.getDescripcion(),
						MatchMode.ANYWHERE));

			if (tipoRespuesta.getTipoConcluida() != null)
				restrictions.add(Restrictions.eq("tipoConcluida", tipoRespuesta.getTipoConcluida()));

			if (tipoRespuesta.getInfomex() != null)
				restrictions.add(Restrictions.eq("infomex", tipoRespuesta.getInfomex()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<TipoRespuesta>) mngrTipoRespuesta.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<TipoRespuesta>>(lst, HttpStatus.OK);
	}

	/**
	 * Firmas aplicables.
	 *
	 * @param objectId       the object id
	 * @param certificadoB64 the certificado B 64
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/firmasAplicables", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<TipoFirma>> firmasAplicables(
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "certificadoB64", required = true) String certificadoB64) throws Exception {

		List<TipoFirma> result = new ArrayList<>();

		try {

			log.debug(" GET :: /firmasAplicables " + objectId);

			String tiposFirmaAplicables = environment.getProperty("eportal.tiposFirmaAplicables");

			if (StringUtils.isNotBlank(tiposFirmaAplicables)) {
				for (String tipo_ : tiposFirmaAplicables.split(",")) {
					result.add(TipoFirma.fromString(tipo_));
				}
			}

			log.debug(" >> " + objectId + " ALL FIRMAS APLICABLES >> " + result.toString());

			return new ResponseEntity<List<TipoFirma>>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		// return firmaController.firmasAplicables(objectId, "false");
	}

	/**
	 * Firmar.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/e-ciudadano/firmar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> firmar(@RequestBody Map<String, Object> body)
			throws Exception {

		List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

		String crestificadoB64 = body.get("certificadoB64").toString();

		String algoritmoFirma = body.get("algoritmoFirma").toString();

		Usuario user = new Usuario();
		user.setEmail(getMailSignerCertificate(crestificadoB64));

		Map<String, List<?>> resultado = new HashMap<String, List<?>>();
		List<Map<String, Object>> listExito = new ArrayList<>();
		List<Map<String, Object>> listFail = new ArrayList<>();

		for (Map<String, String> map : paraFirmar) {

			JSONObject documento = new JSONObject(map);

			try {

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
		ResponseEntity<Map<String, Object>> response = repositorioController.getDocumentAsAdmin(objectId);

		String fileB64 = response.getBody().get("contentB64").toString();
		String fileName = response.getBody().get("name").toString();

		TipoFirma tipoFirma_ = TipoFirma.fromString(tipoFirma);

		SignContentType signContentType = firmaController.isAnexoVersionable(fileName) ? SignContentType.PDF
				: SignContentType.OFICIO;

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

			TipoFirma tipoFirma_ = TipoFirma.fromString(body_.getString("tipoFirma"));

			SignContentType signContentType = firmaController.isAnexoVersionable(fileName) ? SignContentType.PDF
					: SignContentType.OFICIO;

			String certificadoB64 = body_.getString("certificadoB64");

			// para certificados pem
			certificadoB64 = certificadoB64.replace("-----BEGIN CERTIFICATE-----", "")
					.replace("-----END CERTIFICATE-----", "").trim();

			Map<String, Object> hashFile = firmaEndPoint.startSign(//
					body_.getInt("id"), //
					user.getEmail(), //
					certificadoB64, //
					tipoFirma_, //
					signContentType, //
					algoritmoFirma, null, null, null, null, null, null);

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
	 * End firmar.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/e-ciudadano/endFirmar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, List<?>>> endFirmar(@RequestBody Map<String, Object> body)
			throws Exception {
		try {

			List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

			String algoritmoFirma = (String) body.get("algoritmoFirma");
			if (StringUtils.isBlank(algoritmoFirma)) {
				algoritmoFirma = "SHA1withRSA";
			}

			Map<String, List<?>> resultado = new HashMap<String, List<?>>();
			List<Map<String, Object>> listExito = new ArrayList<>();
			List<Map<String, Object>> listFail = new ArrayList<>();

			if (paraFirmar != null && !paraFirmar.isEmpty()) {

				Usuario user = new Usuario();

				for (Map<String, String> map : paraFirmar) {
					JSONObject documento = new JSONObject(map);

					try {

						user.setEmail(getMailSignerCertificate(documento.getString("certificadoB64")));

						documento.put("firmaHex", documento.getString("firma"));

						Map<String, Object> validado = firmaController.validarFirmaProcess(documento, user,
								algoritmoFirma);

						documento.put("isValid", validado.get("isValid"));
						documento.put("HashArchivo", documento.get("HashArchivoHex"));

						Map<String, Object> firmado = firmaController.aplicarFirmaProcess(documento, user);

						documento.put("result", firmado.get("result"));

						Map<String, Object> result = new ObjectMapper().readValue(documento.toString(), HashMap.class);

						listExito.add(result);

					} catch (Exception e) {

						documento.put("failCause", errorMessages.getString("firmaErrorRetry"));

						Map<String, Object> result = new ObjectMapper().readValue(documento.toString(), HashMap.class);

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
	 * Gets the document.
	 *
	 * @param objectId the object id
	 * @return the document
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/repository/downloadDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getDocument(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {
		return repositoryController.getDocumentAsAdmin(objectId);
	}

	/**
	 * Gets the entidad.
	 *
	 * @param entidad the entidad
	 * @return the entidad
	 */
	@RequestMapping(value = "/e-ciudadano/entidad", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> getEntidad(@RequestBody(required = true) Entidad entidad) {

		return entidadController.search(entidad);
	}

	/**
	 * Actualizar asunto.
	 *
	 * @param asunto the asunto
	 * @throws Exception    the exception
	 * @throws IOException  Signals that an I/O exception has occurred.
	 * @throws TSPException the TSP exception
	 * @throws CMSException the CMS exception
	 */
	private void actualizarAsunto(Asunto asunto) throws Exception, IOException, TSPException, CMSException {
		boolean isCopia = asunto.getTipoAsunto().getValue().equals(TipoAsunto.COPIA.getValue());
		if ((isCopia || asunto.getTipoAsunto().getValue().equals(TipoAsunto.ENVIO.getValue()))) {

			if (asunto.getStatusTurno().getIdStatus() == Status.ENVIADO) {
				IEndpoint endpoint = EndpointDispatcher.getInstance();
				String contentIdFolder = crearFolderCiudadano();
				boolean noRequiereRespuesta = false;
				if (asunto.getInstruccion() != null && !asunto.getInstruccion().getRequiereRespuesta()) {
					noRequiereRespuesta = true;
				}

				////////////////////
				// crear subfolder

				String subFolder = null;
				if (!TipoAsunto.COPIA.equals(asunto.getTipoAsunto())) {
					// Generamos el Folio del area que esta
					// aceptando el Asunto recibido
					subFolder = TipoAsunto.ASUNTO.getValue() + asunto.getIdAsunto().toString();
				} else {
					// Se genera el Folio con el prefijo 'C' mas el idAsunto para las
					// copias
					subFolder = TipoAsunto.COPIA.getValue() + asunto.getIdAsunto().toString();
				}

				// Generamos la carpeta del Asunto en el
				// repositorio en el Area
				String folderIdAsunto = endpoint.createFolderIntoId(//
						contentIdFolder, //
						environment.getProperty("folderTypeAsunto"), //
						subFolder);

				asunto.setContentId(folderIdAsunto);

				log.debug("Fin del proceso de actualizacion del asunto en el repositorio");

				//////////////////

				////////////////

				{
					List<Timestamp> timestamps = asunto.getTimestamps();

					if (timestamps == null)
						timestamps = new ArrayList<>();

					timestamps.removeIf(t -> t.getTipo().equals(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO));

					Timestamp timeStamp = new Timestamp();

					timeStamp.setTipo(TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO);

					String stampedData = getStampedData(asunto, timeStamp.getTipo());

					Map<String, Object> time = firmaEndPoint.getTime(stampedData,
							TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO.getTipoString());

					String timestamp = (String) time.get("Tiempo");

					timeStamp.setTimestamp(timestamp);
					timestamps.add(timeStamp);

					asunto.setTimestamps(timestamps);
					asunto.setFechaAcuse(SignatureUtil.timestampToDate(timestamp));
				}

				/////////////

				Status estatusProceso = mngrStatus
						.fetch(isCopia || noRequiereRespuesta ? Status.CONCLUIDO : Status.PROCESO);
				asunto.setStatusTurno(estatusProceso);
				asunto.setStatusAsunto(estatusProceso);

				mngrAsunto.update(asunto);

			} else {
				log.error(
						"El tramite se encuentra en un estado diferente a registrado. No puede ser aceptado por el area");
			}

		}
	}

	/**
	 * Crear folder ciudadano.
	 *
	 * @return the string
	 * @throws Exception the exception
	 */
	private String crearFolderCiudadano() throws Exception {
		// crear folder del area ciudadano
		String idAreaCiudadano = getParamApp("IDCIUDPROMOTOR");
		Area areaCiudadano = mngrArea.fetch(Integer.parseInt(idAreaCiudadano));
		String folderIdArea = null;
		if (StringUtils.isBlank(areaCiudadano.getContentId())) {
			String idArea = areaCiudadano.getIdArea().toString();

			// folder de area
			String nombreFolder = (areaCiudadano.getDescripcion());
			IEndpoint superUser = EndpointDispatcher.getInstance();

			String startFolder = getParamApp("CABINET");

			try {
				folderIdArea = superUser.createFolder(//
						startFolder, //
						environment.getProperty("folderTypeArea"), //
						nombreFolder);
			} catch (Exception e) {
				log.error("ERROR creando folder del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			String nombreGrupo = (environment.getProperty("grpSigap") + idArea);
			try {
				// crear grupos
				superUser.createGroup(nombreGrupo, "");
			} catch (Exception e) {
				log.error("ERROR creando grupo del area >>> " + e.getLocalizedMessage());

				throw e;
			}
			String nombreGrupoConf = (environment.getProperty("grpSigapConf") + idArea);
			try {
				superUser.createGroup(nombreGrupoConf, "");
			} catch (Exception e) {
				log.error("ERROR creando grupo confidencial del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			String newAclName = null;
			try {
				// SET ACL
				Map<String, String> additionalData1 = new HashMap<>();
				additionalData1.put("idArea", idArea);
				superUser.setACL(folderIdArea, environment.getProperty("aclNameFolderArea"), additionalData1);

				// obtener el nombre del acl recien creado
				@SuppressWarnings("unchecked")
				String aclName1 = ((List<String>) superUser.getObjectProperty(folderIdArea, "acl_name")).get(0);

				// renombrar con el estanda de acls de area
				newAclName = environment.getProperty("aclSigapName") + areaCiudadano.getIdArea();
				superUser.renameAcl(aclName1, newAclName);
			} catch (Exception e) {
				log.error("ERROR creando acl del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			try {
				// set properties de area
				Map<String, Object> properties = new HashMap<>();

				properties.put("acl_name", newAclName);
				properties.put("idarea", String.valueOf(areaCiudadano.getIdArea()));
				superUser.setProperties(folderIdArea, properties);
			} catch (Exception e) {
				log.error("ERROR seteando acl al folder del area >>> " + e.getLocalizedMessage());

				throw e;
			}
			areaCiudadano.setContentId(folderIdArea);
			mngrArea.update(areaCiudadano);
		}
		return areaCiudadano.getContentId();
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
	 * Login.
	 *
	 * @param certB64Empresa the cert B 64 empresa
	 * @param tipoLogin      the tipo login
	 * @param certB64        the cert B 64
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/login/multiple", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> login(//
			@RequestParam(value = "param1", required = false) String certB64Empresa, //
			@RequestParam(value = "param2", required = true) String tipoLogin, //
			@RequestParam(value = "param3", required = true) String certB64) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();

		try {

			// String certificateMail = getCertificateMail(certB64);

			String rfcPersona = getRfcSignerCertificate(certB64);
			String rfcEmpresa = null;

			boolean existExterno = false;
			if (StringUtils.isNotBlank(certB64Empresa)) {
				rfcEmpresa = getRfcSignerCertificate(certB64Empresa);
			}

			TipoLogin tipoLoginEnum = TipoLogin.fromString(tipoLogin);
			switch (tipoLoginEnum) {
			case Ciudadano:
				if (StringUtils.isNotBlank(rfcPersona)) {
					Ciudadano ciudadano = getCiudadanoByRFC(rfcPersona);
					response.put("C", ciudadano);
					if (null != ciudadano) {
						existExterno = true;
					}
				}
				break;
			case Empresa:

				if (StringUtils.isNotBlank(rfcEmpresa)) {

					Empresa empresa = getEmpresaByRFC(rfcEmpresa);
					response.put("E", empresa);

					if (null != empresa) {
						existExterno = true;
					}

				}

				break;
			case RepresentanteLegal:
				if (StringUtils.isNotBlank(rfcEmpresa) && StringUtils.isNotBlank(rfcPersona)) {

					Empresa empresaReplegal = getEmpresaByRFC(rfcEmpresa);

					if (null != empresaReplegal) {
						RepresentanteLegal replegal = getRepLegal(rfcPersona, empresaReplegal.getId());
						response.put("R", replegal);

						if (null != replegal) {
							existExterno = true;
						}
					}
				}
				break;
			default:
				break;
			}

			if (!existExterno) {
				return new ResponseEntity<Map<String, Object>>(new HashMap<String, Object>(), HttpStatus.BAD_REQUEST);
			}

			String contentB64 = environment.getProperty("eportal.cadenaB64ParaFirmar");
			String name = "Documento login e-portal.pdf";

			// Map<String, Object> upload = beginFirma(null, name, contentB64, certB64,
			// "gvielma87@gmail.com");

			// DUMMY

			// se envia un status especifico para indicarle al front q tiene q firmar el
			// documento del acuse.
			response.put("contentB64", contentB64);
			response.put("name", name);

			// DUMMY
			// response.put("uploadId", upload.get("uploadId"));
			// response.put("HashArchivo", upload.get("HashArchivo"));
			Integer uploadId = 3566;
			String HashArchivo = "318206b6301806092a864886f70d010903310b06092a864886f70d010701301c06092a864886f70d010905310f170d3138303830333138303435315a302306092a864886f70d01090431160414f93fbb2417e9e87f691d49d5e7bb8cc77da3d6143082065506092a864886f72f0101083182064630820642a182063e3082063a308206360a0100a082062f3082062b06092b06010505073001010482061c308206183081b4a21604149fa0b35cfc5ef77f0ab7928bcc975afe0c90e5af180f32303138303830333138303431385a30643062304d300906052b0e03021a05000414292cc8795fba3b08837c49606f4fe82b8751a12f0414bc29e88e6bbe31366785c637af9c02f421d24ccc021430303030313030303030303330363330303831388000180f32303138303830333138303431385aa1233021301f06092b060105050730010204120410ca58d257a1aa864499cc0391867fabba300d06092a864886f70d0101050500038181008796108a0119afc702af8a57aa6afecc9e8e0f2e35076e978577fb3a27abb14548b08672d5f25ffda7bd7d3d763bd9cb01c53c89a56793e256786ce45fc62d6d8ceb5f00788ab52c847c081e4f7ab10a8d5574e6f611fc18384b09074f90dd6920ccfc9a18b63b3ee11c149a78d0e0c0ed29aef4b541da45a603a553aab652afa08204ca308204c6308204c2308203aaa00302010202143030303031303838383838383030303030303239300d06092a864886f70d01010505003082018a3138303606035504030c2f412e432e2064656c20536572766963696f2064652041646d696e69737472616369c3b36e2054726962757461726961312f302d060355040a0c26536572766963696f2064652041646d696e69737472616369c3b36e205472696275746172696131383036060355040b0c2f41646d696e69737472616369c3b36e20646520536567757269646164206465206c6120496e666f726d616369c3b36e311f301d06092a864886f70d010901161061636f6473407361742e676f622e6d783126302406035504090c1d41762e20486964616c676f2037372c20436f6c2e20477565727265726f310e300c06035504110c053036333030310b3009060355040613024d583119301706035504080c10446973747269746f204665646572616c3114301206035504070c0b437561756874c3a96d6f6331153013060355042d130c5341543937303730314e4e333135303306092a864886f70d0109020c26526573706f6e7361626c653a20436c617564696120436f766172727562696173204f63686f61301e170d3137303232343030333833345a170d3231303433303030333833345a30818c312f302d060355040a1426536572766963696f2064652041646d696e69737472616369c3b36e2054726962757461726961310e300c060355040b13054147435449310b3009060355040613024d583119301706035504081310446973747269746f204665646572616c311330110603550407130a437561756874656d6f63310c300a0603550403130353415430819f300d06092a864886f70d010101050003818d0030818902818100b0da42efd65c4719a079d1ddcc056d205187009b78a9c045a639258fdf80be9e588dfea2953a645408a20c66d134526abbd62e77cc8e170d82a76d215e77323ac7bae517718642f7b8aa4758cb4816670255572f7d55b9151c21e9edd76c920cc9e0e0ad9d3130b91269a9bbd8b474231931072726897f252bc77e30dd9e3fb70203010001a3819e30819b300c0603551d130101ff0402300030130603551d25040c300a06082b06010505070309303606082b06010505070101042a3028302606082b06010505073001861a687474703a2f2f7777772e7361742e676f622e6d782f6f637370301d0603551d0e041604149fa0b35cfc5ef77f0ab7928bcc975afe0c90e5af301f0603551d23041830168014bc29e88e6bbe31366785c637af9c02f421d24ccc300d06092a864886f70d0101050500038201010099e28158110c1af4fb11545ed1335fe64f1c847de4815839c9ef33820aa778c7f86ab0e74b618f05bfb918803d27b6825eae906a32c01d5da47521ee2a4292631fa1e153dba58b713fce924fa0778653a8d666d3798d2ddce81e0372a5a3a0aff828291e247b4c6bd5890fb37ec371fa11db650bc9d95cef2a5da75024ba48e08881a9f37c79b4c5bc58828e11dd1dfb3eac240a33bb33c4c3dccc41fbd25b88cfdd482e0a7ad0ac60f20332a7b36f875739d64924580bde12bd9b58396ec94992fc17ef721d24b92181468b25aeae34f3dd5eabfcb2e44c68e8e48afc5a1cdcf36f7a6ee079906a3426f89c0f1e17f9c4fb049b9644675826e9d967328aa11b";
			response.put("uploadId", uploadId);
			response.put("HashArchivo", HashArchivo);

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Complete firma login.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/login/firmaLogin", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> completeFirmaLogin(//
			@RequestBody Map<String, Object> body//
	) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();

		try {

			// Integer idAsunto;
			String nameFile = body.get("name").toString();
			String certB64 = body.get("param3").toString();
			// para certificados pem
			certB64 = certB64.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "")
					.trim();

			Integer uploadId = (Integer) body.get("uploadId");
			String firmaHex = body.get("firmaB64").toString();

			byte[] decodedHex = Hex.decodeHex(firmaHex.toCharArray());

			String firmaB64 = Base64.encodeBase64String(decodedHex);

			String asuntoB64 = body.get("param2").toString();
			String key = body.get("param1").toString();

			// Validar Firma

			// Comentado para versión DUMMY
			// if (!validarFirmaProcess(uploadId, "gvielma87@gmail.com", certB64, firmaB64,
			// TipoFirma.PDF_FIRMA,
			// SignContentType.PDF)) {
			// return new ResponseEntity<Map<String, Object>>(new HashMap<String, Object>(),
			// HttpStatus.CONFLICT);
			// }

			// obtener documento firmado en formato file
			File evidencia = getEvidencia(uploadId, TipoFirma.PDF_FIRMA, nameFile, false);

			// Asunto asunto = mngrAsunto.fetch(idAsunto);
			// Asunto asuntoOrigen = mngrAsunto.fetch(asunto.getIdAsuntoOrigen());
			String tipoDoc = environment.getProperty("docTypeAdjuntoAsunto");
			Version verDoc = Version.MAYOR;

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());

			throw e;
		}

	}

	/**
	 * Registrar ciudadano.
	 *
	 * @param ciudadano the ciudadano
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/registrarCiudadano", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Ciudadano> registrarCiudadano(@RequestBody(required = true) Ciudadano ciudadano)
			throws Exception {
		try {

			log.debug("CIUDADANO A GUARDAR >> " + ciudadano);
			if (ciudadano.getId() == null) {
				mngrCiudadano.save(ciudadano);
				return new ResponseEntity<Ciudadano>(ciudadano, HttpStatus.CREATED);
			} else {
				mngrCiudadano.update(ciudadano);
				return new ResponseEntity<Ciudadano>(ciudadano, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Registrar rep legal.
	 *
	 * @param repLegal the rep legal
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/registrarRepLegal", method = RequestMethod.PUT)
	public ResponseEntity<RepresentanteLegal> registrarRepLegal(
			@RequestBody(required = true) RepresentanteLegal repLegal) throws Exception {

		try {
			// String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			// if (!esSoloLectura(userId)) {

			log.debug("REPRESENTANTE LEGAL A GUARDAR >> " + repLegal);

			if (repLegal.getId() == null) {
				mngrRepresentanteLegal.save(repLegal);
				return new ResponseEntity<RepresentanteLegal>(repLegal, HttpStatus.CREATED);
			} else {
				mngrRepresentanteLegal.update(repLegal);
				return new ResponseEntity<RepresentanteLegal>(repLegal, HttpStatus.OK);
			}

			// } else {
			// return new ResponseEntity<RepresentanteLegal>(repLegal,
			// HttpStatus.BAD_REQUEST);
			// }
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Search.
	 *
	 * @param body the body
	 * @return the response entity
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/e-ciudadano/asuntoConsultar", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AsuntoConsulta>> asuntosConsultar(
			@RequestBody(required = true) RequestWrapper<AsuntoConsulta> body) {

		AsuntoConsulta asuntoConsulta = body.getObject();
		Map<String, Object> params = body.getParams();
		log.debug("PARAMETROS DE BUSQUEDA :: " + body);

		// String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);

		try {

			List<Criterion> restrictions = new ArrayList<>();

			if (asuntoConsulta.getIdDestinatario() != null && asuntoConsulta.getSubTipoAsunto() != null) {
				restrictions.add(Restrictions.eq("idDestinatario", asuntoConsulta.getIdDestinatario()));
				restrictions.add(Restrictions.eq("subTipoAsunto", asuntoConsulta.getSubTipoAsunto()));
			} else {
				throw new BadRequestException("No se pudo realizar la consulta");
			}

			if (asuntoConsulta.getIdAsunto() != null)
				restrictions.add(Restrictions.eq("idAsunto", asuntoConsulta.getIdAsunto()));

			if (asuntoConsulta.getIdAsuntoPadre() != null)
				restrictions.add(Restrictions.eq("idAsuntoPadre", asuntoConsulta.getIdAsuntoPadre()));

			if (asuntoConsulta.getNumDocto() != null)
				restrictions.add(
						EscapedLikeRestrictions.ilike("numDocto", asuntoConsulta.getNumDocto(), MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(asuntoConsulta.getAsuntoDescripcion()))
				restrictions.add(EscapedLikeRestrictions.ilike("asuntoDescripcion",
						asuntoConsulta.getAsuntoDescripcion(), MatchMode.ANYWHERE));

			if (asuntoConsulta.getIdRemitente() != null)
				restrictions.add(Restrictions.eq("idRemitente", asuntoConsulta.getIdRemitente()));

			if (StringUtils.isNotBlank(asuntoConsulta.getRemitente()))
				restrictions.add(Restrictions.eq("remitente", asuntoConsulta.getRemitente()));

			if (asuntoConsulta.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", asuntoConsulta.getIdArea()));

			if (StringUtils.isNotBlank(asuntoConsulta.getArea()))
				restrictions.add(Restrictions.eq("area", asuntoConsulta.getArea()));

			if (asuntoConsulta.getIdPromotor() != null)
				restrictions.add(Restrictions.eq("idPromotor", asuntoConsulta.getIdPromotor()));

			if (StringUtils.isNotBlank(asuntoConsulta.getPromotor()))
				restrictions.add(Restrictions.eq("promotor", asuntoConsulta.getPromotor()));

			if (StringUtils.isNotBlank(asuntoConsulta.getIdFirmante()))
				restrictions.add(Restrictions.eq("idFirmante", asuntoConsulta.getIdFirmante()));

			if (StringUtils.isNotBlank(asuntoConsulta.getFirmanteAsunto()))
				restrictions.add(Restrictions.eq("firmanteAsunto", asuntoConsulta.getFirmanteAsunto()));

			if (StringUtils.isNotBlank(asuntoConsulta.getFirmanteCargo()))
				restrictions.add(Restrictions.eq("firmanteCargo", asuntoConsulta.getFirmanteCargo()));

			if (asuntoConsulta.getIdTema() != null)
				restrictions.add(Restrictions.eq("idTema", asuntoConsulta.getIdTema()));

			if (asuntoConsulta.getIdSubTema() != null)
				restrictions.add(Restrictions.eq("idSubTema", asuntoConsulta.getIdSubTema()));

			if (asuntoConsulta.getIdEvento() != null)
				restrictions.add(Restrictions.eq("idEvento", asuntoConsulta.getIdEvento()));

			if (asuntoConsulta.getDocumentosAdjuntos() != null)
				restrictions.add(Restrictions.eq("documentosAdjuntos", asuntoConsulta.getDocumentosAdjuntos()));

			if (asuntoConsulta.getStatusAsunto() != null && asuntoConsulta.getIdStatusAsunto() != null) {
				Status stat = mngrStatus.fetch(asuntoConsulta.getIdStatusAsunto());
				if (stat != null)
					restrictions.add(Restrictions.eq("statusAsunto", stat));
			}

			// ++++++++++++++ PARAMS

			if (params != null) {

				if (params.containsKey("respondidoSN")) {
					Boolean val = Boolean.parseBoolean(params.get("respondidoSN").toString());
					if (val) {
						restrictions.add(Restrictions.not(Restrictions.eq("respuestasEnviadas", 0)));
					} else {
						restrictions.add(Restrictions.not(Restrictions.gt("respuestasEnviadas", 0)));
					}

				}

				if (params.containsKey("idStatusTurnoNotIn")) {
					List<Integer> val = (List<Integer>) params.get("idStatusTurnoNotIn");
					if (!val.isEmpty())
						restrictions.add(Restrictions.not(Restrictions.in("idStatusTurno", val)));
				}

				if (params.containsKey("idStatusTurnoIn")) {
					List<Integer> val = (List<Integer>) params.get("idStatusTurnoIn");
					if (!val.isEmpty())
						restrictions.add(Restrictions.in("idStatusTurno", val));
				}

				if (params.containsKey("idStatusAsuntoNotIn")) {
					List<Integer> val = (List<Integer>) params.get("idStatusAsuntoNotIn");
					if (!val.isEmpty())
						restrictions.add(Restrictions.not(Restrictions.in("idStatusAsunto", val)));
				}

				if (params.containsKey("idStatusAsuntoIn")) {
					List<Integer> val = (List<Integer>) params.get("idStatusAsuntoIn");
					if (!val.isEmpty())
						restrictions.add(Restrictions.in("idStatusAsunto", val));
				}

				if (params.containsKey("vencimientoIn")) {
					List<String> val_ = (List<String>) params.get("vencimientoIn");
					List<EnTiempo> val = new ArrayList<>();
					for (String enTiempo : val_) {
						val.add(EnTiempo.valueOf(enTiempo));
					}
					if (!val.isEmpty()) {

						if (val.contains(EnTiempo.EN_TIEMPO)) {
							restrictions.add(Restrictions.or(//
									Restrictions.in("enTiempo", val), //
									Restrictions.isNull("enTiempo")));
						} else {
							restrictions.add(Restrictions.in("enTiempo", val));
						}

					}
				}

				// FILSTROS PARA FECHAS
				if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") != null) {
					restrictions.add(Restrictions.between("fechaEnvio", //
							new Date((Long) params.get("fechaEnvioInicial")),
							new Date((Long) params.get("fechaEnvioFinal"))));
				} else if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") == null) {
					restrictions.add(Restrictions.ge("fechaEnvio", new Date((Long) params.get("fechaEnvioInicial"))));
				} else if (params.get("fechaEnvioInicial") == null && params.get("fechaEnvioFinal") != null) {
					restrictions.add(Restrictions.le("fechaEnvio", new Date((Long) params.get("fechaEnvioFinal"))));
				}

				if (params.get("fechaRecepcionInicial") != null && params.get("fechaRecepcionFinal") != null) {
					restrictions.add(Restrictions.between("fechaRecepcion", //
							new Date((Long) params.get("fechaRecepcionInicial")),
							new Date((Long) params.get("fechaRecepcionFinal"))));
				} else if (params.get("fechaRecepcionInicial") != null && params.get("fechaRecepcionFinal") == null) {
					restrictions.add(
							Restrictions.ge("fechaRecepcion", new Date((Long) params.get("fechaRecepcionInicial"))));
				} else if (params.get("fechaRecepcionInicial") == null && params.get("fechaRecepcionFinal") != null) {
					restrictions
							.add(Restrictions.le("fechaRecepcion", new Date((Long) params.get("fechaRecepcionFinal"))));
				}

				if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
					restrictions.add(Restrictions.between("fechaRegistro", //
							new Date((Long) params.get("fechaRegistroInicial")),
							new Date((Long) params.get("fechaRegistroFinal"))));
				} else if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") == null) {
					restrictions
							.add(Restrictions.ge("fechaRegistro", new Date((Long) params.get("fechaRegistroInicial"))));
				} else if (params.get("fechaRegistroInicial") == null && params.get("fechaRegistroFinal") != null) {
					restrictions
							.add(Restrictions.le("fechaRegistro", new Date((Long) params.get("fechaRegistroFinal"))));
				}

				if (params.get("fechaElaboracionInicial") != null && params.get("fechaElaboracionFinal") != null) {
					restrictions.add(Restrictions.between("fechaElaboracion", //
							new Date((Long) params.get("fechaElaboracionInicial")),
							new Date((Long) params.get("fechaElaboracionFinal"))));
				} else if (params.get("fechaElaboracionInicial") != null
						&& params.get("fechaElaboracionFinal") == null) {
					restrictions.add(Restrictions.ge("fechaElaboracion",
							new Date((Long) params.get("fechaElaboracionInicial"))));
				} else if (params.get("fechaElaboracionInicial") == null
						&& params.get("fechaElaboracionFinal") != null) {
					restrictions.add(
							Restrictions.le("fechaElaboracion", new Date((Long) params.get("fechaElaboracionFinal"))));
				}

				if (params.get("fechaCompromisoInicial") != null && params.get("fechaCompromisoFinal") != null) {
					restrictions.add(Restrictions.between("fechaCompromiso", //
							new Date((Long) params.get("fechaCompromisoInicial")),
							new Date((Long) params.get("fechaCompromisoFinal"))));
				} else if (params.get("fechaCompromisoInicial") != null && params.get("fechaCompromisoFinal") == null) {
					restrictions.add(
							Restrictions.ge("fechaCompromiso", new Date((Long) params.get("fechaCompromisoInicial"))));
				} else if (params.get("fechaCompromisoInicial") == null && params.get("fechaCompromisoFinal") != null) {
					restrictions.add(
							Restrictions.le("fechaCompromiso", new Date((Long) params.get("fechaCompromisoFinal"))));
				}

				if (params.get("fechaEventoInicial") != null && params.get("fechaEventoFinal") != null) {
					restrictions.add(Restrictions.between("fechaEvento", //
							new Date((Long) params.get("fechaEventoInicial")),
							new Date((Long) params.get("fechaEventoFinal"))));
				} else if (params.get("fechaEventoInicial") != null && params.get("fechaEventoFinal") == null) {
					restrictions.add(Restrictions.ge("fechaEvento", new Date((Long) params.get("fechaEventoInicial"))));
				} else if (params.get("fechaEventoInicial") == null && params.get("fechaEventoFinal") != null) {
					restrictions.add(Restrictions.le("fechaEvento", new Date((Long) params.get("fechaEventoFinal"))));
				}

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

				if (params.containsKey("idAreaORIdAreaDestino")) {
					Integer val = Integer.parseInt(params.get("idAreaORIdAreaDestino").toString());

					restrictions.add(Restrictions.or(//
							Restrictions.eq("idAreaDestino", val), //
							Restrictions.eq("idArea", val))//
					);

				}

				if (params.containsKey("inFolioArea")) {
					List<String> val = (ArrayList<String>) params.get("inFolioArea");
					restrictions.add(Restrictions.in("folioArea", val));

				}

			} // fin Params

			List<Order> orders = new ArrayList<Order>();
			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			}

			List<AsuntoConsulta> list = (List<AsuntoConsulta>) mngrAsuntoConsulta.search(restrictions, orders);

			return new ResponseEntity<List<AsuntoConsulta>>(list, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Gets the documentos asunto.
	 *
	 * @param idAsunto the id asunto
	 * @return the documentos asunto
	 */
	private List<?> getDocumentosAsunto(Integer idAsunto) {

		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("idAsunto", idAsunto));
		return mngrDocsAsunto.search(restrictions);

	}

	/**
	 * Gets the ciudadano by RFC.
	 *
	 * @param rfc the rfc
	 * @return the ciudadano by RFC
	 */
	@SuppressWarnings("unchecked")
	private Ciudadano getCiudadanoByRFC(String rfc) {

		Ciudadano ciudadano = null;
		// * * * * * * * * * * * * * * * * * * * * * *
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(EscapedLikeRestrictions.ilike("rfc", rfc, MatchMode.EXACT));

		List<Ciudadano> lst = new ArrayList<Ciudadano>();
		// * * * * * * * * * * * * * * * * * * * * * *
		lst = (List<Ciudadano>) mngrCiudadano.search(restrictions);
		if (null != lst && !lst.isEmpty()) {
			ciudadano = lst.get(0);

		}

		return ciudadano;

	}

	/**
	 * Gets the empresa by RFC.
	 *
	 * @param rfc the rfc
	 * @return the empresa by RFC
	 */
	@SuppressWarnings("unchecked")
	private Empresa getEmpresaByRFC(String rfc) {

		Empresa empresa = null;
		// * * * * * * * * * * * * * * * * * * * * * *
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(EscapedLikeRestrictions.ilike("rfc", rfc, MatchMode.EXACT));

		List<Empresa> lst = new ArrayList<Empresa>();
		// * * * * * * * * * * * * * * * * * * * * * *
		lst = (List<Empresa>) mngrEmpresa.search(restrictions);
		if (null != lst && !lst.isEmpty()) {
			empresa = lst.get(0);

		}

		return empresa;

	}

	/**
	 * Gets the rep lega by RFC.
	 *
	 * @param rfc       the rfc
	 * @param idEmpresa the id empresa
	 * @return the rep lega by RFC
	 */
	@SuppressWarnings("unchecked")
	private RepresentanteLegal getRepLegal(String rfc, Integer idEmpresa) {

		RepresentanteLegal representanteLegal = null;
		// * * * * * * * * * * * * * * * * * * * * * *
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(EscapedLikeRestrictions.ilike("rfc", rfc, MatchMode.EXACT));
		restrictions.add(Restrictions.eq("empresa.id", idEmpresa));
		List<RepresentanteLegal> lst = new ArrayList<RepresentanteLegal>();
		// * * * * * * * * * * * * * * * * * * * * * *
		lst = (List<RepresentanteLegal>) mngrRepresentanteLegal.search(restrictions);
		if (null != lst && !lst.isEmpty()) {
			representanteLegal = lst.get(0);
		}

		return representanteLegal;

	}

	/**
	 * Gets the respuestas asunto.
	 *
	 * @param idAsunto the id asunto
	 * @return the respuestas asunto
	 */
	private List<?> getRespuestasAsunto(Integer idAsunto) {

		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("idAsunto", idAsunto));
		return mngrRespuesta.search(restrictions);

	}

	/**
	 * Gets the certificate mail.
	 *
	 * @param certB64 the cert B 64
	 * @return the certificate mail
	 * @throws CertificateException the certificate exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private String getCertificateMail(String certB64) throws CertificateException, IOException {
		X509Certificate cert = (X509Certificate) SignatureUtil.getCertificateFormStringB64(certB64);

		X500Principal principal = cert.getSubjectX500Principal();

		String principalString = principal.getName("RFC1779");
		return principalString;
	}

	/**
	 * Obtiene el RFC del Firmante del Certificado.
	 *
	 * @param certB64 the cert B 64
	 * @return RFC del Firmante del Certificado
	 * @throws CertificateException the certificate exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private String getRfcSignerCertificate(String certB64) throws CertificateException, IOException {

		X509Certificate cert = (X509Certificate) SignatureUtil.getCertificateFormStringB64(certB64);

		log.debug("::: Ejecutando el metodo getRfcSignerCertificate(X509Certificate)");

		String signer = getInformation(cert.getSubjectDN().getName(), "OID.2.5.4.45=", ",");

		return "Unknown".equals(signer) ? "Unknown RFC" : signer;

	}

	/**
	 * Obtiene el correo del Firmante del Certificado.
	 *
	 * @param certB64 the cert B 64
	 * @return RFC del Firmante del Certificado
	 * @throws CertificateException the certificate exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private String getMailSignerCertificate(String certB64) throws CertificateException, IOException {

		X509Certificate cert = (X509Certificate) SignatureUtil.getCertificateFormStringB64(certB64);

		log.debug("::: Ejecutando el metodo getMailSignerCertificate(X509Certificate)");

		String signer = getInformation(cert.getSubjectDN().getName(), "EMAILADDRESS=", ",");

		return "Unknown".equals(signer) ? "Unknown RFC" : signer;

	}

	/**
	 * Extrae de una cadena de caracteres la informacion solicitada separando la
	 * cadena con la expresion.
	 *
	 * @param info    Cadena de caracteres donde se va a buscar la informacion
	 * @param keyInfo Clave que se va a buscar
	 * @param regExp  Separador de la cadena
	 * @return Valor asociado a la cadena buscada
	 */
	private String getInformation(String info, String keyInfo, String regExp) {

		log.debug("::: Ejecutando el metodo getInformation(String, String, String)");

		String infos[] = info.split(regExp);

		int i = 0;

		while (i < infos.length) {

			if (infos[i].contains(keyInfo)) {
				if ("OID.2.5.4.45=" == keyInfo) {
					String prueba[] = infos[i].split("/");
					if (prueba.length > 1) {
						prueba[0].replace(" ", "").trim();
						return prueba[0].replace(keyInfo, "").trim();
					} else {
						return infos[i].replace(keyInfo, "").trim();
					}

				} else {
					return infos[i].replace(keyInfo, "").trim();
				}
			}
			i++;
		}
		// No se encontro la clave solicitada
		return "Unknown";
	}

	/**
	 * Gets the user by email.
	 *
	 * @param email the email
	 * @return the user by email
	 */
	@SuppressWarnings("unused")
	private Usuario getUserByEmail(String email) {

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.ilike("email", email));

		List<?> list = mngrUsuario.search(restrictions);

		if (list.isEmpty())
			return null;
		else
			return (Usuario) list.get(0);

	}

	/**
	 * Se inicia el proceso de firma del acuse de recibo.
	 *
	 * @param idAsunto        the id asunto
	 * @param objectName      the object name
	 * @param contentB64      the content B 64
	 * @param crestificadoB64 the crestificado B 64
	 * @param email           the email
	 * @return the map
	 * @throws Exception the exception
	 */
	private Map<String, Object> beginFirmaLogin(Integer idAsunto, String objectName, String contentB64,
			String crestificadoB64, String email, String algoritmoFirma) throws Exception {

		try {

			Map<String, Object> result = new HashMap<String, Object>();

			// ---- UPLOAD
			Map<String, Object> uploadIdResponse = firmaEndPoint.uploadFile(//
					contentB64, //
					objectName, //
					TipoFirma.PDF_FIRMA, //
					SignContentType.PDF, null);

			log.debug("IdDocumento >> " + uploadIdResponse.get("IdDocumento"));
			Integer uploadId = Integer.parseInt(uploadIdResponse.get("IdDocumento").toString());

			// ---- INICIAR FIRMA
			Map<String, Object> responseStartSign = firmaEndPoint.startSign(uploadId, email, crestificadoB64,
					TipoFirma.PDF_FIRMA, SignContentType.PDF, algoritmoFirma, null, null, null, null, null, null);

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
	 * Gets the evidencia.
	 *
	 * @param idDocumento   the id documento
	 * @param tipoFirma     the tipo firma
	 * @param fileName      the file name
	 * @param isVersionable the is versionable
	 * @return the evidencia
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private File getEvidencia(Integer idDocumento, TipoFirma tipoFirma, String fileName, boolean isVersionable)
			throws IOException {

		Map<String, Object> firmaObject = firmaEndPoint.getFirma(new Long(idDocumento), tipoFirma);
		String firma = (String) firmaObject.get("Firma");

		File evidencia = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX + "FIRMA_",
				"_" + fileName + (isVersionable ? "_firmado.pdf" : "_pkcs7.p7m"));

		evidencia.deleteOnExit();

		byte[] firmaDecoded = Base64.decodeBase64(firma);

		FileUtils.writeByteArrayToFile(evidencia, firmaDecoded);

		return evidencia;
	}

	/**
	 * Validar firma process.
	 *
	 * @param uploadId        the upload id
	 * @param email           the email
	 * @param certB64         the cert B 64
	 * @param firmaB64        the firma B 64
	 * @param tipoFirma       the tipo firma
	 * @param signContentType the sign content type
	 * @return the map
	 * @throws Exception the exception
	 */
	private boolean validarFirmaProcess(Integer uploadId, String email, String certB64, String firmaB64,
			TipoFirma tipoFirma, SignContentType signContentType, String algoritmoFirma) throws Exception {

		try {

			Map<String, Object> firmaValida = firmaEndPoint.validateSign(uploadId, email, certB64, firmaB64, tipoFirma,
					signContentType, algoritmoFirma);

			log.debug("firmaValida >> " + firmaValida.get("isValid"));

			return firmaValida.get("isValid").equals(true) ? true : false;

		} catch (Exception e) {

			log.error(e.getMessage());

			throw e;
		}

	}

	/**
	 * Firma templates.
	 *
	 * @param objectId the object id
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/firma/templates", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<FirmaImpresaTemplate>> firmaTemplate(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		return firmaController.firmaTemplates(objectId);

	}

	/**
	 * Obtiene el archivo pkcs7 de un archivo firmado.
	 *
	 * @param objectId the object id
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/firma/evidencia", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> firmaEvidencia(
			@RequestParam(value = "objectId", required = true) String objectId) throws Exception {

		return firmaController.evidencia(objectId);

	}

	/**
	 * Firma impresa.
	 *
	 * @param body the body
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/e-ciudadano/firma/impresa", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> firmaImpress(@RequestBody(required = true) String body)
			throws Exception {

		return firmaController.firmaImpresa(body);

	}
}
