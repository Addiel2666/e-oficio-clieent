/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoDetalle;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.InfomexArchivo;
import com.ecm.sigap.data.model.InfomexModalidadEntrega;
import com.ecm.sigap.data.model.InfomexSolicitud;
import com.ecm.sigap.data.model.InfomexSolicitudAsunto;
import com.ecm.sigap.data.model.InfomexSolicitudKey;
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroApp;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.RespuestaConsulta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.util.FileBase64;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoRegistro;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.data.util.FechaUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.InfomexSolicitud}
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
@RestController
public class InfomexSolicitudController extends CustomRestController implements RESTController<InfomexSolicitud> {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(InfomexSolicitudController.class);

	/** Referencia hacia el REST controller de {@link Asunto}. */
	@Autowired
	private AsuntoController asuntoController;

	/** Referencia hacia el REST controller {@link RepositoryController}. */
	@Autowired
	private RepositoryController repositorioController;

	/** Referencia hacia el REST controller de {@link DocumentoAsuntoController}. */
	@Autowired
	private DocumentoAsuntoController documentoAsuntoController;

	/** Referencia hacia el REST controller de {@link RespuestaController}} */
	@Autowired
	private RespuestaController respuestaRespuestaController;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RestController#get(java.io.Serializable)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene detalle solicitud", notes = "Obtiene el detalle de la solicitud infomex")
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

