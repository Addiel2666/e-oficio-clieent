/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.dao.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.stereotype.Repository;

import com.ecm.sigap.data.dao.Dao;
import com.ecm.sigap.data.model.InfomexEntidadFederativa;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Repository("infomexEntidadFederativaDao")
@Transactional
public class InfomexEntidadFederativaDaoImpl extends Dao<InfomexEntidadFederativa> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.Dao#createCriteria(java.util.List,
	 * java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(InfomexEntidadFederativa.class, "infomexEntidadFederativa");

		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;
	}
}