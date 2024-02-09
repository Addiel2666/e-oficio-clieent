/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.firma;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.cmisIntegracion.model.Version;
import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.impl.DocumentoAsuntoController;
import com.ecm.sigap.data.controller.impl.DocumentoRespuestaController;
import com.ecm.sigap.data.controller.impl.FirmaController;
import com.ecm.sigap.data.controller.impl.MailController;
import com.ecm.sigap.data.controller.impl.async.FirmarAsyncProcess;
import com.ecm.sigap.data.model.DocumentoAntefirmaAsunto;
import com.ecm.sigap.data.model.DocumentoAntefirmaKey;
import com.ecm.sigap.data.model.DocumentoAntefirmaRespuesta;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoFirma;
import com.ecm.sigap.data.model.util.TipoNotificacion;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * Metodos compartidos entre la clase {@link FirmaController} y
 * {@link FirmarAsyncProcess}
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
public abstract class FirmaCore extends CustomRestController {

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(FirmaCore.class);

	/**
	 * Referencia hacia el REST controller de {@link MailController}.
	 */
	@Autowired
	protected MailController mailController;

	/** Configuracion global de la acplicacion. */
	@Autowired
	protected Environment environment;

	/**
	 * Core process de aplicarFirma.
	 *
	 * @param documento the documento
	 * @param user      the user
	 * @return the map
	 * @throws DecoderException     the decoder exception
	 * @throws Exception            the exception
	 * @throws JsonParseException   the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> aplicarFirmaProcess(JSONObject documento, Usuario user)
			throws DecoderException, Exception, JsonParseException, JsonMappingException, IOException {
		File evidencia = null;
		try {
			String hashArchivo = documento.getString("HashArchivo");
			byte[] _hex_h = Hex.decodeHex(hashArchivo.toCharArray());
			hashArchivo = Base64.encodeBase64String(_hex_h);

			String firma = documento.getString("firma");
			byte[] _hex_f = Hex.decodeHex(firma.toCharArray());
			firma = Base64.encodeBase64String(_hex_f);
			List<String> firmaLst = new ArrayList<>();
			firmaLst.add(firma);

			TipoFirma tipoFirma = TipoFirma.fromString(documento.getString("tipoFirma"));
			List<String> tiposFirma = new ArrayList<>();
			tiposFirma.add(tipoFirma.getTipo());

			Integer idDocumento = documento.getInt("IdDocumento");
			List<String> idDocumentoLst = new ArrayList<>();
			idDocumentoLst.add(String.valueOf(idDocumento));

			List<String> emailLst = new ArrayList<>();
			emailLst.add(user.getEmail());

			List<Date> fechaFirmaLst = new ArrayList<>();
			fechaFirmaLst.add(new Date());

			String fileName = documento.getString("objectName");
			String objectId = (new String(documento.getString("objectId"))).toLowerCase();

			String objectIdServiceFirma = null;

			// - - - - - - - - - - - - - - - - -

			Map<String, Object> values = new HashMap<>();

			values.put(environment.getProperty("fieldIdentificador"), idDocumentoLst);
			values.put(environment.getProperty("fieldFirma"), firmaLst);
			values.put(environment.getProperty("fieldFechaFirma"), fechaFirmaLst.toArray());
			values.put(environment.getProperty("fieldFirmante"), emailLst);
			values.put(environment.getProperty("fieldTipoFirma"), tiposFirma);

			IEndpoint endpoint = EndpointDispatcher.getInstance();

			// Obtenemos el ACL que tenia antes de las operaciones
			List<String> aclNames = (List<String>) endpoint.getObjectProperty(objectId, "acl_name");

			String aclName = (aclNames == null || aclNames.isEmpty()) ? null : aclNames.get(0);

			// Damos permiso al Super User para que pueda hacer las operaciones
			// sobre el documento
			if (!endpoint.addPermisos(objectId, environment.getProperty("aclNameAplicaFirma"), null)) {
				throw new Exception("ERROR AGREGANDO PERMISOS");
			}

			endpoint.setProperties(objectId, values);

			values.clear();

			String fieldIsFirmado = environment.getProperty("fieldIsFirmado");

			try {
				values.put(fieldIsFirmado, new Boolean[] { Boolean.TRUE });
				endpoint.setProperties(objectId, values);
			} catch (Exception e) {
				log.error("ERROR AL MARCAR EL ARCHIVO COMO FIRMADO EN EL REPOSITORIO como repetible");
				log.error(e.getMessage());

				try {
					log.warn("tratando de marcar el documento como firmado en el reposiotrio como campo single...");
					values.put(fieldIsFirmado, Boolean.TRUE);
					endpoint.setProperties(objectId, values);
				} catch (Exception e1) {
					log.error("ERROR AL MARCAR EL ARCHIVO COMO FIRMADO EN EL REPOSITORIO como single");
					log.error(e1.getMessage());
				}
			}

			log.debug("INFO::: Nombre del Archivo a procesar firma" + fileName);
			// Valida si el documeto es veresionable.
			boolean isVersionable = isAnexoVersionable(fileName);

			// Obteniendo evidencia del documenti firmado

			try {
				evidencia = getEvidencia(idDocumento, tipoFirma, fileName, isVersionable);
				log.debug("evidencia de firma :: " + evidencia.getCanonicalPath());

			} catch (Exception e) {
				log.error("ERROR: OBTENIENDO EVIDENCIA DEL DOCUMENTO FIRMADO");
				throw e;
			}

			Integer idAsunto = documento.getInt("idAsunto");
			String tipoOperacion = documento.getString("tipo");

			Object object = documento.opt("isAntefirmaMejorada");
			Boolean isAntefirmaMejorada = object != null ? Boolean.parseBoolean(object.toString()) : false;

			if (isVersionable) {
				log.debug("INFO:: Es versionable el documento objectid: " + objectId + " nombre archivo: " + fileName);

				if (!endpoint.checkOut(objectId)) {
					log.error("ERROR:: Haciendo checkOut del objectId: " + objectId);
					throw new Exception("ERROR BLOQUEANDO DOCUMENTO PARA VERSIONAR");
				}

				StringBuilder new_fileName = new StringBuilder(fileName);

				if (!fileName.toUpperCase().endsWith(".PDF")) {
					int lastIndexOf = new_fileName.lastIndexOf(".");
					new_fileName.delete(lastIndexOf, new_fileName.length());
					new_fileName.append(".pdf");
				}
				String newObjectId = null;
				try {
					List<Map<String, String>> newDocumentVersion = endpoint.checkIn(objectId, Version.MAYOR,
							"Documento Firmado.", new_fileName.toString(), evidencia);

					newObjectId = newDocumentVersion.get(0).get("documentoId");
					objectIdServiceFirma = newObjectId;

					if (aclNames != null) {
						log.debug("Retornando el ACL anterior '" + aclName + "' al documento con id " + newObjectId);
						endpoint.setACLByDQL(newObjectId, aclName);
					}

				} catch (Exception e) {
					log.error("ERROR: VERSIONANDO EL DOCUMENTO");
					throw e;
				}
				log.debug("ObjectId anterior: " + objectId + "Nuevo ObjectId: " + newObjectId);

				TipoNotificacion tipoNotificacion = TipoFirma.PDF_ANTEFIRMA.equals(tipoFirma)
						? TipoNotificacion.RECDOCANTEFIR
						: TipoNotificacion.RECDOCFIR;

				if ("R".equalsIgnoreCase(tipoOperacion)) {

					Integer idRespuesta = documento.getInt("idRespuesta");

					////////////////////////////////////////////////////////////////////////////

					DocumentoRespuesta newDocActualizado;

					{

						LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

						params.put("V_TIPO", "RESPUESTA");
						params.put("V_ID", idRespuesta.toString());
						params.put("V_OBJECTID_OLD", objectId);
						params.put("V_OBJECTID_NEW", newObjectId);

						mngrDocsAsunto.createStoredProcedureCall("REEMPLAZAR_ID_FIRMADO", params);

						newDocActualizado = mngrDocsRespuesta.fetch(newObjectId);

					}

					////////////////////////////////////////////////////////////////////////////

					objectId = newObjectId;
					documento.put("newObjectIdR", newObjectId);

					// Enviar NOTIFICACION
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								if (StatusFirmaDocumento.FIRMADO.equals(newDocActualizado.getStatus())) {

									Integer idAreaNotificar = TipoAsunto.ASUNTO
											.equals(newDocActualizado.getAsuntoConsulta().getTipoAsunto())
													? newDocActualizado.getAsuntoConsulta().getIdArea()
													: newDocActualizado.getAsuntoConsulta().getIdAreaDestino();

									if (mailController.sendNotificacionDoctoFirmado(newDocActualizado, idAreaNotificar,
											tipoNotificacion)) {
										log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + tipoNotificacion
												+ " SATISFACTORIAMENTE PARA EL DOCUMENTO FIRMADO "
												+ newDocActualizado.getObjectId());
									} else {
										throw new Exception("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO");
									}
								}

							} catch (Exception e) {
								log.error("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
										+ TipoNotificacion.RECDOCFIR + " PARA EL DOCUMENTO FIRMADO "
										+ newDocActualizado.getObjectId() + " " + e.getLocalizedMessage());
							}
						}
					});
					t.setDaemon(true);
					t.start();
					// FIN ENVIAR NOTIFICACION

				} else if ("A".equalsIgnoreCase(tipoOperacion)) {

					{

						////////////////////////////////////////////////////////////////////////////

						DocumentoAsunto newDocActualizado;

						{

							LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

							params.put("V_TIPO", "ASUNTO");
							params.put("V_ID", idAsunto.toString());
							params.put("V_OBJECTID_OLD", objectId);
							params.put("V_OBJECTID_NEW", newObjectId);

							mngrDocsAsunto.createStoredProcedureCall("REEMPLAZAR_ID_FIRMADO", params);

							newDocActualizado = documentoAsuntoController.get(idAsunto.toString(), newObjectId)
									.getBody();

						}

						////////////////////////////////////////////////////////////////////////////

						objectId = newObjectId;
						documento.put("newObjectId", newObjectId);

						// Enviar NOTIFICACION
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									if (StatusFirmaDocumento.FIRMADO.equals(newDocActualizado.getStatus())) {

										Integer idAreaNotificar = TipoAsunto.ASUNTO.getValue()
												.equals(newDocActualizado.getAsuntoConsulta().getTipoAsunto())
														? newDocActualizado.getAsuntoConsulta().getIdArea()
														: newDocActualizado.getAsuntoConsulta().getIdAreaDestino();

										if (mailController.sendNotificacionDoctoFirmado(newDocActualizado,
												idAreaNotificar, tipoNotificacion)) {
											log.debug("SE HA ENVIADO LA NOTIFICACION DE TIPO " + tipoNotificacion
													+ " SATISFACTORIAMENTE PARA EL DOCUMENTO FIRMADO "
													+ newDocActualizado.getObjectId());
										} else {
											log.debug("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
													+ tipoNotificacion + " PARA EL DOCUMENTO FIRMADO "
													+ newDocActualizado.getObjectId());
										}
									}

								} catch (Exception e) {
									log.error("NO SE PUDO EFECTUAR EL ENVIO DE LA NOTIFICACION DE TIPO "
											+ tipoNotificacion + " PARA EL DOCUMENTO FIRMADO "
											+ newDocActualizado.getObjectId() + " " + e.getLocalizedMessage());
								}

							}
						});
						t.setDaemon(true);
						t.start();
						// FIN ENVIAR NOTIFICACION

					}

				}

				// ACTUALIZAR DOCUMENTOS SI ESTAN MARCADOS PARA ANTEFIRMA
				if (isAntefirmaMejorada) {

					String tipo = documento.getString("tipoAR");

					if ("A".equals(tipo)) {

						List<DocumentoAntefirmaAsunto> lstRefDoc = obtenerDocumentosAntefirma(documento, null);

						DocumentoAntefirmaAsunto newDam;

						for (DocumentoAntefirmaAsunto dam : lstRefDoc) {

							newDam = new DocumentoAntefirmaAsunto();

							newDam.setDocumentoAntefirmaKey(new DocumentoAntefirmaKey());

							newDam.getDocumentoAntefirmaKey()
									.setFirmante(new String(dam.getDocumentoAntefirmaKey().getFirmante()));
							newDam.getDocumentoAntefirmaKey()
									.setId(new Integer(dam.getDocumentoAntefirmaKey().getId()));
							newDam.getDocumentoAntefirmaKey()
									.setTipo(new String(dam.getDocumentoAntefirmaKey().getTipo()));
							newDam.getDocumentoAntefirmaKey()
									.setTipoFirmate(dam.getDocumentoAntefirmaKey().getTipoFirmante());

							// se le actualiza su objectId al nuevo.
							newDam.getDocumentoAntefirmaKey().setObjectId(objectId);

							// si es el del usuario es el que firmo
							if (user.getId().equalsIgnoreCase(dam.getDocumentoAntefirmaKey().getFirmante())) {
								newDam.setFirmado(true);
								newDam.setTipoFirma(tipoFirma);
								newDam.setFechaFirma(new Date());
								mngrDocumentoAntefirmaAsunto.save(newDam);
								mngrDocumentoAntefirmaAsunto.delete(dam);
							} else {
								newDam.setFirmado(dam.getFirmado());
								newDam.setTipoFirma(dam.getTipoFirma());
								newDam.setFechaFirma(dam.getFechaFirma());
								mngrDocumentoAntefirmaAsunto.save(newDam);
								mngrDocumentoAntefirmaAsunto.delete(dam);
							}

						}
					} else if ("R".equals(tipo)) {

						List<DocumentoAntefirmaRespuesta> lstRefDoc = obtenerDocumentosAntefirmaRespuesta(documento,
								null);

						DocumentoAntefirmaRespuesta newDam;

						for (DocumentoAntefirmaRespuesta dam : lstRefDoc) {

							newDam = new DocumentoAntefirmaRespuesta();

							newDam.setDocumentoAntefirmaKey(new DocumentoAntefirmaKey());

							newDam.getDocumentoAntefirmaKey()
									.setFirmante(new String(dam.getDocumentoAntefirmaKey().getFirmante()));
							newDam.getDocumentoAntefirmaKey()
									.setId(new Integer(dam.getDocumentoAntefirmaKey().getId()));
							newDam.getDocumentoAntefirmaKey()
									.setTipo(new String(dam.getDocumentoAntefirmaKey().getTipo()));
							newDam.getDocumentoAntefirmaKey()
									.setTipoFirmate(dam.getDocumentoAntefirmaKey().getTipoFirmante());

							// se le actualiza su objectId al nuevo.
							newDam.getDocumentoAntefirmaKey().setObjectId(objectId);

							// si es el del usuario es el que firmo
							if (user.getId().equalsIgnoreCase(dam.getDocumentoAntefirmaKey().getFirmante())) {
								newDam.setFirmado(true);
								newDam.setTipoFirma(tipoFirma);
								newDam.setFechaFirma(new Date());
								mngrDocumentoAntefirmaRespuesta.save(newDam);
								mngrDocumentoAntefirmaRespuesta.delete(dam);
							} else {
								newDam.setFirmado(dam.getFirmado());
								newDam.setTipoFirma(dam.getTipoFirma());
								newDam.setFechaFirma(dam.getFechaFirma());
								mngrDocumentoAntefirmaRespuesta.save(newDam);
								mngrDocumentoAntefirmaRespuesta.delete(dam);
							}

						}

					} else {

						log.warn("No type! no refernces updated.");

					}

				} else {

					////////////////////////////////////////////////////////////////////////

					LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

					params.put("V_OBJECTID_OLD", documento.getString("objectId"));
					params.put("V_OBJECTID_NEW", objectId);

					mngrDocsAsunto.createStoredProcedureCall("ACTUALIZAR_ID_ANTEFIRMA", params);

					////////////////////////////////////////////////////////////////////////

				}
				// - - - - - - - - - - - - - - - - -
				// Actualizar campo r_object_id en tabla "documentosfirmados" firmatsp

				try {
					// Map<String, Object> responseEntify =
					firmaEndPoint.setRObjectOnFirma(idDocumento, objectIdServiceFirma);
					log.info(" :: SE ACTUALIZO EL ID_REPO EN FIRMA");
				} catch (Exception e) {
					log.error("ERROR: NO SE PUDO ACTUALIZAR EL CAMPO ID_REPO EN FIRMA " + e);
				}

			} else {

				log.error("INFO: El objectId a firmar no es versionable: " + objectId);
				// se agrega la evidencia de firma como rendicion.
				String rendObjectId = endpoint.addRendition(objectId, evidencia);

				log.info("Rendicion de Firma ::" + rendObjectId);

				// se marca como firmado.
				if ("R".equalsIgnoreCase(tipoOperacion))
					marcarDocumentoRespuestaProcess(objectId, StatusFirmaDocumento.FIRMADO.toString());
				else if ("A".equalsIgnoreCase(tipoOperacion))
					marcarDocumentoAsuntoProcess(objectId, StatusFirmaDocumento.FIRMADO.toString(), idAsunto);
				log.error("INFO: se actualizó con exito el objectId " + objectId + " en el asuno: " + idAsunto);
			}

			// -----------------------------------,

			Map<String, Object> result = new HashMap<>();

			result.put("result", "ok");
			return result;
		} finally {
			if (evidencia != null && evidencia.exists()) {
				evidencia.delete();
			}
		}

	}

	/**
	 * Core process de validarFirma.
	 *
	 * @param body_ the body
	 * @param user  the user
	 * @return the map
	 * @throws DecoderException        the decoder exception
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException             Signals that an I/O exception has occurred.
	 */
	public Map<String, Object> validarFirmaProcess(JSONObject body_, Usuario user, String algoritmoFirma)
			throws DecoderException, ClientProtocolException, IOException {

		String certificadoB64 = body_.getString("certificadoB64");

		// para certificados pem
		certificadoB64 = certificadoB64.replace("-----BEGIN CERTIFICATE-----", "")
				.replace("-----END CERTIFICATE-----", "").trim();

		String firmaHex = body_.getString("firmaHex");

		byte[] decodedHex = Hex.decodeHex(firmaHex.toCharArray());

		String firmaB64 = Base64.encodeBase64String(decodedHex);

		String fileName = body_.getString("objectName");

		TipoFirma tipoFirma_ = TipoFirma.fromString(body_.getString("tipoFirma"));

		SignContentType signContentType = isAnexoVersionable(fileName) ? SignContentType.PDF : SignContentType.OFICIO;

		Map<String, Object> firmaValida = firmaEndPoint.validateSign(//
				body_.getInt("id"), //
				user.getEmail(), //
				certificadoB64, //
				firmaB64, //
				tipoFirma_, //
				signContentType, algoritmoFirma);

		log.debug("firmaValida >> " + firmaValida.get("isValid"));
		return firmaValida;
	}

