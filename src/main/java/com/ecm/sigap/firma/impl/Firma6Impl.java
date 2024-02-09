/**
 * 
 */
package com.ecm.sigap.firma.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ecm.cmisIntegracion.ws.util.JerseyConsumer;
import com.ecm.sigap.firma.Firma6Service;

/**
 * @author Alfredo Morales
 *
 */
@Component("firma6Service")
public class Firma6Impl extends JerseyConsumer implements Firma6Service {

	/** */
	@Autowired
	private Environment environment;

	@Override
	public Map<String, List<?>> iniciarFirma(List<Map<String, Object>> documentos)
			throws ClientProtocolException, IOException {

		String postUrl = environment.getProperty("urlSignatureService") + "/seguridad/firma6/iniciarFirma";

		JSONObject json = new JSONObject();

		json.put("documentos", documentos);

		// Map<String, Object> response = doPost(postUrl, json);

		// TODO CARGAR DOCUMENTO Y GENERAR ID POR CADA CARGA

		Map<String, List<?>> response = new HashMap<String, List<?>>();

		List<Integer> ids = new ArrayList<Integer>();
		ids.add(345);
		ids.add(678);
		ids.add(901);

		response.put("ids", ids);

		return response;

	}

	// + + + + + + + + + + + + + + + + + + + + + + + + + + +
	// + + + + + + + + + + + + + + + + + + + + + + + + + + +

}
