/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.util;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.criterion.MatchMode;

/**
 * 
 * Clase para el manejo de caracteres de Escape en los String.
 * 
 * @author Adaulfo Herrera
 * @version 1.0
 * 
 */

public class EscapedLikeRestrictions {

	/**
	 * Default constructor.
	 */
	private EscapedLikeRestrictions() {
		super();
	}

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @param matchMode
	 * @return
	 */
	public static Criterion like(String propertyName, String value, MatchMode matchMode) {
		return like(propertyName, value, matchMode, false);
	}

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @param matchMode
	 * @return
	 */
	public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
		return like(propertyName, value, matchMode, true);
	}

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @param matchMode
	 * @param ignoreCase
	 * @return
	 */
	private static Criterion like(String propertyName, String value, MatchMode matchMode, boolean ignoreCase) {

		return new LikeExpression(propertyName, escape(value), matchMode, '!', ignoreCase) {

			@Override
			public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
				String sqlString = super.toSqlString(criteria, criteriaQuery);

				sqlString = "MY_UNACCENT(" + sqlString;

				sqlString = sqlString.replaceFirst(" ilike", ") ilike");

				sqlString = sqlString.replaceFirst(" like", ") like");

				sqlString = sqlString.replaceFirst("\\?", "MY_UNACCENT(?)");

				return sqlString;
			}

			/** */
			private static final long serialVersionUID = 5420142530649140051L;
		};
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private static String escape(String value) {
		return value//

				.replace("!", "!!")//
				.replace("%", "!%")//
				.replace("_", "!_")//

		;
	}
}
