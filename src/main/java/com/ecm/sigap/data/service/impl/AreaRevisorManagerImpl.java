/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AreaRevisor;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link AreaRevisor}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Service("areaRevisorService")
public class AreaRevisorManagerImpl extends ManagerImpl<AreaRevisor> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("areaRevisorDao")
	@Override
	protected void setDao(EntityDAO<AreaRevisor> dao) {
		super.setDao(dao);
	}

}
