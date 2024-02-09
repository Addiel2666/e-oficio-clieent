/**
 * 
 */
package com.ecm.sigap.security.auth;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * @author Alejandro Guzman
 *
 */
public class SacgAbstractAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	/** Logger de la clase */
	private static final Logger log = LogManager.getLogger(SacgAbstractAuthenticationFilter.class);

	protected SacgAbstractAuthenticationFilter() {

		super("/login/cas");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * AbstractAuthenticationProcessingFilter
	 * #attemptAuthentication(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		log.debug("::: Ejecutando el metodo attemptAuthentication(HttpServletRequest, HttpServletResponse)");

		// set ApiKeyToken
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			log.debug("::: Parametro del header ::::[Key=" + key + "] [Value=" + request.getHeader(key) + "]");
		}

		final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("aguzman", "");

		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

		return this.getAuthenticationManager().authenticate(authRequest);

	}

}
