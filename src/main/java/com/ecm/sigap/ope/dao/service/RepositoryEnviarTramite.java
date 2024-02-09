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

import org.springframework.stereotype.Service;

import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.ope.dao.model.EnvioTramites;

@Service("repositoryEnviarTramite")
public class RepositoryEnviarTramite extends Repository<EnvioTramites>{

	@Override
	protected String getIdFieldName() {
		
		return "id";
	}

	@Override
	protected void addRestrictions(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery,
			Root<EnvioTramites> root, RequestWrapper<EnvioTramites> searchObject) {
		
		List<Predicate> restrictions = new ArrayList<Predicate>();

		criteriaQuery.where(restrictions.toArray(new Predicate[] {}));
	}

	@Override
	protected void addOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery<EnvioTramites> criteriaQuery,
			Root<EnvioTramites> root, RequestWrapper<EnvioTramites> searchObject) {
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

}
