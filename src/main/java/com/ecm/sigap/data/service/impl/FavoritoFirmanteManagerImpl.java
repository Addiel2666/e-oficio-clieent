package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FavoritoFirmante;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("favoritoFirmanteService")
public class FavoritoFirmanteManagerImpl extends ManagerImpl<FavoritoFirmante> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favoritoFirmanteDao")
	protected void setDao(EntityDAO<FavoritoFirmante> dao) {
		super.setDao(dao);
	}

}
