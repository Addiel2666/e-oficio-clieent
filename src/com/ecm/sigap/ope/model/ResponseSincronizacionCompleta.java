/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Samuel Garcia
 *
 */
public class ResponseSincronizacionCompleta implements Serializable {

	//TODO hacer Refactorizacion de campos del response
	/** */
	private static final long serialVersionUID = -7107463174856924466L;
	/** */
	private String value;
	/** */
	private String nombreCorto;
	/** */
	private List<AreaInteropera> areasInteropera;
	/** */
	private String titularArea;
	/** */
	private String cargoTitular;
	/** */
	private String correoElectronico;
	/** */
	private String idTitular;
	/** */
	private String institucion;
	/** */
	private String url;
	/** */
	private String versionCatalogo;
	
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the nombreCorto
	 */
	public String getNombreCorto() {
		return nombreCorto;
	}

	/**
	 * @param nombreCorto the nombreCorto to set
	 */
	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}

	/**
	 * @return the areasInteropera
	 */
	public List<AreaInteropera> getAreasInteropera() {
		return areasInteropera;
	}

	/**
	 * @param areasInteropera the nombreCorto to set
	 */
	public void setAreasInteropera(List<AreaInteropera> areasInteropera) {
		this.areasInteropera = areasInteropera;
	}

	/**
	 * @return the value
	 */
	public String getTitularArea() {
		return titularArea;
	}

	public void setTitularArea(String titularArea) {
		this.titularArea = titularArea;
	}

	/**
	 * @return the cargoTitular
	 */
	public String getCargoTitular() {
		return cargoTitular;
	}

	/**
	 * @param cargoTitular the cargoTitular to set
	 */
	public void setCargoTitular(String cargoTitular) {
		this.cargoTitular = cargoTitular;
	}

	/**
	 * @return the correoElectronico
	 */
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	/**
	 * @param correoElectronico the correoElectrocnico to set
	 */
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	/**
	 * @return the titular
	 */
	public String getIdTitular() {
		return idTitular;
	}

	/**
	 * @param titular the titular to set
	 */
	public void setIdTitular(String idTitular) {
		this.idTitular = idTitular;
	}

	/**
	 * @return the institucion
	 */
	public String getInstitucion() {
		return institucion;
	}

	/**
	 * @param intitucion the intitucion to set
	 */
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
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
	
	

}
