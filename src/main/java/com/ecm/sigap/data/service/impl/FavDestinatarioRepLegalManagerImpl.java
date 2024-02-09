package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.service.ManagerImpl;
import com.ecm.sigap.data.model.FavDestinatarioRepLegal;

@Service("favDestinatarioRepLegalService")
public class FavDestinatarioRepLegalManagerImpl extends ManagerImpl<FavDestinatarioRepLegal> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favDestinatarioRepLegalDao")
	protected void setDao(EntityDAO<FavDestinatarioRepLegal> dao) {
		super.setDao(dao);
	}
}
