/**
 * Copyright (c) 2023 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AsuntoRecibidoConsulta;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link AsuntoRecibidoConsulta}.
 * 
 * @author ECM Solutions
 * @version 1.0
 *
 */
@Service("asuntoRecibidoConsultaService")
public class AsuntoRecibidoConsultaManagerImpl extends ManagerImpl<AsuntoRecibidoConsulta> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("asuntoRecibidoConsultaDao")
	protected void setDao(EntityDAO<AsuntoRecibidoConsulta> dao) {
		super.setDao(dao);
	}
}