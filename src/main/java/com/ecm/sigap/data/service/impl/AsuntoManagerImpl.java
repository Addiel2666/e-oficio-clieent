/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Acl;
import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.FolioPS;
import com.ecm.sigap.data.model.FolioPSClave;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.service.EntityManager;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link Asunto}.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Service("asuntoService")
public class AsuntoManagerImpl extends ManagerImpl<Asunto> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AsuntoManagerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#setDao(com.ecm.sigap.data.dao.
	 * EntityDAO)
	 */
	@Autowired
	@Qualifier("asuntoDao")
	@Override
	protected void setDao(EntityDAO<Asunto> dao) {
		super.setDao(dao);
	}

	/** */
	@Value("${folioIntermedioCustom}")
	private Boolean folioIntermedioCustom;

	/** */
	@Value("${folioIntermedioCustomEstructura}")
	private String folioIntermedioCustomEstructura;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#save(java.lang.Object)
	 */
	@Override
	public void save(Asunto item) throws Exception {

		String folderIdAsunto = null;

		String folioClaveParaDesbloquear = null;

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		try {
			// Para el caso de los Tramites, cuando se guarda no tiene Folio
			// Area o es Nulo
			if (null != item.getFolioArea() && (!item.getFolioArea().isEmpty())) {

				String contentIdExpediente = item.getTipoExpediente().getContentId();

				if (contentIdExpediente == null || "".equalsIgnoreCase(contentIdExpediente.trim())) {

					throw new Exception("El tipo de expediente :: " + item.getTipoExpediente().getIdExpediente()
							+ " >> " + item.getTipoExpediente().getDescripcion()
							+ " no posee un id de folder en el repositorio.");

				}

				String pathExpedente = null;
				try {
					pathExpedente = endpoint.getObjectPath(contentIdExpediente);
				} catch (Exception e) {
					log.error("ERROR OBTENIENDO EL pathExpedente: " + e.getMessage());
					throw e;
				}

				String pathAsunto = null;
				try {
					pathAsunto = pathExpedente + "/" + item.getFolioArea();
				} catch (Exception e) {
					log.error("ERROR OBTENIENDO EL pathAsunto");
					throw e;
				}

				Boolean pathAsuntoExist = endpoint.existeCarpeta(pathAsunto);

				if (pathAsuntoExist) {
					throw new Exception("Ya existe un folder con el folio :: " + item.getFolioArea());
				}

				Map<String, String> additionalData = new HashMap<>();

				additionalData.put("idArea", item.getArea().getIdArea().toString());

				Acl acl = endpoint.getAcl(environment.getProperty("aclNameFolderAsunto"), additionalData);

				if (acl == null || acl.getPermissions().isEmpty()) {
					log.debug(">>> ERROR EL ACL ES NULL O LOS PERMISOS ESTAN VACIO");
					throw new Exception("No se pudo obtener el ACL requerido para el asunto... ");
				}

				try {
					folderIdAsunto = endpoint.createFolderIntoId(//
							contentIdExpediente, //
							environment.getProperty("folderTypeAsunto"), //
							item.getFolioArea());
				} catch (Exception e) {
					log.error("ERROR CREANDO FOLDER DEL ASUNTO");
					throw e;
				}

				try { // ACL del folder asunto.

					endpoint.setACL(folderIdAsunto, acl, false);

				} catch (Exception e) {
					log.error("NO SE APLICO ACL AL FOLDER DEL ASUNTO... :: " + e.getMessage());

				}

				item.setContentId(folderIdAsunto);
			}

			if (item.getTipoAsunto() == TipoAsunto.ASUNTO && item.getIdAsunto() == null) {
				if (folioIntermedioCustom) {

					Long consecutivo = getNextval(
							"SEQFOLIOINSTITUCIONAL_" + item.getArea().getInstitucion().getIdInstitucion());

					String folioInter = folioIntermedioCustomEstructura.replace("{CONSECUTIVO}",
							(new DecimalFormat("000000").format(consecutivo)));

					item.getAsuntoDetalle().setFolioIntermedio(folioInter);

				}
			}

			if (item.getAsuntoDetalle().isClaveAuto()) {
				folioClaveParaDesbloquear = item.getAsuntoDetalle().getClave();
				String tmp = formatFolioClave(item.getArea().getIdArea(), folioClaveParaDesbloquear);
				item.getAsuntoDetalle().setClave(tmp);
			}

			super.save(item);

			// El Folio Area no viene lleno para todos los casos, solamente
			// para el caso que se este guardando un Asunto
			if ("A".equals(item.getTipoAsunto().getValue())) {

				addNextFolio(item.getFolioArea(), item.getArea().getIdArea());

				if (item.getAsuntoDetalle().isClaveAuto()) {
					addNextFolioClave(item.getArea().getIdArea(), folioClaveParaDesbloquear);
				}

			}

		} catch (Exception e) {

			log.error(
					"Ocurrio un error al momento de guardar el asunto con la siguiente descripcion: " + e.getMessage());

			// El Folio Area no viene lleno para todos los casos, solamente para
			// el caso que se este guardando un Asunto
			if (TipoAsunto.ASUNTO.equals(item.getTipoAsunto())) {

				if (null != folderIdAsunto) {
					try {

						eliminarFolderAsunto(folderIdAsunto, endpoint);

					} catch (Exception e2) {
						log.error(e2.getLocalizedMessage());
					}
				}

				desbloqueaFolio(item.getFolioArea(), item.getArea().getIdArea());

				if (item.getAsuntoDetalle().isClaveAuto()) {
					desbloquearFolioClave(item.getArea().getIdArea(), item.getAsuntoDetalle().getClave());
				}

				log.debug("Proceso de rollback del asunto completado exitosamente !!");
			}

			throw e;

		}
	}

	/**
	 * al hacer rollback del asunto guardado se elimina su folder creado.
	 * 
	 * @param folderIdAsunto
	 * @param endpoint
	 * @throws Exception
	 */
	private void eliminarFolderAsunto(String folderIdAsunto, IEndpoint endpoint) throws Exception {

		log.debug("Eliminando el folder con objectid " + folderIdAsunto);

		// permisos para eliminar
		String aclRollbackFolderAsunto = environment.getRequiredProperty("aclRollbackFolderAsunto");

		boolean addPermisos = endpoint.addPermisos(folderIdAsunto, aclRollbackFolderAsunto, null);

		if (addPermisos) {

			boolean eliminarFolder = false;

			try {
				eliminarFolder = endpoint.eliminarFolder(folderIdAsunto);
			} catch (Exception ex1) {
				log.error(ex1.getMessage());
			}

			if (eliminarFolder) {

				log.debug("Folder eliminado.. !!");

			} else {

				log.error("NO SE PUDO ELIMINAR EL FOLDER DEL ASUNTO :: " + folderIdAsunto);

			}

		} else {

			log.warn("NO SE PUDIERON AGREGAR PERMISOS AL FOLDER PARA PODERLO ELIMINAR...");

		}
	}

	/**
	 * se agrega el siguiente folio en la serie,
	 * 
	 * @param folioArea
	 * @param idArea
	 */
	private synchronized void addNextFolio(String folioArea, Integer idArea) {

		HashMap<String, Object> params = new HashMap<>();
		params.put("folio", Integer.parseInt(folioArea));
		// TODO Validar este codigo para que use el objeto Remitente
		params.put("idArea", idArea);

		try {

			uniqueResult("addNextFolio", params);
			log.debug("se agrego el siguiente folio del area :: " + folioArea + "  ::  " + idArea);

		} catch (Exception ex) {
			log.error("NO SE PUDO AGREGAR EL SIGUIENTE FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO AGREGAR EL SIGUIENTE FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO AGREGAR EL SIGUIENTE FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO AGREGAR EL SIGUIENTE FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO AGREGAR EL SIGUIENTE FOLIO :: " + folioArea + "  ::  " + idArea);

			log.error(ex.getLocalizedMessage());
		}
	}

	/**
	 * se desbloquea el folio apartado.
	 * 
	 * @param folioArea
	 * @param idArea
	 */
	private synchronized void desbloqueaFolio(String folioArea, Integer idArea) {

		log.debug("Haciendo rollback del folio " + folioArea + " del area " + idArea);

		// Rollback del folio bloqueado
		HashMap<String, Object> params = new HashMap<>();

		params.put("folio", Integer.valueOf(folioArea));
		params.put("idArea", idArea);

		try {

			uniqueResult("desbloqueaFolio", params);
			log.debug("se regresó el folio del area :: " + folioArea + "  ::  " + idArea);

		} catch (Exception ex) {
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioArea + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO :: " + folioArea + "  ::  " + idArea);

			log.error(ex.getLocalizedMessage());
		}

	}

	/** Estrctura para generar el campo Clave. */
	@Value("${estructuraClaveAutomatico}")
	private String estructuraClaveAutomatico;

	/**
	 * Manejador para el tipo {@link FolioPS }
	 */
	@Autowired
	@Qualifier("foliopsClaveService")
	protected EntityManager<FolioPSClave> mngrFoliopsclave;

	/**
	 * toma la clave actual y la formatea segun dlientConfigs
	 * 
	 * @param idArea
	 * @param folioClave
	 * @return
	 */
	private String formatFolioClave(Integer idArea, String folioClave) {

		List<Criterion> restrictions = new ArrayList<Criterion>();

		FolioPSClave folio = mngrFoliopsclave.fetch(idArea);
		folio = folio == null ? new FolioPSClave() : folio;

		String numDocto;

		do {

			numDocto = estructuraClaveAutomatico.replace("{prefijo}", //
					StringUtils.isBlank(folio.getPrefijoFolio()) ? "" : folio.getPrefijoFolio());

			numDocto = numDocto.replace("{consecutivo}", //
					folioClave);

			numDocto = numDocto.replace("{consecutivo_trimed}", //
					StringUtils.stripStart(folioClave, "0"));

			// - - - - - - -

			numDocto = numDocto.replace("{sufijo}",
					StringUtils.isBlank(folio.getSufijoFolio()) ? "" : folio.getSufijoFolio());

			// Validamos que el numero de documento ya no este creado
			restrictions.clear();
			restrictions.add(Restrictions.eq("area.idArea", idArea));
			restrictions.add(Restrictions.eq("asuntoDetalle.numDocto", numDocto));

		} while (!search(restrictions).isEmpty());

		return numDocto;
	}

	/**
	 * Adds the next folio clave.
	 *
	 * @param idArea              the id area
	 * @param ultimoFolioAsignado the ultimo folio asignado
	 */
	private synchronized void addNextFolioClave(Integer idArea, String ultimoFolioAsignado) {
		try {

			if (StringUtils.isBlank(ultimoFolioAsignado))
				return;

			log.info(" Generando siguiente folio clave ::>> " + idArea);
			HashMap<String, Object> params = new HashMap<>();
			params.put("folio", Integer.valueOf(ultimoFolioAsignado));
			params.put("idArea", idArea);
			super.uniqueResult("addNextFolioClave", params);
			log.debug("se agrego el siguiente folio del area :: " + ultimoFolioAsignado + "  ::  " + idArea);

		} catch (Exception e) {
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO CLAVE DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO CLAVE DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO CLAVE DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO CLAVE DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error("NO SE PUDO GENERAR EL SIGUIENTE FOLIO CLAVE DE :: " + ultimoFolioAsignado + "  ::  " + idArea);
			log.error(e.getLocalizedMessage());

		}
	}

	/**
	 * Desbloquear folio clave.
	 *
	 * @param idArea            the id area
	 * @param folioADesbloquear the folio A desbloquear
	 */
	private synchronized void desbloquearFolioClave(Integer idArea, String folioADesbloquear) {
		try {

			if (StringUtils.isBlank(folioADesbloquear))
				return;

			HashMap<String, Object> params = new HashMap<>();
			params.put("folio", Integer.valueOf(folioADesbloquear));
			params.put("idArea", idArea);
			super.uniqueResult("desbloqueaFolioClave", params);
			log.debug("se regresó el folio calve del area :: " + folioADesbloquear + "  ::  " + idArea);

		} catch (Exception ex) {
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folioADesbloquear + "  ::  " + idArea);
			log.error("NO SE PUDO DESBLOQUEAR EL FOLIO CLAVE :: " + folioADesbloquear + "  ::  " + idArea);
			log.error(ex.getLocalizedMessage());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.service.ManagerImpl#search(java.util.List,
	 * java.util.List)
	 */
	@Override
	public List<?> execNativeQuery(String sqlquery, HashMap<String, Object> params) {
		List<?> result = super.execNativeQuery(sqlquery, params);
		return result;
	}
}