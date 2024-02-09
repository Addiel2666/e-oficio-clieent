/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.GrupoEnvio;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link GrupoEnvio}.
 * 
 * @author Angel Colina
 * 
 */
@Service("grupoEnvioService")
public class GrupoEnvioManagerImpl extends ManagerImpl<GrupoEnvio> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("grupoEnvioDao")
	@Override
	protected void setDao(EntityDAO<GrupoEnvio> dao) {
		super.setDao(dao);
	}
}
