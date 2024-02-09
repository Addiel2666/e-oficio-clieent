/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.dao;

import java.io.Serializable;
import java.util.HashMap;
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
public interface EntityDAO<T> {

	/**
	 * obtener.
	 * 
	 * @param id
	 * @return
	 */
	public T fetch(Serializable id);

	/**
	 * guardar.
	 * 
	 * @param item
	 */
	public void save(T item);

	/**
	 * actualizar.
	 * 
	 * @param item
	 */
	public void update(T item);

	/**
	 * actualizar y obtener
	 * 
	 * @param item
	 */
	public T merge(T item);

	/**
	 * eliminar.
	 * 
	 * @param item
	 */
	public void delete(T item);

	/**
	 * Execute Named Query.
	 * 
	 * @param queryName Nombre del Query.
	 * @param params    Parametros del Query.
	 * @return Lista de objectos devueltos por el query.
	 */
	public List<T> execNamedQuery(String queryName, HashMap<String, Object> params);

	/**
	 * Execute Named Query.
	 *
	 * @param query SQL Query.
	 * @return Lista de objectos devueltos por el query.
	 */
	public List<T> execQuery(String query);

	/**
	 * 
	 * @param query
	 * @param firstResult
	 * @param maxResult
	 * @return
	 */
	public List<T> execQuery(String query, int firstResult, int maxResult);

	/**
	 * Ejecutar un query por nombre.
	 * 
	 * @param queryName Nombre del query a ejecutar.
	 * @param params    PArametros del query.
	 * @return Numero de Renglones Afectados.
	 */
	public Integer execUpdateQuery(String queryName, HashMap<String, Object> params);

	/**
	 * Ejecutar un query por nombre que devuelve un valor unico.
	 * 
	 * @param queryName Nombre del query a ejecutar.
	 * @param params    Parametros del query.
	 * @return Valor unico devuelto por el query.
	 */
	public Object uniqueResult(String queryName, HashMap<String, Object> params);

	/**
	 * Busqueda parametrizada.
	 * 
	 * @param restrictions
	 * @param orders
	 * @param fetchSize
	 * @param firstResult
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections, Integer fetchSize,
			Integer firstResult);

	/**
	 * Valida si la coneccion a base de datos esta activa.
	 * 
	 * @return
	 */
	public String isConnected();

	/**
	 * Regresa la secuencia indicada
	 * 
	 * @return
	 */
	public Long getNextval(String seq);

	/**
	 * 
	 */
	public void flush();

	/**
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	public List<?> execNativeQuery(String queryName, HashMap<String, Object> params);

	/**
	 * 
	 * @param procedureName
	 * @param params
	 * @return
	 */
	public Boolean createStoredProcedureCall(String procedureName, HashMap<String, Object> params);

	/**
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	public Integer execNativeUpdateQuery(String queryName, HashMap<String, Object> params);

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
	public HashMap<String, Object> createStoredProcedureCall(String procedureName, HashMap<String, Object> paramsIn,
			HashMap<String, Object> paramsOut);

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
	 * Auxiliar para actualizar
	 * @param item
	 */
	public void inactivate(T item);	

}
