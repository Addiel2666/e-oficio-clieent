/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.validation.ConstraintViolationException;

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
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.DiaFestivo;
import com.ecm.sigap.data.model.TipoPrioridad;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.TipoPrioridad}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class TipoPrioridadController extends CustomRestController implements RESTController<TipoPrioridad> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(TipoPrioridadController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene prioridad", notes = "Obtiene detalle de una prioridad de la seccion nuevo tramite")
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
	@RequestMapping(value = "/tipoPrioridad", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TipoPrioridad> get(
			@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("::: Ejecutando el metodo GET de TipoPrioridad con el id " + id);

		TipoPrioridad item = null;
		try {

			item = mngrTipoPrioridad.fetch(Integer.valueOf((String) id));

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<TipoPrioridad>(item, HttpStatus.OK);
	}

	/**
	 * Obtiene la fecha de compromiso de una Prioridad
	 * 
	 * @param id
	 *            Identificador de la Prioridad
	 * @return Fecha de Compromiso
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tipoPrioridad/fechaCompromiso", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DiaFestivo> getFechaCompromiso(
			@RequestParam(value = "id", required = true) Serializable id) {

		log.debug("::: Ejecutando el metodo para obtener la fecha de compromiso de la prioridad con id " + id);

		Calendar fechaCompromiso = Calendar.getInstance();
		TipoPrioridad tipoPrioridad = null;

		try {

			tipoPrioridad = mngrTipoPrioridad.fetch(id);
			fechaCompromiso.add(Calendar.DATE, tipoPrioridad.getDias());

			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.isNotNull("key.dia")); // línea para evitar error de consulta null
			List<DiaFestivo> diasFestivos = (List<DiaFestivo>) mngrDiaFestivo.search(restrictions);

			int diaSemana = fechaCompromiso.get(Calendar.DAY_OF_WEEK);

			DiaFestivo diaFestivo = new DiaFestivo(fechaCompromiso.getTime());

			// Iteramos hasta que la fecha no sea un Sabado, Domingo o Festivo
			while ((Calendar.SATURDAY == diaSemana) || (Calendar.SUNDAY == diaSemana)
					|| diasFestivos.contains(diaFestivo)) {
				fechaCompromiso.add(Calendar.DATE, 1);
				diaSemana = fechaCompromiso.get(Calendar.DAY_OF_WEEK);
				diaFestivo = new DiaFestivo(fechaCompromiso.getTime());
			}

			return new ResponseEntity<DiaFestivo>(diaFestivo, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta prioridad", notes = "Consulta la lista de prioridad de la seccion nuevo tramite")
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
	@RequestMapping(value = "/tipoPrioridad", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) TipoPrioridad tipoPrioridad) {

		List<?> lst = new ArrayList<TipoPrioridad>();
		log.info("Parametros de busqueda :: " + tipoPrioridad);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (tipoPrioridad.getIdPrioridad() != null)
				restrictions.add(Restrictions.idEq(tipoPrioridad.getIdPrioridad()));

			if (tipoPrioridad.getDescripcion() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("descripcion", tipoPrioridad.getDescripcion(),
						MatchMode.ANYWHERE));

			if (tipoPrioridad.getArea() != null) {

				if (tipoPrioridad.getArea().getIdArea() != null)
					restrictions.add(Restrictions.eq("area.idArea", tipoPrioridad.getArea().getIdArea()));
			}
			if (tipoPrioridad.getDias() != null)
				restrictions.add(Restrictions.eq("dias", tipoPrioridad.getDias()));

			if (tipoPrioridad.getActivo() != null)
				restrictions.add(Restrictions.eq("activo", tipoPrioridad.getActivo()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.desc("descripcion"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrTipoPrioridad.search(restrictions, orders);

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
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Agregar prioridad", notes = "Agrega o edita una prioidad de la seccion nuevo tramite")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 201, message = "Creado"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@Override
	@RequestMapping(value = "/tipoPrioridad", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<TipoPrioridad> save(@RequestBody(required = true) TipoPrioridad tipoPrioridad)
			throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("TIPO PRIORIDAD A GUARDAR >> " + tipoPrioridad);

				if (tipoPrioridad.getIdPrioridad() == null) {
					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoPrioridad no se esten violando con este nuevo
					// registro
					//validateEntity(mngrTipoPrioridad, tipoPrioridad);
					
					List<Criterion> restrictions = new ArrayList<Criterion>();
					
					String descripcionTipoPrioridad= Normalizer.normalize(tipoPrioridad.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					
					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipoPrioridad, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoPrioridad.getArea().getIdArea()));
						
					List<TipoPrioridad> tipos = (List<TipoPrioridad>) mngrTipoPrioridad.search(restrictions);
						
					if(tipos!=null && tipos.size()>0) {
						tipos.forEach(tm -> {
							String tipoPrioridadTmp = Normalizer.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if(descripcionTipoPrioridad.equals(tipoPrioridadTmp)) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre", null);
							}
						});
					}
					// Guardamos la informacion
					mngrTipoPrioridad.save(tipoPrioridad);

					return new ResponseEntity<TipoPrioridad>(tipoPrioridad, HttpStatus.CREATED);
				} else {

					// Validamos que las reglas de validacion de la entidad Tipo
					// TipoPrioridad no se esten violando con este nuevo
					// registro
					
					List<Criterion> restrictions = new ArrayList<Criterion>();
					
					String descripcionTipoPrioridad= Normalizer.normalize(tipoPrioridad.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					
					restrictions.add(EscapedLikeRestrictions.ilike("descripcion", descripcionTipoPrioridad, MatchMode.ANYWHERE));
					restrictions.add(Restrictions.eq("area.idArea", tipoPrioridad.getArea().getIdArea()));
						
					List<TipoPrioridad> tipos = (List<TipoPrioridad>) mngrTipoPrioridad.search(restrictions);
						
					if(tipos!=null && tipos.size()>0) {
						tipos.forEach(tm -> {
							String tipoPrioridadTmp = Normalizer.normalize(tm.getDescripcion().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
							if(descripcionTipoPrioridad.equals(tipoPrioridadTmp) && !tipoPrioridad.getIdPrioridad().equals(tm.getIdPrioridad())) {
								throw new ConstraintViolationException("Ya existe un registro con el mismo nombre", null);
							}
						});
					}
					// Actualizamos la informacion
					mngrTipoPrioridad.update(tipoPrioridad);
					return new ResponseEntity<TipoPrioridad>(tipoPrioridad, HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<TipoPrioridad>(tipoPrioridad, HttpStatus.BAD_REQUEST);
			}
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
	 * Obtiene el primer valor de la tabla que se pase como parametro
	 * 
	 * @param idArea
	 *            Identificador del Area
	 * @param value
	 *            Tipo de valor a obtener
	 * @return Tipo de valor del Area
	 * @throws Exception
	 */
	public TipoPrioridad getDefaultValue(Integer idArea) throws Exception {

		List<Criterion> restrictions = new ArrayList<>();

		try {

			restrictions.add(Restrictions.eq("area.idArea", idArea));
			List<Order> orders = new ArrayList<>();

			orders.add(Order.asc("idPrioridad"));

			@SuppressWarnings("unchecked")
			List<TipoPrioridad> values = mngrTipoPrioridad.search(restrictions, orders, null, 1, 0);

			if (!values.isEmpty()) {
				return values.get(0);
			}

			log.error("No existe un Tipo de Prioridad para el Area con identificador " + idArea);
			throw new Exception("No existe un Tipo de prioridad para el Area con identificador " + idArea);

		} catch (Exception e) {

			log.error("Error al momento de obtener el Tipo de Prioridad");
			throw e;
		}

	}

}
