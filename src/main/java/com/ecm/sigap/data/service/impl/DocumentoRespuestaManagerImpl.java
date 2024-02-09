/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.AsuntoConsulta;
import com.ecm.sigap.data.model.AsuntoConsultaEspecial;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.RespuestaConsulta;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.service.ManagerImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Manejador en base de datos de objetos {@link DocumentoRespuesta}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("documentoRespuestaService")
public class DocumentoRespuestaManagerImpl extends ManagerImpl<DocumentoRespuesta> {

	/** */
	private static final Logger log = LogManager.getLogger(DocumentoRespuestaManagerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#fetch(java.io.Serializable)
	 */
	@Override
	public DocumentoRespuesta fetch(Serializable id) {
		DocumentoRespuesta item = super.fetch(id);

		if (item.getRespuestaConsulta() != null) {
			item.setAsuntoConsulta(getAsuntoConsulta(item.getRespuestaConsulta()));
		}

		getRepoInfo(item);

		return item;

	}

	@Autowired
	private AsuntoConsultaEspecialManagerImpl acmi;

	/**
	 * 
	 * @param respuestaConsulta
	 * @return
	 */
	private AsuntoConsultaEspecial getAsuntoConsulta(RespuestaConsulta respuestaConsulta) {
		return acmi.fetch(respuestaConsulta.getIdAsunto());
	}

	/** Interfaz a base de datos. */
	@Autowired
	@Qualifier("documentoRespuestaDao")
	protected void setDao(EntityDAO<DocumentoRespuesta> dao) {
		super.setDao(dao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#delete(java.lang.Object)
	 */
	@Override
	public void delete(DocumentoRespuesta item) {

		super.delete(item);

		try {
			EndpointDispatcher.getInstance().eliminarDocumento(item.getObjectId());
		} catch (JsonParseException e) {
			log.error(e.getLocalizedMessage());
			
		} catch (JsonMappingException e) {
			log.error(e.getLocalizedMessage());
			
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(DocumentoRespuesta item) throws Exception {

		super.save(item);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List, org.hibernate.criterion.ProjectionList, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions, //
			List<Order> orders, ProjectionList projections, //
			Integer fetchSize, Integer firstResult) {

		List<?> search = super.search(restrictions, orders, projections, fetchSize, firstResult);

		if (search != null && !search.isEmpty() && search.get(0) instanceof DocumentoRespuesta)
			for (DocumentoRespuesta dr : (List<DocumentoRespuesta>) search) {
				if (dr.getRespuestaConsulta() != null)
					dr.setAsuntoConsulta(getAsuntoConsulta(dr.getRespuestaConsulta()));
			}

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoRespuesta>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions) {
		List<?> search = super.search(restrictions);

		if (search != null && !search.isEmpty() && search.get(0) instanceof DocumentoRespuesta)
			for (DocumentoRespuesta dr : (List<DocumentoRespuesta>) search) {
				if (dr.getRespuestaConsulta() != null)
					dr.setAsuntoConsulta(getAsuntoConsulta(dr.getRespuestaConsulta()));
			}

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoRespuesta>) search);

		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders) {
		List<?> search = super.search(restrictions, orders);

		if (search != null && !search.isEmpty() && search.get(0) instanceof DocumentoRespuesta)
			for (DocumentoRespuesta dr : (List<DocumentoRespuesta>) search) {
				if (dr.getRespuestaConsulta() != null)
					dr.setAsuntoConsulta(getAsuntoConsulta(dr.getRespuestaConsulta()));
			}

		if (search != null && !search.isEmpty() && search.get(0) instanceof Documento)
			completeWithRepoInfo((List<DocumentoRespuesta>) search);

		return search;
	}

}