/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.security.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.DecoderException;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class Security {

	/** */
	private static final ResourceBundle config = ResourceBundle.getBundle("security");
	/** */
	private static final int ITERATION_COUNT = 1000;
	/** */
	private static final int KEY_SIZE = 128;

	/**
	 * 
	 * @param encriptedValue
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException
	 */
	public static final String decript(String encriptedValue)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, DecoderException {
		AesUtil aesUtil = new AesUtil(KEY_SIZE, ITERATION_COUNT);

		return aesUtil.decryptHex(//
				config.getString("satlStr").getBytes("UTF-8"), //
				config.getString("ivStr").getBytes("UTF-8"), //
				config.getString("passphrase"), //
				encriptedValue);
	}

	/**
	 * 
	 * @param value
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException
	 */
	public static final String encript(String value)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, DecoderException {
		AesUtil aesUtil = new AesUtil(KEY_SIZE, ITERATION_COUNT);

		return aesUtil.encryptToHex(//
				config.getString("satlStr").getBytes("UTF-8"), //
				config.getString("ivStr").getBytes("UTF-8"), //
				config.getString("passphrase"), //
				value);
	}

}
