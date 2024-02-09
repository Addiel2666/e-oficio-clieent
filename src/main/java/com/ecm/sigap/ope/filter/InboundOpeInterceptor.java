/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.filter;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ecm.sigap.ope.dao.model.Mensaje;
import com.ecm.sigap.ope.dao.service.RepositoryMensaje;
import com.ecm.sigap.ope.dao.service.RepositoryRegistroInstancia;
import com.ecm.sigap.ope.model.OpeHeaders;
import com.ecm.sigap.ope.security.SecurityUtilOPE;

/**
 * Interceptor para solicitudes a enpoints de la OPE, para validacion de firma
 * del request y parametros correctos,
 * 
 * @author Alfredo Morales
 *
 */
public class InboundOpeInterceptor extends HandlerInterceptorAdapter {

	/** */
	private static final ResourceBundle certsStore = ResourceBundle
			.getBundle("com.ecm.sigap.ope.config.publicCertsOPE");

	/**  */
	@Autowired
	@Qualifier("repositoryRegistroInstancia")
	protected RepositoryRegistroInstancia repoRegistroInstancia;

	/**  */
	@Autowired
	@Qualifier("repositoryMensaje")
	protected RepositoryMensaje repoMensaje;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		File signed = null;
		Certificate certificate;

		String firma = request.getHeader(OpeHeaders.HEADER_FIRMA_BODY);
		String keyName = request.getHeader(OpeHeaders.HEADER_PUBLIC_KEY_NAME);

		try {
			if (StringUtils.isBlank(firma) || StringUtils.isBlank(keyName)) {
				return false; // se rechaza la solicitud
			}

			// loguear mensaje recibido,
			Mensaje mensaje = new Mensaje();
			mensaje.setId((UUID.randomUUID()).toString());
			mensaje.setMensajeFirma(StringUtils.substring(firma, 0, 249)); // TODO remover substring
			repoMensaje.save(mensaje);

			request.setAttribute(OpeHeaders.UUID, mensaje.getId());

			// se valida firma del mensaje es valida,
			{
				String certB64 = certsStore.getString("cert." + keyName);
				certificate = SecurityUtilOPE.stringB64toCertificate(certB64);
			}

			{
				signed = File.createTempFile(SecurityUtilOPE.TEMP_FILES_PREFIX, SecurityUtilOPE.TEMP_FILES_SUFIX);
				byte[] content = Base64.getDecoder().decode(firma);
				FileUtils.writeByteArrayToFile(signed, content);
			}

			boolean isValid = SecurityUtilOPE.validateSignature(signed, certificate, false, null);

			if (!isValid)
				return false;

			return super.preHandle(request, response, handler);

		} catch (Exception e) {
			return false;
		} finally {
			if (signed != null)
				signed.delete();
		}
	}

}
