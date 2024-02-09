/**
 * Copyright (c) 2020 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FolioPSMultiple;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link FolioPSMultiple}.
 * 
 * @author ECM SOLUTIONS
 * @version 1.0
 * 
 */
@Service("foliopsMultipleService")
public class FoliopsMultipleManagerImpl extends ManagerImpl<FolioPSMultiple> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("foliopsMultipleDao")
	@Override
	public void setDao(EntityDAO<FolioPSMultiple> dao) {
		super.setDao(dao);
	}
}