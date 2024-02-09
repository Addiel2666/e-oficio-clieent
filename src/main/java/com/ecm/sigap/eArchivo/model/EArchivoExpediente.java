/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eArchivo.model;

/**
 * The Class EArchivo.
 */
public class EArchivoExpediente {

	/**
	 * The numero expediente.
	 */
	private Integer numeroExpediente;
	/** */
	private String folioExpediente;
	/** */
	private String idExpediente;	
	/** */
	private Boolean eliminado;

	/**
	 * The asunto expediente.
	 */
	private String asuntoExpediente;

	/**
	 * The fecha apertura.
	 */
	private Long fechaApertura;

	/**
	 * The titulo.
	 */
	private String titulo;

	/**
	 * The proceso.
	 */
	private Integer proceso;

	/**
	 * The serie.
	 */
	private String serie;

	/**
	 * The subserie.
	 */
	private String subserie;

	/**
	 * The serieSubserie.
	 */
	private Object serieSubserie;

	/**
	 * The codigo alterno.
	 */
	private String codigoAlterno;

	/**
	 * The usuario propietario.
	 */
	private String usuarioPropietario;

	/**
	 * The serie documental.
	 */
	private String serieDocumental;

	/**
	 * The clasificacion archivistica.
	 */
	private String clasificacionArchivistica;

	/**
	 * 
	 * @return
	 */
	public String getIdExpediente() {
		return idExpediente;
	}

	/**
	 * 
	 * @param idExpediente
	 */
	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}

	/**
	 * Gets the numero expediente.
	 *
	 * @return the numero expediente
	 */
	public Integer getNumeroExpediente() {
		return numeroExpediente;
	}

	/**
	 * Sets the numero expediente.
	 *
	 * @param numeroExpediente
	 *            the new numero expediente
	 */
	public void setNumeroExpediente(Integer numeroExpediente) {
		this.numeroExpediente = numeroExpediente;
	}

	/**
	 * Gets the asunto expediente.
	 *
	 * @return the asunto expediente
	 */
	public String getAsuntoExpediente() {
		return asuntoExpediente;
	}

	/**
	 * Sets the asunto expediente.
	 *
	 * @param asuntoExpediente
	 *            the new asunto expediente
	 */
	public void setAsuntoExpediente(String asuntoExpediente) {
		this.asuntoExpediente = asuntoExpediente;
	}

	/**
	 * Gets the fecha apertura.
	 *
	 * @return the fecha apertura
	 */
	public Long getFechaApertura() {
		return fechaApertura;
	}

	/**
	 * Sets the fecha apertura.
	 *
	 * @param fechaApertura
	 *            the new fecha apertura
	 */
	public void setFechaApertura(Long fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	/**
	 * 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * 
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getProceso() {
		return proceso;
	}

	/**
	 * 
	 * @param proceso
	 */
	public void setProceso(Integer proceso) {
		this.proceso = proceso;
	}

	/**
	 * 
	 * @return
	 */
	public String getSerie() {
		return serie;
	}

	/**
	 * 
	 * @param serie
	 */
	public void setSerie(String serie) {
		this.serie = serie;
	}

	/**
	 * 
	 * @return
	 */
	public String getSubserie() {
		return subserie;
	}

	/**
	 * 
	 * @param subserie
	 */
	public void setSubserie(String subserie) {
		this.subserie = subserie;
	}

	/**
	 * 
	 * @return
	 */
	public Object getSerieSubserie() {
		return serieSubserie;
	}

	/**
	 * 
	 * @param serieSubserie
	 */
	public void setSerieSubserie(Object serieSubserie) {
		this.serieSubserie = serieSubserie;
	}

	/**
	 * Gets the codigo alterno.
	 *
	 * @return the codigo alterno
	 */
	public String getCodigoAlterno() {
		return codigoAlterno;
	}

	/**
	 * Sets the codigo alterno.
	 *
	 * @param codigoAlterno
	 *            the new codigo alterno
	 */
	public void setCodigoAlterno(String codigoAlterno) {
		this.codigoAlterno = codigoAlterno;
	}

	/**
	 * Gets the usuario propietario.
	 *
	 * @return the usuario propietario
	 */
	public String getUsuarioPropietario() {
		return usuarioPropietario;
	}

	/**
	 * Sets the usuario propietario.
	 *
	 * @param usuarioPropietario
	 *            the new usuario propietario
	 */
	public void setUsuarioPropietario(String usuarioPropietario) {
		this.usuarioPropietario = usuarioPropietario;
	}

	/**
	 * Gets the serie documental.
	 *
	 * @return the serie documental
	 */
	public String getSerieDocumental() {
		return serieDocumental;
	}

	/**
	 * Sets the serie documental.
	 *
	 * @param serieDocumental
	 *            the new serie documental
	 */
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}

	/**
	 * Gets the clasificacion archivistica.
	 *
	 * @return the clasificacion archivistica
	 */
	public String getClasificacionArchivistica() {
		return clasificacionArchivistica;
	}

	/**
	 * Sets the clasificacion archivistica.
	 *
	 * @param clasificacionArchivistica
	 *            the new clasificacion archivistica
	 */
	public void setClasificacionArchivistica(String clasificacionArchivistica) {
		this.clasificacionArchivistica = clasificacionArchivistica;
	}

	/**
	 * 
	 * @return
	 */
	public String getFolioExpediente() {
		return folioExpediente;
	}

	/**
	 * 
	 * @param folioExpediente
	 */
	public void setFolioExpediente(String folioExpediente) {
		this.folioExpediente = folioExpediente;
	}	

	/**
	 * @return the eliminado
	 */
	public Boolean getEliminado() {
		return eliminado;
	}

	/**
	 * @param eliminado the eliminado to set
	 */
	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EArchivoExpediente{" + "numeroExpediente=" + numeroExpediente + ", folioExpediente='" + folioExpediente
				+ '\'' + ", asuntoExpediente='" + asuntoExpediente + '\'' + ", fechaApertura=" + fechaApertura
				+ ", titulo='" + titulo + '\'' + ", proceso='" + proceso + '\'' + ", serieSubserie=" + serieSubserie
				+ '}';
	}
}