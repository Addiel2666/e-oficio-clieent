package com.ecm.sigap.data.model;

import java.io.Serializable;
//import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;

@Entity
@Table(name = "ASUNTOCONSULTARESPECIAL")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoConsultaEspecial implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 2240497097482299822L;
	
	/** The Constant SIMPLE_DATE_FORMAT. */
//	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	/** The Constant SIMPLE_DATE_FORMAT_HORA. */
//	private static final SimpleDateFormat SIMPLE_DATE_FORMAT_HORA = new SimpleDateFormat("HH-mm-ss");
	

    
	/** Identificador del Asunto. */
    @Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

    /** The id asunto origen. */
	@Column(name = "idAsuntoOrigen")
	private Integer idAsuntoOrigen;
	
    /** The id asunto padre. */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;

    /** The tipo asunto. */
    @Column(name = "idTipoAsunto")
    private String tipoAsunto;

    /** The id status turno. */
	@Column(name = "idStatusTurno")
	private Integer idStatusTurno;

	/** The status turno. */
	@Column(name = "statusTurno")
	private String statusTurno;

	/** The id status asunto. */
	@Column(name = "idStatusAsunto")
	private Integer idStatusAsunto;

	/** The status asunto. */
	@Column(name = "statusAsunto")
	private String statusAsunto;

    /** The fecha elaboracion. */
	@Column(name = "fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracion;

    /** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

    /** The fecha recepcion. */
	@Column(name = "fechaRecepcion")
	@Type(type = "java.util.Date")
	private Date fechaRecepcion;

    /** The fecha registro. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;
	
	/** The fecha compromiso. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

    /** The folio area. */
	@Column(name = "folioArea")
	private String folioArea;

    /** The folio intermedio. */
	@Column(name = "folioIntermedio")
	private String folioIntermedio;

    /** las respuestas enviadas por el tramite. */
	@Column(name = "respuestasEnviadas")
	private Integer respuestasEnviadas;

    /** The documentos count. */
	@Column(name = "documentosPublicados")
	private Integer documentosPublicados;

    /** The num docto. */
	@Column(name = "numDocto")
	private String numDocto;

    /** The palabra clave. */
	@Column(name = "palabraClave")
	private String palabraClave;

    /** The asunto descripcion. */
	@Column(name = "asuntoDescripcion")
	private String asuntoDescripcion;

    /** The comentario. */
	@Column(name = "comentario")
	private String comentario;

    /** The anotaciones. */
	@Column(name = "ANOTACIONES")
	private String anotacion;

    /** The anotaciones. */
	@Column(name = "idTipo")
	private Integer idTipo;

	/** The anotaciones. */
	@Column(name = "tipo")
	private String tipo;

    /** The anotaciones. */
	@Column(name = "clave")
	private String clave;

    /** The id firmante. */
	@Column(name = "idFirmante")
	private String idFirmante;

    /** The id dirigido A. */
	@Column(name = "idDirigidoA")
	private String idDirigidoA;

    /** The id remitente. */
	@Column(name = "idRemitente")
	private Integer idRemitente;

	/** The remitente. */
	@Column(name = "remitente")
	private String remitente;

    /** The id tema. */
	@Column(name = "idTema")
	private Integer idTema;

    /** The id sub tema. */
	@Column(name = "idSubTema")
	private Integer idSubTema;

    /** The id evento. */
	@Column(name = "idEvento")
	private Integer idEvento;

    /** The fecha evento. */
	@Column(name = "eventoFechaHora")
	@Type(type = "java.util.Date")
	private Date fechaEvento;

    /** The id expediente. */
	@Column(name = "idExpediente")
	private String idExpediente;

    /** The id tipo documento. */
	@Column(name = "idTipoDocumento")
	private Integer idTipoDocumento;

    /** The id tipo registro. */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;

    /** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The area. */
	@Column(name = "area")
	private String area;

    /** The firmante asunto. */
	@Column(name = "firmanteAsunto")
	private String firmanteAsunto;

    /** The firmante cargo. */
	@Column(name = "firmanteCargo")
	private String firmanteCargo;

    /** The id promotor. */
	@Column(name = "idPromotor")
	private Integer idPromotor;

	/** The promotor. */
	@Column(name = "promotor")
	private String promotor;

    /** The id destinatario. */
	@Column(name = "idDestinatario")
	private String idDestinatario;

    /** The documentos count. */
	@Column(name = "documentosCount")
	private Integer documentosAdjuntos;

	/** The id area destino. */
	@Column(name = "idAreaDestino")
	private Integer idAreaDestino;

	/** The en tiempo. */
	@Column(name = "EtFt")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;
	
	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;
	
	/** Nombre de empresa. */
	@Column(name = "nombreempresa")
	private String remitenteEmpresa;
	
	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;
	
	/** The prioridad descripcion. */
	@Column(name = "PRIORIDADDESC")
	private String prioridadDescripcion;

	/** The fecha envio. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;
	
	
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

	public String getInstruccionDescripcion() {
		return instruccionDescripcion;
	}

	public void setInstruccionDescripcion(String instruccionDescripcion) {
		this.instruccionDescripcion = instruccionDescripcion;
	}

	public Integer getIdAsunto() {
		return idAsunto;
	}

	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
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

	public String getTipoAsunto() {
		return tipoAsunto;
	}

	public void setTipoAsunto(String tipoAsunto) {
		this.tipoAsunto = tipoAsunto;
	}

	public Integer getIdStatusTurno() {
		return idStatusTurno;
	}

	public void setIdStatusTurno(Integer idStatusTurno) {
		this.idStatusTurno = idStatusTurno;
	}

	public String getStatusTurno() {
		return statusTurno;
	}

	public void setStatusTurno(String statusTurno) {
		this.statusTurno = statusTurno;
	}

	public Integer getIdStatusAsunto() {
		return idStatusAsunto;
	}

	public void setIdStatusAsunto(Integer idStatusAsunto) {
		this.idStatusAsunto = idStatusAsunto;
	}

	public String getStatusAsunto() {
		return statusAsunto;
	}

	public void setStatusAsunto(String statusAsunto) {
		this.statusAsunto = statusAsunto;
	}

	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	public String getFolioArea() {
		return folioArea;
	}

	public void setFolioArea(String folioArea) {
		this.folioArea = folioArea;
	}

	public String getFolioIntermedio() {
		return folioIntermedio;
	}

	public void setFolioIntermedio(String folioIntermedio) {
		this.folioIntermedio = folioIntermedio;
	}

	public Integer getRespuestasEnviadas() {
		return respuestasEnviadas;
	}

	public void setRespuestasEnviadas(Integer respuestasEnviadas) {
		this.respuestasEnviadas = respuestasEnviadas;
	}

	public Integer getDocumentosPublicados() {
		return documentosPublicados;
	}

	public void setDocumentosPublicados(Integer documentosPublicados) {
		this.documentosPublicados = documentosPublicados;
	}

	public String getNumDocto() {
		return numDocto;
	}

	public void setNumDocto(String numDocto) {
		this.numDocto = numDocto;
	}

	public String getPalabraClave() {
		return palabraClave;
	}

	public void setPalabraClave(String palabraClave) {
		this.palabraClave = palabraClave;
	}

	public String getAsuntoDescripcion() {
		return asuntoDescripcion;
	}

	public void setAsuntoDescripcion(String asuntoDescripcion) {
		this.asuntoDescripcion = asuntoDescripcion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getAnotacion() {
		return anotacion;
	}

	public void setAnotacion(String anotacion) {
		this.anotacion = anotacion;
	}

	public Integer getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getIdFirmante() {
		return idFirmante;
	}

	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	public String getIdDirigidoA() {
		return idDirigidoA;
	}

	public void setIdDirigidoA(String idDirigidoA) {
		this.idDirigidoA = idDirigidoA;
	}

	public Integer getIdRemitente() {
		return idRemitente;
	}

	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	public String getRemitente() {
		return remitente;
	}

	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	public Integer getIdTema() {
		return idTema;
	}

	public void setIdTema(Integer idTema) {
		this.idTema = idTema;
	}

	public Integer getIdSubTema() {
		return idSubTema;
	}

	public void setIdSubTema(Integer idSubTema) {
		this.idSubTema = idSubTema;
	}

	public Integer getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(Integer idEvento) {
		this.idEvento = idEvento;
	}

	public Date getFechaEvento() {
		return fechaEvento;
	}

	public void setFechaEvento(Date fechaEvento) {
		this.fechaEvento = fechaEvento;
	}

	public String getIdExpediente() {
		return idExpediente;
	}

	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}

	public Integer getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Integer idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public Integer getIdArea() {
		return idArea;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getFirmanteAsunto() {
		return firmanteAsunto;
	}

	public void setFirmanteAsunto(String firmanteAsunto) {
		this.firmanteAsunto = firmanteAsunto;
	}

	public String getFirmanteCargo() {
		return firmanteCargo;
	}

	public void setFirmanteCargo(String firmanteCargo) {
		this.firmanteCargo = firmanteCargo;
	}

	public Integer getIdPromotor() {
		return idPromotor;
	}

	public void setIdPromotor(Integer idPromotor) {
		this.idPromotor = idPromotor;
	}

	public String getPromotor() {
		return promotor;
	}

	public void setPromotor(String promotor) {
		this.promotor = promotor;
	}

	public String getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public Integer getDocumentosAdjuntos() {
		return documentosAdjuntos;
	}

	public void setDocumentosAdjuntos(Integer documentosAdjuntos) {
		this.documentosAdjuntos = documentosAdjuntos;
	}

	public Integer getIdAreaDestino() {
		return idAreaDestino;
	}

	public void setIdAreaDestino(Integer idAreaDestino) {
		this.idAreaDestino = idAreaDestino;
	}

	public EnTiempo getEnTiempo() {
		return enTiempo;
	}

	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
	}

	public Boolean getConfidencial() {
		return confidencial;
	}

	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
	}

	public String getRemitenteEmpresa() {
		return remitenteEmpresa;
	}

	public void setRemitenteEmpresa(String remitenteEmpresa) {
		this.remitenteEmpresa = remitenteEmpresa;
	}

	@Override
	public String toString() {
		return "AsuntoConsultaEspecial [idAsunto=" + idAsunto + ", idAsuntoOrigen=" + idAsuntoOrigen
				+ ", idAsuntoPadre=" + idAsuntoPadre + ", tipoAsunto=" + tipoAsunto + ", idStatusTurno=" + idStatusTurno
				+ ", statusTurno=" + statusTurno + ", idStatusAsunto=" + idStatusAsunto + ", statusAsunto="
				+ statusAsunto + ", fechaElaboracion=" + fechaElaboracion + ", fechaCompromiso=" + fechaCompromiso
				+ ", fechaRecepcion=" + fechaRecepcion + ", fechaRegistro=" + fechaRegistro + ", fechaAcuse="
				+ fechaAcuse + ", folioArea=" + folioArea + ", folioIntermedio=" + folioIntermedio
				+ ", respuestasEnviadas=" + respuestasEnviadas + ", documentosPublicados=" + documentosPublicados
				+ ", numDocto=" + numDocto + ", palabraClave=" + palabraClave + ", asuntoDescripcion="
				+ asuntoDescripcion + ", comentario=" + comentario + ", anotacion=" + anotacion + ", idTipo=" + idTipo
				+ ", tipo=" + tipo + ", clave=" + clave + ", idFirmante=" + idFirmante + ", idDirigidoA=" + idDirigidoA
				+ ", idRemitente=" + idRemitente + ", remitente=" + remitente + ", idTema=" + idTema + ", idSubTema="
				+ idSubTema + ", idEvento=" + idEvento + ", fechaEvento=" + fechaEvento + ", idExpediente="
				+ idExpediente + ", idTipoDocumento=" + idTipoDocumento + ", idTipoRegistro=" + idTipoRegistro
				+ ", idArea=" + idArea + ", area=" + area + ", firmanteAsunto=" + firmanteAsunto + ", firmanteCargo="
				+ firmanteCargo + ", idPromotor=" + idPromotor + ", promotor=" + promotor + ", idDestinatario="
				+ idDestinatario + ", documentosAdjuntos=" + documentosAdjuntos + ", idAreaDestino=" + idAreaDestino
				+ ", enTiempo=" + enTiempo + ", confidencial=" + confidencial + ", remitenteEmpresa=" + remitenteEmpresa
				+ "]";
	}
	
	
}
