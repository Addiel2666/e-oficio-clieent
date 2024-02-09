/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.eCiudadano.controller;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.impl.RespuestaController;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.util.CopiaRespuesta;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.util.SignatureUtil;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.BadRequestException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
@RestController
@RequestMapping("/e-ciudadano/respuesta")
public class ECiudadanoRespuestaController extends CustomRestController {

	/** Referencia hacia el REST controller {@link RespuestaController}. */
	@Autowired
	private RespuestaController respuestaController;

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ECiudadanoRespuestaController.class);

	/**
	 * Gets the respuestas.
	 *
	 * @param idAsunto the id asunto
	 * @return the respuestas
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Respuesta>> getRespuestas(
			@RequestParam(value = "idAsunto", required = true) Integer idAsunto) throws Exception {

		Respuesta respuesta = new Respuesta();

		respuesta.setIdAsunto(idAsunto);

		ResponseEntity<List<?>> searchResponse = respuestaController.search(respuesta);

		List<Respuesta> respuestas = (List<Respuesta>) searchResponse.getBody();

		return new ResponseEntity<List<Respuesta>>(respuestas, HttpStatus.OK);

	}

	/**
	 * Save respuesta.
	 *
	 * @param respuesta the respuesta
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Respuesta> saveRespuesta(//
			@RequestBody Respuesta respuesta) throws Exception {

		// Respuesta nuevaRespuesta = (Respuesta) body.get("respuesta");

		// String certB64 = (String) body.get("certb64");
		// String certB64 = getHeader(HeaderValueNames.HEADER_LOGIN_CERT);
		try {
			// System.out.println(certB64);
			// String certMail = getCertificateMail(certB64);
			//
			// Usuario user = getUserByEmail(certMail);

			// String userId = getHeader(HeaderValueNames.HEADER_USER_ID);

			log.info("RESPUESTA A GUARDAR :: " + respuesta);

			Integer idAsunto = respuesta.getIdAsunto();

			if (respuesta.getIdRespuesta() == null) {

				// * * * * * * * * * * * * * * * * * * * * * * * *

				respuesta.setTipoRespuesta(mngrTipoRespuesta.fetch(respuesta.getTipoRespuesta().getIdTipoRespuesta()));

				log.debug("CONSULTANDO EL ASUNTO " + idAsunto);
				Asunto asunto_ = mngrAsunto.fetch(idAsunto);

				if (asunto_ == null) {
					throw new BadRequestException();
				}

				// * * * * * * * * * * * * * * * * * * * * * * * *
				log.debug("VALIDANDO PORCENTAJE");
				respuestaController.validaPorcentaje(respuesta);

				// * * * * * * * * * * * * * * * * * * * * * * * *

				if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty())
					for (CopiaRespuesta copia : respuesta.getCopias()) {
						copia.setStatus(mngrStatus.fetch(copia.getStatus().getIdStatus()));
					}

				// * * * * * * * * * * * * * * * * * * * * * * * *

				respuesta.setStatus(mngrStatus.fetch(respuesta.getStatus().getIdStatus()));

				respuesta.setAreaDestino(mngrArea.fetch(respuesta.getAreaDestino().getIdArea()));

				respuesta.setArea(mngrArea.fetch(respuesta.getArea().getIdArea()));

				respuesta.setStatus(mngrStatus.fetch(respuesta.getStatus().getIdStatus()));

				// * * * * * * * * * * * * * * * * * * * * * * * *

				List<Timestamp> timestamps = new ArrayList<>();

				Timestamp timeStamp = new Timestamp();
				timeStamp.setTipo(TipoTimestamp.TIMESTAMP_REGISTRO);

				String stampedData = respuestaController.getStampedData(respuesta, timeStamp.getTipo());

				Map<String, Object> time = firmaEndPoint.getTime(stampedData,
						TipoTimestamp.TIMESTAMP_REGISTRO.getTipoString());

				String timestamp = time.get("Tiempo").toString();

				timeStamp.setTimestamp(timestamp);

				timestamps.add(timeStamp);

				respuesta.setTimestamps(timestamps);

				// * * * * * * * * * * * * * * * * * * * * * * * *

				Date fechaEnvio = SignatureUtil.timestampToDate(timestamp);

				respuesta.setFechaRegistro(fechaEnvio);

				mngrRespuesta.save(respuesta);

			} else {

				Respuesta oldRespuesta = mngrRespuesta.fetch(respuesta.getIdRespuesta());

				if (oldRespuesta.getStatus().getIdStatus() != Status.POR_ENVIAR) {
					throw new BadRequestException();
				}

				if (!oldRespuesta.getTipoRespuesta().getTipoConcluida()
						&& oldRespuesta.getPorcentaje() != respuesta.getPorcentaje()
						&& respuesta.getPorcentaje() < respuestaController.getMaxPorcentaje(idAsunto, respuesta.getIdRespuesta(), false))
					throw new Exception("Porcentaje invalido.");

				if (respuesta.getCopias() != null && !respuesta.getCopias().isEmpty()) {

					for (CopiaRespuesta copiaRespuesta : respuesta.getCopias()) {
						copiaRespuesta.setIdAsunto(idAsunto);
					}

				}

				mngrRespuesta.update(respuesta);

			}

			return new ResponseEntity<Respuesta>(respuesta, HttpStatus.OK);

		} catch (

		Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 * 
	 * @param id
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteRespuesta(@RequestParam(value = "id", required = true) Serializable id) {

		respuestaController.delete(id);

	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/enviar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> enviar(@RequestParam(value = "id", required = true) Serializable id)
			throws Exception {

		return respuestaController.enviar(id);

	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/recuperar", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Respuesta> recuperarRespuesta(
			@RequestParam(value = "id", required = true) Serializable id) throws Exception {

		return respuestaController.recuperar(id);

	}

}
