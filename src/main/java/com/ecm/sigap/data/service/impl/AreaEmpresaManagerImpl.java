package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AreaEmpresa;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("areaEmpresaService")
public class AreaEmpresaManagerImpl extends ManagerImpl<AreaEmpresa> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("areaEmpresaDao")
	protected void setDao(EntityDAO<AreaEmpresa> dao) {
		super.setDao(dao);
	}
}
