/**
 * 
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * Clse de identidad que representa la tabla CUSTOMASUNTOS
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 28-Sep-2016
 * 
 *          Creacion de la clase
 *
 */
@Entity
@Table(name = "CustomAsuntos")
public class CustomAsunto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5376601135911439648L;

	/** Identificador del Asunto */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** Atributo custom de la tabla */
	@Column(name = "custom0")
	String custom0;

	/** Atributo custom de la tabla */
	@Column(name = "custom1")
	String custom1;

	/** Atributo custom de la tabla */
	@Column(name = "custom2")
	String custom2;

	/** Atributo custom de la tabla */
	@Column(name = "custom3")
	String custom3;

	/** Atributo custom de la tabla */
	@Column(name = "custom4")
	String custom4;

	/** Atributo custom de la tabla */
	@Column(name = "custom5")
	String custom5;

	/** Atributo custom de la tabla */
	@Column(name = "custom6")
	String custom6;

	/** Atributo custom de la tabla */
	@Column(name = "custom7")
	String custom7;

	/** Atributo custom de la tabla */
	@Column(name = "custom8")
	String custom8;

	/** Atributo custom de la tabla */
	@Column(name = "custom9")
	String custom9;

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto
	 *            the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * @return the custom0
	 */
	public String getCustom0() {
		return custom0;
	}

	/**
	 * @param custom0
	 *            the custom0 to set
	 */
	public void setCustom0(String custom0) {
		this.custom0 = custom0;
	}

	/**
	 * @return the custom1
	 */
	public String getCustom1() {
		return custom1;
	}

	/**
	 * @param custom1
	 *            the custom1 to set
	 */
	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}

	/**
	 * @return the custom2
	 */
	public String getCustom2() {
		return custom2;
	}

	/**
	 * @param custom2
	 *            the custom2 to set
	 */
	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}

	/**
	 * @return the custom3
	 */
	public String getCustom3() {
		return custom3;
	}

	/**
	 * @param custom3
	 *            the custom3 to set
	 */
	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}

	/**
	 * @return the custom4
	 */
	public String getCustom4() {
		return custom4;
	}

	/**
	 * @param custom4
	 *            the custom4 to set
	 */
	public void setCustom4(String custom4) {
		this.custom4 = custom4;
	}

	/**
	 * @return the custom5
	 */
	public String getCustom5() {
		return custom5;
	}

	/**
	 * @param custom5
	 *            the custom5 to set
	 */
	public void setCustom5(String custom5) {
		this.custom5 = custom5;
	}

	/**
	 * @return the custom6
	 */
	public String getCustom6() {
		return custom6;
	}

	/**
	 * @param custom6
	 *            the custom6 to set
	 */
	public void setCustom6(String custom6) {
		this.custom6 = custom6;
	}

	/**
	 * @return the custom7
	 */
	public String getCustom7() {
		return custom7;
	}

	/**
	 * @param custom7
	 *            the custom7 to set
	 */
	public void setCustom7(String custom7) {
		this.custom7 = custom7;
	}

	/**
	 * @return the custom8
	 */
	public String getCustom8() {
		return custom8;
	}

	/**
	 * @param custom8
	 *            the custom8 to set
	 */
	public void setCustom8(String custom8) {
		this.custom8 = custom8;
	}

	/**
	 * @return the custom9
	 */
	public String getCustom9() {
		return custom9;
	}

	/**
	 * @param custom9
	 *            the custom9 to set
	 */
	public void setCustom9(String custom9) {
		this.custom9 = custom9;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomAsunto [idAsunto=" + idAsunto + ", custom0=" + custom0 + ", custom1=" + custom1 + ", custom2="
				+ custom2 + ", custom3=" + custom3 + ", custom4=" + custom4 + ", custom5=" + custom5 + ", custom6="
				+ custom6 + ", custom7=" + custom7 + ", custom8=" + custom8 + ", custom9=" + custom9 + "]";
	}

}
