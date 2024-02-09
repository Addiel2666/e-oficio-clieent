package com.ecm.sigap.ope.model;

import java.io.Serializable;

public class ResponseAsuntoTramite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3156111083166357888L;
	/** */
	private String versionAsunto;
	/** */
	private String url;
	
	public String getVersionAsunto() {
		return versionAsunto;
	}
	public void setVersionAsunto(String versionAsunto) {
		this.versionAsunto = versionAsunto;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	


}
