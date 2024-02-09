/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class Institucion.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "instituciones", //
		schema = "{SIGAP_SCHEMA}")
@SequenceGenerator(name = "SEQ_INSTITUCIONES", //
		sequenceName = "SECINSTITUC", //
		allocationSize = 1, //
		schema = "{SIGAP_SCHEMA}")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "descripcion" }, message = "{Unique.descripcion}")
public final class Institucion implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1645912272382899434L;

	/** The id institucion. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INSTITUCIONES")
	@Column(name = "idInstitucion")
	private Integer idInstitucion;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The abreviatura. */
	@Column(name = "abreviatura")
	private String abreviatura;

	/** The clave. */
	@Column(name = "clavecdd")
	private String clave;

	/** The tipo. */
	@Column(name = "idTipoInstitucion")
	private String tipo;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** The interopera. */
	@Column(name = "interoperasn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean interopera;

	/** The uri. */
	@Column(name = "uri")
	private String uri;

	/** The endpoint. */
	@Column(name = "endpoint")
	private String endpoint;

	/** The id externo. */
	@Column(name = "idExterno")
	private String idExterno;
	
	@Transient
	private String activeInactive;
	
	/** */
	@Transient
	private boolean notEmpresa;
	
	/** */
	@Transient
	private boolean notCiudadano;

	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the id institucion.
	 *
	 * @return the idInstitucion
	 */
	public Integer getIdInstitucion() {
		return idInstitucion;
	}

	/**
	 * Sets the id institucion.
	 *
	 * @param idInstitucion the idInstitucion to set
	 */
	public void setIdInstitucion(Integer idInstitucion) {
		this.idInstitucion = idInstitucion;
	}

	/**
	 * Gets the abreviatura.
	 *
	 * @return the abreviatura
	 */
	public String getAbreviatura() {
		return abreviatura;
	}

	/**
	 * Sets the abreviatura.
	 *
	 * @param abreviatura the abreviatura to set
	 */
	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	/**
	 * Gets the clave.
	 *
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * Sets the clave.
	 *
	 * @param clave the clave to set
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}

	/**
	 * Gets the interopera.
	 *
	 * @return the interopera
	 */
	public Boolean getInteropera() {
		return interopera;
	}

	/**
	 * Sets the interopera.
	 *
	 * @param interopera the interopera to set
	 */
	public void setInteropera(Boolean interopera) {
		this.interopera = interopera;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the endpoint.
	 *
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Sets the endpoint.
	 *
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Gets the tipo.
	 *
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Sets the tipo.
	 *
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Gets the activo.
	 *
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Sets the activo.
	 *
	 * @param activo the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Gets the id externo.
	 *
	 * @return the id externo
	 */
	public String getIdExterno() {
		return idExterno;
	}

	/**
	 * Sets the id externo.
	 *
	 * @param idExterno the new id externo
	 */
	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	public String getActiveInactive() {
		return activeInactive;
	}

	public void setActiveInactive(String activeInactive) {
		this.activeInactive = activeInactive;
	}
	
	/**
	 * @return the notEmpresa
	 */
	public boolean isNotEmpresa() {
		return notEmpresa;
	}

	/**
	 * @param notEmpresa the notEmpresa to set
	 */
	public void setNotEmpresa(boolean notEmpresa) {
		this.notEmpresa = notEmpresa;
	}

	/**
	 * @return the notCiudadano
	 */
	public boolean isNotCiudadano() {
		return notCiudadano;
	}

	/**
	 * @param notCiudadano the notCiudadano to set
	 */
	public void setNotCiudadano(boolean notCiudadano) {
		this.notCiudadano = notCiudadano;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Institucion [idInstitucion=" + idInstitucion + ", descripcion=" + descripcion + ", abreviatura="
				+ abreviatura + ", clave=" + clave + ", tipo=" + tipo + ", activo=" + activo + ", interopera="
				+ interopera + ", uri=" + uri + ", endpoint=" + endpoint + ", idExterno=" + idExterno + "]";
	}

	@Override
	@JsonIgnore
	public String getId() {
		return String.valueOf(this.idInstitucion);
	}

	@Override
	@JsonIgnore
	public String getLogDeatil() {
		StringBuilder sb = new StringBuilder();
		sb.append("Institución ").append("I".equals(tipo) ? "interna" : "externa").append("<br>")
		.append("Institución: ").append(descripcion).append("<br>")
		.append("Tipo: ").append(tipo).append("<br>")
		.append("Abreviatura: ").append( (getAbreviatura ()!= null) ? abreviatura : "null" ).append("<br>")
		.append("Clave: ").append( (getClave ()!= null) ? clave : "null");

		return sb.toString();
	}

}
