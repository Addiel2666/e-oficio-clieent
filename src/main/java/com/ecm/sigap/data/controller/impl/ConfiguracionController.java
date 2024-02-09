package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
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

import com.ecm.sigap.config.DBVendor;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Configuracion;
import com.ecm.sigap.data.model.ConfiguracionKey;
import com.ecm.sigap.data.model.Folio;
import com.ecm.sigap.data.model.FolioArchivistica;
import com.ecm.sigap.data.model.FolioArea;
import com.ecm.sigap.data.model.FolioAreaKey;
import com.ecm.sigap.data.model.FolioClave;
import com.ecm.sigap.data.model.FolioKey;
import com.ecm.sigap.data.model.FolioPS;
import com.ecm.sigap.data.model.FolioPSClave;
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroKey;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.ConfiguracionArea;
import com.ecm.sigap.data.model.util.ConfiguracionNotificacion;
import com.ecm.sigap.data.model.util.TipoNotificacion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Configuracion}
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@RestController
public class ConfiguracionController extends CustomRestController implements RESTController<ConfiguracionNotificacion> {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(ConfiguracionController.class);

	/**
	 * Referencia hacia el REST controller de {@link Folio}.
	 */
	@Autowired
	private FolioController folioController;

	/**
	 * Referencia hacia el REST controller de {@link FolioClave}.
	 */
	@Autowired
	private FolioClaveController folioClaveController;

	/**
	 * Referencia hacia el REST controller de {@link Parametro}.
	 */
	@Autowired
	private ParametroController parametroController;

	/**
	 * Referencia hacia el REST controller de {@link FolioArea}.
	 */
	@Autowired
	private FolioAreaController folioAreaController;

	/**
	 * Referencia hacia el REST controller de {@link FolioPS}.
	 */
	@Autowired
	private FoliopsController foliopsController;

	/**
	 * Referencia hacia el REST controller de
	 * {@link com.ecm.sigap.data.model.FolioPSClave}.
	 */
	@Autowired
	private FoliopsclaveController foliopsclaveController;

	/**
	 * Referencia hacia el REST controller de {@link Folio}.
	 */
	@Autowired
	private FolioArchivisticaController folioArchivisticaController;

	/** */
	@Autowired
	private DBVendor dbVendor;

