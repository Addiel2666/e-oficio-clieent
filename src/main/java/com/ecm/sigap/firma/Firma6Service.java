/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.firma;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

/**
 * @author alfredo morales
 * @version 1.0
 *
 */
public interface Firma6Service {

	/**
	 * 
	 * @param documentos
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Map<String, List<?>> iniciarFirma(List<Map<String, Object>> documentos)
			throws ClientProtocolException, IOException;

}
