/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import com.ecm.sigap.data.model.FolioArchivistica;
import com.ecm.sigap.data.model.FolioPS;
import com.ecm.sigap.data.model.FolioPSClave;

/**
 * The Class ConfiguracionNotificacion.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
public class ConfiguracionArea {

	/** The area. */
	private Integer idArea;

	/** The folio disponible. */
	private Integer folioDisponible;

	/** The tipo. */
	private String tipo;

	/** The comparte folio SN. */

	private String comparteFolioSN;

	/** The foliador unico SN. */
	private String foliadorUnicoSN;

	/** The id area heredada. */
	private String idAreaHeredada;

	/** The dias pre not asunto. */
	private String diasPreNotAsunto;

	/** The dias pre not tramite. */
	private String diasPreNotTramite;

	/** The foliops. */
	private FolioPS foliops;

	/** The foliops. */
	private FolioPSClave foliopsclave;

	/** The folio archivistica. */
	private FolioArchivistica folioArchivistica;

	/** The Folio asunto. */
	private Integer FolioAsunto;

	/** The Folio respuesta. */
	private Integer FolioRespuesta;

	/** The Folio clave. */
	private Integer FolioClave;

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the folio disponible.
	 *
	 * @return the folio disponible
	 */
	public Integer getFolioDisponible() {
		return folioDisponible;
	}

	/**
	 * Sets the folio disponible.
	 *
	 * @param folioDisponible the new folio disponible
	 */
	public void setFolioDisponible(Integer folioDisponible) {
		this.folioDisponible = folioDisponible;
	}

	/**
	 * Gets the tipo.
	 *
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Sets the tipo.
	 *
	 * @param tipo the new tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Gets the comparte folio SN.
	 *
	 * @return the comparte folio SN
	 */
	public String getComparteFolioSN() {
		return comparteFolioSN;
	}

	/**
	 * Sets the comparte folio SN.
	 *
	 * @param comparteFolioSN the new comparte folio SN
	 */
	public void setComparteFolioSN(String comparteFolioSN) {
		this.comparteFolioSN = comparteFolioSN;
	}

	/**
	 * Gets the foliador unico SN.
	 *
	 * @return the foliador unico SN
	 */
	public String getFoliadorUnicoSN() {
		return foliadorUnicoSN;
	}

	/**
	 * Sets the foliador unico SN.
	 *
	 * @param foliadorUnicoSN the new foliador unico SN
	 */
	public void setFoliadorUnicoSN(String foliadorUnicoSN) {
		this.foliadorUnicoSN = foliadorUnicoSN;
	}

	/**
	 * Gets the id area heredada.
	 *
	 * @return the id area heredada
	 */
	public String getIdAreaHeredada() {
		return idAreaHeredada;
	}

	/**
	 * Sets the id area heredada.
	 *
	 * @param idAreaHeredada the new id area heredada
	 */
	public void setIdAreaHeredada(String idAreaHeredada) {
		this.idAreaHeredada = idAreaHeredada;
	}

	/**
	 * Gets the dias pre not asunto.
	 *
	 * @return the dias pre not asunto
	 */
	public String getDiasPreNotAsunto() {
		return diasPreNotAsunto;
	}

	/**
	 * Sets the dias pre not asunto.
	 *
	 * @param diasPreNotAsunto the new dias pre not asunto
	 */
	public void setDiasPreNotAsunto(String diasPreNotAsunto) {
		this.diasPreNotAsunto = diasPreNotAsunto;
	}

	/**
	 * Gets the dias pre not tramite.
	 *
	 * @return the dias pre not tramite
	 */
	public String getDiasPreNotTramite() {
		return diasPreNotTramite;
	}

	/**
	 * Sets the dias pre not tramite.
	 *
	 * @param diasPreNotTramite the new dias pre not tramite
	 */
	public void setDiasPreNotTramite(String diasPreNotTramite) {
		this.diasPreNotTramite = diasPreNotTramite;
	}

	/**
	 * Gets the foliops.
	 *
	 * @return the foliops
	 */
	public FolioPS getFoliops() {
		return foliops;
	}

	/**
	 * Sets the foliops.
	 *
	 * @param foliops the new foliops
	 */
	public void setFoliops(FolioPS foliops) {
		this.foliops = foliops;
	}

	/**
	 * Gets the folio archivistica.
	 *
	 * @return the folio archivistica
	 */
	public FolioArchivistica getFolioArchivistica() {
		return folioArchivistica;
	}

	/**
	 * Sets the folio archivistica.
	 *
	 * @param folioArchivistica the new folio archivistica
	 */
	public void setFolioArchivistica(FolioArchivistica folioArchivistica) {
		this.folioArchivistica = folioArchivistica;
	}

	/**
	 * Gets the folio asunto.
	 *
	 * @return the folio asunto
	 */
	public Integer getFolioAsunto() {
		return FolioAsunto;
	}

	/**
	 * Sets the folio asunto.
	 *
	 * @param folioAsunto the new folio asunto
	 */
	public void setFolioAsunto(Integer folioAsunto) {
		FolioAsunto = folioAsunto;
	}

	/**
	 * Gets the folio respuesta.
	 *
	 * @return the folio respuesta
	 */
	public Integer getFolioRespuesta() {
		return FolioRespuesta;
	}

	/**
	 * Sets the folio respuesta.
	 *
	 * @param folioRespuesta the new folio respuesta
	 */
	public void setFolioRespuesta(Integer folioRespuesta) {
		FolioRespuesta = folioRespuesta;
	}

	/**
	 * @return the folioClave
	 */
	public Integer getFolioClave() {
		return FolioClave;
	}

	/**
	 * @param folioClave the folioClave to set
	 */
	public void setFolioClave(Integer folioClave) {
		FolioClave = folioClave;
	}

	/**
	 * 
	 * @return
	 */
	public FolioPSClave getFoliopsclave() {
		return foliopsclave;
	}

	/**
	 * 
	 * @param foliopsclave
	 */
	public void setFoliopsclave(FolioPSClave foliopsclave) {
		this.foliopsclave = foliopsclave;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfiguracionArea{" + "idArea=" + idArea + ", folioDisponible=" + folioDisponible + ", tipo='" + tipo
				+ '\'' + ", comparteFolioSN='" + comparteFolioSN + '\'' + ", foliadorUnicoSN='" + foliadorUnicoSN + '\''
				+ ", idAreaHeredada='" + idAreaHeredada + '\'' + ", diasPreNotAsunto='" + diasPreNotAsunto + '\''
				+ ", diasPreNotTramite='" + diasPreNotTramite + '\'' + ", foliops=" + foliops + ", foliopsclave="
				+ foliopsclave + ", folioArchivistica=" + folioArchivistica + ", FolioAsunto=" + FolioAsunto
				+ ", FolioRespuesta=" + FolioRespuesta + ", FolioClave=" + FolioClave + '}';
	}
}
