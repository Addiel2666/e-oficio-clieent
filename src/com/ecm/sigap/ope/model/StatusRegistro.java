/**
 * 
 */
package com.ecm.sigap.ope.model;

/**
 * @author Alfredo Morales
 *
 */
public enum StatusRegistro {

	REGISTRADO("R"), // se a recibido el registro para interoperar,
	CONFIRMADO("C"), // se ha recibido la contrarespuesta del registro,
	;

	/** */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	StatusRegistro(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static StatusRegistro fromTipo(String t) {
		if (t != null) {
			for (StatusRegistro tipo_ : StatusRegistro.values())
				if (t.equalsIgnoreCase(tipo_.value))
					return tipo_;
		} else {
			return REGISTRADO;
		}
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
