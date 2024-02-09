/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoAsuntoKey;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link DocumentoAsunto}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
@Service("documentoAsuntoService")
public class DocumentoAsuntoManagerImpl extends ManagerImpl<DocumentoAsunto> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#fetch(java.io.Serializable)
	 */
	@Override
	public DocumentoAsunto fetch(Serializable id) {

		// Asignamos los valores para crear el Key para buscar el
		// DocumentoAsunto
		DocumentoAsuntoKey documentoKey = new DocumentoAsuntoKey();
		DocumentoAsunto documento = (DocumentoAsunto) id;
		documentoKey.setIdAsunto(documento.getIdAsunto());
		documentoKey.setObjectId(documento.getObjectId());

		DocumentoAsunto item = super.fetch(documentoKey);

		if (null != item) {
			// Complementamos la informacion del Documento con la informacion
			// del repositorio
			getRepoInfo(item);
		}

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("documentoAsuntoDao")
	protected void setDao(EntityDAO<DocumentoAsunto> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(DocumentoAsunto item) throws Exception {
		item.setFechaRegistro(new Date());
		super.save(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List, org.hibernate.criterion.ProjectionList, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public List<?> search(List<Criterion> restrictions, //
			List<Order> orders, ProjectionList projections, //
			Integer fetchSize, Integer firstResult) {

		List<?> search = super.search(restrictions, orders, projections, fetchSize, firstResult);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoAsunto>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List)
	 */
	@Override
	public List<?> search(List<Criterion> restrictions) {
		List<?> search = super.search(restrictions);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoAsunto>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List)
	 */
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		List<?> search = super.search(restrictions, orders);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoAsunto>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List)
	 */
	@Override
	public List<?> execNativeQuery(String sqlquery, HashMap<String, Object> params) {
		List<?> result = super.execNativeQuery(sqlquery, params);
		return result;
	}

}