/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */

package com.ecm.sigap.data.audit.aspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ecm.sigap.data.model.util.TipoAuditoria;

/**
 * The Interface Audit. Anotación para el metodo que requiere auditoria
 * 
 * @author Gustavo Vielma
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Audit {

	/**
	 * Action type.
	 *
	 * @return the tipo auditoria
	 */
	TipoAuditoria actionType();
}
