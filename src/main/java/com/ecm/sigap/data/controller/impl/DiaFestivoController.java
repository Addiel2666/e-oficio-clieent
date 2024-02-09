/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.Days;
import org.joda.time.LocalDate;
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
import com.ecm.sigap.data.model.DiaFestivo;
import com.ecm.sigap.data.model.DiaFestivoKey;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.DiaFestivo}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 * 
 * @author Alfredo Morales
 * @version 1.0.1
 *
 */
@RestController
public class DiaFestivoController extends CustomRestController implements RESTController<DiaFestivo> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(DiaFestivoController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/diaFestivo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DiaFestivo> get(@RequestParam(value = "id", required = true) Serializable id) {

		DiaFestivo item = null;

		try {

			item = mngrDiaFestivo.fetch(Long.valueOf((String) id));

			log.debug("::: Informacion del Dia Festivo " + item);

			return new ResponseEntity<DiaFestivo>(item, HttpStatus.OK);

		} catch (Exception e) {

			log.error(e.getMessage());
			return new ResponseEntity<DiaFestivo>(item, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	

	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta dias festivos", notes = "Consulta todos los dias festivos")
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
	
	@RequestMapping(value = "/diaFestivo", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) Map<String, Object> body) throws Exception {

		List<?> lst = new ArrayList<DiaFestivo>();
		log.info("Parametros de busqueda :: " + body);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();
			//Se coloca para realizar consulta general asignando el id
			restrictions.add(Restrictions.isNotNull("key.dia"));
			
			if (body.get("diaFestivoInicial") != null && body.get("diaFestivoFinal") != null)
				restrictions.add(Restrictions.between("key.dia", //
						new Date((Long) body.get("diaFestivoInicial")), //
						new Date((Long) body.get("diaFestivoFinal"))));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("key.dia"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrDiaFestivo.search(restrictions, orders);

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
	
	@ApiOperation(value = "Agregar dia festivo", notes = "Agrega un dia festivo al calendario")
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
	@RequestMapping(value = "/diaFestivo", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<DiaFestivo> save(//
			@RequestBody(required = true) DiaFestivo diaFestivo) throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			if (!esSoloLectura(userId)) {

				log.debug("::>> DiaFestivo A GUARDAR >> " + diaFestivo);

				DiaFestivo existente = mngrDiaFestivo.fetch(diaFestivo.getKey());

				if (diaFestivo.getKey().getDia() != null & (existente == null
						|| existente.getKey().getIdCalendario() != diaFestivo.getKey().getIdCalendario())) {

					checkDateInBetween(diaFestivo, diaFestivo.getKey().getIdCalendario());

					mngrDiaFestivo.save(diaFestivo);

				} else if (diaFestivo.getKey().getDia() != null) {

					// checkDateInBetween(diaFestivo);
					mngrDiaFestivo.update(diaFestivo);

				} else

					throw new BadRequestException();

				return new ResponseEntity<DiaFestivo>(diaFestivo, HttpStatus.OK);

			} else {

				return new ResponseEntity<DiaFestivo>(diaFestivo, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Valida si la fecha inicio no se encuentra en rago de otro fecha ya
	 * registrada.
	 * 
	 * @param diaFestivo
	 */
	private void checkDateInBetween(DiaFestivo diaFestivo, Integer idCalendario) throws BadRequestException {
		{
			Criterion rhs;

			{

				LogicalExpression start_not_between = Restrictions.and(//
						Restrictions.ge("diaFin", diaFestivo.getKey().getDia()), //
						Restrictions.lt("key.dia", diaFestivo.getKey().getDia()));

				if (diaFestivo.getDiaFin() == null) {

					rhs = start_not_between;

				} else {

					LogicalExpression end_not_between = Restrictions.and(//
							Restrictions.ge("diaFin", diaFestivo.getDiaFin()), //
							Restrictions.lt("key.dia", diaFestivo.getDiaFin()));

					rhs = Restrictions.or(start_not_between, end_not_between);

				}

			}

			// * * * * * * * * * * * * * * * * * * * * * *

			Criterion lhs = Restrictions.isNotNull("diaFin");

			List<Criterion> restrictions_search = new ArrayList<>();

			restrictions_search.add(Restrictions.and(lhs, rhs));

			restrictions_search.add(Restrictions.eq("key.idCalendario", idCalendario));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<?> lst = mngrDiaFestivo.search(restrictions_search);

			if (!lst.isEmpty())
				throw new BadRequestException(
						"Intento guardar un registro que se encuentra dentro del rango de fechas de uno existente.");
		}

		// validacion inversa.

		if (diaFestivo.getDiaFin() != null) {

			// * * * * * * * * * * * * * * * * * * * * * *

			LogicalExpression is_saved_between_new_date = Restrictions.and(//
					Restrictions.lt("key.dia", diaFestivo.getDiaFin()), //
					Restrictions.ge("key.dia", diaFestivo.getKey().getDia()));

			List<Criterion> restrictions_search = new ArrayList<>();

			restrictions_search.add(is_saved_between_new_date);

			restrictions_search.add(Restrictions.eq("key.idCalendario", idCalendario));

			// * * * * * * * * * * * * * * * * * * * * * *
			List<?> lst = mngrDiaFestivo.search(restrictions_search);

			if (!lst.isEmpty())
				throw new BadRequestException(
						"Intento guardar un registro en un rango de fechas que contiene almenos un ya existente.");

		}

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
	
	@ApiOperation(value = "Eliminar dia festivo", notes = "Elimina un dia festivo del calendario")
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
	
	@RequestMapping(value = "/diaFestivo", method = RequestMethod.DELETE)
	public void deleteDiaFestivo(@RequestParam(value = "id", required = true) Long id_,
			@RequestParam(value = "idCalendario", required = true) Integer idCalendario) {

		try {

			DiaFestivoKey dfk = new DiaFestivoKey();

			Date id = new Date(id_);

			dfk.setDia(id);
			dfk.setIdCalendario(idCalendario);

			log.debug("DIA FESTIVO A ELIMINAR >> " + dfk);

			mngrDiaFestivo.delete(mngrDiaFestivo.fetch(dfk));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(DiaFestivo object) throws Exception {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ecm.sigap.data.controller.RESTController#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Serializable id) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Calcula la fecha final despues de agregar los dias indicados y revisar fechas
	 * en el calendario indicado.
	 * 
	 * @param fechaStart
	 * @param daysToAdd
	 * @param calendarId
	 * @return
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Calcular fecha compromiso", notes = "Calcula la fecha final de un tramite de acuerdo a la instruccion y prioridada")
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
	
	@RequestMapping(value = "/diaFestivo/calcula", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> calcFechaCompromiso(
			@RequestParam(value = "fechaStart", required = true) Long fechaStart, //
			@RequestParam(value = "daysToAdd", required = true) Integer daysToAdd, //
			@RequestParam(value = "calendarId", required = true) Integer calendarId) {
		try {

			Map<String, Object> result = new HashMap<String, Object>();

			// * * * * * * * * * * * * * * * * * * * * * *

			// fecha de inicio
			Date start = new Date(fechaStart);

			// fecha final
			Calendar c = Calendar.getInstance();
			c.setTime(start);

			// se agregan los dias inciales
			c.add(Calendar.DATE, daysToAdd);

			int toAdd;
			int weekendsToAdd;
			int feriadosToAdd;

			Date tmpDate;

			do {

				tmpDate = c.getTime();

				weekendsToAdd = getNotWorkingDaysBetweenTwoDates(start, c.getTime());

				feriadosToAdd = conFeriados(start, c.getTime(), calendarId);

				toAdd = weekendsToAdd + feriadosToAdd;

				if (toAdd > 0) {
					c.add(Calendar.DATE, toAdd);
					start = tmpDate;
				}

			} while (toAdd > 0);

			// - - - - - - - - -

			result.put("endDate", c.getTime());

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (

		Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Obtiene el numero dias total recorridos en dias habiles
	 * 
	 * @param start
	 * @param numDias
	 * @return
	 */
	private int getSaturdayAndMonday(Date start, Integer numDias) {

		int diaHabil = 0;
		int diaInHabil = 0;
		Calendar inicio = Calendar.getInstance();
		inicio.setTime(start);

		while (diaHabil < numDias) {
			if (inicio.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					&& inicio.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				diaHabil++;
			} else {
				diaInHabil++;
			}
			inicio.add(Calendar.DATE, 1);
		}

		numDias = diaHabil + diaInHabil;

		return numDias;
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static int getNotWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);

		int notWorkDays = 0;

		// Return 0 if start and end are the same
		if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
			return 0;
		}

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			startCal.setTime(endDate);
			endCal.setTime(startDate);
		}

		do {
			// excluding start date
			startCal.add(Calendar.DAY_OF_MONTH, 1);

			if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				++notWorkDays;
			}

		} while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); // excluding end date

		return notWorkDays;
	}

	/**
	 * Obtiene los eventos entre el rango de fecas indicadas y que sean del
	 * calendario indicado.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private List<DiaFestivo> getEventsInRange(Date start, Date end, Integer idCalendario) {
		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.ge("key.dia", start));
		restrictions.add(Restrictions.lt("key.dia", end));

		restrictions.add(Restrictions.eq("key.idCalendario", idCalendario));

		@SuppressWarnings("unchecked")
		List<DiaFestivo> pulse = (List<DiaFestivo>) mngrDiaFestivo.search(restrictions);
		return pulse;
	}

	/**
	 * agregar dia o dias de manera recursiva.
	 * 
	 * @param c
	 * @param start
	 * @param end
	 * @param idCalendario
	 */
	private int conFeriados(Date start, Date end, Integer idCalendario) {

		List<DiaFestivo> pulse = getEventsInRange(start, end, idCalendario);
		int total = 0;

		for (DiaFestivo df : pulse) {

			if (df.getDiaFin() == null) {
				total++;
			} else {
				int days = daysBetween(df.getKey().getDia(), df.getDiaFin());
				if (days == 0) {
					days = 1;
				}
				total = total + days;
			}
		}

		return total;
	}

	/**
	 * agregar dia o dias de manera recursiva.
	 * 
	 * @param c
	 * @param start
	 * @param end
	 * @param idCalendario
	 */
	private void recalculateEndDate(Calendar c, Date start, Date end, Integer idCalendario) {

		List<DiaFestivo> pulse = getEventsInRange(start, end, idCalendario);

		for (DiaFestivo df : pulse) {

			if (df.getDiaFin() == null) {
				c.add(Calendar.DATE, 1);
			} else {
				int days = daysBetween(df.getKey().getDia(), df.getDiaFin());
				c.add(Calendar.DATE, days);
			}
		}

		if (!getEventsInRange(end, c.getTime(), idCalendario).isEmpty()) {
			// ahora hay mas q agregar.
			recalculateEndDate(c, end, c.getTime(), idCalendario);
		}

	}

	/**
	 * Cuenta el numero de dias entre las fechas indicadas.
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	private static int daysBetween(Date d1, Date d2) {
		return Days.daysBetween(new LocalDate(d1.getTime()), //
				new LocalDate(d2.getTime())).getDays();
	}

}
