/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
import com.ecm.sigap.config.LicenciaUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.AccesoKey;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Configuracion;
import com.ecm.sigap.data.model.ConfiguracionKey;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.TipoNotificacion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.com.ecmsolutions.license.exception.LicenseException;
import mx.com.ecmsolutions.license.model.ECMLicencia;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Usuario}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class UsuarioController extends CustomRestController implements RESTController<Usuario> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(UsuarioController.class);

	/** The max active users. */
	// validacion de licencia
	private int MAX_ACTIVE_USERS;

	/**
	 * Referencia hacia el REST controller de {@link RolController}.
	 */
	@Autowired
	private RolController rolController;

	/**
	 * Referencia hacia el REST controller de {@link AccesoController}.
	 */
	@Autowired
	private AccesoController accesoController;

	@Autowired
	private DBVendor dbVendor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene usuario", notes = "Obtiene el detalle de un usuario")
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

	@RequestMapping(value = "/usuario", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Usuario> get(@RequestParam(value = "id", required = true) Serializable id) {

		Usuario item = null;
		try {

			item = mngrUsuario.fetch((String) id);

			if (item == null) {

				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions.ilike("idUsuario", id.toString()));

				List<?> lst = mngrUsuario.search(restrictions);

				if (!lst.isEmpty())
					item = (Usuario) lst.get(0);

			}

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Usuario>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta usuario", notes = "Consulta un usuario de la lista")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/usuario", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Usuario usuario) {

		List<Usuario> lst = new ArrayList<Usuario>();
		log.info("Parametros de busqueda :: " + usuario);
		try {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			// Se coloca para realizar consulta general asignando el id
			restrictions.add(Restrictions.isNotNull("idUsuario"));

			if ((usuario.getIdUsuario() != null) && (!usuario.getIdUsuario().isEmpty()))
				restrictions.add(Restrictions.eq("idUsuario", usuario.getIdUsuario()));

			if ((usuario.getEmail() != null) && (!usuario.getEmail().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("email", usuario.getEmail(), MatchMode.ANYWHERE));

			if ((usuario.getUserKey() != null) && (!usuario.getUserKey().isEmpty()))
				restrictions.add(Restrictions.eq("userKey", usuario.getUserKey()));

			if ((usuario.getApellidoPaterno() != null) && (!usuario.getApellidoPaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("apellidoPaterno", usuario.getApellidoPaterno(),
						MatchMode.ANYWHERE));

			if ((usuario.getMaterno() != null) && (!usuario.getMaterno().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("materno", usuario.getMaterno(), MatchMode.ANYWHERE));

			if ((usuario.getNombres() != null) && (!usuario.getNombres().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombres", usuario.getNombres(), MatchMode.ANYWHERE));

			if ((usuario.getCargo() != null) && (!usuario.getCargo().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("cargo", usuario.getCargo(), MatchMode.ANYWHERE));

			if (usuario.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", usuario.getActivo()));

			if (usuario.getCapacitado() != null)
				if (usuario.getCapacitado())
					restrictions.add(Restrictions.eq("capacitado", usuario.getCapacitado()));
				else
					restrictions.add(//
							Restrictions.or(//
									Restrictions.eq("capacitado", usuario.getCapacitado()),
									Restrictions.isNull("capacitado")));

			if (usuario.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", usuario.getTipo()));

			if (usuario.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", usuario.getIdArea()));

			if (usuario.getRol() != null) {
				if (usuario.getRol().getIdRol() != null)
					restrictions.add(Restrictions.eq("rol.idRol", usuario.getRol().getIdRol()));

				if (usuario.getRol().getIdAreaLim() != null && usuario.getRol().getIdAreaLim() > 0) {

					String queryName;
					if (DBVendor.POSTGRESQL == dbVendor)
						queryName = String.format(environment.getProperty("obtenerIdAreasHijos_PG"));
					else
						queryName = String.format(environment.getProperty("obtenerIdAreasHijos"));

					HashMap<String, Object> params = new HashMap<>();
					params.put("idArea", usuario.getRol().getIdAreaLim());
					List<?> idsAreas = mngrArea.execNativeQuery(queryName, params);
					restrictions.add(Restrictions.in("idAreaRepresentante",
							Stream.of((idsAreas.toString().replace("[", "").replace("]", "")).split(","))
									.map(elem -> !StringUtils.isNotBlank(elem) ? null : Integer.parseInt(elem))
									.collect(Collectors.toList())));
				}
			}
			if (null != usuario.getAreaAux()) {

				if (usuario.getAreaAux().getInstitucion() != null
						&& usuario.getAreaAux().getInstitucion().getIdInstitucion() != null) {
					restrictions.add(Restrictions.eq("areaAux.institucion.idInstitucion",
							usuario.getAreaAux().getInstitucion().getIdInstitucion()));
				}
				if (usuario.getAreaAux().getActivo() != null) {
					restrictions.add(Restrictions.eq("areaAux.activo", usuario.getAreaAux().getActivo()));
				}
			}

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.asc("idUsuario"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<Usuario>) mngrUsuario.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<Usuario>() {
				@Override
				public int compare(Usuario u1, Usuario u2) {
					return u1.getIdUsuario().compareTo(u2.getIdUsuario());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * Search by nombre completo.
	 *
	 * @param usuario the usuario
	 * @return the response entity
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta usuario nombre completo", notes = "Consulta un usuario por su nombre completo")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/usuario/nombreCompleto", method = RequestMethod.POST)
	public ResponseEntity<List<?>> searchByNombreCompleto(@RequestBody(required = true) Usuario usuario) {

		List<Representante> lstRep = new ArrayList<Representante>();
		List<Usuario> lstUser = new ArrayList<Usuario>();
		log.info("Parametros de busqueda :: " + usuario);

		try {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			// nota: se intento formula en usuario pero se tarda demasiado

			if (StringUtils.isNotBlank(usuario.getNombreCompleto()))
				restrictions.add(EscapedLikeRestrictions.ilike("nombreCompleto", usuario.getNombreCompleto(),
						MatchMode.ANYWHERE));

			if (usuario.getAreaAux() != null && usuario.getAreaAux().getIdArea() != null)
				restrictions.add(Restrictions.eq("area.idArea", usuario.getAreaAux().getIdArea()));

			if (usuario.getAreaAux() != null && usuario.getAreaAux().getInstitucion() != null
					&& usuario.getAreaAux().getInstitucion().getIdInstitucion() != null)
				restrictions.add(Restrictions.eq("area.institucion.idInstitucion",
						usuario.getAreaAux().getInstitucion().getIdInstitucion()));

			restrictions.add(Restrictions.eq("activosn", true));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("nombres"));

			lstRep = (List<Representante>) mngrRepresentante.search(restrictions);

			List<String> idsUsuario = new ArrayList<>();
			// sacar ids para buscar en usuarios
			for (Representante rep : lstRep) {
				idsUsuario.add(rep.getId());
			}
			if (idsUsuario.size() > 0) {

				restrictions.clear();
				restrictions.add(Restrictions.in("idUsuario", idsUsuario));
				restrictions.add(Restrictions.eq("activo", true));

				lstUser = (List<Usuario>) mngrUsuario.search(restrictions);
			}

			// rellenar nombre completo
			StringBuilder sb;
			for (Usuario user : lstUser) {
				sb = new StringBuilder();
				sb.append(user.getNombres());
				sb.append(" ");
				sb.append(user.getApellidoPaterno());

				if (StringUtils.isNotEmpty(user.getMaterno())) {
					sb.append(" ");
					sb.append(user.getMaterno());
				}

				user.setNombreCompleto(sb.toString());
			}

			log.debug("Size found >> " + lstUser.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lstUser, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar usuario", notes = "Agrega o edita un usuario de la lista")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/usuario", method = RequestMethod.PUT)
	public ResponseEntity<Usuario> save(@RequestBody Usuario usuario) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.info("USUARIO A GUARDAR :: " + usuario);

				if (usuario.getIdUsuario() != null && usuario.getIdArea() != null) {

					usuario.setIdUsuario(usuario.getIdUsuario().trim());

					usuario.setUserKey(usuario.getUserKey().trim());

					usuario.setIdAreaRepresentante(usuario.getIdArea());
					usuario.setIdTipoRepresentante("U");

					Usuario usuarioTemp = mngrUsuario.fetch(usuario.getIdUsuario());

					// USUARIO NUEVO
					if (usuarioTemp == null) {

						// validar numero de usuarios permitidos por la licencia
						if (limiteUsuariosAlcanzado()) {
							throw new LicenseException(
									"Se ha alcanzado el maximo de usuarios activos especificados en la licencia:"
											+ MAX_ACTIVE_USERS);
						}

						// validar que el user del repo no exista ya
						if (!validUserKey(usuario.getUserKey())) {
							throw new Exception("El usuario del repositorio ya existe: " + usuario.getUserKey());
						}

						// insertar en representantes
						// ya inserta representante por mapeo

						// insertar en usuarios
						mngrUsuario.save(usuario);

						// insertar configuraciones
						saveConfiguraciones(usuario);

						// insertar prefijo usuario(validar si aun se usa)

						// agregar al usuario a su correspondiente grupo de area
						boolean agregado = agregarGrupos(usuario);

						if (agregado) {
							return new ResponseEntity<Usuario>(usuario, HttpStatus.CREATED);

						} else {
							// si no se pudo agrega al grupo se pone como
							// inactivo
							usuario.setActivo(false);
							//mngrUsuario.update(usuario);
							mngrUsuario.inactivate(usuario);

							return new ResponseEntity<Usuario>(usuario, HttpStatus.PARTIAL_CONTENT);
						}

					} else {
						// UPDATE USUARIO
						boolean activar = true;
						boolean areaActualizada = true;
						boolean rolActualizado = true;

						// si quieren actualizar el usuario
						// validamos que sea un usuario valido
						activar = existeUsuarioRepo(usuario) ? true : false;

						// si quieren activar al usuario entonces verificamos si
						// existe y ya fue agregado al grupo
						if (usuarioTemp.getActivo() == false && usuario.getActivo() == true) {
							// validar numero de usuarios permitidos por la
							// licencia
							if (limiteUsuariosAlcanzado()) {
								activar = false;
								throw new LicenseException(
										"Se ha alcanzado el maximo de usuarios activos especificados en la licencia:"
												+ MAX_ACTIVE_USERS);
							}

							if (existeUsuarioRepo(usuario)) {
								agregarGrupos(usuario);
								// ahora ya se puede activar al usuario
								activar = true;
							} else {
								activar = false;
							}
						}

						// si el usuario cambio de area entonces lo cambiamos de
						// grupo
						if (usuarioTemp.getIdArea().intValue() != usuario.getIdArea().intValue()) {

							if (actualizaGrupoArea(usuarioTemp, usuario)) {
								areaActualizada = true;
							} else {
								areaActualizada = false;
							}

							// eliminar acceso del area que se le está
							// asignando.
							Acceso acceso = new Acceso();

							AccesoKey accesoKey = new AccesoKey();
							accesoKey.setArea(mngrArea.fetch(usuario.getIdArea()));
							accesoKey.setIdUsuario(usuario.getIdUsuario());
							acceso.setAccesoKey(accesoKey);
							List<Acceso> listAcceso = (List<Acceso>) accesoController.search(acceso).getBody();

							for (Acceso accesoResult : listAcceso) {

								// eliminar usuario de grupo del nuevo acceso
								// validar si el rol es confidencial para quitar el grupo conf

								mngrAcceso.delete(accesoResult);
							}
						}

						// si le actualizan el rol verifica si el nuevo tiene
						// permisos de ver confidenciales
						if (usuarioTemp.getRol().getIdRol().intValue() != usuario.getRol().getIdRol().intValue()) {
							if (actualizaGrupoRol(usuarioTemp, usuario)) {
								rolActualizado = true;
							} else {
								rolActualizado = false;
							}
						}

						// si pasa las validaciones de grupo deja activo al
						// usuario
						if (activar && areaActualizada && rolActualizado) {
							// se agrego para identificar especificamente cuando se inactiva usuario
							if (usuarioTemp.getActivo() == true && usuario.getActivo() == false)
								mngrUsuario.inactivate(usuario);
							else
								mngrUsuario.update(usuario);

							return new ResponseEntity<Usuario>(usuario, HttpStatus.CREATED);
						} else {
							usuario.setActivo(false);
							mngrUsuario.update(usuario);

							return new ResponseEntity<Usuario>(usuario, HttpStatus.PARTIAL_CONTENT);
						}

					}
				} else {
					return new ResponseEntity<Usuario>(usuario, HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Usuario>(usuario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Actualiza grupo rol.
	 *
	 * @param usuarioOld the usuario old
	 * @param usuarioNew the usuario new
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean actualizaGrupoRol(Usuario usuarioOld, Usuario usuarioNew) throws Exception {

		boolean oldHasConfidencial = rolController.hasPermisoConfidencial(usuarioOld.getRol());
		boolean newHasConfidencial = rolController.hasPermisoConfidencial(usuarioNew.getRol());
		boolean actualizado = true;
		// si el rol ya no puede ver confidenciales
		// entonces saca a los usuarios del grupo de confidenciales
		String grpSigapConf = environment.getProperty("grpSigapConf") + usuarioOld.getRol().getIdArea();
		if (oldHasConfidencial && !newHasConfidencial) {
			actualizado = removerGrupoUsuario(usuarioOld.getUserKey(), grpSigapConf);
		}

		// si el rol ya puede ver confidenciales
		// entonces agrego a los usuarios de ese rol al grupo
		if (!oldHasConfidencial && newHasConfidencial) {
			actualizado = agregarGrupoUsuario(usuarioNew.getUserKey(), grpSigapConf);
		}

		// si no hay cambio en los permisos de confidencial entonces no hace
		// operaciones de grupo

		return actualizado;
	}

	/**
	 * Actualiza grupo area.
	 *
	 * @param usuarioOld the usuario old
	 * @param usuarioNew the usuario new
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean actualizaGrupoArea(Usuario usuarioOld, Usuario usuarioNew) throws Exception {
		boolean removidos = removerGrupos(usuarioOld);

		// si fueron removidos con exito entonces se agregan a los nuevos grupos
		if (removidos) {
			boolean agregados = agregarGrupos(usuarioNew);
			// si fueron agregados con exito regresa true
			if (agregados) {
				return true;

			} else {
				// si no se pudo agregar al nuevo se regresa al grupo viejo
				agregarGrupos(usuarioOld);

				return false;
			}

		} else {
			return false;
		}
	}

	/**
	 * Agregar grupos.
	 *
	 * @param usuario the usuario
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public synchronized boolean agregarGrupos(Usuario usuario) throws Exception {

		String grpSigap = environment.getProperty("grpSigap") + usuario.getIdArea();

		log.info("agregando el user :: " + usuario.getIdUsuario() + " al grupo :: " + grpSigap);

		boolean agregado = agregarGrupoUsuario(usuario.getUserKey(), grpSigap);

		boolean agregadoConf = true;

		Rol rol = mngrRol.fetch(usuario.getRol().getIdRol());

		if (rolController.hasPermisoConfidencial(rol)) {
			String grpSigapConf = environment.getProperty("grpSigapConf") + usuario.getIdArea();
			agregadoConf = agregarGrupoUsuario(usuario.getUserKey(), grpSigapConf);
		}

		return agregado && agregadoConf;
	}

	/**
	 * Remover grupos.
	 *
	 * @param usuario the usuario
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private synchronized boolean removerGrupos(Usuario usuario) throws Exception {
		// remover grupo de area normal
		String grpSigap = environment.getProperty("grpSigap") + usuario.getIdArea();
		boolean removido = removerGrupoUsuario(usuario.getUserKey(), grpSigap);

		// remover grupo confidencial
		boolean removidoConf = true;
		String grpSigapConf = environment.getProperty("grpSigapConf") + usuario.getIdArea();
		if (rolController.hasPermisoConfidencial(usuario.getRol())) {
			removidoConf = removerGrupoUsuario(usuario.getUserKey(), grpSigapConf);
		}

		return removido && removidoConf;
	}

	/**
	 * Remover grupo usuario.
	 *
	 * @param userKey     the user key
	 * @param nombreGrupo the nombre grupo
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public synchronized boolean removerGrupoUsuario(String userKey, String nombreGrupo) throws Exception {
		IEndpoint superUser = EndpointDispatcher.getInstance();
		String userName = "";

		try {
			userName = superUser.getUserName(userKey);
		} catch (Exception e) {
			log.error(e);
			// si no existe el user key en el repo entonces se avisa al usuario
			return false;
		}

		String idGrpSigap;
		try {

			idGrpSigap = superUser.getIdGrupo(nombreGrupo);

		} catch (Exception e) {
			log.error("El gruo a remover " + nombreGrupo + " no fue encontrado en el repositorio,");
			return true; // si no existe no, se asume q se remueve,
		}

		String removeUsuarioGrupo = "";

		boolean result = false;

		if (StringUtils.isNotBlank(idGrpSigap)) {

			removeUsuarioGrupo = superUser.removeUsuarioGrupo(idGrpSigap, userName);
			result = "OK".equalsIgnoreCase(removeUsuarioGrupo);

		} else {

			throw new Exception("EL NOMBRE DEL GRUPO " + nombreGrupo + "  NO SE ENCUENTRA EN EL REPOSITORIO");

		}
		return result;
	}

	/**
	 * Existe usuario repo.
	 *
	 * @param user the user
	 * @return true, if successful
	 */
	private boolean existeUsuarioRepo(Usuario user) {
		boolean existe = false;
		IEndpoint superUser = EndpointDispatcher.getInstance();

		String userName = "";
		try {
			userName = superUser.getUserName(user.getUserKey());
		} catch (Exception e) {
			log.error(e);
			// si no existe el user key en el repo entonces se avisa al usuario
			existe = false;
		}

		if (StringUtils.isNotBlank(userName)) {
			existe = true;
		} else {
			existe = false;
		}

		return existe;
	}

	/**
	 * Save configuraciones.
	 *
	 * @param usuario the usuario
	 * @throws Exception the exception
	 */
	private void saveConfiguraciones(Usuario usuario) throws Exception {
		// guardar configuraciones de notificaiones y pagina inicial
		saveConf(usuario, "NOTIFICACION", TipoNotificacion.RECTURNO, false);
		saveConf(usuario, "NOTIFICACION", TipoNotificacion.RECRESPUESTA, false);
		saveConf(usuario, "INICIO", TipoNotificacion.PAGINAINICIAL, "P00T01");
	}

	/**
	 * Save conf.
	 *
	 * @param u     the u
	 * @param id    the id
	 * @param tn    the tn
	 * @param valor the valor
	 * @throws Exception the exception
	 */
	private void saveConf(Usuario u, String id, TipoNotificacion tn, Object valor) throws Exception {

		Configuracion conf = new Configuracion();

		ConfiguracionKey confKey = new ConfiguracionKey();

		confKey.setUsuario(u);
		confKey.setIdConfiguracion(id);
		confKey.setClave(tn);
		conf.setConfiguracionKey(confKey);

		if (valor instanceof Boolean)
			conf.setValor(((Boolean) valor) ? "S" : "N");
		else if (valor instanceof String || valor instanceof Integer)
			conf.setValor(valor.toString());
		else
			throw new Exception("Bad Request.");

		mngrConfiguracion.save(conf);
	}

	/**
	 * Limite usuarios alcanzado.
	 *
	 * @return true, if successful
	 */
	private boolean limiteUsuariosAlcanzado() {
		boolean limiteAlcanzado = false;
		boolean validateUserLimit = Boolean.valueOf(environment.getProperty("validate.limit.user"));
		if (validateUserLimit) {
			// TODO encontrar forma de hacer un count mas eficiente
			if (getActiveUsersCount() >= MAX_ACTIVE_USERS) {
				limiteAlcanzado = true;
			}
		}

		return limiteAlcanzado;
	}

	/**
	 * Valid user key.
	 *
	 * @param userKey the user key
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	private boolean validUserKey(String userKey) {
		List<Usuario> users = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("userKey", userKey));

		users = (List<Usuario>) mngrUsuario.search(restrictions);
		if (users != null && users.size() > 0) {
			return false;
		}

		return true;
	}

	/**
	 * Agregar grupo usuario.
	 *
	 * @param userKey     the user key
	 * @param nombreGrupo the nombre grupo
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	protected synchronized boolean agregarGrupoUsuario(String userKey, String nombreGrupo) throws Exception {

		IEndpoint superUser = EndpointDispatcher.getInstance();

		String userName = "";

		try {
			userName = superUser.getUserName(userKey);
		} catch (Exception e) {
			log.error(e);
			return false;
		}

		String idGrpSigap = superUser.getIdGrupo(nombreGrupo);

		boolean result = false;

		if (idGrpSigap != null) {

			String addUsuarioGrupo = superUser.addUsuarioGrupo(idGrpSigap, userName);
			result = "OK".equalsIgnoreCase(addUsuarioGrupo);

		} else {

			throw new Exception("EL NOMBRE DEL GRUPO " + nombreGrupo + "  NO SE ENCUENTRA EN EL REPOSITORIO");

		}

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar usuario", notes = "Elimina un usuario de la lista")
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
	@RequestMapping(value = "/usuario", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("USUARIO A ELIMINAR >> " + id);

		try {

			mngrUsuario.delete(mngrUsuario.fetch((String) id));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtiene la lista de Areas a las cuales tiene acceso el Usuario.
	 *
	 * @param idUsuario Identificador del Usuario
	 * @param idArea    Identificador del Area al cual esta accediendo
	 * @return Lista de Areas a las cuales tiene acceso el Usuario
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene usuario areas", notes = "Obtiene la lista de areas a las cuales tiene acceso el usuario")
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/usuario/areas", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Acceso>> getAreasUsuario(
			@RequestParam(value = "idUsuario", required = true) String idUsuario,
			@RequestParam(value = "idArea", required = false) Integer idArea) {

		List<Acceso> items = new ArrayList<>();
		List<Acceso> accesos = new ArrayList<Acceso>();
		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			// No se coloca el parametro idArea como obligarotior porque no se
			// sabe si afecta otra parte. Cambiar posteriormente
			if (null == idArea) {
				return new ResponseEntity<List<Acceso>>(items, HttpStatus.BAD_REQUEST);
			}

			Usuario usuario = mngrUsuario.fetch(idUsuario);

			Acceso defaultAcceso = new Acceso();
			AccesoKey key = new AccesoKey();
			key.setIdUsuario(idUsuario);
			key.setArea(mngrArea.fetch(idArea));
			defaultAcceso.setAccesoKey(key);

			accesos.add(defaultAcceso);

			// si el area a la que ingresa el usuario es a la q pertenece
			// agrega el rol del usuario
			if (idArea.equals(usuario.getIdArea())) {
				defaultAcceso.getAccesoKey().setRol(mngrRol.fetch(usuario.getRol().getIdRol()));
			}

			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("accesoKey.idUsuario"));

			restrictions.add(Restrictions.eq("accesoKey.idUsuario", idUsuario));
			// traer los acceso del usuario
			items = mngrAcceso.search(restrictions, orders, null, null, null);

			// si el acceso default no tiene rol quiere decir que es un area
			// distinta al usuario
			if (null == defaultAcceso.getAccesoKey().getRol()) {
				Iterator<Acceso> ite = items.iterator();
				while (ite.hasNext()) {
					// si el acceso default es igual al acceso consultado asigna
					// el rol del acceso
					Acceso acceso = ite.next();
					if (acceso.equals(defaultAcceso)) {
						defaultAcceso.getAccesoKey().setRol(acceso.getAccesoKey().getRol());
						ite.remove();
						// Si no es el area por defecto del Usuario, se agrega a
						// la lista de areas que puede cambiar el usuario
						Acceso defaultAccesoArea = new Acceso();
						AccesoKey defaultKey = new AccesoKey();
						defaultKey.setIdUsuario(idUsuario);
						defaultKey.setArea(mngrArea.fetch(usuario.getIdArea()));

						defaultAccesoArea.setAccesoKey(defaultKey);

						accesos.add(defaultAccesoArea);
					}
				}
			}
			accesos.addAll(items);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug("Cantidad de accesos del usuario >> " + accesos.size());
		return new ResponseEntity<List<Acceso>>(accesos, HttpStatus.OK);
	}

	/**
	 * Gets the usuario conexion.
	 *
	 * @param id the id
	 * @return the usuario conexion
	 */
	@RequestMapping(value = "/usuario/conexion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Usuario> getUsuarioConexion(
			@RequestParam(value = "id", required = true) Serializable id) {

		Usuario item = null;

		return new ResponseEntity<Usuario>(item, HttpStatus.OK);
	}

	/**
	 * Gets the active users count.
	 *
	 * @return the active users count
	 */
	private Long getActiveUsersCount() {
		List<?> lst = new ArrayList<>();
		Long count = 0L;
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			ProjectionList projections = Projections.projectionList();
			List<Criterion> restrictions = new ArrayList<Criterion>();

			projections.add(Projections.count("idUsuario").as("totalActivos"));

			restrictions.add(Restrictions.eq("activo", true));

			List<Order> orders = new ArrayList<Order>();

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrUsuario.search(restrictions, orders, projections, null, null);

			if (lst != null) {
				HashMap<?, ?> total = (HashMap<?, ?>) lst.get(0);
				count = (Long) total.get("totalActivos");
			} else {
				throw new Exception("No se pudo consultar el total de usuarios activos");
			}

			log.debug("USUARIOS ACTIVOS >> " + count);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

		}

		return count;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		try {

			ECMLicencia lic = new LicenciaUtil().getLicencia();

			MAX_ACTIVE_USERS = lic.getUsuariosActivosMax();

			log.debug("Numero de usuarios activos permitidos por licencia: " + MAX_ACTIVE_USERS);

		} catch (LicenseException e) {

			log.error("Error al consultar los usuarios de la licencia: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param idArea
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/usuario/atendiendosePor", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody ResponseEntity<List<Usuario>> atendiendosePor(
			@RequestParam(value = "idArea", required = true) Integer idArea,
			@RequestParam(value = "inclAccesos", required = false, defaultValue = "true") Boolean incluirAccessos) {

		List<Usuario> lst = new ArrayList<Usuario>();

		try {

			Usuario usuario_ = new Usuario();
			usuario_.setIdArea(idArea);
			usuario_.setActivo(true);
			ResponseEntity<List<?>> responseUsuarios = search(usuario_);

			if (responseUsuarios.getStatusCode() == HttpStatus.OK)
				lst.addAll((List<Usuario>) responseUsuarios.getBody());

			if (incluirAccessos) {
				Acceso acceso_ = new Acceso();
				acceso_.setAccesoKey(new AccesoKey());
				acceso_.getAccesoKey().setArea(new Area());
				acceso_.getAccesoKey().getArea().setIdArea(idArea);

				ResponseEntity<List<?>> responseAccesos = accesoController.search(acceso_);

				if (responseAccesos.getStatusCode() == HttpStatus.OK)
					for (Acceso acss : (List<Acceso>) responseAccesos.getBody()) {
						if (acss.getUsuario().getActivo())
							lst.add(acss.getUsuario());
					}
			}

			return new ResponseEntity<List<Usuario>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

}