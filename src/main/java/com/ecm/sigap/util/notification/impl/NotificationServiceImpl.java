/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.util.notification.impl;

import com.ecm.sigap.data.model.notification.NotificationFirmaModel;
import com.ecm.sigap.util.notification.NotificationService;
import org.springframework.stereotype.Service;

/**
 * @author Angel Colina
 * @version 1.0
 */
@Service
public class NotificationServiceImpl extends AbstractNotificationImpl implements NotificationService {


    @Override
    public void send(String destino, Object data) {
        connect().send(destino, data);
    }

    @Override
    public void sendFinishFirma(String idDocuemnto, NotificationFirmaModel data) {
        this.send(String.format("/ws/firma/%s", idDocuemnto), data);
    }
}
