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
 * The Class AsuntoRechazadoConsulta.
 *
 * @author ECM Solutions
 * @version 1.0
 * 
 */
@Entity
@Table(name = "TRAMITESRECHAZADOS")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoRechazadoConsulta implements Serializable {

	/** */
	private static final long serialVersionUID = -505244507104630918L;

	/** Identificador del Asunto. */
	@Id
	@Column(name = "IDASUNTO")
	private Integer idAsunto;

	/** Identificador del Asunto Padre. */
	@Column(name = "IDASUNTOPADRE")
	private Integer idAsuntoPadre;

	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;

	/** The comentario. */
	@Column(name = "COMENTARIO")
	private String comentario;

	/** The fecha compromiso. */
	@Column(name = "FECHACOMPROMISO")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

	/** The fecha envio. */
	@Column(name = "FECHAENVIO")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** The fecha acuse. */
	@Column(name = "FECHAACUSE")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/** The tipo asunto. */
	@Column(name = "IDTIPOASUNTO")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** The folio intermedio. */
	@Column(name = "FOLIOINTERMEDIO")
	private String folioIntermedio;

	/** The en tiempo. */
	@Column(name = "ETFT")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;

	/** The id area destino. */
	@Column(name = "IDAREADESTINO")
	private Integer idAreaDestino;

	/** The area destino. */
	@Column(name = "AREADESTINO")
	private String areaDestino;

	/** The id titular area destino. */
	@Column(name = "IDTITULARAREADESTINO")
	private String idTitularAreaDestino;

	/** The titular area destino. */
	@Column(name = "TITULARAREADESTINO")
	private String titularAreaDestino;

	/** The cargo titular area destino. */
	@Column(name = "CARGOTITULARAREADESTINO")
	private String cargoTitularAreaDestino;

	/** The id area. */
	@Column(name = "IDAREA")
	private Integer idArea;

	/** The area. */
	@Column(name = "AREA")
	private String area;

	/** The id asunto origen. */
	@Column(name = "IDASUNTOORIGEN")
	private Integer idAsuntoOrigen;

	/** The folio area asunto padre. */
	@Column(name = "FOLIOAREAASUNTOPADRE")
	private String folioAreaAsuntoPadre;

	/** The fecha registro padre. */
	@Column(name = "FECHAREGISTROPADRE")
	@Type(type = "java.util.Date")
	private Date fechaRegistroPadre;

	/** The fecha compromiso padre. */
	@Column(name = "FECHACOMPROMISOPADRE")
	@Type(type = "java.util.Date")
	private Date fechaCompromisoPadre;

	/** The id tipo registro padre. */
	@Column(name = "IDTIPOREGISTROPADRE")
	private String idTipoRegistroPadre;

	/** The tipo asunto padre. */
	@Column(name = "IDTIPOASUNTOPADRE")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsuntoPadre;

	/** The id status asunto padre. */
	@Column(name = "IDESTATUSASUNTOPADRE")
	private Integer idStatusAsuntoPadre;

	/** The status asunto padre. */
	@Column(name = "ESTATUSASUNTOPADRE")
	private String statusAsuntoPadre;

	/** The num docto padre. */
	@Column(name = "NUMDOCTOPADRE")
	private String numDoctoPadre;

	/** The fecha elaboracion padre. */
	@Column(name = "FECHAELABORACIONPADRE")
	@Type(type = "java.util.Date")
	private Date fechaElaboracionPadre;

	/** The asunto descripcion padre. */
	@Column(name = "ASUNTODESCRIPCIONPADRE")
	private String asuntoDescripcionPadre;

	/** The firmante cargo padre. */
	@Column(name = "FIRMANTECARGOPADRE")
	private String firmanteCargoPadre;

	/** The id firmante padre. */
	@Column(name = "IDFIRMANTEPADRE")
	private String idFirmantePadre;

	/** The firmante asunto padre. */
	@Column(name = "FIRMANTEASUNTOPADRE")
	private String firmanteAsuntoPadre;

	/** The id remitente padre. */
	@Column(name = "IDREMITENTEPADRE")
	private Integer idRemitentePadre;

	/** The remitente padre. */
	@Column(name = "REMITENTEPADRE")
	private String remitentePadre;

	/** The id promotor padre. */
	@Column(name = "IDPROMOTORPADRE")
	private Integer idPromotorPadre;

	/** The promotor padre. */
	@Column(name = "PROMOTORPADRE")
	private String promotorPadre;

	/** The promotor abreviatura padre. */
	@Column(name = "PROMOTORABREVIATURAPADRE")
	private String promotorAbreviaturaPadre;

	/** The id area padre. */
	@Column(name = "IDAREAPADRE")
	private Integer idAreaPadre;

	/** The area padre. */
	@Column(name = "AREAPADRE")
	private String areaPadre;

	/** The especialsn. */
	@Column(name = "ESPECIALSN")
	private String especialsn;

	/** The comentario rechazado. */
	@Column(name = "COMENTARIORECHAZO")
	private String comentarioRechazado;

	/** The nombre turnador. */
	@Column(name = "NOMBRESTURNADOR")
	private String nombreTurnador;

	/** The apellido paterno turnador. */
	@Column(name = "PATERNOTURNADOR")
	private String apellidoPaternoTurnador;

	/** The apellido materno turnador. */
	@Column(name = "MATERNOTURNADOR")
	private String apellidoMaternoTurnador;

	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "CONFIDENCIAL")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

	/** The idtipo. */
	@Column(name = "IDTIPO")
	private Integer idTipo;

	/** The tipo. */
	@Column(name = "TIPO")
	private String tipo;

	/** The clave. */
	@Column(name = "CLAVE")
	private String clave;

	/** The id status turno. */
	@Column(name = "IDSTATUSTURNO")
	private Integer idStatusTurno;

	/** The status turno. */
	@Column(name = "STATUSTURNO")
	private String statusTurno;

	/** The fecha registro. */
	@Column(name = "FECHAREGISTRO")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** The documentos count. */
	@Column(name = "DOCUMENTOSCOUNT")
	private Integer documentosAdjuntos;

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
	 * @return the folioAreaAsuntoPadre
	 */
	public String getFolioAreaAsuntoPadre() {
		return folioAreaAsuntoPadre;
	}

	/**
	 * @param folioAreaAsuntoPadre the folioAreaAsuntoPadre to set
	 */
	public void setFolioAreaAsuntoPadre(String folioAreaAsuntoPadre) {
		this.folioAreaAsuntoPadre = folioAreaAsuntoPadre;
	}

	/**
	 * @return the fechaRegistroPadre
	 */
	public Date getFechaRegistroPadre() {
		return fechaRegistroPadre;
	}

	/**
	 * @param fechaRegistroPadre the fechaRegistroPadre to set
	 */
	public void setFechaRegistroPadre(Date fechaRegistroPadre) {
		this.fechaRegistroPadre = fechaRegistroPadre;
	}

	/**
	 * @return the fechaCompromisoPadre
	 */
	public Date getFechaCompromisoPadre() {
		return fechaCompromisoPadre;
	}

	/**
	 * @param fechaCompromisoPadre the fechaCompromisoPadre to set
	 */
	public void setFechaCompromisoPadre(Date fechaCompromisoPadre) {
		this.fechaCompromisoPadre = fechaCompromisoPadre;
	}

	/**
	 * @return the idTipoRegistroPadre
	 */
	public String getIdTipoRegistroPadre() {
		return idTipoRegistroPadre;
	}

	/**
	 * @param idTipoRegistroPadre the idTipoRegistroPadre to set
	 */
	public void setIdTipoRegistroPadre(String idTipoRegistroPadre) {
		this.idTipoRegistroPadre = idTipoRegistroPadre;
	}

	/**
	 * @return the idStatusAsuntoPadre
	 */
	public Integer getIdStatusAsuntoPadre() {
		return idStatusAsuntoPadre;
	}

	/**
	 * @param idStatusAsuntoPadre the idStatusAsuntoPadre to set
	 */
	public void setIdStatusAsuntoPadre(Integer idStatusAsuntoPadre) {
		this.idStatusAsuntoPadre = idStatusAsuntoPadre;
	}

	/**
	 * @return the statusAsuntoPadre
	 */
	public String getStatusAsuntoPadre() {
		return statusAsuntoPadre;
	}

	/**
	 * @param statusAsuntoPadre the statusAsuntoPadre to set
	 */
	public void setStatusAsuntoPadre(String statusAsuntoPadre) {
		this.statusAsuntoPadre = statusAsuntoPadre;
	}

	/**
	 * @return the numDoctoPadre
	 */
	public String getNumDoctoPadre() {
		return numDoctoPadre;
	}

	/**
	 * @param numDoctoPadre the numDoctoPadre to set
	 */
	public void setNumDoctoPadre(String numDoctoPadre) {
		this.numDoctoPadre = numDoctoPadre;
	}

	/**
	 * @return the fechaElaboracionPadre
	 */
	public Date getFechaElaboracionPadre() {
		return fechaElaboracionPadre;
	}

	/**
	 * @param fechaElaboracionPadre the fechaElaboracionPadre to set
	 */
	public void setFechaElaboracionPadre(Date fechaElaboracionPadre) {
		this.fechaElaboracionPadre = fechaElaboracionPadre;
	}

	/**
	 * @return the asuntoDescripcionPadre
	 */
	public String getAsuntoDescripcionPadre() {
		return asuntoDescripcionPadre;
	}

	/**
	 * @param asuntoDescripcionPadre the asuntoDescripcionPadre to set
	 */
	public void setAsuntoDescripcionPadre(String asuntoDescripcionPadre) {
		this.asuntoDescripcionPadre = asuntoDescripcionPadre;
	}

	/**
	 * @return the firmanteCargoPadre
	 */
	public String getFirmanteCargoPadre() {
		return firmanteCargoPadre;
	}

	/**
	 * @param firmanteCargoPadre the firmanteCargoPadre to set
	 */
	public void setFirmanteCargoPadre(String firmanteCargoPadre) {
		this.firmanteCargoPadre = firmanteCargoPadre;
	}

	/**
	 * @return the idFirmantePadre
	 */
	public String getIdFirmantePadre() {
		return idFirmantePadre;
	}

	/**
	 * @param idFirmantePadre the idFirmantePadre to set
	 */
	public void setIdFirmantePadre(String idFirmantePadre) {
		this.idFirmantePadre = idFirmantePadre;
	}

	/**
	 * @return the firmanteAsuntoPadre
	 */
	public String getFirmanteAsuntoPadre() {
		return firmanteAsuntoPadre;
	}

	/**
	 * @param firmanteAsuntoPadre the firmanteAsuntoPadre to set
	 */
	public void setFirmanteAsuntoPadre(String firmanteAsuntoPadre) {
		this.firmanteAsuntoPadre = firmanteAsuntoPadre;
	}

	/**
	 * @return the idRemitentePadre
	 */
	public Integer getIdRemitentePadre() {
		return idRemitentePadre;
	}

	/**
	 * @param idRemitentePadre the idRemitentePadre to set
	 */
	public void setIdRemitentePadre(Integer idRemitentePadre) {
		this.idRemitentePadre = idRemitentePadre;
	}

	/**
	 * @return the remitentePadre
	 */
	public String getRemitentePadre() {
		return remitentePadre;
	}

	/**
	 * @param remitentePadre the remitentePadre to set
	 */
	public void setRemitentePadre(String remitentePadre) {
		this.remitentePadre = remitentePadre;
	}

	/**
	 * @return the idPromotorPadre
	 */
	public Integer getIdPromotorPadre() {
		return idPromotorPadre;
	}

	/**
	 * @param idPromotorPadre the idPromotorPadre to set
	 */
	public void setIdPromotorPadre(Integer idPromotorPadre) {
		this.idPromotorPadre = idPromotorPadre;
	}

	/**
	 * @return the promotorPadre
	 */
	public String getPromotorPadre() {
		return promotorPadre;
	}

	/**
	 * @param promotorPadre the promotorPadre to set
	 */
	public void setPromotorPadre(String promotorPadre) {
		this.promotorPadre = promotorPadre;
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
	 * @return the idAreaPadre
	 */
	public Integer getIdAreaPadre() {
		return idAreaPadre;
	}

	/**
	 * @param idAreaPadre the idAreaPadre to set
	 */
	public void setIdAreaPadre(Integer idAreaPadre) {
		this.idAreaPadre = idAreaPadre;
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
	 * @return the comentarioRechazado
	 */
	public String getComentarioRechazado() {
		return comentarioRechazado;
	}

	/**
	 * @param comentarioRechazado the comentarioRechazado to set
	 */
	public void setComentarioRechazado(String comentarioRechazado) {
		this.comentarioRechazado = comentarioRechazado;
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
	 * @return the tipoAsuntoPadre
	 */
	public TipoAsunto getTipoAsuntoPadre() {
		return tipoAsuntoPadre;
	}

	/**
	 * @param tipoAsuntoPadre the tipoAsuntoPadre to set
	 */
	public void setTipoAsuntoPadre(TipoAsunto tipoAsuntoPadre) {
		this.tipoAsuntoPadre = tipoAsuntoPadre;
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

	@Override
	public String toString() {
		return "AsuntoRechazadoConsulta [idAsunto=" + idAsunto + ", idAsuntoPadre=" + idAsuntoPadre
				+ ", instruccionDescripcion=" + instruccionDescripcion + ", comentario=" + comentario
				+ ", fechaCompromiso=" + fechaCompromiso + ", fechaEnvio=" + fechaEnvio + ", fechaAcuse=" + fechaAcuse
				+ ", tipoAsunto=" + tipoAsunto + ", folioIntermedio=" + folioIntermedio + ", enTiempo=" + enTiempo
				+ ", idAreaDestino=" + idAreaDestino + ", areaDestino=" + areaDestino + ", idTitularAreaDestino="
				+ idTitularAreaDestino + ", titularAreaDestino=" + titularAreaDestino + ", cargoTitularAreaDestino="
				+ cargoTitularAreaDestino + ", idArea=" + idArea + ", area=" + area + ", idAsuntoOrigen="
				+ idAsuntoOrigen + ", folioAreaAsuntoPadre=" + folioAreaAsuntoPadre + ", fechaRegistroPadre="
				+ fechaRegistroPadre + ", fechaCompromisoPadre=" + fechaCompromisoPadre + ", idTipoRegistroPadre="
				+ idTipoRegistroPadre + ", tipoAsuntoPadre=" + tipoAsuntoPadre + ", idStatusAsuntoPadre="
				+ idStatusAsuntoPadre + ", statusAsuntoPadre=" + statusAsuntoPadre + ", numDoctoPadre=" + numDoctoPadre
				+ ", fechaElaboracionPadre=" + fechaElaboracionPadre + ", asuntoDescripcionPadre="
				+ asuntoDescripcionPadre + ", firmanteCargoPadre=" + firmanteCargoPadre + ", idFirmantePadre="
				+ idFirmantePadre + ", firmanteAsuntoPadre=" + firmanteAsuntoPadre + ", idRemitentePadre="
				+ idRemitentePadre + ", remitentePadre=" + remitentePadre + ", idPromotorPadre=" + idPromotorPadre
				+ ", promotorPadre=" + promotorPadre + ", promotorAbreviaturaPadre=" + promotorAbreviaturaPadre
				+ ", idAreaPadre=" + idAreaPadre + ", areaPadre=" + areaPadre + ", especialsn=" + especialsn
				+ ", comentarioRechazado=" + comentarioRechazado + ", nombreTurnador=" + nombreTurnador
				+ ", apellidoPaternoTurnador=" + apellidoPaternoTurnador + ", apellidoMaternoTurnador="
				+ apellidoMaternoTurnador + ", confidencial=" + confidencial + ", idTipo=" + idTipo + ", tipo=" + tipo
				+ ", clave=" + clave + ", idStatusTurno=" + idStatusTurno + ", statusTurno=" + statusTurno
				+ ", fechaRegistro=" + fechaRegistro + ", documentosAdjuntos=" + documentosAdjuntos + "]";
	}

}
