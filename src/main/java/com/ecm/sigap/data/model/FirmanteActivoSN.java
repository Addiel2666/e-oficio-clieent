/**
 * Copyright (c) 2021 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

/**
 * 
 * @author
 * @version 1.0
 *
 */
@Entity
@Table(name = "firmantes_activosn")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class FirmanteActivoSN implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5438159170583565391L;

	/** */
	@Id
	@Column(name = "idFirmante")
	private String idFirmante;

	/** */
	@Id
	@Column(name = "idPromotor")
	private Integer idPromotor;

	/** */
	@Column(name = "tipoFirmante")
	private String tipoFirmante;

	/** */
	@Column(name = "idRemitente")
	private Integer idRemitente;

	/** */
	@Column(name = "nombre")
	private String nombres;

	/** */
	@Column(name = "paterno")
	private String paterno;

	/** */
	@Column(name = "materno")
	private String materno;

	/** */
	@Formula(" concat( NOMBRE , concat( ' ' , concat(PATERNO , concat(' ' ,  MATERNO)))) ")
	private String nombreCompleto;

	/** */
	@Column(name = "cargo")
	private String cargo;

	/** */
	@Column(name = "activosn")
	private String activosn;

	/** */
	@Formula(value = "{SIGAP_SCHEMA}.COUNT_TITULARES(idFirmante)")
	private Integer numAreasComoTitular;
	
	@Transient
	private boolean soloTitularesArea;

	/**
	 * @return the idFirmante
	 */
	public String getIdFirmante() {
		return idFirmante;
	}

	/**
	 * @param idFirmante the idFirmante to set
	 */
	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	/**
	 * @return the idPromotor
	 */
	public Integer getIdPromotor() {
		return idPromotor;
	}

	/**
	 * @param idPromotor the idPromotor to set
	 */
	public void setIdPromotor(Integer idPromotor) {
		this.idPromotor = idPromotor;
	}

	/**
	 * @return the tipoFirmante
	 */
	public String getTipoFirmante() {
		return tipoFirmante;
	}

	/**
	 * @param tipoFirmante the tipoFirmante to set
	 */
	public void setTipoFirmante(String tipoFirmante) {
		this.tipoFirmante = tipoFirmante;
	}

	/**
	 * @return the idRemitente
	 */
	public Integer getIdRemitente() {
		return idRemitente;
	}

	/**
	 * @param idRemitente the idRemitente to set
	 */
	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	/**
	 * @return the nombres
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * @param nombres the nombres to set
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * @return the paterno
	 */
	public String getPaterno() {
		return paterno;
	}

	/**
	 * @param paterno the paterno to set
	 */
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	/**
	 * @return the materno
	 */
	public String getMaterno() {
		return materno;
	}

	/**
	 * @param materno the materno to set
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
	}

	/**
	 * @return the nombreCompleto
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * @param nombreCompleto the nombreCompleto to set
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/**
	 * @return the cargo
	 */
	public String getCargo() {
		return cargo;
	}

	/**
	 * @param cargo the cargo to set
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * @return the activosn
	 */
	public String getActivosn() {
		return activosn;
	}

	/**
	 * @param activosn the activosn to set
	 */
	public void setActivosn(String activosn) {
		this.activosn = activosn;
	}

	/**
	 * @return the numAreasComoTitular
	 */
	public Integer getNumAreasComoTitular() {
		return numAreasComoTitular;
	}

	/**
	 * @param numAreasComoTitular the numAreasComoTitular to set
	 */
	public void setNumAreasComoTitular(Integer numAreasComoTitular) {
		this.numAreasComoTitular = numAreasComoTitular;
	}

	/**
	 * @return the soloTitularesArea
	 */
	public boolean isSoloTitularesArea() {
		return soloTitularesArea;
	}

	/**
	 * @param soloTitularesArea the soloTitularesArea to set
	 */
	public void setSoloTitularesArea(boolean soloTitularesArea) {
		this.soloTitularesArea = soloTitularesArea;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "FirmanteActivoSN [idFirmante=" + idFirmante + ", idPromotor=" + idPromotor + ", tipoFirmante="
				+ tipoFirmante + ", idRemitente=" + idRemitente + ", nombres=" + nombres + ", paterno=" + paterno
				+ ", materno=" + materno + ", nombreCompleto=" + nombreCompleto + ", cargo=" + cargo + ", activosn="
				+ activosn + ", numAreasComoTitular=" + numAreasComoTitular + "]";
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idFirmante == null) ? 0 : idFirmante.hashCode());
		result = prime * result + ((idPromotor == null) ? 0 : idPromotor.hashCode());
		return result;
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FirmanteActivoSN other = (FirmanteActivoSN) obj;
		if (idFirmante == null) {
			if (other.idFirmante != null)
				return false;
		} else if (!idFirmante.equals(other.idFirmante))
			return false;
		if (idPromotor == null) {
			if (other.idPromotor != null)
				return false;
		} else if (!idPromotor.equals(other.idPromotor))
			return false;
		return true;
	}

}
