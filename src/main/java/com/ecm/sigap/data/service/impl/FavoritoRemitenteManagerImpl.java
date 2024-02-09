package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FavoritoRemitente;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("favoritoRemitenteService")
public class FavoritoRemitenteManagerImpl extends ManagerImpl<FavoritoRemitente> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favoritoRemitenteDao")
	protected void setDao(EntityDAO<FavoritoRemitente> dao) {
		super.setDao(dao);
	}

}
