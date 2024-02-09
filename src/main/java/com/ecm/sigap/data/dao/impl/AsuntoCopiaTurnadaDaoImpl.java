/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecm.sigap.data.dao.Dao;
import com.ecm.sigap.data.model.AsuntoCopiaTurnada;;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Repository("asuntoCopiaTurnadaDao")
@Transactional
public class AsuntoCopiaTurnadaDaoImpl extends Dao<AsuntoCopiaTurnada> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.Dao#createCriteria(java.util.List,
	 * java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(AsuntoCopiaTurnada.class, "asunto");

		criteria.createAlias("areaDestino", "areaDestino", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("areaDestino.titular", "titular", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("areaDestino.institucion", "institucionDestino", JoinType.LEFT_OUTER_JOIN);

		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;
	}

}