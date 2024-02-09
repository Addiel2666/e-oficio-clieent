/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.ecm.sigap.data.model.util.SubTipoAsunto;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
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
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.Firmante;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.Antecedente;
import com.ecm.sigap.data.model.util.AsuntoCiudadano;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.FechaUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controlador para generar el volante de un asunto.
 *
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class VolanteController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(VolanteController.class);

	/** Key para reemplazo en las plantillas */
	private static final ResourceBundle volanteKeys = ResourceBundle
			.getBundle("volanteKeys");

	/** */
	@Autowired(required = true)
	@Qualifier("templateVolante")
	private String templateVolante;

	/**
	 *
	 * @param idAsunto
	 * @return
	 * @throws IOException
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene volante asunto", notes = "Obtiene el volante de un tramite y lo descarga")
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
	@RequestMapping(value = "/volante/asunto", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> getVolante(
			@RequestParam(value = "id", required = true) Serializable idAsunto)
			throws IOException {		
		File volanteExportado = null;
		
		try {

			Map<String, String> item = new HashMap<>();
			Locale esLocale = new Locale("es", "ES");

			Asunto asunto = mngrAsunto.fetch(Integer.valueOf(idAsunto
					.toString()));
			Asunto asuntoPadre;
			if (asunto.getIdAsuntoPadre() == asunto.getIdAsunto())
				asuntoPadre = asunto;
			else
				asuntoPadre = mngrAsunto.fetch(asunto.getIdAsuntoPadre());

			String volanteTemplateString = new String(
					templateVolante.getBytes());

			// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
			{
				SimpleDateFormat sdf = new SimpleDateFormat(
						volanteKeys.getString("dateFormat"), esLocale);
				volanteTemplateString = volanteTemplateString
						.replace(
								volanteKeys.getString("fechaRecepcion"),
								asunto.getAsuntoDetalle().getFechaRecepcion() != null ? sdf
										.format(asunto.getAsuntoDetalle()
												.getFechaRecepcion()) : "-");
			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
			{
				volanteTemplateString = volanteTemplateString
						.replace(
								volanteKeys.getString("fechaElaboracion"),
								asunto.getAsuntoDetalle().getFechaElaboracion() != null ? FechaUtil
										.getDateFormat(asunto.getAsuntoDetalle()
												.getFechaElaboracion(),environment) : "-");

			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			// + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
			{
				SimpleDateFormat sdf = new SimpleDateFormat(
						volanteKeys.getString("dateFormat"), esLocale);
				volanteTemplateString = volanteTemplateString
						.replace(
								volanteKeys.getString("fechaEnvio"),
								asunto.getAsuntoDetalle().getFechaElaboracion() != null ? sdf
										.format(asunto.getFechaEnvio()) : "-");
			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("area"), asunto.getArea() != null ? asunto
					.getArea().getDescripcion() : "");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("comentario"),
					asunto.getComentario() != null ? asunto.getComentario()
							: "");
			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("temas"),
					asuntoPadre.getTema() != null ? asuntoPadre.getTema()
							.getDescripcion() : "");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			volanteTemplateString = volanteTemplateString.replace(//
					volanteKeys.getString("folioIntermmedio"), //
					StringUtils.isNotEmpty(asunto.getAsuntoDetalle()
							.getFolioIntermedio()) ? //
					asunto.getAsuntoDetalle().getFolioIntermedio()
							: "");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("folioPadre"),
					asuntoPadre.getFolioArea() != null ? asuntoPadre
							.getFolioArea() : "");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString
					.replace(
							volanteKeys.getString("numDocto"),
							CDATA + asunto.getAsuntoDetalle().getNumDocto() != null ? asunto
									.getAsuntoDetalle().getNumDocto() : "-"
									.replaceAll(END_CDATA, "") + END_CDATA);

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("promotor"), asunto
							.getAsuntoDetalle().getPromotor().getDescripcion());

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			Firmante firmante = asunto.getAsuntoDetalle().getFirmante();

			if (firmante != null) {
				volanteTemplateString = volanteTemplateString
						.replace(
								volanteKeys.getString("firmante"),
								firmante.getNombres()
										+ " "
										+ firmante.getPaterno()
										+ (firmante.getMaterno() != null ? (" " + firmante
												.getMaterno()) : ""));

				if (StringUtils.isNotBlank(asunto.getAsuntoDetalle()
						.getFirmanteCargo())) {
					volanteTemplateString = volanteTemplateString.replace(
							volanteKeys.getString("firmanteCargo"), asunto
									.getAsuntoDetalle().getFirmanteCargo());
				}
			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			StringBuilder ciudadanos_ = new StringBuilder();
			for (AsuntoCiudadano ciuda : asunto.getAsuntoDetalle()
					.getCiudadanos()) {
				ciudadanos_.append(ciuda.getCiudadano().getNombres())
						.append(" ").append(ciuda.getCiudadano().getPaterno())
						.append(" ").append(ciuda.getCiudadano().getMaterno())
						.append("\n");
			}

			volanteTemplateString = volanteTemplateString
					.replace(volanteKeys.getString("ciudadanos"),
							ciudadanos_.toString());

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			if (asunto.getIdSubTipoAsunto().equals(SubTipoAsunto.D.toString())) {
				String ciudadano = mngrCiudadano.fetch(
						Integer.parseInt(asunto.getDestinatario()))
						.getNombreCompleto();
				volanteTemplateString = volanteTemplateString.replace(
						volanteKeys.getString("nombreCiudadano"),
						ciudadano != null ? ciudadano : "");
			} else {
				volanteTemplateString = volanteTemplateString.replace(
						volanteKeys.getString("nombreCiudadano"), "");
			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			List<Criterion> cricterios = new ArrayList<Criterion>();
			cricterios.add(Restrictions.eq("idAsuntoPadre",
					asunto.getIdAsuntoPadre()));
			List<?> listAsuntoInspect = mngrAsunto.search(cricterios);
			String AreasDestinataria = "";

			for (Asunto tramites : (List<Asunto>) listAsuntoInspect) {
				// SOLO TURNOS/ENVIOS
				if (tramites.getTipoAsunto().equals(TipoAsunto.TURNO)
						|| tramites.getTipoAsunto().equals(TipoAsunto.ENVIO)) {
					AreasDestinataria = AreasDestinataria
							+ " "
							+ "Área: "
							+ tramites.getAreaDestino().getDescripcion()
							+ " - "
							+ "Encargado: "
							+ tramites.getAreaDestino().getTitular()
									.getNombreCompleto() + ",";
				}

			}

			// Salto de linea en el volante
			AreasDestinataria = AreasDestinataria.replaceAll(",",
					"</w:t><w:br/><w:t>");

			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("hijosAreasDestinatario"),
					AreasDestinataria != null ? AreasDestinataria : "");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			Usuario dirigidoA = asuntoPadre.getAsuntoDetalle().getDirigidoA();

			String dirigidoNombre = "";
			String dirigidoCargo = "";

			if (dirigidoA != null) {
				dirigidoNombre = dirigidoA.getNombres() + " "
						+ dirigidoA.getApellidoPaterno() + " "
						+ dirigidoA.getMaterno();
				dirigidoCargo = dirigidoA.getCargo();
			}

			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("dirigido"), dirigidoNombre);

			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("dirigidoCargo"), dirigidoCargo);

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			{
				StringBuilder anexos_ = new StringBuilder();

				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions.eq("idAsunto",
						asunto.getIdAsunto()));

				List<DocumentoAsunto> doctos = (List<DocumentoAsunto>) mngrDocsAsunto
						.search(restrictions);

				for (DocumentoAsunto doct : doctos) {
					anexos_.append(doct.getObjectName()).append("\n");
				}

				volanteTemplateString = volanteTemplateString.replace(
						volanteKeys.getString("anexos"), anexos_.toString());
			}
			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("asuntoDescriocion"), CDATA
							+ asunto.getAsuntoDetalle().getAsuntoDescripcion()
									.replaceAll(END_CDATA, "") + END_CDATA);

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("areaDestino"), asunto.getAreaDestino()
					.getDescripcion());

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("instruccion"),
					asunto.getInstruccion() != null ? asunto.getInstruccion()
							.getDescripcion() : "N/A");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("prioridad"),
					asunto.getPrioridad() != null ? asunto.getPrioridad()
							.getDescripcion() : "N/A");

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			{
				SimpleDateFormat sdf = new SimpleDateFormat(
						volanteKeys.getString("dateFormat"), esLocale);
				volanteTemplateString = volanteTemplateString.replace(
						volanteKeys.getString("fechaCompromiso"),
						asunto.getFechaCompromiso() != null ? sdf.format(asunto
								.getFechaCompromiso()) : "-");
			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			{
				SimpleDateFormat sdf = new SimpleDateFormat(volanteKeys.getString("dateFormat"), esLocale);
				volanteTemplateString = volanteTemplateString.replace(volanteKeys.getString("fechaRegistro"),
						asunto.getFechaRegistro() != null ? sdf.format(asunto.getFechaRegistro()) : "-");
			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			{
				StringBuilder antecedentes_ = new StringBuilder();

				for (Antecedente antecedente : asuntoPadre.getAntecedentes()) {
					antecedentes_.append(antecedente.getIdAntecedentes())
							.append(", ");
				}

				volanteTemplateString = volanteTemplateString.replace(
						volanteKeys.getString("antecedentes"),
						antecedentes_.toString());
			}
			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			Representante titular = asunto.getArea().getTitular();

			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("titular"),
					titular.getNombres() + " " + titular.getPaterno() + " "
							+ titular.getMaterno());

			volanteTemplateString = volanteTemplateString.replace(
					volanteKeys.getString("titularCargo"), titular.getCargo());

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			{
				StringBuilder copias_ = new StringBuilder();

				List<Criterion> restrictions = new ArrayList<>();

				restrictions.add(Restrictions
						.eq("tipoAsunto", TipoAsunto.COPIA));

				restrictions.add(Restrictions.eq("idAsuntoPadre",
						asunto.getIdAsuntoPadre()));

				List<Asunto> copias = (List<Asunto>) mngrAsunto
						.search(restrictions);

				for (Asunto copia : copias) {
					if (copia.getStatusTurno().getIdStatus() != Status.POR_ENVIAR) {
						copias_.append("Área: ");
						copias_.append(copia.getAreaDestino().getDescripcion());
						copias_.append(" - ");
						copias_.append("Encargado: ");
						copias_.append(copia.getAreaDestino().getTitular()
								.getNombreCompleto());
						// Salto de linea en el volante
						copias_.append("</w:t><w:br/><w:t>");
					}
				}

				volanteTemplateString = volanteTemplateString.replace(
						volanteKeys.getString("copias"), copias_.toString());

			}

			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			
			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("idOrigen"), asunto.getIdAsuntoOrigen() != null ? asunto.getIdAsuntoOrigen().toString() : "");
			
			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
			
			volanteTemplateString = volanteTemplateString.replace(volanteKeys
					.getString("instruccionAdicional"), asunto.getComentario() != null ? asunto.getComentario() : "");
			
			// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

			volanteExportado = File.createTempFile(
					FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX + "VOLANTE_exported_",
					"volante.xml");

			volanteExportado.deleteOnExit();

			log.debug("Volante File creadted :: "
					+ volanteExportado.getCanonicalPath());

			FileUtils.writeStringToFile(volanteExportado,
					volanteTemplateString, "UTF-8");

			log.debug("File writen down! ");

			item.put("type", "text/xml");
			item.put("name", "volante.doc");
			item.put("contentB64", Base64.encodeBase64String(FileUtils
					.readFileToByteArray(volanteExportado)));

			return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);

		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
			
			throw e;
		} finally {
			if(null != volanteExportado && volanteExportado.exists())
				volanteExportado.delete();
		}

	}

}
