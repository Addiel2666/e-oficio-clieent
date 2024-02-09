/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.mail;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import mx.com.ecmsolutions.services.model.mail.MailMessage;
import mx.com.ecmsolutions.services.model.mail.MailReceiver;
import mx.com.ecmsolutions.services.model.mail.MailSender;

/**
 * Clase de Negocio que envia las notificaciones via Correo Electronico, usando
 * el componente de envio de Correos Electronicos.
 * 
 * @author Alejandro Guzman
 * @version 0.1 fecha 15-Ene-2012
 * 
 * @author Gustavo Vielma
 * @version 0.2 fecha 17-Feb-2017
 * 
 *          Se hizo la adaptación correspondiente para Sigap5
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Component("mailProcess")
public class MailProcess {

	/** */
	private static final String RIGHT = "}";
	/** */
	private static final String LEFT = "$" + "{";

	/** Archivo de Propiedades para el envio de Correos */
	private ResourceBundle config;

	/** Configuracion del Servidor de Correo */
	private MailConfig mailConfig;

	/**
	 * Ruta scratch del la app
	 */
	private String appPath;

	/** Logger para escribir en el log de la aplicacion. */
	private static final Logger log = LogManager.getLogger(MailProcess.class);

	/** */
	@Autowired(required = true)
	@Qualifier("correosExternosTEXT")
	private String correosExternosText;

	/** */
	@Autowired(required = true)
	@Qualifier("correosExternosHTML")
	private String correosExternosHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("docsFirmaTEXT")
	private String docsFirmaText;

	/** */
	@Autowired(required = true)
	@Qualifier("docsFirmaHTML")
	private String docsFirmaHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("docsMarcadosFirmaTEXT")
	private String docsMarcadosFirmaText;

	/** */
	@Autowired(required = true)
	@Qualifier("docsMarcadosFirmaHTML")
	private String docsMarcadosFirmaHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("borradorTEXT")
	private String borradorText;

	/** */
	@Autowired(required = true)
	@Qualifier("borradorHTML")
	private String borradorHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("minutarioTEXT")
	private String minutarioText;

	/** */
	@Autowired(required = true)
	@Qualifier("minutarioHTML")
	private String minutarioHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("respuestasTEXT")
	private String respuestasText;

	/** */
	@Autowired(required = true)
	@Qualifier("respuestasHTML")
	private String respuestasHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("turnosTEXT")
	private String turnosText;

	/** */
	@Autowired(required = true)
	@Qualifier("turnosHTML")
	private String turnosHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("docsAnteFirmaTEXT")
	private String docsAnteFirmaTEXT;

	/** */
	@Autowired(required = true)
	@Qualifier("docsAnteFirmaHTML")
	private String docsAnteFirmaHTML;

	/** */
	@Autowired(required = true)
	@Qualifier("docsRecAnteFirmaTEXT")
	private String docsRecAnteFirmaTEXT;

	/** */
	@Autowired(required = true)
	@Qualifier("docsRecAnteFirmaHTML")
	private String docsRecAnteFirmaHTML;

	/** */
	@Autowired(required = true)
	@Qualifier("firmaFailTEXT")
	private String firmaFailText;

	/** */
	@Autowired(required = true)
	@Qualifier("firmaFailHTML")
	private String firmaFailHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("emptyBodyTEXT")
	private String emptyBodyText;

	/** */
	@Autowired(required = true)
	@Qualifier("emptyBodyHTML")
	private String emptyBodyHtml;

	/** */
	@Autowired(required = true)
	@Qualifier("docsParaAnteFirmaHTML")
	private String docsParaAnteFirmaHTML;
		
	/** */
	@Autowired(required = true)
	@Qualifier("docsParaAnteFirmaTEXT")
	private String docsParaAnteFirmaTEXT;
	
	/** */
	@Autowired(required = true)
	@Qualifier("docsCanceladoHTML")
	private String docsCanceladoHTML;
	
	/** */
	@Autowired(required = true)
	@Qualifier("docsCanceladoTEXT")
	private String docsCanceladoTEXT;

	/**
	 * Constructor por defecto de la clase
	 * 
	 */
	public MailProcess() throws Exception {
		super();

		try {
			config = ResourceBundle.getBundle("mail");

			mailConfig = new MailConfig(config);

		} catch (NullPointerException e) {
			// if baseName is null
			log.debug("::: Error del tipo NullPointerException al momento de "
					+ "obtener y asignar los valores de configuracion del Servicio "
					+ "de Correo, con la siguiente descripcion: " + e.getMessage());

			throw new Exception(e);

		} catch (MissingResourceException e) {
			// if no resource bundle for the specified base name can be found
			log.debug("::: Error del tipo MissingResourceException al momento de "
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
	 * Convierte una lista de Usuarios en una lista del tipo MailReceiver
	 * 
	 * @param emailAddressesTo
	 *            Lista de Usuarios
	 * @return Lista del tipo MailReceiver
	 */
	private Set<MailReceiver> convertToMailReceiver(Set<String> emailAddressesTo) {

		Set<MailReceiver> mailRecivers = new HashSet<MailReceiver>();
		MailReceiver mailReciver = null;

		for (String email : emailAddressesTo) {
			// Creamos el objero MailReceiver con la informacion del
			// Destinatario de Correo
			mailReciver = new MailReceiver(email);
			mailRecivers.add(mailReciver);
		}
		return mailRecivers;
	}

	/**
	 * Crea el Cuerpo del mensaje del Correo
	 * 
	 * @param emailContenType
	 *            Tipo de Formato del mensaje que va a enviar por correo
	 * @param parameters
	 *            Parametros que van a ser reemplazados en la plantilla
	 * @return Cuerpo del mensaje del Correo
	 * @throws Exception
	 */
	private String createMailBody(String emailContenType, Hashtable<String, String> parameters) throws Exception {

		// Nombre de la plantilla
		String templateFile = emailContenType + ".template";

		// Ruta fisica donde se encuentran las plantillas
		String path = "";
		if (appPath != null && !"".equals(appPath)) {
			path = appPath;
		} else {
			path = mailConfig.getTemplatePath();
		}
		try {
			String rutaTemplate = String.join("", path, templateFile);

			// URI urlTemplate =
			// getClass().getClassLoader().getResource(rutaTemplate).toURI();
			// String fileString = new
			// String(Files.readAllBytes(Paths.get(urlTemplate)),
			// StandardCharsets.UTF_8);

			String fileString = getMessageBody(rutaTemplate);

			Enumeration<String> keyParameters = parameters.keys();
			while (keyParameters.hasMoreElements()) {

				String key = keyParameters.nextElement();
				String value = parameters.get(key);
				if (fileString.contains(LEFT + key + RIGHT)) {
					fileString = fileString.replace(LEFT + key + RIGHT, value);
				}
			}
			return fileString;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}

	}

	/**
	 * 
	 * @param rutaTemplate
	 * @return
	 */
	private String getMessageBody(String rutaTemplate) {

		String fileString;

		if (rutaTemplate.endsWith("HTML.template")) {

			if (rutaTemplate.contains("correosexternos"))
				fileString = correosExternosHtml;
			else if (rutaTemplate.contains("documentosfirma"))
				fileString = docsFirmaHtml;
			else if (rutaTemplate.contains("documentosmardadofirma"))
				fileString = docsMarcadosFirmaHtml;
			else if (rutaTemplate.contains("minutario/borrador"))
				fileString = borradorHtml;
			else if (rutaTemplate.contains("minutario"))
				fileString = minutarioHtml;
			else if (rutaTemplate.contains("respuestas"))
				fileString = respuestasHtml;
			else if (rutaTemplate.contains("turnos"))
				fileString = turnosHtml;
			else if (rutaTemplate.contains("documentoantefirma"))
				fileString = docsAnteFirmaHTML;
			else if (rutaTemplate.contains("documentorechazoantefirma"))
				fileString = docsRecAnteFirmaHTML;
			else if (rutaTemplate.contains("firmaFail"))
				fileString = firmaFailHtml;
			else if (rutaTemplate.contains("documentoparaantefirma"))
				fileString = docsParaAnteFirmaHTML;
			else if (rutaTemplate.contains("documentocancelado"))
				fileString = docsCanceladoHTML;
			else if (rutaTemplate.contains("emptyBody"))
				fileString = emptyBodyHtml;
			else
				fileString = "PLANTILLA NO ENCONTRADA.";

		} else if (rutaTemplate.endsWith("TEXT.template")) {

			if (rutaTemplate.contains("correosexternos"))
				fileString = correosExternosText;
			else if (rutaTemplate.contains("documentosfirma"))
				fileString = docsFirmaText;
			else if (rutaTemplate.contains("documentosmardadofirma"))
				fileString = docsMarcadosFirmaText;
			else if (rutaTemplate.contains("minutario/borrador"))
				fileString = borradorText;
			else if (rutaTemplate.contains("minutario"))
				fileString = minutarioText;
			else if (rutaTemplate.contains("respuestas"))
				fileString = respuestasText;
			else if (rutaTemplate.contains("turnos"))
				fileString = turnosText;
			else if (rutaTemplate.contains("documentoantefirma"))
				fileString = docsAnteFirmaTEXT;
			else if (rutaTemplate.contains("documentorechazoantefirma"))
				fileString = docsRecAnteFirmaTEXT;
			else if (rutaTemplate.contains("firmaFail"))
				fileString = firmaFailText;
			else if (rutaTemplate.contains("documentoparaantefirma"))
				fileString = docsParaAnteFirmaTEXT;
			else if (rutaTemplate.contains("documentocancelado"))
				fileString = docsCanceladoTEXT;
			else if (rutaTemplate.contains("emptyBody"))
				fileString = emptyBodyText;
			else
				fileString = "PLANTILLA NO ENCONTRADA.";

		} else {

			fileString = "TIPO ERRONEO DE PLANTILLA.";

		}

		return fileString;
	}

	/**
	 * 
	 * @param emailAddressTo
	 *            Direccion de Correo a la cual se le va a enviar el Correo
	 *            Electronico
	 * @param noLibro
	 *            Nombre del libro que se genero
	 * @param subject
	 *            Titulo del Correo
	 * @return <t>Verdadero</t> en caso que se envie el correo
	 *         Satisfactoriamente de lo contrario <t>Falso</t>
	 * @throws Exception
	 */
	public boolean sendMail(Set<String> emailAddressTo, Hashtable<String, String> parameters, String subject)
			throws Exception {
		boolean result = false;
		Client client = null;
		Response response = null;
		try {
			MailMessage mailMessage = new MailMessage();

			MailSender mailSender;

			// Validamos si se ingreso informacion del Mail Sender Address
			if (null != mailConfig.getSenderEmail()) {

				// Validamos si se informacion del Mail Sender Desciption
				if (null != mailConfig.getSenderDescripcion()) {

					mailSender = new MailSender(mailConfig.getSenderDescripcion(), mailConfig.getSenderEmail());

				} else {

					mailSender = new MailSender(mailConfig.getSenderEmail(), mailConfig.getSenderEmail());
				}

				mailMessage.setMailSender(mailSender);

			}

			mailMessage.setType(mailConfig.getType());
			mailMessage.setContentType(mailConfig.getContentType());
			mailMessage.setPriority(mailConfig.getPriority());
			mailMessage.setSubject(subject);

			// Escape acentos o caracteres por html
			parameters.entrySet().stream().forEach(entry -> {
				entry.setValue(StringEscapeUtils.escapeHtml(entry.getValue()));
			});

			mailMessage.setBody(createMailBody(mailMessage.getContentType().toString(), parameters));

			List<MailReceiver> adrressesTo = new ArrayList<MailReceiver>(convertToMailReceiver(emailAddressTo));
			mailMessage.setMailTo(adrressesTo);

			// Se configura el Cliente Rest y se procede a hacer la peticioìn
			// put al
			// servicio de envio de notificaciones.
			client = ClientBuilder.newClient(new ClientConfig().register(JacksonJsonProvider.class));
			WebTarget webTarget = client.target(mailConfig.getUrl()).path("api").path("mail");
			response = webTarget.request().put(Entity.entity(mailMessage, MediaType.APPLICATION_JSON_TYPE));

			// Validamos que la respuesta del servicio
			if (Response.Status.OK.getStatusCode() == response.getStatus()) {
				if (log.isDebugEnabled()) {
					log.debug("Correo enviado correctamente a la direccion de correo " + emailAddressTo);
				}
				result = true;
			} else {
				log.error("::: Error: No se pudo enviar el Correo a la direccion " + emailAddressTo
						+ " debido al siguiente error: " + response.getEntity().toString());
				throw new Exception("ErrorNotificacionMessage");
			}
		} catch (ProcessingException e) {
			log.error(e.getLocalizedMessage());
			throw new Exception("ErrorNotificacionMessage");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());			
			throw e;
		} finally {
			if(null != response)
				response.close();
			if(null != client)
				client.close();
		}
		return result;
	}

	/**
	 * Envia el Correo a al Destinatario que se envie por parametros
	 * 
	 * @param emailAddressTo
	 *            Correo Electronico del Destinatario
	 * @param parameters
	 *            Lista de parametros que va a contener el cuerpo del Correo
	 * @param subject
	 *            Titulo del Correo
	 * @return True en caso que pueda enviar el correo, de lo contrario False
	 * @throws Exception
	 *             Cualquier error al momento de ejecutar el metodo
	 */
	public boolean sendMail(String emailAddressTo, Hashtable<String, String> parameters, String subject)
			throws Exception {

		Set<String> mailRecivers = new HashSet<String>();
		mailRecivers.add(emailAddressTo);

		return sendMail(mailRecivers, parameters, subject);
	}

	/**
	 * 
	 * @param appPath
	 */
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

}
