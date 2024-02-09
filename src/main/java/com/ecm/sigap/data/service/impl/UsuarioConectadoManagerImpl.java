/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.UsuarioConectado;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Usuario}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("usuarioConectadoService")
public class UsuarioConectadoManagerImpl extends ManagerImpl<UsuarioConectado> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("usuarioConectadoDao")
	protected void setDao(EntityDAO<UsuarioConectado> dao) {
		super.setDao(dao);
	}

}