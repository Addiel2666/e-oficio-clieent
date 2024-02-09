/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.security.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Clase utilitaria para Encriptar y Desencriptar un texto usando el algoritmo
 * de Cifrado AES
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 21-Nov-2013
 * 
 */
public class CryptoUtil {

	/** Initialization vector */
	private static byte[] IV = { -47, 1, 16, 84, 2, 101, 110, 83, 111, 109, 101, 32, 78, 70, 67, 32 };

	/** Tipo de Transformacion a aplicar en el Cifrado / Descifrado */
	private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	/**
	 * Encripta un texto usando la llave que se pasa como parametro
	 * 
	 * @param message Texto a ser encriptado
	 * @param key     Llave de encriptaci�n
	 * @return Texto Encriptado
	 * @throws SecurityException Cualquier error al momento de hacer la
	 *                           transformacion
	 */
	public static String decryptText(String message, byte[] key) throws SecurityException {

		try {

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

			IvParameterSpec ivspec = new IvParameterSpec(IV);

			// Se creamos el objeto Cipher para el tipo de Transformacion
			// definida
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);

			// Inicializamos el objeto Cipher en modo para Desencriptar
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

			// Desencriptamos el texto
			byte[] decrypted = cipher.doFinal(fromHex(message));

			return new String(decrypted);

		} catch (InvalidKeyException e) {

			
			throw new SecurityException("Error del tipo 'InvalidKeyException' al momento de encriptar el mensaje "
					+ "con la siguiente descripcion: " + e.getMessage());

		} catch (NoSuchAlgorithmException e) {

			
			throw new SecurityException("Error del tipo 'NoSuchAlgorithmException' al momento de encriptar el mensaje "
					+ "con la siguiente descripcion: " + e.getMessage());

		} catch (NoSuchPaddingException e) {

			
			throw new SecurityException("Error del tipo 'NoSuchPaddingException' al momento de encriptar el mensaje "
					+ "con la siguiente descripcion: " + e.getMessage());

		} catch (InvalidAlgorithmParameterException e) {

			
			throw new SecurityException("Error del tipo 'InvalidAlgorithmParameterException' al momento de encriptar "
					+ "el mensaje con la siguiente descripcion: " + e.getMessage());

		} catch (IllegalBlockSizeException e) {

			
			throw new SecurityException("Error del tipo 'IllegalBlockSizeException' al momento de encriptar el mensaje "
					+ "con la siguiente descripcion: " + e.getMessage());

		} catch (BadPaddingException e) {

			
			throw new SecurityException("Error del tipo 'BadPaddingException' al momento de encriptar el mensaje con "
					+ "la siguiente descripcion: " + e.getMessage());
		} catch (DecoderException e) {
			
			throw new SecurityException("Error del tipo 'BadPaddingException' al momento de encriptar el mensaje con "
					+ "la siguiente descripcion: " + e.getMessage());
		}
	}

	/**
	 * Desencripta un texto usando la llave que se pasa como parametro
	 * 
	 * @param message Texto a ser desencriptado
	 * @param key     Llave de desencriptacion
	 * @return Texto desencriptado o plano
	 * @throws SecurityException Cualquier error al momento de hacer la
	 *                           transformacion
	 */
	public static String encryptText(String message, byte[] key) throws SecurityException {

		try {

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

			IvParameterSpec ivspec = new IvParameterSpec(IV);

			// Se creamos el objeto Cipher para el tipo de Transformacion
			// definida
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);

			// Inicializamos el objeto Cipher en modo para Desencriptar
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

			// Encriptamos el texto
			byte[] encrypted = cipher.doFinal(message.getBytes());

			return toHex(encrypted);

		} catch (InvalidKeyException e) {

			
			throw new SecurityException("Error del tipo 'InvalidKeyException' al momento de desencriptar el mensaje "
					+ "con la siguiente descripcion: " + e.getMessage());

		} catch (NoSuchAlgorithmException e) {

			
			throw new SecurityException(
					"Error del tipo 'NoSuchAlgorithmException' al momento de desencriptar el mensaje "
							+ "con la siguiente descripcion: " + e.getMessage());

		} catch (NoSuchPaddingException e) {

			
			throw new SecurityException("Error del tipo 'NoSuchPaddingException' al momento de desencriptar el mensaje "
					+ "con la siguiente descripcion: " + e.getMessage());

		} catch (InvalidAlgorithmParameterException e) {

			
			throw new SecurityException(
					"Error del tipo 'InvalidAlgorithmParameterException' al momento de desencriptar "
							+ "el mensaje con la siguiente descripcion: " + e.getMessage());

		} catch (IllegalBlockSizeException e) {

			
			throw new SecurityException(
					"Error del tipo 'IllegalBlockSizeException' al momento de desencriptar el mensaje "
							+ "con la siguiente descripcion: " + e.getMessage());

		} catch (BadPaddingException e) {

			
			throw new SecurityException(
					"Error del tipo 'BadPaddingException' al momento de desencriptar el mensaje con "
							+ "la siguiente descripcion: " + e.getMessage());
		}
	}

	/**
	 * Convierte un Texto en Hexadecimal a un arreglo de bytes
	 * 
	 * @param text Texto en Hexadecimal
	 * @return Arreglo de bytes
	 * @throws DecoderException Error al momento de hacer la conversion
	 */
	public static byte[] fromHex(String text) throws DecoderException {

		return Hex.decodeHex(text.toCharArray());

	}

	/**
	 * Conviert un Arreglo de bytes a su representancion en texto
	 * 
	 * @param bytes Arreglo de bytes
	 * @return Representacion en texto del arreglo de bytes
	 */
	public static String toHex(byte[] bytes) {

		return Hex.encodeHexString(bytes);
	}

}