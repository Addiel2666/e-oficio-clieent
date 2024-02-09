/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum HeaderValueNames {

	/** Id del Token de la sesion del usuario */
	HEADER_AUTH_TOKEN("sacg-token"),

	/** ID del usuario haciendo la solicitud. */
	HEADER_USER_ID("sacg-user-id"),

	/** Password del usuario en el repositorio */
	HEADER_USER_KEY("sacg-user-key"),

	/** Id del area a la cual esta conectada el usuario. */
	HEADER_AREA_ID("sacg-area-id"),

	/** Id del usuario para la conexion al repositorio */
	HEADER_CONTENT_USER("sacg-content-user"),

	/** */
	HEADER_ECIUDADANO_CERT("login-cert");

	/** */
	private final String h;

	/**
	 * 
	 * @param t
	 */
	HeaderValueNames(String h) {
		this.h = h;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return this.h;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static HeaderValueNames fromString(String t) {
		if (t != null)
			for (HeaderValueNames h_ : HeaderValueNames.values())
				if (t.equalsIgnoreCase(h_.h))
					return h_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
