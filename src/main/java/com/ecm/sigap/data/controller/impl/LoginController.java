/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.cache.CacheStore;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.impl.async.ValidarGruposUsuarioAsyncProcess;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.UsuarioCapacita;
import com.ecm.sigap.data.model.UsuarioLogin;
import com.ecm.sigap.security.auth.RestClient;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controlador para la autenticacion del Usuario
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@RestController
public class LoginController extends CustomRestController {

	/** Log de suscesos. */
	private Logger log = LogManager.getLogger(LoginController.class);

	/** URL del servicio de autenticacion de CAS */
	@Value("${seguridad.cas.url}")
	private String casUrl;

	/** */
	private RestClient restClient;

	/** */
	@Autowired
	private CacheStore<String> connectedUsersCache;

	/** Referencia hacia el controller {@link ValidarGruposUsuarioAsyncProcess}. */
	@Autowired
	private ValidarGruposUsuarioAsyncProcess validarGruposUsuarioAsyncProcess;

	/**
	 * 
	 * Obtiene el token de la sesion del Usuario
	 * 
	 * @param usuarioLogin Informacion del usuario (Usuario y password)
	 * @return Token de la sesion del Usuario
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene token", notes = "Obtiene el token de la sesion del usuario")
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

	@RequestMapping(value = "/seguridad/login", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, String>> loginUser(@RequestBody UsuarioLogin usuarioLogin) {

		log.debug("::: Iniciando el login del usuario(" + usuarioLogin + ")");

		String ticket = "";
		Map<String, String> response = new HashMap<String, String>();
		restClient = new RestClient();

		try {

			// Se obtienen los datos del usuario a partir de su Identificador
			Usuario usuario = null;
			boolean keysensitive = Boolean.valueOf(environment.getProperty("mayus.minus.sensible"));
			List<Criterion> restrictions = new ArrayList<Criterion>();
			if (keysensitive) {
				restrictions.add(Restrictions.eq("userKey", decryptText(usuarioLogin.getLogin())));
			}else {
				restrictions.add(Restrictions.eq("userKey", decryptText(usuarioLogin.getLogin())).ignoreCase());
			}

			// mngrUsuario.flush();

			List<?> items = mngrUsuario.search(restrictions);

			if (null == items || items.isEmpty()) {

				log.error("Error al momento de ejecutar la autenticacion - Usuario '" + usuarioLogin.getLogin()
						+ "' no existe dentro del sistema ");
				response.put("error","Error al momento de ejecutar la autenticacion - Usuario y/o password incorrectos");
				// No se encontro el usuario por lo que se niega el acceso
				return new ResponseEntity<Map<String, String>>(response, HttpStatus.PRECONDITION_FAILED);
			}

			usuario = (Usuario) items.get(0);
			log.debug("::: Usuario >>" + usuario);

			//valida si el area del usuario esta activa
			if (!usuario.getAreaAux().getActivo()) {
				response.put("error", "area inactiva");
				return new ResponseEntity<Map<String, String>>(response, HttpStatus.CONFLICT);
			}

			// Usuario no activo o no capacitado
			if (usuario.getActivo() && usuario.getCapacitado()) {

				// primero se valida q el usuario y pass sean correctos en el repo
				String location = getTicketGrantingTicket(usuarioLogin.getLogin(), usuarioLogin.getPassword());
				log.debug("::: Location= " + location);

				// Usuario no ha aceptado las politicas de uso del sistema
				if (!usuario.getAcepto()) {

					UsuarioCapacita uc = mngrUsuarioCapacita.fetch(usuario.getIdUsuario());

					if (!uc.getAcepto()) {
						response.put("aceptoPolitica", String.valueOf(false));
						response.put("ticket", "");
						response.put("idUsuario", usuario.getIdUsuario());
						response.put("idArea", "");
						return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
					}

				}

				// get SGT
				ticket = getServiceGrantingTicket(location, casUrl);

				log.debug("::: Ticket= " + ticket);

				response.put("aceptoPolitica", String.valueOf(true));
				response.put("ticket", ticket);
				response.put("idUsuario", usuario.getIdUsuario());
				response.put("idArea", String.valueOf(usuario.getIdArea()));
				response.put("accessToken", generateAccessToken(usuarioLogin.getLogin(), usuarioLogin.getPassword()));

				// TODO QUITAR ::
				validarGruposUsuarioAsyncProcess.process(usuario);

				// single session
				if (Boolean.parseBoolean(environment.getProperty("usuario.session.unica"))) {
					final String token = connectedUsersCache.get(usuario.getIdUsuario());
					if (token != null) {
						response.put("error", "El Usuario tiene una sesion activa");
						return new ResponseEntity<Map<String, String>>(response, HttpStatus.NOT_ACCEPTABLE);
					} else {
						connectedUsersCache.add(usuario.getIdUsuario(), response.get("ticket"));
					}
				}

				return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);

			} else {

				return new ResponseEntity<Map<String, String>>(response, HttpStatus.CONFLICT);
			}

		} catch (IOException e) {

			log.error("::: Se genero un error en el metodo con la siguiente descripcion: " + e.getMessage());

		}
		response.put("error", "Error al momento de ejecutar la autenticacion - Usuario y/o password incorrectos");
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.PRECONDITION_FAILED);
	}

	/**
	 * With the TGT location and service url this will get the SGT
	 * 
	 * @param tgtLocation
	 * @param serviceUrl
	 * @return
	 * @throws IOException
	 */
	private String getServiceGrantingTicket(String tgtLocation, String serviceUrl) throws IOException {
		log.debug("::: Se va a obtener el service ticket del ticket " + tgtLocation);
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("service", serviceUrl);
		params.put("method", "POST");

		HttpURLConnection conn = restClient.post(tgtLocation, params);
		StringBuilder responseBuilder = new StringBuilder();
		
		try(Reader inputStream = new InputStreamReader(conn.getInputStream(), "UTF-8");
			BufferedReader in = new BufferedReader(inputStream)){
			String input;
			
			while ((input = in.readLine()) != null) {
				responseBuilder.append(input);
			}

			String response = responseBuilder.toString();
			log.debug("SGT -> " + response);

			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Gets the TGT for the given username and password
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 */
	private String getTicketGrantingTicket(String username, String password) throws IOException {

		log.debug("::: Se va a obtener el ticket para el usuario " + username);

		Map<String, Object> params = new LinkedHashMap<>();

		params.put("username", username);
		params.put("password", password);
		params.put("option", "LoginControl");

		HttpURLConnection conn = restClient.post(casUrl, params);

		log.debug(" conn.getResponseCode() " + conn.getResponseCode());

		if (conn.getResponseCode() == 400) {
			log.error("bad username or password");
			throw new IOException("bad username or password");
		} else if (conn.getResponseCode() == 404) {
			log.error("servicio no encontrado :: " + casUrl);
			throw new IOException("servicio no encontrado :: " + casUrl);
		}

		String location = conn.getHeaderField("Location");

		log.debug("TGT LOCATION -> " + location);

		return location;
	}

	/**
	 * 
	 * Actualiza la aceptacion de las policitas de uso del sistema del Usuario
	 * 
	 * @param usuarioLogin Informacion del usuario (Usuario) que acepta la politica
	 * @return Token de la sesion del Usuario
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Politicas del sistema", notes = "Actualiza la aceptacion de las politicas de uso del sistema")
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

	@RequestMapping(value = "/seguridad/politicas", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> aceptaPoliticas(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		log.debug("::: Usuario acepto las politicas de uso (" + id + ")");

		Map<String, String> response = new HashMap<String, String>();

		try {

			// Se obtienen los datos del usuario a partir de su Identificador
			Usuario usuario = null;

			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("userKey", id).ignoreCase());

			List<?> items = mngrUsuario.search(restrictions);

			if (null != items && !items.isEmpty()) {

				usuario = (Usuario) items.get(0);

				log.debug("::: Usuario >> " + usuario + " acepto las politicas de uso");

				String ipAddress = getRemoteIpAddress();

				log.debug("IP remota " + ipAddress);

				UsuarioCapacita usuarioCapacita = mngrUsuarioCapacita.fetch(usuario.getIdUsuario());
				usuarioCapacita.setAcepto(Boolean.TRUE);
				usuarioCapacita.setFecha(new Date());
				usuarioCapacita.setIp(ipAddress);

				response.put("aceptado", "true");
				mngrUsuarioCapacita.update(usuarioCapacita);

				mngrUsuarioCapacita.flush();
				usuarioCapacita = mngrUsuarioCapacita.fetch(usuario.getIdUsuario());

				return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);

			} else {

				log.error("Error al momento aceptar las politicas de uso del usuario " + id + ". Usuario no existe");
				response.put("aceptado", "false");

				// No se encontro el usuario por lo que se niega el acceso
				return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error("::: Se genero un error en el metodo con la siguiente descripcion: " + e.getMessage());

			throw e;
		}
	}

	private String generateAccessToken(String user, String pass) {

		String accessToken = String.format("%s$$#$$%s$$#$$%d", user, pass, System.currentTimeMillis());
		return encryptText(accessToken);
	}

	/**
	 * 
	 * @param body
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Validar token", notes = "Valida que el token no se encuentre expirado")
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

	@RequestMapping(value = "/seguridad/access", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, String>> loginUserWithAccessToken(
			@RequestBody Map<String, String> body) {
		UsuarioLogin login = new UsuarioLogin();
		Map<String, String> error = new HashMap<>();
		try {

			String token[] = decryptText(body.get("accessToken")).split("(\\$\\$\\#\\$\\$)");
			LocalDateTime dateTime = Instant.ofEpochMilli(Long.parseLong(token[2])).atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			long min = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
			if (min > 30) {
				error.put("error", "El token a expirado");
				return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
			}
			login.setLogin(token[0]);
			login.setPassword(token[1]);
		} catch (Exception ex) {
			error.put("error", "Invalid access token");
			return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
		}
		return loginUser(login);
	}

	@RequestMapping(value = "/seguridad/logout", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void logout() {
		final String usuarioId = getHeader(HeaderValueNames.HEADER_USER_ID);
		connectedUsersCache.remove(usuarioId);
	}
}