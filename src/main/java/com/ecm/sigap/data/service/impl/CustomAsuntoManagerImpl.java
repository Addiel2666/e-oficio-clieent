/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.CustomAsunto;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link CustomAsunto}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("customAsuntoService")
public class CustomAsuntoManagerImpl extends ManagerImpl<CustomAsunto> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("customAsuntoDao")
	@Override
	protected void setDao(EntityDAO<CustomAsunto> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(CustomAsunto item) throws Exception {

		super.save(item);
	}

}