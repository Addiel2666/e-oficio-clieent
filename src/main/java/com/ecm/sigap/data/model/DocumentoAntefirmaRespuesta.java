/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "documentosAntefirma")
@Where(clause = "tipo = 'R'")
public class DocumentoAntefirmaRespuesta extends DocumentoAntefirma {

	/** */
	private static final long serialVersionUID = 7382128884378794876L;

	/** */
	@OneToOne
	@JoinColumn(name = "id", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private RespuestaConsulta respuestaConsulta;

	/**
	 * @return the respuestaConsulta
	 */
	public RespuestaConsulta getRespuestaConsulta() {
		return respuestaConsulta;
	}

	/**
	 * @param respuestaConsulta the respuestaConsulta to set
	 */
	public void setRespuestaConsulta(RespuestaConsulta respuestaConsulta) {
		this.respuestaConsulta = respuestaConsulta;
	}
}
