/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.util.StatusFirmaDocumentoConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "documentosRespuestas")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@NamedNativeQueries({

		@NamedNativeQuery(name = "delDocRespHasRefs", //
				query = " select count(*) from {SIGAP_SCHEMA}.DOCUMENTOSANTEFIRMA where upper(objectId) = upper(:objectId)")

})

public final class DocumentoRespuesta extends Documento implements Serializable, IAuditLog {

	/**  */
	private static final long serialVersionUID = -2911075707972951273L;

	/** ID del {@link Asunto} a la que pertenece. */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** ID de la {@link Respuesta} a la que pertenece. */
	@Column(name = "idRespuesta")
	private Integer idRespuesta;

	/** Area del dueño del documento, para aplicar ACL correspondiente. */
	@Column(name = "idArea")
	private Integer idArea;

	/** status de firma del archivo */
	@Column(name = "status")
	@Convert(converter = StatusFirmaDocumentoConverter.class)
	private StatusFirmaDocumento status;
	
	/** fecha de marcardo el documento */
	@Column(name = "fecha_marca")
	private Date fechaMarca;

	/** asuntoConsulta. */
	@Transient
	private AsuntoConsultaEspecial asuntoConsulta;

	/** */
	@OneToOne(targetEntity = RespuestaConsulta.class)
	@JoinColumn(name = "idRespuesta", referencedColumnName = "idRespuesta", insertable = false, updatable = false)
	@Fetch(FetchMode.SELECT)
	private RespuestaConsulta respuestaConsulta;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentoRespuesta [idAsunto=" + idAsunto + ", idRespuesta=" + idRespuesta + ", getObjectId()="
				+ getObjectId() + ", getFechaRegistro()=" + getFechaRegistro() + ", fechaMarca=" + fechaMarca + "]";
	}

	/**
	 * @return the status
	 */
	public StatusFirmaDocumento getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusFirmaDocumento status) {
		this.status = status;
	}
	
	/**
	 * @return the fechaMarca
	 */
	public Date getFechaMarca() {
		return fechaMarca;
	}

	/**
	 * @param fechaMarca the fechaMarca to set
	 */
	public void setFechaMarca(Date fechaMarca) {
		this.fechaMarca = fechaMarca;
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
	 * @return the asuntoConsulta
	 */
	public AsuntoConsultaEspecial getAsuntoConsulta() {
		return asuntoConsulta;
	}

	/**
	 * @param asuntoConsulta the asuntoConsulta to set
	 */
	public void setAsuntoConsulta(AsuntoConsultaEspecial asuntoConsulta) {
		this.asuntoConsulta = asuntoConsulta;
	}

	/**
	 * @return the respuestaConsulta
	 */
	public RespuestaConsulta getRespuestaConsulta() {
		return respuestaConsulta;
	}

	/**
	 * @param respuestaConsulta the respuestaConsulta to set
	 */
	public void setRespuestaConsulta(RespuestaConsulta respuestaConsulta) {
		this.respuestaConsulta = respuestaConsulta;
	}

	@Override
	@JsonIgnore
	public String getId() {
		return idAsunto + idRespuesta + getObjectId();
	}

	@Override
	@JsonIgnore
	public String getLogDeatil() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Documento respuesta").append("<br>")
		.append("Nombre: ").append(super.getObjectName()).append("<br>")
		.append("Dueño: ").append(super.getOwnerName()).append("<br>")
		.append("Estatus: ").append(status).append("<br>")
		.append("Fecha registro: ").append(super.getFechaRegistro()).append("<br>")
		.append("Versión: ").append(super.getVersion()).append("<br>")
		.append("Id asunto: ").append(idAsunto).append("<br>")
		.append("Id respuesta: ").append(idRespuesta).append("<br>")
		.append("Id object: ").append(getObjectId()).append("<br>")
		.append("Id Area: ").append(idArea);
		
		return sb.toString();
	}

}
