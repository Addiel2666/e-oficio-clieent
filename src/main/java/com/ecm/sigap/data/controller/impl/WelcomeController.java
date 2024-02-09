/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Acl;
import com.ecm.sigap.config.LicenciaUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.util.SignatureUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.com.ecmsolutions.license.exception.LicenseException;
import mx.com.ecmsolutions.license.model.ECMLicencia;

/**
 * Hello Message.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@RestController
public class WelcomeController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(WelcomeController.class);

	/**
	 * Formato de fechas.
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss z");

	/** */
	@Autowired
	private LicenciaUtil licenciaUtil;

	/** Referencia al datasource de base de datos. */
	@Autowired
	@Qualifier("ecmEoficoVDBName")
	private DataSource dataSourceSigapV;

	/** */
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * redirect a index.html
	 *
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Pagina principal testing", notes = "Obtiene un menu de testeo para la aplicacion de e-oficio ")
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

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
	public String welcome() {
		return "<script>window.location.replace(window.location.href + '/index.html' );</script>";
	}

	/**
	 * Mensaje para probar que los servios REST de SIGAP V se encuentren
	 * funcionando.
	 *
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Prueba servicios sigap", notes = "Obtiene los servicios que utiliza SIGAP y muestra los que se encuentran funcionando")
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

	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = "text/html")
	public String hello(//
			@RequestParam(name = "doTest", required = false) String doTest, //
			@RequestParam(name = "testType", required = false) String testType//
	) {

		// - - - - - - - - - - - - - - - - - - - -

		StringBuilder message = new StringBuilder();

		message.append("<div style='padding-left: 4em'>");

		// - - - - - - - - - - - - - - - - - - - -

		{
			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("repositoryTest"))
				repositoryTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("licenceInfo"))
				licenceInfo(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("databaseTest"))
				databaseTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("digitalSignTest"))
				digitalSignTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("notificationServiceTest"))
				notificationServiceTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("documentConversionServiceTest"))
				documentConversionServiceTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("eArchvoTest"))
				eArchvoTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("casTest"))
				casTest(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("buildDate"))
				getBuildDate(message);

			if (StringUtils.isBlank(testType) || testType.equalsIgnoreCase("systemInfo"))
				getSystemInfo(message);

		}

		// - - - - - - - - - - - - - - - - - - - -
		// Perfomance tests.

		if (StringUtils.isNotBlank(doTest) && "SI".equalsIgnoreCase(doTest)) {
			message.append("<pre>");
			message.append(performanceTest());
			message.append("</pre>");
		}

		// - - - - - - - - - - - - - - - - - - - -

		message.append("</div>");

		return message.toString();

	}

	/** */
	private static final int mb = 1024 * 1024;

	/**
	 * 
	 * @param message
	 */
	private void getSystemInfo(StringBuilder message) {

		message.append("<p><b> System Info: </b></p>");

		Runtime runtime = Runtime.getRuntime();

		message.append("<p class=\"bg-warning\">");

		message.append("Used Memory:&nbsp;<i>");
		message.append((runtime.totalMemory() - runtime.freeMemory()) / mb);
		message.append("mb. </i>");

		message.append("<br />Free Memory:&nbsp;<i>");
		message.append(runtime.freeMemory() / mb);
		message.append("mb. </i>");

		message.append("<br />Total Memory:&nbsp;<i>");
		message.append(runtime.totalMemory() / mb);
		message.append("mb. </i>");

		message.append("<br />Max Memory:&nbsp;<i>");
		message.append(runtime.maxMemory() / mb);
		message.append("mb. </i>");

		message.append("<br />Java Version:&nbsp;<i>");
		message.append(Runtime.class.getPackage().getImplementationVersion());
		message.append("</i>");

		message.append("<br />Procesord Id:&nbsp;<i>");
		message.append(System.getenv("PROCESSOR_IDENTIFIER"));
		message.append("</i>");

		message.append("<br />Architecture:&nbsp;<i>");
		message.append(System.getenv("PROCESSOR_ARCHITECTURE"));
		message.append("</i>");

		message.append("<br />Word:&nbsp;<i>");
		message.append(System.getenv("PROCESSOR_ARCHITEW6432"));
		message.append("</i>");

		message.append("<br />No. Processors:&nbsp;<i>");
		message.append(System.getenv("NUMBER_OF_PROCESSORS"));
		message.append("</i>");

		message.append("</p>");

	}

	/**
	 * 
	 * @param message
	 */
	private void licenceInfo(StringBuilder message) {
		try {
			ECMLicencia licencia = licenciaUtil.getLicencia();

			message.append("<p><b> Licence: </b></p>");

			message.append("<p class=\"bg-warning\">");
			message.append("Client:&nbsp;<i>");
			message.append(licencia.getCliente());
			message.append("</i><br />Environment:&nbsp;<i>");
			message.append(licencia.getAmbiente());
			message.append("</i><br />Repo Id:&nbsp;<i>");
			message.append(licencia.getRepositorio());
			message.append("</i><br />Max. Users:&nbsp;<i>");
			message.append(licencia.getUsuariosActivosMax());
			message.append("</i>");
			// message.append(licencia.getMemoriasActivasMax());
			message.append("<br />Expiration date:&nbsp;<i>");
			message.append(sdf.format(licencia.getFechaExpiracion()));
			message.append("</i></ p>");

		} catch (LicenseException e) {

		}

	}

	/**
	 * @param message
	 */
	private void getBuildDate(StringBuilder message) {

		message.append("<p><b> Build date: </b></p>");

		message.append("<p class=\"bg-warning\">");
		message.append(getBuildDate());
		message.append("</ p>");
	}

	/**
	 * @param message
	 */
	private void casTest(StringBuilder message) {
		message.append("<p><b> CAS test: </b></p>");

		try {
			URL obj = new URL(environment.getProperty("seguridad.cas.url"));

			message.append("<p class=\"bg-info\">");
			message.append("Sending request to URL : " + environment.getProperty("seguridad.cas.url"));
			message.append("</ p>");

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod(HttpMethod.POST.name());

			if (con.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {

				message.append("<p class=\"bg-success\">Service Ok!</ p>");

			} else {

				message.append("<p class=\"bg-danger\">");
				message.append(IOUtils.toString(con.getErrorStream(), "UTF-8"));
				message.append("</ p>");

			}

		} catch (Exception e) {

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}
	}

	/**
	 * @param message
	 */
	private void eArchvoTest(StringBuilder message) {
		message.append("<p><b> eArchivo Core: </b></p>");

		try {

			String url = environment.getProperty("e-archivo.url");

			message.append("<p class=\"bg-info\">");
			message.append("Service URL : <a href=\"" + url + "\" target=\"_blank\">" + url + "</a>");
			message.append("</ p>");

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			try {
				con.setRequestMethod(HttpMethod.GET.name());

				int responseCode = con.getResponseCode();

				message.append("<p class=\"bg-success\">Response Code : ");
				message.append(responseCode);
				message.append("</ p>");

			} catch (Exception e) {

				message.append("<p class=\"bg-danger\">Error : ");
				message.append(e.getLocalizedMessage());
				message.append("</ p>");

			}

		} catch (Exception e) {

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}
	}

	/**
	 * @param message
	 */
	private void documentConversionServiceTest(StringBuilder message) {
		message.append("<p><b> Document Convertion Service: </b></p>");

		try {

			String url = environment.getProperty("conversor.url");

			message.append("<p class=\"bg-info\">");
			message.append("Service URL : " + url);
			message.append("</ p>");

			pdfConverterService.test();

			message.append("<p class=\"bg-success\">Service Ok!</ p>");

		} catch (Exception e) {

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}
	}

	/**
	 * @param message
	 */
	private void notificationServiceTest(StringBuilder message) {
		message.append("<p><b> Email Notification Service: </b></p>");

		try {

			String url = environment.getProperty("mail.URL");

			message.append("<p class=\"bg-info\">");
			message.append("Service is : " + environment.getProperty("mail.service"));
			message.append("</ p>");

			message.append("<p class=\"bg-info\">");
			message.append("Service URL : " + url);
			message.append("</ p>");

			if ("ON".equalsIgnoreCase(environment.getProperty("mail.service"))) {

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				try {
					con.setRequestMethod(HttpMethod.GET.name());

					int responseCode = con.getResponseCode();

					message.append("<p class=\"bg-success\">Response Code : ");
					message.append(responseCode);
					message.append("</ p>");

				} catch (Exception e) {

					message.append("<p class=\"bg-danger\">Error : ");
					message.append(e.getLocalizedMessage());
					message.append("</ p>");

				}

			}

		} catch (Exception e) {

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}
	}

	/**
	 * @param message
	 */
	private void digitalSignTest(StringBuilder message) {
		message.append("<p><b> WS Firma Digital test: </b></p>");

		try {

			message.append("<p class=\"bg-info\">");
			message.append("Sending request to URL : " + environment.getProperty("urlSignatureService"));
			message.append("</ p>");

			String toBeStamped = "firmaTest_" + new Date().getTime();

			Map<String, Object> time_ = firmaEndPoint.getTime(toBeStamped, null);

			Date date_ = SignatureUtil.timestampToDate(time_.get("Tiempo").toString());

			message.append("<p class=\"bg-success\">");
			message.append(sdf.format(date_));
			message.append("</ p>");

		} catch (Exception e) {

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}
	}

	/**
	 * @param message
	 */
	private void databaseTest(StringBuilder message) {
		message.append("<p><b>DB test: </b></p>");
		try {

			if (dataSourceSigapV != null) {
				message.append("<p class=\"bg-info\" style='overflow-wrap:break-word;'>");

				Connection connection = null;

				try {

					connection = dataSourceSigapV.getConnection();

					DatabaseMetaData metaData = connection.getMetaData();

					// message.append("Connection String : ");
					// message.append(metaData.getURL());
					// message.append("<br />");
					// message.append("Schema : ");
					// message.append(metaData.getUserName());
					message.append("<br />");
					message.append("DB Product Name : ");
					message.append(metaData.getDatabaseProductName());
					message.append("<br />");
					message.append("DB Version : ");
					message.append(metaData.getDatabaseProductVersion());
					message.append("</ p>");

				} catch (Exception e) {

				} finally {
					if (connection != null)
						connection.close();
				}
			}

			mngrAsunto.fetch(0);
			mngrAsuntoConsulta.fetch(0);
			mngrArea.fetch(0);

			String connected = mngrAsunto.isConnected();

			message.append("<p class=\"bg-success\">");
			message.append("isConnected :: " + connected);
			message.append("</ p>");

		} catch (Exception e) {

			log.error(e.getMessage());

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}
	}

	/**
	 * @param message
	 */
	private void repositoryTest(StringBuilder message) {
		message.append("<p><b>Repository test: </b></p>");

		message.append("<p class=\"bg-info\">");
		message.append("Sending request to URL : " + environment.getProperty("serviceProtocol") + "://"
				+ environment.getProperty("serviceHost") + ":" + environment.getProperty("servicePort")
				+ environment.getProperty("serviceContext"));
		message.append("</ p>");

//		try {
//
//			Map<String, Object> repoInfo = EndpointDispatcher.getInstance().getRepoInfo();
//
//			String prettyJsonString = gson.toJson(repoInfo);
//
//			message.append("<pre class=\"bg-success\">");
//			message.append(prettyJsonString);
//			message.append("</pre>");
//
//		} catch (Exception e) {
//
//			message.append("<p class=\"bg-danger\">");
//			message.append(e.getLocalizedMessage());
//			
//			message.append("</ p>");
//
//		}

		try {

			Map<String, Object> dfcRepoInfo = EndpointDispatcher.getInstance().getDFCRepoInfo();

			String prettyJsonString = gson.toJson(dfcRepoInfo);

			message.append("<pre class=\"bg-success\">");
			message.append(prettyJsonString);
			message.append("</pre>");

		} catch (Exception e) {

			log.error(e.getMessage());

			message.append("<p class=\"bg-danger\">");
			message.append(e.getLocalizedMessage());

			message.append("</ p>");

		}

	}

	/**
	 * Obtiene la fecha de construccion del projecto.
	 *
	 * @return
	 */
	private String getBuildDate() {

		try {

			ResourceBundle info = ResourceBundle.getBundle("info", Locale.getDefault());

			return info.getString("build.date") + " " + info.getString("build.time");

		} catch (Exception e) {
			//
		}

		return "Unavailable";
	}

	/**
	 * Set de pruebas para obtener tiempos y funcionamiento correcto de la
	 * applicacion,
	 *
	 * @return
	 */
	public Map<String, Object> performanceTest() {

		Map<String, Object> times = new HashMap<>();

		try {

			// id de un area
			int idArea = 5;
			// id de una institucion con unas cuantas areas, no muchas
			int idInstitucion = 15;
			// se traeran los ultimos N asuntos con ID mayor a este,
			int idAsuntoBuscar = 2530;
			// id del asunto a consultar
			int idAsuntoFetch = 2070;
			// id del asunto q tenga documentos adjuntos
			int idAsuntoDocumentosFetch = 2070;
			// id del asunto del cual se traeran sus respuestas
			int idAsuntoRespuestasFetch = 1318;
			// se traera los ultimos N minutarios con ID mayor a este,
			int idMinutarioSearch = 2043;
			// id del minutario a consultar
			int idMinutario = 2040;
			// id de la respuesta que tenga adjuntos
			int idRespuestaDocumentosFetch = 423;
			// id del minutario q tiene documentos adjuntos.
			int idMinutarioAnexosFetch = 324;

			try { // obtener area
				long lStartTime = System.nanoTime();

				// Area a = (Area) session.get(Area.class, 5);

				mngrArea.fetch(idArea);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("obtener area ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "obtener area");
				throw e;
			}
			try { // buscar areas
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("activo", true));
				restrictions.add(Restrictions.eq("institucion.idInstitucion", idInstitucion));

				mngrArea.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar areas ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "buscar areas");
				throw e;
			}
			try

			{// buscar asunto
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.gt("idAsunto", idAsuntoBuscar));

				mngrAsunto.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar asunto ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "buscar asunto");
				throw e;
			}
			try

			{ // obtener asunto
				long lStartTime = System.nanoTime();

				mngrAsunto.fetch(idAsuntoFetch);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("obtener asunto ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "obtener asunto");
				throw e;
			}
			try

			{// buscar documentos asunto
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("idAsunto", idAsuntoDocumentosFetch));

				mngrDocsAsunto.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar documentos asunto ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "buscar documentos asunto");
				throw e;
			}
			try

			{// buscar respuestas de asunto
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("idAsunto", idAsuntoRespuestasFetch));

				mngrRespuesta.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar respuestas de asunto ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "buscar respuestas de asunto");
				throw e;
			}
			try

			{// buscar minutario
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.gt("idMinutario", idMinutarioSearch));

				mngrMinutario.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar minutario ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "buscar minutario");
				throw e;
			}
			try

			{ // obtener minutario
				long lStartTime = System.nanoTime();

				mngrMinutario.fetch(idMinutario);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("obtener minutario ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "obtener minutario");
				throw e;
			}
			try

			{ // documentos respuesta
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("idRespuesta", idRespuestaDocumentosFetch));

				mngrDocsRespuesta.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar documentos respuesta ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "documentos respuesta");
				throw e;
			}
			try

			{ // documentos minutario
				long lStartTime = System.nanoTime();

				List<Criterion> restrictions = new ArrayList<Criterion>();

				restrictions.add(Restrictions.eq("idMinutario", idMinutarioAnexosFetch));

				mngrDocsMinutario.search(restrictions);

				long lEndTime = System.nanoTime();

				long output = lEndTime - lStartTime;

				times.put("buscar documentos minutario ", output / 1000000);
			} catch (Exception e) {
				times.put("Error in", "documentos minutario");
				throw e;
			}

			return times;

		} catch (Exception e) {

			times.put("Error", e.getLocalizedMessage());

			return times;

		}
	}

	/**
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Testeo sanityCheck", notes = "Realiza una consulta a la base de datos de cada opcion que se elija y regresa los registros que coinciden")
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

	@RequestMapping(value = "/sanityCheck", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> sanityCheck(@RequestBody(required = true) List<Integer> options) {

		Map<String, Object> result = new HashMap<String, Object>();
		options.stream().forEach(i -> {

			switch (i) {

			case 1:
				try {

					String queryName = "areasSinFolios";
					List<Area> returnQuery = mngrArea.execNamedQuery(queryName, null);

					result.put(queryName, returnQuery);

				} catch (Exception e) {

					result.put("errorAreasSinFolios", e.getLocalizedMessage());

				}
				break;
			case 2:
				try {

					String queryName = "areasSinFoliosDesbloqueados";
					List<Area> returnQuery = mngrArea.execNamedQuery(queryName, null);

					result.put(queryName, returnQuery);

				} catch (Exception e) {

					result.put("errorAreasSinFoliosDesbloqueados", e.getLocalizedMessage());

				}
				break;
			case 3:
				try {

					String queryName = "asuntosSinFechaReg";
					List<Area> returnQuery = mngrArea.execNamedQuery(queryName, null);

					result.put(queryName, returnQuery);

				} catch (Exception e) {

					result.put("errorAsuntosSinFechaReg", e.getLocalizedMessage());

				}
				break;
			case 4:
				try {

					String queryName = "areasSinDescripcion";
					List<Area> returnQuery = mngrArea.execNamedQuery(queryName, null);

					result.put(queryName, returnQuery);

				} catch (Exception e) {

					result.put("errorAreasSinDescripcion", e.getLocalizedMessage());

				}
				break;
			case 5:
				try {

					String queryName = "areasSinContentId";
					List<Area> returnQuery = mngrArea.execNamedQuery(queryName, null);

					result.put(queryName, returnQuery);

				} catch (Exception e) {

					result.put("errorAreasSinContentId", e.getLocalizedMessage());

				}
				break;
			case 6:
				try {
					ProjectionList projections = Projections.projectionList();
					projections.add(Projections.countDistinct("idArea").as("countr"));

					Map<String, Long> map = (Map<String, Long>) mngrArea.search(null, null, projections, null, null)
							.get(0);

					result.put("countArea", map.get("countr"));

				} catch (Exception e) {

					result.put("errorCountArea", e.getLocalizedMessage());

				}
				break;
			case 7:
				try {
					ProjectionList projections = Projections.projectionList();
					projections.add(Projections.countDistinct("idAsunto").as("countr"));
					List<Criterion> res = new ArrayList<>();
					res.add(Restrictions.eq("tipoAsunto", TipoAsunto.ASUNTO));
					Map<String, Long> map = (Map<String, Long>) mngrAsunto.search(res, null, projections, null, null)
							.get(0);

					result.put("countAsunto", map.get("countr"));

				} catch (Exception e) {

					result.put("errorCountAsunto", e.getLocalizedMessage());

				}
				break;
			case 8:
				try {
					ProjectionList projections = Projections.projectionList();
					projections.add(Projections.countDistinct("idAsunto").as("countr"));
					List<Criterion> res = new ArrayList<>();
					res.add(Restrictions.ne("tipoAsunto", TipoAsunto.ASUNTO));
					Map<String, Long> map = (Map<String, Long>) mngrAsunto.search(res, null, projections, null, null)
							.get(0);

					result.put("countTramite", map.get("countr"));

				} catch (Exception e) {

					result.put("errorcountTramite", e.getLocalizedMessage());

				}
				break;
			case 9:
				try {
					ProjectionList projections = Projections.projectionList();
					projections.add(Projections.countDistinct("idRespuesta").as("countr"));

					Map<String, Long> map = (Map<String, Long>) mngrRespuesta
							.search(null, null, projections, null, null).get(0);

					result.put("countRespuesta", map.get("countr"));

				} catch (Exception e) {

					result.put("errorCountRespuesta", e.getLocalizedMessage());

				}
				break;
			case 10:
				try {
					ProjectionList projections = Projections.projectionList();
					projections.add(Projections.countDistinct("idUsuario").as("countr"));

					Map<String, Long> map = (Map<String, Long>) mngrUsuario.search(null, null, projections, null, null)
							.get(0);

					result.put("countUsuario", map.get("countr"));

				} catch (Exception e) {

					result.put("errorCountUsuario", e.getLocalizedMessage());

				}
				break;
			case 11:
				result.put("usuariosSinAreas", usuariosSinAreas());
				break;
			case 12:
				result.put("areaSinAcl", areaSinAcl());
				break;
			case 13:
				result.put("areaSinGrupo", areaSinGrupo());
				break;
			case 14:
				result.put("areaSinFolder", areaSinFolder());
				break;
			case 15:
				result.put("documentoSinId", documentoSinId());
				break;
			case 16:
				try {
					List<Criterion> res = new ArrayList<>();
					res.add(Restrictions.ne("tipoAsunto", TipoAsunto.ASUNTO));

					DetachedCriteria dc = DetachedCriteria.forClass(Respuesta.class, "r");

					ProjectionList projections = Projections.projectionList();
					projections.add(Projections.distinct(Projections.property("idAsunto")));

					dc.add(Restrictions.eq("status.idStatus", Status.CONCLUIDO));
					dc.add(Restrictions.eq("tipoRespuesta.tipoConcluida", true));
					dc.setProjection(projections);

					res.add(Subqueries.propertyIn("idAsunto", dc));
					res.add(Restrictions.eq("statusTurno.idStatus", Status.PROCESO));
					List<?> lst = mngrAsunto.search(res);
					result.put("tramitesProcesados", lst);
				} catch (Exception e) {

					result.put("errorTramitesProcesados", e.getLocalizedMessage());
				}
				break;
			}
		});
		return result;
	}

	private List<Usuario> usuariosSinAreas() {

		List<Usuario> lst = new ArrayList<>();
		IEndpoint endpoint = EndpointDispatcher.getInstance();

		List<Usuario> usuarios = (List<Usuario>) mngrUsuario.search(null);

		for (Usuario usuario : usuarios) {
			try {
				String grupo = String.format("%s%n", environment.getProperty("grpSigap") + usuario.getIdArea());

				if (!endpoint.existsUsuarioGrupo(usuario.getIdUsuario(), grupo)) {

					List<Criterion> restrictions = new ArrayList<>();
					restrictions.add(Restrictions.eq("accesoKey.idUsuario", usuario.getIdUsuario()));
					List<Acceso> accesos = (List<Acceso>) mngrAcceso.search(restrictions);

					for (Acceso acceso : accesos) {

						grupo = String.format("grp_sigap_%n", acceso.getAccesoKey().getArea().getIdArea());
						if (endpoint.existsUsuarioGrupo(usuario.getIdUsuario(), grupo)) {
							continue;
						}
						lst.add(usuario);
					}
					lst.add(usuario);
				}
			} catch (Exception e) {
				lst.add(usuario);
			}
		}

		return lst;
	}

	/**
	 * 
	 * @return
	 */
	private List<Area> areaSinAcl() {

		List<Area> lst = new ArrayList<>();

		List<Area> areas = (List<Area>) mngrArea.search(null);

		for (Area area : areas) {
			try {
				Map<String, String> additionalData = new HashMap<>();
				additionalData.put("idArea", area.getIdArea().toString());

				Acl acl = EndpointDispatcher.getInstance().getAcl(environment.getProperty("aclNameFolderArea"),
						additionalData);
				if (Objects.isNull(acl)) {
					lst.add(area);
				}
			} catch (Exception e) {
				lst.add(area);
			}
		}
		return lst;
	}

	/**
	 * 
	 * @return
	 */
	private List<Area> areaSinGrupo() {

		List<Area> lst = new ArrayList<>();

		List<Area> areas = (List<Area>) mngrArea.search(null);

		for (Area area : areas) {
			try {

				String grupo = EndpointDispatcher.getInstance()
						.getIdGrupo(String.format("%s%n", environment.getProperty("grpSigap") + area.getIdArea()));

				if (StringUtils.isNotBlank(grupo)) {
					lst.add(area);
				}
			} catch (Exception e) {
				lst.add(area);
			}
		}
		return lst;
	}

	/**
	 * 
	 * @return
	 */
	private List<Area> areaSinFolder() {

		List<Area> lst = new ArrayList<>();

		List<Area> areas = (List<Area>) mngrArea.search(null);

		for (Area area : areas) {
			try {

				Boolean exists = EndpointDispatcher.getInstance()
						.existeCarpeta(String.format("%s/%s", getParamApp("CABINET"), getNombreFolder(area)));

				if (Objects.isNull(exists) || !exists) {
					lst.add(area);
				}
			} catch (Exception e) {
				lst.add(area);
			}
		}
		return lst;
	}

	/**
	 * 
	 * @return
	 */
	private List<DocumentoAsunto> documentoSinId() {

		List<DocumentoAsunto> lst = new ArrayList<>();

		List<DocumentoAsunto> dcosAsuntos = (List<DocumentoAsunto>) mngrDocsAsunto.search(null);

		for (DocumentoAsunto docs : dcosAsuntos) {
			try {

				Map<String, Object> map = EndpointDispatcher.getInstance().getObjectProperties(docs.getObjectId());

				if (Objects.isNull(map) || map.isEmpty()) {
					lst.add(docs);
				}
			} catch (Exception e) {
				lst.add(docs);
			}
		}
		return lst;
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	private String getNombreFolder(Area item) {
		// folder de area
		StringBuilder nombreFolder = new StringBuilder();
		nombreFolder.append(item.getInstitucion().getIdInstitucion()).append("_").append(item.getDescripcion())
				.append("_").append(item.getIdArea());

		return nombreFolder.toString();
	}

}
