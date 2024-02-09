/**
 * Copyright (c) 2023 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * The Class AsuntoConsultaRecibido.
 *
 * @author ECM Solutions
 * @version 1.0
 * 
 */
@Entity
@Table(name = "ASUNTOSRECIBIDOS")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoRecibidoConsulta implements Serializable {

	/** */
	private static final long serialVersionUID = 7982570285731633547L;

	/** Identificador del Asunto. */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** Identificador del Asunto Padre. */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;

	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;

	/** Indicador si el Tipo de Instruccion requiere o no respuesta. */
	@Column(name = "INSTRUCCIONRR", length = 1)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuesta;

	/** The prioridad descripcion. */
	@Column(name = "PRIORIDADDESC")
	private String prioridadDescripcion;

	/** The folio area. */
	@Column(name = "folioArea")
	private String folioArea;

	/** The comentario. */
	@Column(name = "comentario")
	private String comentario;

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

	/** The fecha envio. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** The fecha compromiso. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

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

	/** The id status asunto. */
	@Column(name = "idStatusAsunto")
	private Integer idStatusAsunto;

	/** The status turno. */
	@Column(name = "statusTurno")
	private String statusTurno;

	/** The id status turno. */
	@Column(name = "idStatusTurno")
	private Integer idStatusTurno;

	/** The en tiempo. */
	@Column(name = "EtFt")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;

	/** The id area destino. */
	@Column(name = "idAreaDestino")
	private Integer idAreaDestino;

	/** The area destino. */
	@Column(name = "areaDestino")
	private String areaDestino;

	/** The id titular area destino. */
	@Column(name = "idTitularAreaDestino")
	private String idTitularAreaDestino;

	/** The titular area destino. */
	@Column(name = "titularAreaDestino")
	private String titularAreaDestino;

	/** The cargo titular area destino. */
	@Column(name = "cargoTitularAreaDestino")
	private String cargoTitularAreaDestino;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The area. */
	@Column(name = "area")
	private String area;

	/** The id promotor. */
	@Column(name = "idPromotor")
	private Integer idPromotor;

	/** The promotor. */
	@Column(name = "promotor")
	private String promotor;

	/** The promotor abreviatura padre. */
	@Column(name = "promotorAbreviatura")
	private String promotorAbreviatura;

	/** The id remitente. */
	@Column(name = "idRemitente")
	private Integer idRemitente;

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

	/** The documentos count. */
	@Column(name = "documentosCount")
	private Integer documentosAdjuntos;

	/** The especialsn. */
	@Column(name = "especialsn")
	private String especialsn;

	/** The nombre turnador. */
	@Column(name = "NOMBRESTURNADOR")
	private String nombreTurnador;

	/** The apellido paterno turnador. */
	@Column(name = "PATERNOTURNADOR")
	private String apellidoPaternoTurnador;

	/** The apellido materno turnador. */
	@Column(name = "MATERNOTURNADOR")
	private String apellidoMaternoTurnador;

	/** The anotaciones. */
	@Column(name = "idTipo")
	private Integer idTipo;

	/** The anotaciones. */
	@Column(name = "tipo")
	private String tipo;

	/** The anotaciones. */
	@Column(name = "clave")
	private String clave;

	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

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
	 * @return the idAsuntoPadre
	 */
	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	/**
	 * @param idAsuntoPadre the idAsuntoPadre to set
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	/**
	 * @return the instruccionDescripcion
	 */
	public String getInstruccionDescripcion() {
		return instruccionDescripcion;
	}

	/**
	 * @param instruccionDescripcion the instruccionDescripcion to set
	 */
	public void setInstruccionDescripcion(String instruccionDescripcion) {
		this.instruccionDescripcion = instruccionDescripcion;
	}

	/**
	 * @return the requiereRespuesta
	 */
	public Boolean getRequiereRespuesta() {
		return requiereRespuesta;
	}

	/**
	 * @param requiereRespuesta the requiereRespuesta to set
	 */
	public void setRequiereRespuesta(Boolean requiereRespuesta) {
		this.requiereRespuesta = requiereRespuesta;
	}

	/**
	 * @return the prioridadDescripcion
	 */
	public String getPrioridadDescripcion() {
		return prioridadDescripcion;
	}

	/**
	 * @param prioridadDescripcion the prioridadDescripcion to set
	 */
	public void setPrioridadDescripcion(String prioridadDescripcion) {
		this.prioridadDescripcion = prioridadDescripcion;
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
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * @param comentario the comentario to set
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
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
	 * @return the fechaEnvio
	 */
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * @param fechaEnvio the fechaEnvio to set
	 */
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * @return the fechaAcuse
	 */
	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	/**
	 * @param fechaAcuse the fechaAcuse to set
	 */
	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
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

	/**
	 * @return the statusTurno
	 */
	public String getStatusTurno() {
		return statusTurno;
	}

	/**
	 * @param statusTurno the statusTurno to set
	 */
	public void setStatusTurno(String statusTurno) {
		this.statusTurno = statusTurno;
	}

	/**
	 * @return the idStatusTurno
	 */
	public Integer getIdStatusTurno() {
		return idStatusTurno;
	}

	/**
	 * @param idStatusTurno the idStatusTurno to set
	 */
	public void setIdStatusTurno(Integer idStatusTurno) {
		this.idStatusTurno = idStatusTurno;
	}

	/**
	 * @return the enTiempo
	 */
	public EnTiempo getEnTiempo() {
		return enTiempo;
	}

	/**
	 * @param enTiempo the enTiempo to set
	 */
	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
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
	 * @return the areaDestino
	 */
	public String getAreaDestino() {
		return areaDestino;
	}

	/**
	 * @param areaDestino the areaDestino to set
	 */
	public void setAreaDestino(String areaDestino) {
		this.areaDestino = areaDestino;
	}

	/**
	 * @return the idTitularAreaDestino
	 */
	public String getIdTitularAreaDestino() {
		return idTitularAreaDestino;
	}

	/**
	 * @param idTitularAreaDestino the idTitularAreaDestino to set
	 */
	public void setIdTitularAreaDestino(String idTitularAreaDestino) {
		this.idTitularAreaDestino = idTitularAreaDestino;
	}

	/**
	 * @return the titularAreaDestino
	 */
	public String getTitularAreaDestino() {
		return titularAreaDestino;
	}

	/**
	 * @param titularAreaDestino the titularAreaDestino to set
	 */
	public void setTitularAreaDestino(String titularAreaDestino) {
		this.titularAreaDestino = titularAreaDestino;
	}

	/**
	 * @return the cargoTitularAreaDestino
	 */
	public String getCargoTitularAreaDestino() {
		return cargoTitularAreaDestino;
	}

	/**
	 * @param cargoTitularAreaDestino the cargoTitularAreaDestino to set
	 */
	public void setCargoTitularAreaDestino(String cargoTitularAreaDestino) {
		this.cargoTitularAreaDestino = cargoTitularAreaDestino;
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
	 * @return the idPromotor
	 */
	public Integer getIdPromotor() {
		return idPromotor;
	}

	/**
	 * @param idPromotor the idPromotor to set
	 */
	public void setIdPromotor(Integer idPromotor) {
		this.idPromotor = idPromotor;
	}

	/**
	 * @return the promotor
	 */
	public String getPromotor() {
		return promotor;
	}

	/**
	 * @param promotor the promotor to set
	 */
	public void setPromotor(String promotor) {
		this.promotor = promotor;
	}

	/**
	 * @return the promotorAbreviatura
	 */
	public String getPromotorAbreviatura() {
		return promotorAbreviatura;
	}

	/**
	 * @param promotorAbreviatura the promotorAbreviatura to set
	 */
	public void setPromotorAbreviatura(String promotorAbreviatura) {
		this.promotorAbreviatura = promotorAbreviatura;
	}

	/**
	 * @return the idRemitente
	 */
	public Integer getIdRemitente() {
		return idRemitente;
	}

	/**
	 * @param idRemitente the idRemitente to set
	 */
	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
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
	 * @return the documentosAdjuntos
	 */
	public Integer getDocumentosAdjuntos() {
		return documentosAdjuntos;
	}

	/**
	 * @param documentosAdjuntos the documentosAdjuntos to set
	 */
	public void setDocumentosAdjuntos(Integer documentosAdjuntos) {
		this.documentosAdjuntos = documentosAdjuntos;
	}

	/**
	 * @return the especialsn
	 */
	public String getEspecialsn() {
		return especialsn;
	}

	/**
	 * @param especialsn the especialsn to set
	 */
	public void setEspecialsn(String especialsn) {
		this.especialsn = especialsn;
	}

	/**
	 * @return the nombreTurnador
	 */
	public String getNombreTurnador() {
		return nombreTurnador;
	}

	/**
	 * @param nombreTurnador the nombreTurnador to set
	 */
	public void setNombreTurnador(String nombreTurnador) {
		this.nombreTurnador = nombreTurnador;
	}

	/**
	 * @return the apellidoPaternoTurnador
	 */
	public String getApellidoPaternoTurnador() {
		return apellidoPaternoTurnador;
	}

	/**
	 * @param apellidoPaternoTurnador the apellidoPaternoTurnador to set
	 */
	public void setApellidoPaternoTurnador(String apellidoPaternoTurnador) {
		this.apellidoPaternoTurnador = apellidoPaternoTurnador;
	}

	/**
	 * @return the apellidoMaternoTurnador
	 */
	public String getApellidoMaternoTurnador() {
		return apellidoMaternoTurnador;
	}

	/**
	 * @param apellidoMaternoTurnador the apellidoMaternoTurnador to set
	 */
	public void setApellidoMaternoTurnador(String apellidoMaternoTurnador) {
		this.apellidoMaternoTurnador = apellidoMaternoTurnador;
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
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * @param clave the clave to set
	 */
	public void setClave(String clave) {
		this.clave = clave;
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

	@Override
	public String toString() {
		return "AsuntoConsultaRecibido [idAsunto=" + idAsunto + ", idAsuntoPadre=" + idAsuntoPadre
				+ ", instruccionDescripcion=" + instruccionDescripcion + ", requiereRespuesta=" + requiereRespuesta
				+ ", prioridadDescripcion=" + prioridadDescripcion + ", folioArea=" + folioArea + ", comentario="
				+ comentario + ", numDocto=" + numDocto + ", fechaElaboracion=" + fechaElaboracion + ", fechaRegistro="
				+ fechaRegistro + ", fechaCompromiso=" + fechaCompromiso + ", fechaEnvio=" + fechaEnvio
				+ ", fechaAcuse=" + fechaAcuse + ", asuntoDescripcion=" + asuntoDescripcion + ", idTipoRegistro="
				+ idTipoRegistro + ", tipoAsunto=" + tipoAsunto + ", folioIntermedio=" + folioIntermedio
				+ ", statusAsunto=" + statusAsunto + ", idStatusAsunto=" + idStatusAsunto + ", statusTurno="
				+ statusTurno + ", idStatusTurno=" + idStatusTurno + ", enTiempo=" + enTiempo + ", idAreaDestino="
				+ idAreaDestino + ", areaDestino=" + areaDestino + ", idTitularAreaDestino=" + idTitularAreaDestino
				+ ", titularAreaDestino=" + titularAreaDestino + ", cargoTitularAreaDestino=" + cargoTitularAreaDestino
				+ ", idArea=" + idArea + ", area=" + area + ", idPromotor=" + idPromotor + ", promotor=" + promotor
				+ ", promotorAbreviatura=" + promotorAbreviatura + ", idRemitente=" + idRemitente + ", remitente="
				+ remitente + ", idFirmante=" + idFirmante + ", firmanteAsunto=" + firmanteAsunto + ", firmanteCargo="
				+ firmanteCargo + ", idAsuntoOrigen=" + idAsuntoOrigen + ", documentosAdjuntos=" + documentosAdjuntos
				+ ", especialsn=" + especialsn + ", nombreTurnador=" + nombreTurnador + ", apellidoPaternoTurnador="
				+ apellidoPaternoTurnador + ", apellidoMaternoTurnador=" + apellidoMaternoTurnador + ", idTipo="
				+ idTipo + ", tipo=" + tipo + ", clave=" + clave + ", confidencial=" + confidencial + "]";
	}

}
