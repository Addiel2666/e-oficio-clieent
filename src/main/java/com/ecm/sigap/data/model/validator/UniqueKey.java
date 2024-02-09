/*
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Custom Validator para validar que una combinacion de campos no se repita
 * 
 * @author Alejandro Guzman
 * @version 1.0
 * 
 *          Creacion de la clase
 * 
 *          Ejemplos de como se debe de declarar la restriccion
 * 
 *          <li>@UniqueKey(columnNames={"userName" )</li> 
 *          <li>@UniqueKey(columnNames={"userName", "emailId"}) --> composite unique key</li>
 *          <li>@UniqueKey.List(value ={@UniqueKey(columnNames="userName" }),
 * 			    @UniqueKey(columnNames = { "emailId" })}) // more than one unique keys </li>
 *
 */
@Constraint(validatedBy = { UniqueKeyValidator.class })
@Target({ ElementType.TYPE })
@Retention(RUNTIME)
public @interface UniqueKey {

	/** Lista del nombre de las columnas */
	String[] columnNames();

	String message() default "Se violo la regla de registro unico";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ ElementType.TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		UniqueKey[] value();
	}
}