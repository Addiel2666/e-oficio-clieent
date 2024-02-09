package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.TitularNoFavorito;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("titularNoFavoritoService")
public class TitularNoFavoritoManagerImpl extends ManagerImpl<TitularNoFavorito> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("titularNoFavoritoDao")
	protected void setDao(EntityDAO<TitularNoFavorito> dao) {
		super.setDao(dao);
	}
}
