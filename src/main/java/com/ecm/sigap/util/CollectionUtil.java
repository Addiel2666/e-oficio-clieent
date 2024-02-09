/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Clase utilitaria para el manejo de los colecciones.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class CollectionUtil {

	/**
	 * Resta un colecction a otro.
	 * 
	 * @param lista
	 * @param listaQuitar
	 * @return
	 */
	public static Collection<String> substract(Collection<String> lista, Collection<String> listaQuitar) {

		List<String> result = new ArrayList<>();

		for (String item : lista) {

			if (listaQuitar.contains(item) || item == null) {
				// esta en la lista q se resta, se omite.
			} else {
				result.add(new String(item));
			}

		}

		return result;
	}

}
