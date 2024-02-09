package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class ParametroApp.
 *
 * @author hhernandez
 * @version 1.0
 */
@Entity
@Table(name = "ParametrosApp")
@UniqueKey(columnNames = { "idSeccion", "idClave" }, message = "${Unique.descripcion}")
@IdClass(ParametroAppPK.class)
public class ParametroApp implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4585127273437399508L;

	@Id
	@Column(name = "IDSECCION")
	private String idSeccion;

	@Id
	@Column(name = "IDCLAVE")
	private String idClave;

	@Column(name = "VALOR")
	private String valor;

	/**
	 * @return the idSeccion
	 */
	public String getIdSeccion() {
		return idSeccion;
	}

	/**
	 * @param idSeccion
	 *            the idSeccion to set
	 */
	public void setIdSeccion(String idSeccion) {
		this.idSeccion = idSeccion;
	}

	/**
	 * @return the idClave
	 */
	public String getIdClave() {
		return idClave;
	}

	/**
	 * @param idClave
	 *            the idClave to set
	 */
	public void setIdClave(String idClave) {
		this.idClave = idClave;
	}

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor
	 *            the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ParametroApp [idSeccion=" + idSeccion + ", idClave=" + idClave + ", valor=" + valor + "]";
	}

}
