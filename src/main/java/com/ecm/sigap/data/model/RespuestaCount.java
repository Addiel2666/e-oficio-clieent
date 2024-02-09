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
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * The Class Respuesta.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "RESPUESTACONSULTAR_FULL")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RespuestaCount implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -511136033877436305L;

	/** Identificador de la Respuesta. */
	@Id
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Identificador del Asunto al que se responde. */
	@Column(name = "idAsunto")
	private Integer idAsunto;
	
	/** Tipo de Asunto {@link com.ecm.sigap.data.model.util.TipoAsunto} */
	@Column(name = "ASUNTO_IDTIPOASUNTO")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** Fecha de registro de la Respuesta. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** Comentario de la Respuesta. */
	@Column(name = "comentario")
	private String comentario;

	/** Comentario de rechazo. */
	@Column(name = "comentariorechazo")
	private String comentarioRechazo;

	/** */
	@Column(name = "tipoRespuestaId")
	private String tipoRespuestaId;

	/** Estatus de la Respuesta. */
	@OneToOne
	@JoinColumn(name = "idEstatusRespuesta", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Status status;

	/** Identificador del Folio de la Respuesta. */
	@Column(name = "folioRespuesta")
	private String folioRespuesta;
	
	/** */
	@Column(name = "areaId")
	private Integer areaId;

	/** */
	@Column(name = "areaDestinoId")
	private Integer areaDestinoId;
	
	/** */
    @Column(name = "AreaDestinoInstitucionId")
    private Integer areaDestinoInstitucionId;
    
    @Column(name = "areaInstitucionId")
    private Integer areaInstitucionId;

	/** The folio area. */
	@Column(name = "ASUNTO_FOLIOAREA")
	private String folioAreaAsunto;
	
	/** The folio area asunto padre. */
	@Column(name = "ASUNTO_folioAreaAsuntoPadre")
	private String folioAreaAsuntoPadreAsunto;

	/** id asunto origen */
	@Column(name = "asunto_idAsuntoOrigen")
	private Integer idAsuntoOrigen;

	/** */
	@Column(name = "areaDestinoTitularId")
	private String areaDestinoTitularId;

	/** */
	@Column(name = "areaDestinoTitularAreaId")
	private Integer areaDestinoTitularAreaId;

	/** */
	@Column(name = "areaDestTitularAreaInstId")
	private Integer areaDestinoTitularAreaInstitucionId;	

	/** */
	@Column(name = "areaTitularId")
	private String areaTitularId;

	/** */
	@Column(name = "areaTitularAreaId")
	private Integer areaTitularAreaId;

	/** */
	@Column(name = "areaTitularAreaInstitucionId")
	private Integer areaTitularAreaInstitucionId;

	/**  */
	@Transient
	private Area2 area;
	
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
	 * @param areaDestinoTitularAreaInstitucionId the areaDestinoTitularAreaInstitucionId to set
	 */
	public void setAreaDestinoTitularAreaInstitucionId(Integer areaDestinoTitularAreaInstitucionId) {
		this.areaDestinoTitularAreaInstitucionId = areaDestinoTitularAreaInstitucionId;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Respuesta [idRespuesta=" + idRespuesta + ", idAsunto=" + idAsunto + ", fechaRegistro=" + fechaRegistro
				+ ", comentario=" + comentario + ", comentarioRechazo=" + comentarioRechazo + ", tipoRespuestaId="
				+ tipoRespuestaId + ", status=" + status + ", folioRespuesta=" + folioRespuesta + 
				", folioAreaAsunto=" + folioAreaAsunto + ", idAsuntoOrigen=" + idAsuntoOrigen +"]";
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

    public Integer getAreaDestinoInstitucionId() {
        return areaDestinoInstitucionId;
    }

    public void setAreaDestinoInstitucionId(Integer areaDestinoInstitucionId) {
        this.areaDestinoInstitucionId = areaDestinoInstitucionId;
    }

    public Integer getAreaInstitucionId() {
        return areaInstitucionId;
    }

    public void setAreaInstitucionId(Integer areaInstitucionId) {
        this.areaInstitucionId = areaInstitucionId;
    }

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Area2 getArea() {
		return area;
	}

	public void setArea(Area2 area) {
		this.area = area;
	}

}
