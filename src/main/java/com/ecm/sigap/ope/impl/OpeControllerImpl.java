/**

 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.impl.AreaController;
import com.ecm.sigap.data.controller.impl.AsuntoController;
import com.ecm.sigap.data.controller.impl.InstitucionController;
import com.ecm.sigap.data.controller.impl.UsuarioController;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.Institucion;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.ope.client.exception.AlreadyConfirmedException;
import com.ecm.sigap.ope.client.exception.AlreadyRegisteredException;
import com.ecm.sigap.ope.client.exception.BadStateException;
import com.ecm.sigap.ope.client.exception.HashInSignatureNoMatchException;
import com.ecm.sigap.ope.client.exception.UnknownURLException;
import com.ecm.sigap.ope.dao.model.EnvioTramites;
import com.ecm.sigap.ope.dao.model.Mensaje;
import com.ecm.sigap.ope.dao.model.RegistroInstancia;
import com.ecm.sigap.ope.dao.model.SincronizacionData;
import com.ecm.sigap.ope.model.AreaInteropera;
import com.ecm.sigap.ope.model.OpeHeaders;
import com.ecm.sigap.ope.model.RequestConfirmarSubscripcion;
import com.ecm.sigap.ope.model.RequestRecibirSubscripcion;
import com.ecm.sigap.ope.model.RequestVersionCatalogo;
import com.ecm.sigap.ope.model.ResponseConfirmarSubscripcion;
import com.ecm.sigap.ope.model.ResponseRecibirSubscripcion;
import com.ecm.sigap.ope.model.ResponseSincronizacionCompleta;
import com.ecm.sigap.ope.model.ResponseVersionCatalogo;
import com.ecm.sigap.ope.model.StatusRegistro;
import com.ecm.sigap.ope.model.UsuarioAreaInteropera;

/**
 * Webservice con los enpoints para interoperar,
 * 
 * @author Alfredo Morales
 *
 */
@RestController
public final class OpeControllerImpl extends OpeControler {

	/** */
	@Autowired
	private AsuntoController asuntoController;

	/** */
	@Autowired
	private AreaController areaController;
	/** */
	@Autowired
	private InstitucionController institucionController;
	/** */
	@Autowired
	private UsuarioController usuarioController;


