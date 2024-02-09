/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Embeddable
public class Antecedente {

	/** */
	@Column(name = "idAntecedentes")
	private String idAntecedentes;

	/**
	 * @return the idAntecedentes
	 */
	public String getIdAntecedentes() {
		return idAntecedentes;
	}

	/**
	 * @param idAntecedentes
	 *            the idAntecedentes to set
	 */
	public void setIdAntecedentes(String idAntecedentes) {
		this.idAntecedentes = idAntecedentes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Antecedente [idAntecedentes=" + idAntecedentes + "]";
	}

}
