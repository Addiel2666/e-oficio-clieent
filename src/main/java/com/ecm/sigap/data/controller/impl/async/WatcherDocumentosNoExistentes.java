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
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.impl.MailController;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.Folio;
import com.ecm.sigap.data.service.EntityManager;

/**
 * 
 * @author Alfredo Morales
 * 
 */
@Component
public class WatcherDocumentosNoExistentes {

	/** Logger de la clase */
	private final static Logger logger = LogManager.getLogger(WatcherDocumentosNoExistentes.class.getName());

	/**
	 * Manejador para el tipo {@link Folio }
	 */
	@Autowired
	@Qualifier("documentoAsuntoService")
	private EntityManager<DocumentoAsunto> mngrDocumentoAsunto;

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
	// @Scheduled(cron = "0 0 * * * SUN") // at midnigth
	public void watcher() {
		try {

			String mailNotificacion = environment.getProperty("mailNotificacion");

			// - - -

			List<Criterion> restrictions = new ArrayList<Criterion>();

			ProjectionList projections = Projections.projectionList();

			projections.add(Projections.distinct(Projections.groupProperty("objectId").as("objectId")));

			List<?> documentos = mngrDocumentoAsunto.search(restrictions, null, projections, null, null);

			// - - -

			List<String> documentosNoExisten = new ArrayList<>();

			for (Object d : documentos) {
				Map<String, String> map = (Map<String, String>) d;
				try {
					if (!EndpointDispatcher.getInstance().isDocument(map.get("objectId"))) {
						documentosNoExisten.add(map.get("objectId"));
					}
				} catch (Exception e) {
					
					documentosNoExisten.add(map.get("objectId"));
				}
			}

			// - - -

			if (!documentosNoExisten.isEmpty()) {

				String gg = "<br /> Se encontraro documentos referenciados en documentos q no existen en el repositorio:<br />";

				for (String m : documentosNoExisten) {

					gg += m + "<br />";

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
