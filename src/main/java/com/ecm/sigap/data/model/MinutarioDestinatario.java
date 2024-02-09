package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class MinutarioDestinatario.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "minutariosDestinatarios")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "minutarioDestinatarioKey.idArea", "minutarioDestinatarioKey.idDestinatario",
		"minutarioDestinatarioKey.idTipoDestinatario",
		"minutarioDestinatarioKey.areaDestinatario.idArea" }, message = "{Unique.descripcion}")
public class MinutarioDestinatario implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -765817223984319073L;

	/** The area promotor. */
	@EmbeddedId
	private MinutarioDestinatarioKey minutarioDestinatarioKey;

	/**
	 * Gets the minutario destinatario key.
	 *
	 * @return the minutario destinatario key
	 */
	public MinutarioDestinatarioKey getMinutarioDestinatarioKey() {
		return minutarioDestinatarioKey;
	}

	/**
	 * Sets the minutario destinatario key.
	 *
	 * @param minutarioDestinatarioKey
	 *            the new minutario destinatario key
	 */
	public void setMinutarioDestinatarioKey(MinutarioDestinatarioKey minutarioDestinatarioKey) {
		this.minutarioDestinatarioKey = minutarioDestinatarioKey;
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
		return "MinutarioDestinatario [minutarioDestinatarioKey=" + minutarioDestinatarioKey + "]";
	}

}
