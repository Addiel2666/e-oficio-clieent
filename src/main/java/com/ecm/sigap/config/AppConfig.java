/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <b>Sistema Integral de Gestión y Automatización de Procesos.</b>
 * 
 * <b>e-oFICIO 5.0</b>
 * 
 * El Sistema Integral de Gestión y Automatización de Procesos (SIGAP) es una
 * solución abierta y modular espec�ficamente diseñada para ayudar a las
 * Instituciones de Gobierno.
 * 
 * <i>Consultoria y Aplicaciones Avanzadas de ECM S.A. de C.V. ECM
 * Solutions..</i>
 * 
 * @author Alejandro Guzman // Alfredo Morales // Hugo Hernandez
 * @version 1.0
 *
 */
@Configuration("appConfig")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class AppConfig {

	/**
	 * 
	 * @param sessionFactory
	 * @return
	 */
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		return new HibernateTransactionManager(sessionFactory);
	}

}
