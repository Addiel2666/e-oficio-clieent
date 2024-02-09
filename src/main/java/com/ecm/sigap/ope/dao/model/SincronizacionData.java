/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.dao.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

/**
 * Sincronizacion completa de directorio con cada institucion con la que se
 * interopera,
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "OPESINCRONIZACIONDATA")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SECOBJETOS", allocationSize = 1)
public class SincronizacionData implements Serializable {

	/** */
	private static final long serialVersionUID = -8004929904616558487L;

	/**  */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
	@Column(name = "ID")
	private Integer id;

	/** */
	@OneToOne
	@JoinColumn(name = "IDMENSAJE")
	@NotFound(action = NotFoundAction.IGNORE)
	private Mensaje mensaje;

	/** */
	// @Column(name = "IDVERSION")
	@Transient
	private String idVersion;

	/** */
	@Column(name = "IDREGISTRO")
	private Integer idRegistro;

	/** */
	@Column(name = "FECHAREGISTRO")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** */
	@OneToMany
    @JoinColumn(name = "idCatalogo")
	private List<SincronizacionDataAreas> areas;

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
	 * @return the mensaje
	 */
	public Mensaje getMensaje() {
		return mensaje;
	}

	/**
	 * @param mensaje the mensaje to set
	 */
	public void setMensaje(Mensaje mensaje) {
		this.mensaje = mensaje;
	}

	/**
	 * @return the idVersion
	 */
	public String getIdVersion() {
		return idVersion;
	}

	/**
	 * @param idVersion the idVersion to set
	 */
	public void setIdVersion(String idVersion) {
		this.idVersion = idVersion;
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
	 * @return the areas
	 */
	public List<SincronizacionDataAreas> getAreas() {
		return areas;
	}

	/**
	 * @param areas the areas to set
	 */
	public void setAreas(List<SincronizacionDataAreas> areas) {
		this.areas = areas;
	}

	/**
	 * @return the idRegistro
	 */
	public Integer getIdRegistro() {
		return idRegistro;
	}

	/**
	 * @param idRegistro the idRegistro to set
	 */
	public void setIdRegistro(Integer idRegistro) {
		this.idRegistro = idRegistro;
	}

}
