/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ecm.sigap.data.util.HibernateEntityInterceptor;
import com.ecm.sigap.util.convertes.impl.PdfConverterServiceImpl;

/**
 * Carga de los datos y configuracion de base de datos.
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 * @author Alfredo Morales
 * @version 1.0.1
 * 
 * @author Alfredo Morales
 * @version 1.1
 * 
 *          Integracion de C3P0 como manager del pool de conexiones.
 *
 */
@Configuration("hibernateConfig")
@EnableTransactionManagement
@PropertySource(value = { //
		"classpath:application.properties", // DB props
		"classpath:repository.properties", // REPO props
		"classpath:firmaDigital.properties", // Firma
		"classpath:mail.properties", // Servicio de Correo
		"classpath:interoperabilidad.properties", // Interoperabilidad
		"classpath:e-archivo.properties", // e-archivo
		"classpath:clientConfigs.properties", // configuraciones por cliente
		"classpath:sipot.properties", // SIPOT
		"classpath:e-portal.properties" // e-portal
})
@Import({ PdfConverterServiceImpl.class })
public class HibernateConfiguration {

	/**
	 * Configuracion del Session Factory de Hibernet con Spring.
	 * 
	 * @return
	 */
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

		sessionFactory.setHibernateProperties(hibernateProperties());

		sessionFactory.setPackagesToScan("com.ecm.sigap.data.model", "com.ecm.sigap.eCiudadano.model",
				"com.ecm.sigap.ope");

		sessionFactory.setEntityInterceptor(new HibernateEntityInterceptor());

		return sessionFactory;
	}

	/** */
	@Value("${hibernate.dataSourceName}")
	private String datasourceName;
	/** */
	@Value("${hibernate.default_schema}")
	private String default_schema;
	/** */
	@Value("${hibernate.db_type}")
	private String db_type;
	/** */
	@Value("${hibernate.show_sql}")
	private String show_sql;
	/** */
	@Value("${hibernate.format_sql}")
	private String format_sql;

	/**
	 * Configuracion de Hibernate
	 * 
	 * @return
	 */

	private Properties hibernateProperties() {

		Properties properties = new Properties();

		if ("SQLSERVER".equalsIgnoreCase(db_type)) {
			// SQL SERVER
			properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
		} else if ("POSTGRE".equalsIgnoreCase(db_type) || "POSTGRESQL".equalsIgnoreCase(db_type)) {
			// POSTGRESQL
			properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		} else {
			// ORACLE
			properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
		}

		properties.put("hibernate.default_schema", default_schema);

		properties.put("hibernate.connection.datasource", datasourceName);

		// - - -

		properties.put("hibernate.show_sql", show_sql);

		properties.put("hibernate.format_sql", format_sql);

		// - - -

		properties.put("hibernate.order_updates", "false");

		properties.put("hibernate.order_inserts", "false");

		properties.put("hibernate.validator.autoregister_listeners", "false");

		properties.put("hibernate.cache.use_minimal_puts", "true");

		properties.put("hibernate.connection.autocommit", "true");

		properties.put("javax.persistence.validation.mode", "none");

		// - - -

		return properties;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		return txManager;
	}

	/**
	 * Determina el tipo de base de datos configurada.
	 */
	@Bean
	public DBVendor getDBType() {

		if ("SQLSERVER".equalsIgnoreCase(db_type)) {
			// SQL SERVER
			return DBVendor.SQL_SERVER;
		} else if ("POSTGRE".equalsIgnoreCase(db_type) || "POSTGRESQL".equalsIgnoreCase(db_type)) {
			// POSTGRESQL
			return DBVendor.POSTGRESQL;
		} else {
			// ORACLE
			return DBVendor.ORACLE;
		}

	}

}
