/**
 * 
 */
package com.ecm.sigap.data.controller.impl;

import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Institucion;

/**
 * @author seele87
 *
 */
public class BodyCrearInstitucion {

	/** */
	private Area area;

	/** */
	private Institucion institucion;

	/** */
	private Boolean hasArea;

	/**
	 * @return the area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * @param institucion the institucion to set
	 */
	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	/**
	 * @return the hasArea
	 */
	public Boolean getHasArea() {
		return hasArea;
	}

	/**
	 * @param hasArea the hasArea to set
	 */
	public void setHasArea(Boolean hasArea) {
		this.hasArea = hasArea;
	}

	@Override
	public String toString() {
		return "BodyCrearInstitucion [area=" + area + ", institucion=" + institucion + ", hasArea=" + hasArea + "]";
	}

}
