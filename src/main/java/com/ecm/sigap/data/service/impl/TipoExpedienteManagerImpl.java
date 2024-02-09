/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.TipoExpediente;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link TiposExpediente}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Service("tipoExpedienteService")
public class TipoExpedienteManagerImpl extends ManagerImpl<TipoExpediente> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("tipoExpedienteDao")
	protected void setDao(EntityDAO<TipoExpediente> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
//	@Override
//	public void save(TipoExpediente item) throws Exception {
//
//		String idContentArea = item.getArea().getContentId();
//		String folderIdTipoExpediente = EndpointDispatcher.getInstance().createFolderIntoId(//
//				idContentArea, //
//				environment.getProperty("folderTypeTipoExpediente"), //
//				item.getDescripcion());
//		
//		item.setContentId(folderIdTipoExpediente);
//
//		super.save(item);
//	}

}
