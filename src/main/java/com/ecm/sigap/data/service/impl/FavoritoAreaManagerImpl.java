package com.ecm.sigap.data.service.impl;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FavoritoArea;
import com.ecm.sigap.data.service.ManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("favoritoAreaService")
public class FavoritoAreaManagerImpl extends ManagerImpl<FavoritoArea> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favoritoAreaDao")
	protected void setDao(EntityDAO<FavoritoArea> dao) {
		super.setDao(dao);
	}

}
