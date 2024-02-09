/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.ResponseEntity;

/**
 * 
 * Metodos expuestos en los controlladores REST.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public interface RESTController<T> {

	/**
	 *
	 * Obtiene una instancia de {@link T} por ID.
	 *
	 * @param id
	 * @return
	 */
	public ResponseEntity<T> get(Serializable id);

	/**
	 * Eliminar una instancia de {@link T}
	 * 
	 * @param id
	 * @throws Exception 
	 */
	public void delete(Serializable id) throws Exception;

	/**
	 * Busqueda parametrizasda de objetos {@link T}
	 *
	 * @param object
	 * @return
	 */
	public ResponseEntity<List<?>> search(T object) throws Exception;

	/**
	 * Guarda una instancia de {@link T}
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<T> save(T object) throws Exception;

}
