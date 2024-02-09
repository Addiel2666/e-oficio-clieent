/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class TipoExpediente.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "tiposExpediente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "area.idArea", "descripcion" }, message = "{Unique.descripcion}")
public class TipoExpediente implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6910993887950410556L;

	/** The id expediente. */
	@Id
	@GenericGenerator(name = "SEQ_TIPOEXPEDIENTE", //
			strategy = "com.ecm.sigap.data.util.StringSequenceGeneratorObjetos")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TIPOEXPEDIENTE")
	@Column(name = "idExpediente")
	private String idExpediente;

	/** The descripcion. */
	@NotNull
	@NotEmpty
	@Column(name = "descripcion")
	private String descripcion;

	/** The area. */
	@OneToOne
	@NotNull
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private Area area;

	/** The activo. */
	@Column(name = "activosn")
	@NotNull
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** The content id. */
	@Column(name = "contentId")
	@NotNull
	@NotEmpty
	private String contentId;

	/**
	 * Gets the id expediente.
	 *
	 * @return the id expediente
	 */
	public String getIdExpediente() {
		return idExpediente;
	}

	/**
	 * Sets the id expediente.
	 *
	 * @param idExpediente
	 *            the new id expediente
	 */
	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}

	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion
	 *            the new descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
	 * @param area
	 *            the new area
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * Gets the activo.
	 *
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Sets the activo.
	 *
	 * @param activo
	 *            the new activo
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Gets the content id.
	 *
	 * @return the content id
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * Sets the content id.
	 *
	 * @param contentId
	 *            the new content id
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TipoExpediente [idExpediente=" + idExpediente + ", descripcion=" + descripcion + ", area=" + area
				+ ", activo=" + activo + ", contentId=" + contentId + "]";
	}

}