	/**
	 * Obtiene la evidencia de firma, si es un archivo versionable se devuelve el
	 * PDF/A, sino se devuelve un PKCS7.
	 *
	 * @param idDocumento   the id documento
	 * @param tipoFirma     the tipo firma
	 * @param fileName      the file name
	 * @param isVersionable the is versionable
	 * @return the evidencia
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private File getEvidencia(Integer idDocumento, TipoFirma tipoFirma, String fileName, boolean isVersionable)
			throws IOException {

		Map<String, Object> firmaObject = firmaEndPoint.getFirma(new Long(idDocumento), tipoFirma);
		String firma = (String) firmaObject.get("Firma");

		File evidencia = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX + "FIRMA_",
				"_" + fileName + (isVersionable ? "_firmado.pdf" : "_pkcs7.p7m"));

		evidencia.deleteOnExit();

		byte[] firmaDecoded = Base64.decodeBase64(firma);

		FileUtils.writeByteArrayToFile(evidencia, firmaDecoded);

		return evidencia;
	}

	/**
	 * Valida por la extencion del archivo si es versionable o si se agregara la
	 * firma como una rendicion.
	 *
	 * @param fileName the file name
	 * @return the boolean
	 */
	public Boolean isAnexoVersionable(String fileName) {

		String exts[] = environment.getProperty("pdfVersionType").split(",");

		if (fileName != null) {
			fileName = fileName.substring(fileName.lastIndexOf('.') + 1);
			for (String ext : exts) {
				if (ext.equalsIgnoreCase(fileName)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Referencia hacia el REST controller de {@link DocumentoAsuntoController}.
	 */
	@Autowired
	private DocumentoAsuntoController documentoAsuntoController;

	/**
	 * Referencia hacia el REST controller de {@link DocumentoAsuntoController}.
	 */
	@Autowired
	private DocumentoRespuestaController documentoRespuestaController;

	/**
	 * 
	 * @param objectId
	 * @param status
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected DocumentoAsunto marcarDocumentoAsuntoProcess(String objectId, String status, Integer id)
			throws Exception {

		try {

			LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("V_TIPO", "ASUNTO");
			params.put("V_OBJECTID", objectId);
			params.put("V_ID", id.toString());

			boolean isFirmado = isFirmado(objectId);
			String aux = "";

			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("documentoAntefirmaKey.id", id));
			restrictions.add(Restrictions.eq("documentoAntefirmaKey.objectId", objectId));
			restrictions.add(Restrictions.eq("documentoAntefirmaKey.tipo", "A"));
			restrictions.add(Restrictions.isNull("tipoFirma"));
			List<DocumentoAntefirmaAsunto> lstRefDoc = (List<DocumentoAntefirmaAsunto>) mngrDocumentoAntefirmaAsunto
					.search(restrictions);
			DocumentoAsunto documentoAux = documentoAsuntoController.get(id.toString(), objectId).getBody();
			// log.warn("::: documento antes"+documentoAux.getStatus());
			if (documentoAux.getStatus() != null)
				aux = documentoAux.getStatus().toString();

			if ((!isFirmado && lstRefDoc.size() > 0
					&& StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status))
					|| (!isFirmado && (aux.equalsIgnoreCase("P") || aux.equalsIgnoreCase("H"))
							&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))) {
				status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_Y_PARA_FIRMA.getTipo();
			} else if (!isFirmado && lstRefDoc.size() > 0 && StringUtils.isBlank(status)) {
				status = StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo();
			} else if (!isFirmado && aux.equalsIgnoreCase("H")
					&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = StatusFirmaDocumento.PARA_FIRMA.getTipo();
			} else if (!isFirmado && aux.equalsIgnoreCase("E")
					&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = "";
			} else if ((isFirmado && aux.equalsIgnoreCase("F") && lstRefDoc.size() > 0
					&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))
					|| (isFirmado && aux.equalsIgnoreCase("K") && lstRefDoc.size() > 0
							&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))) {
				status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_Y_FIRMADO.getTipo();
			} else if (isFirmado && aux.equalsIgnoreCase("K")
					&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = StatusFirmaDocumento.FIRMADO.getTipo();
			} else if (!isFirmado && lstRefDoc.size() > 0 && aux.equalsIgnoreCase("E")
					&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo();
			} else if (!isFirmado && lstRefDoc.size() < 1 && aux.equalsIgnoreCase("E")
					&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = "";
			} else if ((isFirmado && (aux.equalsIgnoreCase("G") || aux.equalsIgnoreCase("J"))
					&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))
					|| (isFirmado && aux.equalsIgnoreCase("K")
							&& StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status))) {
				status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_PARA_FIRMA_Y_FIRMADO.getTipo();
			} else if (isFirmado && aux.equalsIgnoreCase("J")
					&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = StatusFirmaDocumento.PARA_FIRMA_Y_FIRMADO.getTipo();
			} else if (isFirmado && aux.equalsIgnoreCase("J") && StringUtils.isBlank(status)) {
				status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_Y_FIRMADO.getTipo();
			} else if (isFirmado && lstRefDoc.size() > 0 && aux.equalsIgnoreCase("F")
					&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
				status = StatusFirmaDocumento.FIRMADO.getTipo();
			} else if (isFirmado && StatusFirmaDocumento.BLOQUEADO.getTipo().equalsIgnoreCase(status)
					&& aux.equalsIgnoreCase("F")) {
				status = StatusFirmaDocumento.FIRMADO_Y_BLOQUEADO.getTipo();
			} else if (isFirmado && aux.equalsIgnoreCase("L")
					&& StatusFirmaDocumento.AUX_QUITA_BLOQUEO.getTipo().equalsIgnoreCase(status)) {
				status = StatusFirmaDocumento.FIRMADO.getTipo();
			} else if (!isFirmado && aux.equalsIgnoreCase("B")
					&& StatusFirmaDocumento.AUX_QUITA_BLOQUEO.getTipo().equalsIgnoreCase(status)) {
				status = "";
			}

			if (isFirmado && StringUtils.isBlank(status))
				status = StatusFirmaDocumento.FIRMADO.getTipo();
			else if (isFirmado && StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status))
				status = StatusFirmaDocumento.PARA_FIRMA_Y_FIRMADO.getTipo();

			if (StringUtils.isBlank(status))
				mngrDocsAsunto.createStoredProcedureCall("DESMARCAR_DOC_FIRMA", params);
			else {
				params.put("V_STATUS", status);
				mngrDocsAsunto.createStoredProcedureCall("CAMBIA_STATUS_DOC_FIRMA", params);
			}

			DocumentoAsunto documento = documentoAsuntoController.get(id.toString(), objectId).getBody();

			if (documento == null) {
				throw new BadRequestException();
			}

			return documento;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * 
	 * @param objectId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	protected DocumentoRespuesta marcarDocumentoRespuestaProcess(String objectId, String status) throws Exception {

		try {

			LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("V_TIPO", "RESPUESTA");
			params.put("V_OBJECTID", objectId);
			params.put("V_ID", "1");

			// -----
			boolean isFirmado = isFirmado(objectId);
			String aux = "";

			try {
				List<Criterion> restrictions = new ArrayList<>();
				restrictions.add(Restrictions.eq("documentoAntefirmaKey.objectId", objectId));
				restrictions.add(Restrictions.eq("documentoAntefirmaKey.tipo", "R"));
				restrictions.add(Restrictions.isNull("tipoFirma"));
				List<DocumentoAntefirmaRespuesta> lstRefDoc = (List<DocumentoAntefirmaRespuesta>) mngrDocumentoAntefirmaRespuesta
						.search(restrictions);

				DocumentoRespuesta resp = new DocumentoRespuesta();
				if (lstRefDoc.size() > 0) {
					resp.setIdRespuesta(lstRefDoc.get(0).getRespuestaConsulta().getIdRespuesta());
					resp.setIdAsunto(lstRefDoc.get(0).getRespuestaConsulta().getIdAsunto());
					resp.setObjectId(objectId);
				}

				ResponseEntity<List<?>> dd = documentoRespuestaController.search(resp);
				DocumentoRespuesta doctoAux = (DocumentoRespuesta) dd.getBody().get(0);
				if (doctoAux.getStatus() != null)
					aux = doctoAux.getStatus().toString();

				if ((!isFirmado && lstRefDoc.size() > 0
						&& StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status))
						|| (!isFirmado && (aux.equalsIgnoreCase("P") || aux.equalsIgnoreCase("H"))
								&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))) {
					status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_Y_PARA_FIRMA.getTipo();

				} else if (!isFirmado && lstRefDoc.size() > 0 && StringUtils.isBlank(status)) {
					status = StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo();

				} else if (!isFirmado && aux.equalsIgnoreCase("H")
						&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = StatusFirmaDocumento.PARA_FIRMA.getTipo();

				} else if (!isFirmado && aux.equalsIgnoreCase("E")
						&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = "";

				} else if ((isFirmado && aux.equalsIgnoreCase("F") && lstRefDoc.size() > 0
						&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))
						|| (isFirmado && aux.equalsIgnoreCase("K") && lstRefDoc.size() > 0
								&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))) {
					status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_Y_FIRMADO.getTipo();

				} else if (isFirmado && aux.equalsIgnoreCase("K")
						&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = StatusFirmaDocumento.FIRMADO.getTipo();

				} else if (!isFirmado && lstRefDoc.size() > 0 && aux.equalsIgnoreCase("E")
						&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo();

				} else if (!isFirmado && lstRefDoc.size() < 1 && aux.equalsIgnoreCase("E")
						&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = "";

				} else if ((isFirmado && (aux.equalsIgnoreCase("G") || aux.equalsIgnoreCase("J"))
						&& StatusFirmaDocumento.ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status))
						|| (isFirmado && aux.equalsIgnoreCase("K")
								&& StatusFirmaDocumento.PARA_FIRMA.getTipo().equalsIgnoreCase(status))) {
					status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_PARA_FIRMA_Y_FIRMADO.getTipo();

				} else if (isFirmado && aux.equalsIgnoreCase("J")
						&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = StatusFirmaDocumento.PARA_FIRMA_Y_FIRMADO.getTipo();

				} else if (isFirmado && aux.equalsIgnoreCase("J") && StringUtils.isBlank(status)) {
					status = StatusFirmaDocumento.ENVIO_ANTEFIRMA_Y_FIRMADO.getTipo();

				} else if (isFirmado && lstRefDoc.size() > 0 && aux.equalsIgnoreCase("F")
						&& StatusFirmaDocumento.AUX_QUITA_ENVIO_ANTEFIRMA.getTipo().equalsIgnoreCase(status)) {
					status = StatusFirmaDocumento.FIRMADO.getTipo();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			// -----

			if (isFirmado && StringUtils.isBlank(status))
				status = StatusFirmaDocumento.FIRMADO.getTipo();

			if (StringUtils.isBlank(status))
				mngrDocsAsunto.createStoredProcedureCall("DESMARCAR_DOC_FIRMA", params);
			else {
				params.put("V_STATUS", status);
				mngrDocsAsunto.createStoredProcedureCall("CAMBIA_STATUS_DOC_FIRMA", params);
			}

			DocumentoRespuesta documento = mngrDocsRespuesta.fetch(objectId);

			if (documento == null) {

				throw new BadRequestException();
			}

			return documento;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtener documentos antefirma.
	 *
	 * @param documento the documento
	 * @return the list
	 */
	private List<DocumentoAntefirmaAsunto> obtenerDocumentosAntefirma(JSONObject documento, Boolean firmado) {
		List<Criterion> restrictions = new ArrayList<>();

		// todas las referencias de ese objeto
		restrictions.add(Restrictions.eq("documentoAntefirmaKey.objectId", documento.getString("objectId")));

		if (firmado != null) {
			// todas las referencias de ese objeto que no estan firmadas
			restrictions.add(Restrictions.eq("firmado", firmado));
		}

		@SuppressWarnings("unchecked")
		List<DocumentoAntefirmaAsunto> lstRefDoc = (List<DocumentoAntefirmaAsunto>) mngrDocumentoAntefirmaAsunto
				.search(restrictions);

		return lstRefDoc;
	}

	/**
	 * 
	 * @param documento
	 * @param firmado
	 * @return
	 */
	private List<DocumentoAntefirmaRespuesta> obtenerDocumentosAntefirmaRespuesta(JSONObject documento,
			Boolean firmado) {
		List<Criterion> restrictions = new ArrayList<>();

		// todas las referencias de ese objeto
		restrictions.add(Restrictions.eq("documentoAntefirmaKey.objectId", documento.getString("objectId")));

		if (firmado != null) {
			// todas las referencias de ese objeto que no estan firmadas
			restrictions.add(Restrictions.eq("firmado", firmado));
		}

		@SuppressWarnings("unchecked")
		List<DocumentoAntefirmaRespuesta> lstRefDoc = (List<DocumentoAntefirmaRespuesta>) mngrDocumentoAntefirmaRespuesta
				.search(restrictions);

		return lstRefDoc;
	}

	/**
	 * Checks if is firmado.
	 *
	 * @param objectId the object id
	 * @return true, if is firmado
	 * @throws Exception the exception
	 */
	public boolean isFirmado(String objectId) throws Exception {

		IEndpoint endpoint = EndpointDispatcher.getInstance();

		String field_ = environment.getProperty("fieldIsFirmado");

		log.debug(" >> " + objectId + " field_is_signed >> " + field_);

		Object prop = endpoint.getObjectProperty(objectId, field_);

		log.debug(" >> " + objectId + " field_is_signed >> " + prop);

		Boolean is_signed = false;

		if (prop instanceof Collection) {

			List<?> objectProperty = (List<?>) prop;

			if (!objectProperty.isEmpty()) {
				Object tmp = objectProperty.get(0);
				is_signed = (tmp == null ? false
						: (Boolean.valueOf(tmp.toString()) || "T".equalsIgnoreCase(tmp.toString())));
			}

		} else if (prop instanceof Boolean) {

			if (prop != null)
				is_signed = Boolean.valueOf(prop.toString()) || "T".equalsIgnoreCase(prop.toString());

		}

		log.debug(" >> " + objectId + " is_signed ?? " + is_signed);

		return is_signed;

	}
}
