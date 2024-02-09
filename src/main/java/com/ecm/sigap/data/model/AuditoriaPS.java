/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "auditoriaps")
@SequenceGenerator(name = "SEQ_AUDITORIA", sequenceName = "SECAUDITORIA", allocationSize = 1)
public final class AuditoriaPS implements Serializable {

	/**  */
	private static final long serialVersionUID = -2371074438014929325L;

	/** id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AUDITORIA")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "id", nullable = false)
	private Integer id;

	/** fecha registro. */
	@Column(name = "fechaRegistro", nullable = false, updatable = false)
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** resultado de la prueba. */
	@Column(name = "resultado", nullable = false)
	private String resultado;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the fechaRegistro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * @param fechaRegistro the fechaRegistro to set
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * @return the resultado
	 */
	public String getResultado() {
		return resultado;
	}

	/**
	 * @param resultado the resultado to set
	 */
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	@Override
	public String toString() {
		return "AuditoriaPS [id=" + id + ", fechaRegistro=" + fechaRegistro + ", resultado=" + resultado + "]";
	}

}
