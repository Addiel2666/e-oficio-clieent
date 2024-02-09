package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Configuracion;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("configuracionService")
public class ConfiguracionManagerImpl extends ManagerImpl<Configuracion> {
	
	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("configuracionDao")
	protected void setDao(EntityDAO<Configuracion> dao) {
		super.setDao(dao);
	}
	
}
