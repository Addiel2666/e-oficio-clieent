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
@Table(name = "OPEMENSAJES")
public final class Mensaje implements Serializable {

	/** */
	private static final long serialVersionUID = -4661229129274255149L;
	/** */
	@Id
	@Column(name = "ID")
	private String id;
	/** */
	@Column(name = "MENSAJE")
	private String mensaje;
	/** */
	@Column(name = "MENSAJEFIRMA")
	private String mensajeFirma;
	/** */
	@Column(name = "RESPUESTA")
	private String respuesta;
	/** */
	@Column(name = "RESPUESTAFIRMA")
	private String respuestaFirma;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the mensaje
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * @param mensaje the mensaje to set
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	/**
	 * @return the mensajeFirma
	 */
	public String getMensajeFirma() {
		return mensajeFirma;
	}

	/**
	 * @param mensajeFirma the mensajeFirma to set
	 */
	public void setMensajeFirma(String mensajeFirma) {
		this.mensajeFirma = mensajeFirma;
	}

	/**
	 * @return the respuesta
	 */
	public String getRespuesta() {
		return respuesta;
	}

	/**
	 * @param respuesta the respuesta to set
	 */
	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	/**
	 * @return the respuestaFirma
	 */
	public String getRespuestaFirma() {
		return respuestaFirma;
	}

	/**
	 * @param respuestaFirma the respuestaFirma to set
	 */
	public void setRespuestaFirma(String respuestaFirma) {
		this.respuestaFirma = respuestaFirma;
	}

}
