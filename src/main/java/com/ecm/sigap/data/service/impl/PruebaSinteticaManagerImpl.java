/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.PruebaSintetica;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Area}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("pruebaSinteticaService")
public class PruebaSinteticaManagerImpl extends ManagerImpl<PruebaSintetica> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("pruebaSinteticaDao")
	protected void setDao(EntityDAO<PruebaSintetica> dao) {
		super.setDao(dao);
	}

}