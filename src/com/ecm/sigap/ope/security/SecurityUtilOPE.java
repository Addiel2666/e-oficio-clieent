/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ecm.sigap.ope.client.exception.HashInSignatureNoMatchException;
import com.ecm.sigap.ope.model.EnvelopResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author Alfredo Morales
 *
 */
public final class SecurityUtilOPE {

	/** */
	public static final String TEMP_FILES_SUFIX = ".xml";
	/** */
	public static final String TEMP_FILES_PREFIX = "ecmOPEMessage_";
	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(SecurityUtilOPE.class);
	/** */
	private static final ResourceBundle config = ResourceBundle.getBundle("com.ecm.sigap.ope.config.config");
	/** */
	private static XmlMapper xmlMapper;

	static {
		xmlMapper = new XmlMapper();
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * 
	 * @param rutaJKS
	 * @param passcodeJKS
	 * @param identificadorJKS
	 * @return
	 * @throws KeyStoreException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws UnrecoverableEntryException
	 */
	public static KeyStore.PrivateKeyEntry getPrivateKey(String rutaJKS, String passcodeJKS, String identificadorJKS)
			throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableEntryException {

		// Load the KeyStore and get the signing key and certificate.
		KeyStore ks = KeyStore.getInstance("JKS");

		InputStream jksStream = FileUtils.openInputStream(new File(rutaJKS));

		char[] storeKey = passcodeJKS.toCharArray();
		ks.load(jksStream, storeKey);

		KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(identificadorJKS,
				new KeyStore.PasswordProtection(storeKey));

		return keyEntry;
	}

	public static void importCertInStore(String rutaJKS, String passcodeJKS, String identificadorJKS, Certificate cert)
			throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableEntryException {

		KeyStore ks = KeyStore.getInstance("JKS");

		InputStream jksStream = FileUtils.openInputStream(new File(rutaJKS));

		char[] storeKey = passcodeJKS.toCharArray();
		ks.load(jksStream, storeKey);

		ks.setCertificateEntry(identificadorJKS, cert);

	}

	/**
	 * Genera el hash del contenido indicado,
	 * 
	 * @param contenet
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws JsonProcessingException
	 */
	public static String generateHash(Object object) throws NoSuchAlgorithmException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		String contenet = objectMapper.writeValueAsString(object);

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(contenet.getBytes());
		return Hex.encodeHexString(messageDigest.digest());
	}

