/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AsuntoConsultaEspecial;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Asunto}.
 * 
 * @author Jennifer Miranda
 * @version 1.0
 *
 */
@Service("asuntoConsultaEspecialService")
public class AsuntoConsultaEspecialManagerImpl extends ManagerImpl<AsuntoConsultaEspecial> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("asuntoConsultaEspecialDao")
	protected void setDao(EntityDAO<AsuntoConsultaEspecial> dao) {
		super.setDao(dao);
	}

	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {
		return super.search(restrictions, orders, projections, fetchSize, firstResult);
	}

	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		return super.search(restrictions, orders);
	}

}