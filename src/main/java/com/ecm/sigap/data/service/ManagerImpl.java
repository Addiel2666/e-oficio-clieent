/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.impl.jaxb.EnumPropertiesBase;
import org.apache.chemistry.opencmis.commons.impl.jaxb.EnumPropertiesDocument;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.util.Documento;

/**
 * 
 * Cableado entre los metodos expuiestos del manager y los metodos entregados
 * por el DAO.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 * @param <k> Tipo de Objetos que el Manager utilizara.
 */
public abstract class ManagerImpl<k> implements EntityManager<k> {

	/** Configuracion global de la acplicacion. */
	@Autowired
	protected Environment environment;
	/** */
	@Autowired
	protected PlatformTransactionManager transactionManager;
	/** */
	private TransactionStatus transactionStatus;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#beginTransaction()
	 */
	public void beginTransaction() {
		TransactionDefinition def = new DefaultTransactionDefinition();
		transactionStatus = transactionManager.getTransaction(def);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#commit()
	 */
	public void commit() {
		if (transactionStatus != null)
			transactionManager.commit(transactionStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#rollback()
	 */
	public void rollback() {
		if (transactionStatus != null)
			transactionManager.rollback(transactionStatus);

	}

	/** Interfaz a base de datos. */
	private EntityDAO<k> dao;

	/** Default Constructor. */
	public ManagerImpl() {
		super();
	}

	/**
	 * 
	 * @param dao
	 */
	protected void setDao(EntityDAO<k> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#fetch(java.io.Serializable)
	 */
	@Override
	public k fetch(Serializable id) {
		return dao.fetch(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#save(java.lang.Object)
	 */
	@Override
	public void save(k item) throws Exception {
		dao.save(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#update(java.lang.Object)
	 */
	@Override
	public void update(k item) {
		dao.update(item);
	}

	/**
	 * @param item
	 * @return
	 * @see com.ecm.sigap.data.dao.EntityDAO#merge(java.lang.Object)
	 */
	public k merge(k item) {
		return dao.merge(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#delete(java.lang.Object)
	 */
	@Override
	public void delete(k item) {
		dao.delete(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#search(org.hibernate.criterion
	 * .Criterion, org.hibernate.criterion.Order)
	 */
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {
		return dao.search(restrictions, orders, projections, fetchSize, firstResult);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#search(java.util.List)
	 */
	@Override
	public List<?> search(List<Criterion> restrictions) {
		return dao.search(restrictions, null, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#search(java.util.List,
	 * java.util.List)
	 */
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		return dao.search(restrictions, orders, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.EntityManager#execNamedQuery(java.lang.String,
	 * java.util.HashMap)
	 */
	@Override
	public List<k> execNamedQuery(String queryName, HashMap<String, Object> params) {
		return dao.execNamedQuery(queryName, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.service.EntityManager#execUpdateQuery(java.lang.
	 * String, java.util.HashMap)
	 */
	@Override
	public List<k> execQuery(String sqlquery) {
		return dao.execQuery(sqlquery);
	}

	@Override
	public List<k> execQuery(String query, int firstResult, int maxResult) {
		return dao.execQuery(query, firstResult, maxResult);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.service.EntityManager#execNativeQuery(java.util.List,
	 * java.util.List)
	 */
	@Override
	public List<?> execNativeQuery(String sqlquery, HashMap<String, Object> params) {
		return dao.execNativeQuery(sqlquery, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#execUpdateQuery(java.lang.
	 * String, java.util.HashMap)
	 */
	@Override
	public Integer execUpdateQuery(String queryName, HashMap<String, Object> params) {
		return dao.execUpdateQuery(queryName, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#uniqueResult(java.lang.String,
	 * java.util.HashMap)
	 */
	@Override
	public Object uniqueResult(String queryName, HashMap<String, Object> params) {
		return dao.uniqueResult(queryName, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#isConnected()
	 */
	@Override
	public String isConnected() {
		return dao.isConnected();
	}

	/**
	 * Obtiene informacion adicional del repositorio para la lista de objetos
	 * indicada.
	 * 
	 * @param search
	 * @throws Exception
	 */
	protected void completeWithRepoInfo(List<? extends Documento> search) {
		for (Documento doc : search)
			getRepoInfo(doc);
	}

	/**
	 * Obtiene informacion adicional del repositorio para documento indicado.
	 * 
	 * @param doc
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void getRepoInfo(Documento doc) {
		try {
			// OBETENER INFO DEL ARCHIVO DEL REPO
			Map<String, Object> docProperties = EndpointDispatcher.getInstance().getObjectProperties(doc.getObjectId());
			try {
				doc.setObjectName(((List<String>) docProperties.get(EnumPropertiesBase.CMIS_NAME.value())).get(0));
			} catch (Exception e) {

			}
			try {
				doc.setOwnerName(((List<String>) docProperties.get("owner_name")).get(0));
			} catch (Exception e) {

			}

			try {
				doc.setCheckout(((List<Boolean>) docProperties
						.get(EnumPropertiesDocument.CMIS_IS_VERSION_SERIES_CHECKED_OUT.value())).get(0));
			} catch (Exception e) {

			}
		} catch (Exception e) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#getNextval(java.lang.String)
	 */
	@Override
	public Long getNextval(String seq) {
		return dao.getNextval(seq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#flush()
	 */
	@Override
	public void flush() {
		try {
			dao.flush();
		} catch (Exception e) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#createStoredProcedureCall(java.
	 * lang.String, java.util.HashMap)
	 */
	@Override
	public Boolean createStoredProcedureCall(String procedureName, LinkedHashMap<String, Object> params) {
		return dao.createStoredProcedureCall(procedureName, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#createStoredProcedureCall(java.
	 * lang.String, java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public HashMap<String, Object> createStoredProcedureCall(String procedureName,
			LinkedHashMap<String, Object> paramsIn, LinkedHashMap<String, Object> paramsOut) {
		return dao.createStoredProcedureCall(procedureName, paramsIn, paramsOut);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.EntityManager#execNativeUpdateQuery(java.lang.
	 * String, java.util.HashMap)
	 */
	@Override
	public void execNativeUpdateQuery(String queryName, HashMap<String, Object> params) {
		dao.execNativeUpdateQuery(queryName, params);
	}

	@Override
	public k saveOrUpdate(k item) {
		return dao.saveOrUpdate(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#callFunction(java.lang.String,
	 * java.util.HashMap)
	 */
	@Override
	public Object callFunction(String queryName, HashMap<String, Object> params) {
		return dao.callFunction(queryName, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.EntityManager#updateBitacora(java.lang.Object)
	 */
	@Override
	public void updateBitacora(k item) {
		dao.updateBitacora(item);
	}
	
	@Override
	public void inactivate(k item) {
		dao.inactivate(item);
	}
}
