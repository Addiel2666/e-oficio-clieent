/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;

import org.hibernate.annotations.Type;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Embeddable
public class DiaFestivoKey implements Serializable {

	/** */
	private static final long serialVersionUID = -5392134291121863360L;

	/**
	 * Dia Festivo dentro del sistema
	 */
	@Type(type = "java.util.Date")
	@Column(name = "dia", unique = true, nullable = false)
	private Date dia;

	/** */
	@Column(name = "idCalendario")
	@DefaultValue(value = "0")
	@NotNull
	private Integer idCalendario;

	/**
	 * Obtiene el Dia Festivo
	 * 
	 * @return Dia Festivo
	 */
	public Date getDia() {
		return this.dia;
	}

	/**
	 * Asigna el Dia Festivo
	 * 
	 * @param dia
	 *            Dia Festivo
	 */
	public void setDia(Date dia) {
		this.dia = dia;
	}

	/**
	 * @return the idCalendario
	 */
	public Integer getIdCalendario() {
		return idCalendario;
	}

	/**
	 * @param idCalendario
	 *            the idCalendario to set
	 */
	public void setIdCalendario(Integer idCalendario) {
		this.idCalendario = idCalendario;
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

		if (!(obj instanceof DiaFestivoKey)) {
			return false;
		}

		if (!DiaFestivoKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		DiaFestivoKey tmp = (DiaFestivoKey) obj;

		try {

			if (tmp.dia == this.dia//
					&& tmp.idCalendario == this.idCalendario//
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
		return Objects.hash(this.dia, this.idCalendario);
	}

}
