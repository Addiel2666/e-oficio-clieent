package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("parametroService")
public class ParametroManagerImpl extends ManagerImpl<Parametro> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("parametroDao")
	protected void setDao(EntityDAO<Parametro> dao) {
		super.setDao(dao);
	}

}