	/**
	 * Recibir solicitud para interoperar, se crea el registro y se retornan los
	 * datos para registro en la instancia solicitante,
	 * 
	 * @param body    cuerpo del mensaje,
	 * @param firma   xml con el hash del mensaje firmado,
	 * @param uuid    identificador del registro en la tabla de log de mensajes,
	 * @param request {@link HttpServletRequest}
	 * @return Body con datos para registro,
	 * @throws Exception
	 */
	@RequestMapping(value = "/ope/recibirSubscripcion", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody ResponseEntity<ResponseRecibirSubscripcion> recibirSubscripcion(
			@RequestBody(required = true) RequestRecibirSubscripcion body, //
			@RequestHeader(required = true, value = OpeHeaders.HEADER_FIRMA_BODY) String firma, //
			@RequestAttribute(required = true, value = OpeHeaders.UUID) String uuid //
	) throws Exception {

		try {

			validateRequestUrl(new JSONObject(body), true);
			validateHash(new JSONObject(body), firma);
			logMessageBody(uuid, new JSONObject(body).toString());

			ResponseRecibirSubscripcion response = new ResponseRecibirSubscripcion();

			// REGISTRAR SOLICITUD.
			RegistroInstancia item = new RegistroInstancia();

			Date now = getNow(body.getNombreCorto());

			item.setUrl(body.getUrl());
			item.setDescripcion(body.getNombre());
			item.setAlias(body.getNombreCorto());
			item.setFechaRegistro(now);
			item.setMensaje(new Mensaje());
			item.getMensaje().setId(uuid);
			item.setStatus(StatusRegistro.REGISTRADO);
			item = repoRegistroInstancia.save(item);
			// - - -

			response.setIdRegistro(item.getId());
			response.setNombre(repoOPEConfig.fetch("ope-nombre").getValue());
			response.setNombreCorto(repoOPEConfig.fetch("ope-nombre-corto").getValue());
			response.setUrl(repoOPEConfig.fetch("ope-url").getValue());

			// - - -
			MultiValueMap<String, String> headers = createResponseHeaders(response); // headers Identificadores

			logResponse(uuid, new JSONObject(response).toString(), headers.getFirst(OpeHeaders.HEADER_FIRMA_BODY));

			return new ResponseEntity<ResponseRecibirSubscripcion>(response, headers, HttpStatus.OK);

		} catch (HashInSignatureNoMatchException e) {
			return new ResponseEntity<ResponseRecibirSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		} catch (UnknownURLException e) {
			return new ResponseEntity<ResponseRecibirSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (AlreadyRegisteredException e) {
			return new ResponseEntity<ResponseRecibirSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.ALREADY_REPORTED);
		} catch (Exception e) {
			return new ResponseEntity<ResponseRecibirSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ope/confirmarSubscripcion", method = RequestMethod.POST, produces = {
			"application/json" })
	public @ResponseBody ResponseEntity<ResponseConfirmarSubscripcion> confirmarSubscripcion(
			@RequestBody(required = true) RequestConfirmarSubscripcion body, //
			@RequestHeader(required = true, value = OpeHeaders.HEADER_FIRMA_BODY) String firma, //
			@RequestAttribute(required = true, value = OpeHeaders.UUID) String uuid //
	) throws Exception {

		try {

			validateRequestUrl(new JSONObject(body), false);
			validateHash(new JSONObject(body), firma);
			logMessageBody(uuid, new JSONObject(body).toString());

			ResponseConfirmarSubscripcion response = new ResponseConfirmarSubscripcion();
			// - - -
			RegistroInstancia searchObject = new RegistroInstancia();

			searchObject.setAlias(body.getNombreCorto());
			// searchObject.setStatus(StatusRegistro.REGISTRADO);

			searchObject = repoRegistroInstancia.searchSingle(searchObject);

			if (searchObject == null)
				throw new UnknownURLException();
			else if (searchObject.getStatus() == StatusRegistro.CONFIRMADO)
				throw new AlreadyConfirmedException();
			else if (searchObject.getStatus() != StatusRegistro.REGISTRADO)
				throw new BadStateException();

			searchObject.setStatus(StatusRegistro.CONFIRMADO);

			repoRegistroInstancia.update(searchObject);

			// - - -
			MultiValueMap<String, String> headers = createResponseHeaders(response);

			logResponse(uuid, new JSONObject(response).toString(), headers.getFirst(OpeHeaders.HEADER_FIRMA_BODY));

			return new ResponseEntity<ResponseConfirmarSubscripcion>(response, headers, HttpStatus.OK);

		} catch (HashInSignatureNoMatchException e) {
			return new ResponseEntity<ResponseConfirmarSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		} catch (UnknownURLException e) {
			return new ResponseEntity<ResponseConfirmarSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (AlreadyConfirmedException e) {
			return new ResponseEntity<ResponseConfirmarSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.ALREADY_REPORTED);
		} catch (BadStateException e) {
			return new ResponseEntity<ResponseConfirmarSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<ResponseConfirmarSubscripcion>(createResponseHeaders(new JSONObject()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ope/obtenerVersionCatalogo", method = RequestMethod.POST, produces = {
			"application/json" })
	public @ResponseBody ResponseEntity<ResponseVersionCatalogo> obtenerVersionCatalogo(
			@RequestBody(required = true) RequestVersionCatalogo body, //
			@RequestHeader(required = true, value = OpeHeaders.HEADER_FIRMA_BODY) String firma, //
			@RequestAttribute(required = true, value = OpeHeaders.UUID) String uuid //
	) throws Exception {

		try {

			validateRequestUrl(new JSONObject(body), false);
			validateHash(new JSONObject(body), firma);
			logMessageBody(uuid, new JSONObject(body).toString());

			ResponseVersionCatalogo response = new ResponseVersionCatalogo();

			// - - -
			response.setVersionCatalogo(repoOPEConfig.fetch("ope-version-catalogo").getValue());
			// - - -

			MultiValueMap<String, String> headers = createResponseHeaders(response);

			logResponse(uuid, new JSONObject(response).toString(), headers.getFirst(OpeHeaders.HEADER_FIRMA_BODY));

			return new ResponseEntity<ResponseVersionCatalogo>(response, headers, HttpStatus.OK);

		} catch (HashInSignatureNoMatchException e) {
			return new ResponseEntity<ResponseVersionCatalogo>(createResponseHeaders(new JSONObject()),
					HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		} catch (UnknownURLException e) {
			return new ResponseEntity<ResponseVersionCatalogo>(createResponseHeaders(new JSONObject()),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			return new ResponseEntity<ResponseVersionCatalogo>(createResponseHeaders(new JSONObject()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	@RequestMapping(value = "/ope/envioTramite", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Asunto> envioTramite(@RequestBody(required=true) EnvioTramites body,
			@RequestHeader(required = true, value = OpeHeaders.HEADER_FIRMA_BODY) String firma, //
			@RequestAttribute(required = true, value = OpeHeaders.UUID) String uuid) //
			throws Exception {
		
			try {
				validateRequestUrl(new JSONObject(body), false);
				validateHash(new JSONObject(body), firma);
				logMessageBody(uuid, new JSONObject(body).toString());
				
				Asunto asunto = new Asunto();
				asunto.setPrioridad(body.getPrioridad());
				asunto.setArea(body.getArea());
				asunto.setIdAsuntoPadre(body.getIdAsuntoPadre());
				asunto.setInstruccion(body.getInstruccion());
				asunto.setAsuntoDetalle(body.getAsuntoDetalle());
				asunto.setTipoAsunto(body.getTipoAsunto());//fijo
				asunto.setIdSubTipoAsunto(body.getIdSubTipoAsunto());//fijo del anterior
				asunto.setFechaRegistro(body.getFechaRegistro());
				asunto.setFechaCompromiso(body.getFechaCompromiso());
				asunto.setFechaEnvio(body.getFechaEnvio());//
				asunto.setFechaAcuse(body.getFechaAcuse());//se saca del timestamp
				asunto.setFolioArea(body.getFolioArea());
				asunto.setEnTiempo(body.getEnTiempo());
				asunto.setTurnador(body.getTurnador());
				asunto.setDestinatario(body.getDestinatario());
				asunto.setStatusAsunto(body.getStatusAsunto());
				asunto.setAreaDestino(body.getAreaDestino());
				asunto.setEspecial(body.getEspecial());
				asunto.setComentario(body.getComentario());
				asunto.setComentarioRechazo(body.getComentarioRechazo());
				asunto.setAtributo(body.getAtributo());
				asunto.setAnotacion(body.getAnotacion());
				asunto.setAntecedentes(body.getAntecedentes());
				asunto.setIdTipoRegistro(body.getIdTipoRegistro());
				asunto.setIdTipoRegistro(body.getIdTipoRegistro());
				asunto.setResponsable(body.getResponsable());
				asunto.setContentId(body.getContentId());
				asunto.setTipoExpediente(body.getTipoExpediente());
				asunto.setStatusTurno(body.getStatusTurno());
				asunto.setAsignadoA(body.getAsignadoA());
				asunto.setTipoDocumento(body.getTipoDocumento());
				asunto.setIdArea(body.getIdArea());
				asunto.setExpediente(body.getExpediente());
				asunto.setTema(body.getTema());
				asunto.setEvento(body.getEvento());
				asunto.setFechaEvento(body.getFechaEvento());
				asunto.setSubTema(body.getSubTema());
				asunto.setIdAsuntoOrigen(body.getIdAsuntoOrigen());
				asunto.setDocumentosAdjuntos(body.getDocumentosAdjuntos());
				asunto.setDocumentosPublicados(body.getDocumentosPublicados());
				asunto.setAsuntoPadre(body.getAsuntoPadre());
				
				MultiValueMap<String, String> headers = createResponseHeaders(asunto);

				logResponse(uuid, new JSONObject(asunto).toString(), headers.getFirst(OpeHeaders.HEADER_FIRMA_BODY));

				return new ResponseEntity<Asunto>(asunto,
						createResponseHeaders(new JSONObject(asunto)), HttpStatus.OK);
			} catch (HashInSignatureNoMatchException e) {
				return new ResponseEntity<Asunto>(createResponseHeaders(new JSONObject()),
						HttpStatus.NON_AUTHORITATIVE_INFORMATION);
			} catch (UnknownURLException e) {
				return new ResponseEntity<Asunto>(createResponseHeaders(new JSONObject()),
						HttpStatus.UNPROCESSABLE_ENTITY);
			} catch (AlreadyRegisteredException e) {
				return new ResponseEntity<Asunto>(createResponseHeaders(new JSONObject()),
						HttpStatus.ALREADY_REPORTED);
			} catch (Exception e) {
				return new ResponseEntity<Asunto>(createResponseHeaders(new JSONObject()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/ope/sincronizacion/completa", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ResponseSincronizacionCompleta> sincronizacionCompleta(
			@RequestBody(required = true) RequestRecibirSubscripcion body, //
			@RequestHeader(required = true, value = OpeHeaders.HEADER_FIRMA_BODY) String firma, //
			@RequestAttribute(required = true, value = OpeHeaders.UUID) String uuid) //
			throws Exception {
		try {
			validateRequestUrl(new JSONObject(body), true);
			validateHash(new JSONObject(body), firma);
			logMessageBody(uuid, new JSONObject(body).toString());

			ResponseSincronizacionCompleta response = new ResponseSincronizacionCompleta();

			// TODO Asignar valores del RequestBody al Objeto entidad
			// SINCRONIZACION
			SincronizacionData data = new SincronizacionData();
			Date now = getNow(body.getNombreCorto());

			data.setFechaRegistro(now);
			data.setMensaje(new Mensaje());
			data.getMensaje().setId(uuid);
			data = repoSincronizacionDirectorio.save(data);

			// - -
			Integer idInstitucion = Integer.parseInt(repoOPEConfig.fetch("ope-id-institucion").getValue());

			ResponseEntity<Institucion> responseInst = institucionController.get(idInstitucion);
			Institucion instituicion = responseInst.getBody();

			response.setInstitucion(instituicion.getDescripcion());

			Area areaBusqueda = new Area();
			areaBusqueda.setInstitucion(instituicion);
			areaBusqueda.setActivo(true);
			areaBusqueda.setInteropera(true);

			ResponseEntity<List<?>> responseAreas = areaController.search(areaBusqueda);
			List<Area> listAreas = (List<Area>) responseAreas.getBody();

			List<AreaInteropera> areasInteroperan = listAreas.stream().map(area -> {
				AreaInteropera areaInteroperante = new AreaInteropera();
				areaInteroperante.setIdArea(area.getIdArea());
				areaInteroperante.setTitular(area.getTitular().getNombreCompleto());
				areaInteroperante.setInstitucion(area.getInstitucion().getDescripcion());
				areaInteroperante.setAreaPadre(area.getAreaPadre().getDescripcion());
				areaInteroperante.setCargoTitular(area.getTitular().getId());
				areaInteroperante.setIdExterno(area.getIdExterno());
				
				// Obtener la lista de usuarios
				Usuario usuarioSearch = new Usuario();
				usuarioSearch.setActivo(true);
				usuarioSearch.setIdArea(area.getIdArea());

				ResponseEntity<List<?>> lst = usuarioController.search(usuarioSearch);
				List<Usuario> listUsuario = (List<Usuario>) lst.getBody();

				List<UsuarioAreaInteropera> usuarios = listUsuario.stream().map(usuario -> {
					UsuarioAreaInteropera usuarioObj = new UsuarioAreaInteropera();
					usuarioObj.setNombre(usuario.getNombreCompleto());
					usuarioObj.setTitulo(usuario.getRol().getDescripcion());
					usuarioObj.setPuesto(usuario.getCargo());
					usuarioObj.setCorreoElectronico(usuario.getEmail());
					return usuarioObj;
				}).collect(Collectors.toList());

				areaInteroperante.setUsuario(usuarios);

				return areaInteroperante;
			}).collect(Collectors.toList());
			// - - -
			// Asignacion de valores a los demas campos del Objeto Response
			response.setValue("Dato Dummy");
			response.setNombreCorto(repoOPEConfig.fetch("ope-nombre-corto").getValue());
			response.setAreasInteropera(areasInteroperan);
			response.setCargoTitular("Dato Dummy");
			response.setCorreoElectronico("Dato Dummy");
			response.setIdTitular("Dato Dummy");
			response.setInstitucion("Dato Dummy");
			response.setUrl(repoOPEConfig.fetch("ope-url").getValue());
			response.setVersionCatalogo("1");

			// - - -
			MultiValueMap<String, String> headers = createResponseHeaders(response);

			logResponse(uuid, new JSONObject(response).toString(), headers.getFirst(OpeHeaders.HEADER_FIRMA_BODY));

			return new ResponseEntity<ResponseSincronizacionCompleta>(response,
					createResponseHeaders(new JSONObject(response)), HttpStatus.OK);
		} catch (HashInSignatureNoMatchException e) {
			return new ResponseEntity<ResponseSincronizacionCompleta>(createResponseHeaders(new JSONObject()),
					HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		} catch (UnknownURLException e) {
			return new ResponseEntity<ResponseSincronizacionCompleta>(createResponseHeaders(new JSONObject()),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (AlreadyRegisteredException e) {
			return new ResponseEntity<ResponseSincronizacionCompleta>(createResponseHeaders(new JSONObject()),
					HttpStatus.ALREADY_REPORTED);
		} catch (Exception e) {
			return new ResponseEntity<ResponseSincronizacionCompleta>(createResponseHeaders(new JSONObject()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
