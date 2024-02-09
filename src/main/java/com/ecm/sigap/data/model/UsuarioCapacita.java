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
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "UserCapacita")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class UsuarioCapacita implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1183918934741843149L;

	/** Identificador del Usuario */
	@Id
	@Column(name = "userName")
	private String idUsuario;

	/** Identificador del estatus de capacitacion del Usuario */
	@Column(name = "capacitadosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean capacitado;

	/** Identificador del estatus de la aceptacion de las politicas */
	@Column(name = "aceptosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean acepto;

	/** Fecha de registro de aceptacion de las policitas */
	@Column(name = "fechaAcepto")
	private Date fecha;

	/** Direccion IP del equipo desde donde se acepto la politica */
	@Column(name = "direccionIp")
	private String ip;

	/**
	 * Obtiene el Identificador del Usuario
	 * 
	 * @return Identificador del Usuario
	 */
	public String getIdUsuario() {

		return idUsuario;
	}

	/**
	 * Asigna el Identificador del Usuario
	 * 
	 * @param idUsuario
	 *            Identificador del Usuario
	 */
	public void setIdUsuario(String idUsuario) {

		this.idUsuario = idUsuario;
	}

	/**
	 * Obtiene el Identificador del estatus de capacitacion del Usuario
	 * 
	 * @return Identificador del estatus de capacitacion del Usuario
	 */
	public Boolean getCapacitado() {

		return capacitado;
	}

	/**
	 * Asigna el Identificador del estatus de capacitacion del Usuario
	 * 
	 * @param capacitado
	 *            Identificador del estatus de capacitacion del Usuario
	 */
	public void setCapacitado(Boolean capacitado) {

		this.capacitado = capacitado;
	}

	/**
	 * Obtiene el Identificador del estatus de la aceptacion de las politicas
	 * 
	 * @return Identificador del estatus de la aceptacion de las politicas
	 */
	public Boolean getAcepto() {

		return acepto;
	}

	/**
	 * Asigna el Identificador del estatus de la aceptacion de las politicas
	 * 
	 * @param acepto
	 *            Identificador del estatus de la aceptacion de las politicas
	 */
	public void setAcepto(Boolean acepto) {

		this.acepto = acepto;
	}

	/**
	 * Obtiene la Fecha de registro de aceptacion de las policitas
	 * 
	 * @return Fecha de registro de aceptacion de las policitas
	 */
	public Date getFecha() {

		return fecha;
	}

	/**
	 * Asigna la Fecha de registro de aceptacion de las policitas
	 * 
	 * @param fecha
	 *            Fecha de registro de aceptacion de las policitas
	 */
	public void setFecha(Date fecha) {

		this.fecha = fecha;
	}

	/**
	 * Obtiene la Direccion IP del equipo desde donde se acepto la politica
	 * 
	 * @return Direccion IP del equipo desde donde se acepto la politica
	 */
	public String getIp() {

		return ip;
	}

	/**
	 * Asigna la Direccion IP del equipo desde donde se acepto la politica
	 * 
	 * @param ip
	 *            Direccion IP del equipo desde donde se acepto la politica
	 */
	public void setIp(String ip) {

		this.ip = ip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UsuarioCapacita [idUsuario=" + idUsuario + ", capacitado=" + capacitado + ", acepto=" + acepto
				+ ", fecha=" + fecha + ", ip=" + ip + "]";
	}

}
