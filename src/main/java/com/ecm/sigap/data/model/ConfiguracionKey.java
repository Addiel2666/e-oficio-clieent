/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.model.util.TipoNotificacion;

/**
 * The Class ConfiguracionKey.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 */
@Embeddable
public class ConfiguracionKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -489116251798690989L;

	/** The id configuracion. */
	@Column(name = "idConfiguracion")
	private String idConfiguracion;

	/** The clave. */
	@Column(name = "clave")
	@Enumerated(EnumType.STRING)
	private TipoNotificacion clave;

	/** The usuario. */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idUsuario")
	@Fetch(value = FetchMode.SELECT)
	private Usuario usuario;

	/**
	 * Gets the id configuracion.
	 *
	 * @return the id configuracion
	 */
	public String getIdConfiguracion() {
		return idConfiguracion;
	}

	/**
	 * Sets the id configuracion.
	 *
	 * @param idConfiguracion the new id configuracion
	 */
	public void setIdConfiguracion(String idConfiguracion) {
		this.idConfiguracion = idConfiguracion;
	}

	/**
	 * Gets the clave.
	 *
	 * @return the clave
	 */
	public TipoNotificacion getClave() {
		return clave;
	}

	/**
	 * Sets the clave.
	 *
	 * @param clave the new clave
	 */
	public void setClave(TipoNotificacion clave) {
		this.clave = clave;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfiguracionKey [idConfiguracion=" + idConfiguracion + ", clave=" + clave + ", usuario=" + usuario
				+ "]";
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

		if (!(obj instanceof ConfiguracionKey)) {
			return false;
		}

		if (!ConfiguracionKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		ConfiguracionKey tmp = (ConfiguracionKey) obj;

		try {

			if (tmp.clave == this.clave//
					&& tmp.idConfiguracion.equals(this.idConfiguracion) //
					&& tmp.usuario == this.usuario//
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
		return Objects.hash(this.clave, this.idConfiguracion, this.usuario);
	}
}
