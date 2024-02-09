/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.impl;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Map;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.xml.sax.SAXException;

import com.ecm.sigap.firma.FirmaService;
import com.ecm.sigap.ope.client.exception.AlreadyRegisteredException;
import com.ecm.sigap.ope.client.exception.HashInSignatureNoMatchException;
import com.ecm.sigap.ope.client.exception.UnknownURLException;
import com.ecm.sigap.ope.dao.model.Configuration;
import com.ecm.sigap.ope.dao.model.Mensaje;
import com.ecm.sigap.ope.dao.model.RegistroInstancia;
import com.ecm.sigap.ope.dao.service.RepositoryConfiguration;
import com.ecm.sigap.ope.dao.service.RepositoryEnviarTramite;
import com.ecm.sigap.ope.dao.service.RepositoryMensaje;
import com.ecm.sigap.ope.dao.service.RepositoryRegistroInstancia;
import com.ecm.sigap.ope.dao.service.RepositorySincronizacionDirectorio;
import com.ecm.sigap.ope.model.OpeHeaders;
import com.ecm.sigap.ope.security.SecurityUtilOPE;
import com.ecm.sigap.util.SignatureUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Metodos generales para el controller de la oficina postal electronica,
 * 
 * @author Alfredo Morales
 *
 */
public abstract class OpeControler {

	/** */
	@Autowired
	@Qualifier("firmaService")
	protected FirmaService firmaEndPoint;

	/** Repositorio para el tipo {@link RegistroInstancia} */
	@Autowired
	@Qualifier("repositoryRegistroInstancia")
	protected RepositoryRegistroInstancia repoRegistroInstancia;

	/** Repositorio para el tipo {@link Mensaje} */
	@Autowired
	@Qualifier("repositoryMensaje")
	protected RepositoryMensaje repoMensaje;

	/** Repositorio para el tipo {@link Configuration} */
	@Autowired
	@Qualifier("repositoryConfiguration")
	protected RepositoryConfiguration repoOPEConfig;
	
	/** Repositorio para el tipo {@link SincronizacionDirectorio} */
	@Autowired
	@Qualifier("repositorySincronizacionDirectorio")
	protected RepositorySincronizacionDirectorio repoSincronizacionDirectorio;
	
	@Autowired
	@Qualifier("repositoryEnviarTramite")
	protected RepositoryEnviarTramite repoEnviarTramite;
	/**
	 * 
	 */
	public OpeControler() {
		super();
	}

	/**
	 * se valdia q el hash del body y el hash en el mensaje firmado coincidan,
	 * 
	 * @param jsonObject
	 * @param firma
	 * @throws NoSuchAlgorithmException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws HashInSignatureNoMatchException El mensaje ha sido modificado en el
	 *                                         transporte,
	 */
	protected void validateHash(JSONObject jsonObject, String firma) throws NoSuchAlgorithmException,
			JsonParseException, JsonMappingException, IOException, HashInSignatureNoMatchException {

		SecurityUtilOPE.validateHash(jsonObject.toString(), firma);

	}

	/**
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws MarshalException
	 * @throws XMLSignatureException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableEntryException
	 * @throws TransformerException
	 */
	protected MultiValueMap<String, String> createResponseHeaders(Object object)
			throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, SAXException,
			ParserConfigurationException, MarshalException, XMLSignatureException, KeyStoreException,
			CertificateException, UnrecoverableEntryException, TransformerException {

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

		final String hashDelBody = SecurityUtilOPE.generateHash(object);

		final String sign = SecurityUtilOPE.createSignature(hashDelBody);

		headers.add(OpeHeaders.HEADER_FIRMA_BODY, sign);

		return headers;
	}

	/**
	 * 
	 * Obtiene la fecha actual del servicio de firma ecm,
	 * 
	 * @param toStamp la cadena de texto a ser estampada,
	 * @return
	 * @throws IOException
	 * @throws TSPException
	 * @throws CMSException
	 */
	protected Date getNow(String toStamp) throws IOException, TSPException, CMSException {
		Map<String, Object> time_ = firmaEndPoint.getTime(toStamp, null);
		String timestamp = (String) time_.get("Tiempo");
		Date now = SignatureUtil.timestampToDate(timestamp);
		return now;
	}

	/**
	 * Validar la url de la instancia solicitante, si ya se ecnuentra registrada,
	 * 
	 * @param body
	 * @param isRecibirSubscripcion
	 * @throws UnknownURLException
	 * @throws AlreadyRegisteredException
	 */
	protected void validateRequestUrl(JSONObject body, boolean isRecibirSubscripcion)
			throws UnknownURLException, AlreadyRegisteredException {

		// validar destinatario/remitente validos,
		RegistroInstancia so = new RegistroInstancia();
		so.setUrl(body.getString("url"));
		RegistroInstancia search_ = repoRegistroInstancia.searchSingle(so);

		if (search_ == null && !isRecibirSubscripcion)
			throw new UnknownURLException(); // si el origen no esta registrado se rechaza,
		else if (search_ != null && isRecibirSubscripcion)
			throw new AlreadyRegisteredException(); // se intenta registrar ya registrado

	}

	/**
	 * Se registra en la tabla de log de mensajes la respuesta y su firma,
	 * 
	 * @param uuid
	 * @param respuesta
	 * @param firmaRespuesta
	 */
	protected void logResponse(String uuid, String respuesta, String firmaRespuesta) {

		Mensaje mensaje = repoMensaje.fetch(uuid);
		mensaje.setRespuesta(StringUtils.substring(respuesta, 0, 249)); // TODO remover substring
		mensaje.setRespuestaFirma(StringUtils.substring(firmaRespuesta, 0, 249)); // TODO remover substring
		repoMensaje.saveOrUpdate(mensaje);

	}

	/**
	 * Se registra en la tabla de log de mensajes el body del request,
	 * 
	 * @param uuid
	 * @param message
	 */
	protected void logMessageBody(String uuid, String message) {

		Mensaje mensaje = repoMensaje.fetch(uuid);
		mensaje.setMensaje(StringUtils.substring(message, 0, 249)); // TODO remover substring
		repoMensaje.saveOrUpdate(mensaje);

	}

}
