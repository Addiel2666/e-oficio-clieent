/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

/**
 * Tipos de Destinatarios de un minutario.
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 */
public enum TipoDestinatario {

	FUNCIONARIO_INTERNO(0), //
	FUNCIONARIO_INTERNO_CCP(1), //
	FUNCIONARIO_EXTERNO(2), //
	FUNCIONARIO_EXTERNO_CCP(3), //
	REPRESENTANTE_LEGAL(4), //
	REPRESENTANTE_LEGAL_CCP(5), //
	CIUDADANO(6), //
	CIUDADANO_CCP(7), //
	FUNCIONARIO_INTERNO_TURNO(8), //
	FUNCIONARIO_EXTERNO_TURNO(9), //
	REPRESENTANTE_LEGAL_TURNO(10), //
	CIUDADANO_TURNO(11);
	// CIUDADANO_AMBOS(8);

	/** ID. */
	private final int tipo;

	/**
	 * default constructor.
	 */
	private TipoDestinatario(int tipo) {
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
			return "Funcionario Interno";
		case 1:
			return "Funcionario Interno CCP";
		case 2:
			return "Funcionario Externo";
		case 3:
			return "Funcionario Externo CCP";
		case 4:
			return "Representante Legal";
		case 5:
			return "Representante Legal CCP";
		case 6:
			return "Ciudadano";
		case 7:
			return "Ciudadano CCP";
		case 8:
			return "Funcionario Interno Turno";
		case 9:
			return "Funcionario Externo Turno";
		case 10:
			return "Representante Legal Turno";
		case 11:
			return "Ciudadano Turno";
		default:
			return "";
		}

	}

	/**
	 * 
	 * @param status
	 * @return
	 */
	public static TipoDestinatario fromVal(int tipo) {
		for (TipoDestinatario temp : TipoDestinatario.values()) {
			if (tipo == temp.tipo) {
				return temp;
			}
		}
		return null;
	}

};