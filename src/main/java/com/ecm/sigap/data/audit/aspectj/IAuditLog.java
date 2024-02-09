/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.audit.aspectj;

/**
 * The Interface IAuditLog.
 */
public interface IAuditLog {

	/**
	 * Obtiene el Identificador de la clase para la Auditoria
	 *
	 * @return Identificador de la clase para la Auditoria
	 */
	public String getId();

	/**
	 * Obtiene el Detalle del log de Auditoria
	 *
	 * @return Detalle del log de Auditoria
	 */
	public String getLogDeatil();

}
