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
 * The Class Area Promotor.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Entity
@Table(name = "areasPromotores", //
		schema = "{SIGAP_SCHEMA}" //
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "areaPromotorKey.idArea",
		"areaPromotorKey.institucion.idInstitucion" }, message = "{Unique.descripcion}")
public final class AreaPromotor implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2442872675032224259L;

	/** The area promotor. */
	@EmbeddedId
	private AreaPromotorKey areaPromotorKey;

	/**
	 * Gets the area promotor key.
	 *
	 * @return the area promotor key
	 */
	public AreaPromotorKey getAreaPromotorKey() {
		return areaPromotorKey;
	}

	/**
	 * Sets the area promotor key.
	 *
	 * @param areaPromotorKey the new area promotor key
	 */
	public void setAreaPromotorKey(AreaPromotorKey areaPromotorKey) {
		this.areaPromotorKey = areaPromotorKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AreaPromotor [areaPromotorKey=" + areaPromotorKey + "]";
	}

}
