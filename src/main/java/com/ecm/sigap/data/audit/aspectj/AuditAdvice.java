/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.audit.aspectj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import com.ecm.sigap.data.audit.service.AuditService;
import com.ecm.sigap.data.controller.CustomRestController;

/**
 * The Class AuditAdvice.
 *
 * @author Gustavo Vielma
 */
@Aspect
public class AuditAdvice extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AuditAdvice.class);

	/** The audit service. */
	@Autowired
	private AuditService auditService;

	/**
	 * Audit screen. Este metodo es el Interceptor del las persistencia a bd
	 * realizado por el Dao.
	 * 
	 * @param joinPoint       the join point
	 * @param auditAnnotation the audit annotation
	 * @throws Exception
	 */
	@AfterReturning("execution(* com.ecm.sigap.data.dao.EntityDAO.*(..)) && @annotation(auditAnnotation) ")
	public void auditScreen(JoinPoint joinPoint, Audit auditAnnotation) throws Exception {

		Object entity = joinPoint.getArgs()[0];

		try {
			if (entity instanceof IAuditLog) {
				log.debug("INICIANDO AUDITORIA DE TIPO >>> " + auditAnnotation.actionType().getValue()
						+ " PARA EL ENTITY >>> " + entity.getClass().getName());
				IAuditLog entityAudit = (IAuditLog) entity;
				auditService.saveAuditoria(auditAnnotation, entityAudit);
			}

			// Envio de notificaciones.
			if ("ON".equalsIgnoreCase(environment.getProperty("mail.service", "OFF")))
				auditService.notificar(auditAnnotation, entity);

		} catch (Exception e) {
//			log.error(e.getLocalizedMessage());
//			s
//			throw e;
		}

	}

}
