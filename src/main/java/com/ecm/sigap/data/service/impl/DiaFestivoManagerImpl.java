/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.DiaFestivo;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Acceso}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Service("diaFestivoService")
public class DiaFestivoManagerImpl extends ManagerImpl<DiaFestivo> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("diaFestivoDao")
	@Override
	public void setDao(EntityDAO<DiaFestivo> dao) {
		super.setDao(dao);
	}
}