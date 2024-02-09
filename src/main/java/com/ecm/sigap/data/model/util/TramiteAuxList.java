/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import java.util.List;

import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoAsunto;

/**
 * The Class TramiteAuxList.
 */
public class TramiteAuxList {

	/** The list tramites. */
	private List<Asunto> listTramite;

	/** The list doc tramite. */
	private List<DocumentoAsunto> listDocTramite;

	/** The add doc tramites copias. */
	private Boolean addDocTramitesCopias;

	/**
	 * Gets the list tramite.
	 *
	 * @return the list tramite
	 */
	public List<Asunto> getListTramite() {
		return listTramite;
	}

	/**
	 * Sets the list tramite.
	 *
	 * @param listTramite the new list tramite
	 */
	public void setListTramite(List<Asunto> listTramite) {
		this.listTramite = listTramite;
	}

	/**
	 * Gets the list doc tramite.
	 *
	 * @return the list doc tramite
	 */
	public List<DocumentoAsunto> getListDocTramite() {
		return listDocTramite;
	}

	/**
	 * Sets the list doc tramite.
	 *
	 * @param listDocTramite the new list doc tramite
	 */
	public void setListDocTramite(List<DocumentoAsunto> listDocTramite) {
		this.listDocTramite = listDocTramite;
	}

	/**
	 * Gets the adds the doc tramites copias.
	 *
	 * @return the adds the doc tramites copias
	 */
	public Boolean getAddDocTramitesCopias() {
		return addDocTramitesCopias;
	}

	/**
	 * Sets the adds the doc tramites copias.
	 *
	 * @param addDocTramitesCopias the new adds the doc tramites copias
	 */
	public void setAddDocTramitesCopias(Boolean addDocTramitesCopias) {
		this.addDocTramitesCopias = addDocTramitesCopias;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TramiteAuxList [listTramite=" + listTramite + ", listDocTramite=" + listDocTramite
				+ ", addDocTramitesCopias=" + addDocTramitesCopias + "]";
	}
}
