/**
 * Copyright (c) 2023 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * 
 * @author ECM Solutions
 * @version 1.0
 */
@Entity
@Table(name = "ASUNTOSANTECEDENTES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoAntecedente implements Serializable {

	/** */
	private static final long serialVersionUID = -8320453611740367100L;

	/** Identificador del Asunto. */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** The folio area. */
	@Column(name = "folioArea")
	private String folioArea;

	/** The num docto. */
	@Column(name = "numDocto")
	private String numDocto;

	/** The fecha elaboracion. */
	@Column(name = "fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracion;

	/** The fecha registro. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

	/** The asunto descripcion. */
	@Column(name = "asuntoDescripcion")
	private String asuntoDescripcion;

	/** The id tipo registro. */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;

	/** The tipo asunto. */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** The folio intermedio. */
	@Column(name = "folioIntermedio")
	private String folioIntermedio;

	/** The status asunto. */
	@Column(name = "statusAsunto")
	private String statusAsunto;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The area. */
	@Column(name = "area")
	private String area;

	/** The remitente. */
	@Column(name = "remitente")
	private String remitente;

	/** The id firmante. */
	@Column(name = "idFirmante")
	private String idFirmante;

	/** The firmante asunto. */
	@Column(name = "firmanteAsunto")
	private String firmanteAsunto;

	/** The firmante cargo. */
	@Column(name = "firmanteCargo")
	private String firmanteCargo;

	/** The id asunto origen. */
	@Column(name = "idAsuntoOrigen")
	private Integer idAsuntoOrigen;

	/** The promotor abreviatura */
	@Column(name = "promotorAbreviaturaPadre")
	private String promotorAbreviaturaPadre;

	/** The anotaciones. */
	@Column(name = "idTipo")
	private Integer idTipo;

	/** The tipo. */
	@Column(name = "tipo")
	private String tipo;

	/** The id area destino. */
	@Column(name = "idAreaDestino")
	private Integer idAreaDestino;

	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

	/** The id status asunto. */
	@Column(name = "idStatusAsunto")
	private Integer idStatusAsunto;

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * @return the folioArea
	 */
	public String getFolioArea() {
		return folioArea;
	}

	/**
	 * @param folioArea the folioArea to set
	 */
	public void setFolioArea(String folioArea) {
		this.folioArea = folioArea;
	}

	/**
	 * @return the numDocto
	 */
	public String getNumDocto() {
		return numDocto;
	}

	/**
	 * @param numDocto the numDocto to set
	 */
	public void setNumDocto(String numDocto) {
		this.numDocto = numDocto;
	}

	/**
	 * @return the fechaElaboracion
	 */
	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	/**
	 * @param fechaElaboracion the fechaElaboracion to set
	 */
	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	/**
	 * @return the fechaRegistro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * @param fechaRegistro the fechaRegistro to set
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * @return the fechaCompromiso
	 */
	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	/**
	 * @param fechaCompromiso the fechaCompromiso to set
	 */
	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	/**
	 * @return the asuntoDescripcion
	 */
	public String getAsuntoDescripcion() {
		return asuntoDescripcion;
	}

	/**
	 * @param asuntoDescripcion the asuntoDescripcion to set
	 */
	public void setAsuntoDescripcion(String asuntoDescripcion) {
		this.asuntoDescripcion = asuntoDescripcion;
	}

	/**
	 * @return the idTipoRegistro
	 */
	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	/**
	 * @param idTipoRegistro the idTipoRegistro to set
	 */
	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	/**
	 * @return the tipoAsunto
	 */
	public TipoAsunto getTipoAsunto() {
		return tipoAsunto;
	}

	/**
	 * @param tipoAsunto the tipoAsunto to set
	 */
	public void setTipoAsunto(TipoAsunto tipoAsunto) {
		this.tipoAsunto = tipoAsunto;
	}

	/**
	 * @return the folioIntermedio
	 */
	public String getFolioIntermedio() {
		return folioIntermedio;
	}

	/**
	 * @param folioIntermedio the folioIntermedio to set
	 */
	public void setFolioIntermedio(String folioIntermedio) {
		this.folioIntermedio = folioIntermedio;
	}

	/**
	 * @return the statusAsunto
	 */
	public String getStatusAsunto() {
		return statusAsunto;
	}

	/**
	 * @param statusAsunto the statusAsunto to set
	 */
	public void setStatusAsunto(String statusAsunto) {
		this.statusAsunto = statusAsunto;
	}

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
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * @return the remitente
	 */
	public String getRemitente() {
		return remitente;
	}

	/**
	 * @param remitente the remitente to set
	 */
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	/**
	 * @return the idFirmante
	 */
	public String getIdFirmante() {
		return idFirmante;
	}

	/**
	 * @param idFirmante the idFirmante to set
	 */
	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	/**
	 * @return the firmanteAsunto
	 */
	public String getFirmanteAsunto() {
		return firmanteAsunto;
	}

	/**
	 * @param firmanteAsunto the firmanteAsunto to set
	 */
	public void setFirmanteAsunto(String firmanteAsunto) {
		this.firmanteAsunto = firmanteAsunto;
	}

	/**
	 * @return the firmanteCargo
	 */
	public String getFirmanteCargo() {
		return firmanteCargo;
	}

	/**
	 * @param firmanteCargo the firmanteCargo to set
	 */
	public void setFirmanteCargo(String firmanteCargo) {
		this.firmanteCargo = firmanteCargo;
	}

	/**
	 * @return the idAsuntoOrigen
	 */
	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	/**
	 * @param idAsuntoOrigen the idAsuntoOrigen to set
	 */
	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
	}

	/**
	 * @return the promotorAbreviaturaPadre
	 */
	public String getPromotorAbreviaturaPadre() {
		return promotorAbreviaturaPadre;
	}

	/**
	 * @param promotorAbreviaturaPadre the promotorAbreviaturaPadre to set
	 */
	public void setPromotorAbreviaturaPadre(String promotorAbreviaturaPadre) {
		this.promotorAbreviaturaPadre = promotorAbreviaturaPadre;
	}

	/**
	 * @return the idTipo
	 */
	public Integer getIdTipo() {
		return idTipo;
	}

	/**
	 * @param idTipo the idTipo to set
	 */
	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the idAreaDestino
	 */
	public Integer getIdAreaDestino() {
		return idAreaDestino;
	}

	/**
	 * @param idAreaDestino the idAreaDestino to set
	 */
	public void setIdAreaDestino(Integer idAreaDestino) {
		this.idAreaDestino = idAreaDestino;
	}

	/**
	 * @return the confidencial
	 */
	public Boolean getConfidencial() {
		return confidencial;
	}

	/**
	 * @param confidencial the confidencial to set
	 */
	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
	}

	/**
	 * @return the idStatusAsunto
	 */
	public Integer getIdStatusAsunto() {
		return idStatusAsunto;
	}

	/**
	 * @param idStatusAsunto the idStatusAsunto to set
	 */
	public void setIdStatusAsunto(Integer idStatusAsunto) {
		this.idStatusAsunto = idStatusAsunto;
	}

	@Override
	public String toString() {
		return "AsuntoRelacionado [idAsunto=" + idAsunto + ", folioArea=" + folioArea + ", numDocto=" + numDocto
				+ ", fechaElaboracion=" + fechaElaboracion + ", fechaRegistro=" + fechaRegistro + ", fechaCompromiso="
				+ fechaCompromiso + ", asuntoDescripcion=" + asuntoDescripcion + ", idTipoRegistro=" + idTipoRegistro
				+ ", tipoAsunto=" + tipoAsunto + ", folioIntermedio=" + folioIntermedio + ", statusAsunto="
				+ statusAsunto + ", idArea=" + idArea + ", area=" + area + ", remitente=" + remitente + ", idFirmante="
				+ idFirmante + ", firmanteAsunto=" + firmanteAsunto + ", firmanteCargo=" + firmanteCargo
				+ ", idAsuntoOrigen=" + idAsuntoOrigen + ", promotorAbreviaturaPadre=" + promotorAbreviaturaPadre
				+ ", idTipo=" + idTipo + ", tipo=" + tipo + ", idAreaDestino=" + idAreaDestino + ", confidencial="
				+ confidencial + ", idStatusAsunto=" + idStatusAsunto + "]";
	}
}
