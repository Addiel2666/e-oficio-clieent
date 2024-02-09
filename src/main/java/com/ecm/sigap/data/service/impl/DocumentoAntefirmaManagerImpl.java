/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Ciudadano;
import com.ecm.sigap.data.model.DocumentoAntefirmaAsunto;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.Funcionario;
import com.ecm.sigap.data.model.RepresentanteLegal;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.Documento;
import com.ecm.sigap.data.model.util.TipoDestinatario;
import com.ecm.sigap.data.service.EntityManager;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link DocumentoAntefirmaAsunto}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Service("documentoAntefirmaAsuntoService")
public class DocumentoAntefirmaManagerImpl extends ManagerImpl<DocumentoAntefirmaAsunto> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("documentoAntefirmaAsuntoDao")
	@Override
	protected void setDao(EntityDAO<DocumentoAntefirmaAsunto> dao) {
		super.setDao(dao);
	}

	/** Manejador para el tipo {@link com.ecm.sigap.data.model.Ciudadano} */
	@Autowired
	@Qualifier("ciudadanoService")
	protected EntityManager<Ciudadano> mngrCiudadano;

	/** Manejador para el tipo {@link com.ecm.sigap.data.model.Usuario} */
	@Autowired
	@Qualifier("usuarioService")
	protected EntityManager<Usuario> mngrUsuario;

	/**
	 * Manejador para el tipo
	 * {@link com.ecm.sigap.data.model.RepresentanteLegal}
	 */
	@Autowired
	@Qualifier("representanteLegalService")
	protected EntityManager<RepresentanteLegal> mngrRepresentanteLegal;

	/** Manejador para el tipo {@link com.ecm.sigap.data.model.Fondo} */
	@Autowired
	@Qualifier("funcionarioService")
	protected EntityManager<Funcionario> mngrFuncionario;
	
	/** Manejador para el tipo {@link com.ecm.sigap.data.model.DocumentosAsunto} */
	@Autowired
	@Qualifier("documentoAsuntoService")
	protected EntityManager<DocumentoAsunto> mngrDocsAsunto;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#fetch(java.io.Serializable)
	 */
	@Override
	public DocumentoAntefirmaAsunto fetch(Serializable id) {
		DocumentoAntefirmaAsunto fetch = super.fetch(id);
		if (null != fetch) {
			completeItem(fetch);
		}
		return fetch;
	}

	/**
	 * Obtiene informacion adicional del repositorio.
	 * 
	 * @param fetch
	 */
	private void completeItem(DocumentoAntefirmaAsunto fetch) {

		// info del repo
		Documento doc_tmp = new DocumentoAsunto();
		doc_tmp.setObjectId(fetch.getDocumentoAntefirmaKey().getObjectId());
		getRepoInfo(doc_tmp);
		
		fetch.setObjectName(doc_tmp.getObjectName());
		
		if("A".equals(fetch.getDocumentoAntefirmaKey().getTipo())) {
			DocumentoAsunto doc_asunto = new DocumentoAsunto();
			doc_asunto.setIdAsunto(fetch.getDocumentoAntefirmaKey().getId());
			doc_asunto.setObjectId(fetch.getDocumentoAntefirmaKey().getObjectId());
			DocumentoAsunto docMarca_tmp = mngrDocsAsunto.fetch(doc_asunto);
			
			if(docMarca_tmp != null)
				fetch.setFechaMarca(docMarca_tmp.getFechaMarca());
		}

		String idFirmante = fetch.getDocumentoAntefirmaKey().getFirmante();

		String nombres = "";
		String paterno = "";
		String materno = "";

		// nombre del firmante
		if (fetch.getDocumentoAntefirmaKey().getTipoFirmante() == TipoDestinatario.CIUDADANO.getStatus()) {

			Ciudadano ciudadano = mngrCiudadano.fetch(Integer.valueOf(idFirmante));

			nombres = ciudadano.getNombres();
			paterno = ciudadano.getPaterno();
			materno = ciudadano.getMaterno();

		} else if (fetch.getDocumentoAntefirmaKey().getTipoFirmante() == TipoDestinatario.FUNCIONARIO_EXTERNO
				.getStatus()) {

			Funcionario funcionario = mngrFuncionario.fetch(idFirmante);

			nombres = funcionario.getNombres();
			paterno = funcionario.getPaterno();
			materno = funcionario.getMaterno();

		} else if (fetch.getDocumentoAntefirmaKey().getTipoFirmante() == TipoDestinatario.FUNCIONARIO_INTERNO
				.getStatus()) {

			Usuario usuario = mngrUsuario.fetch(idFirmante);

			nombres = usuario.getNombres();
			paterno = usuario.getApellidoPaterno();
			materno = usuario.getMaterno();

		} else if (fetch.getDocumentoAntefirmaKey().getTipoFirmante() == TipoDestinatario.REPRESENTANTE_LEGAL
				.getStatus()) {

			RepresentanteLegal repLegal = mngrRepresentanteLegal.fetch(Integer.valueOf(idFirmante));

			nombres = repLegal.getNombres();
			paterno = repLegal.getPaterno();
			materno = repLegal.getMaterno();

		}

		fetch.setPaterno(paterno);
		fetch.setMaterno(materno);
		fetch.setNombres(nombres);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List, org.hibernate.criterion.ProjectionList,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> search(List<Criterion> restrictions, List<Order> orders, ProjectionList projections,
			Integer fetchSize, Integer firstResult) {
		List<DocumentoAntefirmaAsunto> search = (List<DocumentoAntefirmaAsunto>) super.search(restrictions, orders,
				projections, fetchSize, firstResult);

		if (search != null && !search.isEmpty() && search.get(0) instanceof DocumentoAntefirmaAsunto)
			for (DocumentoAntefirmaAsunto fetch : search)
				completeItem(fetch);

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
		List<DocumentoAntefirmaAsunto> search = (List<DocumentoAntefirmaAsunto>) super.search(restrictions);
		for (DocumentoAntefirmaAsunto fetch : search)
			completeItem(fetch);
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
		List<DocumentoAntefirmaAsunto> search = (List<DocumentoAntefirmaAsunto>) super.search(restrictions, orders);
		for (DocumentoAntefirmaAsunto fetch : search)
			completeItem(fetch);
		return search;
	}

}