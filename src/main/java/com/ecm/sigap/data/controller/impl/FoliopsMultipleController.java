/**
 * Copyright (c) 2020 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
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
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.FolioAreaKeyMultiple;
import com.ecm.sigap.data.model.FolioAreaMultilple;
import com.ecm.sigap.data.model.FolioPSMultiple;
import com.ecm.sigap.data.model.util.FoliadoraMultiple;
import com.google.common.base.Stopwatch;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.FolioPSMultiple}
 * 
 * @author ECM SOLUTIONS
 * @version 1.0
 *
 */
@RestController
public class FoliopsMultipleController extends CustomRestController implements RESTController<FolioPSMultiple> {

	/** */
	private static final Logger log = LogManager.getLogger(FoliopsMultipleController.class);

	/**
	 * Referencia hacia el REST controller de {@link FolioAreaMultiple}.
	 */
	@Autowired
	private FolioAreaMultilpleController folioAreaMultipleController;

	@Override
	@RequestMapping(value = "/foliopsMultiple", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<FolioPSMultiple> get(
			@RequestParam(value = "id", required = true) Serializable id) {
		return null;
	}
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Eliminar foliadora", notes = "Elimina una foliadora")
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

	@RequestMapping(value = "/foliopsMultiple", method = RequestMethod.DELETE)
	public void delete(@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		log.debug("FOLIOPS A ELIMINAR >> " + id);
		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		try {

			folioAreaMultipleController.delete(id);
			mngrFoliopsmultiple
					.delete(mngrFoliopsmultiple.fetch(new FolioPSMultiple(Integer.valueOf((String) id), idArea)));

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	@RequestMapping(value = "/foliopsMultiple", method = RequestMethod.POST)
	public ResponseEntity<List<?>> search(@RequestBody(required = true) FolioPSMultiple folioPSMult) throws Exception {

		List<FolioPSMultiple> lst = new ArrayList<FolioPSMultiple>();
		log.info("Parametros de busqueda :: " + folioPSMult);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (folioPSMult.getId() != null)
				restrictions.add(Restrictions.idEq(folioPSMult.getId()));

			if ((folioPSMult.getPrefijoFolio() != null) && (!folioPSMult.getPrefijoFolio().isEmpty()))
				restrictions.add(EscapedLikeRestrictions.ilike("prefijoFolio", folioPSMult.getPrefijoFolio(),
						MatchMode.ANYWHERE));

			if ((folioPSMult.getSufijoFolio() != null) && (!folioPSMult.getSufijoFolio().isEmpty()))
				restrictions.add(
						EscapedLikeRestrictions.ilike("sufijoFolio", folioPSMult.getSufijoFolio(), MatchMode.ANYWHERE));

			if (folioPSMult.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", folioPSMult.getIdArea()));

			if ((folioPSMult.getDescripcion() != null) && (!folioPSMult.getDescripcion().isEmpty()))
				restrictions.add(
						EscapedLikeRestrictions.ilike("descripcion", folioPSMult.getDescripcion(), MatchMode.ANYWHERE));

			if ((folioPSMult.getComparteFolioSN() != null) && (!folioPSMult.getComparteFolioSN().isEmpty()))
				restrictions.add(Restrictions.eq("comparteFolioSN", folioPSMult.getComparteFolioSN()));

			if ((folioPSMult.getFoliadorUnicoSN() != null) && (!folioPSMult.getFoliadorUnicoSN().isEmpty()))
				restrictions.add(Restrictions.eq("foliadorUnicoSN", folioPSMult.getFoliadorUnicoSN()));

			if ((folioPSMult.getTipo() != null) && (!folioPSMult.getTipo().isEmpty()))
				restrictions.add(Restrictions.eq("tipo", folioPSMult.getTipo()));

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.asc("id"));
			// * * * * * * * * * * * * * * * * * * * * * *

			lst = (List<FolioPSMultiple>) mngrFoliopsmultiple.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<FolioPSMultiple>() {
				@Override
				public int compare(FolioPSMultiple f1, FolioPSMultiple f2) {
					return f1.getId().compareTo(f2.getId());
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
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta foliadora heredada", notes = "Consulta las foliadoras heredadas")
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
	@RequestMapping(value = "/foliopsMultiple/foliadora", method = RequestMethod.POST)
	public ResponseEntity<List<?>> getFoliadorasMultiples(@RequestBody(required = true) FolioPSMultiple folioPSMult)
			throws Exception {

		FoliadoraMultiple fm = null;
		List<FolioPSMultiple> lst = new ArrayList<FolioPSMultiple>();
		List<FoliadoraMultiple> lstFm = new ArrayList<FoliadoraMultiple>();
		Map<String, Integer> foliosAreaMultiple = new HashMap<String, Integer>();
		log.info("Parametros de busqueda :: " + folioPSMult);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (folioPSMult.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", folioPSMult.getIdArea()));
			
			if (folioPSMult.getId() != null)
				restrictions.add(Restrictions.eq("id", folioPSMult.getId()));
			
			if ((folioPSMult.getComparteFolioSN() != null) && (!folioPSMult.getComparteFolioSN().isEmpty()))
				restrictions.add(Restrictions.eq("comparteFolioSN", folioPSMult.getComparteFolioSN()));

			// List<Order> orders = new ArrayList<Order>();

			// orders.add(Order.asc("id"));
			// * * * * * * * * * * * * * * * * * * * * * *

			lst = (List<FolioPSMultiple>) mngrFoliopsmultiple.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<FolioPSMultiple>() {
				@Override
				public int compare(FolioPSMultiple f1, FolioPSMultiple f2) {
					return f1.getId().compareTo(f2.getId());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			log.debug("Size found >> " + lst.size());

			for (FolioPSMultiple fpsm : lst) {
				fm = new FoliadoraMultiple();
				// FOLIOPSMULTIPLE
				fm.setPrefijoFolio(fpsm.getPrefijoFolio());
				fm.setSufijoFolio(fpsm.getSufijoFolio());
				fm.setId(fpsm.getId());
				fm.setIdArea(fpsm.getIdArea());
				fm.setDescripcion(fpsm.getDescripcion());
				fm.setFoliadorUnicoSN(fpsm.getFoliadorUnicoSN());
				fm.setComparteFolioSN(fpsm.getComparteFolioSN());
				fm.setIdFolioHeredado(fpsm.getIdFolioHeredado());
				fm.setIdAreaHereda(fpsm.getIdAreaHereda());
				fm.setTipoFoliadora(fpsm.getTipo());
				// FOLIOSAREA
				foliosAreaMultiple = getFolioAreaMultiple(fpsm.getId());
				if (foliosAreaMultiple != null && !foliosAreaMultiple.isEmpty()) {
					fm.setFolioAsunto(foliosAreaMultiple.get("1"));
					fm.setFolioRespuesta(foliosAreaMultiple.get("0"));
				}
				lstFm.add(fm);
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			
			throw e;

		}

		return new ResponseEntity<List<?>>(lstFm, HttpStatus.OK);
	}
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Consulta folios multiple", notes = "Consulta los folios multiples heredadas")
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
	@RequestMapping(value = "/foliopsMultiple/PropiasHeredadas", method = RequestMethod.POST)
	public ResponseEntity<List<?>> getFoliadorasPropiasHerencia(
			@RequestBody(required = true) FolioPSMultiple folioPSMult) throws Exception {

		FoliadoraMultiple fm = null;
		List<FolioPSMultiple> lst = new ArrayList<FolioPSMultiple>();
		List<FoliadoraMultiple> lstFm = new ArrayList<FoliadoraMultiple>();
		Map<String, Integer> foliosAreaMultiple = new HashMap<String, Integer>();
		log.info("Parametros de busqueda :: " + folioPSMult);

		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (folioPSMult.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", folioPSMult.getIdArea()));

			lst = (List<FolioPSMultiple>) mngrFoliopsmultiple.search(restrictions, null);

			for (FolioPSMultiple fh : lst) {
				if ("H".equals(fh.getTipo())) {
					FolioPSMultiple foliadoraHeredada = mngrFoliopsmultiple
							.fetch(new FolioPSMultiple(Integer.valueOf(fh.getIdFolioHeredado()), fh.getIdAreaHereda()));
					// fh.setId(foliadoraHeredada.getId());
					fh.setPrefijoFolio(foliadoraHeredada.getPrefijoFolio());
					fh.setSufijoFolio(foliadoraHeredada.getSufijoFolio());
					fh.setFoliadorUnicoSN(foliadoraHeredada.getFoliadorUnicoSN());
				}
			}

			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<FolioPSMultiple>() {
				@Override
				public int compare(FolioPSMultiple f1, FolioPSMultiple f2) {
					return f1.getId().compareTo(f2.getId());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			log.debug("Size found >> " + lst.size());

			for (FolioPSMultiple fpsm : lst) {
				fm = new FoliadoraMultiple();
				// FOLIOPSMULTIPLE
				fm.setPrefijoFolio(fpsm.getPrefijoFolio());
				fm.setSufijoFolio(fpsm.getSufijoFolio());
				fm.setId(fpsm.getId());
				fm.setIdArea(fpsm.getIdArea());
				fm.setDescripcion(fpsm.getDescripcion());
				fm.setFoliadorUnicoSN(fpsm.getFoliadorUnicoSN());
				fm.setComparteFolioSN(fpsm.getComparteFolioSN());
				fm.setIdFolioHeredado(fpsm.getIdFolioHeredado());
				fm.setIdAreaHereda(fpsm.getIdAreaHereda());
				fm.setTipoFoliadora(fpsm.getTipo());
				// FOLIOSAREA
				foliosAreaMultiple = getFolioAreaMultiple(
						"H".equals(fpsm.getTipo()) ? Integer.valueOf(fpsm.getIdFolioHeredado()) : fpsm.getId());
				if (foliosAreaMultiple != null && !foliosAreaMultiple.isEmpty()) {
					fm.setFolioAsunto(foliosAreaMultiple.get("1"));
					fm.setFolioRespuesta(foliosAreaMultiple.get("0"));
				}
				lstFm.add(fm);
			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());
			
			throw e;

		}

		return new ResponseEntity<List<?>>(lstFm, HttpStatus.OK);
	}
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear foliadora", notes = "Crea una nueva foliadora")
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

	@RequestMapping(value = "/foliopsMultiple", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<FoliadoraMultiple> save(
			@RequestBody(required = true) FoliadoraMultiple dataFolioArea) throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			FolioPSMultiple folioPSMult = null;

			log.debug("::>> FOLIOPSMULTIPLE A GUARDAR O ACTUALIZAR >> " + dataFolioArea);

			if (!esSoloLectura(userId)) {
				if (dataFolioArea.getId() != null) {

					/* * * * U P D A T E * * * */

					// ACTUALIZANDO FOLIO PREFIJO/SUFIJO

					FolioPSMultiple fpstmp = mngrFoliopsmultiple
							.fetch(new FolioPSMultiple(dataFolioArea.getId(), dataFolioArea.getIdArea()));
					String des = StringUtils.isBlank(fpstmp.getDescripcion()) ? "" : fpstmp.getDescripcion();
					if (!(dataFolioArea.getDescripcion().equalsIgnoreCase(des))
							&& !(validarDescripcion(dataFolioArea.getIdArea(), dataFolioArea.getDescripcion())))
						return new ResponseEntity<FoliadoraMultiple>(dataFolioArea, HttpStatus.CONFLICT);

					folioPSMult = new FolioPSMultiple();
					folioPSMult.setId(dataFolioArea.getId());
					folioPSMult.setDescripcion(dataFolioArea.getDescripcion());
					folioPSMult.setPrefijoFolio(dataFolioArea.getPrefijoFolio());
					folioPSMult.setSufijoFolio(dataFolioArea.getSufijoFolio());
					folioPSMult.setIdArea(idArea);
					folioPSMult.setFoliadorUnicoSN(dataFolioArea.getFoliadorUnicoSN());
					folioPSMult.setComparteFolioSN(dataFolioArea.getComparteFolioSN());
					folioPSMult.setIdFolioHeredado(dataFolioArea.getIdFolioHeredado());
					folioPSMult.setIdAreaHereda(dataFolioArea.getIdAreaHereda());
					folioPSMult.setTipo(dataFolioArea.getTipoFoliadora());
					mngrFoliopsmultiple.update(folioPSMult);

					// ENVIANDO A ACTUALIZAR FOLIO MULTIPLE X AREA
					if (dataFolioArea.getFolioRespuesta() != null) {
						FolioAreaMultilple folioRespuesta = new FolioAreaMultilple(
								new FolioAreaKeyMultiple(dataFolioArea.getId(), 0), dataFolioArea.getFolioRespuesta(),
								null);
						folioAreaMultipleController.save(folioRespuesta);
					}
					if (dataFolioArea.getFolioAsunto() != null) {
						FolioAreaMultilple folioAsunto = new FolioAreaMultilple(
								new FolioAreaKeyMultiple(dataFolioArea.getId(), 1), dataFolioArea.getFolioAsunto(),
								null);
						folioAreaMultipleController.save(folioAsunto);
					}

					log.debug("::>> Registro Actualizado");

					return new ResponseEntity<FoliadoraMultiple>(dataFolioArea, HttpStatus.OK);

				} else {

					/* * * * I N S E R T * * * */

					if (!validarDescripcion(dataFolioArea.getIdArea(), dataFolioArea.getDescripcion()))
						return new ResponseEntity<FoliadoraMultiple>(dataFolioArea, HttpStatus.CONFLICT);

					// Guardamos la informacion
					folioPSMult = new FolioPSMultiple();
					folioPSMult.setDescripcion(dataFolioArea.getDescripcion());
					folioPSMult.setPrefijoFolio(dataFolioArea.getPrefijoFolio());
					folioPSMult.setSufijoFolio(dataFolioArea.getSufijoFolio());
					folioPSMult.setIdArea(idArea);
					folioPSMult.setFoliadorUnicoSN(dataFolioArea.getFoliadorUnicoSN());
					folioPSMult.setComparteFolioSN(dataFolioArea.getComparteFolioSN());
					folioPSMult.setIdFolioHeredado(dataFolioArea.getIdFolioHeredado());
					folioPSMult.setIdAreaHereda(dataFolioArea.getIdAreaHereda());
					folioPSMult.setTipo(dataFolioArea.getTipoFoliadora());
					mngrFoliopsmultiple.save(folioPSMult);

					Stopwatch timer = Stopwatch.createStarted();
					ExecutorService taskExecutor = createFolioMultipleAsunto(folioPSMult.getId(),
							dataFolioArea.getFolio());
					taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					timer.stop();

					log.debug("::>> Registro Guardado");

					return new ResponseEntity<FoliadoraMultiple>(dataFolioArea, HttpStatus.CREATED);

				}

			} else {

				return new ResponseEntity<FoliadoraMultiple>(dataFolioArea, HttpStatus.BAD_REQUEST);

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	@Override
	public ResponseEntity<FolioPSMultiple> save(FolioPSMultiple object) throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * Gets the folio area. Obtiene Folio Multiple de Asunto y Respuesta de un
	 * area
	 *
	 * @param idArea
	 *            the id area
	 * @return the map
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> getFolioAreaMultiple(int idFoliopsMultiple) throws Exception {

		Map<String, Integer> items = new HashMap<String, Integer>();
		List<FolioAreaMultilple> lst = new ArrayList<FolioAreaMultilple>();
		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();
			// List<Order> orders = new ArrayList<Order>();

			restrictions.add(Restrictions.eq("folioAreaKeyMul.idFoliopsMultiple", idFoliopsMultiple));
			restrictions.add(Restrictions.in("folioAreaKeyMul.idTipoFolio", new Object[] { 0, 1 }));
			// orders.add(Order.asc("folioAreaKeyMul.idTipoFolio"));

			lst = (List<FolioAreaMultilple>) mngrFolioAreaMultiple.search(restrictions, null);

			// * * * * * ORDENA EL LIST POR DESCRIPCION (ASC) * * * * * *
			Collections.sort(lst, new Comparator<FolioAreaMultilple>() {
				@Override
				public int compare(FolioAreaMultilple f1, FolioAreaMultilple f2) {
					return f1.getFolioAreaKeyMul().getIdTipoFolio().compareTo(f2.getFolioAreaKeyMul().getIdTipoFolio());
				}
			});
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

			if (lst != null && !lst.isEmpty()) {
				for (FolioAreaMultilple folioAreaMul : lst) {
					items.put(String.valueOf(folioAreaMul.getFolioAreaKeyMul().getIdTipoFolio()),
							folioAreaMul.getFolio());
				}
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		return items;
	}

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
	 * @return
	 */
	private ExecutorService createFolioMultipleAsunto(Integer idFoliopsMultiple, Integer folio) {

		ThreadFactory threadFactory = new WorkerThreadFactory("ecm");
		ExecutorService taskExecutor = Executors.newFixedThreadPool(11, threadFactory);

		// folios de documentos
		{
			Future<?> future = taskExecutor.submit(//
					new SaveFoliosMultiplesAsuntosThread(idFoliopsMultiple, folio));
			try {
				future.get();
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
		}

		taskExecutor.shutdown();
		return taskExecutor;
	}

	public class SaveFoliosMultiplesAsuntosThread implements Callable<Object> {

		private Integer idFoliopsMultiple;
		private Integer folio;

		public SaveFoliosMultiplesAsuntosThread(Integer idFoliopsMultiple, Integer folio) {
			this.idFoliopsMultiple = idFoliopsMultiple;
			this.folio = folio;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public Object call() throws Exception {

			// tipoFolio 0 = asunto
			// tipoFolio 1 = respuesta
			// tipoFolio 2 = customimss??
			for (int i = 0; i <= 2; i++) {
				FolioAreaMultilple fa = new FolioAreaMultilple();
				fa.setFolioAreaKeyMul(new FolioAreaKeyMultiple(idFoliopsMultiple, i));
				fa.setFolio(folio);

				try {
					mngrFolioAreaMultiple.save(fa);
				} catch (Exception e) {
					
					throw e;
				}
			}
			return null;

		}

	}

	@SuppressWarnings("unchecked")
	public boolean validarDescripcion(Integer idarea, String descripcion) {
		boolean isValid = true;
		List<FolioPSMultiple> lst = new ArrayList<FolioPSMultiple>();
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("idArea", idarea));
		restrictions.add(Restrictions.eq("descripcion", descripcion).ignoreCase());
		lst = (List<FolioPSMultiple>) mngrFoliopsmultiple.search(restrictions);
		if (lst != null && lst.size() > 0) {
			isValid = false;
		}
		return isValid;
	}

}
