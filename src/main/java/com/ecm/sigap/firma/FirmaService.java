/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.firma;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.http.client.ClientProtocolException;

import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.TipoFirma;

/**
 * @author alfredo morales
 * @version 1.0
 *
 */
public interface FirmaService {

	/**
	 * Se inicia el proceso de firma de un documento subiendolo al WS de firma
	 * digital y obteniendo su identificador.
	 * 
	 * @param fileB64
	 * @param fileName
	 * @param tipoFirma
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String, Object> uploadFile(String fileB64, String fileName, TipoFirma tipoFirma,
			SignContentType signContentType, String objectId) throws ClientProtocolException, IOException;

	/**
	 * Generar Hash del archivo cargado para firma
	 * 
	 * @param id
	 * @param email
	 * @param certB64
	 * @param tipoFirma
	 * @param signContentType
	 * @param algoritmoFirma
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String, Object> startSign(Integer id, String email, String certB64, TipoFirma tipoFirma,
			SignContentType signContentType, String algoritmoFirma, Integer coordenadax, Integer coordenaday, Integer onpage ,String firmaB64IMG, String nivelUI, String cargo) throws ClientProtocolException, IOException;

	/**
	 * Consulta con el servidor si la firma proporcionada es valida.
	 * 
	 * @param id
	 * @param email
	 * @param certB64
	 * @param tipoFirma
	 * @param firmaHex
	 * @param algoritmoFirma
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws DecoderException
	 */
	public Map<String, Object> validateSign(Integer id, String email, String certB64, String firmaB64,
			TipoFirma tipoFirma, SignContentType signContentType, String algoritmoFirma)
			throws ClientProtocolException, IOException, DecoderException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 */
	public Map<String, Object> getDocumentoFirmado(Integer id) throws UnsupportedOperationException, IOException;

	/**
	 * Obtiene la evidencia de firma, si es un archivo versionable se devuelve el
	 * PDF/A, sino se devuelve un PKCS7.
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public Map<String, Object> getFirma(Long idDocumento, TipoFirma tipoFirma)
			throws UnsupportedOperationException, IOException;

	/**
	 * 
	 * Obtener timestamp.
	 * 
	 * @param data
	 * @param tipo
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public Map<String, Object> getTime(String data, String tipo) throws UnsupportedOperationException, IOException;

	/**
	 * Obtiene la representacion impresa
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public Map<String, Object> getRepresentacionImpresaPlantilla(Long idDocumento, TipoFirma tipoFirma, String tipoConf,
			String evidence, String signedData) throws UnsupportedOperationException, IOException;
	
	/**
	 * Actualizad el r_object_id una vez terminado el proceso de versionado en el repositorio
	 *
	 * @return
	 * @param iddocumento
	 * @param idObjeto
	 *
	 */
	public Map<String, Object> setRObjectOnFirma(Integer idDocumento, String rObject ) throws UnsupportedOperationException, IOException;
		

}
