/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.model;

import java.util.List;

/**
 * @author Samuel Garcia
 *
 */
public class AreaInteropera {
	
	private String descripcion;
	
	private String titular;
	
	private String institucion;
	
	private String areaPadre;
	
	private String cargoTitular;
	
	private String idExterno;
	
	private List<UsuarioAreaInteropera> usuario;
	
	private Integer idArea;
	
	

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the titular
	 */
	public String getTitular() {
		return titular;
	}

	/**
	 * @param titular the titular to set
	 */
	public void setTitular(String titular) {
		this.titular = titular;
	}

	/**
	 * @return the institucion
	 */
	public String getInstitucion() {
		return institucion;
	}

	/**
	 * @param institucion the institucion to set
	 */
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	/**
	 * @return the usuario
	 */
	public List<UsuarioAreaInteropera> getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(List<UsuarioAreaInteropera> usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the areaPadre
	 */
	public String getAreaPadre() {
		return areaPadre;
	}

	/**
	 * @param areaPadre the areaPadre to set
	 */
	public void setAreaPadre(String areaPadre) {
		this.areaPadre = areaPadre;
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
	 * @return the idExterno
	 */
	public String getIdExterno() {
		return idExterno;
	}

	/**
	 * @param idExterno the idExterno to set
	 */
	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}



	
}
