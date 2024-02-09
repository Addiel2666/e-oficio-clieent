/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import java.util.Map;

import com.ecm.sigap.data.model.Usuario;

/**
 * The Class ConfiguracionNotificacion.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
public class ConfiguracionNotificacion {

	/** The id usuario. */
	private Usuario usuario;

	/** The notificacion. */
	private Map<String, Boolean> notificacion;

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
	 * Gets the notificacion.
	 *
	 * @return the notificacion
	 */
	public Map<String, Boolean> getNotificacion() {
		return notificacion;
	}

	/**
	 * Sets the notificacion.
	 *
	 * @param notificacion
	 *            the notificacion
	 */
	public void setNotificacion(Map<String, Boolean> notificacion) {
		this.notificacion = notificacion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfiguracionNotificacion [usuario=" + usuario + ", notificacion=" + notificacion + "]";
	}

}
