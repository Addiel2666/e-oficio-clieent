/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * Estados de un asunto generado como minutario.
 * 
 * @author Alfredo M
 * @version 1.0
 * 
 */
public enum StatusMinutario {

	REGISTRADO(1), //

	PARA_REVISION(2), //

	REVISADO(3), //

	AUTORIZADO(4), //

	CONCLUIDO(5), //

	CANCELADO(6);

	/** ID. */
	private final int status;

	/**
	 * default constructor.
	 */
	private StatusMinutario(int status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {

		switch (status) {
		case 1:
			return "Registrado";
		case 2:
			return "Para Revisión";
		case 3:
			return "Revisado";
		case 4:
			return "Autorizado";
		case 5:
			return "Concluido";
		case 6:
			return "Cancelado";
		default:
			return "";
		}

	}

	/**
	 * 
	 * @param status
	 * @return
	 */
	public static StatusMinutario fromVal(int status) {
		for (StatusMinutario temp : StatusMinutario.values()) {
			if (status == temp.status) {
				return temp;
			}
		}
		return null;
	}

};