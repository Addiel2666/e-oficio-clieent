/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ws.rs.BadRequestException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.bouncycastle.tsp.TimeStampToken;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.CustomAsunto;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Firmante;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.data.util.FechaUtil;
import com.ecm.sigap.eCiudadano.model.AcuseFirmado;
import com.ecm.sigap.util.SignatureUtil;
import com.ecm.sigap.util.convertes.PdfConverterService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controllador para generar un pdf con la informacion del acuse de un
 * asunto/respuesta.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public final class AcuseController extends CustomRestController {
	/** */
	private static final String LINE_RETURN = "</w:t><w:br/><w:t>";

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AcuseController.class);

	/** */
	private ResourceBundle keysAcuse = ResourceBundle.getBundle("acuseKeys");

	/** */
	@Autowired(required = true)
	@Qualifier("pdfConverterService")
	private PdfConverterService pdfConverterService;

	/** */
	@Autowired(required = true)
	@Qualifier("templateAcuseAsunto")
	private String templateAcuseAsunto;

	/** */
	@Autowired(required = true)
	@Qualifier("templateAcuseRespuesta")
	private String templateAcuseRespuesta;

	/**
	 * Referencia hacia el REST controller de {@link RepositoryController}.
	 */
	@Autowired
	private RepositoryController repositoryController;

	/**
	 * 
	 * @param idAsunto
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Descargar acuse", notes = "Descarga el acuse de un tramite")
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
	
	@RequestMapping(value = "/acuse/asunto", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> getAcuseAsunto(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		File acuse_ = null;

		try {

			acuse_ = getAcuseAsuntoFile(id);

			Map<String, String> item = formatBodyResponse(id, acuse_);

			return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		} finally {
			if (acuse_ != null && acuse_.exists())
				acuse_.delete();
		}

	}

	/**
	 * Obtiene el acuse de una Respuesta
	 * 
	 * @param id Identificador de la Respuesta de la cual se quiere obtener el acuse
	 * @return Acuse de una Respuesta
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Descargar acuse", notes = "Descarga el acuse de una respuesta")
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
	
	@RequestMapping(value = "/acuse/respuesta", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> getAcuseRespuesta(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		File acuse_ = null;
		try {

			acuse_ = getAcuseRespuestaFile(id);

			Map<String, String> item = formatBodyResponse(id, acuse_);

			return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		} finally {
			if (acuse_ != null && acuse_.exists())
				acuse_.delete();
		}
	}

	/**
	 * 
	 * Crea el body de respuesta.
	 * 
	 * @param id
	 * @param acuse_
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> formatBodyResponse(Serializable id, File acuse_) throws IOException {

		Map<String, String> item = new HashMap<>();

		item.put("type", "application/pdf");
		item.put("name", "acuse_" + id.toString() + ".pdf");
		item.put("contentB64", Base64.encodeBase64String(FileUtils.readFileToByteArray(acuse_)));

		return item;
	}

	/**
	 * Genera el documento de acuse de un asunto indicado por ID.
	 * 
	 * @param idAsunto
	 * @param acuse
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private File getAcuseAsuntoFile(Serializable idAsunto) throws Exception {

		try {
			Asunto asunto = mngrAsunto.fetch(Integer.valueOf((String) idAsunto));

			if (asunto == null) {
				throw new BadRequestException();
			}

			// id del area CIUDADANO
			String idareaCiudadano = getParamApp("IDCIUDPROMOTOR");

			String idAreaDestino = null;

			if (asunto.getAreaDestino() != null)
				idAreaDestino = asunto.getAreaDestino().getIdArea().toString();

			if (idareaCiudadano.equalsIgnoreCase(idAreaDestino)
					&& asunto.getStatusTurno().getIdStatus() != Status.POR_ENVIAR //
					&& asunto.getStatusTurno().getIdStatus() != Status.ENVIADO) {

				AcuseFirmado acuseFirmado = mngrAcuseFirmado.fetch(asunto.getIdAsunto());

				if (acuseFirmado != null) {

					String objectId = acuseFirmado.getObjectId();

					log.debug("acuse ya firmado :: " + objectId + " :: " + acuseFirmado.getIdAsunto());

					Map<String, Object> map = repositoryController.downloadDocumento(null, null, objectId);

					String tmpStringFileContent = map.get("contentB64").toString();

					File acuse = FileUtil.createTempFile(tmpStringFileContent, "a.pdf").toFile();

					acuse.deleteOnExit();

					return acuse;

				}
			}

			//
			String tmpStringFileContent = new String(templateAcuseAsunto.getBytes());

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				Asunto asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.idAsuntoPadre"), //
						asuntoPadre.getFolioArea());
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.idAsunto"), //
						idAsunto.toString());
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.noOficio"), //
						formatText(asunto.getAsuntoDetalle().getNumDocto()));
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				String d = asunto.getAsuntoDetalle().getAsuntoDescripcion();
				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.descripcion"), //
						formatText(d));
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				Area barea = asunto.getArea();

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.areaRemitente.descripcion"), //
						formatText(barea.getDescripcion()));

			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				Firmante firmante = asunto.getAsuntoDetalle().getFirmante();

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.remitente.nombres"),
						firmante != null ? firmante.getNombres() : " ");

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.remitente.paterno"),
						firmante != null ? firmante.getPaterno() : " ");

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.remitente.materno"),
						(firmante != null && StringUtils.isNotBlank(firmante.getMaterno())) ? firmante.getMaterno()
								: " ");

			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{

				Area barea = asunto.getAreaDestino();

				if (barea != null) {
					tmpStringFileContent = tmpStringFileContent.replace( //
							keysAcuse.getString("asunto.areaDestinatario.descripcion"), //
							formatText(barea.getDescripcion()));

					// - - - - - - - - - - - - - - - - - - - - - - - -{

					if (barea.getId().equals(idareaCiudadano)) {

						tmpStringFileContent = tmpStringFileContent.replace( //
								keysAcuse.getString("asunto.areaDestinatario.titular.nombres"), "N/A");

						tmpStringFileContent = tmpStringFileContent.replace( //
								keysAcuse.getString("asunto.areaDestinatario.titular.paterno"), "");

						tmpStringFileContent = tmpStringFileContent.replace( //
								keysAcuse.getString("asunto.areaDestinatario.titular.materno"), "");

					} else {
						Representante titular = barea.getTitular();
						if (null != titular) {
							tmpStringFileContent = tmpStringFileContent.replace(
									keysAcuse.getString("asunto.areaDestinatario.titular.nombres"),
									titular.getNombres());
							tmpStringFileContent = tmpStringFileContent.replace(
									keysAcuse.getString("asunto.areaDestinatario.titular.paterno"),
									titular.getPaterno());
							tmpStringFileContent = tmpStringFileContent.replace(
									keysAcuse.getString("asunto.areaDestinatario.titular.materno"),
									titular.getMaterno());
						}
					}
				}

			}
			// - - - - - - - - - - - - - - - - - - - - - - - -

			if (asunto.getIdSubTipoAsunto().equals(SubTipoAsunto.D.toString())) {
				String ciudadano = mngrCiudadano.fetch(Integer.parseInt(asunto.getDestinatario())).getNombreCompleto();
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("Ciudadano"),
						ciudadano != null ? ciudadano : "");
			} else {
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("Ciudadano"), "N/A");
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				String fechaEnvio = "";
				for (Timestamp ts : asunto.getTimestamps())
					if (ts.getTipo() == TipoTimestamp.TIMESTAMP_ENVIO) {

						TimeStampToken tst = SignatureUtil.getTimeStampToken(ts.getTimestamp());

						fechaEnvio = tst.getTimeStampInfo().getGenTime().toString() + LINE_RETURN + "TSA: "
								+ tst.getTimeStampInfo().getTsa() + LINE_RETURN + "Serial number: "
								+ tst.getTimeStampInfo().getSerialNumber() + LINE_RETURN + "Encoded: "
								+ DatatypeConverter.printBase64Binary(tst.getEncoded());

						break;
					}
				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.fechaEnvio"), fechaEnvio);
			}
			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				tmpStringFileContent = tmpStringFileContent.replace(//
						keysAcuse.getString("asunto.folioRecepcion"),
						asunto.getFolioArea() != null ? asunto.getFolioArea() : " ");
			}
			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				String fechaAcuse = "";
				for (Timestamp ts : asunto.getTimestamps())
					if (ts.getTipo() == TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO) {

						TimeStampToken tst = SignatureUtil.getTimeStampToken(ts.getTimestamp());

						fechaAcuse = tst.getTimeStampInfo().getGenTime().toString() + LINE_RETURN + "TSA: "
								+ tst.getTimeStampInfo().getTsa() + LINE_RETURN + "Serial number: "
								+ tst.getTimeStampInfo().getSerialNumber() + LINE_RETURN + "Encoded: "
								+ DatatypeConverter.printBase64Binary(tst.getEncoded());

						break;

					}
				tmpStringFileContent = tmpStringFileContent.replace(//
						keysAcuse.getString("asunto.fechaAcuse"), fechaAcuse);

			}
			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				TipoInstruccion beanins = asunto.getInstruccion();

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.instruccion.descripcion"), //
						formatText(beanins.getDescripcion()));

			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				Date d = asunto.getFechaCompromiso();

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.fechaCompromiso"), //
						d == null ? "N/A" : FechaUtil.getDateFormat(d,environment));

			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{

				List<Criterion> restrictions = new ArrayList<>();
				restrictions.add(Restrictions.eq("idAsunto", Integer.valueOf(idAsunto.toString())));

				List<DocumentoAsunto> documentos = (List<DocumentoAsunto>) mngrDocsAsunto.search(restrictions);

				StringBuffer docs_ = new StringBuffer();

				if (documentos == null || documentos.isEmpty()) {

					docs_.append(" - SIN DOCUMENTOS - ");

				} else {
					String objectName;
					for (DocumentoAsunto doc : documentos) {

						objectName = doc.getObjectName();
						objectName = CDATA + objectName.replaceAll(END_CDATA, "") + END_CDATA;

						docs_.append(objectName).append(LINE_RETURN);
					}
				}

				tmpStringFileContent = tmpStringFileContent.replace(//
						keysAcuse.getString("asunto.documentosAdjuntos"), //
						docs_.toString());
			}
			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				String tipo = "";

				if ("A".equalsIgnoreCase(asunto.getTipoAsunto().getValue()))
					tipo = "Asunto";
				else if ("E".equalsIgnoreCase(asunto.getTipoAsunto().getValue()))
					tipo = "Envio";
				else if ("T".equalsIgnoreCase(asunto.getTipoAsunto().getValue()))
					tipo = "Turno";
				else if ("C".equalsIgnoreCase(asunto.getTipoAsunto().getValue()))
					tipo = "Copia";

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.tipo"), //
						tipo);

			}
			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.status"), //
						asunto.getStatusAsunto().getDescripcion());
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				String mensajeRechazo;
				if (asunto.getStatusAsunto().getIdStatus() == Status.RECHAZADO) {
					mensajeRechazo = asunto.getComentarioRechazo();

				} else {
					mensajeRechazo = " ";
				}

				tmpStringFileContent = tmpStringFileContent.replace( //
						keysAcuse.getString("asunto.comentarioRechazo"), formatText(mensajeRechazo));
			}
			// - - - - - - - - - - - - Tag usuario que acepta el tramite - - - - - - - - - -
			// - -
			{
				String usuarioAceptoTramite = "";
				CustomAsunto custom = mngrCustomAsunto.fetch(Integer.valueOf((String) idAsunto));
				if (custom != null && StringUtils.isNotEmpty(custom.getCustom2())) {
					Representante item = mngrRepresentante.fetch((String) custom.getCustom2());
					usuarioAceptoTramite = (item != null && StringUtils.isNotEmpty(item.getNombreCompleto()))
							? item.getNombreCompleto()
							: "";
				}
				tmpStringFileContent = tmpStringFileContent.replace(
						keysAcuse.getString("asunto.usuario.acepto.tramite"), formatText(usuarioAceptoTramite));
			}

			// + + + + + + + + + + + + + + + + + + + + + + + + +
			// + + + + + + + + + + + + + + + + + + + + + + + + +
			
			File acuse = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX + "ACUSE_ASUNTO_" + idAsunto, ".doc");

			acuse.deleteOnExit();
			try{
				FileUtils.writeStringToFile(acuse, tmpStringFileContent, "UTF-8");

				log.info("FILE ACUSE :: " + acuse.getCanonicalPath());

				// + + + + + + + + + + + + + + + + + + + + + + + + +
				// + + + + + + + + + + + + + + + + + + + + + + + + +

				File convert = pdfConverterService.convert(acuse);
				
				return convert;
			} catch (Exception e){			
				throw e;
			} finally {
				if (acuse != null && acuse.exists())
					acuse.delete();
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Genera el documento de acuse de un asunto indicado por ID.
	 * 
	 * @param idRespuesta
	 * @param acuse
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private File getAcuseRespuestaFile(Serializable idRespuesta) throws Exception {
		try {

			Respuesta respuesta = mngrRespuesta.fetch(Integer.valueOf((String) idRespuesta));

			if (respuesta == null) {
				throw new BadRequestException();
			}

			Asunto asunto = mngrAsunto.fetch(respuesta.getIdAsunto());
			
			//
			String tmpStringFileContent = new String(templateAcuseRespuesta.getBytes());

			{
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.noOficio"),
						asunto.getAsuntoDetalle().getNumDocto());
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.descripcion"),
						formatText(asunto.getAsuntoDetalle().getAsuntoDescripcion()));
			}

			if (asunto.getIdSubTipoAsunto().equals(SubTipoAsunto.D.toString())) {
				String ciudadano = mngrCiudadano.fetch(Integer.parseInt(asunto.getDestinatario())).getNombreCompleto();
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("Ciudadano"),
						ciudadano != null ? ciudadano : "");
			} else {
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("Ciudadano"), "N/A");
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			{
				tmpStringFileContent = tmpStringFileContent.replace(
						keysAcuse.getString("respuesta.areaRemitenteOrigen.descripcion"),
						escapeXml( (asunto.getAsuntoDetalle().getRemitente() != null) ? 
									asunto.getAsuntoDetalle().getRemitente().getDescripcion() :
									""
						));
				
				try {
					Asunto asuntoConsulta = mngrAsunto.fetch(asunto.getIdAsuntoOrigen());
				    tmpStringFileContent = tmpStringFileContent.replace(
	                        keysAcuse.getString("respuesta.areaOrigenRegistro.descripcion"),
	                        escapeXml( asuntoConsulta.getArea().getDescripcion() )
	                );
                } catch (Exception e) {
                    log.error(":: Error al intentar agregar el dato para el area del asunto original");
                    tmpStringFileContent = tmpStringFileContent.replace(
                            keysAcuse.getString("respuesta.areaRemitenteOriginal.descripcion"),
                            escapeXml("")
                    );
                }

				tmpStringFileContent = tmpStringFileContent.replace(
						keysAcuse.getString("respuesta.areaRemitente.descripcion"),
						escapeXml(respuesta.getAreaDestino().getDescripcion()));

				tmpStringFileContent = tmpStringFileContent.replace(
						keysAcuse.getString("respuesta.areaDestinatario.descripcion"),
						escapeXml(respuesta.getArea().getDescripcion()));
			}

			{
				// - - - - - - - - - - - - - - - - - - - - - - - -
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.folioRespuesta"),
						StringUtils.isBlank(respuesta.getFolioRespuesta()) ? "S/N" : formatText(respuesta.getFolioRespuesta()));
			}

			{
				// - - - - - - - - - - - - - - - - - - - - - - - -
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.tipo"), //
						"C".equalsIgnoreCase(respuesta.getTipoRespuesta().getIdTipoRespuesta()) ? "Concluida"
								: "Avance");

			}

			{
				// - - - - - - - - - - - - - - - - - - - - - - - -
				tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.comentario"),
						formatText(respuesta.getComentario()));
			}

			// - - - - - - - - - - - - - - - - - - - - - - - -
			String fechaAcuse = "";
			TimeStampToken tst;
			for (Timestamp ts : respuesta.getTimestamps())
				if (ts.getTipo() == TipoTimestamp.TIMESTAMP_ENVIO) {

					tst = SignatureUtil.getTimeStampToken(ts.getTimestamp());

					fechaAcuse = tst.getTimeStampInfo().getGenTime().toString() + LINE_RETURN + "TSA: "
							+ tst.getTimeStampInfo().getTsa() + LINE_RETURN + "Serial number: "
							+ tst.getTimeStampInfo().getSerialNumber() + LINE_RETURN + "Encoded: "
							+ DatatypeConverter.printBase64Binary(tst.getEncoded());

					break;
				}

			tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.fechaAcuse"),
					fechaAcuse);

			// - - - - - - - - - - - - - - - - - - - - - - - -
			String fechaRecepcion = "";

			for (Timestamp ts : respuesta.getTimestamps())
				if (ts.getTipo() == TipoTimestamp.TIMESTAMP_ACUSE_RECEPCION_RECHAZO) {

					tst = SignatureUtil.getTimeStampToken(ts.getTimestamp());

					fechaRecepcion = tst.getTimeStampInfo().getGenTime().toString() + LINE_RETURN + "TSA: "
							+ tst.getTimeStampInfo().getTsa() + LINE_RETURN + "Serial number: "
							+ tst.getTimeStampInfo().getSerialNumber() + LINE_RETURN + "Encoded: "
							+ DatatypeConverter.printBase64Binary(tst.getEncoded());

					break;
				}
			tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.fechaRecepcion"),
					fechaRecepcion);

			// - - - - - - - - - - - - - - - - - - - - - - - -
			tmpStringFileContent = tmpStringFileContent.replace(
					keysAcuse.getString("respuesta.instruccion.descripcion"),
					asunto.getInstruccion() != null ? asunto.getInstruccion().getDescripcion() : "");

			// - - - - - - - - - - - - - - - - - - - - - - - -

			String fechaAtencion = "";
			for (Timestamp ts : respuesta.getTimestamps())
				if (ts.getTipo().getTipo() == TipoTimestamp.TIMESTAMP_ATENCION.getTipo()) {

					tst = SignatureUtil.getTimeStampToken(ts.getTimestamp());

					fechaAtencion = tst.getTimeStampInfo().getGenTime().toString() + LINE_RETURN + "TSA: "
							+ tst.getTimeStampInfo().getTsa() + LINE_RETURN + "Serial number: "
							+ tst.getTimeStampInfo().getSerialNumber() + LINE_RETURN + "Encoded: "
							+ DatatypeConverter.printBase64Binary(tst.getEncoded());

					break;
				}

			tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.fechaAtencion"),
					fechaAtencion);
			// - - - - - - - - - - - - - - - - - - - - - - - -

			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("idRespuesta", Integer.valueOf(idRespuesta.toString())));

			List<DocumentoRespuesta> documentos = (List<DocumentoRespuesta>) mngrDocsRespuesta.search(restrictions);

			StringBuffer sbf = new StringBuffer();

			if (documentos != null && documentos.isEmpty()) {

				sbf.append("- SIN DOCUMENTOS -");

			} else {
				String objectName;
				for (DocumentoRespuesta doc : documentos) {

					objectName = doc.getObjectName();
					objectName = CDATA + objectName.replaceAll(END_CDATA, "") + END_CDATA;

					sbf.append(objectName).append(LINE_RETURN);
				}
			}

			tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.documentosAdjuntos"),
					sbf.toString());

			// - - - - - - - - - - - - - - - - - - - - - - - -
			tmpStringFileContent = tmpStringFileContent.replace(keysAcuse.getString("respuesta.tipo"), "Respuesta");

			// + + + + + + + + + + + + + + + + + + + + + + + + +
			// + + + + + + + + + + + + + + + + + + + + + + + + +
			File acuse = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX + "ACUSE_RESPUESTA_" + idRespuesta,
					".doc");

			acuse.deleteOnExit();
			try {
				FileUtils.writeStringToFile(acuse, tmpStringFileContent, StandardCharsets.UTF_8);

				log.info("FILE ACUSE :: " + acuse.getCanonicalPath());

				// + + + + + + + + + + + + + + + + + + + + + + + + +
				// + + + + + + + + + + + + + + + + + + + + + + + + +

				File convert = pdfConverterService.convert(acuse);

				return convert;
			} catch (Exception e){
				throw e;
			} finally {
				if (acuse != null && acuse.exists())
					acuse.delete();
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	private String formatText(String text) {
		String string = CDATA + text.replaceAll("[^\\x20-\\xff]", "").replaceAll(END_CDATA, "") + END_CDATA;
		return string;
	}

	/**
	 * Escapes the characters in a string using XML entities.
	 * 
	 * @param str
	 * @return
	 */
	private String escapeXml(String str) {
		return StringUtils.isNotBlank(str) ? StringEscapeUtils.escapeXml(str) : "";
	}

}
