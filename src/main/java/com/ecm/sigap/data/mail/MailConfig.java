/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.mail;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import mx.com.ecmsolutions.services.model.mail.MailContentType;
import mx.com.ecmsolutions.services.model.mail.MailPriority;
import mx.com.ecmsolutions.services.model.mail.MailType;

/**
 * Clase de entidad que representa la configuracion del Servidor de Correo.
 *
 * @author Alejandro Guzman
 * @version 1.0 Fecha 19-Ene-2012
 */
public class MailConfig {

	/** URL del Componente de Correo. */
	private String url;

	/**
	 * Direccion de Correo Electronico del Usuario que se quiere usar para
	 * enviar los correos de la aplicacion.
	 */
	private String senderEmail;

	/**
	 * Descripcion del Usuario del Correo Electronico que se quiere usar para
	 * enviar los correos de la aplicacion.
	 */
	private String senderDescripcion;

	/** Tipo de Correo que se quiere enviar. */
	private MailType type;

	/** Tipo de Formato del mensaje que va a enviar por correo. */
	private MailContentType contentType;

	/** Prioridad que van a tener los correos que se envian. */
	private MailPriority priority;

	/** Logger para escribir en el log de la aplicacion. */
	private static final Logger log = LogManager.getLogger(MailConfig.class);

	/**
	 * Ruta fisica donde se encuentran las plantillas a ser usadas como cuerpo
	 * del mensaje de Correo.
	 */
	private String templatePath;

	/**
	 * Instantiates a new mail config.
	 *
	 * @param objResourceBundle
	 *            the obj resource bundle
	 * @throws Exception
	 *             the exception
	 */
	public MailConfig(ResourceBundle objResourceBundle) throws Exception {

		try {

			if ((!"".equals(objResourceBundle.getString("mail.URL")))
					&& (!"".equals(objResourceBundle.getString("mail.type")))
					&& (!"".equals(objResourceBundle.getString("mail.content.type")))
			// &&
			// (!"".equals(objResourceBundle.getString("mail.template.path")))
			) {

				setUrl(objResourceBundle.getString("mail.URL"));
				setSenderEmail(objResourceBundle.getString("mail.sender.email"));
				setSenderDescripcion(objResourceBundle.getString("mail.sender.descripcion"));
				setType(objResourceBundle.getString("mail.type"));
				setContentType(objResourceBundle.getString("mail.content.type"));
				setPriority(objResourceBundle.getString("mail.priority"));
				// setTemplatePath(objResourceBundle.getString("mail.template.path"));

			} else {

				throw new Exception("::: Error: Uno de los atributos obligatorios del "
						+ "servicio de Correo no fueron asignados. Por favor verifique "
						+ "el archivo de configuracion mail.properties");
			}
		} catch (NullPointerException e) {
			// if key is null
			log.debug("::: Error del tipo NullPointerException al momento de "
					+ "obtener y asignar los valores de configuracion del Servicio "
					+ "de Correo, con la siguiente descripcion: " + e.getMessage());

			throw new Exception(e);

		} catch (MissingResourceException e) {
			// if no object for the given key can be found
			log.debug("::: Error del tipo MissingResourceException al momento de "
					+ "obtener y asignar los valores de configuracion del Servicio "
					+ "de Correo, con la siguiente descripcion: " + e.getMessage());

			throw new Exception(e);

		} catch (ClassCastException e) {
			// if the object found for the given key is not a string
			log.debug("::: Error del tipo ClassCastException al momento de "
					+ "obtener y asignar los valores de configuracion del Servicio "
					+ "de Correo, con la siguiente descripcion: " + e.getMessage());

			throw new Exception(e);

		} catch (Exception e) {

			log.debug("::: Error del tipo Exception al momento de "
					+ "obtener y asignar los valores de configuracion del Servicio "
					+ "de Correo, con la siguiente descripcion: " + e.getMessage());

			throw new Exception(e);
		}
	}

	/**
	 * Obtiene la URL del Componente de Correo.
	 *
	 * @return URL del Componente de Correo
	 */
	public String getUrl() {

		return url;
	}

	/**
	 * Asigna la URL del Componente de Correo.
	 *
	 * @param url
	 *            URL del Componente de Correo
	 */
	private void setUrl(String url) {

		this.url = url;
	}

	/**
	 * Obtiene la Direccion de Correo Electronico del Usuario que se quiere usar
	 * para enviar los correos de la aplicacion.
	 *
	 * @return Direccion de Correo Electronico del Usuario que se quiere usar
	 *         para enviar los correos de la aplicacion
	 */
	public String getSenderEmail() {

		return senderEmail;
	}

	/**
	 * Asigna la Direccion de Correo Electronico del Usuario que se quiere usar
	 * para enviar los correos de la aplicacion.
	 *
	 * @param senderEmail
	 *            Direccion de Correo Electronico del Usuario que se quiere usar
	 *            para enviar los correos de la aplicacion
	 */
	private void setSenderEmail(String senderEmail) {

		if (!"".equals(senderEmail.trim())) {

			this.senderEmail = senderEmail;

		} else {

			this.senderEmail = null;
		}
	}

