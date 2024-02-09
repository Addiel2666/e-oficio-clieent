/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo.model;

/**
 * 
 * @author
 * @version 1.0
 *
 */
public class EArchivoDocumento {

	/** */
	private String objectId;
	/** */
	private Integer idLegajo;
	/** */
	private Integer tipoCatalogo;
	/** */
	private Integer idExpediente;
	
	private Integer numeroLegajo;

	/**
	 * 
	 * @return
	 */
	public Integer getIdExpediente() {
		return idExpediente;
	}

	/**
	 * 
	 * @param idExpediente
	 */
	public void setIdExpediente(Integer idExpediente) {
		this.idExpediente = idExpediente;
	}

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the idLegajo
	 */
	public Integer getIdLegajo() {
		return idLegajo;
	}

	/**
	 * @param idLegajo
	 *            the idLegajo to set
	 */
	public void setIdLegajo(Integer idLegajo) {
		this.idLegajo = idLegajo;
	}

	/**
	 * @return the tipoCatalogo
	 */
	public Integer getTipoCatalogo() {
		return tipoCatalogo;
	}

	/**
	 * @param tipoCatalogo
	 *            the tipoCatalogo to set
	 */
	public void setTipoCatalogo(Integer tipoCatalogo) {
		this.tipoCatalogo = tipoCatalogo;
	}

	public Integer getNumeroLegajo() {
		return numeroLegajo;
	}

	public void setNumeroLegajo(Integer numeroLegajo) {
		this.numeroLegajo = numeroLegajo;
	}

	@Override
	public String toString() {
		return "EArchivoDocumento [objectId=" + objectId + ", idLegajo=" + idLegajo + ", tipoCatalogo=" + tipoCatalogo
				+ ", idExpediente=" + idExpediente + ", numeroLegajo=" + numeroLegajo + "]";
	}
	
}
