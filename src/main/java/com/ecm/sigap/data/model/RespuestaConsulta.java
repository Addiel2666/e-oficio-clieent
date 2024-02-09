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
import javax.persistence.Transient;

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
 * The Class RespuestaConsulta.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "RESPUESTACONSULTAR_FULL")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RespuestaConsulta implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7257160762279872067L;

	/** Identificador de la Respuesta. */
	@Id
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Identificador del Asunto al que se responde. */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	// - - - - - - - - - - - -

	/** Identificador de la procedencia del Asunto al que se responde. */
	@Column(name = "idProcedencia")
	private String idProcedencia;

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

	/** The instruccion descripcion. */
	@Column(name = "ASUNTO_INSTRUCCIONDESC")
	private String instruccionDescripcionAsunto;

	/** The prioridad descripcion. */
	@Column(name = "ASUNTO_PRIORIDADDESC")
	private String prioridadDescripcionAsunto;

	/** The especialsn. */
	@Column(name = "ASUNTO_ESPECIALSN")
	private String especialsnAsunto;

	/** The folio area. */
	@Column(name = "ASUNTO_FOLIOAREA")
	private String folioAreaAsunto;

	/** The folio area asunto padre. */
	@Column(name = "ASUNTO_folioAreaAsuntoPadre")
	private String folioAreaAsuntoPadreAsunto;

	/** The fecha registro padre. */
	@Column(name = "ASUNTO_fechaRegistroPadre")
	@Type(type = "java.util.Date")
	private Date fechaRegistroPadreAsunto;

	/** The asunto descripcion padre. */
	@Column(name = "ASUNTO_asuntoDescripcionPadre")
	private String asuntoDescripcionPadreAsunto;

	/** The firmante asunto padre. */
	@Column(name = "ASUNTO_firmanteAsuntoPadre")
	private String firmanteAsuntoPadreAsunto;

	/** The firmante cargo padre. */
	@Column(name = "ASUNTO_firmanteCargoPadre")
	private String firmanteCargoPadreAsunto;

	/** The area padre. */
	@Column(name = "ASUNTO_areaPadre")
	private String areaPadreAsunto;

	/** The promotor padre. */
	@Column(name = "ASUNTO_promotorPadre")
	private String promotorPadreAsunto;

	/** The tipo asunto padre. */
	@Column(name = "ASUNTO_idTipoAsuntoPadre")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsuntoPadreAsunto;

	/** The remitente padre. */
	@Column(name = "ASUNTO_remitentePadre")
	private String remitentePadreAsunto;

	/** The status asunto padre. */
	@Column(name = "ASUNTO_ESTATUSASUNTOPADRE")
	private String statusAsuntoPadreAsunto;

	/** The fecha elaboracion. */
	@Column(name = "ASUNTO_fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracionAsunto;

	/** The asunto descripcion. */
	@Column(name = "ASUNTO_asuntoDescripcion")
	private String asuntoDescripcionAsunto;

	/** The firmante cargo. */
	@Column(name = "ASUNTO_firmanteCargo")
	private String firmanteCargoAsunto;

	/** The remitente. */
	@Column(name = "ASUNTO_remitente")
	private String remitenteAsunto;

	/** The tipo asunto. */
	@Column(name = "ASUNTO_idSubTipoAsunto")
	@Convert(converter = SubTipoAsuntoToStringConverter.class)
	private SubTipoAsunto subTipoAsunto;

	/** The fecha compromiso. */
	@Column(name = "ASUNTO_fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromisoAsunto;

	/** The palabra clave. */
	@Column(name = "ASUNTO_palabraClave")
	private String palabraClaveAsunto;

	/** The anotaciones. */
	@Column(name = "ASUNTO_tipo")
	private String tipo_asunto;

	// - - -

	/** The id tipo registro. */
	@Column(name = "ASUNTO_idTipoRegistro")
	private String idTipoRegistroAsunto;

	/** The documentos count. */
	@Column(name = "ASUNTO_documentosCount")
	private Integer documentosAdjuntosAsunto;

	/** The promotor. */
	@Column(name = "ASUNTO_promotor")
	private String promotorAsunto;

	/** The id tipo registro padre. */
	@Column(name = "ASUNTO_idTipoRegistroPadre")
	private String idTipoRegistroPadreAsunto;

	/** The nombre turnador padre. */
	@Column(name = "ASUNTO_nombreTurnadorPadre")
	private String nombreTurnadorPadre;

	/** The status asunto. */
	@Column(name = "ASUNTO_statusAsunto")
	private String statusAsunto;

	/** The fecha registro. */
	@Column(name = "ASUNTO_fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistroAsunto;

	/** Indicador si el Tipo de Instruccion requiere o no respuesta. */
	@Column(name = "ASUNTO_INSTRUCCIONRR", length = 1)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuestaAsunto;

	/** The fecha recepcion. */
	@Column(name = "ASUNTO_fechaRecepcion")
	@Type(type = "java.util.Date")
	private Date fechaRecepcionAsunto;

	/** The num docto. */
	@Column(name = "ASUNTO_numDocto")
	private String numDoctoAsunto;

	/** Identificador del Asunto Padre. */
	@Column(name = "ASUNTO_idAsuntoPadre")
	private Integer idAsuntoPadre;

	// - - -

	/** The fecha compromiso padre. */
	@Column(name = "asuntoPadre_fechacompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromisoPadreAsunto;

	/** The num docto padre. */
	@Column(name = "asuntoPadre_numdocto")
	private String numDoctoPadreAsunto;

	/** The fecha elaboracion padre. */
	@Column(name = "ASUNTO_FECHAELABPADREASUNTO")
	@Type(type = "java.util.Date")
	private Date fechaElaboracionPadreAsunto;

	/** Identificador si el Asunto / Tramite es confidencial */
	@Column(name = "ASUNTO_CONFIDENCIALSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;


	/** id asunto origen */
	@Column(name = "asunto_idAsuntoOrigen")
	private Integer idAsuntoOrigen;
	// - - - - - - - - - - - -

	/** Fecha de registro de la Respuesta. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** Fecha de envio de la Respuesta. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** Fecha de acuse de recepcion de la Respuesta. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/** Comentario de la Respuesta. */
	@Column(name = "comentario")
	private String comentario;

	/** Comentario de rechazo. */
	@Column(name = "comentariorechazo")
	private String comentarioRechazo;

	/** Porcentaje de avance de la Respuesta. */
	@Column(name = "porcentaje")
	private Integer porcentaje;

	/** */
	@Column(name = "tipoRespuestaDescripcion")
	private String tipoRespuestaDescripcion;

	/** */
	@Column(name = "tipoRespuestaId")
	private String tipoRespuestaId;

	/** Estatus de la Respuesta. */
	@OneToOne
	@JoinColumn(name = "idEstatusRespuesta", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Status status;

	/** */
	@Column(name = "areaDestinoId")
	private Integer areaDestinoId;

	/** */
	@Column(name = "areaDestinoDescripcion")
	private String areaDestinoDescripcion;

	/** */
	@Column(name = "AreaDestinoInstitucionId")
	private Integer areaDestinoInstitucionId;

	/** */
	@Column(name = "AREADESTINOINSTITUCIONDESC")
	private String AreaDestinoInstitucionDescripcion;

	/** */
	@Column(name = "areaDestinoTitularId")
	private String areaDestinoTitularId;

	/** */
	@Column(name = "areaDestinoTitularAreaId")
	private Integer areaDestinoTitularAreaId;

	/** */
	@Column(name = "areaDestTitularAreaInstId")
	private Integer areaDestinoTitularAreaInstitucionId;

	/** Empresa que genera la Respuesta (Para el caso de Tramites Externos). */
	@OneToOne
	@JoinColumn(name = "areaDestinoId", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Empresa empresaDestino;

	/** */
	@Column(name = "areaId")
	private Integer areaId;

	/** */
	@Column(name = "areaDescripcion")
	private String areaDescripcion;

	/** */
	@Column(name = "areaInstitucionId")
	private Integer areaInstitucionId;

	/** */
	@Column(name = "areaInstitucionDescripcion")
	private String areaInstitucionDescripcion;

	/** */
	@Column(name = "areaTitularId")
	private String areaTitularId;

	/** */
	@Column(name = "areaTitularAreaId")
	private Integer areaTitularAreaId;

	/** */
	@Column(name = "areaTitularAreaInstitucionId")
	private Integer areaTitularAreaInstitucionId;

	/** Atributos de la Respuesta. */
	@Column(name = "atributos")
	private String atributos;

	/** Identificador del Folio de la Respuesta. */
	@Column(name = "folioRespuesta")
	private String folioRespuesta;

	/**
	 * Indicador si la respuesta se va a cargar en el ZIP de Respuestas Infomex.
	 */
	@Column(name = "infomexzipsn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean infomexZip;

	/** cuenta cuantos documentos tiene la respuesta */
	@Formula("{SIGAP_SCHEMA}.RESPUESTAHASDOCS(idRespuesta)")
	private Integer documentosAdjuntos;

	/**  */
	@Column(name = "acepto_rechazo_id")
	private String acepto_rechazo_id;

	/**  */
	@Column(name = "acepto_rechazo_nombrecompleto")
	private String acepto_rechazo_nombrecompleto;

	/** Nombre del usuario que envia la respuesta. */
	@Column(name = "nombre_firmante")
	private String NombreFirmante;
	
	/**  */
	@Column(name = "area_titular_nombrecompleto")
	private String areaTitularNombrecompleto;

	/** */
	@Column(name = "areaInstitucionAbreviatura")
	private String areaInstitucionAbreviatura;
	
	/** Flag Bandeja de respuestas que me han rechazado */
	@Transient
	private boolean bandRespRech;
	
	/**  */
	@Transient
	private Area2 area;
	
	/**
	 * Gets the id respuesta.
	 *
	 * @return the id respuesta
	 */
	public Integer getIdRespuesta() {
		return idRespuesta;
	}

	/**
	 * Sets the id respuesta.
	 *
	 * @param idRespuesta the new id respuesta
	 */
	public void setIdRespuesta(Integer idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	/**
	 * Gets the id asunto.
	 *
	 * @return the id asunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * Sets the id asunto.
	 *
	 * @param idAsunto the new id asunto
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * Gets the id procedencia.
	 *
	 * @return the id procedencia
	 */
	public String getIdProcedencia() {
		return idProcedencia;
	}

	/**
	 * Sets the id procedencia.
	 *
	 * @param idProcedencia the new id procedencia
	 */
	public void setIdProcedencia(String idProcedencia) {
		this.idProcedencia = idProcedencia;
	}

	/**
	 * Gets the fecha registro.
	 *
	 * @return the fecha registro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * Sets the fecha registro.
	 *
	 * @param fechaRegistro the new fecha registro
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Gets the fecha envio.
	 *
	 * @return the fecha envio
	 */
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * Sets the fecha envio.
	 *
	 * @param fechaEnvio the new fecha envio
	 */
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Gets the fecha acuse.
	 *
	 * @return the fecha acuse
	 */
	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	/**
	 * Sets the fecha acuse.
	 *
	 * @param fechaAcuse the new fecha acuse
	 */
	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	/**
	 * Gets the comentario.
	 *
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * Sets the comentario.
	 *
	 * @param comentario the new comentario
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	/**
	 * Gets the comentario rechazo.
	 *
	 * @return the comentario rechazo
	 */
	public String getComentarioRechazo() {
		return comentarioRechazo;
	}

	/**
	 * Sets the comentario rechazo.
	 *
	 * @param comentarioRechazo the new comentario rechazo
	 */
	public void setComentarioRechazo(String comentarioRechazo) {
		this.comentarioRechazo = comentarioRechazo;
	}

	/**
	 * Gets the porcentaje.
	 *
	 * @return the porcentaje
	 */
	public Integer getPorcentaje() {
		return porcentaje;
	}

	/**
	 * Sets the porcentaje.
	 *
	 * @param porcentaje the new porcentaje
	 */
	public void setPorcentaje(Integer porcentaje) {
		this.porcentaje = porcentaje;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Gets the empresa destino.
	 *
	 * @return the empresa destino
	 */
	public Empresa getEmpresaDestino() {
		return empresaDestino;
	}

	/**
	 * Sets the empresa destino.
	 *
	 * @param empresaDestino the new empresa destino
	 */
	public void setEmpresaDestino(Empresa empresaDestino) {
		this.empresaDestino = empresaDestino;
	}

	/**
	 * Gets the atributos.
	 *
	 * @return the atributos
	 */
	public String getAtributos() {
		return atributos;
	}

	/**
	 * Sets the atributos.
	 *
	 * @param atributos the new atributos
	 */
	public void setAtributos(String atributos) {
		this.atributos = atributos;
	}

	/**
	 * Gets the folio respuesta.
	 *
	 * @return the folio respuesta
	 */
	public String getFolioRespuesta() {
		return folioRespuesta;
	}

	/**
	 * Sets the folio respuesta.
	 *
	 * @param folioRespuesta the new folio respuesta
	 */
	public void setFolioRespuesta(String folioRespuesta) {
		this.folioRespuesta = folioRespuesta;
	}

	/**
	 * Gets the infomex zip.
	 *
	 * @return the infomex zip
	 */
	public Boolean getInfomexZip() {
		return infomexZip;
	}

	/**
	 * Sets the infomex zip.
	 *
	 * @param infomexZip the new infomex zip
	 */
	public void setInfomexZip(Boolean infomexZip) {
		this.infomexZip = infomexZip;
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
	 * @return the acepto_rechazo_id
	 */
	public String getAcepto_rechazo_id() {
		return acepto_rechazo_id;
	}

	/**
	 * @param acepto_rechazo_id the acepto_rechazo_id to set
	 */
	public void setAcepto_rechazo_id(String acepto_rechazo_id) {
		this.acepto_rechazo_id = acepto_rechazo_id;
	}

	/**
	 * @return the acepto_rechazo_nombre
	 */
	public String getAcepto_rechazo_nombrecompleto() {
		return acepto_rechazo_nombrecompleto;
	}

	/**
	 * @param acepto_rechazo_nombre the acepto_rechazo_nombre to set
	 */
	public void setAcepto_rechazo_nombrecompleto(String acepto_rechazo_nombrecompleto) {
		this.acepto_rechazo_nombrecompleto = acepto_rechazo_nombrecompleto;
	}

	public TipoAsunto getTipoAsunto() {
		return tipoAsunto;
	}

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
	 * @return the instruccionDescripcionAsunto
	 */
	public String getInstruccionDescripcionAsunto() {
		return instruccionDescripcionAsunto;
	}

	/**
	 * @param instruccionDescripcionAsunto the instruccionDescripcionAsunto to set
	 */
	public void setInstruccionDescripcionAsunto(String instruccionDescripcionAsunto) {
		this.instruccionDescripcionAsunto = instruccionDescripcionAsunto;
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
	 * @return the prioridadDescripcionAsunto
	 */
	public String getPrioridadDescripcionAsunto() {
		return prioridadDescripcionAsunto;
	}

	/**
	 * @param prioridadDescripcionAsunto the prioridadDescripcionAsunto to set
	 */
	public void setPrioridadDescripcionAsunto(String prioridadDescripcionAsunto) {
		this.prioridadDescripcionAsunto = prioridadDescripcionAsunto;
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
	 * @return the fechaRegistroPadreAsunto
	 */
	public Date getFechaRegistroPadreAsunto() {
		return fechaRegistroPadreAsunto;
	}

	/**
	 * @param fechaRegistroPadreAsunto the fechaRegistroPadreAsunto to set
	 */
	public void setFechaRegistroPadreAsunto(Date fechaRegistroPadreAsunto) {
		this.fechaRegistroPadreAsunto = fechaRegistroPadreAsunto;
	}

	/**
	 * @return the folioAreaAsuntoPadreAsunto
	 */
	public String getFolioAreaAsuntoPadreAsunto() {
		return folioAreaAsuntoPadreAsunto;
	}

	/**
	 * @param folioAreaAsuntoPadreAsunto the folioAreaAsuntoPadreAsunto to set
	 */
	public void setFolioAreaAsuntoPadreAsunto(String folioAreaAsuntoPadreAsunto) {
		this.folioAreaAsuntoPadreAsunto = folioAreaAsuntoPadreAsunto;
	}

	/**
	 * @return the areaPadreAsunto
	 */
	public String getAreaPadreAsunto() {
		return areaPadreAsunto;
	}

	/**
	 * @param areaPadreAsunto the areaPadreAsunto to set
	 */
	public void setAreaPadreAsunto(String areaPadreAsunto) {
		this.areaPadreAsunto = areaPadreAsunto;
	}

	/**
	 * @return the firmanteCargoPadreAsunto
	 */
	public String getFirmanteCargoPadreAsunto() {
		return firmanteCargoPadreAsunto;
	}

	/**
	 * @param firmanteCargoPadreAsunto the firmanteCargoPadreAsunto to set
	 */
	public void setFirmanteCargoPadreAsunto(String firmanteCargoPadreAsunto) {
		this.firmanteCargoPadreAsunto = firmanteCargoPadreAsunto;
	}

	/**
	 * @return the firmanteAsuntoPadreAsunto
	 */
	public String getFirmanteAsuntoPadreAsunto() {
		return firmanteAsuntoPadreAsunto;
	}

	/**
	 * @param firmanteAsuntoPadreAsunto the firmanteAsuntoPadreAsunto to set
	 */
	public void setFirmanteAsuntoPadreAsunto(String firmanteAsuntoPadreAsunto) {
		this.firmanteAsuntoPadreAsunto = firmanteAsuntoPadreAsunto;
	}

	/**
	 * @return the asuntoDescripcionPadreAsunto
	 */
	public String getAsuntoDescripcionPadreAsunto() {
		return asuntoDescripcionPadreAsunto;
	}

	/**
	 * @param asuntoDescripcionPadreAsunto the asuntoDescripcionPadreAsunto to set
	 */
	public void setAsuntoDescripcionPadreAsunto(String asuntoDescripcionPadreAsunto) {
		this.asuntoDescripcionPadreAsunto = asuntoDescripcionPadreAsunto;
	}

	/**
	 * @return the tipoAsuntoPadreAsunto
	 */
	public TipoAsunto getTipoAsuntoPadreAsunto() {
		return tipoAsuntoPadreAsunto;
	}

	/**
	 * @param tipoAsuntoPadreAsunto the tipoAsuntoPadreAsunto to set
	 */
	public void setTipoAsuntoPadreAsunto(TipoAsunto tipoAsuntoPadreAsunto) {
		this.tipoAsuntoPadreAsunto = tipoAsuntoPadreAsunto;
	}

	/**
	 * @return the promotorPadreAsunto
	 */
	public String getPromotorPadreAsunto() {
		return promotorPadreAsunto;
	}

	/**
	 * @param promotorPadreAsunto the promotorPadreAsunto to set
	 */
	public void setPromotorPadreAsunto(String promotorPadreAsunto) {
		this.promotorPadreAsunto = promotorPadreAsunto;
	}

	/**
	 * @return the statusAsuntoPadreAsunto
	 */
	public String getStatusAsuntoPadreAsunto() {
		return statusAsuntoPadreAsunto;
	}

	/**
	 * @param statusAsuntoPadreAsunto the statusAsuntoPadreAsunto to set
	 */
	public void setStatusAsuntoPadreAsunto(String statusAsuntoPadreAsunto) {
		this.statusAsuntoPadreAsunto = statusAsuntoPadreAsunto;
	}

	/**
	 * @return the remitentePadreAsunto
	 */
	public String getRemitentePadreAsunto() {
		return remitentePadreAsunto;
	}

	/**
	 * @param remitentePadreAsunto the remitentePadreAsunto to set
	 */
	public void setRemitentePadreAsunto(String remitentePadreAsunto) {
		this.remitentePadreAsunto = remitentePadreAsunto;
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
	 * @return the firmanteCargoAsunto
	 */
	public String getFirmanteCargoAsunto() {
		return firmanteCargoAsunto;
	}

	/**
	 * @param firmanteCargoAsunto the firmanteCargoAsunto to set
	 */
	public void setFirmanteCargoAsunto(String firmanteCargoAsunto) {
		this.firmanteCargoAsunto = firmanteCargoAsunto;
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
	 * @return the remitenteAsunto
	 */
	public String getRemitenteAsunto() {
		return remitenteAsunto;
	}

	/**
	 * @param remitenteAsunto the remitenteAsunto to set
	 */
	public void setRemitenteAsunto(String remitenteAsunto) {
		this.remitenteAsunto = remitenteAsunto;
	}

	/**
	 * @return the documentosAdjuntosAsunto
	 */
	public Integer getDocumentosAdjuntosAsunto() {
		return documentosAdjuntosAsunto;
	}

	/**
	 * @param documentosAdjuntosAsunto the documentosAdjuntosAsunto to set
	 */
	public void setDocumentosAdjuntosAsunto(Integer documentosAdjuntosAsunto) {
		this.documentosAdjuntosAsunto = documentosAdjuntosAsunto;
	}

	/**
	 * @return the promotorAsunto
	 */
	public String getPromotorAsunto() {
		return promotorAsunto;
	}

	/**
	 * @param promotorAsunto the promotorAsunto to set
	 */
	public void setPromotorAsunto(String promotorAsunto) {
		this.promotorAsunto = promotorAsunto;
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
	 * @return the idTipoRegistroPadreAsunto
	 */
	public String getIdTipoRegistroPadreAsunto() {
		return idTipoRegistroPadreAsunto;
	}

	/**
	 * @param idTipoRegistroPadreAsunto the idTipoRegistroPadreAsunto to set
	 */
	public void setIdTipoRegistroPadreAsunto(String idTipoRegistroPadreAsunto) {
		this.idTipoRegistroPadreAsunto = idTipoRegistroPadreAsunto;
	}

	/**
	 * @return the nombreTurnadorPadre
	 */
	public String getNombreTurnadorPadre() {
		return nombreTurnadorPadre;
	}

	/**
	 * @param nombreTurnadorPadre the nombreTurnadorPadre to set
	 */
	public void setNombreTurnadorPadre(String nombreTurnadorPadre) {
		this.nombreTurnadorPadre = nombreTurnadorPadre;
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
	 * @return the tipo_asunto
	 */
	public String getTipo_asunto() {
		return tipo_asunto;
	}

	/**
	 * @param tipo_asunto the tipo_asunto to set
	 */
	public void setTipo_asunto(String tipo_asunto) {
		this.tipo_asunto = tipo_asunto;
	}

	/**
	 * @return the requiereRespuestaAsunto
	 */
	public Boolean getRequiereRespuestaAsunto() {
		return requiereRespuestaAsunto;
	}

	/**
	 * @param requiereRespuestaAsunto the requiereRespuestaAsunto to set
	 */
	public void setRequiereRespuestaAsunto(Boolean requiereRespuestaAsunto) {
		this.requiereRespuestaAsunto = requiereRespuestaAsunto;
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
	 * @return the tipoRespuestaDescripcion
	 */
	public String getTipoRespuestaDescripcion() {
		return tipoRespuestaDescripcion;
	}

	/**
	 * @param tipoRespuestaDescripcion the tipoRespuestaDescripcion to set
	 */
	public void setTipoRespuestaDescripcion(String tipoRespuestaDescripcion) {
		this.tipoRespuestaDescripcion = tipoRespuestaDescripcion;
	}

	/**
	 * @return the tipoRespuestaId
	 */
	public String getTipoRespuestaId() {
		return tipoRespuestaId;
	}

	/**
	 * @param tipoRespuestaId the tipoRespuestaId to set
	 */
	public void setTipoRespuestaId(String tipoRespuestaId) {
		this.tipoRespuestaId = tipoRespuestaId;
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
	 * @return the areaDestinoDescripcion
	 */
	public String getAreaDestinoDescripcion() {
		return areaDestinoDescripcion;
	}

	/**
	 * @param areaDestinoDescripcion the areaDestinoDescripcion to set
	 */
	public void setAreaDestinoDescripcion(String areaDestinoDescripcion) {
		this.areaDestinoDescripcion = areaDestinoDescripcion;
	}

	/**
	 * @return the areaDestinoInstitucionId
	 */
	public Integer getAreaDestinoInstitucionId() {
		return areaDestinoInstitucionId;
	}

	/**
	 * @param areaDestinoInstitucionId the areaDestinoInstitucionId to set
	 */
	public void setAreaDestinoInstitucionId(Integer areaDestinoInstitucionId) {
		this.areaDestinoInstitucionId = areaDestinoInstitucionId;
	}

	/**
	 * @return the areaDestinoTitularId
	 */
	public String getAreaDestinoTitularId() {
		return areaDestinoTitularId;
	}

	/**
	 * @param areaDestinoTitularId the areaDestinoTitularId to set
	 */
	public void setAreaDestinoTitularId(String areaDestinoTitularId) {
		this.areaDestinoTitularId = areaDestinoTitularId;
	}

	/**
	 * @return the areaDestinoTitularAreaId
	 */
	public Integer getAreaDestinoTitularAreaId() {
		return areaDestinoTitularAreaId;
	}

	/**
	 * @param areaDestinoTitularAreaId the areaDestinoTitularAreaId to set
	 */
	public void setAreaDestinoTitularAreaId(Integer areaDestinoTitularAreaId) {
		this.areaDestinoTitularAreaId = areaDestinoTitularAreaId;
	}

	/**
	 * @return the areaDestinoTitularAreaInstitucionId
	 */
	public Integer getAreaDestinoTitularAreaInstitucionId() {
		return areaDestinoTitularAreaInstitucionId;
	}

	/**
	 * @param areaDestinoTitularAreaInstitucionId the
	 *                                            areaDestinoTitularAreaInstitucionId
	 *                                            to set
	 */
	public void setAreaDestinoTitularAreaInstitucionId(Integer areaDestinoTitularAreaInstitucionId) {
		this.areaDestinoTitularAreaInstitucionId = areaDestinoTitularAreaInstitucionId;
	}

	/**
	 * @return the areaId
	 */
	public Integer getAreaId() {
		return areaId;
	}

	/**
	 * @param areaId the areaId to set
	 */
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	/**
	 * @return the areaDescripcion
	 */
	public String getAreaDescripcion() {
		return areaDescripcion;
	}

	/**
	 * @param areaDescripcion the areaDescripcion to set
	 */
	public void setAreaDescripcion(String areaDescripcion) {
		this.areaDescripcion = areaDescripcion;
	}

	/**
	 * @return the areaInstitucionId
	 */
	public Integer getAreaInstitucionId() {
		return areaInstitucionId;
	}

	/**
	 * @param areaInstitucionId the areaInstitucionId to set
	 */
	public void setAreaInstitucionId(Integer areaInstitucionId) {
		this.areaInstitucionId = areaInstitucionId;
	}

	/**
	 * @return the areaTitularId
	 */
	public String getAreaTitularId() {
		return areaTitularId;
	}

	/**
	 * @param areaTitularId the areaTitularId to set
	 */
	public void setAreaTitularId(String areaTitularId) {
		this.areaTitularId = areaTitularId;
	}

	/**
	 * @return the areaTitularAreaId
	 */
	public Integer getAreaTitularAreaId() {
		return areaTitularAreaId;
	}

	/**
	 * @param areaTitularAreaId the areaTitularAreaId to set
	 */
	public void setAreaTitularAreaId(Integer areaTitularAreaId) {
		this.areaTitularAreaId = areaTitularAreaId;
	}

	/**
	 * @return the areaTitularAreaInstitucionId
	 */
	public Integer getAreaTitularAreaInstitucionId() {
		return areaTitularAreaInstitucionId;
	}

	/**
	 * @param areaTitularAreaInstitucionId the areaTitularAreaInstitucionId to set
	 */
	public void setAreaTitularAreaInstitucionId(Integer areaTitularAreaInstitucionId) {
		this.areaTitularAreaInstitucionId = areaTitularAreaInstitucionId;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	/**
	 * 
	 * @param idAsuntoPadre
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	/**
	 * @return the fechaCompromisoPadreAsunto
	 */
	public Date getFechaCompromisoPadreAsunto() {
		return fechaCompromisoPadreAsunto;
	}

	/**
	 * @param fechaCompromisoPadreAsunto the fechaCompromisoPadreAsunto to set
	 */
	public void setFechaCompromisoPadreAsunto(Date fechaCompromisoPadreAsunto) {
		this.fechaCompromisoPadreAsunto = fechaCompromisoPadreAsunto;
	}

	/**
	 * @return the numDoctoPadreAsunto
	 */
	public String getNumDoctoPadreAsunto() {
		return numDoctoPadreAsunto;
	}

	/**
	 * @param numDoctoPadreAsunto the numDoctoPadreAsunto to set
	 */
	public void setNumDoctoPadreAsunto(String numDoctoPadreAsunto) {
		this.numDoctoPadreAsunto = numDoctoPadreAsunto;
	}

	/**
	 * @return the fechaElaboracionPadreAsunto
	 */
	public Date getFechaElaboracionPadreAsunto() {
		return fechaElaboracionPadreAsunto;
	}

	/**
	 * @param fechaElaboracionPadreAsunto the fechaElaboracionPadreAsunto to set
	 */
	public void setFechaElaboracionPadreAsunto(Date fechaElaboracionPadreAsunto) {
		this.fechaElaboracionPadreAsunto = fechaElaboracionPadreAsunto;
	}

	/**
	 * Gets the id asunto origen.
	 *
	 * @return the id asunto origen
	 */
	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	/**
	 * Sets the id asunto origen.
	 *
	 * @param idAsunto the new id asunto origen
	 */
	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
	}

	/**
	 * Obtiene el Identificador si el Asunto / Tramite es confidencial
	 * 
	 * @return Identificador si el Asunto / Tramite es confidencial
	 */
	public Boolean getConfidencial() {

		return confidencial;
	}

	/**
	 * Asigna el Identificador si el Asunto / Tramite es confidencial
	 * 
	 * @param confidencial Identificador si el Asunto / Tramite es confidencial
	 */
	public void setConfidencial(Boolean confidencial) {

		this.confidencial = confidencial;
	}

	/**
	 * 
	 * @return
	 */
	public String getAreaDestinoInstitucionDescripcion() {
		return AreaDestinoInstitucionDescripcion;
	}

	/**
	 * 
	 * @param areaDestinoInstitucionDescripcion
	 */
	public void setAreaDestinoInstitucionDescripcion(String areaDestinoInstitucionDescripcion) {
		AreaDestinoInstitucionDescripcion = areaDestinoInstitucionDescripcion;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAreaInstitucionDescripcion() {
		return areaInstitucionDescripcion;
	}

	/**
	 * 
	 * @param areaDestinoInstitucionDescripcion
	 */
	public void setAreaInstitucionDescripcion(String areaInstitucionDescripcion) {
		this.areaInstitucionDescripcion = areaInstitucionDescripcion;
	}

	/**
	 * @return the bandRespRech
	 */
	public boolean isBandRespRech() {
		return bandRespRech;
	}

	/**
	 * @param bandRespRech the bandRespRech to set
	 */
	public void setBandRespRech(boolean bandRespRech) {
		this.bandRespRech = bandRespRech;
	}

	/**
	 * @return the nombreFirmante
	 */
	public String getNombreFirmante() {
		return NombreFirmante;
	}

	/**
	 * @param nombreFirmante the nombreFirmante to set
	 */
	public void setNombreFirmante(String nombreFirmante) {
		NombreFirmante = nombreFirmante;
	}	

	public String getAreaTitularNombrecompleto() {
		return areaTitularNombrecompleto;
	}

	public void setAreaTitularNombrecompleto(String areaTitularNombrecompleto) {
		this.areaTitularNombrecompleto = areaTitularNombrecompleto;
	}

	public String getAreaInstitucionAbreviatura() {
		return areaInstitucionAbreviatura;
	}

	public void setAreaInstitucionAbreviatura(String areaInstitucionAbreviatura) {
		this.areaInstitucionAbreviatura = areaInstitucionAbreviatura;
	}

	public Area2 getArea() {
		return area;
	}

	public void setArea(Area2 area) {
		this.area = area;
	}

	@Override
	public String toString() {
		return "RespuestaConsulta [idRespuesta=" + idRespuesta + ", idAsunto=" + idAsunto + ", NombreFirmante=" + NombreFirmante 
				+ ", tipoAsunto=" + tipoAsunto + ", comentarioAsunto=" + comentarioAsunto 
				+ ", fechaEnvioAsunto=" + fechaEnvioAsunto + ", fechaAcuseAsunto=" + fechaAcuseAsunto + ", enTiempo=" + enTiempo
				+ ", instruccionDescripcionAsunto=" + instruccionDescripcionAsunto + ", prioridadDescripcionAsunto="
				+ prioridadDescripcionAsunto + ", especialsnAsunto=" + especialsnAsunto + ", folioAreaAsunto="
				+ folioAreaAsunto + ", folioAreaAsuntoPadreAsunto=" + folioAreaAsuntoPadreAsunto
				+ ", fechaRegistroPadreAsunto=" + fechaRegistroPadreAsunto + ", asuntoDescripcionPadreAsunto="
				+ asuntoDescripcionPadreAsunto + ", firmanteAsuntoPadreAsunto=" + firmanteAsuntoPadreAsunto
				+ ", firmanteCargoPadreAsunto=" + firmanteCargoPadreAsunto + ", areaPadreAsunto=" + areaPadreAsunto
				+ ", promotorPadreAsunto=" + promotorPadreAsunto + ", tipoAsuntoPadreAsunto=" + tipoAsuntoPadreAsunto
				+ ", remitentePadreAsunto=" + remitentePadreAsunto + ", statusAsuntoPadreAsunto="
				+ statusAsuntoPadreAsunto + ", fechaElaboracionAsunto=" + fechaElaboracionAsunto
				+ ", asuntoDescripcionAsunto=" + asuntoDescripcionAsunto + ", firmanteCargoAsunto="
				+ firmanteCargoAsunto + ", remitenteAsunto=" + remitenteAsunto + ", subTipoAsunto=" + subTipoAsunto
				+ ", fechaCompromisoAsunto=" + fechaCompromisoAsunto + ", palabraClaveAsunto=" + palabraClaveAsunto
				+ ", tipo_asunto=" + tipo_asunto + ", idTipoRegistroAsunto=" + idTipoRegistroAsunto
				+ ", documentosAdjuntosAsunto=" + documentosAdjuntosAsunto + ", promotorAsunto=" + promotorAsunto
				+ ", idTipoRegistroPadreAsunto=" + idTipoRegistroPadreAsunto + ", nombreTurnadorPadre="
				+ nombreTurnadorPadre + ", statusAsunto=" + statusAsunto + ", fechaRegistroAsunto="
				+ fechaRegistroAsunto + ", requiereRespuestaAsunto=" + requiereRespuestaAsunto
				+ ", fechaRecepcionAsunto=" + fechaRecepcionAsunto + ", numDoctoAsunto=" + numDoctoAsunto
				+ ", idAsuntoPadre=" + idAsuntoPadre + ", fechaCompromisoPadreAsunto=" + fechaCompromisoPadreAsunto
				+ ", numDoctoPadreAsunto=" + numDoctoPadreAsunto + ", fechaElaboracionPadreAsunto="
				+ fechaElaboracionPadreAsunto + ", fechaRegistro=" + fechaRegistro + ", fechaEnvio=" + fechaEnvio
				+ ", fechaAcuse=" + fechaAcuse + ", comentario=" + comentario + ", comentarioRechazo="
				+ comentarioRechazo + ", porcentaje=" + porcentaje + ", tipoRespuestaDescripcion="
				+ tipoRespuestaDescripcion + ", tipoRespuestaId=" + tipoRespuestaId + ", status=" + status
				+ ", areaDestinoId=" + areaDestinoId + ", areaDestinoDescripcion=" + areaDestinoDescripcion
				+ ", areaDestinoInstitucionId=" + areaDestinoInstitucionId + ", areaDestinoTitularId="
				+ areaDestinoTitularId + ", areaDestinoTitularAreaId=" + areaDestinoTitularAreaId
				+ ", areaDestinoTitularAreaInstitucionId=" + areaDestinoTitularAreaInstitucionId + ", empresaDestino="
				+ empresaDestino + ", areaId=" + areaId + ", areaDescripcion=" + areaDescripcion
				+ ", areaInstitucionId=" + areaInstitucionId + ", areaTitularId=" + areaTitularId
				+ ", areaTitularAreaId=" + areaTitularAreaId + ", areaTitularAreaInstitucionId="
				+ areaTitularAreaInstitucionId + ", atributos=" + atributos + ", folioRespuesta=" + folioRespuesta
				+ ", infomexZip=" + infomexZip + ", documentosAdjuntos=" + documentosAdjuntos + ", acepto_rechazo_id="
				+ acepto_rechazo_id + ", acepto_rechazo_nombrecompleto=" + acepto_rechazo_nombrecompleto + ", idAsuntoOrigen="
				+ idAsuntoOrigen + ", confidencial=" + confidencial + "]";
	}

}
