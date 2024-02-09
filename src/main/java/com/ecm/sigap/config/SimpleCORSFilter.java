/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.ecm.sigap.data.controller.HeaderValueNames;

/**
 * TODO: TEMPORARL IMPLEMENTAR CAS
 * 
 * @author Alfredo Morales
 *
 */
@Component
public class SimpleCORSFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Headers",
				"cache-control,Origin,X-Requested-With,Content-Type,Accept," //
						+ HeaderValueNames.HEADER_AREA_ID.getName() //
						+ "," + HeaderValueNames.HEADER_AUTH_TOKEN.getName() //
						+ "," + HeaderValueNames.HEADER_ECIUDADANO_CERT.getName() //
						+ "," + HeaderValueNames.HEADER_USER_KEY.getName() //
						+ "," + HeaderValueNames.HEADER_USER_ID.getName() //
						+ "," + HeaderValueNames.HEADER_CONTENT_USER.getName());

		chain.doFilter(req, res);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

}