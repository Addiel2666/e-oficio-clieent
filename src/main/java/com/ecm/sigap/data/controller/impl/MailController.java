/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.mail.MailProcess;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoDetalle;
import com.ecm.sigap.data.model.Configuracion;
import com.ecm.sigap.data.model.ConfiguracionKey;
import com.ecm.sigap.data.model.DocumentoAntefirmaAsunto;
import com.ecm.sigap.data.model.DocumentoAntefirmaRespuesta;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Minutario;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.RevisorMinutario;
import com.ecm.sigap.data.model.util.StatusMinutario;
import com.ecm.sigap.data.model.util.TipoNotificacion;

/**
 * The Class MailController.
 *
 * @author Gustavo Vielma
 * @version 1.1
 */
@RestController
public class MailController extends CustomRestController {

	/** Logger de la aplicacion. */
	private static final Logger log = LogManager.getLogger(MailController.class);

	/** The mail. */
	@Autowired
	@Qualifier("mailProcess")
	private MailProcess mail;

	/**
	 * Envio inmediato de una notificacion de un correo externo.
	 *
	 * @param destinatario the destinatario
	 * @param claveAcceso  the clave acceso
	 * @param parametros   the parametros
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean sendNotificacionCorreoExterno(Usuario destinatario, String claveAcceso, String parametros)
			throws Exception {

		// ruta donde se encuentran las plantillas de correo
		mail.setAppPath("/correosexternos/");

		if (destinatario == null || destinatario.getEmail() == null || "".equals(destinatario.getEmail())) {
			throw new Exception("Remitente de correo no válido");
		}

		// parametros a sustituir en la plantilla
		Hashtable<String, String> parameters = new Hashtable<String, String>();
		parameters.put("nombreCiudadano", destinatario.getNombreCompleto());
		parameters.put("parametrosURL", parametros);
		parameters.put("claveAcceso", claveAcceso);

		return mail.sendMail(destinatario.getEmail().toLowerCase(), parameters,
				environment.getProperty("sigap.notificaciones.externas.subject"));
	}

	/**
	 * 
	 * @param email
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public boolean sendNotificacionEmpty(String email, String body) throws Exception {

		// ruta donde se encuentran las plantillas de correo
		mail.setAppPath("/emptyBody/");

		// parametros a sustituir en la plantilla
		Hashtable<String, String> parameters = new Hashtable<String, String>();
		parameters.put("body", body);

		return mail.sendMail(email, parameters, "Notificacion e-oficio interno.");
	}

	/**
	 * Obtiene la lista de los correos electronicos de los usuarios.
	 *
	 * @param usuarios Lista de Usuarios
	 * @return Lista de los correos electronicos
	 */
	private Set<String> getCorreosUsuarios(List<Usuario> usuarios) {

		Set<String> correos = new HashSet<String>();
		for (Usuario usuario : usuarios) {
			if (!StringUtils.isBlank(usuario.getEmail())) {
				// correos.add("gvielma87@gmail.com");
				// break;
				correos.add(usuario.getEmail().trim());
			}
		}
		return correos;
	}

	/**
	 * Obtiene la lista de Usuarios que pertenecen a un Area
	 * 
	 * @param idArea Identificdor del Area
	 * @return Lista de Usuarios que pertenecen a un Area
	 * @throws Exception Cualquier error al momento de ejcutar el metodo
	 */
	@SuppressWarnings("unchecked")
	private List<Usuario> getDestinatariosArea(int idArea) throws Exception {

		List<?> lst = new ArrayList<>();
		log.info("Consultandao usuarios del area :: " + idArea);

		// * * * * * * * * * * * * * * * * * * * * * *
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("idArea", idArea));
		restrictions.add(Restrictions.eq("activo", true));
		// * * * * * * * * * * * * * * * * * * * * * *
		lst = mngrUsuario.search(restrictions);

