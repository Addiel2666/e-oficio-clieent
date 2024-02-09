/*
 * Copyright (c) 2014 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;

import com.ecm.sigap.data.exception.TSPGeneralException;
import com.ecm.sigap.util.CertificateUtility;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;

/**
 * Clase utilitaria para el manejo de las peticiones al TSA.
 * 
 * La logica de la clase se baso en las deficiones del estandar para el envio de
 * las peticiones y recepcion de respuetas a un Time Stamping Authority (TSA)
 * 
 * @see <a href="https://www.ietf.org/rfc/rfc3161.txt">RFC 3161</a>
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
public class TspUtility {

	/** Logger de la clase */
	private final static Logger logger = LogManager.getLogger(TspUtility.class.getName());

	private static final String TSP_SERVER_FILENAME = "tspserver";

	// Formato de la fecha del sistema
	private static final SimpleDateFormat SYSTEM_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a");

	private static TspUtility instance = null;

	/**
	 * Obtiene una instancia de la clase
	 * 
	 * @return Instancia
	 */
	public static synchronized TspUtility getInstance(String provider) throws TSPGeneralException {

		logger.debug("::: Ejecutando el metodo getInstance");

		if (null != instance) {

			return instance;

		} else {

			return new TspUtility(provider);
		}
	}

	/**
	 * Obtiene una instancia de la clase
	 * 
	 * @return Instancia
	 */
	public static synchronized TspUtility getInstance(byte[] fingerPrint, BigInteger nonce) throws TSPGeneralException {

		logger.debug("::: Ejecutando el metodo getInstance");

		if (null != instance) {

			return instance;

		} else {

			return new TspUtility(fingerPrint, nonce);
		}
	}

	/**
	 * Obtiene una instancia de la clase
	 * 
	 * @return Instancia
	 */
	public static synchronized TspUtility getInstance(byte[] fingerPrint, BigInteger nonce, String provider)
			throws TSPGeneralException {

		logger.debug("::: Ejecutando el metodo getInstance");

		if (null != instance) {

			return instance;

		} else {

			return new TspUtility(fingerPrint, nonce, provider);
		}
	}

	// Servidor TSP de la aplicacion
	private String tspServerUrl;

	// Usuario de autenticacion del servidor
	private String tspUser;

	// Password del Usuario
	private String tspUserPassword;

	// Indicador si el servidor necesita autenticacion
	private boolean tspIsServerAutentica;

	// Fecha del servidor TSP
	private Date fechaTsp;

	// Cadena con la Informaion del Estampado de Tiempo
	private String stamp;

	/** Objeto del tipo {@link TimeStampResponse} de la peticion al TSP **/
	private TimeStampResponse response;

	private boolean requestGeneratorCertReq;

	@Deprecated
	private TspUtility() {

		logger.debug("::: Ejecutando el constructor por defecto de la clase");
		init(null);
	}

	private String tspResquestFingerPrint;

	private TspUtility(String provider) {

		logger.debug("::: Ejecutando el constructor por defecto de la clase");
		init(provider);
	}

	@Deprecated
	private TspUtility(byte[] fingerPrint, BigInteger nonce) throws TSPGeneralException {
		throw new TSPGeneralException("Proveedor TSP nulo");
	}

	/**
	 * Constructor por defecto de la clase
	 */
	private TspUtility(byte[] fingerPrint, BigInteger nonce, String provider) throws TSPGeneralException {

		logger.debug("::: Ejecutando el constructor con parametros de la clase");

		init(provider);

		OutputStream out = null;
		HttpURLConnection con = null;
		InputStream in = null;
		TimeStampResp resp = null;
		ASN1InputStream asn1InputStream = null;

		try {

			TimeStampRequestGenerator timeStampRequestGenerator = new TimeStampRequestGenerator();

			timeStampRequestGenerator.setCertReq(requestGeneratorCertReq);

			TimeStampRequest timeStampRequest = timeStampRequestGenerator.generate(TSPAlgorithms.SHA1, fingerPrint,
					nonce);

			byte request[] = timeStampRequest.getEncoded();

			URL url = new URL(tspServerUrl);

			logger.debug("::: Estableciendo la conexion con el servidor TSP");
			con = (HttpURLConnection) url.openConnection();

			// Si el servidor pide autenticaci�n, mandar solicitud con
			// credenciales.
			// No mandar en con HTTP, siempre con HTTPS!!
			if (tspIsServerAutentica) {

				logger.debug("::: Asignando los valores para la autenticacion");

				String userCredentials = tspUser + ":" + tspUserPassword;
				String basicAuth = "Basic " + new String(CertificateUtility.encodeBASE64(userCredentials.getBytes()));
				con.setRequestProperty("Authorization", basicAuth);
			}

			// Necesario para subir cuerpo de mensaje a servidor.
			con.setDoOutput(true);
			// Necesario para descargar cuerpo de respuesta de servidor.
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "application/timestamp-query");
			con.setRequestProperty("Content-length", String.valueOf(request.length));
			out = con.getOutputStream();
			out.write(request);
			out.flush();

			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {

				throw new TSPGeneralException(
						"Received HTTP error: " + con.getResponseCode() + " - " + con.getResponseMessage());
			} else {

				logger.debug("::: Response Code: ".concat(Integer.toString(con.getResponseCode())));
			}

			in = con.getInputStream();
			asn1InputStream = new ASN1InputStream(in);
			resp = TimeStampResp.getInstance(asn1InputStream.readObject());
			TimeStampResponse response = new TimeStampResponse(resp);

			response.validate(timeStampRequest);

			if (response.getFailInfo() != null) {

				switch (response.getFailInfo().intValue()) {
				case 0: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "Unrecognized or unsupported Algorithm Identifier");
					throw new TSPGeneralException("Unrecognized or unsupported Algorithm Identifier");
				}

				case 2: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "Transaction not permitted or supported");
					throw new TSPGeneralException("Transaction not permitted or supported");
				}

				case 5: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "The data submitted has the wrong format");
					throw new TSPGeneralException("The data submitted has the wrong format");
				}

				case 14: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "The TSA�s time source is not available");
					throw new TSPGeneralException("The TSA�s time source is not available");
				}

				case 15: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "The requested TSA policy is not supported by the TSA");
					throw new TSPGeneralException("The requested TSA policy is not supported by the TSA");
				}
				case 16: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "The requested extension is not supported by the TSA");
					throw new TSPGeneralException("The requested extension is not supported by the TSA");
				}

				case 17: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "The additional information requested could not be understood or is not available");
					throw new TSPGeneralException(
							"The additional information requested could not be understood or is not available");
				}

				case 25: {
					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "The request cannot be handled due to system failure");
					throw new TSPGeneralException("The request cannot be handled due to system failure");
				}
				default: {

					logger.error("::: Se produjo un error al momento de obtener la "
							+ "informacion del servidor TSP, con la siguiente descripcion: "
							+ "Fail Info int value is: " + response.getFailInfo().intValue());
					if (PKIFailureInfo.unacceptedPolicy == response.getFailInfo().intValue()) {
						logger.error("unaccepted policy");
					}
					throw new TSPGeneralException("Fail Info int value is: " + response.getFailInfo().intValue());
				}

				}
			}

			logger.debug("::: El codigo del estatus de la respuesta es: '" + response.getStatus()
					+ "' y la descripcion es '" + response.getStatusString() + "'");
			/**
			 * When the status contains the value zero or one, a TimeStampToken MUST be
			 * present. When status contains a value other than zero or one, a
			 * TimeStampToken MUST NOT be present. One of the following values MUST be
			 * contained in status:
			 * 
			 * PKIStatus ::= INTEGER {
			 * 
			 * granted (0), -- when the PKIStatus contains the value zero a TimeStampToken,
			 * as requested, is present.
			 * 
			 * grantedWithMods (1), -- when the PKIStatus contains the value one a
			 * TimeStampToken, with modifications, is present.
			 * 
			 * rejection (2),
			 * 
			 * waiting (3),
			 * 
			 * revocationWarning (4),
			 */
			if (response.getStatus() == 0 || response.getStatus() == 1) {

				String fecha = null;

				// Asignamos la fecha para usarla posteriormente
				fechaTsp = response.getTimeStampToken().getTimeStampInfo().getGenTime();

				/**
				 * The Java API documentation clearly states :
				 * 
				 * <t>Date formats are not synchronized. It is recommended to create separate
				 * format instances for each thread. If multiple threads access a format
				 * concurrently, it must be synchronized externally." </t>
				 */
				synchronized (SYSTEM_DATE_FORMAT) {
					fecha = SYSTEM_DATE_FORMAT.format(fechaTsp);
				}

				stamp = "Certificado=" + response.getTimeStampToken().getTimeStampInfo().getTsa().toString()
						+ "; Fecha=" + fecha + "; Policy=" + response.getTimeStampToken().getTimeStampInfo().getPolicy()
						+ "; SerialNumber=" + response.getTimeStampToken().getTimeStampInfo().getSerialNumber();

				this.response = response;

				if (logger.isDebugEnabled()) {

					logger.debug("Timestamp: " + response.getTimeStampToken().getTimeStampInfo().getGenTime());
					logger.debug("TSA: " + response.getTimeStampToken().getTimeStampInfo().getTsa());
					logger.debug("Serial number: " + response.getTimeStampToken().getTimeStampInfo().getSerialNumber());
					logger.debug("Policy: " + response.getTimeStampToken().getTimeStampInfo().getPolicy());
					logger.debug("Encoded: " + CertificateUtility.encodeBASE64(response.getEncoded()));
					logger.debug(
							"Digest: " + CertificateUtility.encodeBASE64(response.getTimeStampToken().getEncoded()));
					logger.debug("Digest Response: " + CertificateUtility
							.encodeBASE64(response.getTimeStampToken().getTimeStampInfo().getMessageImprintDigest()));
					logger.debug("Digest Encoded: " + CertificateUtility
							.encodeBASE64(response.getTimeStampToken().getTimeStampInfo().getEncoded()));
					logger.debug("::: Stamp: " + stamp);
					logger.debug("::: Tsp Time: " + fecha);
				}
			} else {

				throw new TSPGeneralException(
						"La respuesta recibida por el servidor de tiempo no contiene la informacion necesaria para generar la respuesta");
			}
		} catch (IOException e) {

			logger.error("::: Error - IOException: Ocurrio un error al momento de obtener la "
					+ "informacion del TSP, con la siguiente descripcion: " + e.getMessage());
			
			throw new TSPGeneralException("Error al momento de obtener la "
					+ "informacion del TSP, con la siguiente descripcion: " + e.getMessage());

		} catch (TSPException e) {

			logger.error("::: Error - TSPException: Ocurrio un error al momento de obtener la "
					+ "informacion del TSP, con la siguiente descripcion: " + e.getMessage());
			

			throw new TSPGeneralException("Error al momento de obtener la "
					+ "informacion del TSP, con la siguiente descripcion: " + e.getMessage());

		}
