/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
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

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.model.PermisoKey;
import com.ecm.sigap.data.model.Usuario;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Permiso}
 * 
 * @author Alejandro Guzman
 * @version 1.0
 *
 */
@RestController
public class PermisoController extends CustomRestController implements RESTController<Permiso> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(PermisoController.class);

	/**
	 * Obtiene la lista de permisos que tiene el Usuario para un Area
	 * determinada
	 * 
	 * @param idUsuario
	 *            Identificador del Usuario
	 * @param idArea
	 *            Identificador del Area
	 * @return Lista de permisos
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene permisos", notes = "Obtiene la lista de permisos que tiene el usuario para un area determinada")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/seguridad/permiso", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Permiso>> getPermisos(
			@RequestParam(value = "idUsuario", required = true) String idUsuario,
			@RequestParam(value = "idArea", required = true) Integer idArea) {

		log.info("::: Parametros de busqueda: [idUsuario=" + idUsuario + "], [idArea=" + idArea + "]");

		List<Permiso> lst = new ArrayList<Permiso>();
		try {

			List<Criterion> restrictions = new ArrayList<Criterion>();

			Usuario usuario = mngrUsuario.fetch(idUsuario);

			if (null == usuario) {
				return new ResponseEntity<List<Permiso>>(lst, HttpStatus.BAD_REQUEST);
			}

			Integer rol = usuario.getRol().getIdRol();
			String tipoRol = usuario.getRol().getTipo();

			if (!idArea.equals(usuario.getIdArea())) {
				// Se obtienen los accesos del Usuario para el Area seleccionada
				restrictions.add(Restrictions.eq("accesoKey.idUsuario", idUsuario));
				restrictions.add(Restrictions.eq("accesoKey.area.idArea", idArea));
				List<?> accesos = mngrAcceso.search(restrictions);

				if (accesos.isEmpty()) {
					return new ResponseEntity<List<Permiso>>(lst, HttpStatus.BAD_REQUEST);
				}

				Acceso acceso = (Acceso) accesos.get(0);
				log.debug("::: Acceso del usuario: " + acceso);

				restrictions = new ArrayList<Criterion>();
				rol = acceso.getAccesoKey().getRol().getIdRol();
				tipoRol = acceso.getAccesoKey().getRol().getTipo();
			}

			// Se obtienen los permisos del Usuario para el Area seleccionada
			restrictions.add(Restrictions.eq("permisoKey.idArea", idArea));
			restrictions.add(Restrictions.eq("permisoKey.idRol", rol));
			restrictions.add(Restrictions.isNotNull("descripcion"));

			//List<Order> orders = new ArrayList<Order>();
			//orders.add(Order.desc("descripcion"));
			lst = (List<Permiso>) mngrPermiso.search(restrictions, null);
			
			// * * * * * ORDENA EL LIST POR DESCRIPCION (DESC) * * * * * * 
			Collections.sort(lst, new Comparator<Permiso>() {
				@Override
				public int compare(Permiso p1, Permiso p2){
					return p2.getDescripcion().compareTo(p1.getDescripcion());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * *	

			// Valida si es solo lectura y añade a la lista un permiso con solo
			// descipcion actualizaInfo
			if (!"R".equalsIgnoreCase(tipoRol)) {
				Permiso permisoSoloLectura = new Permiso();
				PermisoKey permisoSoloLecturaKey = new PermisoKey();
				permisoSoloLecturaKey.setIdArea(idArea);
				permisoSoloLecturaKey.setIdRol(rol);
				permisoSoloLecturaKey.setIdObjeto("idObjeto");
				permisoSoloLecturaKey.setIdTipoPermiso("idTipoPermiso");
				permisoSoloLectura.setPermisoKey(permisoSoloLecturaKey);
				permisoSoloLectura.setDescripcion("actualizaInfo");
				lst.add(permisoSoloLectura);
			}

			log.debug("::: Cantidad permisos asignados " + lst.size());

			return new ResponseEntity<List<Permiso>>(lst, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return new ResponseEntity<List<Permiso>>(lst, HttpStatus.INTERNAL_SERVER_ERROR);
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
	
	@ApiOperation(value = "Consulta permiso", notes = "Consulta y obtiene los permisos del rol del usuario")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@Override
	@RequestMapping(value = "/seguridad/permiso", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Permiso permiso) {

		List<?> lst = new ArrayList<Permiso>();
		log.info("Parametros de busqueda :: " + permiso);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (permiso.getPermisoKey() != null) {

				if (permiso.getPermisoKey().getIdArea() != null)
					restrictions.add(Restrictions.eq("permisoKey.idArea", permiso.getPermisoKey().getIdArea()));

				if (permiso.getPermisoKey().getIdRol() != null)
					restrictions.add(Restrictions.eq("permisoKey.idRol", permiso.getPermisoKey().getIdRol()));

				if (!StringUtils.isBlank(permiso.getPermisoKey().getIdObjeto()))
					restrictions.add(Restrictions.eq("permisoKey.idObjeto", permiso.getPermisoKey().getIdObjeto()));

				if (!StringUtils.isBlank(permiso.getPermisoKey().getIdTipoPermiso()))
					restrictions.add(
							Restrictions.eq("permisoKey.idTipoPermiso", permiso.getPermisoKey().getIdTipoPermiso()));
			}
			if (permiso.getDescripcion() != null) {
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", permiso.getDescripcion(), MatchMode.ANYWHERE));
			} else {
				restrictions.add(Restrictions.isNotNull("descripcion"));
			}
				

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrPermiso.search(restrictions, orders);

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
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/seguridad/permiso", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Permiso> save(@RequestBody(required = true) Permiso permiso) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::: Permiso que se desea Guardar / Modificar" + permiso);

				if (permiso.getPermisoKey() != null //
						&& permiso.getPermisoKey().getIdTipoPermiso() != null //
						&& permiso.getPermisoKey().getIdObjeto() != null //
						&& permiso.getPermisoKey().getIdArea() != null//
						&& permiso.getPermisoKey().getIdRol() != null
				// && !StringUtils.isBlank(permiso.getDescripcion())
				) {

					Permiso permisoTmp = mngrPermiso.fetch(permiso.getPermisoKey());

					if (permisoTmp != null) {

						mngrPermiso.update(permiso);
						return new ResponseEntity<Permiso>(permiso, HttpStatus.OK);

					} else {

						mngrPermiso.save(permiso);
						return new ResponseEntity<Permiso>(permiso, HttpStatus.CREATED);

					}

				} else {
					return new ResponseEntity<Permiso>(permiso, HttpStatus.BAD_REQUEST);

				}
			} else {
				return new ResponseEntity<Permiso>(permiso, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Delete.
	 *
	 * @param idArea
	 *            the id area
	 * @param idRol
	 *            the id rol
	 * @param idObject
	 *            the id object
	 * @param idTipoPermiso
	 *            the id tipo permiso
	 */
	@RequestMapping(value = "/seguridad/permiso", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "idRol", required = true) Serializable idRol,
			@RequestParam(value = "idObjeto", required = true) Serializable idObjeto,
			@RequestParam(value = "idTipoPermiso", required = true) Serializable idTipoPermiso) {

		PermisoKey permisoKey = new PermisoKey();
		permisoKey.setIdArea(Integer.valueOf((String) idArea));
		permisoKey.setIdRol(Integer.valueOf((String) idRol));
		permisoKey.setIdObjeto(String.valueOf((String) idObjeto));
		permisoKey.setIdTipoPermiso(String.valueOf((String) idTipoPermiso));

		log.debug("::: Identificador del permiso a eliminar " + permisoKey);

		try {

			mngrPermiso.delete(mngrPermiso.fetch(permisoKey));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	protected boolean verConfidencial(String userId, int idArea) {
		List<Permiso> lstPermiso = (List<Permiso>) getPermisos(userId, idArea).getBody();
		// se filtra la lista de permiso. el findFirst termina el ciclo
		// cuando lo consigue
		Optional<Permiso> optPermiso = lstPermiso.stream()
				.filter(p -> "A00".equalsIgnoreCase(p.getPermisoKey().getIdObjeto())).findFirst();
		return (optPermiso.isPresent()) ? true : false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	@Override
	public void delete(Serializable id) throws Exception {
		throw new UnsupportedOperationException();

	}

	@Override
	public ResponseEntity<Permiso> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

}