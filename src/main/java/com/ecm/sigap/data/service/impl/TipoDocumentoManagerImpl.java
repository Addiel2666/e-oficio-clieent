/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.TipoDocumento;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link TipoDocumento}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Service("tipoDocumentoService")
public class TipoDocumentoManagerImpl extends ManagerImpl<TipoDocumento> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("tipoDocumentoDao")
	protected void setDao(EntityDAO<TipoDocumento> dao) {
		super.setDao(dao);
	}

}
