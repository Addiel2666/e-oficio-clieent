package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("rolService")
public class RolManagerImpl extends ManagerImpl<Rol> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("rolDao")
	protected void setDao(EntityDAO<Rol> dao) {
		super.setDao(dao);
	}

}
