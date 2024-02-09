/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.util.SerializationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.config.DBVendor;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.AreaAux;
import com.ecm.sigap.data.model.AreaAuxiliar;
import com.ecm.sigap.data.model.AreaPromotor;
import com.ecm.sigap.data.model.AreaPromotorKey;
import com.ecm.sigap.data.model.AreaRemitente;
import com.ecm.sigap.data.model.AreaRemitenteKey;
import com.ecm.sigap.data.model.Folio;
import com.ecm.sigap.data.model.FolioArea;
import com.ecm.sigap.data.model.FolioAreaKey;
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroKey;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.model.PermisoKey;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Tema;
import com.ecm.sigap.data.model.TipoDocumento;
import com.ecm.sigap.data.model.TipoEvento;
import com.ecm.sigap.data.model.TipoExpediente;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.TipoPrioridad;
import com.ecm.sigap.data.service.EntityManager;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Area}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@RestController
public class AreaController extends CustomRestController implements RESTController<Area> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(AreaController.class);

	/**
	 * The area padre.
	 */
	private Area areaPadre;

	/**
	 * The area original.
	 */
	private Area areaOriginal;

	/**
	 * The rollback acl name.
	 */
	private String rollbackAclName = null;

	/**
	 * The rollback grupo area.
	 */
	private String rollbackNameGrupoArea = null;

	/**
	 * The rollback grupo area conf.
	 */
	private String rollbackNameGrupoAreaConf = null;

	/**
	 * The rollback folder object id.
	 */
	private String rollbackFolderObjectId = null;

	/**
	 * The rollback Sub folder object id.
	 */
	private List<String> rollbackSubFolderObjectId = new ArrayList<>();

	/**
	 * Referencia hacia el REST controller de {@link TipoExpedienteController}.
	 */
	@Autowired
	private TipoExpedienteController tipoExpedienteController;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene area", notes = "Obtiene detalle de una area")
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
	@RequestMapping(value = "/area", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Area> get(@RequestParam(value = "id", required = true) Serializable id) {

		Area item = null;
		try {

			item = mngrArea.fetch(Integer.valueOf((String) id));

			log.debug(" Detalles del area consultada >> " + item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<Area>(item, HttpStatus.OK);

	}

	/**
	 * 
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene grupos turnado", notes = "Obtiene la lista de grupos de envio turnado")
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

	@RequestMapping(value = "/areaAux", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<AreaAux> getAux(@RequestParam(value = "id", required = true) Serializable id) {

		AreaAux item = null;
		try {

			item = mngrAreaAux.fetch(Integer.valueOf((String) id));

			log.debug(" Detalles del area consultada >> " + item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<AreaAux>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RestController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consultar area", notes = "Consulta la lista de areas")
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
	@RequestMapping(value = "/area", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Area area) {

		List<?> lst = new ArrayList<Area>();
		log.debug("PARAMETROS DE BUSQUEDA : " + area);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (area.getIdArea() != null)
				restrictions.add(Restrictions.idEq(area.getIdArea()));

			if (area.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", area.getActivo()));

			if (area.getInteropera() != null)
				restrictions.add(Restrictions.eq("interopera", area.getInteropera()));

			if ((area.getClave() != null) && (!area.getClave().isEmpty())) {
				// busqueda exacta o parcial
				if (area.isExactSearch()) {
					restrictions.add(
							EscapedLikeRestrictions.ilike("clave", area.getClave().toLowerCase(), MatchMode.EXACT));
				} else {
					restrictions.add(
							EscapedLikeRestrictions.ilike("clave", area.getClave().toLowerCase(), MatchMode.ANYWHERE));
				}
			}

			if (StringUtils.isNotBlank(area.getClaveDepartamental())) {
				// busqueda exacta o parcial
				if (area.isExactSearch()) {
					restrictions.add(EscapedLikeRestrictions.ilike("claveDepartamental",
							area.getClaveDepartamental().toLowerCase(), MatchMode.EXACT));
				} else {
					restrictions.add(EscapedLikeRestrictions.ilike("claveDepartamental",
							area.getClaveDepartamental().toLowerCase(), MatchMode.ANYWHERE));
				}
			}

			if (StringUtils.isNotBlank(area.getSiglas())) {
				// busqueda exacta o parcial
				if (area.isExactSearch()) {
					restrictions.add(
							EscapedLikeRestrictions.ilike("siglas", area.getSiglas().toLowerCase(), MatchMode.EXACT));
				} else {
					restrictions.add(EscapedLikeRestrictions.ilike("siglas", area.getSiglas().toLowerCase(),
							MatchMode.ANYWHERE));
				}
			}

			if ((area.getDescripcion() != null) && (!area.getDescripcion().isEmpty())) {
				// Cuando el front pide explicitamente el area ciudadano
				if (area.getDescripcion().equals("areaCiudadanoOriginal"))
					restrictions.add(Restrictions.eq("descripcion", "CIUDADANO"));
				else if (area.getDescripcion().equals("empresa"))
					restrictions.add(Restrictions.eq("descripcion", "EMPRESA"));
				else
					restrictions.add(
							EscapedLikeRestrictions.ilike("descripcion", area.getDescripcion(), MatchMode.ANYWHERE));
			}

			if (area.getIdAreaPadre() != null) {
				if (area.getIdAreaPadre() == -1) {
					// para obtener el area principal idAreaPadre == null
					restrictions.add(Restrictions.isNull("idAreaPadre"));
				} else {
					restrictions.add(Restrictions.eq("idAreaPadre", area.getIdAreaPadre()));
				}
			}

			if (area.getInstitucion() != null) {

				if (area.getInstitucion() != null && area.getInstitucion().getIdInstitucion() != null)
					restrictions.add(
							Restrictions.eq("institucion.idInstitucion", area.getInstitucion().getIdInstitucion()));

				if ((area.getInstitucion().getTipo() != null) && (!area.getInstitucion().getTipo().isEmpty()))
					restrictions.add(Restrictions.ilike("institucion.tipo", area.getInstitucion().getTipo()));
			}

			if (area.getTitular() != null) {

				if ((area.getTitular().getId() != null) && (!area.getTitular().getId().isEmpty()))
					restrictions.add(Restrictions.eq("titular.id", area.getTitular().getId()));

				if ((area.getTitular().getPaterno() != null) && (!area.getTitular().getPaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("titular.paterno", area.getTitular().getPaterno(),
							MatchMode.ANYWHERE));

				if ((area.getTitular().getMaterno() != null) && (!area.getTitular().getMaterno().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("titular.materno", area.getTitular().getMaterno(),
							MatchMode.ANYWHERE));

				if ((area.getTitular().getNombres() != null) && (!area.getTitular().getNombres().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("titular.nombres", area.getTitular().getNombres(),
							MatchMode.ANYWHERE));

				if (StringUtils.isNotBlank(area.getTitular().getNombreCompleto()))
					restrictions.add(EscapedLikeRestrictions.ilike("titular.nombreCompleto",
							area.getTitular().getNombreCompleto(), MatchMode.ANYWHERE));

				if ((area.getTitular().getIdTipo() != null) && (!area.getTitular().getIdTipo().isEmpty()))
					restrictions.add(Restrictions.eq("titular.idTipo", area.getTitular().getIdTipo()));

				if ((area.getTitular().getCargo() != null) && (!area.getTitular().getCargo().isEmpty()))
					restrictions.add(EscapedLikeRestrictions.ilike("titular.cargo", area.getTitular().getCargo(),
							MatchMode.ANYWHERE));
			}

			if (StringUtils.isNotBlank(area.getTitularCargo()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("titularCargo", area.getTitularCargo(), MatchMode.ANYWHERE));

			if (area.getLimite() != null) {

				String queryName;
				if (DBVendor.POSTGRESQL == dbVendor)
					queryName = String.format(environment.getProperty("obtenerIdAreasHijos_PG"));
				else
					queryName = String.format(environment.getProperty("obtenerIdAreasHijos"));

				HashMap<String, Object> params = new HashMap<>();
				params.put("idArea", area.getLimite());
				List<?> idsAreas = mngrArea.execNativeQuery(queryName, params);
				if (idsAreas.toString() != null) {
					restrictions
							.add(Restrictions.in("idArea",
									Stream.of((idsAreas.toString().replace("[", "").replace("]", "")).split(","))
											.map(elem -> StringUtils.isBlank(elem) || !StringUtils.isNumeric(elem)
													? null
													: Integer.parseInt(elem))
											.collect(Collectors.toList())));
				}
			}

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc((StringUtils.isNotBlank(area.getOrder()) ? area.getOrder() : "descripcion")));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrArea.search(restrictions, orders, null, null, null);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Verify limite.
	 *
	 * @param idArea the id area
	 * @param limite the limite
	 * @return the response entity
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Verifica area limite", notes = "Verifica el area padre si se encuentra en el limite ")
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

	@RequestMapping(value = "/area/limite", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> verifyLimite(
			@RequestParam(value = "idArea", required = true) Integer idArea,
			@RequestParam(value = "limite", required = true) Integer limite) {

		List<?> lst = new ArrayList<>();
		Boolean verified = false;
		try {

			List<?> idsAreas = new ArrayList<>();
			if (limite != null && idArea != null) {

				String queryName;
				if (DBVendor.POSTGRESQL == dbVendor)
					queryName = String.format(environment.getProperty("obtenerIdAreasHijos_PG"));
				else
					queryName = String.format(environment.getProperty("obtenerIdAreasHijos"));

				HashMap<String, Object> params = new HashMap<>();
				params.put("idArea", limite);
				idsAreas = mngrArea.execNativeQuery(queryName, params);
			} else {
				return new ResponseEntity<Boolean>(verified, HttpStatus.BAD_REQUEST);
			}

			lst = Stream.of((idsAreas.toString().replace("[", "").replace("]", "")).split(",")).map(
					elem -> StringUtils.isBlank(elem) || !StringUtils.isNumeric(elem) ? null : Integer.parseInt(elem))
					.collect(Collectors.toList());

			if (lst != null && lst.stream().filter(o -> o.equals(idArea)).findAny().isPresent()) {
				verified = true;
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<Boolean>(verified, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ecm.sigap.data.controller.RestController#delete(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar area", notes = "Elimina una area")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@Override
	@RequestMapping(value = "/area", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("AREA A ELIMINAR >> " + id);

		try {

			mngrArea.delete(mngrArea.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RestController#save(java.lang.Object)
	 */
	@Override
	public ResponseEntity<Area> save(Area object) throws Exception {
		return save(object, "DEL_AREA_TEMPLATE");
	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear area", notes = "Crea una nueva area en el sistema")
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

	@RequestMapping(value = "/area", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Area> save(//
			@RequestBody(required = true) Area area, //
			@RequestParam(name = "clonarCatalogos", required = false, defaultValue = "DEL_AREA_TEMPLATE") String clonarCatalogos //
	) throws Exception {

		try {

			validateSystemFolders();

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			if (null != area && StringUtils.isNotBlank(area.getDescripcion())) {

				if (validarCaracteresEspeciales(area.getDescripcion())) {

					String caracteresInvalidos = environment.getProperty("invalid.caracter.nameArea");

					throw new ConstraintViolationException(
							errorMessages.getString("areaCarateresInval") + caracteresInvalidos.replace("+|\\", " "),
							null);

				}

			} else {

				throw new Exception(errorMessages.getString("areaSinDesc"));

			}

			if (!esSoloLectura(userId)) {

				log.debug("AREA A GUARDAR >> " + area);

				if (area.getInstitucion() != null && area.getInstitucion().getIdInstitucion() != null) {

					// si el titular usuario viene vacio se le pone el
					// representante
					// SINASIGNAR
					if (area != null && area.getTitular() == null) {
						area.setTitular(mngrRepresentante.fetch("SINASIGNAR"));
					}

					// poner prefijo o sufijo si se encuentra repetido para la
					// carpeta del repo
					// area.setDescripcion(normalizaDescripcion(area));

					if (area.getIdArea() == null) {

						// Validamos que las reglas de validacion de la entidad
						// Tipo Area no se esten violando con este nuevo
						// registro se valida si existe la
						// clave(Descripcion,claveDepartamental,IdInstitucion)
						validateEntity(mngrArea, area);

						// // verificar nombre de area
						// if (!descripcionValida(area)) {
						// return new ResponseEntity<Area>(area,
						// HttpStatus.IM_USED);
						// }
						// e valida si existe un area con la misma desripcion y
						// con
						// el mismo area padre
						if (existeMismaAreaMismaAreaPadre(area)) {
							return new ResponseEntity<Area>(area, HttpStatus.CONFLICT);
						}
						/*
						 * se comenta ya que esta validacion se realiza en un proceso anterior y el
						 * front no permite ejecutar la creacion hasta tener clave, clave departamental
						 * y/o siglas validadas que no existen en el sistema// Validamos que no se
						 * repita la clave, clave departamental y/o siglas if
						 * (!validarClaveDepSiglasClave(area)) { throw new
						 * Exception(errorMessages.getString("areaSiglasClaveExiste")); }
						 */

						try {

							// guardar para poder obtener el idArea
							mngrArea.save(area);

							// SI EL AREA SE MARCO PARA QUE INTEROPERE, Y SU
							// AREA
							// PADRE TAMBIEN, SE GUARDA EL REGISTRO PARA QUE SE
							// ENVIE EN LA
							// SINCRONIZACION PARCIAL DE DIRECTORIOS.
							// TODO insertar registro en MODIFICACIONES_INTEROP
							// INSERT INTO modificaciones_interop(idusuario,
							// idarea, keyusuario, tipomodificacion) VALUES
							// ('-', v_IDAREA, 'AREA', 1);

							if ("I".equals(area.getInstitucion().getTipo())) {

								try {

									ExecutorService taskExecutor = createAreaStructure(area, clonarCatalogos);

									taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

								} catch (InterruptedException ex) {
									log.error(ex.getLocalizedMessage());

									throw ex;
								}

							}

						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());

							throw ex;
						}

						return new ResponseEntity<Area>(area, HttpStatus.CREATED);

					} else {

						Area old = mngrArea.fetch(area.getIdArea());

						area.setContentId(old.getContentId());

						if (null == area.getAreaPadre() || null == old.getIdAreaPadre()) {

							if (existeMismaAreaMismaAreaPadre(area)) {
								return new ResponseEntity<Area>(area, HttpStatus.CONFLICT);
							}

						} else if (!old.getDescripcion().equalsIgnoreCase(area.getDescripcion())
								|| !old.getIdAreaPadre().equals(area.getIdAreaPadre())) {
							if (existeMismaAreaMismaAreaPadre(area)) {
								return new ResponseEntity<Area>(area, HttpStatus.CONFLICT);
							}
						}

						// Validamos que no se repita la clave departamental y/o siglas
						if (!validarClaveDepSiglasClave(area)) {
							throw new ConstraintViolationException(errorMessages.getString("areaSiglasClaveExiste"),
									null);
						}

						if (!validarNombreAreaEnDestino(area, old)) {
							throw new ConstraintViolationException(errorMessages.getString("areaMismoNombre"), null);
						}
						
						if(area.getActivo() != old.getActivo())
							area.setActiveInactive(area.getActivo().toString());
						else
							area.setActiveInactive(null);

						try {
							// mngrArea.beginTransaction();
							// if((StringUtils.isBlank(area.getClaveDepartamental())
							// && are))
							mngrArea.update(area);

							// si cambia la descripcion y pasa las validaciones,
							// renombrar el folder dearea
							if (!old.getDescripcion().equalsIgnoreCase(area.getDescripcion()) 
									&& "I".equals(area.getInstitucion().getTipo())) {
								EndpointDispatcher.getInstance().renameFolder(area.getContentId(),
										getNombreFolder(area));
							}
							// si interopera entonces se actualizan los
							// parametros de ws
							if (!old.getInteropera().equals(area.getInteropera())) {
								saveParametrosWS(area.getIdArea(), true);
							}
						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());

							throw ex;
						}
						return new ResponseEntity<Area>(area, HttpStatus.OK);
					}
				} else {

					throw new BadRequestException();

				}
			} else {

				throw new BadRequestException();

			}

		} catch (Exception e) {
			// invoca metodo para hacer rollback de lo creado en el repo
			rollbackRepo();
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Valdiacion para evitar areas con nombre repetido,
	 * 
	 * @param areaModificada
	 * @param areaOriginal
	 * @return
	 */
	private boolean validarNombreAreaEnDestino(Area areaModificada, Area areaOriginal) {

		List<Criterion> res = new ArrayList<Criterion>();

		// se valida si hay otra con el mismo nombre
		res.add(Restrictions.eq("idAreaPadre", areaOriginal.getIdAreaPadre()));
		res.add(EscapedLikeRestrictions.ilike("descripcion", areaModificada.getDescripcion(), MatchMode.EXACT));
		res.add(Restrictions.not(Restrictions.eq("idArea", areaOriginal.getIdArea())));

		List<?> lst = mngrArea.search(res);

		if (lst.size() > 0)
			return false;

		// si se mueve se valide en el destino
		if (areaModificada.getIdAreaPadre() != areaOriginal.getIdAreaPadre()) {
			res.clear();

			res.add(Restrictions.eq("idAreaPadre", areaModificada.getIdAreaPadre()));
			res.add(EscapedLikeRestrictions.ilike("descripcion", areaModificada.getDescripcion(), MatchMode.EXACT));
			res.add(Restrictions.not(Restrictions.eq("idArea", areaOriginal.getIdArea())));

			lst = mngrArea.search(res);

			if (lst.size() > 0)
				return false;
		}

		return true;
	}

	/**
	 * 
	 * @author Alfredo Morales
	 *
	 */
	public class WorkerThreadFactory implements ThreadFactory {

		private int counter = 0;

		private String prefix = "";

		public WorkerThreadFactory(String prefix) {
			this.prefix = prefix;
		}

		public Thread newThread(Runnable r) {
			return new Thread(r, prefix + "-" + counter++);
		}

	}

	/**
	 * @param area
	 * @param clonarCatalogos
	 * @return
	 */
	private ExecutorService createAreaStructure(Area area, String clonarCatalogos) {

		ThreadFactory threadFactory = new WorkerThreadFactory("ecm");

		ExecutorService taskExecutor = Executors.newFixedThreadPool(11, threadFactory);

		// CONSULTAR AREA PLANTILLA
		Integer idAreaTemplate = Integer.parseInt(getParamApp("SIGAPTEMPLATE", "IDAREA"));

		// replicar roles y permisos del template
		{
			Future<?> future = taskExecutor.submit(//
					new SaveRolesThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar el folio inicial del template y
		// folios de documentos
		{
			Future<?> future = taskExecutor.submit(//
					new SaveFoliosAsuntosThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar catalogos template
		{
			Integer idAreaFrom = null;

			if ("DEL_AREA_PADRE".equalsIgnoreCase(clonarCatalogos)) {

				idAreaFrom = area.getIdAreaPadre();

			} else if ("DEL_AREA_TEMPLATE".equalsIgnoreCase(clonarCatalogos)) {

				idAreaFrom = idAreaTemplate;

			}

			Future<?> future = taskExecutor.submit(//
					new SaveCatalogosThread(idAreaFrom, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}

		}

		// replicar areas promotoras template
		{
			Future<?> future = taskExecutor.submit(//
					new SaveAreasPromotoresThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar areas remitentes template
		{
			Future<?> future = taskExecutor.submit(//
					new SaveAreasRemitentesThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar parametros de content
		{
			Future<?> future = taskExecutor.submit(//
					new SaveParametrosContentThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar parametros de ws
		{
			Future<?> future = taskExecutor.submit(//
					new SaveParametrosWSThread(area.getIdArea(), false));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar parametros de content
		{
			Future<?> future = taskExecutor.submit(//
					new SaveParametrosFoliosDocThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// foliacion por area
		{
			Future<?> future = taskExecutor.submit(//
					new SaveFoliosPorAreaThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}
		// foliacion de foliosclave por area
		{
			Future<?> future = taskExecutor.submit(//
					new SaveFoliosClavePorAreaThread(idAreaTemplate, area.getIdArea()));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		// replicar folders y aplicar acl
		{
			Future<?> future = taskExecutor.submit(//
					new SaveFoldersAreaThread(area));

			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		taskExecutor.shutdown();

		return taskExecutor;
	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveRolesThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveRolesThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/**
		 * Save permisos rol.
		 *
		 * @param rol            the rol
		 * @param idAreaTemplate the id area template
		 * @param idRolTemplate  the id rol template
		 * @throws Exception the exception
		 */
		private void savePermisosRol(Rol rol, Integer idAreaTemplate, Integer idRolTemplate) throws Exception {
			List<Permiso> lst = new ArrayList<>();

			// obtener los permisos template
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("permisoKey.idArea", idAreaTemplate));
			restrictions.add(Restrictions.eq("permisoKey.idRol", idRolTemplate));

			lst = (List<Permiso>) mngrPermiso.search(restrictions);

			for (Permiso p : lst) {
				Permiso per = new Permiso();
				per.setPermisoKey(new PermisoKey());
				// setear el area y rol del nuevo rol creado en la nueva area
				per.getPermisoKey().setIdArea(rol.getIdArea());
				per.getPermisoKey().setIdRol(rol.getIdRol());
				per.getPermisoKey().setIdObjeto(p.getPermisoKey().getIdObjeto());
				per.getPermisoKey().setIdTipoPermiso(p.getPermisoKey().getIdTipoPermiso());

				per.setDescripcion(p.getDescripcion());
				mngrPermiso.save(per);
			}

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Object call() throws Exception {

			// obtener roles area plantilla
			List<Rol> roles = getRoles(idAreaOrigen);

			for (Rol rol : roles) {
				Rol newRol = new Rol();
				// obtenemos el idRol original antes de
				// cambiarlo por el nuevo
				Integer idRolTemplate = rol.getIdRol();

				// insertar roles en la nueva area, se pone id
				// null para que genere el nuevo
				newRol.setIdArea(idAreaDestino);
				// rol.setIdRol(null);
				newRol.setActivo(rol.getActivo());
				newRol.setAtributos(rol.getAtributos());
				newRol.setDescripcion(rol.getDescripcion());
				newRol.setIdAreaLim(rol.getIdAreaLim());
				newRol.setTipo(rol.getTipo());
				try {
					mngrRol.save(newRol);
				} catch (Exception e1) {
					log.error(e1.getLocalizedMessage());
				}

				// GUARDAR LOS PERMISOS PARA CADA ROL DEL AREA
				// TEMPLATE PERO EN LA NUEVA AREA
				try {

					savePermisosRol(newRol, idAreaOrigen, idRolTemplate);

				} catch (Exception e) {

					throw e;
				}
			}

			return null;
		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveFoliosAsuntosThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveFoliosAsuntosThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {
			// Valida si para el folio asunto y folio respuesta de la foliadora default se
			// usará el dato del template o si usara el configurado defaul
			// Estos datos estan en la tabla parametrosapp con idSeccion SIGAPTEMPLATE
			Boolean usaPlantilla = Boolean.parseBoolean(getParamApp("SIGAPTEMPLATE", "USESIGAPTEMPLATE"));

			if (usaPlantilla) {
				// traemos el folio inicial
				List<Folio> lst = new ArrayList<>();
				Folio inicial = new Folio();
				List<Criterion> restrictions = new ArrayList<>();
				restrictions.add(Restrictions.eq("folioKey.idArea", idAreaOrigen));

				lst = (List<Folio>) mngrFolio.search(restrictions);
				if (lst != null && lst.size() != 1) {
					log.error("El folio inicial es distinto de 1");
					// throw new Exception("El folio inicial es distinto de 1");
				} else {
					inicial = lst.get(0);
				}

				// guardar los folios de documento de area nueva usando el folio
				// inicial
				// tipoFolio 0 = asunto
				// tipoFolio 1 = respuesta
				// tipoFolio 2 = customimss??
				for (int i = 0; i <= 2; i++) {
					FolioArea fa = new FolioArea();
					fa.setFolioAreaKey(new FolioAreaKey(idAreaDestino, i));
					fa.setFolio(inicial.getFolioKey().getFolio());

					try {
						mngrFolioArea.save(fa);
					} catch (Exception e) {

						throw e;
					}
				}
			} else {
				// Obtiene el valor defaul que se asignara al folio asunto y folio respuesta de
				// la foliadora para las nuevas areas
				Integer folioDefault = Integer.parseInt(getParamApp("SIGAPTEMPLATE", "FOLIODEFAULT"));

				// guardar los folios de documento de area nueva usando el folio default
				// tipoFolio 0 = asunto
				// tipoFolio 1 = respuesta
				// tipoFolio 2 = customimss??
				for (int i = 0; i <= 2; i++) {
					FolioArea fa = new FolioArea();
					fa.setFolioAreaKey(new FolioAreaKey(idAreaDestino, i));
					fa.setFolio(folioDefault);

					try {
						mngrFolioArea.save(fa);
					} catch (Exception e) {

						throw e;
					}
				}
			}
			return null;
		}
	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveCatalogosThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveCatalogosThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			try {

				if (idAreaOrigen != null) {
					saveCat(idAreaOrigen, idAreaDestino, Tema.class, mngrTema, "idTema");
					saveCat(idAreaOrigen, idAreaDestino, TipoDocumento.class, mngrTipoDocumento, "idTipoDocumento");
					saveCat(idAreaOrigen, idAreaDestino, TipoEvento.class, mngrTipoEvento, "idEvento");
					saveCat(idAreaOrigen, idAreaDestino, TipoInstruccion.class, mngrTipoInstruccion, "idInstruccion");
					saveCat(idAreaOrigen, idAreaDestino, TipoPrioridad.class, mngrTipoPrioridad, "idPrioridad");

					// cuando es expediente se consulta el folder por default
					String idExpDefault = getExpedienteDefault(idAreaOrigen);
					saveCat(idAreaOrigen, idAreaDestino, TipoExpediente.class, mngrTipoExpediente, "idExpediente",
							idExpDefault);

				} else {

					TipoExpediente tipoExpediente = new TipoExpediente();
					Area area = mngrArea.fetch(idAreaDestino);
					tipoExpediente.setArea(area);
					tipoExpediente.setActivo(true);
					String descripcion = environment.getProperty("folderNameExpedienteDefault");
					tipoExpediente.setDescripcion(descripcion);

					mngrTipoExpediente.save(tipoExpediente);

					saveExpedienteDefautl(idAreaDestino, tipoExpediente.getIdExpediente());

				}

			} catch (Exception e) {

				throw e;
			}

			return null;
		}

		/**
		 * Save cat.
		 *
		 * @param <T>           the generic type
		 * @param idAreaOrigen  the id area origen
		 * @param idAreaDestino the id area destino
		 * @param clazz         the clazz
		 * @param mngr          the mngr
		 * @param idProperty    the id property
		 * @throws Exception the exception
		 */
		private <T> void saveCat(Integer idAreaOrigen, Integer idAreaDestino, Class<T> clazz, EntityManager<T> mngr,
				String idProperty) throws Exception {
			saveCat(idAreaOrigen, idAreaDestino, clazz, mngr, idProperty, null);
		}

		/**
		 * Save cat.
		 *
		 * @param <T>           the generic type
		 * @param idAreaOrigen  the id area origen
		 * @param idAreaDestino the id area destino
		 * @param clazz         the clazz
		 * @param mngr          the mngr
		 * @param idProperty    the id property
		 * @param idExpDefault  the id exp default
		 * @throws Exception the exception
		 */
		private <T> void saveCat(Integer idAreaOrigen, Integer idAreaDestino, Class<T> clazz, EntityManager<T> mngr,
				String idProperty, String idExpDefault) throws Exception {
			//
			List<T> items = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("area.idArea", idAreaOrigen));
			restrictions.add(Restrictions.eq("activo", true));

			items = (List<T>) mngr.search(restrictions);
			boolean savedDefaultExp = false;

			for (T t : items) {

				T newT = (T) SerializationHelper.clone((Serializable) t);
				/*
				 * antes de poner el id en null para insertar, recuperamos el valor del
				 * expediente para saber si es el expediente default y si es guardamos la
				 * bandera para replicar el default del area
				 */
				boolean defaultExp = false;
				if (t instanceof TipoExpediente) {
					if (idExpDefault != null && idExpDefault.equals(((TipoExpediente) newT).getIdExpediente())) {
						defaultExp = true;
					}
				}

				// set id catalogo y area
				PropertyUtils.setProperty(newT, idProperty, null);
				if ((newT instanceof TipoInstruccion) || (newT instanceof TipoPrioridad)) {
					AreaAuxiliar areaAux = new AreaAuxiliar();
					areaAux.setIdArea(idAreaDestino);
					PropertyUtils.setNestedProperty(newT, "area", areaAux);
				} else {
					PropertyUtils.setNestedProperty(newT, "area.idArea", idAreaDestino);
				}
				mngr.save(newT);

				// si el objeto es tipoExpediente replicar el parametro de
				// expediente default para esa area
				if (newT instanceof TipoExpediente) {
					if (defaultExp) {
						saveExpedienteDefautl(idAreaDestino, ((TipoExpediente) newT).getIdExpediente());
						savedDefaultExp = true;
					}
				}
			}

			if (!savedDefaultExp && idExpDefault != null) {
				saveExpedienteDefautl(idAreaDestino, idExpDefault);
			}
		}
	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveAreasPromotoresThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveAreasPromotoresThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			// obtener areas promotores origen
			List<AreaPromotor> items = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("areaPromotorKey.idArea", idAreaOrigen));

			items = (List<AreaPromotor>) mngrAreaPromotor.search(restrictions);

			for (AreaPromotor ap : items) {
				// replicar registros para el area destino
				AreaPromotor areaProm = new AreaPromotor();
				areaProm.setAreaPromotorKey(new AreaPromotorKey());
				areaProm.getAreaPromotorKey().setIdArea(idAreaDestino);
				areaProm.getAreaPromotorKey().setInstitucion(ap.getAreaPromotorKey().getInstitucion());

				try {
					mngrAreaPromotor.save(areaProm);
				} catch (Exception e) {

					throw e;
				}
			}

			return null;

		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveAreasRemitentesThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveAreasRemitentesThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			// obtener areas remitentes origen
			List<AreaRemitente> items = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("areaRemitenteKey.idArea", idAreaOrigen));

			items = (List<AreaRemitente>) mngrAreaRemitente.search(restrictions);

			for (AreaRemitente ar : items) {
				// replicar registros para el area destino
				AreaRemitente areaRem = new AreaRemitente();
				areaRem.setAreaRemitenteKey(new AreaRemitenteKey());
				areaRem.getAreaRemitenteKey().setIdArea(idAreaDestino);
				areaRem.getAreaRemitenteKey().setIdInstitucion(ar.getAreaRemitenteKey().getIdInstitucion());
				areaRem.getAreaRemitenteKey().setAreaRemitente(ar.getAreaRemitenteKey().getAreaRemitente());

				try {
					mngrAreaRemitente.save(areaRem);
				} catch (Exception e) {

					throw e;
				}
			}

			return null;

		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveParametrosContentThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveParametrosContentThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			// obtener parametros origen
			List<Parametro> items = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("parametroKey.idArea", idAreaOrigen));
			restrictions.add(Restrictions.eq("parametroKey.idSeccion", "CONTENTFOLDER"));

			items = (List<Parametro>) mngrParametro.search(restrictions);

			for (Parametro p : items) {
				// replicar registros para el area destino

				Parametro par = new Parametro();
				par.setParametroKey(new ParametroKey());
				par.getParametroKey().setIdArea(idAreaDestino);
				par.getParametroKey().setIdClave(p.getParametroKey().getIdClave());
				par.getParametroKey().setIdSeccion(p.getParametroKey().getIdSeccion());
				par.setValor(null);

				try {
					mngrParametro.save(par);
				} catch (Exception e) {

					throw e;
				}
			}

			return null;
		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveParametrosWSThread implements Callable<Object> {

		private Boolean isUpdate;
		private Integer idAreaDestino;

		public SaveParametrosWSThread(Integer idAreaDestino, Boolean isUpdate) {
			this.isUpdate = isUpdate;
			this.idAreaDestino = idAreaDestino;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			Parametro turnoExt = new Parametro();
			turnoExt.setParametroKey(new ParametroKey());
			turnoExt.getParametroKey().setIdArea(idAreaDestino);
			turnoExt.getParametroKey().setIdSeccion("WEBSERVICES");
			turnoExt.getParametroKey().setIdClave("TURNOSEXTERNOS");
			turnoExt.setValor(isUpdate ? "1" : "0");// cuando es nueva queda en
			// cero

			Parametro turnoExtI = new Parametro();
			turnoExtI.setParametroKey(new ParametroKey());
			turnoExtI.getParametroKey().setIdArea(idAreaDestino);
			turnoExtI.getParametroKey().setIdSeccion("WEBSERVICES");
			turnoExtI.getParametroKey().setIdClave("TURNOSEXTERNOSI");
			turnoExtI.setValor(isUpdate ? "1" : "0");// cuando es nueva queda en
			// cero

			if (isUpdate) {
				Parametro turnoExtUPDATE = new Parametro();
				Parametro turnoExtIUPDATE = new Parametro();
				turnoExtUPDATE.setParametroKey(turnoExt.getParametroKey());
				turnoExtUPDATE.setValor(turnoExt.getValor());

				turnoExtIUPDATE.setParametroKey(turnoExtI.getParametroKey());
				turnoExtIUPDATE.setValor(turnoExtI.getValor());

				mngrParametro.update(turnoExtUPDATE);
				mngrParametro.update(turnoExtIUPDATE);
			} else {
				try {
					mngrParametro.save(turnoExt);
					mngrParametro.save(turnoExtI);
				} catch (Exception e) {

					throw e;
				}
			}

			return null;

		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveParametrosFoliosDocThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveParametrosFoliosDocThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			// obtener parametros origen
			List<Parametro> items = new ArrayList<>();
			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("parametroKey.idArea", idAreaOrigen));
			restrictions.add(Restrictions.eq("parametroKey.idSeccion", "FOLIODOC"));

			items = (List<Parametro>) mngrParametro.search(restrictions);

			for (Parametro p : items) {
				// replicar registros para el area destino

				Parametro parametro = new Parametro();
				parametro.setParametroKey(new ParametroKey());
				parametro.getParametroKey().setIdArea(idAreaDestino);
				parametro.getParametroKey().setIdClave(p.getParametroKey().getIdClave());
				parametro.getParametroKey().setIdSeccion(p.getParametroKey().getIdSeccion());
				parametro.setValor(p.getValor());

				try {
					mngrParametro.save(parametro);
				} catch (Exception e) {

					throw e;
				}
			}

			return null;

		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveFoliosPorAreaThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveFoliosPorAreaThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			HashMap<String, Object> params = new HashMap<>();

			params.put("idAreaDestino", idAreaDestino);
			params.put("idAreaOrigen", idAreaOrigen);

			// Integer result =
			// mngrFolio.execUpdateQuery("foliosPorArea", params);

			if (dbVendor == DBVendor.POSTGRESQL)
				mngrFolioClave.uniqueResult("foliosPorArea2", params);
			else
				mngrFolioClave.execUpdateQuery("foliosPorArea", params);

			return null;
		}

	}

	/**
	 * @author adan quintero
	 * @version 1.0
	 */
	public class SaveFoliosClavePorAreaThread implements Callable<Object> {

		private Integer idAreaOrigen;
		private Integer idAreaDestino;

		public SaveFoliosClavePorAreaThread(Integer idAreaOrigen, Integer idAreaDestino) {
			this.idAreaDestino = idAreaDestino;
			this.idAreaOrigen = idAreaOrigen;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			HashMap<String, Object> params = new HashMap<>();

			params.put("idAreaDestino", idAreaDestino);
			params.put("idAreaOrigen", idAreaOrigen);

			// // Integer result =
			if (dbVendor == DBVendor.POSTGRESQL)
				mngrFolioClave.uniqueResult("foliosClavePorArea2", params);
			else
				mngrFolioClave.execUpdateQuery("foliosClavePorArea", params);

			return null;
		}

	}

	/**
	 * @author alfredo morales
	 * @version 1.0
	 */
	public class SaveFoldersAreaThread implements Callable<Object> {

		private Area item;

		public SaveFoldersAreaThread(Area area) {
			this.item = area;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			String idArea = item.getIdArea().toString();

			// folder de area
			String nombreFolder = getNombreFolder(item);
			IEndpoint superUser = EndpointDispatcher.getInstance();

			String startFolder = getParamApp("CABINET");
			String folderIdArea = null;
			try {
				folderIdArea = superUser.createFolder(//
						startFolder, //
						environment.getProperty("folderTypeArea"), //
						nombreFolder);
			} catch (Exception e) {
				log.error("ERROR creando folder del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			String nombreGrupo = (environment.getProperty("grpSigap") + idArea);
			try {
				// crear grupos
				superUser.createGroup(nombreGrupo, "");
			} catch (Exception e) {
				rollbackFolderObjectId = folderIdArea;
				log.error("ERROR creando grupo del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			String nombreGrupoConf = (environment.getProperty("grpSigapConf") + idArea);
			try {
				superUser.createGroup(nombreGrupoConf, "");
			} catch (Exception e) {
				rollbackFolderObjectId = folderIdArea;
				rollbackNameGrupoArea = nombreGrupo;
				log.error("ERROR creando grupo confidencial del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			String newAclName = null;
			try {
				// SET ACL
				Map<String, String> additionalData = new HashMap<>();
				additionalData.put("idArea", idArea);
				superUser.setACL(folderIdArea, environment.getProperty("aclNameFolderArea"), additionalData);

				// obtener el nombre del acl recien creado
				String aclName = ((List<String>) superUser.getObjectProperty(folderIdArea, "acl_name")).get(0);

				// renombrar con el estanda de acls de area
				newAclName = environment.getProperty("aclSigapName") + item.getIdArea();
				superUser.renameAcl(aclName, newAclName);
			} catch (Exception e) {
				rollbackFolderObjectId = folderIdArea;
				rollbackNameGrupoArea = nombreGrupo;
				rollbackNameGrupoAreaConf = nombreGrupoConf;
				log.error("ERROR creando acl del area >>> " + e.getLocalizedMessage());

				throw e;
			}

			try {
				// set properties de area
				Map<String, Object> properties = new HashMap<>();

				properties.put("acl_name", newAclName);
				properties.put("idarea", String.valueOf(item.getIdArea()));
				superUser.setProperties(folderIdArea, properties);
			} catch (Exception e) {
				rollbackFolderObjectId = folderIdArea;
				rollbackNameGrupoArea = nombreGrupo;
				rollbackNameGrupoAreaConf = nombreGrupoConf;
				rollbackAclName = newAclName;
				log.error("ERROR seteando acl al folder del area >>> " + e.getLocalizedMessage());

				throw e;
			}
			item.setContentId(folderIdArea);
			mngrArea.update(item);

			// verificar id del folder
			if (StringUtils.isNotBlank(item.getContentId())) {
				try {
					// crear los folders de parametros del area nueva
					saveFoldersParametros(item);
				} catch (Exception e) {
					rollbackFolderObjectId = folderIdArea;
					rollbackNameGrupoArea = nombreGrupo;
					rollbackNameGrupoAreaConf = nombreGrupoConf;
					rollbackAclName = newAclName;
					log.error("ERROR creando folder de parametros del area >>> " + e.getLocalizedMessage());

					throw e;
				}
				try {
					// crear folders de expedientes
					saveFoldersExpedientes(item);
				} catch (Exception e) {
					rollbackFolderObjectId = folderIdArea;
					rollbackNameGrupoArea = nombreGrupo;
					rollbackNameGrupoAreaConf = nombreGrupoConf;
					rollbackAclName = newAclName;
					log.error("ERROR creando folder de expedientes del area  >>> " + e.getLocalizedMessage());

					throw e;
				}

				// crear folders de plantillas de area
				// saveFoldersPlantillas(superUser, item);
			}

			return null;

		}

	}

	private void saveParametrosWS(Integer idAreaDestino, boolean isUpdate) {

		Parametro turnoExt = new Parametro();
		turnoExt.setParametroKey(new ParametroKey());
		turnoExt.getParametroKey().setIdArea(idAreaDestino);
		turnoExt.getParametroKey().setIdSeccion("WEBSERVICES");
		turnoExt.getParametroKey().setIdClave("TURNOSEXTERNOS");
		turnoExt.setValor(isUpdate ? "1" : "0");// cuando es nueva queda en
		// cero

		Parametro turnoExtI = new Parametro();
		turnoExtI.setParametroKey(new ParametroKey());
		turnoExtI.getParametroKey().setIdArea(idAreaDestino);
		turnoExtI.getParametroKey().setIdSeccion("WEBSERVICES");
		turnoExtI.getParametroKey().setIdClave("TURNOSEXTERNOSI");
		turnoExtI.setValor(isUpdate ? "1" : "0");// cuando es nueva queda en
		// cero

		if (isUpdate) {
			Parametro turnoExtUPDATE = new Parametro();
			Parametro turnoExtIUPDATE = new Parametro();
			turnoExtUPDATE.setParametroKey(turnoExt.getParametroKey());
			turnoExtUPDATE.setValor(turnoExt.getValor());

			turnoExtIUPDATE.setParametroKey(turnoExtI.getParametroKey());
			turnoExtIUPDATE.setValor(turnoExtI.getValor());

			mngrParametro.update(turnoExtUPDATE);
			mngrParametro.update(turnoExtIUPDATE);
		} else {
			try {
				mngrParametro.save(turnoExt);
				mngrParametro.save(turnoExtI);
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Rollback repo.
	 *
	 * @throws Exception the exception
	 */
	private void rollbackRepo() throws Exception {

		if (StringUtils.isNotBlank(rollbackFolderObjectId)) {

			IEndpoint endpoint = EndpointDispatcher.getInstance();
			String objectIdFolder = rollbackFolderObjectId;

			log.debug("Eliminando el folder con objectid " + objectIdFolder);

			// permisos para eliminar
			boolean addPermisos = endpoint.addPermisos(objectIdFolder, environment.getProperty("aclRollbackFolderArea"),
					null);
			// agregando permisos para poder eliminar subFolder
			if (!rollbackSubFolderObjectId.isEmpty()) {
				for (String objectIdSubfolder : rollbackSubFolderObjectId) {
					endpoint.addPermisos(objectIdSubfolder, environment.getProperty("aclRollbackFolderArea"), null);
				}
			}

			if (addPermisos) {
				boolean eliminarFolder = false;
				try {
					eliminarFolder = endpoint.eliminarFolderRecursivo(objectIdFolder);
				} catch (Exception ex1) {
					log.error(ex1.getLocalizedMessage());
				}
				if (eliminarFolder) {

					log.debug("Folder eliminado.. !!");
					if (StringUtils.isNotBlank(rollbackNameGrupoArea)) {
						rollbackGrupoArea(rollbackNameGrupoArea);
					}
					if (StringUtils.isNotBlank(rollbackNameGrupoAreaConf)) {
						rollbackGrupoArea(rollbackNameGrupoAreaConf);
					}
					if (StringUtils.isNotBlank(rollbackAclName)) {
						rollbackEliminarAcl(rollbackAclName);
					}

				} else {

					log.error("NO SE PUDO ELIMINAR EL FOLDER DEL AREA :: " + objectIdFolder);

				}

			} else {

				log.warn("NO SE PUDIERON AGREGAR PERMISOS AL FOLDER PARA PODERLO ELIMINAR...");

			}
		}
	}

	/**
	 * Rollback grupo area.
	 *
	 * @param nombreGrupo the nombre grupo
	 */
	private void rollbackGrupoArea(String nombreGrupo) {
		IEndpoint endpoint = EndpointDispatcher.getInstance();
		try {
			boolean eliminarGrupo = endpoint.eliminarGroup(nombreGrupo);
			if (eliminarGrupo) {
				log.debug("Grupo eliminado.. !!");
			} else {
				log.error("NO SE PUDO ELIMINAR EL GRUPO DEL AREA :: " + nombreGrupo);
			}
		} catch (Exception ex) {
			log.error("NO SE PUDO ELIMINAR EL GRUPO DEL AREA :: " + nombreGrupo);

		}

	}

	/**
	 * Rollback eliminar acl.
	 *
	 * @param nombreAcl the nombre acl
	 */
	private void rollbackEliminarAcl(String nombreAcl) {
		IEndpoint endpoint = EndpointDispatcher.getInstance();
		try {
			boolean eliminarAcl = endpoint.eliminarAcl(nombreAcl);
			if (eliminarAcl) {
				log.debug("Acl eliminado.. !!");
			} else {
				log.error("NO SE PUDO ELIMINAR EL ACL DEL AREA :: " + nombreAcl);
			}
		} catch (Exception ex) {
			log.error("NO SE PUDO ELIMINAR EL ACL DEL AREA :: " + nombreAcl);

		}
	}

	/**
	 * Gets the nombre folder.
	 *
	 * @param item the item
	 * @return the nombre folder
	 */
	private String getNombreFolder(Area item) {
		// folder de area
		StringBuilder nombreFolder = new StringBuilder();
		nombreFolder.append(item.getInstitucion().getIdInstitucion()).append("_").append(item.getDescripcion())
				.append("_").append(item.getIdArea());

		return nombreFolder.toString();
	}

	/**
	 * Save folders plantillas.
	 *
	 * @param endpoint the endpoint
	 * @param item     the item
	 * @return the string
	 * @throws Exception the exception
	 */
	protected String saveFoldersPlantillas(IEndpoint endpoint, Area item) throws Exception {

		String tipoFolder = environment.getProperty("docTypeFolderPlantillas");

		String objectId = endpoint.createFolderIntoId(item.getContentId(), tipoFolder,
				environment.getProperty("folderNamePlantillas"));

		String aclName = environment.getProperty("aclNameFolderPlantillas");

		Map<String, String> additionalData = new HashMap<String, String>();

		additionalData.put("idArea", item.getIdArea().toString());

		endpoint.setACL(objectId, aclName, additionalData);

		log.debug("Nuevo folder para plantillas creado con id :: " + objectId);

		return objectId;

	}

	/**
	 * Crea los folders de los expedientes en el repositorio.
	 *
	 * @param area Area a la cual se le van a generar los folders
	 * @throws Exception Cualquier error en el repositorio
	 */
	private void saveFoldersExpedientes(Area area) throws Exception {
		List<TipoExpediente> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("area.idArea", area.getIdArea()));

		items = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);

		for (TipoExpediente te : items) {
			// crear los subfolders y actualizar registro con el contentId
			String idFolderParam = tipoExpedienteController.createExpediente(te);
			// createSubfolderArea(folderIdArea, te.getDescripcion(),
			// additionalData);

			te.setContentId(idFolderParam);

			rollbackSubFolderObjectId.add(idFolderParam);
			mngrTipoExpediente.update(te);
		}
	}

	/**
	 * Save folders parametros.
	 *
	 * @param area the area
	 * @throws Exception the exception
	 */
	private void saveFoldersParametros(Area area) throws Exception {
		List<Parametro> itemsParam = new ArrayList<Parametro>();
		List<Criterion> restrictionsParam = new ArrayList<>();
		restrictionsParam.add(Restrictions.eq("parametroKey.idArea", area.getIdArea()));
		restrictionsParam.add(Restrictions.eq("parametroKey.idSeccion", "CONTENTFOLDER"));

		itemsParam = (List<Parametro>) mngrParametro.search(restrictionsParam);

		for (Parametro p : itemsParam) {

			// crear los subfolders y actualizar registro con el contentId
			TipoExpediente newExp = new TipoExpediente();
			newExp.setArea(area);
			newExp.setDescripcion(p.getParametroKey().getIdClave());

			String idFolderParam = tipoExpedienteController.createExpediente(newExp);
			rollbackSubFolderObjectId.add(idFolderParam);

			p.setValor(idFolderParam);

			mngrParametro.update(p);
		}

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

	/**
	 * Gets the expediente default.
	 *
	 * @param idArea the id area
	 * @return the expediente default
	 * @throws Exception the exception
	 */
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
	 * Gets the roles.
	 *
	 * @param idArea the id area
	 * @return the roles
	 */
	private List<Rol> getRoles(Integer idArea) {
		List<Rol> lst = new ArrayList<Rol>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("idArea", idArea));

		lst = (List<Rol>) mngrRol.search(restrictions);

		return lst;
	}

	/**
	 * Existe misma area misma area padre.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	private boolean existeMismaAreaMismaAreaPadre(Area area) {

		boolean result;
		// verifica si el nombre y el idAreaPadre se repiten
		List<Criterion> restrictions = new ArrayList<>();

		if (null != area.getIdArea())
			restrictions.add(Restrictions.ne("idArea", area.getIdArea()));

		restrictions.add(
				EscapedLikeRestrictions.ilike("descripcion", area.getDescripcion().toLowerCase(), MatchMode.EXACT));
		restrictions.add(Restrictions.eq("idAreaPadre", area.getIdAreaPadre()));

//		restrictions.add(Restrictions.eq("descripcion", area.getDescripcion()));
//		restrictions.add(Restrictions.ne("idArea", area.getIdArea()));
//		if (null != area.getIdAreaPadre())
//			restrictions.add(Restrictions.eq("idAreaPadre", area.getIdAreaPadre()));

		List<Area> lst = (List<Area>) mngrArea.search(restrictions);

		if (null != area.getIdAreaPadre()) {
			result = (lst != null && !lst.isEmpty()) ? true : false;
		} else {
			Optional<Area> optArea = lst.stream()
					.filter(a -> Normalizer.normalize(area.getDescripcion(), Normalizer.Form.NFD)
							.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
							.equalsIgnoreCase(Normalizer.normalize(a.getDescripcion(), Normalizer.Form.NFD)
									.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""))
							&& area.getInstitucion().getIdInstitucion().equals(a.getInstitucion().getIdInstitucion()))
					.findFirst();
			result = (optArea.isPresent()) ? true : false;
		}

		return result;
	}

	/**
	 * Gets the path.
	 *
	 * @param id the id
	 * @return the path
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene estructura area", notes = "Obtiene la estructura del area seleccionada")
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

	@RequestMapping(value = "/area/path", method = RequestMethod.GET)
	public ResponseEntity<List<?>> getPath(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("ID AREA  >> " + id);
		List<Area> path = new ArrayList<>();
		List<Area> pathOrdenado = new ArrayList<>();
		try {
			if (id != null) {
				Area hija = mngrArea.fetch(Integer.valueOf((String) id));

				if (hija != null) {

					String rutaId = hija.getRutaId();
					log.debug("RUTA ID: " + rutaId);

					if (rutaId != null) {
						String[] pathStr = hija.getRutaId().split("/");
						List<Integer> pathInt = new ArrayList<Integer>();

						for (String ids : pathStr) {
							if (StringUtils.isNotBlank(ids)) {
								pathInt.add(new Integer(ids));
							}
						}

						List<Criterion> restrictions = new ArrayList<Criterion>();
						restrictions.add(Restrictions.in("idArea", pathInt));

						List<Order> orders = new ArrayList<Order>();
						// orders.add(Order.asc("idArea"));

						path = (List<Area>) mngrArea.search(restrictions, orders);

						// Ordenando el resultado para devolverlo segun rutaId
						for (Integer ordenIds : pathInt) {
							pathOrdenado.add(
									path.stream().filter(a -> ordenIds.equals(a.getIdArea())).findAny().orElse(null));
						}
					}
				}
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<List<?>>(pathOrdenado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<?>>(pathOrdenado, HttpStatus.OK);
	}

	/**
	 * Validar referencia circular.
	 *
	 * @param area     the area
	 * @param areaBody the area body
	 * @return true, if successful
	 */
	private boolean validarReferenciaCircular(Area area, Area areaBody) {
		boolean s = true;
		if (areaBody.getIdArea().intValue() != areaBody.getAreaPadre().getIdArea().intValue()) {
			String rutaId = area.getRutaId();
			log.debug("RUTA ID: " + rutaId);
			if (rutaId != null) {
				String[] pathStr = area.getRutaId().split("/");
				Collections.reverse(Arrays.asList(pathStr));
				List<Integer> pathInt = new ArrayList<Integer>();
				for (String ids : pathStr) {
					if (StringUtils.isNotBlank(ids)) {
						pathInt.add(new Integer(ids));
					}
				}
				for (Integer temp : pathInt) {
					if (areaBody.getIdArea().intValue() == temp.intValue()) {
						s = false;
						log.error("El Area: " + area.getIdArea() + " no puede moverse al area destino "
								+ area.getAreaPadre().getIdArea() + " ya que crearía una referencia circular");
						break; // se rompe el look para que la comparacion
						// sea hasta el area buscada
					}
				}
			}

		}
		return s;
	}

	/**
	 * Mover.
	 *
	 * @param area the area
	 * @return the response entity
	 * @throws Exception the exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Mover area", notes = "Mueve un area a otra area de destino")
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

	@RequestMapping(value = "/area/mover", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Area> mover(@RequestBody(required = true) Area area) throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("AREA A MOVER >> " + area);

				if (validarArea(area)) {
					if (validarReferenciaCircular(areaPadre, area)) {
						areaOriginal.setIdAreaPadre(area.getAreaPadre().getIdArea());
						mngrArea.update(areaOriginal);
						log.debug("El Area: " + area.getIdArea() + "  fue movida al area:  "
								+ area.getAreaPadre().getIdArea());
						return new ResponseEntity<Area>(areaOriginal, HttpStatus.OK);
					} else {
						return new ResponseEntity<Area>(areaOriginal, HttpStatus.CONFLICT);
					}
				}

				return new ResponseEntity<Area>(area, HttpStatus.CONFLICT);
			} else {
				return new ResponseEntity<Area>(area, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * 
	 * @param area
	 * @return
	 */
	public boolean validarClaveDepSiglasClave(Area area) {
		boolean isValid = true;
		List<Area> lst = new ArrayList<Area>();
		List<Criterion> restrictions = new ArrayList<Criterion>();

		if (null != area.getIdArea())
			restrictions.add(Restrictions.ne("idArea", area.getIdArea()));

		restrictions.add(Restrictions.or(
				EscapedLikeRestrictions.ilike("claveDepartamental", area.getClaveDepartamental().toLowerCase(),
						MatchMode.EXACT),
				EscapedLikeRestrictions.ilike("clave", area.getClave().toLowerCase(), MatchMode.EXACT),
				EscapedLikeRestrictions.ilike("siglas", area.getSiglas().toLowerCase(), MatchMode.EXACT)));

		// restrictions.add(Restrictions.or(EscapedLikeRestrictions.ilike("claveDepartamental",
		// area.getClaveDepartamental(),
		// MatchMode.ANYWHERE), EscapedLikeRestrictions.ilike("siglas",
		// area.getSiglas(), MatchMode.ANYWHERE)));

		// restrictions.add(EscapedLikeRestrictions.ilike("claveDepartamental",
		// area.getClaveDepartamental(),MatchMode.ANYWHERE));
		// restrictions.add(EscapedLikeRestrictions.ilike("siglas", area.getSiglas(),
		// MatchMode.ANYWHERE));
		lst = (List<Area>) mngrArea.search(restrictions);
		if (lst != null && lst.size() > 0) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Validar area.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	private boolean validarArea(Area area) {
		boolean s = false;
		if (validarNulos(area))
			if (validarAreaOriginal(area.getIdArea()))
				if (validarAreaPadre(area))
					if (validarAreasIguales(area))
						s = true;
		return s;
	}

	/**
	 * Validar nulos.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	private boolean validarNulos(Area area) {
		boolean s = true;
		if (area.getIdArea() == null || area.getAreaPadre().getIdArea() == null) {
			log.error("El idArea de entrada y el idAreaPadre no pueden ser nulos");
			s = false;
		}
		return s;
	}

	/**
	 * Validar area original.
	 *
	 * @param idArea the id area
	 * @return true, if successful
	 */
	private boolean validarAreaOriginal(Integer idArea) {
		boolean s = true;
		areaOriginal = mngrArea.fetch(idArea);
		if (areaOriginal == null) {
			log.error("El Area " + idArea + " no existe");
			s = false;
		}
		return s;
	}

	/**
	 * Validar area padre.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	private boolean validarAreaPadre(Area area) {
		boolean s = true;
		areaPadre = mngrArea.fetch(area.getAreaPadre().getIdArea());
		if (areaPadre == null) {
			log.error("El Area padre" + area.getAreaPadre().getIdAreaPadre() + " no existe");
			s = false;
		}

		boolean valida = validarAreasHijas(area);

		return s && valida;
	}

	/**
	 * Validar areas iguales.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	private boolean validarAreasIguales(Area area) {
		boolean s = true;
		if (areaOriginal.getAreaPadre() != null)
			if (area.getAreaPadre().getIdArea().intValue() == areaOriginal.getAreaPadre().getIdArea().intValue()) {
				log.error("El Area : " + area.getIdArea() + " ya tiene como padre al area:  "
						+ area.getAreaPadre().getIdArea());
				s = false;
			}
		return s;
	}

	/**
	 * Validar areas hijas.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	private boolean validarAreasHijas(Area area) {
		boolean s = true;
		List<Area> hijas = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("idAreaPadre", area.getAreaPadre().getIdArea()));
		restrictions.add(Restrictions.eq("descripcion", StringUtils.trim(area.getDescripcion())));

		hijas = (List<Area>) mngrArea.search(restrictions);
		if (hijas != null && hijas.size() > 0) {
			s = false;
		}

		return s;
	}

	/**
	 * Validar caracteres especiales.
	 *
	 * @param cadena the cadena
	 * @return true, if successful
	 */
	private boolean validarCaracteresEspeciales(String cadena) {

		String caracteresInvalidos = environment.getProperty("invalid.caracter.nameArea");
		Pattern pattern = Pattern.compile(caracteresInvalidos);
		Matcher matcher = pattern.matcher(cadena);
		return matcher.find();
	}

}
