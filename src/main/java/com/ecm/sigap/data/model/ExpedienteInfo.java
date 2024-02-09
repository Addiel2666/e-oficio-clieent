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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class ExpedienteInfo.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "expedienteArchivistica")
@SequenceGenerator(name = "SEQ_EXPEDIENTES", sequenceName = "EXPEDIENTEARCHIVISTICA_SEQ", allocationSize = 1)
public class ExpedienteInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5133713453544414451L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EXPEDIENTES")
	@Column(name = "idExpediente")
	private Integer id;

	/** The estructura. */
	@Column(name = "idClasificacion")
	private Integer estructura;

	/** The clasificacion documental. */
	@OneToOne
	@JoinColumn(name = "idClasificaDocumental")
	@Fetch(value = FetchMode.SELECT)
	private ClasificacionDocumental clasificacionDocumental;

	/** The expediente. */
	@Column(name = "noExpediente")
	private String expediente;

	/** The asunto. */
	@Column(name = "asunto")
	private String asunto;

	/** The status. */
	@OneToOne
	@JoinColumn(name = "idEstatusExpediente")
	@Fetch(value = FetchMode.SELECT)
	private StatusExpediente status;

	/** The fecha apertura. */
	@Column(name = "feApertura")
	@Type(type = "java.util.Date")
	private Date fechaApertura;

	/** The oficial. */
	@Column(name = "oficialSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean oficial;

	/** The area. */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(value = FetchMode.SELECT)
	private Area area;

	/** The consecutivo. */
	@Column(name = "consecutivo")
	private String consecutivo;

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
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the clasificacion documental.
	 *
	 * @return the clasificacionDocumental
	 */
	public ClasificacionDocumental getClasificacionDocumental() {
		return clasificacionDocumental;
	}

	/**
	 * Sets the clasificacion documental.
	 *
	 * @param clasificacionDocumental the clasificacionDocumental to set
	 */
	public void setClasificacionDocumental(ClasificacionDocumental clasificacionDocumental) {
		this.clasificacionDocumental = clasificacionDocumental;
	}

	/**
	 * Gets the expediente.
	 *
	 * @return the expediente
	 */
	public String getExpediente() {
		return expediente;
	}

	/**
	 * Sets the expediente.
	 *
	 * @param expediente the expediente to set
	 */
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}

	/**
	 * Gets the asunto.
	 *
	 * @return the asunto
	 */
	public String getAsunto() {
		return asunto;
	}

	/**
	 * Sets the asunto.
	 *
	 * @param asunto the asunto to set
	 */
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public StatusExpediente getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(StatusExpediente status) {
		this.status = status;
	}

	/**
	 * Gets the fecha apertura.
	 *
	 * @return the fechaApertura
	 */
	public Date getFechaApertura() {
		return fechaApertura;
	}

	/**
	 * Sets the fecha apertura.
	 *
	 * @param fechaApertura the fechaApertura to set
	 */
	public void setFechaApertura(Date fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	/**
	 * Gets the oficial.
	 *
	 * @return the oficial
	 */
	public Boolean getOficial() {
		return oficial;
	}

	/**
	 * Sets the oficial.
	 *
	 * @param oficial the oficial to set
	 */
	public void setOficial(Boolean oficial) {
		this.oficial = oficial;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * Gets the consecutivo.
	 *
	 * @return the consecutivo
	 */
	public String getConsecutivo() {
		return consecutivo;
	}

	/**
	 * Sets the consecutivo.
	 *
	 * @param consecutivo the consecutivo to set
	 */
	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	/**
	 * Gets the estructura.
	 *
	 * @return the estructura
	 */
	public Integer getEstructura() {
		return estructura;
	}

	/**
	 * Sets the estructura.
	 *
	 * @param estructura the new estructura
	 */
	public void setEstructura(Integer estructura) {
		this.estructura = estructura;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the estructura
	 */
	// public Expediente getEstructura() {
	// return estructura;
	// }

	/**
	 * @param estructura the estructura to set
	 */
	// public void setEstructura(Expediente estructura) {
	// this.estructura = estructura;
	// }

}
