/**
 * 
 */
package com.ecm.sigap.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author aguzman
 *
 */
public class SacgAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	/** */
	private static final Logger log = LogManager.getLogger(SacgAuthenticationSuccessHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * AuthenticationSuccessHandler
	 * #onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.debug(
				":::: Iniciando el metodo onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication )");
		log.debug("::: El usuario autenticado es:" + authentication.getName());

		// response.setHeader(arg0, arg1);sendError(403);/

		// CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
		// .getName());
		//
		// log.debug("::: csrf: " + csrf);
		// if (csrf != null) {
		// log.debug("::: csrf no es null !!");
		//
		// Cookie cookie = new Cookie("XSRF-TOKEN", csrf.getToken());
		// cookie.setPath("/");
		// response.addCookie(cookie);
		//
		// log.debug("::: Token " + csrf.getToken());
		// }
	}
}
