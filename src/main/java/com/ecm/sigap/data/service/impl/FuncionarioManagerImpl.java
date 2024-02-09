/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Funcionario;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Tema}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@Service("funcionarioService")
public class FuncionarioManagerImpl extends ManagerImpl<Funcionario> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("funcionarioDao")
	protected void setDao(EntityDAO<Funcionario> dao) {
		super.setDao(dao);
	}
}