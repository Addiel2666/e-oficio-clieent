package com.ecm.sigap.data.service.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AsuntoDetalleModal;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("asuntoDetalleModalService")
public class AsuntoDetalleModalManagerImpl extends ManagerImpl<AsuntoDetalleModal>{

	@Autowired
	@Qualifier("asuntoDetalleModalDao")
	protected void setDao(EntityDAO<AsuntoDetalleModal> dao) {
		super.setDao(dao);
	}
	
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {
		return super.search(restrictions, orders, projections, fetchSize, firstResult);
	}

	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		return super.search(restrictions, orders);
	}
}
