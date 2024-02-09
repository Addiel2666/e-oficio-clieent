/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.service.ManagerImpl;
import com.ecm.sigap.data.model.MinutarioDestinatario;

/**
 * Manejador en base de datos de objetos {@link MinutarioDestinatario}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Service("minutarioDestinatarioService")
public class MinutarioDestinatarioManagerImpl extends ManagerImpl<MinutarioDestinatario> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("minutarioDestinatarioDao")
	protected void setDao(EntityDAO<MinutarioDestinatario> dao) {
		super.setDao(dao);
	}

}