		// Set<String> idsUsuarios = new HashSet<String>();
		// for (Usuario usuario : lst) {
		// idsUsuarios.add(usuario.getIdUsuario());
		// }
		return (List<Usuario>) lst;
	}

	/**
	 * Obtiene la lista de Usuarios que pertenecen a varias Areas
	 * 
	 * @param Lista de idAreas, Identificador de las Areas
	 * @return Lista de Usuarios que pertenecen a varias Areas
	 * @throws Exception Cualquier error al momento de ejcutar el metodo
	 */
	@SuppressWarnings("unchecked")
	private List<Usuario> getDestinatariosArea(List<Integer> idAreas) {
//		List<?> lst = new ArrayList<>();
//		List<?> lst2 = new ArrayList<>();
//		log.info("Consultandao usuarios de las areas :: " + idAreas);
//		
//		for (Integer idArea : idAreas) {
//			// * * * * * * * * * * * * * * * * * * * * * *
//			List<Criterion> restrictions = new ArrayList<Criterion>();
//			restrictions.add(Restrictions.eq("idArea", idArea));
//			restrictions.add(Restrictions.eq("activo", true));
//			// * * * * * * * * * * * * * * * * * * * * * *
//			lst=mngrUsuario.search(restrictions);
//			lst2= Stream.concat(lst.stream(), lst2.stream()).collect(Collectors.toList());
//		}
//		return (List<Usuario>) lst2;

		List<Usuario> lst = new ArrayList<>();
		List<?> lst2 = new ArrayList<>();
		log.info("Consultandao usuarios de las areas :: " + idAreas);

		for (Integer idArea : idAreas) {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Rol> roles = new ArrayList<>();
			List<Integer> idrol = new ArrayList<>();
			roles = getRoles(idArea);

			// Se agrega el idrol de roles administradores para su busqueda.
			for (Rol rol : roles) {
				log.info("IdRol administrador :: " + rol.getIdRol());
				idrol.add(rol.getIdRol());
			}

			// Si la lista de roles administradores no esta vacia, se ejecuta la busqueda de
			// sus usuarios con dicho rol.
			if (!roles.isEmpty()) {
				log.info("Consultando usuarios con rol de administrador en el area :: " + idArea);
				List<Criterion> restrictions = new ArrayList<Criterion>();
				restrictions.add(Restrictions.eq("idArea", idArea));
				restrictions.add(Restrictions.eq("activo", true));
				restrictions.add(Restrictions.in("rol.idRol", idrol));
				lst = (List<Usuario>) mngrUsuario.search(restrictions);
			}

			log.info("Consultando el usuario titular del area :: " + idArea);
			Area area = mngrArea.fetch(idArea);
			Usuario user = mngrUsuario.fetch(area.getTitular().getUsuario().getIdUsuario());
			if (user != null)
				lst.add(user);
			// * * * * * * * * * * * * * * * * * * * * * *
			lst2 = Stream.concat(lst.stream(), lst2.stream()).collect(Collectors.toList());
		}
		return (List<Usuario>) lst2;
	}

	/**
	 * Obtenemos la Lista concatenada de los nombres de los usuarios.
	 *
	 * @param usuarios Lista de los Usuarios
	 * @return Lista concatenada de los nombres de los usuarios
	 */
	private String getNombresUsuarios(List<Usuario> usuarios) {

		StringBuilder nombres = new StringBuilder();

		for (Usuario usuario : usuarios) {
			nombres.append(usuario.getNombres()).append(" ").append(usuario.getApellidoPaterno()).append(" ")
					.append(usuario.getMaterno()).append(" /");
		}
		String nombresStr = nombres.toString();
		return nombresStr.substring(0, nombres.length() - 1);
	}

	/**
	 * Valida si el usuario tiene activo el envio de la notificacion.
	 *
	 * @param tipoNotificacion Tipo de notificacion
	 * @param idsUsuarios      Identificador del usuario
	 * @return true-activada/ false cualquier otro
	 * @throws Exception Error al momento de ejecutar el metodo
	 */
	private List<Usuario> listUsuarioNotificacion(TipoNotificacion tipoNotificacion, List<Usuario> idsUsuarios)
			throws Exception {

		Iterator<Usuario> iter = idsUsuarios.iterator();
		while (iter.hasNext()) {
			Usuario idUsuario = iter.next();
			ConfiguracionKey configKey = new ConfiguracionKey();
			configKey.setClave(tipoNotificacion);
			configKey.setIdConfiguracion("NOTIFICACION");
			configKey.setUsuario(idUsuario);
			Configuracion config = mngrConfiguracion.fetch(configKey);
			if (config == null) {
				iter.remove();
			} else if (!StringUtils.isBlank(idUsuario.getEmail()) //
					&& "S".equalsIgnoreCase(config.getValor()) != true) {
				iter.remove();
			}
		}

		return idsUsuarios;
	}

	/**
	 * Send notificacion docto firmado.
	 *
	 * @param entity           the entity
	 * @param scratchPath      the scratch path
	 * @param idAreaNotificar  the id area notificar
	 * @param tipoNotificacion
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean sendNotificacionDoctoFirmado(Object entity, Integer idAreaNotificar,
			TipoNotificacion tipoNotificacion) throws Exception {

		return sendNotificacionArea(entity, idAreaNotificar, tipoNotificacion);

	}

	/**
	 * Envia una notificacion a todos los miembros de una Area.
	 *
	 * @param entity       the entity
	 * @param scratchPath  Ruta del directorio Scratch de SIGAP
	 * @param idArea       Identificador del Area al que se le va a enviar la
	 *                     notificacion
	 * @param notificacion Tipo de notificacion que se quiere enviar
	 *                     {@link TipoNotificacion }
	 * @return True en caso que se pudo enviar todas las notificaciones a los
	 *         usuarios del Area, de lo contrario False
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	public boolean sendNotificacionArea(Object entity, Integer idArea, TipoNotificacion notificacion) throws Exception {

		List<Usuario> usuariosArea = null;
		// se comenta ya que se solicita que las notificaciones deberán llegar para todos los usuarios que esten registrados en el área 
		//if (TipoNotificacion.RECDOCANTEFIR.equals(notificacion)) {
			//usuariosArea = getDestinatariosAdminArea(idArea);
	//	} else {
			usuariosArea = getDestinatariosArea(idArea);
		//}

		return sendNotificacionSigap(entity, usuariosArea, notificacion);
	}

	/**
	 * Send notificacion de un documento.
	 *
	 * @param entity           the entity
	 * @param scratchPath      the scratch path
	 * @param idAreaNotificar  the id area notificar
	 * @param tipoNotificacion
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean sendNotificacionDocto(Object entity, List<Integer> idAreasNotificar,
			TipoNotificacion tipoNotificacion) throws Exception {
		return sendNotificacionArea(entity, idAreasNotificar, tipoNotificacion);
	}

	/**
	 * Envia una notificacion a todos los miembros de varias Area.
	 *
	 * @param entity       the entity
	 * @param scratchPath  Ruta del directorio Scratch de SIGAP
	 * @param idArea       Identificador del Area al que se le va a enviar la
	 *                     notificacion
	 * @param notificacion Tipo de notificacion que se quiere enviar
	 *                     {@link TipoNotificacion }
	 * @return True en caso que se pudo enviar todas las notificaciones a los
	 *         usuarios del Area, de lo contrario False
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	public boolean sendNotificacionArea(Object entity, List<Integer> idAreas, TipoNotificacion notificacion)
			throws Exception {
		List<Usuario> usuariosArea = null;
		usuariosArea = getDestinatariosArea(idAreas);
		return sendNotificacionSigap(entity, usuariosArea, notificacion, false);
	}

	/**
	 * Envia un tipo de notificacion por correo a una lista de usuarios.
	 *
	 * @param entity       the entity
	 * @param idsUsuarios  Lista de los Identificador del Usuario destinatario de la
	 *                     Notificacion
	 * @param notificacion Tipo de Notificacion que se desea enviar
	 *                     {@link TipoNotificacion}
	 * @return True en caso que se pueda enviar la notificacion de lo contrario
	 *         False
	 * @throws Exception 2 * Cualquier error al momento de ejcutar el metodo
	 */
	public boolean sendNotificacionSigap(Object entity, List<Usuario> idsUsuarios, TipoNotificacion notificacion,
			boolean checkConfig) throws Exception {

		log.debug("Iniciando el envio de la notificacion del tipo " + notificacion.name() + " para el usuario "
				+ Arrays.toString(idsUsuarios.toArray()));

		// Se agrego esta validación ya que "ENIAR PARA ANTEFIRMA" Y "MARCAR PARA FIRMA"
		// comparten la misma casilla/clave en las notificaciones.
		TipoNotificacion notif = TipoNotificacion.DOC_PARA_ANTEFIRMA.equals(notificacion) ? TipoNotificacion.RECDOCPFIR
				: notificacion;

		// Se verifica y se filtra la lista de usuarios que tienen activo el
		// envio de notificación.
		// List<Usuario> destinatarios = listUsuarioNotificacion(notif, idsUsuarios);
		List<Usuario> destinatarios = checkConfig ? listUsuarioNotificacion(notif, idsUsuarios) : idsUsuarios;

		// Se valida que al menos un Usuario tenga activa la notificacion
		if (destinatarios != null && !destinatarios.isEmpty()) {

			Set<String> correosDest = getCorreosUsuarios(destinatarios);

			if (correosDest == null || correosDest.isEmpty()) {
				log.debug("::::-> Aunque existe Usuarios no se encontró ningun email");
				return false;
			}

			log.debug(":::> INICIANDO ENVIO DE NOTIFICACION");
			String subject = "";
			String url = "";

			// MailProcess mail = new MailProcess();

			// parametros a sustituir en la plantilla
			Hashtable<String, String> parameters = new Hashtable<String, String>();

			switch (notificacion) {

			case REOFICIO:
				if (entity instanceof Minutario) {
					Minutario minutario = (Minutario) entity;
					log.debug("::: Enviando la notificacion para la revision delminutario");

					RevisorMinutario revisor = minutario.getRevisores().get(minutario.getRevisores().size() - 1);

					if (Double.valueOf(revisor.getVersion()) > 1) {
						mail.setAppPath("/minutario/borrador/");
					} else {
						mail.setAppPath("/minutario/");
					}
					parameters.put("idMinutario", minutario.getIdMinutario().toString());
					parameters.put("url", environment.getProperty("sigap.minutario.url"));
					parameters.put("revisor", getNombresUsuarios(destinatarios));
					parameters.put("area", minutario.getRemitente().getDescripcion());
					parameters.put("firmante",
							minutario.getFirmante().getNombres() + " " + minutario.getFirmante().getApellidoPaterno());
					// VERSION 1.2
					if (StatusMinutario.REVISADO.equals(minutario.getStatus()))
						subject = "sigap.minutario.revisado.subject";
					else if (StatusMinutario.PARA_REVISION.equals(minutario.getStatus())
							&& (Double.valueOf(revisor.getVersion()) > 1))
						subject = "Revisar borrador de oficio de la " + minutario.getRemitente().getDescripcion();
					else if (StatusMinutario.PARA_REVISION.equals(minutario.getStatus()))
						subject = "sigap.minutario.difFirmante.subject";
					else
						subject = "sigap.minutario.subject";
					break;
				} else {
					throw new Exception("No existe instancia para el objeto Minutario");
				}

			case RECRESPUESTA:
				if (entity instanceof Respuesta) {
					Respuesta respuesta = (Respuesta) entity;

					log.debug("::: Enviando la notificacion de la Respuesta");
					Asunto asuntoRespuesta = mngrAsunto.fetch(respuesta.getIdAsunto());
					if (asuntoRespuesta == null) {
						throw new Exception("No existe el asunto con id: " + respuesta.getIdAsunto()
								+ " asociado a la respuesta con id: " + respuesta.getIdRespuesta());
					}

					mail.setAppPath("/respuestas/");
					url = "sigap.respuesta.url";
					String id = respuesta.getIdRespuesta().toString();
					subject = "sigap.respuesta.subject";

					parameters.put("IDASUNTO", id);
					parameters.put("oficio", asuntoRespuesta.getAsuntoDetalle().getNumDocto());
					parameters.put("firmante",
							(asuntoRespuesta.getAsuntoDetalle().getFirmante() != null
									? asuntoRespuesta.getAsuntoDetalle().getFirmante().getNombreCompleto()
									: ""));

					parameters.put("asunto", asuntoRespuesta.getAsuntoDetalle().getAsuntoDescripcion());
					parameters.put("area", respuesta.getArea().getDescripcion());
					parameters.put("institucion", respuesta.getArea().getInstitucion().getDescripcion());
					parameters.put("idorigen", asuntoRespuesta.getIdAsuntoOrigen().toString());
					parameters.put("url", environment.getProperty(url));
					parameters.put("destinatario", getNombresUsuarios(destinatarios));
					break;
				} else {
					throw new Exception("No existe instancia para el objeto Respuesta");
				}

			case RECTURNO:
				if (entity instanceof Asunto) {
					Asunto asunto = (Asunto) entity;

					log.debug("::: Enviando la notificacion del Turno");
					String requiereRespuesta = null;
					mail.setAppPath("/turnos/");

					url = "sigap.turno.url";
					if (asunto.getInstruccion().getRequiereRespuesta() == true) {
						requiereRespuesta = "Requiere Respuesta";
					} else {
						requiereRespuesta = "No Requiere Respuesta";
					}

					parameters.put("IDASUNTO", asunto.getIdAsunto().toString());
					subject = "sigap.turno.nuevo.subject";

					parameters.put("instruccion", asunto.getInstruccion().getDescripcion());
					parameters.put("requiere", requiereRespuesta);
					parameters.put("oficio", asunto.getAsuntoDetalle().getNumDocto());
					parameters.put("firmante",
							(asunto.getAsuntoDetalle().getFirmante() != null
									? asunto.getAsuntoDetalle().getFirmante().getNombreCompleto()
									: ""));
					parameters.put("asunto", asunto.getAsuntoDetalle().getAsuntoDescripcion());
					parameters.put("comentario", (null != asunto.getComentario() ? asunto.getComentario() : ""));
					parameters.put("area", asunto.getArea().getDescripcion());
					parameters.put("institucion", asunto.getArea().getInstitucion().getDescripcion());
					parameters.put("url", environment.getProperty(url));
					parameters.put("destinatario", getNombresUsuarios(destinatarios));
					parameters.put("idorigen", asunto.getIdAsuntoOrigen().toString());
					break;
				} else {
					throw new Exception("No existe instancia para el objeto Asunto");
				}
			case RECTURNORECH:
				if (entity instanceof Asunto) {
					Asunto asunto = (Asunto) entity;

					log.debug("::: Enviando la notificacion del Turno Rechazado");
					String requiereRespuesta = null;

					mail.setAppPath("/turnos/");

					url = "sigap.asuntorechazado.url";

					if (asunto.getInstruccion() != null //
							&& asunto.getInstruccion().getRequiereRespuesta() == true) {
						requiereRespuesta = "Requiere Respuesta";
					} else {
						requiereRespuesta = "No Requiere Respuesta";
					}

					parameters.put("IDASUNTO", "");
					subject = "sigap.asunto.rechazado.subject";

					parameters.put("instruccion",
							asunto.getInstruccion() != null ? asunto.getInstruccion().getDescripcion() : "");
					parameters.put("requiere", requiereRespuesta);

					AsuntoDetalle ad = asunto.getAsuntoDetalle();

					parameters.put("oficio", ad.getNumDocto());

					parameters.put("firmante", //
							ad.getFirmante() != null ? ad.getFirmante().getNombreCompleto() : "");

					parameters.put("asunto", ad.getAsuntoDescripcion());

					parameters.put("comentario", //
							StringUtils.isNotBlank(asunto.getComentario()) ? asunto.getComentario() : "");

					Area area = asunto.getArea();

					parameters.put("area", area != null ? area.getDescripcion() : "");
					parameters.put("institucion", area != null ? area.getInstitucion().getDescripcion() : "");
					parameters.put("url", environment.getProperty(url));
					parameters.put("destinatario", getNombresUsuarios(destinatarios));
					parameters.put("idorigen", asunto.getIdAsuntoOrigen().toString());

					log.debug("parameters :: " + parameters);

					break;

				} else {

					throw new Exception("No existe instancia para el objeto Asunto Rechazado");

				}
			case RECDOCPFIR:
				Asunto asuntoDocumento = null;

				if (entity instanceof DocumentoAsunto) {
					DocumentoAsunto documentoAsunto = (DocumentoAsunto) entity;
					asuntoDocumento = mngrAsunto.fetch(documentoAsunto.getIdAsunto());
				} else if (entity instanceof DocumentoRespuesta) {
					DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entity;
					asuntoDocumento = mngrAsunto.fetch(documentoRespuesta.getIdAsunto());
				}
				if (asuntoDocumento != null) {

					log.debug("::: Generando la notificacion del Documento marcado para firma");
					mail.setAppPath("/documentosmardadofirma/");
					parameters.put("IDASUNTO", asuntoDocumento.getIdAsunto().toString());
					parameters.put("oficio", asuntoDocumento.getAsuntoDetalle().getNumDocto());
					parameters.put("asunto", asuntoDocumento.getAsuntoDetalle().getAsuntoDescripcion());
					subject = "sigap.documentos.marcado.firma.subject";
					parameters.put("destinatario", getNombresUsuarios(destinatarios));
					parameters.put("idorigen", asuntoDocumento.getIdAsuntoOrigen().toString());
					break;
				} else {
					throw new Exception("No se puede Generar la Notificacion, el asunto se encuentra NULL");
				}
			case RECDOCFIR:
				Asunto asuntoDocumentoA = null;

				if (entity instanceof DocumentoAsunto) {
					DocumentoAsunto documentoAsunto = (DocumentoAsunto) entity;
					asuntoDocumentoA = mngrAsunto.fetch(documentoAsunto.getIdAsunto());
				} else if (entity instanceof DocumentoRespuesta) {
					DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entity;
					asuntoDocumentoA = mngrAsunto.fetch(documentoRespuesta.getIdAsunto());
				}
				if (asuntoDocumentoA != null) {
					log.debug("::: Enviando la notificacion del Documento Firmado");
					mail.setAppPath("/documentosfirma/");
					parameters.put("IDASUNTO", asuntoDocumentoA.getIdAsunto().toString());
					parameters.put("oficio", asuntoDocumentoA.getAsuntoDetalle().getNumDocto());
					parameters.put("idorigen", asuntoDocumentoA.getIdAsuntoOrigen().toString());
					subject = "sigap.documentos.firma.subject";
					break;
				} else {
					throw new Exception("No se puede Generar la Notificacion, el asunto se encuentra NULL");
				}
			case RECDOCANTEFIR:
				Asunto asuntoDocumentoAF = null;

				if (entity instanceof DocumentoAsunto) {
					DocumentoAsunto documentoAsunto = (DocumentoAsunto) entity;
					asuntoDocumentoAF = mngrAsunto.fetch(documentoAsunto.getIdAsunto());
				} else if (entity instanceof DocumentoRespuesta) {
					DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entity;
					asuntoDocumentoAF = mngrAsunto.fetch(documentoRespuesta.getIdAsunto());
				}
				if (asuntoDocumentoAF != null) {
					log.debug("::: Enviando la notificacion del Documento Antefirmado.");
					mail.setAppPath("/documentoantefirma/");
					parameters.put("IDASUNTO", asuntoDocumentoAF.getIdAsunto().toString());
					parameters.put("oficio", asuntoDocumentoAF.getAsuntoDetalle().getNumDocto());
					parameters.put("idorigen", asuntoDocumentoAF.getIdAsuntoOrigen().toString());
					subject = "sigap.documentos.antefirma.subject";
					break;
				} else {
					throw new Exception("No se puede Generar la Notificacion, el asunto se encuentra NULL");
				}
			case DOC_CANCELADO:
				Asunto asuntoDocumentoCan = null;
				String nombreDoc = null;
				if (entity instanceof DocumentoAsunto) {
					DocumentoAsunto documentoAsunto = (DocumentoAsunto) entity;
					nombreDoc = documentoAsunto.getObjectName();
					asuntoDocumentoCan = mngrAsunto.fetch(documentoAsunto.getIdAsunto());
				} else if (entity instanceof DocumentoRespuesta) {
					DocumentoRespuesta documentoRespuesta = (DocumentoRespuesta) entity;
					nombreDoc = documentoRespuesta.getObjectName();
					asuntoDocumentoCan = mngrAsunto.fetch(documentoRespuesta.getIdAsunto());
				}
				if (asuntoDocumentoCan != null) {
					log.debug("::: Enviando la notificacion del Documento Cancelado.");
					mail.setAppPath("/documentocancelado/");
					parameters.put("oficio", asuntoDocumentoCan.getAsuntoDetalle().getNumDocto());
					parameters.put("nombredoc", nombreDoc);
					parameters.put("idorigen", asuntoDocumentoCan.getIdAsuntoOrigen().toString());
					subject = "sigap.documentos.cancelado.subject";
					break;
				} else {
					throw new Exception("No se puede Generar la Notificacion, el asunto se encuentra NULL");
				}
			case DOC_PARA_ANTEFIRMA:
				log.debug("::: Notificacion del Documento enviado para antefirma.");
				mail.setAppPath("/documentoparaantefirma/");
				parameters.put("destinatario", getNombresUsuarios(destinatarios));
				subject = "sigap.documento.pendiente.antefirma.subject";
				break;
			default:
				throw new Exception("El tipo de notificion soliciada no esta soportada");
			}
			log.debug("::: Se va a llamar al metodo para el envio de la notificacion");
			if (subject.contains("borrador"))
				return mail.sendMail(correosDest, parameters, subject);
			else
				return mail.sendMail(correosDest, parameters, environment.getProperty(subject));
		} else {

			log.debug(
					"::::-> No hay usuarios para enviar la notificacion. Puede ser que no tiene la notificacion ativada");
			return false;
		}
	}

	/**
	 * Método sobrecarga de notificacion por correo al usuario.
	 *
	 * @param entity       the entity
	 * @param idUsuarios   Identificador del Usuarios destinatario de la
	 *                     Notificacion
	 * @param notificacion Tipo de Notificacion que se desea enviar
	 *                     {@link TipoNotificacion}
	 * @return True en caso que se pueda enviar la notificacion de lo contrario
	 *         False
	 * @throws Exception Cualquier error al momento de ejcutar el metodo
	 */
	public boolean sendNotificacionSigap(Object entity, List<Usuario> idsUsuarios, TipoNotificacion notificacion)
			throws Exception {
		return sendNotificacionSigap(entity, idsUsuarios, notificacion, true);
	}

	/**
	 * Envia un tipo de notificacion por correo al usuario.
	 *
	 * @param entity       the entity
	 * @param idUsuario    Identificador del Usuario destinatario de la Notificacion
	 * @param notificacion Tipo de Notificacion que se desea enviar
	 *                     {@link TipoNotificacion}
	 * @return True en caso que se pueda enviar la notificacion de lo contrario
	 *         False
	 * @throws Exception Cualquier error al momento de ejcutar el metodo
	 */
	public boolean sendNotificacionSigap(Object entity, Usuario idUsuario, TipoNotificacion notificacion)
			throws Exception {
		log.debug("Iniciando el envio de la notificacion del tipo " + notificacion.name() + " para el usuario "
				+ idUsuario);

		List<Usuario> idUsuariosDestino = new ArrayList<>();
		idUsuariosDestino.add(idUsuario);

		return sendNotificacionSigap(entity, idUsuariosDestino, notificacion);
	}

	/**
	 * 
	 * @param entity
	 * @param idsUsuarios
	 * @param notificacion
	 * @param comentarioRechazo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public boolean enviarNotificacionSigap(Object entity, TipoNotificacion notificacion, String comentarioRechazo)
			throws Exception {

		Asunto asuntoDocumentoAR = null;
		String objectName = "";

		if (entity instanceof DocumentoAntefirmaAsunto) {
			DocumentoAntefirmaAsunto documentoAntefirmaAsunto = (DocumentoAntefirmaAsunto) entity;
			objectName = documentoAntefirmaAsunto.getObjectName();
			asuntoDocumentoAR = mngrAsunto.fetch(documentoAntefirmaAsunto.getDocumentoAntefirmaKey().getId());
		} else if (entity instanceof DocumentoAntefirmaRespuesta) {
			DocumentoAntefirmaRespuesta documentoAntefirmaRespuesta = (DocumentoAntefirmaRespuesta) entity;
			objectName = documentoAntefirmaRespuesta.getObjectName();
			asuntoDocumentoAR = mngrAsunto.fetch(documentoAntefirmaRespuesta.getRespuestaConsulta().getIdAsunto());
		}

		List<Usuario> idsUsuarios = new ArrayList<Usuario>();
		Usuario user = mngrUsuario.fetch(asuntoDocumentoAR.getAsuntoDetalle().getIdFirmante());
		idsUsuarios.add(user);

		log.debug(" :: Iniciando el envio de la notificacion del tipo " + notificacion.name() + " para el usuario "
				+ Arrays.toString(idsUsuarios.toArray()));

		// Se verifica y se filtra la lista de usuarios que tienen activo el
		// envio de notificación.
		List<Usuario> destinatarios = listUsuarioNotificacion(notificacion, idsUsuarios);
		// Se valida que al menos un Usuario tenga activa la notificacion
		if (destinatarios != null && !destinatarios.isEmpty()) {

			Set<String> correosDest = getCorreosUsuarios(destinatarios);

			if (correosDest == null || correosDest.isEmpty()) {
				log.debug("::::-> Aunque existe Usuarios no se encontró ningun email");
				return false;
			}

			log.debug(":::> INICIANDO ENVIO DE NOTIFICACION");
			String subject = "";
			Hashtable<String, String> parameters = new Hashtable<String, String>();

			switch (notificacion) {
			case RECANTEFIRMAREC:
				if (asuntoDocumentoAR != null) {
					log.debug("::: Enviando la notificacion del Documento Antefirmado.");
					mail.setAppPath("/documentorechazoantefirma/");
					parameters.put("IDASUNTO", asuntoDocumentoAR.getIdAsunto().toString());
					parameters.put("objectName", objectName);
					parameters.put("oficio", asuntoDocumentoAR.getAsuntoDetalle().getNumDocto());
					parameters.put("comentarioRechazo", comentarioRechazo);
					subject = "sigap.documentos.antefirma.rechazado.subject";
					break;
				} else {
					throw new Exception("No se puede Generar la Notificacion, el asunto se encuentra NULL");
				}
			default:
				throw new Exception("El tipo de notificion soliciada no esta soportada");
			}
			log.debug("::: Se va a llamar al metodo para el envio de la notificacion");
			return mail.sendMail(correosDest, parameters, environment.getProperty(subject));
		} else {
			log.debug(
					"::::-> No hay usuarios para enviar la notificacion. Puede ser que no tiene la notificacion ativada");
			return false;
		}
	}

	/**
	 * Obtiene la lista de Usuarios que pertenecen a un Area y son Administradores,
	 * asi como el titular del area.
	 * 
	 * @param idArea Identificdor del Area
	 * @return Obtiene la lista de Usuarios que pertenecen a un Area y son
	 *         Administradores, asi como el titular del area.
	 * @throws Exception Cualquier error al momento de ejcutar el metodo
	 */
	@SuppressWarnings("unchecked")
	private List<Usuario> getDestinatariosAdminArea(int idArea) throws Exception {

		List<Usuario> lst = new ArrayList<>();
		List<Rol> roles = new ArrayList<>();
		List<Integer> idrol = new ArrayList<>();

		log.info("Consultando roles administradores del area :: " + idArea);
		roles = getRoles(idArea);

		// Se agrega el idrol de roles administradores para su busqueda.
		for (Rol rol : roles) {
			log.info("IdRol administrador :: " + rol.getIdRol());
			idrol.add(rol.getIdRol());
		}

		// Si la lista de roles administradores no esta vacia
		// se ejecuta la busqueda de sus usuarios con dicho rol.
		if (!roles.isEmpty()) {
			log.info("Consultando usuarios con rol de administrador en el area :: " + idArea);
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("idArea", idArea));
			restrictions.add(Restrictions.eq("activo", true));
			restrictions.add(Restrictions.in("rol.idRol", idrol));
			lst = (List<Usuario>) mngrUsuario.search(restrictions);
		}

		log.info("Consultando el usuario titular del area :: " + idArea);
		Area area = mngrArea.fetch(idArea);
		Usuario user = mngrUsuario.fetch(area.getTitular().getUsuario().getIdUsuario());
		if (user != null)
			lst.add(user);

		return lst;
	}

	/**
	 * Obtiene los roles administradores de un area.
	 *
	 * @param idArea the id area
	 * @return the roles
	 */
	@SuppressWarnings("unchecked")
	private List<Rol> getRoles(Integer idArea) {
		List<Rol> lst = new ArrayList<Rol>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("idArea", idArea));
		restrictions.add(Restrictions.like("descripcion", "ADMIN%"));

		lst = (List<Rol>) mngrRol.search(restrictions);

		return lst;
	}
}
