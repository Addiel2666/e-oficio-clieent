/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class Configuracion.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "configuraciones")
@UniqueKey(columnNames = { "configuracionKey.idConfiguracion", "configuracionKey.clave",
		"configuracionKey.usuario.idUsuario" }, message = "{Unique.descripcion}")
public class Configuracion implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2069842214320896435L;

	/** The id. */
	@EmbeddedId
	private ConfiguracionKey configuracionKey;

	/** The valor. */
	@Column(name = "valor")
	private String valor;

	/**
	 * Gets the configuracion key.
	 *
	 * @return the configuracion key
	 */
	public ConfiguracionKey getConfiguracionKey() {
		return configuracionKey;
	}

	/**
	 * Sets the configuracion key.
	 *
	 * @param configuracionKey
	 *            the new configuracion key
	 */
	public void setConfiguracionKey(ConfiguracionKey configuracionKey) {
		this.configuracionKey = configuracionKey;
	}

	/**
	 * Gets the valor.
	 *
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * Sets the valor.
	 *
	 * @param valor
	 *            the new valor
	 */
	public void setValor(String valor) {
		this.valor = valor;
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
		return "Configuracion [configuracionKey=" + configuracionKey + ", valor=" + valor + "]";
	}

}
