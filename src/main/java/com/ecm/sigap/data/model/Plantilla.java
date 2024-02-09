/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.TipoPlantilla;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "plantillas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_PLANTILLAS", sequenceName = "SECPLANTILLAS", allocationSize = 1)
public class Plantilla implements Serializable {

	/**  */
	private static final long serialVersionUID = -1511081456930786344L;

	/** */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PLANTILLAS")
	@Column(name = "id")
	private Integer id;

	/** */
	@Column(name = "objectId")
	private String objectId;

	/** */
	@Column(name = "nombre")
	private String nombre;

	/** */
	@Column(name = "tipo")
	@Enumerated(EnumType.ORDINAL)
	private TipoPlantilla tipo;

	/** */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** */
	@Column(name = "idOwner")
	private String idOwner;

	/** Cadena en base64 con el contenido del archivo */
	@Transient
	private String fileB64;

	/** Id del folder donde esta alamacenada. */
	@Transient
	private String parentId;

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
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the tipo
	 */
	public TipoPlantilla getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(TipoPlantilla tipo) {
		this.tipo = tipo;
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
	 * @return the idOwner
	 */
	public String getIdOwner() {
		return idOwner;
	}

	/**
	 * @param idOwner
	 *            the idOwner to set
	 */
	public void setIdOwner(String idOwner) {
		this.idOwner = idOwner;
	}

	/**
	 * @return the fileB64
	 */
	public String getFileB64() {
		return fileB64;
	}

	/**
	 * @param fileB64
	 *            the fileB64 to set
	 */
	public void setFileB64(String fileB64) {
		this.fileB64 = fileB64;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Plantilla [id=" + id + ", objectId=" + objectId + ", nombre=" + nombre + ", tipo=" + tipo
				+ ", fechaRegistro=" + fechaRegistro + ", idOwner=" + idOwner + "]";
	}

}
