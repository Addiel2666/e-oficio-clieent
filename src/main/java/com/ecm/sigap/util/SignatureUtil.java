/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
public class SignatureUtil {

	/**
	 * 
	 * @param timestamp
	 * @return
	 * @throws TSPException
	 * @throws IOException
	 * @throws CMSException
	 */
	public static Date timestampToDate(String timestamp) throws TSPException, IOException, CMSException {

		TimeStampToken tst = getTimeStampToken(timestamp);

		Date fecha = tst.getTimeStampInfo().getGenTime();

		return fecha;

	}

	/**
	 * 
	 * @param timestamp
	 * @return
	 * @throws TSPException
	 * @throws IOException
	 * @throws CMSException
	 */
	public static TimeStampToken getTimeStampToken(String timestamp) throws TSPException, IOException, CMSException {

		byte[] stampAcuse = Base64.getDecoder().decode(timestamp);

		TimeStampToken tst = new TimeStampToken(new CMSSignedData(stampAcuse));

		return tst;

	}
	/**
	 * Gets the certificate form string B 64.
	 *
	 * @param certificateStringB64
	 *            the certificate string B 64
	 * @return the certificate form string B 64
	 * @throws CertificateException
	 *             the certificate exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Certificate getCertificateFormStringB64(String certificateStringB64)
			throws CertificateException, IOException {

		byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(certificateStringB64);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		try (InputStream inStream = new ByteArrayInputStream(decodedBytes)){

			X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

			cert.checkValidity();

			return cert;
		}  catch (Exception e) {
			throw e;
		}
	}
}
