/**
 * 
 */
package com.ecm.sigap.data.controller.impl;

import java.util.List;

import com.ecm.sigap.data.model.DocumentoRespuestaAux;

/**
 * @author Alfredo Morales
 *
 */
public class BodyAddDocuments {

	/** */
	private List<DocumentoRespuestaAux> docs;
	/** */
	private Integer idRespuesta;

	/**
	 * @return the docs
	 */
	public List<DocumentoRespuestaAux> getDocs() {
		return docs;
	}

	/**
	 * @param docs the docs to set
	 */
	public void setDocs(List<DocumentoRespuestaAux> docs) {
		this.docs = docs;
	}

	/**
	 * @return the idRespuesta
	 */
	public Integer getIdRespuesta() {
		return idRespuesta;
	}

	/**
	 * @param idRespuesta the idRespuesta to set
	 */
	public void setIdRespuesta(Integer idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

}
