/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.TipoEvento;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link TipoEvento}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Service("tipoEventoService")
public class TipoEventoManagerImpl extends ManagerImpl<TipoEvento> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("tipoEventoDao")
	protected void setDao(EntityDAO<TipoEvento> dao) {
		super.setDao(dao);
	}

}
