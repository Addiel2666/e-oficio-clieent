/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;

/**
 * Interfaces de operaciones disponibles para manejo de datos hacia base de
 * datos.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 * @param <T> Tipo de objeto devuelto.
 */
public interface EntityManager<T> {

	/**
	 * Obtener.
	 * 
	 * @param id ID del objeto.
	 * @return Objeto solicitado.
	 */
	public T fetch(Serializable id);

	/**
	 * Guardar.
	 * 
	 * @param item Nuevo objeto a agregar.
	 */
	public void save(T item) throws Exception;

	/**
	 * Actualizar.
	 * 
	 * @param item
	 */
	public void update(T item);

	/**
	 * Actualizar y Obtener
	 * 
	 * @param item
	 */
	public T merge(T item);

	/**
	 * Eliminar.
	 * 
	 * @param item Objeto que se eliminara.
	 */
	public void delete(T item);

	/**
	 * 
	 * Execute Named Query.
	 * 
	 * @param queryName Nombre del query a ejecutar.
	 * @param params    Parametros de query.
	 * @return objetos devueltos por el query.
	 */
	public List<T> execNamedQuery(String queryName, HashMap<String, Object> params);

	/**
	 *
	 * Execute Named Query.
	 *
	 * @param sqlquery query a ejecutar.
	 * @return objetos devueltos por el query.
	 */
	public List<T> execQuery(String sqlquery);

	/**
	 * 
	 * @param query
	 * @param i
	 * @param j
	 * @return
	 */
	public List<T> execQuery(String query, int firstResult, int maxResult);

	/**
	 * Ejecuta un procedure/query retornando la cantidad de objetos afectados.
	 * 
	 * @param queryName Nombre del query a ejecutar.
	 * @param params    Parametros del query.
	 * @return Cantidad de renglones afectados.
	 */
	public Integer execUpdateQuery(String queryName, HashMap<String, Object> params);

	/**
	 * Busqueda parametrizada.
	 * 
	 * @param restrictions Lista de Parametros de busqueda.
	 * @param orders       Lista de ordenes de la lista devueta.
	 * @param fetchSize    Cantidad de elementos devueltos.
	 * @param firstResult  Inidice del primer objeto devuelto.
	 * @return Lista de Objetos devueltos en la busqueda.
	 */
	@SuppressWarnings("rawtypes")
	public List search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections, Integer fetchSize,
			Integer firstResult);

	/**
	 * Busqueda parametrizada.
	 * 
	 * @param restrictions Lista de Parametros de busqueda.
	 * @return Lista de Objetos devueltos en la busqueda.
	 */
	public List<?> search(List<Criterion> restrictions);

	/**
	 * Busqueda parametrizada.
	 * 
	 * @param restrictions Lista de Parametros de busqueda.
	 * @param orders       Lista de ordenes de la lista devueta.
	 * @return Lista de Objetos devueltos en la busqueda.
	 */
	public List<?> search(List<Criterion> restrictions, List<Order> orders);

	/**
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	public Object uniqueResult(String queryName, HashMap<String, Object> params);

	/**
	 * 
	 * @return
	 */
	public String isConnected();

	/**
	 * 
	 * @param seq
	 * @return
	 */
	public Long getNextval(String seq);

	/**
	 * 
	 */
	public void beginTransaction();

	/**
	 * 
	 */
	public void commit();

	/**
	 * 
	 */
	public void rollback();

	/**
	 * 
	 */
	public void flush();

	/**
	 *
	 * Execute Native Query.
	 *
	 * @param sqlquery query a ejecutar.
	 * @param params   Parametros de query.
	 * @return objetos devueltos por el query.
	 */
	public List<?> execNativeQuery(String sqlquery, HashMap<String, Object> params);

	/**
	 * 
	 * @param procedureName
	 * @param params
	 * @return
	 */
	public Boolean createStoredProcedureCall(String procedureName, LinkedHashMap<String, Object> params);

	/**
	 * 
	 * @param string
	 * @param params
	 */
	public void execNativeUpdateQuery(String string, HashMap<String, Object> params);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public T saveOrUpdate(T item);

	/**
	 * 
	 * @param procedureName
	 * @param paramsIn
	 * @param paramsOut
	 * @return
	 */
	public HashMap<String, Object> createStoredProcedureCall(String procedureName,
			LinkedHashMap<String, Object> paramsIn, LinkedHashMap<String, Object> paramsOut);

	/**
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	public Object callFunction(String queryName, HashMap<String, Object> params);

	/**
	 * Auxiliar de actualizar.
	 * 
	 * @param item
	 */
	public void updateBitacora(T item);

	/**
	 * Auxiliar de actualizar.
	 * 
	 * @param item
	 */
	public void inactivate(T item);

}