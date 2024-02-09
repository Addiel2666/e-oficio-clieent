/**
 * 
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

/**
 * @author jmorales
 *
 */
@Entity
@Table(name = "ContadoresView")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@Immutable
public class ContadoresView implements Serializable {

	/** */
	private static final long serialVersionUID = 8268557000032983256L;

	/** The id area. */
	@Id
	@Column(name = "idArea")
	private Integer idArea;

	@Column(name = "COPIASRECIBIDASDERESPUESTA")
	private Integer copiasRecibidasDeRespuesta;

	@Column(name = "RESPUESTASRECIBIDAS")
	private Integer respuestaRecibidas;

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the copiasRecibidasDeRespuesta
	 */
	public Integer getCopiasRecibidasDeRespuesta() {
		return copiasRecibidasDeRespuesta;
	}

	/**
	 * @param copiasRecibidasDeRespuesta the copiasRecibidasDeRespuesta to set
	 */
	public void setCopiasRecibidasDeRespuesta(Integer copiasRecibidasDeRespuesta) {
		this.copiasRecibidasDeRespuesta = copiasRecibidasDeRespuesta;
	}

	/**
	 * @return the respuestaRecibidas
	 */
	public Integer getRespuestaRecibidas() {
		return respuestaRecibidas;
	}

	/**
	 * @param respuestaRecibidas the respuestaRecibidas to set
	 */
	public void setRespuestaRecibidas(Integer respuestaRecibidas) {
		this.respuestaRecibidas = respuestaRecibidas;
	}

	@Override
	public String toString() {
		return "ContadoresView [idArea=" + idArea + ", copiasRecibidasDeRespuesta=" + copiasRecibidasDeRespuesta
				+ ", respuestaRecibidas=" + respuestaRecibidas + "]";
	}

}
