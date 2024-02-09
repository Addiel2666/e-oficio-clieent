package com.ecm.sigap.data.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.service.ManagerImpl;
import com.ecm.sigap.data.model.FavDestinatarioFuncionario;

@Service("favDestinatarioFuncionarioService")
public class FavDestinatarioFuncionarioManagerImpl extends ManagerImpl<FavDestinatarioFuncionario> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("favDestinatarioFuncionarioDao")
	protected void setDao(EntityDAO<FavDestinatarioFuncionario> dao) {
		super.setDao(dao);
	}
}
