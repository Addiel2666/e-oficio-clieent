/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.util.notification;

import com.ecm.sigap.data.model.notification.NotificationFirmaModel;

/**
 * @author Angel Colina
 * @version 1.0
 */
public interface NotificationService {
    void send(String destino, Object data);

    void sendFinishFirma(String idDocuemnto, NotificationFirmaModel data);
}
