/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

/**
 * Clase de entidad que representa la vista FIRMANTES.
 *
 * @author Alejandro Guzman
 * @version 1.0
 */
@Entity
@Table(name = "FIRMANTES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@Immutable
@IdClass(FirmantePK.class)
public class Firmante implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6197473179440227839L;

	/** Identificador del Firmante. */
	@Id
	@Column(name = "idfirmante")
	private String idFirmante;

	/**
	 * Identificador de la Institucion / Empresa a la que pertence el firmante.
	 */
	@Id
	@Column(name = "idPromotor")
	private Integer idPromotor;

	/** Identificador del tipo Firmante. */
	@Column(name = "tipoFirmante")
	private String tipoFirmante;

	/** Identificador del Area o Empresa a la que pertenece el Firmante. */
	@Id
	@Column(name = "idRemitente")
	private Integer idRemitente;

	/** Nombre del Firmante. */
	@Column(name = "nombre")
	private String nombres;

	/** Apellido paterno del Firmante. */
	@Column(name = "paterno")
	private String paterno;

	/** Apellido materno del Firmante. */
	@Column(name = "materno")
	private String materno;

	/** The nombre completo. */
	@Formula(" concat( NOMBRE , concat( ' ' , concat(PATERNO , concat(' ' ,  MATERNO)))) ")
	private String nombreCompleto;

	/** Descripcion del cargo del Firmante. */
	@Column(name = "cargo")
	private String cargo;
	
	/** Num de áreas donde es titular el Firmante*/
	@Formula(value = "{SIGAP_SCHEMA}.COUNT_TITULARES(idFirmante)")
	private Integer numAreasComoTitular;

	/**
	 * Gets the id firmante.
	 *
	 * @return the id firmante
	 */
	public String getIdFirmante() {
		return idFirmante;
	}

	/**
	 * Sets the id firmante.
	 *
	 * @param idFirmante
	 *            the new id firmante
	 */
	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	/**
	 * Gets the tipofirmante.
	 *
	 * @return the tipofirmante
	 */
	public String getTipoFirmante() {
		return tipoFirmante;
	}

	/**
	 * Sets the tipofirmante.
	 *
	 * @param tipofirmante
	 *            the new tipofirmante
	 */
	public void setTipoFirmante(String tipoFirmante) {
		this.tipoFirmante = tipoFirmante;
	}

	/**
	 * Gets the id promotor.
	 *
	 * @return the id promotor
	 */
	public Integer getIdPromotor() {
		return idPromotor;
	}

	/**
	 * Sets the id promotor.
	 *
	 * @param idPromotor
	 *            the new id promotor
	 */
	public void setIdPromotor(Integer idPromotor) {
		this.idPromotor = idPromotor;
	}

	/**
	 * Gets the id remitente.
	 *
	 * @return the id remitente
	 */
	public Integer getIdRemitente() {
		return idRemitente;
	}

	/**
	 * Sets the id remitente.
	 *
	 * @param idRemitente
	 *            the new id remitente
	 */
	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	/**
	 * Gets the nombres.
	 *
	 * @return the nombres
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Sets the nombres.
	 *
	 * @param nombres
	 *            the new nombres
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Gets the paterno.
	 *
	 * @return the paterno
	 */
	public String getPaterno() {
		return paterno;
	}

	/**
	 * Sets the paterno.
	 *
	 * @param paterno
	 *            the new paterno
	 */
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	/**
	 * Gets the materno.
	 *
	 * @return the materno
	 */
	public String getMaterno() {
		return materno;
	}

	/**
	 * Sets the materno.
	 *
	 * @param materno
	 *            the new materno
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
	}

	/**
	 * Gets the nombre completo.
	 *
	 * @return the nombre completo
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * Sets the nombre completo.
	 *
	 * @param nombreCompleto
	 *            the new nombre completo
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/**
	 * Gets the cargo.
	 *
	 * @return the cargo
	 */
	public String getCargo() {
		return cargo;
	}

	/**
	 * Sets the cargo.
	 *
	 * @param cargo
	 *            the new cargo
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	
	public Integer getNumAreasComoTitular() {
		return numAreasComoTitular;
	}

	/**
	 * Sets the Numero de areas como titular.
	 *
	 * @param numAreasComoTitular
	 *            the new num Areas Como Titular
	 */
	public void setNumAreasComoTitular(Integer numAreasComoTitular) {
		this.numAreasComoTitular = numAreasComoTitular;
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
		return "Firmante [idFirmante=" + idFirmante + ", idPromotor=" + idPromotor + ", idRemitente=" + idRemitente
				+ ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno + ", nombreCompleto="
				+ nombreCompleto + ", cargo=" + cargo + "]";
	}

}
