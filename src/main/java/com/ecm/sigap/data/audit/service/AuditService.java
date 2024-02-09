/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.audit.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.audit.aspectj.Audit;
import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.impl.MailController;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.Auditoria;
import com.ecm.sigap.data.model.Bitacora;
import com.ecm.sigap.data.model.DocumentoAntefirma;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Institucion;
import com.ecm.sigap.data.model.Minutario;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.BitacoraTipoIdentificador;
import com.ecm.sigap.data.model.util.RevisorMinutario;
import com.ecm.sigap.data.model.util.StatusAsunto;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.model.util.StatusMinutario;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoAuditoria;
import com.ecm.sigap.data.model.util.TipoNotificacion;

/**
 * 
 * @author ECM Solutions.
 * @version 1.0
 *
 */
@Service
public class AuditService extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AuditService.class);

	/**
	 * Referencia hacia el REST controller de {@link MailController}.
	 */
	@Autowired
	private MailController mailController;

	/**
	 * Save auditoria. Construye el objeto auditoria y lo envia a guardar
	 *
	 * @param actionType  the action type
	 * @param entityAudit the entity audit
	 * @throws Exception the exception
	 */
	public void saveAuditoria(Audit tipoAuditoria, IAuditLog entityAudit) throws Exception {

		Auditoria auditoria = new Auditoria();

		String userId = null;
		Integer idArea = null;

		try {
			userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
		} catch (Exception e) {
//			
		}

		if (null == userId) {
			return;
		}
		
		boolean isUpdate = tipoAuditoria.actionType().equals(TipoAuditoria.UPDATE) ? true : false;

		auditoria.setFechaRegistro(new Date());
		auditoria.setAccion(tipoAuditoria.actionType().getValue());
		auditoria.setIdEntity(entityAudit.getId());
		auditoria.setNombreEntity(entityAudit.getClass().getSimpleName());
		auditoria.setInformacion(entityAudit.getLogDeatil());
		auditoria.setIdUsuario(userId);
		auditoria.setIdArea(idArea);
		if (entityAudit instanceof Asunto) {
			Asunto asunto = (Asunto) entityAudit;
			auditoria.setTipoEntity(asunto.getTipoAsunto().getValue());
			
			if(asunto.getArea() != null) {
				if(asunto.getArea().getInstitucion() != null)
					auditoria.setInstitucionId(asunto.getArea().getInstitucion().getIdInstitucion());
			}
			
			if(isUpdate)
				if(TipoAsunto.ASUNTO.getValue().equals(asunto.getTipoAsunto().getValue())){
					// Se agregan nuevas acciones para Asunto CANCEL/CONCLUDE
					if(StatusAsunto.CANCELADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
						auditoria.setAccion(TipoAuditoria.CANCEL.getValue());
					} else if(StatusAsunto.CONCLUIDO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
						auditoria.setAccion(TipoAuditoria.CONCLUDE.getValue());
					}
				} else {
					if(StatusAsunto.ENVIADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
						auditoria.setAccion(TipoAuditoria.SEND.getValue());
					} else if(
							StatusAsunto.PROCESO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus())) || 
							(
								StatusAsunto.CONCLUIDO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus())) && 
								(!asunto.getInstruccion().getRequiereRespuesta()) || 
								TipoAsunto.COPIA.getValue().equals(asunto.getTipoAsunto().getValue())
							)
					) {
						if(idArea.intValue() != asunto.getAsuntoDetalle().getIdRemitente().intValue())
							auditoria.setAccion(TipoAuditoria.ACCEPT.getValue());
						else
							return;
					} else if(StatusAsunto.RECHAZADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
						auditoria.setAccion(TipoAuditoria.REJECT.getValue());
					} else if(StatusAsunto.CONCLUIDO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))) {
						auditoria.setAccion(TipoAuditoria.CONCLUDE.getValue());
					}
				}
		} else if (entityAudit instanceof Respuesta) {
			
			Respuesta resp = (Respuesta) entityAudit;
			if(resp.getArea() != null) {
				if(resp.getArea().getInstitucion() != null)
					auditoria.setInstitucionId(resp.getArea().getInstitucion().getIdInstitucion());
			}
			
			// Se agregan nuevas acciones para Respuesta SEND/REJECT/CONCLUDE
			if(isUpdate)
				if(Status.ENVIADO == resp.getStatus().getIdStatus())
					auditoria.setAccion(TipoAuditoria.SEND.getValue());
				else if(Status.RECHAZADO == resp.getStatus().getIdStatus())
					auditoria.setAccion(TipoAuditoria.REJECT.getValue());
				else if(Status.CONCLUIDO == resp.getStatus().getIdStatus())
				auditoria.setAccion(TipoAuditoria.CONCLUDE.getValue());
			
		} else if (entityAudit instanceof Institucion) {
			
			Institucion institucion = (Institucion) entityAudit;
			
			if(institucion.getIdInstitucion() != null)
				auditoria.setInstitucionId(institucion.getIdInstitucion());
			
			if(isUpdate && institucion.getActiveInactive() != null) {
				if(institucion.getActiveInactive().equals("true"))
					auditoria.setAccion(TipoAuditoria.ACTIVE.getValue());
				if(institucion.getActiveInactive().equals("false"))
					auditoria.setAccion(TipoAuditoria.INACTIVE.getValue());
			}
			
		} else if (entityAudit instanceof Area) {
			
			Area area = (Area) entityAudit;
			if(area.getInstitucion() != null)
				auditoria.setInstitucionId(area.getInstitucion().getIdInstitucion());
			
			if(isUpdate && area.getActiveInactive() != null) {
				if(area.getActiveInactive().equals("true"))
					auditoria.setAccion(TipoAuditoria.ACTIVE.getValue());
				if(area.getActiveInactive().equals("false"))
					auditoria.setAccion(TipoAuditoria.INACTIVE.getValue());
			}
			
		} else if (entityAudit instanceof Usuario) {
			Usuario user = (Usuario) entityAudit;
			
			if(user.getAreaAux() != null) {
				if(user.getAreaAux().getInstitucion() != null)
					auditoria.setInstitucionId(user.getAreaAux().getInstitucion().getIdInstitucion());
			}
			// if(isUpdate && !user.getActivo())
			//	auditoria.setAccion(TipoAuditoria.INACTIVE.getValue());
		} else if (entityAudit instanceof DocumentoAsunto) {
			DocumentoAsunto documentoAsunto = (DocumentoAsunto) entityAudit;
			
			if(documentoAsunto.getIdOrigen() != null)
				auditoria.setOrigenId(documentoAsunto.getIdOrigen());
			
			if(documentoAsunto.getAsuntoConsulta() != null) {
				if(documentoAsunto.getAsuntoConsulta().getIdPromotor() != null)
					auditoria.setInstitucionId(documentoAsunto.getAsuntoConsulta().getIdPromotor());
				
				if(documentoAsunto.getAsuntoConsulta().getIdAsuntoOrigen() != null)
					auditoria.setOrigenId(documentoAsunto.getAsuntoConsulta().getIdAsuntoOrigen());
			}
			
			if (TipoAuditoria.UPDATEBITACORA.equals(tipoAuditoria.actionType())) {

				if (StatusFirmaDocumento.BLOQUEADO.equals(documentoAsunto.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCLOCK.getValue());

				if (StatusFirmaDocumento.AUX_QUITA_BLOQUEO.equals(documentoAsunto.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCUUNLOCK.getValue());

				if (StatusFirmaDocumento.FIRMADO.equals(documentoAsunto.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCSIGNED.getValue());

				if (StatusFirmaDocumento.PARA_FIRMA.equals(documentoAsunto.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCMARKEDF.getValue());

			} else if (TipoAuditoria.DELETE.equals(tipoAuditoria.actionType()) && documentoAsunto.isVersionable()) {
				auditoria.setAccion(TipoAuditoria.DELETEVERSION.getValue());
			} else if (TipoAuditoria.SAVE.equals(tipoAuditoria.actionType()) && documentoAsunto.isVersionable()) {
				auditoria.setAccion(TipoAuditoria.VERSION.getValue());
			} else {
				if (isUpdate) {
					if (StatusFirmaDocumento.ENVIO_ANTEFIRMA.equals(documentoAsunto.getStatus()))
						auditoria.setAccion(TipoAuditoria.DOCSENDF.getValue());

					if (StatusFirmaDocumento.PARA_FIRMA.equals(documentoAsunto.getStatus()))
						auditoria.setAccion(TipoAuditoria.DOCMARKEDF.getValue());
				}
			}
			
		} else if (entityAudit instanceof DocumentoRespuesta) {
			 DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entityAudit;
				
			if(documentoRespuesta.getAsuntoConsulta() != null) {
				if(documentoRespuesta.getAsuntoConsulta().getIdPromotor() != null)
					auditoria.setInstitucionId(documentoRespuesta.getAsuntoConsulta().getIdPromotor());
				
				if(documentoRespuesta.getAsuntoConsulta().getIdAsuntoOrigen() != null)
					auditoria.setOrigenId(documentoRespuesta.getAsuntoConsulta().getIdAsuntoOrigen());
			}
			
			if(TipoAuditoria.UPDATEBITACORA.equals(tipoAuditoria.actionType())) {
				
				if(StatusFirmaDocumento.BLOQUEADO.equals(documentoRespuesta.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCLOCK.getValue());
				
				if(StatusFirmaDocumento.AUX_QUITA_BLOQUEO.equals(documentoRespuesta.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCUUNLOCK.getValue());
				
				if(StatusFirmaDocumento.FIRMADO.equals(documentoRespuesta.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCSIGNED.getValue());
				
				if(StatusFirmaDocumento.PARA_FIRMA.equals(documentoRespuesta.getStatus()))
					auditoria.setAccion(TipoAuditoria.DOCMARKEDF.getValue());
				
			} else {
				if(isUpdate) {
					if(StatusFirmaDocumento.ENVIO_ANTEFIRMA.equals(documentoRespuesta.getStatus()))
						auditoria.setAccion(TipoAuditoria.DOCSENDF.getValue());
					
					if(StatusFirmaDocumento.PARA_FIRMA.equals(documentoRespuesta.getStatus()))
						auditoria.setAccion(TipoAuditoria.DOCMARKEDF.getValue());
				}
			}
		} else if (entityAudit instanceof Acceso) {
			
			auditoria.setNombreEntity("Usuario");
			
			if(tipoAuditoria.actionType().equals(TipoAuditoria.SAVE))
				auditoria.setAccion(TipoAuditoria.ACCESSA.getValue());
			
			if(tipoAuditoria.actionType().equals(TipoAuditoria.DELETE))
				auditoria.setAccion(TipoAuditoria.ACCESSD.getValue());
		}
		
		try {
			InetAddress info = InetAddress.getLocalHost();
			auditoria.setIp(info.getHostAddress());
	        //String localIp = ip.getHostAddress();
			auditoria.setNombreEquipo(info.getHostName());
	        //String hostname = info.getHostName();
		} catch (UnknownHostException e) {
			// TODO: handle exception
			log.debug("Error: " + e);
		}

		log.debug("AUDITORIA A GUARDAR >> " + auditoria);
		mngrAuditoria.save(auditoria);

		saveBitacora(tipoAuditoria, entityAudit);
		log.info("AUDITORIA GUARDADA >> ");
	}

	/**
	 * Save bitacota. Construye el objeto bitacota y lo envia a guardar (asuntos, respuestas y tramites del sistema)
	 * 
	 * @param tipoAuditoria
	 * @param entityAudit
	 * @throws Exception
	 */
	private void saveBitacora(Audit tipoAuditoria, IAuditLog entityAudit) throws Exception {

		if (entityAudit != null) {
			String userId = getHeader(HeaderValueNames.HEADER_USER_ID);
			Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

			if (null == userId)
				userId = "ASANCHEZ";
			
			Bitacora bitacora = new Bitacora();
			Usuario usuario = mngrUsuario.fetch(userId);
			boolean isUpdate = tipoAuditoria.actionType().equals(TipoAuditoria.UPDATE) ? true : false;

			bitacora.setTipoIdentificador(BitacoraTipoIdentificador.O);
			bitacora.setAccion(tipoAuditoria.actionType().getValue());
			bitacora.setIdArea(idArea);

			if (tipoAuditoria.actionType().equals(TipoAuditoria.SAVE)) {
				bitacora.setGrupo(1);
			} else if (tipoAuditoria.actionType().equals(TipoAuditoria.UPDATE)) {
				bitacora.setGrupo(5);
			}

			if (entityAudit instanceof Asunto) {

				Asunto asunto = (Asunto) entityAudit;
				
				bitacora.setTipoIdentificador(BitacoraTipoIdentificador.fromString(asunto.getTipoAsunto().getValue()));
				if(asunto.getArea() != null) {
					if(asunto.getArea().getInstitucion() != null)
						bitacora.setInstitucionId(asunto.getArea().getInstitucion().getIdInstitucion());
				}
				
				if(isUpdate)
					if(TipoAsunto.ASUNTO.getValue().equals(asunto.getTipoAsunto().getValue())){
						// Nuevas acciones para Asunto CANCEL/CONCLUDE
						if(StatusAsunto.CANCELADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
							bitacora.setAccion(TipoAuditoria.CANCEL.getValue());
						} else if(StatusAsunto.CONCLUIDO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
							bitacora.setAccion(TipoAuditoria.CONCLUDE.getValue());
						}
					} else {
						if(StatusAsunto.ENVIADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
							bitacora.setAccion(TipoAuditoria.SEND.getValue());
						} else if(StatusAsunto.PROCESO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))  ||
							(	StatusAsunto.CONCLUIDO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))  && 
								(!asunto.getInstruccion().getRequiereRespuesta()) ||
								TipoAsunto.COPIA.getValue().equals(asunto.getTipoAsunto().getValue())
							)
						){
							if(idArea.intValue() != asunto.getAsuntoDetalle().getIdRemitente().intValue())
								bitacora.setAccion(TipoAuditoria.ACCEPT.getValue());
							else
								return;
						} else if(StatusAsunto.RECHAZADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))){
							bitacora.setAccion(TipoAuditoria.REJECT.getValue());
						} else if(StatusAsunto.CONCLUIDO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))) {
							bitacora.setAccion(TipoAuditoria.CONCLUDE.getValue());
						}
					}
				
			} else if (entityAudit instanceof Respuesta) {
				
				Respuesta resp = (Respuesta) entityAudit;
				if(resp.getArea() != null) {
					if(resp.getArea().getInstitucion() != null)
						bitacora.setInstitucionId(resp.getArea().getInstitucion().getIdInstitucion());
				}
				
				bitacora.setTipoIdentificador(BitacoraTipoIdentificador.R);
				// Nuevas acciones para Respuesta SEND/REJECT/CONCLUDE
				if(isUpdate)
					if (Status.ENVIADO == resp.getStatus().getIdStatus())
						bitacora.setAccion(TipoAuditoria.SEND.getValue());
					else if (Status.RECHAZADO == resp.getStatus().getIdStatus())
						bitacora.setAccion(TipoAuditoria.REJECT.getValue());
					else if (Status.CONCLUIDO == resp.getStatus().getIdStatus())
					bitacora.setAccion(TipoAuditoria.CONCLUDE.getValue());

			} else if (entityAudit instanceof Area) {
				Area area = (Area) entityAudit;
				
				if(area.getInstitucion() != null)
					bitacora.setInstitucionId(area.getInstitucion().getIdInstitucion());
				
				if(isUpdate && area.getActiveInactive() != null) {
					if(area.getActiveInactive().equals("true"))
						bitacora.setAccion(TipoAuditoria.ACTIVE.getValue());
					if(area.getActiveInactive().equals("false"))
						bitacora.setAccion(TipoAuditoria.INACTIVE.getValue());
				}
				
			} else if (entityAudit instanceof Usuario) {
				bitacora.setTipoIdentificador(BitacoraTipoIdentificador.U);
				Usuario user = (Usuario) entityAudit;
				
				if(user.getAreaAux() != null) {
					if(user.getAreaAux().getInstitucion() != null)
						bitacora.setInstitucionId(user.getAreaAux().getInstitucion().getIdInstitucion());
				}
				// if(isUpdate && !user.getActivo())
				//	bitacora.setAccion(TipoAuditoria.INACTIVE.getValue());			
			} else if (entityAudit instanceof DocumentoAsunto) {
				DocumentoAsunto documentoAsunto = (DocumentoAsunto) entityAudit;
				
				if(documentoAsunto.getIdOrigen() != null)
					bitacora.setOrigenId(documentoAsunto.getIdOrigen());
				
				if(documentoAsunto.getAsuntoConsulta() != null) {
					if(documentoAsunto.getAsuntoConsulta().getIdPromotor() != null)
						bitacora.setInstitucionId(documentoAsunto.getAsuntoConsulta().getIdPromotor());
					
					if(documentoAsunto.getAsuntoConsulta().getIdAsuntoOrigen() != null)
						bitacora.setOrigenId(documentoAsunto.getAsuntoConsulta().getIdAsuntoOrigen());
				}
				
				if (tipoAuditoria.actionType().equals(TipoAuditoria.UPDATEBITACORA)) {

					if (documentoAsunto.getStatus().equals(StatusFirmaDocumento.BLOQUEADO))
						bitacora.setAccion(TipoAuditoria.DOCLOCK.getValue());

					if (documentoAsunto.getStatus().equals(StatusFirmaDocumento.AUX_QUITA_BLOQUEO))
						bitacora.setAccion(TipoAuditoria.DOCUUNLOCK.getValue());

					if (documentoAsunto.getStatus().equals(StatusFirmaDocumento.FIRMADO))
						bitacora.setAccion(TipoAuditoria.DOCSIGNED.getValue());

					if (documentoAsunto.getStatus().equals(StatusFirmaDocumento.PARA_FIRMA))
						bitacora.setAccion(TipoAuditoria.DOCMARKEDF.getValue());

				} else if (TipoAuditoria.DELETE.equals(tipoAuditoria.actionType()) && documentoAsunto.isVersionable()) { // Version
																															// Publica
					bitacora.setAccion(TipoAuditoria.DELETEVERSION.getValue());
					
				} else if (TipoAuditoria.SAVE.equals(tipoAuditoria.actionType()) && documentoAsunto.isVersionable()) { // Version
																														// Publica
					bitacora.setAccion(TipoAuditoria.VERSION.getValue());
					
				} else {
					if (isUpdate) {
						if (documentoAsunto.getStatus().equals(StatusFirmaDocumento.ENVIO_ANTEFIRMA))
							bitacora.setAccion(TipoAuditoria.DOCSENDF.getValue());

						if (documentoAsunto.getStatus().equals(StatusFirmaDocumento.PARA_FIRMA))
							bitacora.setAccion(TipoAuditoria.DOCMARKEDF.getValue());
					}
				}
				
			} else if (entityAudit instanceof DocumentoRespuesta) {
				 DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entityAudit;
					
					if(documentoRespuesta.getAsuntoConsulta() != null) {
						if(documentoRespuesta.getAsuntoConsulta().getIdPromotor() != null)
							bitacora.setInstitucionId(documentoRespuesta.getAsuntoConsulta().getIdPromotor());
						
						if(documentoRespuesta.getAsuntoConsulta().getIdAsuntoOrigen() != null)
							bitacora.setOrigenId(documentoRespuesta.getAsuntoConsulta().getIdAsuntoOrigen());
					}
					
					if(TipoAuditoria.UPDATEBITACORA.equals(tipoAuditoria.actionType())) {
						
						if(StatusFirmaDocumento.BLOQUEADO.equals(documentoRespuesta.getStatus()))
							bitacora.setAccion(TipoAuditoria.DOCLOCK.getValue());
						
						if(StatusFirmaDocumento.AUX_QUITA_BLOQUEO.equals(documentoRespuesta.getStatus()))
							bitacora.setAccion(TipoAuditoria.DOCUUNLOCK.getValue());
						
						if(StatusFirmaDocumento.FIRMADO.equals(documentoRespuesta.getStatus()))
							bitacora.setAccion(TipoAuditoria.DOCSIGNED.getValue());
						
						if(StatusFirmaDocumento.PARA_FIRMA.equals(documentoRespuesta.getStatus()))
							bitacora.setAccion(TipoAuditoria.DOCMARKEDF.getValue());
						
					} else {
						if(isUpdate) {
							if(StatusFirmaDocumento.ENVIO_ANTEFIRMA.equals(documentoRespuesta.getStatus()))
								bitacora.setAccion(TipoAuditoria.DOCSENDF.getValue());
							
							if(StatusFirmaDocumento.PARA_FIRMA.equals(documentoRespuesta.getStatus()))
								bitacora.setAccion(TipoAuditoria.DOCMARKEDF.getValue());
						}
					}
					
			} else if (entityAudit instanceof Acceso) {
				bitacora.setTipoIdentificador(BitacoraTipoIdentificador.U);
				
				if(tipoAuditoria.actionType().equals(TipoAuditoria.SAVE))
					bitacora.setAccion(TipoAuditoria.ACCESSA.getValue());
				
				if(tipoAuditoria.actionType().equals(TipoAuditoria.DELETE))
					bitacora.setAccion(TipoAuditoria.ACCESSD.getValue());
			} else if (entityAudit instanceof Institucion) {
				
				Institucion institucion = (Institucion) entityAudit;
				
				if(institucion.getIdInstitucion() != null)
					bitacora.setInstitucionId(institucion.getIdInstitucion());
				
				if(isUpdate && institucion.getActiveInactive() != null) {
					if(institucion.getActiveInactive().equals("true"))
						bitacora.setAccion(TipoAuditoria.ACTIVE.getValue());
					if(institucion.getActiveInactive().equals("false"))
						bitacora.setAccion(TipoAuditoria.INACTIVE.getValue());
				}
			}
			
			bitacora.setFechaRegistro(new Date());

			if (entityAudit.getId().matches("[0-9]*")) {
				try {
					bitacora.setIdentificador(Integer.parseInt(entityAudit.getId()));
				} catch (Exception e) {
					bitacora.setIdentificador(null);
				}
			} else {
				bitacora.setIdentificador(null);
			}

			bitacora.setInformacion(entityAudit.getLogDeatil());
			bitacora.setUsuario(usuario);
			bitacora.setIdUsuario(usuario.getIdUsuario());
			try {
				InetAddress info = InetAddress.getLocalHost();
				bitacora.setIp(info.getHostAddress());
		        //String localIp = ip.getHostAddress();
				bitacora.setNombreEquipo(info.getHostName());
		        //String hostname = info.getHostName();
			} catch (UnknownHostException e) {
				// TODO: handle exception
				log.debug("Error: " + e);
			}

			log.debug("BITACORA A GUARDAR >> " + bitacora);
			mngrBitacora.save(bitacora);

			log.debug(bitacora);

		}
	}

	/**
	 * 
	 * @param tipoAuditoria
	 * @param entity
	 * @throws Exception
	 */
	public void notificar(Audit tipoAuditoria, Object entity) throws Exception {

		boolean notificacionEnviada = false;

		if (entity instanceof Asunto && TipoAuditoria.UPDATE.equals(tipoAuditoria.actionType())) {

			Asunto asunto = (Asunto) entity;
			if ((StatusAsunto.ENVIADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))
					&& TipoAuditoria.UPDATE.equals(tipoAuditoria.actionType()))
					|| StatusAsunto.RECHAZADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))) {

				Integer areaDestinatario = null;
				TipoNotificacion tipoNotificacionEnviar = null;
				if (StatusAsunto.ENVIADO.equals(StatusAsunto.forValue(asunto.getStatusAsunto().getIdStatus()))) {
					tipoNotificacionEnviar = TipoNotificacion.RECTURNO;
					areaDestinatario = asunto.getAreaDestino().getIdArea();
				} else {
					tipoNotificacionEnviar = TipoNotificacion.RECTURNORECH;
					areaDestinatario = asunto.getArea().getIdArea();
				}
				notificacionEnviada = mailController.sendNotificacionArea(entity, areaDestinatario,
						tipoNotificacionEnviar);

				if (notificacionEnviada) {
					log.debug(
							"SE HA ENVIADO LA NOTIFICACION DE TIPO " + tipoNotificacionEnviar + " SATISFACTORIAMENTE");
				} else {
					log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO " + tipoNotificacionEnviar);
				}
			}
		} else if (entity instanceof Minutario && TipoAuditoria.UPDATE.equals(tipoAuditoria.actionType())) {
			Minutario minutario = (Minutario) entity;
			if (StatusMinutario.PARA_REVISION.equals(minutario.getStatus())
					|| StatusMinutario.REVISADO.equals(minutario.getStatus())) {
				Usuario destinatario = null;
				if (StatusMinutario.PARA_REVISION.equals(minutario.getStatus())) {
					RevisorMinutario revisor = minutario.getRevisores().get(minutario.getRevisores().size() - 1);
					destinatario = mngrUsuario.fetch(revisor.getId());
				} else {
					destinatario = minutario.getUsuario();
				}
				notificacionEnviada = mailController.sendNotificacionSigap(entity, destinatario,
						TipoNotificacion.REOFICIO);

				if (notificacionEnviada) {
					log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + TipoNotificacion.REOFICIO
							+ " SATISFACTORIAMENTE");
				} else {
					log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO " + TipoNotificacion.REOFICIO);
				}

			}
		} else if (entity instanceof Respuesta && TipoAuditoria.UPDATE.equals(tipoAuditoria.actionType())) {
			Respuesta respuesta = (Respuesta) entity;
			if (StatusAsunto.ENVIADO.equals(StatusAsunto.forValue(respuesta.getStatus().getIdStatus()))
					|| StatusAsunto.RECHAZADO.equals(StatusAsunto.forValue(respuesta.getStatus().getIdStatus()))) {

				notificacionEnviada = mailController.sendNotificacionArea(entity,
						respuesta.getAreaDestino().getIdArea(), TipoNotificacion.RECRESPUESTA);

				if (notificacionEnviada) {
					log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + TipoNotificacion.RECRESPUESTA
							+ " SATISFACTORIAMENTE");
				} else {
					log.debug(
							"NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO " + TipoNotificacion.RECRESPUESTA);
				}
			}
		} else if (entity instanceof DocumentoAsunto && TipoAuditoria.UPDATE.equals(tipoAuditoria.actionType())) {
			DocumentoAsunto documentoAsunto = (DocumentoAsunto) entity;

			if (StatusFirmaDocumento.PARA_FIRMA.equals(documentoAsunto.getStatus()) || StatusFirmaDocumento.PARA_FIRMA_Y_FIRMADO.equals(documentoAsunto.getStatus())) {
				Area areaDocumento = mngrArea.fetch(documentoAsunto.getIdArea());				
				notificacionEnviada = mailController.sendNotificacionArea(entity, areaDocumento.getIdArea(), TipoNotificacion.RECDOCPFIR);

				if (notificacionEnviada) {
					log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + TipoNotificacion.RECDOCPFIR
							+ " SATISFACTORIAMENTE");
				} else {
					log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO " + TipoNotificacion.RECDOCPFIR);
				}

			}
			// else if
			// (StatusFirmaDocumento.FIRMADO.equals(documentoAsunto.getStatus()))
			// {
			//
			// notificacionEnviada = mailController.sendNotificacionArea(entity,
			// rutaTemplate,
			// documentoAsunto.getIdArea(), TipoNotificacion.RECDOCFIR);
			// if (notificacionEnviada) {
			// log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " +
			// TipoNotificacion.RECDOCFIR
			// + " SATISFACTORIAMENTE");
			// } else {
			// log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE
			// TIPO " + TipoNotificacion.RECDOCFIR);
			// }
			// }
		} else if (entity instanceof DocumentoRespuesta && TipoAuditoria.UPDATE.equals(tipoAuditoria.actionType())) {
			DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entity;
			Asunto asuntoDocumento = mngrAsunto.fetch(documentoRespuesta.getIdAsunto());

			Area areaDestino = asuntoDocumento.getAreaDestino();

			if (areaDestino != null)
				if (StatusFirmaDocumento.PARA_FIRMA.equals(documentoRespuesta.getStatus()) || StatusFirmaDocumento.PARA_FIRMA_Y_FIRMADO.equals(documentoRespuesta.getStatus())) {

					notificacionEnviada = mailController.sendNotificacionArea(entity, areaDestino.getIdArea(), TipoNotificacion.RECDOCPFIR);

					if (notificacionEnviada) {
						log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + TipoNotificacion.RECDOCPFIR
								+ " SATISFACTORIAMENTE");
					} else {
						log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
								+ TipoNotificacion.RECDOCPFIR);

					}

				}
			// else if
			// (StatusFirmaDocumento.FIRMADO.equals(documentoRespuesta.getStatus()))
			// {
			//
			// notificacionEnviada = mailController.sendNotificacionArea(entity,
			// rutaTemplate,
			// areaDestino.getIdArea(), TipoNotificacion.RECDOCFIR);
			//
			// if (notificacionEnviada) {
			// log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " +
			// TipoNotificacion.RECDOCFIR
			// + " SATISFACTORIAMENTE");
			// } else {
			// log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE
			// TIPO "
			// + TipoNotificacion.RECDOCFIR);
			// }
			// }
		} else if (entity instanceof DocumentoAntefirma && TipoAuditoria.SAVE.equals(tipoAuditoria.actionType())) {
			DocumentoAntefirma documentoAntefirma = (DocumentoAntefirma) entity;
			if (documentoAntefirma.getFirmado() == null) {
				Usuario destinatario = mngrUsuario.fetch(documentoAntefirma.getDocumentoAntefirmaKey().getFirmante());

				notificacionEnviada = mailController.sendNotificacionSigap(entity, destinatario,
						TipoNotificacion.DOC_PARA_ANTEFIRMA);

				if (notificacionEnviada) {
					log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + TipoNotificacion.DOC_PARA_ANTEFIRMA
							+ " SATISFACTORIAMENTE");
				} else {
					log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
							+ TipoNotificacion.DOC_PARA_ANTEFIRMA);
				}
			}
		}

	}
	
}
