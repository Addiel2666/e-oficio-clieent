package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * The Class AreaPromotorKey.
 */
@Embeddable
public class MinutarioDestinatarioKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6104259278179030025L;

	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;

	/** The destinatario. */

	@Column(name = "idDestinatario")
	private String idDestinatario;

	/** The id tipo destinatario. */
	@Column(name = "idTipoDestinatario")
	private Integer idTipoDestinatario;

	/** The id area destinatario. */

	@OneToOne 
	@JoinColumn(name = "idAreaDestinatario")
	@Fetch(value = FetchMode.SELECT)
	private Area areaDestinatario;

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea
	 *            the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

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
	 * @param idDestinatario
	 *            the new id destinatario
	 */
	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * Gets the id tipo destinatario.
	 *
	 * @return the id tipo destinatario
	 */
	public Integer getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	/**
	 * Sets the id tipo destinatario.
	 *
	 * @param idTipoDestinatario
	 *            the new id tipo destinatario
	 */
	public void setIdTipoDestinatario(Integer idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	/**
	 * Gets the area destinatario.
	 *
	 * @return the area destinatario
	 */
	public Area getAreaDestinatario() {
		return areaDestinatario;
	}

	/**
	 * Sets the area destinatario.
	 *
	 * @param areaDestinatario
	 *            the new area destinatario
	 */
	public void setAreaDestinatario(Area areaDestinatario) {
		this.areaDestinatario = areaDestinatario;
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
		return "MinutarioDestinatarioKey [idArea=" + idArea + ", idDestinatario=" + idDestinatario
				+ ", idTipoDestinatario=" + idTipoDestinatario + ", areaDestinatario=" + areaDestinatario + "]";
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

		if (!(obj instanceof MinutarioDestinatarioKey)) {
			return false;
		}

		if (!MinutarioDestinatarioKey.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		MinutarioDestinatarioKey tmp = (MinutarioDestinatarioKey) obj;

		try {

			if (tmp.idDestinatario.equals(this.idDestinatario)//
					&& tmp.idArea == this.idArea//
					&& tmp.idTipoDestinatario == this.idTipoDestinatario//
					&& tmp.areaDestinatario == this.areaDestinatario//
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
		return Objects.hash(this.areaDestinatario, this.idArea, this.idDestinatario, this.idTipoDestinatario);
	}

}