//		catch (CertificateUtilityException e) {
//
//			logger.error("::: Error - CertificateUtilityException: Ocurrio un error al momento de obtener la "
//					+ "informacion del TSP, con la siguiente descripcion: "
//					+ e.getMessage());
//			
//			throw new TSPGeneralException("Error al momento de obtener la "
//					+ "informacion del TSP, con la siguiente descripcion: "
//					+ e.getMessage());
//
//		} 
		finally {

			if (null != in) {

				try {

					in.close();
					asn1InputStream.close();
					out.close();

				} catch (IOException e) {

					logger.warn("Error al momento de cerrar los objetos usados en el metodo");
					
				}
			}

		}

	}

	/**
	 * Obtiene el objeto del tipo TimeStampResponse de la peticion al TSP
	 * 
	 * @return Objeto del tipo TimeStampResponse de la peticion al TSP
	 */
	public TimeStampResponse getResponse() {

		return response;
	}

	/**
	 * Obtiene la Cadena con la Informaion del Estampado de Tiempo
	 * 
	 * @return Cadena con la Informaion del Estampado de Tiempo
	 */
	public String getStamp() {

		return stamp;
	}

	/**
	 * Obtiene la fecha del TSP
	 * 
	 * @return Fecha del TSP
	 */
	public Date getTime() {

		return fechaTsp;
	}

	/**
	 * Obtiene el arreglo de bytes que representan el objeto TimeStampToken
	 * {@link org.bouncycastle.tsp.TimeStampToken}
	 * 
	 * @return rreglo de bytes que representan el objeto TimeStampToken
	 * @throws TSPGeneralException Error al momento de ejecutar el metodo
	 */
	public byte[] getTimeStampToken() throws TSPGeneralException {

		try {

			return response.getTimeStampToken().getEncoded();

		} catch (IOException e) {

			
			throw new TSPGeneralException(
					"Error al momento de obtener la fecha y hora actual con la siguiente descripcion : "
							+ e.getMessage());
		}
	}

	/**
	 * Obtiene un objeto de tipo {@link TSAClient}
	 * 
	 * @return Objeto del tipo TSAClient
	 */
	public TSAClient getTsaClient() {

		logger.debug("::: Ejecutando el metodo getTsaClient");

		TSAClientBouncyCastle tsc = null;

		if (tspIsServerAutentica) {

			logger.debug("::: El servidor requiere autenticacion para su conexion");
			tsc = new TSAClientBouncyCastle(tspServerUrl, tspUser, tspUserPassword, 4192,
					CryptographicAlgorithmName.SHA1.getAlgorithm());
		} else {

			logger.debug("::: El servidor no requiere autenticacion y se envian los datos nulos");
			tsc = new TSAClientBouncyCastle(tspServerUrl, null, null, 4192,
					CryptographicAlgorithmName.SHA1.getAlgorithm());
		}

		return tsc;
	}

	private void init(String provider) {
		logger.debug("::: Ejecutando el metodo init() de la clase");
		// Leemos los valores del servidor TSP configurado
		Locale defaultLocale = Locale.getDefault();
		ResourceBundle rb = ResourceBundle.getBundle(TSP_SERVER_FILENAME, defaultLocale);

		provider = provider.toLowerCase();
		tspServerUrl = rb.getString(provider + ".url");
		tspIsServerAutentica = "Y".equals(rb.getString(provider + ".autenticacion")) ? true : false;
		tspUser = rb.getString(provider + ".user");
		tspUserPassword = rb.getString(provider + ".password");
		tspResquestFingerPrint = rb.getString(provider + ".resquestFingetPrint");

		requestGeneratorCertReq = Boolean.parseBoolean(rb.getString(provider + ".requestgeneratorcertreq"));

		logger.debug("::: Servidor TSP configurado: [URL=" + tspServerUrl + "], [Usuario=" + tspUser + "], [Password="
				+ tspUserPassword + "], [Autentica=" + tspIsServerAutentica + "]");
	}

	public static void main(String[] args) {
		// Llamamos al TSP para obtener la informacion de la fecha
		try {
//			ConfigurationTypeConfigurer conf = new PropertiesSignatureConfigurer();
//			conf.setDefaults();
			TspUtility tspInfo = TspUtility.getInstance(new byte[20], BigInteger.valueOf(100), "ecm_solutions");
			// System.out.println(tspInfo.getStamp());
		} catch (TSPGeneralException e) {
			// TODO Auto-generated catch block
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	}

	// /**
	// * Crea un archivo temporal para enviar en el request del TSA
	// *
	// * @param text
	// * Texto que se va a agregar al archivo
	// * @return Arreglo de bytes que representan el archivo temporal
	// * @throws IOException
	// * Error al momento de crear el archivo
	// */
	// private byte[] createFileToSendAsFingerPrint(String text)
	// throws IOException {
	//
	// logger.debug("::: Creando un archivo temporal para enviar en el request del
	// TSA");
	//
	// return FileBytes.getFileBytes(FileUtil.createTempFile(text));
	// }

}
