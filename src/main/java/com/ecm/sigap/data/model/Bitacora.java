/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.BitacoraTipoIdentificador;
import com.ecm.sigap.data.util.BitacoraTipoIdentificadorConverter;

/**
 * Clase de entidad que representa la tabla BITACORA
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "bitacora")
@SequenceGenerator(name = "SEQ_BITACORA", sequenceName = "SECBITACORA", allocationSize = 1)
public final class Bitacora implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7130661894821292575L;

	/** Identificador de registro de la Bitacora */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BITACORA")
	@Column(name = "idBitacora")
	private Integer id;

	/**  */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/**  */
	@Column(name = "grupo")
	private Integer grupo;

	/**  */
	@Column(name = "accion")
	private String accion;

	/**  */
	@Column(name = "tipoIdentificador")
	@Convert(converter = BitacoraTipoIdentificadorConverter.class)
	private BitacoraTipoIdentificador tipoIdentificador;

	/**  */
	@Column(name = "identificador")
	private Integer identificador;

	/**  */
	@Column(name = "informacion")
	private String informacion;

	/**  */
	@Column(name = "idUsuario")
	private String idUsuario;

	/**  */
	@ManyToOne
	@JoinColumn(name = "idUsuario", updatable = false, insertable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Usuario usuario;
	
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

	public Integer getOrigenId() {
		return origenId;
	}

	public void setOrigenId(Integer origenId) {
		this.origenId = origenId;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the grupo
	 */
	public Integer getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo the grupo to set
	 */
	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}

	/**
	 * @return the accion
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * @param accion the accion to set
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * @return the tipoIdentificador
	 */
	public BitacoraTipoIdentificador getTipoIdentificador() {
		return tipoIdentificador;
	}

	/**
	 * @param tipoIdentificador the tipoIdentificador to set
	 */
	public void setTipoIdentificador(BitacoraTipoIdentificador tipoIdentificador) {
		this.tipoIdentificador = tipoIdentificador;
	}

	/**
	 * @return the identificador
	 */
	public Integer getIdentificador() {
		return identificador;
	}

	/**
	 * @param identificador the identificador to set
	 */
	public void setIdentificador(Integer identificador) {
		this.identificador = identificador;
	}

	/**
	 * @return the informacion
	 */
	public String getInformacion() {
		return informacion;
	}

	/**
	 * @param informacion the informacion to set
	 */
	public void setInformacion(String informacion) {
		this.informacion = informacion;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the idUsuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @param idUsuario the idUsuario to set
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
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
	 * @param nombreEquipo the nombreEquipo to set
	 */
	public void setNombreEquipo(String nombreEquipo) {
		this.NombreEquipo = nombreEquipo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Bitacora [id=" + id + ", fechaRegistro=" + fechaRegistro + ", grupo=" + grupo + ", accion=" + accion
				+ ", tipoIdentificador=" + tipoIdentificador + ", identificador=" + identificador + ", informacion="
				+ informacion + ", idUsuario=" + idUsuario + ", usuario=" + usuario + ", idArea=" + idArea + ", Ip="
				+ Ip + ", NombreEquipo=" + NombreEquipo + ", institucionId=" + institucionId + ", origenId=" 
				+ origenId + "]";
	}

}
