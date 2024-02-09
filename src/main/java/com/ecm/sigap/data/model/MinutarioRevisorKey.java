/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * The Class RevisorMinutario.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class MinutarioRevisorKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 32397774115138304L;

	/** The id minutario. */
	@OneToOne
	@JoinColumn(name = "idMinutario")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Minutario minutario;

	/** The object id. */
	@Column(name = "contentId")
	private String objectId;

	/** The revisor. */
	@OneToOne
	@JoinColumn(name = "idRevisor")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Representante revisor;

	/** The version. */
	@Column(name = "version")
	private String version;

	/**
	 * Gets the minutario.
	 *
	 * @return the minutario
	 */
	public Minutario getMinutario() {
		return minutario;
	}

	/**
	 * Sets the minutario.
	 *
	 * @param minutario the new minutario
	 */
	public void setMinutario(Minutario minutario) {
		this.minutario = minutario;
	}

	/**
	 * Gets the object id.
	 *
	 * @return the object id
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * Sets the object id.
	 *
	 * @param objectId the new object id
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * Gets the revisor.
	 *
	 * @return the revisor
	 */
	public Representante getRevisor() {
		return revisor;
	}

	/**
	 * Sets the revisor.
	 *
	 * @param revisor the new revisor
	 */
	public void setRevisor(Representante revisor) {
		this.revisor = revisor;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version) {
		this.version = version;
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
		return "MinutarioRevisorKey [minutario=" + minutario + ", objectId=" + objectId + ", revisor=" + revisor
				+ ", version=" + version + "]";
	}

}
