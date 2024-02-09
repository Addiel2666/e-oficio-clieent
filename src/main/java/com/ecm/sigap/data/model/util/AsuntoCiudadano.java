/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.model.Ciudadano;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Embeddable
public class AsuntoCiudadano {

	/** */
	@OneToOne
	@JoinColumn(name = "idCiudadano")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Ciudadano ciudadano;

	/**
	 * @return the ciudadano
	 */
	public Ciudadano getCiudadano() {
		return ciudadano;
	}

	/**
	 * @param ciudadano the ciudadano to set
	 */
	public void setCiudadano(Ciudadano ciudadano) {
		this.ciudadano = ciudadano;
	}

}
