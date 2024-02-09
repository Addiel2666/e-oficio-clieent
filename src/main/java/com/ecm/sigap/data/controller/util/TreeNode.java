/**
 * 
 */
package com.ecm.sigap.data.controller.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Clase para generar arboles de elementos.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class TreeNode<j> {

	/** objeto del nodo */
	private j object;
	/** nodos hijos del elemento */
	private List<TreeNode<j>> children;

	/**
	 * 
	 */
	public TreeNode() {
		super();
	}

	/**
	 * 
	 * @param object
	 */
	public TreeNode(j object) {
		super();
		this.object = object;
	}

	/**
	 * @return the object
	 */
	public j getObject() {
		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(j object) {
		this.object = object;
	}

	/**
	 * @return the children
	 */
	public List<TreeNode<j>> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<TreeNode<j>> children) {
		this.children = children;
	}

	/**
	 * 
	 * @param item
	 */
	public void add(TreeNode<j> item) {

		if (this.children == null)
			this.children = new ArrayList<>();

		this.children.add(item);
	}

	/**
	 * 
	 * @param item
	 */
	public void addAll(Collection<TreeNode<j>> items) {

		if (this.children == null)
			this.children = new ArrayList<>();

		for (TreeNode<j> item : items)
			this.children.add(item);
	}
}
