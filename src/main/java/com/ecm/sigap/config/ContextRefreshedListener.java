/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

	/** */
	private final static Logger log = LogManager.getLogger(ContextRefreshedListener.class.getName());

	/** */
	@Autowired
	private TrialVersionObject trial;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

		try {
			trial.validateLicense();
		} catch (Exception e) {
			log.error(e.getMessage());
			
		}

	}
}