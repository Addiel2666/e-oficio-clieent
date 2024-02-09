/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.cache.CacheStore;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.impl.PermisoController;
import com.ecm.sigap.data.exception.SessionException;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.UsuarioConectado;
import com.ecm.sigap.data.model.UsuarioConectadoKey;
import com.ecm.sigap.data.service.EntityManager;
import com.ecm.sigap.security.util.Security;

/**
 * Interceptor de todos los request hacia el core, filtra solicitudes
 * desconocidas de usuarios no autenticados en la applicacion
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Component
public class ControllerInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LogManager.getLogger(ControllerInterceptor.class);

	/** Manejador para el tipo {@link com.ecm.sigap.data.model.Area} */
	@Autowired
	@Qualifier("areaService")
	private EntityManager<Area> mngrArea;

	/** Manejador para el tipo {@link com.ecm.sigap.data.model.Usuario} */
	@Autowired
	@Qualifier("usuarioService")
	private EntityManager<Usuario> mngrUsuario;

	/** */
	@Autowired
	private PermisoController permisoController;

	@Autowired
	@Qualifier("usuarioConectadoService")
	protected EntityManager<UsuarioConectado> mngrUsuarioConectado;

	/** */
	@Autowired
	private Environment environment;

	/** */
	@Autowired
	private CacheStore<String> connectedUsersCache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * preHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String requestURL = request.getRequestURI();
		// String deployName = request.getContextPath();

		if (requestURL.endsWith(".js") //
				|| requestURL.endsWith(".gif") //
				|| requestURL.endsWith(".ico") //
				|| requestURL.endsWith(".png") //
				|| requestURL.endsWith(".css") //
				|| requestURL.endsWith(".map")
				|| requestURL.endsWith("/downloadDocument2")
				) {
			if (requestURL.endsWith(".js"))
				response.setHeader("Content-Type", "application/javascript");
			if (requestURL.endsWith(".css"))
				response.setHeader("Content-Type", "text/css");
			if (requestURL.endsWith(".ico"))
				response.setHeader("Content-Type", "image/x-icon");
			if (requestURL.endsWith(".gif"))
				response.setHeader("Content-Type", "image/gif");
			if (requestURL.endsWith(".png"))
				response.setHeader("Content-Type", "image/png");
			return true;
		}

		// - - - - - - - - - - - - - - - - - - - - - - - - -

		String user_id = request.getHeader(HeaderValueNames.HEADER_USER_ID.getName());
		String content_user = request.getHeader(HeaderValueNames.HEADER_CONTENT_USER.getName());
		String user_key = request.getHeader(HeaderValueNames.HEADER_USER_KEY.getName());
		String area_id = request.getHeader(HeaderValueNames.HEADER_AREA_ID.getName());
		String token_id = request.getHeader(HeaderValueNames.HEADER_AUTH_TOKEN.getName());

		// - - - - - - - - - - - - - - - - - - - - - - - - -
		try {
			if (StringUtils.isBlank(user_id))
				throw new Exception("Header " + HeaderValueNames.HEADER_USER_ID.getName() + " missing value!");

			// - - - - - - - - - - - - - - - - - - - - - - - - -

			if (StringUtils.isBlank(content_user))
				throw new Exception("Header " + HeaderValueNames.HEADER_CONTENT_USER.getName() + " missing value!");

			// - - - - - - - - - - - - - - - - - - - - - - - - -

			if (StringUtils.isBlank(user_key))
				throw new Exception("Header " + HeaderValueNames.HEADER_USER_KEY.getName() + " missing value!");

			// - - - - - - - - - - - - - - - - - - - - - - - - -

			if (StringUtils.isBlank(area_id))
				throw new Exception("Header " + HeaderValueNames.HEADER_AREA_ID.getName() + " missing value!");

			// - - - - - - - - - - - - - - - - - - - - - - - - -

			if (StringUtils.isBlank(token_id))
				throw new Exception("Header " + HeaderValueNames.HEADER_AUTH_TOKEN.getName() + " missing value!");
			else {

				// validar que el token de seguridad sea valido,
				validateToken(request, user_id);

			}

		} catch (Exception e) {

			// log.error(e.getLocalizedMessage());
			return false;

		}

		// - - - - - - - - - - - - - - - - - - - - - - - - -
		String userId;
		try {

			// validar area conectada exista y sea valida
			int areaId = Integer.parseInt(Security.decript(area_id));

			Area areaConectada = mngrArea.fetch(areaId);

			if (areaConectada == null || !areaConectada.getActivo())
				throw new Exception("Invalid " + HeaderValueNames.HEADER_AREA_ID.getName() + " !");

			// validar usuario conectado exista y sea valido
			userId = Security.decript(user_id);
			String userKey = Security.decript(content_user);
			Usuario usuarioConectado = mngrUsuario.fetch(userId);

			if (usuarioConectado == null //
					|| usuarioConectado.getActivo() == null || !usuarioConectado.getActivo()//
					|| usuarioConectado.getCapacitado() == null || !usuarioConectado.getCapacitado()
					|| !usuarioConectado.getUserKey().equalsIgnoreCase(userKey))
				throw new Exception("Invalid " + HeaderValueNames.HEADER_USER_ID.getName() + " !");

			// validar que el usuario pertenesca al area.
			ResponseEntity<List<Permiso>> permisosResponse = permisoController.getPermisos(userId, areaId);

			List<Permiso> permisos = permisosResponse.getBody();

			if (permisos == null || permisos.isEmpty()) {
				throw new Exception("Invalid " + HeaderValueNames.HEADER_AUTH_TOKEN.getName() + " :: "
						+ HeaderValueNames.HEADER_USER_ID.getName() + " combination.");
			}

			// validar coneccion al repositorio
			if (!EndpointDispatcher.getInstance().validarAccesso(userKey, user_key)) {
				throw new Exception("Invalid " + HeaderValueNames.HEADER_USER_ID.getName() + " :: "
						+ HeaderValueNames.HEADER_USER_KEY.getName() + " combination.");
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return false;

		}

		// cache
		if (Boolean.parseBoolean(environment.getProperty("usuario.session.unica"))) {
			final String tokenCache = connectedUsersCache.get(userId);
			if (requestURL.contains("/seguridad/logout")) {
				if (tokenCache != null && !tokenCache.equals(token_id)) {
					throw new SessionException("El usuario '" + userId + "' tiene una nueva sesion activa");
				}
			} else {
				if (tokenCache != null && !tokenCache.equals(token_id)) {
					throw new SessionException("El usuario '" + userId + "' tiene una nueva sesion activa");
				} else {
					connectedUsersCache.add(user_id, token_id);
				}
			}
		}

		// - - - - - -

		// insert con usuario conectado.

		Date loginTime = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(loginTime);

		UsuarioConectado item = new UsuarioConectado();
		item.setUsuarioConectadoKey(new UsuarioConectadoKey());
		item.getUsuarioConectadoKey().setIdUsuario(userId);
		item.getUsuarioConectadoKey().setDay(c.get(Calendar.DATE));
		item.setLoginTime(loginTime);
		item.setIpAddress(request.getRemoteAddr());

		try {
			mngrUsuarioConectado.saveOrUpdate(item);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
		// - - - - - -

		return true;
	}

	/**
	 * 
	 * @param servletRequest
	 * @param user_id
	 * @throws Exception
	 */
	private void validateToken(HttpServletRequest servletRequest, String user_id) throws Exception {

		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) servletRequest
				.getUserPrincipal();

		if (token.getPrincipal().toString().equalsIgnoreCase(Security.decript(user_id))) {
			// OK
		} else {
			throw new Exception("Invalid " + HeaderValueNames.HEADER_AUTH_TOKEN.getName() + " :: "
					+ HeaderValueNames.HEADER_USER_ID.getName() + " combination.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * postHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * org.springframework.web.servlet.ModelAndView)
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/YYYY HH:MM:SS");

		response.setHeader("sacg-response-time", sdf.format(now));

	}
}