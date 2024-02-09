/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.dao.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.dao.Dao;

/**
 * @author Alfredo Morales
 *
 */
public abstract class Repository<k> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(Dao.class);

	/** Configuracion global de la acplicacion. */
	@Autowired
	protected Environment environment;

	/** */
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

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

	@SuppressWarnings("unchecked")
	private Class<k> getK() {
		return (Class<k>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}

	/**
	 * 
	 * @param searchObject
	 * @return
	 */
	public List<k> search(k searchObject) {
		return search(new RequestWrapper<k>(searchObject), null, null);
	}

	/**
	 * 
	 * @param searchObject
	 * @return
	 */
	public List<k> search(RequestWrapper<k> searchObject) {
		return search(searchObject, null, null);
	}

	/**
	 * 
	 * @param searchObject
	 * @return
	 */
	public k searchSingle(k searchObject) {
		List<k> search = search(new RequestWrapper<k>(searchObject), null, null);
		return search.isEmpty() ? null : search.get(0);
	}

	/**
	 * 
	 * @param searchObject
	 * @return
	 */
	public k searchSingle(RequestWrapper<k> searchObject) {
		List<k> search = search(searchObject, null, null);
		return search.isEmpty() ? null : search.get(0);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public k fetch(Serializable id) {

		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		CriteriaQuery<k> criteriaQuery = criteriaBuilder.createQuery(getK());
		Root<k> root = criteriaQuery.from(getK());

		criteriaQuery.select(root);

		EntityType<k> model = root.getModel();
		SingularAttribute<? super k, ?> idAttribute = model.getId(model.getIdType().getJavaType());

		criteriaQuery.where(criteriaBuilder.equal(root.get(idAttribute), id));

		TypedQuery<k> query = getSession().createQuery(criteriaQuery);

		return query.setMaxResults(1).getSingleResult();

	}

	/**
	 * 
	 * @return
	 */
	protected abstract String getIdFieldName();

	/**
	 * 
	 * @param searchObject
	 * @param fetchSize
	 * @param startAt
	 * @return
	 */
	public List<k> search(RequestWrapper<k> searchObject, Integer fetchSize, Integer startAt) {

		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		CriteriaQuery<k> criteriaQuery = criteriaBuilder.createQuery(getK());
		Root<k> root = criteriaQuery.from(getK());

		criteriaQuery.select(root);

		addRestrictions(criteriaBuilder, criteriaQuery, root, searchObject);

		addOrderBy(criteriaBuilder, criteriaQuery, root, searchObject);

		TypedQuery<k> query = getSession().createQuery(criteriaQuery);

		if (startAt != null && startAt >= 0)
			query.setFirstResult(startAt);

		if (fetchSize != null && fetchSize > 0)
			query.setMaxResults(fetchSize);

		return query.getResultList();

	}

	/**
	 * 
	 * @param searchObject
	 * @return
	 */
	public Long count(RequestWrapper<k> searchObject) {

		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<k> root = criteriaQuery.from(getK());

		criteriaQuery.select(criteriaBuilder.countDistinct(criteriaQuery.from(getK())));

		addRestrictions(criteriaBuilder, criteriaQuery, root, searchObject);

		org.hibernate.query.Query<Long> query = getSession().createQuery(criteriaQuery);

		return query.getSingleResult();

	}

	/**
	 * 
	 * @param criteriaBuilder
	 * @param criteriaQuery
	 * @param root
	 * @param searchObject
	 */
	protected abstract void addRestrictions( //
			CriteriaBuilder criteriaBuilder, //
			CriteriaQuery<?> criteriaQuery, //
			Root<k> root, //
			RequestWrapper<k> searchObject);

	/**
	 * 
	 * @param criteriaBuilder
	 * @param criteriaQuery
	 * @param root
	 * @param searchObject
	 */
	protected abstract void addOrderBy( //
			CriteriaBuilder criteriaBuilder, //
			CriteriaQuery<k> criteriaQuery, //
			Root<k> root, //
			RequestWrapper<k> searchObject);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public k save(k item) {
		Session sess = getSession();
		Transaction tx = null;
		Serializable savedItemId;
		try {
			tx = sess.beginTransaction();
			savedItemId = sess.save(item);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
		return fetch(savedItemId);
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	public void saveOrUpdate(k item) {
		Session sess = getSession();
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			sess.saveOrUpdate(item);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	public void update(k item) {
		Session sess = getSession();
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			sess.update(item);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
	}

	/**
	 * 
	 * @param item
	 */
	public void delete(k item) {
		Session sess = getSession();
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			sess.delete(item);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
	}

}
