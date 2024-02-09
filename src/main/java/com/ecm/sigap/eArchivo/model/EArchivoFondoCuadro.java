package com.ecm.sigap.eArchivo.model;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class EArchivoFondoCuadro.
 */
public class EArchivoFondoCuadro {
	
	/** The id fondo. */
	private Integer idFondo;
	
	/** The descripcion. */
	private String descripcion;
	
	/** The clave. */
	private String clave;
	
	/** The usuario registra. */
	private String usuarioRegistra;
	
	/** The fecha registra. */
	private Date fechaRegistra;
	
	/** The activo. */
	private Boolean activo;
	
	/**
	 * Gets the id fondo.
	 *
	 * @return the id fondo
	 */
	public Integer getIdFondo() {
		return idFondo;
	}
	
	/**
	 * Sets the id fondo.
	 *
	 * @param idFondo the new id fondo
	 */
	public void setIdFondo(Integer idFondo) {
		this.idFondo = idFondo;
	}
	
	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}
	
	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion the new descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	/**
	 * Gets the clave.
	 *
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}
	
	/**
	 * Sets the clave.
	 *
	 * @param clave the new clave
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	/**
	 * Gets the usuario registra.
	 *
	 * @return the usuario registra
	 */
	public String getUsuarioRegistra() {
		return usuarioRegistra;
	}
	
	/**
	 * Sets the usuario registra.
	 *
	 * @param usuarioRegistra the new usuario registra
	 */
	public void setUsuarioRegistra(String usuarioRegistra) {
		this.usuarioRegistra = usuarioRegistra;
	}
	
	/**
	 * Gets the fecha registra.
	 *
	 * @return the fecha registra
	 */
	public Date getFechaRegistra() {
		return fechaRegistra;
	}
	
	/**
	 * Sets the fecha registra.
	 *
	 * @param fechaRegistra the new fecha registra
	 */
	public void setFechaRegistra(Date fechaRegistra) {
		this.fechaRegistra = fechaRegistra;
	}
	
	/**
	 * Gets the activo.
	 *
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}
	
	/**
	 * Sets the activo.
	 *
	 * @param activo the new activo
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "EArchivoFondoCuadro [idFondo=" + idFondo + ", descripcion=" + descripcion + ", clave=" + clave
				+ ", usuarioRegistra=" + usuarioRegistra + ", fechaRegistra=" + fechaRegistra + ", activo=" + activo
				+ "]";
	}
	
	
}
