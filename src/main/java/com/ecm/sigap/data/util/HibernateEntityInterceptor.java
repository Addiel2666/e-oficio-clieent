/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.AsuntoDetalle;

/**
 * Interceptor de Operaciones hacia base de datos.
 * <p>
 * Para el caso de necesitar mantener todos los campos de texto en mayusculas
 * inidicarlo por tipo en el metodo onSave.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
public class HibernateEntityInterceptor extends EmptyInterceptor {

	/** */
	private static final long serialVersionUID = 3426109629980895632L;

	/** */
	protected static final ResourceBundle config = ResourceBundle.getBundle("application");

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(HibernateEntityInterceptor.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.EmptyInterceptor#onDelete(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		super.onDelete(entity, id, state, propertyNames, types);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.EmptyInterceptor#afterTransactionBegin(org.hibernate.
	 * Transaction)
	 */
	@Override
	public void afterTransactionBegin(Transaction tx) {
		super.afterTransactionBegin(tx);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.EmptyInterceptor#afterTransactionCompletion(org.hibernate.
	 * Transaction)
	 */
	@Override
	public void afterTransactionCompletion(Transaction tx) {
		super.afterTransactionCompletion(tx);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.hibernate.EmptyInterceptor#beforeTransactionCompletion(org.hibernate.
	 * Transaction)
	 */
	@Override
	public void beforeTransactionCompletion(Transaction tx) {
		super.beforeTransactionCompletion(tx);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

		if ("true".equalsIgnoreCase(config.getString("auto_uppercase")))

			if (entity instanceof AsuntoDetalle) {

				AsuntoDetalle instance = (AsuntoDetalle) entity;

				instance.setAsuntoDescripcion(instance.getAsuntoDescripcion().toUpperCase());

			} else if (entity instanceof Area) {

				Area instance = (Area) entity;

				instance.setDescripcion(instance.getDescripcion().toUpperCase());
				instance.setClave(instance.getClave().toUpperCase());
				instance.setSiglas(instance.getSiglas().toUpperCase());

			}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onPrepareStatement(java.lang.String)
	 */
	@Override
	public String onPrepareStatement(String sql) {

		String default_schema = config.getString("hibernate.default_schema");
		String dbType = config.getString("hibernate.db_type");

		sql = sql.replaceAll("\\{SIGAP_SCHEMA}", default_schema); //

		if (dbType.equals("POSTGRE") || dbType.equals("POSTGRESQL")) {
			sql = sql.replaceAll("from dual", " ");
		}

		if (!sql.toLowerCase().contains("where") //
				&& !sql.toLowerCase().contains("insert") //
				&& !sql.toLowerCase().contains("{call") //
				&& !sql.toLowerCase().contains("from dual"))
			log.warn("QUERY SIN CONDICION WHERE :: \n" + sql);

		return sql;

	}

}
