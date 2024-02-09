/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

/**
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "archivos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_INFOMEX_ARCHIVO", sequenceName = "SECOBJETOS", allocationSize = 1)
public class InfomexArchivo implements Serializable {

	/**  */
	private static final long serialVersionUID = -8917727342079484117L;

	/**  */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INFOMEX_ARCHIVO")
	@Column(name = "idArchivo")
	private Integer id;

	/** */
	@Column(name = "nombreArchivo")
	private String nombreArchivo;

	/** */
	@Column(name = "idTipoArchivo")
	private String tipoArchivo;

	/** */
	@Column(name = "idSentido")
	private String idSentido;

	/**  */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/**  */
	@Column(name = "fechaProceso")
	@Type(type = "java.util.Date")
	private Date fechaProceso;

	/**  */
	@OneToOne
	@JoinColumn(name = "idEstatusArchivo")
	@Fetch(value = FetchMode.SELECT)
	private Status status;

	/** */
	@Column(name = "atributos")
	private String atributos;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the nombreArchivo
	 */
	public String getNombreArchivo() {
		return nombreArchivo;
	}

	/**
	 * @param nombreArchivo
	 *            the nombreArchivo to set
	 */
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	/**
	 * @return the fechaRegistro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * @param fechaRegistro
	 *            the fechaRegistro to set
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * @return the fechaProceso
	 */
	public Date getFechaProceso() {
		return fechaProceso;
	}

	/**
	 * @param fechaProceso
	 *            the fechaProceso to set
	 */
	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the atributos
	 */
	public String getAtributos() {
		return atributos;
	}

	/**
	 * @param atributos
	 *            the atributos to set
	 */
	public void setAtributos(String atributos) {
		this.atributos = atributos;
	}

	/**
	 * @return the tipoArchivo
	 */
	public String getTipoArchivo() {
		return tipoArchivo;
	}

	/**
	 * @param tipoArchivo
	 *            the tipoArchivo to set
	 */
	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}

	/**
	 * @return the idSentido
	 */
	public String getIdSentido() {
		return idSentido;
	}

	/**
	 * @param idSentido
	 *            the idSentido to set
	 */
	public void setIdSentido(String idSentido) {
		this.idSentido = idSentido;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfomexArchivo [id=" + id + ", nombreArchivo=" + nombreArchivo + ", fechaRegistro=" + fechaRegistro
				+ ", fechaProceso=" + fechaProceso + ", status=" + status + ", atributos=" + atributos + "]";
	}

}
