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

@Entity
@Table(name = "documentosAntefirma")
@Where(clause = "tipo = 'A'")
public class DocumentoAntefirmaAsunto extends DocumentoAntefirma {

	/** */
	private static final long serialVersionUID = -8818344335737007846L;

	/** */
	@OneToOne
	@JoinColumn(name = "id", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private AsuntoConsulta asuntoConsulta;

	/**
	 * Gets the asunto consulta.
	 *
	 * @return the asunto consulta
	 */
	public AsuntoConsulta getAsuntoConsulta() {
		return asuntoConsulta;
	}

	/**
	 * Sets the asunto consulta.
	 *
	 * @param asuntoConsulta the new asunto consulta
	 */
	public void setAsuntoConsulta(AsuntoConsulta asuntoConsulta) {
		this.asuntoConsulta = asuntoConsulta;
	}

}
