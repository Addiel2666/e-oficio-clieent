/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.AccesoKey;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Usuario;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Acceso}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class AccesoController extends CustomRestController implements RESTController<Acceso> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AccesoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta accesos", notes = "Consulta todos los accesos del area seleccionada")
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
	@RequestMapping(value = "/acceso", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Acceso acceso) {

		List<?> lst = new ArrayList<Acceso>();
		log.info("Parametros de busqueda :: " + acceso);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("accesoKey.idUsuario"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrAcceso.search(find(acceso, true), orders);

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<List<?>>(lst, HttpStatus.OK);
	}

	/**
	 * 
	 * @param acceso
	 * @param includeRole
	 * @return
	 */
	private List<Criterion> find(Acceso acceso, boolean includeRole) {
		List<Criterion> restrictions = new ArrayList<Criterion>();

		if (acceso.getAccesoKey().getIdUsuario() != null)
			restrictions.add(Restrictions.eq("accesoKey.idUsuario", acceso.getAccesoKey().getIdUsuario()));

		if (acceso.getAccesoKey().getArea() != null && acceso.getAccesoKey().getArea().getIdArea() != null)
			restrictions.add(Restrictions.eq("accesoKey.area.idArea", acceso.getAccesoKey().getArea().getIdArea()));

		if (acceso.getAccesoKey().getRol() != null && acceso.getAccesoKey().getRol().getIdRol() != null
				&& includeRole) {
			restrictions.add(Restrictions.eq("accesoKey.rol.idRol", acceso.getAccesoKey().getRol().getIdRol()));
		}

		return restrictions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Cambiar acceso", notes = "Inserta un nuevo acceso a un usuario")
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
	@RequestMapping(value = "/acceso", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Acceso> save(@RequestBody(required = true) Acceso acceso) throws Exception {

		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		if (!esSoloLectura(userId)) {
			log.debug("::>> ACCESO A GUARDAR O ACTUALIZAR >> " + acceso);
			
			try {
				if(null != acceso.getAccesoKey() && 
				   null != acceso.getAccesoKey().getArea() &&
				   null == acceso.getAccesoKey().getArea().getDescripcion()) {
					
					acceso.getAccesoKey().setArea(mngrArea.fetch(acceso.getAccesoKey().getArea().getIdArea()));
				}
			} catch (Exception e) { }

			try {

				if ((acceso.getAccesoKey().getIdUsuario() != null)
						&& (acceso.getAccesoKey().getArea() != null
								&& acceso.getAccesoKey().getArea().getIdArea() != null)
						&& (acceso.getAccesoKey().getRol() != null
								&& acceso.getAccesoKey().getRol().getIdRol() != null)) {

					// ACTUALIZAR
					if (acceso.getIdNuevoRol() != null) {
						Acceso item = mngrAcceso.fetch(acceso.getAccesoKey());
						if (item != null) {
							mngrAcceso.delete(item);
							removerGrupoUsuario(acceso);

							// llenar con el nuevo rol
//							Rol newRol = new Rol();
//							newRol.setIdRol(acceso.getIdNuevoRol());
//							newRol.setIdArea(acceso.getAccesoKey().getArea().getIdArea());
//							acceso.getAccesoKey().setRol(mngrRol.fetch(newRol));
							acceso.getAccesoKey().setRol(mngrRol.fetch(acceso.getIdNuevoRol()));

							// validar
							validateEntity(mngrAcceso, acceso);

							// agregar usuario a grupo del nuevo acceso
							// validar si el rol es confidencial para el grupo conf
							if (agregarGrupoUsuario(acceso)) {
								// Guardamos la informacion
								mngrAcceso.save(acceso);
							} else {
								log.error("Error al actualizar el grupo para " + acceso);
								throw new Exception("No fue posible agregar al grupo de usuarios del nuevo acceso");
							}

						} else {
							log.error("El registro de ACCESO que se desea modificar no existe->  Area: "
									+ acceso.getAccesoKey().getArea().getIdArea() + " Rol: "
									+ acceso.getAccesoKey().getRol().getIdRol() + " Usuario: "
									+ acceso.getAccesoKey().getIdUsuario());
							return new ResponseEntity<Acceso>(acceso, HttpStatus.BAD_REQUEST);
						}
					} else {
						acceso.getAccesoKey().setRol(mngrRol.fetch(acceso.getAccesoKey().getRol().getIdRol()));
						// NUEVO REGISTRO Validamos que las reglas de validacion
						// de
						// la entidad Tipo Acceso no se esten violando con este
						// nuevo registro
						validateEntity(mngrAcceso, acceso);

						// agregar usuario a grupo del nuevo acceso
						// validar si el rol es confidencial para el grupo conf
						if (agregarGrupoUsuario(acceso)) {
							// Guardamos la informacion
							mngrAcceso.save(acceso);
						} else {
							log.error("Error al agregar el grupo para " + acceso);
							throw new Exception("No fue posible agregar al grupo de usuarios del nuevo acceso");
						}

					}

					return new ResponseEntity<Acceso>(acceso, HttpStatus.CREATED);
				} else {
					return new ResponseEntity<Acceso>(acceso, HttpStatus.BAD_REQUEST);
				}
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());

				throw e;
			}
		} else {
			return new ResponseEntity<Acceso>(acceso, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param acceso
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean agregarGrupoUsuario(Acceso acceso) throws Exception {

		Usuario usuario = mngrUsuario.fetch(acceso.getAccesoKey().getIdUsuario());
		IEndpoint superUser = EndpointDispatcher.getInstance();
		String userName = "";

		try {
			userName = superUser.getUserName(usuario.getUserKey());
		} catch (Exception e) {
			log.error(e);
			return false;
		}

		Integer idArea = acceso.getAccesoKey().getArea().getIdArea();
		Integer idRol = acceso.getAccesoKey().getRol().getIdRol();

		String idGrpSigap = superUser.getIdGrupo(environment.getProperty("grpSigap") + idArea);
		superUser.addUsuarioGrupo(idGrpSigap, userName);

		if (isConfidencial(idArea, idRol)) {
			String idGrpSigapConf = superUser.getIdGrupo(environment.getProperty("grpSigapConf") + idArea);
			superUser.addUsuarioGrupo(idGrpSigapConf, userName);
		}

		return true;

	}

	/**
	 * 
	 * @param acceso
	 * @return
	 * @throws Exception
	 */
	protected synchronized boolean removerGrupoUsuario(Acceso acceso) throws Exception {
		Usuario usuario = mngrUsuario.fetch(acceso.getAccesoKey().getIdUsuario());
		IEndpoint superUser = EndpointDispatcher.getInstance();
		String userName = "";
		try {
			userName = superUser.getUserName(usuario.getUserKey());
		} catch (Exception e) {
			log.error(e);
			// si no existe el user key en el repo entonces se avisa al usuario
			return false;
		}

		Integer idArea = acceso.getAccesoKey().getArea().getIdArea();
		Integer idRol = acceso.getAccesoKey().getRol().getIdRol();
		String idGrpSigap = superUser.getIdGrupo(environment.getProperty("grpSigap") + idArea);
		superUser.removeUsuarioGrupo(idGrpSigap, userName);

		if (isConfidencial(idArea, idRol)) {
			String idGrpSigapConf = superUser.getIdGrupo(environment.getProperty("grpSigapConf") + idArea);
			superUser.removeUsuarioGrupo(idGrpSigapConf, userName);
		}

		return true;

	}

	/**
	 * 
	 * @param idArea
	 * @param idRol
	 * @return
	 * @throws Exception
	 */
	private boolean isConfidencial(Integer idArea, Integer idRol) throws Exception {
		List<Criterion> restrictions = new ArrayList<Criterion>();
		List<?> lst = new ArrayList<Permiso>();
		boolean isConfidencial = false;

		if (idArea == null || idRol == null) {
			throw new Exception("idArea o idRol nulo");
		}

		restrictions.add(Restrictions.eq("permisoKey.idArea", idArea));
		restrictions.add(Restrictions.eq("permisoKey.idRol", idRol));
		restrictions.add(Restrictions.eq("permisoKey.idObjeto", "A00"));
		restrictions.add(Restrictions.eq("permisoKey.idTipoPermiso", "A"));
		// Se agrega para busqueda mas especifica, si cumple con las condiciones
		// anteriores pero descripcion esta vacia, no es confidencial.
		restrictions.add(Restrictions.isNotNull("descripcion"));

		lst = mngrPermiso.search(restrictions);

		if (!lst.isEmpty()) {
			isConfidencial = true;
		}

		return isConfidencial;
	}

	/**
	 * Delete.
	 *
	 * @param acceso the acceso
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar acceso", notes = "Elimina el acceso de un usuario")
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

	@RequestMapping(value = "/acceso", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "idRol", required = true) Serializable idRol,
			@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idUsuario", required = true) Serializable idUsuario) throws Exception {

		log.debug("ACCESO A ELIMINAR >> " + idRol);
		log.debug("ACCESO A ELIMINAR >> " + idArea);
		log.debug("ACCESO A ELIMINAR >> " + idUsuario);

		try {

			if (idArea != null & idRol != null && idUsuario != null) {

				Acceso acceso = new Acceso();
				AccesoKey key = new AccesoKey();

				try {
					key.setArea(mngrArea.fetch(Integer.valueOf((String) idArea)));
				} catch (Exception e) {
					Area area = new Area();
					area.setIdArea(Integer.valueOf((String) idArea));
					key.setArea(area);
				}
				
				Rol rol = new Rol();
				rol.setIdRol(Integer.valueOf((String) idRol));
				key.setRol(rol);

				key.setIdUsuario((String) idUsuario);

				acceso.setAccesoKey(key);

				// eliminar usuario de grupo del nuevo acceso
				// validar si el rol es confidencial para quitar el grupo conf
				if (removerGrupoUsuario(acceso)) {
					mngrAcceso.delete(acceso);
				} else {
					log.error("Error al remover el grupo para el acceso " + acceso);
					throw new Exception("No fue posible remover el grupo de usuarios para el acceso");
				}

			}
			log.debug("DELETE! ");

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
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
	 * 
	 * @param idRol
	 * @param idArea
	 * @param idUsuario
	 * @return
	 */
	@RequestMapping(value = "/acceso", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody ResponseEntity<Acceso> get(@RequestParam(value = "idRol", required = true) Serializable idRol,
			@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idUsuario", required = true) Serializable idUsuario) {
		Acceso acceso = new Acceso();
		Acceso item = null;
		if (idArea != null & idRol != null && idUsuario != null) {
			try {

				AccesoKey accesoKey = new AccesoKey();

				Area area = new Area();
				area.setIdArea(Integer.valueOf((String) idArea));
				accesoKey.setArea(area);

				Rol rol = new Rol();
				rol.setIdRol(Integer.valueOf((String) idRol));
				accesoKey.setRol(rol);

				accesoKey.setIdUsuario((String) idUsuario);

				acceso.setAccesoKey(accesoKey);

				item = mngrAcceso.fetch(accesoKey);
				log.debug("::: Informacion del acceso de retorno " + accesoKey);

				return new ResponseEntity<Acceso>(item, HttpStatus.OK);
			} catch (Exception e) {

				log.error(e.getMessage());
				return new ResponseEntity<Acceso>(item, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<Acceso>(acceso, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<Acceso> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

}