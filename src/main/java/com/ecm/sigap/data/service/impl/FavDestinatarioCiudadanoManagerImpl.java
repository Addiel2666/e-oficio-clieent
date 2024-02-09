package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.service.ManagerImpl;
import com.ecm.sigap.data.model.FavDestinatarioCiudadano;

@Service("favDestinatarioCiudadanoService")
public class FavDestinatarioCiudadanoManagerImpl extends ManagerImpl<FavDestinatarioCiudadano> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favDestinatarioCiudadanoDao")
	protected void setDao(EntityDAO<FavDestinatarioCiudadano> dao) {
		super.setDao(dao);
	}
}
