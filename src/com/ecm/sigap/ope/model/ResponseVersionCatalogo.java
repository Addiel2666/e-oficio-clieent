/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.model;

import java.io.Serializable;

/**
 * @author Alfredo Morales
 *
 */
public final class ResponseVersionCatalogo implements Serializable {

	/** */
	private static final long serialVersionUID = -8532298517139661379L;
	/** */
	private String versionCatalogo;
	/** */
	private String url;

	/**
	 * @return the versionCatalogo
	 */
	public String getVersionCatalogo() {
		return versionCatalogo;
	}

	/**
	 * @param versionCatalogo the versionCatalogo to set
	 */
	public void setVersionCatalogo(String versionCatalogo) {
		this.versionCatalogo = versionCatalogo;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
