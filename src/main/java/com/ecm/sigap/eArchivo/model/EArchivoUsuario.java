package com.ecm.sigap.eArchivo.model;

// TODO: Auto-generated Javadoc
/**
 * The Class EArchivoUsuario.
 */
public class EArchivoUsuario {

	/** The id usuario. */
	private String idUsuario;
	
	/** The nombres. */
	private String nombres;
	
	/** The apellido materno. */
	private String apellidoMaterno;
	
	/** The apellido paterno. */
	private String apellidoPaterno;

	/**
	 * Gets the id usuario.
	 *
	 * @return the id usuario
	 */
	public String getIdUsuario() {
		return idUsuario;
	}

	/**
	 * Sets the id usuario.
	 *
	 * @param idUsuario the new id usuario
	 */
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * Gets the nombres.
	 *
	 * @return the nombres
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Sets the nombres.
	 *
	 * @param nombres the new nombres
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Gets the apellido materno.
	 *
	 * @return the apellido materno
	 */
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	/**
	 * Sets the apellido materno.
	 *
	 * @param apellidoMaterno the new apellido materno
	 */
	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	/**
	 * Gets the apellido paterno.
	 *
	 * @return the apellido paterno
	 */
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	/**
	 * Sets the apellido paterno.
	 *
	 * @param apellidoPaterno the new apellido paterno
	 */
	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "EArchivoUsuario [idUsuario=" + idUsuario + ", nombres=" + nombres + ", apellidoMaterno="
				+ apellidoMaterno + ", apellidoPaterno=" + apellidoPaterno + "]";
	}
	
	
}
