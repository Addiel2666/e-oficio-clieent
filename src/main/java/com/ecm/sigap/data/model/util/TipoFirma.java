/**
 * 
 */
package com.ecm.sigap.data.model.util;

/**
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public enum TipoFirma {

	PDF_FIRMA("PDF_FIRMA"), //

	PDF_FIRMA_IMG("PDF_FIRMA_IMG"), //

	PDF_ANTEFIRMA("PDF_ANTEFIRMA"), //

	PDF_CLASIFICACION("PDF_CLASIFICACION"), //

	PDF_DESCLASIFICACION("PDF_DESCLASIFICACION"), //

	PDF_MULTISIGN("PDF_MULTISIGN"),

	CMS("CMS"), //

	AVALUO("AVALUO"), //

	OFICIO("OFICIO"), //

	MSJINTEROP("MSJINTEROP"), //

	OFICIO_AUTOR("OFICIO_AUTOR"), //

	OFICIO_ORGANIZACION("OFICIO_ORGANIZACION"), //

	OFICIO_OTRO("OFICIO_OTRO"), //

	SOLICITUD_RESPUESTA("SOLICITUD_RESPUESTA"), //

	SOLICITUD_REGISTRO("SOLICITUD_REGISTRO");

	/** */
	private final String t;

	/**
	 * 
	 * @param t
	 */
	TipoFirma(String t) {
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
	public static TipoFirma fromString(String t) {
		if (t != null)
			for (TipoFirma tipo_ : TipoFirma.values())
				if (t.equalsIgnoreCase(tipo_.t))
					return tipo_;
		throw new IllegalArgumentException("No constant with text " + t + " found");
	}

}
