/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */

package com.ecm.sigap.data.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecm.sigap.data.dao.Dao;
import com.ecm.sigap.data.model.DestinatarioGrupoEnvio;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Repository("destinatarioGrupoEnvioDao")
@Transactional
public class DestinatarioGrupoEnvioDaoImpl extends Dao<DestinatarioGrupoEnvio> {

	@Override
	protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		Criteria criteria = getSession().createCriteria(DestinatarioGrupoEnvio.class);

		processCriteria(criteria, restrictions, orders, projections, fetchSize, firstResult);

		return criteria;

	}

}
