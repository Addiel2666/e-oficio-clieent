/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Representante}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("representanteService")
public class RepresentanteManagerImpl extends ManagerImpl<Representante> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("representanteDao")
	protected void setDao(EntityDAO<Representante> dao) {
		super.setDao(dao);
	}

}