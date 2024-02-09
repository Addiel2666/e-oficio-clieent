/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The Class InfomexSolicitud.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "parametros")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Parametro implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5754757677921303124L;

	/** The parametro key. */
	@EmbeddedId
	private ParametroKey parametroKey;

	/** The valor. */
	@Column(name = "valor")
	private String valor;

	/**
	 * Instantiates a new parametro.
	 */
	public Parametro() {
		super();
	}

	/**
	 * Instantiates a new parametro.
	 *
	 * @param parametroKey
	 *            the parametro key
	 * @param valor
	 *            the valor
	 */
	public Parametro(ParametroKey parametroKey, String valor) {
		super();
		this.parametroKey = parametroKey;
		this.valor = valor;
	}

	/**
	 * Gets the parametro key.
	 *
	 * @return the parametro key
	 */
	public ParametroKey getParametroKey() {
		return parametroKey;
	}

	/**
	 * Sets the parametro key.
	 *
	 * @param parametroKey
	 *            the new parametro key
	 */
	public void setParametroKey(ParametroKey parametroKey) {
		this.parametroKey = parametroKey;
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
		return "Parametro [parametroKey=" + parametroKey + ", valor=" + valor + "]";
	}

}
