/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.model.Usuario;

/**
 * The Class RevisorMinutario.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class RevisorMinutario implements Serializable, Comparable<RevisorMinutario> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8796013008163601069L;

	/** Content Id del documento en el repositorio */
	@Column(name = "contentId")
	private String objectId;

	/** Usuario revisor del Minutario */
	@Column(name = "idRevisor")
	private String id;

	/** Version de la revision */
	@Column(name = "version")
	private String version;

	/** Fecha de registro de la revision */
	@Column(name = "fechaReg")
	private Date fechaRegistro;

	/** Comentario de la revision */
	@Column(name = "comentarios")
	private String comentario;

	/** Nombre del documento */
	@Column(name = "docName")
	private String documentName;

	/** Descripcion del Usuario */
	@OneToOne(optional = true)
	@JoinColumn(name = "usuario")
	@Fetch(FetchMode.SELECT)
	private Usuario usuario;

	/** Dueño del documento en el repositorio. */
	@Transient
	private String nombreUsuario;

	/** Propiedades del objeto en el repositorio. */
	@Transient
	private Map<String, Object> documentProperties;

	/**
	 * Obtiene el Content Id del documento en el repositorio.
	 *
	 * @return Content Id del documento en el repositorio
	 */
	public String getObjectId() {

		return objectId;
	}

	/**
	 * Asigna el Content Id del documento en el repositorio
	 *
	 * @param objectId Content Id del documento en el repositorio
	 */
	public void setObjectId(String objectId) {

		this.objectId = objectId;
	}

	/**
	 * Obtiene el Usuario revisor del Minutario
	 *
	 * @return Usuario revisor del Minutario
	 */
	public String getId() {

		return id;
	}

	/**
	 * Asigna el Usuario revisor del Minutario
	 *
	 * @param revisor Usuario revisor del Minutario
	 */
	public void setId(String id) {

		this.id = id;
	}

	/**
	 * Obtiene la Version de la revision
	 *
	 * @return Version de la revision
	 */
	public String getVersion() {

		return version;
	}

	/**
	 * Asigna la Version de la revision
	 *
	 * @param version Version de la revision
	 */
	public void setVersion(String version) {

		this.version = version;
	}

	/**
	 * Obtiene la Fecha de registro de la revision
	 *
	 * @return Fecha de registro de la revision
	 */
	public Date getFechaRegistro() {

		return fechaRegistro;
	}

	/**
	 * Asigna la Fecha de registro de la revision
	 *
	 * @param fechaRegistro Fecha de registro de la revision
	 */
	public void setFechaRegistro(Date fechaRegistro) {

		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Obtiene el Comentario de la revision
	 *
	 * @return Comentario de la revision
	 */
	public String getComentario() {

		return comentario;
	}

	/**
	 * Asigna el Comentario de la revision
	 *
	 * @param comentario Comentario de la revision
	 */
	public void setComentario(String comentario) {

		this.comentario = comentario;
	}

	/**
	 * Obtiene el Nombre del documento
	 *
	 * @return Nombre del documento
	 */
	public String getDocumentName() {

		return documentName;
	}

	/**
	 * Asigna el Nombre del documento
	 *
	 * @param documentName Nombre del documento
	 */
	public void setDocumentName(String documentName) {

		this.documentName = documentName;
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
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the documentProperties
	 */
	public Map<String, Object> getDocumentProperties() {

		return documentProperties;
	}

	/**
	 * @param documentProperties the documentProperties to set
	 */
	public void setDocumentProperties(Map<String, Object> documentProperties) {

		this.documentProperties = documentProperties;
	}

	/**
	 * @return the nombreUsuario
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	/**
	 * @param nombreUsuario the nombreUsuario to set
	 */
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RevisorMinutario [objectId=" + objectId + ", id=" + id + ", version=" + version + ", fechaRegistro="
				+ fechaRegistro + ", comentario=" + comentario + ", documentName=" + documentName + ", usuario="
				+ usuario + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RevisorMinutario o) {

		// float ver_1 = Float.parseFloat(this.getVersion()) * 100;
		// float ver_2 = Float.parseFloat(o.getVersion()) * 100;

		Date date_1 = this.fechaRegistro;
		Date date_2 = o.fechaRegistro;

		if (date_1.after(date_2)) {
			return -1;
		} else if (date_1.before(date_2)) {
			return 1;
		} else {
			return 0;
		}

		// return (int) ((ver_1 - ver_2) + (date_1.getTime() - date_2.getTime()));

	}

}