	@RequestMapping(value = "/infomexSolicitud", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InfomexSolicitud> get(
			@RequestParam(value = "idFolio", required = true) String idFolio,
			@RequestParam(value = "idInstitucion", required = true) int idInstitucion) {

		InfomexSolicitud item = null;
		try {

			InfomexSolicitudKey infomexSolicitudKey = new InfomexSolicitudKey();
			infomexSolicitudKey.setFolioSisi(idFolio);
			infomexSolicitudKey.setIdInstitucion(idInstitucion);

			item = mngrInfomexSolicitud.fetch(infomexSolicitudKey);

			log.debug(item);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		log.debug(" Item Out >> " + item);

		return new ResponseEntity<InfomexSolicitud>(item, HttpStatus.OK);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#search(java.lang.Object)
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consulta solicitud ", notes = "Consulta las solicitudes registradas")
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
	@RequestMapping(value = "/infomexSolicitud", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> search(@RequestBody(required = true) InfomexSolicitud infomexSolicitud)
			throws Exception {

		List<?> lst = new ArrayList<InfomexSolicitud>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		log.info("Parametros de busqueda :: " + infomexSolicitud);

		try {

			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<Criterion>();

			if (infomexSolicitud.getInfomexSolicitudKey() != null) {

				if (StringUtils.isNotBlank(infomexSolicitud.getInfomexSolicitudKey().getFolioSisi())) {
					// restrictions.add(Restrictions.eq("infomexSolicitudKey.folioSisi",
					// infomexSolicitud.getInfomexSolicitudKey().getFolioSisi()));
					restrictions.add(EscapedLikeRestrictions.ilike("infomexSolicitudKey.folioSisi",
							infomexSolicitud.getInfomexSolicitudKey().getFolioSisi(), MatchMode.ANYWHERE));

				}
				if (infomexSolicitud.getInfomexSolicitudKey().getIdInstitucion() != null) {
					restrictions.add(Restrictions.eq("infomexSolicitudKey.idInstitucion",
							infomexSolicitud.getInfomexSolicitudKey().getIdInstitucion()));
				}
			}

			if ((infomexSolicitud.getStatuSolicitud() != null)
					&& (infomexSolicitud.getStatuSolicitud().getIdStatus() != null)) {
				restrictions.add(
						Restrictions.eq("statuSolicitud.idStatus", infomexSolicitud.getStatuSolicitud().getIdStatus()));
			}

			if (infomexSolicitud.getArchivo() != null && infomexSolicitud.getArchivo().getNombreArchivo() != null)
				restrictions.add(
						Restrictions.eq("archivo.nombreArchivo", infomexSolicitud.getArchivo().getNombreArchivo()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_tipo()))
				restrictions.add(Restrictions.eq("us_tipo", infomexSolicitud.getUs_tipo()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_subEnl()))
				restrictions.add(Restrictions.eq("us_subEnl", infomexSolicitud.getUs_subEnl()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_arcDes()))
				restrictions.add(Restrictions.eq("us_arcDes", infomexSolicitud.getUs_arcDes()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_nombre()))
				restrictions.add(EscapedLikeRestrictions.ilike("us_nombre", infomexSolicitud.getUs_nombre(),
						MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_apePat()))
				restrictions.add(EscapedLikeRestrictions.ilike("us_apePat", infomexSolicitud.getUs_apePat(),
						MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_apeMat()))
				restrictions.add(EscapedLikeRestrictions.ilike("us_apeMat", infomexSolicitud.getUs_apeMat(),
						MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_corEle()))
				restrictions.add(Restrictions.eq("us_corEle", infomexSolicitud.getUs_corEle()));

			if (infomexSolicitud.getStatus() != null && infomexSolicitud.getStatus().getId() != null)
				restrictions.add(Restrictions.eq("status.id", infomexSolicitud.getStatus().getId()));

			if (infomexSolicitud.getUs_fecRecepcion() != null) {
				// restrictions.add(Restrictions.eq("us_fecRecepcion",
				// infomexSolicitud.getUs_fecRecepcion()));
				String format1 = sdf.format(infomexSolicitud.getUs_fecRecepcion()).toString() + " 00:00";
				String format2 = sdf.format(infomexSolicitud.getUs_fecRecepcion()).toString() + " 23:59";

				restrictions.add(Restrictions.between("us_fecRecepcion", //
						sdf2.parse(format1), //
						sdf2.parse(format2)));
			}

			if (infomexSolicitud.getModoEntrega() != null
					&& StringUtils.isNotBlank(infomexSolicitud.getModoEntrega().getDescripcion()))
				restrictions.add(
						Restrictions.eq("modoEntrega.descripcion", infomexSolicitud.getModoEntrega().getDescripcion()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_unienl()))
				restrictions.add(Restrictions.eq("us_unienl", infomexSolicitud.getUs_unienl()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_otroMod()))
				restrictions.add(Restrictions.eq("us_otroMod", infomexSolicitud.getUs_otroMod()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_otrosDatos()))
				restrictions.add(EscapedLikeRestrictions.ilike("us_otrosDatos", infomexSolicitud.getUs_otrosDatos(),
						MatchMode.ANYWHERE));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_curp()))
				restrictions.add(Restrictions.eq("us_curp", infomexSolicitud.getUs_curp()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_sexo()))
				restrictions.add(Restrictions.eq("us_sexo", infomexSolicitud.getUs_sexo()));

			if (infomexSolicitud.getUs_fecNac() != null)
				restrictions.add(Restrictions.eq("us_fecNac", infomexSolicitud.getUs_fecNac()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_ocupacion()))
				restrictions.add(Restrictions.eq("us_ocupacion", infomexSolicitud.getUs_ocupacion()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_calle()))
				restrictions.add(Restrictions.eq("us_calle", infomexSolicitud.getUs_calle()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_numExt()))
				restrictions.add(Restrictions.eq("us_numExt", infomexSolicitud.getUs_numExt()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_numInt()))
				restrictions.add(Restrictions.eq("us_numInt", infomexSolicitud.getUs_numInt()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_col()))
				restrictions.add(Restrictions.eq("us_col", infomexSolicitud.getUs_col()));

			if (StringUtils.isNotBlank(infomexSolicitud.getKe_claEst()))
				restrictions.add(Restrictions.eq("ke_claEst", infomexSolicitud.getKe_claEst()));

			if (StringUtils.isNotBlank(infomexSolicitud.getKmu_claMun()))
				restrictions.add(Restrictions.eq("kmu_claMun", infomexSolicitud.getKmu_claMun()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_codPos()))
				restrictions.add(Restrictions.eq("us_codPos", infomexSolicitud.getUs_codPos()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_tel()))
				restrictions.add(Restrictions.eq("us_tel", infomexSolicitud.getUs_tel()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_edoExt()))
				restrictions.add(Restrictions.eq("us_edoExt", infomexSolicitud.getUs_edoExt()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_ciudadExt()))
				restrictions.add(Restrictions.eq("us_ciudadExt", infomexSolicitud.getUs_ciudadExt()));

			if (StringUtils.isNotBlank(infomexSolicitud.getUs_rfc()))
				restrictions.add(Restrictions.eq("us_rfc", infomexSolicitud.getUs_rfc()));

			List<Order> orders = new ArrayList<Order>();

			orders.add(Order.asc("infomexSolicitudKey.folioSisi"));

			// * * * * * * * * * * * * * * * * * * * * * *
			lst = mngrInfomexSolicitud.search(restrictions, orders);

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

	@ApiOperation(value = "Registrar archivo", notes = "Registra un archivo en el sistema")
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
	@RequestMapping(value = "/infomexSolicitud", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<InfomexSolicitud> save(
			@RequestBody(required = true) InfomexSolicitud infomexSolicitud) throws Exception {

		log.debug("INFOMEX A GUARDAR >> " + infomexSolicitud);

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		// folder de adjuntos del asunto infomex
		String newFolder = null;

		try {

			if (infomexSolicitud.getInfomexSolicitudKey() != null
					&& infomexSolicitud.getInfomexSolicitudKey().getFolioSisi() != null
					&& !infomexSolicitud.getInfomexSolicitudKey().getFolioSisi().isEmpty()
					&& infomexSolicitud.getInfomexSolicitudKey().getIdInstitucion() != null
					&& infomexSolicitud.getStatuSolicitud() != null//
					&& infomexSolicitud.getModoEntrega() != null
					&& infomexSolicitud.getStatuSolicitud().getIdStatus() != null
					&& infomexSolicitud.getArchivo() != null) {

				ResponseEntity<InfomexSolicitud> existResponse = get(
						infomexSolicitud.getInfomexSolicitudKey().getFolioSisi(),
						infomexSolicitud.getInfomexSolicitudKey().getIdInstitucion());

				boolean exist = existResponse.getBody() != null;

				if (exist) {

					// DO update.

					mngrInfomexSolicitud.update(infomexSolicitud);

					return new ResponseEntity<InfomexSolicitud>(infomexSolicitud, HttpStatus.OK);

				} else {

					// Validamos que las reglas de validacion de la entidad Tipo
					// InfomexSolicitud no se esten violando con este nuevo
					// registro
					validateEntity(mngrInfomexSolicitud, infomexSolicitud);

					String parentFolderPath = getFolderInfomex();

					String parentFolderId = endpoint.getFolderIdByPath(parentFolderPath);

					if (parentFolderId == null)
						throw new BadRequestException(
								"No se encuentra creado el folder para almacenar los archivos adjuntos a la solicitud infomex.");

					newFolder = endpoint.createFolderIntoId(parentFolderId,
							environment.getProperty("folderTypeInfomexSolicitud"),
							infomexSolicitud.getInfomexSolicitudKey().getFolioSisi());

					infomexSolicitud.setContentId(newFolder);

					// Guardamos la informacion
					mngrInfomexSolicitud.save(infomexSolicitud);

					return new ResponseEntity<InfomexSolicitud>(infomexSolicitud, HttpStatus.OK);

				}

			} else {
				return new ResponseEntity<InfomexSolicitud>(infomexSolicitud, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			if (newFolder != null) {
				try {

					// Se elimina para sustituir por renombrar el folder
					// endpoint.eliminarFolder(newFolder);

					StringBuilder nuevoNombreFolder = new StringBuilder();
					nuevoNombreFolder.append("folioSisi_")
							.append(infomexSolicitud.getInfomexSolicitudKey().getFolioSisi()).append("_BACKUP_ERROR_")
							.append(dateFormat.format(new Date()));

					endpoint.renameFolder(newFolder, nuevoNombreFolder.toString());

				} catch (Exception e1) {
					log.error(e1.getLocalizedMessage());
				}
			}

			throw e;
		}

	}

	/**
	 * Cargar archivos infomex.
	 * 
	 * @param lstFileBase64
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Cargar archivos infomex", notes = "Carga un archivo infomex en el sistema")
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

	@RequestMapping(value = "/infomexSolicitud/file", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Map<String, Object>> processFileSisi(
			@RequestBody(required = true) List<FileBase64> lstFileBase64) throws Exception {

		Map<String, Object> items = new HashMap<String, Object>();

		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		Area area = mngrArea.fetch(areaId);

		log.info("ARCHIVOS A PROCESAR :: " + lstFileBase64.size());

		try {

			if (!lstFileBase64.isEmpty() //
					&& area != null) {

				int indexArchivos = 0;

				List<Map<String, String>> archivosNoProcesados = new ArrayList<Map<String, String>>();
				List<Map<String, String>> registrosNoProcesados = new ArrayList<Map<String, String>>();
				List<InfomexSolicitud> registrados = new ArrayList<InfomexSolicitud>();

				byte[] base64decodedBytes;

				Map<String, File> archivosAdjuntos;

				List<String> listFolioSisi = new ArrayList<String>();

				for (FileBase64 fileBase64 : lstFileBase64) {

					indexArchivos++;

					log.info("PROCESANDO ARCHIVO :: " + indexArchivos + " DE " + lstFileBase64.size() + ".");

					// Se Valida contenido del objeto
					if (fileBase64.getFileName() != null //
							&& !fileBase64.getFileName().isEmpty()//
							&& fileBase64.getStringBase64() != null //
							&& !fileBase64.getStringBase64().isEmpty()) {

						List<String> contenidos = new ArrayList<String>();

						archivosAdjuntos = new HashMap<String, File>();

						if (fileBase64.getFileName().toLowerCase().trim().endsWith(".zip")) {

							// si es un ZIP se descomprime
							File zipFile = FileUtil.B64StringToFile(fileBase64.getStringBase64(),
									fileBase64.getFileName());

							Map<String, File> files = FileUtil.unZipIt2(zipFile.getAbsolutePath());

							try {
								if (zipFile != null && zipFile.exists())
									zipFile.delete();
							} catch (Exception e) {
							}

							log.debug("Unzip file result :: " + files.size());

							File file;

							for (String key : files.keySet()) {

								file = files.get(key);

								log.debug(" >> " + key);

								if (key.toLowerCase().trim().endsWith(".txt")) {
									contenidos.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
									try {
										if (file != null && file.exists())
											file.delete();
									} catch (Exception e) {
									}
								} else {
									log.debug(" >> >> Adjunto!! ");
									archivosAdjuntos.put(key, file);
								}

							}

						} else if (fileBase64.getFileName().toLowerCase().trim().endsWith(".txt")) {

							// Se decodifica el StringBase64 si solo es el
							// archivo directo.
							base64decodedBytes = Base64.getDecoder()
									.decode(fileBase64.getStringBase64().getBytes("UTF-8"));
							String contenidoFile = new String(base64decodedBytes, Charset.forName("UTF-8"));

							contenidos.add(contenidoFile);

						} else {

							log.warn("Extencion de archivo desconocido :: " + fileBase64.getFileName());

							Map<String, String> regNoProcesado = new HashMap<String, String>();
							regNoProcesado.put("errorMessage",
									fileBase64.getFileName() + " :: Tipo De Archivo desconocido. ");
							registrosNoProcesados.add(regNoProcesado);

							continue;

						}

						if (contenidos.isEmpty()) {

							log.error("Registro existente.");
							Map<String, String> regNoProcesado = new HashMap<String, String>();
							regNoProcesado.put("errorMessage", " :: " + errorMessages.getString("infomexNoTxtInZip"));
							registrosNoProcesados.add(regNoProcesado);

						} else
							for (String contenidoFile : contenidos) {

								// se divide por renglones
								String[] linesFile = contenidoFile.split("\n");

								String[] dataLine;

								log.info("ARCHIVO :: " + indexArchivos + " :: " + fileBase64.getFileName()
										+ " CONTIENE " + linesFile.length + " LINEAS.");

								for (int i = 0; i < linesFile.length; i++) {

									log.debug("PROCESANDO LINEA NO. " + (i + 1) + " DE " + linesFile.length + ".");

									try {
										// Separar linea por |
										dataLine = linesFile[i].split("\\|");

										InfomexSolicitudKey infomexSolicitudKey = new InfomexSolicitudKey();
										infomexSolicitudKey.setFolioSisi(dataLine[0]);
										infomexSolicitudKey.setIdInstitucion(area.getInstitucion().getIdInstitucion());

										// se busca si ya se habia registrado.
										InfomexSolicitud infomexSolicitud = mngrInfomexSolicitud
												.fetch(infomexSolicitudKey);

										if (infomexSolicitud != null) {

											log.debug("Registro existente.");
											Map<String, String> regNoProcesado = new HashMap<String, String>();
											regNoProcesado.put("errorMessage",
													dataLine[0] + " :: Ya se encuentra registrado");
											registrosNoProcesados.add(regNoProcesado);

										} else {

											// llenar el objeto con la data de
											// la
											// linea
											infomexSolicitud = processLine(dataLine, areaId,
													area.getInstitucion().getIdInstitucion(), fileBase64.getFileName());

											// --> sección para validar tamaño de la descripcion infomex cuando es
											// Oracle
											if (ResourceBundle.getBundle("application").getString("hibernate.db_type")
													.equalsIgnoreCase("ORACLE")) {
												if (infomexSolicitud.getUs_datDes().getBytes("utf8").length > 4000) {
													listFolioSisi.add(
															infomexSolicitud.getInfomexSolicitudKey().getFolioSisi());
													infomexSolicitud.setUs_datDes(
															"Se adjunta archivo PDF con la descripción de la Solicitud de Información");
												}
											}
											// <--

											// Guardar el registro
											ResponseEntity<InfomexSolicitud> resultSave = save(infomexSolicitud);

											// Validar si el registro se guardo
											if (resultSave.getStatusCode().equals(HttpStatus.OK)) {

												InfomexSolicitud infomexSolicitudGuardada = resultSave.getBody();

												registrados.add(infomexSolicitudGuardada);

												saveAdjuntosInfomex(infomexSolicitudGuardada, archivosAdjuntos);

											} else {

												Map<String, String> regNoProcesado = new HashMap<String, String>();
												regNoProcesado.put("errorMessage",
														infomexSolicitud.getInfomexSolicitudKey().getFolioSisi()
																+ resultSave.getStatusCode().toString());
												registrosNoProcesados.add(regNoProcesado);

												log.error(regNoProcesado.toString());

											}

										}

									} catch (Exception e) {

										log.error(e.getLocalizedMessage());

										Map<String, String> regNoProcesado = new HashMap<String, String>();
										regNoProcesado.put("errorMessage", fileBase64.getFileName() + " @ line "
												+ (i + 1) + " :: " + e.getLocalizedMessage());
										registrosNoProcesados.add(regNoProcesado);
									}
								}
							}

					} else {

						Map<String, String> fileNoProcesado = new HashMap<String, String>();
						fileNoProcesado.put("errorMessage", "Archivo #" + indexArchivos + " >> "
								+ fileBase64.getFileName() + " :: Datos incompletos o erroneos.");
						log.error(fileNoProcesado.toString());
						archivosNoProcesados.add(fileNoProcesado);
					}

				}

				items.put("folioSisiDescripcionExcesiva", listFolioSisi);
				items.put("archivosNoProcesados", archivosNoProcesados);
				items.put("registrosNoProcesados", registrosNoProcesados);
				items.put("registrados", registrados);

			} else {

				return new ResponseEntity<Map<String, Object>>(items, HttpStatus.BAD_REQUEST);

			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

		return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);

	}

	/**
	 * Obtiene el status de la solicitud infomex por defecto dependiendo al area
	 * conectada.
	 * 
	 * @param idArea
	 * @return
	 */
	private InfomexModalidadEntrega getDefaultModalidadEntrega(Integer idArea) throws BadRequestException {

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("parametroKey.idClave", "DEFAULTMODENTR"));
		restrictions.add(Restrictions.eq("parametroKey.idSeccion", "SISI"));
		restrictions.add(Restrictions.eq("parametroKey.idArea", idArea));

		List<?> result = mngrParametro.search(restrictions);

		InfomexModalidadEntrega statusDefault;

		if (result == null || result.isEmpty()) {

			throw new BadRequestException(
					"No hay modalidad de entrega configurada por defecto para el area id " + idArea);

		} else {
			Parametro p = (Parametro) result.get(0);

			log.debug("modalidad entrega default para el area " + idArea + " es " + p + " obteniendo...");

			statusDefault = mngrInfomexModalidadEntrega.fetch(Integer.valueOf(p.getValor()));
		}

		log.info("modalidad entrega :: " + idArea + " es " + statusDefault);

		return statusDefault;

	}

	/**
	 * Guarda los documentos que se encuentrar en el zip y q no pueden ser
	 * procesados como documentos adjuntos.
	 * 
	 * @param infomexSolicitudGuardada
	 * @param archivosAdjuntos
	 * @throws Exception
	 */
	private void saveAdjuntosInfomex(InfomexSolicitud infomexSolicitud, Map<String, File> archivos) throws Exception {

		IEndpoint cmisDispatcher = null;
		String newDoc = null;
		File file = null;

		String folioSisiCompare = infomexSolicitud.getInfomexSolicitudKey().getFolioSisi();

		String pathPartial = File.separatorChar + folioSisiCompare + File.separatorChar;

		try {
			cmisDispatcher = EndpointDispatcher.getInstance();

			for (String key : archivos.keySet()) {

				file = archivos.get(key);

				if (file.getAbsolutePath().contains(pathPartial) || key.contains(folioSisiCompare)) {

					newDoc = cmisDispatcher.saveDocumentoIntoId(//
							infomexSolicitud.getContentId(), //
							key, //
							environment.getProperty("docTypeInfomexSolicitudAdjunto"), //
							Version.NONE, "Documento adjunto en el ZIP.", //
							file);

					try {
						if (file != null && file.exists())
							file.delete();
					} catch (Exception e) {
					}

					log.debug("Archivo Adjunto :: " + key + " :: " + newDoc);

				}
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Process line.
	 *
	 * @param line the line
	 * @return the ifai sisi solicitud
	 */
	private InfomexSolicitud processLine(String[] line, int idArea, int idInstitucion, String fileName)
			throws Exception {

		String data[] = new String[line.length];

		for (int i = 0; i < line.length; i++) {
			if (!line[i].trim().equals("NULL")) {
				data[i] = line[i].trim();
			} else {
				data[i] = "";
			}
		}
		Date fechaRecepcion = null;
		Date fechaNac = null;

		if (data[3].trim().equals("") || data[3] == null) {
			// no data to convertv
		} else {
			fechaRecepcion = FechaUtil.stringToDate(data[3]);
		}

		if (data[23].trim().equals("") || data[23] == null) {
			// no data to convert
		} else {
			fechaNac = FechaUtil.stringToDate(data[23]);
		}

		InfomexSolicitud infomexSolicitud = new InfomexSolicitud();

		InfomexSolicitudKey infomexSolicitudKey = new InfomexSolicitudKey();
		infomexSolicitudKey.setFolioSisi(data[0]);
		infomexSolicitudKey.setIdInstitucion(idInstitucion);

		Status statuColicitud = mngrStatus.fetch(Status.POR_ENVIAR);

		infomexSolicitud.setInfomexSolicitudKey(infomexSolicitudKey);
		infomexSolicitud.setUs_unienl(data[1]);
		infomexSolicitud.setUs_tipo(data[2]);
		infomexSolicitud.setUs_fecRecepcion(fechaRecepcion);
		infomexSolicitud.setUs_repLegal(data[4]);
		infomexSolicitud.setUs_rfc(data[5]);
		infomexSolicitud.setUs_apePat(data[6]);
		infomexSolicitud.setUs_apeMat(data[7]);
		infomexSolicitud.setUs_nombre(data[8]);
		infomexSolicitud.setUs_curp(data[9]);
		infomexSolicitud.setUs_calle(data[10]);
		infomexSolicitud.setUs_numExt(data[11]);
		infomexSolicitud.setUs_numInt(data[12]);
		infomexSolicitud.setUs_col(data[13]);

		// infomexSolicitud.setEstado(mngrInfomexEntidadFederativa.fetch(Integer.parseInt(data[14])));
		infomexSolicitud.setKe_claEst(data[14]);

		// infomexSolicitud.setMunicipio(mngrInfomexMunicipio.fetch(Integer.parseInt(data[15])));
		infomexSolicitud.setKmu_claMun(data[15]);

		infomexSolicitud.setUs_codPos(data[16]);
		infomexSolicitud.setUs_tel(data[17]);
		infomexSolicitud.setUs_corEle(data[18]);

		// infomexSolicitud.setPais(mngrInfomexPais.fetch(Integer.parseInt(data[19])));
		infomexSolicitud.setUs_idPais(data[19]);

		infomexSolicitud.setUs_edoExt(data[20]);
		infomexSolicitud.setUs_ciudadExt(data[21]);
		infomexSolicitud.setUs_sexo(data[22]);
		infomexSolicitud.setUs_fecNac(fechaNac);

		// infomexSolicitud.setOcupacion(mngrInfomexOcupacion.fetch(Integer.parseInt(data[24])));
		infomexSolicitud.setUs_ocupacion(data[24]);

		infomexSolicitud.setModoEntrega(getDefaultModalidadEntrega(idArea));
		// infomexSolicitud.setUs_modEnt(data[25]);

		infomexSolicitud.setUs_otroMod(data[26]);
		infomexSolicitud.setUs_arcDes(data[27]);
		try {
			infomexSolicitud.setUs_datDes(data[28]);
		} catch (IndexOutOfBoundsException e) {
			infomexSolicitud.setUs_datDes("");
			log.debug("FALTA EL PENULTIMO (29) DATO EN EL FOLIO INFORMEX, SE GUARDA COMO VACIO :: ");
		}

		try {
			infomexSolicitud.setUs_otrosDatos(data[29]);
		} catch (IndexOutOfBoundsException e) {
			infomexSolicitud.setUs_otrosDatos("");
			log.debug("FALTA EL ULTIMO (30) DATO EN EL FOLIO INFORMEX, SE GUARDA COMO VACIO :: ");
		}

		// infomexSolicitud.setIdArchivo(0);
		InfomexArchivo archivo = new InfomexArchivo();

		archivo.setAtributos("");
		archivo.setFechaProceso(null);
		archivo.setFechaRegistro(new Date());
		archivo.setNombreArchivo(fileName);
		archivo.setStatus(mngrStatus.fetch(Status.ENVIADO));
		archivo.setIdSentido("E");
		archivo.setTipoArchivo("T");

		infomexSolicitud.setArchivo(archivo);

		infomexSolicitud.setDescRespuesta("0");
		// infomexSolicitud.setStatus(getStatusDefault(idArea));
		infomexSolicitud.setStatuSolicitud(statuColicitud);

		return infomexSolicitud;
	}

	/**
	 * 
	 * @param infomexSolicitudAsunto
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Generar asunto", notes = "Genera un asunto a partir de un archivo infomex")
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

	@RequestMapping(value = "/infomexSolicitud/generarAsunto", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Asunto> gernerarAsuntoInfomex(
			@RequestBody(required = true) InfomexSolicitudAsunto infomexSolicitudAsunto) throws Exception {

		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
		if (!esSoloLectura(userId)) {

			log.info("Parametros :: " + infomexSolicitudAsunto);

			InfomexSolicitud infomexSolicitud = infomexSolicitudAsunto.getInfomexSolicitud();

			InfomexSolicitud infomexSolicitudOriginal = mngrInfomexSolicitud
					.fetch(infomexSolicitud.getInfomexSolicitudKey());

			if (infomexSolicitudOriginal.getStatuSolicitud().getIdStatus() == Status.POR_ENVIAR) {

				if (infomexSolicitud.getStatus() != null)
					if (infomexSolicitud.getStatus().getId() != null)
						infomexSolicitud.setStatus(mngrInfomexStatus.fetch(infomexSolicitud.getStatus().getId()));
					else
						infomexSolicitud.setStatus(null);

				// se actualiza si trae cambios.
				mngrInfomexSolicitud.update(infomexSolicitud);

				/**
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 */

				Asunto asunto = new Asunto();

				asunto.setAsuntoDetalle(new AsuntoDetalle());
				// TODO
				// Validar porque da error cuando se cambiar a ASUNTO
				asunto.setTipoAsunto(TipoAsunto.ASUNTO);
				asunto.setIdTipoRegistro("T");
				asunto.setIdSubTipoAsunto(SubTipoAsunto.T.getValue());

				asunto.setStatusAsunto(mngrStatus.fetch(Status.POR_ENVIAR));
				asunto.getAsuntoDetalle().setConfidencial(false);

				// ??? - no se almacena.
				// String instruccionAdcional =
				// infomexSolicitud.getUs_otrosDatos();

				// asunto.getAsuntoDetalle().setIdProcedencia("E");

				asunto.getAsuntoDetalle().setTipoRegistro(TipoRegistro.INFOMEX);

				asunto.getAsuntoDetalle().setFechaElaboracion(infomexSolicitud.getUs_fecRecepcion());

				asunto.getAsuntoDetalle().setFechaRecepcion(infomexSolicitud.getUs_fecRecepcion());

				asunto.getAsuntoDetalle().setAsuntoDescripcion(infomexSolicitud.getUs_datDes());

				asunto.setArea(mngrArea.fetch(areaId));

				asunto.setFechaRegistro(
						getCurrentTime(getStampedData(infomexSolicitud, TipoTimestamp.TIMESTAMP_REGISTRO)));

				asunto.setTurnador(mngrRepresentante.fetch(userId));

				asunto.getAsuntoDetalle().setNumDocto(infomexSolicitud.getInfomexSolicitudKey().getFolioSisi());

				// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
				// +

				if (infomexSolicitudAsunto.getAsuntoCorrespondencia() != null) {

					if (infomexSolicitudAsunto.getAsuntoCorrespondencia().getEvento() != null
							&& infomexSolicitudAsunto.getAsuntoCorrespondencia().getEvento().getIdEvento() != null) {

						asunto.setEvento(mngrTipoEvento
								.fetch(infomexSolicitudAsunto.getAsuntoCorrespondencia().getEvento().getIdEvento()));

					}

					if (infomexSolicitudAsunto.getAsuntoCorrespondencia().getTema() != null
							&& infomexSolicitudAsunto.getAsuntoCorrespondencia().getTema().getIdTema() != null) {

						asunto.setTema(mngrTema
								.fetch(infomexSolicitudAsunto.getAsuntoCorrespondencia().getTema().getIdTema()));

					}

					if (infomexSolicitudAsunto.getAsuntoCorrespondencia().getSubtema() != null
							&& infomexSolicitudAsunto.getAsuntoCorrespondencia().getSubtema().getIdSubTema() != null) {

						asunto.setSubTema(mngrSubTema
								.fetch(infomexSolicitudAsunto.getAsuntoCorrespondencia().getSubtema().getIdSubTema()));

					}

					asunto.setFechaEvento(infomexSolicitudAsunto.getAsuntoCorrespondencia().getFechaEvento());

				}

				// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +

				// se setea a anotacion, porque comentario es para "Intruccion adicional"
				asunto.setAnotacion(infomexSolicitudAsunto.getComentario());

				// String folioArea = asuntoController.getFolioArea(areaId);
				//
				// asunto.setFolioArea(folioArea);

				asunto.getAsuntoDetalle().setIdExterno(infomexSolicitud.getInfomexSolicitudKey().getFolioSisi());

				// TipoExpediente expediente = getDefaultExpediente(areaId);
				//
				// asunto.setTipoExpediente(expediente);
				// asunto.getAsuntoCorrespondencia().setExpediente(expediente);

				/**
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 */

				log.debug(" Asunto Generado >> " + asunto);

				ResponseEntity<Asunto> response = asuntoController.save(asunto);

				if (response.getStatusCode() == HttpStatus.CREATED) {

					// se cambia el estatus del asunto infomex
					infomexSolicitud.setStatuSolicitud(mngrStatus.fetch(Status.PROCESO));

					mngrInfomexSolicitud.update(infomexSolicitud);

					Asunto asuntoNuevo = response.getBody();

					ResponseEntity<List<Map<String, String>>> documentosAdjuntosResponse = getDocumentos(
							infomexSolicitud.getInfomexSolicitudKey().getFolioSisi());

					List<Map<String, String>> documentosAdjuntos = documentosAdjuntosResponse.getBody();

					DocumentoAsunto documento;

					for (Map<String, String> doc : documentosAdjuntos) {

						documento = new DocumentoAsunto();

						documento.setFechaRegistro(new Date());

						ResponseEntity<Map<String, Object>> resp = repositorioController
								.getDocument(doc.get("r_object_id"), null);
						documento.setFileB64(resp.getBody().get("contentB64").toString());

						documento.setIdArea(areaId);
						documento.setIdAsunto(asuntoNuevo.getIdAsunto());
						documento.setObjectName(doc.get("object_name"));

						documentoAsuntoController.save(documento);
					}

					return new ResponseEntity<Asunto>(asuntoNuevo, HttpStatus.OK);

				} else {

					String message = response.getBody().toString();
					throw new Exception("Error al geenerar el asunto :: " + message);

				}

			} else {
				throw new BadRequestException(
						"El tramite se encuentra en un estado diferente a enviado. No se puede generar un asunto.");
			}
		} else {
			return new ResponseEntity<Asunto>(new Asunto(), HttpStatus.BAD_REQUEST);
		}
	}

	// /**
	// *
	// * @param areaId
	// * @return
	// */
	// private TipoExpediente getDefaultExpediente(Integer areaId) {
	//
	// ResponseEntity<Parametro> p = parametroController.get(areaId.toString(),
	// "DEFAULT", "IDEXPEDIENTE");
	//
	// String idExpedienteDefault = p.getBody().getValor();
	//
	// TipoExpediente expediente =
	// mngrTipoExpediente.fetch(idExpedienteDefault);
	//
	// return expediente;
	//
	// }

	/**
	 * 
	 * Obtene el folder default donde se guardan los archivos adjuntos al asunto
	 * infomex.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getFolderInfomex() {

		log.debug("Obteniendo folder raiz para adjuntos de infomex...");

		List<Criterion> restrictions = new ArrayList<>();

		restrictions.add(Restrictions.eq("idSeccion", "SIGAP"));
		restrictions.add(Restrictions.eq("idClave", "CABINETEXTERNO"));

		List<ParametroApp> result = (List<ParametroApp>) mngrParamApp.search(restrictions);

		if (result.isEmpty())
			throw new BadRequestException("No se a definido parametro \"CABINETEXTERNO\" en configuracion.");

		String valor = result.get(0).getValor();

		log.debug("CABINETEXTERNO :: " + valor);

		String property = environment.getProperty("folderNameInfomexFolder");

		String folder = valor + "/" + property;

		log.info("FOLDER INFOMEX ADJUNTOS :: " + folder);

		return folder;

	}

	private String getStampedData(InfomexSolicitud sol, TipoTimestamp tipots) {
		String toBeStamped = sol.getInfomexSolicitudKey().getFolioSisi() + "-" + tipots.getTipo();

		return toBeStamped;
	}

	/**
	 * 
	 * @param idFolio
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Obtiene documentos", notes = "Obtiene los documentos de una solicitud infomex")
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

	@RequestMapping(value = "/infomexSolicitud/documentos", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Map<String, String>>> getDocumentos(
			@RequestParam(value = "idFolio", required = true) String idFolio) throws Exception {

		try {
			Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			Area area = mngrArea.fetch(areaId);

			InfomexSolicitudKey infomexSolicitudKey = new InfomexSolicitudKey();

			infomexSolicitudKey.setFolioSisi(idFolio);
			infomexSolicitudKey.setIdInstitucion(area.getInstitucion().getIdInstitucion());

			InfomexSolicitud infomexSolicitud = mngrInfomexSolicitud.fetch(infomexSolicitudKey);

			String folderInfomexId = infomexSolicitud.getContentId();

			List<Map<String, String>> documentos = EndpointDispatcher.getInstance()
					.obtenerDocumentosInfomex(folderInfomexId);

			return new ResponseEntity<List<Map<String, String>>>(documentos, HttpStatus.OK);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.controller.RESTController#get(java.io.Serializable)
	 */
	@Override
	public ResponseEntity<InfomexSolicitud> get(Serializable id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Generar archivo ZIP de la respuesta infomex.
	 * 
	 * @param idAsunto
	 * @return
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Generar zip respuesta", notes = "Genera un zip con los documentos de las respuestas")
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
	@RequestMapping(value = "/infomexSolicitud/respuesta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map<String, Object>> getRespuestasInfomex(
			@RequestBody(required = true) Map<String, Object> params) throws Exception {

		Serializable idAsunto = (Serializable) params.get("idAsunto");
		List<String> documentosMarcados = (List<String>) params.get("documentos");

		final String pipe = "|";
		final String tipoInformacion = "0";

		log.debug("Generando el archivo ZIP Infomex para el asunto " + idAsunto);

		Asunto asunto = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			try {
				validateAsunto(Integer.valueOf((String) idAsunto));
			} catch (IllegalArgumentException e) {
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
			}

			asunto = mngrAsunto.fetch(Integer.valueOf((String) idAsunto));

			// Buscamos todas las respuestas que se marcaron para ser generadas
			// en el zip,
			Respuesta respuestaSearch = new Respuesta();
			respuestaSearch.setIdAsunto(asunto.getIdAsunto());
			respuestaSearch.setInfomexZip(Boolean.TRUE);

			ResponseEntity<List<?>> response = respuestaRespuestaController.search(respuestaSearch);
			Set<String> documentosAdjuntos = new HashSet<>();

			// Path path = Files.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX +
			// "RESP_" + idAsunto, ".txt");
			Path path = Files.createTempFile("RESP_" + idAsunto, ".txt");

			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

				if (response.getStatusCode() == HttpStatus.OK) {

					List<RespuestaConsulta> respuestas = (List<RespuestaConsulta>) response.getBody();

					for (int i = 0, size = respuestas.size(); i < size; i++) {

						RespuestaConsulta respuesta = (RespuestaConsulta) respuestas.get(i);

						String lineaRespuesta =
								// - - - -
								// asunto.getFolioArea()
								asunto.getAsuntoDetalle().getIdExterno()
										// - - - -
										+ pipe + respuesta.getTipoRespuestaDescripcion() //
										+ pipe + tipoInformacion//
										+ pipe + respuesta.getComentario()//
										+ pipe + path.getFileName();

						writer.write(lineaRespuesta);

						writer.newLine();

						// Obtenemos la lista de documentos de la respuesta para
						// copiarlos a la nueva respuesta
						List<Criterion> restrictions = new ArrayList<>();

						restrictions.add(Restrictions.eq("idRespuesta", respuesta.getIdRespuesta()));
						if (documentosMarcados == null || documentosMarcados.isEmpty()) {

							log.debug("NO SE AGREGARAN DOCUMENTOS ADJUNTOS...");

						} else {

							List<DocumentoRespuesta> documentos = (List<DocumentoRespuesta>) mngrDocsRespuesta
									.search(restrictions);

							for (DocumentoRespuesta documento : documentos) {

								// Obtenemos el contenido del documento
								if (documentosMarcados.contains(documento.getObjectId())) {

									ResponseEntity<Map<String, Object>> resp = repositorioController
											.getDocument(documento.getObjectId(), null);

									documentosAdjuntos
											.add(FileUtil.createTempFiles(resp.getBody().get("contentB64").toString(),
													documento.getObjectName()).toString());
								}
							}

							log.debug("Documentos de la respuesta" + documentosAdjuntos.toString());

						}
					}

				}
			}

			// crear el archivo zip con los documentos generados
			documentosAdjuntos.add(path.toString());
			log.debug("Iniciando la creacion del zip");

			String zipFileName = asunto.getAsuntoDetalle().getIdExterno() + ".zip";
			File zipFile = FileUtil.zipFiles(zipFileName, documentosAdjuntos);

			result.put("contentB64", FileUtil.fileToStringB64(zipFile));
			result.put("type", "application/zip");
			result.put("name", zipFileName);

			// Eliminamos los documentos generados
			documentosAdjuntos.add(zipFile.getPath());
			FileUtil.deleteFiles(documentosAdjuntos);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/**
	 * Valida la informacion del Asunto
	 * 
	 * @param idAsunto Identificador del Asunto
	 * @return Asunto {@link Asunto}
	 * @throws IllegalArgumentException Error al momento de hacer las validaciones
	 *                                  al Asunto
	 */
	private Asunto validateAsunto(Integer idAsunto) throws IllegalArgumentException {

		Asunto asunto = mngrAsunto.fetch(idAsunto);

		Integer areaId = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		if (null == asunto || !"T".equals(asunto.getIdTipoRegistro()) || !areaId.equals(asunto.getArea().getIdArea())) {
			log.error("Error al momento de ejecutar las validaciones del Asunto");
			throw new IllegalArgumentException(
					"El Asunto no existe, no es un Asunto tipo Infomex o no pertenece al Area logeada del usuario");
		}

		return asunto;
	}

	/**
	 * Valida la informacion de la Respuesta
	 * 
	 * @param idAsunto    Identificador del Asunto
	 * @param idRespuesta Identificador de la Respuesta
	 * @return Respuesta {@link Respuesta}
	 * @throws IllegalArgumentException Error al momento de hacer las validaciones
	 *                                  de la respuesta
	 */
	private Respuesta validateRespuestaAsunto(Integer idAsunto, Integer idRespuesta) throws IllegalArgumentException {

		Respuesta respuesta = mngrRespuesta.fetch(idRespuesta);

		if (respuesta == null || !respuesta.getIdAsunto().equals(idAsunto)) {
			log.error("Error al momento de ejecutar las validaciones de la Respuesta");
			throw new IllegalArgumentException(
					"La respuesta no existe o no corresponde con una respuesta del Identificador del Asunto");
		}

		return respuesta;
	}

	/**
	 * Marca/Desmarca una respuesta para que sea generada en el ZIP de Infomex. La
	 * marca consiste en colocar el atributo INFOMEXZIPSN en 'Y' para que la
	 * considere y 'N' para que no
	 * 
	 * @param idRespuesta Identificador de la Respuesta
	 * @param idAsunto    Identificador del Asunto
	 * @return Respuesta marcada para que sea generada en el ZIP de Infomex
	 * @throws Exception Error al momento de ejecutar el metodo
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Marcar/desmarcar respuesta", notes = "Marca o desmarca una respuesta de tipo infomex")
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

	@RequestMapping(value = "/infomexSolicitud/marcaRespuesta", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Respuesta> marcaRespuestaInfomex(
			@RequestBody(required = true) Respuesta respuesta) throws Exception {

		try {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			if (!esSoloLectura(userId)) {

				log.debug("Marcando respuesta para zip infomex :: " + respuesta);

				if (null == respuesta.getIdAsunto() || null == respuesta.getIdRespuesta()
						|| null == respuesta.getInfomexZip()) {
					return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);
				}
				validateAsunto(respuesta.getIdAsunto());

				Respuesta respuestaNew = validateRespuestaAsunto(respuesta.getIdAsunto(), respuesta.getIdRespuesta());
				respuestaNew.setInfomexZip(respuesta.getInfomexZip());
				mngrRespuesta.update(respuestaNew);

				return new ResponseEntity<Respuesta>(respuestaNew, HttpStatus.OK);
			} else {
				return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);
			}
		} catch (IllegalArgumentException e) {

			return new ResponseEntity<Respuesta>(new Respuesta(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Gets the infomex solicitud area.
	 *
	 * @param body the body
	 * @return the infomex solicitud area
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Consultar asuntos infomex", notes = "Consulta asuntos infomex por area")
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
	@RequestMapping(value = "/infomexSolicitud/area", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> getinfomexSolicitudArea(
			@RequestBody(required = true) RequestWrapper<InfomexSolicitud> body) {
		try {
			if (body.getParams() != null) {
				log.info("Parametro de busqueda infomex ::: " + body);
				InfomexSolicitud infomexParam = body.getObject();
				log.info("Parametro de busqueda fecha recepcion infomex ::: " + infomexParam.getUs_fecRecepcion());
				Map<String, Object> params = body.getParams();
				Integer idArea = Integer.parseInt(params.get("idArea").toString());

				List<Asunto> lstAsunto = new ArrayList<>();
				List<InfomexSolicitud> lstInfomexSolicitud = new ArrayList<>();
				log.info("Parametro de busqueda id asunto ::: " + idArea);

				Area area = mngrArea.fetch(idArea);
				if (null == area) {
					return new ResponseEntity<List<?>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
				}

				InfomexSolicitudKey infomexKey = new InfomexSolicitudKey();
				List<Criterion> restrictionsAsunto = new ArrayList<Criterion>();
				restrictionsAsunto.add(Restrictions.eq("area.idArea", idArea));
				restrictionsAsunto.add(Restrictions.eq("idSubTipoAsunto", SubTipoAsunto.T.getValue()));
				restrictionsAsunto.add(Restrictions.eq("idTipoRegistro", TipoRegistro.INFOMEX.getValue()));

				if (infomexParam.getInfomexSolicitudKey() != null
						&& StringUtils.isNotBlank(infomexParam.getInfomexSolicitudKey().getFolioSisi())) {
					restrictionsAsunto.add(Restrictions.ilike("asuntoDetalle.idExterno",
							infomexParam.getInfomexSolicitudKey().getFolioSisi(), MatchMode.ANYWHERE));
				}

				lstAsunto = (List<Asunto>) mngrAsunto.search(restrictionsAsunto);

				for (Asunto asunto : lstAsunto) {
					if (!StringUtils.isBlank(asunto.getAsuntoDetalle().getIdExterno())) {
						infomexKey.setFolioSisi(asunto.getAsuntoDetalle().getIdExterno());
						infomexKey.setIdInstitucion(area.getInstitucion().getIdInstitucion());
						infomexParam.setInfomexSolicitudKey(infomexKey);

						List<InfomexSolicitud> infomemexTemp = null;

						try {
							infomemexTemp = (List<InfomexSolicitud>) search(infomexParam).getBody();
						} catch (Exception e) {

							log.error(e.getMessage());
						}

						if (null != infomemexTemp && !infomemexTemp.isEmpty()) {
							if ((asunto.getArea() != null && asunto.getArea().getIdArea().equals(idArea))
									|| (asunto.getAreaDestino() != null
											&& asunto.getAreaDestino().getIdArea().equals(idArea))) {
								lstInfomexSolicitud.add(infomemexTemp.get(0));
							}
						}

					}
				}
				log.debug("Size found >> " + lstInfomexSolicitud.size());
				return new ResponseEntity<List<?>>(lstInfomexSolicitud, HttpStatus.OK);
			} else {
				return new ResponseEntity<List<?>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			return new ResponseEntity<List<?>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Desmarca una respuesta para que no sea generada en el ZIP de Infomex
	 * 
	 * @param idRespuesta Identificador de la Respuesta
	 * @param idAsunto    Identificador del Asunto
	 * @return Respuesta desmarcada para que sea generada en el ZIP de Infomex
	 * @throws Exception
	 */
	// @RequestMapping(value = "/infomexSolicitud/desmarcaRespuesta", method =
	// RequestMethod.PUT)
	// public @ResponseBody ResponseEntity<Respuesta> desmarcaRespuestaInfomex(
	// @RequestBody(required = true) Respuesta respuesta) {
	//
	// log.debug("Desmarcando respuesta del zip infomex :: " + respuesta);
	//
	// if (null == respuesta.getIdAsunto() || null ==
	// respuesta.getIdRespuesta()) {
	// return new ResponseEntity<Respuesta>(new Respuesta(),
	// HttpStatus.BAD_REQUEST);
	// }
	//
	// try {
	// validateAsunto(respuesta.getIdAsunto());
	//
	// Respuesta respuestaNew = validateRespuestaAsunto(respuesta.getIdAsunto(),
	// respuesta.getIdRespuesta());
	// respuestaNew.setAtributos("NNNNNNN");
	// mngrRespuesta.update(respuestaNew);
	//
	// return new ResponseEntity<Respuesta>(respuestaNew, HttpStatus.OK);
	//
	// } catch (IllegalArgumentException e) {
	// log.error("Error al momento de ejecutar las validaciones del Asunto o
	// Respuesta");
	// return new ResponseEntity<Respuesta>(new Respuesta(),
	// HttpStatus.BAD_REQUEST);
	// }
	// }

}
