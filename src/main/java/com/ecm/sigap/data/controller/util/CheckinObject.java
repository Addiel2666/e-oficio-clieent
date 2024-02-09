/**
 * Copyright (c) 2016 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.util;

import com.ecm.cmisIntegracion.model.Version;

/**
 * Parametros para la operacion de versionamiento de un documento en el
 * repositorio.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class CheckinObject {

	/** */
	private String objectId;
	/** */
	private Version version;
	/** */
	private String newVersion;
	/** */
	private String comment;
	/** */
	private String nombre;
	/** */
	private String documentB64;
	/** */
	private String newObjectId;

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
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the documentB64
	 */
	public String getDocumentB64() {
		return documentB64;
	}

	/**
	 * @param documentB64
	 *            the documentB64 to set
	 */
	public void setDocumentB64(String documentB64) {
		this.documentB64 = documentB64;
	}

	/**
	 * @return the newObjectId
	 */
	public String getNewObjectId() {
		return newObjectId;
	}

	/**
	 * @param newObjectId
	 *            the newObjectId to set
	 */
	public void setNewObjectId(String newObjectId) {
		this.newObjectId = newObjectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckinObject [objectId=" + objectId + ", version=" + version + ", comment=" + comment + ", nombre="
				+ nombre + ", newObjectId=" + newObjectId + "]";
	}

	/**
	 * @return the newVersion
	 */
	public String getNewVersion() {
		return newVersion;
	}

	/**
	 * @param newVersion
	 *            the newVersion to set
	 */
	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}

}
