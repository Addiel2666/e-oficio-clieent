/**
 * 
 */
package com.ecm.sigap.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author aguzman
 *
 */
public class SacgAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

	/** */
	private static final Logger log = LogManager.getLogger(SacgAuthenticationEntryPoint.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("::: Ejecutando el metodo afterPropertiesSet()");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.AuthenticationEntryPoint#commence(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException, ServletException {

		if (log.isDebugEnabled()) {
			log.debug(
					"::: Ejecutando el metodo commence(HttpServletRequest, HttpServletResponse, AuthenticationException)");
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
	}
}