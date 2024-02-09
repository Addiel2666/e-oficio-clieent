/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.TipoFirma;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.TipoFirmaToStringConverter;

/**
 * 
 * Clase de Identidad que representa la tabla documentosAntefirma
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@MappedSuperclass
public abstract class DocumentoAntefirma implements Serializable {

	/** */
	private static final long serialVersionUID = 5405653310249991334L;

	/** key */
	@EmbeddedId
	private DocumentoAntefirmaKey documentoAntefirmaKey;

	/**  */
	@Column(name = "firmadosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean firmado;

	/**  */
	@Column(name = "fecha")
	@Type(type = "java.util.Date")
	private Date fechaFirma;

	/**  */
	@Column(name = "tipoFirma")
	@Convert(converter = TipoFirmaToStringConverter.class)
	private TipoFirma tipoFirma;

	/** */
	@Transient
	private String objectName;

	/** */
	@Transient
	private String nombres;

	/** */
	@Transient
	private String paterno;

	/** */
	@Transient
	private String materno;
	
	/** */
	@Transient
	private Date fechaMarca;

	/**
	 * 
	 */
	public DocumentoAntefirma() {
		super();
	}

	/**
	 * @return the documentoAntefirmaKey
	 */
	public DocumentoAntefirmaKey getDocumentoAntefirmaKey() {
		return documentoAntefirmaKey;
	}

	/**
	 * @param documentoAntefirmaKey
	 *            the documentoAntefirmaKey to set
	 */
	public void setDocumentoAntefirmaKey(DocumentoAntefirmaKey documentoAntefirmaKey) {
		this.documentoAntefirmaKey = documentoAntefirmaKey;
	}

	/**
	 * @return the firmado
	 */
	public Boolean getFirmado() {
		return firmado;
	}

	/**
	 * @param firmado
	 *            the firmado to set
	 */
	public void setFirmado(Boolean firmado) {
		this.firmado = firmado;
	}

	/**
	 * @return the fechaFirma
	 */
	public Date getFechaFirma() {
		return fechaFirma;
	}

	/**
	 * @param fechaFirma
	 *            the fechaFirma to set
	 */
	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	/**
	 * @return the tipoFirma
	 */
	public TipoFirma getTipoFirma() {
		return tipoFirma;
	}

	/**
	 * @param tipoFirma
	 *            the tipoFirma to set
	 */
	public void setTipoFirma(TipoFirma tipoFirma) {
		this.tipoFirma = tipoFirma;
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return the nombres
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * @param nombres
	 *            the nombres to set
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
	 * @param paterno
	 *            the paterno to set
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
	 * @param materno
	 *            the materno to set
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
	}
	
	/**
	 * @return the fechaMarca
	 */
	public Date getFechaMarca() {
		return fechaMarca;
	}
	
	/**
	 * @param fechaMarca
	 *            the fechaMarca to set
	 */

	public void setFechaMarca(Date fechaMarca) {
		this.fechaMarca = fechaMarca;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentoAntefirma [documentoAntefirmaKey=" + documentoAntefirmaKey + ", firmado=" + firmado
				+ ", fechaFirma=" + fechaFirma + ", tipoFirma=" + tipoFirma + ", objectName=" + objectName
				+ ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno + ", fechaMarca="
				+ fechaMarca + "]";
	}

	
	

}
