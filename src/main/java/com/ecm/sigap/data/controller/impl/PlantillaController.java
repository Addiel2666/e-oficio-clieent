/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.ws.rs.BadRequestException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.Ciudadano;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Firmante;
import com.ecm.sigap.data.model.Minutario;
import com.ecm.sigap.data.model.Plantilla;
import com.ecm.sigap.data.model.Remitente;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.RepresentanteLegal;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.util.Antecedente;
import com.ecm.sigap.data.model.util.AsuntoCiudadano;
import com.ecm.sigap.data.model.util.DestinatariosMinutario;
import com.ecm.sigap.data.model.util.RevisorMinutario;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoDestinatario;
import com.ecm.sigap.data.model.util.TipoPlantilla;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Plantilla}
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class PlantillaController extends CustomRestController implements RESTController<Plantilla> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(PlantillaController.class);

	/** Referencia hacia el REST controller de {@link DocumentoAsunto}. */
	@Autowired
	private DocumentoAsuntoController documentoAsuntoController;

	/** Referencia hacia el REST controller de {@link DocumentoRespuesta}. */
	@Autowired
	private DocumentoRespuestaController documentoRespuestaController;

	/** Referencia hacia el REST controller de {@link AreaController}. */
	@Autowired
	private AreaController areaController;

	/**
	 * 
	 * @param plantilla
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta plantilla", notes = "Consulta la lista de plantillas")
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

	@RequestMapping(value = "/plantilla", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Plantilla>> search2(@RequestBody(required = true) Plantilla plantilla)
			throws Exception {

		List<Plantilla> lst = new ArrayList<Plantilla>();
		try {
			String area_id = getHeader(HeaderValueNames.HEADER_AREA_ID);
			Area userArea = mngrArea.fetch(Integer.valueOf(area_id));

			String plantillasIntitucionalesFolder = getParamApp("SIGAP", "PLANTILLASINSTITUCIONALES");
			String plantillasAreaFolder = StringUtils.isBlank(userArea.getContentId()) ? "-1"
					: userArea.getContentId().toLowerCase();

			List<Map<String, String>> object = EndpointDispatcher.getInstance().obtenerPlantillas(
					plantilla.getTipo().toString(), plantillasIntitucionalesFolder, plantillasAreaFolder);
			Plantilla p;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			for (Map<String, String> entry : object) {
				p = new Plantilla();
				p.setFechaRegistro(sdf.parse(entry.get("r_creation_date")));
				p.setNombre(entry.get("object_name"));
				p.setObjectId(entry.get("r_object_id"));

				lst.add(p);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
		return new ResponseEntity<List<Plantilla>>(lst, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */
	@Override
	@RequestMapping(value = "/plantilla/db", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) Plantilla plantilla) {

		List<?> lst = new ArrayList<Plantilla>();
		log.debug("PARAMETROS DE BUSQUEDA :: " + plantilla);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (plantilla.getId() != null)
				restrictions.add(Restrictions.idEq(plantilla.getId()));

			if (plantilla.getFechaRegistro() != null)
				restrictions.add(Restrictions.eq("fechaRegistro", plantilla.getFechaRegistro()));

			if (plantilla.getIdOwner() != null) {
				restrictions.add(Restrictions.eq("idOwner", plantilla.getIdOwner()));
			}

			if (plantilla.getNombre() != null)
				restrictions.add(EscapedLikeRestrictions.ilike("nombre", plantilla.getNombre(), MatchMode.ANYWHERE));

			if (plantilla.getObjectId() != null)
				restrictions.add(Restrictions.eq("objectId", plantilla.getObjectId()));

			if (plantilla.getTipo() != null)
				restrictions.add(Restrictions.eq("tipo", plantilla.getTipo()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("fechaRegistro"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrPlantilla.search(restrictions, orders);

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
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	@RequestMapping(value = "/plantilla", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Plantilla> get(@RequestParam(required = true) Serializable id) {

		Plantilla item = null;

		try {
			item = mngrPlantilla.fetch(Integer.valueOf((String) id));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<Plantilla>(item, HttpStatus.OK);

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

	@ApiOperation(value = "Eliminar plantilla", notes = "Elimina de la lista una plantilla")
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
	@RequestMapping(value = "/plantilla", method = RequestMethod.DELETE)
	public void delete(@RequestParam(required = true, value = "objectId") Serializable objectId) throws Exception {

		try {

			boolean deleted = EndpointDispatcher.getInstance().eliminarDocumento(String.valueOf(objectId));

			log.debug("borrado: " + deleted);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
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

	@ApiOperation(value = "Agregar plantilla", notes = "Agrega un plantilla a la lista")
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
	@RequestMapping(value = "/plantilla", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Plantilla> save(@RequestBody(required = true) Plantilla plantilla)
			throws Exception {

		try {

			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("PLANTILLA A GUARDAR >> " + plantilla);

				String area_id = getHeader(HeaderValueNames.HEADER_AREA_ID);
				Area user_area = mngrArea.fetch(Integer.valueOf(area_id));

				if (plantilla.getId() == null) {

					// Se almacena la plantilla en diferente folder segun el
					// tipo.

					String ruta;

					IEndpoint endpoint = EndpointDispatcher.getInstance();
					if (plantilla.getTipo() == TipoPlantilla.INSTITUCIONAL) {

						plantilla.setIdOwner(user_area.getInstitucion().getIdInstitucion().toString());

						String startFolder = getParamApp("CABINET");

						ruta = startFolder + "/" + environment.getProperty("folderNamePlantillasInstitucionales");

					} else if (plantilla.getTipo() == TipoPlantilla.POR_AREA) {

						plantilla.setIdOwner(area_id);

						ruta = endpoint.getObjectPath(user_area.getContentId()) + "/"
								+ environment.getProperty("folderNamePlantillas");

					} else {
						throw new Exception("BAD TYPE!");
					}

					String parentId;

					try {

						parentId = endpoint.getFolderIdByPath(ruta);

					} catch (Exception e) {

						if (e.getMessage() != null && e.getMessage().contains("Object Not Found!")) {

							if (plantilla.getTipo() == TipoPlantilla.POR_AREA) {

								parentId = areaController.saveFoldersPlantillas(endpoint, user_area);

							} else {

								throw new Exception("No existe el folder para las plantillas institucionales.");

							}

						} else {

							throw e;

						}
					}

					plantilla.setParentId(parentId);

					mngrPlantilla.save(plantilla);

					if (plantilla.getTipo() == TipoPlantilla.INSTITUCIONAL) {

						Map<String, Object> additionalData = new HashMap<String, Object>();

						additionalData.put("a_content_type", "xml");

						endpoint.setProperties(plantilla.getObjectId(), additionalData);
					} else {

						String aclName = environment.getProperty("aclNamePlantillas");

						Map<String, String> additionalData = new HashMap<String, String>();

						additionalData.put("idArea", user_area.getIdArea().toString());

						endpoint.setACL(plantilla.getObjectId(), aclName, additionalData);

						plantilla.setFileB64(null);
					}

					return new ResponseEntity<Plantilla>(plantilla, HttpStatus.CREATED);

				} else {

					mngrPlantilla.update(plantilla);

					return new ResponseEntity<Plantilla>(plantilla, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<Plantilla>(plantilla, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Registra la plantilla indicada como un documento en el minutario.
	 * 
	 * @param idPlantilla
	 * @param idMinutario
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Registrar plantilla borrador", notes = "Registra la plantilla indicada como un documento adjunto a un borrador")
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

	@RequestMapping(value = "/plantilla/minutario/exportar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RevisorMinutario>> exportarMinutario(
			//
			@RequestParam(name = "idPlantilla", required = true) String objectId, //
			@RequestParam(name = "idMinutario", required = true) Integer idMinutario//
	) throws Exception {

		IEndpoint instance = EndpointDispatcher.getInstance();

		String idUser = getHeader(HeaderValueNames.HEADER_USER_ID);
		// String idArea = getHeader(HeaderValueNames.HEADER_AREA_ID);

		try {

			// Plantilla plantilla = mngrPlantilla.fetch(idPlantilla);
			Minutario minutario = mngrMinutario.fetch(idMinutario);

			log.debug(" Exportando plantilla " + objectId + " en minutario " + minutario.getIdMinutario());

			String plantillaFileString = getPlantillaAsString(objectId);

			// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +

			plantillaFileString = reemplazaKeysMinutario(minutario, plantillaFileString, null);

			// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
			String objectName = instance.getObjectName(objectId);

			// se pone el prefijo del documento
			String replaced_ext_name = idMinutario + "_" + objectName.replace(".xml", ".doc");

			File plantillaExported = plantillaStringToFile(plantillaFileString, replaced_ext_name);

			if (minutario.getContentId() == null) {
				throw new Exception("EL MINUTARIO NO TIENE FOLDER ID!!");
			}

			String newObjectId = //
					instance//
							.saveDocumentoIntoId( //
									minutario.getContentId(), //
									replaced_ext_name, //
									environment.getProperty("docTypeAdjuntoMinutario"), //
									Version.MAYOR, //
									objectName, //
									plantillaExported);

			plantillaExported.delete();

			if (StringUtils.isBlank(newObjectId))
				throw new Exception("error al subir plantilla exportada...");

			Map<String, Object> properties = new HashMap<>();
			// Obtenemos el User Name para asignarlo como el Owner del
			// documento
			String userName = instance.getUserName(getHeader(HeaderValueNames.HEADER_CONTENT_USER));
			properties.put("owner_name", userName);

			instance.setProperties(newObjectId, properties);

			// AGREGAR ACL
			Map<String, String> additionalData = new HashMap<>();
			additionalData.put("idOwnerDoc", userName);

			instance.setACL(newObjectId, environment.getProperty("aclNameAdjuntoMinutario"), additionalData);

			// Registrar el archivo exportado al minutario.
			RevisorMinutario newRevision = new RevisorMinutario();

			newRevision.setId(idUser);
			newRevision.setComentario("Plantilla exportada.");
			newRevision.setDocumentName(replaced_ext_name);
			newRevision.setFechaRegistro(new Date());
			newRevision.setObjectId(newObjectId);
			newRevision.setUsuario(mngrUsuario.fetch(idUser));
			newRevision.setVersion("1.0");

			minutario.getRevisores().add(newRevision);
			mngrMinutario.update(minutario);

			minutario = mngrMinutario.fetch(idMinutario);

			return new ResponseEntity<List<RevisorMinutario>>(minutario.getRevisores(), HttpStatus.OK);

		} catch (Exception ex) {

			log.error(ex.getLocalizedMessage());

			throw ex;

		}

	}

	/**
	 * Toma una plantilla en forma de cadena de texto y reemplaza los KEYS por sus
	 * valores del minutario indicado.
	 * 
	 * @param minutario
	 * @param plantillaFileString
	 * @return
	 */
	protected String reemplazaKeysMinutario(Minutario minutario, String plantillaFileString, String folioRespuesta) {
		// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +

		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.asunto"),
				CDATA + minutario.getAsunto().replaceAll(END_CDATA, "") + END_CDATA);

		// * * * * *
		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.tituloDocumento"),
				CDATA + minutario.getTituloDocumento().replaceAll(END_CDATA, "") + END_CDATA);

		// * * * * *
		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.contentId"),
				minutario.getContentId() != null ? minutario.getContentId() : "-");

		// * * * * *
		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.idAsunto"),
				minutario.getIdAsunto() != null ? minutario.getIdAsunto().toString() : "-");

		// * * * * *

		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.cofidencial"),
				minutario.getConfidencial() != null ? minutario.getConfidencial().toString() : "-");

		// * * * * *
		{
			StringBuffer revisores = new StringBuffer("");
			String revisorRow;
			for (RevisorMinutario revisor : minutario.getRevisores()) {
				revisorRow = plantillasKeys.getString("minutario.revisores.orden");

				revisorRow = revisorRow.replace(plantillasKeys.getString("minutario.revisores.comentario"),
						(revisor.getComentario() != null ? revisor.getComentario() : "-"));
				revisorRow = revisorRow.replace(plantillasKeys.getString("minutario.revisores.documentName"),
						revisor.getDocumentName());
				revisorRow = revisorRow.replace(plantillasKeys.getString("minutario.revisores.usuario"),
						revisor.getUsuario() != null ? revisor.getUsuario().getIdUsuario() : "-");
				revisorRow = revisorRow.replace(plantillasKeys.getString("minutario.revisores.objectId"),
						revisor.getObjectId());

				revisores.append(revisorRow);
				revisores.append("\n");
			}

			plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.revisores"),
					revisores.toString());
		}
		// * * * * *
		{
			StringBuffer destinatarios = new StringBuffer("");
			StringBuffer destinatariosSeparados = new StringBuffer("");
			StringBuffer destinatarios_cpp = new StringBuffer("");

			StringBuffer copiasCargos = new StringBuffer("");
			StringBuffer destinatariosCargos = new StringBuffer("");

			String destinatarioRow;
			String destinatarioSeparadoRow;
			String destinatarioCopiasRow;

			Collections.sort(minutario.getDestinatarios());

			boolean isDestinatarioTag = plantillaFileString
					.contains(plantillasKeys.getString("minutario.destinatarios"));

			boolean isDestinatarioSeparadoTag = plantillaFileString
					.contains(plantillasKeys.getString("minutario.destinatarios.separados"));

			boolean isDestinatarioCopiasTag = //
					plantillaFileString//
							.contains(plantillasKeys.getString("minutario.copias_cargos"))
							|| plantillaFileString//
									.contains(plantillasKeys.getString("minutario.destinatarios_cargos"));

			if (isDestinatarioTag || isDestinatarioSeparadoTag || isDestinatarioCopiasTag) {

				for (DestinatariosMinutario destinatario : minutario.getDestinatarios()) {

					destinatarioRow = plantillasKeys.getString("minutario.destinatarios.orden");
					destinatarioSeparadoRow = plantillasKeys.getString("minutario.destinatarios.separados.orden");
					destinatarioCopiasRow = plantillasKeys.getString("minutario.destinatarios_cargos.orden");

					switch (destinatario.getIdTipoDestinatario()) {

					case FUNCIONARIO_INTERNO:
					case FUNCIONARIO_INTERNO_CCP:
					case FUNCIONARIO_EXTERNO:
					case FUNCIONARIO_EXTERNO_CCP:
						// case FUNCIONARIO_INTERNO_TURNO:
						// case FUNCIONARIO_EXTERNO_TURNO:

						Representante user = mngrRepresentante.fetch(destinatario.getIdDestinatario());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.materno"), //
								user.getMaterno());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.paterno"), //
								user.getPaterno());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.nombres"), //
								user.getNombres());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.area"), //
								destinatario.getIdAreaDestinatario().getDescripcion());

						// Tag de Destinatarios Separados
						if (isDestinatarioSeparadoTag) {

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.materno"), //
									user.getMaterno());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.paterno"), //
									user.getPaterno());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.nombres"), //
									user.getNombres());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.area"), //
									destinatario.getIdAreaDestinatario().getDescripcion());
						}

						// Tad de copias cargos
						if (isDestinatarioCopiasTag) {

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.materno"), //
									user.getMaterno());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.paterno"), //
									user.getPaterno());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.nombres"), //
									user.getNombres());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.cargo"), //
									user.getCargo());
						}

						break;
					case CIUDADANO:
						// case CIUDADANO_TURNO:
					case CIUDADANO_CCP:

						Ciudadano ciudadano = mngrCiudadano.fetch(Integer.parseInt(destinatario.getIdDestinatario()));

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.materno"),
								(ciudadano.getMaterno() != null ? ciudadano.getMaterno() : " "));

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.paterno"), //
								ciudadano.getPaterno());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.nombres"), //
								ciudadano.getNombres());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.area"), //
								" N/A ");

						// Tag de Destinatarios Separados
						if (isDestinatarioSeparadoTag) {

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.materno"),
									(ciudadano.getMaterno() != null ? ciudadano.getMaterno() : " "));

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.paterno"),
									ciudadano.getPaterno());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.nombres"),
									ciudadano.getNombres());

							destinatarioSeparadoRow = destinatarioSeparadoRow
									.replace(plantillasKeys.getString("minutario.destinatarios.area"), " N/A ");
						}

						// Tad de copias cargos
						if (isDestinatarioCopiasTag) {

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.materno"), //
									(ciudadano.getMaterno() != null ? ciudadano.getMaterno() : " "));

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.paterno"), //
									ciudadano.getPaterno());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.nombres"), //
									ciudadano.getNombres());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.cargo"), //
									"N/A");
						}

						break;
					case REPRESENTANTE_LEGAL:
					case REPRESENTANTE_LEGAL_CCP:
						// case REPRESENTANTE_LEGAL_TURNO:
						RepresentanteLegal repLegal = mngrRepresentanteLegal
								.fetch(Integer.parseInt(destinatario.getIdDestinatario()));

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.materno"), //
								repLegal.getMaterno());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.paterno"), //
								repLegal.getPaterno());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.nombres"), //
								repLegal.getNombres());

						destinatarioRow = destinatarioRow.replace(
								plantillasKeys.getString("minutario.destinatarios.area"),
								repLegal.getEmpresa().getNombre());

						if (isDestinatarioSeparadoTag) {

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.materno"), //
									repLegal.getMaterno());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.paterno"), //
									repLegal.getPaterno());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.nombres"), //
									repLegal.getNombres());

							destinatarioSeparadoRow = destinatarioSeparadoRow.replace(
									plantillasKeys.getString("minutario.destinatarios.area"), //
									repLegal.getEmpresa().getNombre());
						}

						// Tad de copias cargos
						if (isDestinatarioCopiasTag) {

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.materno"), //
									repLegal.getMaterno());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.paterno"), //
									repLegal.getPaterno());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.nombres"), //
									repLegal.getNombres());

							destinatarioCopiasRow = destinatarioCopiasRow.replace(
									plantillasKeys.getString("minutario.destinatarios.cargo"), //
									"N/A");
						}

						break;

					default:

						destinatarioRow = "";
						destinatarioSeparadoRow = "";
						destinatarioCopiasRow = "";

						log.debug("Ignorando el tipo de destinatarios " + destinatario.getIdTipoDestinatario());

						break;

					}

					if (destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_INTERNO_CCP
							|| destinatario.getIdTipoDestinatario() == TipoDestinatario.FUNCIONARIO_EXTERNO_CCP
							|| destinatario.getIdTipoDestinatario() == TipoDestinatario.CIUDADANO_CCP
							|| destinatario.getIdTipoDestinatario() == TipoDestinatario.REPRESENTANTE_LEGAL_CCP) {

						destinatarios_cpp.append(destinatarioRow).append("\n");
						copiasCargos.append(destinatarioCopiasRow).append("\n");

					} else {

						destinatarios.append(destinatarioRow).append("\n");
						destinatariosSeparados.append(destinatarioSeparadoRow);
						destinatariosCargos.append(destinatarioCopiasRow).append("\n");

					}
				}
			}

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("minutario.destinatarios"), //
					destinatarios.toString());

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("minutario.destinatarios.separados"), //
					destinatariosSeparados.toString());

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("minutario.destinatarios_cpp"), //
					destinatarios_cpp.toString());

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("minutario.copias_cargos"), //
					copiasCargos.toString());

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("minutario.destinatarios_cargos"), //
					destinatariosCargos.toString());

		}
		// * * * * *

		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.remitente.idArea"),
				minutario.getRemitente().getIdArea().toString());

		plantillaFileString = plantillaFileString.replace(plantillasKeys.getString("minutario.remitente.descripcion"),
				minutario.getRemitente().getDescripcion());

		// * * * * *
		String nombreFirmate = plantillasKeys.getString("minutario.firmante.orden");

		nombreFirmate = nombreFirmate.replace(//
				plantillasKeys.getString("minutario.firmante.paterno"), //
				minutario.getFirmante() != null ? minutario.getFirmante().getApellidoPaterno() : "");

		nombreFirmate = nombreFirmate.replace(//
				plantillasKeys.getString("minutario.firmante.materno"), //
				minutario.getFirmante() != null ? (StringUtils.isNotBlank(minutario.getFirmante().getMaterno())
						? minutario.getFirmante().getMaterno()
						: " ") : "");

		nombreFirmate = nombreFirmate.replace(//
				plantillasKeys.getString("minutario.firmante.nombres"), //
				minutario.getFirmante() != null ? minutario.getFirmante().getNombres() : "");

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.firmante.descripcion"), //
				nombreFirmate);

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.firmante.cargo"), //
				minutario.getFirmante() != null ? minutario.getFirmante().getCargo() : "");
		// * * * * *

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.idMinutario"), //
				minutario.getIdMinutario().toString());

		// * * * * *

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.status"), //
				minutario.getStatus().toString());

		// * * * * *

		plantillaFileString = plantillaFileString.replace(
				plantillasKeys.getString("minutario.institucion.idInstitucion"),
				minutario.getInstitucion().getIdInstitucion().toString());

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.institucion.descripcion"), //
				minutario.getInstitucion().getDescripcion());

		// * * * * *

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.usuario.idUsuario"), //
				minutario.getUsuario().getIdUsuario());

		// * * * * *

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.usuario.paterno"), //
				minutario.getUsuario().getApellidoPaterno());

		// * * * * *

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.usuario.materno"), //
				(StringUtils.isNotBlank(minutario.getUsuario().getMaterno()) ? minutario.getUsuario().getMaterno()
						: " "));

		// * * * * *

		plantillaFileString = plantillaFileString.replace(//
				plantillasKeys.getString("minutario.usuario.nombres"), //
				minutario.getUsuario().getNombres());

		// * * * * *
		if (minutario.getIdAsunto() != null) {
			try {
				Date fecha_ = new Date();

				// La fecha de registro no se reemplaza sino hasta que se
				// envia a Generar la respuesta/asunto
				SimpleDateFormat sdf = new SimpleDateFormat(//
						plantillasKeys.getString("minutario.datePatern"),
						new Locale(plantillasKeys.getString("minutario.lenguage"),
								plantillasKeys.getString("minutario.country")));

				String fechaRegistroFormated_ = sdf.format(fecha_);

				plantillaFileString = plantillaFileString.replace(//
						plantillasKeys.getString("minutario.fechaRegistro"), fechaRegistroFormated_);

				if (StringUtils.isNotBlank(folioRespuesta)) {
					plantillaFileString = plantillaFileString.replace(
							plantillasKeys.getString("minutario.asunto.numdocto"), //
							folioRespuesta);

				} else {
					Asunto asunto = mngrAsunto.fetch(minutario.getIdAsunto());
					if (asunto != null) {
						// * * * * *
						plantillaFileString = plantillaFileString.replace(
								plantillasKeys.getString("minutario.asunto.numdocto"),
								asunto.getAsuntoDetalle().getNumDocto());
					}

				}
			} catch (Exception e) {

			}

		}
		// * * * * *
		{

			if (minutario.getFirmante() == null)
				throw new BadRequestException("El minutario no tiene firmante ");

			DefaultMutableTreeNode tree = getAreaTree(minutario.getRemitente().getIdArea());

			int depth = 0;
			try {
				depth = getTreeDepth(tree);
			} catch (Exception e) {

			}

			StringBuffer area2_3 = new StringBuffer();

			DefaultMutableTreeNode node_2 = null;

			if (depth >= 2) {
				node_2 = (DefaultMutableTreeNode) tree.getFirstChild();
				area2_3.append(((Area) node_2.getUserObject()).getDescripcion());
			}

			DefaultMutableTreeNode node_3 = null;

			if (depth >= 3) {
				area2_3.append(plantillasKeys.getString("minutario.area2_3.lineBreak"));

				node_3 = (DefaultMutableTreeNode) node_2.getFirstChild();

				area2_3.append(((Area) node_3.getUserObject()).getDescripcion());

			}

			if (depth > 3) {
				area2_3.append(plantillasKeys.getString("minutario.area2_3.lineBreak"));

				DefaultMutableTreeNode node_last = node_3;

				while (!node_last.isLeaf()) {
					node_last = (DefaultMutableTreeNode) node_last.getFirstChild();
				}

				area2_3.append(((Area) node_last.getUserObject()).getDescripcion());

			}

			log.info(area2_3.toString());

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("minutario.area2_3"), //
					area2_3.toString());

		}

		// * * * * *

		return plantillaFileString;
	}

	/**
	 * Obtiene la profundidad maxima del arbol.
	 * 
	 * @param tree
	 * @return
	 */
	private int getTreeDepth(DefaultMutableTreeNode tree) {

		DefaultMutableTreeNode item = tree;

		int depth = 1;
		boolean last = false;

		do {

			Area area = (Area) item.getUserObject();

			log.info(depth + " :: " + area.getDescripcion() + " :: " + area.getIdArea());

			if (item.getChildCount() > 0) {
				depth++;
				item = (DefaultMutableTreeNode) item.getFirstChild();
			} else {
				last = true;
			}

		} while (!last);
		return depth;
	}

	/**
	 * Obtiene un arbol con las areas padres hasta el area principal.
	 * 
	 * @param idArea
	 * @return
	 */
	private DefaultMutableTreeNode getAreaTree(Integer idArea) {
		Area area_ = mngrArea.fetch(idArea);

		DefaultMutableTreeNode tree = new DefaultMutableTreeNode();

		tree.setUserObject(area_);

		if (area_.getIdAreaPadre() == null || area_.getIdAreaPadre().equals(area_.getIdArea())) {
			// es top.
			return tree;

		} else {

			return getParentArea(tree, area_.getIdAreaPadre());
		}
	}

	/**
	 * Recursivo para obtener las areas padres.
	 * 
	 * @param tree
	 * @param idArea
	 * @return
	 */
	private DefaultMutableTreeNode getParentArea(DefaultMutableTreeNode tree, Integer idArea) {
		Area area_ = mngrArea.fetch(idArea);

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();

		newNode.setUserObject(area_);

		newNode.add(tree);

		if (area_.getIdAreaPadre() == null || area_.getIdAreaPadre().equals(area_.getIdArea())) {
			return newNode;
			// top.
		} else {
			return getParentArea(newNode, area_.getIdAreaPadre());
		}
	}

	/**
	 * Descarga una plantilla con los datos de un asunto.
	 * 
	 * @param idPlantilla
	 * @param idAsunto
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Descargar plantilla", notes = "Descarga una plantilla con los datos de un asunto")
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

	@RequestMapping(value = "/plantilla/asunto/ver", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> verPlantillaAsunto(//
			@RequestParam(name = "idPlantilla", required = true) String objectId, //
			@RequestParam(name = "idAsunto", required = true) Integer idAsunto//
	) throws Exception {

		// Plantilla plantilla = mngrPlantilla.fetch(idPlantilla);

		Asunto asunto = mngrAsunto.fetch(idAsunto);

		File plantillaExported = fillPlantillaAsunto(objectId, asunto);

		Map<String, String> item = new HashMap<String, String>();

		item.put("type", "text/xml");
		item.put("name", "plantilla.doc");
		item.put("contentB64", Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(plantillaExported)));

		plantillaExported.delete();

		return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);
	}

	/**
	 * Registra la plantilla indicada como un documento adjunto en el asunto.
	 * 
	 * @param idPlantilla
	 * @param idAsurto
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Registrar plantilla asunto", notes = "Registra una plantilla como un documento adjunto en el asunto")
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

	@RequestMapping(value = "/plantilla/asunto/exportar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DocumentoAsunto> exportarAsunto(//
			@RequestParam(name = "idPlantilla", required = true) String objectId, //
			@RequestParam(name = "idAsunto", required = true) Integer idAsunto//
	) throws Exception {

		String idUser = getHeader(HeaderValueNames.HEADER_USER_ID);
		String idArea = getHeader(HeaderValueNames.HEADER_AREA_ID);

		try {

			// Plantilla plantilla = mngrPlantilla.fetch(idPlantilla);

			Asunto asunto = mngrAsunto.fetch(idAsunto);

			File plantillaExported = fillPlantillaAsunto(objectId, asunto);

			// Registrar el archivo exportado al asunto.
			DocumentoAsunto documentoAsunto = new DocumentoAsunto();
			documentoAsunto.setFechaRegistro(new Date());
			byte[] bytes = FileUtils.readFileToByteArray(plantillaExported);
			String fileB64 = Base64.getEncoder().encodeToString(bytes);
			documentoAsunto.setFileB64(fileB64);
			documentoAsunto.setGubernamental(false);
			documentoAsunto.setIdArea(Integer.valueOf(idArea));
			documentoAsunto.setIdAsunto(idAsunto);

			String objectName = EndpointDispatcher.getInstance().getObjectName(objectId);
			documentoAsunto.setObjectName(objectName.replace(".xml", ".doc"));
			documentoAsunto.setOwnerName(idUser);
			documentoAsunto.setParentContentId(asunto.getContentId());

			ResponseEntity<DocumentoAsunto> response_ = documentoAsuntoController.save(documentoAsunto);

			documentoAsunto = response_.getBody();

			plantillaExported.delete();

			return new ResponseEntity<DocumentoAsunto>(documentoAsunto, HttpStatus.OK);

		} catch (Exception ex) {

			log.error(ex.getLocalizedMessage());

			throw ex;

		}

	}

	/**
	 * Registra la plantilla indicada como un documento adjunto a la respuesta.
	 * 
	 * @param idPlantilla
	 * @param idAsurto
	 * @return
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Registrar plantilla respuesta", notes = "Registra la plantilla indicada como un documento adjunto a una respuesta")
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

	@RequestMapping(value = "/plantilla/respuesta/exportar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<DocumentoRespuesta> exportarRespuesta(//
			@RequestParam(name = "idPlantilla", required = true) String objectId, //
			@RequestParam(name = "idRespuesta", required = true) Integer idRespuesta//
	) throws Exception {

		String idUser = getHeader(HeaderValueNames.HEADER_USER_ID);
		String idArea = getHeader(HeaderValueNames.HEADER_AREA_ID);

		try {

			Respuesta respuesta = mngrRespuesta.fetch(idRespuesta);

			Asunto asunto = mngrAsunto.fetch(respuesta.getIdAsunto());

			File plantillaExported = fillPlantillaAsunto(objectId, asunto);

			// Registrar el archivo exportado al asunto.
			DocumentoRespuesta documentoResp = new DocumentoRespuesta();

			documentoResp.setFechaRegistro(new Date());

			byte[] bytes = FileUtils.readFileToByteArray(plantillaExported);
			String fileB64 = Base64.getEncoder().encodeToString(bytes);

			documentoResp.setFileB64(fileB64);
			documentoResp.setIdArea(Integer.valueOf(idArea));
			documentoResp.setIdAsunto(asunto.getIdAsunto());
			documentoResp.setIdRespuesta(respuesta.getIdRespuesta());

			String objectName = EndpointDispatcher.getInstance().getObjectName(objectId);
			documentoResp.setObjectName(objectName.replace(".xml", ".doc"));
			documentoResp.setOwnerName(idUser);
			documentoResp.setParentContentId(asunto.getContentId());

			ResponseEntity<DocumentoRespuesta> response_ = documentoRespuestaController.save(documentoResp);

			documentoResp = response_.getBody();

			plantillaExported.delete();

			return new ResponseEntity<DocumentoRespuesta>(documentoResp, HttpStatus.OK);

		} catch (Exception ex) {

			log.error(ex.getLocalizedMessage());

			throw ex;

		}

	}

	/**
	 * Generar una archivo con la plantilla como template llena con los datos del
	 * asunto indicado.
	 * 
	 * @param plantilla
	 * @param asunto
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private File fillPlantillaAsunto(String objectId, Asunto asunto)
			throws Exception, IOException, FileNotFoundException {

		log.debug(" Exportando plantilla " + objectId + " en asunto " + asunto.getIdAsunto());

		String plantillaFileString = getPlantillaAsString(objectId);

		plantillaFileString = reemplazaKeysAsunto(asunto, plantillaFileString);

		File plantillaExported = plantillaStringToFile(plantillaFileString,
				EndpointDispatcher.getInstance().getObjectName(objectId));

		return plantillaExported;
	}

	/**
	 * Reemplaza los keys en la plantlla con los datos del asunto proporcionado.
	 * 
	 * @param asunto
	 * @param plantillaFileString
	 */
	private String reemplazaKeysAsunto(Asunto asunto, String plantillaFileString) {
		{
			String asuntoDescripcion = asunto.getAsuntoDetalle().getAsuntoDescripcion();

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.asuntoDescripcion"),
					CDATA + asuntoDescripcion.replaceAll(END_CDATA, "") + END_CDATA);
		}
		{
			// tomados de sigap4
			// TODO verificar cuando es ciudadano o empresa
			Remitente rem = asunto.getAsuntoDetalle().getRemitente();
			String remitente = rem.getDescripcion();
			Integer idInstitucion = rem.getRemitenteKey().getPromotor().getIdInstitucion();
			if (getParamApp("SIGAP", "IDCIUDPROMOTOR").equals(String.valueOf(idInstitucion))) {

				if (asunto.getAsuntoDetalle().getCiudadanos().size() == 1) {
					Ciudadano ciud = ((com.ecm.sigap.data.model.util.AsuntoCiudadano) asunto.getAsuntoDetalle()
							.getCiudadanos().get(0)).getCiudadano();
					remitente = ciud.getNombres() + " " + ciud.getPaterno() + " " + ciud.getMaterno();
				}

			} else if (idInstitucion == 0) {
				// nombre del ciudadano que tenga idremitente = idciudadano y
				// idtipo
				List<Criterion> restrictions = new ArrayList<Criterion>();
				restrictions.add(Restrictions.eq("idTipo", "E"));
				restrictions.add(Restrictions.eq("id", rem.getRemitenteKey().getIdRemitente()));
				// List<?> ciuds = mngrCiudadano.search(restrictions);
			}

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.idRemitente"), //
					CDATA + remitente.replaceAll(END_CDATA, "") + END_CDATA);
		}

		{
			Representante repDest = null;
			if (asunto.getAreaDestino() != null) {
				repDest = asunto.getAreaDestino().getTitular();
			}

			String titularDestino = "";
			if (repDest != null) {
				titularDestino = repDest.getNombres() + " " + repDest.getPaterno() + " " + repDest.getMaterno();
			}

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.titular.destino"), //
					CDATA + titularDestino.replaceAll(END_CDATA, "") + END_CDATA);
		}
		{
			String promotor = asunto.getAsuntoDetalle().getPromotor().getDescripcion();
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.idPromotor"), //
					CDATA + promotor.replaceAll(END_CDATA, "") + END_CDATA);
		}
		{
			Representante repUsuario = asunto.getArea().getTitular();

			String titularUsuario = repUsuario.getNombres() + " " + repUsuario.getPaterno() + " "
					+ repUsuario.getMaterno();

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.titular.usuario"), //
					CDATA + titularUsuario.replaceAll(END_CDATA, "") + END_CDATA);
		}
		{
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("tipoAsunto", TipoAsunto.COPIA)); // "C"
			restrictions.add(Restrictions.eq("idAsuntoPadre", asunto.getIdAsuntoPadre()));
			List<?> copias = mngrAsunto.search(restrictions);
			String separador = " , ";
			String ccp = "";
			for (int i = 0; i < copias.size(); i++) {
				Asunto copia = (Asunto) copias.get(i);
				// TODO validar areadestino
				if (copia.getAreaDestino() != null) {
					ccp += copia.getAreaDestino().getDescripcion() + (i < copias.size() - 1 ? separador : "");
				}

			}
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.ccpTurnos"), //
					ccp);
		}
		{
			String tema = (asunto.getTema() != null ? asunto.getTema().getDescripcion() : "");
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.tema.descripcion"), //
					tema);
		}
		{
			String subtema = (asunto.getSubTema() != null ? asunto.getSubTema().getDescripcion() : "");
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.subtema.descripcion"), //
					subtema);
		}
		{
			Firmante fdes = asunto.getAsuntoDetalle().getFirmante();
			String firmanteDes = (fdes != null ? fdes.getNombres() + " " + fdes.getPaterno() + " " + fdes.getMaterno()
					: "");
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.firmante.descripcion"), //
					firmanteDes);
		}
		{
			String tipoDoc = (asunto.getTipoDocumento() != null ? asunto.getTipoDocumento().getDescripcion() : "");
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.tipoDoc"), //
					tipoDoc);
		}
		// TODO
		// plantillaFileString =
		// plantillaFileString.replace(plantillasKeys.getString("asunto.respuestas"),
		// asunto.getAsuntoDetalle().getAsuntoDescripcion());

		// TODO customasuntos
		// plantillaFileString =
		// plantillaFileString.replace(plantillasKeys.getString("asunto.numVolante"),
		// asunto.getAsuntoDetalle().getAsuntoDescripcion());

		// TODO Validar este codigo para que use el objeto Remitente
		{
			String firmante = "";
			if (asunto.getAsuntoDetalle().getFirmante() != null) {
				Area remArea = mngrArea.fetch(asunto.getAsuntoDetalle().getFirmante().getIdRemitente());
				firmante = (remArea != null ? remArea.getDescripcion() : "");
			}
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.firmante"), //
					firmante);
		}
		{
			AsuntoCiudadano ciudadano = (asunto.getAsuntoDetalle().getCiudadanos().size() > 0
					? (AsuntoCiudadano) asunto.getAsuntoDetalle().getCiudadanos().get(0)
					: null);
			String ciud = (ciudadano != null
					? ciudadano.getCiudadano().getNombres() + " " + ciudadano.getCiudadano().getPaterno() + " "
							+ ciudadano.getCiudadano().getMaterno()
					: "");
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.ciudadano"), //
					ciud);
		}
		{
			TipoInstruccion ti = asunto.getInstruccion();
			String reqResp = (ti != null ? (ti.getRequiereRespuesta() ? "Requiere respuesta" : "No requiere respuesta")
					: "");
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.insReqResp"), //
					reqResp);
		}
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.fechaHoy"), //
					sdf.format(new Date()));

			if (asunto.getFechaRegistro() != null) {
				plantillaFileString = plantillaFileString.replace(//
						plantillasKeys.getString("asunto.fechaReg"), //
						sdf.format(asunto.getFechaRegistro()));
			} else {
				plantillaFileString = plantillaFileString.replace(//
						plantillasKeys.getString("asunto.fechaReg"), //
						"");
			}
		}
		{
			List<Antecedente> antecedentes = asunto.getAntecedentes();
			String ants = "";
			for (Antecedente a : antecedentes) {
				ants += a.getIdAntecedentes() + ", ";
			}
			if (StringUtils.isNotBlank(ants)) {
				ants = ants.substring(0, ants.lastIndexOf(","));
				ants += ".";
			}
			plantillaFileString = plantillaFileString.replace(//
					plantillasKeys.getString("asunto.antecedente"), //
					ants);
		}
		return plantillaFileString;
	}

	/**
	 * Genera un archivo temporal con la cadena indicada.
	 * 
	 * @param plantillaFileString
	 * @param suffix
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private File plantillaStringToFile(String plantillaFileString, String suffix)
			throws IOException, FileNotFoundException {

		File file = File.createTempFile("SIGAP_V_exported_", suffix);

		file.deleteOnExit();

		log.debug("File creadted :: " + file.getCanonicalPath());

		FileUtils.writeStringToFile(file, plantillaFileString, "UTF-8");

		log.debug("File writen down! ");

		return file;
	}

	/**
	 * Obtiene el contenido del archivo y lo convierte en una cadena.
	 * 
	 * @param objectId
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	private String getPlantillaAsString(String objectId) throws Exception, IOException {

		File plantillaFile = EndpointDispatcher.getInstance().getFile(objectId);

		plantillaFile.deleteOnExit();

		log.debug("File creadted :: " + plantillaFile.getCanonicalPath());

		String plantillaFileString = FileUtils.readFileToString(plantillaFile, "UTF-8");

		plantillaFile.delete();

		return plantillaFileString;

	}

}
