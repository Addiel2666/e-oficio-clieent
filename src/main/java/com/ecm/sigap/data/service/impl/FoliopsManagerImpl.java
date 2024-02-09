/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FolioPS;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link FolioPS}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Service("foliopsService")
public class FoliopsManagerImpl extends ManagerImpl<FolioPS> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("foliopsDao")
	@Override
	public void setDao(EntityDAO<FolioPS> dao) {
		super.setDao(dao);
	}
}