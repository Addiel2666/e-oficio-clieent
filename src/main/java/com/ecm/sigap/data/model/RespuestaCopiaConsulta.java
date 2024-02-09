/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.SubTipoAsuntoToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * Clase de identidad para detalle de las CopiasRespuesta.
 *
 * @author ECM Solutions
 * @version 1.0
 */
@Entity
@Table(name = "RESPUESTACONSULTADETALLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RespuestaCopiaConsulta implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5970466525042990524L;

	/** --- - - R E S P U E S T A - - --- */

	/** Identificador de la Respuesta. */
	@Id
	@Column(name = "RESPUESTA_IDRESPUESTA")
	private Integer idRespuesta;

	/** Identificador de la Respuesta. */
	@Column(name = "RESPUESTA_IDASUNTO")
	private Integer idAsuntoRespuesta;

	/** Fecha de registro de la Respuesta. */
	@Column(name = "RESPUESTA_FECHAREGISTRO")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** Fecha de envio de la Respuesta. */
	@Column(name = "RESPUESTA_FECHAENVIO")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** Fecha de acuse de recepcion de la Respuesta. */
	@Column(name = "RESPUESTA_FECHAACUSE")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/** Comentario de la Respuesta. */
	@Column(name = "RESPUESTA_COMENTARIO")
	private String comentario;

	/** Comentario de rechazo. */
	@Column(name = "RESPUESTA_COMENTARIORECHAZO")
	private String comentarioRechazo;

	/** Porcentaje de avance de la Respuesta. */
	@Column(name = "RESPUESTA_PORCENTAJE")
	private Integer porcentaje;

	/** Estatus de la Respuesta. */
	@OneToOne
	@JoinColumn(name = "RESPUESTA_IDESTATUSRESPUESTA", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Status statusRespuesta;

	/** */
	@Column(name = "RESPUESTA_IDAREA")
	private Integer idAreaRespuesta;

	/** */
	@Column(name = "RESPUESTA_AREADESTINOID")
	private Integer areaDestinoId;

	/** Atributos de la Respuesta. */
	@Column(name = "RESPUESTA_ATRIBUTOS")
	private String atributos;

	/** Identificador del Folio de la Respuesta. */
	@Column(name = "RESPUESTA_FOLIORESPUESTA")
	private String folioRespuesta;

	/**
	 * Indicador si la respuesta se va a cargar en el ZIP de Respuestas Infomex.
	 */
	@Column(name = "RESPUESTA_INFOMEXZIPSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean infomexZip;

	/** --- - - A S U N T O - - --- */

	/** Identificador del Asunto al que se responde. */
	@Column(name = "ASUNTO_IDASUNTO")
	private Integer idAsunto;

	/** Tipo de Asunto {@link com.ecm.sigap.data.model.util.TipoAsunto} */
	@Column(name = "ASUNTO_IDTIPOASUNTO")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** The comentario. */
	@Column(name = "ASUNTO_COMENTARIO")
	private String comentarioAsunto;

	/** The fecha envio. */
	@Column(name = "ASUNTO_FECHAENVIO")
	@Type(type = "java.util.Date")
	private Date fechaEnvioAsunto;

	/** The fecha compromiso. */
	@Column(name = "ASUNTO_FECHAACUSE")
	@Type(type = "java.util.Date")
	private Date fechaAcuseAsunto;

	/** The en tiempo. */
	@Column(name = "ASUNTO_ETFT")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;

	/** The especialsn. */
	@Column(name = "ASUNTO_ESPECIALSN")
	private String especialsnAsunto;

	/** The folio area. */
	@Column(name = "ASUNTO_FOLIOAREA")
	private String folioAreaAsunto;

	/** The id tipo registro. */
	@Column(name = "ASUNTO_IDTIPOREGISTRO")
	private String idTipoRegistroAsunto;

	/** The tipo asunto. */
	@Column(name = "ASUNTO_IDSUBTIPOASUNTO")
	@Convert(converter = SubTipoAsuntoToStringConverter.class)
	private SubTipoAsunto subTipoAsunto;

	/** The fecha compromiso. */
	@Column(name = "ASUNTO_FECHACOMPROMISO")
	@Type(type = "java.util.Date")
	private Date fechaCompromisoAsunto;

	/** The fecha registro. */
	@Column(name = "ASUNTO_FECHAREGISTRO")
	@Type(type = "java.util.Date")
	private Date fechaRegistroAsunto;

	/** Identificador del Asunto Padre. */
	@Column(name = "ASUNTO_IDASUNTOPADRE")
	private Integer idAsuntoPadre;

	/**  */
	@Column(name = "ASUNTO_INSTRUCCION")
	private String instruccion;

	/**  */
	@Column(name = "ASUNTO_PRIORIDAD")
	private String prioridad;

	/** The status asunto. */
	@OneToOne
	@JoinColumn(name = "ASUNTO_IDESTATUSASUNTO", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Status estatusAsunto;

	/** id padre mas antiguo */
	@Column(name = "asunto_idAsuntoOrigen")
	private Integer idAsuntoOrigen;

	/** cuenta cuantos documentos tiene la respuesta */
	@Formula("{SIGAP_SCHEMA}.RESPUESTAHASDOCS(RESPUESTA_IDRESPUESTA)")
	private Integer documentosAdjuntos;

	/** The fecha elaboracion. */
	@Column(name = "ASUNTO_FECHAELABORACION")
	@Type(type = "java.util.Date")
	private Date fechaElaboracionAsunto;

	/** The asunto descripcion. */
	@Column(name = "ASUNTO_ASUNTODESCRIPCION")
	private String asuntoDescripcionAsunto;

	/** The palabra clave. */
	@Column(name = "ASUNTO_PALABRACLAVE")
	private String palabraClaveAsunto;

	/** The fecha recepcion. */
	@Column(name = "ASUNTO_FECHARECEPCION")
	@Type(type = "java.util.Date")
	private Date fechaRecepcionAsunto;

	/** The num docto. */
	@Column(name = "ASUNTO_NUMDOCTO")
	private String numDoctoAsunto;

	/** Identificador de la procedencia del Asunto al que se responde. */
	@Column(name = "ASUNTO_IDPROCEDENCIA")
	private String idProcedencia;

	/** Identificador idPromotor. */
	@Column(name = "ASUNTO_IDPROMOTOR")
	private String idPromotor;

	/** Identificador idPromotor. */
	@Column(name = "ASUNTO_FIRMANTEASUNTO")
	private String firmante;

	/** Identificador idPromotor. */
	@Column(name = "ASUNTO_FIRMANTECARGO")
	private String firmanteCargo;

	/** --- - - P R O M O T O R - - --- */

	/** The institucion. */
	@OneToOne
	@JoinColumn(name = "ASUNTO_IDPROMOTOR", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Institucion Promotor;

	/** --- - - T I P O R E S P U E S T A - - --- */

	/** Tipo de respuesta */
	@OneToOne
	@JoinColumn(name = "RESPUESTA_IDTIPORESPUESTA", nullable = false)
	@Fetch(FetchMode.SELECT)
	private TipoRespuesta TipoRespuesta;

	/** --- - - A R E A S R E S P U E S T A - - --- */

	/** Area que genera la Respuesta */
	@OneToOne(optional = true)
	@JoinColumn(name = "RESPUESTA_IDAREA", insertable = false, updatable = false)
	@Fetch(FetchMode.SELECT)
	private Area2 area;

	/** --- - - F I N - - --- */

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

	/**
	 * @return the idAsuntoRespuesta
	 */
	public Integer getIdAsuntoRespuesta() {
		return idAsuntoRespuesta;
	}

	/**
	 * @param idAsuntoRespuesta the idAsuntoRespuesta to set
	 */
	public void setIdAsuntoRespuesta(Integer idAsuntoRespuesta) {
		this.idAsuntoRespuesta = idAsuntoRespuesta;
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
	 * @return the comentarioRechazo
	 */
	public String getComentarioRechazo() {
		return comentarioRechazo;
	}

	/**
	 * @param comentarioRechazo the comentarioRechazo to set
	 */
	public void setComentarioRechazo(String comentarioRechazo) {
		this.comentarioRechazo = comentarioRechazo;
	}

	/**
	 * @return the porcentaje
	 */
	public Integer getPorcentaje() {
		return porcentaje;
	}

	/**
	 * @param porcentaje the porcentaje to set
	 */
	public void setPorcentaje(Integer porcentaje) {
		this.porcentaje = porcentaje;
	}

	/**
	 * @return the statusRespuesta
	 */
	public Status getStatusRespuesta() {
		return statusRespuesta;
	}

	/**
	 * @param statusRespuesta the statusRespuesta to set
	 */
	public void setStatusRespuesta(Status statusRespuesta) {
		this.statusRespuesta = statusRespuesta;
	}

	/**
	 * @return the idAreaRespuesta
	 */
	public Integer getIdAreaRespuesta() {
		return idAreaRespuesta;
	}

	/**
	 * @param idAreaRespuesta the idAreaRespuesta to set
	 */
	public void setIdAreaRespuesta(Integer idAreaRespuesta) {
		this.idAreaRespuesta = idAreaRespuesta;
	}

	/**
	 * @return the areaDestinoId
	 */
	public Integer getAreaDestinoId() {
		return areaDestinoId;
	}

	/**
	 * @param areaDestinoId the areaDestinoId to set
	 */
	public void setAreaDestinoId(Integer areaDestinoId) {
		this.areaDestinoId = areaDestinoId;
	}

	/**
	 * @return the atributos
	 */
	public String getAtributos() {
		return atributos;
	}

	/**
	 * @param atributos the atributos to set
	 */
	public void setAtributos(String atributos) {
		this.atributos = atributos;
	}

	/**
	 * @return the folioRespuesta
	 */
	public String getFolioRespuesta() {
		return folioRespuesta;
	}

	/**
	 * @param folioRespuesta the folioRespuesta to set
	 */
	public void setFolioRespuesta(String folioRespuesta) {
		this.folioRespuesta = folioRespuesta;
	}

	/**
	 * @return the infomexZip
	 */
	public Boolean getInfomexZip() {
		return infomexZip;
	}

	/**
	 * @param infomexZip the infomexZip to set
	 */
	public void setInfomexZip(Boolean infomexZip) {
		this.infomexZip = infomexZip;
	}

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
	 * @return the comentarioAsunto
	 */
	public String getComentarioAsunto() {
		return comentarioAsunto;
	}

	/**
	 * @param comentarioAsunto the comentarioAsunto to set
	 */
	public void setComentarioAsunto(String comentarioAsunto) {
		this.comentarioAsunto = comentarioAsunto;
	}

	/**
	 * @return the fechaEnvioAsunto
	 */
	public Date getFechaEnvioAsunto() {
		return fechaEnvioAsunto;
	}

	/**
	 * @param fechaEnvioAsunto the fechaEnvioAsunto to set
	 */
	public void setFechaEnvioAsunto(Date fechaEnvioAsunto) {
		this.fechaEnvioAsunto = fechaEnvioAsunto;
	}

	/**
	 * @return the fechaAcuseAsunto
	 */
	public Date getFechaAcuseAsunto() {
		return fechaAcuseAsunto;
	}

	/**
	 * @param fechaAcuseAsunto the fechaAcuseAsunto to set
	 */
	public void setFechaAcuseAsunto(Date fechaAcuseAsunto) {
		this.fechaAcuseAsunto = fechaAcuseAsunto;
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
	 * @return the especialsnAsunto
	 */
	public String getEspecialsnAsunto() {
		return especialsnAsunto;
	}

	/**
	 * @param especialsnAsunto the especialsnAsunto to set
	 */
	public void setEspecialsnAsunto(String especialsnAsunto) {
		this.especialsnAsunto = especialsnAsunto;
	}

	/**
	 * @return the folioAreaAsunto
	 */
	public String getFolioAreaAsunto() {
		return folioAreaAsunto;
	}

	/**
	 * @param folioAreaAsunto the folioAreaAsunto to set
	 */
	public void setFolioAreaAsunto(String folioAreaAsunto) {
		this.folioAreaAsunto = folioAreaAsunto;
	}

	/**
	 * @return the idTipoRegistroAsunto
	 */
	public String getIdTipoRegistroAsunto() {
		return idTipoRegistroAsunto;
	}

	/**
	 * @param idTipoRegistroAsunto the idTipoRegistroAsunto to set
	 */
	public void setIdTipoRegistroAsunto(String idTipoRegistroAsunto) {
		this.idTipoRegistroAsunto = idTipoRegistroAsunto;
	}

	/**
	 * @return the subTipoAsunto
	 */
	public SubTipoAsunto getSubTipoAsunto() {
		return subTipoAsunto;
	}

	/**
	 * @param subTipoAsunto the subTipoAsunto to set
	 */
	public void setSubTipoAsunto(SubTipoAsunto subTipoAsunto) {
		this.subTipoAsunto = subTipoAsunto;
	}

	/**
	 * @return the fechaCompromisoAsunto
	 */
	public Date getFechaCompromisoAsunto() {
		return fechaCompromisoAsunto;
	}

	/**
	 * @param fechaCompromisoAsunto the fechaCompromisoAsunto to set
	 */
	public void setFechaCompromisoAsunto(Date fechaCompromisoAsunto) {
		this.fechaCompromisoAsunto = fechaCompromisoAsunto;
	}

	/**
	 * @return the fechaRegistroAsunto
	 */
	public Date getFechaRegistroAsunto() {
		return fechaRegistroAsunto;
	}

	/**
	 * @param fechaRegistroAsunto the fechaRegistroAsunto to set
	 */
	public void setFechaRegistroAsunto(Date fechaRegistroAsunto) {
		this.fechaRegistroAsunto = fechaRegistroAsunto;
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
	 * @return the instruccion
	 */
	public String getInstruccion() {
		return instruccion;
	}

	/**
	 * @param instruccion the instruccion to set
	 */
	public void setInstruccion(String instruccion) {
		this.instruccion = instruccion;
	}

	/**
	 * @return the prioridad
	 */
	public String getPrioridad() {
		return prioridad;
	}

	/**
	 * @param prioridad the prioridad to set
	 */
	public void setPrioridad(String prioridad) {
		this.prioridad = prioridad;
	}

	/**
	 * @return the estatusAsunto
	 */
	public Status getEstatusAsunto() {
		return estatusAsunto;
	}

	/**
	 * @param estatusAsunto the estatusAsunto to set
	 */
	public void setEstatusAsunto(Status estatusAsunto) {
		this.estatusAsunto = estatusAsunto;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	/**
	 * 
	 * @param idAsuntoOrigen
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
	 * @return the fechaElaboracionAsunto
	 */
	public Date getFechaElaboracionAsunto() {
		return fechaElaboracionAsunto;
	}

	/**
	 * @param fechaElaboracionAsunto the fechaElaboracionAsunto to set
	 */
	public void setFechaElaboracionAsunto(Date fechaElaboracionAsunto) {
		this.fechaElaboracionAsunto = fechaElaboracionAsunto;
	}

	/**
	 * @return the asuntoDescripcionAsunto
	 */
	public String getAsuntoDescripcionAsunto() {
		return asuntoDescripcionAsunto;
	}

	/**
	 * @param asuntoDescripcionAsunto the asuntoDescripcionAsunto to set
	 */
	public void setAsuntoDescripcionAsunto(String asuntoDescripcionAsunto) {
		this.asuntoDescripcionAsunto = asuntoDescripcionAsunto;
	}

	/**
	 * @return the palabraClaveAsunto
	 */
	public String getPalabraClaveAsunto() {
		return palabraClaveAsunto;
	}

	/**
	 * @param palabraClaveAsunto the palabraClaveAsunto to set
	 */
	public void setPalabraClaveAsunto(String palabraClaveAsunto) {
		this.palabraClaveAsunto = palabraClaveAsunto;
	}

	/**
	 * @return the fechaRecepcionAsunto
	 */
	public Date getFechaRecepcionAsunto() {
		return fechaRecepcionAsunto;
	}

	/**
	 * @param fechaRecepcionAsunto the fechaRecepcionAsunto to set
	 */
	public void setFechaRecepcionAsunto(Date fechaRecepcionAsunto) {
		this.fechaRecepcionAsunto = fechaRecepcionAsunto;
	}

	/**
	 * @return the numDoctoAsunto
	 */
	public String getNumDoctoAsunto() {
		return numDoctoAsunto;
	}

	/**
	 * @param numDoctoAsunto the numDoctoAsunto to set
	 */
	public void setNumDoctoAsunto(String numDoctoAsunto) {
		this.numDoctoAsunto = numDoctoAsunto;
	}

	/**
	 * @return the idProcedencia
	 */
	public String getIdProcedencia() {
		return idProcedencia;
	}

	/**
	 * @param idProcedencia the idProcedencia to set
	 */
	public void setIdProcedencia(String idProcedencia) {
		this.idProcedencia = idProcedencia;
	}

	/**
	 * @return the idPromotor
	 */
	public String getIdPromotor() {
		return idPromotor;
	}

	/**
	 * @param idPromotor the idPromotor to set
	 */
	public void setIdPromotor(String idPromotor) {
		this.idPromotor = idPromotor;
	}

	/**
	 * @return the promotor
	 */
	public Institucion getPromotor() {
		return Promotor;
	}

	/**
	 * @param promotor the promotor to set
	 */
	public void setPromotor(Institucion promotor) {
		Promotor = promotor;
	}

	/**
	 * @return the tipoRespuesta
	 */
	public TipoRespuesta getTipoRespuesta() {
		return TipoRespuesta;
	}

	/**
	 * @param tipoRespuesta the tipoRespuesta to set
	 */
	public void setTipoRespuesta(TipoRespuesta tipoRespuesta) {
		TipoRespuesta = tipoRespuesta;
	}

	/**
	 * @return the area
	 */
	public Area2 getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(Area2 area) {
		this.area = area;
	}

	/**
	 * @return the firmante
	 */
	public String getFirmante() {
		return firmante;
	}

	/**
	 * @param firmante the firmante to set
	 */
	public void setFirmante(String firmante) {
		this.firmante = firmante;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RespuestaCopiaConsulta [idRespuesta=" + idRespuesta + ", idAsuntoRespuesta=" + idAsuntoRespuesta
				+ ", fechaRegistro=" + fechaRegistro + ", fechaEnvio=" + fechaEnvio + ", fechaAcuse=" + fechaAcuse
				+ ", comentario=" + comentario + ", comentarioRechazo=" + comentarioRechazo + ", porcentaje="
				+ porcentaje + ", statusRespuesta=" + statusRespuesta + ", idAreaRespuesta=" + idAreaRespuesta
				+ ", areaDestinoId=" + areaDestinoId + ", atributos=" + atributos + ", folioRespuesta=" + folioRespuesta
				+ ", infomexZip=" + infomexZip + ", idAsunto=" + idAsunto + ", tipoAsunto=" + tipoAsunto
				+ ", comentarioAsunto=" + comentarioAsunto + ", fechaEnvioAsunto=" + fechaEnvioAsunto
				+ ", fechaAcuseAsunto=" + fechaAcuseAsunto + ", enTiempo=" + enTiempo + ", especialsnAsunto="
				+ especialsnAsunto + ", folioAreaAsunto=" + folioAreaAsunto + ", idTipoRegistroAsunto="
				+ idTipoRegistroAsunto + ", subTipoAsunto=" + subTipoAsunto + ", fechaCompromisoAsunto="
				+ fechaCompromisoAsunto + ", fechaRegistroAsunto=" + fechaRegistroAsunto + ", idAsuntoPadre="
				+ idAsuntoPadre + ", estatusAsunto=" + estatusAsunto + ", documentosAdjuntos=" + documentosAdjuntos
				+ ", fechaElaboracionAsunto=" + fechaElaboracionAsunto + ", asuntoDescripcionAsunto="
				+ asuntoDescripcionAsunto + ", palabraClaveAsunto=" + palabraClaveAsunto + ", fechaRecepcionAsunto="
				+ fechaRecepcionAsunto + ", numDoctoAsunto=" + numDoctoAsunto + ", idProcedencia=" + idProcedencia
				+ ", idPromotor=" + idPromotor + ", Promotor=" + Promotor + ", TipoRespuesta=" + TipoRespuesta
				+ ", area=" + area + "]";
	}

}
