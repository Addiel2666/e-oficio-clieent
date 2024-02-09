/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.DocumentoMinutario;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.service.ManagerImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Manejador en base de datos de objetos {@link DocumentoMinutario}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Service("documentoMinutarioService")
public class DocumentoMinutarioImpl extends ManagerImpl<DocumentoMinutario> {

	/** */
	private static final Logger log = LogManager.getLogger(DocumentoMinutarioImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#fetch(java.io.Serializable)
	 */
	@Override
	public DocumentoMinutario fetch(Serializable id) {
		DocumentoMinutario item = super.fetch(id);

		getRepoInfo(item);

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("documentoMinutarioDao")
	@Override
	protected void setDao(EntityDAO<DocumentoMinutario> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(DocumentoMinutario item) throws Exception {
		log.debug("SAVING :: " + item);
		super.save(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List, org.hibernate.criterion.ProjectionList,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions, //
			List<Order> orders, ProjectionList projections, //
			Integer fetchSize, Integer firstResult) {

		List<?> search = super.search(restrictions, orders, projections, fetchSize, firstResult);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoMinutario>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions) {
		List<?> search = super.search(restrictions);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoMinutario>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		List<?> search = (List<DocumentoMinutario>) super.search(restrictions, orders);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoMinutario>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#delete(java.lang.Object)
	 */
	@Override
	public void delete(DocumentoMinutario item) {

		super.delete(item);

		try {
			EndpointDispatcher.getInstance().eliminarDocumento(item.getObjectId());
		} catch (JsonParseException e) {
			log.error(e.getLocalizedMessage());
			
		} catch (JsonMappingException e) {
			log.error(e.getLocalizedMessage());
			
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
		}

	}

}