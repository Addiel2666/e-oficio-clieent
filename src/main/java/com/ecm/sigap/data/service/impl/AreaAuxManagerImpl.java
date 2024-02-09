/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AreaAux;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link AreaAux}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("areaAuxService")
public class AreaAuxManagerImpl extends ManagerImpl<AreaAux> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("areaAuxDao")
	protected void setDao(EntityDAO<AreaAux> dao) {
		super.setDao(dao);
	}

}