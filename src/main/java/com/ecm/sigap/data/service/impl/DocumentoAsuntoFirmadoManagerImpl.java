/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.DocumentoAsuntoFirmado;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link DocumentoAsuntoFirmado}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("documentoAsuntoFirmadoService")
public class DocumentoAsuntoFirmadoManagerImpl extends ManagerImpl<DocumentoAsuntoFirmado> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("documentoAsuntoFirmadoDao")
	protected void setDao(EntityDAO<DocumentoAsuntoFirmado> dao) {
		super.setDao(dao);
	}

}