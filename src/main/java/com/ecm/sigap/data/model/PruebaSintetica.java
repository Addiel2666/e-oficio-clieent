/**
 * 
 */
package com.ecm.sigap.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "PruebaSintetica", //
		schema = "{SIGAP_SCHEMA}" //
)
public class PruebaSintetica {

	/** The descripcion. */
	@Id
	@Column(name = "cadena")
	private String cadena;

	/**
	 * 
	 * @param cadena
	 */
	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

}
