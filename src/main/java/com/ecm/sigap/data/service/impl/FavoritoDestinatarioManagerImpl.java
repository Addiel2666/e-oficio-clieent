package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FavDestinatario;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("favoritoDestinatarioService")
public class FavoritoDestinatarioManagerImpl extends ManagerImpl<FavDestinatario> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favoritoDestinatarioDao")
	protected void setDao(EntityDAO<FavDestinatario> dao) {
		super.setDao(dao);
	}
}
