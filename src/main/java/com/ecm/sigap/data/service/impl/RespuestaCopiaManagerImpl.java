/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.RespuestaCopia;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link CopiaRespuesta}.
 * 
 * @author Adaulfo Herrera
 * @version 1.0
 *
 */
@Service("respuestaCopiaService")
public class RespuestaCopiaManagerImpl extends ManagerImpl<RespuestaCopia> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("respuestaCopiaDao")
	protected void setDao(EntityDAO<RespuestaCopia> dao) {
		super.setDao(dao);
	}

}