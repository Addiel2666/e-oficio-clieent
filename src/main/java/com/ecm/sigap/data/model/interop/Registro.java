/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */

package com.ecm.sigap.data.model.interop;

import com.ecm.sigap.data.model.Institucion;
import com.ecm.sigap.data.model.Usuario;

/**
 * The Class Registro.
 */
public class Registro {

	/** The institucion. */
	private Institucion institucion;

	/** The usuario. */
	private Usuario usuario;

	/** The endpoint. */
	private String endpoint;

	/** The url web service sigap. */
	private String urlWebServiceSigap;

	/** The uri. */
	private String uri;

	/** The url web service ope. */
	private String urlWebServiceOpe;

	/** The registrado ope. */
	private Boolean registradoOpe;

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion
	 *            the new institucion
	 */
	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	/**
	 * Gets the usuario.
	 *
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Sets the usuario.
	 *
	 * @param usuario
	 *            the new usuario
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * Gets the endpoint.
	 *
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Sets the endpoint.
	 *
	 * @param endpoint
	 *            the new endpoint
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Gets the url web service sigap.
	 *
	 * @return the url web service sigap
	 */
	public String getUrlWebServiceSigap() {
		return urlWebServiceSigap;
	}

	/**
	 * Sets the url web service sigap.
	 *
	 * @param urlWebServiceSigap
	 *            the new url web service sigap
	 */
	public void setUrlWebServiceSigap(String urlWebServiceSigap) {
		this.urlWebServiceSigap = urlWebServiceSigap;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri
	 *            the new uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the url web service ope.
	 *
	 * @return the url web service ope
	 */
	public String getUrlWebServiceOpe() {
		return urlWebServiceOpe;
	}

	/**
	 * Sets the url web service ope.
	 *
	 * @param urlWebServiceOpe
	 *            the new url web service ope
	 */
	public void setUrlWebServiceOpe(String urlWebServiceOpe) {
		this.urlWebServiceOpe = urlWebServiceOpe;
	}

	/**
	 * Gets the registrado ope.
	 *
	 * @return the registrado ope
	 */
	public Boolean getRegistradoOpe() {
		return registradoOpe;
	}

	/**
	 * Sets the registrado ope.
	 *
	 * @param registradoOpe
	 *            the new registrado ope
	 */
	public void setRegistradoOpe(Boolean registradoOpe) {
		this.registradoOpe = registradoOpe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Registro [institucion=" + institucion + ", usuario=" + usuario + ", endpoint=" + endpoint
				+ ", urlWebServiceSigap=" + urlWebServiceSigap + ", uri=" + uri + ", urlWebServiceOpe="
				+ urlWebServiceOpe + ", registradoOpe=" + registradoOpe + "]";
	}
}
