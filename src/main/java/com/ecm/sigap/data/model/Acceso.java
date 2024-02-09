/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class Acceso.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "accesos")
@UniqueKey(columnNames = { "accesoKey.idUsuario", "accesoKey.area.idArea",
		"accesoKey.rol.idRol" }, message = "{Unique.descripcion}")
public class Acceso implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2540153869687546972L;

	/** The acceso key. */
	@EmbeddedId
	private AccesoKey accesoKey;

	/** The usuario. */
	@ManyToOne
	@JoinColumn(name = "idUsuario", insertable = false, updatable = false)
	@Fetch(FetchMode.SELECT)
	private Usuario usuario;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** */
	@Transient
	private Integer idNuevoRol;

	/**
	 * Gets the acceso key.
	 *
	 * @return the acceso key
	 */
	public AccesoKey getAccesoKey() {
		return accesoKey;
	}

	/**
	 * Sets the acceso key.
	 *
	 * @param accesoKey the new acceso key
	 */
	public void setAccesoKey(AccesoKey accesoKey) {
		this.accesoKey = accesoKey;
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
	 * @param activo the new activo
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Acceso [accesoKey=" + accesoKey + ", activo=" + activo + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accesoKey.getArea() == null) ? 0 : accesoKey.getArea().hashCode());
		result = prime * result + ((accesoKey.getIdUsuario() == null) ? 0 : accesoKey.getIdUsuario().hashCode());
		result = prime * result + ((accesoKey.getRol() == null) ? 0 : accesoKey.getRol().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Acceso other = (Acceso) obj;
		if (accesoKey.getArea() == null) {
			if (other.accesoKey.getArea() != null)
				return false;
		} else if (!accesoKey.getArea().equals(other.accesoKey.getArea()))
			return false;
		if (accesoKey.getIdUsuario() == null) {
			if (other.accesoKey.getIdUsuario() != null)
				return false;
		} else if (!accesoKey.getIdUsuario().equals(other.accesoKey.getIdUsuario()))
			return false;

		return true;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getIdNuevoRol() {
		return idNuevoRol;
	}

	/**
	 * 
	 * @param idNuevoRol
	 */
	public void setIdNuevoRol(Integer idNuevoRol) {
		this.idNuevoRol = idNuevoRol;
	}

	@Override
	public String getId() {
		return (null != this.accesoKey) ? String.valueOf(this.accesoKey.getIdUsuario()) : "null";
	}

	@Override
	public String getLogDeatil() {	
		StringBuilder sb = new StringBuilder();
		sb.append("Usuario").append("<br>")
		.append("Usuario: ").append(null != accesoKey ? accesoKey.getIdUsuario() : "null").append("<br>")
		.append("Área: ").append( (null != accesoKey) ? (null != accesoKey.getArea() ? accesoKey.getArea().getDescripcion() : "null") : "null").append("<br>")
		.append("Rol: ").append(null != accesoKey.getRol() ? accesoKey.getRol().getDescripcion() : "null");
		
		return sb.toString();
	}

}
