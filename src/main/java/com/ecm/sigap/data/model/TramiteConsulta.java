package com.ecm.sigap.data.model;

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
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.SubTipoAsuntoToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * The Class TramiteConsulta.
 *
 * @version 1.0
 * 
 */
@Entity
@Table(name = "TRAMITECONSULTA")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class TramiteConsulta {
	
	/** Identificador del Asunto. */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;
	
	/** Identificador del Asunto Padre. */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;
	
	/** The id asunto origen. */
	@Column(name = "idAsuntoOrigen")
	private Integer idAsuntoOrigen;
	
	/** The id area destino. */
	@Column(name = "idAreaDestino")
	private Integer idAreaDestino;
	
	/** The tipo asunto. */
	@Column(name = "idSubTipoAsunto")
	@Convert(converter = SubTipoAsuntoToStringConverter.class)
	private SubTipoAsunto subTipoAsunto;
	
	/** The id destinatario. */
	@Column(name = "idDestinatario")
	private String idDestinatario;
	
	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;
	
	/** las respuestas enviadas por el tramite. */
	@Column(name = "respuestasEnviadas")
	private Integer respuestasEnviadas;
	
	/** The especialsn. */
	@Column(name = "especialsn")
	private String especialsn;
	
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
	
	/** The tipo asunto. */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;
	
	/** The fecha registro. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;
	
	/** The titular area destino. */
	@Column(name = "titularAreaDestino")
	private String titularAreaDestino;
	
	/** The cargo titular area destino. */
	@Column(name = "cargoTitularAreaDestino")
	private String cargoTitularAreaDestino;
	
	/** The area destino. */
	@Column(name = "areaDestino")
	private String areaDestino;
	
	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;
	
	/** The comentario. */
	@Column(name = "comentario")
	private String comentario;
	
	/** The fecha envio. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** The fecha compromiso. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;
	
	/** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;
	
	/** The prioridad descripcion. */
	@Column(name = "PRIORIDADDESC")
	private String prioridadDescripcion;
	
	/** The folio area asunto padre. */
	@Column(name = "folioAreaAsuntoPadre")
	private String folioAreaAsuntoPadre;
	
	/** The num docto padre. */
	@Column(name = "numDoctoPadre")
	private String numDoctoPadre;
	
	/** The fecha elaboracion padre. */
	@Column(name = "fechaElaboracionPadre")
	@Type(type = "java.util.Date")
	private Date fechaElaboracionPadre;
	
	/** The fecha registro padre. */
	@Column(name = "fechaRegistroPadre")
	@Type(type = "java.util.Date")
	private Date fechaRegistroPadre;
	
	/** The asunto descripcion padre. */
	@Column(name = "asuntoDescripcionPadre")
	private String asuntoDescripcionPadre;
	
	/** The firmante asunto padre. */
	@Column(name = "firmanteAsuntoPadre")
	private String firmanteAsuntoPadre;
	
	/** The firmante cargo padre. */
	@Column(name = "firmanteCargoPadre")
	private String firmanteCargoPadre;
	
	/** The remitente padre. */
	@Column(name = "remitentePadre")
	private String remitentePadre;
	
	/** The tipo asunto padre. */
	@Column(name = "idTipoAsuntoPadre")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsuntoPadre;
	
	/** The status asunto padre. */
	@Column(name = "ESTATUSASUNTOPADRE")
	private String statusAsuntoPadre;
	
	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;
	
	/** The idtipo. */
	@Column(name = "idTipo")
	private Integer idTipo;

	/** The tipo. */
	@Column(name = "tipo")
	private String tipo;
	
	/** The clave. */
	@Column(name = "clave")
	private String clave;
	
	/** Indicador si el Tipo de Instruccion requiere o no respuesta. */
	@Column(name = "INSTRUCCIONRR", length = 1)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuesta;
	
	/** The folio intermedio. */
	@Column(name = "folioIntermedio")
	private String folioIntermedio;
	
	/** The id tipo registro padre. */
	@Column(name = "idTipoRegistroPadre")
	private String idTipoRegistroPadre;
	
	/** The fecha compromiso padre. */
	@Column(name = "fechaCompromisoPadre")
	@Type(type = "java.util.Date")
	private Date fechaCompromisoPadre;
	
	/** The promotor abreviatura padre. */
	@Column(name = "promotorAbreviaturaPadre")
	private String promotorAbreviaturaPadre;
	
	/** The id tipo registro. */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;
	
	/** The area padre. */
	@Column(name = "areaPadre")
	private String areaPadre;
	
	/** Nombre del destinatario asunto. */
	@Column(name = "destinatarioAsuntoNombre")
	private String destinatarioAsuntoNombre;

	public Integer getIdAsunto() {
		return idAsunto;
	}

	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}
	
	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
	}

	public Integer getIdAreaDestino() {
		return idAreaDestino;
	}

	public void setIdAreaDestino(Integer idAreaDestino) {
		this.idAreaDestino = idAreaDestino;
	}

	public SubTipoAsunto getSubTipoAsunto() {
		return subTipoAsunto;
	}

	public void setSubTipoAsunto(SubTipoAsunto subTipoAsunto) {
		this.subTipoAsunto = subTipoAsunto;
	}

	public String getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public Integer getIdArea() {
		return idArea;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public Integer getRespuestasEnviadas() {
		return respuestasEnviadas;
	}

	public void setRespuestasEnviadas(Integer respuestasEnviadas) {
		this.respuestasEnviadas = respuestasEnviadas;
	}

	public String getEspecialsn() {
		return especialsn;
	}

	public void setEspecialsn(String especialsn) {
		this.especialsn = especialsn;
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

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
	public String getTitularAreaDestino() {
		return titularAreaDestino;
	}

	public void setTitularAreaDestino(String titularAreaDestino) {
		this.titularAreaDestino = titularAreaDestino;
	}

	public String getCargoTitularAreaDestino() {
		return cargoTitularAreaDestino;
	}

	public void setCargoTitularAreaDestino(String cargoTitularAreaDestino) {
		this.cargoTitularAreaDestino = cargoTitularAreaDestino;
	}

	public String getAreaDestino() {
		return areaDestino;
	}

	public void setAreaDestino(String areaDestino) {
		this.areaDestino = areaDestino;
	}

	public String getInstruccionDescripcion() {
		return instruccionDescripcion;
	}

	public void setInstruccionDescripcion(String instruccionDescripcion) {
		this.instruccionDescripcion = instruccionDescripcion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	public String getPrioridadDescripcion() {
		return prioridadDescripcion;
	}

	public void setPrioridadDescripcion(String prioridadDescripcion) {
		this.prioridadDescripcion = prioridadDescripcion;
	}

	public String getFolioAreaAsuntoPadre() {
		return folioAreaAsuntoPadre;
	}

	public void setFolioAreaAsuntoPadre(String folioAreaAsuntoPadre) {
		this.folioAreaAsuntoPadre = folioAreaAsuntoPadre;
	}

	public String getNumDoctoPadre() {
		return numDoctoPadre;
	}

	public void setNumDoctoPadre(String numDoctoPadre) {
		this.numDoctoPadre = numDoctoPadre;
	}

	public Date getFechaElaboracionPadre() {
		return fechaElaboracionPadre;
	}

	public void setFechaElaboracionPadre(Date fechaElaboracionPadre) {
		this.fechaElaboracionPadre = fechaElaboracionPadre;
	}

	public Date getFechaRegistroPadre() {
		return fechaRegistroPadre;
	}

	public void setFechaRegistroPadre(Date fechaRegistroPadre) {
		this.fechaRegistroPadre = fechaRegistroPadre;
	}

	public String getAsuntoDescripcionPadre() {
		return asuntoDescripcionPadre;
	}

	public void setAsuntoDescripcionPadre(String asuntoDescripcionPadre) {
		this.asuntoDescripcionPadre = asuntoDescripcionPadre;
	}

	public String getFirmanteAsuntoPadre() {
		return firmanteAsuntoPadre;
	}

	public void setFirmanteAsuntoPadre(String firmanteAsuntoPadre) {
		this.firmanteAsuntoPadre = firmanteAsuntoPadre;
	}

	public String getFirmanteCargoPadre() {
		return firmanteCargoPadre;
	}

	public void setFirmanteCargoPadre(String firmanteCargoPadre) {
		this.firmanteCargoPadre = firmanteCargoPadre;
	}

	public String getRemitentePadre() {
		return remitentePadre;
	}

	public void setRemitentePadre(String remitentePadre) {
		this.remitentePadre = remitentePadre;
	}

	public TipoAsunto getTipoAsuntoPadre() {
		return tipoAsuntoPadre;
	}

	public void setTipoAsuntoPadre(TipoAsunto tipoAsuntoPadre) {
		this.tipoAsuntoPadre = tipoAsuntoPadre;
	}

	public String getStatusAsuntoPadre() {
		return statusAsuntoPadre;
	}

	public void setStatusAsuntoPadre(String statusAsuntoPadre) {
		this.statusAsuntoPadre = statusAsuntoPadre;
	}

	public Boolean getConfidencial() {
		return confidencial;
	}

	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
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

	public Boolean getRequiereRespuesta() {
		return requiereRespuesta;
	}

	public void setRequiereRespuesta(Boolean requiereRespuesta) {
		this.requiereRespuesta = requiereRespuesta;
	}

	public String getFolioIntermedio() {
		return folioIntermedio;
	}

	public void setFolioIntermedio(String folioIntermedio) {
		this.folioIntermedio = folioIntermedio;
	}

	public String getIdTipoRegistroPadre() {
		return idTipoRegistroPadre;
	}

	public void setIdTipoRegistroPadre(String idTipoRegistroPadre) {
		this.idTipoRegistroPadre = idTipoRegistroPadre;
	}

	public Date getFechaCompromisoPadre() {
		return fechaCompromisoPadre;
	}

	public void setFechaCompromisoPadre(Date fechaCompromisoPadre) {
		this.fechaCompromisoPadre = fechaCompromisoPadre;
	}

	public String getPromotorAbreviaturaPadre() {
		return promotorAbreviaturaPadre;
	}

	public void setPromotorAbreviaturaPadre(String promotorAbreviaturaPadre) {
		this.promotorAbreviaturaPadre = promotorAbreviaturaPadre;
	}

	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public String getAreaPadre() {
		return areaPadre;
	}

	public void setAreaPadre(String areaPadre) {
		this.areaPadre = areaPadre;
	}

	public String getDestinatarioAsuntoNombre() {
		return destinatarioAsuntoNombre;
	}

	public void setDestinatarioAsuntoNombre(String destinatarioAsuntoNombre) {
		this.destinatarioAsuntoNombre = destinatarioAsuntoNombre;
	}
	
}
