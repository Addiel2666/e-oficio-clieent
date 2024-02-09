/**
k * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Path;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Path}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("pathService")
public class PathManagerImpl extends ManagerImpl<Path> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("pathDao")
	@Override
	protected void setDao(EntityDAO<Path> dao) {
		super.setDao(dao);
	}

}