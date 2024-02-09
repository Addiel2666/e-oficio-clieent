/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroKey;
import com.ecm.sigap.data.model.TipoExpediente;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.TipoExpediente}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class TipoExpedienteController extends CustomRestController implements RESTController<TipoExpediente> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TipoExpedienteController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene tipo expediente", notes = "Obtiene el detalle de tipo de expediente de la seccion documentos")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@Override
	@RequestMapping(value = "/tipoExpediente", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TipoExpediente> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		TipoExpediente item = null;
		try {

			item = mngrTipoExpediente.fetch(String.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<TipoExpediente>(item, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta tipo expediente", notes = "Consulta la lista de tipo de expediente de la seccion documentos")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@Override
	@RequestMapping(value = "/tipoExpediente", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) TipoExpediente tipoExpediente)
			throws Exception {

		List<?> lst = new ArrayList<TipoExpediente>();
		log.info("Parametros de busqueda :: " + tipoExpediente);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tipoExpediente.getIdExpediente() != null)
				restrictions.add(Restrictions.idEq(tipoExpediente.getIdExpediente()));

			if (tipoExpediente.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", tipoExpediente.getDescripcion(),
						MatchMode.ANYWHERE));

			if (tipoExpediente.getArea() != null) {

				if (tipoExpediente.getArea().getIdArea() != null)
					restrictions.add(Restrictions.eq("area.idArea", tipoExpediente.getArea().getIdArea()));
			}

			if (tipoExpediente.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", tipoExpediente.getActivo()));

			// no listar el expediente por defecto o SIN EXPEDIENTE del area
			String defaultIdExp = getIdExpedienteDefault(tipoExpediente.getArea().getIdArea());
			restrictions.add(Restrictions.ne("idExpediente", defaultIdExp));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrTipoExpediente.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * 
	 * @param idArea
	 * @return
	 * @throws Exception
	 */
	private String getIdExpedienteDefault(Integer idArea) throws Exception {
		Parametro defaultExp = mngrParametro.fetch(new ParametroKey(idArea, "DEFAULT", "IDEXPEDIENTE"));

		if (defaultExp == null) {

			try {
				Integer idAreaTemplate = Integer.parseInt(getParamApp("SIGAPTEMPLATE", "IDAREA"));

				if (idAreaTemplate != null) {
					String idExpDefault = getExpedienteDefault(idAreaTemplate);

					if (idExpDefault != null) {
						saveExpedienteDefautl(idArea, idExpDefault);
						defaultExp = mngrParametro.fetch(new ParametroKey(idArea, "DEFAULT", "IDEXPEDIENTE"));
						if (defaultExp == null) {
							throw new Exception("Expediente por defecto no encontrado, idArea: " + idArea);
						}
					}
				}

			} catch (Exception e) {
				throw new Exception("Expediente por defecto no encontrado, idArea: " + idArea);
			}
		}

		return defaultExp.getValor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar tipo expediente", notes = "Agrega o edita un tipo de expediente de la seccion documentos")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 201, message = "Creado"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@Override
	@RequestMapping(value = "/tipoExpediente", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<TipoExpediente> save(
			@RequestBody(required = true) TipoExpediente tipoExpediente) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> EXPEDIENTE A GUARDAR O ACTUALIZAR: " + tipoExpediente);

				// validar que no tenga diagonales en la descripcion
				if (!validarDescripcion(tipoExpediente)) {
					return new ResponseEntity<TipoExpediente>(tipoExpediente, HttpStatus.BAD_REQUEST);
				}

				if (tipoExpediente.getIdExpediente() == null) {
					// se asigna esa cadena tmp para pasar la validacion de
					// entidad cuando es nuevo
					// en este momento no se tiene el contentId y para obtenerlo
					// es necesario el idExpediente
					tipoExpediente.setContentId("tmp");

					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoExpediente no se esten violando con este nuevo
					// registro
					// validateEntity(mngrTipoExpediente, tipoExpediente);

					List<Criterion> restrictions = new ArrayList<Criterion>();

					String descripcionTipo = Normalizer
							.normalize(tipoExpediente.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
							.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipo, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoExpediente.getArea().getIdArea()));

					List<TipoExpediente> tipos = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);

					if (tipos != null && tipos.size() > 0) {
						tipos.forEach(tm -> {
							String tipoTmp = Normalizer
									.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
									.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if (descripcionTipo.equals(tipoTmp)) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre",
										null);
							}
						});
					}

					log.debug("::>> Se va a guardar la informacion");
					// Guardamos la informacion
					mngrTipoExpediente.save(tipoExpediente);

					// crear el folder del expediente
					String expObjectId = createExpediente(tipoExpediente);
					if (StringUtils.isBlank(expObjectId)) {
						// deja inactivo
						setInactive(tipoExpediente);
						throw new Exception(
								"No fue posible crear el folder del expediente, el expediente ha quedado inactivo");

					} else {
						// actualizar el object id del expediente en la bd
						tipoExpediente.setContentId(expObjectId);
						mngrTipoExpediente.update(tipoExpediente);
					}

					return new ResponseEntity<TipoExpediente>(tipoExpediente, HttpStatus.CREATED);

				} else {
					TipoExpediente oldTipoExpediente = mngrTipoExpediente.fetch(tipoExpediente.getIdExpediente());

					log.debug("::>> Cambio de descripcion");

					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoExpediente no se esten violando con este nuevo
					// registro
					// validateEntity(mngrTipoExpediente, tipoExpediente);

					List<Criterion> restrictions = new ArrayList<Criterion>();

					String descripcionTipo = Normalizer
							.normalize(tipoExpediente.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
							.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipo, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoExpediente.getArea().getIdArea()));

					List<TipoExpediente> tipos = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);

					if (tipos != null && tipos.size() > 0) {
						tipos.forEach(tm -> {
							String tipoTmp = Normalizer
									.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD)
									.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if (descripcionTipo.equals(tipoTmp)
									&& !tipoExpediente.getIdExpediente().equals(tm.getIdExpediente())) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre",
										null);
							}
						});
					}

					if (expedienteConTramites(tipoExpediente.getIdExpediente()) > 0
							&& !oldTipoExpediente.getDescripcion().equals(tipoExpediente.getDescripcion())) {

						throw new ConstraintViolationException(
								"No es posible modificar el nombre del expediente ya tiene trámites asignados", null);

					} else if (expedienteConTramites(tipoExpediente.getIdExpediente()) == 0
							&& !oldTipoExpediente.getDescripcion().equals(tipoExpediente.getDescripcion())) {
						log.debug("::>> Renombrar folder");
						EndpointDispatcher.getInstance().renameFolder(oldTipoExpediente.getContentId(),
								tipoExpediente.getDescripcion());
					}

					// Actualizamos la informacion
					tipoExpediente.setContentId(oldTipoExpediente.getContentId());
					mngrTipoExpediente.update(tipoExpediente);

					log.debug("::>> Registro Actualizado");
					return new ResponseEntity<TipoExpediente>(tipoExpediente, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<TipoExpediente>(tipoExpediente, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * cuenta cuantos tramties el tipo expedietne
	 * 
	 * @param idExpediente
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int expedienteConTramites(String idExpediente) {

		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("tipoExpediente.idExpediente", idExpediente));

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.countDistinct("idAsunto").as("countr"));

		List<Map<String, Long>> result = mngrAsunto.search(restrictions, null, projections, null, null);

		return result.get(0).get("countr").intValue();
	}

	/**
	 * 
	 * @param exp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean descripcionRepetida(TipoExpediente exp) {
		List<TipoExpediente> exps = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("area.idArea", exp.getArea().getIdArea()));
		restrictions.add(Restrictions.eq("descripcion", exp.getDescripcion()));

		exps = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);

		if (exps != null && exps.size() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param exp
	 */
	private void setInactive(TipoExpediente exp) {
		if (exp != null && exp.getIdExpediente() != null) {
			exp.setActivo(false);
			mngrTipoExpediente.update(exp);
		}
	}

	/**
	 * 
	 * @param tipoExpediente
	 * @return
	 * @throws Exception
	 */
	private boolean validarDescripcion(TipoExpediente tipoExpediente) throws Exception {
		return !StringUtils.contains(tipoExpediente.getDescripcion(), "/");
	}

	/**
	 * 
	 * @param exp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String createExpediente(TipoExpediente exp) throws Exception {
		String expObjectId = "";
		try {
			IEndpoint repo = EndpointDispatcher.getInstance();

			log.debug("::::: obtener el path del area");
			exp.setArea(mngrArea.fetch(exp.getArea().getIdArea()));
			if (exp.getArea() == null || exp.getArea().getContentId() == null) {
				throw new Exception("No se pudo recuperar el identificador del folder de area");
			}

			String pathArea = repo.getObjectPath(exp.getArea().getContentId());

			log.debug(":::::: verificar si ya existe el folder");
			String pathExpediente = pathArea + "/" + exp.getDescripcion();
			if (repo.existeCarpeta(pathExpediente)) {
				log.debug(":::::: el folder existe, devolver el robjectid del expediente");
				expObjectId = repo.getFolderIdByPath(pathExpediente);

			} else {
				log.debug(":::::: el folder NO existe, crear expediente");
				expObjectId = repo.createFolderIntoId(exp.getArea().getContentId(),
						environment.getProperty("subfolderTypeArea"), //
						exp.getDescripcion());

				// linkear carpeta de area
				log.debug(":::::: el metodo createFolderIntoId ya hace el link");

				log.debug(":::::: setear ACL, se usa el mismo del folder de area");
				String aclArea = ((List<String>) repo.getObjectProperty(exp.getArea().getContentId(), "acl_name"))
						.get(0);

				try {
					repo.setACLByDQL(expObjectId, aclArea);
				} catch (Exception e) {

				}

				log.debug(":::::: agregar atributos");
				Map<String, Object> properties = new HashMap<>();
				properties.put("acl_name", aclArea);
				properties.put("idarea", String.valueOf(exp.getArea().getIdArea()));
				properties.put("idsubfolder", exp.getIdExpediente());
				properties.put("idrecord", "1");
				repo.setProperties(expObjectId, properties);

				log.debug(" NUEVO FOLDER \"" + exp.getDescripcion() + " creado. ID :: " + expObjectId);

			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			setInactive(exp);
			throw new Exception("No fue posible crear el folder del expediente, el expediente ha quedado inactivo");
		}

		return expObjectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Serializable id) {
		throw new UnsupportedOperationException();

	}

	/**
	 * Gets the expediente default.
	 *
	 * @param idArea the id area
	 * @return the expediente default
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	private String getExpedienteDefault(Integer idArea) throws Exception {
		List<Parametro> lst = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("parametroKey.idArea", idArea));
		restrictions.add(Restrictions.eq("parametroKey.idSeccion", "DEFAULT"));
		restrictions.add(Restrictions.eq("parametroKey.idClave", "IDEXPEDIENTE"));

		lst = (List<Parametro>) mngrParametro.search(restrictions);

		if (lst != null && lst.size() != 1) {
			throw new Exception("Numero de registros de default idExpediente != 1");
		}

		Parametro defaultExpParam = lst.get(0);
		String defaultExp = defaultExpParam.getValor();

		if (defaultExp != null && defaultExp.length() > 20) {
			defaultExp = defaultExp.substring(0, 20);
		}

		return defaultExp;
	}

	/**
	 * Save expediente defautl.
	 *
	 * @param idArea       the id area
	 * @param idExpediente the id expediente
	 * @throws Exception the exception
	 */
	private void saveExpedienteDefautl(Integer idArea, String idExpediente) throws Exception {
		Parametro paramExp = new Parametro();
		paramExp.setParametroKey(new ParametroKey(idArea, "DEFAULT", "IDEXPEDIENTE"));
		paramExp.setValor(idExpediente);

		mngrParametro.save(paramExp);
	}
}
