package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AreaRemitente;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("areaRemitenteService")
public class AreaRemitenteManagerImpl extends ManagerImpl<AreaRemitente> {
	
	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("areaRemitenteDao")
	protected void setDao(EntityDAO<AreaRemitente> dao) {
		super.setDao(dao);
	}
	
}
