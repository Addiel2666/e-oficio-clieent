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
import com.ecm.sigap.data.model.Respuesta;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Repository("respuestaDao")
@Transactional
public class RespuestaDaoImpl extends Dao<Respuesta> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.Dao#createCriteria(java.util.List,
	 * java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(Respuesta.class, "respuesta");

		criteria.createAlias("areaDestino", "areaDestino", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("status", "status", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("area", "area", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("area.titular", "titular", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("area.institucion", "institucion", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("areaDestino.titular", "areaDestinoTitular", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("areaDestino.institucion", "areaDestinoInstitucion", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("tipoRespuesta", "tipoRespuesta");

		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;

	}

}