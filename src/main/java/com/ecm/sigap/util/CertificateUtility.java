package com.ecm.sigap.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

/**
 * 
 * Utilitario para manipulacion de certificados digitales.
 * 
 * @author J Alfredo Morales V
 * @version 1.0
 * 
 * 
 * @author Alfredo Morales
 * @version 1.2
 * 
 *          El sistema mostrara una liga "Ver" para ver el acuse del turno, esta
 *          liga debe estar en una columna que diga "Acuse" que debe estar al
 *          lado de la columna "Volante" en la lista de turnos, debe aparecer
 *          para cada turno generado.<br />
 * 
 *          El PDF de acuse de recibo debe llevar los siguientes datos:
 *          <ol>
 *          <il>no. oficio</il><br />
 *          <il>descripcian del asunto</il><br />
 *          <il>area remitente</il><br />
 *          <il>area destinatario</il><br />
 *          <il>folio envao</il><br />
 *          <il>fecha y hora de envao (sello de tiempo)</il><br />
 *          <il>folio recepcian</il><br />
 *          <il>fecha y hora de recepcian (sello de tiempo)</il> <br />
 *          <il>instruccian</il> <br />
 *          <il>lista de documentos anexos al turno o envao</il><br />
 *          <il>folio recepcian</il> <br />
 *          <il>copias marcadas en el turno</il><br />
 *          <il>Indicar si es un turno o un envao</il>
 *          </ol>
 */
public class CertificateUtility {

	/** */
	private static final Logger log = LogManager.getLogger(CertificateUtility.class);

	/** */
	public static final int tipoPKCS12 = 1;
	/** */
	public static final int tipoPKCS7 = 2;
	/** */
	public static final int tipoCERT = 3;

	/**
	 * 
	 * @return
	 */
	public static Certificate getCertificateCurrentMachine() {
		Certificate certificate = null;
		try {
			KeyStore ks = KeyStore.getInstance("Windows-MY");
			ks.load(null, null);
			Enumeration<?> en = ks.aliases();
			while (en.hasMoreElements()) {
				String aliasKey = (String) en.nextElement();
				certificate = ks.getCertificate(aliasKey);
				break;
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			
		}
		return certificate;
	}

	/**
	 * obtener el certificado del usuario desde un archivo pkcs12.
	 * 
	 * @param file
	 * @param password
	 * @return
	 */
	public static Certificate getCertificatePK12(File file, String password) {
		Certificate certificate = null;
		try(InputStream inStream = new FileInputStream(file)) {
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(inStream, password.toCharArray());
			Enumeration<String> aliases = keystore.aliases();
			String alias;
			while (aliases.hasMoreElements()) {
				alias = aliases.nextElement();
				certificate = keystore.getCertificate(alias);
				break;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());			
		}
		return certificate;
	}

	/**
	 * 
	 * @param file
	 * @param string
	 * @return
	 */
	public static Certificate getCertificatePK7(File file, String string) {
		Certificate certificate = null;
		try(InputStream in = new FileInputStream(file)){
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			
			CertPath certPath = cf.generateCertPath(in, "PKCS7");

			List<? extends Certificate> certs = certPath.getCertificates();
			for (Certificate cert : certs) {
				certificate = cert;
				break;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());			
		}
		return certificate;
	}

	/**
	 * 
	 * @param file
	 * @param string
	 * @return
	 */
	public static Certificate getCertificateCert(File file) {
		Certificate certificate = null;
		try(FileInputStream is = new FileInputStream(file)){			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			certificate = cf.generateCertificate(is);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			
		}
		return certificate;
	}

	/**
	 * Obtiene el encoding en base64 de un certificado.
	 * 
	 * @param certificate
	 * @return
	 * @throws CertificateEncodingException
	 */
	public static String getCertificateStringB64(Certificate certificate) throws CertificateEncodingException {

		String certB64__ = DatatypeConverter.printBase64Binary(certificate.getEncoded());

		return certB64__;
	}

	/**
	 * 
	 * Obtiene el encoding en base64 del archivo de un certificado.
	 * 
	 * @param certificate
	 * @return
	 * @throws CertificateEncodingException
	 */
	public static String getCertificateStringB64(File certificate) throws CertificateEncodingException {

		Certificate certificate_ = CertificateUtility.getCertificateCert(certificate);

		String certB64__ = getCertificateStringB64(certificate_);

		return certB64__;
	}

	/**
	 * Obtiene el certificado en base a una cadena Base64.
	 * 
	 * @param certificateStringB64
	 * @return
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static Certificate getCertificateFormStringB64(String certificateStringB64)
			throws CertificateException, IOException {

		byte[] decodedBytes = Base64.decodeBase64(certificateStringB64.getBytes());
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		
		try(InputStream inStream = new ByteArrayInputStream(decodedBytes)){
			return (X509Certificate) cf.generateCertificate(inStream);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Obtiene el archivo pkcs7 de un PDF firmado.
	 * 
	 * @param pdfPath
	 * @return
	 * @throws IOException
	 */
	public static List<File> getPKCS7fromPDF(String pdfPath) throws IOException {

		AcroFields acroFields = new PdfReader(pdfPath).getAcroFields();
		List<String> names_ = acroFields.getSignatureNames();

		List<File> pkcs7Files = new ArrayList<File>();

		File file;
		byte[] origbytes;
		PdfDictionary dict;
		PdfString contents;
		for (String name : names_) {
			dict = acroFields.getSignatureDictionary(name);
			contents = (PdfString) PdfReader.getPdfObject(dict.get(PdfName.CONTENTS));

			origbytes = contents.getOriginalBytes();

			file = File.createTempFile(name, ".p7b");
			file.deleteOnExit();

			try (FileOutputStream fos = new FileOutputStream(file)){
				fos.write(origbytes);
				fos.flush();
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			}

			pkcs7Files.add(file);

		}

		return pkcs7Files;
	}

	/**
	 * Obtiene los certificadoscontenidos en un archivo pkcs7.
	 * 
	 * @param file
	 * @param string
	 * @return
	 */
	public static Certificate getCertificatePK7(File file) {

		Certificate certificate = null;

		try (InputStream in = new FileInputStream(file)){
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			CertPath certPath = cf.generateCertPath(in, "PKCS7");

			List<? extends Certificate> certs = certPath.getCertificates();

			for (Certificate cert : certs) {
				certificate = cert;
				break;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());			
		}
		
		return certificate;
	}

	/**
	 * Encode bytes array to BASE64 string
	 * 
	 * @param bytes
	 * @return Encoded string
	 */
	public static String encodeBASE64(byte[] bytes) {

		return DatatypeConverter.printBase64Binary(bytes);

	}

	/** */
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String encodeHexadecimal(byte[] bytes) {

		char[] hexChars = new char[bytes.length * 2];

		for (int j = 0; j < bytes.length; j++) {

			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];

		}
		return new String(hexChars);

	}

	/**
	 * 
	 * @param bytes
	 * @return String
	 */
	public static String getSha256Hex(byte[] content) {
		log.debug("::: Ejecutando el metodo getSha256Hex(byte[])");
		String hash = DigestUtils.sha256Hex(content);
		return hash;
	}

}
