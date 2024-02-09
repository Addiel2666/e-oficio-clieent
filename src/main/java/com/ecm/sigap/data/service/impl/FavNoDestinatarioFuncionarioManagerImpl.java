/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.service.ManagerImpl;
import com.ecm.sigap.data.model.FavNoDestinatarioFuncionario;

@Service("favNoDestinatarioFuncionarioService")
public class FavNoDestinatarioFuncionarioManagerImpl extends ManagerImpl<FavNoDestinatarioFuncionario> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favNoDestinatarioFuncionarioDao")
	protected void setDao(EntityDAO<FavNoDestinatarioFuncionario> dao) {
		super.setDao(dao);
	}
}
