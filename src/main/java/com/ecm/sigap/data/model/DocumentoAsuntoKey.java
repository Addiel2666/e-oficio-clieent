/**
 * 
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Clase de entidad que se usa como clave primaria de la tabla Documentos
 * Asuntos
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@Embeddable
public class DocumentoAsuntoKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2175667654440336757L;

	/** Identificador en el repositorio del documento */
	private String objectId;

	/** Identificador del Asunto asociado al documento */
	private Integer idAsunto;

	/**
	 * Constructor por defecto de la clase
	 */
	public DocumentoAsuntoKey() {
	}

	/**
	 * Full constructor de la clase
	 * 
	 * @param objectId
	 *            Identificador en el repositorio del documento
	 * @param idAsunto
	 *            Identificador del Asunto asociado al documento
	 */
	public DocumentoAsuntoKey(String objectId, Integer idAsunto) {
		this.objectId = objectId;
		this.idAsunto = idAsunto;
	}

	/**
	 * Obtiene el Identificador en el repositorio del documento
	 * 
	 * @return Identificador en el repositorio del documento
	 */
	public final String getObjectId() {

		return objectId;
	}

	/**
	 * Asigna el Identificador en el repositorio del documento
	 * 
	 * @param objectId
	 *            Identificador en el repositorio del documento
	 */
	public void setObjectId(String objectId) {

		this.objectId = objectId;
	}

	/**
	 * Obtiene el Identificador del Asunto asociado al documento
	 * 
	 * @return the idAsunto
	 */
	public final Integer getIdAsunto() {

		return idAsunto;
	}

	/**
	 * Asigna el Identificador del Asunto asociado al documento
	 * 
	 * @param idAsunto
	 *            Identificador del Asunto asociado al documento
	 */
	public void setIdAsunto(Integer idAsunto) {

		this.idAsunto = idAsunto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentoAsuntoKey [objectId=" + objectId + ", idAsunto=" + idAsunto + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idAsunto == null) ? 0 : idAsunto.hashCode());
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentoAsuntoKey other = (DocumentoAsuntoKey) obj;
		if (idAsunto == null) {
			if (other.idAsunto != null)
				return false;
		} else if (!idAsunto.equals(other.idAsunto))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		return true;
	}

	
	
	
	
}
