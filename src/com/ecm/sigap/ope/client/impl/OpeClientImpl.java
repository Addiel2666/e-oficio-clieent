/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.client.impl;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChain.Scope;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.ecm.sigap.ope.client.OpeClient;
import com.ecm.sigap.ope.client.exception.AlreadyRegisteredException;
import com.ecm.sigap.ope.client.exception.HashInSignatureNoMatchException;
import com.ecm.sigap.ope.client.exception.UnknownURLException;
import com.ecm.sigap.ope.model.OpeHeaders;
import com.ecm.sigap.ope.model.RequestConfirmarSubscripcion;
import com.ecm.sigap.ope.model.RequestRecibirSubscripcion;

import com.ecm.sigap.ope.model.RequestSincronizacionCompleta;
import com.ecm.sigap.ope.model.RequestVersionCatalogo;
import com.ecm.sigap.ope.model.ResponseAsuntoTramite;
import com.ecm.sigap.ope.model.ResponseConfirmarSubscripcion;
import com.ecm.sigap.ope.model.ResponseRecibirSubscripcion;
import com.ecm.sigap.ope.model.ResponseSincronizacionCompleta;
import com.ecm.sigap.ope.model.ResponseVersionCatalogo;
import com.ecm.sigap.ope.security.SecurityUtilOPE;
import com.google.gson.Gson;

/**
 * Implementacion de los endpoints de la Oficina Postal Electronica,
 * 
 * @author Alfredo Morales
 *
 */
public final class OpeClientImpl implements OpeClient {

	/**
	 * Devuelve una instancia con los metodos para interoperar,
	 * 
	 * @param urlDestino      URI de la instancia con la que se quiere interoperar,
	 * @param nombreRemitente nombre corto o identificador de la instancia actual,
	 *                        debe ser igual siempre para identificar la instancia,
	 * @return
	 */
	public static OpeClient instanciate(String urlDestino, String nombreRemitente) {
		return new OpeClientImpl(urlDestino, nombreRemitente);
	}

	/** URL a la que se esta conectando, */
	private String URL_CORE;
	/** identificador de la instancia actual, */
	private String KEY_NAME;
	/** see {@link HttpClientBuilder} */
	private HttpClientBuilder builder;

	/**
	 * Default Construtctor,
	 * 
	 * @param url     URL a la que se esta conectando,
	 * @param keyName identificador de la instancia actual,
	 */
	private OpeClientImpl(String url, String keyName) {
		super();

		URL_CORE = url;
		KEY_NAME = keyName;

		builder = HttpClients.custom();

		// Add HttpRequestInterceptor
		builder.addResponseInterceptorLast(new HttpResponseInterceptor() {

			@Override
			public void process(HttpResponse response, EntityDetails entity, HttpContext context)
					throws HttpException, IOException {
			}

		});

		builder.addRequestInterceptorFirst(new HttpRequestInterceptor() {
			@Override
			public void process(final HttpRequest request, final EntityDetails entity, final HttpContext context)
					throws HttpException, IOException {
			}
		});

		// Execution Interceptors
		builder.addExecInterceptorAfter(ChainElement.PROTOCOL.name(), "AAA", new ExecChainHandler() {
			@Override
			public ClassicHttpResponse execute(ClassicHttpRequest request, Scope scope, ExecChain chain)
					throws IOException, HttpException {
				return chain.proceed(request, scope);
			}
		});

		builder.addExecInterceptorBefore("AAA", "BBB", new ExecChainHandler() {
			@Override
			public ClassicHttpResponse execute(ClassicHttpRequest request, Scope scope, ExecChain chain)
					throws IOException, HttpException {
				// SE AGREGAR FIRMA DEL BODY ENVIADO

				request.addHeader(OpeHeaders.HEADER_PUBLIC_KEY_NAME, KEY_NAME);

				String contenido = EntityUtils.toString(request.getEntity());

				try {
					String hash = SecurityUtilOPE.generateHash(contenido);

					String firmaBody = SecurityUtilOPE.createSignature(hash);

					request.addHeader(OpeHeaders.HEADER_FIRMA_BODY, firmaBody);

				} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | KeyStoreException
						| CertificateException | UnrecoverableEntryException | SAXException
						| ParserConfigurationException | MarshalException | XMLSignatureException
						| TransformerException e) {
					e.printStackTrace();
				}

				return chain.proceed(request, scope);
			}

		});

		builder.addExecInterceptorAfter("AAA", "CCC", new ExecChainHandler() {
			@Override
			public ClassicHttpResponse execute(ClassicHttpRequest request, Scope scope, ExecChain chain)
					throws IOException, HttpException {
				return chain.proceed(request, scope);
			}
		});

