/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Collections;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotAcceptableException;

import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecm.sigap.data.exception.SessionException;

/**
 * Este controlador atrapa los error generados en los controladores REST y los
 * devuelve en manera de JSON.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@ControllerAdvice
public class GlobalExceptionController {

	/** Log de suscesos. */
	private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionController.class);

	/** */
	private static final ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages");

	/**
	 * Handler para errores tipo {@link Exception}
	 * 
	 * @param ex Exception ocurrida durante la llamada de los servicios Rest
	 * @return Json que representa el error que se presento y el Codigo de Error
	 *         HTTP que representa dicho error
	 */
	@ExceptionHandler(Exception.class)
	public @ResponseBody ResponseEntity<Map<String, String>> handleAllException(Exception ex) {

		Map<String, String> result = new HashMap<String, String>();

		String errorCause;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		if (ex != null && ex.getCause() != null) {

			if (ex.getClass() == GenericJDBCException.class) {

				// DATABASE ERROR
				errorCause = errorMessages.getString("dataBaseError");

				GenericJDBCException ex2 = (GenericJDBCException) ex;

				result.put("sql", ex2.getSQL());
				result.put("sqlMessage", ex2.getSQLException().getMessage());

			} else if (ex.getClass() == CmisException.class) {

				// REPOSITORY ERROR
				errorCause = errorMessages.getString("repositoryError");

			} else if (ex.getCause().toString().contains("ErrorNotificacionMessage")) {

				errorCause = errorMessages.getString("notificacion.error");
			} else {

				// GENERIC ERROR
				errorCause = errorMessages.getString("internalServerError");

			}

		} else {

			// NULLPOINTER ERROR
			errorCause = errorMessages.getString("internalServerError");
			result.put("isNullPointer", Boolean.TRUE.toString());

		}

		// error
		if (ex.getMessage() != null) {
			result.put("statusText", ":: " + ex.getMessage());
		} else {
			result.put("statusText", ":: " + errorCause);
		}
		// mensaje
		result.put("errorCause", errorCause);

		// stackTrace
		StringWriter sw = new StringWriter();

		result.put("stackTrace", sw.toString());

		// response status
		HttpStatus status;

		if (ex instanceof MissingServletRequestParameterException) {

			// Exception del tipo 400 (Bad Request)
			LOGGER.error("::>> Los parametros requeridos por el metodo no fueron enviados completos");
			status = HttpStatus.BAD_REQUEST;

		} else if (ex instanceof HttpRequestMethodNotSupportedException) {

			// Exception del tipo 405 (Method Not Allowed)
			LOGGER.error("::>> El metodo que se quiere llamar no esta soportado");
			status = HttpStatus.METHOD_NOT_ALLOWED;

		} else if (ex instanceof ConstraintViolationException) {

			// Exception del tipo 409 (Conflict)
			LOGGER.error("::>> Se violo alguna de las reglas de la entidad ");
			status = HttpStatus.CONFLICT;

		} else if (ex instanceof JDBCConnectionException || ex instanceof CmisException) {

			// Exception del tipo 503 (Service Unavailable)
			LOGGER.error("::>> Error al establecer la conexion con la base de datos ");
			status = HttpStatus.SERVICE_UNAVAILABLE;

		} else if (ex instanceof PersistenceException) {

			LOGGER.error("::>> Error de Persistencia ");
			status = HttpStatus.INTERNAL_SERVER_ERROR;

		} else if (ex instanceof AccessDeniedException) {

			// Exception del tipo 401
			LOGGER.error("::>> Error de permisos en la operacion ");
			status = HttpStatus.UNAUTHORIZED;

		} else if (ex instanceof NotAcceptableException) {
			// Exception del tipo 400 (Bad Request)
			LOGGER.error("::>> Los parametros requeridos por el metodo no fueron enviados completos");
			status = HttpStatus.NOT_ACCEPTABLE;

		} else {

			// Exception del tipo 500 (Internal Server Error)
			LOGGER.error("::>> Error Interno del Servidor ");
			status = HttpStatus.INTERNAL_SERVER_ERROR;

		}

		LOGGER.error(ex.getLocalizedMessage());

		return new ResponseEntity<Map<String, String>>(result, responseHeaders, status);

	}

	/**
	 * 
	 * @param ex
	 * @return
	 */
	@Deprecated
	public @ResponseBody ResponseEntity<Map<String, String>> handleAllExceptionOld(Exception ex) {

		Map<String, String> result = new HashMap<String, String>();

		// Se valida que la excepcion traiga una causa, de lo contrario se
		// coloca una generica
		if (null != ex.getCause()) {
			result.put("errorCause", ex.getCause().toString());
		} else {
			result.put("errorCause", "Error interno en el servidor");
		}

		result.put("statusText", " " + ex.getMessage());

		if (ex.getClass() == GenericJDBCException.class) {
			result.put("sql", ((GenericJDBCException) ex).getSQL());
			result.put("sqlMessage", ((GenericJDBCException) ex).getSQLException().getMessage());
		}

		StringWriter sw = new StringWriter();

		result.put("stackTrace", sw.toString());

		if (ex instanceof MissingServletRequestParameterException) {
			// Exception del tipo 400 (Bad Request)
			LOGGER.error("::>> Los parametros requeridos por el metodo no fueron enviados completos");
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.BAD_REQUEST);

		} else if (ex instanceof HttpRequestMethodNotSupportedException) {

			// Exception del tipo 405 (Method Not Allowed)
			LOGGER.error("::>> El metodo que se quiere llamar no esta soportado");
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.METHOD_NOT_ALLOWED);

		} else if (ex instanceof ConstraintViolationException) {

			// Exception del tipo 409 (Conflict)
			LOGGER.error("::>> Se violo alguna de las reglas de la entidad ");
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.CONFLICT);
		} else if (ex instanceof JDBCConnectionException) {

			// Exception del tipo 503 (Service Unavailable)
			LOGGER.error("::>> Error al establecer la conexion con la base de datos ");
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.SERVICE_UNAVAILABLE);
		} else if (ex instanceof AccessDeniedException) {

			// Exception del tipo 401
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.UNAUTHORIZED);
		}

		// Exception del tipo 500 (Internal Server Error)
		return new ResponseEntity<Map<String, String>>(result, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	 @ExceptionHandler(SessionException.class)
	    public ResponseEntity<?> resourceSessionException(SessionException ex) {
	    	final Map<String, String> error = Collections.singletonMap("error", ex.getMessage());
	        return new ResponseEntity<Map<String, String>>(error, HttpStatus.NOT_ACCEPTABLE);
	    }

}
