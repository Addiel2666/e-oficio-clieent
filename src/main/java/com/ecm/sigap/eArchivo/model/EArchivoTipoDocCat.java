package com.ecm.sigap.eArchivo.model;

/**
 * Tipos de Documento
 *
 * @author Adaulfo Herrera
 * @version 1.0
 */
public enum EArchivoTipoDocCat {

	FISICO(0), //
	FISICO_DIG(1), //
	ELEC_FIRMA_PDF(2), //
	ELEC_FIRMA_CMS(3), //
	ELEC_FIRMA_XML(4), //
	ELEC_SIN_FIRMA(5);

	/**
	 * ID.
	 */
	private final int tipo;

	/**
	 * default constructor.
	 */
	private EArchivoTipoDocCat(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return tipo;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {

		switch (tipo) {
		case 0:
			return "FISICO";
		case 1:
			return "FISICO_DIG";
		case 2:
			return "ELEC_FIRMA_PDF";
		case 3:
			return "ELEC_FIRMA_CMS";
		case 4:
			return "ELEC_FIRMA_XML";
		case 5:
			return "ELEC_SIN_FIRMA";
		default:
			return "";
		}

	}

};
