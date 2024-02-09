/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.AuditoriaPS;
import com.ecm.sigap.data.model.PruebaSintetica;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

/**
 * Hello Message.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class PruebaSinteticaController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(PruebaSinteticaController.class);

	/**
	 * se ejecuta la prueba sintetica cada X tiempo
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "${pruebassinteticas.cronjobTime}")
	public void pruebaSinteticaWatcher() {
		if ("ON".equalsIgnoreCase(environment.getProperty("pruebassinteticas.cronjob", "OFF")))
			pruebaSintetica(true);
	}

	/**
	 * 
	 * @param saveResult
	 * @return
	 */
	@RequestMapping(value = "/pruebaSintetica/test", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> pruebaSintetica(
			@RequestParam(name = "saveResult", required = false, defaultValue = "false") Boolean saveResult) {

		Map<String, Object> response = new HashMap<String, Object>();

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		{

//			File documento = new File(getClass().getClassLoader()
//					.getResource("pruebaSintetica.png").getFile());

			try {

				// * * * * * * * * * * * * * * * * * * * * *

				response = endpoint.saveDocumentoIntoIdSimple(//
						null);

				String objectId = (String) response.get("documentoId");
				Object millis = response.getOrDefault("tiempo_carga_archivo_repositorio", "N/A");
				Object secs = response.getOrDefault("tiempo_carga_archivo_repositorio_millis", "N/A");

				// * * * * * * * * * * * * * * * * * * * * *

				response.put("cargar_archivo_result", "ok");
				response.put("cargar_archivo_id", objectId);
				response.put("tiempo_carga_archivo_repositorio", millis.toString());
				response.put("tiempo_carga_archivo_repositorio_millis", secs.toString());

			} catch (Exception e) {
				response.put("cargar_archivo_result", "fail :: " + e.getMessage());

			}

		}

		{
			Stopwatch timer = Stopwatch.createUnstarted();

			try {

				PruebaSintetica p = new PruebaSintetica();

				String cadena = generateString(250);
				p.setCadena(cadena);

				// * * * * * * * * * * * * * * * * * * * * *
				timer.start();

				mngrPruebaSintetica.save(p);

				timer.stop();
				// * * * * * * * * * * * * * * * * * * * * *

				response.put("guardar_cadena_result", "ok");
				response.put("guardar_cadena_generada", cadena);

			} catch (Exception e) {
				response.put("guardar_cadena_result", "fail :: " + e.getMessage());

			}

			response.put("tiempo_carga_texto_base_datos", timer.elapsed(TimeUnit.SECONDS));
			response.put("tiempo_carga_texto_base_datos_millis", timer.elapsed(TimeUnit.MILLISECONDS));
		}

		{

			try {
				List<Map<String, String>> activeSessions;

				// * * * * * * * * * * * * * * * * * * * * *

				String query = environment.getProperty("pruebassinteticas.dqlSessiones");

				activeSessions = endpoint.obtenerSessionesActivas(query);

				// * * * * * * * * * * * * * * * * * * * * *

				ArrayList<String> allUsers = new ArrayList<String>();

				for (Map<String, String> map : activeSessions) {
					allUsers.add(map.get("user_name"));
				}

				Set<String> users = new HashSet<>(allUsers);

				for (String usrRemove : environment.getProperty("pruebassinteticas.usersExclude").split(",")) {
					if (StringUtils.isNotBlank(usrRemove))
						users.remove(usrRemove);
				}

				// * * * * * * * * * * * * * * * * * * * * *

				response.put("sessiones_activas", users.size());

			} catch (Exception e) {
				response.put("sessiones_activas", "fail :: " + e.getMessage());

			}

		}

		try {
			InetAddress ip = InetAddress.getLocalHost();

			response.put("ip_address", ip.toString());

		} catch (UnknownHostException e1) {
			log.error(e1.getLocalizedMessage());
			response.put("ip_address", "unavailable");
		}

		if (saveResult)
			try {

				ObjectMapper mapperObj = new ObjectMapper();
				String jsonResp = mapperObj.writeValueAsString(response);

				AuditoriaPS item = new AuditoriaPS();

				item.setFechaRegistro(new Date());
				item.setResultado(jsonResp);

				mngrAuditoriaPS.save(item);

			} catch (Exception e) {
				log.error(e.getLocalizedMessage());

				response = new HashMap<String, Object>();
				response.put("saveError", e.getMessage());
			}

		return response;
	}

	/** */
	private static final String characters = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm";

	/** */
	private static String generateString(int length) {

		length -= 17;

		SecureRandom rng = new SecureRandom();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}

		return sdf.format(new Date()) + "-" + (new String(text));
	}

	/**
	 * Formato de fechas.
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/**
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pruebaSintetica/query", method = RequestMethod.POST)
	public @ResponseBody List<AuditoriaPS> pruebaSinteticaConsultar(@RequestBody Map<String, Object> body)
			throws Exception {

		List<AuditoriaPS> result = new ArrayList<AuditoriaPS>();
		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();

			Object start_ = body.get("start");
			Object end_ = body.get("end");

			try {
				if (start_ != null && end_ != null)
					restrictions.add(Restrictions.between("fechaRegistro", //
							sdf.parse(body.get("start").toString() + " 00:00"), //
							sdf.parse(body.get("end").toString() + " 23:59")));

				else if (start_ != null)
					restrictions
							.add(Restrictions.gt("fechaRegistro", sdf.parse(body.get("start").toString() + " 00:00")));

				else if (end_ != null)
					restrictions
							.add(Restrictions.lt("fechaRegistro", sdf.parse(body.get("end").toString() + " 23:59")));
			} catch (ParseException e) {

			}

			List<Order> orders = new ArrayList<>();

			orders.add(Order.desc("fechaRegistro"));

			result = (List<AuditoriaPS>) mngrAuditoriaPS.search(restrictions, orders);

		} catch (Exception e) {

			throw e;

		}

		return result;

	}

	/**
	 * descargar imagen cargada en prueba sintetica,
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pruebaSintetica/downloadFile", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> pruebaSinteticaDownloadFile(
			@RequestParam(name = "id", required = true) String id) {

		Map<String, String> response = new HashMap<>();

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.ilike("resultado", "\"" + id + "\"", MatchMode.ANYWHERE));

		List<AuditoriaPS> result = (List<AuditoriaPS>) mngrAuditoriaPS.search(restrictions);

		if (result == null || result.isEmpty())
			throw new BadRequestException();
		
		File f = null;
		
		try {

			response.put("base64", endpoint.getObjectContentB64(id));

			String fileName = endpoint.getObjectName(id);

			response.put("name", fileName);

			f = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX, fileName);
			f.deleteOnExit();
			
			String ct = new MimetypesFileTypeMap().getContentType(f);

			if (StringUtils.isEmpty(ct)) {
				log.warn("El documento \"" + fileName + "\" con id " + id + " no se pudo obtener su mime-type.");
				ct = "application/octet-stream";
			}

			response.put("type", ct);

		} catch (JsonParseException e) {

		} catch (JsonMappingException e) {

		} catch (IOException e) {

		} catch (Exception e) {

		} finally {
			if(null != f && f.exists())
				f.delete();
		}

		return response;

	}

}
