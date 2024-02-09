/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.ecm.sigap.data.model.util.CatalogoArchivistica;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "catalogosArchivistica")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@Where(clause = " TIPOCATALOGO = 'R' ")
public class Serie extends CatalogoArchivistica implements Serializable {

	/** */
	private static final long serialVersionUID = -8625204029000523195L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Serie " + super.toString();
	}

}
