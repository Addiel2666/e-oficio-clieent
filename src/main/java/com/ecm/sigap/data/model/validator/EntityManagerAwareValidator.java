package com.ecm.sigap.data.model.validator;

import com.ecm.sigap.data.service.EntityManager;



public interface EntityManagerAwareValidator {
	
	void setEntityManager(EntityManager<?> entityManager); 

}
