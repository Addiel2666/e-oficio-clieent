/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.service.ManagerImpl;
import com.ecm.sigap.eCiudadano.model.AcuseFirmado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
@Service("acuseFirmadoService")
public class AcuseFirmadoManagerImpl extends ManagerImpl<AcuseFirmado> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("acuseFirmadoDao")
	@Override
	protected void setDao(EntityDAO<AcuseFirmado> dao) {
		super.setDao(dao);
	}
}