	/**
	 * Crea un envelop firmado conteniendo el hash,
	 * 
	 * @param hash
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws MarshalException
	 * @throws XMLSignatureException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableEntryException
	 * @throws TransformerException
	 */
	public static String createSignature(String hash)
			throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, SAXException,
			ParserConfigurationException, MarshalException, XMLSignatureException, KeyStoreException,
			CertificateException, UnrecoverableEntryException, TransformerException {

		File fileToSign = null;
		File signed = null;

		try {

			String rutaJKS = config.getString("jkstore.location") + File.separatorChar
					+ config.getString("jkstore.name");
			String passcodeJKS = config.getString("jkstore.key");
			String identificadorJKS = config.getString("jkstore.cert");

			PrivateKey pk = getPrivateKey(rutaJKS, passcodeJKS, identificadorJKS).getPrivateKey();

			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
					Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
					null, null);

			// Create the SignedInfo.
			SignedInfo si = fac.newSignedInfo(
					fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
					fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

			// Instantiate the document to be signed.
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setNamespaceAware(true);

			// - - -

			ObjectMapper objectMapper = new XmlMapper();

			EnvelopResponse re = new EnvelopResponse();

			re.setId(UUID.randomUUID().toString());
			re.setHash(hash);
			re.setFechaRegistro(new Date());

			String xml = objectMapper.writeValueAsString(re);

			fileToSign = File.createTempFile(TEMP_FILES_PREFIX, TEMP_FILES_SUFIX);

			FileUtils.write(fileToSign, xml, Charset.forName("UTF-8"));

			// - - -

			Document doc = dbf.newDocumentBuilder().parse(fileToSign);

			// Create a DOMSignContext and specify the RSA PrivateKey and
			// location of the resulting XMLSignature's parent element.
			DOMSignContext dsc = new DOMSignContext(pk, doc.getDocumentElement());

			// Create the XMLSignature, but don't sign it yet.
			XMLSignature signature = fac.newXMLSignature(si, null);

			// Marshal, generate, and sign the enveloped signature.
			signature.sign(dsc);

			signed = File.createTempFile(TEMP_FILES_PREFIX, TEMP_FILES_SUFIX);

			// Output the resulting document.
			OutputStream os = new FileOutputStream(signed);

			TransformerFactory tf = TransformerFactory.newInstance();

			Transformer trans = tf.newTransformer();

			trans.transform(new DOMSource(doc), new StreamResult(os));

			// log.debug(fileToSign.getAbsolutePath());

			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(signed));

		} catch (Exception e) {
			throw e;
		} finally {
			if (fileToSign != null)
				fileToSign.delete();
			if (signed != null)
				signed.delete();
		}
	}

	/**
	 * Valida si la firma es valida con respecto al certificado proporcionado,
	 * 
	 * @param signed          cadena de Firma,
	 * @param cert            LLave publica o certificado,
	 * @param recoverOriginal indicar si se devuelve el documento origianl sin
	 *                        firma,
	 * @param outputPath      ruta donde se devuelve el documento original,
	 * @return
	 */
	public static boolean validateSignature(File signed, Certificate cert, boolean recoverOriginal, String outputPath) {

		// Find Signature element.
		// Validate the XMLSignature.
		boolean coreValidity = false;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = dbf.newDocumentBuilder().parse(signed);

			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (nl.getLength() == 0) {
				throw new Exception("Cannot find Signature element");
			}

			// Create a DOMValidateContext and specify a KeySelector
			// and document context.
			DOMValidateContext valContext = new DOMValidateContext(cert.getPublicKey(), nl.item(0));

			// Unmarshal the XMLSignature.
			valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);

			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			XMLSignature signature2 = fac.unmarshalXMLSignature(valContext);

			coreValidity = signature2.validate(valContext);

			// Check core validation status.
			if (coreValidity == false) {
				log.error("Signature failed core validation");
				boolean sv = signature2.getSignatureValue().validate(valContext);
				log.debug("signature validation status: " + sv);
				if (sv == false) {
					// Check the validation status of each Reference.
					Iterator<?> i = signature2.getSignedInfo().getReferences().iterator();
					for (int j = 0; i.hasNext(); j++) {

						boolean refValid = ((Reference) i.next()).validate(valContext);
						log.debug("ref[" + j + "] validity status: " + refValid);
					}
				}
			} else {
				if (recoverOriginal) {
					log.debug("Signature passed core validation");
					Iterator<?> i = signature2.getSignedInfo().getReferences().iterator();
					for (; i.hasNext();) {
						InputStream is = ((Reference) i.next()).getDigestInputStream();

						Document docorig = dbf.newDocumentBuilder().parse(is);

						OutputStream os = new FileOutputStream(outputPath);
						TransformerFactory tf = TransformerFactory.newInstance();
						Transformer trans = tf.newTransformer();
						trans.transform(new DOMSource(docorig), new StreamResult(os));
					}
				}
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return coreValidity;
	}

	/**
	 * Crea una instancia de {@link Certificate} apartir de una cadena base64 del
	 * mismo,
	 * 
	 * @param certB64
	 * @return
	 * @throws CertificateException
	 */
	public static Certificate stringB64toCertificate(String certB64) throws CertificateException {
		Certificate certificate;
		byte[] content = Base64.getDecoder().decode(certB64);
		InputStream is = new ByteArrayInputStream(content);
		CertificateFactory cf = CertificateFactory.getInstance("X.509", Security.getProvider("BC"));
		certificate = cf.generateCertificate(is);
		return certificate;
	}

	/**
	 * Valida si el hash del objeto coincide con el hash mensionado en el envelopt
	 * firmado,
	 * 
	 * @param jsonObject
	 * @param firma
	 * @throws NoSuchAlgorithmException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws HashInSignatureNoMatchException
	 */
	public static void validateHash(Object jsonObject, String firma) throws NoSuchAlgorithmException,
			JsonParseException, JsonMappingException, IOException, HashInSignatureNoMatchException {

		String hashDelBody = SecurityUtilOPE.generateHash(jsonObject);

		EnvelopResponse value = xmlMapper.readValue(Base64.getDecoder().decode(firma), EnvelopResponse.class);

		String hashDelMensaje = value.getHash();

		if (!hashDelBody.equals(hashDelMensaje)) {
			throw new HashInSignatureNoMatchException();
		}

	}

}
