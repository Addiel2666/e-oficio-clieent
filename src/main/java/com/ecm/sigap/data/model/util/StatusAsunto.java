/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase Enumerado que representa los Status de Asuntos que tiene el sistema
 * 
 * @author Adaulfo Herrera
 * @version 1.0
 *
 */
public enum StatusAsunto {

	/** Registrado */
	POR_ENVIAR(0),

	/** Enviado */
	ENVIADO(1),

	/** Proceso */
	PROCESO(2),

	/** Concluido */
	CONCLUIDO(3),

	/** Vencido */
	VENCIDO(4),

	/** Resuelto */
	RESUELTO(5),

	/** Rechazado */
	RECHAZADO(6),

	/** Cancelado */
	CANCELADO(7),

	/** Atendido */
	ATENDIDO(8),

	/**
	 * aux-ninguno - evita devolver resultados (caso respuestas recibidas sin
	 * estado)
	 */
	NINGUNO(9);

	private static final Map<Integer, StatusAsunto> typesByValue = new HashMap<Integer, StatusAsunto>();

	static {
		for (StatusAsunto type : StatusAsunto.values()) {
			typesByValue.put(type.value, type);
		}
	}

	/** id */
	private final int value;

	/**
	 * default constructor.
	 */
	private StatusAsunto(int value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public int getStatus() {
		return value;
	}

	public static StatusAsunto forValue(int value) {
		return typesByValue.get(value);
	}

	/**
	 * 
	 * @param valueOf
	 * @return
	 */
	public static StatusAsunto fromVal(Integer valueOf) {
		for (StatusAsunto temp : StatusAsunto.values()) {
			if (valueOf == temp.value) {
				return temp;
			}
		}
		return null;
	}
	
	public String toString() {
		switch (value) {
		case 0:
			return "REGISTRADO";
		case 1:
			return "ENVIADO";
		case 2:
			return "PROCESO";
		case 3:
			return "CONCLUIDO";
		case 4:
			return "VENCIDO";
		case 5:
			return "RESUELTO";
		case 6:
			return "RECHAZADO";
		case 7:
			return "CANCELADO";
		case 8:
			return "ATENDIDO";
		default:
			return "";
		}
	}
}
