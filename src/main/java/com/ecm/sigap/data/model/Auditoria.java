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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * The Class Auditoria.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "auditoria")
@SequenceGenerator(name = "SEQ_AUDITORIA", sequenceName = "SECAUDITORIA", allocationSize = 1)
public final class Auditoria implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 383818017889021715L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AUDITORIA")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "id", nullable = false)
	private Integer id;

	/** The fecha registro. */
	@Column(name = "fechaRegistro", nullable = false, updatable = false)
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** The accion. */
	@Column(name = "accion", nullable = false)
	private String accion;

	/** The id entity. */
	@Column(name = "id_entity", nullable = false)
	private String idEntity;

	/** The nombre entity. */
	@Column(name = "nombreEntity", nullable = false)
	private String nombreEntity;

	/** The informacion. */
	@Column(name = "informacion", nullable = false)
	private String informacion;

	/** The id usuario. */
	@Column(name = "idUsuario", nullable = false)
	private String idUsuario;

	/** The tipo entity. */
	@Column(name = "tipoEntity")
	private String tipoEntity;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** */
	@Column(name = "Ip")
	private String Ip;
	
	/** */
	@Column(name = "NombreEquipo")
	private String NombreEquipo;
	
	@Column(name = "INTITUCIONID")
	private Integer institucionId;
	
	@Column(name = "origenId")
	private Integer origenId;

	public Integer getInstitucionId() {
		return institucionId;
	}

	public void setInstitucionId(Integer institucionId) {
		this.institucionId = institucionId;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @param fechaRegistro
	 *            the new fecha registro
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Gets the accion.
	 *
	 * @return the accion
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * Sets the accion.
	 *
	 * @param accion
	 *            the new accion
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * Gets the id entity.
	 *
	 * @return the id entity
	 */
	public String getIdEntity() {
		return idEntity;
	}

	/**
	 * Sets the id entity.
	 *
	 * @param idEntity
	 *            the new id entity
	 */
	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}

	/**
	 * Gets the nombre entity.
	 *
	 * @return the nombre entity
	 */
	public String getNombreEntity() {
		return nombreEntity;
	}

	/**
	 * Sets the nombre entity.
	 *
	 * @param nombreEntity
	 *            the new nombre entity
	 */
	public void setNombreEntity(String nombreEntity) {
		this.nombreEntity = nombreEntity;
	}

	/**
	 * Gets the informacion.
	 *
	 * @return the informacion
	 */
	public String getInformacion() {
		return informacion;
	}

	/**
	 * Sets the informacion.
	 *
	 * @param informacion
	 *            the new informacion
	 */
	public void setInformacion(String informacion) {
		this.informacion = informacion;
	}

	/**
	 * Gets the id usuario.
	 *
	 * @return the id usuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * Sets the id usuario.
	 *
	 * @param idUsuario
	 *            the new id usuario
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * Gets the tipo entity.
	 *
	 * @return the tipo entity
	 */
	public String getTipoEntity() {
		return tipoEntity;
	}

	/**
	 * Sets the tipo entity.
	 *
	 * @param tipoEntity
	 *            the new tipo entity
	 */
	public void setTipoEntity(String tipoEntity) {
		this.tipoEntity = tipoEntity;
	}

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea
	 *            the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return Ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.Ip = ip;
	}

	/**
	 * @return the nombreEquipo
	 */
	public String getNombreEquipo() {
		return NombreEquipo;
	}

	/**
	 * @return the origenId
	 */
	public Integer getOrigenId() {
		return origenId;
	}

	/**
	 * @param origenId the origenId to set
	 */
	public void setOrigenId(Integer origenId) {
		this.origenId = origenId;
	}

	/**
	 * @param nombreEquipo the nombreEquipo to set
	 */
	public void setNombreEquipo(String nombreEquipo) {
		this.NombreEquipo = nombreEquipo;
	}
	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Auditoria [id=" + id + ", fechaRegistro=" + fechaRegistro + ", accion=" + accion + ", idEntity="
				+ idEntity + ", nombreEntity=" + nombreEntity + ", informacion=" + informacion + ", idUsuario="
				+ idUsuario + ", tipoEntity=" + tipoEntity + ", idArea=" + idArea + ", Ip=" + Ip + ", NombreEquipo="
				+ NombreEquipo + ", institucionId=" + institucionId + ", origenId=" + origenId + "]";
	}
	



}
