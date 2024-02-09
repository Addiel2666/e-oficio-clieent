/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Permite utilizar un sequence numerico en un campo tipo String.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
public class StringSequenceGeneratorRepresentante implements IdentifierGenerator {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(StringSequenceGeneratorRepresentante.class);
	/** */
	private static final ResourceBundle config = ResourceBundle.getBundle("application");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.id.IdentifierGenerator#generate(org.hibernate.engine.spi.
	 * SessionImplementor, java.lang.Object)
	 */
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		Connection connection = session.connection();
		PreparedStatement ps = null;
		String result = "";

		String schema = config.getString("hibernate.default_schema");
		String db_type = config.getString("hibernate.db_type");

		try {

			String query;

			if ("POSTGRE".equalsIgnoreCase(db_type) || "POSTGRESQL".equalsIgnoreCase(db_type)) {

				query = "select nextval('{SIGAP_SCHEMA}.SECREPRESENTANTES') AS TABLE_PK";

			} else if ("SQLSERVER".equalsIgnoreCase(db_type)) {

				query = "select (NEXT VALUE FOR {SIGAP_SCHEMA}.SECREPRESENTANTES) AS TABLE_PK";

			} else {

				query = "select {SIGAP_SCHEMA}.SECREPRESENTANTES.nextval AS TABLE_PK from dual";

			}

			query = query.replace("{SIGAP_SCHEMA}", schema);

			ps = connection.prepareStatement(query);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int pk = rs.getInt("TABLE_PK");

				// Convert to a String
				result = Integer.toString(pk);
			}

		} catch (SQLException e) {

			log.error(e.getLocalizedMessage());
			throw new HibernateException("Unable to generate Primary Key");

		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					log.error("Unable to close prepared statement.");
					log.error(e.getLocalizedMessage());
				}
			}
		}

		return result;
	}

}