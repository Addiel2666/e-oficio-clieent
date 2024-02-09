/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class AreaRevisor.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "areasRevisores")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "areaRevisorKey.idArea", "areaRevisorKey.revisor.id",
		"areaRevisorKey.idUsuario" }, message = "{Unique.descripcion}")
public class AreaRevisor implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5270495278910248982L;

	/** The area revisor key. */
	@EmbeddedId
	private AreaRevisorKey areaRevisorKey;

	/**
	 * Gets the area revisor key.
	 *
	 * @return the area revisor key
	 */
	public AreaRevisorKey getAreaRevisorKey() {
		return areaRevisorKey;
	}

	/**
	 * Sets the area revisor key.
	 *
	 * @param areaRevisorKey
	 *            the new area revisor key
	 */
	public void setAreaRevisorKey(AreaRevisorKey areaRevisorKey) {
		this.areaRevisorKey = areaRevisorKey;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "AreaRevisor [areaRevisorKey=" + areaRevisorKey + "]";
	}

}
