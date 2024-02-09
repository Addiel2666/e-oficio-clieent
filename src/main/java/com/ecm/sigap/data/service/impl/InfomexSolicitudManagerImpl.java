/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.InfomexSolicitud;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link InfomexSolicitud}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@Service("infomexSolicitudService")
public class InfomexSolicitudManagerImpl extends ManagerImpl<InfomexSolicitud> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("infomexSolicitudDao")
	protected void setDao(EntityDAO<InfomexSolicitud> dao) {
		super.setDao(dao);
	}

}