/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import java.io.IOException;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;

import mx.com.ecmsolutions.license.exception.LicenseException;
import mx.com.ecmsolutions.license.model.ECMLicencia;
import mx.com.ecmsolutions.sigap.interoperabilidad.TSPGeneralException;

/**
 * Clase utilitaria para la desencriptar la llave de de activacion del servicio
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 11-Nov-2014
 * 
 *          Creacion de la clase
 * 
 */
@Component
public class TrialVersionObject {

	/** Logger de la clase */
	private final static Logger logger = LogManager.getLogger(TrialVersionObject.class.getName());

	/** */
	private boolean isValid;

	/** */
	@Value("${licValidator}")
	private Boolean licValidator;

	/**
	 * Constructor de la clase
	 * 
	 * @param strActivationKey String que representa la llave de activacion en
	 *                         formato de Base64
	 */
	public TrialVersionObject() {
		super();
	}

	/**
	 * Se valida la licencia diario a media noche.
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "${licValidatorCron}")
	public void watcher() {
		if (licValidator)
			validateLicense();
	}

	/**
	 * Metodo principal de la clase, encargado de procesar la llave de activacion y
	 * determinar si es valida o no
	 * 
	 * @param strActivationKey String que representa la llave de activacion en
	 *                         formato de Base64
	 * @throws ParseException              Error al momento de procesar las fechas
	 *                                     de la llave de activacion
	 * @throws CertificateUtilityException Error al momento de desencriptar la llave
	 *                                     de activacion
	 * @throws TSPGeneralException         Error al momento de obtener la fecha del
	 *                                     servidor de Estampado de tiempo
	 * @throws LicenseException
	 */
	@Async
	public void validateLicense() {
		try {
			ECMLicencia lic = new LicenciaUtil().getLicencia();

			boolean validMAC = isMacValid(lic);

			boolean notExpired = isTimeValid(lic);

			boolean repoFine = isRepoValid(lic);

			logger.info("Repositorio valido?: " + repoFine);
			logger.info("Repositorio valido?: " + repoFine);
			logger.info("Repositorio valido?: " + repoFine);

			logger.info("Lista de direcciones MAC valida?: " + validMAC);
			logger.info("Lista de direcciones MAC valida?: " + validMAC);
			logger.info("Lista de direcciones MAC valida?: " + validMAC);

			logger.info("Fecha de expiracion valida?: " + notExpired);
			logger.info("Fecha de expiracion valida?: " + notExpired);
			logger.info("Fecha de expiracion valida?: " + notExpired);

			// si la MAC esta incluida y la licencia no ha expirado
			isValid = validMAC && notExpired && repoFine;

			if (!isValid) {

				logger.error("validMAC :: " + validMAC);
				logger.error("validMAC :: " + validMAC);
				logger.error("validMAC :: " + validMAC);

				logger.error("notExpired :: " + notExpired);
				logger.error("notExpired :: " + notExpired);
				logger.error("notExpired :: " + notExpired);

				logger.error("repoFine :: " + repoFine);
				logger.error("repoFine :: " + repoFine);
				logger.error("repoFine :: " + repoFine);

				logger.error(":::::::: e-oficio v - La licencia es invalida, la aplicacion no se puede iniciar.");
				logger.error(":::::::: e-oficio v - La licencia es invalida, la aplicacion no se puede iniciar.");
				logger.error(":::::::: e-oficio v - La licencia es invalida, la aplicacion no se puede iniciar.");
				logger.error(":::::::: e-oficio v - La licencia es invalida, la aplicacion no se puede iniciar.");
				logger.error(":::::::: e-oficio v - La licencia es invalida, la aplicacion no se puede iniciar.");
				logger.error(":::::::: e-oficio v - La licencia es invalida, la aplicacion no se puede iniciar.");

				System.exit(0);
			}

		} catch (Exception e) {

			
			logger.error(":::::::: e-oficio v - Error al validar la licencia.");
			logger.error(":::::::: e-oficio v - Error al validar la licencia.");
			logger.error(":::::::: e-oficio v - Error al validar la licencia.");
			logger.error(":::::::: e-oficio v - Error al validar la licencia.");
			logger.error(":::::::: e-oficio v - Error al validar la licencia.");
			logger.error(":::::::: e-oficio v - Error al validar la licencia.");

			System.exit(0);

		}

	}

	/**
	 * Valida que el nombre del repositorio coincida con el repositorio indicado en
	 * la licencia,.
	 * 
	 * @param lic
	 * @return
	 * @throws LicenseException
	 * @throws Exception
	 */
	private boolean isRepoValid(ECMLicencia lic) throws LicenseException, Exception {

		String repoLic = lic.getRepositorio();

		Map<String, Object> repoInfo = EndpointDispatcher.getInstance().getDFCRepoInfo();

		logger.info("Repositorio licencia: " + repoLic);

		Object repoInfo_ = repoInfo.get("RepId");

		logger.info("Repositorio conectado: " + repoInfo_);

		if (repoInfo_ != null && repoLic.equalsIgnoreCase(repoInfo_.toString())) {
			return true;
		}

		return false;
	}

	/**
	 * Valida si la fecha del servidor esta dentro de la fecha de vigencia de la
	 * licencia.
	 * 
	 * @param lic
	 * @return
	 * @throws LicenseException
	 */
	private boolean isTimeValid(ECMLicencia lic) throws LicenseException {

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		Date now = new Date();

		Date fechaExpiracion = lic.getFechaExpiracion();

		logger.info("Fecha actual: " + sf.format(now));
		logger.info("Fecha expiracion: " + sf.format(fechaExpiracion));

		return now.before(fechaExpiracion);

	}

	/**
	 * Valida si la mac address del servidor donde se esta ejecutando esta dentro de
	 * la lista de mac address permitidas,
	 * 
	 * @param lic
	 * @return
	 * @throws LicenseException
	 * @throws IOException
	 */
	private boolean isMacValid(ECMLicencia lic) throws LicenseException, IOException {

		List<String> allowed = lic.getDireccionesMAC();

		// si contiene la mac address 00:00:00:00:00:00
		// no se valdia la mac del server.
		if (allowed.contains("000000000000"))
			return true;

		List<String> macAdresses = getMacAdress();

		for (String mac : allowed) {

			for (String macAdress : macAdresses) {

				if (mac.equalsIgnoreCase(macAdress)) {

					logger.info("mac address current :: " + macAdress);
					logger.info("mac address allowed :: " + mac);

					return true;
				}

			}
		}

		return false;
	}

	/**
	 * Obtiene las Mac Addresses del equipo donde se esta ejecutando el WS
	 * 
	 * @return Mac Address del equipo
	 * @throws IOException
	 */
	private List<String> getMacAdress() throws IOException {

		List<String> macs = new ArrayList<>();

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		StringBuilder sb;

		for (NetworkInterface interface_ : Collections.list(interfaces)) {

			if (interface_.isLoopback())
				continue;

			if (!interface_.isUp())
				continue;

			byte[] mac = interface_.getHardwareAddress();

			if (mac != null) {

				sb = new StringBuilder();

				for (byte b : interface_.getHardwareAddress()) {
					sb.append(String.format("%02X", b));
				}

				macs.add(sb.toString());

			}

		}

		return macs;
	}

	/**
	 * Obtiene el estatus de la Licencia
	 * 
	 * @return <t>true</t> en caso que las validaciones de la licencia sean
	 *         satisfactoria, de lo contrario <t>false</t>
	 */
	public boolean isValid() {
		return isValid;
	}
}
