/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoCopiaTurnada;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Asunto}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("asuntoCopiaTurnadaService")
public class AsuntoCopiaTurnadaManagerImpl extends ManagerImpl<AsuntoCopiaTurnada> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("asuntoCopiaTurnadaDao")
	@Override
	protected void setDao(EntityDAO<AsuntoCopiaTurnada> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(AsuntoCopiaTurnada item) throws Exception {
		throw new Exception();
	}
}