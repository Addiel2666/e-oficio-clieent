/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Table;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;

/**
 * The Class Area.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "favDestinatariosFuncionarios")
public class FavDestinatarioFuncionario implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5393698133183823296L;

	/** The funcionario key. */
	@EmbeddedId
	private FavDestinatarioFuncionarioKey funcionarioKey;

	/** The area destinatario. */
	@Column(name = "areaDestinatario", insertable = false, updatable = false)
	private String areaDestinatario;

	/** The siglas area. */
	@Column(name = "siglasArea", insertable = false, updatable = false)
	private String siglasArea;

	/** The id institucion. */
	@Column(name = "idInstitucion", insertable = false, updatable = false)
	private Integer idInstitucion;

	/** The institucion. */
	@Column(name = "institucion", insertable = false, updatable = false)
	private String institucion;

	/** The id tipo institucion. */
	@Column(name = "idTipoInstitucion", insertable = false, updatable = false)
	private String idTipoInstitucion;

	/** The abrev institucion. */
	@Column(name = "abrevInstitucion", insertable = false, updatable = false)
	private String abrevInstitucion;

	/** The id representante. */
	@Column(name = "idRepresentante", insertable = false, updatable = false)
	private String idRepresentante;

	/** The id tipo representante. */
	@Column(name = "idTipoRepresentante", insertable = false, updatable = false)
	private String idTipoRepresentante;

	/** The paterno. */
	@Column(name = "paterno", insertable = false, updatable = false)
	private String paterno;

	/** The materno. */
	@Column(name = "materno", insertable = false, updatable = false)
	private String materno;

	/** The nombre. */
	@Column(name = "nombre", insertable = false, updatable = false)
	private String nombre;

	/** The cargo. */
	@Column(name = "cargo", insertable = false, updatable = false)
	private String cargo;

	/** The id area representante. */
	@Column(name = "idAreaRepresentante", insertable = false, updatable = false)
	private Integer idAreaRepresentante;

	/** The area representate. */
	@Column(name = "areaRepresentate", insertable = false, updatable = false)
	private String areaRepresentate;

	/** The cvearea representante. */
	@Column(name = "cveareaRepresentante", insertable = false, updatable = false)
	private String cveareaRepresentante;

	/** The id tipo usuario. */
	@Column(name = "idTipoUsuario", insertable = false, updatable = false)
	private String idTipoUsuario;

	/** The usuario activo SN. */
	@Column(name = "usuarioActivoSN", insertable = false, updatable = false)
	private String usuarioActivoSN;

	/**
	 * Gets the funcionario key.
	 *
	 * @return the funcionario key
	 */
	public FavDestinatarioFuncionarioKey getFuncionarioKey() {
		return funcionarioKey;
	}

	/**
	 * Sets the funcionario key.
	 *
	 * @param funcionarioKey
	 *            the new funcionario key
	 */
	public void setFuncionarioKey(FavDestinatarioFuncionarioKey funcionarioKey) {
		this.funcionarioKey = funcionarioKey;
	}

	/**
	 * Gets the area destinatario.
	 *
	 * @return the area destinatario
	 */
	public String getAreaDestinatario() {
		return areaDestinatario;
	}

	/**
	 * Sets the area destinatario.
	 *
	 * @param areaDestinatario
	 *            the new area destinatario
	 */
	public void setAreaDestinatario(String areaDestinatario) {
		this.areaDestinatario = areaDestinatario;
	}

	/**
	 * Gets the siglas area.
	 *
	 * @return the siglas area
	 */
	public String getSiglasArea() {
		return siglasArea;
	}

	/**
	 * Sets the siglas area.
	 *
	 * @param siglasArea
	 *            the new siglas area
	 */
	public void setSiglasArea(String siglasArea) {
		this.siglasArea = siglasArea;
	}

	/**
	 * Gets the id institucion.
	 *
	 * @return the id institucion
	 */
	public Integer getIdInstitucion() {
		return idInstitucion;
	}

	/**
	 * Sets the id institucion.
	 *
	 * @param idInstitucion
	 *            the new id institucion
	 */
	public void setIdInstitucion(Integer idInstitucion) {
		this.idInstitucion = idInstitucion;
	}

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public String getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion
	 *            the new institucion
	 */
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	/**
	 * Gets the id tipo institucion.
	 *
	 * @return the id tipo institucion
	 */
	public String getIdTipoInstitucion() {
		return idTipoInstitucion;
	}

	/**
	 * Sets the id tipo institucion.
	 *
	 * @param idTipoInstitucion
	 *            the new id tipo institucion
	 */
	public void setIdTipoInstitucion(String idTipoInstitucion) {
		this.idTipoInstitucion = idTipoInstitucion;
	}

	/**
	 * Gets the abrev institucion.
	 *
	 * @return the abrev institucion
	 */
	public String getAbrevInstitucion() {
		return abrevInstitucion;
	}

	/**
	 * Sets the abrev institucion.
	 *
	 * @param abrevInstitucion
	 *            the new abrev institucion
	 */
	public void setAbrevInstitucion(String abrevInstitucion) {
		this.abrevInstitucion = abrevInstitucion;
	}

	/**
	 * Gets the id representante.
	 *
	 * @return the id representante
	 */
	public String getIdRepresentante() {
		return idRepresentante;
	}

	/**
	 * Sets the id representante.
	 *
	 * @param idRepresentante
	 *            the new id representante
	 */
	public void setIdRepresentante(String idRepresentante) {
		this.idRepresentante = idRepresentante;
	}

	/**
	 * Gets the id tipo representante.
	 *
	 * @return the id tipo representante
	 */
	public String getIdTipoRepresentante() {
		return idTipoRepresentante;
	}

	/**
	 * Sets the id tipo representante.
	 *
	 * @param idTipoRepresentante
	 *            the new id tipo representante
	 */
	public void setIdTipoRepresentante(String idTipoRepresentante) {
		this.idTipoRepresentante = idTipoRepresentante;
	}

	/**
	 * Gets the paterno.
	 *
	 * @return the paterno
	 */
	public String getPaterno() {
		return paterno;
	}

	/**
	 * Sets the paterno.
	 *
	 * @param paterno
	 *            the new paterno
	 */
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	/**
	 * Gets the materno.
	 *
	 * @return the materno
	 */
	public String getMaterno() {
		return null != materno ? materno : "";
	}

	/**
	 * Sets the materno.
	 *
	 * @param materno
	 *            the new materno
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
	}

	/**
	 * Gets the nombre.
	 *
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Sets the nombre.
	 *
	 * @param nombre
	 *            the new nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Gets the cargo.
	 *
	 * @return the cargo
	 */
	public String getCargo() {
		return cargo;
	}

	/**
	 * Sets the cargo.
	 *
	 * @param cargo
	 *            the new cargo
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * Gets the id area representante.
	 *
	 * @return the id area representante
	 */
	public Integer getIdAreaRepresentante() {
		return idAreaRepresentante;
	}

	/**
	 * Sets the id area representante.
	 *
	 * @param idAreaRepresentante
	 *            the new id area representante
	 */
	public void setIdAreaRepresentante(Integer idAreaRepresentante) {
		this.idAreaRepresentante = idAreaRepresentante;
	}

	/**
	 * Gets the area representate.
	 *
	 * @return the area representate
	 */
	public String getAreaRepresentate() {
		return areaRepresentate;
	}

	/**
	 * Sets the area representate.
	 *
	 * @param areaRepresentate
	 *            the new area representate
	 */
	public void setAreaRepresentate(String areaRepresentate) {
		this.areaRepresentate = areaRepresentate;
	}

	/**
	 * Gets the cvearea representante.
	 *
	 * @return the cvearea representante
	 */
	public String getCveareaRepresentante() {
		return cveareaRepresentante;
	}

	/**
	 * Sets the cvearea representante.
	 *
	 * @param cveareaRepresentante
	 *            the new cvearea representante
	 */
	public void setCveareaRepresentante(String cveareaRepresentante) {
		this.cveareaRepresentante = cveareaRepresentante;
	}

	/**
	 * Gets the id tipo usuario.
	 *
	 * @return the id tipo usuario
	 */
	public String getIdTipoUsuario() {
		return idTipoUsuario;
	}

	/**
	 * Sets the id tipo usuario.
	 *
	 * @param idTipoUsuario
	 *            the new id tipo usuario
	 */
	public void setIdTipoUsuario(String idTipoUsuario) {
		this.idTipoUsuario = idTipoUsuario;
	}

	/**
	 * Gets the usuario activo SN.
	 *
	 * @return the usuario activo SN
	 */
	public String getUsuarioActivoSN() {
		return usuarioActivoSN;
	}

	/**
	 * Sets the usuario activo SN.
	 *
	 * @param usuarioActivoSN
	 *            the new usuario activo SN
	 */
	public void setUsuarioActivoSN(String usuarioActivoSN) {
		this.usuarioActivoSN = usuarioActivoSN;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FavDestinatarioFuncionario [funcionarioKey=" + funcionarioKey + ", areaDestinatario=" + areaDestinatario
				+ ", siglasArea=" + siglasArea + ", idInstitucion=" + idInstitucion + ", institucion=" + institucion
				+ ", idTipoInstitucion=" + idTipoInstitucion + ", abrevInstitucion=" + abrevInstitucion
				+ ", idRepresentante=" + idRepresentante + ", idTipoRepresentante=" + idTipoRepresentante + ", paterno="
				+ paterno + ", materno=" + materno + ", nombre=" + nombre + ", cargo=" + cargo
				+ ", idAreaRepresentante=" + idAreaRepresentante + ", areaRepresentate=" + areaRepresentate
				+ ", cveareaRepresentante=" + cveareaRepresentante + ", idTipoUsuario=" + idTipoUsuario + "]";
	}

}