		// Add HttpResponseInterceptor
		builder.addResponseInterceptorLast(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, EntityDetails entity, HttpContext context)
					throws HttpException, IOException {
			}
		});

		builder.addResponseInterceptorFirst(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, EntityDetails entity, HttpContext context)
					throws HttpException, IOException {
			}
		});

	}

	@Override
	public ResponseRecibirSubscripcion solicitarSubscripcion(String nombre, String nombreCorte, String url)
			throws Exception {

		RequestRecibirSubscripcion rrs = new RequestRecibirSubscripcion();

		rrs.setNombre(nombre);
		rrs.setNombreCorto(nombreCorte);
		rrs.setUrl(url);

		JSONObject jsonObject = new JSONObject(rrs);

		JSONObject g = postRequest(jsonObject, "/ope/recibirSubscripcion");

		ResponseRecibirSubscripcion rrs_ = new Gson().fromJson(g.getJSONObject("body").toString(),
				ResponseRecibirSubscripcion.class);

		SecurityUtilOPE.validateHash(rrs_, g.getString("firmaRetorno"));

		return rrs_;
	}

	@Override
	public ResponseSincronizacionCompleta solicitarSincronizacionCompleta(String nombre, String nombreCorte, String url)
			throws Exception {
		// TODO Auto-generated method stub
		RequestSincronizacionCompleta rsc = new RequestSincronizacionCompleta();

		rsc.setNombre(nombre);
		rsc.setNombreCorto(nombreCorte);
		rsc.setUrl(url);

		JSONObject jsonObject = new JSONObject();

		JSONObject g = postRequest(jsonObject, "/ope/sincronizacion/completa");

		ResponseSincronizacionCompleta rsc_ = new Gson().fromJson(g.getJSONObject("body").toString(),
				ResponseSincronizacionCompleta.class);

		SecurityUtilOPE.validateHash(rsc_, g.getString("firmaRetorno"));

		return rsc_;
	}

	/**
	 * @throws Exception
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	private JSONObject postRequest(JSONObject body, String endpoint) throws Exception {

		final HttpPost httpPost = new HttpPost(URL_CORE + endpoint);

		final StringEntity entity = new StringEntity(body.toString());
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		try (CloseableHttpClient client = builder.build();
				CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost)) {

			final int statusCode = response.getCode();

			if (statusCode == HttpStatus.SC_OK) {

				JSONObject jsonObject = new JSONObject();

				String h = EntityUtils.toString(response.getEntity(), "UTF-8");
				jsonObject.put("body", new JSONObject(h));

				Header firmaRetorno = response.getHeader(OpeHeaders.HEADER_FIRMA_BODY);
				jsonObject.put("firmaRetorno", firmaRetorno.getValue());

				return jsonObject;

			} else if (statusCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION) {
				throw new HashInSignatureNoMatchException();
			} else if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
				throw new UnknownURLException();
			} else if (statusCode == HttpStatus.SC_ALREADY_REPORTED) {
				throw new AlreadyRegisteredException();
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public ResponseConfirmarSubscripcion confirmarSubscripcion(String id, String nombreCorte, String url)
			throws Exception {

		RequestConfirmarSubscripcion rcs = new RequestConfirmarSubscripcion();
		rcs.setId(id);
		rcs.setNombreCorto(nombreCorte);
		rcs.setUrl(url);

		JSONObject jsonObject = new JSONObject(rcs);

		JSONObject g = postRequest(jsonObject, "/ope/confirmarSubscripcion");

		ResponseConfirmarSubscripcion rcs_ = new Gson().fromJson(g.getJSONObject("body").toString(),
				ResponseConfirmarSubscripcion.class);

		SecurityUtilOPE.validateHash(rcs_, g.getString("firmaRetorno"));

		return rcs_;

	}

	@Override
	public ResponseVersionCatalogo obtenerVersionCatalogo(String url) throws Exception {

		RequestVersionCatalogo rvc = new RequestVersionCatalogo();
		rvc.setUrl(url);

		JSONObject jsonObject = new JSONObject(rvc);

		JSONObject g = postRequest(jsonObject, "/ope/obtenerVersionCatalogo");

		ResponseVersionCatalogo rvc_ = new Gson().fromJson(g.getJSONObject("body").toString(),
				ResponseVersionCatalogo.class);

		SecurityUtilOPE.validateHash(rvc_, g.getString("firmaRetorno"));

		return rvc_;
		
	}
	
	@Override
	public ResponseAsuntoTramite envioTramite(String url) throws Exception {

		ResponseAsuntoTramite rvc = new ResponseAsuntoTramite();
		rvc.setUrl(url);

		JSONObject jsonObject = new JSONObject(rvc);

		JSONObject g = postRequest(jsonObject, "/ope/envioTramite");

		String rvc_ = new Gson().fromJson(g.getJSONObject("body").toString(),
				String.class);
		rvc.setVersionAsunto(rvc_);

		SecurityUtilOPE.validateHash(rvc_, g.getString("firmaRetorno"));

		return rvc;
		
	}

}
