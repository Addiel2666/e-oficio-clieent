/**
 * 
 */
package com.ecm.sigap.security.auth;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.security.util.Security;

/**
 * @author Alejandro Guzman
 *
 */
public final class SacgAuthenticationFilter extends GenericFilterBean {

	/** Logger de la clase */
	private static final Logger log = LogManager.getLogger(SacgAuthenticationFilter.class);

	/**
	 * 
	 */
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * 
	 */
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

	/**
	 * 
	 */
	private SacgUserDetailServiceImpl sacgUserDetailService;

	/**
	 * 
	 */
	private RequestMatcher requiresAuthenticationRequestMatcher;

	/**
	 * 
	 */
	private RequestMatcher eciudadanoRequestMatcher;

	/**
	 * 
	 */
	public SacgAuthenticationFilter() {
		log.debug("::: Constructor de la clase");
		this.requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/seguridad/login");
		this.eciudadanoRequestMatcher = new AntPathRequestMatcher("/e-ciudadano/**");
	}

	/**
	 * Se encarga de crear el Usuario Autenticado en el contexto de Seguridad
	 * 
	 * @param userName
	 *            Identificador del Usuario
	 * @param httpRequest
	 *            Request
	 */
	private final void createAutheticatedUser(String userName, HttpServletRequest httpRequest)
			throws AuthenticationException {

		if (log.isDebugEnabled()) {
			log.debug("::: Iniciando la creacion del contexto de seguridad del usuario " + userName);
		}
		try {
			Collection<? extends GrantedAuthority> authorities = this.getSacgUserDetailService()
					.loadUserByUsername(userName).getAuthorities();

			if (authorities.isEmpty()) {
				log.error("::: El usuario " + userName + " no contiene ningun permiso dentro de la aplicacion");
				throw new AuthenticationServiceException(
						"El usuario no contiene ningun permiso configurado en la aplicacion");
			}

			final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName,
					"", authorities);

			authRequest.setDetails(authenticationDetailsSource.buildDetails(httpRequest));

			if (log.isDebugEnabled()) {
				log.debug(
						"::: Se generaron todos los elementos necesarios para crear el contexto de Autenticacion del Usuario");
			}
			SecurityContextHolder.getContext().setAuthentication(authRequest);

			if (log.isDebugEnabled()) {
				log.debug("::: Contexto de seguridad creado satisfactoriamente para el Usuario " + userName);
			}
		} catch (UsernameNotFoundException e) {
			
			throw new AuthenticationServiceException(
					"Error al momento de obtener los permisos del usuario con la siguiente descripcion: "
							+ e.getMessage());
		} catch (Exception e) {
			
			throw new AuthenticationServiceException(
					"Error al momento de obtener los permisos del usuario con la siguiente descripcion: "
							+ e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (log.isDebugEnabled()) {
			log.debug("::: Iniciando el Filtro de Seguridad de la app SACG");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (!requiresAuthentication(httpRequest)) {
			log.debug("::: La URL '" + httpRequest.getRequestURI() + "' no necesita autenticacion");
			chain.doFilter(request, response);
			return;
		}

		if (!isEciudadano(httpRequest)) {
			log.debug("::: La URL '" + httpRequest.getRequestURI() + "' no necesita autenticacion");
			chain.doFilter(request, response);
			return;
		}

		// Validamos si el usuario ya estaba autenticado o tiene que
		// autenticarse
		if (isAuthenticationTokenExist(httpRequest)) {

			// String authToken = getSacgAuthenticatedToken(httpRequest);
			// TODO validar que el token es valido en CAS
			String userNameAES = httpRequest.getHeader(HeaderValueNames.HEADER_USER_ID.getName());

			String userName;

			try {
				userName = Security.decript(userNameAES);
			} catch (Exception e1) {
				userName = userNameAES;
			}

			try {

				createAutheticatedUser(userName, httpRequest);

			} catch (AuthenticationException e) {
				
				log.error(
						"::: Error al momento de crear el contexto de seguridad del Usuario con la siguiente descripcion: "
								+ e.getMessage());
			}

		}

		chain.doFilter(request, response);

		if (log.isDebugEnabled()) {
			log.debug("::: Contexto de Autenticacion: " + SecurityContextHolder.getContext().getAuthentication());
		}
	}

	/**
	 * Obtiene el valor del ticket que esta guardado en la Cookie
	 * 
	 * @param request
	 * @return
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unused")
	private String getSacgAuthenticatedToken(HttpServletRequest request) throws IllegalArgumentException {

		// Validamos el valor en el headers
		String authToken = request.getHeader(HeaderValueNames.HEADER_AUTH_TOKEN.getName());
		if (null != authToken) {

			log.debug("::: Valor del token de autenticacion: " + authToken);
			return authToken;

		}
		throw new IllegalArgumentException(
				"Ocurrio un error al momento de obtener el valor de token autenticado de la sesion");
	}

	/**
	 * 
	 */
	@PostConstruct
	public void init() {
		log.debug(" *** SacgAuthenticationFilter.init with: " + applicationContext);
	}

	/**
	 * Valida si la Cookie de Autenticacion esta presenta en el encabezado para
	 * no realizar el proceso de Autenticacion
	 * 
	 * @param request
	 * @return
	 */
	private boolean isAuthenticationTokenExist(HttpServletRequest request) {

		if (log.isDebugEnabled()) {
			log.debug("::: Validando los tokens en la cabecera del request");
		}
		if (null != request.getHeader(HeaderValueNames.HEADER_AUTH_TOKEN.getName())
				&& (null != request.getHeader(HeaderValueNames.HEADER_USER_ID.getName()))) {

			log.debug("::: El token existe por lo que se va a verificar su validez");
			// TODO Validar el ticket que esta guardado en la cookie sea valido
			return true;
		}
		return false;
	}

	/**
	 * @param sacgUserDetailService
	 *            the sacgUserDetailService to set
	 */
	public void setSacgUserDetailService(SacgUserDetailServiceImpl sacgUserDetailService) {
		this.sacgUserDetailService = sacgUserDetailService;
	}

	/**
	 * @return the sacgUserDetailService
	 */
	private SacgUserDetailServiceImpl getSacgUserDetailService() {
		return sacgUserDetailService;
	}

	/**
	 * Indicates whether this filter should attempt to process a login request
	 * for the current invocation.
	 * <p>
	 * It strips any parameters from the "path" section of the request URL (such
	 * as the jsessionid parameter in
	 * <em>http://host/myapp/index.html;jsessionid=blah</em>) before matching
	 * against the <code>filterProcessesUrl</code> property.
	 * <p>
	 * Subclasses may override for special requirements, such as Tapestry
	 * integration.
	 *
	 * @return <code>true</code> if the filter should attempt authentication,
	 *         <code>false</code> otherwise.
	 */
	protected boolean requiresAuthentication(HttpServletRequest request) {
		return !requiresAuthenticationRequestMatcher.matches(request);
	}

	protected boolean isEciudadano(HttpServletRequest request) {
		return !eciudadanoRequestMatcher.matches(request);
	}

}
