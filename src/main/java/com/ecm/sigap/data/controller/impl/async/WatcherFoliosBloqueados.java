/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecm.sigap.data.controller.impl.MailController;
import com.ecm.sigap.data.model.Folio;
import com.ecm.sigap.data.service.EntityManager;

/**
 * 
 * @author Alfredo Morales
 * 
 */
@Component
public class WatcherFoliosBloqueados {

	/** Logger de la clase */
	private final static Logger logger = LogManager.getLogger(WatcherFoliosBloqueados.class.getName());

	/**
	 * Manejador para el tipo {@link Folio }
	 */
	@Autowired
	@Qualifier("folioService")
	private EntityManager<Folio> mngrFolio;

	/**
	 * Referencia hacia el REST controller de {@link MailController}.
	 */
	@Autowired
	private MailController mailController;

	/**
	 * Configuracion global de la acplicacion.
	 */
	@Autowired
	protected Environment environment;

	/**
	 * Se valida la licencia diario a media noche.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	// descomentar para activar
	// @Scheduled(cron = "0 0 * * * *") // at midnigth
	public void watcher() {
		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("vlock", "A"));

			ProjectionList projections = Projections.projectionList();

			projections.add(Projections.groupProperty("folioKey.idArea").as("idArea"));
			projections.add(Projections.count("folioKey.folio").as("cuantos"));

			List<?> foliosBloqueadosPorArea = mngrFolio.search(restrictions, null, projections, null, null);

			String mailNotificacion = environment.getProperty("mailNotificacion");

			if (!foliosBloqueadosPorArea.isEmpty()) {

				String gg = "<br /> Se encontraro folios bloqueados en las siguientes areas:<br />";

				for (Object m : foliosBloqueadosPorArea) {

					Map<String, Object> map = (Map<String, Object>) m;
					gg += map.toString() + "<br />";

				}

				Log.warn(gg.replace("<br />", "\n"));

				if (StringUtils.isNotBlank(mailNotificacion))
					mailController.sendNotificacionEmpty(mailNotificacion, gg);

			}

		} catch (Exception e) {

			
			logger.error(e.getMessage());
		}

	}

}
