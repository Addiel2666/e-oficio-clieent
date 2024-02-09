/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.security.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * @author alfredo morales
 * @version 1.0
 */
public class AesUtil {

	/** */
	private final int keySize;
	/** */
	private final int iterationCount;
	/** */
	private final Cipher cipher;

	/**
	 * @param keySize
	 * @param iterationCount
	 */
	public AesUtil(int keySize, int iterationCount) {
		this.keySize = keySize;
		this.iterationCount = iterationCount;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchPaddingException e) {
			throw fail(e);
		} catch (NoSuchAlgorithmException e) {
			throw fail(e);
		}
	}

	/**
	 * @param salt
	 * @param iv
	 * @param passphrase
	 * @param ciphertext
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException
	 */
	public String decryptHex(byte[] salt, byte[] iv, String passphrase, String ciphertext)
			throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, DecoderException {
		SecretKey key = generateKey(salt, passphrase);
		byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, decodeHex(ciphertext));
		return new String(decrypted, "UTF-8");
	}

	/**
	 * @param salt
	 * @param iv
	 * @param passphrase
	 * @param ciphertext
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException
	 */
	public String decryptB64(byte[] salt, byte[] iv, String passphrase, String ciphertext)
			throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, DecoderException {
		SecretKey key = generateKey(salt, passphrase);
		byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, decodeB64(ciphertext));
		return new String(decrypted, "UTF-8");
	}

	/**
	 * @param encryptMode
	 * @param key
	 * @param iv
	 * @param bytes
	 * @return
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] doFinal(int encryptMode, SecretKey key, byte[] iv, byte[] bytes) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(encryptMode, key, new IvParameterSpec(iv));
		return cipher.doFinal(bytes);

	}

	/**
	 * @param salt
	 * @param passphrase
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private SecretKey generateKey(byte[] salt, String passphrase)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterationCount, keySize);
		SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return key;
	}

	/**
	 * @param str
	 * @return
	 */
	public static byte[] decodeB64(String str) {
		return Base64.decodeBase64(str);
	}

	/**
	 * @param str
	 * @return
	 * @throws DecoderException
	 */
	public static byte[] decodeHex(String str) throws DecoderException {
		return Hex.decodeHex(str.toCharArray());
	}

	/**
	 * @param e
	 * @return
	 */
	private IllegalStateException fail(Exception e) {
		return new IllegalStateException(e);
	}

	/**
	 * @param salt
	 * @param iv
	 * @param passphrase
	 * @param ciphertext
	 * @return
	 */
	public String encryptToHex(byte[] salt, byte[] iv, String passphrase, String ciphertext)
			throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {

		SecretKey key = generateKey(salt, passphrase);
		byte[] encripted = doFinal(Cipher.ENCRYPT_MODE, key, iv, ciphertext.getBytes("UTF-8"));
		return new String(Hex.encodeHex(encripted));

	}

	/**
	 * @param salt
	 * @param iv
	 * @param passphrase
	 * @param ciphertext
	 * @return
	 */
	public String encryptToB64(byte[] salt, byte[] iv, String passphrase, String ciphertext)
			throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKey key = generateKey(salt, passphrase);
		byte[] encripted = doFinal(Cipher.ENCRYPT_MODE, key, iv, ciphertext.getBytes("UTF-8"));
		return Base64.encodeBase64String(encripted);

	}

}
