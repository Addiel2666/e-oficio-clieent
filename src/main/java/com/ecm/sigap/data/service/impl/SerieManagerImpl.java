/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Serie;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("serieService")
public class SerieManagerImpl extends ManagerImpl<Serie> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("serieDao")
	protected void setDao(EntityDAO<Serie> dao) {
		super.setDao(dao);
	}
}
