/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class Representante.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "representantes")
@SecondaryTables({

		@SecondaryTable(name = "usuarios", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "idUsuario", referencedColumnName = "idRepresentante")),

		@SecondaryTable(name = "prefijoUsuarios", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "idRepresentante", referencedColumnName = "idRepresentante"))

})
public class Representante implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 151288763338190905L;

	/** The id. */
	@Id
	@GenericGenerator(name = "SEQ_REPRESENTANTE", //
			strategy = "com.ecm.sigap.data.util.StringSequenceGeneratorRepresentante")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REPRESENTANTE")
	@Column(name = "idRepresentante")
	private String id;

	/** prefijo. */
	@Column(name = "prefijo", table = "prefijoUsuarios")
	@NotFound(action = NotFoundAction.IGNORE)
	private String prefijo;

	/** The nombres. */
	@Column(name = "nombre")
	private String nombres;

	/** The paterno. */
	@Column(name = "paterno")
	private String paterno;

	/** The materno. */
	@Column(name = "materno")
	private String materno;

	/** The nombre completo. */
	@Formula(" concat( NOMBRE , concat( ' ' , concat(PATERNO , concat(' ' ,  MATERNO)))) ")
	private String nombreCompleto;

	/** The cargo. */
	@Column(name = "cargo")
	private String cargo;

	/** The id area. */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idArea")
	@Fetch(value = FetchMode.SELECT)
	private AreaAuxiliar area;

	/** The id externo. */
	@Column(name = "idExterno")
	private String idExterno;

	/** The id tipo. */
	@Column(name = "idTipoRepresentante")
	private String idTipo;

	/** The activosn. */
	@Column(name = "activosn", table = "usuarios", updatable = false, insertable = false)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activosn;

	/** Identificador del Rol. */
	@OneToOne
	@JoinColumns({ //
			@JoinColumn(name = "idArea", table = "usuarios", updatable = false, insertable = false),
			@JoinColumn(name = "idRol", table = "usuarios", updatable = false, insertable = false) //
	})
	@Fetch(value = FetchMode.SELECT)
	private Rol2 rol;

	//@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idRepresentante")
	@Fetch(value = FetchMode.SELECT)
	private Usuario usuario;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @param paterno the new paterno
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
	 * @param materno the new materno
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
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
	 * @param cargo the new cargo
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public AreaAuxiliar getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area the new area
	 */
	public void setArea(AreaAuxiliar area) {
		this.area = area;
	}

	/**
	 * Gets the id externo.
	 *
	 * @return the id externo
	 */
	public String getIdExterno() {
		return idExterno;
	}

	/**
	 * Sets the id externo.
	 *
	 * @param idExterno the new id externo
	 */
	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	/**
	 * Gets the id tipo.
	 *
	 * @return the id tipo
	 */
	public String getIdTipo() {
		return idTipo;
	}

	/**
	 * Sets the id tipo.
	 *
	 * @param idTipo the new id tipo
	 */
	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the activosn.
	 *
	 * @return the activosn
	 */
	public Boolean getActivosn() {
		return activosn;
	}

	/**
	 * Sets the activosn.
	 *
	 * @param activosn the new activosn
	 */
	public void setActivosn(Boolean activosn) {
		this.activosn = activosn;
	}

	/**
	 * Gets the nombre completo.
	 *
	 * @return the nombreCompleto
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * Sets the nombre completo.
	 *
	 * @param nombreCompleto the nombreCompleto to set
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/**
	 * Gets the rol.
	 *
	 * @return the rol
	 */
	public Rol2 getRol() {
		return rol;
	}

	/**
	 * Sets the rol.
	 *
	 * @param rol the new rol
	 */
	public void setRol(Rol2 rol) {
		this.rol = rol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Representante [id=" + id + ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno
				+ ", nombreCompleto=" + nombreCompleto + ", cargo=" + cargo + ", area=" + area + ", idExterno="
				+ idExterno + ", idTipo=" + idTipo + ", activosn=" + activosn + ", rol=" + rol + "]";
	}

	/**
	 * @return the prefijo
	 */
	public String getPrefijo() {
		return prefijo;
	}

	/**
	 * @param prefijo the prefijo to set
	 */
	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
