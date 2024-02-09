/**
 * Copyright (c) 2023 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AsuntoRechazadoConsulta;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link AsuntoRechazadoConsulta}.
 * 
 * @author ECM Solutions
 * @version 1.0
 *
 */
@Service("asuntoRechazadoConsultaService")
public class AsuntoRechazadoConsultaManagerImpl extends ManagerImpl<AsuntoRechazadoConsulta> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("asuntoRechazadoConsultaDao")
	protected void setDao(EntityDAO<AsuntoRechazadoConsulta> dao) {
		super.setDao(dao);
	}
}