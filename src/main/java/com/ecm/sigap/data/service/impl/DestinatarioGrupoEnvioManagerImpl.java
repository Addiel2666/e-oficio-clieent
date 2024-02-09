/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.DestinatarioGrupoEnvio;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link DestinatarioGrupoEnvio}.
 * 
 * @author Alfredo Morales
 * 
 */
@Service("destinatarioGrupoEnvioService")
public class DestinatarioGrupoEnvioManagerImpl extends ManagerImpl<DestinatarioGrupoEnvio> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("destinatarioGrupoEnvioDao")
	@Override
	protected void setDao(EntityDAO<DestinatarioGrupoEnvio> dao) {
		super.setDao(dao);
	}
}