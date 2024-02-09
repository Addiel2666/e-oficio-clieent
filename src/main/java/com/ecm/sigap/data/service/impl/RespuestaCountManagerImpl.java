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
import com.ecm.sigap.data.model.RespuestaCount;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link RespuestaCount}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("respuestaCountService")
public class RespuestaCountManagerImpl extends ManagerImpl<RespuestaCount> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("respuestaCountDao")
	protected void setDao(EntityDAO<RespuestaCount> dao) {
		super.setDao(dao);
	}

	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {
		return super.search(restrictions, null, projections, fetchSize, firstResult);
	}

	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		return super.search(restrictions, null);
	}

}