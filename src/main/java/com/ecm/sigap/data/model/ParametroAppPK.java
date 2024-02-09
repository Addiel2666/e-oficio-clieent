/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */

package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Class ParametroAppPK.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
public class ParametroAppPK implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4452675208965182099L;

	/** The id seccion. */
	protected String idSeccion;

	/** The id clave. */
	protected String idClave;

	/**
	 * Instantiates a new parametro app PK.
	 */
	public ParametroAppPK() {
	}

	/**
	 * Instantiates a new parametro app PK.
	 *
	 * @param idSeccion
	 *            the id seccion
	 * @param idClave
	 *            the id clave
	 */
	public ParametroAppPK(String idSeccion, String idClave) {
		this.idSeccion = idSeccion;
		this.idClave = idClave;
	}

	/**
	 * Gets the id seccion.
	 *
	 * @return the idSeccion
	 */
	public String getIdSeccion() {
		return idSeccion;
	}

	/**
	 * Sets the id seccion.
	 *
	 * @param idSeccion
	 *            the idSeccion to set
	 */
	public void setIdSeccion(String idSeccion) {
		this.idSeccion = idSeccion;
	}

	/**
	 * Gets the id clave.
	 *
	 * @return the idClave
	 */
	public String getIdClave() {
		return idClave;
	}

	/**
	 * Sets the id clave.
	 *
	 * @param idClave
	 *            the idClave to set
	 */
	public void setIdClave(String idClave) {
		this.idClave = idClave;
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

		if (!(obj instanceof ParametroAppPK)) {
			return false;
		}

		if (!ParametroAppPK.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		ParametroAppPK tmp = (ParametroAppPK) obj;

		try {

			if (tmp.idClave.equals(this.idClave)//
					&& tmp.idSeccion.equals(this.idSeccion)//
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
		return Objects.hash(this.idClave, this.idSeccion);
	}

}
