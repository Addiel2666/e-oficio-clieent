package com.ecm.sigap.data.model.util;

import java.io.Serializable;

public class MinutarioDestinatarioUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4424752765762821881L;
	private Integer idArea;
	private String idDestinatario;
	private Integer idTipoDestinatario;
	private String nombre;
	private String paterno;
	private String materno;
	private String cargo;
	private Integer idAreaDestinatario;
	private String areaDestinatario;
	private Integer idInstitucion;
	private String institucion;
	private String instAbrev;

	public Integer getIdArea() {
		return idArea;
	}

	public MinutarioDestinatarioUtil() {
		super();
	}

	public MinutarioDestinatarioUtil(Integer idArea, String idDestinatario, Integer idTipoDestinatario, String nombre,
			String paterno, String materno, String cargo, Integer idAreaDestinatario, String areaDestinatario,
			Integer idInstitucion, String institucion, String instAbrev) {
		super();
		this.idArea = idArea;
		this.idDestinatario = idDestinatario;
		this.idTipoDestinatario = idTipoDestinatario;
		this.nombre = nombre;
		this.paterno = paterno;
		this.materno = materno;
		this.cargo = cargo;
		this.idAreaDestinatario = idAreaDestinatario;
		this.areaDestinatario = areaDestinatario;
		this.idInstitucion = idInstitucion;
		this.institucion = institucion;
		this.instAbrev = instAbrev;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public String getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public Integer getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	public void setIdTipoDestinatario(Integer idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPaterno() {
		return paterno;
	}

	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	public String getMaterno() {
		return materno;
	}

	public void setMaterno(String materno) {
		this.materno = materno;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Integer getIdAreaDestinatario() {
		return idAreaDestinatario;
	}

	public void setIdAreaDestinatario(Integer idAreaDestinatario) {
		this.idAreaDestinatario = idAreaDestinatario;
	}

	public String getAreaDestinatario() {
		return areaDestinatario;
	}

	public void setAreaDestinatario(String areaDestinatario) {
		this.areaDestinatario = areaDestinatario;
	}

	public Integer getIdInstitucion() {
		return idInstitucion;
	}

	public void setIdInstitucion(Integer idInstitucion) {
		this.idInstitucion = idInstitucion;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public String getInstAbrev() {
		return instAbrev;
	}

	public void setInstAbrev(String instAbrev) {
		this.instAbrev = instAbrev;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "MinutarioDestinatarioUtil [idArea=" + idArea + ", idDestinatario=" + idDestinatario
				+ ", idTipoDestinatario=" + idTipoDestinatario + ", nombre=" + nombre + ", paterno=" + paterno
				+ ", materno=" + materno + ", cargo=" + cargo + ", idAreaDestinatario=" + idAreaDestinatario
				+ ", areaDestinatario=" + areaDestinatario + ", idInstitucion=" + idInstitucion + ", institucion="
				+ institucion + ", instAbrev=" + instAbrev + "]";
	}
	
	

}
