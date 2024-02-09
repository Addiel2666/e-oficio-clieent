/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.util.CopiaRespuesta;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Respuesta.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "respuestas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_RESPUESTAS", sequenceName = "SECRESPUESTAS", allocationSize = 1)
@NamedNativeQueries({

		@NamedNativeQuery(name = "folioRespuestaExiste", //
				query = " select count(*) from {SIGAP_SCHEMA}.RESPUESTAS where upper(FOLIORESPUESTA) = upper(:folioRespuesta) "),

		@NamedNativeQuery(name = "noDoctoExisteRespuestas", //
				query = " select count(FOLIORESPUESTA) from {SIGAP_SCHEMA}.respuestas where FOLIORESPUESTA != 'S/N' and FOLIORESPUESTA is not null and FOLIORESPUESTA = :folioRespuesta ")

})
public class Respuesta implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -511136033877436305L;

	/** Identificador de la Respuesta */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESPUESTAS")
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Identificador del Asunto al que se responde */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** Fecha de registro de la Respuesta */
	@Column(name = "fechaRegistro", nullable = false)
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** Fecha de envio de la Respuesta */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** Fecha de acuse de recepcion de la Respuesta */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/** Comentario de la Respuesta */
	@Column(name = "comentario")
	private String comentario;

	/** Comentario de rechazo */
	@Column(name = "comentariorechazo")
	private String comentarioRechazo;

	/** Porcentaje de avance de la Respuesta */
	@Column(name = "porcentaje")
	private Integer porcentaje;

	/** Tipo de Respuesta */
	@OneToOne
	@JoinColumn(name = "idTipoRespuesta", nullable = false)
	@NotNull
	@NotEmpty
	@Fetch(FetchMode.SELECT)
	private TipoRespuesta tipoRespuesta;

	/** Estatus de la Respuesta */
	@OneToOne
	@JoinColumn(name = "idEstatusRespuesta", nullable = false)
	@Fetch(value = FetchMode.SELECT)
	private Status status;

	/** Area que genero el Asunto y a la cual se responde */
	@OneToOne(optional = true)
	@JoinColumn(name = "idAreaDestino")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Area areaDestino;

	/** Empresa que genera la Respuesta (Para el caso de Tramites Externos) */
	@OneToOne
	@JoinColumn(name = "idAreaDestino", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Empresa empresaDestino;

	/** Area que genera la Respuesta */
	@OneToOne(optional = true)
	@JoinColumn(name = "idArea")
	@Fetch(value = FetchMode.SELECT)
	private Area area;

	/** Atributos de la Respuesta */
	@Column(name = "atributos")
	private String atributos;

	/** Identificador del Folio de la Respuesta */
	@Column(name = "folioRespuesta")
	private String folioRespuesta;

	/**
	 * Indicador si la respuesta se va a cargar en el ZIP de Respuestas Infomex
	 */
	@Column(name = "infomexzipsn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean infomexZip;

	/** Acuses de recibo de la Respuesta */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "respuestatimestamps", joinColumns = { @JoinColumn(name = "idRespuesta") })
	@JoinColumn(name = "idRespuesta")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@JsonIgnore
	private List<Timestamp> timestamps;

	/** Copias de la Respuesta */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "copiasRespuesta", joinColumns = { @JoinColumn(name = "idRespuesta") })
	@JoinColumn(name = "idRespuesta")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@Enumerated(EnumType.STRING)
	private List<CopiaRespuesta> copias;

	/** cuenta cuantos documentos tiene la respuesta */
	@Formula("{SIGAP_SCHEMA}.RESPUESTAHASDOCS(idRespuesta)")
	private Integer documentosAdjuntos;

	/** */
	@JsonIgnore
	@Transient
	private Integer idFolioMultiple;

	/** Identificador del Usuario que acepta la respuesta */
	@OneToOne(fetch = FetchType.EAGER, targetEntity = Usuario.class)
	@JoinColumn(name = "acepto_rechazo_id", nullable = true, updatable = true)
	@Fetch(FetchMode.SELECT)
	private Usuario aceptoRespuesta;

	/**
	 * Obtiene el Identificador de la Respuesta
	 *
	 * @return Identificador de la Respuesta
	 */
	public Integer getIdRespuesta() {

		return idRespuesta;
	}

	/**
	 * Asigna el Identificador de la Respuesta
	 *
	 * @param idRespuesta Identificador de la Respuesta
	 */
	public void setIdRespuesta(Integer idRespuesta) {

		this.idRespuesta = idRespuesta;
	}

	/**
	 * Obtiene el Identificador del Asunto al que se responde
	 *
	 * @return Identificador del Asunto al que se responde
	 */
	public Integer getIdAsunto() {

		return idAsunto;
	}

	/**
	 * Obtiene el Identificador del Asunto al que se responde
	 *
	 * @param idAsunto Identificador del Asunto al que se responde
	 */
	public void setIdAsunto(Integer idAsunto) {

		this.idAsunto = idAsunto;
	}

	/**
	 * Obtiene la Fecha de registro de la Respuesta
	 *
	 * @return Fecha de registro de la Respuesta
	 */
	public Date getFechaRegistro() {

		return fechaRegistro;
	}

	/**
	 * Asigna la Fecha de registro de la Respuesta
	 *
	 * @param fechaRegistro Fecha de registro de la Respuesta
	 */
	public void setFechaRegistro(Date fechaRegistro) {

		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Obtiene la Fecha de envio de la Respuesta
	 *
	 * @return Fecha de envio de la Respuesta
	 */
	public Date getFechaEnvio() {

		return fechaEnvio;
	}

	/**
	 * Asigna la Fecha de envio de la Respuesta
	 *
	 * @param fechaEnvio Fecha de envio de la Respuesta
	 */
	public void setFechaEnvio(Date fechaEnvio) {

		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Obtiene la Fecha de acuse de recepcion de la Respuesta
	 *
	 * @return Fecha de acuse de recepcion de la Respuesta
	 */
	public Date getFechaAcuse() {

		return fechaAcuse;
	}

	/**
	 * Asigna la Fecha de acuse de recepcion de la Respuesta
	 *
	 * @param fechaAcuse Fecha de acuse de recepcion de la Respuesta
	 */
	public void setFechaAcuse(Date fechaAcuse) {

		this.fechaAcuse = fechaAcuse;
	}

	/**
	 * Obtiene el Comentario de la Respuesta
	 *
	 * @return Comentario de la Respuesta
	 */
	public String getComentario() {

		return comentario;
	}

	/**
	 * Asigna el Comentario de la Respuesta
	 *
	 * @param comentario Comentario de la Respuesta
	 */
	public void setComentario(String comentario) {

		this.comentario = comentario;
	}

	/**
	 * Obtiene el Comentario de rechazo
	 *
	 * @return Comentario de rechazo
	 */
	public String getComentarioRechazo() {

		return comentarioRechazo;
	}

	/**
	 * Asigna el Comentario de rechazo
	 *
	 * @param comentarioRechazo Comentario de rechazo
	 */
	public void setComentarioRechazo(String comentarioRechazo) {

		this.comentarioRechazo = comentarioRechazo;
	}

	/**
	 * Obtiene el Porcentaje de avance de la Respuesta
	 *
	 * @return Porcentaje de avance de la Respuesta
	 */
	public Integer getPorcentaje() {

		return porcentaje;
	}

	/**
	 * Asigna el Porcentaje de avance de la Respuesta
	 *
	 * @param porcentaje Porcentaje de avance de la Respuesta
	 */
	public void setPorcentaje(Integer porcentaje) {

		this.porcentaje = porcentaje;
	}

	/**
	 * Obtiene el Tipo de Respuesta
	 *
	 * @return Tipo de Respuesta
	 */
	public TipoRespuesta getTipoRespuesta() {

		return tipoRespuesta;
	}

	/**
	 * Asigna el Tipo de Respuesta
	 *
	 * @param tipoRespuesta Tipo de Respuesta
	 */
	public void setTipoRespuesta(TipoRespuesta tipoRespuesta) {

		this.tipoRespuesta = tipoRespuesta;
	}

	/**
	 * Obtiene el Estatus de la Respuesta
	 *
	 * @return Estatus de la Respuesta
	 */
	public Status getStatus() {

		return status;
	}

	/**
	 * Asigna el Estatus de la Respuesta
	 *
	 * @param status Estatus de la Respuesta
	 */
	public void setStatus(Status status) {

		this.status = status;
	}

	/**
	 * Obtiene el Area que genera la Respuesta
	 *
	 * @return Area que genera la Respuesta
	 */
	public Area getAreaDestino() {

		return areaDestino;
	}

	/**
	 * Asigna el Area que genera la Respuesta
	 *
	 * @param areaDestino Area que genera la Respuesta
	 */
	public void setAreaDestino(Area areaDestino) {

		this.areaDestino = areaDestino;
	}

	/**
	 * Obtiene la Empresa que genera la Respuesta (Para el caso de Tramites
	 * Externos)
	 * 
	 * @return Empresa que genera la Respuesta
	 */
	public Empresa getEmpresaDestino() {

		return empresaDestino;
	}

	/**
	 * Asigna la Empresa que genera la Respuesta (Para el caso de Tramites Externos)
	 * 
	 * @param empresaDestino Empresa que genera la Respuesta
	 */
	public void setEmpresaDestino(Empresa empresaDestino) {

		this.empresaDestino = empresaDestino;
	}

	/**
	 * Obtiene el Area que genero el Asunto y a la cual se responde
	 *
	 * @return Area que genero el Asunto y a la cual se responde
	 */
	public Area getArea() {

		return area;
	}

	/**
	 * Asigna el Area que genero el Asunto y a la cual se responde
	 *
	 * @param area Area que genero el Asunto y a la cual se responde
	 */
	public void setArea(Area area) {

		this.area = area;
	}

	/**
	 * Obtiene los Atributos de la Respuesta
	 *
	 * @return Atributos de la Respuesta
	 */
	public String getAtributos() {

		return atributos;
	}

	/**
	 * Asigna los Atributos de la Respuesta
	 *
	 * @param atributos Atributos de la Respuesta
	 */
	public void setAtributos(String atributos) {

		this.atributos = atributos;
	}

	/**
	 * Obtiene el Identificador del Folio de la Respuesta
	 *
	 * @return Identificador del Folio de la Respuesta
	 */
	public String getFolioRespuesta() {

		return folioRespuesta;
	}

	/**
	 * Asigna el Identificador del Folio de la Respuesta
	 *
	 * @param folioRespuesta Identificador del Folio de la Respuesta
	 */
	public void setFolioRespuesta(String folioRespuesta) {

		this.folioRespuesta = folioRespuesta;
	}

	/**
	 * Obtiene el Indicador si la respuesta se va a cargar en el ZIP de Respuestas
	 * Infomex
	 * 
	 * @return Indicador si la respuesta se va a cargar en el ZIP de Respuestas
	 *         Infomex
	 */
	public Boolean getInfomexZip() {

		return infomexZip;
	}

	/**
	 * Asigna el Indicador si la respuesta se va a cargar en el ZIP de Respuestas
	 * Infomex
	 * 
	 * @param infomexZip Indicador si la respuesta se va a cargar en el ZIP de
	 *                   Respuestas Infomex
	 */
	public void setInfomexZip(Boolean infomexZip) {

		this.infomexZip = infomexZip;
	}

	/**
	 * Obtiene los Acuses de recibo de la Respuesta
	 * 
	 * @return Acuses de recibo de la Respuesta
	 */
	public List<Timestamp> getTimestamps() {

		return timestamps;
	}

	/**
	 * Asigna los Acuses de recibo de la Respuesta
	 * 
	 * @param timestamps Acuses de recibo de la Respuesta
	 */
	public void setTimestamps(List<Timestamp> timestamps) {

		this.timestamps = timestamps;
	}

	/**
	 * Obtiene las Copias de la Respuesta
	 * 
	 * @return Copias de la Respuesta
	 */
	public List<CopiaRespuesta> getCopias() {

		return copias;
	}

	/**
	 * Asigna las Copias de la Respuesta
	 * 
	 * @param copias Copias de la Respuesta
	 */
	public void setCopias(List<CopiaRespuesta> copias) {

		this.copias = copias;
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
	 * @return the idFolioMultiple
	 */
	public Integer getIdFolioMultiple() {
		return idFolioMultiple;
	}

	/**
	 * @param idFolioMultiple the idFolioMultiple to set
	 */
	@JsonProperty("idFolioMultiple")
	public void setIdFolioMultiple(Integer idFolioMultiple) {
		this.idFolioMultiple = idFolioMultiple;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Respuesta [idRespuesta=" + idRespuesta + ", idAsunto=" + idAsunto + ", fechaRegistro=" + fechaRegistro
				+ ", fechaEnvio=" + fechaEnvio + ", fechaAcuse=" + fechaAcuse + ", comentario=" + comentario
				+ ", comentarioRechazo=" + comentarioRechazo + ", porcentaje=" + porcentaje + ", tipoRespuesta="
				+ tipoRespuesta + ", status=" + status + ", areaDestino=" + areaDestino + ", area=" + area
				+ ", atributos=" + atributos + ", folioRespuesta=" + folioRespuesta + "]";
	}

	@Override
	@JsonIgnore
	public String getId() {
		return String.valueOf(this.idRespuesta);
	}

	@Override
	@JsonIgnore
	public String getLogDeatil() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Respuesta").append("<br>")
		.append("Comentario: ").append(comentario).append("<br>")
		.append("Comentario rechazado: ").append(comentarioRechazo).append("<br>")
		.append("Fecha registro: ").append(fechaRegistro).append("<br>")
		.append("Tipo respuesta: ").append(tipoRespuesta).append("<br>")
		.append("Porcentaje: ").append(porcentaje).append("<br>")
		.append("Área: ").append( (area != null) ? area.getDescripcion() : "null").append("<br>")
		.append("Folio respuesta: ").append(folioRespuesta).append("<br>")
		.append("Institución: ").append( (area != null) ? ( (area.getInstitucion() != null) ? area.getInstitucion().getDescripcion() : "null") : "null").append("<br>")
		.append("Id asunto: ").append(idAsunto).append("<br>")
		.append("Id área: ").append((area != null) ? area.getIdArea() : "null").append("<br>")
		.append("Id área destino: ").append( (areaDestino != null) ? areaDestino.getIdArea() : "null").append("<br>")
		.append("Id institución: ").append( (area != null) ? ( (area.getInstitucion() != null) ? area.getInstitucion().getIdInstitucion() : "null") : "null").append("<br>")
		.append("Id respuesta: ").append(idRespuesta);
		// TODO
		// Crear uno para describir si la respuesta en generada, recibida, aceptada o rechazada

		return sb.toString();
	}

	public Usuario getAceptoRespuesta() {
		return aceptoRespuesta;
	}

	public void setAceptoRespuesta(Usuario aceptoRespuesta) {
		this.aceptoRespuesta = aceptoRespuesta;
	}

}
