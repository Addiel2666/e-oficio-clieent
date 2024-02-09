/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.util;

import java.util.List;
import java.util.Map;

/**
 * 
 * Wrapper para busqueda de Objetos.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
public final class RequestWrapper<K> {

	/** Arquetipo de busqueda. */
	private K object;
	/** Parametros adicionales de Busqueda. */
	private Map<String, Object> params;
	/** Orden de la lista devuelta. */
	private List<Order> orders;
	/** indice inicial de los objetos devueltos. */
	private Integer beginAt;
	/** cantidad de objetos devueltos. */
	private Integer size;

	/**
	 * 
	 */
	public RequestWrapper() {
		super();
	}

	/**
	 * 
	 */
	public RequestWrapper(K object) {
		super();
		this.object = object;
	}

	/**
	 * @return the object
	 */
	public K getObject() {
		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(K object) {
		this.object = object;
	}

	/**
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	/**
	 * @return the orders
	 */
	public List<Order> getOrders() {
		return orders;
	}

	/**
	 * @param orders
	 *            the orders to set
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	/**
	 * @return the beginAt
	 */
	public Integer getBeginAt() {
		return beginAt;
	}

	/**
	 * @param beginAt
	 *            the beginAt to set
	 */
	public void setBeginAt(Integer beginAt) {
		this.beginAt = beginAt;
	}

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestWrapper [object=" + object + ", params=" + params + ", orders=" + orders + ", beginAt=" + beginAt
				+ ", size=" + size + "]";
	}

}
