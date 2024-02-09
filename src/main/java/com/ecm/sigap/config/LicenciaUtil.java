/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import mx.com.ecmsolutions.license.exception.LicenseException;
import mx.com.ecmsolutions.license.model.ECMLicencia;

/**
 * 
 * @author ECM SOLUTIONS
 * @version 1.0
 *
 */
@Component
public class LicenciaUtil {

	/** */
	private final static Logger log = LogManager.getLogger(LicenciaUtil.class.getName());

	/**
	 * 
	 * @return
	 * @throws LicenseException
	 */
	public ECMLicencia getLicencia() throws LicenseException {

		Locale defaultLocale = Locale.getDefault();

		ResourceBundle rb = ResourceBundle.getBundle("application", defaultLocale);

		ClassLoader classLoader = getClass().getClassLoader();

		String licenceName = rb.getString("ecm.licencia.name");

		log.info("Cargando licencia >> " + licenceName);

		ECMLicencia licencia = null;
		
		try(InputStream input = classLoader.getResourceAsStream(licenceName)){

			if (input == null) {

				log.error(":::::::: e-oficio v - El archivo de licencia no existe, la aplicacion no se puede iniciar.");
				log.error(":::::::: e-oficio v - El archivo de licencia no existe, la aplicacion no se puede iniciar.");
				log.error(":::::::: e-oficio v - El archivo de licencia no existe, la aplicacion no se puede iniciar.");
				log.error(":::::::: e-oficio v - El archivo de licencia no existe, la aplicacion no se puede iniciar.");
				log.error(":::::::: e-oficio v - El archivo de licencia no existe, la aplicacion no se puede iniciar.");

				System.exit(0);

			}

			try {

				licencia = new ECMLicencia(input);

				licencia.setProducto(ECMLicencia.E_OFICIO);

			} catch (Exception e) {

				log.error(
						":::::::: e-oficio v - Error al cargar la licencia desde el archivo, la aplicacion no se puede iniciar.");
				log.error(
						":::::::: e-oficio v - Error al cargar la licencia desde el archivo, la aplicacion no se puede iniciar.");
				log.error(
						":::::::: e-oficio v - Error al cargar la licencia desde el archivo, la aplicacion no se puede iniciar.");
				log.error(
						":::::::: e-oficio v - Error al cargar la licencia desde el archivo, la aplicacion no se puede iniciar.");
				log.error(
						":::::::: e-oficio v - Error al cargar la licencia desde el archivo, la aplicacion no se puede iniciar.");

				System.exit(0);

			}
		} catch (Exception e) {
			log.error(":::::::: e-oficio v - Eror al recuperar el archivo de licencia, la aplicacion no se puede iniciar.");
			log.error(":::::::: e-oficio v - Eror al recuperar el archivo de licencia, la aplicacion no se puede iniciar.");
			log.error(":::::::: e-oficio v - Eror al recuperar el archivo de licencia, la aplicacion no se puede iniciar.");
			log.error(":::::::: e-oficio v - Eror al recuperar el archivo de licencia, la aplicacion no se puede iniciar.");
			log.error(":::::::: e-oficio v - Eror al recuperar el archivo de licencia, la aplicacion no se puede iniciar.");

			System.exit(0);
		}
		return licencia;
	}
}
