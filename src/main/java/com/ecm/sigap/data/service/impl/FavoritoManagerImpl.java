package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Favorito;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("favoritoService")
public class FavoritoManagerImpl extends ManagerImpl<Favorito> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favoritoDao")
	protected void setDao(EntityDAO<Favorito> dao) {
		super.setDao(dao);
	}
}
