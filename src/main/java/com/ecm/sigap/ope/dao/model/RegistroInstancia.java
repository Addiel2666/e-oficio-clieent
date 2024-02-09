/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.dao.model;

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
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.ope.dao.util.StatusRegistroConverter;
import com.ecm.sigap.ope.model.StatusRegistro;

/**
 * Registro individual por cada institucion con la que se interopera,
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "OPEREGISTROS")
@UniqueKey(columnNames = { "id", "url", "alias" }, message = "{Unique.descripcion}")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SECOBJETOS", allocationSize = 1)
public class RegistroInstancia implements Serializable {

	/** */
	private static final long serialVersionUID = -8004929904616558487L;

	/**  */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
	@Column(name = "ID")
	private Integer id;

	/** */
	@Column(name = "URL")
	@NotBlank
	private String url;

	/** */
	@Column(name = "DESCRIPCION")
	@NotBlank
	private String descripcion;

	/** */
	@Column(name = "NOMBRECORTO")
	@NotBlank
	private String alias;

	/** */
	@Column(name = "FECHAREGISTRO")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** */
	@Column(name = "STATUS")
	@Convert(converter = StatusRegistroConverter.class)
	private StatusRegistro status;

	/** */
	@Column(name = "IDINSTITUCION")
	private Integer idInstitucion;

	/** */
	@OneToOne
	@JoinColumn(name = "IDMENSAJE")
	@NotFound(action = NotFoundAction.IGNORE)
	private Mensaje mensaje;

	/** */
	@Transient
	// @Column(name = "VERSIONCATALOGO")
	private String versionCatalogo;

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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the status
	 */
	public StatusRegistro getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusRegistro status) {
		this.status = status;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
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
	 * @return the idInstitucion
	 */
	public Integer getIdInstitucion() {
		return idInstitucion;
	}

	/**
	 * @param idInstitucion the idInstitucion to set
	 */
	public void setIdInstitucion(Integer idInstitucion) {
		this.idInstitucion = idInstitucion;
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
	 * @return the versionCatalogo
	 */
	public String getVersionCatalogo() {
		return versionCatalogo;
	}

	/**
	 * @param versionCatalogo the versionCatalogo to set
	 */
	public void setVersionCatalogo(String versionCatalogo) {
		this.versionCatalogo = versionCatalogo;
	}

}
