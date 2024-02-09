/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
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

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.Path;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.model.PermisoKey;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.util.CollectionUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Rol}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
@RestController
public class RolController extends CustomRestController implements RESTController<Rol> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RolController.class);

	@Autowired
	private PermisoController permisoController;

	@Autowired
	private UsuarioController userController;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene rol", notes = "Obtiene el rol de un usuario")
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
	@RequestMapping(value = "/rol", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Rol> get(@RequestParam(value = "id", required = true) Serializable id) {

		Rol item = null;
		try {

			item = mngrRol.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Rol>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta rol", notes = "Consulta la lista de roles")
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
	@RequestMapping(value = "/rol", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Rol rol) {

		List<?> lst = new ArrayList<Rol>();
		log.info("Parametros de busqueda :: " + rol);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (rol.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", rol.getIdArea()));

			if ((rol.getDescripcion() != null) && (!rol.getDescripcion().isEmpty()))
				restrictions
						.add(EscapedLikeRestrictions.ilike("descripcion", rol.getDescripcion(), MatchMode.ANYWHERE));

			if (rol.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", rol.getActivo()));

			if ((rol.getAtributos() != null) && (!rol.getAtributos().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("atributos", rol.getAtributos(), MatchMode.ANYWHERE));

			if (rol.getIdAreaLim() != null)
				restrictions.add(Restrictions.eq("idAreaLim", rol.getIdAreaLim()));

			if ((rol.getTipo() != null) && (!rol.getTipo().isEmpty()))
				restrictions.add(Restrictions.eq("tipo", rol.getTipo()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrRol.search(restrictions, orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
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

	@ApiOperation(value = "Eliminar rol", notes = "Elimina el rol de un usuario")
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
	@RequestMapping(value = "/rol", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("ROL A ELIMINAR >> " + id);

		try {

			mngrRol.delete(mngrRol.fetch(Integer.valueOf((String) id)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar rol", notes = "Agrega un nuevo rol al area seleccionada")
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
	@RequestMapping(value = "/rol", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Rol> save(@RequestBody(required = true) Rol newRol) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("ROL A GUARDAR >> " + newRol);

				if (newRol.getIdRol() == null) {

					mngrRol.save(newRol);
					return new ResponseEntity<Rol>(newRol, HttpStatus.CREATED);

				} else {

					mngrRol.update(newRol);
					return new ResponseEntity<Rol>(newRol, HttpStatus.OK);

				}
			} else {
				return new ResponseEntity<Rol>(newRol, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * 
	 * @param idRol
	 * @param grupo
	 * @param agregar
	 * @throws Exception
	 */
	private void actualizarUsuariosGrupo(Integer idRol, String grupo, boolean agregar) throws Exception {
		List<Usuario> users = new ArrayList<>();

		List<Criterion> restrictions;
		{
			restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("rol.idRol", idRol));
			// restrictions.add(Restrictions.eq("activo", true));
			List<Usuario> users_ = (List<Usuario>) mngrUsuario.search(restrictions);
			users.addAll(users_);
		}
		{
			restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("accesoKey.rol.idRol", idRol));
			List<Acceso> accessos_ = (List<Acceso>) mngrAcceso.search(restrictions);

			for (Acceso acceso : accessos_) {
				users.add(acceso.getUsuario());
			}

		}

		for (Usuario user : users) {
			if (agregar) {
				userController.agregarGrupoUsuario(user.getUserKey(), grupo);
			} else {
				userController.removerGrupoUsuario(user.getUserKey(), grupo);
			}

		}

	}

	/**
	 * Valida si el Rol indicado puede ver asuntos confidenciales.
	 * 
	 * @param rol
	 * @return
	 * @throws Exception
	 */
	public boolean hasPermisoConfidencial(Rol rol) throws Exception {

		List<Criterion> restrictions = new ArrayList<Criterion>();

		List<?> lst = new ArrayList<Permiso>();

		boolean hasConfidencial = false;

		if (rol.getIdArea() == null || rol.getIdRol() == null) {
			throw new Exception("idArea o idRol nulo");
		}

		Path verConfidencial = getPathVerConfidencial();

		restrictions.add(Restrictions.eq("permisoKey.idArea", rol.getIdArea()));
		restrictions.add(Restrictions.eq("permisoKey.idRol", rol.getIdRol()));
		restrictions.add(Restrictions.eq("permisoKey.idObjeto", verConfidencial.getId()));
		restrictions.add(Restrictions.eq("permisoKey.idTipoPermiso", verConfidencial.getTipo()));
		restrictions.add(Restrictions.isNotNull("descripcion"));

		lst = mngrPermiso.search(restrictions);

		if (lst != null && !lst.isEmpty()) {
			hasConfidencial = true;
		}

		return hasConfidencial;
	}

	/**
	 * Permiso ver confidenciales.
	 * 
	 * @return
	 * @throws Exception
	 */
	private Path getPathVerConfidencial() throws Exception {
		Path fetch = mngrPath.fetch("A00");
		if (fetch == null)
			throw new Exception("Path para CONFIDENCIAL no existe!");
		return fetch;
	}

	/**
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar permisos", notes = "Agrega los nuevos permisos y elimina los permisos anteriores")
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

	@RequestMapping(value = "/rol/permisos", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Rol> savePermisosRol(@RequestBody(required = true) Map<String, Object> data)
			throws Exception {

		try {

			Integer idRol = (Integer) data.get("idRol");
			Integer idArea = (Integer) data.get("idArea");

			List<String> permisos = (List<String>) data.get("permisos");

			Rol rol = mngrRol.fetch(idRol);

			if (rol == null)
				throw new BadRequestException("No existe rol.");

			Permiso permisoSrch = new Permiso();
			permisoSrch.setPermisoKey(new PermisoKey());
			permisoSrch.getPermisoKey().setIdRol(idRol);

			ResponseEntity<List<?>> permisosO = permisoController.search(permisoSrch);

			if (permisosO.getStatusCode() == HttpStatus.OK) {

				List<Permiso> permisosOrig = (List<Permiso>) permisosO.getBody();
				List<String> permisosOriginales = new ArrayList<>();

				for (Permiso p : permisosOrig)
					permisosOriginales.add(p.getDescripcion());

				List<String> porAgregar = (List<String>) CollectionUtil.substract(permisos, permisosOriginales);
				List<String> porQuitar = (List<String>) CollectionUtil.substract(permisosOriginales, permisos);

				Path verConfidencial = getPathVerConfidencial();
				String grpSigapConf = environment.getProperty("grpSigapConf") + idArea;

				Permiso permiso;

				for (String permiso_ : porQuitar) {
					log.info(" :: quitando permiso " + permiso_ + " en  rol " + idRol);
					for (Permiso p : permisosOrig) {
						if (null != p.getDescripcion() && p.getDescripcion().equalsIgnoreCase(permiso_)) {

							// si el permiso a quitar es el de ver confidencial
							// entonces sacamos a todos los usuarios q tengan
							// ese rol del grupo confidencial
							if (verConfidencial.getDescripcion().equalsIgnoreCase(permiso_)) {
								actualizarUsuariosGrupo(idRol, grpSigapConf, false);

							}
							p.setDescripcion(null);
							permisoController.save(p);

							break;
						}

					}

				}

				for (String permiso_ : porAgregar) {

					log.info(" :: agregando permiso " + permiso_ + " en  rol " + idRol);

					Path path = getPath(permiso_);

					if (path != null) {

						permiso = new Permiso();
						permiso.setPermisoKey(new PermisoKey());

						permiso.getPermisoKey().setIdRol(idRol);
						permiso.getPermisoKey().setIdArea(idArea);
						permiso.getPermisoKey().setIdObjeto(path.getId());
						permiso.getPermisoKey().setIdTipoPermiso(path.getTipo());

						permiso.setDescripcion(permiso_);

						// si el permiso a agregar es el de ver confidencial
						// entonces agregamos a todos los usuarios q tengan ese
						// rol del grupo confidencial
						if (verConfidencial.getDescripcion().equalsIgnoreCase(permiso_)) {
							actualizarUsuariosGrupo(idRol, grpSigapConf, true);
						}

						permisoController.save(permiso);

					} else {

						log.warn(" EL PERMISO " + permiso_ + " NO SE ENCUENTRA DEFINIDO.");

					}
				}

			}

			return new ResponseEntity<Rol>(rol, HttpStatus.CREATED);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Obtener relacions pagina(sigap 4) a permiso(sigap v)
	 * 
	 * @param permiso
	 * @return
	 */
	private Path getPath(String permiso) {

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("descripcion", permiso));

		List<Path> path = (List<Path>) mngrPath.search(restrictions);

		for (Path p : path) {
			return p;
		}

		return null;

	}

}