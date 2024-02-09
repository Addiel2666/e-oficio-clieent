/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.model.Area;

/**
 * The Class DestinatariosMinutario.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Embeddable
public class DestinatariosMinutario implements Comparable<DestinatariosMinutario> {

	/** The id destinatario. */
	@Column(name = "idDestinatario")
	private String idDestinatario;

	/** The status. */
	@Column(name = "idTipoDestinatario")
	@Enumerated(EnumType.ORDINAL)
	private TipoDestinatario idTipoDestinatario;

	/** The id area destinatario. */
	@OneToOne
	@JoinColumn(name = "idAreaDestinatario", nullable = false)
	@Fetch(FetchMode.SELECT)
	private Area idAreaDestinatario;

	/** The orden. */
	@Column(name = "orden")
	private Integer orden;

	/**
	 * Gets the id destinatario.
	 *
	 * @return the id destinatario
	 */
	public String getIdDestinatario() {
		return idDestinatario;
	}

	/**
	 * Sets the id destinatario.
	 *
	 * @param idDestinatario the new id destinatario
	 */
	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * Gets the id tipo destinatario.
	 *
	 * @return the id tipo destinatario
	 */
	public TipoDestinatario getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	/**
	 * Sets the id tipo destinatario.
	 *
	 * @param idTipoDestinatario the new id tipo destinatario
	 */
	public void setIdTipoDestinatario(TipoDestinatario idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	/**
	 * Gets the id area destinatario.
	 *
	 * @return the id area destinatario
	 */
	public Area getIdAreaDestinatario() {
		return idAreaDestinatario;
	}

	/**
	 * Sets the id area destinatario.
	 *
	 * @param idAreaDestinatario the new id area destinatario
	 */
	public void setIdAreaDestinatario(Area idAreaDestinatario) {
		this.idAreaDestinatario = idAreaDestinatario;
	}

	/**
	 * Gets the orden.
	 *
	 * @return the orden
	 */
	public Integer getOrden() {
		return orden;
	}

	/**
	 * Sets the orden.
	 *
	 * @param orden the new orden
	 */
	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DestinatariosMinutario [idDestinatario=" + idDestinatario + ", idTipoDestinatario=" + idTipoDestinatario
				+ ", idAreaDestinatario=" + idAreaDestinatario + ", orden=" + orden + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DestinatariosMinutario o) {

		if (this.getOrden() != null && o.getOrden() != null)
			return this.getOrden() - o.getOrden();
		else
			return 0;
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

		if (!(obj instanceof DestinatariosMinutario)) {
			return false;
		}

		if (!DestinatariosMinutario.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		DestinatariosMinutario tmp = (DestinatariosMinutario) obj;

		try {

			if (tmp.hashCode() == this.hashCode())
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
		return Objects.hash(this.idAreaDestinatario.getIdArea(), this.idDestinatario,
				this.idTipoDestinatario.getStatus());
	}

}
