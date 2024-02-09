/**
 * 
 */
package com.ecm.sigap.ope.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Alfredo Morales
 *
 */
@Entity
@Table(name = "OPEConfig")
public final class Configuration implements Serializable {

	/** */
	private static final long serialVersionUID = 4472293683181295608L;

	/** */
	@Id
	@Column(name = "LLAVE")
	private String key;

	/** */
	@Column(name = "VALOR")
	private String value;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
