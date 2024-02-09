/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Auditoria;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Auditoria}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@Service("auditoriaService")
public class AuditoriaManagerImpl extends ManagerImpl<Auditoria> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("auditoriaDao")
	protected void setDao(EntityDAO<Auditoria> dao) {
		super.setDao(dao);
	}
}