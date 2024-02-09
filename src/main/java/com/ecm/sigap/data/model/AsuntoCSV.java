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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * The Class AsuntoCSV.
 *
 * @version 1.0
 * 
 */
@Entity
@Table(name = "ASUNTOCSV")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoCSV implements Serializable{
	
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
	
	/** The fecha recepcion. */
	@Column(name = "fechaRecepcion")
	@Type(type = "java.util.Date")
	private Date fechaRecepcion;
	
	/** The fecha registro. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;
	
	/** The asunto descripcion. */
	@Column(name = "asuntoDescripcion")
	private String asuntoDescripcion;
	
	/** The firmante asunto. */
	@Column(name = "firmanteAsunto")
	private String firmanteAsunto;
	
	/** The firmante cargo. */
	@Column(name = "firmanteCargo")
	private String firmanteCargo;
	
	/** The area. */
	@Column(name = "area")
	private String area;
	
	/** The promotor. */
	@Column(name = "promotor")
	private String promotor;
	
	/** The id tipo registro. */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;
	
	/** The remitente. */
	@Column(name = "remitente")
	private String remitente;
	
	/** The en tiempo. */
	@Column(name = "EtFt")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;
	
	/** The tipo asunto. */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;
	
	/** The Firmante area destino. */
	@Column(name = "firmantedestinatario")
	private String firmanteDestinatario;
	
	/** The id asunto origen. */
	@Column(name = "idAsuntoOrigen")
	private Integer idAsuntoOrigen;
	
	/** The folio intermedio. */
	@Column(name = "folioIntermedio")
	private String folioIntermedio;
	
	/** The nombres turnador padre. */
	@Column(name = "nombresTurnadorPadre")
	private String nombresTurnadorPadre;

	/** The paterno turnador padre. */
	@Column(name = "paternoTurnadorPadre")
	private String paternoTurnadorPadre;

	/** The materno turnador padre. */
	@Column(name = "maternoTurnadorPadre")
	private String maternoTurnadorPadre;
	
	/** The nombre turnador. */
	@Column(name = "NOMBRESTURNADOR")
	private String nombreTurnador;

	/** The apellido paterno turnador. */
	@Column(name = "PATERNOTURNADOR")
	private String apellidoPaternoTurnador;

	/** The apellido materno turnador. */
	@Column(name = "MATERNOTURNADOR")
	private String apellidoMaternoTurnador;
	
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
	
	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;
	
	/** The id area destino. */
	@Column(name = "idAreaDestino")
	private Integer idAreaDestino;
	
	/** The fecha compromiso. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;
	
	/** The documentos count. */
	@Column(name = "documentosPublicados")
	private Integer documentosPublicados;

	/** las respuestas enviadas por el tramite. */
	@Column(name = "respuestasEnviadas")
	private Integer respuestasEnviadas;
	
	/** The tipos. */
	@Column(name = "idTipo")
	private Integer idTipo;
	
	/** The comentario. */
	@Column(name = "comentario")
	private String comentario;
	
	/** The anotaciones. */
	@Column(name = "ANOTACIONES")
	private String anotacion;
	
	/** The palabra clave. */
	@Column(name = "palabraClave")
	private String palabraClave;
	
	/** The id remitente. */
	@Column(name = "idRemitente")
	private Integer idRemitente;
	
	/** The id dirigido A. */
	@Column(name = "idDirigidoA")
	private String idDirigidoA;
	
	/** The id tipo documento. */
	@Column(name = "idTipoDocumento")
	private Integer idTipoDocumento;
	
	/** The id expediente. */
	@Column(name = "idExpediente")
	private String idExpediente;
	
	/** The id tema. */
	@Column(name = "idTema")
	private Integer idTema;

	/** The id sub tema. */
	@Column(name = "idSubTema")
	private Integer idSubTema;

	/** The id evento. */
	@Column(name = "idEvento")
	private Integer idEvento;
	
	/** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;
	
	/** The fecha evento. */
	@Column(name = "eventoFechaHora")
	@Type(type = "java.util.Date")
	private Date fechaEvento;
	
	/** The id firmante. */
	@Column(name = "idFirmante")
	private String idFirmante;
	
	/** The clave. */
	@Column(name = "clave")
	private String clave;
	
	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;
	
	/** */
	@Formula(value = "{SIGAP_SCHEMA}.OBTIENE_ANTECEDENTES(idAsunto)")
	private String antecedentes;

	public Integer getIdAsunto() {
		return idAsunto;
	}

	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	public String getFolioArea() {
		return folioArea;
	}

	public void setFolioArea(String folioArea) {
		this.folioArea = folioArea;
	}

	public String getNumDocto() {
		return numDocto;
	}

	public void setNumDocto(String numDocto) {
		this.numDocto = numDocto;
	}

	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
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

	public String getAsuntoDescripcion() {
		return asuntoDescripcion;
	}

	public void setAsuntoDescripcion(String asuntoDescripcion) {
		this.asuntoDescripcion = asuntoDescripcion;
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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPromotor() {
		return promotor;
	}

	public void setPromotor(String promotor) {
		this.promotor = promotor;
	}

	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public String getRemitente() {
		return remitente;
	}

	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	public EnTiempo getEnTiempo() {
		return enTiempo;
	}

	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
	}

	public TipoAsunto getTipoAsunto() {
		return tipoAsunto;
	}

	public void setTipoAsunto(TipoAsunto tipoAsunto) {
		this.tipoAsunto = tipoAsunto;
	}

	public String getFirmanteDestinatario() {
		return firmanteDestinatario;
	}

	public void setFirmanteDestinatario(String firmanteDestinatario) {
		this.firmanteDestinatario = firmanteDestinatario;
	}

	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
	}

	public String getFolioIntermedio() {
		return folioIntermedio;
	}

	public void setFolioIntermedio(String folioIntermedio) {
		this.folioIntermedio = folioIntermedio;
	}

	public String getNombresTurnadorPadre() {
		return nombresTurnadorPadre;
	}

	public void setNombresTurnadorPadre(String nombresTurnadorPadre) {
		this.nombresTurnadorPadre = nombresTurnadorPadre;
	}

	public String getPaternoTurnadorPadre() {
		return paternoTurnadorPadre;
	}

	public void setPaternoTurnadorPadre(String paternoTurnadorPadre) {
		this.paternoTurnadorPadre = paternoTurnadorPadre;
	}

	public String getMaternoTurnadorPadre() {
		return maternoTurnadorPadre;
	}

	public void setMaternoTurnadorPadre(String maternoTurnadorPadre) {
		this.maternoTurnadorPadre = maternoTurnadorPadre;
	}

	public String getNombreTurnador() {
		return nombreTurnador;
	}

	public void setNombreTurnador(String nombreTurnador) {
		this.nombreTurnador = nombreTurnador;
	}

	public String getApellidoPaternoTurnador() {
		return apellidoPaternoTurnador;
	}

	public void setApellidoPaternoTurnador(String apellidoPaternoTurnador) {
		this.apellidoPaternoTurnador = apellidoPaternoTurnador;
	}

	public String getApellidoMaternoTurnador() {
		return apellidoMaternoTurnador;
	}

	public void setApellidoMaternoTurnador(String apellidoMaternoTurnador) {
		this.apellidoMaternoTurnador = apellidoMaternoTurnador;
	}

	public String getStatusAsunto() {
		return statusAsunto;
	}

	public void setStatusAsunto(String statusAsunto) {
		this.statusAsunto = statusAsunto;
	}

	public Integer getIdStatusAsunto() {
		return idStatusAsunto;
	}

	public void setIdStatusAsunto(Integer idStatusAsunto) {
		this.idStatusAsunto = idStatusAsunto;
	}

	public String getStatusTurno() {
		return statusTurno;
	}

	public void setStatusTurno(String statusTurno) {
		this.statusTurno = statusTurno;
	}

	public Integer getIdStatusTurno() {
		return idStatusTurno;
	}

	public void setIdStatusTurno(Integer idStatusTurno) {
		this.idStatusTurno = idStatusTurno;
	}

	public Integer getIdArea() {
		return idArea;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public Integer getIdAreaDestino() {
		return idAreaDestino;
	}

	public void setIdAreaDestino(Integer idAreaDestino) {
		this.idAreaDestino = idAreaDestino;
	}

	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	public Integer getDocumentosPublicados() {
		return documentosPublicados;
	}

	public void setDocumentosPublicados(Integer documentosPublicados) {
		this.documentosPublicados = documentosPublicados;
	}

	public Integer getRespuestasEnviadas() {
		return respuestasEnviadas;
	}

	public void setRespuestasEnviadas(Integer respuestasEnviadas) {
		this.respuestasEnviadas = respuestasEnviadas;
	}

	public Integer getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
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

	public String getPalabraClave() {
		return palabraClave;
	}

	public void setPalabraClave(String palabraClave) {
		this.palabraClave = palabraClave;
	}

	public Integer getIdRemitente() {
		return idRemitente;
	}

	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	public String getIdDirigidoA() {
		return idDirigidoA;
	}

	public void setIdDirigidoA(String idDirigidoA) {
		this.idDirigidoA = idDirigidoA;
	}

	public Integer getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Integer idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getIdExpediente() {
		return idExpediente;
	}

	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
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

	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	public Date getFechaEvento() {
		return fechaEvento;
	}

	public void setFechaEvento(Date fechaEvento) {
		this.fechaEvento = fechaEvento;
	}

	public String getAntecedentes() {
		return antecedentes;
	}

	public void setAntecedentes(String antecedentes) {
		this.antecedentes = antecedentes;
	}

	public String getIdFirmante() {
		return idFirmante;
	}

	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Boolean getConfidencial() {
		return confidencial;
	}

	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
	}
	
}
