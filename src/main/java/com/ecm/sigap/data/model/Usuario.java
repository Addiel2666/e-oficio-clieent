/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.util.TipoUsuario;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Clase de entidad que representa a los Usuario Internos del Sistema
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "usuarios")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SecondaryTables({ //

		@SecondaryTable(name = "userCapacita", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "userName", referencedColumnName = "idUsuario")),

		@SecondaryTable(name = "representantes", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "idRepresentante", referencedColumnName = "idUsuario")), //

		@SecondaryTable(name = "prefijousuarios", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "idRepresentante", referencedColumnName = "idUsuario")) //

})
public class Usuario implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1697096368470144022L;

	/** Identificador del usuario */
	@Id
	@Column(name = "idUsuario")
	private String idUsuario;

	/** Correo Electronico */
	@Column(name = "email")
	private String email;

	/** Identificador del Rol */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "idRol")
	@Fetch(FetchMode.JOIN)
	private Rol rol;

	/** Identificador del Area al que pertenece el Usuario */
	@Column(name = "idArea", nullable = false)
	private Integer idArea;

	/** Identificador del estatus del Usuario en el sistema */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** Tipo */
	@Column(name = "idTipoUsuario")
	@Enumerated(EnumType.STRING)
	private TipoUsuario tipo;

	/** Identificador del usuario en el Repositorio */
	@Column(name = "keyUsuario", nullable = false)
	private String userKey;

	/** Identificador del estatus de capacitacion del Usuario */
	@Column(name = "capacitadosn", table = "userCapacita")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean capacitado;

	/** Identificador del estatus de la aceptacion de las politicas */
	@Column(name = "aceptosn", table = "userCapacita")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean acepto;

	/** Apellido Materno */
	@Column(name = "materno", table = "representantes")
	private String materno;

	/** Apellido Paterno */
	@Column(name = "paterno", table = "representantes")
	private String apellidoPaterno;

	/** Nombres */
	@Column(name = "nombre", table = "representantes")
	private String nombres;

	// TODO se tarda mucho usando formula
	@Transient
	private String nombreCompleto;

	/** Cargo */
	@Column(name = "cargo", table = "representantes")
	private String cargo;

	/** Prefijo */
	@Column(name = "prefijo", table = "prefijousuarios")
	private String prefijo;

	/** Identificador del Tipo de Representante */
	@Column(name = "idTipoRepresentante", table = "representantes", nullable = false)
	private String idTipoRepresentante;

	/** Identificador del Area al que pertenece el usuario */
	@Column(name = "idArea", table = "representantes", nullable = false)
	private Integer idAreaRepresentante;

	/** Identificador del Area al que pertenece el usuario */
	@OneToOne
	@JoinColumn(name = "idArea", insertable = false, updatable = false)
	@Fetch(FetchMode.SELECT)
	private AreaAux areaAux;

	/**
	 * Obtiene el Identificador del usuario .
	 *
	 * @return Identificador del usuario
	 */
	public String getIdUsuario() {

		return idUsuario;
	}

	/**
	 * Asigna el Identificador del usuario
	 *
	 * @param idUsuario Identificador del usuario
	 */
	public void setIdUsuario(String idUsuario) {

		this.idUsuario = idUsuario;
	}

	/**
	 * Obtiene el Correo Electronico
	 *
	 * @return Correo Electronico
	 */
	public String getEmail() {

		return email;
	}

	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	/**
	 * Asigna el Correo Electronico
	 *
	 * @param email Correo Electronico
	 */
	public void setEmail(String email) {

		this.email = email;
	}

	/**
	 * Obtiene el Identificador del Rol
	 *
	 * @return Identificador del Rol
	 */
	public Rol getRol() {

		return rol;
	}

	/**
	 * Asigna el Identificador del Rol
	 *
	 * @param rol Identificador del Rol
	 */
	public void setRol(Rol rol) {

		this.rol = rol;
	}

	/**
	 * Obtiene el Identificador del Area al que pertenece el Usuario
	 *
	 * @return Identificador del Area al que pertenece el Usuario
	 */
	public Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del Area al que pertenece el Usuario
	 *
	 * @param idArea Identificador del Area al que pertenece el Usuario
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene el Identificador del estatus del Usuario en el sistema, <t>true</t>
	 * en caso que este activo, de lo contrario <t>false</t>
	 *
	 * @return Identificador del estatus del Usuario en el sistema
	 */
	public Boolean getActivo() {

		return activo;
	}

	/**
	 * Asigna el Identificador del estatus del Usuario en el sistema, <t>true</t> en
	 * caso que este activo, de lo contrario <t>false</t>
	 *
	 * @param activo Identificador del estatus del Usuario en el sistema
	 */
	public void setActivo(Boolean activo) {

		this.activo = activo;
	}

	/**
	 * Obtiene el Tipo {@link TipoUsuario}
	 *
	 * @return Tipo
	 */
	public TipoUsuario getTipo() {

		return tipo;
	}

	/**
	 * Asigna el Tipo {@link TipoUsuario}
	 *
	 * @param tipo Tipo {@link TipoUsuario}
	 */
	public void setTipo(TipoUsuario tipo) {

		this.tipo = tipo;
	}

	/**
	 * Obtiene el Identificador del usuario en el Repositorio
	 *
	 * @return Identificador del usuario en el Repositorio
	 */
	public String getUserKey() {

		return userKey;
	}

	/**
	 * Asigna el Identificador del usuario en el Repositorio
	 *
	 * @param userKey Identificador del usuario en el Repositorio
	 */
	public void setUserKey(String userKey) {

		this.userKey = userKey;
	}

	/**
	 * Obtiene el Identificador del estatus de capacitacion del Usuario
	 *
	 * @return Identificador del estatus de capacitacion del Usuario
	 */
	public Boolean getCapacitado() {

		return capacitado;
	}

	/**
	 * Asigna el Identificador del estatus de capacitacion del Usuario
	 *
	 * @param capacitado Identificador del estatus de capacitacion del Usuario
	 */
	public void setCapacitado(Boolean capacitado) {

		this.capacitado = capacitado;
	}

	/**
	 * Obtiene el Identificador del estatus de la aceptacion de las politicas
	 *
	 * @return Identificador del estatus de la aceptacion de las politicas
	 */
	public Boolean getAcepto() {

		return acepto;
	}

	/**
	 * Asigna el Identificador del estatus de la aceptacion de las politicas
	 *
	 * @param acepto Identificador del estatus de la aceptacion de las politicas
	 */
	public void setAcepto(Boolean acepto) {

		this.acepto = acepto;
	}

	/**
	 * Obtiene el Apellido Materno
	 *
	 * @return Apellido Materno
	 */
	public String getMaterno() {

		return materno;
	}

	/**
	 * Asigna el Apellido Materno
	 *
	 * @param materno Apellido Materno
	 */
	public void setMaterno(String materno) {

		this.materno = materno;
	}

	/**
	 * Obtiene el Apellido Paterno.
	 *
	 * @return Apellido Paterno
	 */
	public String getApellidoPaterno() {

		return apellidoPaterno;
	}

	/**
	 * Asigna el Apellido Paterno
	 *
	 * @param apellidoPaterno Apellido Paterno
	 */
	public void setApellidoPaterno(String apellidoPaterno) {

		this.apellidoPaterno = apellidoPaterno;
	}

	/**
	 * Obtiene los Nombres.
	 *
	 * @return Nombres
	 */
	public String getNombres() {

		return nombres;
	}

	/**
	 * Asigna los Nombres
	 *
	 * @param nombres Nombres
	 */
	public void setNombres(String nombres) {

		this.nombres = nombres;
	}

	/**
	 * Obtiene el Cargo
	 *
	 * @return Cargo
	 */
	public String getCargo() {

		return cargo;
	}

	/**
	 * Asigna el Cargo
	 *
	 * @param cargo Cargo
	 */
	public void setCargo(String cargo) {

		this.cargo = cargo;
	}

	/**
	 * Obtiene el Identificador del Tipo de Representante
	 *
	 * @return Identificador del Tipo de Representante
	 */
	public String getIdTipoRepresentante() {

		return idTipoRepresentante;
	}

	/**
	 * Asigna el Identificador del Tipo de Representante
	 *
	 * @param idTipoRepresentante Identificador del Tipo de Representante
	 */
	public void setIdTipoRepresentante(String idTipoRepresentante) {

		this.idTipoRepresentante = idTipoRepresentante;
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
	 * @param idAreaRepresentante the new id area representante
	 */
	public void setIdAreaRepresentante(Integer idAreaRepresentante) {

		this.idAreaRepresentante = idAreaRepresentante;
	}

	/**
	 * Obtiene el Area al que pertenece el Usuario
	 *
	 * @return Area al que pertenece el Usuario
	 */
	public AreaAux getAreaAux() {
		return areaAux;
	}

	/**
	 * Asigna el Area al que pertenece el Usuario
	 *
	 * @param areaAux Area al que pertenece el Usuario
	 */
	public void setAreaAux(AreaAux areaAux) {
		this.areaAux = areaAux;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", email=" + email + ", rol=" + rol + ", idArea=" + idArea
				+ ", activo=" + activo + ", tipo=" + tipo + ", userKey=" + userKey + ", capacitado=" + capacitado
				+ ", acepto=" + acepto + ", materno=" + materno + ", apellidoPaterno=" + apellidoPaterno + ", nombres="
				+ nombres + ", cargo=" + cargo + ", idTipoRepresentante=" + idTipoRepresentante
				+ ", idAreaRepresentante=" + idAreaRepresentante + ", areaAux=" + areaAux + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.audit.aspectj.IAuditLog#getId()
	 */
	@Override
	@JsonIgnore
	public String getId() {
		return String.valueOf(this.idUsuario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.audit.aspectj.IAuditLog#getLogDeatil()
	 */
	@Override
	@JsonIgnore
	public String getLogDeatil() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Usuario").append("<br>")
		.append("Usuario: ").append(idUsuario).append("<br>")
		.append("Nombres: ").append(nombres).append("<br>")
		.append("Apellido paterno: ").append(apellidoPaterno).append("<br>")
		.append("Apellido materno: ").append(materno).append("<br>")
		.append("Nombre: ").append(nombres + " " + apellidoPaterno + " " + ( (materno != null) ? materno : "") ).append("<br>")
		.append("Activo: ").append(activo).append("<br>")
		.append("Capacitado: ").append(capacitado).append("<br>")
		.append("Email: ").append(email).append("<br>")
		.append("Cargo: ").append(cargo).append("<br>")
		.append("Rol: ").append(rol.getDescripcion()).append("<br>")
		.append("Tipo: ").append( (tipo.getTipo ()!= null) ? tipo.getTipo () : "null" ).append("<br>")
		.append("Id área: ").append(idArea).append("<br>");
		
		if(areaAux != null) {
			sb.append("Área: ").append( (areaAux.getDescripcion() != null) ? areaAux.getDescripcion() : "null" ).append("<br>");
			if (areaAux.getInstitucion() != null) {
				sb.append("Id institución: ").append(areaAux.getInstitucion().getIdInstitucion()).append("<br>")
				.append("Institución: ").append(areaAux.getInstitucion().getDescripcion());
			} else {
				sb.append("Id institución: ").append("null").append("<br>")
				.append("Institución: ").append("null");
			}
		} else {
			sb.append("Área: ").append("null").append("<br>")
			.append("Id institución: ").append("null").append("<br>")
			.append("Institución: ").append("null");
		}
		
		return sb.toString();
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

}