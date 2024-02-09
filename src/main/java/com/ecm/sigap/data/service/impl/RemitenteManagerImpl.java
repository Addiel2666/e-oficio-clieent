package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Remitente;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("remitenteService")
public class RemitenteManagerImpl extends ManagerImpl<Remitente> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("remitenteDao")
	protected void setDao(EntityDAO<Remitente> dao) {
		super.setDao(dao);
	}

}
