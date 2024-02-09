package com.ecm.sigap.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecm.sigap.data.dao.EntityDAO;
import com.ecm.sigap.data.model.FirmanteActivoSN;
import com.ecm.sigap.data.service.ManagerImpl;

@Service("firmanteActivoSNService")
public class FirmanteActivoSNManagerImpl extends ManagerImpl<FirmanteActivoSN> {

	@Autowired
	@Qualifier("firmanteActivoSNDao")
	protected void setDao(EntityDAO<FirmanteActivoSN> dao) {
		super.setDao(dao);
	}
}
