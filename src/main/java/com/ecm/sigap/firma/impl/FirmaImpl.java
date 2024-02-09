/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.firma.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.chemistry.opencmis.commons.impl.json.JSONArray;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ecm.cmisIntegracion.ws.util.JerseyConsumer;
import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.TipoFirma;
import com.ecm.sigap.data.util.TspUtility;
import com.ecm.sigap.firma.FirmaService;

/**
 * Servicio de firma digital.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Component("firmaService")
public final class FirmaImpl extends JerseyConsumer implements FirmaService {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(FirmaImpl.class);
	
	/** */
	protected static final ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages");

	/** */
	@Autowired
	private Environment environment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#uploadFile(java.lang.String,
	 * java.lang.String, com.ecm.sigap.data.model.util.TipoFirma,
	 * com.ecm.sigap.data.model.util.SignContentType)
	 */
	@Override
	public Map<String, Object> uploadFile(String fileB64, String fileName, TipoFirma tipoFirma,
			SignContentType signContentType, String objectId) throws ClientProtocolException, IOException {

		log.debug(" Cargando documento a servicio de firma digital...");
		log.debug(fileName + " << " + tipoFirma.getTipo());

		String postUrl = environment.getProperty("urlSignatureService") + "/SubirDocumento" + getFirmaTipoConf();

		JSONObject json = new JSONObject();

		json.put("contentType", signContentType.getValue());
		json.put("data", fileB64);
		json.put("name", fileName);
		json.put("signatureType", tipoFirma.getTipo());
		json.put("algoritmoHash", "MD5");
		json.put("idOrigen", objectId);

		// json.put("firmadoPor", "el texto q reemplaza....");

		Map<String, Object> response = null;
		try {
			response = doPost(postUrl, json);
		} catch (ConnectException e) {
			throw new ConnectException(errorMessages.getString("servicioFirmaNoDisponible"));
		}

		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#startSign(java.lang.Integer,
	 * java.lang.String, java.lang.String, com.ecm.sigap.data.model.util.TipoFirma,
	 * com.ecm.sigap.data.model.util.SignContentType)
	 */
	@Override
	public Map<String, Object> startSign(Integer id, String email, String certB64, TipoFirma tipoFirma,
			SignContentType signContentType, String algoritmoFirma, Integer coordenadax, Integer coordenaday,
			Integer onpage, String firmaB64IMG, String nivelUI, String cargo)
			throws ClientProtocolException, IOException {

		log.debug("Iniciando proceso de firma...");
		log.debug(">>" + id);

		String postUrl = environment.getProperty("urlSignatureService") + "/IniciarFirma" + getFirmaTipoConf();

		JSONObject signable = new JSONObject();
		JSONObject firmaImg = new JSONObject();
		boolean isFirmaAvanzada = tipoFirma.getTipo().equalsIgnoreCase("PDF_FIRMA_IMG")
				|| tipoFirma.getTipo().equalsIgnoreCase("PDF_ANTEFIRMA") && nivelUI != null;
		signable.put("contentType", signContentType.getValue());
		signable.put("signatureType", tipoFirma.getTipo());
		signable.put("identificador", id);
		signable.put("algoritmoFirma", algoritmoFirma);
		if (isFirmaAvanzada) {
			firmaImg.put("x", coordenadax);
			firmaImg.put("y", coordenaday);
			firmaImg.put("height", 0);
			firmaImg.put("width", 0);
			firmaImg.put("page", onpage);
			firmaImg.put("b64", firmaB64IMG);
			firmaImg.put("nivelUI", nivelUI);
			firmaImg.put("cargo", cargo);
			signable.put("firmaImg", firmaImg);
		}

		JSONObject json = new JSONObject();
		json.put("anexo", signable);
		json.put("certificado", certB64);

		Map<String, String> headers = new HashMap<>();
		headers.put("email", email);

		Map<String, Object> response = doPost(postUrl, json, headers);

		return response;
	}

	/**
	 * 
	 * @return
	 */
	private String getFirmaTipoConf() {
		String tipoConf = environment.getProperty("tsp.firma");
		String param = (StringUtils.isNotBlank(tipoConf) ? "?tipoConf=" + tipoConf : "");
		return param;
	}

	/**
	 * 
	 * @return
	 */
	private String getParamsToFirma(String iddoumento, String idRepoNuevo) {
		String param = ("?id=" + iddoumento + "&" + "rObjectId=" + idRepoNuevo);
		return param;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#validateSign(java.lang.Integer,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * com.ecm.sigap.data.model.util.TipoFirma,
	 * com.ecm.sigap.data.model.util.SignContentType)
	 */
	@Override
	public Map<String, Object> validateSign(Integer id, String email, String certB64, String firmaB64,
			TipoFirma tipoFirma, SignContentType signContentType, String algoritmoFirma)
			throws ClientProtocolException, IOException, DecoderException {

		log.debug("Iniciando proceso de validacion de firma...");
		log.debug(">>" + id);

		String postUrl = environment.getProperty("urlSignatureService") + "/ValidarFirma" + getFirmaTipoConf();

		JSONObject signable = new JSONObject();
		signable.put("contentType", signContentType.getValue());
		signable.put("signatureType", tipoFirma.getTipo());
		signable.put("identificador", id);

		signable.put("algoritmoFirma", algoritmoFirma);

		// indicador de app/modulo que consume la firma
		signable.put("consumidor", environment.getProperty("firmacore.consumer"));

		JSONObject firma = new JSONObject();
		firma.put("firma", firmaB64);

		JSONArray firmas = new JSONArray();
		firmas.add(firma);
		signable.put("firmas", firmas);

		JSONObject json = new JSONObject();
		json.put("anexo", signable);
		json.put("certificado", certB64);

		Map<String, String> headers = new HashMap<>();
		headers.put("email", email);

		Map<String, Object> response = doPost(postUrl, json, headers);

		response.put("isValid", true);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#getDocumentoFirmado(java.lang.Integer)
	 */
	@Override
	public Map<String, Object> getDocumentoFirmado(Integer id) throws UnsupportedOperationException, IOException {

		log.debug("Obtener Documento Firmado...");
		log.debug(">>" + id);

		String getUrl = environment.getProperty("urlSignatureService") + "/documentoFirmado";

		Map<String, Object> params = new HashMap<>();

		params.put("id", id);

		Map<String, Object> response = doGet(getUrl, params);

		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#getFirma(java.lang.Long,
	 * com.ecm.sigap.data.model.util.TipoFirma)
	 */
	@Override
	public Map<String, Object> getFirma(Long idDocumento, TipoFirma tipoFirma)
			throws UnsupportedOperationException, IOException {

		String getUrl = environment.getProperty("urlSignatureService") + "/ObtenerFirma" + getFirmaTipoConf();

		Map<String, Object> params = new HashMap<>();

		params.put("IdDocumento", idDocumento);
		params.put("TipoFirma", tipoFirma.getTipo());

		Map<String, Object> response = doGet(getUrl, params);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#getTime(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Map<String, Object> getTime(String data, String tipo) throws UnsupportedOperationException, IOException {

		Map<String, Object> response = new HashMap<>();

		// valida por donde va obtener la estampa, atraves de firma-core o directo al
		// TSP
		if (Boolean.parseBoolean(environment.getProperty("timestamp.firmacore.enabled"))) {
			StringBuilder url = new StringBuilder(environment.getProperty("urlSignatureService"));
			url.append("/ObtenerTiempo?toBeStamped=");
			url.append(URLEncoder.encode(data, "UTF-8"));
			if (StringUtils.isNotBlank(tipo)) {
				url.append("&tipoConf=").append(tipo);
			}
			log.info(" URL STAMPA DE TIPO :: " + url.toString());
			log.info(" URL STAMPA DE TIPO :: " + url.toString());
			log.info(" URL STAMPA DE TIPO :: " + url.toString());
			log.info(" URL STAMPA DE TIPO :: " + url.toString());
			log.info(" URL STAMPA DE TIPO :: " + url.toString());
			log.info(" URL STAMPA DE TIPO :: " + url.toString());
			response = doGet(url.toString());
		} else {
			/**
			 * invoca directo al TSP
			 */
			if (StringUtils.isBlank(tipo)) {
				tipo = "ecm_solutions";
				log.warn("::: Proveedor de estampa indefinido, usando default: ecm_solutions");
			} else {
				log.debug("::: Proveedor de estampa: " + tipo);
			}

			try {
				// Llamamos al TSP para obtener la informacion de la fecha
				byte[] currentTime = TspUtility
						.getInstance(DigestUtils.sha1(data), BigInteger.valueOf(System.currentTimeMillis()), tipo)
						.getTimeStampToken();

				response.put("Tiempo", Base64.encodeBase64String(currentTime));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				log.error("ocurrio un error al consultar la fecha del TSP");
			}
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.firma.FirmaService#getRepresentacionImpresaPlantilla(java.
	 * lang.Long, com.ecm.sigap.data.model.util.TipoFirma, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getRepresentacionImpresaPlantilla(Long idDocumento, TipoFirma tipoFirma, String tipoConf,
			String evidence, String signedData) throws UnsupportedOperationException, IOException {

		String postUrl = environment.getProperty("urlSignatureService") + "/ObtenerEvidenciaVisualCustom";

		JSONObject json = new JSONObject();
		json.put("idDocumento", idDocumento);
		json.put("signatureType", tipoFirma.getTipo());
		json.put("tipoConf", tipoConf);
		json.put("evidence", (evidence == null ? "" : evidence));
		json.put("signedData", signedData);

		Map<String, String> headers = new HashMap<>();

		Map<String, Object> response = doPost(postUrl, json, headers);

		return response;
	}

	@Override
	public Map<String, Object> setRObjectOnFirma(Integer idDocumento, String rObject)
			throws UnsupportedOperationException, IOException {

		String putUrl = environment.getProperty("urlSignatureService") + "/documentoFirmado"
				+ getParamsToFirma(idDocumento.toString(), rObject);
		JSONObject json = new JSONObject();

		Map<String, Object> response = doPost(putUrl, json);

		return response;
	}

}
