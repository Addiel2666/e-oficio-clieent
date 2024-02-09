/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

public enum CryptographicAlgorithmName {

	/**
	 * The MD2 message digest algorithm as defined in RFC 1319.
	 * <p>
	 * <i> Can be specified when generating an instance of MessageDigest </i>
	 */
	MD2("MD2"),

	/**
	 * The MD5 message digest algorithm as defined in RFC 1321.
	 * <p>
	 * <i> Can be specified when generating an instance of MessageDigest </i>
	 */
	MD5("MD5"),

	/**
	 * The Secure Hash Algorithm, as defined in Secure Hash Standard, NIST FIPS
	 * 180-1.
	 * <p>
	 * <i> Can be specified when generating an instance of MessageDigest </i>
	 */
	SHA1("SHA-1"),

	/**
	 * The Secure Hash Algorithm, as defined in Secure Hash Standard, NIST FIPS
	 * 180-1.
	 * <p>
	 * <i> Can be specified when generating an instance of MessageDigest </i>
	 */
	SHA256("SHA-256"),

	/**
	 * The Secure Hash Algorithm, as defined in Secure Hash Standard, NIST FIPS
	 * 180-1.
	 * <p>
	 * <i> Can be specified when generating an instance of MessageDigest </i>
	 */
	SHA384("SHA-384"),

	/**
	 * The Secure Hash Algorithm, as defined in Secure Hash Standard, NIST FIPS
	 * 180-1.
	 * <p>
	 * <i> Can be specified when generating an instance of MessageDigest </i>
	 */
	SHA512("SHA-512"),

	/**
	 * The Digital Signature Algorithm as defined in FIPS PUB 186.
	 * <p>
	 * <i>Can be specified when generating an instance of KeyPairGenerator,
	 * KeyFactory, AlgorithmParameterGenerator, and AlgorithmParameters </i>
	 */
	DSA("DSA"),

	/**
	 * The RSA encryption algorithm as defined in PKCS #1.
	 * <p>
	 * <i>Can be specified when generating an instance of KeyPairGenerator,
	 * KeyFactory, AlgorithmParameterGenerator, and AlgorithmParameters </i>
	 */
	RSA("RSA"),

	/**
	 * The DSA with SHA-1 signature algorithm which uses the SHA-1 digest algorithm
	 * and DSA to create and verify DSA digital signatures as defined in FIPS PUB
	 * 186.
	 * <p>
	 * <i> Can be specified when generating an instance of Signature. </i>
	 */
	SHA1withDSA("SHA1withDSA"),

	/**
	 * The signature algorithm with SHA-1 and the RSA encryption algorithm as
	 * defined in the OSI Interoperability Workshop, using the padding conventions
	 * described in PKCS #1.
	 * <p>
	 * <i> Can be specified when generating an instance of Signature. </i>
	 */
	SHA1withRSA("SHA1withRSA"),

	/**
	 * The MD2 with RSA Encryption signature algorithm which uses the MD2 digest
	 * algorithm and RSA to create and verify RSA digital signatures as defined in
	 * PKCS #1.
	 * <p>
	 * <i> Can be specified when generating an instance of Signature. </i>
	 */
	MD2withRSA("MD2withRSA"),

	/**
	 * The MD5 with RSA Encryption signature algorithm which uses the MD5 digest
	 * algorithm and RSA to create and verify RSA digital signatures as defined in
	 * PKCS #1.
	 * <p>
	 * <i> Can be specified when generating an instance of Signature. </i>
	 */
	MD5withRSA("MD5withRSA"),

	/**
	 * The name of the pseudo-random number generation (PRNG) algorithm supplied by
	 * the SUN provider. This implementation follows the IEEE P1363 standard,
	 * Appendix G.7: "Expansion of source bits", and uses SHA-1 as the foundation of
	 * the PRNG. It computes the SHA-1 hash over a true-random seed value
	 * concatenated with a 64-bit counter which is incremented by 1 for each
	 * operation. From the 160-bit SHA-1 output, only 64 bits are used.
	 * <p>
	 * <i> Can be specified when generating an instance of SecureRandom. </i>
	 */
	SHA1PRNG("SHA1PRNG"),

	/**
	 * The certificate type defined in X.509.
	 * <p>
	 * <i> Can be specified when generating an instance of CertificateFactory </i>
	 */
	X509("X.509"),

	/**
	 * The name of the keystore implementation provided by the SUN provider.
	 * <p>
	 * <i> Can be specified when generating an instance of KeyStore </i>
	 */
	JKS("JKS"),

	/**
	 * The transfer syntax for personal identity information as defined in PKCS #12.
	 * <p>
	 * <i> Can be specified when generating an instance of KeyStore </i>
	 */
	PKCS12("PKCS12");

	private final String algorithm;

	private CryptographicAlgorithmName(String algorithm) {

		this.algorithm = algorithm;
	}

	public String getAlgorithm() {

		return algorithm;
	}
}
