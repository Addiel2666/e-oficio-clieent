/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.notification;

import java.io.Serializable;

/**
 * @author Angel Colina
 * @version 1.0
 */
public class NotificationFirmaModel implements Serializable {
    private String docName;
    private boolean firm;

    public NotificationFirmaModel() {
    }

    public NotificationFirmaModel(String docName, boolean firm) {
        this.docName = docName;
        this.firm = firm;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public boolean isFirm() {
        return firm;
    }

    public void setFirm(boolean firm) {
        this.firm = firm;
    }

    public static NotificationFirmaModel buildSuccess(String docName) {
        return new NotificationFirmaModel(docName, true);
    }

    public static NotificationFirmaModel buildError(String docName) {
        return new NotificationFirmaModel(docName, false);
    }
}
