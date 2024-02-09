/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.dao.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.ope.dao.model.Mensaje;

/**
 * @author Alfredo Morales
 *
 */
@Service("repositoryMensaje")
public final class RepositoryMensaje extends Repository<Mensaje> {

	@Override
	protected void addRestrictions( //
			CriteriaBuilder criteriaBuilder, //
			CriteriaQuery<?> criteriaQuery, //
			Root<Mensaje> root, //
			RequestWrapper<Mensaje> searchObject) {

		Mensaje so = searchObject.getObject();

		List<Predicate> restrictions = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(so.getId()))
			restrictions.add( //
					criteriaBuilder.equal( //
							root.get("id").as(String.class) //
							, //
							so.getId()));

		criteriaQuery.where(restrictions.toArray(new Predicate[] {}));

	}

	@Override
	protected void addOrderBy(CriteriaBuilder criteriaBuilder, //
			CriteriaQuery<Mensaje> criteriaQuery, //
			Root<Mensaje> root, //
			RequestWrapper<Mensaje> searchObject) {

		if (searchObject.getOrders() != null && !searchObject.getOrders().isEmpty()) {

			Set<Order> orders = searchObject.getOrders().stream().map(o -> {
				Order order_;
				if (o.isDesc())
					order_ = criteriaBuilder.desc(root.get(o.getField()));
				else
					order_ = criteriaBuilder.asc(root.get(o.getField()));

				return order_;
			}).collect(Collectors.toSet());

			criteriaQuery.orderBy(orders.toArray(new Order[] {}));

		}
	}

	@Override
	protected String getIdFieldName() {
		return "id";
	}

}
