/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.ValorDocumentalPrimario;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link ValorDocumentalPrimario}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Service("valorDocumentalPrimarioService")
public class ValorDocumentalPrimarioManagerImpl extends ManagerImpl<ValorDocumentalPrimario> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("valorDocumentalPrimarioDao")
	protected void setDao(EntityDAO<ValorDocumentalPrimario> dao) {
		super.setDao(dao);
	}

}