/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FolioArchivistica;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("folioArchivisticaService")
public class FolioArchivisticaImpl extends ManagerImpl<FolioArchivistica> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("folioArchivisticaDao")
	protected void setDao(EntityDAO<FolioArchivistica> dao) {
		super.setDao(dao);
	}
}