	/**
	 * Configuracio usuario.
	 *
	 * @param idUsuario the id usuario
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene configuracion usuario", notes = "Obtiene el detalle de la configuracion del usuario")
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
	@RequestMapping(value = "/configuracion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ConfiguracionNotificacion> configuracioUsuario(
			@RequestParam(value = "idUsuario", required = true) Serializable idUsuario) throws Exception {

		List<Configuracion> lst = new ArrayList<Configuracion>();
		ConfiguracionNotificacion configNotificacion = new ConfiguracionNotificacion();

		Map<String, Boolean> mapConf = new HashMap<>();
		log.info("Parametros de busqueda :: " + String.valueOf((String) idUsuario));
		Usuario usuario = null;
		try {

			usuario = mngrUsuario.fetch(String.valueOf((String) idUsuario));
			if (usuario == null) {
				return new ResponseEntity<ConfiguracionNotificacion>(configNotificacion, HttpStatus.BAD_REQUEST);
			}
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			restrictions.add(Restrictions.eq("configuracionKey.usuario", usuario));
			restrictions.add(Restrictions.eq("configuracionKey.idConfiguracion", "NOTIFICACION"));

			List<Order> orders = new ArrayList<Order>();

			// * * * * * * * * * * * * * * * * * * * * * *

			lst = (List<Configuracion>) mngrConfiguracion.search(restrictions, orders);

			for (Configuracion configuracion : lst) {
				String valor = configuracion.getValor();
				mapConf.put(configuracion.getConfiguracionKey().getClave().getTipo(), "S".equalsIgnoreCase(valor));
			}
			configNotificacion.setNotificacion(mapConf);
			configNotificacion.setUsuario(usuario);

			log.debug("Size found >> " + lst.size());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<ConfiguracionNotificacion>(configNotificacion, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#save(java.lang.Object)
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Guardar configuracion usuario", notes = "Guarda la configuracion del usuario")
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
	
	@RequestMapping(value = "/configuracion", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<ConfiguracionNotificacion> save(
			@RequestBody(required = true) ConfiguracionNotificacion configuracioUsuario) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("::>> CONFIGURACION A GUARDAR >> " + configuracioUsuario);
				Usuario usuario = null;
				if (configuracioUsuario.getUsuario() != null
						&& configuracioUsuario.getUsuario().getIdUsuario() != null) {

					usuario = mngrUsuario.fetch(configuracioUsuario.getUsuario().getIdUsuario());
					if (usuario == null) {
						return new ResponseEntity<ConfiguracionNotificacion>(configuracioUsuario,
								HttpStatus.BAD_REQUEST);
					}

				} else {
					return new ResponseEntity<ConfiguracionNotificacion>(configuracioUsuario, HttpStatus.BAD_REQUEST);
				}

				ConfiguracionKey configuracionKey = new ConfiguracionKey();

				for (Map.Entry<String, Boolean> entry : configuracioUsuario.getNotificacion().entrySet()) {

					configuracionKey.setIdConfiguracion("NOTIFICACION");
					configuracionKey.setUsuario(usuario);
					configuracionKey.setClave(TipoNotificacion.fromString(entry.getKey()));

					Configuracion config = mngrConfiguracion.fetch(configuracionKey);

					Boolean value = entry.getValue();

					if (config != null) {

						Boolean tmp = "S".equalsIgnoreCase(config.getValor());

						if (tmp != value) {
							config.setValor(value ? "S" : "N");
							mngrConfiguracion.update(config);
						} else {
							continue;
						}

					} else {

						config = new Configuracion();
						config.setConfiguracionKey(configuracionKey);
						config.setValor(value ? "S" : "N");
						mngrConfiguracion.save(config);

					}

				}

				return new ResponseEntity<ConfiguracionNotificacion>(configuracioUsuario, HttpStatus.OK);
			} else {
				return new ResponseEntity<ConfiguracionNotificacion>(configuracioUsuario, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Configuracio area.
	 *
	 * @param idArea the id area
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene configuracion area", notes = "Obtiene el detalle de la configuracion del area")
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/configuracion/area", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ConfiguracionArea> configuracioArea(
			@RequestParam(value = "idArea", required = true) Serializable idArea) throws Exception {

		ConfiguracionArea configArea = new ConfiguracionArea();

		try {

			Area area = mngrArea.fetch(Integer.valueOf((String) idArea));
			Map<String, Integer> foliosArea = new HashMap<String, Integer>();
			int areaFolio;

			if (area != null) {

				List<Parametro> listParametro = new ArrayList<Parametro>();
				List<Criterion> restrictionsParametro = new ArrayList<Criterion>();

				restrictionsParametro.add(Restrictions.eq("parametroKey.idArea", area.getIdArea()));
				restrictionsParametro
						.add(Restrictions.in("parametroKey.idSeccion", new Object[] { "FOLIODOC", "NOTIPREFT" }));

				FolioPS foliops = mngrFoliops.fetch(area.getIdArea());
				FolioPSClave foliopsclave = mngrFoliopsclave.fetch(area.getIdArea());

				FolioArchivistica folioArchivistica = mngrFolioArchivistica.fetch(area.getIdArea());

				configArea.setIdArea(area.getIdArea());
				configArea.setFoliops(foliops);
				configArea.setFoliopsclave(foliopsclave);
				configArea.setFolioArchivistica(folioArchivistica);

				Integer folio = folioController.getFolioDisponible(idArea).getBody().getFolioKey().getFolio();
				configArea.setFolioDisponible(folio);

				Integer folioClave = folioClaveController.getFolioDisponible(idArea).getBody().getFolioKey().getFolio();
				configArea.setFolioClave(folioClave);

				listParametro = (List<Parametro>) mngrParametro.search(restrictionsParametro, null);

				for (Parametro param : listParametro) {

					switch (param.getParametroKey().getIdClave()) {
					case "COMPARTIR":
						configArea.setComparteFolioSN(param.getValor());
						break;
					case "UNICO":
						configArea.setFoliadorUnicoSN(param.getValor());
						break;
					case "TIPO":
						configArea.setTipo(param.getValor());
						break;
					case "IDAREAHEREDA":
						configArea.setIdAreaHeredada(param.getValor());
						break;
					case "ASUNTO":
						configArea.setDiasPreNotAsunto(param.getValor());
						break;
					case "TURNO":
						configArea.setDiasPreNotTramite(param.getValor());
						break;
					}
				}

				// Valida si la configuraci�n actual es Heredada y Obtene
				// FolioAsunto y FolioRrspuesta.
				if (configArea.getIdAreaHeredada() != null && !configArea.getIdAreaHeredada().isEmpty()) {

					areaFolio = Integer.valueOf(configArea.getIdAreaHeredada());

				} else {

					areaFolio = Integer.valueOf((String) idArea);

				}

				foliosArea = getFolioArea(areaFolio);
				if (foliosArea != null && !foliosArea.isEmpty()) {
					configArea.setFolioAsunto(foliosArea.get("1"));
					configArea.setFolioRespuesta(foliosArea.get("0"));
				}

			} else {
				throw new Exception("El area no existe");
			}

		} catch (

		Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

		return new ResponseEntity<ConfiguracionArea>(configArea, HttpStatus.OK);

	}

	/**
	 * Save config area.
	 *
	 * @param configuracionArea the configuracion area
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Guardar configuracion area", notes = "Guarda la configuracion del area")
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
	
	@RequestMapping(value = "/configuracion/area", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<ConfiguracionArea> saveConfigArea(
			@RequestBody(required = true) ConfiguracionArea configuracionArea) throws Exception {

		log.debug("CONFIGURACION AREA A GUARDAR >> " + configuracionArea);

		try {
			if (configuracionArea.getIdArea() != null && configuracionArea.getFolioDisponible() != null) {

				Integer idArea = configuracionArea.getIdArea();
				Integer folio = configuracionArea.getFolioDisponible();
				Integer folioClaveint = configuracionArea.getFolioClave();
				Integer folioDisponibleActual = folioController.getFolioDisponible(String.valueOf(idArea)).getBody()
						.getFolioKey().getFolio();
				Integer folioClaveDisponibleActual = folioClaveController.getFolioDisponible(String.valueOf(idArea))
						.getBody().getFolioKey().getFolio();

				FolioArea folioRespuestaActual = folioAreaController.get(String.valueOf(idArea), String.valueOf("0"))
						.getBody();

				if (folioRespuestaActual != null && configuracionArea.getFolioRespuesta()
						.intValue() < folioRespuestaActual.getFolio().intValue()) {
				}

				if (folio.intValue() < folioDisponibleActual.intValue()) {
					log.debug("El Folio Disponible no puede ser menor al Folio Disponible actual");
					return new ResponseEntity<ConfiguracionArea>(configuracionArea, HttpStatus.CONFLICT);
				}
				if (folioDisponibleActual.intValue() != folio.intValue()) {
					actualizarFolio(idArea, folio);
				}
				if (folioClaveDisponibleActual != null
						&& folioClaveDisponibleActual.intValue() != folioClaveint.intValue()) {
					actualizarFolioClave(idArea, folioClaveint);
				}

				// Enviando a Guardar o Actualizar PARAMETROS
				List<Parametro> parametros = new ArrayList<>();
				parametros.add(new Parametro(new ParametroKey(configuracionArea.getIdArea(), "FOLIODOC", "COMPARTIR"),
						configuracionArea.getComparteFolioSN()));
				parametros.add(new Parametro(new ParametroKey(configuracionArea.getIdArea(), "FOLIODOC", "UNICO"),
						configuracionArea.getFoliadorUnicoSN()));
				parametros.add(new Parametro(new ParametroKey(configuracionArea.getIdArea(), "FOLIODOC", "TIPO"),
						configuracionArea.getTipo()));
				parametros.add(new Parametro(new ParametroKey(configuracionArea.getIdArea(), "NOTIPREFT", "ASUNTO"),
						configuracionArea.getDiasPreNotAsunto()));
				parametros.add(new Parametro(new ParametroKey(configuracionArea.getIdArea(), "NOTIPREFT", "TURNO"),
						configuracionArea.getDiasPreNotTramite()));
				if ("H".equalsIgnoreCase(configuracionArea.getTipo())) {
					parametros.add(
							new Parametro(new ParametroKey(configuracionArea.getIdArea(), "FOLIODOC", "IDAREAHEREDA"),
									String.valueOf(getFolioAreaHeredada(configuracionArea.getIdArea()))));
				} else {
					parametros.add(new Parametro(
							new ParametroKey(configuracionArea.getIdArea(), "FOLIODOC", "IDAREAHEREDA"), null));
				}

				for (Parametro parametro : parametros) {
					parametroController.save(parametro);
				}
				// Enviando a Guardar o Actualizar FOLIOAREA
				if (configuracionArea.getFolioRespuesta() != null) {
					FolioArea folioRespuesta = new FolioArea(new FolioAreaKey(configuracionArea.getIdArea(), 0),
							configuracionArea.getFolioRespuesta(), null);
					folioAreaController.save(folioRespuesta);
				}
				if (configuracionArea.getFolioAsunto() != null) {
					FolioArea folioAsunto = new FolioArea(new FolioAreaKey(configuracionArea.getIdArea(), 1),
							configuracionArea.getFolioAsunto(), null);
					folioAreaController.save(folioAsunto);
				}
				if (configuracionArea.getFolioClave() != null) {
					FolioClave folioClave = new FolioClave(
							new FolioKey(configuracionArea.getIdArea(), configuracionArea.getFolioClave()), "D");
					folioClaveController.save(folioClave);
				}

				// Enviando a Gurdar o Actualizar FOLIOPS
				if (configuracionArea.getFoliops() != null && configuracionArea.getFoliops().getIdArea() != null) {
					foliopsController.save(configuracionArea.getFoliops());
				}

				// Enviando a Gurdar o Actualizar FOLIOPSCLAVE
				if (configuracionArea.getFoliopsclave() != null
						&& configuracionArea.getFoliopsclave().getIdArea() != null) {
					foliopsclaveController.save(configuracionArea.getFoliopsclave());
				}

				// Enviando a Gurdar o Actualizar FOLIO ARCHIVISTICA
				if (configuracionArea.getFolioArchivistica() != null
						&& configuracionArea.getFolioArchivistica().getIdArea() != null
						&& configuracionArea.getFolioArchivistica().getFolio() != null) {
					folioArchivisticaController.save(configuracionArea.getFolioArchivistica());
				}

				// mngrArea.update(configuracionArea);
				return new ResponseEntity<ConfiguracionArea>(configuracionArea, HttpStatus.OK);
			} else {
				return new ResponseEntity<ConfiguracionArea>(configuracionArea, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Foliador Obtiene los Folios Asunto y Respuesta dependiendo si Heredado (H) o
	 * Propio (P).
	 *
	 * @param idArea the id area
	 * @param tipo   the tipo (Heredado "H" o Propio "P")
	 * @return the response entity
	 * @throws Exception the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Seleccionar foliadora heredada", notes = "Obtiene la foliadora heredada")
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
	
	@RequestMapping(value = "/configuracion/area/heredada", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Integer>> foliador(
			@RequestParam(value = "idArea", required = true) Serializable idArea,
			@RequestParam(value = "tipo", required = true) Serializable tipo) throws Exception {

		Map<String, Integer> items = new HashMap<String, Integer>();
		try {
			int areaId = Integer.valueOf((String) idArea);
			if ("H".equalsIgnoreCase(String.valueOf((String) tipo))) {
				areaId = getFolioAreaHeredada(areaId);
				if (areaId == -1) {
					return new ResponseEntity<Map<String, Integer>>(items, HttpStatus.BAD_REQUEST);
				}
			}
			items = getFolioArea(areaId);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		return new ResponseEntity<Map<String, Integer>>(items, HttpStatus.OK);
	}

	/**
	 * Gets the folio area. Obtiene los Folios Asunto y Respuesta de un area
	 *
	 * @param idArea the id area
	 * @return the map
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> getFolioArea(int idArea) throws Exception {

		Map<String, Integer> items = new HashMap<String, Integer>();
		List<FolioArea> lst = new ArrayList<FolioArea>();
		try {
			List<Criterion> restrictions = new ArrayList<Criterion>();
			List<Order> orders = new ArrayList<Order>();

			restrictions.add(Restrictions.eq("folioAreaKey.idArea", idArea));
			restrictions.add(Restrictions.in("folioAreaKey.idTipoFolio", new Object[] { 0, 1 }));
			orders.add(Order.asc("folioAreaKey.idTipoFolio"));

			lst = (List<FolioArea>) mngrFolioArea.search(restrictions, orders);

			if (lst != null && !lst.isEmpty()) {
				for (FolioArea folioArea : lst) {
					items.put(String.valueOf(folioArea.getFolioAreaKey().getIdTipoFolio()), folioArea.getFolio());
				}
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
		return items;
	}

	/**
	 * Obtine el idArea del que se heredan los folios asunto y respuesta.
	 *
	 * @param idArea Identificador del Area del cual se desea obtener el folio
	 *               heredado
	 * @return idArea que hereda para tomar los folio
	 */
	private Integer getFolioAreaHeredada(Integer idArea) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("idArea", idArea);
		BigDecimal areaHeredada = (BigDecimal) mngrArea.uniqueResult("obtieneFolioAreaHeredada", params);
		return areaHeredada.intValue();
	}

	/**
	 * Obtine el idArea del que se heredan los folios asunto y respuesta.
	 *
	 * @param idArea Identificador del Area del cual se desea obtener el folio
	 *               heredado
	 * @return idArea que hereda para tomar los folio
	 */
	private Integer actualizarFolio(int idArea, int folio) throws Exception {
		HashMap<String, Object> params = new HashMap<>();
		Integer result = null;
		params.put("idArea", idArea);
		params.put("folio", folio);

		if (dbVendor == DBVendor.POSTGRESQL)
			mngrFolio.uniqueResult("actualizaFolio2", params);
		else
			mngrFolio.execUpdateQuery("actualizaFolio", params);

		return result;
	}

	/**
	 * Obtine el idArea del que se heredan los folios asunto y respuesta.
	 *
	 * @param idArea Identificador del Area del cual se desea obtener el folio
	 *               heredado
	 * @return idArea que hereda para tomar los folio
	 */
	private Integer actualizarFolioClave(int idArea, int folio) throws Exception {
		HashMap<String, Object> params = new HashMap<>();
		Integer result = null;
		params.put("idArea", idArea);
		params.put("folio", folio);

		if (dbVendor == DBVendor.POSTGRESQL)
			mngrFolio.uniqueResult("foliosClavePorArea2", params);
		else
			mngrFolio.execUpdateQuery("foliosClavePorArea", params);

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<ConfiguracionNotificacion> get(Serializable id) {
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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	public ResponseEntity<List<?>> search(ConfiguracionNotificacion object) throws Exception {
		throw new UnsupportedOperationException();
	}

}
