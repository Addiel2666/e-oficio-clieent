/**
 * 
 */
package com.ecm.sigap.data.model.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ecm.sigap.data.service.EntityManager;

/**
 * @author Alejandro Guzman
 *
 */
public class ConstraintValidatorFactoryImpl implements ConstraintValidatorFactory {

	/** Log de suscesos. */
	private static final Logger LOGGER = LogManager.getLogger(ConstraintValidatorFactoryImpl.class);

	private EntityManager<?> entityManagerFactory;

	public ConstraintValidatorFactoryImpl(EntityManager<?> entityManagerFactory) {
		LOGGER.debug("::>> Ejecutando el contructor de la clase");
		this.entityManagerFactory = entityManagerFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidatorFactory#getInstance(java.lang.Class)
	 */
	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {

		LOGGER.debug("::>> Ejecutando el metodo getInstance()");

		T instance = null;

		try {
			instance = key.newInstance();
		} catch (Exception e) {
			// could not instantiate class
			
		}

		if (EntityManagerAwareValidator.class.isAssignableFrom(key)) {
			EntityManagerAwareValidator validator = (EntityManagerAwareValidator) instance;
			validator.setEntityManager(entityManagerFactory);
		}

		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.validation.ConstraintValidatorFactory#releaseInstance(javax.validation
	 * .ConstraintValidator)
	 */
	@Override
	public void releaseInstance(ConstraintValidator<?, ?> arg0) {
		// TODO Auto-generated method stub

	}

}
