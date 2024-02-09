/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.ws.rs.DefaultValue;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * Clase de entidad que representa la tabla DIASFESTIVOS
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 *          Creacion de la clase
 * 
 * @author Alfredo Morales
 * @version 1.0.1
 *
 */
@Entity
@Table(name = "diasFestivos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class DiaFestivo implements Serializable {

	/** Formato de la fecha */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7295604424465450148L;

	/** The acceso key. */
	@EmbeddedId
	private DiaFestivoKey key;

	/** */
	@Type(type = "java.util.Date")
	@Column(name = "diaFin")
	private Date diaFin;

	/** */
	@Column(name = "titulo")
	private String titulo;

	/** */
	@Column(name = "idIcono")
	@DefaultValue(value = "0")
	private Integer idIcono;

	/**
	 * Constructor por defecto de la clase
	 */
	public DiaFestivo() {
		super();
	}

	/**
	 * 
	 * @param dia
	 */
	public DiaFestivo(Date dia) {
		super();
		DiaFestivoKey key = new DiaFestivoKey();
		key.setDia(dia);
		this.setKey(key);
	}

	/**
	 * @return the key
	 */
	public DiaFestivoKey getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(DiaFestivoKey key) {
		this.key = key;
	}

	/**
	 * @return the diaFin
	 */
	public Date getDiaFin() {
		return diaFin;
	}

	/**
	 * @param diaFin
	 *            the diaFin to set
	 */
	public void setDiaFin(Date diaFin) {
		this.diaFin = diaFin;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DiaFestivo [dia=" + key.getDia() + ", diaFin=" + diaFin + ", titulo=" + titulo + ", idCalendario="
				+ key.getIdCalendario() + ", idIcono=" + idIcono + "]";
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
		result = prime * result + ((key.getDia() == null) ? 0 : key.getDia().hashCode());
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
		DiaFestivo other = (DiaFestivo) obj;
		if (key.getDia() == null) {
			if (other.key.getDia() != null)
				return false;
		} else if (!DATE_FORMAT.format(key.getDia()).equals(DATE_FORMAT.format(other.key.getDia())))
			return false;
		return true;
	}

	/**
	 * @return the idIcono
	 */
	public Integer getIdIcono() {
		return idIcono;
	}

	/**
	 * @param idIcono
	 *            the idIcono to set
	 */
	public void setIdIcono(Integer idIcono) {
		this.idIcono = idIcono;
	}

}
