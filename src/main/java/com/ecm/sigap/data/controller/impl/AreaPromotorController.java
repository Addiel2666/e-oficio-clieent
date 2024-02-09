package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.BadRequestException;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.AreaPromotor;
import com.ecm.sigap.data.model.AreaPromotorKey;
import com.ecm.sigap.data.model.Institucion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.AreaPromotor}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public final class AreaPromotorController extends CustomRestController implements RESTController<AreaPromotor> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AreaPromotorController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@RequestMapping(value = "/areaPromotor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) AreaPromotor areaPromotor) {

		List<AreaPromotor> lst = new ArrayList<AreaPromotor>();
		// log.info("Parametros de busqueda :: " + areaPromotor);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (areaPromotor.getAreaPromotorKey().getIdArea() != null)
				restrictions
						.add(Restrictions.eq("areaPromotorKey.idArea", areaPromotor.getAreaPromotorKey().getIdArea()));
			if (areaPromotor.getAreaPromotorKey().getInstitucion() != null) {

				if (areaPromotor.getAreaPromotorKey().getInstitucion().getIdInstitucion() != null)
					restrictions.add(Restrictions.eq("areaPromotorKey.idInstitucion",
							areaPromotor.getAreaPromotorKey().getInstitucion().getIdInstitucion()));

			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("areaPromotorKey.idInstitucion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<AreaPromotor>) mngrAreaPromotor.search(restrictions, orders);

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
	@RequestMapping(value = "/areaPromotor", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<AreaPromotor> save(@RequestBody(required = true) AreaPromotor areaPromotor)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("PROMOTOR A GUARDAR >> " + areaPromotor);

				if ((areaPromotor.getAreaPromotorKey().getIdArea() != null)
						&& (areaPromotor.getAreaPromotorKey().getInstitucion().getIdInstitucion() != null)) {

					// Validamos que las reglas de validacion de la entidad Tipo
					// AreaPromotor no se esten violando con este nuevo registro
					validateEntity(mngrAreaPromotor, areaPromotor);

					// Guardamos la informacion
					mngrAreaPromotor.save(areaPromotor);
					return new ResponseEntity<AreaPromotor>(areaPromotor, HttpStatus.CREATED);

				} else {
					return new ResponseEntity<AreaPromotor>(areaPromotor, HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<AreaPromotor>(areaPromotor, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * Search instituciones no promotor. Obtiene las instituciones no agregadas como
	 * promotor en areasPromotores
	 *
	 * @param areaPromotor the area promotor
	 * @return List Institucion
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta instituciones no promotor", notes = "Consulta las instituciones no agregadas como promotor")
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
	@RequestMapping(value = "/institucionesNoPromotor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Institucion>> searchInstitucionesNoPromotor(
			@RequestBody(required = true) AreaPromotor areaPromotor) {

		List<Institucion> lst = new ArrayList<Institucion>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *

			List<Criterion> restrictions = new ArrayList<Criterion>();

			// - - - -
			AreaPromotor ttt = new AreaPromotor();
			ttt.setAreaPromotorKey(new AreaPromotorKey());
			ttt.getAreaPromotorKey().setIdArea(areaPromotor.getAreaPromotorKey().getIdArea());

			List<Institucion> lasQueSi = searchInstitucionePromotor(ttt).getBody();

			for (Institucion institucion : lasQueSi) {
				restrictions.add(Restrictions.not(Restrictions.idEq(institucion.getIdInstitucion())));
			}

			// - - - -

			if (areaPromotor.getAreaPromotorKey().getInstitucion() != null) {

				if (StringUtils.isNotBlank(areaPromotor.getAreaPromotorKey().getInstitucion().getDescripcion())) {
					restrictions.add(EscapedLikeRestrictions.ilike("descripcion",
							areaPromotor.getAreaPromotorKey().getInstitucion().getDescripcion(), MatchMode.ANYWHERE));
				}

				if (StringUtils.isNotBlank(areaPromotor.getAreaPromotorKey().getInstitucion().getTipo())) {
					restrictions
							.add(Restrictions.eq("tipo", areaPromotor.getAreaPromotorKey().getInstitucion().getTipo()));
				}
			}

			restrictions.add(Restrictions.eq("activo", Boolean.TRUE));

			// - - - -

			//List<Order> orders = new ArrayList<Order>();
			//orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = (List<Institucion>) mngrInstitucion.search(restrictions, null);
			
			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<Institucion>() {
				@Override
				public int compare(Institucion i1, Institucion i2) {
					return i1.getDescripcion().compareTo(i2.getDescripcion());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
						

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<Institucion>>(lst, HttpStatus.OK);
	}

	/**
	 * searchInstitucionePromotor. Obtiene las instituciones agregadas como promotor
	 * en areasPromotores
	 *
	 * @param areaPromotor the area promotor
	 * @return List Institucion
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta instituciones promotor", notes = "Consulta las instituciones agregadas como promotor")
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
	@RequestMapping(value = "/institucionesPromotor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Institucion>> searchInstitucionePromotor(
			@RequestBody(required = true) AreaPromotor areaPromotor) {

		List<Institucion> lst = new ArrayList<Institucion>();
		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			/*
			 * // NUNCA ACEPTO ESTE JOIN PARA LOS FILTROS. if
			 * (areaPromotor.getAreaPromotorKey().getInstitucion() != null) {
			 * 
			 * if
			 * (StringUtils.isNotBlank(areaPromotor.getAreaPromotorKey().getInstitucion().
			 * getDescripcion())) {
			 * restrictions.add(EscapedLikeRestrictions.ilike("institucion.descripcion",
			 * areaPromotor.getAreaPromotorKey().getInstitucion().getDescripcion(),
			 * MatchMode.ANYWHERE)); }
			 * 
			 * if
			 * (StringUtils.isNotBlank(areaPromotor.getAreaPromotorKey().getInstitucion().
			 * getTipo())) { restrictions.add(Restrictions.eq("institucion.tipo",
			 * areaPromotor.getAreaPromotorKey().getInstitucion().getTipo())); } }
			 */

			restrictions
					.add((Restrictions.eq("areaPromotorKey.idArea", areaPromotor.getAreaPromotorKey().getIdArea())));

			List<Order> orders = new ArrayList<Order>();
			// orders.add(Order.asc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<AreaPromotor> lstAP = (List<AreaPromotor>) mngrAreaPromotor.search(restrictions, orders);

			for (AreaPromotor areaProm : lstAP) {
				if (areaProm.getAreaPromotorKey().getInstitucion().getActivo()) {

					if (areaPromotor.getAreaPromotorKey().getInstitucion() != null) {

						if (StringUtils.isNotBlank(areaPromotor.getAreaPromotorKey().getInstitucion().getDescripcion()) //
								&& !areaProm.getAreaPromotorKey().getInstitucion().getDescripcion().toLowerCase()
										.contains(areaPromotor.getAreaPromotorKey().getInstitucion().getDescripcion()
												.toLowerCase())) {
							continue; // no contienen la descripcion
						}

						if (StringUtils.isNotBlank(areaPromotor.getAreaPromotorKey().getInstitucion().getDescripcion()) //
								&& !areaProm.getAreaPromotorKey().getInstitucion().getTipo().equalsIgnoreCase(
										areaPromotor.getAreaPromotorKey().getInstitucion().getTipo())) {
							continue; // no es del mismo tipo
						}

					}

					lst.add(areaProm.getAreaPromotorKey().getInstitucion());

				}
			}

			log.debug("Size found >> " + lst.size());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<List<Institucion>>(lst, HttpStatus.OK);
	}

	/**
	 * Eliminar un areaPromotor.
	 *
	 * @param areaPromotor the area promotor
	 */
	
	/*
	 * Documentacion con swagger
	 */

@ApiOperation(value = "Eliminar area promotor", notes = "Elimina una area promotor de la lista")
@ApiImplicitParams({
	@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
	@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
	@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
	@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
	@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 204, message = "La peticion se ha completado con exito pero su respuesta no tiene ningun contenido"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@RequestMapping(value = "/removerAreaPromotor", method = RequestMethod.POST)
	public void delete(@RequestBody(required = true) AreaPromotor areaPromotor) {

		log.debug("ELIMINAR INSTITUCION FAVORITA >> " + areaPromotor);

		try {

			if (areaPromotor.getAreaPromotorKey() != null //
					&& (areaPromotor.getAreaPromotorKey().getIdArea() != null)
					&& (areaPromotor.getAreaPromotorKey().getInstitucion() != null)
					&& (areaPromotor.getAreaPromotorKey().getInstitucion().getIdInstitucion() != null)) {

				areaPromotor.getAreaPromotorKey().setInstitucion(
						mngrInstitucion.fetch(areaPromotor.getAreaPromotorKey().getInstitucion().getIdInstitucion()));

				mngrAreaPromotor.delete(areaPromotor);
				return;
			}

			throw new BadRequestException();

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<AreaPromotor> get(Serializable id) {
		throw new UnsupportedOperationException();
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

}
