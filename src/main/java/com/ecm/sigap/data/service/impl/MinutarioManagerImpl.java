/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Minutario;
import com.ecm.sigap.data.model.util.RevisorMinutario;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Minutario}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("minutarioService")
public class MinutarioManagerImpl extends ManagerImpl<Minutario> {

	/** Log de Succesos. */
	private static final Logger log = LogManager.getLogger(MinutarioManagerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("minutarioDao")
	@Override
	protected void setDao(EntityDAO<Minutario> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(Minutario item) throws Exception {

		try {

			Integer idMinutario = getNextval("MINUTARIO_SEQ").intValue();

			item.setIdMinutario(idMinutario);

			String folderIdMinutario = createMinutarioFolder(item);

			item.setIdDocumento(folderIdMinutario);

			super.save(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			if (null != item.getIdMinutario()) {
				delete(item);
			}
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#fetch(java.io.Serializable)
	 */
	@Override
	public Minutario fetch(Serializable id) {

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		Minutario minutario = super.fetch(Integer.valueOf(id.toString()));

		String folderId = null;

		if (minutario != null) {
			try {

				String areaFolderId = minutario.getRemitente().getContentId();

				String folderPath = endpoint.getObjectPath(areaFolderId);

				folderPath = folderPath + "/" + environment.getProperty("folderNameMinutarios") + "/"
						+ minutario.getIdMinutario();

				if (endpoint.existeCarpeta(folderPath)) {

					folderId = endpoint.getFolderIdByPath(folderPath);

				} else {

					folderId = createMinutarioFolder(minutario);
				}

				minutario.setContentId(folderId);

			} catch (Exception e) {
				
			}

			Map<String, Object> objectProperties;
			Map<String, Object> objectPropertiesClean;

			for (RevisorMinutario revisor : minutario.getRevisores()) {
				try {
					objectProperties = endpoint.getObjectProperties(revisor.getObjectId());

					objectPropertiesClean = new HashMap<String, Object>();

					for (String key : objectProperties.keySet()) {
						objectPropertiesClean.put(key.replace("cmis:", ""), objectProperties.get(key));
					}

					revisor.setDocumentProperties(objectPropertiesClean);
				} catch (Exception e) {
					log.error(e.getLocalizedMessage());
					
				}
			}

		}

		return minutario;
	}

	/**
	 * Se crea el folder para los documentos del minutario.
	 * 
	 * @param minutario
	 * @return
	 * @throws Exception
	 */
	private String createMinutarioFolder(Minutario minutario) throws Exception {

		log.debug("Creating minutario folder...");

		String areaFolderId = minutario.getRemitente().getContentId();

		log.debug("Area Folder Id :: " + areaFolderId);

		IEndpoint endpoint = EndpointDispatcher.getInstance();
		String areaFolderPath = endpoint.getObjectPath(areaFolderId);

		log.debug("Area Folder Path :: " + areaFolderPath);

		String parentFolderPath = areaFolderPath + "/" + environment.getProperty("folderNameMinutarios");

		log.debug("Parent Path :: " + parentFolderPath);

		String tipoFolder = environment.getProperty("folderTypeMinutarioAnexos");

		log.debug("Folder type para Minutario :: " + tipoFolder);

		String folderName = minutario.getIdMinutario().toString();

		log.debug("Folder Name :: " + folderName);

		if (endpoint.existeCarpeta(parentFolderPath)) {

			String parentFolderId = endpoint.getFolderIdByPath(parentFolderPath);

			log.debug(" Parent Id :: " + parentFolderId);

		} else {

			// ESTA ES LA CARPETA DONDE TODOS LOS MINUTARIOS
			// SE GUARDAN PARA UN AREA.
			log.warn("-----------------------------------------------");

			log.warn("El folder para almacenar los OFICIOS no existe.");

			String folderIdMinutario = endpoint.createFolderIntoId(areaFolderId,
					environment.getProperty("folderTypeMinutarios"), environment.getProperty("folderNameMinutarios"));

			log.debug("Nuevo folder " + folderIdMinutario + " para almacenar Oficios para el area "
					+ minutario.getRemitente().getDescripcion());

			// SET ACL

			Map<String, String> additionalData = new HashMap<>();

			additionalData.put("idArea", minutario.getRemitente().getIdArea().toString());

			endpoint.setACL(folderIdMinutario, environment.getProperty("aclNameFolderMinutariosArea"), additionalData);

			log.warn("-----------------------------------------------");

		}

		String folderIdMinutario = endpoint.createFolder(parentFolderPath, tipoFolder, folderName);

		log.debug("New Folder Id :: " + folderIdMinutario + " para el minutario " + minutario.toString());

		Map<String, String> additionalData = new HashMap<>();

		additionalData.put("idArea", minutario.getRemitente().getIdArea().toString());

		endpoint.setACL(folderIdMinutario, environment.getProperty("aclNameFolderMinutario"), additionalData);

		return folderIdMinutario;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#delete(java.lang.Object)
	 */
	@Override
	public void delete(Minutario item) {

		try {
			// Eliminamo todas las versiones del documento generado para este
			// Minutario
			for (RevisorMinutario revisor : item.getRevisores()) {
				EndpointDispatcher.getInstance().eliminarDocumento(revisor.getObjectId());
			}

			super.delete(item);
		} catch (Exception e) {
			log.error("Ocurrio un error al momento de ejecutar el delete del minutario con identificador "
					+ item.getIdMinutario());
			
		}
	}

}