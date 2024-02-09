/**
 * 
 */
package com.ecm.sigap.data.model.util;

/**
 * @author alfredo morales
 * @version 1.0
 *
 */
public enum Vlock {

	/** Bloqueado */
	A("A"),

	/** Libre */
	D("D");

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	private Vlock(String t) {
		this.t = t;
	}

	/**
	 * 
	 * @return
	 */
	public String getTipo() {
		return this.t;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static Vlock fromString(String t) {
		if (t != null)
			for (Vlock v_ : Vlock.values())
				if (t.equalsIgnoreCase(v_.t))
					return v_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
