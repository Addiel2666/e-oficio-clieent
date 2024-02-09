/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FolioPSClave;
import com.ecm.sigap.data.service.ManagerImpl;

/**
 * Manejador en base de datos de objetos {@link FolioPSClave}.
 *
 * @author Adan Quintero
 * @version 1.0
 *
 */
@Service("foliopsClaveService")
public class FoliopsClaveManagerImpl extends ManagerImpl<FolioPSClave> {

    /** Interfaz a base de datos. */
    @Autowired
    @Qualifier("foliopsClaveDao")
    @Override
    public void setDao(EntityDAO<FolioPSClave> dao) {
        super.setDao(dao);
    }
}