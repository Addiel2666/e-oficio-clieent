/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

/**
 * @author Alfredo Morales
 *
 */
public class InfomexSolicitudAsunto {

	/** */
	private InfomexSolicitud infomexSolicitud;

	/** */
	private AsuntoCorrespondencia asuntoCorrespondencia;

	/** */
	private String comentario;

	/**
	 * @return the infomexSolicitud
	 */
	public InfomexSolicitud getInfomexSolicitud() {
		return infomexSolicitud;
	}

	/**
	 * @param infomexSolicitud
	 *            the infomexSolicitud to set
	 */
	public void setInfomexSolicitud(InfomexSolicitud infomexSolicitud) {
		this.infomexSolicitud = infomexSolicitud;
	}

	/**
	 * @return the asuntoCorrespondencia
	 */
	public AsuntoCorrespondencia getAsuntoCorrespondencia() {
		return asuntoCorrespondencia;
	}

	/**
	 * @param asuntoCorrespondencia
	 *            the asuntoCorrespondencia to set
	 */
	public void setAsuntoCorrespondencia(AsuntoCorrespondencia asuntoCorrespondencia) {
		this.asuntoCorrespondencia = asuntoCorrespondencia;
	}

	/**
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * @param comentario
	 *            the comentario to set
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfomexSolicitudAsunto [infomexSolicitud=" + infomexSolicitud + ", asuntoCorrespondencia="
				+ asuntoCorrespondencia + ", comentario=" + comentario + "]";
	}

}
