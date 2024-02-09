/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.model.util.TipoGrupoEnvio;
import com.ecm.sigap.data.util.TipoGrupoEnvioToStringConverter;

/**
 * 
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "grupo_envio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_GRUPO_ENVIOS", sequenceName = "SECOBJETOS", allocationSize = 1)
public final class GrupoEnvio implements Serializable {

	/** */
	private static final long serialVersionUID = -2378145961237432392L;

	/** */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GRUPO_ENVIOS")
	@Column(name = "id")
	private Integer id;

	/** */
	@Column(name = "descripcion")
	private String descripcion;

	/** */
	@Column(name = "tipo")
	@Convert(converter = TipoGrupoEnvioToStringConverter.class)
	private TipoGrupoEnvio tipo;

	/** */
	@Column(name = "idArea")
	private Integer idArea;
	
	/** Dueno del gpo privado */
	@Column(name = "idUsuario")
	private String idUsuario;

	/** */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = GrupoEnvioDestinatario.class)
	@CollectionTable(name = "grupo_envio_destinatarios", joinColumns = { @JoinColumn(name = "idGrupo") })
	@JoinColumn(name = "idGrupo", updatable = true)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<GrupoEnvioDestinatario> destinatarios;

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
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea
	 *            the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the destinatarios
	 */
	public List<GrupoEnvioDestinatario> getDestinatarios() {
		return destinatarios == null ? new ArrayList<GrupoEnvioDestinatario>() : destinatarios;
	}

	/**
	 * @param destinatarios
	 *            the destinatarios to set
	 */
	public void setDestinatarios(List<GrupoEnvioDestinatario> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public TipoGrupoEnvio getTipo() {
		return tipo;
	}

	public void setTipo(TipoGrupoEnvio tipo) {
		this.tipo = tipo;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GrupoEnvio [id=" + id + ", descripcion=" + descripcion + ", tipo=" + tipo + ", idArea=" + idArea
				+ ", idUsuario=" + idUsuario + ", destinatarios=" + destinatarios + "]";
	}

}
