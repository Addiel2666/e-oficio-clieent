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
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.ecm.sigap.data.dao.Dao;
import com.ecm.sigap.data.model.InfomexSolicitud;

/**
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Repository("infomexSolicitudDao")
@Transactional
public class InfomexSolicitudDaoImpl extends Dao<InfomexSolicitud> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.Dao#createCriteria(java.util.List,
	 * java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(InfomexSolicitud.class, "infomexSolicitudDao");
		criteria.createAlias("status", "status", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("archivo", "archivo",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("modoEntrega", "modoEntrega",JoinType.LEFT_OUTER_JOIN);
		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;
	}
}