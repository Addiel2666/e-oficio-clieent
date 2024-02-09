/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Embeddable
public class DocumentoAntefirmaKey implements Serializable {

	/** */
	private static final long serialVersionUID = 4323544398760766208L;

	/** */
	@Column(name = "objectId")
	private String objectId;

	/** */
	@Column(name = "id")
	private Integer id;

	/** */
	@Column(name = "tipo")
	private String tipo;

	/** */
	@Column(name = "idFirmante")
	private String firmante;

	/** */
	@Column(name = "tipoFirmante")
	private Integer tipoFirmante;

	/**
	 * 
	 */
	public DocumentoAntefirmaKey() {
		super();
	}

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

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
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the firmante
	 */
	public String getFirmante() {
		return firmante;
	}

	/**
	 * @param firmante
	 *            the firmante to set
	 */
	public void setFirmante(String firmante) {
		this.firmante = firmante;
	}

	/**
	 * @return the tipoFirmate
	 */
	public Integer getTipoFirmante() {
		return tipoFirmante;
	}

	/**
	 * @param tipoFirmate
	 *            the tipoFirmate to set
	 */
	public void setTipoFirmate(Integer tipoFirmante) {
		this.tipoFirmante = tipoFirmante;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentoAntefirmaKey [objectId=" + objectId + ", id=" + id + ", tipo=" + tipo + ", firmante="
				+ firmante + ", tipoFirmante=" + tipoFirmante + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this)
			return true;

		if (!(obj instanceof DocumentoAntefirmaKey)) {
			return false;
		}

		if (!DocumentoAntefirmaKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		DocumentoAntefirmaKey tmp = (DocumentoAntefirmaKey) obj;

		try {

			if (tmp.id == this.id//
					&& tmp.tipoFirmante == this.tipoFirmante//
					&& tmp.firmante.equals(this.firmante)//
					&& tmp.objectId.equals(this.objectId)//
					&& tmp.tipo.equals(this.tipo)//
			)
				return true;

		} catch (NullPointerException e) {
			return false;
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.firmante, this.id, this.objectId, this.tipo, this.tipoFirmante);
	}

}
