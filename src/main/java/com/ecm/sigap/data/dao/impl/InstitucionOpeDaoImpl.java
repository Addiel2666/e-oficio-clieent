/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecm.sigap.data.dao.Dao;
import com.ecm.sigap.data.model.interop.InstitucionOpe;

/**
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
@Repository("institucionOpeDao")
@Transactional
public class InstitucionOpeDaoImpl extends Dao<InstitucionOpe> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.Dao#createCriteria(java.util.List,
	 * java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(InstitucionOpe.class, "institucionOpe");
		criteria.setResultTransformer(Transformers.TO_LIST);
		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;

	}

}