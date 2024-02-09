/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.security.util;

import java.util.Random;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
public class ClaveGenerator {
	/**
	 * Lista de posibles caracteres con los cuales de genera la clave de acceso
	 * enviada por correo
	 */
	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	/** Longuitud de la clave de acceso */
	private static final int RANDOM_STRING_LENGTH = 8;

	/**
	 * genera una clave de accesso.
	 * 
	 * @return
	 */
	public static String generateClave() {

		ClaveGenerator cg = new ClaveGenerator();

		String claveAcceso = cg.generateString(CHAR_LIST, RANDOM_STRING_LENGTH);

		return claveAcceso;

	}

	/**
	 * 
	 * @param characters
	 * @param length
	 * @return
	 */
	private String generateString(String characters, int length) {
		Random randomGenerator = new Random();

		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(randomGenerator.nextInt(characters.length()));
		}
		return new String(text);
	}

}
