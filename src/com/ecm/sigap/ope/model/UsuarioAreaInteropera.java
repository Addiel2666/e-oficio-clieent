/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.ope.model;

/**
 * @author Samuel Garcia
 *
 */
public class UsuarioAreaInteropera {
	
	private String Nombre;
	
	private String Titulo;
	
	private String Puesto;
	
	private String CorreoElectronico;

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return Nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		Nombre = nombre;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return Titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		Titulo = titulo;
	}

	/**
	 * @return the puesto
	 */
	public String getPuesto() {
		return Puesto;
	}

	/**
	 * @param puesto the puesto to set
	 */
	public void setPuesto(String puesto) {
		Puesto = puesto;
	}

	/**
	 * @return the correoElectronico
	 */
	public String getCorreoElectronico() {
		return CorreoElectronico;
	}

	/**
	 * @param correoElectronico the correoElectronico to set
	 */
	public void setCorreoElectronico(String correoElectronico) {
		CorreoElectronico = correoElectronico;
	}	
	
}
