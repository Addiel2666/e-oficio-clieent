/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.DestinatariosMinutario;
import com.ecm.sigap.data.model.util.RevisorMinutario;
import com.ecm.sigap.data.model.util.StatusMinutario;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.StatusMinutarioConverter;

/**
 * The Class Minutario.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "asuntosMinutario", //
		schema = "{SIGAP_SCHEMA}" //
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Minutario implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5679264618622532009L;

	/** The id minutario. */
	@Id
	@Column(name = "idMinutario")
	private Integer idMinutario;

	/** The institucion. */
	@OneToOne
	@JoinColumn(name = "idInstitucion", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Institucion institucion;

	/** The remitente. */
	@OneToOne
	@JoinColumn(name = "idRemitente", nullable = false)
	@Fetch(value = FetchMode.SELECT)
	private Area remitente;

	/** The firmante. */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idFirmante")
	@Fetch(FetchMode.SELECT)
	private Usuario firmante;

	/** The titulo documento. */
	@Column(name = "tituloDocumento")
	private String tituloDocumento;

	/** The asunto. */
	@Column(name = "asunto")
	private String asunto;

	/** The usuario. */
	@OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idUsuario")
	@Fetch(FetchMode.SELECT)
	private Usuario usuario;

	/** The id documento. */
	@Column(name = "contentId")
	private String idDocumento;

	/** The status. */
	@Column(name = "idStatus")
	@Convert(converter = StatusMinutarioConverter.class)
	private StatusMinutario status;

	/** The fecha registro. */
	@Column(name = "fechaReg", insertable = false, updatable = false)
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** The id asunto. */
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** The confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

	/** Id del folder en el repositorio. */
	@Transient
	private String contentId;

	/** The destinatarios. */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = DestinatariosMinutario.class)
	@CollectionTable(name = "asuntos_Minutarios_Dest", //
			schema = "{SIGAP_SCHEMA}", //
			joinColumns = { @JoinColumn(name = "idMinutario") })
	@JoinColumn(name = "idMinutario", insertable = true, referencedColumnName = "idMinutario")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.ALL)
	@OrderBy(clause = "orden")
	private List<DestinatariosMinutario> destinatarios;

	/** The revisores. */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = RevisorMinutario.class)
	@CollectionTable(name = "minutarioRevisores", //
			schema = "{SIGAP_SCHEMA}", //
			joinColumns = { @JoinColumn(name = "idMinutario") })
	@JoinColumn(name = "idMinutario", insertable = true, referencedColumnName = "idMinutario")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.ALL)
	private List<RevisorMinutario> revisores;

	/**
	 * Gets the id minutario.
	 *
	 * @return the idMinutario
	 */
	public Integer getIdMinutario() {
		return idMinutario;
	}

	/**
	 * Sets the id minutario.
	 *
	 * @param idMinutario the idMinutario to set
	 */
	public void setIdMinutario(Integer idMinutario) {
		this.idMinutario = idMinutario;
	}

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion the institucion to set
	 */
	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	/**
	 * Gets the remitente.
	 *
	 * @return the remitente
	 */
	public Area getRemitente() {
		return remitente;
	}

	/**
	 * Sets the remitente.
	 *
	 * @param remitente the remitente to set
	 */
	public void setRemitente(Area remitente) {
		this.remitente = remitente;
	}

	/**
	 * Gets the firmante.
	 *
	 * @return the firmante
	 */
	public Usuario getFirmante() {
		return firmante;
	}

	/**
	 * Sets the firmante.
	 *
	 * @param firmante the firmante to set
	 */
	public void setFirmante(Usuario firmante) {
		this.firmante = firmante;
	}

	/**
	 * Gets the titulo documento.
	 *
	 * @return the tituloDocumento
	 */
	public String getTituloDocumento() {
		return tituloDocumento;
	}

	/**
	 * Sets the titulo documento.
	 *
	 * @param tituloDocumento the tituloDocumento to set
	 */
	public void setTituloDocumento(String tituloDocumento) {
		this.tituloDocumento = tituloDocumento;
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
	 * Gets the id documento.
	 *
	 * @return the idDocumento
	 */
	public String getIdDocumento() {
		return idDocumento;
	}

	/**
	 * Sets the id documento.
	 *
	 * @param idDocumento the idDocumento to set
	 */
	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public StatusMinutario getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(StatusMinutario status) {
		this.status = status;
	}

	/**
	 * Gets the fecha registro.
	 *
	 * @return the fechaRegistro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * Sets the fecha registro.
	 *
	 * @param fechaRegistro the fechaRegistro to set
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Gets the destinatarios.
	 *
	 * @return the destinatarios
	 */
	public List<DestinatariosMinutario> getDestinatarios() {
		return destinatarios;
	}

	/**
	 * Sets the destinatarios.
	 *
	 * @param destinatarios the destinatarios to set
	 */
	public void setDestinatarios(List<DestinatariosMinutario> destinatarios) {
		this.destinatarios = destinatarios;
	}

	/**
	 * Gets the revisores.
	 *
	 * @return the revisores
	 */
	public List<RevisorMinutario> getRevisores() {
		return revisores;
	}

	/**
	 * Sets the revisores.
	 *
	 * @param revisores the revisores to set
	 */
	public void setRevisores(List<RevisorMinutario> revisores) {
		this.revisores = revisores;
	}

	/**
	 * Gets the id asunto.
	 *
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * Sets the id asunto.
	 *
	 * @param idAsunto the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * Gets the usuario.
	 *
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Sets the usuario.
	 *
	 * @param usuario the new usuario
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * Gets the confidencial.
	 *
	 * @return the confidencial
	 */
	public Boolean getConfidencial() {
		return confidencial;
	}

	/**
	 * Sets the confidencial.
	 *
	 * @param confidencial the confidencial to set
	 */
	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
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
		return "Minutario [idMinutario=" + idMinutario + ", institucion=" + institucion + ", remitente=" + remitente
				+ ", firmante=" + firmante + ", tituloDocumento=" + tituloDocumento + ", asunto=" + asunto
				+ ", usuario=" + usuario + ", idDocumento=" + idDocumento + ", status=" + status + ", fechaRegistro="
				+ fechaRegistro + ", idAsunto=" + idAsunto + ", confidencial=" + confidencial + ", destinatarios="
				+ destinatarios + ", revisores=" + revisores + "]";
	}

	/**
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

}
