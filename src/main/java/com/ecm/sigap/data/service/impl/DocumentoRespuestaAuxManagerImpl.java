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
import com.ecm.sigap.data.model.DocumentoRespuestaAux;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.service.ManagerImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Manejador en base de datos de objetos {@link DocumentoRespuestaAux}.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@Service("documentoRespuestaAuxService")
public class DocumentoRespuestaAuxManagerImpl extends ManagerImpl<DocumentoRespuestaAux> {

	/** */
	private static final Logger log = LogManager.getLogger(DocumentoRespuestaAuxManagerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#fetch(java.io.Serializable)
	 */
	@Override
	public DocumentoRespuestaAux fetch(Serializable id) {
		DocumentoRespuestaAux item = super.fetch(id);

		getRepoInfo(item);

		return item;

	}

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("documentoRespuestaAuxDao")
	protected void setDao(EntityDAO<DocumentoRespuestaAux> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#delete(java.lang.Object)
	 */
	@Override
	public void delete(DocumentoRespuestaAux item) {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(DocumentoRespuestaAux item) throws Exception {

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
			completeWithRepoInfo((List<DocumentoRespuestaAux>) search);

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
			completeWithRepoInfo((List<DocumentoRespuestaAux>) search);

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
		List<?> search = super.search(restrictions, orders);

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoRespuestaAux>) search);

		return search;
	}

}