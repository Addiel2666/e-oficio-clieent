/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.ParameterMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import com.ecm.sigap.config.DBVendor;
import com.ecm.sigap.data.audit.aspectj.Audit;
import com.ecm.sigap.data.model.util.TipoAuditoria;

/**
 * 
 * Metodos generales de operaciones disponibles para manejo de datos hacia base
 * de datos.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public abstract class Dao<k> implements EntityDAO<k> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(Dao.class);

	/** */
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	/** */
	@Autowired
	private DBVendor dbVendor;

	/**
	 * Configuracion global de la acplicacion.
	 */
	@Autowired
	protected Environment environment;

	/**
	 * Obtiene una session de conexion con la base de datos.
	 * 
	 * @return
	 */
	protected Session getSession() {

		Session session = null;

		try {
			session = sessionFactory.getCurrentSession();
		} catch (Exception e) {
			try {
				session = sessionFactory.openSession();
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}

		if (session != null) {
			session.setCacheMode(CacheMode.NORMAL);
			session.setHibernateFlushMode(FlushMode.AUTO);
		}

		return session;
	}

	/**
	 * Determina la clase seteada via generics
	 */
	@SuppressWarnings("unchecked")
	private Class<k> getK() {
		return (Class<k>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#isConnected()
	 */
	public String isConnected() {
		return Boolean.toString(getSession().isConnected());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.services.EntityManager#fetch(java.io.Serializable)
	 */
	@Override
	@Transactional
	public k fetch(Serializable id) {
		return (k) getSession().get(getK(), id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.services.EntityManager#save(java.lang.Object)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	@Audit(actionType = TipoAuditoria.SAVE)
	public void save(k item) {
		getSession().save(item);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public k saveOrUpdate(k item) {
		getSession().saveOrUpdate(item);
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#update(java.lang.Object)
	 */
	@Transactional
	@Override
	@Audit(actionType = TipoAuditoria.UPDATE)
	public void update(k item) {
		getSession().update(item);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	@Audit(actionType = TipoAuditoria.UPDATE)
	public k merge(k item) {
		return (k) getSession().merge(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.services.EntityManager#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	@Audit(actionType = TipoAuditoria.DELETE)
	public void delete(k item) {
		getSession().delete(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#execNamedQuery(java.lang.String,
	 * java.util.HashMap)
	 */
	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public List<k> execNamedQuery(String queryName, HashMap<String, Object> params) {

		Query<?> query = getSession().getNamedQuery(queryName);

		setParameters(params, query);

		return (List<k>) query.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#execNamedQuery(java.lang.String,
	 * java.util.HashMap)
	 */
	@Override
	@Transactional
	public List<?> execNativeQuery(String queryName, HashMap<String, Object> params) {

		Query<?> query = getSession().createSQLQuery(queryName);

		setParameters(params, query);
		List<?> result = query.list();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#execNativeUpdateQuery(java.lang.String,
	 * java.util.HashMap)
	 */
	@Override
	@Transactional
	public Integer execNativeUpdateQuery(String queryName, HashMap<String, Object> params) {

		Query<?> query = getSession().createSQLQuery(queryName);

		setParameters(params, query);

		return query.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#execQuery(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<k> execQuery(String sqlquery) {
		Query<?> query = getSession().createQuery(sqlquery);
		List<?> list = query.list();
		return (List<k>) list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#execQuery(java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<k> execQuery(String sqlquery, int firstResult, int maxResult) {
		Query<?> query = getSession().createQuery(sqlquery);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		List<?> list = query.list();
		return (List<k>) list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#execUpdateQuery(java.lang.String,
	 * java.util.HashMap)
	 */
	@Transactional
	@Override
	public Integer execUpdateQuery(String queryName, HashMap<String, Object> params) {

		if (dbVendor == DBVendor.SQL_SERVER)
			queryName += "_SQLSERVER";
		else if (dbVendor == DBVendor.POSTGRESQL)
			queryName += "_POSTGRESQL";

		Query<?> query = getSession().getNamedQuery(queryName);

		setParameters(params, query);

		return query.executeUpdate();
	}

	/**
	 * 
	 * @param params
	 * @param procedureCall
	 */
	protected void setParameters(HashMap<String, Object> params, ProcedureCall procedureCall) {
		if (params != null)
			for (String key : params.keySet()) {
				if (params.get(key) instanceof String) {

					procedureCall.registerParameter(key, String.class, ParameterMode.IN)
							.bindValue((String) params.get(key));

				} else if (params.get(key) instanceof Integer) {

					procedureCall.registerParameter(key, Integer.class, ParameterMode.IN)
							.bindValue((Integer) params.get(key));

				} else if (params.get(key) instanceof Double) {

					procedureCall.registerParameter(key, Double.class, ParameterMode.IN)
							.bindValue((Double) params.get(key));

				} else if (params.get(key) instanceof Date) {

					procedureCall.registerParameter(key, Date.class, ParameterMode.IN)
							.bindValue((Date) params.get(key));

				}
			}
	}

	/**
	 * @param paramsIn
	 * @param paramsOut
	 * @param procedureCall
	 */
	protected void setParameters(HashMap<String, Object> paramsIn, HashMap<String, Object> paramsOut,
			ProcedureCall procedureCall) {
		if (paramsIn != null)
			for (String key : paramsIn.keySet()) {
				if (paramsIn.get(key) instanceof String) {

					procedureCall.registerParameter(key, String.class, ParameterMode.IN)
							.bindValue((String) paramsIn.get(key));

				} else if (paramsIn.get(key) instanceof Integer) {

					procedureCall.registerParameter(key, Integer.class, ParameterMode.IN)
							.bindValue((Integer) paramsIn.get(key));

				} else if (paramsIn.get(key) instanceof Double) {

					procedureCall.registerParameter(key, Double.class, ParameterMode.IN)
							.bindValue((Double) paramsIn.get(key));

				} else if (paramsIn.get(key) instanceof Date) {

					procedureCall.registerParameter(key, Date.class, ParameterMode.IN)
							.bindValue((Date) paramsIn.get(key));

				}
			}
		if (paramsOut != null)
			for (String key : paramsOut.keySet()) {
				if (paramsOut.get(key) instanceof String) {

					procedureCall.registerParameter(key, String.class, ParameterMode.INOUT)
							.bindValue((String) paramsOut.get(key));

				} else if (paramsOut.get(key) instanceof Integer) {

					procedureCall.registerParameter(key, Integer.class, ParameterMode.INOUT)
							.bindValue((Integer) paramsOut.get(key));

				} else if (paramsOut.get(key) instanceof Double) {

					procedureCall.registerParameter(key, Double.class, ParameterMode.INOUT)
							.bindValue((Double) paramsOut.get(key));

				} else if (paramsOut.get(key) instanceof Date) {

					procedureCall.registerParameter(key, Date.class, ParameterMode.INOUT)
							.bindValue((Date) paramsOut.get(key));

				}
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#createStoredProcedureCall(java.lang.
	 * String, java.util.HashMap)
	 */
	@Transactional
	@Override
	public Boolean createStoredProcedureCall(String procedureName, HashMap<String, Object> params) {

		if (dbVendor == DBVendor.ORACLE) {

			procedureName = "{SIGAP_SCHEMA}." + procedureName;
			ProcedureCall procedureCall = getSession().createStoredProcedureCall(procedureName);

			setParameters(params, procedureCall);

			procedureCall.getOutputs();

		} else {

			StringBuilder sqlquery = new StringBuilder();

			sqlquery.append("do $$\r\n");
			sqlquery.append("begin\r\n");

			// - - - -

			sqlquery.append("  perform ");
			sqlquery.append(procedureName);

			// - - - -

			sqlquery.append("(");

			List<String> params_ = new ArrayList<String>();

			Object val;
			for (String key : params.keySet()) {

				val = params.get(key);

				if (val instanceof Integer)
					params_.add(val.toString());
				else
					params_.add("'" + val.toString() + "'");

			}

			sqlquery.append(String.join(", ", params_));

			// - - - -

			sqlquery.append(");\r\n");

			// - - - -

			sqlquery.append("end\r\n");
			sqlquery.append("$$;");

			NativeQuery<?> query = getSession().createSQLQuery(sqlquery.toString());
			query.executeUpdate();

		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#createStoredProcedureCall(java.lang.
	 * String, java.util.HashMap, java.util.HashMap)
	 */
	@Transactional
	@Override
	public HashMap<String, Object> createStoredProcedureCall(String procedureName, HashMap<String, Object> paramsIn,
			HashMap<String, Object> paramsOut) {
		LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
		if (dbVendor == DBVendor.ORACLE) {

			procedureName = "{SIGAP_SCHEMA}." + procedureName;
			ProcedureCall procedureCall = getSession().createStoredProcedureCall(procedureName);

			setParameters(paramsIn, paramsOut, procedureCall);

			if (paramsOut != null)
				for (String key : paramsOut.keySet()) {
					output.put(key, procedureCall.getOutputs().getOutputParameterValue(key));
				}

		}
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#search(java.util.List, java.util.List)
	 */
	@Override
	@Transactional
	public List<?> search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {

		if (restrictions == null || restrictions.isEmpty()) {
			throw new HibernateException("Restriction required.");
		}

		Criteria criteria = createCriteria(restrictions, orders, projections, fetchSize, firstResult);

		criteria.setCacheable(true);
		criteria.setCacheMode(CacheMode.NORMAL);
		criteria.setCacheRegion("ECM_SIGAP_V_CACHE_REGION");

		if (projections == null || projections.getLength() < 1) {
			criteria.setResultTransformer(Criteria.ROOT_ENTITY);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		} else {
			criteria.setResultTransformer(Criteria.PROJECTION);
			criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}

		return criteria.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#uniqueResult(java.lang.String,
	 * java.util.HashMap)
	 */
	@Transactional
	@Override
	public Object uniqueResult(String queryName, HashMap<String, Object> params) {

		Query<?> query = getSession().getNamedQuery(queryName);

		setParameters(params, query);

		Object uniqueResult = query.uniqueResult();

		return uniqueResult;
	}

	/**
	 * Llena un query con los parametros indicados en el Map.
	 * 
	 * @param params
	 * @param query
	 */
	protected void setParameters(HashMap<String, Object> params, Query<?> query) {
		if (params != null)
			for (String key : params.keySet()) {
				if (params.get(key) instanceof String) {
					query.setParameter(key, (String) params.get(key));
				} else if (params.get(key) instanceof Integer) {
					query.setParameter(key, (Integer) params.get(key));
				} else if (params.get(key) instanceof Double) {
					query.setParameter(key, (Double) params.get(key));
				} else if (params.get(key) instanceof Date) {
					query.setParameter(key, (Date) params.get(key));
				} else if (params.get(key) instanceof Long) {
					query.setParameter(key, (Long) params.get(key));
				} else if (params.get(key) instanceof List) {
					query.setParameter(key, (List<?>) params.get(key));
				}
			}
	}

	/*
	 * 
	 */
	@Transactional
	@Override
	public Long getNextval(String seq) {

		StringBuilder sql = new StringBuilder();

		if (dbVendor == DBVendor.ORACLE) {

			sql.append("select ");
			sql.append(environment.getProperty("hibernate.default_schema"));
			sql.append(".");
			sql.append(seq);
			sql.append(".nextval seq ");
			sql.append(" from dual");

		} else if (dbVendor == DBVendor.SQL_SERVER) {

			sql.append("SELECT (NEXT VALUE FOR");
			sql.append(environment.getProperty("hibernate.default_schema"));
			sql.append(".");
			sql.append(seq);
			sql.append(") AS seq ");

		} else if (dbVendor == DBVendor.POSTGRESQL) {

			sql.append("select nextval('");
			sql.append(environment.getProperty("hibernate.default_schema"));
			sql.append(".");
			sql.append(seq);
			sql.append("') AS seq ");

		} else {

			log.warn("TIPO DE BASE DE DATOS DESCONOCIDA!!!!");

		}

		NativeQuery<?> sqlQuery = getSession().createSQLQuery(sql.toString());

		sqlQuery.addScalar("seq", LongType.INSTANCE);

		return (Long) sqlQuery.uniqueResult();
	}

	/**
	 * Llena el en el query de consulta los parametros proporcionados.
	 * 
	 * @param restrictions Condiciones WHERE
	 * @param orders       Condiciones ORDER BY
	 * @param criteria     Query de consulta.
	 * @param firstResult  Inidice Inicial de Elementos Devueltos.
	 * @param fetchSize    Tamaño de elementos devueltos.
	 */
	protected void processCriteria(Criteria criteria, List<Criterion> restrictions, List<Order> orders,
			ProjectionList projections, Integer fetchSize, Integer firstResult) {

		criteria.setFlushMode(FlushMode.ALWAYS);

		if (firstResult != null)
			criteria.setFirstResult(firstResult);

		if (fetchSize != null) {
			criteria.setFetchSize(fetchSize);
			criteria.setMaxResults(fetchSize);
		}

		if (restrictions != null)
			for (Criterion restrction : restrictions)
				criteria.add(restrction);

		if (projections != null)
			criteria.setProjection(projections);

		if (orders != null)
			for (Order order : orders)
				criteria.addOrder(order);

	}

	/**
	 * Definicion para consulta del objeto en especifico.
	 * 
	 * @param restrictions
	 * @param orders
	 * @param fetchSize
	 * @param firstResult
	 * @return
	 */
	abstract protected Criteria createCriteria(List<Criterion> restrictions, List<Order> orders,
			ProjectionList projections, Integer fetchSize, Integer firstResult);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#flush()
	 */
	@Override
	public void flush() {
		getSession().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#uniqueResult(java.lang.String,
	 * java.util.HashMap)
	 */
	@Transactional
	@Override
	public Object callFunction(String queryName, HashMap<String, Object> params) {
		if (dbVendor == DBVendor.ORACLE) {
			Query<?> query = getSession().getNamedQuery(queryName + "Oracle");

			setParameters(params, query);

			Object uniqueResult = query.uniqueResult();

			return uniqueResult;
		} else if (dbVendor == DBVendor.POSTGRESQL) {
			Query<?> query = getSession().getNamedQuery(queryName + "PostgreSql");

			setParameters(params, query);

			Object uniqueResult = query.uniqueResult();

			return uniqueResult;
		} else {
			return "";
		}

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.dao.EntityDAO#updateBitacora(java.lang.Object)
	 */
	@Transactional
	@Override
	@Audit(actionType = TipoAuditoria.UPDATEBITACORA)
	public void updateBitacora(k item) {
		log.warn("updateBitacora!!!!");
	}
	
	@Transactional
	@Override
	@Audit(actionType = TipoAuditoria.INACTIVE)
	public void inactivate(k item) {
		getSession().update(item);
	}	
	
}