package com.ecm.sigap.data.model.util;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoPlantilla {

	POR_AREA(0), //
	INSTITUCIONAL(1);

	/** */
	private final int i;

	/**
	 * 
	 * @param i
	 */
	private TipoPlantilla(int i) {
		this.i = i;
	}

	/**
	 * 
	 * @return
	 */
	public int getTipo() {
		return i;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static TipoPlantilla fromString(int t) {
		for (TipoPlantilla tipo_ : TipoPlantilla.values())
			if (t == tipo_.i)
				return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

	@Override
	public String toString() {

		if (this == INSTITUCIONAL)
			return "INSTITUCIONAL";
		else if (this == POR_AREA)
			return "POR_AREA";

		return super.toString();
	}

}
