package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.ParametroApp;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("parametroAppService")
public class ParametroAppManageImpl extends ManagerImpl<ParametroApp> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("parametroAppDao")
	protected void setDao(EntityDAO<ParametroApp> dao) {
		super.setDao(dao);
	}

}