	/**
	 * Obtiene la Descripcion del Usuario del Correo Electronico que se quiere
	 * usar para enviar los correos de la aplicacion.
	 *
	 * @return Descripcion del Usuario del Correo Electronico que se quiere usar
	 *         para enviar los correos de la aplicacion
	 */
	public String getSenderDescripcion() {

		return senderDescripcion;
	}

	/**
	 * Asigna la Descripcion del Usuario del Correo Electronico que se quiere
	 * usar para enviar los correos de la aplicacion.
	 *
	 * @param senderDescripcion
	 *            Descripcion del Usuario del Correo Electronico que se quiere
	 *            usar para enviar los correos de la aplicacion
	 */
	private void setSenderDescripcion(String senderDescripcion) {

		if (!"".equals(senderDescripcion.trim())) {

			this.senderDescripcion = senderDescripcion;

		} else {

			this.senderDescripcion = null;
		}
	}

	/**
	 * Obtiene el Tipo de Correo que se quiere enviar.
	 *
	 * @return Tipo de Correo que se quiere enviar
	 */
	public MailType getType() {

		return type;
	}

	/**
	 * Asigna el Tipo de Correo que se quiere enviar.
	 *
	 * @param type
	 *            Tipo de Correo que se quiere enviar
	 */
	private void setType(String type) {

		if ("INTRANET".equals(type)) {

			this.type = MailType.INTRANET;

		} else if ("INTERNET".equals(type)) {

			this.type = MailType.INTERNET;

		} else {
			log.warn("::: El atributo 'mail.type' no tiene un valor permiritdo, " + "se va a usar el tipo INTRANET.");
			this.type = MailType.INTRANET;
		}
	}

	/**
	 * Obtiene el Tipo de Formato del mensaje que va a enviar por correo.
	 *
	 * @return Tipo de Formato del mensaje que va a enviar por correo
	 */
	public MailContentType getContentType() {

		return contentType;
	}

	/**
	 * Asigna el Tipo de Formato del mensaje que va a enviar por correo.
	 *
	 * @param contentType
	 *            Tipo de Formato del mensaje que va a enviar por correo
	 */
	private void setContentType(String contentType) {

		if ("TEXT".equals(contentType)) {

			this.contentType = MailContentType.TEXT;

		} else if ("HTML".equals(contentType)) {

			this.contentType = MailContentType.HTML;

		} else {
			log.warn("::: El atributo 'mail.content.type' no tiene un valor permiritdo, "
					+ "se va a usar el tipo TEXT.");
			this.contentType = MailContentType.TEXT;
		}
	}

	/**
	 * Obtiene la Prioridad que van a tener los correos que se envian.
	 *
	 * @return Prioridad que van a tener los correos que se envian
	 */
	public MailPriority getPriority() {

		return priority;
	}

	/**
	 * Asigna la Prioridad que van a tener los correos que se envian.
	 *
	 * @param priority
	 *            Prioridad que van a tener los correos que se envian
	 */
	private void setPriority(String priority) {

		int prioridad = 0;

		try {

			// Validamos que no este vacio
			if (!"".equals(priority)) {

				prioridad = Integer.parseInt(priority);
			}

			switch (prioridad) {
			case 1:
				this.priority = MailPriority.MUY_ALTA;
				break;
			case 2:
				this.priority = MailPriority.ALTA;
				break;
			case 3:
				this.priority = MailPriority.NORMAL;
				break;
			case 4:
				this.priority = MailPriority.BAJA;
				break;
			case 5:
				this.priority = MailPriority.MUY_BAJA;
				break;
			default:
				log.warn("::: El atributo 'mail.priority' no tiene un valor permiritdo, "
						+ "se va a usar el tipo INTRANET.");
				this.priority = MailPriority.NORMAL;
				break;
			}
		} catch (NumberFormatException e) {

			log.warn("::: El atributo 'mail.priority' no tiene un valor numerico valido, "
					+ "se va a usar el tipo NORMAL.");
			this.priority = MailPriority.NORMAL;
		}

	}

	/**
	 * Obtiene la Ruta fisica donde se encuentran las plantillas a ser usadas
	 * como cuerpo del mensaje de Correo.
	 *
	 * @return Ruta fisica donde se encuentran las plantillas a ser usadas como
	 *         cuerpo del mensaje de Correo
	 */
	public String getTemplatePath() {

		return templatePath;
	}

	// /**
	// * Asigna la Ruta fisica donde se encuentran las plantillas a ser usadas
	// * como cuerpo del mensaje de Correo.
	// *
	// * @param templatePath
	// * Ruta fisica donde se encuentran las plantillas a ser usadas
	// * como cuerpo del mensaje de Correo
	// */
	// private void setTemplatePath(String templatePath) {
	//
	// this.templatePath = templatePath;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MailConfig [url=" + url + ", senderEmail=" + senderEmail + ", senderDescripcion=" + senderDescripcion
				+ ", type=" + type + ", contentType=" + contentType + ", priority=" + priority + ", templatePath="
				+ templatePath + "]";
	}

}
