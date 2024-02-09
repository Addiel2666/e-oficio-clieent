package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("permisoService")
public class PermisoManagerImpl extends ManagerImpl<Permiso> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("permisoDao")
	protected void setDao(EntityDAO<Permiso> dao) {
		super.setDao(dao);
	}
	
}
