/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.cmisIntegracion.ws.util.JerseyConsumer;
import com.ecm.sigap.data.controller.impl.FirmaController;
import com.ecm.sigap.data.controller.impl.RepositoryController;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.eArchivo.ArchivoService;
import com.ecm.sigap.eArchivo.model.EArchivoArea;
import com.ecm.sigap.eArchivo.model.EArchivoDocumento;
import com.ecm.sigap.eArchivo.model.EArchivoExpediente;
import com.ecm.sigap.eArchivo.model.EArchivoFondoCuadro;
import com.ecm.sigap.eArchivo.model.EArchivoLegajo;
import com.ecm.sigap.eArchivo.model.EArchivoSerieSubserie;
import com.ecm.sigap.eArchivo.model.EArchivoTipoDocCat;
import com.ecm.sigap.eArchivo.model.EArchivoTipoDocCatalogo;
import com.ecm.sigap.eArchivo.model.EArchivoUnidad;
import com.ecm.sigap.security.util.Security;

/**
 * Implementacion del servicio de e-archivo.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Component("archivoService")
public final class ArchivoServiceImpl extends JerseyConsumer implements ArchivoService {
	private static final Logger log = LogManager.getLogger(ArchivoServiceImpl.class);
	/**
	 *
	 */
	@Autowired
	private RepositoryController repository;

	/**
	 * Configuracion global de la acplicacion.
	 */
	@Autowired
	private Environment environment;

	@Autowired
	private FirmaController firmaController;
	
	/** */
	protected static final ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages");

	/**
	 *
	 */
	public ArchivoServiceImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.archivo.ArchivoService#getExpedientes()
	 */
	@Override
	public JSONObject getExpedientes(EArchivoExpediente expediente, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws Exception {

		String url = environment.getProperty("e-archivo.url") + "/expediente/view";

		JSONObject json = new JSONObject();
		JSONObject params = new JSONObject();

		json.put("statusExpediente", "ABIERTO");
		if (expediente.getNumeroExpediente() != null) {
			json.put("idExpediente", expediente.getNumeroExpediente());
		}

		if (StringUtils.isNotBlank(expediente.getFolioExpediente())) {
			json.put("folioExpediente", expediente.getFolioExpediente());
		}

		if (StringUtils.isNotBlank(expediente.getAsuntoExpediente())) {
			json.put("asunto", expediente.getAsuntoExpediente());
		}

		if (expediente.getFechaApertura() != null) {
			params.put("fechaFinal", expediente.getFechaApertura());
			params.put("fechaInicial", expediente.getFechaApertura());
			params.put("tipoFecha", "fechaApertura");
		}

		if (StringUtils.isNotBlank(expediente.getTitulo())) {
			json.put("titulo", expediente.getTitulo());
		}

		if (Objects.nonNull(expediente.getProceso())) {
			json.put("procesoId", expediente.getProceso());
		}

		if (Objects.nonNull(expediente.getEliminado())) {
			json.put("eliminado", expediente.getEliminado());
		}

		if (StringUtils.isNotBlank(expediente.getCodigoAlterno())) {
			// json.put("CAMPO EN E-ARCHIVO EXPEDIENTE",
			// expediente.getCodigoAlterno());
		}

		if (StringUtils.isNotBlank(expediente.getUsuarioPropietario())) {
			// json.put("CAMPO EN E-ARCHIVO EXPEDIENTE",
			// expediente.getUsuarioPropietario);
		}

		if (StringUtils.isNotBlank(expediente.getSerieDocumental())) {
			// json.put("CAMPO EN E-ARCHIVO EXPEDIENTE",
			// expediente.getSerieDocumental());
		}

		if (StringUtils.isNotBlank(expediente.getClasificacionArchivistica())) {
			// json.put("CAMPO EN E-ARCHIVO EXPEDIENTE",
			// expediente.getClasificacionArchivistica());
		}

		// json.put("statusExpediente",
		// environment.getProperty("e-archivo.statusExpediente"));
		json.put("tipoArchivo", environment.getProperty("e-archivo.tipoArchivo"));

		JSONObject area = findLoginArea(userId, claveDepartamental);
		Integer idArea = area.getInt("idAreaProductora");
		json.put("idArea", idArea);

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea.toString()));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		if (Objects.nonNull(expediente.getSerieSubserie())) {

			JSONObject serieSubserie = findSerieSubserie(expediente.getSerieSubserie(), headers);

			if (serieSubserie != null) {

				JSONArray serieSubserieArray = new JSONArray(serieSubserie.get("entity").toString());
				JSONObject serie = ((JSONObject) serieSubserieArray.get(0));

				Integer idSerie = null;
				Integer idSubserie = null;

				try {
					if (serie.get("idSubserie") != null) {
						idSubserie = (Integer) serie.get("idSubserie");
					}
				} catch (JSONException e) {

					try {
						if (serie.get("idSerie") != null) {
							idSerie = (Integer) serie.get("idSerie");
						}
					} catch (JSONException ex) {

					}
				}

				String claveComp = (String) serie.get("claveComp");

				if (idSerie != null) {
					json.put("serieClaveComp", claveComp);
				} else if (idSubserie != null) {
					json.put("subserieClaveComp", claveComp);
				}
			}

		}

		JSONObject body = new JSONObject();
		body.put("object", json);
		body.put("params", params);

		return doPostJsonObject(url, body, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.archivo.ArchivoService#aplicarExpediente(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void aplicarExpediente(String objectId, String expedienteId) throws Exception {

		String url = environment.getProperty("e-archivo.url") //
				+ "/expediente/" + objectId + "/" + expedienteId + "/";

		doGet(url);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.eArchivo.ArchivoService#glosarDocumento(com.ecm.sigap.
	 * eArchivo.model.EArchivoDocumento, java.lang.String)
	 */
	@Override
	@Deprecated
	public JSONArray glosarDocumento(EArchivoDocumento documento, DocumentoAsunto oficioDoc, String userId,
			String claveDepartamental, String user_key, String contentUser, String authToken) throws Exception {

		String url = environment.getProperty("e-archivo.url");

		JSONObject doc = new JSONObject();

		ResponseEntity<Map<String, Object>> responseDocumentoAdjunto = repository.getDocument(documento.getObjectId(),
				null);

		String contentB64 = (String) responseDocumentoAdjunto.getBody().get("contentB64");
		String nombre = (String) responseDocumentoAdjunto.getBody().get("name");

		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		Integer idUnidad = null;
		if (Objects.nonNull(area)) {
			idUnidad = area.getJSONObject("unidad").getInt("idUnidad");
		}

		JSONArray legajos = findLegajo(new EArchivoLegajo(documento.getIdExpediente(), 0), claveDepartamental, userId,
				user_key, contentUser, authToken);

		if (legajos.length() == 0) {
			throw new Exception("Legajo 0 no encontrado.");
		}

		JSONObject legajo_ = legajos.getJSONObject(0);

		log.debug("LEGAJO 0 ::: " + legajo_.toString());

		doc.put("legajo", legajo_.getInt("idLegajo"));
		doc.put("unidad", new JSONObject().put("idUnidad", idUnidad));
		doc.put("descripcion", nombre.subSequence(0, nombre.lastIndexOf(".")));
		doc.put("activo", true);
		doc.put("docIcono", "fa fa-bookmark text-success");
		doc.put("docDefault", false);
		doc.put("valid", true);
		doc.put("asunto", oficioDoc.getAsuntoConsulta().getAsuntoDescripcion());
		doc.put("oficio", oficioDoc.getAsuntoConsulta().getNumDocto());
		doc.put("tradicion", environment.getProperty("e-archivo.tradicion"));
		doc.put("usuarioTitular", decryptText(userId));
		doc.put("usuarioRegistra", decryptText(userId));
		doc.put("docOrigen", environment.getProperty("e-archivo.docOrigen"));
		doc.put("tipoDocumental", environment.getProperty("e-archivo.tipoDocumental"));
		doc.put("tipoDocumentoAnexo", environment.getProperty("e-archivo.tipoDocumentoAnexo"));
		doc.put("clasificacion", environment.getProperty("e-archivo.clasificacion"));
		doc.put("numFojas", 0);
		doc.put("idExpediente", documento.getIdExpediente());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		JSONObject documentoDetalle = new JSONObject();
		documentoDetalle.put("areaProductora", area);
		documentoDetalle.put("docExternoSN", false);
		documentoDetalle.put("fechaElab", sdf.format(oficioDoc.getFechaRegistro()));
		documentoDetalle.put("fechaElabFinal", sdf.format(oficioDoc.getFechaRegistro()));
		documentoDetalle.put("extension",
				new JSONObject().put("idExtension", environment.getProperty("e-archivo.idExtension", Integer.class)));
		documentoDetalle.put("formato", environment.getProperty("e-archivo.formato"));
		documentoDetalle.put("idFirma", environment.getProperty("e-archivo.idFirma", Integer.class));
		documentoDetalle.put("lenguaje", environment.getProperty("e-archivo.lenguaje"));
		documentoDetalle.put("nombreElectronico", nombre);
		documentoDetalle.put("autor", oficioDoc.getAsuntoConsulta().getFirmanteAsunto());
		documentoDetalle.put("autorCargo", oficioDoc.getAsuntoConsulta().getFirmanteCargo());
		documentoDetalle.put("softwareObj", new JSONObject().put("selected",
				new JSONObject().put("idSoftware", environment.getProperty("e-archivo.idSoftware", Integer.class))));

		doc.put("documentoDetalle", documentoDetalle);

		boolean isFirmado = firmaController.isFirmado(documento.getObjectId());
		doc.put("tipoDocCatalogo",
				new JSONObject().put("idTipoDocCatalogo", isFirmado ? EArchivoTipoDocCat.ELEC_FIRMA_PDF.getStatus()
						: EArchivoTipoDocCat.ELEC_SIN_FIRMA.getStatus()));

		doc.put("fileB64", contentB64);

		List<JSONObject> list = new ArrayList<>();
		list.add(doc);

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		JSONObject docs = doPutJsonObject(url + "/documento?isForm=false", list, headers);

		return new JSONArray(docs.get("entity").toString());

	}

	@Override
	public JSONArray glosarDocumentoMultipart(EArchivoDocumento documento, DocumentoAsunto oficioDoc, String userId,
			String claveDepartamental, String user_key, String contentUser, String authToken) throws Exception {

		String url = environment.getProperty("e-archivo.url");

		File file;
		String nombre;

		{
			ResponseEntity<Map<String, Object>> responseDocumentoAdjunto = repository
					.getDocument(documento.getObjectId(), null);

			String contentB64 = (String) responseDocumentoAdjunto.getBody().get("contentB64");
			nombre = (String) responseDocumentoAdjunto.getBody().get("name");

			Path file_ = FileUtil.createTempFile(contentB64, nombre);

			file_ = Files.move(file_, file_.resolveSibling(nombre), StandardCopyOption.REPLACE_EXISTING);

			file = file_.toFile();
		}

		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		Integer idUnidad = null;
		if (Objects.nonNull(area)) {
			idUnidad = area.getJSONObject("unidad").getInt("idUnidad");
		}

		Integer numLegajo = documento.getNumeroLegajo() != null ? documento.getNumeroLegajo() : 0;
		JSONArray legajos = findLegajo(new EArchivoLegajo(documento.getIdExpediente(), numLegajo), claveDepartamental, userId,
				user_key, contentUser, authToken);

		if (legajos.length() == 0) {
			throw new Exception("Legajo "+ numLegajo +" no encontrado.");
		}

		JSONObject legajo_ = legajos.getJSONObject(0);

		log.debug("LEGAJO "+ numLegajo +" ::: " + legajo_.toString());

		JSONObject doc = new JSONObject();

		doc.put("legajo", legajo_.getInt("idLegajo"));
		doc.put("unidad", new JSONObject().put("idUnidad", idUnidad));
		doc.put("descripcion", nombre.subSequence(0, nombre.lastIndexOf(".")));
		doc.put("activo", true);
		doc.put("docIcono", "fa fa-bookmark text-success");
		doc.put("docDefault", false);
		doc.put("valid", true);
		doc.put("asunto", oficioDoc.getAsuntoConsulta().getAsuntoDescripcion());
		doc.put("oficio", oficioDoc.getAsuntoConsulta().getNumDocto());
		doc.put("tradicion", environment.getProperty("e-archivo.tradicion"));
		doc.put("usuarioTitular", decryptText(userId));
		doc.put("usuarioRegistra", decryptText(userId));
		doc.put("docOrigen", environment.getProperty("e-archivo.docOrigen"));
		doc.put("tipoDocumental", environment.getProperty("e-archivo.tipoDocumental"));
		doc.put("tipoDocumentoAnexo", environment.getProperty("e-archivo.tipoDocumentoAnexo"));
		doc.put("clasificacion", environment.getProperty("e-archivo.clasificacion"));
		doc.put("numFojas", 0);
		doc.put("idExpediente", documento.getIdExpediente());
		doc.put("valoresDoc", getValoresDoc(documento.getIdExpediente(), userId, claveDepartamental, user_key,
				contentUser, authToken));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		String formato = "PDF";
		int i = oficioDoc.getObjectName().lastIndexOf('.');
		
		if (i > 0) {
			formato = oficioDoc.getObjectName().substring(i+1);
		}
		
		JSONObject documentoDetalle = new JSONObject();
		documentoDetalle.put("areaProductora", area);
		documentoDetalle.put("docExternoSN", false);
		documentoDetalle.put("fechaElab", sdf.format(oficioDoc.getFechaRegistro()));
		documentoDetalle.put("fechaElabFinal", sdf.format(oficioDoc.getFechaRegistro()));
		documentoDetalle.put("formato", formato);
		documentoDetalle.put("idFirma", environment.getProperty("e-archivo.idFirma", Integer.class));
		documentoDetalle.put("lenguaje", environment.getProperty("e-archivo.lenguaje"));
		documentoDetalle.put("nombreElectronico", nombre);
		documentoDetalle.put("autor", oficioDoc.getAsuntoConsulta().getFirmanteAsunto());
		documentoDetalle.put("autorCargo", oficioDoc.getAsuntoConsulta().getFirmanteCargo());

		doc.put("documentoDetalle", documentoDetalle);

		boolean isFirmado = firmaController.isFirmado(documento.getObjectId());
		JSONObject idTipoDoc = new JSONObject();
		try {
			switch (documento.getTipoCatalogo()) {
				case 1:
					idTipoDoc.put("idTipoDocCatalogo", EArchivoTipoDocCat.FISICO_DIG.getStatus());
					break;
				case 2:
					idTipoDoc.put("idTipoDocCatalogo", EArchivoTipoDocCat.ELEC_FIRMA_PDF.getStatus());
					break;
				case 3:
					idTipoDoc.put("idTipoDocCatalogo", EArchivoTipoDocCat.ELEC_FIRMA_CMS.getStatus());
					break;
				case 4:
					idTipoDoc.put("idTipoDocCatalogo", EArchivoTipoDocCat.ELEC_FIRMA_XML.getStatus());
					break;
				case 5:
					idTipoDoc.put("idTipoDocCatalogo", EArchivoTipoDocCat.ELEC_SIN_FIRMA.getStatus());
					break;
				default:
					idTipoDoc.put("idTipoDocCatalogo", isFirmado ? 
							EArchivoTipoDocCat.ELEC_FIRMA_PDF.getStatus() :
								EArchivoTipoDocCat.ELEC_SIN_FIRMA.getStatus());
			}
		} catch (Exception e) {
			idTipoDoc.put("idTipoDocCatalogo", isFirmado ? 
					EArchivoTipoDocCat.ELEC_FIRMA_PDF.getStatus() :
						EArchivoTipoDocCat.ELEC_SIN_FIRMA.getStatus());
		}
		
		doc.put("tipoDocCatalogo", idTipoDoc);

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		JSONObject docs = doPostMultiPart(url + "/documento/form", doc, file, headers);

		file.delete();

		return new JSONArray(docs.get("entity").toString());

	}

	/**
	 * 
	 * @param documento
	 * @param userId
	 * @param claveDepartamental
	 * @param user_key
	 * @param contentUser
	 * @param authToken
	 * @throws Exception
	 */
	private JSONArray getValoresDoc(Integer idExpediente, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception {

		JSONArray b = new JSONArray();
		try {
			JSONObject items_ = getExpedienteFull(idExpediente, claveDepartamental, userId, user_key, contentUser,
					authToken);
			String expediente_ = (String) items_.get("entity");

			JSONObject expediente = (new JSONArray(expediente_)).getJSONObject(0);
			b = expediente.getJSONArray("expedienteValor");

		} catch (Exception e) {
			
		}
		return b;
	}

	/**
	 * 
	 * @param eArchivoExpediente
	 * @param claveDepartamental
	 * @param userId
	 * @param user_key
	 * @param contentUser
	 * @param authToken
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private JSONObject getExpedienteFull(Integer idExpediente, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws ClientProtocolException, IOException {

		String url = environment.getProperty("e-archivo.url") + "/expediente";

		JSONObject area = findLoginArea(userId, claveDepartamental);
		Integer idArea = area.getInt("idAreaProductora");

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea.toString()));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		JSONObject exp_ = new JSONObject();
		exp_.put("idExpediente", idExpediente);

		JSONObject body = new JSONObject();
		body.put("object", exp_);

		return doPostJsonObject(url, body, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.eArchivo.ArchivoService#getUnidad(com.ecm.sigap.eArchivo.
	 * model.EArchivoUnidad, java.lang.String)
	 */
	@Override
	public JSONObject getUnidad(EArchivoUnidad unidad, String userId) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();

		if (unidad.getIdUnidad() != null) {
			json.put("idUnidad", unidad.getIdUnidad());
		}
		if (unidad.getDescripcion() != null) {
			json.put("descripcion", unidad.getDescripcion());
		}
		if (unidad.getTitutlar() != null) {
			json.put("titutlar", unidad.getTitutlar());
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);

		return doPostJsonObject(url.concat("/unidad"), json, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.eArchivo.ArchivoService#getTipoCatalogo(com.ecm.sigap.
	 * eArchivo.model.EArchivoTipoDocCatalogo, java.lang.String)
	 */
	@Override
	public JSONObject getTipoCatalogo(EArchivoTipoDocCatalogo tipocatalogo, String userId) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();

		if (tipocatalogo.getIdTipoDocCatalogo() != null) {
			json.put("idTipoDocCatalogo", tipocatalogo.getIdTipoDocCatalogo());
		}
		if (tipocatalogo.getDescripcion() != null) {
			json.put("descripcion", tipocatalogo.getDescripcion());
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);

		return doPostJsonObject(url.concat("/tipoDocCatalogo"), json, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.eArchivo.ArchivoService#findArea(com.ecm.sigap.eArchivo.
	 * model.EArchivoArea, java.lang.String)
	 */
	@Override
	public JSONObject findArea(EArchivoArea area, String userId) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();

		if (area.getIdArea() != null) {
			json.put("idAreaProductora", area.getIdArea());
		}
		if (StringUtils.isNotBlank(area.getDescripcion())) {
			json.put("descripcion", area.getDescripcion());
		}
		if (StringUtils.isNotBlank(area.getClavePresupuestal())) {
			json.put("clavePresupuestal", area.getClavePresupuestal());
			json.put("exactSearch", true);
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);

		return doPostJsonObject(url.concat("/areaProductora"), json, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.eArchivo.ArchivoService#findProceso(com.ecm.sigap.eArchivo.
	 * model.EArchivoExpediente, java.lang.String)
	 */
	@Override
	public JSONObject findProceso(EArchivoExpediente exp, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();
		json.put("descripcion", exp.getProceso());

		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		return doPostJsonObject(url.concat("/proceso"), json, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.eArchivo.ArchivoService#getSerie(com.ecm.sigap.eArchivo.
	 * model.EArchivoSerieSubserie, java.lang.String)
	 */
	@Override
	public JSONObject getSerie(EArchivoSerieSubserie serie, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();
		JSONObject body = new JSONObject();

		if (serie.getSerieSubserie() != null) {
			json.put("descripcion", serie.getSerieSubserie());
		}

		body.put("object", json);

		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		return doPostJsonObject(url.concat("/serie"), body, headers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.eArchivo.ArchivoService#getSerieAcceso(com.ecm.sigap.
	 * eArchivo. model.EArchivoSerieSubserie, java.lang.String, java.lang.String)
	 */
	@Override
	public JSONObject getSerieAcceso(EArchivoSerieSubserie serie, String userId, String clave, String user_key,
			String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");
		JSONObject area = findLoginArea(userId, clave);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		JSONObject json = new JSONObject();

		if (serie.getSerieSubserie() != null) {
			json.put("descripcion", serie.getSerieSubserie());
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		return doPostJsonObject(url.concat("/serie/acceso"), json, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.eArchivo.ArchivoService#getSubserie(com.ecm.sigap.eArchivo.
	 * model.EArchivoSerieSubserie, java.lang.String)
	 */
	@Override
	public JSONObject getSubserie(EArchivoSerieSubserie subserie, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();
		JSONObject body = new JSONObject();

		if (subserie.getSerieSubserie() != null) {
			json.put("descripcion", subserie.getSerieSubserie());
		}

		body.put("object", json);

		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		return doPostJsonObject(url.concat("/subserie"), body, headers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.eArchivo.ArchivoService#getSubserieAcceso(com.ecm.sigap.
	 * eArchivo.model.EArchivoSerieSubserie, java.lang.String, java.lang.String)
	 */
	@Override
	public JSONObject getSubserieAcceso(EArchivoSerieSubserie subserie, String userId, String clave, String user_key,
			String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");
		JSONObject area = findLoginArea(userId, clave);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		JSONObject json = new JSONObject();

		if (subserie.getSerieSubserie() != null) {
			json.put("descripcion", subserie.getSerieSubserie());
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		return doPostJsonObject(url.concat("/subserie/acceso"), json, headers);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.eArchivo.ArchivoService#findSerieSubserie(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public JSONObject findSerieSubserie(Object serieSubserie, Map<String, String> headers) throws Exception {
		String url = environment.getProperty("e-archivo.url");

		JSONObject json = new JSONObject();

		json.put("object", serieSubserie);

		JSONObject serie = doPostJsonObject(url.concat("/serie"), json, headers);
		JSONObject subserie = doPostJsonObject(url.concat("/subserie"), json, headers);
		JSONArray serieArray = new JSONArray(serie.get("entity").toString());
		JSONArray subserieArray = new JSONArray(subserie.get("entity").toString());

		if (serieArray.length() != 0) {
			return serie;
		} else if (subserieArray.length() != 0) {
			return subserie;
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.eArchivo.ArchivoService#findLegajo(com.ecm.sigap.eArchivo.
	 * model.EArchivoLegajo, java.lang.String)
	 */
	@Override
	public JSONArray findLegajo(EArchivoLegajo legajo, String claveDepartamental, String userId, String user_key,
			String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");
		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));
		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		JSONObject body = new JSONObject();
		body.put("idExpediente", legajo.getIdExpediente());
		body.put("numero", legajo.getNumero());
		JSONObject result = doPostJsonObject(url.concat("/legajo"), body, headers);
		return new JSONArray(result.get("entity").toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.eArchivo.ArchivoService#getStatusExpediente(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public JSONObject getStatusExpediente(String objectId, String userId, String claveDepartamental, String user_key,
			String contentUser, String authToken) throws Exception {
		String url = environment.getProperty("e-archivo.url");
		JSONObject area = findLoginArea(userId, claveDepartamental, "status");

		if (area.length() == 0)
			return area;
		else {
			String idArea = Integer.toString(area.getInt("idAreaProductora"));

			Map<String, String> headers = new HashMap<>();
			headers.put("archivo-user-id", userId);
			headers.put("archivo-acceso-id", encryptText(idArea));
			headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
			headers.put("archivo-user-key", user_key);
			headers.put("archivo-content-user", contentUser);
			headers.put("archivo-token", authToken);

			return doGetJsonObject(String.format("%s/expediente/documento/%s/status", url, objectId), null, headers);
		}
	}

	/**
	 * 
	 * @param userId
	 * @param claveDepartamental
	 * @param type
	 * @return
	 */
	private JSONObject findLoginArea(String userId, String claveDepartamental, String type) {
		EArchivoArea ea = new EArchivoArea();
		ea.setClavePresupuestal(claveDepartamental);
		JSONArray area = null;
		try {
			JSONObject areasArcvhivo = findArea(ea, userId);
			area = new JSONArray(areasArcvhivo.get("entity").toString());
		} catch (Exception ex) {
			log.info(String.format("Area %s no encontrada ", claveDepartamental));
		}

		if (Objects.isNull(area) || area.length() < 1) {
			if (type.equals("status"))
				return new JSONObject();
			else
				throw new RuntimeException(String.format(errorMessages.getString("errorAreaIntegracionArchivo")));
		} else if (area.length() > 1) {
			throw new RuntimeException(
					String.format("ARCHIVISTIVA: Se encontro más de una clave: ", claveDepartamental));
		}
		return area.getJSONObject(0);
	}

	/**
	 * 
	 * @param userId
	 * @param claveDepartamental
	 * @return
	 */
	private JSONObject findLoginArea(String userId, String claveDepartamental) {
		EArchivoArea ea = new EArchivoArea();
		ea.setClavePresupuestal(claveDepartamental);
		JSONArray area = null;
		try {
			JSONObject areasArcvhivo = findArea(ea, userId);
			area = new JSONArray(areasArcvhivo.get("entity").toString());
		} catch (Exception ex) {
			log.info(String.format("Area %s no encontrada ", claveDepartamental));
		}

		if (Objects.isNull(area) || area.length() < 1) {
			throw new RuntimeException(errorMessages.getString("errorAreaIntegracionArchivo"));
		} else if (area.length() > 1) {
			throw new RuntimeException(
					String.format("ARCHIVISTIVA: Se encontro más de una clave: ", claveDepartamental));
		}
		return area.getJSONObject(0);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	protected String encryptText(String value) {

		try {

			return Security.encript(value);

		} catch (Exception e) {
			
			return value;
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	protected String decryptText(String value) {
		try {
			return Security.decript(value);

		} catch (Exception e) {
			
			return value;
		}
	}

	@Override
	public JSONObject getUsuario(String userId, String claveDepartamental, String user_key, String contentUser,
			String authToken, String idUsuario) throws Exception {

		String url = environment.getProperty("e-archivo.url") + "/usuario";
		JSONObject area = findLoginArea(userId, claveDepartamental);

		if (area.length() == 0)
			return area;
		else {

			HashMap<String, Object> params = new HashMap<>();
			params.put("id", idUsuario);

			String idArea = Integer.toString(area.getInt("idAreaProductora"));

			Map<String, String> headers = new HashMap<>();
			headers.put("archivo-user-id", userId);
			headers.put("archivo-acceso-id", encryptText(idArea));
			headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
			headers.put("archivo-user-key", user_key);
			headers.put("archivo-content-user", contentUser);
			headers.put("archivo-token", authToken);

			return doGetJsonObject(url, params, headers);
		}

	}

	@Override
	public JSONObject getFondo(String userId, String claveDepartamental, String user_key, String contentUser,
			String authToken, EArchivoFondoCuadro fondo) {

		String url = environment.getProperty("e-archivo.url") + "/cuadroArchivistica/fondo";
		JSONObject area = findLoginArea(userId, claveDepartamental, "status");

		if (area.length() == 0)
			return area;
		else {

			String idArea = Integer.toString(area.getInt("idAreaProductora"));

			Map<String, String> headers = new HashMap<>();
			headers.put("archivo-user-id", userId);
			headers.put("archivo-acceso-id", encryptText(idArea));
			headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
			headers.put("archivo-user-key", user_key);
			headers.put("archivo-content-user", contentUser);
			headers.put("archivo-token", authToken);

			JSONObject params = new JSONObject();

			if (fondo.getActivo() != null) {
				params.put("activo", fondo.getActivo());
			}

			JSONObject result = null;
			try {
				result = doPostJsonObject(url, params, headers);
			} catch (ClientProtocolException e) {
				
			} catch (IOException e) {
				
			}

			return result;
		}

	}

	@Override
	public JSONObject getSerieAccesoFolio(EArchivoSerieSubserie serie, String userId, String claveDepartamental,
			String user_key, String contentUser, String authToken) {
		String url = environment.getProperty("e-archivo.url");
		JSONObject area = findLoginArea(userId, claveDepartamental);

		if (area.length() == 0)
			return area;
		else {

			String idArea = Integer.toString(area.getInt("idAreaProductora"));

			JSONObject params = new JSONObject();

			if (serie.getSerieSubserie() != null) {
				params.put("descripcion", serie.getSerieSubserie());
			}

			Map<String, String> headers = new HashMap<>();
			headers.put("archivo-user-id", userId);
			headers.put("archivo-acceso-id", encryptText(idArea));
			headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
			headers.put("archivo-user-key", user_key);
			headers.put("archivo-content-user", contentUser);
			headers.put("archivo-token", authToken);

			JSONObject result = null;

			try {
				result = doPostJsonObject(url.concat("/serie/acceso/folio"), params, headers);
			} catch (ClientProtocolException e) {
				
			} catch (IOException e) {
				
			}

			return result;
		}
	}

	@Override
	public JSONObject getSeriesSubseries(EArchivoSerieSubserie seriesubserie, String userId, String clave,
			String user_key, String contentUser, String authToken) throws Exception {

		JSONObject area = findLoginArea(userId, clave);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		JSONObject json = new JSONObject();

		if (seriesubserie.getSerieSubserie() != null) {
			json.put("descripcion", seriesubserie.getSerieSubserie());
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		JSONObject serieSubserie = findSerieSubserie(json, headers);

		return serieSubserie;
	}

	@Override
	public JSONObject getCurrentAcceso(String userId, String claveDepartamental) {
		JSONObject area = findLoginArea(userId, claveDepartamental);
		return area;
	}

	@Override
	public JSONObject saveExpediente(HashMap<String, Object> expediente, String claveDepartamental, String userId,
			String user_key, String contentUser, String authToken) throws Exception {

		String url = environment.getProperty("e-archivo.url");

		JSONObject area = findLoginArea(userId, claveDepartamental);
		String idArea = Integer.toString(area.getInt("idAreaProductora"));

		Map<String, String> headers = new HashMap<>();
		headers.put("archivo-user-id", userId);
		headers.put("archivo-acceso-id", encryptText(idArea));
		headers.put("archivo-tipo-acceso", encryptText("AREAPROD"));
		headers.put("archivo-user-key", user_key);
		headers.put("archivo-content-user", contentUser);
		headers.put("archivo-token", authToken);

		JSONObject jsonExpediente = new JSONObject();
		jsonExpediente.put("aniosConc", expediente.get("aniosConc"));
		jsonExpediente.put("aniosTramite", expediente.get("aniosTramite"));
		jsonExpediente.put("apertura", expediente.get("apertura"));
		jsonExpediente.put("archivo", expediente.get("archivo"));
		jsonExpediente.put("areaProductora", expediente.get("areaProductora"));
		jsonExpediente.put("asunto", expediente.get("asunto"));
		jsonExpediente.put("docActivoSN", expediente.get("docActivoSN"));
		jsonExpediente.put("expedienteValor", expediente.get("expedienteValor"));
		jsonExpediente.put("fechaApertura", expediente.get("fechaApertura"));
		jsonExpediente.put("folioAutomatico", expediente.get("folioAutomatico"));
		jsonExpediente.put("folioExpediente", expediente.get("folioExpediente"));
		jsonExpediente.put("mesesConc", expediente.get("mesesConc"));

		jsonExpediente.put("mesesTramite", expediente.get("mesesTramite"));
		jsonExpediente.put("noInventarioExpediente", expediente.get("noInventarioExpediente"));
		jsonExpediente.put("observaciones", expediente.get("observaciones"));
		jsonExpediente.put("proceso", expediente.get("proceso"));
		jsonExpediente.put("procesoId", expediente.get("procesoId"));
		jsonExpediente.put("serie", expediente.get("serie"));
		jsonExpediente.put("subserie", expediente.get("subserie"));
		jsonExpediente.put("tecnica", expediente.get("tecnica"));
		jsonExpediente.put("titulo", expediente.get("titulo"));
		jsonExpediente.put("unidadExpediente", expediente.get("unidadExpediente"));
		jsonExpediente.put("unidadExpedienteApertura", expediente.get("unidadExpedienteApertura"));
		jsonExpediente.put("usuario", expediente.get("usuario"));
		jsonExpediente.put("usuarioCierraExpe", expediente.get("usuarioCierraExpe"));
		jsonExpediente.put("usuarioRegistra", expediente.get("usuarioRegistra"));
		jsonExpediente.put("valores", expediente.get("valores"));
		jsonExpediente.put("vinculos", expediente.get("vinculos"));

		JSONObject newExpediente = doPutJsonObjectEArchivo(url + "/expediente", jsonExpediente, headers);

		return newExpediente;
	}

}
