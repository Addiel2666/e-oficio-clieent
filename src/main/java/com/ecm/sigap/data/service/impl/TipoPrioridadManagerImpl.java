/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.TipoPrioridad;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("tipoPrioridadService")
public class TipoPrioridadManagerImpl extends ManagerImpl<TipoPrioridad> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("tipoPrioridadDao")
	protected void setDao(EntityDAO<TipoPrioridad> dao) {
		super.setDao(dao);
	}
}
