/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Plantilla;
import com.ecm.sigap.data.model.util.TipoPlantilla;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Plantilla}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Service("plantillaService")
public class PlantillaManagerImpl extends ManagerImpl<Plantilla> {

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("plantillaDao")
	protected void setDao(EntityDAO<Plantilla> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(Plantilla item) throws Exception {

		Version verDoc = Version.MAYOR;
		String descDoc = "Nueva Plantilla";

		File documento = FileUtil.createTempFile2(item.getFileB64(), item.getNombre());
		String nombreArchivo = item.getNombre();
		String folderId = item.getParentId();

		String tipoDoc;

		if (item.getTipo() == TipoPlantilla.INSTITUCIONAL) {

			tipoDoc = environment.getProperty("docTypePlantillaInstitucional");

		} else if (item.getTipo() == TipoPlantilla.POR_AREA) {

			tipoDoc = environment.getProperty("docTypePlantilla");

		} else {

			throw new Exception("BAD TYPE!");

		}

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		String objectId = endpoint.saveDocumentoIntoId(folderId, nombreArchivo, tipoDoc, verDoc, descDoc, documento);

		item.setObjectId(objectId);

		super.save(item);
	}

}