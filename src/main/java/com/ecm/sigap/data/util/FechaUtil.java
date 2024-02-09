/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.core.env.Environment;

/**
 * The Class FechaUtil. Manejador para fechas
 * 
 * @author Gustavo Vielma
 * @version 1.0
 *
 */
public class FechaUtil {

	//
	/**
	 * Gets the fecha actual. Metodo usado para obtener la fecha actual
	 *
	 * @return retorna un java.util.Date con la fecha actual
	 */
	public static Date getFechaActual() {

		return new Date();
	}

	/**
	 * String to date. Devuele un java.util.Date desde un String en formato
	 * dd/MM/yyyy
	 *
	 * @param fecha
	 *            a convertir a formato date
	 * @return the date
	 * @throws ParseException 
	 */
	public static synchronized Date stringToDate(String fecha) throws ParseException {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaEnviar = null;
		try {
			fechaEnviar = formatoDelTexto.parse(fecha);
			return fechaEnviar;
		} catch (ParseException e) {
			
			throw e;
		}
	}
	
	//Metodo para asignarle una zona horaria y un formato a las fechas
	public static String getDateFormat(Date fecha, Environment environment) {
		String timezoneId = environment.getProperty("zona.horaria");
		TimeZone tz = TimeZone.getTimeZone(timezoneId);
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        fmt.setTimeZone(tz);
        
        String fechaConZona = fmt.format(fecha);
        
		return fechaConZona;
	}

}
