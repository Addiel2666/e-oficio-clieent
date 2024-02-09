/**
 * Copyright (c) 2023 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AsuntoAntecedente;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link AsuntoAntecedente}.
 * 
 * @author ECM Solutions
 * @version 1.0
 *
 */
@Service("asuntoAntecedenteService")
public class AsuntoAntecedenteManagerImpl extends ManagerImpl<AsuntoAntecedente> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("asuntoAntecedenteDao")
	public void setDao(EntityDAO<AsuntoAntecedente> dao) {
		super.setDao(dao);
	}

}