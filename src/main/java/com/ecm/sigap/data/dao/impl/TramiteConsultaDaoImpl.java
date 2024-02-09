package com.ecm.sigap.data.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecm.sigap.data.dao.Dao;
import com.ecm.sigap.data.model.TramiteConsulta;

@Repository("tramiteConsultaDao")
@Transactional
public class TramiteConsultaDaoImpl extends Dao<TramiteConsulta>{

	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(TramiteConsulta.class, "tramiteConsulta");

		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;
	}
}