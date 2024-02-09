/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.notification.NotificationFirmaModel;
import com.ecm.sigap.data.model.util.StatusFirmaDocumento;
import com.ecm.sigap.data.model.util.TipoNotificacion;
import com.ecm.sigap.firma.FirmaCore;
import com.ecm.sigap.util.notification.NotificationService;

/**
 *
 * @author alfredo morales
 * @version 1.0
 *
 */
@Component
public class FirmarAsyncProcess extends FirmaCore {

	@Autowired
	private NotificationService notificationService;

	/**
	 *
	 * @param body
	 * @param user
	 */
	@Async
	public synchronized void process(Map<String, Object> body, Usuario user, String algoritmoFirma) {

		try {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> paraFirmar = (List<Map<String, String>>) body.get("documentos");

			List<JSONObject> listExito = new ArrayList<>();
			List<JSONObject> listFail = new ArrayList<>();

			for (Map<String, String> map : paraFirmar) {

				JSONObject documento = new JSONObject(map);

				try {

					documento.put("firmaHex", documento.getString("firma"));

					Map<String, Object> validado = validarFirmaProcess(documento, user, algoritmoFirma);

					documento.put("isValid", validado.get("isValid"));
					documento.put("HashArchivo", documento.get("HashArchivoHex"));

					Map<String, Object> firmado = aplicarFirmaProcess(documento, user);

					documento.put("result", firmado.get("result"));

					listExito.add(documento);
					notificationService.sendFinishFirma(documento.getString("IdDocumento"),
							NotificationFirmaModel.buildSuccess(documento.getString("objectName")));
				} catch (Exception e) {

					documento.put("failCause", e.getMessage());
					notificationService.sendFinishFirma(documento.getString("IdDocumento"),
							NotificationFirmaModel.buildError(documento.getString("objectName")));
					listFail.add(documento);

				}

			}

			for (JSONObject documento : listFail) {

				try {

					// remove firmando state
					String objectId = documento.getString("objectId");
					Integer idAsunto = documento.getInt("idAsunto");
					String tipoOperacion = documento.getString("tipo");

					if ("R".equalsIgnoreCase(tipoOperacion))
						marcarDocumentoRespuestaProcess(objectId, //
								StatusFirmaDocumento.PARA_FIRMA.toString());

					else if ("A".equalsIgnoreCase(tipoOperacion))
						marcarDocumentoAsuntoProcess(objectId, //
								StatusFirmaDocumento.PARA_FIRMA.toString(), idAsunto);

					// send fail mail

					TipoNotificacion notificacion = TipoNotificacion.FIRMA_FALLIDA;

					mailController.sendNotificacionSigap(//
							(Object) documento, //
							user, //
							notificacion);

				} catch (Exception e) {
					
				}

			}

		} catch (Exception e) {
			
		}

	}

}
