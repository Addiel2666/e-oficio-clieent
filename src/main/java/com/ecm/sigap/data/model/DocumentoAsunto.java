/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.StatusFirmaDocumentoConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Clase de Identidad que representa la tabla DOCUMENTOSASUNTOS
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Access(AccessType.FIELD)
@IdClass(DocumentoAsuntoKey.class)
@Entity
@Table(name = "documentosAsuntos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@NamedNativeQueries({

		@NamedNativeQuery(name = "delDocAsuHasRefsTramite", //
				query = " select count(r_object_id) from {SIGAP_SCHEMA}.DOCUMENTOSASUNTOS where idasunto != :idAsunto "
						+ " and upper(r_object_id) = upper(:objectId)"),

		@NamedNativeQuery(name = "delDocAsuHasRefsFirma", //
				query = " select count(r_object_id) from  {SIGAP_SCHEMA}.DOCUMENTOSASUNTOS where status = 'P' and idasunto = :idAsunto "
						+ " and upper(r_object_id) = upper(:objectId)"),

		@NamedNativeQuery(name = "delDocAsuHasRefsAntefirma", //
				query = " select count(objectId) from {SIGAP_SCHEMA}.DOCUMENTOSANTEFIRMA where FIRMADOSN != 'S' "
						+ " and upper(objectId) = upper(:objectId)")

})
public final class DocumentoAsunto extends Documento implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2303004854481159840L;

	/** Identificador del Asunto asociado al documento */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** asuntoConsulta. */
	@OneToOne
	@JoinColumn(name = "idAsunto", insertable = false, updatable = false)
	@Fetch(FetchMode.SELECT)
	private AsuntoConsultaEspecial asuntoConsulta;

	/** Area a la que pertenece el archivo. */
	@Column(name = "idArea")
	private Integer idArea;

	/** Documento gubernamental SI/NO */
	@Column(name = "docgubernamentalsn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean gubernamental;

	/** status de firma del archivo */
	@Column(name = "status", length = 1)
	@Convert(converter = StatusFirmaDocumentoConverter.class)
	private StatusFirmaDocumento status;

	/** */
	@Column(name = "r_object_id_archivo")
	private String archivoId;

	/** */
	@Column(name = "path_published")
	private String pathPublished;

	/** */
	@Column(name = "status_cancelacion")
	private String statusCancelacion;

	/** */
	@Column(name = "motivo_cancelacion")
	private String motivoCancelacion;

	/** */
	@Column(name = "fecha_cancelacion")
	@Type(type = "java.util.Date")
	private Date fechaCancelacion;

	/** */
	@Column(name = "idacta_cancelacion")
	private String idActaCancelacion;
	
	/** */
	@Column(name = "fecha_marca")
	private Date fechaMarca;
	
	/** Id en el repositorio del documento original */
	@Column(name = "object_id_origen")
    private String objectIdOrigen;
    
    /** Version publica SI/NO */
    @Column(name = "public_version")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean publicVersion;
    
    /** habilitado para enviar SI/NO */
    @Column(name = "enabled_to_send")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean enabledToSend ;
    
    /** Campo importante para Bitácora*/
    @Transient
    private Integer idOrigen;
    
    /** Auxiliar check versionar version publica */
    @Transient
	private boolean versionable;

	public Integer getIdOrigen() {
		return idOrigen;
	}

	public void setIdOrigen(Integer idOrigen) {
		this.idOrigen = idOrigen;
	}

	/** */
	@Override
	@Id
	@Access(AccessType.PROPERTY)
	@Column(name = "r_object_id")
	public String getObjectId() {

		return super.getObjectId();
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
	 * @return the gubernamental
	 */
	public Boolean getGubernamental() {
		return gubernamental;
	}

	/**
	 * @param gubernamental the gubernamental to set
	 */
	public void setGubernamental(Boolean gubernamental) {
		this.gubernamental = gubernamental;
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
	 * Gets the asunto consulta.
	 *
	 * @return the asunto consulta
	 */
	public AsuntoConsultaEspecial getAsuntoConsulta() {
		return asuntoConsulta;
	}

	/**
	 * Sets the asunto consulta.
	 *
	 * @param asuntoConsulta the new asunto consulta
	 */
	public void setAsuntoConsulta(AsuntoConsultaEspecial asuntoConsulta) {
		this.asuntoConsulta = asuntoConsulta;
	}

	public String getArchivoId() {
		return archivoId;
	}

	public void setArchivoId(String archivoId) {
		this.archivoId = archivoId;
	}

	public String getPathPublished() {
		return pathPublished;
	}

	public void setPathPublished(String pathPublished) {
		this.pathPublished = pathPublished;
	}

	public String getStatusCancelacion() {
		return statusCancelacion;
	}

	public void setStatusCancelacion(String statusCancelacion) {
		this.statusCancelacion = statusCancelacion;
	}

	public String getMotivoCancelacion() {
		return motivoCancelacion;
	}

	public void setMotivoCancelacion(String motivoCancelacion) {
		this.motivoCancelacion = motivoCancelacion;
	}

	public Date getFechaCancelacion() {
		return fechaCancelacion;
	}

	public void setFechaCancelacion(Date fechaCancelacion) {
		this.fechaCancelacion = fechaCancelacion;
	}

	public Date getFechaMarca() {
		return fechaMarca;
	}

	public void setFechaMarca(Date fechaMarca) {
		this.fechaMarca = fechaMarca;
	}

	public String getIdActaCancelacion() {
		return idActaCancelacion;
	}

	public void setIdActaCancelacion(String idActaCancelacion) {
		this.idActaCancelacion = idActaCancelacion;
	}
	   
	public String getObjectIdOrigen() {
        return objectIdOrigen;
    }

    public void setObjectIdOrigen(String objectIdOrigen) {
        this.objectIdOrigen = objectIdOrigen;
    }

    public Boolean getPublicVersion() {
        return publicVersion;
    }

    public void setPublicVersion(Boolean publicVersion) {
        this.publicVersion = publicVersion;
    }

    public Boolean getEnabledToSend() {
        return enabledToSend;
    }

    public void setEnabledToSend(Boolean enabledToSend) {
        this.enabledToSend = enabledToSend;
    }

    /**
	 * @return the versionable
	 */
	public boolean isVersionable() {
		return versionable;
	}

	/**
	 * @param versionable the versionable to set
	 */
	public void setVersionable(boolean versionable) {
		this.versionable = versionable;
	}

	@Override
	@JsonIgnore
	public String getId() {
		return idAsunto + getObjectId();
	}

	@Override
	@JsonIgnore
	public String getLogDeatil() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Documento asunto").append("<br>")
		.append("Nombre: ").append(super.getObjectName()).append("<br>")
		.append("Dueño: ").append(super.getOwnerName()).append("<br>")
		.append("Estatus: ").append(status).append("<br>")
		.append("Fecha registro: ").append(super.getFechaRegistro()).append("<br>")
		.append("Versión: ").append(super.getVersion()).append("<br>")
		.append("Id asunto: ").append(idAsunto).append("<br>")
		.append("Id archivo: ").append(archivoId).append("<br>")
		.append("Id object: ").append(getObjectId()).append("<br>")
		.append("Id Area: ").append(idArea);
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return "DocumentoAsunto [idAsunto=" + idAsunto + ", asuntoConsulta=" + asuntoConsulta + ", idArea=" + idArea
				+ ", gubernamental=" + gubernamental + ", status=" + status + ", archivoId=" + archivoId
				+ ", pathPublished=" + pathPublished + ", statusCancelacion=" + statusCancelacion
				+ ", motivoCancelacion=" + motivoCancelacion + ", fechaCancelacion=" + fechaCancelacion
				+ ", idActaCancelacion=" + idActaCancelacion + ", fechaMarca=" + fechaMarca
				+ ", getFechaRegistro()=" + getFechaRegistro()
				+ ", getFileB64()=" + getFileB64() + ", getObjectName()=" + getObjectName() + ", getParentContentId()="
				+ getParentContentId() + ", getOwnerName()=" + getOwnerName() + ", isCheckout()=" + isCheckout()
				+ ", isVersionable()=" + isVersionable() + ", getVersion()=" + getVersion() + "]";
	}
	

}
