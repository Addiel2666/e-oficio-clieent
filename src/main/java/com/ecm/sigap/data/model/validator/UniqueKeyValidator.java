/*
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.validator;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.ecm.sigap.data.service.EntityManager;

/**
 * Custom Validator creado para determinar que no existan registros repetidos en
 * la Base de Datos
 * 
 * @author Alejandro Guzman
 * @version 1.0 fecha: 21-Oct-2015
 * 
 *          Creacion de la clase
 *
 */
public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, Serializable>, EntityManagerAwareValidator {

	/** Log de suscesos. */
	private static final Logger LOGGER = LogManager.getLogger(UniqueKeyValidator.class);

	/** Entity Manager para ejecutar la consulta a la BD */
	private EntityManager<?> entityManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.model.validator.EntityManagerAwareValidator#
	 * setEntityManager(com.ecm.sigap.data.service.EntityManager)
	 */
	@Override
	public void setEntityManager(EntityManager<?> entityManager) {
		this.entityManager = entityManager;
	}

	private String[] columnNames;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.validation.ConstraintValidator#initialize(java.lang.annotation.
	 * Annotation)
	 */
	@Override
	public void initialize(UniqueKey constraintAnnotation) {

		LOGGER.debug("::>> Ejecutando el metodo initialize");
		this.columnNames = constraintAnnotation.columnNames();
		LOGGER.debug("::>> Columns Names: " + Arrays.toString(columnNames));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
	 * javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Serializable target, ConstraintValidatorContext context) {

		LOGGER.debug("::>> Ejecutando el metodo isValid");
		List<Criterion> restrictions = new ArrayList<Criterion>();

		try {
			LOGGER.debug("Target " + target);
			for (int i = 0; i < columnNames.length; i++) {

				String propertyName = columnNames[i];
				Object propertyValue = showProperties(target, propertyName);
				String propertyType = getPropertyType(target.getClass(), propertyName);
				LOGGER.debug("::>> Restriccion [Name=" + propertyName + "],[Value=" + propertyValue + "],[Type="
						+ propertyType + "]");

				// Validamos el tipo del atributo o propiedad y si es String se
				// valida ignorando el case sensitive
				if ("String".equals(propertyType)) {
					restrictions.add(Restrictions.eq(propertyName, propertyValue).ignoreCase());
				} else {
					restrictions.add(Restrictions.eq(propertyName, propertyValue));
				}
			}
			LOGGER.debug("::>> Ejecutando la consulta para validar si se esta violando al restriccion");
			List<?> resultSet = entityManager.search(restrictions, null);
			LOGGER.debug("::>> Cantidad de registros encontrados: " + resultSet.size());
			return resultSet.size() == 0;

		} catch (Exception e) {

			LOGGER.error("Ocurrio un error al momento de ejecutar la validacion de la clase "
					+ target.getClass().getName() + " con la siguiente descripcion: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Obtenemos el valor de un Atributo para un tipo de Objeto
	 * 
	 * @param bean
	 *            Tipo de Objeto
	 * @param property
	 *            Nombre del Atributo del Objeto del cual se quiere obtener el
	 *            valor
	 * @return Valor del Atributo
	 * @throws Exception
	 *             Error al momento de obtener el valor
	 */
	private Object showProperties(Object bean, String property) throws Exception {

		BeanInfo info = Introspector.getBeanInfo(bean.getClass(), Object.class);
		PropertyDescriptor[] props = info.getPropertyDescriptors();

		String propertyName = property;
		boolean isPropertyaObject = false;

		// Si el nombre del Atributo tiene un punto, obtenemos el valor del
		// objeto y el de su Atributo
		if (property.contains(".")) {
			isPropertyaObject = true;
			propertyName = property.substring(0, property.indexOf("."));
			property = property.substring(property.indexOf(".") + 1);
		}

		// Recorremos todos los atributos del objeto hasta obtener el que
		// necesitamos
		for (PropertyDescriptor pd : props) {

			String name = pd.getName();

			Method getter = pd.getReadMethod();

			if (name.equals(propertyName)) {

				Object value = getter.invoke(bean);

				if (null != value) {
					if (isPropertyaObject) {

						return showProperties(value, property);
					}
					return value;
				} else {

					throw new Exception("El valor de atributo a validar es nulo");
				}
			}
		}
		return null;
	}

	/**
	 * Obtiene el nombre del tipo de un atributo de objeto
	 * 
	 * @param targetClass
	 *            Objeto del que se va a extraer al atributo
	 * @param property
	 *            Nombre del a propiedad que se va a obtener el Tipo
	 * @return Nombre del tipo de un atributo
	 * @throws Exception
	 *             Cualquier error al momento de ejecutar el metodo
	 */
	private String getPropertyType(Class<?> targetClass, String property) throws Exception {

		BeanInfo info = Introspector.getBeanInfo(targetClass, Object.class);
		PropertyDescriptor[] props = info.getPropertyDescriptors();

		String propertyName = property;
		boolean isPropertyaObject = false;

		// Si el nombre del Atributo tiene un punto, obtenemos el valor del
		// objeto y el de su Atributo
		if (property.contains(".")) {
			isPropertyaObject = true;
			propertyName = property.substring(0, property.indexOf("."));
			property = property.substring(property.indexOf(".") + 1);
		}

		// Recorremos todos los atributos del objeto hasta obtener el que
		// necesitamos
		for (PropertyDescriptor pd : props) {

			String name = pd.getName();

			Method getter = pd.getReadMethod();

			if (name.equals(propertyName)) {

				Class<?> value = getter.getReturnType();

				if (isPropertyaObject) {

					return getPropertyType(value, property);
				}
				return value.getSimpleName();
			}
		}
		return null;
	}
}
